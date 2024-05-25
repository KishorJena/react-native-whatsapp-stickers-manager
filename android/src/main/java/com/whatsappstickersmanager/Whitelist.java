package com.whatsappstickersmanager;

/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

import static com.whatsappstickersmanager.WhatsappStickersManagerModule.getContentProviderAuthority;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

class Whitelist {
    private static final String AUTHORITY_QUERY_PARAM = "authority";
    private static final String IDENTIFIER_QUERY_PARAM = "identifier";
    private static String STICKER_APP_AUTHORITY;
    static final String CONSUMER_WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    static final String SMB_WHATSAPP_PACKAGE_NAME = "com.whatsapp.w4b";
    private static final String CONTENT_PROVIDER = ".provider.sticker_whitelist_check";
    private static final String QUERY_PATH = "is_whitelisted";
    private static final String QUERY_RESULT_COLUMN_NAME = "result";

    static WritableMap isWhitelisted(@NonNull Context context, @NonNull String identifier, String[] customPackageNames) {
        STICKER_APP_AUTHORITY = getContentProviderAuthority(context);
        WritableMap result = new WritableNativeMap();
        WritableMap apps = new WritableNativeMap();
        WritableMap whitelistStatus = new WritableNativeMap();
        PackageManager packageManager = context.getPackageManager();
        try {

            boolean smbInstalled = isWhatsAppSmbAppInstalled(packageManager);
            boolean consumerInstalled = isWhatsAppConsumerAppInstalled(packageManager);
            apps.putBoolean("smb", smbInstalled);
            apps.putBoolean("consumer", consumerInstalled);



            if (smbInstalled || consumerInstalled) {

                if (smbInstalled) {
                    boolean smbResult = isStickerPackWhitelistedInWhatsAppSmb(context, identifier);
                    whitelistStatus.putBoolean("smb", smbResult);
                }
                if (consumerInstalled) {
                    boolean consumerResult = isStickerPackWhitelistedInWhatsAppConsumer(context, identifier);
                    whitelistStatus.putBoolean("consumer", consumerResult);
                }

            }

            // for Unofficial apps
            for( String customPackageName : customPackageNames){
                boolean isInstalled = isPackageInstalled(customPackageName, packageManager);
                apps.putBoolean(customPackageName, isInstalled);
                if(isInstalled){
                    boolean isWhitelisted = isWhitelistedFromProvider(context, identifier, customPackageName);
                    whitelistStatus.putBoolean(customPackageName, isWhitelisted);
                }
            }

            result.putMap("package_availability", apps);
            result.putMap("whitelist_status", whitelistStatus);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return  result;
    }

    private static boolean isWhitelistedFromProvider(@NonNull Context context, @NonNull String identifier, String whatsappPackageName) {
        final PackageManager packageManager = context.getPackageManager();
        if (isPackageInstalled(whatsappPackageName, packageManager)) {
            final String whatsappProviderAuthority = whatsappPackageName + CONTENT_PROVIDER;
            final ProviderInfo providerInfo = packageManager.resolveContentProvider(whatsappProviderAuthority, PackageManager.GET_META_DATA);
            // provider is not there. The WhatsApp app may be an old version.
            if (providerInfo == null) {
                return false;
            }

            final Uri queryUri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(whatsappProviderAuthority).appendPath(QUERY_PATH).appendQueryParameter(AUTHORITY_QUERY_PARAM, STICKER_APP_AUTHORITY).appendQueryParameter(IDENTIFIER_QUERY_PARAM, identifier).build();
            try (final Cursor cursor = context.getContentResolver().query(queryUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    final int whiteListResult = cursor.getInt(cursor.getColumnIndexOrThrow(QUERY_RESULT_COLUMN_NAME));
                    return whiteListResult == 1;
                }
            }
        } else {
            //if app is not installed, then don't need to take into its whitelist info into account.
            return true;
        }
        return false;
    }

    static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            //noinspection SimplifiableIfStatement
            if (applicationInfo != null) {
                return applicationInfo.enabled;
            } else {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static boolean isWhatsAppConsumerAppInstalled(PackageManager packageManager) {
        return Whitelist.isPackageInstalled(CONSUMER_WHATSAPP_PACKAGE_NAME, packageManager);
    }

    static boolean isWhatsAppSmbAppInstalled(PackageManager packageManager) {
        return Whitelist.isPackageInstalled(SMB_WHATSAPP_PACKAGE_NAME, packageManager);
    }

    static boolean isStickerPackWhitelistedInWhatsAppConsumer(@NonNull Context context, @NonNull String identifier) {
        return isWhitelistedFromProvider(context, identifier, CONSUMER_WHATSAPP_PACKAGE_NAME);
    }

    static boolean isStickerPackWhitelistedInWhatsAppSmb(@NonNull Context context, @NonNull String identifier) {
        return isWhitelistedFromProvider(context, identifier, SMB_WHATSAPP_PACKAGE_NAME);
    }
}

