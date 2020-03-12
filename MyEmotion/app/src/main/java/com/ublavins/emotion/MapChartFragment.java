package com.ublavins.emotion;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
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
public class MapChartFragment extends Fragment implements OnMapReadyCallback {

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
        mapView = (MapView)view.findViewById(R.id.mapEmotions);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        db.collection("Entries").document(
                FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("entry").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                setMarker(new MarkerOptions()
                                .position(new LatLng(
                                        Double.parseDouble(document.get("Lat").toString()),
                                        Double.parseDouble(document.get("Lon").toString())
                                )));
                            }
                        }
                    }
                }
        );
        loadMap();
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void loadMap() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(latLng, 8));
                        }
                    }
                });
    }

    private void setMarker(MarkerOptions markerOptions) {
        googleMap.addMarker(markerOptions);
    }
}
