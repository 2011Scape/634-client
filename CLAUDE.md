# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

The deobfuscated RuneScape client from December 2010 (build 634, RuneTek 5), patched to play on
[Void](https://github.com/GregHib/void) servers, plus a launcher (`RuneTek5`) and optional extras.
It is a Kotlin Multiplatform project (one Gradle module) with two targets: the JVM (desktop, Java 21
toolchain, `mise.toml` pins Java 21; runs on Java 21–25 only, because the client is an `Applet`) and
the browser (Kotlin/JS, WebGL 2, a WebSocket relay to the game server, an IndexedDB cache).

`docs/` holds the subsystem guides — read the relevant one before changing an area:
`FEATURES.md` (how to add a feature, the extras in the browser), `PACKAGES.md` (source sets, code
layout, frozen native bindings), `CLIENT.md` (main loop, map build), `NETWORK.md`, `ENTITIES.md`,
`CONFIG.md`, `CACHE.md`, `CS2.md`, `RENDERING.md`. The README says how to run the browser client.

## Commands

```bash
./gradlew build                            # both targets, the browser bundle, checkClasses (what CI runs)
./gradlew run --args="--address localhost" # launcher (RuneTek5); default server play.2011.rs
./gradlew runClient -Pclient.debug=true    # client main directly, against a local Void server
./gradlew release                          # fat jar + browser zip -> build/release/ (-Pversion=x.y.z)
./gradlew jsBrowserDevelopmentWebpack      # browser dev bundle -> build/kotlin-webpack/js/developmentExecutable
node tools/relay.mjs                       # WebSocket relay ws://localhost:8081 -> localhost:43594
node tools/serve.mjs                       # serves the dev bundle on :8080 (and the browser smoke refs)
```

There are no unit tests and no linter. Beyond `checkClasses` (every class loads and verifies from
the release jar; no method over the JIT limit unless `config/huge-methods.txt` lists it), the check
is the in-client smoke test (`client/features/SmokeTest`), which needs a Void server running locally
with a `smoketest` / `smoketest1` account:

```bash
./gradlew runClient -Pclient.test=smoke [-Pclient.test.toolkit=5] [-Pclient.test.shot=out.png] \
  [-Pclient.test.reference=<dir>] [-Pclient.test.command="gfx level_up"]
# in the browser (relay and serve.mjs running), in an 800x600 page (the refs' size):
#   http://localhost:8080/?client.test=smoke&client.test.toolkit=1
# frame-time benchmark instead of the checks (FEATURES.md); in the browser measure the production
# bundle: node tools/serve.mjs 8080 build/dist/js/productionExecutable
./gradlew runClient -Pclient.test=smoke -Pclient.test.bench=300 [-Pclient.test.benchMode=resizable|fixed]
```

It logs in, renders N frames, runs the extras' checks, checks the frame is not blank (and close to a
reference PNG if given), prints `SMOKE PASS`/`SMOKE FAIL` and exits 0/1 (in the browser it stops the
client and reports in `#smoke-result` and the page title). Toolkit ids: 0 Java, 1 Java OpenGL, 2
native software, 5 native OpenGL; the browser has 0, 1 and 2. See the class doc for all
`client.test.*` properties. Desktop and browser smoke tests both log in as `smoketest`: one at a time.

### Two entry points, two sets of defaults

- `RuneTek5` (launcher, `run`, release jar) and the browser page: turn options into `client.*`
  system properties (`--flags` on the desktop; the URL query, `?client.msaa=4`, or `window.CONFIG` in
  the browser), then call `client.main`. Both apply `LauncherDefaults` (144 fps, 2× render distance,
  zoom, console, shift-drop, screen-based scale) for whatever was not set — except that the browser
  always draws at render distance 1 (`main.js.kt` overrides any `client.renderDistance`) and
  defaults to `client.fps=60`, drawn on `requestAnimationFrame` (`BackgroundTimer.js.kt`).
- `client` (`runClient`, and the browser smoke test): the original six-arg `main`
  (`3594 3594 0 live english game0`), local mode. **Every extra is off** unless passed as
  `-Pclient.<name>=…` (or in the URL).
- `runClient` only forwards properties listed by name in `build.gradle.kts`. A new `client.*`
  property must be added to that list or `-P` silently does nothing.

### Native libraries on Linux

`run`/`runClient` prepend `~/.jagex_cache_32/runescape` (override `-Pclient.nativeDir=<dir>`) to
`LD_LIBRARY_PATH`, because `libhw3d.so` links `libjaclib.so` by name. Without it native OpenGL fails
and the client silently falls back to the software toolkit (looks like safe mode). In an IDE, also
pass `--add-opens java.base/java.lang=ALL-UNNAMED --enable-native-access=ALL-UNNAMED -Xmx256m`.

## Architecture

- **Source sets**: `src/commonMain/kotlin` is the game and everything not tied to a platform, written
  against shims of the Java APIs it uses (`awt`, `io`, `lang`, `net`, `util`, `sound`) and root
  platform seams (`PlatformLoop.kt`: `runLoop`, `canBlock`, `clientArraysAvailable`, …).
  `src/jvmMain/kotlin` has the JVM actuals, the native classes and `RuneTek5`. `src/jsMain/kotlin`
  has the browser actuals: `main.js.kt`, the WebSocket `net.Socket`, the IndexedDB file system, the
  class registry behind `lang.forName`, `jaggl` as a WebGL 2 emulator, and throwing stand-ins for
  natives a browser cannot have. `webpack.config.d/` fills Kotlin/JS arrays with null (PACKAGES.md).
- **Root package** (~165 files): `client` (main, `maininit`, `onPulse`, `mainRedraw`), the packet
  decoder, command processor, mini-menu executor/picking, software renderer, loading screens, and
  every class that touches a native class.
- **`com/jagex/game/runetek5/**`**: everything else, by subsystem (`client`, `config`, `entity`,
  `js5`, `cache`, `network`, `scene`, `graphics`, `toolkit/opengl`, `sound`, `preference`, …).
  Client-wide statics (`mainLogicStep`, `clientCycle`, `activeToolkit`) live in `client/ClientState`;
  other statics sit in the class they serve.
- **`jaclib`, `jaggl`, `jagdx`, `jagtheora`, `jagex3`**: the Kotlin side of JNI libraries shipped as
  binaries in the game cache (emulated in the browser for `jaclib` and `jaggl`).
- **`client/features/`**: our additions. `ClientFeatures` reads all `client.*` switches (server
  protocol `void`/`jagex`, packet-size overrides, fps interpolation of camera/entities, scale, the
  OpenGL AA pipeline). One class per extra (`TileMarkers`, `XpDrops`, `NpcLabels`,
  `GroundItemLabels`, `ConsoleEditing`, …); `LauncherDefaults`, `SmokeTest`.
- `lib/stub.jar` supplies stubs for compile; `openrs2-deob-annotations` comes from the OpenRS2
  snapshot repo.

**Import direction:** a packaged class cannot import a root-package class (root-level seams like
`canBlock` can be imported by name). Root code may call packaged features; when a feature needs root
state (e.g. `client.statXp`), pass it in from the root-package hook.

Rendering has three largely separate toolkits (Java OpenGL, native OpenGL, software, plus DirectX on
32-bit Windows), and the browser has its own versions. A drawing change that works on one can break
another — test each (desktop and browser), and after a map reload and a relog.

## Rules for changing code

- **Native bindings are frozen.** Never rename, move or change the signature of: a class declaring
  `external` (native) functions (or its package), any such function, fields natives read/write
  (`peer`, `nativeid`, `bar`, `width`, `height`, …), root-package classes with 1–2 letter names
  (`a`, `ba`, `oa`, …), or `oa.c(S)`, `oa.WA(S)`, `oa.g()`, `oa.OA()`. Breakage shows only at runtime.
- Leave `@Original*` and `@Pc` annotations untouched — they map declarations to the 2010 jar.
- Every hook into original code gets a comment starting **"Not part of the original client"**
  (grep that phrase to find all modifications).
- **JVM behaviour stays the original's.** A change the browser needs goes behind an `expect` seam
  (`PlatformLoop.kt`) or is a no-op on the JVM; bit arithmetic keeps its exact meaning.
- Extras are off by default behind a `client.<name>` switch read in `ClientFeatures`, with a
  `::command` toggle in `ClientCommandProcessor`, and a launcher flag in `RuneTek5` if exposed.
- Code style in client code is the 2010 idiom, in Kotlin: tabs, an explicit type on every
  declaration, plain arrays and `for`/`while` loops (above all in per-frame code), no collection
  pipelines, sequences or data classes. (`RuneTek5.kt` and `jsMain` platform code are the
  exception: idiomatic Kotlin, 4-space indent.)
- Feature work goes on a `feature/<name>` branch.
- Hook points for features (draw overlay, mini-menu entries, menu clicks, packet reactions, logout,
  console commands) are tabulated in `docs/FEATURES.md`.

## Release

Pushing a `v*` tag runs `.github/workflows/release.yml`: `./gradlew release -Pversion=<tag sans v>`
(the jar and the browser zip), writes `SHA256SUMS` for both, and creates a GitHub release. A push
that changes the workflow runs it as a dry run.
