package com.whatsappstickersmanager;

import static com.whatsappstickersmanager.Const.Pack.COL_ANIMATED;
import static com.whatsappstickersmanager.Const.Pack.COL_IDENTIFIER;
import static com.whatsappstickersmanager.Const.Pack.COL_STICKERS;
import static com.whatsappstickersmanager.Const.Pack.COL_TRAY;
import static com.whatsappstickersmanager.Const.Sticker.COL_EMOJIS;
import static com.whatsappstickersmanager.Const.Sticker.COL_IMAGE_FILE;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.whatsappstickersmanager.database.PackDAO;
import com.whatsappstickersmanager.database.StickerDAO;
import com.whatsappstickersmanager.database.StickerPackDAO;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WhatsappStickersManagerModule extends com.whatsappstickersmanager.WhatsappStickersManagerSpec implements ActivityEventListener {
  static String TAG = WhatsappStickersManagerModule.class.getSimpleName();
  public static final String NAME = "WhatsappStickersManager";
  public static final String ASSET_FOLDER_NAME = "stickers_asset";
  public static String STICKER_ASSET_PATH = null;

  Promise activityResultPromise;
  ReactApplicationContext context;
  WhatsappStickersManagerModule(ReactApplicationContext pcontext) {
    super(pcontext);

    this.context = pcontext;
    STICKER_ASSET_PATH = pcontext.getFilesDir().getAbsolutePath() + "/"+ ASSET_FOLDER_NAME;
    pcontext.addActivityEventListener(this);
  }

  public static String getContentProviderAuthority(Context context) {
    return context.getPackageName()+".stickercontentprovider";
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
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

      boolean isAnimated = packDAO.getPackType(identifier);
      // save tray and stickers into asset with identifier
      for (Map.Entry<String, String> entry : pathMap.entrySet()) {
        String sourcePath = entry.getKey();
        String fileName = entry.getValue();
        String createdFile = FileActions.SaveImage( sourcePath, fileName, identifier, isAnimated);
        //TODO set result if file craeted or not ; createdFile
      }

      WritableMap result = new WritableNativeMap();
      result.putInt("identifier", identifier);
      result.putString("name", packData.getString("name"));
      promise.resolve(result);
    }catch (Exception e){
      Log.v(TAG, "createPack error "+ e);
      promise.reject(e);
    }
  }


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


  ////// Update Pack //////
  @ReactMethod
  public void updatePackMeta(int identifier, ReadableMap options, com.facebook.react.bridge.Promise promise) {
    try {
      ContentValues fields = DataConverter.map2Content_option(options);
      PackDAO packDAO = new PackDAO(context);
      int rowUpdated = packDAO.updatePack(identifier, fields);
      packDAO.updateVersion(identifier);
      promise.resolve(rowUpdated);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  @ReactMethod
  public void insertStickerToPack(int identifier, String newStickerPath, Promise promise) {

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
      boolean isAnimated = new PackDAO(context).getPackType(identifier);
      FileActions.SaveImage(newStickerPath, destinationFileName, identifier, isAnimated);

      promise.resolve(rowUpdated);
    }catch ( Exception e ){
      promise.resolve(e);
    }

  }

  @ReactMethod
  public void removeStickerFromPack(int identifier, int sticker_id, Promise promise) {

    try{
      StickerDAO stickerDAO = new StickerDAO(context);
      String fileName = stickerDAO.getStickerName( identifier, sticker_id);

      int rowDeleted = stickerDAO.deleteSticker(identifier, sticker_id);
      new PackDAO(context).updateVersion(identifier);

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

      // File
      boolean isAnimated = new PackDAO(context).getPackType(identifier);
      FileActions.SaveImage(sourceFilePath, targetFileName, identifier, isAnimated);
      FileActions.deleteFile(new File(STICKER_ASSET_PATH+"/"+identifier+"/"+image_file));

      int rowUpdated = stickerDAO.updateSticker(identifier, sticker_id, values);
      new PackDAO(context).updateVersion(identifier);

      promise.resolve(rowUpdated);
    }catch ( Exception e ){
      promise.resolve(e);
    }
  }

  @ReactMethod
  public void updateTrayIcon(int identifier, String sourceFilePath, Promise promise) {

    try {
      PackDAO packDAO = new PackDAO(context);

      String existingTrayName = packDAO.getTrayImageFilePath(identifier);
      String targetFileName = UUID.randomUUID().toString() + ".webp";

      ContentValues values = new ContentValues();
      values.put(COL_TRAY, targetFileName);
      int rowUpdated = packDAO.updateTrayIcon(identifier, values);


      // save File
      FileActions.SaveImage(sourceFilePath, targetFileName, identifier, false);

      // delete existing
      FileActions.deleteFile(new File(STICKER_ASSET_PATH+"/"+identifier+"/"+existingTrayName));

      packDAO.updateVersion(identifier);

      promise.resolve(rowUpdated);
    }catch ( Exception e ){
      promise.resolve(e);
    }
  }

  // all
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
  public void getPacksAll(Promise promise) {
    try{
      PackDAO packDAO = new PackDAO(context);
      WritableArray packs = packDAO.getPacksALL();
      promise.resolve(packs);
    }catch (Exception e){
      promise.reject(e);
    }
  }

  // MISC
  @ReactMethod
  public void addPackToWhatsApp(int identifier, String name, Promise promise) {
    activityResultPromise = promise;
    try {
      String authority=getContentProviderAuthority(context);
      Intent intent = new Intent();
      intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
      intent.putExtra(Const.Extra.EXTRA_STICKER_PACK_ID, String.valueOf(identifier)); //identifier is the pack's identifier in contents.json file
      intent.putExtra(Const.Extra.EXTRA_STICKER_PACK_AUTHORITY, authority); //authority is the ContentProvider's authority. In the case of the sample app it is BuildConfig.CONTENT_PROVIDER_AUTHORITY.
      intent.putExtra(Const.Extra.EXTRA_STICKER_PACK_NAME, name); //stickerPackName is the name of the sticker pack.

      context.startActivityForResult(intent, Const.ADD_PACK_CODE, null);
    }catch (ActivityNotFoundException e){
      Log.v(TAG, "installPack : ActivityNotFoundException", e);
    } catch (Exception e) {
      Log.v(TAG, "installPack : Exception ", e);
    }
  }
  @ReactMethod
  public void isPackAddedToWhatsApp(int identifier, ReadableArray includeExtraPackages, Promise promise) {
    try {
      String[] customPackageNames = new String[includeExtraPackages.size()];
      for (int i = 0; i < includeExtraPackages.size(); i++) {
        customPackageNames[i] = includeExtraPackages.getString(i);
      }
      // check
      WritableMap result = Whitelist.isWhitelisted(context, String.valueOf(identifier),customPackageNames);
      promise.resolve(result);
    }catch ( Exception e ){
      promise.resolve(e);
    }
  }

  @ReactMethod
  public void validatePack(ReadableMap stickerPack, Promise promise){
    WritableArray result = Arguments.createArray();

    try{
      ReadableArray stickers = stickerPack.getArray(COL_STICKERS);
      for ( int i = 0; i < stickers.size(); i++){
        ReadableMap sticker = stickers.getMap(i);
        String imageFileName = sticker.getString(COL_IMAGE_FILE);
//        WritableMap stickerResult = StickerPackValidator.validateStickerFile(imageFileName, stickerPack.getBoolean(COL_ANIMATED));
        result.pushString(imageFileName);
//        result.pushMap(stickerResult);
      }
      promise.resolve(result);
    }catch (Exception e){
      promise.reject(e);
    }

  }
  ////// Handle Activity Result for Adding Sticker to Whatsapppack //////
  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
    WritableMap map = Arguments.createMap();
    // Left - keys
    String KEY_CODE = "code";
    String KEY_TYPE = "type";
    String KEY_MESSAGE = "message";
    String KEY_VALIDATE = "isPackValid";
    // Right - values
    String type = null;
    String message = null;
    boolean isPackValid = false;

    if (intent != null) {
      Bundle extras = intent.getExtras();
      if (extras != null) {
        for (String key : extras.keySet()) {
          Object value = extras.get(key);
          if (value != null) {
            type = key;
            message = value.toString();
          }
        }
      }
    }

    // Match the requestCode ADD PACK : 200
    // user_cancelled, validation_error, already_added, user_touched_outside
    if (requestCode == Const.ADD_PACK_CODE) {
      switch (resultCode){
        case Activity.RESULT_OK:
          isPackValid = true;
          break;
        case Activity.RESULT_CANCELED:
          if(intent==null){
            isPackValid = true;
            type = "user_touched_outside";
            message = "User might have touched outside the alert box whici is similar as [user_cancelled]";
          }else if(message != null && message.equals("true")){
            isPackValid = true;
          }
          break;
        default:
          // not encountered this case yet
          type = "unknown";
          message = "Issue is unknown";
      }

    } // elese other requestedCodes if defined

    map.putInt(KEY_CODE, resultCode);
    map.putString(KEY_TYPE, type);
    map.putString(KEY_MESSAGE, message);
    map.putBoolean(KEY_VALIDATE, isPackValid);

    activityResultPromise.resolve(map);

  }

  @Override
  public void onNewIntent(Intent intent) {

  }
}
