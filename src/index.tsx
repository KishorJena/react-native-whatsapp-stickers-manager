import { NativeModules, Platform } from 'react-native';
import type { StickerPack } from './types'
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


// !!!!!!!!!!!!!!!!
//      P A C K 
// !!!!!!!!!!!!!!!!

//  Create
export function createPack(stickerPack: StickerPack): Promise<void> {
  if (!stickerPack.name || stickerPack.name.length > 128) {
    throw new Error("Name is required and must be 128 characters or less");
  }
  if (!stickerPack.publisher || stickerPack.publisher.length > 128) {
    throw new Error("Publisher is required and must be 128 characters or less");
  }
  if (!stickerPack.stickers || stickerPack.stickers.length < 3) {
    throw new Error("At least one sticker is required");
  }
  stickerPack.stickers.forEach((sticker) => {
    if (!sticker.image_file) {
      throw new Error("Image file is required for each sticker");
    }
    if (!sticker.emojis || sticker.emojis.length === 0) {
      sticker.emojis = ["😂"]; // default emojis
    }
  });


  return WhatsappStickersManager.createPack(stickerPack);
}

//  Retrieve 
export function getPack(identifier: number): Promise<any> {
  return WhatsappStickersManager.getPack(identifier);
}
export function getPacksAll(): Promise<any> {
  return WhatsappStickersManager.getPacksAll();
}

// Update ✅
export function updatePackMeta(identifier: number, options:{ name: string; publisher: string;}): Promise<any> {
  if (!options.name || options.name.length > 128) {
    throw new Error("Name is required and must be 128 characters or less");
  }
  if (!options.publisher || options.publisher.length > 128) {
    throw new Error("Publisher is required and must be 128 characters or less");
  }
  return WhatsappStickersManager.updatePackMeta(identifier);
}
export function updateTrayIcon(identifier:number, sticker_id:number, newImagePath:string): Promise<any> {
  return WhatsappStickersManager.updateTrayIcon(identifier, sticker_id, newImagePath);
}

// Delete ✅
export function deletePack(identifier: number): Promise<any> {
  return WhatsappStickersManager.deletePack(identifier);
}
export function deletePacksAll(): Promise<any> {
  return WhatsappStickersManager.deletePacksAll();
}



// !!!!!!!!!!!!!!!!
//  S T I C K E R
// !!!!!!!!!!!!!!!!

// create
export function addStickerToPack(identifier: number, stickerPath:string): Promise<any> {
  return WhatsappStickersManager.insertStickerToPack(stickerPath, identifier);
}

// delete
export function removeStickerFromPack(identifier:number, sticker_id:number, newImagePath:string): Promise<any> {
  return WhatsappStickersManager.removeStickerFromPack({identifier, sticker_id, newImagePath});
}

// update
export function replaceSticker(identifier:number, sticker_id:number, newImagePath:string): Promise<any> {
  return WhatsappStickersManager.updateSticker(identifier, sticker_id, newImagePath);
}


// !!!!!!!!!!!!!!!!
//  NON CRUD; MISC
// !!!!!!!!!!!!!!!!

export function addPackToWhatsApp(identifier:number, name:string): Promise<any> {
  return WhatsappStickersManager.addPackToWhatsApp(identifier, name);
}
export function isPackAddedToWhatsApp(identifier:number, packages:string[]): Promise<any> {
  return WhatsappStickersManager.isPackAddedToWhatsApp(identifier, packages || []);
}


export const PackManager  = {
  createPack, 
  deletePack, 
  deletePacksAll,
  getPack, 
  getPacksAll, 
  updatePackMeta, 
  updateTrayIcon, 
  isPackAddedToWhatsApp,
  addPackToWhatsApp
};

export const StickerManager  = {
  addStickerToPack,
  removeStickerFromPack,
  replaceSticker
};


export default {
  PackManager, 
  StickerManager
};
