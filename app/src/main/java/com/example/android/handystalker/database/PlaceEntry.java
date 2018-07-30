package com.example.android.handystalker.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "place")
public class PlaceEntry {

    @PrimaryKey
    @NonNull
    private int id;
    private String title;
    private String originalTitle;
    private String releaseDate;
    private String voteAverage;
    private String poster;
    private String plot;

    @Ignore
    public PlaceEntry() {
    }

    public PlaceEntry(int id, String title, String originalTitle, String releaseDate, String voteAverage, String poster, String plot) {
        this.title = title;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.poster = poster;
        this.plot = plot;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public String getOriginalTitle() {
        return originalTitle;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getVoteAverage() {
        return voteAverage;
    }
    public String getPoster() {
        return poster;
    }
    public String getPlot() {
        return plot;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }
    public void setPlot(String plot) {
        this.plot = plot;
    }
}