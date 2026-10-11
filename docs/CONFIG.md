# Config types

How item, NPC, location, animation and the other definitions are loaded from the cache, and where to
add a new property.

## Where it lives

Where its state lives: the client's type lists in `TypeLists`, each list's caches on the list itself, and
the archives they read in `Resource`.

Each kind of definition comes as a pair of classes: a **type** (`config/item/ItemType`,
`config/npc/NPCType`, `config/sequence/SeqType`, and so on) and a **type list** that loads and
caches it (`ObjTypeList`, `NPCTypeList`, `SeqTypeList`, ...). `config/TypeLists` holds the client's
list for each kind (`TypeLists.OBJ`, `TypeLists.NPC`, `TypeLists.LOC`, `TypeLists.SEQ`,
`TypeLists.ENUM`, ...). They are created in `LoadingStageWork` once the cache archives are open, with the
game (`Game.CURRENT_GAME`), the language (`ClientState.language`) and the `Js5ResourceProvider` of
each archive (`Resource.OBJ`, `Resource.NPC`, `Resource.MODEL`, ...).

| Type | List | Archive |
|---|---|---|
| `ItemType` | `ObjTypeList` | `Resource.OBJ`, models from `Resource.MODEL` |
| `NPCType` | `NPCTypeList` | `Resource.NPC` |
| `LocType` | `LocTypeList` | locations |
| `SeqType` | `SeqTypeList` | sequences, with `FrameSet`s from the frame archives |
| `SpotAnimationType` | `SpotAnimationTypeList` | spot animations |
| `EnumType`, `StructType`, `ParamType`, `VarbitType`, `VarPlayerType`, `BasType`, `IdkType`, `FloType`, `FluType`, `CursorType`, `HitSplatType`, `MECType`, `QuestType`, `SkyboxType`, `SunType`, `LightType`, `MapSpriteType` | the matching `*TypeList` | the config archive (`Resource.CONFIG`) groups |

## How a definition is loaded

Every list has the same shape:

- `list(id)` returns the cached type. If it is not cached, `list`:
  1. reads the file from the archive (`getFile(group, file)`);
  2. creates the type and sets its defaults;
  3. calls `type.decode(new Packet(data))`;
  4. calls `type.postDecode()`;
  5. applies the list-wide rules: the members-only rule and the language strings, plus noted and lent items for `ObjTypeList`;
  6. caches the type.
- `cacheClean()`, `cacheReset()` and `cacheRemoveSoftReferences()` manage the caches every cycle and on logout. `setCacheSize(n)` resizes them. `setAllowMembers(...)` and `setFeatureMask(...)` change what the list builds and reset its caches.

Each type decodes in two steps:
- `decode(Packet)` loops over opcodes until it reads `0`.
- `decode(Packet, opcode)` (or `decode(opcode, Packet)`) reads one property.

A new opcode the client does not read desynchronises the rest of the file, so a client and cache
must agree on every opcode.

## Models

Types that have models build them from meshes in `Resource.MODEL`:
- `ItemType`: `getBodyMesh`, `getHeadMesh`, `getAnimatedModel`, and the inventory icon in `getSpritePixels`.
- `NPCType`: `getBodyModel`, `getAnimatedHeadModel`.
- `SpotAnimationType`: `getModel`, `getContouredModel`.

Animation goes through `SeqType`:
- `animateModel` and `animateRotatedModel` copy the model and apply a `FrameSet` frame.
- `getFunctionMask` tells the model which parts the frames change: alpha, colour or billboards (see `FrameSet.modifiesAlpha`, `modifiesColors`, `modifiesBillboards`).

## Textures

Procedural textures are `config/material/texture/TextureType`, a tree of `TextureOperation`s. Each operation:
- decodes its settings with `decode(Packet, opcode)`;
- produces rows with `getColorOutput(y)` or `monochromeOutput(y)`.

`TextureType.getPixelsRgb`, `getPixelsArgb` and `getPixelsHdr` render a texture for the toolkits.
`MaterialProvider` (implemented by `Js5TextureSource`) is the toolkits' way in.

## Adding a property

1. Add the field to the type, with the default the client should use when the opcode is absent.
2. Read it in `decode(Packet, opcode)` under the opcode the cache uses, reading exactly the bytes
   the cache writes.
3. If it depends on other properties, finish it in `postDecode`.
4. Use it where the type is consumed: the model builders, the mini menu (actions) or CS2 (params
   through `getIntParam` and `getStringParam`).
5. Start the client (`./gradlew runClient`) and log in on each toolkit: Java OpenGL, native OpenGL
   and software, switched in the graphics options. Logging in loads items, NPCs, locations, animations
   and textures on all three toolkits.

## Example: ground item labels

`client/features/GroundItemLabels` reads the piles the server's ground item packets build
(`ObjStack.GROUND_ITEMS`, keyed by `level << 28 | y << 14 | x` in absolute tiles, each pile's
`groundItems` already ordered most valuable first by `ObjStack.addGroundItem`), and names each
`ObjStackEntry` from its config type: `TypeLists.OBJ.list(entry.id).name`, with `entry.count`. It
projects the middle of the tile with `OverlayManager.projectToScreenWithOffset`.
