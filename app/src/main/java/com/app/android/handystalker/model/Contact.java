package com.app.android.handystalker.model;

import android.os.Parcel;
import android.os.Parcelable;

//Class for Parcelable Object creation - Contact
public class Contact implements Parcelable {
    private int id;
    private String phone;
    private String name;

    /* No args constructor */
    public Contact() {
    }

    public Contact(int id, String phone, String name) {
        this.id = id;
        this.phone = phone;
        this.name = name;
    }

    public int getContactId() {
        return id;
    }
    public String getPhone() {
        return phone;
    }
    public String getName() {
        return name;
    }


    private Contact(Parcel in){
        id = in.readInt();
        phone = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(phone);
        parcel.writeString(name);
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel parcel) {
            return new Contact(parcel);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }

    };
}
