package com.ublavins.emotion;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiper.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FloatingActionButton addEntry;
    private RecyclerView diaryRecyclerView;
    private RecyclerView.Adapter diaryAdapter;
    private RecyclerView.LayoutManager diaryLayoutManager;
    private FirebaseFirestore db;
    private List<DiaryEntry> entries = new ArrayList<>();
    private MaterialSpinner emotionSpinner;
    private static final String[] EMOTIONS = {"All", "Happy", "Okay", "Stress", "Sad", "Angry"};
    private boolean test = true;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final ArrayList<DiaryEntry> entryList = new ArrayList<>();
        diaryRecyclerView = view.findViewById(R.id.diaryRecyclerView);
        diaryRecyclerView.setHasFixedSize(true);
        emotionSpinner = view.findViewById(R.id.emotionSpinner);
        emotionSpinner.setHintAnimationEnabled(false);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, EMOTIONS);
        emotionSpinner.setAdapter(adapter);
        emotionSpinner.setSelection(0);
        diaryLayoutManager = new LinearLayoutManager(getContext());
        diaryAdapter = new DiaryRecyclerAdapter(entryList);

        db.collection("Entries").document(
                FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("entry").orderBy("Timestamp",
                Query.Direction.DESCENDING).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && test) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    DiaryEntry entry = new DiaryEntry(
                                                document.getId(),
                                                document.getString("Emotion"),
                                                getIcon(document.get("Emotion").toString()),
                                                document.get("Date").toString(),
                                                document.get("Time").toString(),
                                                document.get("Thoughts").toString(),
                                                document.getLong("Timestamp"),
                                                document.getString("Location")
                                        );
                                    entryList.add(entry);
                                    entries.add(entry);
                            }
                            diaryRecyclerView.setLayoutManager(diaryLayoutManager);
                            diaryRecyclerView.setAdapter(diaryAdapter);
                            test = false;
                        } else {

                        }
                    }
                }
        );

        emotionSpinner.setOnItemSelectedListener(
                new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner materialSpinner, View view, int i, long l) {
                        filterEmotion(materialSpinner.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(MaterialSpinner materialSpinner) {
                    }
                }
        );

        addEntry = (FloatingActionButton)view.findViewById(R.id.addEntryFloatingButton);

        addEntry.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BottomNavigationView bottomNavigationView = getActivity().
                                findViewById(R.id.mainNavBar);
                        bottomNavigationView.setSelectedItemId(R.id.nav_add_entry);
                    }
                }
        );
        return view;
    }

    private void filterEmotion(String emotion) {
        int icon = getIcon(emotion);
        List<DiaryEntry> filterEntries = new ArrayList<>();
        diaryLayoutManager = new LinearLayoutManager(getContext());
        diaryAdapter = new DiaryRecyclerAdapter((ArrayList<DiaryEntry>) filterEntries);
        if (emotion.equals("All")) {
            for (DiaryEntry entry : entries) filterEntries.add(entry);
        } else {
            for (DiaryEntry entry : entries) {
                if (entry.getIcon() == icon) {
                    filterEntries.add(entry);
                }
            }
        }
        diaryRecyclerView.setLayoutManager(diaryLayoutManager);
        diaryRecyclerView.setAdapter(diaryAdapter);
    }

    private int getIcon(String emotion) {
        int icon = 0;
        switch (emotion) {
            case "Happy":
                icon = R.drawable.ic_happy;
                break;
            case "Okay":
                icon = R.drawable.ic_okay;
                break;
            case "Stress":
                icon = R.drawable.ic_stress;
                break;
            case "Sad":
                icon = R.drawable.ic_sad;
                break;
            case "Angry":
                icon = R.drawable.ic_angry;
                break;
        }
        return icon;
    }

}
