# runetek5-634

[![Build][badge-ci]][ci]
[![Java 21 to 25][badge-java]][java]
[![Void compatible][badge-void]][void]

The RuneScape client from December 2010 (build 634), patched to play on [Void][void] servers. It
comes with a launcher that connects to any Void server, OpenGL on every desktop platform, and a set
of optional quality-of-life extras. It also runs in a web browser.

**[Download the latest release][releases]** (stable), or the [development build][dev] (what is on
`main` right now, rebuilt on every push; it may break).

## Contents

- [Quick start](#quick-start)
- [Features](#features)
- [Options](#options): [connecting](#connecting), [gameplay](#gameplay), [graphics](#graphics),
  [developer](#developer)
- [Platforms](#platforms)
- [Building from source](#building-from-source)
- [For developers](#for-developers)
- [Credits](#credits)

## Quick start

1. Install **Java 21 to 25** ([Temurin][java], or [Liberica][liberica] for a 32-bit Windows Java).
2. Download `runetek5-634-<version>.jar` from the [releases page][releases] (or the
   [development build][dev] for the newest, less tested code).
3. Double-click it, or start it from a terminal:

   ```bash
   java -jar runetek5-634-<version>.jar
   ```

It connects to [2011Scape](https://2011.rs) (`play.2011.rs`). For your own server, add
`--address localhost`; the [Void repository][void] says how to run one.

## Features

- **Any Void server**: choose the host, port and keys from the launcher.
- **OpenGL on Linux, Windows and macOS**, through [LWJGL](https://www.lwjgl.org).
- **Smooth and sharp**: up to 144 fps, a window scaled to your screen, and MSAA or SMAA.
- **Extras**: tile markers, experience drops, NPC and ground item labels, shift-drop, a longer
  render distance and a wider zoom.
- **Developer tools**: ids on right-click menus, coordinates on screen, a better console.
- **Plugins**: jars in `~/.634-client/plugins` that read the game, draw on it and add menu entries
  and commands, on a small stable API ([docs/PLUGINS.md](docs/PLUGINS.md)). A plugin runs with
  everything the client can do, so install only ones from people you trust.
- **In a browser**: the same game on WebGL 2 ([below](#the-browser-client)).

## Options

By default the launcher turns on 144 fps, twice the render distance, the wider zoom, the better
console, shift-drop and a scale that suits the screen. The other extras are off until you turn them
on; most can also be toggled in game with the command shown.

```bash
java -jar runetek5-634-<version>.jar --tile-markers --xp-drops --npc-labels
```

### Connecting

| Option | What it does |
|---|---|
| `--address <host>` | The server to connect to. The default is `play.2011.rs`; `localhost` is a server on this computer. |
| `--port <port>` | The server's port, if it isn't Void's 43594. |
| `--keys <file>` | Read the server's keys from its `game.properties` (Void keeps it in `game/src/main/resources`). |
| `--login-modulus <hex>`, `--js5-modulus <hex>` | Give the server's keys in hex instead. |
| `--require-keys` | Refuse to connect to a remote server that uses the standard Void keys, instead of warning. |
| `--server <void\|jagex>` | The server's protocol. `void` (the default) suits a Void server; `jagex` restores the 2010 client's login through the lobby and its original packet sizes and scripts. |
| `--packets <file>` | Override server packet sizes, one `<opcode>=<size>` per line (`-1` and `-2` for a size byte or short). |
| `--world <id>` | The world. The default is 1. |
| `--lang <english\|german>` | The language. The default is English. |

> [!WARNING]
> The client has the keys of a standard Void server built in, and those keys are public, private
> halves included. Anyone who can see the traffic to a server that keeps them can read players'
> passwords and change the game files the client downloads. A public server should generate its own
> keys, and players should pass them with `--keys`. The launcher warns when it connects to a remote
> server with the standard keys (not for `localhost`).

Developer console commands that read or write local files (`setoutput`, `runscript`, `heapdump`, …)
are off unless the client is started with `--dev-commands`.

### Gameplay

| Option | What it does | In game |
|---|---|---|
| `--tile-markers` | Right-click a tile and choose *Mark tile* to outline it; markers stay until unmarked. | `::tilemarkers` |
| `--xp-drops` | A "+N Skill" drop rises and fades near the minimap when you gain experience. | `::xpdrops` |
| `--idle-notifier` | A chat line and a red frame around the game view once you have been idle for 15 seconds. | `::idle`, `::idle <seconds>` |
| `--inventory-value` | What your backpack is worth, with its three most valuable items, at the top of the game view. | `::invvalue` |
| `--name-plates` | Other players' names over their heads: red when they can attack you, green for friends, blue for your friends chat with its name underneath. | `::nameplates` |
| | Make an option the left click of its menu, wherever it appears (a menu entry swapper). | `::swap <option> [target]`, `::unswap <option>`, `::swaps` |
| `--npc-labels` | NPC names, with their combat level, above their heads. | `::npclabels` |
| `--ground-items` | The names (and counts) of the items on each tile, above it. | `::grounditems` |
| `--no-shift-drop` | Turn off dropping an item with Shift and a left click. | `::shiftdrop` |
| `--no-zoom` | Keep the mouse wheel to the original zoom range. | |
| `--render-distance <x>` | Draw the world x times further away. The default is 2; `1` is the original distance. | |

### Graphics

| Option | What it does | In game |
|---|---|---|
| `--fps <n>` | Draw up to n frames a second (default 144). The game keeps its speed; the camera, players, NPCs and animations move smoothly between game cycles. | |
| `--scale <x>` | Draw the game x times larger. By default it follows the screen: 1.5 from 1080p, 2 at 4K, otherwise 1, measured as the desktop sizes it, so a display your system already enlarges isn't scaled twice. `--scale 1` keeps the original size. Works in every mode except native OpenGL, which keeps the game at its own size. | |
| `--msaa <n>` | OpenGL: anti-alias the 3D view with n samples (0, 2, 4 or 8) in a framebuffer of its own, so the interface stays sharp. Cut-out textures such as leaves get alpha-to-coverage (`--no-a2c` turns that off). | `::msaa n` |
| `--smaa` | OpenGL: smooth the 3D view's edges with SMAA 1x, with or without MSAA. | `::smaa` |
| `--render-scale <x>` | OpenGL: draw the 3D view 1.5 or 2 times larger and shrink it (supersampling). | `::renderscale x` |
| `--toolkit <name>` | Start on a graphics mode: `hd` (634HD, needs OpenGL 3.3), `opengl`, `java` (Safe Mode). Graphics Options has a 634HD page too, from the 634HD button in its preset row, with the switch and every 634HD setting. | `::tk6` |

### Developer

| Option | What it does |
|---|---|
| `--debug` | Show ids and tile coordinates on right-click menus, and your coordinates under `::displayfps`. |
| `--no-console` | Turn off the console's command history, word editing and tab completion. |
| `--dev` | `--debug` with the wider zoom and the better console. |
| `--dev-commands` | Allow the console commands that read or write local files. |
| `--plugins <dir>` | Load the plugin jars in `dir` instead of `~/.634-client/plugins` ([docs/PLUGINS.md](docs/PLUGINS.md)). `--no-plugins` loads none. |

## Platforms

The game has four graphics modes, chosen in its graphics options, and 634HD, chosen on its page of
Graphics Options (the 634HD button in the preset row), `--toolkit hd` or `::tk6`.

| Platform | Java | OpenGL | Native OpenGL | DirectX | 634HD |
|---|:-:|:-:|:-:|:-:|:-:|
| Linux (x64) | ✓ | ✓ | ✓ | | ✓ |
| Linux (arm64) | ✓ | ✓ | | | ✓ |
| Windows (64-bit Java) | ✓ | ✓ | ✓ | | ✓ |
| Windows (32-bit Java) | ✓ | ✓ | ✓ | ✓ | ✓ |
| macOS (Intel) | ✓ | ✓ | ✓ | | ✓ |
| macOS (Apple Silicon) | ✓ | ✓ | | | ✓ |

634HD needs a driver with OpenGL 3.3 (macOS gives 4.1) and no Jagex library; it is tested on Linux
x64 only so far. If it cannot start, the game switches to OpenGL (then Safe Mode) and says why in
the chat box.

DirectX needs a 32-bit Java ([Liberica][liberica] publishes one for Java 21). Native OpenGL and
DirectX use the game's own 2010 libraries, which exist only for x86 and x86_64.

## Building from source

```bash
git clone https://github.com/2011Scape/634-client.git
cd 634-client
./gradlew run                                    # the launcher, connecting to play.2011.rs
./gradlew run --args="--address localhost"       # options go in --args
./gradlew release                                # the release jar, the plugin API jar and browser zip, in build/release
./gradlew installDist                            # a folder with start scripts, in build/install
```

On Windows, use `gradlew.bat` instead of `./gradlew`. A folder from `installDist` starts with
`bin/runetek5-634` (`bin\runetek5-634.bat` on Windows); copy the whole folder to share it.

<details>
<summary><b>In an IDE</b></summary>

Open the folder as a Gradle project with a Java 21 SDK. Then either run the Gradle task
`application → run`, or make an *Application* run configuration with main class `RuneTek5`, the
`runetek5-634.main` classpath, the options as program arguments, and these VM options:

```
--add-opens java.base/java.lang=ALL-UNNAMED --enable-native-access=ALL-UNNAMED -Xmx512m
```

</details>

## For developers

The client is Kotlin, as a Kotlin Multiplatform project with a JVM and a browser target. It is a
straight conversion of the deobfuscated Java, which keeps the original's structure, names and
annotations. Code added to the 2010 client is marked with a comment that starts "Not part of the
original client".

| Source set | What's in it |
|---|---|
| `src/commonMain/kotlin` | The game, on shims of the Java APIs it uses. |
| `src/jvmMain/kotlin` | The desktop side: the native classes, OpenGL on LWJGL, and the launcher `RuneTek5`. |
| `src/jsMain/kotlin` | The browser side: WebGL 2, WebSockets and an IndexedDB cache. |

**Guides**, in `docs/`:

| Guide | Covers |
|---|---|
| [FEATURES.md](docs/FEATURES.md) | Adding a feature: where it hooks in, how it draws, how to try it. Start here. |
| [PACKAGES.md](docs/PACKAGES.md) | Where the code is, and the native bindings that must not change. |
| [RENDERING.md](docs/RENDERING.md) | The graphics modes, the LWJGL binding, and how a frame is drawn. |
| [CLIENT.md](docs/CLIENT.md) | The main loop, map building, scene and sound. |
| [NETWORK.md](docs/NETWORK.md) | Server and client packets. |
| [ENTITIES.md](docs/ENTITIES.md) | Players, NPCs and update masks. |
| [CONFIG.md](docs/CONFIG.md) | Items, NPCs, locations and the other config types. |
| [CACHE.md](docs/CACHE.md) | The disk cache and JS5. |
| [CS2.md](docs/CS2.md) | Interfaces and client scripts. |
| [PLUGINS.md](docs/PLUGINS.md) | Plugins: installing them, writing one, the API, and how the runtime hooks into the client. |

**Trying and checking a change**

- `./gradlew runClient` starts the client itself against a Void server on this computer, with
  `-Pclient.*` switches such as `-Pclient.debug=true`.
- `./gradlew check` is what CI runs, with no server needed. It loads and verifies every class from
  the release jar, and fails on a method over HotSpot's 8000-byte JIT limit unless
  `config/huge-methods.txt` lists it.
- `./gradlew build` also builds the browser target, which fails if common code uses a JVM API.
- The in-client smoke test (`-Pclient.test=smoke`, see `SmokeTest`) logs in, checks the frame and
  the extras, and prints `SMOKE PASS` or `SMOKE FAIL`. It needs a Void server with a `smoketest`
  account.
- Dependencies are verified against `gradle/verification-metadata.xml`. After changing one, run
  `./gradlew --write-verification-metadata sha256 build release` and review the diff.
- Two release channels (`.github/workflows/release.yml`): a tag `vX.Y.Z` on `main` publishes a
  **stable** release of that name, the newest of which is GitHub's "latest"; every push to `main`
  rebuilds the one **development** pre-release, tagged `dev` and versioned `dev-<date>-<commit>`,
  with the merges since the last stable tag as its notes. Both carry the jar, the browser zip and
  `SHA256SUMS`.

### The browser client

The same game runs in a browser through a WebSocket relay. A release includes the browser bundle
as a zip beside the jar.

<details>
<summary><b>Building, serving and testing it</b></summary>

1. Run a Void server on this computer, as for the desktop smoke test.
2. Start the relay, from WebSockets to the server's TCP port: `node tools/relay.mjs` (no
   dependencies; arguments `[listenPort=8081] [serverHost=localhost] [serverPort=43594]`), or
   `websockify 8081 localhost:43594`. The client connects to `ws://localhost:8081/proxy`, or to
   `window.CONFIG.url` when the page sets it. The relay listens on 127.0.0.1 only (`HOST=0.0.0.0`
   for other machines) and takes WebSockets only from pages served by this computer;
   `ORIGINS=https://example.org` allows a hosted page's origin as well.
3. Build with `./gradlew jsBrowserDevelopmentWebpack`, serve with `node tools/serve.mjs` (no
   dependencies; arguments `[port=8080] [bundleDir] [referenceDir=~/634-smoke-ref/browser]
   [shotDir=build/smoke-browser]`), and open `http://localhost:8080/`. It never lets the browser
   cache, so a rebuilt bundle is picked up on the next reload, and it listens on 127.0.0.1 only
   (`HOST=0.0.0.0` to serve other machines). `./gradlew build` makes the minified bundle in
   `build/dist/js/productionExecutable`, and `./gradlew release` zips it beside the jar.
4. The first visit downloads the cache into IndexedDB; later visits load it from there. The client
   starts as the launcher does, but always at render distance 1, on the graphics mode the game's
   settings name. The tutorial hides the chat box, so commands go through the developer console
   (the `` ` `` key): `tk1` switches to OpenGL on WebGL 2, `tk2` to the software renderer's browser
   version, and `tele 3222 3218` takes an admin player to Lumbridge (so does ctrl-clicking the
   minimap). Switches go in the address (`?client.msaa=4`) or a hosted page's `window.CONFIG`. A
   hosted page ignores the development switches in its address (`client.test*`,
   `client.devCommands`). `window.__log` holds the last 500 console lines. Leaving the page closes
   the client's connections, so the server logs the player out at once.
5. For the smoke test, open `http://localhost:8080/?client.test=smoke&client.test.toolkit=1`. It
   logs in as `smoketest`, runs the desktop test's checks without the launcher's defaults, compares
   its frames with `~/634-smoke-ref/browser` (served at `/smoke-ref/`), uploads them to
   `build/smoke-browser`, and reports SMOKE PASS or FAIL on the console, in `window.__log`, in
   `#smoke-result` and in the page title. It and the desktop smoke test both log in as `smoketest`,
   so run one at a time.

</details>

## Credits

- [Void][void] and [GregHib/void-client][void-client], for the server, and for the client patches
  and extras this client builds on.
- [OpenRS2][openrs2], for the deobfuscator and the [archive][archive] of the original client.
- [RuneWiki][runewiki], for deobfuscating build 634.
- [StrongHold/runescape-667][667], [LostCityRS/RS910][rs910], [Pazaz/RT4-Client][rt4] and
  [Pazaz/RT5-Client][rt5], for the names given to the code.
- [FabricMC Matcher][matcher], for matching the code between builds.
- [LWJGL](https://www.lwjgl.org) and [lwjgl3-awt](https://github.com/LWJGLX/lwjgl3-awt), for
  OpenGL on the desktop.
- [SMAA](https://github.com/iryoku/smaa) by Jorge Jimenez, Jose I. Echevarria, Belen Masia, Fernando
  Navarro and Diego Gutierrez, for the SMAA shader and lookup textures (MIT licence, in
  `src/jvmMain/resources/smaa/`).
- [117HD](https://github.com/117HD/RLHD), for the lava flow map behind the fire cape and the lava
  and the water's normal, flow and foam maps (BSD 2-Clause licence, in
  `src/jvmMain/resources/634hd/materials/flow/`), for the per-area fog and ground fog of its
  `environments.json` (the entries of `src/jvmMain/resources/634hd/data/environments.json` marked
  `source`, BSD 2-Clause licence), and as the reference for
  its flowing, unlit materials, its water (`utils/water.glsl`, its water types) and its fog
  (`utils/fog.glsl`, its ground fog and fogged sky).

## Disclaimer

This project is not affiliated with or endorsed by Jagex Ltd. RuneScape is a trademark of
Jagex Ltd.

[badge-ci]: https://github.com/2011Scape/634-client/actions/workflows/build.yml/badge.svg
[badge-java]: https://img.shields.io/badge/Java-21_to_25-ED8B00?logo=openjdk&logoColor=white
[badge-void]: https://img.shields.io/badge/Void-compatible-2EA44F
[ci]: https://github.com/2011Scape/634-client/actions/workflows/build.yml
[releases]: https://github.com/2011Scape/634-client/releases/latest
[dev]: https://github.com/2011Scape/634-client/releases/tag/dev
[java]: https://adoptium.net/temurin/releases/?version=21
[liberica]: https://bell-sw.com/pages/downloads/#jdk-21-lts
[void]: https://github.com/GregHib/void
[void-client]: https://github.com/GregHib/void-client
[openrs2]: https://github.com/openrs2/openrs2
[archive]: https://archive.openrs2.org/clients
[runewiki]: https://github.com/RuneWiki/openrs2-nonfree
[667]: https://github.com/StrongHold/runescape-667
[rt4]: https://github.com/Pazaz/RT4-Client
[rt5]: https://github.com/Pazaz/RT5-Client
[rs910]: https://github.com/LostCityRS/RS910
[matcher]: https://github.com/FabricMC/Matcher
