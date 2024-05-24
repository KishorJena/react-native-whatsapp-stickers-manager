package com.whatsappstickersmanager.database;

import com.whatsappstickersmanager.ContentProvider.StickerPack;

import java.util.List;

public interface StickerPackManager {
    List<StickerPack> getStickerPacks();


    boolean isFilenameExistInPack(String identifier, String fileName);
}
