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
| Toolkit ids | `graphics/ToolkitType` | `JAVA` = 0, `GL` = 1, `SSE` = 2, `D3D` = 3, `GLX` = 5 |
| Java | `JavaToolkit` (root package) | Pure Java software renderer. Always available; the fallback |
| SSE | `oa` | Native software renderer in the `sw3d` library |
| GL | `graphics/gl/GlToolkit` | OpenGL driven from Java through `jaggl.OpenGL` |
| GLX | `toolkit/GlxToolkit` | OpenGL in the native `hw3d` library. Shares `toolkit/AbstractNativeToolkit` with D3D |
| D3D | `toolkit/D3DToolkit` | Direct3D through `jagdx`. Windows only |
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
