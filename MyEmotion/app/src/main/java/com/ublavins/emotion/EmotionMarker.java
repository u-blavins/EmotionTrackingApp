package com.ublavins.emotion;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EmotionMarker {

    private String markerLat;
    private String markerLon;
    private String markerEmotion;
    private String markerThoughts;
    private MarkerOptions markerOptions;

    public EmotionMarker() {
        markerOptions = new MarkerOptions();
    }

    public void setLat(String lat) {
        markerLat = lat;
    }

    public void setLon(String lon) {
        markerLon = lon;
    }

    public void setEmotion(String emotion) {
        markerEmotion = emotion;
    }

    public void setThoughts(String thoughts) {
        markerThoughts = thoughts;
    }

    public void setPosition() {
        if (!markerLat.isEmpty() && !markerLon.isEmpty()) {
            LatLng latLng = new LatLng(Double.parseDouble(markerLat),
                    Double.parseDouble(markerLon));
            markerOptions.position(latLng);
        }
    }

    public void setTitle() {
        if (markerEmotion != null && !markerEmotion.isEmpty()) {
            markerOptions.title(markerEmotion);
        }
    }

    public void setSnippet() {
        if (markerThoughts != null && !markerThoughts.isEmpty()) {
            markerOptions.snippet(markerThoughts);
        }
    }

    public MarkerOptions getMarker() {
        if (markerLat != null && !markerLat.isEmpty() &&
                markerLon != null && !markerLon.isEmpty()) {
            setPosition();
            setTitle();
            setSnippet();
        }
        return markerOptions;
    }

}
