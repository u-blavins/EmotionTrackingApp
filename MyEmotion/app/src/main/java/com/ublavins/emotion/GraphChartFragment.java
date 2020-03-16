package com.ublavins.emotion;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiper.MaterialSpinner;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphChartFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private MaterialSpinner menuSpinner;
    private FirebaseFirestore db;
    private static final String[] CHARTS = {"Pie", "Bar"};
    private int count;
    private Map<String, Integer> emotion = new ArrayMap<>();

    public GraphChartFragment() {
        // Required empty public constructor
    }

    public static GraphChartFragment newInstance(String param1, String param2) {
        GraphChartFragment fragment = new GraphChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        count = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph_chart, container, false);
        menuSpinner = view.findViewById(R.id.chartType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, CHARTS);
        menuSpinner.setAdapter(adapter);
        menuSpinner.setSelection(0);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        loadPieChart();
        loadBarChart();
        db.collection("Entries").document(
                FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("entry").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!emotion.containsKey(document.get("Emotion").toString())) {
                                    emotion.put(document.get("Emotion").toString(), 1);
                                    count++;
                                } else {
                                    emotion.put(
                                            document.get("Emotion").toString(),
                                            emotion.get(document.get("Emotion").toString()) + 1
                                    );
                                    count++;
                                }
                            }

                            if (count > 0) {
                                ArrayList<String> keys = new ArrayList<>();
                                ArrayList<PieEntry> vals = new ArrayList<>();
                                ArrayList<BarEntry> bars = new ArrayList<>();

                                for (String key : emotion.keySet()) { keys.add(key);}
                                for (int i = 0; i < keys.size(); i++) {
                                    vals.add(new PieEntry((float)emotion.get(keys.get(i))/count, keys.get(i)));
                                    bars.add(new BarEntry(i, emotion.get(keys.get(i))));
                                }

                                Log.d("Test", keys.toString());

                                PieDataSet dataSet = new PieDataSet(vals, "Emotions");
                                BarDataSet barDataSet = new BarDataSet(bars, "Emotions");
                                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                BarData bData = new BarData(barDataSet);
                                barChart.setData(bData);
                                XAxis xAxis = barChart.getXAxis();
                                xAxis.setValueFormatter(new IndexAxisValueFormatter(keys));
                                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                                xAxis.setDrawGridLines(false);
                                xAxis.setLabelCount(keys.size());


                                dataSet.setSliceSpace(3f);
                                dataSet.setSelectionShift(5f);
                                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                                PieData pData = new PieData(dataSet);
                                pData.setValueTextSize(10f);
                                pData.setValueTextColor(Color.YELLOW);

                                pieChart.setData(pData);
                                pieChart.setDrawHoleEnabled(false);
                                pieChart.invalidate();
                                barChart.invalidate();
                            } else {
                                pieChart.setNoDataText("No data available");
                            }

                        }
                    }
                }
        );

        menuSpinner.setOnItemSelectedListener(
                new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner materialSpinner, View view, int i, long l) {
                        if (materialSpinner.getSelectedItem() == "Pie") {
                            barChart.setVisibility(View.INVISIBLE);
                            pieChart.animateXY(1500, 1500);
                            pieChart.setVisibility(View.VISIBLE);
                        } else {
                            pieChart.setVisibility(View.INVISIBLE);
                            barChart.animateY(1500);
                            barChart.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(MaterialSpinner materialSpinner) {
                    }
                }
        );
        return view;
    }

    private void loadPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setVisibility(View.VISIBLE);
        pieChart.animateXY(1500, 1500);
        pieChart.setNoDataText("");
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    }

    private void loadBarChart() {
        barChart.setVisibility(View.INVISIBLE);
        barChart.getDescription().setEnabled(false);
        barChart.setNoDataText("");
    }
}
