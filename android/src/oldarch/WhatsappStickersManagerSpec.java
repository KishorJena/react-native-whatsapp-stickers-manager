package com.whatsappstickersmanager;

import static com.whatsappstickersmanager.Const.Pack.COL_IDENTIFIER;
import static com.whatsappstickersmanager.Const.Pack.COL_TRAY;
import static com.whatsappstickersmanager.Const.Sticker.COL_EMOJIS;
import static com.whatsappstickersmanager.Const.Sticker.COL_IMAGE_FILE;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.whatsappstickersmanager.database.PackDAO;
import com.whatsappstickersmanager.database.StickerDAO;

import java.util.List;

abstract class WhatsappStickersManagerSpec extends ReactContextBaseJavaModule {
  protected ReactApplicationContext context;

  WhatsappStickersManagerSpec(ReactApplicationContext context) {
    super(context);
  }

  public abstract void multiply(double a, double b, Promise promise);;

  public abstract void createPack(ReadableMap packData, Promise promise);

  public abstract void getPacksAll(Promise promise);

  public abstract void getPack(int identifier, Promise promise);

  public abstract void deletePack(int identifer, Promise promise);

  public abstract void deletePacksAll(Promise promise);

  public abstract void updatePackMeta(int identifier, ReadableMap fields, Promise promise);

  public abstract void updateTrayIcon(int identifier, String iconPath, Promise promise);

  public abstract void insertStickerToPack(int identifier, String stickerPath, Promise promise);

  public abstract void removeStickerFromPack(int identifier, int stickerId, Promise promise);

  public abstract void updateSticker(int identifier, int stickerId, String stickerPath, Promise promise);


}
