package com.whatsappstickersmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.whatsappstickersmanager.WhatsappStickersManagerSpec;
import com.whatsappstickersmanager.database.PackDAO;

import java.util.List;

public class WhatsappStickersManagerModule extends WhatsappStickersManagerSpec implements ActivityEventListener {
  String TAG = WhatsappStickersManagerModule.class.getSimpleName();
  public static final String NAME = "WhatsappStickersManager";
  public static final String ASSET_FOLDER_NAME = "stickers_asset";
  public static String STICKER_ASSET_PATH = null;

  WhatsappStickersManagerModule(ReactApplicationContext context) {
    super(context);

    this.context = context;
    STICKER_ASSET_PATH = context.getFilesDir().getAbsolutePath() + "/"+ ASSET_FOLDER_NAME;
    context.addActivityEventListener(this);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    String s = context.getFilesDir().getAbsolutePath();
    promise.resolve(s);
  }

  @ReactMethod
  public void createPack(ReadableMap packData, Promise promise) {
    try{
      ContentValues pack = DataConverter.getPack(packData);
      Log.v(TAG,  "createPack pack "+ pack.get(Const.Pack.COL_IDENTIFIER));

      PackDAO packDAO = new PackDAO(context);
      int identifier = (int) packDAO.insertPack(pack);
      // log the identifer
      Log.v(TAG, "createPack success :identifer "+ identifier);

      List<ContentValues> stickers = DataConverter.getStickers(packData, identifier);
      packDAO.insertStickers(stickers);

      promise.resolve("createPack success :identifer"+ identifier);
    }catch (Exception e){
      Log.v(TAG, "createPack error "+ e);
      promise.reject(e);
    }
  }


  @ReactMethod
  public void getPacksAll(Promise promise) {
    try{
      PackDAO packDAO = new PackDAO(context);
      WritableArray packs = packDAO.getPacks();
      promise.resolve(packs);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  @Override
  public void getPack(int packId, Promise promise) {

  }

  @Override
  public void deletePack(int packId, Promise promise) {

  }

  @Override
  public void deletePacksAll(Promise promise) {

  }

  @Override
  public void onActivityResult(Activity activity, int i, int i1, @Nullable Intent intent) {

  }

  @Override
  public void onNewIntent(Intent intent) {

  }
}
