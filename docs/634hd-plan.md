# 634HD Renderer: Implementation Plan

Oct 13, 2026

**Start at Phase 3.** The earlier phases (LWJGL migration and porting the OpenGL toolkit) are done and no longer part of this plan. Before writing Phase 3 code, confirm in the codebase that they hold (LWJGL-based OpenGL toolkit, no jaggl, jagdx or sw3d in the build, client runs on a 64-bit JDK) and record any gaps in Current state rather than fixing them silently.

## Overview

634HD is a new GPU-only rendering toolkit for the 2011Scape 634 client, built on LWJGL 3 inside the client's existing AWT window. It replaces Jagex's 32-bit native libraries (jaggl, jagdx, sw3d), removes the 32-bit JRE requirement, and adds modern graphics features in the spirit of the 117HD RuneLite plugin.

Goals, in order:

1. Run on any 64-bit JDK on Windows, Linux and macOS (including Apple Silicon) with no native Jagex DLLs.
2. Keep the existing OpenGL and Safe Mode renderers working as fallbacks.
3. Ship 634HD as a selectable toolkit with extended draw distance, shadows, per-pixel lighting, a material system and modern post-processing.

Non-goals:

- No DirectX support. The DirectX toolkit is removed, not ported.
- No server or protocol changes. Everything here is client-side.
- No porting of 117HD's code or content data directly. Its shaders and techniques are reference material only, and its OSRS-keyed data does not apply to the 634 cache.
- No GLFW migration. The client keeps its AWT window and input.

Repository: [2011Scape/634-client](https://github.com/2011Scape/634-client) (fork of GregHib/void-client, deobfuscated and refactored 634 client).

## Current state and constraints

The hardware renderers depend on closed-source 32-bit native libraries, which is why the README tells users to run a 32-bit JRE. A 64-bit JVM cannot load them.

| Toolkit | Native dependency | Status after this plan |
| --- | --- | --- |
| Java software (Safe Mode) | None | Kept as is |
| OpenGL | jaggl (JNI GL bindings) | Ported to LWJGL |
| DirectX 9 | jagdx (Windows only) | Removed |
| Native software / SSE | sw3d | Removed; Java software renderer covers it |
| 634HD | None (LWJGL) | New in Phase 3 |

LWJGL 3 is already a dependency. The client keeps its AWT window, canvas and input; GLFW is not used. Before starting Phase 3, the agent should record here exactly how the current build creates its GL context (jaggl, an AWT bridge such as rlawt or lwjgl3-awt, or something else) and which parts of the OpenGL toolkit already call LWJGL instead of jaggl.

Other facts the agent should verify in the codebase before starting:

- The game shell extends an AWT `Canvas`/`Applet` and runs its main loop on its own thread.
- Input arrives through AWT `KeyListener`, `MouseListener`, `MouseMotionListener`, `MouseWheelListener` and `FocusListener` on the canvas, which push events into the client's own queues.
- A table maps AWT key codes to Jagex key codes.
- The software renderer rasterizes into an `int[]` and blits through an AWT `Image`. Loading and error screens draw with `Graphics.drawString`.
- The GL toolkit uses fixed-function OpenGL plus ARB extensions (vertex buffer objects, possibly ARB assembly programs).
- Logic runs in 20ms cycles; rendering is capped at 50 FPS.
- The README's `-d32` flag does nothing on Windows and was removed in Java 10. Remove it from the README if it's still there.

### Verified state (Oct 13, 2026, before Phase 3)

- **GL context.** `jaggl` still exists, as a facade over LWJGL 3.4.3 whose signatures stay frozen
  (1289 call sites in commonMain plus the browser's WebGL 2 emulation); libjaggl is never loaded.
  Contexts come from lwjgl3-awt 0.2.5's platform canvases (GLX on Linux, also under XWayland;
  NSOpenGLView on macOS) and our own `WglCanvas` on Windows, not rlawt (`jvmMain/kotlin/jaggl/GlContext.kt`).
  No version or profile is requested: the newest compatibility context on GLX/WGL, legacy 2.1 on macOS.
  No commonMain code calls LWJGL directly; every toolkit goes through `jaggl.OpenGL`.
- **Gap: the legacy natives are not removed.** jagdx (DirectX, toolkit 3), sw3d (SSE `oa`, toolkit 2),
  hw3d (the vertex packers behind toolkit 5), jagtheora and jagmisc are still built and loaded from the
  cache (`StartupDependencyLoading.kt`). Decision: keep them as fallbacks; 634HD needs none of them.
- **64-bit JDK.** Runs on Java 21–25 (toolchain 21). The README has no `-d32`; it says DirectX needs a
  32-bit Java, which is still true.
- **Shell and input** are as described above (`GameShell : Applet`, `GameCanvas`, `CanvasKeyRecorder`
  with `REMAPPED_KEYS`, `MouseManager`, `JavaDefaultSurface` blit, `AwtLoadingScreen` text).
- **GL toolkit** (`graphics/gl/GlToolkit`, commonMain) is fixed-function plus optional ARB VBOs, ARB
  vertex programs (water), GLSL through ARB shader objects (water, SMAA) and EXT framebuffers. Shaders
  are inline strings.
- **Frame rate.** 50 FPS only when `client.fps` is unset; the launcher defaults to 144. Camera, entity
  and animation-keyframe interpolation already exist for every toolkit (`ClientFeatures.blendCamera`,
  `blendEntities`, `animationStep`), so Phase 4 item 1 is mostly done.

### Where the code differs from this plan

1. The legacy natives above are still in the build (kept by decision).
2. The AWT bridge is lwjgl3-awt, not rlawt. A 3.3 core context needs new context code; macOS needs a
   core-profile NSOpenGL pixel format for 4.1.
3. The toolkit choice in the options screen is a cache CS2 interface that clamps ids to 0..5
   (`CS2Interpreter`); the client has no toolkit labels of its own. 634HD is selected with
   `client.toolkit` / `--toolkit` / `::tk6`, and shown in the options screen by a client-side hook.
4. The existing fallback is silent (last saved toolkit, then Java). 634HD adds 6 → 1 → 0 with a message.
5. The client is Kotlin, not Java. 634HD is JVM-only (`jvmMain`, `graphics/hd/`); the browser keeps 0 and 1.
6. The screenshot harness exists as `SmokeTest` (reference compare by blocks); Phase 3 adds camera
   positions and a per-pixel diff image.

## Phase 3: 634HD toolkit foundation

**Status (Oct 13, 2026): done on Linux x64 (RTX 5070), see docs/RENDERING.md "634HD".**
- **Parity:** every harness scene, the map reload, the relog and a loc change are within 3–35 pixels
  per thousand of toolkit 1. Toolkit 1 against itself is 3–29.
- **Busy areas** at 1920×1006: Varrock 375 draw calls at 409 fps, the GE 345 at 453 fps (toolkit 1:
  2060 and 303 fps in Varrock).
- **Region build:** at most 18–29 ms in one frame (the build is spread over frames).
- **Not measured:** Iris Xe and RTX 3060 numbers, Windows and macOS (no such machines here).

Outcome: a new toolkit, labelled "634HD" in settings, renders the game identically to the existing OpenGL toolkit but with a modern architecture that later features build on.

Baseline: OpenGL 3.3 core profile on all platforms. Features that need GL 4.3 (compute shaders, SSBOs) are optional, detected at runtime, and must have a 3.3 path or be disabled. macOS tops out at 4.1 core.

1. Create `HdToolkit` (and its model, ground and sprite classes) implementing the existing toolkit interface. Start by copying the existing OpenGL toolkit, then rewrite it. Do not modify the old GL toolkit; it stays as the fallback.
2. Register it as a new toolkit ID and add it to the graphics settings with the label "634HD (requires OpenGL 3.3)". If context creation or a capability check fails, fall back to OpenGL, then Safe Mode, and tell the user why.
3. Replace fixed-function code with GLSL shaders: model, terrain, sprite/interface, and particle programs at minimum. Keep shaders as resource files, loaded and compiled with readable error logs.
4. Retained scene. Split geometry into two paths:
   - Static: terrain, walls, floor decorations and non-animated scenery are uploaded once per region load into large shared vertex and index buffers, grouped by chunk (8x8 tiles or similar) for culling.
   - Dynamic: players, NPCs, projectiles, graphics objects and animated scenery are streamed each frame through a ring buffer (persistent mapping where available, `glBufferSubData` orphaning otherwise).
   - Hook the static upload directly into the client's region and scene loading code. Rebuild only affected chunks when the scene changes (doors opening, objects spawning).
5. Vertex data: position, packed color, UV, normal, texture/material ID, and face flags. Derive UVs at upload from RS texture-space triangles and 634's cylindrical, spherical and cube mappings.
6. Transparency: reproduce face priorities and per-face alpha. Draw opaque geometry first, then sort transparent faces back to front. Use a CPU sort on the 3.3 path; a compute-shader sort is an optional 4.3 optimisation.
7. Interfaces and 2D sprites render through the same toolkit in a final pass, unchanged in appearance.
8. Frustum-cull static chunks on the CPU. Batch draws to keep the call count low (target under 500 draw calls in a busy area).

Done when: 634HD matches the existing OpenGL toolkit in the screenshot test set, holds 60 FPS or better on a mid-range integrated GPU (for example Intel Iris Xe) at 1080p in a busy area such as Varrock square or the Grand Exchange, and region loads cause no hitch longer than 100ms.

## Phase 4: Rendering features

**Status (Oct 13, 2026): done on Linux x64 (RTX 5070); see docs/RENDERING.md "634HD features". Every switch on, Varrock at 1920x1006 with 4x MSAA and 2x
draw distance: 212 fps (off: 308), 476 draw calls, region hitches 10-20 ms; Grand Exchange 310 fps. Iris Xe, RTX 3060, Windows and macOS not measured.**

Build these in order, one pull request each. Every feature gets a toggle or quality level in settings and must not change behaviour when disabled.

1. Decouple rendering from the 20ms logic cycle and uncap the framerate. Interpolate camera, player and NPC positions and orientations between ticks using the fraction of the current cycle elapsed. Blend between animation keyframes rather than holding frames (check which 634 sequences already tween and leave those alone). Add an FPS cap setting.
2. Move to a linear color pipeline. Convert Jagex HSL colors to linear RGB at upload, light in linear space, render to an RGBA16F target, tonemap at the end (AgX or ACES, selectable while tuning), then output sRGB. Add ordered or blue-noise dithering on output to remove banding.
3. Generate mipmaps for all world textures and add anisotropic filtering (setting: off, 2x to 16x). Keep nearest filtering for interfaces.
4. Extend draw distance beyond the stock scene size, with a setting. Add distance fog driven by the environment config. Fade objects in over a short distance band with dithered transparency instead of popping. Merge far terrain chunks into simplified meshes.
5. Bake per-vertex ambient occlusion for static geometry during region upload and store it in the vertex data.
6. Move lighting from baked vertex colors to the fragment shader: one directional sun light plus ambient from the environment config. Preserve the original look as closely as possible when other features are off.
7. Add a directional shadow map from the sun (cascades or a single fitted frustum) with PCF filtering and a resolution setting. Static geometry and dynamic entities both cast.
8. Add point lights attached to object IDs, NPC IDs, spot animations and projectiles, defined in data files (see Content pipeline). Use clustered or tiled light culling; cap visible lights per tile on the 3.3 path.
9. Blend between tile materials using a noise mask so tile-grid edges disappear. Use triplanar mapping on steep terrain.
10. Upgrade water with animated normals, fresnel, depth-based color and shoreline foam. Screen-space reflections as a high-quality option.
11. Use alpha-to-coverage for cutout foliage textures when MSAA is on, and vertex-shader wind sway on object IDs listed in data.
12. Replace flat sky colors with a gradient sky or simple atmospheric scattering, plus height fog, both from the environment config. No day/night cycle unless the team decides otherwise.
13. Move the 634 particle system to the GPU path with soft particles (depth fade) and optional light emission.
14. Add MSAA (2x to 8x) on the scene target, optional FXAA or SMAA, bloom from the HDR buffer, and a per-environment color grading LUT.
15. Render 3D at native resolution and scale the interface by integer factors with nearest filtering, with an optional xBR-style upscale for sprites and fonts.

Skip motion blur and depth of field.

## Phase 5: Material system and texture maps

**Status (Oct 13, 2026): done with 23 map sets, fine height detail generated with /generate (5 MB; BC5/BC7 precompressed at build time by
`./gradlew compressMaterialMaps`). Tangents come from screen-space derivatives, not per-face data. Materials off restores the
Phase 4 look (harness unchanged).**

Outcome: textured surfaces support normal, roughness, ambient occlusion, emissive and optional height maps, loaded from files outside the cache. Depends on per-pixel lighting (Phase 4, item 6).

1. The cache texture stays the albedo. Extra maps load from a materials folder keyed by 634 texture ID. Never modify the cache.
2. Store maps in texture arrays, one `GL_TEXTURE_2D_ARRAY` per map type, one layer per texture. Standardise layer size (512x512 with mips) or keep a small number of arrays by size.
3. Pack channels: normals in BC5, a packed AO/roughness/emissive-mask map in BC7 (fall back to uncompressed RGBA8 if BC7 is unavailable). Precompress at build time, not at runtime.
4. A material table in a uniform buffer maps texture ID to layer indices and parameters:

```glsl
struct Material {
    int albedoLayer;
    int normalLayer;    // -1 = flat normal
    int ormLayer;       // packed AO / roughness / emissive mask, -1 = defaults
    int heightLayer;    // -1 = no parallax
    vec2 uvScroll;      // 634 animated textures; applied to every map
    float specularStrength;
    float emissiveStrength;
};
```

5. Textures without extra maps fall back to defaults (flat normal, mid roughness, no emission), so content can be added one texture at a time.
6. Compute per-face tangents during the Phase 3 UV derivation pass. Terrain tangents align with world axes.
7. Normal maps may be higher resolution than the albedo (for example 128x128 albedo with a 512x512 normal map). This is the preferred way to add detail while keeping the original art.
8. Parallax occlusion mapping for height-mapped ground textures, behind a quality setting.
9. Untextured faces (most RS geometry) get materials only through override data keyed by model ID, face color or area, plus an optional generic detail normal blended in at close range.
10. No metallic channel. The workflow is albedo, normal, roughness/specular, emissive, optional height.

Done when: a test set of at least 20 common textures (ground, stone, wood, water, lava) has full map sets, toggling materials off restores the Phase 4 look exactly, and memory for the texture arrays stays under 512 MB at the highest quality.

## Content pipeline and tooling

**Status (Oct 13, 2026): done: five JSON files with schemas, hot reload, debug panel. GPU time is a
timer query around the 3D view; the light-cluster overlay shows per-pixel light counts.**

All 634HD visual data lives in versioned files in the repo, keyed by 634 IDs and coordinates, and hot-reloads in debug builds. Build the debug panel during Phase 4, not after; content work depends on it.

| File | Keyed by | Holds |
| --- | --- | --- |
| `materials.json` | Texture ID | Map file paths, UV scroll, specular, emissive strength |
| `lights.json` | Object ID, NPC ID, spot animation ID, projectile ID | Color, radius, intensity, offset, flicker |
| `environments.json` | Area (coordinate bounds or region IDs) | Sun direction and color, ambient, fog color and distance, sky colors, LUT, water tint |
| `model_overrides.json` | Model ID, face color, area | Material assignment, wind sway flag, shadow casting flag |
| `foliage.json` | Object ID | Wind sway strength and frequency |

Rules:

- JSON with a schema file per format, validated at load. Invalid entries log a warning with file and line and are skipped; they never crash the client.
- Environments blend over a short distance when the player crosses an area boundary.
- Texture maps live under `resources/634hd/materials/<textureId>/` as `normal.png`, `orm.png`, `height.png`, `emissive.png`. A Gradle task compresses them to BC5/BC7 for release builds.

Debug panel (debug builds only, toggled with a key):

- Shows FPS, frame time breakdown (CPU, GPU via timer queries), draw calls, triangle count, visible lights and region load time.
- Live-edit the current environment and the nearest lights, with a button to write changes back to the JSON file.
- Toggle each Phase 4 feature independently.
- Overlay modes: normals, material IDs, light clusters, shadow cascades, chunk bounds.
- Hover a face to show its model ID, object or NPC ID, texture ID and tile coordinates. Content authors need this to write overrides.

### Generated assets

The agent can create images with the `/generate` skill. Use it whenever 634HD needs a new image asset instead of leaving a placeholder.

Good uses:

- Material maps for Phase 5: height, roughness/ORM and emissive maps for existing cache textures, and detail normal maps.
- Particle sprites (smoke, embers, sparkles, magic effects) and water foam and caustic textures.
- Sky gradients, cloud layers and starting-point color grading LUTs for environments.
- Generic detail textures for untextured faces and terrain blend noise masks.

Rules:

1. Match the 2010 RuneScape HD art style and palette. Show the source cache texture or an in-game screenshot as reference in the prompt where the skill accepts one.
2. Never replace existing cache albedo textures with generated ones. Generated albedo is only for new assets, kept at the original resolution (64x64 or 128x128).
3. For normal maps, generate a height map and convert it to a normal map with a tool, rather than generating normals directly. Generated "normal maps" are usually not valid tangent-space normals.
4. Anything that tiles must be checked for visible seams by previewing it tiled 3x3 in-game before it's committed.
5. Save the prompt and settings next to each asset as `prompt.txt` so it can be regenerated or tweaked later.
6. Generated assets go through the same folder layout, compression and validation as hand-made ones, and are listed in the pull request with before/after screenshots for human review.

## Testing and acceptance

Every phase ends with the screenshot test passing on the target platforms. Build that harness first, before any Phase 3 rendering work.

1. Screenshot harness: a debug command that teleports the camera to a fixed list of positions (coordinate, height, yaw, pitch, zoom), waits for the region to load, captures the frame and writes a PNG. Store reference images per toolkit in the repo or CI artifacts.
2. Comparison: a script diffs captures against references with a per-pixel tolerance and writes a diff image. Phase 3 (parity) requires no visible differences; feature phases update references deliberately.
3. Test positions, at minimum: Lumbridge castle courtyard, Varrock square, Grand Exchange, Falador park (foliage), Draynor swamp (water and fog), Karamja volcano (lava, emissive), a dungeon interior (darkness, point lights), a busy combat area with many NPCs, and a scene with heavy transparency (spell effects).
4. Performance capture: average and 1% low FPS over 60 seconds at each position, at 1080p, on three tiers of hardware (integrated GPU, mid-range discrete, Apple Silicon). Record results per phase so regressions are visible.
5. Platforms: Windows x64, Linux x64 and macOS arm64 on every phase. Linux arm64 and macOS x64 at release.
6. Fallback tests: force context creation failure and missing extensions, and confirm the client falls back to OpenGL, then Safe Mode, with a message.
7. Long-session test: 2 hours of play crossing many regions with GPU memory tracked. Memory must plateau, not grow.

Performance targets for 634HD at default settings and 1080p: 60 FPS on an Intel Iris Xe class integrated GPU, 144 FPS on a mid-range discrete GPU (for example RTX 3060), and region loads without hitches longer than 100ms.

## Risks and open questions

| Risk | Impact | Mitigation |
| --- | --- | --- |
| AWT bridge misbehaves on macOS (resize, fullscreen, HiDPI) | Black or stretched frames, crashes on mode switch | Test resize and fullscreen on macOS every phase; move to GLFW only if bridge bugs keep recurring |
| Transparency sorting differs from the original face-priority behaviour | Visual glitches on spell effects, glass, water | Dedicated test positions; CPU sort that mirrors original priority rules before optimising |
| Extended draw distance exceeds what scene loading and pathing code assumes | Crashes or wrong clicks at the scene edge | Keep logic scene size unchanged; extend only the render scene, loading extra regions read-only |
| Interpolation introduces visible lag between click and movement | Players feel input delay | Interpolate one tick behind at most; setting to disable |
| Content work (materials, lights, environments) is open-ended | Engine done, world still looks stock | Ship engine features first; do content area by area with the debug panel |
| Old drivers on Windows lie about GL 3.3 support | Crashes on startup | Capability checks plus fallback chain; never make 634HD the default until crash data looks clean |

Open questions for the team:

- [ ] How does the current build create its GL context, and how much of the OpenGL toolkit already uses LWJGL? (Agent records the answer in Current state.)
- [ ] Should 634HD become the default toolkit at release, or stay opt-in for the first version?
- [ ] Is a day/night cycle wanted at all?
- [ ] Who authors material maps and environment data, and is there an art style guide to follow?
- [ ] Minimum supported hardware: is OpenGL 3.3 acceptable, or must 634HD also run on 2.1-era GPUs?

## Reference repositories

RuneLite and 117HD are already cloned locally as reference. They solve the same problems 634HD faces in an RS client: retained GPU scenes, transparency sorting, shadows, data-driven lights and environments, and LWJGL inside an AWT window.

- RuneLite: `/home/pixel/Documents/Projects/runelite/`
- 117HD (RLHD): `/home/pixel/Documents/Projects/RLHD/`

If rlawt is the AWT bridge, clone it alongside them:

```bash
git clone --depth 1 https://github.com/runelite/rlawt /home/pixel/Documents/Projects/rlawt
```

| Repo | Read for | Phase |
| --- | --- | --- |
| [runelite/runelite](https://github.com/runelite/runelite) (`plugins/gpu`) | Scene upload, static/dynamic split, compute face sort, capability fallbacks | 3 |
| [117HD/RLHD](https://github.com/117HD/RLHD) (`rs117.hd` package) | Shaders, shadow maps, lighting model, water, fog, and the file formats for lights, environments, materials and overrides | 4, 5, Content pipeline |
| [runelite/rlawt](https://github.com/runelite/rlawt) | Context creation and recreation, macOS layer quirks, HiDPI | 3 |

Rules:

1. Treat the reference folders as read-only. Never modify them, add them as dependencies, or copy them into this repo.
2. Read RuneLite's GPU plugin first. It's smaller and shows the core ideas; 117HD layers years of features on top.
3. Use them for techniques, not data. Anything keyed to OSRS (model IDs, texture IDs, coordinates, overlay and underlay IDs, the 104x104 scene size, OSRS model formats) does not apply to the 634 cache. Don't carry those assumptions over.
4. Check each repo's LICENSE before copying code. RuneLite is BSD 2-clause; confirm RLHD's and rlawt's. Copied code keeps its license header and the pull request names its source file.
5. When a design choice comes from one of these repos, note the source file and commit in the pull request description so reviewers can compare.

## Working rules for the coding agent

1. Work one phase at a time, and within Phase 4 one numbered feature at a time. One pull request per item, with screenshots and performance numbers in the description.
2. Before writing code for a phase, read the relevant client code and post a short plan listing the classes you will touch. Flag anything in this document that the code contradicts instead of working around it.
3. The game must stay playable at the end of every pull request. Safe Mode must work at all times.
4. Never modify the cache, the network protocol, or game logic. If a rendering change seems to need one of these, stop and ask.
5. Do not edit the existing OpenGL toolkit except for bug fixes. 634HD changes go in `HdToolkit` and its classes.
6. Shaders live in resource files, never in Java strings. Every new setting has a default that matches the previous behaviour.
7. No per-frame heap allocation in the render loop. Use LWJGL's `MemoryStack` or preallocated buffers.
8. Check GL errors with `KHR_debug` in debug builds where the driver supports it, and with `glGetError` after each pass otherwise.
9. Don't copy code from 117HD or RuneLite without checking its license and keeping the license header. Treat them as references.
10. Update the README when user-facing requirements change (JDK version, macOS flag, minimum GPU).

When a task needs a new image asset, generate it with the `/generate` skill following the rules under Generated assets.
