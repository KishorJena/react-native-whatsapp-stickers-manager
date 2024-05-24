package com.whatsappstickersmanager.ContentProvider;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.whatsappstickersmanager.Const;
import com.whatsappstickersmanager.database.StickerPackDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// GROUP  1 Cursor
// ❌⭕
public class MyCursor {
    private static final String TAG = Cursor.class.getSimpleName();
    private final Context context;

    public MyCursor(Context context) {
        this.context = context;
    }

    // ❌
    public static Cursor getCursorForAllStickerPacks(@NonNull Uri uri, Context context) {
        return getStickerPackInfo(uri, new StickerPackDAO(context).getStickerPacks(), context);
    }
    public static Cursor getCursorForSingleStickerPack(@NonNull Uri uri, Context context) {
        Log.v(TAG,"getCursorForSingleStickerPack() B ");
        final String identifier = uri.getLastPathSegment();
        List<StickerPack> stickerPacks = new StickerPackDAO(context).getStickerPacks();
        Log.v(TAG,"identifier  "+identifier);

        for (StickerPack stickerPack : stickerPacks) {
            if (identifier.equals(stickerPack.identifier)) {
                Log.v(TAG,"stickerPack.identifier "+stickerPack.identifier);
                return getStickerPackInfo(uri, Collections.singletonList(stickerPack), context);
            }
        }
        Log.v(TAG,"Did not match . going for list ");
        return getStickerPackInfo(uri, new ArrayList<StickerPack>(), context);
    }
    @NonNull
    public static Cursor getCursorForStickersOfStickerPack(@NonNull Uri uri, Context context) {
        Log.v(TAG,"getCursorForStickersOfStickerPack() D ");
        final String identifier = uri.getLastPathSegment();
//        Log.v(TAG,"identifier ??? "+identifier);
        MatrixCursor cursor = new MatrixCursor(new String[]{Const.Query.STICKER_FILE_NAME_IN_QUERY, Const.Query.STICKER_FILE_EMOJI_IN_QUERY});
        for (StickerPack stickerPack : new StickerPackDAO(context).getStickerPacks()) {
            if (identifier.equals(stickerPack.identifier)) {
                for (Sticker sticker : stickerPack.getStickers()) {
                    cursor.addRow(new Object[]{sticker.imageFileName, TextUtils.join(",", sticker.emojis)});
                }
            }
        }
        cursor.setNotificationUri(Objects.requireNonNull(context).getContentResolver(), uri);
        return cursor;
    }
    @NonNull
    private static Cursor getStickerPackInfo(@NonNull Uri uri, @NonNull List<StickerPack> stickerPackList, Context context) {
        Log.v(TAG,"getStickerPackInfo() C ");
        // Log.d("ReactNative2","getStickerPackInfo() | stickerPack.imageDataVersion here: "+String.valueOf(stickerPack.imageDataVersion));
        MatrixCursor cursor = new MatrixCursor(
                new String[]{
                        Const.Query.STICKER_PACK_IDENTIFIER_IN_QUERY,
                        Const.Query.STICKER_PACK_NAME_IN_QUERY,
                        Const.Query.STICKER_PACK_PUBLISHER_IN_QUERY,
                        Const.Query.STICKER_PACK_ICON_IN_QUERY,
                        Const.Query.ANDROID_APP_DOWNLOAD_LINK_IN_QUERY,
                        Const.Query.IOS_APP_DOWNLOAD_LINK_IN_QUERY,
                        Const.Query.PUBLISHER_EMAIL,
                        Const.Query.PUBLISHER_WEBSITE,
                        Const.Query.PRIVACY_POLICY_WEBSITE,
                        Const.Query.LICENSE_AGREENMENT_WEBSITE,
                        Const.Query.IMAGE_DATA_VERSION,
                        Const.Query.AVOID_CACHE,
                        Const.Query.ANIMATED_STICKER_PACK,
                });
        for (StickerPack stickerPack : stickerPackList) {
            Log.v(TAG,"getStickerPackInfo() builder.add(stickerPack.identifier) :" +
                    ""+stickerPack.identifier);
            MatrixCursor.RowBuilder builder = cursor.newRow();
            builder.add(stickerPack.identifier);
            builder.add(stickerPack.name);
            builder.add(stickerPack.publisher);
            builder.add(stickerPack.trayImageFile);
            builder.add(stickerPack.androidPlayStoreLink);
            builder.add(stickerPack.iosAppStoreLink);
            builder.add(stickerPack.publisherEmail);
            builder.add(stickerPack.publisherWebsite);
            builder.add(stickerPack.privacyPolicyWebsite);
            builder.add(stickerPack.licenseAgreementWebsite);
            builder.add(stickerPack.imageDataVersion);  // #373
            builder.add(stickerPack.avoidCache ? 1 : 0);    // #373
            builder.add(stickerPack.animatedStickerPack ? 1 : 0);   // #373
        }
        cursor.setNotificationUri(Objects.requireNonNull(context).getContentResolver(), uri);

        return cursor;
    }

}
