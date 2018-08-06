package com.example.android.handystalker.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "rule", indices = {@Index("arrival_id"), @Index("departure_id"), @Index("contact_id")}, foreignKeys = {@ForeignKey(entity = PlaceEntry.class,
        parentColumns = "id",
        childColumns = "arrival_id",
        onDelete = CASCADE),
        @ForeignKey(entity = PlaceEntry.class,
                parentColumns = "id",
                childColumns = "departure_id",
                onDelete = CASCADE),
        @ForeignKey(entity = ContactsEntry.class,
        parentColumns = "id",
        childColumns = "contact_id",
        onDelete = CASCADE)})
public class RuleEntry {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "arrival_id")
    private Integer arrivalId;

    @ColumnInfo(name = "departure_id")
    private Integer departureId;

    @ColumnInfo(name = "contact_id")
    private int contactId;

    @ColumnInfo(name = "type")
    @NonNull
    private String type;

    @ColumnInfo(name = "active")
    @NonNull
    private boolean active;

    @Ignore
    public RuleEntry() {
    }

    public RuleEntry(Integer arrivalId, Integer departureId, int contactId, @NonNull String type,@NonNull boolean active) {
        this.arrivalId = arrivalId;
        this.departureId = departureId;
        this.contactId = contactId;
        this.type = type;
        this.active = active;
    }

    // Getters and setters are required for Room to work.

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Integer getArrivalId() {
        return arrivalId;
    }
    public void setArrivalId(Integer arrivalId) {
        this.arrivalId = arrivalId;
    }

    public Integer getDepartureId() { return departureId; }
    public void setDepartureId(Integer departureId) {
        this.departureId = departureId;
    }

    public int getContactId() { return contactId; }
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getType() { return type; }
    public void setType(String type) {
        this.type = type;
    }

    public boolean getActive() { return active; }
    public void setActive(boolean active) {
        this.active = active;
    }
}