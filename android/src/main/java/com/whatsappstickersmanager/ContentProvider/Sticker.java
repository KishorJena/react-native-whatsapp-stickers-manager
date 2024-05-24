package com.whatsappstickersmanager.ContentProvider;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

public class Sticker implements Parcelable {
    int pack_id;
    String TAG = Sticker.class.getSimpleName();
    public String imageFileName;
    List<String> emojis;
    long size;

    public Sticker(String imageFileName, List<String> emojis) {
        this.imageFileName = imageFileName;
        Log.d(TAG,"Sticker-String : imageFileName "+imageFileName);
        this.emojis = emojis;
    }

    protected Sticker(Parcel in) {
        imageFileName = in.readString();
        emojis = in.createStringArrayList();
        size = in.readLong();
        Log.d(TAG,"Sticker-Parcel : imageFileName "+imageFileName);
    }

    public void setID(int pack_id) {
        this.pack_id = pack_id;
    }

    public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
        @Override
        public Sticker createFromParcel(Parcel in) {
            return new Sticker(in);
        }

        @Override
        public Sticker[] newArray(int size) {
            return new Sticker[size];
        }
    };

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageFileName);
        dest.writeStringList(emojis);
        dest.writeLong(size);
    }

    public String getContent() {
        return imageFileName;
    }
}