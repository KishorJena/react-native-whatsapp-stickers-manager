export type Emoji = string; // e.g. "😎", "⭕"

export interface Sticker {
  image_file: string; 
  emojis: Emoji[]; 
}

export interface StickerPack {
  android_play_store_link: string;
  ios_app_store_link: string;
  publisher_email: string;
  publisher_website: string;
  privacy_policy_website: string;
  license_agreement_website: string;
  name: string; // required, max 128 characters
  publisher: string; // required, max 128 characters
  tray_image_file: string;
  image_data_version: string; // required, initial value is "1"
  avoid_cache: boolean; // default is false
  animated_sticker_pack: boolean;
  stickers: Sticker[]; // required
}

export interface UpdatePackOptions {
  name: string;
  publisher: string;
}
