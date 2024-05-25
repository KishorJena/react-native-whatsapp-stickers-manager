export interface StickerPack {
  android_play_store_link: string; // can be empty
  ios_app_store_link: string; // can be empty
  publisher_email: string; // can be empty
  publisher_website: string; // can be empty
  privacy_policy_website: string; // can be empty
  license_agreement_website: string; // can be empty
  name: string; // required, max 128 characters
  publisher: string; // required, max 128 characters
  tray_image_file: string; // required
  image_data_version: string; // required
  avoid_cache: boolean; // required 
  animated_sticker_pack: boolean; // required 
  stickers: Sticker[]; // required
}
export interface Sticker {
  image_file: string; // required
  emojis: Emoji[]; // required 1 to 3 emojis
}

export type Emoji = string; // e.g. "😀"
