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
            finalBitmap.compress(Bitmap.CompressFormat.WEBP, 70, out);
            finalBitmap.recycle();
            return destinationFIle.getAbsolutePath();
        }catch (Exception e) {
            Log.e(TAG, "Failed to save file", e);
            return null;
        }

    }

    public static void emptyDirectory(File directory) {
        for (File f : directory.listFiles()) {
            deleteDirectory(f);
        }

    }
    public static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            String[] children = directory.list();
            for (int i = 0; i < children.length; i++) {
                deleteDirectory(new File(directory, children[i]));
            }
        }
        // Delete the directory (or file) whether it's empty or not
        directory.delete();
    }

    public static void deleteDirectories(File... directories) {
        for (File directory : directories) {
            if (directory.exists()) {
                deleteDirectory(directory);
            }
        }
    }

    public static boolean deleteFile(File file) {
        return file.exists() && file.delete();
    }

}
