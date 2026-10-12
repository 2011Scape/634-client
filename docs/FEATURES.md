# Building a feature

How to add something new to the client, from the features built so far: tile markers (`client/features/TileMarkers`), experience drops (`XpDrops`), npc labels
(`NpcLabels`) and ground item labels (`GroundItemLabels`). Each is one class plus a few one-line hooks, behind a switch that is off by default.

## The rules a feature follows

- **It is ours, not Jagex's.** Every hook into the 2010 code carries a comment that starts "Not
  part of the original client", so a search for that phrase finds every feature's hooks.
- **Off by default.** A `public static boolean enabled = Boolean.getBoolean("client.<name>")`, a
  console command that flips it, and the property added to the list the run task passes on
  (`build.gradle.kts`, the `runClient` task).
- **The 2010 idiom, in Kotlin.** Tabs, and an explicit type on every declaration (`val count: Int = 0`,
  not inferred); plain arrays and `for`/`while` loops, above all in code that runs every frame; no
  collection pipelines (`map`, `filter`, sequences), no data classes, no extension-heavy DSLs.
- **On a branch.** Build it on `feature/<name>` and merge it once it works on all three toolkits.
- **Hands off the native bindings.** Never rename or move a native class, method or field the
  native libraries look up ([PACKAGES.md](PACKAGES.md#native-code-is-frozen) lists them), and leave
  the `@Original*` and `@Pc` annotations as they are.

## Where to hook in

| You want to | Hook | Example |
|---|---|---|
| Draw on the game view, after the scene | a call in `ClientState.drawGameArea`, after `OverlayManager.drawEntities`, given the view's `areaX`, `areaY`, `width`, `height` | `TileMarkers.draw`, `NpcLabels.draw`, `XpDrops.draw` |
| Add a mini-menu entry | `MiniMenu.addEntry(...)` where the menu is built (`ViewportMiniMenuPicking` for tiles and entities), with a click type of your own in `MiniMenuAction` (from 1000 up it sorts below the original entries) | `MiniMenuAction.TILE_MARKER` |
| Act on a menu click | a branch for your click type in `MiniMenuActionExecutor.processMenuClick`, before the original branches; `param0`/`param1` are what you passed to `addEntry` | `TileMarkers.toggle` |
| React to a server packet | a call in the packet's branch of `PacketDecoder.decode`, after its fields are read and before they are stored | `XpDrops.statUpdated` in `UPDATE_STAT` |
| Clear state at logout | a call in `ClientLogout.logout` | `XpDrops.reset` |
| Add a console command | a branch in `ClientCommandProcessor`, beside `tilemarkers` and `xpdrops` | `npclabels` |

The root package (`client`, `PacketDecoder`, `ClientCommandProcessor`, the executor and the
picking) can call a packaged feature class, and in Kotlin a packaged class can import a
root-package one too (`SmokeTest` calls `ClientCommandProcessor.executeCommand` directly). Passing
state in from the root-package hook, as the stat packet does for `XpDrops` with `statXp` and
`statLevels`, still keeps a feature independent of the root classes.

A feature is common code (`src/commonMain`): use the shims (`awt.Graphics`, `lang.getProperty`),
not `java.*`, and put anything that only the JVM can do behind an `expect` function in `jvmMain`,
as `FeaturePlatform` does for `Graphics2D` scaling.

## Drawing

- **Toolkit.** Draw through `ClientState.activeToolkit`; every toolkit implements the 2D calls.
  Lines: `line(x1, y1, x2, y2, argb, 1)`. Text: `Renderers.FONT_RENDERER.renderCentered(x, argb,
  text, shadowArgb, y)`. Colours are ARGB, so give an opaque one `0xFF` alpha.
- **World to screen.** `OverlayManager.projectToScreenWithOffset(level, heightOffset, 0, x, 0, z, 0)`
  for a point in world units (512 per tile, scene-relative: `(tile - MapRegion.sceneBaseX) * 512`),
  or `OverlayManager.projectEntityToScreen(entity.getOverheadHeight(), width >> 1, height >> 1,
  entity)` for the point above an entity. The result is in
  `OverlayManager.projectedScreenPosition` ([0] is -1 when the point is off screen), relative to
  the view, so add `areaX`/`areaY`.
- **What the interfaces cover.** The interfaces draw after the game view: the minimap, the orbs and
  the chat box sit on top of it. Keep drawing clear of them (the experience drops start left of
  the minimap), and clear of the debug overlay's lines at the top right.
- **Which npcs and players.** Walk them the way `OverlayManager.drawEntities` does: this cycle's
  npcs in `NPCList.NPC_UPDATE_INDICES` (looked up in `NPCList.NPC_MAP`), an npc type through
  `getMultiNpc` when it has `alternativeIds`, and only entities drawn this frame or on the local
  player's level.

## Trying it

Start the client with your switch on, `./gradlew runClient -Pclient.<name>=true`, against a Void
server on this computer, and use the feature on each toolkit: Java OpenGL, native OpenGL and
software, switched in the graphics options. The three share little code, and a drawing hook that
works on one can fail on another. Try it again after the map reloads (walk far, or teleport) and
after logging out and back in, which clear and rebuild most of the client's state.

## In the browser

The browser client ([MULTIPLATFORM.md](MULTIPLATFORM.md#running-the-browser-client)) starts with the
launcher's defaults (`LauncherDefaults`: 144 fps, zoom, the console, shift-drop, a scale from the
screen's height) except the render distance, which is always 1 there (a page has too little memory
and time per frame for a longer view; `client.renderDistance` is ignored with a console message), and takes the same switches from its page address,
`http://localhost:8080/?client.msaa=4&client.zoom=false` (a switch without a value is true,
`?client.smaa`), or from a hosted page's `window.CONFIG`. It has the Java software toolkit (0), its
browser version of the native software toolkit (2), and the Java OpenGL toolkit (1) on a WebGL 2
emulator; the native toolkits need libraries a browser cannot load. Every extra works there,
SMAA included (its shaders are translated to GLSL ES 3.00). A feature that draws should be tried on
toolkits 0 and 1 in the browser too, and the smoke test runs there:
`http://localhost:8080/?client.test=smoke&client.test.toolkit=1`.

## Where to read next

The area guides say where each part of the client lives and how it fits the main loop, and each
ends with the feature that used it: [RENDERING.md](RENDERING.md) (tile markers),
[NETWORK.md](NETWORK.md) (experience drops), [ENTITIES.md](ENTITIES.md) (npc labels),
[CONFIG.md](CONFIG.md) (ground item labels), and [CS2.md](CS2.md), [CACHE.md](CACHE.md), [CLIENT.md](CLIENT.md).
