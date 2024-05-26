<div style="text-align: center;" align="center">
  <img src="https://imgur.com/cNwrHoD.png" width="150" height="150">
  <div><h1> React Native Whatsapp Stickers Manager </h1></div>

  <div align="center">
    <img src="https://img.shields.io/npm/v/react-native-whatsapp-stickers-manager">  
    <img src="https://img.shields.io/npm/l/react-native-whatsapp-stickers-manager">  
  </div>
  <p align="center">
    "The ultimate WhatsApp sticker manager to create, manage and share the stickers for React Native apps."
    </p>
</div>



## Description

React Native Whatsapp Stickers Manager is a library that allows you to create and manage WhatsApp sticker packs dynamically within your React Native application. It provides a set of APIs to perform various operations such as creating new sticker packs, adding stickers to existing packs, updating pack metadata, and more.

## Features

#### Basic Features : 
- Create a new sticker pack
- Retrieve individual pack
- Delete individual pack 
- Retrieve all packs
- Delete all packs
- Add sticker pack to WhatsApp
- Whitelist check (includes unofficial whatsapp mods)


#### Advance Features : work with existing packs on WhatsApp
- 📦 Change Sticker Pack name
- 📦 Change publisher
- 📦 Replace tray icon image
- 🎭 Add a sticker to pack
- 🎭 Remove any sticker from the pack
- 🎭 Replace sticker images


## Supported Platforms

- [x] Android (Tested with API level 33, 34)
- [ ] iOS


## Installation

```sh
npm install react-native-whatsapp-stickers-manager...
```

# Import
```js
import RNWSManager from 'react-native-whatsapp-stickers-manager';

```

# Usage

## 1.A: Pack Specific Methods 📦

### Create a new sticker pack
here is an example of how to create a new sticker pack.<br/>
For a complete reference of the sticker pack structure and requirements, see []:


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
### Retrieve Sticker Packs 
```ts
// Retrieve a single sticker pack by identifier
await RNWSManager.Pack.getPack({
  identifier: number
})

// Retrieve all available sticker packs
await RNWSManager.Pack.getPacksAll()
```
### Update Sticker Pack Metadata 
```ts
// Update sticker pack metadata with optional fields
await RNWSManager.Pack.updatePackMeta({
  identifier: number,
  name?: string,
  publisher?: string
});
```
### Replace Tray Image Icon of a Sticker Pack
```ts
// Replace the tray image icon of a sticker pack
await RNWSManager.Pack.updateTrayIcon({
  identifier: number,
  newTrayImagePath: string
})
```
### Delete Sticker Packs 
```ts
// Delete a single sticker pack by identifier
await RNWSManager.Pack.deletePack(identifier: number)

// Delete all sticker packs
await RNWSManager.Pack.deletePacksAll()
```

## 1.B: WhatsApp Interaction 🟢

### Add a Sticker Pack to WhatsApp
```js
// Add a sticker pack to WhatsApp with a given identifier and name
await RNWSManager.Pack.addPackToWhatsApp({
  identifier: number,
  name: string
})
```
### Check if a Sticker Pack is Added to WhatsApp

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

### Add a New Sticker
```ts 
// Add a new sticker to a sticker pack
await RNWSManager.Sticker.addStickerToPack({
  identifier: number,
  newStickerPath: string
})
```
### Replace a Sticker
```ts 
// Replace an existing sticker with a new one
await RNWSManager.Sticker.replaceSticker({
  identifier: number,
  sticker_id: number,
  newStickerPath: string
})

```
### Delete a Sticker
```ts 
// Remove a sticker from a sticker pack
await RNWSManager.Sticker.removeStickerFromPack({
  identifier: number,
  sticker_id: number
})
```



## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
