package com.whatsappstickersmanager.database;

import android.content.ContentValues;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.whatsappstickersmanager.ContentProvider.StickerPack;

import java.util.List;

public interface PackManager {
    long insertPack(ContentValues values);

    void insertStickers(List<ContentValues> stickers);

    int deletePack(long id);
    WritableArray getPacksALL();

    int deletePackALL();

    WritableMap getPack(int packId);

    int updatePack(int identifier, ContentValues values);

    int updateTrayIcon(int identifier,  ContentValues values);

    int updateVersion(int identifier);
}
