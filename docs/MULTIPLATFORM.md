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

### [ ] 2. Kotlin on the JVM

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
| [ ] | d. `entity`, `scene`, `preference`, `sound` | 120 |
| [ ] | e. `graphics`, `toolkit` | 176 |
| [ ] | f. `client` (with `client/features`) and the 188 classes directly in `com/jagex/game/runetek5` | about 250 |
| [ ] | g. The native packages `jaclib`, `jaggl`, `jagdx`, `jagtheora`, `jagex3` | 59 |
| [ ] | h. The root package, last: the native classes, `client`, the launcher `RuneTek5` | 165 |

For each batch, replace `<batch>` in this goal with its letter and file list:

```
/goal Read docs/MULTIPLATFORM.md and CLAUDE.md, then convert batch <batch> of milestone 2 from Java to Kotlin (JVM only, AWT unchanged), following the milestone's rules: frozen native names, classes, packages and signatures unchanged (natives become external fun, statics reached by name get @JvmStatic/@JvmField); @Original* and @Pc annotations kept on every declaration; bit arithmetic keeps its exact meaning (ushr, toByte, explicit masks); "Not part of the original client" comments kept. Done when: no .java file is left in the batch's packages; ./gradlew assemble succeeds; ./gradlew runClient -Pclient.test=smoke -Pclient.test.toolkit=<id> prints SMOKE PASS for each of the toolkit ids 0, 1, 2 and 5, against the references in the directory named by -Pclient.test.reference when one exists (show every SMOKE line); the batch is ticked in the table in docs/MULTIPLATFORM.md; and the batch is committed on feature/multi-platform-shim. Stop and report if a smoke test fails and two attempts to fix it do not pass, if the conversion would need a frozen name changed, or after 40 turns.
```

Tick milestone 2 when every batch is done and no `.java` file is left under `src/main`.

### [ ] 3. Multiplatform, still desktop only

Switch to the `kotlin("multiplatform")` plugin with a `jvm()` target, move the game into
`commonMain`, and bring over void-client's Java API shims and their JVM actuals, so the common code
names `lang.Thread`, `io.RandomAccessFile`, `awt.Canvas` and so on instead of `java.*`. On the JVM
those are type aliases of the JDK classes, so the client behaves exactly as in milestone 2. The root
platform seams (`PlatformLoop` and the others) come over too, with the main loop and the worker loops
going through `runLoop`. The `./gradlew run`, `runClient` and `release` tasks keep working.

```
/goal Read docs/MULTIPLATFORM.md, CLAUDE.md and /home/pixel/Documents/Projects/void-client/CLAUDE.md, then complete milestone 3 only: make this a Kotlin Multiplatform project with only a jvm() target, move the game into src/commonMain/kotlin, copy void-client's expect shims (awt, io, lang, net, util, sound) and their jvmMain actuals, and the root platform seams (PlatformLoop, Clock, Lock, SoftRef, BigInt, TimedRecordAccessor), adapting their call sites to this client's names through the docs/multiplatform mapping. commonMain must not import any java.* or javax.* package (show the output of a grep that proves it). Done when: ./gradlew assemble succeeds; ./gradlew runClient -Pclient.test=smoke -Pclient.test.toolkit=<id> prints SMOKE PASS for each of 0, 1, 2 and 5 (show every SMOKE line); ./gradlew release builds a jar that starts with java -jar; milestone 3 is ticked; and the work is committed on feature/multi-platform-shim. Stop and report if two attempts to fix a failing smoke test do not pass, or after 40 turns.
```

### [ ] 4. The browser target, in software

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

### [ ] 5. WebGL

Bring the Java OpenGL toolkit (1) to the browser on void-client's WebGL 2 emulator
(`jsMain/kotlin/jaggl`), with its fixes (sky, HD water, MSAA rims, sprite seams, black world map
labels). This client's own OpenGL work goes through the emulator too: the 3D view's framebuffer for
MSAA, SMAA and the render scale, and alpha-to-coverage. Where the emulator lacks something these
need (multisampled framebuffers, blits), add it or turn the feature off in the browser and say so in
[FEATURES.md](FEATURES.md).

```
/goal Read docs/MULTIPLATFORM.md, CLAUDE.md and /home/pixel/Documents/Projects/void-client/CLAUDE.md, then complete milestone 5 only: run the Java OpenGL toolkit (1) in the browser on void-client's WebGL 2 emulator in jsMain/kotlin/jaggl, with void-client's WebGL fixes carried over through the docs/multiplatform mapping, and make this client's anti-aliasing pipeline (client.msaa, client.smaa, client.renderScale, client.a2c) work through the emulator or be turned off in the browser with a note in docs/FEATURES.md. Done when: in the browser on toolkit 1 the client logs in as smoketest, and screenshots in the conversation show the sky, water, the minimap and the world map drawn correctly with MSAA off and on; the desktop smoke test still prints SMOKE PASS on toolkits 0, 1, 2 and 5 (show every SMOKE line); milestone 5 is ticked; and the work is committed on feature/multi-platform-shim. Stop and report after 60 turns.
```

### [ ] 6. The extras, the smoke test in the browser, CI and the docs

The extras on both targets; the launcher's options read in the browser from the page's
configuration or the URL; `SmokeTest` running in the browser (log in, wait for the frames, check the
frame, report pass or fail to the page and the console); CI building both targets and the release
publishing the jar and the browser bundle; and the README, these docs and `CLAUDE.md` describing the
Kotlin project (the "2010 idiom" rule becomes its Kotlin equivalent: explicit types, plain arrays
and loops in the hot paths).

```
/goal Read docs/MULTIPLATFORM.md and CLAUDE.md, then complete milestone 6 only: make every extra (tile markers, xp drops, npc labels, ground item labels, shift-drop, zoom, render distance, fps interpolation, scaling, msaa, smaa, render scale) work in the browser as on desktop, with the launcher's options given in the browser by the page's configuration or URL parameters; run SmokeTest in the browser with its result printed to the console; build both targets in .github/workflows/build.yml and publish the browser bundle in release.yml; and update README.md, docs/*.md and CLAUDE.md for the Kotlin Multiplatform project. Done when: the browser smoke test prints SMOKE PASS on toolkits 0 and 1 and the desktop smoke test prints SMOKE PASS on 0, 1, 2 and 5 (show every SMOKE line); ./gradlew build builds both targets; milestone 6 is ticked and the top-level goal's checks are met; and the work is committed on feature/multi-platform-shim. Stop and report after 60 turns.
```

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
