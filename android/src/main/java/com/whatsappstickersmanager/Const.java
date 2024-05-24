package com.whatsappstickersmanager;

import android.content.UriMatcher;

public class Const {

    public static final int ADD_PACK_CODE = 200;

    public static class Pack {
        public static final String COL_ANDROID = "android_play_store_link";
        public static final String COL_IOS = "ios_app_store_link";
        public static final String COL_PRIVACY = "privacy_policy_website";
        public static final String COL_LICENSE = "license_agreement_website";
        public static final String COL_PUB_LINK = "publisher_website";
        public static final String COL_PUB_EMAIL = "publisher_email";
        public static final String COL_IDENTIFIER = "identifier";
        public static final String COL_NAME = "name";
        public static final String COL_PUBLISHER = "publisher";
        public static final String COL_TRAY = "tray_image_file";
        public static final String COL_VERSION = "image_data_version";
        public static final String COL_AVOID_CACHE = "avoid_cache";
        public static final String COL_ANIMATED = "animated_sticker_pack";
        public static final String COL_STICKERS = "stickers";
    }

    public static class Sticker {
        public static final String COL_STICKER_ID = "sticker_id";
        public static final String COL_IDENTIFIER_IN_STICKER = Pack.COL_IDENTIFIER;
        public static final String COL_IMAGE_FILE = "image_file";
        public static final String COL_EMOJIS = "emojis";
    }

    public static class Extra {
        public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
        public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
        public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";
    }

    public static class Query {
        /**
         * Do not change the strings listed below, as these are used by WhatsApp. And changing these will break the interface between sticker app and WhatsApp.
         */
        public static final String STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier";
        public static final String STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name";
        public static final String STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher";
        public static final String STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon";
        public static final String ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link";
        public static final String IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link";
        public static final String PUBLISHER_EMAIL = "sticker_pack_publisher_email";
        public static final String PUBLISHER_WEBSITE = "sticker_pack_publisher_website";
        public static final String PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website";
        public static final String LICENSE_AGREENMENT_WEBSITE = "sticker_pack_license_agreement_website";
        public static final String IMAGE_DATA_VERSION = "image_data_version"; // #373
        public static final String AVOID_CACHE = "whatsapp_will_not_cache_stickers"; // #373
        public static final String ANIMATED_STICKER_PACK = "animated_sticker_pack"; // #373

        //
        public static final String STICKER_FILE_NAME_IN_QUERY = "sticker_file_name";
        public static final String STICKER_FILE_EMOJI_IN_QUERY = "sticker_emoji";

        //
        public static final String CONTENT_FILE_NAME = "contents.json";

    }

    public static class Match {
        /**
         * Do not change the values in the UriMatcher because otherwise, WhatsApp will not be able to fetch the stickers from the ContentProvider.
         */
        public static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        public static final String METADATA = "metadata";
        public static final int METADATA_CODE = 1;

        public static final int METADATA_CODE_FOR_SINGLE_PACK = 2;

        public static final String STICKERS = "stickers";
        public static final int STICKERS_CODE = 3;

        public static final String STICKERS_ASSET = "stickers_asset";
        public static final int STICKERS_ASSET_CODE = 4;

        public static final int STICKER_PACK_TRAY_ICON_CODE = 5;
//        public static final Uri AUTHORITY_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(BuildConfig.CONTENT_PROVIDER_AUTHORITY).appendPath(StickerContentProvider.METADATA).build();

    }
}


