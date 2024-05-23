package com.whatsappstickersmanager.database;

import static com.whatsappstickersmanager.Const.Pack.COL_IDENTIFIER;
import static com.whatsappstickersmanager.database.DatabaseHelper.TABLE_NAME_PACKS;
import static com.whatsappstickersmanager.database.DatabaseHelper.TABLE_NAME_STICKERS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.whatsappstickersmanager.Const;
import com.whatsappstickersmanager.DataConverter;

import java.util.List;

public class PackDAO implements PackManager {
    String TAG = PackDAO.class.getSimpleName();
    private final DatabaseHelper helper;

    private Context context;
    public PackDAO(Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }

    @Override
    public long insertPack(ContentValues values) {
        try(SQLiteDatabase db = helper.getWritableDatabase()) {
            Log.v(TAG, "insertPack: " + values);
            return db.insert(TABLE_NAME_PACKS, null, values);
        } catch (Exception e) {
            throw new RuntimeException("Error at PackDAO.insertPack: "+ e.getMessage());
        }
    }

    @Override
    public void insertStickers(List<ContentValues> stickers) {
        Log.v(TAG, "insertSticker: stickers.size()" + stickers.size());

        try(SQLiteDatabase db = helper.getWritableDatabase()) {
            for (ContentValues sticker : stickers) {
                Log.v(TAG, "insertSticker: " + sticker.toString());
                long id = db.insert(TABLE_NAME_STICKERS, null, sticker);
                Log.v(TAG, "insertSticker: ID " + id);

            }
        } catch (Exception e) {
            Log.e(TAG, "Error at PackDAO.insertStickers: "+ e.toString());
        }
    }

    @Override
    public int deletePack(long identifier) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            String selection = COL_IDENTIFIER + "=?";
            String[] selectionArgs = new String[]{String.valueOf(identifier)};
            return db.delete(TABLE_NAME_PACKS, selection, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error at PackDAO.deletePack: "+ e.toString());
            return -1;
        }
    }

    @Override
    public WritableArray getPacksALL() {
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

                    // get the pack data
//                    WritableMap pack =  extractPack(cursor);
                    WritableMap pack =  DataConverter.cursor2map_pack(cursor);

                    // get the stickers for the current pack
                    WritableArray stickersArray = DataConverter.cursor2array_stickers(db, identifier);

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

    @Override
    public int deletePackALL() {

        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            return db.delete(TABLE_NAME_PACKS, null, null);
        }

    }

    @Override
    public WritableMap getPack(int identifier) {
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            String selection = COL_IDENTIFIER + "=?";
            String[] selectionArgs = new String[]{String.valueOf(identifier)};
            Cursor cursor = db.query(TABLE_NAME_PACKS, null, selection, selectionArgs, null, null, null);

            if (cursor!= null && cursor.moveToFirst()) { // Check if cursor is not null and move to first row
                WritableMap pack = DataConverter.cursor2map_pack(cursor);

                // get the stickers for the current pack
                WritableArray stickersArray = DataConverter.cursor2array_stickers(db, identifier);

                pack.putArray(Const.Pack.COL_STICKERS, stickersArray);

                return pack;
            } else {
                return null; // Return null if cursor is null or empty
            }
        }catch ( Exception e ) {
            Log.e(TAG, "Error at PackDAO.getPack: "+ e.toString());
            return null;
        }
    }

    @Override
    public int updatePack(int identifier, ContentValues values) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            if (!values.isEmpty()) {
                String whereClause = COL_IDENTIFIER + "=?";
                String[] whereArgs = new String[]{String.valueOf(identifier)};

                db.update(TABLE_NAME_PACKS, values, whereClause, whereArgs);
            }
        }
        return 0;
    }

    @Override
    public int updateTrayIcon(int identifier, ContentValues values) {

        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            String whereClause = COL_IDENTIFIER + "=?";
            String[] whereArgs = new String[]{String.valueOf(identifier)};
            return db.update(TABLE_NAME_PACKS, values, whereClause, whereArgs);
        }catch ( Exception e ) {
            Log.e(TAG, "Error at PackDAO.updateTrayIcon: "+ e.toString());
            return -1;
        }
    }

}
