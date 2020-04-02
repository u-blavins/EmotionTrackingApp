package com.ublavins.emotion;

import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmotionMarkerTest {

    private EmotionMarker emotionMarker;

    @Before
    public void setUp() {
        emotionMarker = new EmotionMarker();
    }

    @After
    public void tearDown() {
        emotionMarker = null;
    }

    @Test
    public void emptyMarkerWithoutOptions() {
        MarkerOptions sut = new EmotionMarker().getMarker();
        assertNull(sut.getPosition());
    }

    @Test
    public void setLatLonPosition() {
        String fakeLat = "5.024012";
        String fakeLon = "-1.429342";
        emotionMarker.setLat(fakeLat);
        emotionMarker.setLon(fakeLon);
        MarkerOptions sut = emotionMarker.getMarker();
        assertEquals(fakeLat, String.valueOf(sut.getPosition().latitude));
        assertEquals(fakeLon, String.valueOf(sut.getPosition().longitude));
    }

    @Test
    public void emptyLatLonNullPosition() {
        String fakeLat = "";
        String fakeLon = "";
        emotionMarker.setLat(fakeLat);
        emotionMarker.setLon(fakeLon);
        MarkerOptions sut = emotionMarker.getMarker();
        assertNull(sut.getPosition());
    }

    @Test
    public void setEmotionTitle() {
        String fakeLat = "5.024012";
        String fakeLon = "-1.429342";
        String fakeEmotion = "TestEmotion";
        emotionMarker.setLat(fakeLat);
        emotionMarker.setLon(fakeLon);
        emotionMarker.setEmotion(fakeEmotion);
        MarkerOptions sut = emotionMarker.getMarker();
        assertEquals(fakeEmotion, sut.getTitle());
    }

    @Test
    public void emptyEmotionNullTitle() {
        String fakeLat = "5.024012";
        String fakeLon = "-1.429342";
        String fakeEmotion = "";
        emotionMarker.setLat(fakeLat);
        emotionMarker.setLon(fakeLon);
        emotionMarker.setEmotion(fakeEmotion);
        MarkerOptions sut = emotionMarker.getMarker();
        assertNull(sut.getTitle());
    }

    @Test
    public void setThoughtsSnippet() {
        String fakeLat = "5.024012";
        String fakeLon = "-1.429342";
        String fakeThoughts = "This is a test";
        emotionMarker.setLat(fakeLat);
        emotionMarker.setLon(fakeLon);
        emotionMarker.setThoughts(fakeThoughts);
        MarkerOptions sut = emotionMarker.getMarker();
        assertEquals(fakeThoughts, sut.getSnippet());
    }

    @Test
    public void emptyThoughtsNullSnippet() {
        String fakeLat = "5.024012";
        String fakeLon = "-1.429342";
        String fakeThoughts = "";
        emotionMarker.setLat(fakeLat);
        emotionMarker.setLon(fakeLon);
        emotionMarker.setThoughts(fakeThoughts);
        MarkerOptions sut = emotionMarker.getMarker();
        assertNull(sut.getSnippet());
    }

    @Test
    public void emptyLocationEntryNotMarked() {
        String fakeLat = "";
        String fakeLon = "";
        String fakeEmotion = "Test Emotion";
        String fakeThoughts = "Test Thoughts";
        emotionMarker.setLat(fakeLat);
        emotionMarker.setLon(fakeLon);
        emotionMarker.setEmotion(fakeEmotion);
        emotionMarker.setThoughts(fakeThoughts);
        MarkerOptions sut = emotionMarker.getMarker();
        assertNull(sut.getPosition());
        assertNull(sut.getTitle());
        assertNull(sut.getSnippet());
    }

}
