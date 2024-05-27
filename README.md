<div style="text-align: center;" align="center">
  <img src="https://imgur.com/cNwrHoD.png" width="150" height="150">
  <div><h1> React Native Whatsapp Stickers Manager </h1></div>
  <p align="center">
    "The ultimate WhatsApp sticker manager to create, manage and share the stickers for React Native apps."
  </p>
  <div align="center">
    <a href="https://www.npmjs.com/package/react-native-whatsapp-stickers-manager">
        <img src="https://img.shields.io/npm/v/react-native-whatsapp-stickers-manager">  
    </a>
    <a href="/LICENSE">
        <img src="https://img.shields.io/npm/l/react-native-whatsapp-stickers-manager">  
    </a>
  </div>

</div>



## Description

React Native Whatsapp Stickers Manager is a library that allows you to create and manage WhatsApp sticker packs dynamically within your React Native application. It provides a set of APIs to perform various operations such as creating new sticker packs, adding stickers to existing packs, updating pack metadata, and more.

## Features

#### Basic Features : 
- Create sticker pack
- Retrieve all packs
- Delete all packs
- Retrieve a pack by identifier
- Delete a pack by identifier
- Add sticker pack to WhatsApp
- Whitelist check (includes unofficial whatsapp mods)


#### Advance Features :
- ✨ Update existing pack (packs that are already added to WhatsApp))
- 📦 Change the sticker pack name
- 📦 Change the publisher name
- 📦 Replace tray icon image
- 🎭 Add a new sticker to the pack
- 🎭 Remove a sticker from the pack
- 🎭 Replace sticker images
- 📨 Event-based response status from WhatsApp on adding pack to WA. see [here](#add-a-sticker-pack-to-whatsapp)

## Supported Platforms

- [x] Android (Tested with API level 33, 34)
- [ ] iOS


## Installation

```sh
npm install react-native-whatsapp-stickers-manager...
```

## Import
```js
import RNWSManager from 'react-native-whatsapp-stickers-manager';

```

## Usage

## 1.A: Pack Specific Methods 📦

### 1.1 Create a new sticker pack
Create a new sticker pack with the provided pack details and sticker images
For a complete reference of the sticker pack structure and requirements, see [Sticker Pack and Sticker Object](#Sticker-Pack-and-Sticker-Object):


```js
// Define a sticker object
const sticker = {
  image_file: '/storage/emulated/0/Download/stickers/flying_bird.webp',
  emojis: ['✌️', '🕊️']
}

// Define a sticker pack object
const stickerPack = {
  // Required fields (see Sticker Pack Structure for details)
  tray_image_file: '/storage/emulated/0/Picture/rolling_panda.webp',
  name: 'Sticker Pack Name',
  publisher: 'Publisher Name',
  stickers: [sticker, sticker2, sticker3]
}

// Create a new sticker pack
await RNWSManager.Pack.createPack(stickerPack)

```
### 1.2 Retrieve Sticker Packs 

```ts
// Retrieve a single sticker pack by identifier
await RNWSManager.Pack.getPack({
  identifier: number
})
```
Get a list of all sticker packs currently available in the app.
```ts
// Retrieve all available sticker packs
await RNWSManager.Pack.getPacksAll()
```
### 1.3 Update Sticker Pack Metadata 
```ts
// Update sticker pack metadata with optional fields
await RNWSManager.Pack.updatePackMeta({
  identifier: number,
  name?: string,
  publisher?: string
});
```
### 1.4 Replace Tray Image Icon of a Sticker Pack
```ts
// Replace the tray image icon of a sticker pack
await RNWSManager.Pack.updateTrayIcon({
  identifier: number,
  newTrayImagePath: string
})
```
### 1.5 Delete Sticker Packs 
```ts
// Delete a single sticker pack by identifier
await RNWSManager.Pack.deletePack(identifier: number)

// Delete all sticker packs
await RNWSManager.Pack.deletePacksAll()
```

## 1.B: WhatsApp Interaction 🟢

### 1.6 Add a Sticker Pack to WhatsApp
Get the response status from WhatsApp when sending a sticker pack.<br/>
The response status contains information about the outcome, including a `type` field indicating success or error`, and a `message` field providing a brief description of the error.

Alert Boxes which returns the response status from WhatsApp: 

<img src="https://imgur.com/mv1bYzb.png" height="80"  /> <img src="https://imgur.com/60HPBjW.png" height="80"  /> <img src="https://imgur.com/tmYsdYP.png" height="80"  />


```js
// Add a sticker pack to WhatsApp with a given identifier and name
await RNWSManager.Pack.addPackToWhatsApp({
  identifier: number,
  name: string
})
```
Response result example: 
```bash 

 "status": {
    "code": 0, 
    "isPackValid": false, 
    "message": "sticker count should be between 3 to 30 inclusive, it currently has 2, sticker pack: 34", 
    "type": "validation_error"
  }
```
❗For more information on the response object and its properties, see the [Response Object Details section](#Response-Object-Details)

### 1.7 Check if a Sticker Pack is Added to WhatsApp

```js
// Check if a sticker pack is added to WhatsApp with an optional filter
await RNWSManager.Pack.isPackAddedToWhatsApp({
  identifier: number,
  includeExtraPackages?: string[] // Optional filter for WhatsApp mods
})
```


<!--
// !!!!!!!!!!!!!!!!
//  S T I C K E R 🎭
// !!!!!!!!!!!!!!!!
  -->

## 2.A: Stickers Specific Methods 🎭

### 2.1 Add a New Sticker
```ts 
// Add a new sticker to a sticker pack
await RNWSManager.Sticker.addStickerToPack({
  identifier: number,
  newStickerPath: string
})
```
### 2.2 Replace a Sticker
```ts 
// Replace an existing sticker with a new one
await RNWSManager.Sticker.replaceSticker({
  identifier: number,
  sticker_id: number,
  newStickerPath: string
})

```
### 2.3 Delete a Sticker
```ts 
// Remove a sticker from a sticker pack
await RNWSManager.Sticker.removeStickerFromPack({
  identifier: number,
  sticker_id: number
})
```

### Sticker Pack and Sticker Object
```js
let stickerPack = {
      android_play_store_link: "https://play.google.com/store/apps/details?id=com.myapp",
      ios_app_store_link: "https://itunes.apple.com/app/myapp/id123456",
      publisher_email:"contact@myproject.com",
      publisher_website:"https://myproject.com",
      privacy_policy_website:"https://myproject.com/legal",
      license_agreement_website:"https://myproject.com/license",
      // identifier, // Auto filled using primary key
      name: "My Sticker Pack", 
      publisher: "Publisher Name",
      tray_image_file: "/storage/emulated/0/Download/trayIcon.webp",
      image_data_version: "1",
      avoid_cache: false, // false is default
      animated_sticker_pack:true,
      stickers: stickerFiles.map(item=>{
          return {
              image_file: item.path, // eg.: "/storage/emulated/0/Download/flying_bird.webp
              emojis: item.emojiArray // eg.: ["😁","😋","😍"]
          }
      })
      
};
```
- name: The name of the sticker pack.
- publisher: The name of the publisher for the sticker pack.
- tray_image_file: The file path to the tray/preview image for the sticker pack.
- image_data_version: The version of the image data for the sticker pack.
- avoid_cache: A boolean value indicating whether to avoid caching for this sticker pack. The default - value is false.
- animated_sticker_pack: A boolean value indicating whether this is an animated sticker pack or not.
- stickers: An array of sticker objects, where each sticker object has the following properties:

  - image_file: The file path to the sticker image file (e.g., /storage/emulated/0/Download/flying_bird.webp).
  - emojis: An array of emojis associated with the sticker (e.g., ["😁", "😋", "😍"]).

### Response Object Details

The response returned when user interact with the alert box of whatsapp appeares when sending sticker pack to whatsapp.
This result is useful when you don't know what went wrong at runtime.
- It will resolve/reject the promise when user will interact with the alert box as the promie is attached to event listener.
- It supports most common types of errors/sucess. 
- In some cases, `isPackValid` may be false even if the pack is valid. However, it will always be true for the last two alert boxes that say "pack is already added" or "add pack".
- The response type `user_touched_outside` is custom and not emitted from WhatsApp. This is returned if the pack is valid, but the user cancels the alert box by tapping outside of it. in that case response would be  
```js 
  {
  ... 
  isPackValid: true, 
  type: "user_touched_outside
  ...
  }
```

⚠️ Note: While the library aims to provide accurate response results, there may be edge cases or scenarios where the response is incorrect or unexpected. If you encounter any problematic response results, please [create a new issue](/issues/new) to help improve this feature.



## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT License [LICENSE](https://github.com/your-username/your-repo-name/blob/main/LICENSE)


---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
