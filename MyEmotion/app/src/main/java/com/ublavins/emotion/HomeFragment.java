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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private FloatingActionButton addEntry;
    private RecyclerView diaryRecyclerView;
    private RecyclerView.Adapter diaryAdapter;
    private RecyclerView.LayoutManager diaryLayoutManager;
    private FirebaseFirestore db;

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
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final ArrayList<DiaryEntry> entryList = new ArrayList<>();
        diaryRecyclerView = (RecyclerView)view.findViewById(R.id.diaryRecyclerView);
        diaryRecyclerView.setHasFixedSize(true);
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
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                entryList.add(
                                        new DiaryEntry(
                                                document.getId(),
                                                getIcon(document.get("Emotion").toString()),
                                                document.get("Date").toString(),
                                                document.get("Time").toString(),
                                                document.get("Thoughts").toString(),
                                                document.getLong("Timestamp")
                                        )
                                );
                            }
                            diaryRecyclerView.setLayoutManager(diaryLayoutManager);
                            diaryRecyclerView.setAdapter(diaryAdapter);
                        } else {

                        }
                    }
                }
        );

        addEntry = (FloatingActionButton)view.findViewById(R.id.addEntryFloatingButton);

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

    private int getIcon(String emotion) {
        int icon = 0;
        switch (emotion) {
            case "Happy":
                icon = R.drawable.ic_happy;
                break;
            case "Okay":
                icon = R.drawable.ic_okay;
                break;
            case "Neutral":
                icon = R.drawable.ic_neutral;
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
