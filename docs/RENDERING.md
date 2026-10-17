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
- **Settings panel** (`client/features/HdSettingsPanel`): the "634HD Settings" button at the right of that
  title bar, or `::hdsettings`, opens a window the client draws over the interfaces: presets (Off,
  Low, Medium, High, Ultra; `HdSettings.applyPreset`, also `::hd preset <name>`) and a row per
  switch, each click stepping to its next value. Changes apply at once and are saved to
  `~/.634hd-settings.txt`, read at the next start unless `client.hd.*` switches are given. Two rows
  are the whole client's, outside the presets: draw distance (1x, 1.5x, 2x; `ClientFeatures.RENDER_DISTANCE`,
  live: the map is built again; see below) and interface scale (auto or 1x-3x; `ClientFeatures.SCALE`, from the
  next start, since the window's layout is made at start). Saved, they win over the launcher's
  defaults (`LauncherDefaults` marks what it set with `<name>.default`) but not over `--render-distance`
  or `--scale`; also `::hd renderDistance <n>` and `::hd scale <n>`. The smoke test neither reads nor
  writes that file; `client.test.findText=open:742` checks the toggle, the Medium preset, the panel's
  presets, rows (the draw distance's map rebuild too) and Close.
- **Draw distance:** the game's own longer view, a bigger scene. The scene the client builds is
  sized from the last region packet (`StaticRegionInit.hdApplySceneSize`): 136 tiles at 1.5x, 168 at
  2x, never smaller than the server's (Void sends 104). The server's keys are matched to the regions
  by position and the rest get zero keys (Void's maps are not encrypted); the map data comes from the
  cache. The zone offsets the server sends (loc changes, ground items, spot animations) are from the
  base of its own scene, so the client adds half the size difference (`ClientFeatures.serverZoneShift`,
  `UpdateZonePacket.hdServerZoneShift`); a live size change moves the base and the entities as a
  region packet does (`MapRebuild`). The fog's end and the ground's draw radius then come from the game's own tables for the size
  (46 and 47 tiles at 168, against 29 and 32 at 104), so the loaded world extends well past the fog.
  (Scaling the fog and the draw radius of the 104-tile scene instead put the fog's end on the scene's
  edge: the sea stopped in a wall, the ground in a cliff, nothing beyond.) NPCs, players and the
  server's own locs beyond the server's 104 tiles are not there until the server's view moves. A
  168-tile scene needs about twice the heap of a 104 one; `clientJvmArgs` gives 512 MB; under 448 MB
  the client builds 136 instead and under 320 MB only the server's scene (`ClientFeatures.sceneMapSize`;
  168 and 136 both went out of memory at 256 MB).
- **Checks:**
  - the build gates: warnings as errors on every target, ktlint and detekt against their baselines
    (`./gradlew check`, in `build` and CI; CLAUDE.md);
  - unit tests in `src/jvmTest` (`./gradlew jvmTest`, part of `build` and so of CI): the logic that
    needs no running client, `ClientFeatures.sceneMapSize` so far;
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
    match toolkit 1 to 0-1 per 1000 and have a limit of 25, room for a passer-by), and a fifth from
    behind (`outfit_firecape`, yaw 8192) with the fire cape's lava in view: the game's scrolling
    texture with everything off, the flowing, unlit material with `materials` on;
  - flicker and pops: `client.test.sceneBurst=N` measures the 3D view's mean brightness every frame
    and fails on a frame-to-frame jump above `client.test.sceneLumaJump` (default 3), saving the two
    frames (`build/smoke/scenes/jump-*`); `client.test.sceneWalk` walks during it
    (`sceneWalkTiles` at a time, again every `sceneWalkEvery` frames: far enough to load a new
    region), `client.test.sceneBurstLoc=N` takes a loc out at frame N, `sceneBurstSave=false` keeps
    only the measurement, `sceneLumaLog` prints every frame's value. The game's own roof hiding (the
    player stepping under or out of a roof, a real jump of 5 to 15) is detected from
    `MapRegion.roofMin*/roofMax*`, the remove-roof flag of the player's tile, the roof stamps within
    12 tiles (`MapRegion.roofStamps`, what the scene draws roofs by) and the scene base (a map
    rebuild), and the three frames after a change are not measured (about 6% of a long walk); a negative `sceneWalkTiles` walks
    west; after a relog the harness teleports back to the scene. Long walks still pass through
    changes of the game's own that are not flickers (toolkit 1 jumps by up to 7 there too), so the
    occlusion check runs on the occlusion alone: `-Pclient.test.hdOverlay=6` (applied without the
    debug panel in test runs) draws only the applied occlusion. With the roof stamps the whole-frame
    check passes on its own: Draynor eastwards, 2000 frames across region borders, a loc taken out
    mid-walk, AO and shadows on: largest jump 1.3 (toolkit 1 on the same walk: 2.1); the code before
    the occlusion fixes (5cc6657) fails it at 6.3 and 4.8, the ground's occlusion switching;
  - the regular smoke test against toolkit 1's references;
  - the bench at a position (`-Pclient.test.scenePosition=...`), which prints draw calls by category,
    the region-build hitch and chunk rebuilds (`client/features/RenderStats`).

### 634HD features (Phase 4, 5 and content)

Every feature is off by default and leaves the frame as toolkit 1 draws it (the parity harness runs
with all of them off). Switches live in `client/features/HdSettings`: `client.hd.<name>=<value>` at
start (`--hd name=value,...` in the launcher), `::hd <name> <value>` while playing, or the debug panel.

| Switch | What it does | Where |
| --- | --- | --- |
| `tonemap` (aces, agx), `dither` | linear colour, half-float view, filmic tonemap. Exposure 0.95 (ACES) and 1.05 (AgX, with a little saturation back): the view keeps the game's brightness. The game's light multiplies the sRGB colour before it is made linear (`hdApplyLight`, `ff.vert`, `scene.vert`) and highlights are added in sRGB terms (`hdAddHighlight`): applied in linear light the same amounts flattened the shading and over-brightened highlights, a plastic look. In linear mode every sRGB input is converted - the base texture, the other texture stages (environment maps, the water's wave textures), the stage constant (water tints), the ARB water programs' vertex colours - and fog is mixed in sRGB as the game mixes it (in linear light thick fog turned dark water pale, Draynor's sea) | `scene/HdPost`, `post_composite.frag`, `ff.frag`, `hdFogMix` |
| `bloom`, `fxaa`, `colourGrading` | post chain; a 16³ LUT per environment (`luts/`, `tools/make_luts.py`). The bloom is the only one on toolkit 6: the game's own (Graphics Options' Bloom, `HdRectangularTexture`) is never available there (`supportsBloom` is false, its compositor display is not created). It hides the halos the cache marks hide-with-bloom (`HdModel.renderBillboards`), as the game's bloom does. The bright pass reads the half-float view, so anything unclamped there blooms. Fixed-function lighting clamped colour × light at 1 per channel (a strong enough light made a face white); in linear colour the product is kept over 1 for the tonemapper but no further than `HD_LIT_MAX` (2, white under the tonemap), per channel as the game's clamp was, by `hdLitColour` in `hd_lib.glsl` (used by `hdApplyLight`, `ff.vert` and `scene.vert`). The game's point lights attenuate as 1 / (d / radius)² with no constant term, and a lantern's own flame sits at its light: unclamped it reached the half-float ceiling and bloomed into a blob the size of Edgeville bank; capped lower (a ceiling on the light alone) the flame stayed its own dull orange and nothing glowed. Bloom strength 0.175, threshold 0.75 in linear colour. `client.test.hdSceneMax=true` prints the view's brightest value every 30 frames to find such a source | `scene/HdPost`, `ff.vert`, `hd_lib.glsl`, `scene.vert` |
| `mipmaps`, `anisotropy` | full mip chains for every world texture, anisotropic samplers | `HdTextureCache`, `scene/HdMaterials` |
| `pixelLighting` | the fixed-function formula per pixel | `ff.frag`, `scene.frag` |
| `lights` | point lights from `lights.json`, 64 nearest, falling off as a windowed inverse square (`hdPointFalloff`: 1 at the light, its knee a third of the radius out, nothing at the radius; the radii in lights.json were widened for it). Separately, and always on toolkit 6: the game's own map lights flicker on the 50 Hz logic tick (a sine, a noise table that jumps every 20 ms, or a hard 1–4 Hz blink), which fixed-function lighting's clamp at 1 mostly hid; in linear colour, through the bloom's threshold, the steps read as a strobe. `scene/HdFlicker` low-passes each light's intensity (0.3 s), floors it at 70 % and, for the lights the game flickers, adds a flame's shimmer of its own (a 1.1 Hz drift and a 4.3 Hz tremble, at most 10 %, each light on its own phase) between `PointLight.getIntensity` and the GL light, so a flame breathes instead of blinking; the original curves are untouched | `scene/HdLights`, `hdPointLights`; `scene/HdFlicker`, `HdToolkit.updatePointLights` |
| `playerLight` (on in Medium and up) | the light of the lit light sources the player carries or wears (an item with an Extinguish action in the backpack, container 93, or worn, 94; a worn Firemaking cape or Fire cape): brightness tiers from the game's light sources (torches and candles 1; oil lamp, oil lantern, sapphire lantern, mining helmet, fire cape 2; bullseye and emerald lanterns 3) add up to at most 3 and set the reach (4.5, 6, 7.5 tiles) and strength; flames warm and flickering, the fire cape's lava a deeper orange, lanterns steady yellow; a sapphire lantern makes it blue, an emerald lantern green, both teal. Full at night (`dayNight`), faint by day. `client.test.carry=<id>,...` puts items in the client's own backpack for checks, `client.test.wear=<slot>:<id>,...` dresses the player and fills the worn container, `client.test.hdItemScan` lists the cache's candidate items | `HdLights.findCarried`; the first point light |
| `shadows` (1024/2048/4096) | sun shadow map, PCF | `scene/HdShadows`, `hdShadow` |
| `cutoutShadows` | static casters drawn with their textures: texels (or faces) under half opaque cast nothing, so leaves, wheat and fences cast their shape | `caster_cutout.vert`/`.frag` |
| `ambientOcclusion` | baked: the region's static scene into a height map when its chunks change or the player changes floor - roofs and the floors above the player left out, as the game hides them indoors - then (a few ms a frame) into every retained vertex and into a texture per ground level, four samples a tile side; a ground pixel reads one texel, and measures the moving models' map (the player left out; walls only, never cover overhead) itself only near the player; other moving models measure per pixel. A new bake never changes the look before it is complete (a ground keeps its previous texture; a rebuilt chunk is shaded at once from the last bake); after a region load occlusion fades in once everything is baked | `scene/HdShadows` (`bindGroundOcclusion`), `HdSceneRenderer.skyVisibility`, `hdGroundOcclusion`, `hdAmbientOcclusion` |
| `fog`, `fade`, `sky` | `fog`: 634HD's fog, in the game's units (512 a tile), the draw distance being the fog's end (the game's tables for the scene size, or `client.renderDistance`): the greatest of an edge fog over the five tiles before the loaded scene's edge or the draw distance's square (corners rounded), a linear ramp over the last 15% of the distance, and an exponential fog by the environment's `fog.depth` (scaled on the CPU by min(tiles, 90) / 10: 6.5 in the open overworld, 25 in most areas, 50 in the deep desert), combined with a ground fog by height (`groundFog`: full below its start, none above its end, world y, at its opacity, fading in over the first 6000 units) as 1 - (1 - a)(1 - g); mixed in sRGB (`hdFogMix`) in the environment's `fog.colour`, with `sky` in the sky's colour behind the fragment seen through the fog (`hdFoggedSkyColour`: the fog colour tints the horizon by the unscaled depth, so the desert's haze shows against its sky, which `sky.frag` draws the same way); the seabed takes the distance fog but no ground fog; the water surface's alpha is raised by the fog (`a = fog + a(1 - fog)`). The environment is the first of environments.json whose area holds the player (hand-written entries first, then the 209 generated ones, marked `source` with the area they cover: fog colour and depth, ground fog, the area as bounds, nothing else); a change blends in over 3 s, smoothstepped, at once after a jump of 41 tiles or a content reload; the game's own fog colour and the time of day keep the 0.4 s ease. Overlay 10 shows the distance fog, the ground fog and the fog colour by screen third; `client.test.hdDebug` prints `HDDEBUG fog` with the depth, the end, the ground fog, the transition and the environment. `fade`: with `client.renderDistance` above 1 the fade's band is a quarter of the fog's end (3 tiles otherwise), so the far sea and shore fade into the sky over a long way; without `fog`, everything also fades into the sky within 8 tiles of the loaded scene's edge whatever the distance (`hdSceneSize`): at a long draw distance the view reaches past it (with `fog` its edge term does this) | `scene/HdFeatures`, `hdFog`, `hdDistanceFog`, `hdGroundFogAmount`, `sky.frag` |
| `water` (0 off, 1 Medium, 2 High and Ultra) | 1: the game's water as the standard OpenGL toolkit draws it (its animated net over the fogged seabed), kept right under 634HD's other features: the seabed's fog (the still water effect's stage 1, by depth and distance) is made once more after the HD steps (material maps, the lighting model, world textures, caustics), which brought the seabed's stretched textures back through the sea as streaks. Water draws are composed in sRGB under the tonemap and shown as the game shows them, the tonemap's exposure and curve undone (`hdTonemapInverse`: the curve added contrast to a surface made for plain sRGB), and take the time of day's light (`hdUnlitLight`); with the fade on, water fades into a sea horizon. 2: 634HD's own surface in place of the game's sheet on the water draws, the ground's and the water models' (ponds and pools that are locs: the Lumbridge swamp's, the Al Kharid oasis; waterfalls keep the game's), 634HD's water: its normal map scrolled in three layers (3, 5 and 7 tiles a repeat) and bent by its flow map (128x128 maps, trailing layers of the flow array), lit by the sun on the ripples with the sun's shadow, the ambient light, half the fog colour as sky light and the point lights (`hdPointLights`, `hdPointHighlights` on the ripples' normal in eye space), the sun's glint (`pow(dot(view, reflect(sun)), gloss)`, with `hdSunStrength`, so the moon does not glint like the sun), the reflection mixed in by a fresnel share (`clamp(mix(0.4, 1, (1 - dot(view, n)) * 1.2))`: the sky in the reflected direction with `sky`, else its gradient of the area's water tint, with the scene mirrored in the water where `reflections` drew it mixed in at `HD_WATER_MIRROR_SHARE`, bent by the ripples; at full strength the water was a pane of glass), foam at the shore (the foam map, where the vertex's water depth is under `HD_WATER_FOAM_DEPTH`), a quarter darkening of plain water, an alpha (the greatest of the type's opacity, the foam, the fresnel and the glint), raised as the water deepens (`HD_WATER_DEPTH_OPACITY`) and by the fog; a water model has no vertex depth, so no foam, and no seabed pass, so its type's depth colour is laid under the surface and it is at least `HD_WATER_MODEL_OPACITY` opaque (the oasis over bright sand was a pale, flat sheet), and it takes the gradient alone, never the mirrored scene (which is mirrored about the ground's water plane: a pond at its own height got the willow's lit canopy displaced over it at night); a `flat` type (the swamp, SWAMP_WATER_FLAT on texture 895) is opaque over its depth colour; the seabed under it (the game's underwater pass) tinted by the depth colour over the first 30% of `HD_WATER_DEPTH_RANGE` (three tiles) and towards black below (`hdWaterBed`), the caustics kept; neither is shown through the tonemap's inverse. The water types (`materials.json` `water` on a water texture: surface, foam and depth colours, opacity, specular and gloss, normalStrength, fresnel, duration, hasFoam, flat; the defaults WATER's; `HdWaterTypes`, a third block, binding 3; the type at `hdMapD.w`), modulated per area by `environments.json` `water` (tint and strength pull the surface colour's hue, at the type's own brightness, and hue the gradient; speed divides the period; waves, glint and size scale the ripples, the glint and the tiling). The vertex's water depth reaches the surface draw as `vArbWaterDepth` (the transpiler exports the ground's third texture coordinate, which the water programs drop) The material maps and the flow array are bound whenever this is 2, with `materials` off too. `client.test.hdDebug` prints `HDDEBUG ground water`: the water vertices, their greatest depth, the plane and each layer's fog scale and intensity | `ff.frag`; `hd_material.glsl` (`hdWaterSurface`, `hdWaterBed`); `GlState.hdWater` (the effect's number)
| `reflections` (on in High and Ultra) | the scene mirrored in the water: once a frame, before the ground and the water, the retained static scene (`drawAllForBake`, a colour program `reflect.vert/frag`: lit as the scene is, fogged, clipped to above the water) and the sky are drawn mirrored about the water plane (the mean height of the player's ground's water tiles, `HdGround.hdWaterPlaneY`) into a half-size texture (unit 26); the water surface draws sample it at their screen position: with `water` 1, bent by the game's wave texture, as a layer of its own under the game's surface sheet, at an opacity set by the angle of view (3% looking down to 45% at grazing angles), the sheet keeping its own opacity (raised to the reflection's, the near-white sheet made the water pale, grey at night); with `water` 2, bent by the ripples' normal and mixed in by the fresnel share and the type's fresnel amount (`hdWaterReflection`), the sky or the gradient standing in where the texture holds nothing (the ground is not reflected, so a bank's reflection is the sky's colour). The reflection is fogged as the view is (`hdFog`, from the direction the eye sees the object in at the mirrored distance) and its sky is drawn sRGB (`uSrgbOut`), so it stays right at dusk and at night. Players, NPCs and animated locs are reflected from their recorded draws (`HdShadows.drawReflected`: the shadow recorder keeps each emulated draw's attributes, texture and lighting for it; the translucent ones, drawn after the pass, a frame late; a retained model's own emulated draws are left out, `GlState.hdRetainedDraw`); the ground and water models are not. Debugging: `client.test.hdReflectionShot=<png>` saves the reflection texture every 120 frames, `client.test.hdReflectionParts=static,dynamic,sky[,nomirror][,now|late]` picks what it holds | `scene/HdReflections`; `HdShadows.drawReflected`; `ff.frag` |
| `terrainBlend` | triplanar on slopes, anti-tiling blend, broad variation on the ground | `hdTerrainTexture`; `GlState.hdGround` |
| `foliage` | wind sway from `foliage.json`; alpha to coverage with MSAA | `scene.vert`, `scene/HdSceneRenderer` |
| `worldTextures` | 634HD's own materials on models (`model_overrides.json` `material`) and tiles (`tile_overrides.json`): faces and tiles the game leaves untextured, or all with `replace`; toggling rebuilds the map | `HdSceneRenderer.appendModel`/`overrideUv`, `HdGround.hdRetexture` |
| `dayNight` (off, realtime, or minutes a day) | the time of day moves a sun (up to 70 degrees at noon, from the east) or, while it is down, a moon across from it: their direction replaces the game's light 0 (and light 1 opposite it) in every lighting path and steers the shadow sun; warm light near the horizon, faint blue moonlight, ambient down to 0.45; the sky and fog darken to a night blue and glow at dusk; `sky.frag` draws stars and the moon's disc. Not in the presets. `client.test.timeOfDay` (0 midnight, 0.5 noon) fixes the time in a smoke run; the shadow sun moves in 0.5 degree steps and stays at least 12 degrees up (moved every frame, shadows flickered); the water draws, unlit, take the same change in light as a lit face (`hdUnlitLight`), so water does not glow at night | `HdFeatures.dayNight`, `hdLightDirection`, `hdDayNight`/`hdMoon`, `sky.frag` |
| `caustics` | an animated, procedural web of light on the ground the game's underwater pass draws (the riverbed; `GlState.hdUnderwater` between `ra` and `pa`) | `hdCaustics`, `ff.frag` |
| `lightGrid` (on in High and Ultra) | room light: the lamps' light carried through their rooms and stopped by walls. When the scene is built or a loc changes (`HdToolkit.sceneBuilt`/`sceneChanged`, taken up on the next frame since the collision flags are set after the callback), every light of lights.json on a loc and every map light of the game (`Scene.aClass300Array1`) is flooded over the tile grid (Dijkstra, 8 neighbours, corners only where both ways round are open) up to twice its radius and at least 7 tiles (the game's own map lights are small, and one must still carry a soft fill to the far wall of a 6-tile room; at 6 tiles it is down to a seventh), through the edges the collision map leaves open to a projectile (the line-of-sight bits 0x400/0x1000/0x4000/0x10000, a tile opaque only at 0x20000: light goes where an arrow goes, so walls and doors stop it but counters, tables and fences do not; a bridge tile uses the level below). What arrives is accumulated per level, linear in the path distance, through a soft ceiling (the lamps' sum s becomes 1.2 × (1 − e^(−s/1.2)): one lamp is little changed, a hall of torches saturates gently instead of stacking to white), into a grid of two texels a tile (one texel a tile bled through walls under bilinear filtering), blurred once without crossing walls, and uploaded as a `GL_TEXTURE_2D_ARRAY` on unit 24 (`uHdLightGrid`, RGBA32F; alpha the tile's floor y, 60000 where a level has no ground there, read from BLOCKWALK still set). `hdLightFill` picks the level whose floor is the highest at or below the fragment (sampled a quarter tile out along the surface's normal, so a wall's two faces read their own sides) and adds the fill, non-directional, in `hdAddPointLights`, at `hdGridParams.x`: 0.25 at noon to 1 at night with the time of day, 0.6 without one; in linear colour the lit colour with the point lights and the fill added is capped at `HD_LIT_MAX`'s linear value, so the view's peak stays where the game's light alone could take it. The flood also records, per lights.json loc light, a 32×32-tile visibility patch (the path distance against the straight one, squared) on unit 30 (`uHdLightVisibility`, one layer per light, layer 0 all 1 for NPC, projectile, spot-animation and carried lights); `hdLightGrid[i]` carries each written light's layer and position, and `hdPointLights`/`hdPointHighlights` multiply the light's falloff by it, so a lamp's own light stays behind the walls round it. The game's own lights get a soft window to nothing at their radius (`hdGameLightWindow`) in place of the hard cut of their footprint box. The fill is steady (the lights' flicker is left to their direct light). Overlay 8 shows the fill per tile (red where no level's floor is found); `client.test.hdOverlayRaw=true` makes it the picked level, the fill ×2 and the floor height instead. Tuning constants at the top of `HdLightGrid`; `client.test.hdDebug` prints the seeds near the player, the build time by phase, a tile map of the player's room (every tile walked through the edges open to a projectile, with the lit ones, the lamps, and any lit open tile beyond a wall), and a census of every room of the level; `client.test.hdGridDump=<file>` writes the level-0 seeds, wall bits and fill as JSON, which `tools/light_grid_reference.py` checks against an independent one-bounce reference (ray-cast direct light plus one bounce): at Edgeville bank the fill correlates with it at 0.895 and lights every tile it lights | `scene/HdLightGrid`, `hdLightFill`, `hdLightVisibility`, `hdPointFalloff`, `hdGameLightWindow` |
| `grassDisplacement` | grass and plants (foliage with wind strength 0.7 and up) bend away from the player and up to seven NPCs within 10 tiles (`hdActors`) | `scene.vert`, `HdFeatures.findActors` |
| `environmentLighting` | the environment's `sun` colour and intensity and `ambient` tint the game's light 0 and ambient light (`hdEnvSun`/`hdEnvAmbient`); `sun.yaw` turns the shadow sun; every field eases between areas, the grading table crossfades | `HdFeatures`, `hdEnvDiffuse`/`hdEnvAmbientLight`, `HdPost.useGrading` |
| `softParticles` | particles fade into geometry (depth copy) | `scene/HdSoftParticles` |
| `interfaceScale` (2/3/4) | the window at a whole-number scale, 3D at native resolution | `gl/HdGl.windowScale` |
| `materials` | Phase 5 normal / ORM / emissive / height maps; the flowing, unlit materials (the fire cape and lava, below) | `scene/HdMaterialMaps`, `hd_material.glsl` |
| `lightingModel` (on in High and Ultra) | lit surfaces relit per pixel as a sum of terms: the sun on the material's mapped normal (bent around the smooth normal) with the shadow, a neutral ambient plus a hemisphere (sky light on upward faces, slightly tinted towards the sky; warm bounce on downward ones) scaled by the occlusion map, a Phong highlight sized by the roughness map; the point lights on the mapped normal (the difference from their light on the smooth normal) with their own highlights; emission; applied per channel as the model's light over its own light on the smooth normal in full sun (times 1.08, so the scene keeps its brightness), so where nothing differs the game's colour stays. Replaces the ratio shortcut (`hdApplyMaterial`, clamped 0.45-1.6) and the separate shadow darkening for those surfaces. the terms are 634HD's own | `hdLightModel`, `hdModelLight` (`hd_material.glsl`), `scene.frag`, `ff.frag` |
| `textureReplacements` (on in High and Ultra) | fresh textures: a game texture whose `materials.json` entry names an `albedo` draws at 256x256 with that art (its colour; the alpha is the game's own, made at its own size and scaled with back-projection, so cut-outs, effects and scrolling stay - or, for an albedo with alpha, its pattern at the game mask's mean and spread), with the `fresh/` material maps beside it; toggling remakes every world texture and the retained scene | `content/HdMaterialProvider.fresh`/`replaced`, `HdTextureCache` (the 256 size), `HdMaterials` (a third array, units 28 and 29), `HdMaterialMaps` (`fresh/`) |
| `particles` | GPU particles: embers over fires and torches, sparks over lava, one instanced draw; each emitter starts at its loc's flame - found once per loc type, shape and rotation in the loc's model (`HdModel.hdFlamePoint`: flame sprites, the game's particle-emitter triangles, faces drawn with the fire texture 110, bright orange faces, or for candles, torches and braziers the model's top), placed with the loc's rotation and wall offset; the light's fixed offset when none is found. The light moves to the flame too, and the embers are sized to it (`particles.vert`): spread across the flame's width, their count, rise, size and wander scaled by its height (a candle's about 60 units: few, small, low; a campfire's about 250: many), ovens capped to a few small sparks, fireplaces' embers kept within the opening | `scene/HdGpuParticles`, `particles.vert` |
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
  leaves untextured (Lumbridge's own region: 2 tiles; Varrock's: none - the cache textures nearly
  every floor), with an example tile for each, and the Inspect lists show the few untextured loc faces.
  Those few are authored: Lumbridge castle's untextured wall faces as stone (locs 45332, 45335,
  45336, 33877 by face colour), Varrock's boxes and beams as planks, Lumbridge's two untextured
  floor tiles (overlays 51 and 55) as cobbles - all small, so with worldTextures on the default
  scenes stay within their limits against toolkit 1.
  Material maps: the 11 most used textures of Lumbridge and Varrock that had none (`HDDEBUG textures
  used` in both areas: ground 90 and 309, stone 127, grass 108 and 185, sand 213, gravel 125, wood
  75, 387, 163 and 159) got normal, occlusion/roughness and height maps from
  `tools/make_material_maps.py` (which now keeps the entries it does not make, such as 634HD's own
  materials); 42 sets in all.
- **Fresh textures.** (The art was generated and fitted by tooling that is not in the repository;
  what it made is committed as each set's `albedo.png` and `fresh/` maps.) The 10 kept textures now
  use painterly-realistic art (a prompt asking for clean readable shapes with soft painted shading,
  no photographic speckle, a middle ground between painted and photographic), fitted with contrast 1.0-1.3 times the game's
  and no ground sharpening;
  their `fresh/` relief is softened (wider blur, half the strength), since full-strength relief from
  the new art lit as speckle. The photoreal art below stays as the fallback.
  The realistic mode (used for every texture with a photoreal generation): photoreal art from
  `/generate` (nano-banana-2 at 2K, a photoscan-style prompt; the reference is the game texture scaled up and blurred, so it sets only scale
  and layout and the art is not a repaint of the old pixels) is the texture itself, fitted to the
  game: grey with the game texture's mean hue (the face tints still colour it), its own broad light and
  dark flattened and the game texture's laid over it (90's darker bottom), the game texture's mean
  brightness, contrast between 1.6 and 2 times the game's (ground textures at 2, with their pebble-
  cluster scale lifted, since that is what survives the distance). Art that tiles by itself is used
  whole; art with a seam over 1.5 is cross-faded along each axis. Effect 1 textures (90, 91) get the
  art's pattern in alpha, which `HdMaterialProvider` scales to the game mask's mean and spread, so the
  wet shine follows the new stones. The `fresh/` material maps take their relief from the new art.
  Kept: the 10 that looked clearly sharper and more realistic in game, side by side with the original
  at the same camera with 16x anisotropic filtering - Varrock's street 505, its stone 70, 96, 309 and
  house walls 82, Lumbridge's dirt and paths 66, 91, 90, sand 154 and rocky ground 181. Where fine
  photo detail vanished at its size on screen the art was made again with a bolder prompt
  (bold mid-scale shapes: ripples, distinct stones); dropped: the textures under about 3% of every
  view tried (clothing, reeds, hidden faces), too small to judge. Nano-banana-2 seam repairs (the art shifted by half, the centre cross
  inpainted) worked twice in eleven tries, so they are not used. An earlier detail-only mode (the
  game texture with generated grain under one texel) still runs for any listed texture without
  realistic art.
- **The world's textures (in progress).** A catalogue (kept with the generation tooling, outside the
  repository) lists the most drawn surface textures across the world (a tour of 27 places with
  `HDDEBUG textures used`; effects, cut-outs, scrolling and blank textures and non-materials left
  out; textures that are another with a gradient over it share its art), each with a name, a material
  class and the texture whose art it takes; it is what adds `materials.json` entries for new ones,
  their art painterly on nano-banana-2-lite. Limits for it: 448 map sets (the uniform
  block's 16 KB), an emissive array only for the textures that glow (lava), 128 fresh-texture layers
  started afresh at a region build once three quarters full, and `compress_material_maps.py`
  compressing the `fresh/` sets too, only what changed.
- **Environments beyond Lumbridge and Varrock.** Draynor village (regions 12338, 12339: a light
  coastal haze, the sea's own tint; the swamp grade it had moved to Draynor Manor, 12340), Falador (the
  whole city, 11827-11829, 12083, 12084, which had only the park; 12083 was wrongly under Draynor) and
  Al Kharid (13104-13106, 13360-13362: a warm sun, a hazy sandy horizon, `desert.png` from
  `tools/make_luts.py`, the oasis's turquoise water). The three areas' light sources were already all
  in `lights.json` (`client.test.hdLocScan` lists a scene's locs, flagging those with a light).
- **634HD's own materials.** `materials.json` entries from texture 10000 up with an `albedo` file in
  `634hd/materials/<id>/` (`content/HdMaterialProvider` serves them beside the cache's, so every
  path draws them): near-grey detail the face or tile colour tints, made by
  `tools/make_detail_textures.py` (bark 10001, stone 10002, brick 10003, roof tiles 10004, planks
  10005, grass 10006, dirt 10007, cobbles 10008; procedural, no third-party art). Most of 634's
  models and floors are already textured, so overrides mostly replace (`replace: true`) or fill the
  few untextured faces (match them by `faceColours`). Model overrides map uvs by box projection in
  world space (`uv`, `uvScale` tiles a repeat); tile overrides take `scale` tiles a repeat.
- **Flowing, unlit materials (the fire cape, lava).** A `materials.json`
  entry with a `flowMap` (a map in the shared `634hd/materials/flow/`; the 128x128
  `lava_flow_map.png`, BSD 2-Clause, the licence beside it) has its uv moved by the map's red and
  green times `flowStrength`, the map itself sampled at the uv slid once per `flowDuration` seconds
  along u and v, after an extra `uvScroll` (uv per second) on top of the game's own scroll; `unlit`
  draws the texture alone - the face colour, the game's lights, the point lights and the fill, the
  lighting model, shadows and occlusion all passed over (only the emissive map is added, so lava keeps
  its glow), shown as the game shows it whatever the tonemap (`hdTonemapInverse`, as the water is:
  through AgX the lava went a washed peach); its values stay under 1, so it does not bloom of itself.
  The fire cape is texture 40: strength 0.05, durations 12 and 4 s, scroll (0, 0.1) - a scroll of
  1/3 set against OSRS's scroll rate and sign nearly cancels it to a crawl; here
  the game scrolls the cape 1 uv per 6.4 s the other way, so 0.1 leaves the same slow crawl in the
  game's own direction; lava (110, which the game does not scroll): 0.05, 36 and 22 s, (0, -0.05),
  with its maps. The table is a second uniform block (`HdMaterialFlow`,
  binding 2; the first sits just under GL's least block size) and the flow maps a fourth array (unit
  31, sRGB, read linearised, with mipmaps) in `HdMaterialMaps`; `hdFlowUv`,
  `hdUnlit` and `hdUnlitMaterial` in `hd_material.glsl`, hooked in `ff.frag` (models, the ground) and
  `scene.frag` (static locs) before the parallax, so the albedo and the maps agree. The scroll and the
  slide take the fraction of `hdTime.x` (seconds, wrapping hourly), so the uv keeps its precision; 22
  does not divide 3600, so the lava's v-phase pops once an hour, as the caustics do.
  Both light their surroundings: a worn fire cape is a carried flame light (`HdLights.findCarried`,
  tier 2, a deeper orange than a torch, flickering), and a floor tile whose texture's entry has a
  `light` colour (lava) is a seed of the room fill (`HdLightGrid.collectFloorSeeds`: 3 tiles' reach,
  0.3 bright, a seed a tile, so a lava field glows onto its banks and walls under the knee and a lone
  tile is a faint glow; these seeds come first, so a scene full of lava keeps its lamps within
  `MAX_SEEDS`).
- **Materials.** `tools/make_material_maps.py` builds map sets from the cache textures (dumped
  with `client.test.findText=textures:`) into `634hd/materials/<texture>/` with a `recipe.txt` each.
  Fine relief on 23 sets came from height maps made with `/generate` (nano-banana-2-lite, the cache
  texture as the reference; the prompt in `prompt.txt` beside each set; the generation tooling is not
  in the repository, and the script now remakes a set from the albedo alone): their high frequencies over the
  albedo's own shapes, normals then computed from that (plan rule 3), every height made to tile
  (checked: no edge seam left in any of the 23). It
  and writes `materials.json`. `./gradlew compressMaterialMaps` (`tools/compress_material_maps.py`)
  precompresses them at build time - BC5 normals, BC7 (mode 6) packed maps with the height in alpha,
  BC7 emissive maps, every mip level - and `HdMaterialMaps` uploads those blocks as they are (23 sets,
  5 MB; GPU decode within 1.8/255 of the PNGs for BC5, 3.3 for BC7, checked with
  `client.test.hdDebug`). The jar carries only the blocks and `albedo.png`: `jvmProcessResources`
  leaves out the other PNGs and the provenance files (`recipe.txt`, `prompt.txt`).
  Without BC7, or when the driver refuses to compress (`client.hd.uncompressedMaps`), the RGBA8 arrays
  are filled by decoding the blocks on the CPU (`decodeBc5`, `decodeBc7`: mode 6). A set in the data
  folder without blocks yet is read from its PNGs and compressed by the driver.
- **Debug panel** (`HdDebugPanel`, `client.debug` only; F10 or `::hdpanel`): stats with GPU time,
  every switch, overlays, live editing of the current environment and nearest light with Save to
  the JSON file, texture and mini-menu inspection, content warnings.
- **Drivers checked:** NVIDIA (RTX 5070, 3.3 core) and Mesa's GLSL compiler on llvmpipe with a
  forward-compatible 4.1 core context, as a Mac asks for (`__GLX_VENDOR_LIBRARY_NAME=mesa
  LIBGL_ALWAYS_SOFTWARE=1`, `-Pclient.test.hdGlVersion=4.1`): every shader compiles, every feature
  runs, and with them off the harness matches toolkit 1. Intel (Iris) on Linux uses the same Mesa
  compiler. Windows and macOS themselves, and Iris Xe and RTX 3060 speeds, are not measured.
- **Not done:** the ground in the water's reflection (banks reflect the sky), and
  blending across tile texture edges. Occlusion is baked into a height map per region, not into
  vertices. The game's own particle systems stay CPU-simulated; the GPU
  particles are an added, data-driven system.
