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
import static com.whatsappstickersmanager.Utility.getLastBitFromUrl;
import static com.whatsappstickersmanager.database.DatabaseHelper.TABLE_NAME_PACKS;
import static com.whatsappstickersmanager.database.DatabaseHelper.TABLE_NAME_STICKERS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.whatsappstickersmanager.Const;
import com.whatsappstickersmanager.ContentProvider.Sticker;
import com.whatsappstickersmanager.ContentProvider.StickerPack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
@SuppressLint("Range")
public class StickerPackDAO implements StickerPackManager {
    private static final String TAG = StickerPackDAO.class.getSimpleName();
    private final DatabaseHelper helper;

    private Context context;
    public StickerPackDAO(Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }

    @Override
    public List<StickerPack> getStickerPacks() {
        try(SQLiteDatabase db = helper.getReadableDatabase()){
            List<StickerPack> stickerPacks = new ArrayList<>();

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_PACKS, null);

            if (cursor.moveToFirst()) {
                do {
                    // Extract data from the cursor
                    int identifier = cursor.getInt(cursor.getColumnIndex(COL_IDENTIFIER));
                    List<Sticker> stickerList = getStickersForPack(identifier);

                    Log.d(TAG, "stickerList.size(): " + stickerList.size());

                    String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
                    String publisher = cursor.getString(cursor.getColumnIndex(COL_PUBLISHER));
                    String trayImageFile = cursor.getString(cursor.getColumnIndex(COL_TRAY));

                    int imageDataVersion = cursor.getInt(cursor.getColumnIndex(COL_VERSION));
                    int avoidCache = cursor.getInt(cursor.getColumnIndex(COL_AVOID_CACHE));
                    int animatedStickerPack = cursor.getInt(cursor.getColumnIndex(COL_ANIMATED));


                    String android_play_store_link = cursor.getString(cursor.getColumnIndex(COL_ANDROID));
                    String ios_app_store_link = cursor.getString(cursor.getColumnIndex(COL_IOS));
                    String publisherEmail = cursor.getString(cursor.getColumnIndex(COL_PUB_EMAIL));
                    String publisherWebsite = cursor.getString(cursor.getColumnIndex(COL_PUB_LINK));
                    String privacyPolicyWebsite = cursor.getString(cursor.getColumnIndex(COL_PRIVACY));
                    String licenseAgreementWebsite = cursor.getString(cursor.getColumnIndex(COL_LICENSE));



                    StickerPack stickerPack = new StickerPack(
                            String.valueOf(identifier),
                            name,
                            publisher,
//                        trayImageFile,
                            getLastBitFromUrl(trayImageFile),
//                        getLastBitFromUrl(trayImageFile).replace(" ", "_").replace(".png",".webp"),
                            publisherEmail,
                            publisherWebsite,
                            privacyPolicyWebsite,
                            licenseAgreementWebsite,
                            String.valueOf(imageDataVersion),
                            avoidCache==1,
                            animatedStickerPack==1);

                    stickerPack.setStickers(stickerList);
                    stickerPack.setIsWhitelisted(false);
                    stickerPack.setIosAppStoreLink(ios_app_store_link);
                    stickerPack.setAndroidPlayStoreLink(android_play_store_link);

                    stickerPacks.add(stickerPack);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return stickerPacks;
        }catch (Exception e){
            Log.e(TAG, "getStickerPacks:Exception ", e);
            return null;
        }
    }

    @Override
    public boolean isFilenameExistInPack(String packId, String imagePath) {
        String query = "SELECT " +
                "p."+COL_IDENTIFIER+", " +
                "p."+Const.Pack.COL_TRAY+", " +
                "s."+Const.Sticker.COL_IMAGE_FILE+" " +
                "FROM " + TABLE_NAME_PACKS + " p " +
                "LEFT JOIN " + TABLE_NAME_STICKERS + " s ON p."+ COL_IDENTIFIER+" = s."+COL_IDENTIFIER+" " +
                "WHERE p."+COL_IDENTIFIER+" = ? AND (p."+Const.Pack.COL_TRAY+" = ? OR s."+Const.Sticker.COL_IMAGE_FILE+" = ?)";

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{packId, imagePath, imagePath});
        boolean matchFound = cursor.moveToFirst();
        cursor.close();
        return matchFound;
    }


    public List<Sticker> getStickersForPack(int identifier) {
        List<Sticker>  stickers = new ArrayList<>();

        try(SQLiteDatabase db = helper.getReadableDatabase()){
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_STICKERS + " WHERE " + COL_IDENTIFIER + " = ?", new String[]{String.valueOf(identifier)});

            Log.d(TAG, "getStickersForPack: identifier " + identifier);
            // Loop through the cursor to extract and log data

            if (cursor != null && cursor.moveToFirst()) {
                Log.v(TAG, "getStickersForPack: moveToFirst " + Arrays.toString(cursor.getColumnNames()));

                do {
                    Log.v(TAG, "getStickersForPack: d0 " );

                    int id = cursor.getInt(cursor.getColumnIndex(Const.Sticker.COL_STICKER_ID));
                    String image_file = cursor.getString(cursor.getColumnIndex(Const.Sticker.COL_IMAGE_FILE));
                    String emojisString = cursor.getString(cursor.getColumnIndex(Const.Sticker.COL_EMOJIS));

                    Log.v(TAG, "getStickersForPack: "+id+" "+image_file+" "+emojisString );
//                    Log.v(TAG, "getStickersForPack: "+image_file+" "+emojisString );

                    String[] emojiArrayS = emojisString.split(",");

                    List<String> emojisArray = Arrays.asList(emojiArrayS);

                    Sticker sticker = new Sticker(
                            getLastBitFromUrl(image_file),
                            emojisArray
                    );
                    sticker.setID(id);
                    stickers.add(sticker);
                } while (cursor.moveToNext());
            } else {
                Log.v(TAG, "No data found in sticker_packs table");
            }

            cursor.close();
            return stickers;
        }catch (Exception e){
            Log.e(TAG, "getStickersForPack:Exception ", e);
            return stickers;
        }
    }
}
