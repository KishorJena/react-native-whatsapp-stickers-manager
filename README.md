<div style="text-align: center;" align="center">
  <div><h1> React Native Whatsapp Stickers Manager </h1></div>
  <img src="https://github.com/KishorJena/react-native-whatsapp-stickers-manager/assets/19212755/93e8dc9b-13ef-4c83-920e-16dc7fb7acb5" width="150" height="150">
<br/><br/>
  <div align="center">
    <img src="https://img.shields.io/npm/v/react-native-whatsapp-stickers-manager">  
    <img src="https://img.shields.io/npm/l/react-native-whatsapp-stickers-manager">  
  </div>
</div>

<p align="center">
  Create and manage whatsapp stickers dynamaically 
</p>

## Description

React Native Whatsapp Stickers Manager is a library that allows you to create and manage WhatsApp sticker packs dynamically within your React Native application. It provides a set of APIs to perform various operations such as creating new sticker packs, adding stickers to existing packs, updating pack metadata, and more.

## Features

#### Basic Operations : 
- Create a new sticker pack
- Add sticker pack to WhatsApp
- Check if a sticker pack is added to WhatsApp
###
- Retrieve individual pack
- Delete individual pack 
- Retrieve all packs
- Delete all packs

#### Advance Operations : work with existing packs on WhatsApp
- 📦 Change Sticker Pack name
- 📦 Change publisher
- 📦 Replace tray image icon
- 🎭 Add a sticker to pack
- 🎭 Remove any sticker from the pack
- 🎭 Replace any sticker


## Supported Platforms

- [x] Android (Tested in API level 34)
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
## 1.A: 📦 Pack Specifica Operation 
<!-- CREATE 📦 -->
### Create a new sticker pack
here is an example of how to create a new sticker pack.<br/>
For a complete reference of the sticker pack structure and requirements, see []:


```js
const sticker = {
    image_file: '/storage/emulated/0/Download/stickers/flying_bird.webp',
    emojis: ['✌️', '🕊️']
}

const stickerPack = {
  // Required fields (see below for detailed structure)
  tray_image_file: '/storage/emulated/0/Picture/rolling_panda.webp',
  name: 'Sticker Pack Name',
  publisher: 'Publisher Name',
  stickers: [ sticker, sticker2, sticker3 ]
}

await RNWSManager.Pack.createPack(stickerpack: StickerPack)

```

### Retrieve one specific or all sticker packs 
```js
// RETRIEVE 📦
await RNWSManager.Pack.getPack(identifier: number)
await RNWSManager.Pack.getPacksAll()
```

### Update sticker pack
```js
// UPDATE 📦
await RNWSManager.Pack.updatePackMeta({identifier:number; name:string, publisher:string})
```
OR
```js
// UPDATE 📦
await RNWSManager.Pack.updatePackMeta({identifier:number, name:string})
```
OR
```js
// UPDATE 📦
await RNWSManager.Pack.updatePackMeta({identifier:number, publisher:string})
```

### Replace tray image icon of a sticker pack
```js
// UPDATE 📦
await RNWSManager.Pack.updateTrayIcon({identifier:number, newTrayImagePath:string}): 
```
### Delete one specific or all sticker packs  
```js
// DELETE 📦
await RNWSManager.Pack.deletePack(identifier: number)
await RNWSManager.Pack.deletePacksAll()
```
## 1.B: 🟢 WhatsApp Interaction

### Add a sticker pack to WhatsApp
```js
await RNWSManager.Pack.addPackToWhatsApp({identifier: number, name:string})
```

### Check if a sticker pack is added to WhatsApp

```js
await RNWSManager.Pack.isPackAddedToWhatsApp({identifier:number, includeExtraPackages?:string[]});
```


<!--
// !!!!!!!!!!!!!!!!
//  S T I C K E R 🎭
// !!!!!!!!!!!!!!!!
  -->
## 2.A: 🎭 Stickers Specific Operations
### Add/Remove/Replace individual stickers of a sticker pack

```js
// CREATE 🎭
await RNWSManager.Sticker.addStickerToPack({identifier: number, newStickerPath:string})

// UPDATE 🎭
await RNWSManager.Sticker.replaceSticker({identifier:number, sticker_id:number newStickerPath:string})

// DELETE 🎭
await RNWSManager.Sticker.removeStickerFromPack({identifier:number, sticker_id:number})

```






## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
