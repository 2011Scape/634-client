# Client shell and main loop

How the client starts, runs its cycle, moves between loading, login, lobby and game, and builds the
map. Scenes, ground and sound are covered briefly at the end. Most entry points are in the root
package, so each part is listed with its class.

Where its state lives: the client-wide statics (`mainLogicStep`, `clientCycle`, `playerModLevel`,
the active toolkit) are in `client/ClientState`. The shell's timing is in `GameShell`
(`pulseIntervalNanos`), the startup stages in `LoadingStage`, the scene's tiles in `Scene`, and the map
base in `MapRegion` (`sceneBaseX`, `sceneBaseY`).

## Where it lives

| Part | Class | What it does |
|---|---|---|
| The shell | `com/jagex/game/runetek5/client/GameShell` | The applet, its frame (`startApplication`, `startApplet`), the canvas (`addcanvas`), and the main loop in `run` |
| The client | `client` (root package) | `main` (six arguments: worlds, mode, language, game), `maininit`, `onPulse`, `mainRedraw` |
| The state | `core/constants/MainLogicStep`, `ClientState.mainLogicStep` | The client's current step, from `STEP_STARTUP` to `STEP_ERROR` |
| Changing step | `MainLogicStepTransition.setMainLogicStep(step)` | Opens the login or lobby screen, starts a login, switches music and archive load types |
| Loading | `LoadingStageTick.cycle`, `LoadingStageWork.processLoadingStage`, `LoadingStage`, `StartupDependencyLoading.loadDependencies` | The startup stages, from opening the cache to the login screen |
| The game tick | `GameTick.processGameTick` | Packets, input, interfaces, hooks, the mini menu, the camera and sound, once per cycle |
| Map building | `MapBuildStart.performUpdate`, `MapBuildStep.loadRegion`, `SceneInitializer.initializeScene` | Moves the scene base and builds the new region |
| Region packets | `StaticRegionInit.initializeRegion`, `ConstructedMapInit.initializeConstructedMap` | Read `REGION` and `DYNAMIC_MAP_REGION` and start a map build |
| The scene | `scene/Scene`, `scene/tile/Tile`, `scene/ground/Ground`, `MapRegion` (a `Terrain`) | Tiles per level, the ground mesh, and the map decoders |
| Sound | `sound/` (`PcmPlayer`, `MidiPcmStream`, `PcmStreamMixer`, `VorbisPcmStream`, `AmbientSound`, `SongPlayer`) | Music, jingles, sound effects and area sounds |
| Window modes | `WindowModeRequest.requestWindowMode`, `WindowModeApply.applyWindowMode`, `client/window/WindowModeType` | Fixed, resizable and fullscreen |

## How the client starts

`client.main` reads the world, mode, language and game, creates the `client`, and calls `startApplication`. That opens a `Frame` and starts the shell's thread. As an applet, `startApplet` does the same inside the page.

`GameShell.run` then:
1. checks the Java version, and raises the `wrongjava` error screen for a version that is too old;
2. adds the canvas (`addcanvas`) and calls `maininit`, which sets the ports, the key and mouse recorders, and opens the disk cache;
3. runs the main loop until the client stops. Any uncaught exception is reported and shows the `crash` screen.

## Each cycle

The loop asks the cycle manager how many logic cycles are due. A logic cycle is 20 ms, so there are 50 per second (`GameShell.pulseIntervalNanos`). It calls `onPulseWrapper` once for each due cycle, then `mainRedrawWrapper` once. A slow machine therefore skips frames, not logic. `ClientFeatures.FPS` (`client.fps`) paces frames on its own and keeps the tick rate.

`onPulse` calls `pulse`, which:
1. advances `ClientState.clientCycle`, pumps the JS5 worker (`pulseFileRequester`, `RequestManager.pulse`) and the video and vorbis streams (`VideoStreamTick.processVorbisStreams`);
2. collects the key and mouse events recorded since the last cycle, and runs the console when it is open;
3. runs the work for the current step:
   - loading: `LoadingStageTick.cycle`;
   - a map build step: `MapBuildStep.loadRegion`;
   - login screen and lobby: `processLobbyTick` and the login steps (`Static701.method5595`);
   - in game: `GameTick.processGameTick`;
   - reconnecting: the login steps, with a logout if the reconnect fails;
4. renders queued item sprites (`ItemSpriteBatchRender.renderItems`).

`mainRedraw` calls `redraw`. It handles a window resize, then draws for the step: the loading screen (`LoadingScreenRepaint`), the login and lobby frame (`MenuScreenFrameRender.drawInterfaceFrame`), the "loading" box during a map build, or the game (`GameScreenFrameRender.drawGameFrame`). Then it flips the frame. On the OpenGL toolkit, an exception in `pulse` or `redraw` is reported, and the client falls back to the software renderer.

## Steps

`MainLogicStep` lists the steps in order: startup, two loading steps, login screen, lobby and game. Each of those three has a `*_MAP_BUILD` step, and there are steps for each login path, plus `STEP_RECONNECTING` and `STEP_ERROR`. Always change step through `setMainLogicStep`. It opens the screen for the step, starts the matching login, plays the login music outside the game, and keeps the config archives loaded during a map build.

## Loading

`LoadingStage` holds one startup stage: its text and the percentages the bar shows. `LoadingStage.getValues` lists the 21 stages in order. `LoadingStageTick.cycle` starts the loading screen thread and calls `processLoadingStage`. That method runs the branch for the current stage: it returns a percentage while the stage waits, or advances to the next stage. In order, the stages:
- wait for memory to settle, then fetch and check the JS5 master index;
- load the loading screens and fonts;
- open every archive (`Resource.createProvider`) and wait for their indexes;
- start sound (`SoundManager.initializeAudio`) and move to `STEP_LOADING_2`;
- load the native libraries and cached archives (`StartupDependencyLoading`);
- create the config type lists (`docs/CONFIG.md`), the world map and the client variables;
- load the login interface, pick the toolkit and the window mode, and move to the login screen.

## The game tick

`processGameTick` runs every cycle in game, in this order:
1. Decodes up to 100 packets through `GuardedPacketDecode.decodePacketFromStream` (`docs/NETWORK.md`).
2. Queues the client's reports: mouse movement and clicks, keys, the camera angle, focus and preferences.
3. Moves the camera for its mode (`Camera.cameraCalculationType`), and rebuilds the map if the level changed (`LevelChangeRebuild.checkLevelChange`).
4. Plays area sounds (`AudioRenderer.updateAreaSounds`), and handles a lost connection after 750 idle cycles.
5. Moves players and NPCs (`EntityTick.movePlayers`), and handles var changes (`MainLogicManager.onVarChange`).
6. Applies the queued interface pulse events, then the interface input (`InterfaceInputTick.processRootInterfaceInput`).
7. Runs the hooks (`docs/CS2.md`), then dragging (`ComponentDragTick.processDrag`) and the mini menu (`MiniMenuInputTick.processMenuInput`).
8. Updates camera shake and drift, queues a ping when idle, and flushes the outgoing packets.

## Map building

`REGION` calls `StaticRegionInit.initializeRegion`, and `DYNAMIC_MAP_REGION` calls `ConstructedMapInit.initializeConstructedMap`. Each one reads the regions and their XTEA keys, finds the map groups (`m`, `l`, `um`, `ul`), and calls `MapBuildStart.performUpdate`. That method sets the map build step and moves the scene base. Then it shifts the players, NPCs, hint arrows, ground items and camera into the new base.

`MapBuildStep.loadRegion` runs each cycle until every map and location file is loaded. Then it:
- calls `SceneInitializer.initializeScene` to allocate the tile grids, the ground and the render workers;
- decodes the tiles into a `MapRegion`, and builds the floor (`initializeFloor`, `calculateUnderlayPalette`, `calculateVisibility`);
- adds the locations;
- returns to the login screen, the lobby or the game. In game, it tells the server the build is done.

## Scene and ground, briefly

The scene is a grid of `Tile`s per level (`Scene.aClass100ArrayArrayArray1`). A tile holds its walls, wall decorations, ground decoration, ground items, interactive locations and a `bridge` link. `Ground` is one level's mesh. Each toolkit has its own: `JavaGround`, `GlGround` or `GpuFloorModel`. It gives `getHeight` and `averageHeight`, and draws with `renderTiles` and `renderFloor`. `Scene.destroy` frees all of it.

## Sound, briefly

Two `PcmPlayer`s run on `PcmPlayerThread`, which cycles both every 10 ms. One plays music and jingles (`SoundEffectPlayer.jinglePlayer`). The other plays game sounds through the `PcmStreamMixer` in `AmbientSound.gameSounds`. Music is a `MidiPcmStream` from `Resource.MUSIC`; `SongPlayer` keeps its archive and channel volumes. Vorbis sounds and video audio are `VorbisPcmStream`s. `AmbientSound` is a sound attached to a location, NPC or player, and `SoundManager.updateAmbientSound` updates its volume by distance.

## Window modes

`WindowModeType` is `FIXED`, `RESIZABLE` or `FULLSCREEN`. `WindowModeRequest.requestWindowMode(mode, width, replaceCanvas, height)` decides whether the canvas must be replaced, and calls `WindowModeApply.applyWindowMode`. That method:
- opens or closes the fullscreen frame;
- measures the window and sizes and places the canvas;
- resizes the interfaces;
- tells the server the new mode when in game.

`redraw` requests the mode again shortly after the window is resized, and leaves fullscreen when the client loses focus.

## Adding work to the cycle

1. Pick the step it belongs to. Use `MainLogicManager.isInGameStep` or `MainLogicManager.inLobby` rather than comparing single steps.
2. For game work, add it to `GameTick.processGameTick` at the point that matches its inputs. Put it after packet decoding to see this cycle's server state, and before the flush if it sends a packet. Drawing goes in `redraw` or `drawGameFrame`, never in the tick.
3. To change step, call `MainLogicStepTransition.setMainLogicStep`. To rebuild the map, call `MapBuildStart.performUpdate` or `MapRebuild.rebuildMap`.
4. Start the client (`./gradlew runClient`) and log in on each toolkit: Java OpenGL, native OpenGL
   and software, switched in the graphics options. Logging in goes through loading, the login screen, a map build and the game tick.
