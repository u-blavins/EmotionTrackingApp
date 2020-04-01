package com.ublavins.emotion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEntryFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_IMAGE_PICK = 101;
    private ImageView happyView, okayView, stressView, sadView, angryView;
    private MapView mapView;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private Marker marker;
    private SearchView searchView;
    private ImageButton currLocationButton;
    private ImageView photoView;
    private FusedLocationProviderClient fusedLocationClient;
    private MaterialButton uploadPhoto, selectPhoto;
    private TextInputLayout thoughtsLayout;
    private TextInputEditText thoughtsText;
    private String emotion = "";
    private CheckBox happyCheck, okayCheck, stressCheck, sadCheck, angryCheck;
    private FirebaseFirestore db;
    private FirebaseUser mUser;
    private FloatingActionButton addEntryButton;
    private StorageReference mStorageRef;
    private Uri mFilepath;

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
        mStorageRef = FirebaseStorage.getInstance().getReference();
        geocoder = new Geocoder(getContext(), Locale.ENGLISH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_entry, container, false);
        searchView = view.findViewById(R.id.mapSearch);
        currLocationButton = view.findViewById(R.id.currLocationButton);
        addEntryButton = view.findViewById(R.id.addEntryFloatingButton);
        happyView = view.findViewById(R.id.happyImage);
        okayView = view.findViewById(R.id.okayImage);
        stressView = view.findViewById(R.id.stressImage);
        sadView = view.findViewById(R.id.sadImage);
        angryView = view.findViewById(R.id.angryImage);
        setEmojiClick();
        happyCheck = view.findViewById(R.id.happyCheck);
        okayCheck = view.findViewById(R.id.okayCheck);
        stressCheck = view.findViewById(R.id.stressCheck);
        sadCheck = view.findViewById(R.id.sadCheck);
        angryCheck = view.findViewById(R.id.angryCheck);
        setCheckboxes();
        thoughtsLayout = view.findViewById(R.id.thoughtsLayout);
        thoughtsText = view.findViewById(R.id.thoughtsText);
        selectPhoto = view.findViewById(R.id.selectPhotoButton);
        uploadPhoto = view.findViewById(R.id.uploadPhotoButton);
        photoView = view.findViewById(R.id.photoView);
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
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_LOCATION);

                            return;
                        }
                        getCurrentLocation();
                    }
                }
        );

        selectPhoto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectPhoto();
                    }
                }
        );

        uploadPhoto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_IMAGE_CAPTURE);
                            return;
                        } else {
                            uploadPhoto();
                        }
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
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            photoView.getLayoutParams().width = 800;
            photoView.getLayoutParams().height = 800;
            photoView.requestLayout();
            mFilepath = getImageUri(getContext(), imageBitmap);
            photoView.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            mFilepath = data.getData();
            Bitmap imageBitmap = null;
            try {
                photoView.getLayoutParams().width = 800;
                photoView.getLayoutParams().height = 800;
                photoView.requestLayout();
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mFilepath);
                photoView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // https://stackoverflow.com/questions/9890757/android-camera-data-intent-returns-null
    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), photo, "", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                return;
            }
            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadPhoto();
                }
                return;
            }
            case REQUEST_IMAGE_PICK: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPhoto();
                }
                return;
            }

        }
    }

    private void selectPhoto() {
        Intent getPictureIntent = new Intent(Intent.ACTION_PICK);
        getPictureIntent.setType("image/*");
        startActivityForResult(getPictureIntent, REQUEST_IMAGE_PICK);
    }

    private void uploadPhoto() {
        // https://developer.android.com/training/camera/photobasics
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            loadLocation(latLng.latitude, latLng.longitude);
                            setMarker(new MarkerOptions().position(latLng).title("Current Location"));
                            googleMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(latLng, 18));
                        }
                    }
                });
    }

    private void storePhoto(String uri) {
        if (mFilepath != null) {
            StorageReference storageReference = mStorageRef.child(uri);

            storageReference.putFile(mFilepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        }
    }

    private void setEmojiClick() {
        happyView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        happyCheck.setChecked(true);
                        okayCheck.setChecked(false);
                        stressCheck.setChecked(false);
                        sadCheck.setChecked(false);
                        angryCheck.setChecked(false);
                    }
                }
        );
        okayView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        okayCheck.setChecked(true);
                        happyCheck.setChecked(false);
                        stressCheck.setChecked(false);
                        sadCheck.setChecked(false);
                        angryCheck.setChecked(false);
                    }
                }
        );
        stressView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stressCheck.setChecked(true);
                        happyCheck.setChecked(false);
                        okayCheck.setChecked(false);
                        sadCheck.setChecked(false);
                        angryCheck.setChecked(false);
                    }
                }
        );
        sadView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sadCheck.setChecked(true);
                        happyCheck.setChecked(false);
                        okayCheck.setChecked(false);
                        stressCheck.setChecked(false);
                        angryCheck.setChecked(false);
                    }
                }
        );
        angryView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        angryCheck.setChecked(true);
                        happyCheck.setChecked(false);
                        okayCheck.setChecked(false);
                        sadCheck.setChecked(false);
                        stressCheck.setChecked(false);
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
                    stressCheck.setChecked(false);
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
                    stressCheck.setChecked(false);
                    sadCheck.setChecked(false);
                    angryCheck.setChecked(false);
                }
            }
        });
        stressCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stressCheck.isChecked()) {
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
                    stressCheck.setChecked(false);
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
                    stressCheck.setChecked(false);
                }
            }
        });
    }

    private void loadLocation(double lat, double lon) {
        String address = "";
        try {
            address = geocoder.getFromLocation(lat, lon, 1
            ).get(0).getAddressLine(0);
            searchView.setQuery(address, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addEntry() {
        if (validateEntry()) {
            Calendar now = Calendar.getInstance();
            String thoughts = thoughtsText.getText().toString();
            String lat = "";
            String lon = "";
            String imgUrl = "";
            String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(now.getTime());
            String currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(now.getTime());
            LatLng latLng;
            String location = "";
            Map<String, Object> entry = new ArrayMap<String, Object>();

            if (marker != null) {
                latLng = marker.getPosition();
                lat = String.valueOf(latLng.latitude);
                lon = String.valueOf(latLng.longitude);
                try {
                    location = geocoder.getFromLocation(
                            latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if (mFilepath != null) {
                imgUrl = "entries/" + mUser.getUid() + "/" + new Date().getTime() / 1000 + ".jpg";
                storePhoto(imgUrl);
            }

            entry.put("Emotion", emotion);
            entry.put("Thoughts", thoughts);
            entry.put("Lat", lat);
            entry.put("Lon", lon);
            entry.put("Date", currentDate);
            entry.put("Time", currentTime);
            entry.put("Timestamp", new Date().getTime() / 1000);
            entry.put("Photo", imgUrl);
            entry.put("Location", location);

            db.collection("Entries")
                    .document(mUser.getUid()).collection("entry")
                    .add(entry).addOnCompleteListener(
                    new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                BottomNavigationView bottomNavBar = getActivity().findViewById(R.id.mainNavBar);
                                bottomNavBar.setSelectedItemId(R.id.nav_home);
                                makeToast("Added entry to diary");
                                HomeFragment homeFragment = new HomeFragment();
                                getFragmentManager().beginTransaction().replace(R.id.mainFragmentFrame,
                                        homeFragment).commit();
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
        if (stressCheck.isChecked()) emotion = "Stress";
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
