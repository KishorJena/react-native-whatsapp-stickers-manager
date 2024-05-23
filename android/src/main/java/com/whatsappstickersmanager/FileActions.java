package com.whatsappstickersmanager;

import static com.whatsappstickersmanager.WhatsappStickersManagerModule.STICKER_ASSET_PATH;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class FileActions {
    private static final String TAG = FileActions.class.getSimpleName();

    public static String SaveImage(String source, String destName, int identifier) {

        File parent = new File(STICKER_ASSET_PATH + "/" + identifier);
        parent.mkdirs();

        File destinationFIle = new File(parent, destName);
        if (destinationFIle.exists()) destinationFIle.delete();

        try (FileOutputStream out = new FileOutputStream(destinationFIle)) {
            Bitmap finalBitmap = BitmapFactory.decodeFile(source);
            finalBitmap.compress(Bitmap.CompressFormat.WEBP, 90, out);
            return destinationFIle.getAbsolutePath();
        }catch (Exception e) {
            Log.e(TAG, "Failed to save file", e);
            return null;
        }

    }

}
