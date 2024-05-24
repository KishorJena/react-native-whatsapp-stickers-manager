package com.whatsappstickersmanager.database;

import android.content.ContentValues;

import com.facebook.react.bridge.WritableArray;

import java.util.List;

public interface iPackDAO {
    long insertPack(ContentValues values);

    void insertStickers(List<ContentValues> stickers);

    WritableArray getPacks();
}
