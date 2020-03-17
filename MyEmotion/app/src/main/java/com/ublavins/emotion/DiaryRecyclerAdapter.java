package com.ublavins.emotion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class DiaryRecyclerAdapter extends RecyclerView.Adapter<DiaryRecyclerAdapter.DiaryViewHolder> {
    private ArrayList<DiaryEntry> diaryEntryList;

    public DiaryRecyclerAdapter(ArrayList<DiaryEntry> entryList) {
        diaryEntryList = entryList;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_entry_cards, parent, false);
        DiaryViewHolder diaryViewHolder = new DiaryViewHolder(view);
        return diaryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryEntry diaryEntry = diaryEntryList.get(position);
        holder.itemView.setTag(diaryEntry.getId());
        holder.entryImage.setImageResource(diaryEntry.getIcon());
        holder.entryDate.setText(diaryEntry.getDate());
        holder.entryTime.setText(diaryEntry.getTime());
        holder.entryThoughts.setText(diaryEntry.getThoughts());
    }

    @Override
    public int getItemCount() {
        return diaryEntryList.size();
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView entryImage;
        public TextView entryDate;
        public TextView entryTime;
        public TextView entryThoughts;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            entryImage = itemView.findViewById(R.id.imageView);
            entryDate = itemView.findViewById(R.id.dateTextView);
            entryTime = itemView.findViewById(R.id.timeTextView);
            entryThoughts = itemView.findViewById(R.id.thoughtsView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            FirebaseFirestore.getInstance().collection("Entries")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("entry").document(view.getTag().toString()).get()
                    .addOnSuccessListener(
                    new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            AppCompatActivity activity = (AppCompatActivity)view.getContext();
                            EntryFragment entryFragment = new EntryFragment(documentSnapshot);
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainFragmentFrame, entryFragment)
                                    .addToBackStack(null).commit();
                        }
                    }
            );
        }
    }

}
