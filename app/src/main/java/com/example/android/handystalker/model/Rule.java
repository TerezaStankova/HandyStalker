package com.example.android.handystalker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Rule  implements Parcelable {
    private int id;
    private String arrivalPlace;
    private String name;
    private String departurePlace;

    /* No args constructor */
    public Rule() {
    }

    public Rule(int id, String arrivalPlace, String name, String departurePlace) {
        this.id = id;
        this.arrivalPlace = arrivalPlace;
        this.name = name;
        this.departurePlace = departurePlace;
    }

    public int getRuleId() {
        return id;
    }
    public String getArrivalPlace() {
        return arrivalPlace;
    }
    public String getName() {
        return name;
    }
    public String getDeparturePlace() {
        return departurePlace;
    }


    private Rule(Parcel in){
        id = in.readInt();
        arrivalPlace = in.readString();
        name = in.readString();
        departurePlace = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(arrivalPlace);
        parcel.writeString(name);
        parcel.writeString(departurePlace);
    }

    public static final Parcelable.Creator<Rule> CREATOR = new Parcelable.Creator<Rule>() {
        @Override
        public Rule createFromParcel(Parcel parcel) {
            return new Rule(parcel);
        }

        @Override
        public Rule[] newArray(int size) {
            return new Rule[size];
        }

    };
}
