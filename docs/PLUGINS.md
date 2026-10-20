# Plugins

How to add to the client without changing it: a plugin is a jar in a folder, written against a
small, stable API, that reads the game, draws on it, adds menu entries and commands, and keeps its
own settings. The experience drops (`XpDrops`) and the idle notifier (`IdleNotifier`) are two, built in, so the client's own code shows the
way.

## Contents

- [What a plugin can do](#what-a-plugin-can-do)
- [Installing plugins](#installing-plugins)
- [Writing a plugin](#writing-a-plugin)
- [The API](#the-api)
- [Building](#building)
- [Trouble](#trouble)
- [How it works inside the client](#how-it-works-inside-the-client)
- [What comes next](#what-comes-next)

## What a plugin can do

A plugin follows the rule RuneLite's do: it **reads** the game and **shows** things; it does not play
the game for you.

- Read: the players, npcs and items on the ground in the scene, and what each actor is doing (its
  animation, graphic, target, facing, hit splats, overhead text and icons); skills, player variables
  (varps and varbits), inventories, interface components; the item, npc and location types from the
  cache (names, options, sizes, values); the locations and the movement flags of any tile; where any
  of it is on the screen.
- Draw: on the game view every frame (under the interfaces), or over the whole frame.
- Add: entries to the right-click menu of a tile, npc, player, ground item or location, with an action
  of its own; console commands (`::name`); lines in the chat box and the developer console.
- Hear: every client cycle and game tick, every stat update, variable change, chat line and inventory
  update, the menu being built and clicked, keys and mouse buttons, logins and logouts, the graphics
  mode changing; every npc, player and ground item appearing and leaving, every hit, animation,
  change of target and line of overhead text, and every projectile and graphic.
- Keep: settings of its own, saved beside the plugins.

It cannot send anything to the server, click or walk for you, or change the game's files. That is
the API's policy, not a sandbox: a plugin is code running in the client with everything the client
can do, so **only install plugins from people you trust**, as you would any program.

## Installing plugins

Put the plugin's jar in the plugins folder, `~/.634-client/plugins` (`C:\Users\<you>\.634-client\plugins`
on Windows), and start the client. The folder is made on the first start. Another folder:
`--plugins <dir>` on the client's command line (in the Void launcher: Settings, Developer, *Extra
client arguments*); `--no-plugins` loads none.

In the game, the developer console (the `` ` `` key) has:

| Command | What it does |
|---|---|
| `::plugins` | Lists every plugin: id, name, version, whether it is on, off or failed (and why), and its jar. |
| `::plugin <id> off` | Turns a plugin off, now and on later starts (kept in `~/.634-client/plugins.properties`). A plugin that is off runs nothing: its class is not even initialised until it is turned on. |
| `::plugin <id> on` | Turns it on again, or tries again one that failed. |

A plugin's own settings are in `~/.634-client/plugin-config/<id>.properties`.

A plugin that throws is turned off on the spot, with a line in the console and the chat box
("Plugin X was disabled: ..."), and the game goes on. `::plugin <id> on` tries it again; the
stack trace is in the client's log (the launcher keeps it in `~/.void/logs`).

## Writing a plugin

Start from [`plugin-template/`](../plugin-template): copy the folder, and change the manifest in
`build.gradle.kts` and the class in `src/main/kotlin`. In short, a plugin is:

1. A class with a public constructor without arguments that implements `Plugin`:

   ```kotlin
   class MyPlugin : Plugin {
       override fun start(context: PluginContext) {
           context.addOverlay { g, view -> g.text(view.x + 8, view.y + 20, "Hello", 0xFFFFFF00.toInt(), 0xFF000000.toInt()) }
           context.registerCommand("hello") { _, args -> context.client().chatMessage("Hello, " + args.joinToString(" ")) }
           context.events().subscribe(Events.STAT_CHANGED) { event -> /* ... */ }
       }
   }
   ```

   Java works just as well: the API is interfaces and plain classes, and `stop()` has a default.

2. A jar whose manifest names it:

   | Attribute | |
   |---|---|
   | `Plugin-Class` | The class, with its package. Needed. |
   | `Plugin-Id` | 2 to 32 of `a-z`, `0-9` and `-`, starting with a letter: the name in commands and files. Needed. |
   | `Plugin-Name` | The name the player sees. |
   | `Plugin-Version` | The plugin's version, any text. |
   | `Plugin-Api-Version` | The `PluginApi.VERSION` it was written against (1 if missing). The client refuses a plugin that needs a newer API than it has. |
   | `Plugin-Author`, `Plugin-Description` | For `::plugins`. |

   One plugin a jar, and the jar is all of it: one that carries the client's classes (`com/jagex/...`)
   or the Kotlin standard library (`kotlin/...`) is refused, since the client has them, and so is one
   whose manifest has a `Class-Path` or `Multi-Release: true`, which would load classes from outside
   that check. The check is on the jar's entry names; it is a guard against a shaded build, not a sandbox.

### What happens when

- `start(context)` runs on the game thread once the cache's types, the interfaces and the player's
  variables exist, before the login screen. The toolkit's fonts may not exist yet
  (`Graphics.fontReady()`); the first `ToolkitChanged` event says when they do, and so does every
  change of graphics mode, after which every font and sprite is new.
- Every callback runs on the game thread: listeners, overlays, commands, menu actions. Keep the
  per-frame ones quick, and the "2010 idiom" of the client's own features suits them: plain arrays
  and loops, no allocation in a loop that runs every frame.
- The event objects of the events that happen every frame or cycle (`ClientTick`, `GameTick`,
  `ViewportDraw`, `FrameDraw`, `KeyPressed`, `MouseClicked`, `MenuEntryAdded`, `MenuOptionClicked`,
  `ConsoleCommand`) are reused: read them in the listener, keep nothing.
- `stop()` runs when the player turns the plugin off or the client quits. Everything the plugin
  registered through its context is removed for it afterwards, and the context registers nothing
  more: a listener or thread the plugin forgot cannot bring it back.
- A throw anywhere disables the plugin ([above](#installing-plugins)).

### The pieces

| Through the context | |
|---|---|
| `client()` | `ClientApi`: the game's state, world-to-screen projection, chat and console output, `addMenuEntry`. |
| `events()` | `subscribe(Events.X, listener)` and `unsubscribe`. |
| `config()` | `PluginConfig`: `get`/`set` (and `Int`/`Boolean`), `save()`. |
| `addOverlay(overlay)` | Draws on the game view every frame, after the scene and the client's own overlays, before the interfaces. The minimap, the orbs and the chat box cover what is under them; the client's experience drops start 300 pixels left of the view's right edge for that reason. |
| `registerCommand(name, handler)` | `::name` in the console, after the client's own commands have had their turn; a plugin's command is not sent to the server, and Tab (completion) never runs it. False when the name is taken or the client's own. |
| `registerMenuAction(action)` | A click type to give `client().addMenuEntry(label, clickType)` from a `MenuEntryAdded` listener. The action gets the target kind, the scene tile (an npc's or player's south-west tile) and the id (npc index, player index, item id, location id). |
| `log(message)` | Standard error, with the plugin id. |

`Graphics` (in overlays and the draw events) draws lines, rectangles and text in the client's small
font, in screen pixels, with ARGB colours whose alpha blends. World to screen: `ClientApi.projectTile`,
`projectPoint` (scene units, 512 a tile) and `projectActor` write the view-relative point into an
`IntArray(2)` and say whether it is on screen; add the viewport's `x` and `y` to draw there.

## The API

The API is the package `com.jagex.game.runetek5.plugin.api`, published with every client release as
`runetek5-plugin-api-<version>.jar` with a `-sources.jar` beside it; its KDoc is the reference. The jar
is also inside the client jar, which is what runs the plugin.

`PluginApi.VERSION` is the API's version, and goes up with every release that adds to the API (its
KDoc lists what each version brought). Nothing is removed or changed in a way that breaks an older
plugin: one built against version N runs on every client whose API is N or newer, and a client
refuses a plugin that needs a newer API than its own, with a message to update the client, rather
than failing inside it when the plugin reaches for what is not there.

## Building

The template's `build.gradle.kts` is the whole of it:

- `compileOnly(files(apiJar))`, where the jar comes from a release or from `./gradlew releaseApi` in
  the client repository (`build/release`); `-PapiJar=<path>` says where (absolute, or relative to
  the template folder: Gradle resolves it against the project, not against where you ran it).
- `compileOnly(kotlin("stdlib"))` and `kotlin.stdlib.default.dependency=false`: the client has the
  standard library, and a plugin that bundles one is refused. The plugin's Kotlin version must not
  be newer than the client's (2.2.20 at the time of writing).
- A plain `jar` task with the manifest attributes. No shading.

To try it from the client repository: `./gradlew runClient -Pclient.plugins=$PWD/plugin-template/build/libs`
(against a Void server on this computer), or the launcher's extra argument `--plugins <dir>`.
`--plugins` is kept across the client's own restart on Linux (it restarts itself once to set the
native library path).

Plugins are not loaded in the smoke test (`-Pclient.test=smoke`) unless `client.plugins` names a
folder, so the test sees the same client every time.

## Trouble

| | |
|---|---|
| `::plugins` says `failed: the manifest has no Plugin-Class` | The jar's manifest lacks the attributes; see the template's `tasks.jar`. |
| `failed: it needs plugin API version N` | The plugin was built against a newer client; update the client. |
| `failed: the jar carries kotlin/...` | The build bundled the standard library or the API; make both `compileOnly`. |
| `failed: fixture.X does not implement ...Plugin` | `Plugin-Class` names the wrong class. |
| `Plugin X was disabled: java.lang.NoSuchMethodError` | The plugin was compiled against another Kotlin version than the client's, or another API version. |
| Nothing draws | `Graphics.fontReady()` is false until the first `ToolkitChanged`; or the drawing is under the minimap or chat box. |
| `plugins.properties`, `plugin-config` not written | No home folder, or a smoke-test run (`client.test`). |

## How it works inside the client

For the client's own developers. The runtime is `com.jagex.game.runetek5.plugin` in `commonMain`,
so it is common code (no `java.*`; the browser build compiles it, though a browser loads only the
built-in plugins):

- `PluginRuntime` is what the game calls. Each hook in the original code (every one commented "Not
  part of the original client", as [FEATURES.md](FEATURES.md) asks) is one line: `drawViewport` from
  `ClientState.drawGameArea`, `statChanged` and `containerChanged` from `PacketDecoder`, `gameTick`
  and `varpChanged` from `GameTick`, `clientTick`, `keyPressed`, `mouseClicked`, `drawFrame` and
  `stop` from `client`, `stepChanged` from `MainLogicStepTransition`, `toolkitChanged` from
  `ToolkitRebuild`, `chatMessage` from `ChatLineWriter`, `consoleCommand` from
  `ClientCommandProcessor` (after the client's own commands, before the line goes to the server),
  `menuClicked` from `MiniMenuActionExecutor`, `menuTarget` from
  `ViewportMiniMenuPicking`, `MiniMenuBuilder` and `MiniMenu`, and `start` from `LoadingStageWork`
  at stage 15.
- `EventBusImpl` dispatches from arrays, allocating nothing; a listener that throws is reported to
  the runtime, which disables its plugin and removes everything it registered.
- `ClientApiImpl` is the façade over the client's statics; `PathingEntity`, `NPCEntity`,
  `PlayerEntity`, `ItemContainer` and `Component` implement the API's `Actor`, `Npc`, `Player`,
  `ItemContainer` and `Widget` themselves, so the API hands out the client's objects with no wrapping.
- `ToolkitGraphics` draws through the active toolkit and `Renderers.FONT_RENDERER`.
- Plugins' menu entries take the click types `MiniMenuAction.PLUGIN_FIRST` to `PLUGIN_LAST`; the
  target kind rides in the high half of the entry's `param0`, the tile in `param1` and the low half.
- `expect fun discoverPlugins()` is the platform seam: the JVM's actual (`plugin/loader`) reads the
  jars in the plugins folder, one `URLClassLoader` each with the client as parent; the browser's
  returns nothing. `BuiltinPlugins` lists the plugins compiled in, which every platform runs.
- The API is the Gradle subproject `api/`, a Kotlin Multiplatform library with no dependencies that
  the client's `commonMain` depends on, so API code cannot reach into the client. `releaseApi`
  copies its jar beside the release jar.

A new extra for the client should be a built-in plugin on this API where the API suffices, as
`XpDrops` is; where it does not, extend the API (additively, raising `PluginApi.VERSION`) rather
than adding another one-off hook.

## What comes next

Not built yet, in the order it is likely to come: a Plugins page in Graphics Options to turn plugins
on and off and edit their settings; a plugin hub in the Void launcher (a signed list of plugins to
install and update, verified by SHA-256); plugins in the browser client; dependencies between
plugins and reloading one without a restart; the interfaces in detail (children, items, sprites,
options) and their opening and closing; menu entries on interface components and the power to edit
the menu, cancel a click or a key; sprites, icons and polygons in overlays; the camera and the
minimap; sounds; and a way for work on another thread to come back to the game thread.
