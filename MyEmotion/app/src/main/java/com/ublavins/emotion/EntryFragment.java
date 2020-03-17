package com.ublavins.emotion;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntryFragment extends Fragment implements OnMapReadyCallback {

    private DocumentSnapshot entry;
    private MaterialButton updateEntry, deleteEntry;
    private CheckBox happyCheck, okayCheck, neutralCheck, sadCheck, angryCheck;
    private TextInputEditText thoughtsText;
    private GoogleMap googleMap;
    private Marker marker;
    private MapView map;
    private SearchView searchView;
    private ImageButton currLocationButton;
    private FirebaseFirestore db;
    private FirebaseUser mUser;
    private FusedLocationProviderClient fusedLocationClient;
    private String emotionStr = "";

    public EntryFragment(DocumentSnapshot documentSnapshot) {
        entry = documentSnapshot;
    }

    public static EntryFragment newInstance(DocumentSnapshot documentSnapshot) {
        EntryFragment fragment = new EntryFragment(documentSnapshot);
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
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        emotionStr = entry.get("Emotion").toString();
        map = view.findViewById(R.id.mapView);
        map.onCreate(savedInstanceState);
        map.onResume();
        map.getMapAsync(this);
        happyCheck = view.findViewById(R.id.happyCheck);
        okayCheck = view.findViewById(R.id.okayCheck);
        neutralCheck = view.findViewById(R.id.neutralCheck);
        sadCheck = view.findViewById(R.id.sadCheck);
        angryCheck = view.findViewById(R.id.angryCheck);
        thoughtsText = view.findViewById(R.id.thoughtsText);
        searchView = view.findViewById(R.id.mapSearch);
        currLocationButton = view.findViewById(R.id.currLocationButton);
        updateEntry = view.findViewById(R.id.updateEntryButton);
        deleteEntry = view.findViewById(R.id.deleteEntryButton);
        setCheck();
        setCheckboxes();
        thoughtsText.setText(entry.get("Thoughts").toString());
        setMap();


        updateEntry.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateEntry();
                    }
                }
        );

        deleteEntry.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteEntry();
                    }
                }
        );


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
                        getCurrentLocation();
                    }
                }
        );
        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void updateEntry() {
        if (validateEntry()) {
            String thoughts = thoughtsText.getText().toString();
            String lat = "";
            String lon = "";
            LatLng latLng;

            if (marker != null) {
                latLng = marker.getPosition();
                lat = String.valueOf(latLng.latitude);
                lon = String.valueOf(latLng.longitude);
            }

            db.collection("Entries").document(mUser.getUid())
                    .collection("entry").document(entry.getId()).update(
                        "Emotion", emotionStr,
                        "Thoughts", thoughts,
                        "Lat", lat,
                        "Lon", lon
            );
        }
    }

    private boolean validateEntry() {
        boolean isValid = true;
        if (happyCheck.isChecked()) emotionStr = "Happy";
        if (okayCheck.isChecked()) emotionStr = "Okay";
        if (neutralCheck.isChecked()) emotionStr = "Neutral";
        if (sadCheck.isChecked()) emotionStr = "Sad";
        if (angryCheck.isChecked()) emotionStr = "Angry";

        if (emotionStr.equals("")) isValid = false;

        return isValid;
    }

    private void deleteEntry() {
        db.collection("Entries").document(mUser.getUid()).collection("entry")
                .document(entry.getId()).delete().addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getFragmentManager().popBackStack();
                    }
                }
        );
    }

    private void setCheckboxes() {
        happyCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (happyCheck.isChecked()) {
                    okayCheck.setChecked(false);
                    neutralCheck.setChecked(false);
                    sadCheck.setChecked(false);
                    angryCheck.setChecked(false);
                }
            }
        });
        okayCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (okayCheck.isChecked()) {
                    happyCheck.setChecked(false);
                    neutralCheck.setChecked(false);
                    sadCheck.setChecked(false);
                    angryCheck.setChecked(false);
                }
            }
        });
        neutralCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (neutralCheck.isChecked()) {
                    happyCheck.setChecked(false);
                    okayCheck.setChecked(false);
                    sadCheck.setChecked(false);
                    angryCheck.setChecked(false);
                }
            }
        });
        sadCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sadCheck.isChecked()) {
                    happyCheck.setChecked(false);
                    okayCheck.setChecked(false);
                    neutralCheck.setChecked(false);
                    angryCheck.setChecked(false);
                }
            }
        });
        angryCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (angryCheck.isChecked()) {
                    happyCheck.setChecked(false);
                    okayCheck.setChecked(false);
                    sadCheck.setChecked(false);
                    neutralCheck.setChecked(false);
                }
            }
        });
    }

    private void setCheck() {
        String emotion = entry.get("Emotion").toString();
        switch (emotion) {
            case "Happy":
                happyCheck.setChecked(true);
                break;
            case "Okay":
                okayCheck.setChecked(true);
                break;
            case "Neutral":
                neutralCheck.setChecked(true);
                break;
            case "Sad":
                sadCheck.setChecked(true);
                break;
            case "Angry":
                angryCheck.setChecked(true);
                break;
        }
    }

    private void setMap() {
        db.collection("Entries").document(mUser.getUid())
                .collection("entry").document(entry.getId()).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String lat = documentSnapshot.get("Lat").toString();
                        String lon = documentSnapshot.get("Lon").toString();

                        if (!lat.equals("") && !lon.equals("")) {
                            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                            marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Location"));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        }
                    }
                }
        );
    }

    private void getCurrentLocation() {
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

    private void setMarker(MarkerOptions markerOptions) {
        if (marker != null) {
            marker.remove();
            marker = googleMap.addMarker(markerOptions);
        } else {
            marker = googleMap.addMarker(markerOptions);
        }
    }
}
