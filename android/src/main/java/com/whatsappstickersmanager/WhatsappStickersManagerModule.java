package com.whatsappstickersmanager;

import static com.whatsappstickersmanager.Const.Pack.COL_IDENTIFIER;
import static com.whatsappstickersmanager.Const.Pack.COL_STICKERS;
import static com.whatsappstickersmanager.Const.Pack.COL_TRAY;
import static com.whatsappstickersmanager.Const.Sticker.COL_EMOJIS;
import static com.whatsappstickersmanager.Const.Sticker.COL_IMAGE_FILE;
import static com.whatsappstickersmanager.Const.Sticker.COL_STICKER_ID;

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
import com.facebook.react.bridge.WritableMap;
import com.whatsappstickersmanager.database.PackDAO;
import com.whatsappstickersmanager.database.StickerDAO;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WhatsappStickersManagerModule extends com.whatsappstickersmanager.WhatsappStickersManagerSpec implements ActivityEventListener {
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
    // Create a map to store the original file names and their corresponding new random file names
    Map<String, String> pathMap = new HashMap<>();

    try{
      // Insert Pack
      ContentValues pack = DataConverter.map2content_pack(packData, pathMap);
      PackDAO packDAO = new PackDAO(context);
      int identifier = (int) packDAO.insertPack(pack);

      // Insert Stickers
      List<ContentValues> stickersContent = DataConverter.array2content_stickers(packData.getArray(COL_STICKERS), identifier, pathMap);
      packDAO.insertStickers(stickersContent);

      // save tray and stickers into asset with identifier
      for (Map.Entry<String, String> entry : pathMap.entrySet()) {
        String sourcePath = entry.getKey();
        String fileName = entry.getValue();
        String createdFile = FileActions.SaveImage( sourcePath, fileName, identifier);
        //TODO set result if file craeted or not ; createdFile
      }
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
      WritableArray packs = packDAO.getPacksALL();
      promise.resolve(packs);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  @Override
  @ReactMethod
  public void getPack(int identifier, Promise promise) {
    try{
      PackDAO packDAO = new PackDAO(context);
      WritableMap pack = packDAO.getPack(identifier);
      promise.resolve(pack);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  @Override
  @ReactMethod
  public void deletePack(int identifer, Promise promise) {
      try {
        PackDAO packDAO = new PackDAO(context);
        int rowDeleted = packDAO.deletePack(identifer);
        FileActions.deleteDirectory(new File(STICKER_ASSET_PATH+"/"+identifer));
        promise.resolve(rowDeleted);
      }catch (Exception e){
        promise.reject(e);
      }
  }

  @Override
  @ReactMethod
  public void deletePacksAll(Promise promise) {
    try {
      PackDAO packDAO = new PackDAO(context);
      int rowDeleted = packDAO.deletePackALL();
      FileActions.emptyDirectory(new File(STICKER_ASSET_PATH));
      promise.resolve(rowDeleted);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  @ReactMethod
  public void updatePackMeta(int identifier, ReadableMap fields, com.facebook.react.bridge.Promise promise) {
    try {
      ContentValues pack = DataConverter.map2Content_option(fields);
      PackDAO packDAO = new PackDAO(context);
      int rowUpdated = packDAO.updatePack(identifier, pack);
      promise.resolve(rowUpdated);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  @Override
  public void updateTrayIcon(ReadableMap option, String sourceFilePath, Promise promise) {

  }

  @ReactMethod // ✅
  public void insertStickerToPack(String stickerPath, int identifier, Promise promise) {

    try {
      String destinationFileName = UUID.randomUUID().toString() + ".webp";

      ContentValues values = new ContentValues();
      values.put(COL_IMAGE_FILE, destinationFileName);
      values.put(COL_EMOJIS, "♻️,🖼️");
      values.put(COL_IDENTIFIER, identifier);

      StickerDAO stickerDAO = new StickerDAO(context);
      int rowUpdated = (int) stickerDAO.insertSticker(identifier, values);

      new PackDAO(context).updateVersion(identifier);

      // Save file
      FileActions.SaveImage(stickerPath, destinationFileName, identifier);

      promise.resolve(rowUpdated);
    }catch ( Exception e ){
      promise.resolve(e);
    }

  }

  @ReactMethod // ✅
  public void removeStickerFromPack(ReadableMap option, Promise promise) {
    int identifier = option.getInt(COL_IDENTIFIER);
    int stickerId = option.getInt(COL_STICKER_ID);
    String fileName = option.getString(COL_IMAGE_FILE);

    try{
      StickerDAO stickerDAO = new StickerDAO(context);
      int rowDeleted = stickerDAO.deleteSticker(identifier, stickerId);
      FileActions.deleteFile(new File(STICKER_ASSET_PATH+"/"+identifier+"/"+fileName));
      promise.resolve(rowDeleted);
    }catch (Exception e){
      promise.reject(e);
    }
  }
  

  @ReactMethod
  public void updateSticker(int identifier,int sticker_id, String sourceFilePath, Promise promise) {

    try {
      StickerDAO stickerDAO = new StickerDAO(context);
      String image_file = stickerDAO.getStickerName(identifier,sticker_id);
      String targetFileName = UUID.randomUUID().toString() + ".webp";

      // DB
      ContentValues values = new ContentValues();
      values.put(COL_IMAGE_FILE, targetFileName);
      values.put(COL_EMOJIS, "♻️,🖼️");
      values.put(COL_IDENTIFIER, identifier);

      int rowUpdated = stickerDAO.updateSticker(identifier, sticker_id, values);
      new PackDAO(context).updateVersion(identifier);

      // File
      FileActions.SaveImage(sourceFilePath, targetFileName, identifier);
      FileActions.deleteFile(new File(image_file));
      promise.resolve(rowUpdated);
    }catch ( Exception e ){
      promise.resolve(e);
    }
  }
  @ReactMethod
  public void updateTrayIcon(int identifier, String sourceFilePath, Promise promise) {

    try {
      PackDAO packDAO = new PackDAO(context);

      String targetFileName = UUID.randomUUID().toString() + ".webp";

      ContentValues values = new ContentValues();
      values.put(COL_TRAY, targetFileName);

      int rowUpdated = packDAO.updateTrayIcon(identifier, values);

      // File
      FileActions.SaveImage(sourceFilePath, targetFileName, identifier);

      // delete existing tray
      String existingTrayPath = packDAO.getTrayImageFilePath(identifier);
      FileActions.deleteFile(new File(existingTrayPath));

      promise.resolve(rowUpdated);
    }catch ( Exception e ){
      promise.resolve(e);
    }
  }

  @Override
  public void onActivityResult(Activity activity, int i, int i1, @Nullable Intent intent) {

  }

  @Override
  public void onNewIntent(Intent intent) {

  }
}
