package com.ublavins.emotion;

public class DiaryEntry {
    private String entryId;
    private int entryEmojiIcon;
    private String entryDate;
    private String entryTime;
    private String entryThoughts;

    public DiaryEntry(String id, int icon, String date, String time, String thoughts) {
        entryId = id;
        entryEmojiIcon = icon;
        entryDate = date;
        entryTime = time;
        entryThoughts = thoughts;
    }

    public String getId() {
        return entryId;
    }

    public int getIcon() {
        return entryEmojiIcon;
    }

    public String getDate() {
        return entryDate;
    }

    public String getTime() {
        return entryTime;
    }

    public String getThoughts() {
        return entryThoughts;
    }
}
