package com.whatsappstickersmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;

import com.facebook.animated.webp.WebPImage;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

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

    static WritableMap validateStickerFile(final String filePath, final boolean animatedStickerPack) throws IllegalStateException {
        WritableMap result = new WritableNativeMap();
        try {
            final byte[] stickerInBytes = toByteArray(filePath);
            if (!animatedStickerPack && stickerInBytes.length > STATIC_STICKER_FILE_LIMIT_KB * KB_IN_BYTES) {
                int currentSize = (int) (stickerInBytes.length / KB_IN_BYTES);
                WritableMap map = new WritableNativeMap();
                result.putInt("currentSize", currentSize);
                result.putInt("limitSize", STATIC_STICKER_FILE_LIMIT_KB);
//                throw new IllegalStateException("static sticker should be less than " + STATIC_STICKER_FILE_LIMIT_KB + "KB, current file is " + stickerInBytes.length / KB_IN_BYTES + " KB, sticker pack identifier: " + identifier + ", filename: " + fileName);
            }
            if (animatedStickerPack && stickerInBytes.length > ANIMATED_STICKER_FILE_LIMIT_KB * KB_IN_BYTES) {
                int currentSize = (int) (stickerInBytes.length / KB_IN_BYTES);
                WritableMap map = new WritableNativeMap();
                map.putInt("currentSize",currentSize);
                map.putInt("limitSize", ANIMATED_STICKER_FILE_LIMIT_KB);
                result.putMap("fileSizeError", map);
//                throw new IllegalStateException("animated sticker should be less than " + ANIMATED_STICKER_FILE_LIMIT_KB + "KB, current file is " + stickerInBytes.length / KB_IN_BYTES + " KB, sticker pack identifier: " + identifier + ", filename: " + fileName);
            }
            try {
                final WebPImage webPImage = WebPImage.createFromByteArray(stickerInBytes, ImageDecodeOptions.defaults());
                if (webPImage.getHeight() != IMAGE_HEIGHT) {
                    WritableMap map = new WritableNativeMap();
                    int currentHeight = webPImage.getHeight();
                    map.putInt("currentHeight", currentHeight);
                    map.putInt("requiredHeight", IMAGE_HEIGHT);
                    result.putMap("fileSizeError", map);
//                    throw new IllegalStateException("sticker height should be " + IMAGE_HEIGHT + ", current height is " + webPImage.getHeight() + ", sticker pack identifier: " + identifier + ", filename: " + fileName);
                }
                if (webPImage.getWidth() != IMAGE_WIDTH) {
                    WritableMap map = new WritableNativeMap();
                    int currentWidth = webPImage.getWidth();
                    map.putInt("currentWidth", currentWidth);
                    map.putInt("requiredWidth", IMAGE_WIDTH);
                    result.putMap("fileSizeError", map);
//                    throw new IllegalStateException("sticker width should be " + IMAGE_WIDTH + ", current width is " + webPImage.getWidth() + ", sticker pack identifier: " + identifier + ", filename: " + fileName);
                }
                if (animatedStickerPack) {
                    if (webPImage.getFrameCount() <= 1) {
                        WritableMap map = new WritableNativeMap();
                        map.putInt("frameCount", webPImage.getFrameCount());
                        result.putMap("frameCountError", map);
//                        throw new IllegalStateException("this pack is marked as animated sticker pack, all stickers should animate, sticker pack identifier: " + identifier + ", filename: " + fileName);
                    }
//                    checkFrameDurationsForAnimatedSticker(webPImage.getFrameDurations(), identifier, fileName);
                    if (webPImage.getDuration() > ANIMATED_STICKER_TOTAL_DURATION_MAX) {
                        WritableMap map = new WritableNativeMap();
                        map.putInt("duration", webPImage.getDuration());
                        result.putMap("durationError", map);
//                        throw new IllegalStateException("sticker animation max duration is: " + ANIMATED_STICKER_TOTAL_DURATION_MAX + " ms, current duration is: " + webPImage.getDuration() + " ms, sticker pack identifier: " + identifier + ", filename: " + fileName);
                    }
                } else if (webPImage.getFrameCount() > 1) {
                    WritableMap map = new WritableNativeMap();
                    map.putInt("frameCount", webPImage.getFrameCount());
                    result.putMap("frameCountError", map);
//                    throw new IllegalStateException("this pack is not marked as animated sticker pack, all stickers should be static stickers, sticker pack identifier: " + identifier + ", filename: " + fileName);
                }
            } catch (IllegalArgumentException e) {
                WritableMap map = new WritableNativeMap();
                map.putString("message", e.getMessage());
                result.putMap("imageTypeError", map);

//                throw new IllegalStateException("Error parsing webp image, sticker pack identifier: " + identifier + ", filename: " + fileName, e);
            }
        } catch (IOException e) {
            WritableMap map = new WritableNativeMap();
            map.putString("message", e.getMessage());
            result.putMap("imageFileReadingError", map);

//            throw new IllegalStateException("cannot open sticker file: sticker pack identifier: " + identifier + ", filename: " + fileName, e);
        }
        return result;
    }

    private static void checkFrameDurationsForAnimatedSticker(@NonNull final int[] frameDurations, @NonNull final String identifier, @NonNull final String fileName) {
        WritableArray array = new WritableNativeArray();
        for (int frameDuration : frameDurations) {
            if (frameDuration < ANIMATED_STICKER_FRAME_DURATION_MIN) {
                WritableMap map = new WritableNativeMap();
                map.putInt("frameDuration", frameDuration);
                map.putString("fileName", fileName);
                array.pushMap(map);
//                throw new IllegalStateException("animated sticker frame duration limit is " + ANIMATED_STICKER_FRAME_DURATION_MIN + ", sticker pack identifier: " + identifier + ", filename: " + fileName);
            }
        }
    }

    private static void checkStringValidity(@NonNull String string) {
        String pattern = "[\\w-.,'\\s]+"; // [a-zA-Z0-9_-.' ]
        if (!string.matches(pattern)) {
            throw new IllegalStateException(string + " contains invalid characters, allowed characters are a to z, A to Z, _ , ' - . and space character");
        }
        if (string.contains("..")) {
            throw new IllegalStateException(string + " cannot contain ..");
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isValidWebsiteUrl(String websiteUrl) throws IllegalStateException {
        try {
            new URL(websiteUrl);
        } catch (MalformedURLException e) {
            Log.e("StickerPackValidator", "url: " + websiteUrl + " is malformed");
            throw new IllegalStateException("url: " + websiteUrl + " is malformed", e);
        }
        return URLUtil.isHttpUrl(websiteUrl) || URLUtil.isHttpsUrl(websiteUrl);

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isURLInCorrectDomain(String urlString, String domain) throws IllegalStateException {
        try {
            URL url = new URL(urlString);
            if (domain.equals(url.getHost())) {
                return true;
            }
        } catch (MalformedURLException e) {
            Log.e("StickerPackValidator", "url: " + urlString + " is malformed");
            throw new IllegalStateException("url: " + urlString + " is malformed");
        }
        return false;
    }
}
