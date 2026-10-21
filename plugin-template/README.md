# Example plugin for the 634 client

A plugin to copy: a line of text on the game view, a `::hello` command, a "Hello" entry on every npc's
right-click menu, a "Value" entry on every item, a hotkey, a thread, and a counter in the plugin's
settings. The client's guide to plugins is [docs/PLUGINS.md](../docs/PLUGINS.md).

`src/main/kotlin/recipes` holds three more plugins, each a few dozen lines: attack and idle
timers, a ground item overlay with values, and item tags for the inventory and the bank. The guide's [cookbook](../docs/PLUGINS.md#cookbook) walks through them.
`./gradlew recipeJars` (with the same `-PapiJar` as `jar`) builds one jar each in `build/libs/recipes`,
with its icon from `src/main/resources/recipes/<id>.png`, ready for `~/.634-client/plugins`; the
example's jar carries the example alone.

## Building

Inside the client repository, after `./gradlew releaseApi` in the client (it puts the API jar in
`build/release`):

```bash
cd plugin-template
./gradlew jar
```

Anywhere else, say where the API jar from a [client release](https://github.com/2011Scape/634-client/releases) is
(an absolute path, or one relative to this folder):

```bash
./gradlew jar -PapiJar=/path/to/runetek5-plugin-api-<version>.jar
```

The plugin is `build/libs/example-plugin-0.1.jar`.

This build fetches the Kotlin Gradle plugin from the Gradle Plugin Portal and checks nothing
against a verification file, unlike the client's build: add `gradle/verification-metadata.xml`
(`./gradlew --write-verification-metadata sha256 jar`) if your plugin's build should.

## Trying it

Copy the jar into `~/.634-client/plugins` and start the client, or point the client at the build
folder: `java -jar runetek5-634-<version>.jar --plugins plugin-template/build/libs`, or from the
client repository `./gradlew runClient -Pclient.plugins=$PWD/plugin-template/build/libs` (against a
Void server on this computer). `::plugins` in the game's console (the `` ` `` key) lists what loaded.

## Making it yours

- `build.gradle.kts`: the manifest attributes name the plugin (`Plugin-Id`: 2 to 32 of `a-z`, `0-9`
  and `-`) and its class. `Plugin-Api-Version` is the `PluginApi.VERSION` the plugin was written
  against: keep it at the lowest version that has everything the plugin uses, so that older clients
  that have it still load the plugin, and never lower, or an older client loads it and it fails inside.
- `src/main/kotlin`: the plugin. Java works too: the API is plain interfaces.
- `src/main/resources/icon.png`: the plugin's icon in the Plugins window, 25x25 (up to 64x64, scaled),
  named by the manifest's `Plugin-Icon`; drop both for no icon.
- Keep the API and the Kotlin standard library `compileOnly`, and the Kotlin version at or below the
  client's: a jar that bundles either is refused.
