package com.app.android.handystalker.database;

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
    @ColumnInfo(name = "id")
    @NonNull
    private Integer id;

    @ColumnInfo(name = "place_id")
    @NonNull
    private String placeId;

    @ColumnInfo(name = "place_name")
    @NonNull
    private String placeName;

    @Ignore
    public PlaceEntry() {
    }

    public PlaceEntry(@NonNull String placeId, @NonNull String placeName) {
        this.placeId = placeId;
        this.placeName = placeName;
    }

    // Getters and setters are required for Room to work.

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}