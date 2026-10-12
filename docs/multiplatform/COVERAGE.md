# Name mapping: void-client 228561a ↔ runetek5-634 f2649c4

The name mapping between void-client and this client. Side A (`void`) is void-client at `228561a`
(`client/src`, Java, root package, `ClassNNN`/`methodNNN`/`anIntNNN` names). Side B (`runetek5`) is
this repository at `f2649c4`.

## Files

| File | What it is |
|---|---|
| `void-client-228561a.tiny` | Tiny v2, namespaces `void` → `runetek5`. Descriptors are in `void` names. A member whose owner in this client differs from the class its `void` owner maps to has a comment `Declared in <class>`; a `void` class matched to an original class that this client dissolved has an empty `runetek5` name. |
| `void-client-228561a.members.tsv` | The same, flat, with the real `runetek5` owner and descriptor on every row: `kind void_owner void_name void_desc runetek5_owner runetek5_name runetek5_desc`. Use this one to translate void-client changes. |
| `void-client-228561a.match` | Matcher's raw match file (against the analysis jar from `tools/Reloc.java`, see below). |
| `tools/` | `reproduce.sh` rebuilds all three from scratch; `Reloc.java`, `Drive.java`, `Export.java` are the steps. |

## How it was made

[FabricMC Matcher](https://github.com/FabricMC/Matcher) at `73db496` runs headless: its `matcher-cli`
module has an `automatch` command. With both jars as they are, it matched only 168 classes
(22%). Two things block it:

1. **Statics are in different classes.** OpenRS2 moved 3,324 static fields and methods (25% of all
   members) out of their 2010 owner into the class they serve, and dissolved 87 original classes.
   void-client keeps the 2010 layout (`Class102` is `client!mga`, `PlainTile`, with the same fields).
   Matcher only matches members inside matched classes, so it could not line them up.
2. **Different compilation.** void-client's sources need Java 8 (`Component.getPeer`), which compiles
   string concatenation to `StringBuilder`; this client compiles to Java 21's `invokedynamic` concat.

So:

- `Reloc.java` writes an **analysis-only** copy of this client's jar in which every static member
  is moved back to the class its `@OriginalMember(owner = …)` names (dissolved classes come back
  as `_orig/<name>`), and records each move. Nothing in `src/` is changed.
- Both sides are compiled with string concatenation inlined (void-client `--release 8`, this client
  `-XDstringConcat=inline`).
- `Drive.java` runs Matcher through its API: the same set-up as `matcher-cli automatch`, three
  default `autoMatchAll` passes, then classes, methods and fields at Full and Extra levels with the
  absolute threshold stepped from 0.80 down to 0.50, each step to a fixed point. The relative
  threshold stays at Matcher's 0.085, so a candidate must still clearly beat the runner-up.
- Matched by name, not by structure: the native packages (`jaclib`, `jaggl`, `jagdx`, `jagtheora`,
  `jagex3`, same 59 classes on both sides) and the 14 root-package classes that declare `native`
  methods. JNI fixes those names, so the identity is the correct mapping. Members of the root native
  classes are still matched structurally.
- `Export.java` puts relocated members back on their real owner and writes the Tiny and TSV files.

## Coverage

| | void (A) | matched | runetek5 (B) | matched |
|---|---:|---:|---:|---:|
| Classes | 815 | **772 (94.7%)** | 1,134 | **714 (63.0%)** |
| Methods | 8,124 | **6,533 (80.4%)** | 7,804 | **6,533 (83.7%)** |
| Fields | 10,752 | **5,301 (49.3%)** | 8,038 | **5,301 (65.9%)** |

What the numbers mean:

- **Classes, A.** 815 = 756 game classes + 59 native-package classes (void-client ships those in
  `libs/clientlibs.jar`). Of the 772 matched, 714 map to a class of this client and 58 to an
  original class this client dissolved (its members are mapped one by one to wherever they live
  now). Without the dissolved ones: 714 / 815 = 87.6%. Either way, above the 70% bar.
- **Classes, B.** 407 of this client's 1,134 classes have no `@OriginalClass`: holders that OpenRS2
  created for moved statics, the root-package code split out of `client` (`PacketDecoder`,
  `CS2Interpreter`, …), `client/features`, and the launcher. They cannot match a class by
  construction; their members can, and 1,514 of their 4,282 members are matched. Of the 727
  classes that do have an `@OriginalClass`, 712 are matched (97.9%).
- **Fields, A.** void-client keeps the obfuscator's per-method call counters: 4,055 `static int`
  fields whose only use is `x = x + 1`. OpenRS2 removed them from this client (1 is left). Without
  them, 5,294 of 6,697 fields are matched (79.0%). The other 7 matched counters are probably wrong
  matches.

Cross-checks on the class matches:

- void-client kept the original name of 21 classes (`aa`, `client`, `d`, `ha`, `r`, `s`, `za`, …)
  and renamed 2 by hand (`Npc`, `Player`). All 21 map to the class of this client with that
  `@OriginalClass`; `Npc` → `NPCEntity` and `Player` → `PlayerEntity`.
- void-client numbered its top-level `ClassN` in the original jar's order (names sorted, with
  `a*`/`b*`/`c*` names interleaved at a steady step). All 348 matched top-level `ClassN` fall into
  that order; only `Class36` → `client!sda`, `Class157` → `client!qca` and `Class363` → `client!kf`
  look out of place.
- Superclasses agree for 322 of 325 matched pairs where either side extends a game class. The 3
  that do not are low confidence: `Class328_Sub2_Sub1` → `TextureOpTiledSprite`, `Class328_Sub3`
  → `Js5ResourceRequest`, `Class75_Sub1` → `_orig/ada`.
- All 257 matches from Matcher's default (strict) run are kept.

## Root-package native classes

The classes declaring `native` methods are the same 14 on both sides, with the same number of
native methods. Each maps to itself.

| Class | Native methods | Maps to |
|---|---:|---|
| `a` | 15 | `a` |
| `ba` | 1 | `ba` |
| `h` | 4 | `h` |
| `i` | 37 | `i` |
| `j` | 19 | `j` |
| `ja` | 17 | `ja` |
| `n` | 4 | `n` |
| `na` | 2 | `na` |
| `oa` | 49 | `oa` |
| `p` | 5 | `p` |
| `t` | 10 | `t` |
| `wa` | 2 | `wa` |
| `xa` | 2 | `xa` |
| `ya` | 4 | `ya` |

12 of them matched themselves structurally in a run without the name rule. `h` and `n` (two native
font renderers with the same shape) could not be told apart, so they come from the name rule. No
other class on either side was matched to any of the 14.

Other short root names are **not** native and are not identities: `aa` → `ComponentSurface`,
`d` → `MaterialProvider`, `ha` → `Toolkit`, `r` → `Shadow`, `s` → `Ground`, `za` → `MemoryPool`.

## The 20 largest unmatched classes

Size is the class file size in bytes.

### void-client

| Class | Bytes | Methods | Fields | Extends |
|---|---:|---:|---:|---|
| `Loader` | 6,803 | 15 | 13 | `java/applet/Applet` |
| `Class27` | 5,025 | 8 | 13 | |
| `Class122` | 2,904 | 8 | 9 | |
| `Class2` | 2,707 | 6 | 7 | |
| `Class328` | 2,565 | 3 | 3 | |
| `Class93` | 2,379 | 6 | 6 | |
| `Class307` | 2,322 | 3 | 2 | |
| `Class284` | 2,308 | 6 | 17 | |
| `aa_Sub3` | 2,278 | 5 | 9 | `aa` |
| `Class306` | 2,190 | 5 | 5 | |
| `Class182` | 2,169 | 5 | 12 | |
| `IOException_Sub1` | 2,128 | 5 | 6 | `java/io/IOException` |
| `Class331` | 2,106 | 7 | 10 | |
| `Class348_Sub42_Sub16_Sub2` | 2,081 | 7 | 9 | `Class348_Sub42_Sub16` |
| `Class327` | 1,998 | 6 | 9 | |
| `Class70` | 1,900 | 6 | 5 | |
| `Class371` | 1,708 | 3 | 6 | |
| `Class374` | 1,688 | 4 | 3 | |
| `Class241_Sub2` | 1,565 | 6 | 6 | `Class241` |
| `Class241_Sub3` | 1,472 | 6 | 6 | `Class241` |

`Loader` is void-client's own applet loader and has no counterpart. The rest are mostly static
holders whose statics, after the relocation, did not score high enough against any original class.

### This client, classes with an `@OriginalClass` (all 15)

| Class | Bytes | Original |
|---|---:|---|
| `client/cycle/JaclibCycleManager` | 2,525 | `client!md` |
| `client/cycle/SystemCycleManager` | 2,445 | `client!oq` |
| `client/cycle/DefaultTickScheduler` | 2,432 | `client!cca` |
| `client/cycle/AbstractCycleManager` | 2,041 | `client!tb` |
| `ClientTrigger` | 1,649 | `client!uw` |
| `graphics/particles/ParticleEmitterRelated` | 1,608 | `client!vha` |
| `TextureCombineFunction` | 1,536 | `client!sia` |
| `entity/EntityOverhead` | 1,488 | `client!ql` |
| `config/rsinterface/JavaClippingMask` | 998 | `client!nba` |
| `entity/gameobject/GameObject` | 930 | `client!se` |
| `cache/node/impl/ShortNode` | 865 | `client!oia` |
| `core/io/socket/ProxyAuthenticationException` | 774 | `client!gv` |
| `graphics/ModelAndShadow` | 742 | `client!ak` |
| `GpuVertexNormal` | 690 | `client!fr` |
| `client/loading/loader/Loader` | 553 | `client!lu` |

(Paths below `com/jagex/game/runetek5/` are shortened.) `ProxyAuthenticationException` is likely
`IOException_Sub1`.

### This client, classes without an `@OriginalClass` (largest 20)

These are unmatched as classes by construction (see above). The last column is how many of their
members are mapped.

| Class | Bytes | Members | Mapped |
|---|---:|---:|---:|
| `CS2Interpreter` | 127,452 | 11 | 7 |
| `PacketDecoder` | 56,247 | 2 | 1 |
| `client/menu/MiniMenu` | 38,724 | 104 | 84 |
| `client/features/SmokeTest` | 35,961 | 123 | 0 |
| `Minimap` | 32,489 | 32 | 25 |
| `scene/Scene` | 31,922 | 85 | 72 |
| `scene/culling/Culling` | 29,699 | 56 | 44 |
| `utils/Utils` | 29,375 | 65 | 34 |
| `graphics/map/worldmap/WorldMap` | 29,295 | 57 | 49 |
| `InterfaceComponentDraw` | 28,140 | 2 | 1 |
| `config/rsinterface/cs2/ClientScriptOpCode` | 26,606 | 645 | 0 |
| `ClientCommandProcessor` | 26,545 | 2 | 1 |
| `OverlayManager` | 26,433 | 23 | 6 |
| `InterfaceComponentInput` | 23,443 | 2 | 1 |
| `OrthoMode` | 23,346 | 93 | 86 |
| `LoadingStageWork` | 22,274 | 2 | 1 |
| `GameTick` | 21,247 | 2 | 1 |
| `client/input/camera/Camera` | 19,122 | 103 | 95 |
| `SceneDrawer` | 18,320 | 33 | 21 |
| `sound/AreaSoundSampleMixer` | 17,721 | 18 | 1 |

## Using it

To carry a void-client change over: look the `void` owner, name and descriptor up in
`void-client-228561a.members.tsv` and use the `runetek5` columns. A member that is not in the file
is unmatched: find it by hand (its `@OriginalMember` names its 2010 owner and name, and void-client
keeps the 2010 layout), and do not trust the low-confidence matches above without reading both
sides.
