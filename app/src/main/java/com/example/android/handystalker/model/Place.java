package com.example.android.handystalker.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable {
    private String id;
    private String name;
    private String address;

    /* No args constructor */
    public Place() {
    }

    public Place(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }


    private Place(Parcel in){
        id = in.readString();
        name = in.readString();
        address = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(address);
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel parcel) {
            return new Place(parcel);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }

    };
}
