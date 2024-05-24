package com.whatsappstickersmanager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

abstract class WhatsappStickersManagerSpec extends ReactContextBaseJavaModule {
  protected ReactApplicationContext context;

  WhatsappStickersManagerSpec(ReactApplicationContext context) {
    super(context);
  }

  public abstract void createPack(ReadableMap packData, Promise promise);

  public abstract void getPacksAll(Promise promise);

  public abstract void getPack(int identifier, Promise promise);

  public abstract void deletePack(int identifer, Promise promise);

  public abstract void deletePacksAll(Promise promise);

  public abstract void updatePackMeta(int identifier, ReadableMap fields, Promise promise);

  public abstract void insertStickerToPack(String stickerPath, int identifier, Promise promise);

  public abstract void removeStickerFromPack(ReadableMap option, Promise promise);

  public abstract void updateSticker(int identifier,int sticker_id, String sourceFilePath, Promise promise);

  public abstract void updateTrayIcon(int identifier, String sourceFilePath, Promise promise);

  public abstract void addToWhatsApp(int identifier, String name, Promise promise);

  public abstract void isWhitelisted(int identifier, Promise promise);
}
