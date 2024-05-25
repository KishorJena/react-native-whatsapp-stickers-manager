import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-whatsapp-stickers-manager' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const WhatsappStickersManagerModule = isTurboModuleEnabled
  ? require('./NativeWhatsappStickersManager').default
  : NativeModules.WhatsappStickersManager;

const WhatsappStickersManager = WhatsappStickersManagerModule
  ? WhatsappStickersManagerModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const Manager = WhatsappStickersManager;  

interface CreatePackData {
  android_play_store_link?: string;
  ios_app_store_link?: string;
  publisher_email?: string;
  publisher_website?: string;
  privacy_policy_website?: string;
  license_agreement_website?: string;
  name: string;
  publisher: string;
  tray_image_file?: string;
  image_data_version?: string;
  avoid_cache?: boolean;
  animated_sticker_pack?: boolean;
  stickers: {
    image_file: string;
    emojis?: string[];
  }[];
}
const createPack = (packData) => {
  
  const validatedData = ;
  return Manager.createPack();
};
export default WhatsappStickersManager;
