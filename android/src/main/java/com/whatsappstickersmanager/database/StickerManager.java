package com.whatsappstickersmanager.database;

import android.content.ContentValues;

public interface StickerManager {
    long insertSticker(int identifier, ContentValues values);

    int deleteSticker(int identifier, int stickerId);

    int updateSticker(int identifier, int stickerId, ContentValues values);


}
