package com.ublavins.emotion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {

    private BottomNavigationView chartNav;

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        chartNav = (BottomNavigationView)view.findViewById(R.id.chartNav);

        mapFrame();

        chartNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.chart_map:
                                mapFrame();
                                break;
                            case R.id.chart_graph:
                                graphFrame();
                                break;
                        }
                        return true;
                    }
                }
        );

        return view;
    }

    private void mapFrame() {
        MapChartFragment mapChartFragment = new MapChartFragment();
        getFragmentManager().beginTransaction().replace(R.id.chartFragementFrame, mapChartFragment)
                .addToBackStack(null).commit();
    }

    private void graphFrame() {

    }
}
