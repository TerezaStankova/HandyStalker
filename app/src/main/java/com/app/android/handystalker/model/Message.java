package com.app.android.handystalker.model;

import android.os.Parcel;
import android.os.Parcelable;

//Class for Parcelable Object creation - Contact
public class Message implements Parcelable {
    private int id;
    private String text;

    /* No args constructor */
    public Message() {
    }

    public Message(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getMessageId() {
        return id;
    }
    public String getText() {
        return text;
    }


    private Message(Parcel in){
        id = in.readInt();
        text = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(text);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel parcel) {
            return new Message(parcel);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }

    };
}
