package com.whatsappstickersmanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.animated.webp.WebPImage;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import java.io.IOException;

class StickerPackValidator {
    static String TAG = StickerPackValidator.class.getSimpleName();
    private static final int STATIC_STICKER_FILE_LIMIT_KB = 100;
    private static final int ANIMATED_STICKER_FILE_LIMIT_KB = 500;
    private static final int EMOJI_MAX_LIMIT = 3;
    private static final int EMOJI_MIN_LIMIT = 1;
    private static final int IMAGE_HEIGHT = 512;
    private static final int IMAGE_WIDTH = 512;
    private static final int STICKER_SIZE_MIN = 3;
    private static final int STICKER_SIZE_MAX = 30;
    private static final int CHAR_COUNT_MAX = 128;
    private static final long KB_IN_BYTES = 1024;
    private static final int TRAY_IMAGE_FILE_SIZE_MAX_KB = 50;
    private static final int TRAY_IMAGE_DIMENSION_MIN = 24;
    private static final int TRAY_IMAGE_DIMENSION_MAX = 512;
    private static final int ANIMATED_STICKER_FRAME_DURATION_MIN = 8;
    private static final int ANIMATED_STICKER_TOTAL_DURATION_MAX = 10 * 1000; //ms
    private static final String PLAY_STORE_DOMAIN = "play.google.com";
    private static final String APPLE_STORE_DOMAIN = "itunes.apple.com";

    static WritableMap validateStaticFile(final String filePath, final boolean animatedStickerPack) {
        WritableMap result = new WritableNativeMap();
        result.putString("filePath", filePath);
        try {
            WritableMap info = new WritableNativeMap();

            final byte[] stickerInBytes = FileActions.toByteArray(filePath);

            int size = (int) (stickerInBytes.length / KB_IN_BYTES);
            info.putInt("size", size);

            final WebPImage webPImage = WebPImage.createFromByteArray(stickerInBytes, ImageDecodeOptions.defaults());
            int height = webPImage.getHeight();
            int width = webPImage.getWidth();
            int frameCount = webPImage.getFrameCount();
            boolean animatedSticker = frameCount > 1;
            info.putInt("width", width);
            info.putInt("height", height);
            info.putInt("frameCount", frameCount);
            info.putBoolean("animatedSticker", animatedSticker);

            boolean validSize = stickerInBytes.length <= STATIC_STICKER_FILE_LIMIT_KB * KB_IN_BYTES;
            boolean validHeight = webPImage.getHeight() == IMAGE_HEIGHT;
            boolean validWidth = webPImage.getWidth() == IMAGE_WIDTH;
            boolean validFrameCount = !(webPImage.getFrameCount() > 1);
            boolean validStickerType = animatedSticker == animatedStickerPack ;
            result.putMap("info", info);

            boolean validSticker = validSize && validHeight && validWidth && validFrameCount && validStickerType;
            result.putBoolean("validSticker", validSticker);

            // error
            WritableMap errorMap = new WritableNativeMap();
            if (!validSize) {
                errorMap.putString("sizeError", "Size exceeds maximum allowed size");
            }
            if (!validHeight) {
                errorMap.putString("heightError", "Height is out of range");
            }
            if (!validWidth) {
                errorMap.putString("widthError", "Width is out of range");
            }
            if (!validFrameCount) {
                errorMap.putString("frameCountError", "Frame count is invalid");
            }
            if (!validStickerType) {
                errorMap.putString("stickerTypeError", "Sticker type is invalid");
            }
            result.putMap("error", errorMap);

            result.putBoolean("validSticker", validSticker);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error reading file in validateStaticFile: ", e);
            throw new RuntimeException("Error reading file in validateStaticFile: ",e);
        }

    }

    static WritableMap validateAnimatedStickerFil(final String filePath, final boolean animatedStickerPack) throws IllegalStateException {
        WritableMap result = new WritableNativeMap();
        result.putString("filePath", filePath);
        try {
            WritableMap info = new WritableNativeMap();

            final byte[] stickerInBytes = FileActions.toByteArray(filePath);

            //
            int size = (int) (stickerInBytes.length / KB_IN_BYTES);
            info.putInt("size", size);
            //
            final WebPImage webPImage = WebPImage.createFromByteArray(stickerInBytes, ImageDecodeOptions.defaults());
            int width = webPImage.getWidth();
            int height = webPImage.getHeight();
            int frameCount = webPImage.getFrameCount();
            int totalDuration = webPImage.getDuration();
            boolean animatedSticker = frameCount > 1;
            info.putInt("width", width);
            info.putInt("height", height);
            info.putInt("frameCount", frameCount);
            info.putInt("totalDuration", totalDuration);
            info.putBoolean("animatedSticker", animatedSticker);
            //
            WritableMap frameDurations = checkFrameDurationsForAnimatedSticker(webPImage.getFrameDurations());
            ReadableArray invalidDelayIndexes = frameDurations.getArray("invalidDurationIndexes");
            ReadableArray frameDelays =  frameDurations.getArray("delaysArray");
            info.putArray("invalidDelayIndexes",invalidDelayIndexes);
            info.putArray("frameDelays",frameDelays);


            result.putMap("info", info);

            //
            boolean validSize = !(stickerInBytes.length > ANIMATED_STICKER_FILE_LIMIT_KB * KB_IN_BYTES);
            boolean validHeight = webPImage.getHeight() == IMAGE_HEIGHT;
            boolean validWidth = webPImage.getWidth() == IMAGE_WIDTH;
            boolean validDuration = !(webPImage.getDuration() > ANIMATED_STICKER_TOTAL_DURATION_MAX);
            boolean validFrameCount = !(webPImage.getFrameCount() <= 1);
            boolean validStickerType = animatedSticker == animatedStickerPack ;
            boolean validDelays =  invalidDelayIndexes == null || invalidDelayIndexes.size() == 0;

            boolean validSticker = validSize && validHeight && validWidth && validFrameCount && validStickerType && validDuration && validDelays;
            result.putBoolean("validSticker", validSticker);


            //
            WritableMap errorMap = new WritableNativeMap();
            if (!validSize) {
                errorMap.putString("sizeError", "Size exceeds maximum allowed size");
            }
            if (!validHeight) {
                errorMap.putString("heightError", "Height is out of range");
            }
            if (!validWidth) {
                errorMap.putString("widthError", "Width is out of range");
            }
            if (!validDuration) {
                errorMap.putString("durationError", "Total duration exceeds maximum allowed duration");
            }
            if (!validFrameCount) {
                errorMap.putString("frameCountError", "Frame count is invalid");
            }
            if (!validStickerType) {
                errorMap.putString("stickerTypeError", "Sticker type is invalid");
            }
            if (!validDelays) {
                errorMap.putString("frameDelayError", "frame delay exceeds the limit");
            }

            result.putMap("error", errorMap);

        } catch (IOException e) {
            throw new RuntimeException("Error reading file in validateStickerFileAnim: ",e);
        }
        return result;
    }

    static WritableMap validateTrayIconFile(String filePath){
        WritableMap result = new WritableNativeMap();
        result.putString("filePath", filePath);
        try{
            final byte[] trayBytes = FileActions.toByteArray(filePath);
            // 1. size
            int size = (int) (trayBytes.length / KB_IN_BYTES);
            // 2. dimensions
            Bitmap bitmap = BitmapFactory.decodeByteArray(trayBytes, 0, trayBytes.length);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();


            // 3. requiremnet
            boolean validLength  = !(trayBytes.length > TRAY_IMAGE_FILE_SIZE_MAX_KB * KB_IN_BYTES);
            boolean validHeight  = !(bitmap.getHeight() > TRAY_IMAGE_DIMENSION_MAX || bitmap.getHeight() < TRAY_IMAGE_DIMENSION_MIN);
            boolean validWidth  = !(bitmap.getWidth() > TRAY_IMAGE_DIMENSION_MAX || bitmap.getWidth() < TRAY_IMAGE_DIMENSION_MIN);
            boolean validTray = validLength && validHeight && validWidth;

            // error
            WritableMap errorMap = new WritableNativeMap();
            if (!validLength) {
                errorMap.putString("sizeError", "Size exceeds maximum allowed size");
            }
            if (!validHeight) {
                errorMap.putString("heightError", "Height is out of range");
            }
            if (!validWidth) {
                errorMap.putString("widthError", "Width is out of range");
            }

            // set result
            result.putInt("size", size);
            result.putInt("height",height);
            result.putInt("width",width);

            result.putBoolean("validTrayIcon",validTray);

            return result;
        } catch (IOException e) {
            Log.e(TAG, "Error reading file in validateTrayIcon: ", e);
            throw new RuntimeException(e);
        }
    }

    private static WritableMap checkFrameDurationsForAnimatedSticker(@NonNull final int[] frameDurations) {
        WritableMap duration = new WritableNativeMap();
        WritableArray durationsArray = new WritableNativeArray();
        WritableArray invalidDurationsArray = new WritableNativeArray();
        for (int i = 0; i < frameDurations.length; i++) {
            int frameDuration = frameDurations[i];
            durationsArray.pushInt(frameDuration);
            if (frameDuration < ANIMATED_STICKER_FRAME_DURATION_MIN) {
                invalidDurationsArray.pushInt(i);
            }
        }
        duration.putArray("durationsArray",durationsArray);
        duration.putArray("invalidIndexes",invalidDurationsArray);
        return duration;
    }

}
