import { NativeModules, Platform } from 'react-native';
import type { StickerPack } from './types';
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
//      P A C K  ✅
// !!!!!!!!!!!!!!!!

//  Create ✅
function createPack(stickerPack: StickerPack): Promise<void> {
  if (!stickerPack.name || stickerPack.name.length > 128) {
    throw new Error('Name is required and must be 128 characters or less');
  }
  if (!stickerPack.publisher || stickerPack.publisher.length > 128) {
    throw new Error('Publisher is required and must be 128 characters or less');
  }
  if (!stickerPack.stickers || stickerPack.stickers.length < 3) {
    throw new Error('At least one sticker is required');
  }
  stickerPack.stickers.forEach((sticker) => {
    if (!sticker.image_file) {
      throw new Error('Image file is required for each sticker');
    }
    if (!sticker.emojis || sticker.emojis.length === 0) {
      sticker.emojis = ['😂']; // default emojis
    }
  });

  return WhatsappStickersManager.createPack(stickerPack);
}

//  Retrieve ✅
function getPack(identifier: number): Promise<any> {
  return WhatsappStickersManager.getPack(identifier);
}

//  Retrieve All ✅
function getPacksAll(): Promise<any> {
  return WhatsappStickersManager.getPacksAll();
}

// Update Meta ✅
function updatePackMeta(options: {
  identifier: number;
  name: string;
  publisher: string;
}): Promise<any> {
  if (!options.name || options.name.length > 128) {
    throw new Error('Name is required and must be 128 characters or less');
  }
  if (!options.publisher || options.publisher.length > 128) {
    throw new Error('Publisher is required and must be 128 characters or less');
  }
  return WhatsappStickersManager.updatePackMeta(options.identifier, {
    name: options.name,
    publisher: options.publisher,
  });
}

// Update Tray Icon ✅
function updateTrayIcon(option: {
  identifier: number;
  newTrayImagePath: string;
}): Promise<any> {
  return WhatsappStickersManager.updateTrayIcon(
    option.identifier,
    option.newTrayImagePath
  );
}

// Delete ✅
function deletePack(identifier: number): Promise<any> {
  return WhatsappStickersManager.deletePack(identifier);
}

// Delete All ✅
function deletePacksAll(): Promise<any> {
  return WhatsappStickersManager.deletePacksAll();
}

// !!!!!!!!!!!!!!!!
//  S T I C K E R
// !!!!!!!!!!!!!!!!

// create ✅
function addStickerToPack(option: {
  identifier: number;
  newStickerPath: string;
}): Promise<any> {
  return WhatsappStickersManager.insertStickerToPack(
    option.identifier,
    option.newStickerPath
  );
}

// delete ✅
function removeStickerFromPack(option: {
  identifier: number;
  sticker_id: number;
}): Promise<any> {
  return WhatsappStickersManager.removeStickerFromPack(
    option.identifier,
    option.sticker_id
  );
}

// update ✅
function replaceSticker(option: {
  identifier: number;
  sticker_id: number;
  newStickerPath: string;
}): Promise<any> {
  return WhatsappStickersManager.updateSticker(
    option.identifier,
    option.sticker_id,
    option.newStickerPath
  );
}

// !!!!!!!!!!!!!!!!
//  NON CRUD; MISC
// !!!!!!!!!!!!!!!!
// Add to WhatsApp ✅
function addPackToWhatsApp(options: {
  identifier: number;
  name: string;
}): Promise<any> {
  const { identifier, name } = options;
  if (typeof identifier !== 'number' || typeof name !== 'string') {
    throw new Error(
      'Invalid options: identifier must be a number and name must be a string'
    );
  }

  return WhatsappStickersManager.addPackToWhatsApp(identifier, name);
}

// Is Added to WhatsApp ✅
function isPackAddedToWhatsApp(option: {
  identifier: number;
  includeExtraPackages?: string[];
}): Promise<any> {
  return WhatsappStickersManager.isPackAddedToWhatsApp(
    option.identifier,
    option.includeExtraPackages ?? []
  );
}

export const Pack = {
  createPack,
  deletePack,
  deletePacksAll,
  getPack,
  getPacksAll,
  updatePackMeta,
  updateTrayIcon,
  isPackAddedToWhatsApp,
  addPackToWhatsApp,
};

export const Sticker = {
  addStickerToPack,
  removeStickerFromPack,
  replaceSticker,
};

export default {
  Pack,
  Sticker,
};
