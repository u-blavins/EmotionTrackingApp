package com.ublavins.emotion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapChartFragment extends Fragment implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_LOCATION = 1;
    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    public MapChartFragment() {
        // Required empty public constructor
    }

    public static MapChartFragment newInstance() {
        MapChartFragment fragment = new MapChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_chart, container, false);
        mapView = view.findViewById(R.id.mapEmotions);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        loadMap();
        mapView.getMapAsync(this);
        db.collection("Entries").document(
                FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("entry").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.get("Lat").toString().equals("") &&
                                !document.get("Lon").toString().equals("")) {
                                    setMarker(getMarker(
                                            document.get("Lat").toString(),
                                            document.get("Lon").toString(),
                                            document.get("Emotion").toString()
                                    ));
                                }
                            }
                        }
                    }
                }
        );
        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        // Check if ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions have been set
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void loadMap() {
        // Check ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions
        // If not set request permissions then request permissions
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(latLng, 12));
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadMap();
                }
                return;
            }

        }
    }

    private MarkerOptions getMarker(String lat, String lon, String emotion) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(
                Double.parseDouble(lat),
                Double.parseDouble(lon)
        ));
        marker.icon(getIcon(emotion));
        return marker;
    }

    private BitmapDescriptor getIcon(String emotion) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), getBitmap(emotion));
        Bitmap bitmap = Bitmap.createScaledBitmap(icon, 80, 80, false);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private int getBitmap(String emotion) {
        int icon = 0;
        switch (emotion) {
            case "Happy":
                icon = R.drawable.happy;
                break;
            case "Okay":
                icon = R.drawable.okay;
                break;
            case "Neutral":
                icon = R.drawable.neutral;
                break;
            case "Sad":
                icon = R.drawable.sad;
                break;
            case "Angry":
                icon = R.drawable.angry;
                break;
        }
        return icon;
    }

    private void setMarker(MarkerOptions markerOptions) {
        googleMap.addMarker(markerOptions);
    }
}
