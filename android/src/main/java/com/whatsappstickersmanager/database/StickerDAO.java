package com.whatsappstickersmanager.database;

import static com.whatsappstickersmanager.Const.Pack.COL_IDENTIFIER;
import static com.whatsappstickersmanager.Const.Sticker.COL_STICKER_ID;
import static com.whatsappstickersmanager.database.DatabaseHelper.TABLE_NAME_STICKERS;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StickerDAO implements StickerManager {
    String TAG = StickerDAO.class.getSimpleName();
    private final DatabaseHelper helper;

    private Context context;
    public StickerDAO(Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }

    @Override
    public long insertSticker(int identifier, ContentValues values) {

        try (SQLiteDatabase db = helper.getWritableDatabase();){
            return db.insert(TABLE_NAME_STICKERS, null, values);
        } catch (Exception e){
            Log.e(TAG, "insertSticker: ", e);
            return -1;
        }

    }

    @Override
    public int updateSticker(int identifier, ContentValues values) {
        try (SQLiteDatabase db = helper.getWritableDatabase();){
            String whereClause = COL_IDENTIFIER + "=?";
            String[] whereArgs = {String.valueOf(identifier)};
            return db.update(TABLE_NAME_STICKERS, values, whereClause, whereArgs);
        } catch (Exception e){
            Log.e(TAG, "updateSticker: ", e);
            return 0;
        }
    }

    @Override
    public int deleteSticker(int identifier, int stickerId) {

        try (SQLiteDatabase db = helper.getWritableDatabase();){

            String whereClause = COL_IDENTIFIER + "=? AND " + COL_STICKER_ID + "=?";
            String[] whereArgs = {String.valueOf(identifier), String.valueOf(stickerId)};

            return db.delete(TABLE_NAME_STICKERS, whereClause, whereArgs);
        } catch (Exception e){
            Log.e(TAG, "deleteSticker: ", e);
            return 0;
        }
    }

    @Override
    public int updateSticker(int identifier, int stickerId, ContentValues values) {

        try (SQLiteDatabase db = helper.getWritableDatabase();){
            String whereClause = COL_IDENTIFIER + "=? AND " + COL_STICKER_ID + "=?";
            String[] whereArgs = {String.valueOf(identifier), String.valueOf(stickerId)};

            return db.update(TABLE_NAME_STICKERS, values, whereClause, whereArgs);
        } catch (Exception e){
            Log.e(TAG, "updateSticker: ", e);
            return 0;
        }

    }
}
