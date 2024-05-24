package com.whatsappstickersmanager;

import static com.whatsappstickersmanager.Const.Match.MATCHER;
import static com.whatsappstickersmanager.Const.Match.METADATA;
import static com.whatsappstickersmanager.Const.Match.METADATA_CODE;
import static com.whatsappstickersmanager.Const.Match.METADATA_CODE_FOR_SINGLE_PACK;
import static com.whatsappstickersmanager.Const.Match.STICKERS;
import static com.whatsappstickersmanager.Const.Match.STICKERS_ASSET;
import static com.whatsappstickersmanager.Const.Match.STICKERS_ASSET_CODE;
import static com.whatsappstickersmanager.Const.Match.STICKERS_CODE;
import static com.whatsappstickersmanager.Const.Match.STICKER_PACK_TRAY_ICON_CODE;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whatsappstickersmanager.ContentProvider.MyCursor;
import com.whatsappstickersmanager.ContentProvider.Sticker;
import com.whatsappstickersmanager.ContentProvider.StickerPack;
import com.whatsappstickersmanager.database.StickerPackDAO;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class StickerContentProvider extends ContentProvider {
    private static final String TAG = StickerContentProvider.class.getSimpleName();

    // GROUP 0
    @Override
    public boolean onCreate() {

        Log.v(TAG, "onCreate: ");
        final String authority = WhatsappStickersManagerModule.getContentProviderAuthority(getContext());
        if (!authority.startsWith(Objects.requireNonNull(getContext()).getPackageName())) {
            throw new IllegalStateException("your authority (" + authority + ") for the content provider should start with your package name: " + getContext().getPackageName());
        }

        //the call to get the metadata for the sticker packs.
        MATCHER.addURI(authority, METADATA, METADATA_CODE);

        //the call to get the metadata for single sticker pack. * represent the identifier
        MATCHER.addURI(authority, METADATA + "/*", METADATA_CODE_FOR_SINGLE_PACK);

        //gets the list of stickers for a sticker pack, * respresent the identifier.
        MATCHER.addURI(authority, STICKERS + "/*", STICKERS_CODE);

        for (StickerPack stickerPack : new StickerPackDAO(getContext()).getStickerPacks()) {
            MATCHER.addURI(authority, STICKERS_ASSET + "/" + stickerPack.identifier + "/" + stickerPack.trayImageFile, STICKER_PACK_TRAY_ICON_CODE);

            for (Sticker sticker : stickerPack.getStickers()) {
                MATCHER.addURI(authority, STICKERS_ASSET + "/" + stickerPack.identifier + "/" + sticker.imageFileName, STICKERS_ASSET_CODE);
            }
        }

        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, String selection,  String[] selectionArgs, String sortOrder) {
        final int code = MATCHER.match(uri);

        Log.v(TAG,"Query ");

        switch (code) {
            case METADATA_CODE:
                return MyCursor.getCursorForAllStickerPacks(uri, getContext());
            case METADATA_CODE_FOR_SINGLE_PACK:
                return MyCursor.getCursorForSingleStickerPack(uri, getContext());
            case STICKERS_CODE:
                return MyCursor.getCursorForStickersOfStickerPack(uri, getContext());
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) {
        final int matchCode = MATCHER.match(uri);
//        if (matchCode == STICKERS_ASSET_CODE || matchCode == STICKER_PACK_TRAY_ICON_CODE) {
//            return getImageAsset(uri);
//        }
        Log.v(TAG,"_____________" + matchCode + ", uri "+uri);

        AssetManager am = Objects.requireNonNull(getContext()).getAssets();
        final List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() != 3) {
            Log.w(TAG,"path segments should be 3, uri is: " + uri);
            throw new IllegalArgumentException("path segments should be 3, uri is: " + uri);
        }

        // filename.extention
        String fileName = pathSegments.get(pathSegments.size() - 1);
        final String identifier = pathSegments.get(pathSegments.size() - 2);

        if (TextUtils.isEmpty(identifier)) {
            Log.w(TAG,"identifier is empty, uri: " + uri);
            throw new IllegalArgumentException("identifier is empty, uri: " + uri);}
        if (TextUtils.isEmpty(fileName)) {
            Log.w(TAG,"file name is empty, uri: " + uri);
            throw new IllegalArgumentException("file name is empty, uri: " + uri);
        }

        boolean exists = new StickerPackDAO(getContext()).isFilenameExistInPack(identifier, fileName);


        if(exists){ // TODO remove tray boolean
            return fetchFile(uri, am, identifier, fileName);
        }

        return null;
    }


    @Override // ❌
    public String getType(@NonNull Uri uri) {
        final int matchCode = MATCHER.match(uri);
         Log.w(TAG,"getType()  ");

        switch (matchCode) {
            case METADATA_CODE:
                Log.v(TAG,"getType() METADATA_CODE  "+uri);
                return "vnd.android.cursor.dir/vnd." + WhatsappStickersManagerModule.getContentProviderAuthority(getContext()) + "." + METADATA;
            case METADATA_CODE_FOR_SINGLE_PACK:
                return "vnd.android.cursor.item/vnd." + WhatsappStickersManagerModule.getContentProviderAuthority(getContext()) + "." + METADATA;
            case STICKERS_CODE:
                return "vnd.android.cursor.dir/vnd." + WhatsappStickersManagerModule.getContentProviderAuthority(getContext()) + "." + STICKERS;
            case STICKERS_ASSET_CODE:
                return "image/webp";
            case STICKER_PACK_TRAY_ICON_CODE:
                return "image/png";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
    // CRUD
    @Override  // ❌
    public int delete(@NonNull Uri uri, @Nullable String selection, String[] selectionArgs) {throw new UnsupportedOperationException("Not supported");}
    @Override // ❌
    public Uri insert(@NonNull Uri uri, ContentValues values) {throw new UnsupportedOperationException("Not supported");}
    @Override // ❌
    public int update(@NonNull Uri uri, ContentValues values, String selection,  String[] selectionArgs) {
        Log.v(TAG, "update: ");
        return 0;
    }

    // Group 2
    private AssetFileDescriptor fetchFile( @NonNull Uri uri, @NonNull AssetManager am, @NonNull String identifier, @NonNull String fileName) {

        File file = new File(getContext().getFilesDir(), STICKERS_ASSET + "/" + identifier + "/" + fileName);
        try {
            return new AssetFileDescriptor(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY), 0L, -1L);
        } catch (Exception e) {
            Log.v(TAG,"IOException fetchFile(), uri:" + uri);
//            return am.openFd("1" + "/" + "namaskar.webp");
            return null;
        }
    }


}