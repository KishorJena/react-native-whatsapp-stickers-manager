package com.whatsappstickersmanager;

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
import static com.whatsappstickersmanager.database.DatabaseHelper.TABLE_NAME_STICKERS;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.whatsappstickersmanager.ContentProvider.StickerPack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DataConverter {
    private static final String TAG = DataConverter.class.getSimpleName();
    static ContentValues map2content_pack(ReadableMap packData, Map<String, String> pathMap) {
        try{
            ContentValues pack = new ContentValues();

            Set<String> keys = packData.toHashMap().keySet();
            for (String key : keys) {
                switch (packData.getType(key)){
                    case Number:
                        pack.put(key, packData.getInt(key));
                        break;
                    case Boolean:
                        pack.put(key, packData.getBoolean(key));
                        break;
                    case String:
                        if(key.equals(COL_TRAY)){
//                            String fileName = Utility.getLastBitFromUrl(packData.getString(key));
//                            Log.w(TAG, "getPack: COL_TRAY " + fileName);
                            String ogPath = packData.getString(key);
                            String newName = UUID.randomUUID().toString()+".webp";
                            pathMap.put(ogPath, newName);
                            pack.put(key, newName);
                            break;
                        }
                        pack.put(key, packData.getString(key));
                        Log.v(TAG, "getPack:  keu:"+key );

                        break;
                }
            }
            return pack;
        }catch ( Exception e ){
            throw new RuntimeException("Error converting pack data",e);
        }
    }

    public static List<ContentValues> array2content_stickers(ReadableArray array, int identifier, Map<String, String> pathMap) {
        List<ContentValues> stickersList = new ArrayList<>();

        for (int i = 0; i< array.size(); i++) {
            ReadableMap stickerData = array.getMap(i);

            ContentValues sticker = new ContentValues();

            // Convert array to comma separated string
            String emojis = array2string_emoji(stickerData.getArray(COL_EMOJIS));

            String fileName = stickerData.getString(COL_IMAGE_FILE);
            String newName = UUID.randomUUID().toString() + ".webp";
            pathMap.put(fileName, newName);

            sticker.put(COL_EMOJIS, emojis);
            sticker.put(COL_IMAGE_FILE, newName);
            sticker.put(COL_IDENTIFIER, identifier);
            stickersList.add(sticker);
        }

        return stickersList;
    }



    @SuppressLint("Range")
    public static WritableMap cursor2map_pack(Cursor cursor) {
        WritableMap packMap = new WritableNativeMap();
        try{
            // priority 1
            int identifier = cursor.getInt(cursor.getColumnIndex(COL_IDENTIFIER));
            packMap.putInt(COL_IDENTIFIER, identifier);
            packMap.putString(COL_TRAY, STICKER_ASSET_PATH+"/"+identifier+"/"+cursor.getString(cursor.getColumnIndex(COL_TRAY)));
            packMap.putString(COL_NAME, cursor.getString(cursor.getColumnIndex(COL_NAME)));
            packMap.putString(COL_PUBLISHER, cursor.getString(cursor.getColumnIndex(COL_PUBLISHER)));
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
            Log.e(TAG, "Error at PackDAO.extractPack: "+ e.getMessage());
            return packMap;
        }
    }
    @SuppressLint("Range")
    public static WritableArray cursor2array_stickers(SQLiteDatabase db, int identifier) {
        Cursor cursor = null;
        try {
            WritableArray stickerArray = new WritableNativeArray();

            String selection = COL_IDENTIFIER + "=?";
            String[] selectionArgs = new String[]{String.valueOf(identifier)};
            cursor = db.query(TABLE_NAME_STICKERS, null, selection, selectionArgs, null, null, null, null);

            if (cursor!= null && cursor.moveToFirst()) {
                do {
                    WritableMap stickerMap = new WritableNativeMap();

                    String emojisString = cursor.getString(cursor.getColumnIndex(COL_EMOJIS));
                    WritableArray emojis = string2array_emoji(emojisString);

                    stickerMap.putInt(COL_STICKER_ID, cursor.getInt(cursor.getColumnIndex(COL_STICKER_ID)));
                    stickerMap.putInt(COL_IDENTIFIER, identifier);
                    stickerMap.putString(COL_IMAGE_FILE, STICKER_ASSET_PATH+"/"+identifier+"/"+cursor.getString(cursor.getColumnIndex(COL_IMAGE_FILE)));
                    stickerMap.putArray(COL_EMOJIS, emojis);

                    stickerArray.pushMap(stickerMap);
                } while (cursor.moveToNext());
                return stickerArray;
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error at PackDAO.extractStickers: " + e.toString());
            return null;
        } finally {
            if (cursor!= null) {
                cursor.close();
            }
        }
    }

    private static String array2string_emoji(ReadableArray array) {
        // Add the emojis to the ContentValues object
        String emojisString = "";
        for (int i=0; i<array.size(); i++) {
            emojisString += array.getString(i);
            if(i<array.size()-1){
                emojisString += ",";
            }
        }
        return emojisString;
    }

    private static WritableArray string2array_emoji(String emojisString) {
        String[] emojisArray = emojisString.split(",");
        WritableArray emojisWritableArray = new WritableNativeArray();
        for (String emoji : emojisArray) {
            emojisWritableArray.pushString(emoji);
        }
        return emojisWritableArray;
    }

    public static ContentValues map2Content_option(ReadableMap fields) {
        ContentValues values = new ContentValues();
        if(fields.hasKey(COL_NAME)){
            values.put(COL_NAME, fields.getString(COL_NAME));
        }
        if (fields.hasKey(COL_PUBLISHER)){
            values.put(COL_PUBLISHER, fields.getString(COL_PUBLISHER));
        }
//        for (Map.Entry<String, Object> entry : fields.toHashMap().entrySet()) {
//            values.put(entry.getKey(), entry.getValue().toString());
//        }

        return values.size() > 0? values : null;
    }


}
