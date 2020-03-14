package com.ublavins.emotion;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private FloatingActionButton addEntry;
    private RecyclerView diaryRecyclerView;
    private RecyclerView.Adapter diaryAdapter;
    private RecyclerView.LayoutManager diaryLayoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ArrayList<DiaryEntry> entryList = new ArrayList<>();
        entryList.add(new DiaryEntry("testId", R.drawable.ic_happy, "testDate", "testTime", "testThoughts"));
        entryList.add(new DiaryEntry("testId", R.drawable.ic_sad, "testDate", "testTime", "testThoughts"));
        entryList.add(new DiaryEntry("testId", R.drawable.ic_angry, "testDate", "testTime", "testThoughts"));

        addEntry = (FloatingActionButton)view.findViewById(R.id.addEntryFloatingButton);
        diaryRecyclerView = (RecyclerView)view.findViewById(R.id.diaryRecyclerView);
        diaryRecyclerView.setHasFixedSize(true);
        diaryLayoutManager = new LinearLayoutManager(getContext());
        diaryAdapter = new DiaryRecyclerAdapter(entryList);
        diaryRecyclerView.setLayoutManager(diaryLayoutManager);
        diaryRecyclerView.setAdapter(diaryAdapter);

        addEntry.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddEntryFragment addEntryFrag = new AddEntryFragment();
                        getFragmentManager().beginTransaction().replace(R.id.mainFragmentFrame,
                                addEntryFrag).addToBackStack(null).commit();
                    }
                }
        );

        return view;
    }

}
