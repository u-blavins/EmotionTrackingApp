package com.ublavins.emotion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DiaryRecyclerAdapter extends RecyclerView.Adapter<DiaryRecyclerAdapter.DiaryViewHolder> {
    private ArrayList<DiaryEntry> diaryEntryList;

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
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
        }
    }

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
        holder.entryImage.setImageResource(diaryEntry.getIcon());
        holder.entryDate.setText(diaryEntry.getDate());
        holder.entryTime.setText(diaryEntry.getTime());
        holder.entryThoughts.setText(diaryEntry.getThoughts());
    }

    @Override
    public int getItemCount() {
        return diaryEntryList.size();
    }

}
