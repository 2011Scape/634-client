# Entities and update masks

How players, NPCs and the other scene entities are kept up to date and drawn, and where to change
what one player sees on another. The player update is `PlayerUpdate`, the NPC update `NpcUpdate`,
and the moving entities share `PathingEntity`. The table below gives the class each part lives in.

Where its state lives: the player lists and update state in `PlayerUpdate` (`players`, `localPlayer`,
the index lists), the NPC lists and update state in `NPCList` (`NPC_MAP`, `NPC_ARRAY`, the update
indices) and `NpcUpdate` (`NPC_MASK_UPDATE_INDICES`), and each entity's own state on the entity.

## Where it lives

| Part | Class | What it holds |
|---|---|---|
| Scene entities | `entity/Entity` | `x`, `y`, `z`, `level`, and the methods every entity overrides: `render`, `picked`, `shareLight`, `particleCycle`, `getModelMinY`, `hasAlpha`, `canRender`, `isOccluded` |
| Locations | `AbstractInteractiveEntity` and `Wall`, `WallDecor`, `GroundDecor`; `Static*` and `Dynamic*` subclasses | `Static*` locs keep a built model; `Dynamic*` locs animate through a `LocEntity`. Both implement `GameObject` (`upload`, `addShadow`, `removeShadow`) |
| Characters | `entity/PathingEntity` | Movement queue (`movementBaseX/Y`, `movementQueueSpeed`), `yaw`/`roll`/`pitch` (`EasedAngle`), animations (`currentSequence`, `movingSequence`, `sequences`), spot animations, hitsplats (`hit`), health and timer bars, overhead text (`hintDetails`) |
| Players | `entity/PlayerEntity`, `entity/pathingentity/player/PlayerAppearance` | Appearance decode, title, model building (`getBodyModel`, `getAnimatedHeadModel`) |
| NPCs | `entity/NPCEntity` | `npcType` (`setNpcType`), face coordinate, `getAnimatedBodyModel` |
| Effects | `ProjectileAnimation`, `SpotAnimation`, `ParticleSystem`, `ParticleEmitter` | |
| Player list | `entity/pathingentity/player/PlayerUpdate` | `players` (2048), `localPlayer`, `myPlayerIndex`, `OUT_PLAYER_INDICES`, `UPDATE_MASK_PLAYER_INDICES`, `APPEARANCE_BUFFERS` |
| NPC list | `NPCList` | `NPC_MAP` (by index), `NPC_ARRAY`, `npcCount`, `NPC_UPDATE_INDICES` |

## The player update

The server sends `PLAYER_UPDATE`. The decoder (`docs/NETWORK.md`) calls
`PlayerUpdate.decodePlayerUpdate(buffer, size)`, which works in two passes:

1. **Movement.** `PlayerInfoBitDecoder.decodeUpdate` walks the local and the outside player lists:
   - For players in view, it calls `PlayerList.decodePositionUpdate`: walk, run, teleport or remove.
   - For the others, it calls `PlayerList.decodeMovementUpdate`: add, level, direction, or region.
   - Runs of unchanged players are skipped with `PlayerInfoBitDecoder.decodeSkipCount`.
   - Every player whose update-mask bit is set is added to `PlayerUpdate.UPDATE_MASK_PLAYER_INDICES`.
2. **Masks.** `PlayerUpdate.decodeUpdateMasks` reads each listed player's flags (one byte, extended by `0x40` and `0x4000`) and calls `PlayerUpdate.decodePlayerUpdateMask`. That method tests each bit in a fixed order:

| Bit | Block |
|---|---|
| `0x1` | Face entity |
| `0x2` | Face direction |
| `0x4` | Hitsplats and health bar (`hit`) |
| `0x8` | Four movement animations and their speed (`animate`) |
| `0x10` | Appearance (`PlayerEntity` decodes it into `PlayerAppearance`) |
| `0x20` | Primary spot animation (`playSpotAnimation`) |
| `0x80` | Movement type |
| `0x100` | Slot-masked sequences |
| `0x200` | Secondary spot animation |
| `0x400` | Timer bar (`secondaryBar*`) |
| `0x800` | Temporary movement type (127 is a teleport) |
| `0x1000` | Forced chat (`hintDetails.text`) |
| `0x2000` | Force movement |
| `0x8000` | Minimap dot |
| `0x10000` | Worn-item targets |
| `0x40000` | Tint |

The bit order in the packet matters. A block must be read in the order the server writes it.

## The NPC update

The server sends `NPC_UPDATE`. The decoder calls `NpcUpdate.decodeNpcUpdate`, which works in three steps:

1. `NPCList.decodeAddRemove` reads each known NPC's keep, move or remove bits.
2. `NPCList.decodeNewNpcs` adds new NPCs. It reads a 15-bit index for each, ending at 32767.
3. `NPCList.decodeNpcMasks` reads each listed NPC's flags (the second byte is flagged by `0x10`) and its blocks:

| Bit | Block |
|---|---|
| `0x1` | Forced chat |
| `0x2` | Transform (`setNpcType`) |
| `0x4` | Face coordinate (`faceX`, `faceY`) |
| `0x8` | Animation (`NPCEntity.animateNpc`) |
| `0x20` | Primary spot animation |
| `0x40` | Hitsplats |
| `0x80` | Face entity |
| `0x200` | Worn-item targets |
| `0x400` | Secondary spot animation |
| `0x800` | Timer bar |
| `0x1000` | Force movement |
| `0x2000` | Tint |
| `0x4000` | Slot-masked sequences (`NPCEntity.animateNpcSlots`) |

## Each cycle

- The game tick moves every character one step along its movement queue and advances its animations.
- `EntitySceneBuilder` adds the visible players, NPCs and effects to the scene.
- `Entity.render` draws them.
- `OverlayManager` draws the overhead text, hitsplats, health and timer bars.

## Adding an update block

1. Add the bit test to `decodePlayerUpdateMask` or `decodeNpcMasks`, at the position the server writes the block. Read every field even when the client ignores it.
2. Store the result on the entity: a field on `PathingEntity` for both kinds, or on `PlayerEntity` or `NPCEntity`.
3. Draw or act on it in the cycle: `OverlayManager` for overhead elements, and `getAnimatedBodyModel`/`getBodyModel` for model changes.
4. Start the client (`./gradlew runClient`) and log in on each toolkit: Java OpenGL, native OpenGL
   and software, switched in the graphics options. Logging in exercises:
   - logging in, which runs the player update with appearance;
   - the gfx step (spot animation, mask `0x20`);
   - the chat step (forced chat, `0x1000`).

## Example: npc labels

`client/features/NpcLabels` reads the npc list the way the overhead text does
(`OverlayManager.drawEntities`): it walks `NPCList.NPC_UPDATE_INDICES` (this cycle's npcs), looks
each up in `NPCList.NPC_MAP`, takes `NPCEntity.npcType` (through `getMultiNpc` when the type has
`alternativeIds`, for npcs that transform with a varp or varbit), skips npcs not drawn this frame or
not on the player's level, and projects the point at the npc's overhead height (`getY()`) with
`OverlayManager.projectEntityToScreen`. The label is the type's `name` and `combatLevel`.
