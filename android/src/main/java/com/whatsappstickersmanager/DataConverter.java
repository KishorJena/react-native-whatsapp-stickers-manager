package com.whatsappstickersmanager;

import static com.whatsappstickersmanager.Const.Pack.COL_IDENTIFIER;
import static com.whatsappstickersmanager.Const.Pack.COL_TRAY;
import static com.whatsappstickersmanager.Const.Sticker.COL_EMOJIS;
import static com.whatsappstickersmanager.Const.Sticker.COL_IMAGE_FILE;

import android.content.ContentValues;
import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataConverter {
    private static final String TAG = DataConverter.class.getSimpleName();
    static ContentValues getPack(ReadableMap packData) {
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
                            String fileName = Utility.getLastBitFromUrl(packData.getString(key));
                            Log.w(TAG, "getPack: COL_TRAY " + fileName);
                            pack.put(key, fileName);
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

    public static List<ContentValues> getStickers(ReadableMap packData, int identifier) {
        List<ContentValues> stickers = new ArrayList<>();

        ReadableArray array = packData.getArray("stickers");

        for (int i = 0; i< array.size(); i++) {
            ReadableMap stickerData = array.getMap(i);
            stickers.add(getSticker(stickerData, identifier));
        }

        return stickers;
    }

    // convert sticker ReadableMap into ContentValues (map)
    private static ContentValues getSticker(ReadableMap stickerData, int identifier) {
        ContentValues sticker = new ContentValues();

        // Convert array to comma separated string
        String emojis = getEmojis(stickerData.getArray(COL_EMOJIS));
        // extract only last segment from url
        String fileName = Utility.getLastBitFromUrl(stickerData.getString(COL_IMAGE_FILE));
        Log.v(TAG, "getSticker: " + fileName);
        sticker.put(COL_EMOJIS, emojis);
        sticker.put(COL_IMAGE_FILE, fileName);
        sticker.put(COL_IDENTIFIER, identifier);
        return sticker;
    }

    private static String getEmojis(ReadableArray array) {
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
}
