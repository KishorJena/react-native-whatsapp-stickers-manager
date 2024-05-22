package com.whatsappstickersmanager.database;

import static com.whatsappstickersmanager.Const.Pack.COL_ANDROID;
import static com.whatsappstickersmanager.Const.Pack.COL_ANIMATED;
import static com.whatsappstickersmanager.Const.Pack.COL_AVOID_CACHE;
import static com.whatsappstickersmanager.Const.Pack.COL_IDENTIFIER;
import static com.whatsappstickersmanager.Const.Pack.COL_IOS;
import static com.whatsappstickersmanager.Const.Pack.COL_LICENSE;
import static com.whatsappstickersmanager.Const.Pack.COL_NAME;
import static com.whatsappstickersmanager.Const.Pack.COL_PRIVACY;
import static com.whatsappstickersmanager.Const.Pack.COL_PUBLISHER;
import static com.whatsappstickersmanager.Const.Pack.COL_PUB_EMAIL;
import static com.whatsappstickersmanager.Const.Pack.COL_PUB_LINK;
import static com.whatsappstickersmanager.Const.Pack.COL_TRAY;
import static com.whatsappstickersmanager.Const.Pack.COL_VERSION;
import static com.whatsappstickersmanager.Const.Sticker.COL_EMOJIS;
import static com.whatsappstickersmanager.Const.Sticker.COL_IMAGE_FILE;
import static com.whatsappstickersmanager.Const.Sticker.COL_STICKER_ID;
import static com.whatsappstickersmanager.WhatsappStickersManagerModule.STICKER_ASSET_PATH;
import static com.whatsappstickersmanager.database.DatabaseHelper.TABLE_NAME_PACKS;
import static com.whatsappstickersmanager.database.DatabaseHelper.TABLE_NAME_STICKERS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.whatsappstickersmanager.Const;

import java.util.List;

public class PackDAO implements iPackDAO {
    String TAG = PackDAO.class.getSimpleName();
    private DatabaseHelper helper;

    public PackDAO(Context context) {
        helper = new DatabaseHelper(context);
    }

    @Override
    public long insertPack(ContentValues values) {
        try(SQLiteDatabase db = helper.getWritableDatabase()) {
            return db.insert(TABLE_NAME_PACKS, null, values);
        } catch (Exception e) {
            throw new RuntimeException("Error at PackDAO.insertPack: "+ e.getMessage());
        }
    }

    @Override
    public void insertStickers(List<ContentValues> stickers) {
        try(SQLiteDatabase db = helper.getWritableDatabase()) {
            for (ContentValues sticker : stickers) {
                db.insert(TABLE_NAME_STICKERS, null, sticker);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error at PackDAO.insertPack: "+ e.getMessage());
        }
    }

    @Override
    public WritableArray getPacks() {
        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            // cursor for all rows, columns in the packs table
            Cursor cursor = db.query(TABLE_NAME_PACKS, null, null, null, null, null, null);
            if(cursor == null) return null;

            // init the array to return
            WritableArray packs = new WritableNativeArray();


            packs.pushString("packs");
            if (cursor.moveToFirst()) {
                do {
                    // get the identifier of the pack for extracting the stickers
                    int identifier = cursor.getInt(cursor.getColumnIndex(COL_IDENTIFIER));
                    Log.v(TAG, "real identifier: " + identifier);

                    // get the pack data
                    WritableMap pack =  extractPack(cursor);
                    // get the stickers for the current pack
                    WritableArray stickersArray = extractStickers(db, identifier);
                    // put the stickers in the pack
                    if(stickersArray != null) pack.putArray(Const.Pack.COL_STICKERS, stickersArray);
                    // add the pack to the array
                    packs.pushMap(pack);
                } while (cursor.moveToNext());
            }
            return packs;  // return the array
        } catch (Exception e) {
            throw new RuntimeException("at PackDAO.getPacks: "+ e.getMessage());
        }
    }

    private WritableArray extractStickers(SQLiteDatabase db, int identifier) {
        try{
            WritableArray stickers = new WritableNativeArray();

            String selection = COL_IDENTIFIER + "=?";
            String[] selectionArgs = new String[]{String.valueOf(identifier)};
            Cursor cursor =  db.query(TABLE_NAME_STICKERS, null, selection, selectionArgs, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    WritableMap stickerMap = new WritableNativeMap();

                    String emojisString = cursor.getString(cursor.getColumnIndex(COL_EMOJIS));
                    WritableArray emojis = convertEmojis2Array(emojisString);

                    stickerMap.putInt(COL_STICKER_ID, cursor.getInt(cursor.getColumnIndex(COL_STICKER_ID)));
                    stickerMap.putInt(COL_IDENTIFIER, identifier);
                    stickerMap.putString(COL_IMAGE_FILE, cursor.getString(cursor.getColumnIndex(COL_IMAGE_FILE)));
                    stickerMap.putArray(COL_EMOJIS, emojis);

                    stickers.pushMap(stickerMap);
                } while (cursor.moveToNext());
            }

            if (cursor != null) cursor.close();
            return stickers;
        }catch (Exception e){
            Log.v(TAG, "Error at PackDAO.extractStickers: "+ e.toString());
            return null;
//            throw new RuntimeException("Error at PackDAO.extractStickers: "+ e.getMessage());
        }
    }

    private ReadableMap extractSticker(Cursor cursor) {
        try{
            WritableMap stickerMap = new WritableNativeMap();

            int stickerID = cursor.getInt(cursor.getColumnIndex(COL_STICKER_ID));
            String identifier = cursor.getString(cursor.getColumnIndex(COL_IDENTIFIER));
            String imageFile = cursor.getString(cursor.getColumnIndex(COL_IMAGE_FILE));
            String emojisString = cursor.getString(cursor.getColumnIndex(COL_EMOJIS));

            WritableArray emojis = convertEmojis2Array(emojisString);

            stickerMap.putString(COL_STICKER_ID, String.valueOf(stickerID));
            stickerMap.putString(COL_IDENTIFIER, identifier);
            stickerMap.putString(COL_IMAGE_FILE, imageFile);
            stickerMap.putArray(COL_EMOJIS, emojis);

            return stickerMap;
        }catch (Exception e){
            throw new RuntimeException("Error at PackDAO.extractSticker: "+ e.getMessage());
        }
    }

    private WritableArray convertEmojis2Array(String emojisString) {
        String[] emojisArray = emojisString.split(",");
        WritableArray emojisWritableArray = new WritableNativeArray();
        for (String emoji : emojisArray) {
            emojisWritableArray.pushString(emoji);
        }
        return emojisWritableArray;
    }

    private WritableMap extractPack(Cursor cursor) {
        try{
            WritableMap packMap = new WritableNativeMap();
            // priority 1
            packMap.putInt(COL_IDENTIFIER, cursor.getInt(cursor.getColumnIndex(COL_IDENTIFIER)));
            packMap.putString(COL_NAME, cursor.getString(cursor.getColumnIndex(COL_NAME)));
            packMap.putString(COL_PUBLISHER, cursor.getString(cursor.getColumnIndex(COL_PUBLISHER)));
            packMap.putString(COL_TRAY, cursor.getString(cursor.getColumnIndex(COL_TRAY)));
            packMap.putString(COL_VERSION, cursor.getString(cursor.getColumnIndex(COL_VERSION)));
            packMap.putBoolean(COL_ANIMATED, 1 == cursor.getInt(cursor.getColumnIndex(COL_ANIMATED)));
            packMap.putBoolean(COL_AVOID_CACHE, 1 == cursor.getInt(cursor.getColumnIndex(COL_AVOID_CACHE)));

            // priority 2
            packMap.putString(COL_ANDROID, cursor.getString(cursor.getColumnIndex(COL_ANDROID)));
            packMap.putString(COL_IOS, cursor.getString(cursor.getColumnIndex(COL_IOS)));
            packMap.putString(COL_PRIVACY, cursor.getString(cursor.getColumnIndex(COL_PRIVACY)));
            packMap.putString(COL_LICENSE, cursor.getString(cursor.getColumnIndex(COL_LICENSE)));
            packMap.putString(COL_PUB_LINK, cursor.getString(cursor.getColumnIndex(COL_PUB_LINK)));
            packMap.putString(COL_PUB_EMAIL, cursor.getString(cursor.getColumnIndex(COL_PUB_EMAIL)));

            return packMap;
        }catch ( Exception e){
            throw new RuntimeException("Error at PackDAO.extractPack: "+ e.getMessage());
        }
    }
}
