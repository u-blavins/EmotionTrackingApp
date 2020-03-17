package com.ublavins.emotion;

public class DiaryEntry {
    private String entryId;
    private int entryEmojiIcon;
    private String entryDate;
    private String entryTime;
    private String entryThoughts;
    private long entryTimestamp;

    public DiaryEntry() {}

    public DiaryEntry(String id, int icon, String date, String time, String thoughts, long timestamp) {
        entryId = id;
        entryEmojiIcon = icon;
        entryDate = date;
        entryTime = time;
        entryThoughts = thoughts;
        entryTimestamp = timestamp;
    }

    public String getId() {
        return entryId;
    }

    public void setId(String id) {
        entryId = id;
    }

    public int getIcon() {
        return entryEmojiIcon;
    }

    public void setIcon(int icon) {
        entryEmojiIcon = icon;
    }

    public String getDate() {
        return entryDate;
    }

    public void setDate(String date) {
        entryDate = date;
    }

    public String getTime() {
        return entryTime;
    }

    public void setTime(String time) {
        entryTime = time;
    }

    public String getThoughts() {
        return entryThoughts;
    }

    public void setThoughts(String thoughts) {
        entryThoughts = thoughts;
    }

    public long getTimestamp() {
        return entryTimestamp;
    }

    public void setEntryTimestamp(long timestamp) {
        entryTimestamp = timestamp;
    }
}
