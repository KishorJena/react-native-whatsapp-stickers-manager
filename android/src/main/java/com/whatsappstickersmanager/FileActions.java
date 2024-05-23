package com.whatsappstickersmanager;

import static com.whatsappstickersmanager.Const.Pack.COL_IDENTIFIER;
import static com.whatsappstickersmanager.Const.Pack.COL_STICKERS;
import static com.whatsappstickersmanager.Const.Pack.COL_TRAY;
import static com.whatsappstickersmanager.Const.Sticker.COL_IMAGE_FILE;
import static com.whatsappstickersmanager.WhatsappStickersManagerModule.STICKER_ASSET_PATH;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileActions {
    private static final String TAG = FileActions.class.getSimpleName();

    // create constructor

    public static void copyFiles(ReadableMap packData, int identifier) {
        List<String> paths = extractPaths(packData);

        for (String path : paths) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(path.replaceFirst("file://", ""));
                SaveImage(bitmap, Utility.getLastBitFromUrl(path), identifier);
//                doCopy(bitmap, Utility.getLastBitFromUrl(path), identifier);
            }catch (Exception e){
                Log.e(TAG, "Failed to copy files", e);
                continue;
            }
        }

    }


    private static List<String> extractPaths(ReadableMap packData) {
        List<String> paths = new ArrayList<>();

        // extract tray path and add to paths List
        String tray = packData.getString(COL_TRAY);
        paths.add(tray);

        // extract stickers path and add to paths List
        ReadableArray stickersArray = packData.getArray(COL_STICKERS);
        for (int i = 0; i < stickersArray.size(); i++) {
            ReadableMap stickerData = stickersArray.getMap(i);
            String stickerPath = stickerData.getString(COL_IMAGE_FILE);
            paths.add(stickerPath);
        }

        return paths;

    }

    private static void doCopy(Bitmap bitmap, String filename, int identifier) {
//        Log.v(TAG,"CopyImageData "+oldPath);

        File destFIle = new File(STICKER_ASSET_PATH+"/"+ identifier +"/"+filename);

        if (destFIle.exists()){
            if(destFIle.delete()){
                Log.v(TAG, "⚠️Deleted " + destFIle.getAbsolutePath());
            }else{
                Log.v(TAG, "⚠️⭕ CANT Deleted " + destFIle.getAbsolutePath());
            }
        }

        try (FileOutputStream out = new FileOutputStream(destFIle)) {

            bitmap.compress(android.graphics.Bitmap.CompressFormat.WEBP, 70, out);
            Log.v(TAG,"Saved "+destFIle.getAbsolutePath());

        }catch (Exception e) {
            Log.e(TAG, "Failed to copy file", e);
        }

    }

    public static String SaveImage(Bitmap finalBitmap, String fileName, int packId) {
        Log.v(TAG,"Saved "+packId);

        if(STICKER_ASSET_PATH == null){
            Log.e(TAG,"STICKER_ASSET_PATH is null ");
        }

        File packDir = new File(STICKER_ASSET_PATH + "/" + String.valueOf(packId));
        packDir.mkdirs();
        File stickerFIle = new File(packDir, fileName);

        if (stickerFIle.exists()){
            stickerFIle.delete();
        }


        try (FileOutputStream out = new FileOutputStream(stickerFIle)) {
            finalBitmap.compress(Bitmap.CompressFormat.WEBP, 90, out);
        }catch (Exception e) {
            e.printStackTrace();
        }

        Log.v(TAG,"Saved "+stickerFIle.getAbsolutePath());

        return stickerFIle.getAbsolutePath();
    }

    public static void copyFiles(ContentValues pack) {
        ContentValues packNew = new ContentValues();
        try{
            String identifier = pack.getAsString(COL_IDENTIFIER);
            String tray = pack.getAsString(COL_TRAY);

            Bitmap trayBitmap = BitmapFactory.decodeFile(tray);

        }catch ( Exception e ) {
//            e.printStackTrace();
            Log.e(TAG, "Failed to copy files", e);
        }
    }
}
