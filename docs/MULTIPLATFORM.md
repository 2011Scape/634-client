# Multiplatform: the client on desktop and in a browser

The plan for making this client a Kotlin Multiplatform project with a JVM and a browser target. It
reuses the platform layers of void-client's `multiplatform-shim` branch, so the same game code runs on
desktop (with the native toolkits as now) and in a browser (WebGL 2, a WebSocket relay, an IndexedDB
cache), and keeps what this client adds: its names, these docs, the extras, and the `@Original*` and
`@Pc` annotations.

Work through the milestones in order, one `/goal` per milestone (or per batch, for the conversion).
Tick a milestone's box when its goal is met, in the same commit as the work.

## The goal

**Done when:**

- Desktop: `./gradlew runClient -Pclient.test=smoke` prints `SMOKE PASS` on toolkits 0, 1, 2 and 5,
  against the same reference frames and tolerances as before the port.
- Browser: the client logs in to a local Void server through the relay, loads a region and passes a
  browser version of the smoke test.
- The extras (tile markers, experience drops, npc and ground item labels, the fps interpolation, the
  scaling, MSAA and SMAA) work on both.
- No native binding has been renamed or moved ([PACKAGES.md](PACKAGES.md#native-code-is-frozen)).

**Not part of it:** Android or iOS, new features, a new renderer. LWJGL is an optional milestone
after the rest (milestone 7).

## The source to borrow from

The branch is checked out at `/home/pixel/Documents/Projects/void-client` (branch
`multiplatform-shim`). Read its `CLAUDE.md` first: it describes the shim strategy, the source sets, and
"JS rules learned the hard way", which apply here unchanged.

| What | Where in void-client |
|---|---|
| The last Java commit, before the conversion | `228561a`, layout `client/src` |
| Java API shims (`expect`) | `src/commonMain/kotlin/{awt,io,lang,net,util,sound}` |
| JVM actuals (mostly `actual typealias` to the JDK) | `src/jvmMain/kotlin/**` |
| Browser actuals | `src/jsMain/kotlin/**` |
| Native shims | `jaggl`, `jagdx`, `jaclib`, `jagtheora`, `jagex3` in all three source sets; the WebGL 2 emulator is `src/jsMain/kotlin/jaggl/` |
| Root-package platform seams | `PlatformLoop`, `Clock`, `Lock`, `SoftRef`, `BigInt`, `TimedRecordAccessor` |

It has no `gradlew` script: use the wrapper's cached distribution, as its `CLAUDE.md` says. Its game
code keeps obfuscated member names (`methodNNNN`, `anIntNNNN`) and other class names than ours, so its
changes to the game code are carried over through the name mapping from milestone 1, never copied
as they are.

How the toolkits line up: ours are 0 Java, 1 Java OpenGL, 2 native software (`oa`, `sw3d`), 5 native
OpenGL (`hw3d`), and 3 DirectX. In the browser, 0 and 1 work; 2 becomes void-client's Kotlin port of
`oa`; 3 and 5 are not available.

## Before each goal

- A Void server is running on this computer with a `smoketest` account (password `smoketest1`). Every
  smoke test needs it.
- On Linux, the native libraries are unpacked in `~/.jagex_cache_32/runescape` (or pass
  `-Pclient.nativeDir`), or toolkits 2 and 5 fall back to software.
- The session is in auto mode, so the goal's turns do not stop at permission prompts.
- The work is on the branch `feature/multi-platform-shim`.

## Milestones

### [x] 1. The name mapping

Match void-client `228561a` (Java) against this client's `f2649c4` with
[FabricMC Matcher](https://github.com/FabricMC/Matcher). Both are build 634, so most classes and
members should match. The mapping and its coverage go in `docs/multiplatform/`. Matcher is mainly a
GUI: if it cannot run headless, run the matching yourself, export the mapping to
`docs/multiplatform/void-client-228561a.tiny` (or another format Matcher exports), and let the goal
check it.

**Decision point:** if far fewer classes match than expected, stop and weigh the other route, porting
the extras onto void-client instead.

```
/goal Read docs/MULTIPLATFORM.md and CLAUDE.md, then complete milestone 1 only. Produce a name mapping between void-client commit 228561a (/home/pixel/Documents/Projects/void-client, client/src, Java) and this repository's commit f2649c4, using FabricMC Matcher headless if it supports that; if it does not, stop and ask me to run Matcher and export the mapping to docs/multiplatform/void-client-228561a.tiny. Done when: the mapping file exists under docs/multiplatform/; docs/multiplatform/COVERAGE.md states how many classes, methods and fields of each side are matched (show the numbers in the conversation), lists the root-package native classes (oa, a, ba, i, j, ja, ...) and confirms each maps to itself, and lists the 20 largest unmatched classes; and milestone 1 is ticked in docs/MULTIPLATFORM.md. Do not change any source file. Stop and report if fewer than 70% of classes match, or after 20 turns.
```

### [x] 2. Kotlin on the JVM

Convert the Java sources to Kotlin, still a JVM-only project with AWT, behaving exactly as before.
Add the `kotlin("jvm")` plugin first: Java and Kotlin compile together in one module, so the
conversion goes a batch at a time with a working client after each.

The rules for every batch:

- Statics a native library or another class reaches by name become `@JvmStatic` and `@JvmField`;
  `native` methods become `external fun` with the same name and signature; no class, method or field
  in the frozen list changes its name, package or signature.
- `@Original*` and `@Pc` stay on every declaration.
- Bit arithmetic keeps its exact meaning: `>>>` is `ushr`, a `(byte)` cast is `.toByte()`, and
  `& 0xff` widening is written out. A converted line that changes sign extension is a bug, even when
  it compiles.
- The comments "Not part of the original client" stay on every hook.

The batches, smallest risk first:

| Done | Batch | Files |
|---|---|---|
| [x] | a. `core`, `utils`, `encoding`, `buffer`, `statics`, `vardomain`, `trigger`, `tools`, `camera`, `rendering`, `interaction`, `sign`, `load` | about 80 |
| [x] | b. `cache`, `js5`, `network` | 103 |
| [x] | c. `config` | 173 |
| [x] | d. `entity`, `scene`, `preference`, `sound` | 120 |
| [x] | e. `graphics`, `toolkit` | 176 |
| [x] | f. `client` (with `client/features`) and the 188 classes directly in `com/jagex/game/runetek5` | about 250 |
| [x] | g. The native packages `jaclib`, `jaggl`, `jagdx`, `jagtheora`, `jagex3` | 59 |
| [x] | h. The root package, last: the native classes, `client`, the launcher `RuneTek5` | 165 |

For each batch, replace `<batch>` in this goal with its letter and file list:

```
/goal Read docs/MULTIPLATFORM.md and CLAUDE.md, then convert batch <batch> of milestone 2 from Java to Kotlin (JVM only, AWT unchanged), following the milestone's rules: frozen native names, classes, packages and signatures unchanged (natives become external fun, statics reached by name get @JvmStatic/@JvmField); @Original* and @Pc annotations kept on every declaration; bit arithmetic keeps its exact meaning (ushr, toByte, explicit masks); "Not part of the original client" comments kept. Done when: no .java file is left in the batch's packages; ./gradlew assemble succeeds; ./gradlew runClient -Pclient.test=smoke -Pclient.test.toolkit=<id> prints SMOKE PASS for each of the toolkit ids 0, 1, 2 and 5, against the references in the directory named by -Pclient.test.reference when one exists (show every SMOKE line); the batch is ticked in the table in docs/MULTIPLATFORM.md; and the batch is committed on feature/multi-platform-shim. Stop and report if a smoke test fails and two attempts to fix it do not pass, if the conversion would need a frozen name changed, or after 40 turns.
```

Tick milestone 2 when every batch is done and no `.java` file is left under `src/main`.

### [x] 3. Multiplatform, still desktop only

Switch to the `kotlin("multiplatform")` plugin with a `jvm()` target, move the game into
`commonMain`, and bring over void-client's Java API shims and their JVM actuals, so the common code
names `lang.Thread`, `io.RandomAccessFile`, `awt.Canvas` and so on instead of `java.*`. On the JVM
those are type aliases of the JDK classes, so the client behaves exactly as in milestone 2. The root
platform seams (`PlatformLoop` and the others) come over too, with the main loop and the worker loops
going through `runLoop`. The `./gradlew run`, `runClient` and `release` tasks keep working.

How it went, for the milestones after it:

- `./gradlew compileCommonMainKotlinMetadata -PcommonCheck=true` is the real check that common
  code is common: with the JVM as the only target, common code still sees the JDK through Kotlin's
  default `java.lang` imports, so a grep for `java.` is not enough. `-PcommonCheck` adds a `js()`
  target for this compile only; CI runs it.
- The native classes stay in `jvmMain` as `actual` classes; their `expect` declarations in
  `commonMain` were generated from them (every non-private member). Nothing native was renamed.
- `GameShell` still extends `java.applet.Applet` on the JVM, through an `applet` shim, instead of
  void-client's change to `Panel`: the launcher drives the client as an applet. Milestone 4 gives
  the shim a browser actual.
- Shims were added where void-client's lacked something this client uses, always with the JDK's
  behaviour on the JVM (`lang.parseInt`, `javaTrim`, `round`, the calendar classes, `Inflater`,
  `applet.Applet`, ...). A browser actual for each is part of milestone 4.
- `SmokeTest` calls the root package directly (Kotlin allows the import) instead of by reflection;
  SignLink's reflection is behind `expect` functions (`SignLinkPlatform`).
- `TimedRecordAccessor` in void-client is a game class (`Class348_Sub45`), not a platform seam;
  this client has its own version of it. `executeWorkerTasksInline` is declared but not used yet:
  SignLink needs it only where it has no worker thread (milestone 4).
- The main loop (`GameShell.run`), the loading screen (`AssetLoadingScreenRenderer.run`), sound
  (`PcmPlayerThread.run`) and SignLink's worker (`SignLink.run`) go through `runLoop`.

```
/goal Read docs/MULTIPLATFORM.md, CLAUDE.md and /home/pixel/Documents/Projects/void-client/CLAUDE.md, then complete milestone 3 only: make this a Kotlin Multiplatform project with only a jvm() target, move the game into src/commonMain/kotlin, copy void-client's expect shims (awt, io, lang, net, util, sound) and their jvmMain actuals, and the root platform seams (PlatformLoop, Clock, Lock, SoftRef, BigInt, TimedRecordAccessor), adapting their call sites to this client's names through the docs/multiplatform mapping. commonMain must not import any java.* or javax.* package (show the output of a grep that proves it). Done when: ./gradlew assemble succeeds; ./gradlew runClient -Pclient.test=smoke -Pclient.test.toolkit=<id> prints SMOKE PASS for each of 0, 1, 2 and 5 (show every SMOKE line); ./gradlew release builds a jar that starts with java -jar; milestone 3 is ticked; and the work is committed on feature/multi-platform-shim. Stop and report if two attempts to fix a failing smoke test do not pass, or after 40 turns.
```

### [x] 4. The browser target, in software

Add the `js(IR) { browser() }` target and void-client's browser actuals: the Java API shims, `oa`
and the other native-class shims, the WebSocket socket, the IndexedDB file system, the threads and the
`setTimeout` loop. Carry over void-client's changes to the game code that the browser needs (for
example `RingBufferInputStream` yielding on an empty read), found by diffing its branch against
`228561a` and translated through the mapping. First light is the Java software toolkit (0) in the
browser, through a WebSocket relay to the local server (void-client uses `ws://localhost:8081/proxy`
in local mode; `websockify 8081 localhost:43594` is one such relay).

```
/goal Read docs/MULTIPLATFORM.md, CLAUDE.md and /home/pixel/Documents/Projects/void-client/CLAUDE.md (including its "JS rules learned the hard way"), then complete milestone 4 only: add a js(IR) browser target, copy void-client's jsMain actuals for the Java API shims, the native shims, the socket, the IndexedDB file system, threads and PlatformLoop, and carry over its browser-specific changes to the game code, translated through the docs/multiplatform mapping. Done when: the browser bundle builds; with a WebSocket relay on localhost:8081 to the local Void server, the browser client on the software toolkit (0) logs in as smoketest and loads a region, which is shown by its console output or a screenshot in the conversation; the desktop smoke test still prints SMOKE PASS on toolkits 0, 1, 2 and 5 (show every SMOKE line); milestone 4 is ticked; and the work is committed on feature/multi-platform-shim. Stop and report after 60 turns or if the relay or server is not reachable.
```

How it went, for the milestones after it:

- The browser needs no worker threads: `SignLink` runs each request when it is queued
  (`executeWorkerTasksInline`), and the worker loops (the cache worker, the socket reader and
  writers, the ping worker) go through `runLoop` and end their turn where they would wait
  (`canBlock` false). On the JVM both flags keep the original behaviour.
- Three game-code fixes, each a no-op on the JVM: the JS5 handshake waits until its bytes are in
  (void-client's fix); `AsyncInputStream` does not take an empty buffer for a full one (a race the
  original only survives because its reader blocks); and Whirlpool's S-box is a `CharArray`, because
  the original string literal holds lone surrogates that a UTF-8 bundle turns into U+FFFD.
- void-client's game-code changes are mostly coroutine rewrites of its stream classes and speed work
  (the JS5 read budget per tick, Whirlpool, region loading); the ones above were what this client
  needed to run. The original reads at most 100 JS5 blocks a tick; in the browser `js5ReadsPerTick`
  lifts that cap (void-client's raised budget, written again on this client), and stays 100 on
  the JVM. Against a local server both fill the cache about as fast; it matters when the page ticks
  slowly (a background tab, a low frame rate), since the cap is per tick.
- `oa` in the browser is built on this client's own refactored code, by decision: it hands its
  calls to a `JavaToolkit` (the software renderer of toolkit 0), rather than porting void-client's
  `oa`, a 2,535-line copy of that renderer translated from the obfuscated deob. It runs in the
  browser (`tk2` in the developer console). The other root native classes are only ever made by
  the JVM's `oa`.

### Running the browser client

1. A Void server on this computer, as for the desktop smoke test.
2. The relay, from WebSockets to the server's TCP port: `node tools/relay.mjs` (no dependencies;
   arguments `[listenPort=8081] [serverHost=localhost] [serverPort=43594]`), or
   `websockify 8081 localhost:43594`. The client connects to `ws://localhost:8081/proxy`, or to
   `window.CONFIG.url` when the page sets it.
3. `./gradlew jsBrowserDevelopmentWebpack`, then serve it: `node tools/serve.mjs` (no
   dependencies; arguments `[port=8080] [bundleDir] [referenceDir=~/634-smoke-ref/browser]
   [shotDir=build/smoke-browser]`), and open `http://localhost:8080/`. It never lets the browser
   cache, so a rebuilt bundle is picked up on the next reload. `./gradlew build` makes the minified
   bundle in `build/dist/js/productionExecutable`, and `./gradlew release` zips it beside the jar.
4. The first visit downloads the cache into IndexedDB; later visits load it from there. The client
   starts as the launcher does (`LauncherDefaults`), on the toolkit the game's settings name, which
   it keeps across reloads. The tutorial hides the chat box, so commands go through the developer
   console (the `` ` `` key): `tk1` switches to the Java OpenGL toolkit on WebGL 2, `tk2` to the
   native software toolkit's browser version, and `tele 3222 3218` takes an admin player to
   Lumbridge (so does ctrl-clicking the minimap). Switches go in the address (`?client.msaa=4`) or a
   hosted page's `window.CONFIG`. `window.__log` holds the last 500 console lines. Leaving the page
   closes the client's connections, so the server logs the player out at once.
5. The smoke test: `http://localhost:8080/?client.test=smoke&client.test.toolkit=1` logs in as
   `smoketest`, runs the desktop test's checks without the launcher's defaults, compares its frames
   with `~/634-smoke-ref/browser` (served at `/smoke-ref/`), uploads them to `build/smoke-browser`,
   and puts SMOKE PASS or FAIL on the console, in `window.__log`, in `#smoke-result` and in the page
   title. It and the desktop smoke test both log in as `smoketest`: run one at a time.

### [x] 5. WebGL

Bring the Java OpenGL toolkit (1) to the browser on void-client's WebGL 2 emulator
(`jsMain/kotlin/jaggl`), with its fixes (sky, HD water, MSAA rims, sprite seams, black world map
labels). This client's own OpenGL work goes through the emulator too: the 3D view's framebuffer for
MSAA, SMAA and the render scale, and alpha-to-coverage. Where the emulator lacks something these
need (multisampled framebuffers, blits), add it or turn the feature off in the browser and say so in
[FEATURES.md](FEATURES.md).

```
/goal Read docs/MULTIPLATFORM.md (including milestone 4's notes and "Running the browser client"), docs/PACKAGES.md, docs/FEATURES.md, CLAUDE.md and /home/pixel/Documents/Projects/void-client/CLAUDE.md (its "JS rules learned the hard way" are binding), then complete milestone 5 only, on feature/multi-platform-shim: the Java OpenGL toolkit (1) in the browser. Copy void-client's WebGL 2 emulator (its jsMain/kotlin/jaggl files) as the JS side of our jaggl classes, keeping every frozen native name and signature. Recreate void-client's WebGL fixes (sky, HD water, MSAA rims, sprite seams, black world map labels) on this client's own refactored code, not by translating void-client's deob copy: any change to game code goes behind an expect seam, or is a no-op on the JVM, so JVM behaviour is unchanged. Let the page pass client.* switches (from the URL query string, e.g. ?client.msaa=4) into the JS system properties, so the browser can turn on this client's anti-aliasing pipeline: client.msaa, client.smaa, client.renderScale and client.a2c. Each must work through the emulator, adding what the emulator lacks (multisampled renderbuffers, framebuffer blits), or be turned off in the browser with a note in docs/FEATURES.md. Keep the rules: @Original*/@Pc on every declaration, "Not part of the original client" on every hook, exact bit arithmetic, one commit per working step, no attribution trailers. Done when: (1) ./gradlew build passes for both targets and checkClasses passes with the allowlist unchanged; (2) on the JVM, a javap method-by-method comparison against 35768fd shows only changes this milestone justifies; (3) with the relay (node tools/relay.mjs) and the local Void server running, the development bundle in Chrome logs in as browsertest (whose saved spawn is Lumbridge) on toolkit 1, confirmed by "Entered tk1" in the developer console. Screenshots in the conversation show the sky, water, the minimap and the world map drawn correctly, once with MSAA off and once with ?client.msaa=4. The Void audit log shows the login, and window.__log shows no uncaught errors; (4) the desktop smoke test prints SMOKE PASS on toolkits 0, 1, 2 and 5 against ~/634-smoke-ref on the final commit, rerunning a toolkit once on a reference-comparison-only failure, and the release jar passes java -Dclient.test=smoke -jar <jar> --address localhost (show every SMOKE PASS/FAIL line); (5) CI passes on GitHub after a push; (6) MULTIPLATFORM.md has milestone 5's notes and is ticked, and FEATURES.md lists anything turned off in the browser. Stop and report if a frozen native name would have to change, if WebGL 2 is unavailable in Chrome, if toolkit 1 cannot reach the game screen after three distinct attempts, or after 150 turns.
```

How it went, for the milestones after it:

- Toolkit 1 needed three things beyond the emulator: `NativeLibraryOwner` in the browser reports
  jaclib and jaggl as loaded (both are emulated); dynamic vertex data goes in buffer objects, since
  the emulator has no client-side arrays (`clientArraysAvailable`, as on AMD); and a webpack loader
  fills arrays with null (PACKAGES.md), without which `GlToolkit.setActiveTexture` failed an
  identity check on an unwritten slot.
- void-client's sky, HD water, MSAA rim and sprite seam fixes live in the emulator, so they came
  with it. Its world map label fix is game code and was written again on `HelveticaFont`, behind
  `textAntialiased`. Its SD water state fixes were not needed here: the water draws steadily.
- The anti-aliasing pipeline works through the emulator except SMAA, whose desktop GLSL 1.30
  shaders it cannot translate; FEATURES.md lists that. The page passes `client.*` switches in its
  query string.
- Switching toolkits while the world map is open can leave the map on "Loading..."; closing and
  opening it again loads it.

### [x] 6. The extras, the smoke test in the browser, CI and the docs

The extras on both targets; the launcher's options read in the browser from the page's
configuration or the URL; `SmokeTest` running in the browser (log in, wait for the frames, check the
frame, report pass or fail to the page and the console); CI building both targets and the release
publishing the jar and the browser bundle; and the README, these docs and `CLAUDE.md` describing the
Kotlin project (the "2010 idiom" rule becomes its Kotlin equivalent: explicit types, plain arrays
and loops in the hot paths).

```
/goal Read docs/MULTIPLATFORM.md and CLAUDE.md, then complete milestone 6 only: make every extra (tile markers, xp drops, npc labels, ground item labels, shift-drop, zoom, render distance, fps interpolation, scaling, msaa, smaa, render scale) work in the browser as on desktop, with the launcher's options given in the browser by the page's configuration or URL parameters; run SmokeTest in the browser with its result printed to the console; build both targets in .github/workflows/build.yml and publish the browser bundle in release.yml; and update README.md, docs/*.md and CLAUDE.md for the Kotlin Multiplatform project. Done when: the browser smoke test prints SMOKE PASS on toolkits 0 and 1 and the desktop smoke test prints SMOKE PASS on 0, 1, 2 and 5 (show every SMOKE line); ./gradlew build builds both targets; milestone 6 is ticked and the top-level goal's checks are met; and the work is committed on feature/multi-platform-shim. Stop and report after 60 turns.
```

How it went:

- `LauncherDefaults` (common) holds the launcher's defaults; `RuneTek5` and the page both apply it,
  after the page's URL switches and `window.CONFIG`. Twice the render distance is slow on the
  software toolkit in a browser (well under 10 fps); the smoke test runs without the defaults, as
  `runClient` does.
- Settings had never been saved in the browser: `GamePreferences.getEncodedSize` counts fields by
  reflection, got 0, and every save overflowed a 1-byte buffer and threw in silence.
  `classFieldsAvailable` gives it a fixed bound there, and the chosen toolkit now survives a reload.
- SMAA runs in the browser: without a version line (`smaaGlslVersion`) the emulator translates its
  shaders to GLSL ES 3.00.
- The browser smoke test needed a few things around it: `halt` stops every loop and reports to the
  page, `tools/serve.mjs` receives its frames and serves the browser references (captured from
  passing runs, in `~/634-smoke-ref/browser`), the emulator's `glReadPixelsi` reads RGBA and packs
  BGRA itself, and `SmokeTest` asks each check only until it is done (its pass line had repeated
  every finished check on every frame since milestone 2, 77 KB by the end).
- Shift-drop reads the held Shift key, so a test has to send a key-down; a click with the shift
  flag alone does not drop. The zoom needs the pointer over the game view first.

### [ ] 7. Optional: LWJGL on desktop

Move void-client's GL emulator (immediate mode, matrix stack, fixed-function shader, the ARB and
GLSL transpilers) from `jsMain` to `commonMain` over a small GLES 3 interface, with WebGL 2 and LWJGL
as its two implementations. The JVM can then draw toolkit 1 through the same emulator as the browser,
so a desktop smoke test also tests the browser's GL path, and the Java OpenGL toolkit no longer needs
`jaggl` from the game cache. The AWT canvas needs a GL context of its own
([lwjgl3-awt](https://github.com/LWJGLX/lwjgl3-awt)), the main risk. Keep the `jaggl` natives as the
default until the LWJGL path passes the smoke test on its own references.

```
/goal Read docs/MULTIPLATFORM.md and CLAUDE.md, then complete milestone 7 only: move the GL emulator from src/jsMain/kotlin/jaggl into commonMain behind a GLES 3 interface implemented by WebGL 2 (js) and LWJGL with lwjgl3-awt (jvm), selected on the JVM by -Pclient.gl=lwjgl, with the jaggl natives still the default. Done when: ./gradlew runClient -Pclient.test=smoke -Pclient.test.toolkit=1 -Pclient.gl=lwjgl prints SMOKE PASS against its own references; the default desktop smoke test still prints SMOKE PASS on 0, 1, 2 and 5 and the browser smoke test on 0 and 1 (show every SMOKE line); milestone 7 is ticked; and the work is committed on feature/multi-platform-shim. Stop and report if lwjgl3-awt cannot give the canvas a context on this machine, or after 60 turns.
```

## The risks

- **How well the clients match.** Milestone 1 measures it; the browser changes in milestones 4 and 5
  are only as good as the mapping.
- **Silent arithmetic changes in the conversion.** Byte, sign and shift semantics differ between Java
  and Kotlin. The smoke test's reference frames are the main net, so keep references for every
  toolkit before milestone 2 starts.
- **Speed in the browser.** void-client's performance commits (the region load, the world map,
  batched decor, Whirlpool, DEFLATE) are part of what milestones 4 and 5 carry over.
