package com.whatsappstickersmanager;

import androidx.annotation.NonNull;

import com.facebook.animated.webp.WebPImage;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class StickerPackValidator {
    private static final int STATIC_STICKER_FILE_LIMIT_KB = 100;
    private static final int ANIMATED_STICKER_FILE_LIMIT_KB = 500;
    static final int EMOJI_MAX_LIMIT = 3;
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


    public static byte[] toByteArray(String path) throws IOException {
        File imageFile = new File(path);
        FileInputStream fileInputStream = new FileInputStream(imageFile);
        byte[] imageData = new byte[1024];
        int bytesRead;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((bytesRead = fileInputStream.read(imageData)) != -1) {
            bos.write(imageData, 0, bytesRead);
        }
        fileInputStream.close();
        return bos.toByteArray();
    }

    static WritableMap validateStaticFile(final String filePath, final boolean animatedStickerPack) throws IllegalStateException {
        WritableMap result = new WritableNativeMap();
        result.putString("filePath", filePath);

        try {
            WritableMap report = new WritableNativeMap();

            final byte[] stickerInBytes = toByteArray(filePath);
            int size = (int) (stickerInBytes.length / KB_IN_BYTES);
            report.putInt("size", size);

            final WebPImage webPImage = WebPImage.createFromByteArray(stickerInBytes, ImageDecodeOptions.defaults());

            int height = webPImage.getHeight();
            report.putInt("height", height);
            int width = webPImage.getWidth();
            report.putInt("width", width);
            int frameCount = webPImage.getFrameCount();
            report.putInt("frameCount", frameCount);
            boolean animatedSticker = frameCount > 1;
            report.putBoolean("animatedSticker", animatedSticker);

            boolean validSize = stickerInBytes.length <= STATIC_STICKER_FILE_LIMIT_KB * KB_IN_BYTES;
            boolean validHeight = webPImage.getHeight() == IMAGE_HEIGHT;
            boolean validWidth = webPImage.getWidth() == IMAGE_WIDTH;
            boolean validFrameCount = !(webPImage.getFrameCount() > 1);
            boolean validStickerTyep = animatedSticker == animatedStickerPack ;

            boolean validSticker = validSize && validHeight && validWidth && validFrameCount && validStickerTyep;
            result.putMap("report", report);

            result.putBoolean("validSticker", validSticker);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error reading file in validateStaticFile: ",e);
        }

    }

    static WritableMap validateAnimatedStickerFil(final String filePath, final boolean animatedStickerPack) throws IllegalStateException {
        WritableMap result = new WritableNativeMap();
        try {
            WritableMap report = new WritableNativeMap();

            final byte[] stickerInBytes = toByteArray(filePath);

            // 1. size
            int size = (int) (stickerInBytes.length / KB_IN_BYTES);
            report.putInt("size", size);

            final WebPImage webPImage = WebPImage.createFromByteArray(stickerInBytes, ImageDecodeOptions.defaults());

            // 2 Height
            int height = webPImage.getHeight();
            report.putInt("height", height);

            // 3.width
            int width = webPImage.getWidth();
            report.putInt("width", width);

            // 4. frameCount
            int frameCount = webPImage.getFrameCount();
            report.putInt("frameCount", frameCount);

            // 5. total Duration
            int totalDuration = webPImage.getDuration();
            report.putInt("totalDuration", totalDuration);

            // 6. animatedSticker
            boolean animatedSticker = frameCount > 1;
            report.putBoolean("animatedSticker", animatedSticker);

            // 7. frame Duration
            WritableMap frameDuration = checkFrameDurationsForAnimatedSticker(webPImage.getFrameDurations());
            ReadableArray invalidDurationIndexes = frameDuration.getArray("invalidDurationIndexes");
            ReadableArray durationsArray =  frameDuration.getArray("durationsArray");
            report.putArray("invalidDelaysIndexes",invalidDurationIndexes);
            report.putArray("framesDelay",durationsArray);

            boolean validSize = !(stickerInBytes.length > ANIMATED_STICKER_FILE_LIMIT_KB * KB_IN_BYTES);
            boolean validHeight = webPImage.getHeight() == IMAGE_HEIGHT;
            boolean validWidth = webPImage.getWidth() == IMAGE_WIDTH;
            boolean validDuration = !(webPImage.getDuration() > ANIMATED_STICKER_TOTAL_DURATION_MAX);
            boolean validFrameCount = !(webPImage.getFrameCount() <= 1);
            boolean validStickerTyep = animatedSticker == animatedStickerPack ;
            boolean validSticker = validSize && validHeight && validWidth && validFrameCount && validStickerTyep && validDuration;


            result.putMap("report", report);

            result.putBoolean("validSticker", validSticker);

        } catch (IOException e) {
            throw new RuntimeException("Error reading file in validateStickerFileAnim: ",e);
        }
        return result;
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
