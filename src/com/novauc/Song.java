package com.novauc;

/**
 * Created by james on 3/9/17.
 */
public class Song {
    int id;
    String artist;
    String title;

    public Song(int id, String artist, String title) {
        this.id = id;
        this.artist = artist;
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }
}
