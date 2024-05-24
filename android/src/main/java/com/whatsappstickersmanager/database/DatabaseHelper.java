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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String TAG = SQLiteOpenHelper.class.getName();
    private static final String DATABASE_NAME = "new_sticker_packs.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_PACKS = "sticker_packs";
    public static final String TABLE_NAME_STICKERS = "stickers";
    private static final String CREATE_STICKER_PACKS_TABLE =
            "CREATE TABLE " + TABLE_NAME_PACKS + " (" +
                    COL_IDENTIFIER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT, " +
                    COL_PUBLISHER + " TEXT, " +
                    COL_TRAY + " TEXT, " +
                    COL_VERSION + " TEXT, " +
                    COL_AVOID_CACHE + " BOOLEAN, " +
                    COL_ANIMATED + " BOOLEAN, " +
                    COL_ANDROID + " TEXT, " +
                    COL_IOS + " TEXT, " +
                    COL_PUB_EMAIL + " TEXT, " +
                    COL_PUB_LINK + " TEXT, " +
                    COL_PRIVACY + " TEXT, " +
                    COL_LICENSE + " TEXT " +
                    ")";


    private static final String CREATE_STICKERS_TABLE = "CREATE TABLE " +
            TABLE_NAME_STICKERS + " (" +
            COL_STICKER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_IDENTIFIER + " INTEGER, " +
            COL_IMAGE_FILE + " TEXT NOT NULL, " +
            COL_EMOJIS + " TEXT, " +
            "FOREIGN KEY("+COL_IDENTIFIER+") REFERENCES " + TABLE_NAME_PACKS +" ("+COL_IDENTIFIER+") ON DELETE CASCADE" +
            ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STICKER_PACKS_TABLE);
        db.execSQL(CREATE_STICKERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_STICKERS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }
}
