# Example plugin for the 634 client

A plugin to copy: a line of text on the game view, a `::hello` command, a "Hello" entry on every npc's
right-click menu, and a counter in the plugin's settings. The client's guide to plugins is
[docs/PLUGINS.md](../docs/PLUGINS.md).

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

## Trying it

Copy the jar into `~/.634-client/plugins` and start the client, or point the client at the build
folder: `java -jar runetek5-634-<version>.jar --plugins plugin-template/build/libs`, or from the
client repository `./gradlew runClient -Pclient.plugins=$PWD/plugin-template/build/libs` (against a
Void server on this computer). `::plugins` in the game's console (the `` ` `` key) lists what loaded.

## Making it yours

- `build.gradle.kts`: the manifest attributes name the plugin (`Plugin-Id`: 2 to 32 of `a-z`, `0-9`
  and `-`) and its class.
- `src/main/kotlin`: the plugin. Java works too: the API is plain interfaces.
- Keep the API and the Kotlin standard library `compileOnly`, and the Kotlin version at or below the
  client's: a jar that bundles either is refused.
