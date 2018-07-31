package com.example.android.handystalker.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "place",
        indices = {@Index(value = "place_id", unique = true)})
public class PlaceEntry {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "place_id")
    @NonNull
    private String placeId;

    @Ignore
    public PlaceEntry() {
    }

    public PlaceEntry(@NonNull String placeId) {
        this.placeId = placeId;

    }

    // Getters and setters are required for Room to work.

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}