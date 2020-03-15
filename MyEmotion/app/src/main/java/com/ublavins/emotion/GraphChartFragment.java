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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphChartFragment extends Fragment {

    private PieChart pieChart;
    private FirebaseFirestore db;
    private int count;

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
        Map<String, Integer> emotion = new ArrayMap<>();
        pieChart = (PieChart)view.findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

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
                                ArrayList<PieEntry> vals = new ArrayList<>();
                                for (String key : emotion.keySet()) {
                                    vals.add(new PieEntry((float)emotion.get(key)/count, key));
                                    Log.d("test",String.valueOf((float)emotion.get(key)/count));
                                }
                                PieDataSet dataSet = new PieDataSet(vals, "Emotions");
                                dataSet.setSliceSpace(3f);
                                dataSet.setSelectionShift(5f);
                                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

                                PieData data = new PieData(dataSet);
                                data.setValueTextSize(10f);
                                data.setValueTextColor(Color.YELLOW);

                                pieChart.setData(data);
                            }
                        }
                    }
                }
        );




        return view;
    }
}
