package com.ublavins.emotion;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEntryFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private Marker marker;
    private SearchView searchView;
    private ImageButton currLocationButton;
    private FusedLocationProviderClient fusedLocationClient;
    private MaterialButton addEntryButton;
    private TextInputLayout thoughtsLayout;
    private TextInputEditText thoughtsText;
    private String emotion = "";
    private CheckBox happyCheck, okayCheck, neutralCheck, sadCheck, angryCheck;
    private FirebaseFirestore db;
    private FirebaseUser mUser;

    public AddEntryFragment() {
        // Required empty public constructor
    }

    public static AddEntryFragment newInstance() {
        AddEntryFragment fragment = new AddEntryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_entry, container, false);
        searchView = view.findViewById(R.id.mapSearch);
        currLocationButton = view.findViewById(R.id.currLocationButton);
        addEntryButton = view.findViewById(R.id.addEntryButton);
        happyCheck = view.findViewById(R.id.happyCheck);
        okayCheck = view.findViewById(R.id.okayCheck);
        neutralCheck = view.findViewById(R.id.neutralCheck);
        sadCheck = view.findViewById(R.id.sadCheck);
        angryCheck = view.findViewById(R.id.angryCheck);
        thoughtsLayout = view.findViewById(R.id.thoughtsLayout);
        thoughtsText = view.findViewById(R.id.thoughtsText);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addresses = null;

                if (location != null && !location.isEmpty()) {
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addresses = geocoder.getFromLocationName(location, 1);
                    } catch (IOException ex) {
                        Log.d("Location", ex.toString());
                    }
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    setMarker(new MarkerOptions().position(latLng).title(location));
                    googleMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(latLng, 18));
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        currLocationButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            setMarker(new MarkerOptions().position(latLng).title("Current Location"));
                                            googleMap.animateCamera(CameraUpdateFactory
                                                    .newLatLngZoom(latLng, 18));
                                        }
                                    }
                                });
                    }
                }
        );

        addEntryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addEntry();
                    }
                }
        );

        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void addEntry() {
        if (validateEntry()) {
            Calendar now = Calendar.getInstance();
            String thoughts = thoughtsText.getText().toString();
            String lat = "";
            String lon = "";
            String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(now.getTime());
            String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(now.getTime());
            LatLng latLng;
            Map<String, Object> entry = new ArrayMap<String, Object>();

            if (marker != null) {
                latLng = marker.getPosition();
                lat = String.valueOf(latLng.latitude);
                lon = String.valueOf(latLng.longitude);
            }

            entry.put("Emotion", emotion);
            entry.put("Thoughts", thoughts);
            entry.put("Lat", lat);
            entry.put("Lon", lon);
            entry.put("Date", currentDate);
            entry.put("Time", currentTime);
            entry.put("Timestamp", new Date().getTime() / 1000);

            db.collection("Entries")
                    .document(mUser.getUid()).collection("entry")
                    .add(entry).addOnCompleteListener(
                    new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                makeToast("Added entry to diary");
                                getFragmentManager().popBackStack();
                            } else {
                                makeToast("Request unsuccessful");
                            }
                        }
                    }
            );

        } else {
            makeToast("Please select an emotion");
        }
    }

    private boolean validateEntry() {
        boolean isValid = true;

        if (happyCheck.isChecked()) emotion = "Happy";
        if (okayCheck.isChecked()) emotion = "Okay";
        if (neutralCheck.isChecked()) emotion = "Neutral";
        if (sadCheck.isChecked()) emotion = "Sad";
        if (angryCheck.isChecked()) emotion = "Angry";

        if (emotion.equals("")) isValid = false;

        return isValid;
    }

    private void setMarker(MarkerOptions markerOptions) {
        if (marker != null) {
            marker.remove();
            marker = googleMap.addMarker(markerOptions);
        } else {
            marker = googleMap.addMarker(markerOptions);
        }
    }

    private void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
