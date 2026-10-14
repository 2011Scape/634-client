# Rendering and toolkits

How the client draws a frame, how it picks a renderer, and where to add something new to draw. All
drawing goes through one `Toolkit` at a time, held in `ClientState.activeToolkit`.

Where its state lives: the active toolkit is a client-wide static in `ClientState`. The shared font
renderers are in `rendering/Renderers` (`FONT_RENDERER`). Every model, ground, sprite and font belongs
to the toolkit that made it.

## Where it lives

| Part | Class | What it does |
|---|---|---|
| The renderer API | `graphics/Toolkit` | The abstract toolkit: 2D (`fillRect`, `drawLine`, `outlineRect`), clipping (`KA`, `la`), the camera (`createMatrix`, `setCamera`), and the factories for models, grounds, sprites and fonts (`createModel`, `createGround`, `generateSprite`, `createNativeSprite`, `createFontRenderer`). `renderFrame` and `flipDirtyRects` show the frame |
| Toolkit ids | `graphics/ToolkitType` | `JAVA` = 0, `GL` = 1, `SSE` = 2, `D3D` = 3, `GLX` = 5, `HD` = 6 |
| Java | `JavaToolkit` (root package) | Pure Java software renderer. Always available; the fallback |
| SSE | `oa` | Native software renderer in the `sw3d` library |
| GL | `graphics/gl/GlToolkit` | OpenGL driven from Java through `jaggl.OpenGL` |
| GLX | `toolkit/GlxToolkit` | OpenGL in the native `hw3d` library. Shares `toolkit/AbstractNativeToolkit` with D3D |
| D3D | `toolkit/D3DToolkit` | Direct3D through `jagdx`. Windows only |
| HD | `graphics/hd/HdToolkit` (jvmMain) | 634HD: OpenGL 3.3 core, no Jagex library. See "634HD" below |
| Models | `graphics/model/Model` | `JavaModel`, `GlModel`, `GpuModel` (GLX and D3D) and `i` (SSE). `render`, `copy`, `transform`, `picked` |
| Grounds | `scene/ground/Ground` | One per level, made by `Terrain` with `createGround`: `JavaGround`, `GlGround`, `GpuFloorModel` (GLX and D3D) and `t` (SSE). `addTile`, `renderTiles`, `drawMinimap` |
| Sprites | `graphics/sprite/NativeSprite` | `SpriteSM` (`OpaqueSpriteSM`, `TransparentSpriteSM`, `PaletteSpriteSM`) for Java, `SpriteGL`, `GpuSprite` and `j` |
| Fonts | `graphics/font/AbstractFontRenderer` | `JavaMonoFont`, `JavaMonoAlphaFont`, `GlFont`, `NativeFont` and `h`. Metrics come from `FontMetricsLoader.getFontMetrics` |
| Scene | `SceneDrawer.drawScene` | Draws the grounds and entities for one camera |
| Interfaces | `InterfaceRootDraw.drawInterface`, `InterfaceComponentDraw.drawComponents` | Draw an interface and its components |
| Frames | `GameScreenFrameRender.drawGameFrame`, `MenuScreenFrameRender.drawInterfaceFrame` | One frame in game, or on the login and lobby screens |

## Native classes

The SSE toolkit and its objects are root-package classes with one- or two-letter names: `oa` (the
toolkit), `t` (ground), `i` (model), `j` (sprite), `h` and `n` (fonts), `ja` (matrix), `ya` (memory
pool), and others such as `a`, `ba`, `na`, `p`, `xa`. They implement `graphics/sw/SoftwareObject`.
The `jaggl`, `jagdx` and `jaclib` packages and `jagex3.graphics2.hw.NativeInterface` wrap the
OpenGL, Direct3D and memory libraries.

`jagdx`, `jagex3` and the SSE classes are bound by JNI. The native libraries look up their classes,
fields and methods by name and signature, so they keep their names, packages and signatures. Code
may call them freely; their declarations stay as they are.

`jaggl` and `jaclib`'s memory classes are no longer JNI on the JVM: they run on
[LWJGL](https://www.lwjgl.org) 3, with the signatures of the 2010 classes.

- `jaggl.OpenGL` makes its context through `jaggl/GlContext` and
  [lwjgl3-awt](https://github.com/LWJGLX/lwjgl3-awt): GLX on Linux (also under Wayland, through
  XWayland), WGL on Windows, and a legacy 2.1 context on macOS. No version or profile is asked for,
  so GLX and WGL give the newest compatibility context, which keeps immediate mode, the matrix
  stack and ARB programs. Each `gl*` function calls lwjgl-opengl.
- A call that takes an address (`gl*Pointer`, `glDrawElements`, `glBufferDataARBa`) passes it
  through as it is.
- A call that takes an array and an offset copies the array through `jaggl/GlScratch`.
- There are no pbuffers (`arePbuffersAvailable` is false), so the toolkits draw offscreen into
  framebuffers.
- `jaclib.memory` (`NativeHeap`, `NativeBuffer`) allocates real native memory through LWJGL's
  `MemoryUtil`, because the GL driver and `libhw3d` read and write through those addresses.
- `HardwareInfo` reports nothing.
- `libjaggl` is never loaded. `libjaclib` is still loaded where the cache has one, only because
  `libhw3d` (toolkit 5) links against it.
- Toolkits 0 and 1 need no Jagex native library, so they run wherever LWJGL does (Linux, Windows
  and macOS, x64 and arm64, and 32-bit Windows).
- Toolkit 5 still needs `libhw3d`, so it runs only on x86 and x86_64 Linux and Windows, and on
  Intel Macs.
- Linux x64 is tested. Windows, macOS and arm64 are untested so far.

## How a toolkit is chosen

- **At start-up.** `LoadingStageWork` calls `ToolkitSwitch.loadAssets` with the saved
  `toolkitDefault` setting, or Java.
- **Auto-detect.** `GraphicsAutoDetect.calculateGraphics` times SSE, D3D and GL with
  `ToolkitProfiler.getProfilingPerformance` and switches to the fastest with matching detail.
- **On request.** CS2 (the graphics options), console commands and the world map call
  `ToolkitSwitch.loadAssets(skipSettingsVerification, toolkitId)`.

A switch runs `ToolkitRebuild.loadAssets`:

1. `ActiveToolkitInit.prepareForRendering` calls `CanvasToolkitFactory.create`, which reads the
   canvas size and calls `ToolkitFactory.createToolkit`.
   That picks `JavaToolkitFactory`, `SseToolkitFactory`, `D3DToolkitFactory`, or the GL and GLX
   constructors, by id. Each native toolkit loads its library first.
2. If creation throws, it retries with the toolkit saved in the settings, the last one that worked.
3. It stores the id, calls `initialize`, creates the camera matrices, and applies the texture,
   bloom and scene-size settings.
4. Back in `loadAssets`, the sprites and fonts are rebuilt for the new toolkit and
   `GraphicCacheReset.resetGraphicCaches` drops every cached model and sprite.
5. The main step moves to its `*_MAP_BUILD` step, so the grounds are built again.

Every model, ground, sprite and font belongs to the toolkit that made it. None of them survive a switch.

## Each frame

1. `GameShell` calls `client.mainRedraw`, which calls `redraw`.
2. `redraw` draws by `MainLogicStep`: the loading screen, `MenuScreenFrameRender.drawInterfaceFrame`
   for login and lobby, or `GameScreenFrameRender.drawGameFrame` in game. The game frame moves the
   camera first.
3. Both frames call `Static701.method5594`, which calls `InterfaceRootDraw.drawInterface` for
   `BASE_WINDOW_ID`. `drawComponents` walks the components by `ComponentType`: `CONTAINER` (and any
   sub-interface), `FIGURE` (a rectangle), `TEXT` (`getFontRenderer`), `SPRITE` (`getSprite`), `MODEL`, `LINE`.
4. The viewport component calls `ClientState.drawGameArea`. It draws the skybox, sets the camera with
   `setCamera`, calls `SceneDrawer.drawScene`, then `OverlayManager.drawEntities` for hitsplats and
   overhead text.
5. The frame resets the clip (`la`) and sets the cursor. `redraw` then shows it with `renderFrame`,
   or `flipDirtyRects` for changed rectangles only.

If showing the frame fails, or the SSE toolkit throws while drawing, the client switches to Java.

## Adding a drawing operation

1. Draw through `ClientState.activeToolkit`. Never test for a toolkit class.
2. If `Toolkit` has no method for it, add an abstract method and implement it in `JavaToolkit`,
   `GlToolkit`, `AbstractNativeToolkit` (for GLX and D3D) and `oa`. In `oa`, build it from the
   existing native calls. Do not add or change a native method.
3. Create models, sprites and fonts from the active toolkit, and cache them where
   `resetGraphicCaches` or `ToolkitRebuild` drops them, so a switch does not keep stale objects.
4. Start the client (`./gradlew runClient`) and log in on each toolkit: Java OpenGL, native OpenGL
   and software, switched in the graphics options. The three share
   little code, and each has broken alone before.

## Example: tile markers

`client/features/TileMarkers` draws on top of the scene the way the overlays do: after
`OverlayManager.drawEntities` in `ClientState.drawGameArea`, it projects each corner of a tile with
`OverlayManager.projectToScreenWithOffset` (world units, 512 per tile, the tile's height from
`Scene`), adds the viewport's offset, and draws the outline with `Toolkit.line`, which every toolkit
implements. The menu entry that marks a tile is added beside Walk here in `ViewportMiniMenuPicking`
and handled in `MiniMenuActionExecutor`, with its own click type (`MiniMenuAction.TILE_MARKER`).

## 634HD (toolkit 6)

Not part of the original client; desktop only (`createHdToolkit` in `PlatformLoop.kt`; a browser
throws and falls back). The plan and its status are in `634hd-plan.md`.

- **Code.** `src/jvmMain/kotlin/com/jagex/game/runetek5/graphics/hd/` holds `HdToolkit`, `HdModel`,
  `HdGround`, `HdSprite`, `HdFont` and the rest. They are copies of the Java OpenGL toolkit's classes
  (toolkit 1 is never edited for 634HD) that call `gl/HdGl` instead of `jaggl.OpenGL`.
- **`gl/HdGl`** is jaggl's API on a GL 3.3 core context (`gl/HdContext`, made by
  `jaggl.GlContext.create(..., core = true)`; WGL through `wglCreateContextAttribsARB`). It is the
  browser's WebGL 2 emulator ported to LWJGL:
  - fixed-function state, immediate mode, matrix stacks, display lists, and ARB vertex programs
    transpiled to GLSL;
  - shaders in `src/jvmMain/resources/634hd/shaders/` (`ff.vert`, `ff.frag`; `HdShaders` loads them
    and prints the driver's log against numbered lines);
  - luminance, alpha and intensity textures become R8/RG8 with swizzles;
  - consecutive unlit immediate-mode draws in the same fragment state are drawn as one. Every
    `CoreGl` call draws a pending batch first, so GL sees draws in the client's order.
- **Retained scene** (`scene/HdSceneRenderer`). Static locs (`Static*` entities on the scene's static
  lists) are captured once per region (`Toolkit.sceneBuilt`, from `MapBuildStep`) by running their own
  `render()` in a capture mode. Their uploaded geometry is read back into one buffer of 8×8-tile chunks.
  - **Per frame:** `SceneDrawer` still decides what is drawn. `HdModel.render` marks a retained
    object visible with the matrices and point lights the emulator would use. After the opaque
    entities (`Toolkit.opaqueEntitiesDone`) the visible chunks draw in one `glMultiDrawElements`
    (`scene.vert`, `scene.frag`).
  - **Translucent locs** draw when the client draws them, faces sorted on the CPU by priority group,
    then back to front.
  - **Materials** are copied into texture arrays (`scene/HdMaterials`). Water, environment maps and
    HDR stay on the emulated path, batch by batch.
  - **Changes.** `Toolkit.sceneChanged` (hooked in `Scene`'s add and remove functions) rebuilds a
    chunk. A region builds a few chunks per frame, nearest first.
- **Streamed geometry** (`scene/HdDynamicStream`). Animated models upload into ranges of one stream
  buffer, orphaned when full, instead of rewriting a shared buffer between draws.
- **Fallback.** `ActiveToolkitInit` falls back 6 → 1 → 0, and `client/features/HdFallback` says why in
  the chat box. `client.test.hdFail=context|extension` forces it.
- **Selection.** `client.toolkit` / `--toolkit`, `::tk6`, and `client/features/HdOptionsButton`, a
  toggle the client draws in the Graphics Options title bar (the screen is a cache script that knows
  toolkits 0 to 5 only). Turning it on for the first time, with nothing saved, applies the Medium
  preset.
- **Settings panel** (`client/features/HdSettingsPanel`): the "Settings" button at the right of that
  title bar, or `::hdsettings`, opens a window the client draws over the interfaces: presets (Off,
  Low, Medium, High, Ultra; `HdSettings.applyPreset`, also `::hd preset <name>`) and a row per
  switch, each click stepping to its next value. Changes apply at once and are saved to
  `~/.634hd-settings.txt`, read at the next start unless `client.hd.*` switches are given. Two rows
  are the whole client's, outside the presets: draw distance (1x, 1.5x, 2x; `ClientFeatures.RENDER_DISTANCE`,
  live: the map is built again) and interface scale (auto or 1x-3x; `ClientFeatures.SCALE`, from the
  next start, since the window's layout is made at start). Saved, they win over the launcher's
  defaults (`LauncherDefaults` marks what it set with `<name>.default`) but not over `--render-distance`
  or `--scale`; also `::hd renderDistance <n>` and `::hd scale <n>`. The smoke test neither reads nor
  writes that file; `client.test.findText=open:742` checks the toggle, the Medium preset, the panel's
  presets, rows (the draw distance's map rebuild too) and Close.
- **Checks:**
  - the scene harness against toolkit 1's references (`-Pclient.test.toolkit=6
    -Pclient.test.scenes=default -Pclient.test.sceneReferenceToolkit=1`): every scene, the reload, the
    relog and a loc change within the noise of toolkit 1 against itself (40 per 1000; the cow
    field, whose cows wander between runs, has its own limit of 65: toolkit 1 against itself there
    differs by 33 to 57);
    the list also holds two scenes full of NPCs (Varrock palace guards, Barbarian Village; limit 65)
    and four close-ups of the player in outfits that between them use every way an item's material
    draws (`client.test.materialScan` lists the material classes and items using each; a scene's
    eighth value dresses the player, on the client only, and the harness puts the clothes back; the
    player is held in one pose and only the middle third of the view is compared, so the outfits
    match toolkit 1 to 0-1 per 1000 and have a limit of 25, room for a passer-by);
  - flicker and pops: `client.test.sceneBurst=N` measures the 3D view's mean brightness every frame
    and fails on a frame-to-frame jump above `client.test.sceneLumaJump` (default 3), saving the two
    frames (`build/smoke/scenes/jump-*`); `client.test.sceneWalk` walks during it
    (`sceneWalkTiles` at a time, again every `sceneWalkEvery` frames: far enough to load a new
    region), `client.test.sceneBurstLoc=N` takes a loc out at frame N, `sceneBurstSave=false` keeps
    only the measurement, `sceneLumaLog` prints every frame's value. The game's own roof hiding (the
    player stepping under or out of a roof, a real jump of 5 to 15) is detected from
    `MapRegion.roofMin*/roofMax*`, the remove-roof flag of the player's tile and the scene base (a map
    rebuild), and the three frames after a change are not measured; a negative `sceneWalkTiles` walks
    west; after a relog the harness teleports back to the scene. Long walks still pass through
    changes of the game's own that are not flickers (toolkit 1 jumps by up to 7 there too), so the
    occlusion check runs on the occlusion alone: `-Pclient.test.hdOverlay=6` (applied without the
    debug panel in test runs) draws only the applied occlusion, where roofs and rebuilds barely
    register. Draynor eastwards, 2000 frames across region borders with AO and shadows on: largest
    jump 1.3;
  - the regular smoke test against toolkit 1's references;
  - the bench at a position (`-Pclient.test.scenePosition=...`), which prints draw calls by category,
    the region-build hitch and chunk rebuilds (`client/features/RenderStats`).

### 634HD features (Phase 4, 5 and content)

Every feature is off by default and leaves the frame as toolkit 1 draws it (the parity harness runs
with all of them off). Switches live in `client/features/HdSettings`: `client.hd.<name>=<value>` at
start (`--hd name=value,...` in the launcher), `::hd <name> <value>` while playing, or the debug panel.

| Switch | What it does | Where |
| --- | --- | --- |
| `tonemap` (aces, agx), `dither` | linear colour, half-float view, filmic tonemap | `scene/HdPost`, `post_composite.frag` |
| `bloom`, `fxaa`, `colourGrading` | post chain; a 16³ LUT per environment (`luts/`, `tools/make_luts.py`) | `scene/HdPost` |
| `mipmaps`, `anisotropy` | full mip chains for every world texture, anisotropic samplers | `HdTextureCache`, `scene/HdMaterials` |
| `pixelLighting` | the fixed-function formula per pixel | `ff.frag`, `scene.frag` |
| `lights` | point lights from `lights.json`, 64 nearest | `scene/HdLights`, `hdPointLights` |
| `shadows` (1024/2048/4096) | sun shadow map, PCF | `scene/HdShadows`, `hdShadow` |
| `terrainShadows` | plane 0's tile-corner heights as a caster mesh in the cached static map, drawn 96 units low so flat ground does not shade itself (with a low sun hills shade what is behind them) | `HdShadows.ensureTerrain`/`drawTerrain` |
| `cutoutShadows` | static casters drawn with their textures: texels (or faces) under half opaque cast nothing, so leaves, wheat and fences cast their shape | `caster_cutout.vert`/`.frag` |
| `ambientOcclusion` | baked: the region's static scene into a height map when its chunks change or the player changes floor - roofs and the floors above the player left out, as the game hides them indoors - then (a few ms a frame) into every retained vertex and into a texture per ground level, four samples a tile side; a ground pixel reads one texel, and measures the moving models' map (the player left out; walls only, never cover overhead) itself only near the player; other moving models measure per pixel. A new bake never changes the look before it is complete (a ground keeps its previous texture; a rebuilt chunk is shaded at once from the last bake); after a region load occlusion fades in once everything is baked | `scene/HdShadows` (`bindGroundOcclusion`), `HdSceneRenderer.skyVisibility`, `hdGroundOcclusion`, `hdAmbientOcclusion` |
| `fog`, `fade`, `sky` | environment fog, dithered fade at the edge, gradient sky and height fog | `scene/HdFeatures`, `hdFog`, `sky.frag` |
| `water` | wave normals, Fresnel sky reflection, glint, tint; per area (environments.json `water`: tint, strength, waves, speed, size, glint, eased between areas) | `hdWater`; `GlState.hdWater` |
| `terrainBlend` | triplanar on slopes, anti-tiling blend, broad variation on the ground | `hdTerrainTexture`; `GlState.hdGround` |
| `foliage` | wind sway from `foliage.json`; alpha to coverage with MSAA | `scene.vert`, `scene/HdSceneRenderer` |
| `worldTextures` | 634HD's own materials on models (`model_overrides.json` `material`) and tiles (`tile_overrides.json`): faces and tiles the game leaves untextured, or all with `replace`; toggling rebuilds the map | `HdSceneRenderer.appendModel`/`overrideUv`, `HdGround.hdRetexture` |
| `dayNight` (off, realtime, or minutes a day) | the time of day moves a sun (up to 70 degrees at noon, from the east) or, while it is down, a moon across from it: their direction replaces the game's light 0 (and light 1 opposite it) in every lighting path and steers the shadow sun; warm light near the horizon, faint blue moonlight, ambient down to 0.45; the sky and fog darken to a night blue and glow at dusk; `sky.frag` draws stars and the moon's disc. Not in the presets. `client.test.timeOfDay` (0 midnight, 0.5 noon) fixes the time in a smoke run | `HdFeatures.dayNight`, `hdLightDirection`, `hdDayNight`/`hdMoon`, `sky.frag` |
| `caustics` | an animated, procedural web of light on the ground the game's underwater pass draws (the riverbed; `GlState.hdUnderwater` between `ra` and `pa`) | `hdCaustics`, `ff.frag` |
| `grassDisplacement` | grass and plants (foliage with wind strength 0.7 and up) bend away from the player and up to seven NPCs within 10 tiles (`hdActors`) | `scene.vert`, `HdFeatures.findActors` |
| `environmentLighting` | the environment's `sun` colour and intensity and `ambient` tint the game's light 0 and ambient light (`hdEnvSun`/`hdEnvAmbient`); `sun.yaw` turns the shadow sun; every field eases between areas, the grading table crossfades | `HdFeatures`, `hdEnvDiffuse`/`hdEnvAmbientLight`, `HdPost.useGrading` |
| `softParticles` | particles fade into geometry (depth copy) | `scene/HdSoftParticles` |
| `interfaceScale` (2/3/4) | the window at a whole-number scale, 3D at native resolution | `gl/HdGl.windowScale` |
| `materials` | Phase 5 normal / ORM / emissive / height maps | `scene/HdMaterialMaps`, `hd_material.glsl` |
| `particles` | GPU particles: embers over fires and torches, sparks over lava, one instanced draw | `scene/HdGpuParticles`, `particles.vert` |
| `smoothInterface` | the scaled interface sampled with smoothstep bilinear | `ff.frag` `sharpBilinear` |

- **Frame data.** `scene/HdFeatures` works out per-frame values once the camera is set and writes
  the `HdFrame` uniform block (`scene/HdFrameUniforms`, std140, binding 0). `hd_lib.glsl` holds the
  block and the shared functions; `hd_material.glsl` the fragment-only ones. `HdShaders.link` points
  every program's samplers at their units: shadow map 16, height map 17, soft-particle depth 18,
  material arrays 19-21 (block `HdMaterialMaps`, binding 1), baked occlusion 22, the ground's baked
  occlusion 25.
- **`castShadows: false`** in `model_overrides.json` keeps an object (by loc id) out of the sun's
  static map and the occlusion bake: its matrix in the casters' buffer is zero.
- **Shadow casters.** Static: every retained object in the chunks around the player, opaque or with
  transparent parts (tree canopies), on screen or not, drawn into a cached map (`staticMap`) only when the map's centre moves a four-tile step or
  the region changes, and copied into the sun's map each frame. Moving: every emulated indexed draw
  `HdGl` records between the camera and `opaqueEntitiesDone` (not the ground). A recorded draw keeps
  its buffers' data by copy-on-write: when `HdGl` is about to write a buffer a caster was drawn
  from, that caster's vertices or indices are copied on the GPU first (`HdShadows.beforeWrite`).
  The animated-model stream starts fresh storage only at the start of a frame, so ranges recorded
  in a frame stay valid until its map is drawn. Translucent entities (drawn after the map) cast
  into the next frame's map. Moving models drawn before the map is made do not receive shadows.
  While sun shadows are on, the game's own blob shadows under locs (`HdShadowMap`'s overlay on the
  ground) are not drawn; the wall darkening baked into the ground's colours stays.
- **Memory.** The retained scene's CPU geometry and read-back copies, and the shadow casters' index
  copies, are allocated with LWJGL's allocator and freed when the toolkit stops: left to the garbage
  collector, a few switches to and from 634HD ran out of direct memory.
- **Content** (`hd/content/HdContent`): `src/jvmMain/resources/634hd/data/*.json`, each with a
  `*.schema.json` checked at load (`JsonSchema`; a bad entry is skipped with its file and line). With
  `client.debug` from the repository (or `client.hd.dataDir`) the folder is read directly and
  reloaded when a file changes. `client.test.findText=locs:<regex>`, `locmodels:<regex>`, `npcs:` and
  `textures:` list or dump cache data for writing it; `client.test.hdDebug` prints loc ids near the
  player and texture use. The debug panel's Inspect tab shows the tile under the mouse (position,
  plane, floor underlay and overlay ids: `Terrain.floorIds`) and each loc (id, shape, rotation, model
  ids and its untextured face colours as the loc recolours them: what `faceColours` takes);
  `client.test.inspect=true` prints the same for every loc within `client.test.inspectRadius` tiles.
- **Content by area** (Lumbridge, then Varrock). Lights: every cooking range that burns (`Cooking
  range` and `(lit)`, not `(empty)`/`(with logs)`), cooking pots over fires, ovens, fireboxes, fire
  pits, furnaces, elven lamps and mushroom torches joined `lights.json` (found with
  `client.test.findText=locs:<regex>` against what it had). Environments: Lumbridge (warm sun and
  ambient, a calm river), Varrock (cooler city light), and a new Lumbridge swamp (regions 12593 and
  12849: hazier sky, a soft mist, murky green, slow water, the swamp grade). Foliage: the courtyard's
  grass tufts bend and sway. Textures: `client.test.hdFloorTally` counts the floor faces each region
  leaves untextured (Lumbridge 24, Varrock 12: the cache textures nearly every floor) and the Inspect
  lists show the few untextured loc faces, so these areas need no texture overrides; the engine is
  there for areas that do.
- **634HD's own materials.** `materials.json` entries from texture 10000 up with an `albedo` file in
  `634hd/materials/<id>/` (`content/HdMaterialProvider` serves them beside the cache's, so every
  path draws them): near-grey detail the face or tile colour tints, made by
  `tools/make_detail_textures.py` (bark 10001, stone 10002, brick 10003, roof tiles 10004, planks
  10005, grass 10006, dirt 10007, cobbles 10008; procedural, no third-party art). Most of 634's
  models and floors are already textured, so overrides mostly replace (`replace: true`) or fill the
  few untextured faces (match them by `faceColours`). Model overrides map uvs by box projection in
  world space (`uv`, `uvScale` tiles a repeat); tile overrides take `scale` tiles a repeat.
- **Materials.** `tools/make_material_maps.py` builds map sets from the cache textures (dumped
  with `client.test.findText=textures:`) into `634hd/materials/<texture>/` with a `recipe.txt` each.
  Fine relief comes from height maps made with `/generate` (nano-banana-2-lite, the cache texture
  as the reference; output and sidecar in `~/Pictures/generations/634hd_height-<id>_*`, prompt in
  `prompt.txt` and the sidecar in `generate.json` beside each set): their high frequencies over the
  albedo's own shapes, normals then computed from that (plan rule 3), every height made to tile
  (checked: no edge seam left in any of the 23). It
  and writes `materials.json`. `./gradlew compressMaterialMaps` (`tools/compress_material_maps.py`)
  precompresses them at build time - BC5 normals, BC7 (mode 6) packed maps with the height in alpha,
  BC7 emissive maps, every mip level - and `HdMaterialMaps` uploads those blocks as they are (23 sets,
  5 MB; GPU decode within 1.8/255 of the PNGs for BC5, 3.3 for BC7, checked with
  `client.test.hdDebug`). Without a precompressed file the driver compresses the PNG at load; without
  BC7 the arrays are RGBA8.
- **Debug panel** (`HdDebugPanel`, `client.debug` only; F10 or `::hdpanel`): stats with GPU time,
  every switch, overlays, live editing of the current environment and nearest light with Save to
  the JSON file, texture and mini-menu inspection, content warnings.
- **Drivers checked:** NVIDIA (RTX 5070, 3.3 core) and Mesa's GLSL compiler on llvmpipe with a
  forward-compatible 4.1 core context, as a Mac asks for (`__GLX_VENDOR_LIBRARY_NAME=mesa
  LIBGL_ALWAYS_SOFTWARE=1`, `-Pclient.test.hdGlVersion=4.1`): every shader compiles, every feature
  runs, and with them off the harness matches toolkit 1. Intel (Iris) on Linux uses the same Mesa
  compiler. Windows and macOS themselves, and Iris Xe and RTX 3060 speeds, are not measured.
- **Not done:** foam on water, and
  blending across tile texture edges. Occlusion is baked into a height map per region, not into
  vertices. The game's own particle systems stay CPU-simulated; the GPU
  particles are an added, data-driven system.
