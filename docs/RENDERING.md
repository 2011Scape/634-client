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
  toolkits 0 to 5 only).
- **Checks:**
  - the scene harness against toolkit 1's references (`-Pclient.test.toolkit=6
    -Pclient.test.scenes=default -Pclient.test.sceneReferenceToolkit=1`): every scene, the reload, the
    relog and a loc change within the noise of toolkit 1 against itself;
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
| `ambientOcclusion` | sky visibility from a top-down occlusion height map | `scene/HdShadows`, `hdAmbientOcclusion` |
| `fog`, `fade`, `sky` | environment fog, dithered fade at the edge, gradient sky and height fog | `scene/HdFeatures`, `hdFog`, `sky.frag` |
| `water` | wave normals, Fresnel sky reflection, glint, tint | `hdWater`; `GlState.hdWater` |
| `terrainBlend` | triplanar on slopes, anti-tiling blend, broad variation on the ground | `hdTerrainTexture`; `GlState.hdGround` |
| `foliage` | wind sway from `foliage.json`; alpha to coverage with MSAA | `scene.vert`, `scene/HdSceneRenderer` |
| `softParticles` | particles fade into geometry (depth copy) | `scene/HdSoftParticles` |
| `interfaceScale` (2/3/4) | the window at a whole-number scale, 3D at native resolution | `gl/HdGl.windowScale` |
| `materials` | Phase 5 normal / ORM / emissive / height maps | `scene/HdMaterialMaps`, `hd_material.glsl` |

- **Frame data.** `scene/HdFeatures` works out per-frame values once the camera is set and writes
  the `HdFrame` uniform block (`scene/HdFrameUniforms`, std140, binding 0). `hd_lib.glsl` holds the
  block and the shared functions; `hd_material.glsl` the fragment-only ones. `HdShaders.link` points
  every program's samplers at their units: shadow map 16, height map 17, soft-particle depth 18,
  material arrays 19-21 (block `HdMaterialMaps`, binding 1).
- **Shadow casters** are the retained objects drawn this frame plus every emulated indexed draw
  `HdGl` records between the camera and `opaqueEntitiesDone`. Translucent entities (drawn later)
  cast into the next frame's map. Moving models drawn before the map is made do not receive shadows.
- **Content** (`hd/content/HdContent`): `src/jvmMain/resources/634hd/data/*.json`, each with a
  `*.schema.json` checked at load (`JsonSchema`; a bad entry is skipped with its file and line). With
  `client.debug` from the repository (or `client.hd.dataDir`) the folder is read directly and
  reloaded when a file changes. `client.test.findText=locs:<regex>`, `locmodels:<regex>`, `npcs:` and
  `textures:` list or dump cache data for writing it; `client.test.hdDebug` prints loc ids near the
  player and texture use.
- **Materials.** `tools/make_material_maps.py` derives map sets from the cache textures (dumped
  with `client.test.findText=textures:`) into `634hd/materials/<texture>/` with a `recipe.txt` each,
  and writes `materials.json`. Maps are uncompressed RGBA8 (23 sets, 22 MB).
- **Debug panel** (`HdDebugPanel`, `client.debug` only; F10 or `::hdpanel`): stats with GPU time,
  every switch, overlays, live editing of the current environment and nearest light with Save to
  the JSON file, texture and mini-menu inspection, content warnings.
- **Not done:** GPU-simulated particles (particles stay CPU-simulated, drawn in one batch),
  `smoothInterface`, BC5/BC7 compression of the maps, foam on water, blending across tile texture
  edges, and per-vertex baked AO (occlusion is computed per pixel from the height map instead).
