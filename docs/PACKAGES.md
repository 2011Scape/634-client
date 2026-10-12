# Code layout

The client is a Kotlin Multiplatform project ([MULTIPLATFORM.md](MULTIPLATFORM.md)). Its code is
in two source sets:

| Source set | What |
|---|---|
| `src/commonMain/kotlin` | The game, and everything it needs that is not tied to a platform. Common code names no `java.*` or `javax.*` package; the browser target compiles it with the common standard library alone, so `./gradlew build` fails on a JVM API there. |
| `src/jvmMain/kotlin` | The JVM side: the native classes, the launcher `RuneTek5`, and the actuals of the shims and seams below. |
| `src/jsMain/kotlin` | The browser side ([MULTIPLATFORM.md](MULTIPLATFORM.md#running-the-browser-client)): the entry point `main.js.kt`, browser actuals of every shim and seam, the WebSocket `net.Socket`, the IndexedDB-backed file system in `io`, the class registry behind `lang.forName`, and the native classes, which throw `NativeUnavailableException` (an `Exception`, so the client falls back as from a missing library) except `oa`, which draws with the client's software renderer, `jaggl.OpenGL`, a WebGL 2 emulator (from void-client) under the Java OpenGL toolkit, and the `jaclib.memory` classes it uses. |

Inside `commonMain` the game keeps its layout:

| Where | What |
|---|---|
| `com/jagex/game/runetek5/**` | Everything that can live in a package, grouped by subsystem (`config`, `entity`, `js5`, `network`, `scene`, `graphics`, `client`, ...). |
| The root package | `client`, the software renderer, the loading screens, `PacketDecoder`, `CS2Interpreter` and the other classes the deobfuscator left there, and the platform seams (`PlatformLoop`'s `runLoop`, `Lock`'s `withLock`, `Clock`, `SoftRef`, `BigInt`). |
| `awt`, `applet`, `io`, `lang`, `net`, `util`, `sound` | The Java API shims: `expect` declarations of the JDK classes the game uses (`awt.Canvas`, `io.RandomAccessFile`, `lang.Thread`, ...), from void-client's `multiplatform-shim` branch plus what this client needs. On the JVM most are type aliases of the JDK classes. A JDK static is a top-level function (`lang.sleep`, `awt.getDefaultToolkit`), and a JDK public field an extension property (`Rectangle.x`, `Dimension.width`). |
| `jaclib`, `jaggl`, `jagdx`, `jagtheora`, `jagex3`, and the native root classes (`a`, `oa`, `xa`, ...) | `expect` declarations of the native classes, so common code can use them; generated from the JVM classes, which are the `actual`s. |
| `org/openrs2/deob/annotation` | The `@OriginalClass`, `@OriginalMember`, `@OriginalArg` and `@Pc` annotations, declared here with the OpenRS2 artifact's names, targets and class retention, since that artifact is JVM-only. |

In `jvmMain`, besides the actuals: the native classes themselves, the Microsoft JVM adapters,
`NativeLibraryOwner` (native library loading, which reflects into the JVM's class loader),
`ProxyManager` and `ByteBufferArrayWrapper` (loaded by name), `WindowIcons`, and small JVM files
for what common code reaches through an `expect` function: ImageIO decoding (`JpegPixels`), the
packet size file and `Graphics2D` scaling (`FeaturePlatform`), SmokeTest's PNG files, and SignLink's
reflection (`SignLinkPlatform`).

The shims keep the JDK's behaviour on the JVM, which matters where Kotlin's own function differs:
`lang.parseInt` throws `NumberFormatException` on null like `Integer.parseInt`, `javaTrim` strips
every character up to a space like `String.trim`, `lang.round` returns 0 for NaN like `Math.round`.
Other `Math` functions are `kotlin.math`'s, which compile to the same `java.lang.Math` calls, and
`synchronized` is `withLock`, which inlines to the same monitor. The statics the whole client uses
(the cycle and tick counters, the main logic step, the active toolkit) are in
`client/ClientState`; every other static sits in the class it serves.

## Where new code goes

- **New game code** goes in `commonMain`, in the `runetek5` package of the class it extends or
  works with. A packaged class may import a root-package one (`import client`); the old rule that
  it cannot was Java's.
- **A JDK class or method common code needs** goes in the shims: an `expect` declaration in the
  shim package, and its JVM `actual`, usually a type alias. Add only what the code uses; the browser
  target will have to implement each one.
- **Code that only the JVM can run** goes in `jvmMain`, behind an `expect` function in `commonMain`,
  with a browser actual in `jsMain` (often one that throws an `Exception` the caller handles).
- **A loop that waits for another thread** goes through `runLoop`, and checks `canBlock` before it
  waits (`PlatformLoop.kt`): in a browser there are no threads, and a wait would spin for ever.
- **Arrays in the browser start out null**: Kotlin/JS compiles `arrayOfNulls(n)` to a bare JS
  `Array(n)` whose slots are `undefined`, which `==` treats as null but `===` does not;
  `webpack.config.d/null-arrays-loader.js` fills them with null in the bundle, so identity checks
  agree with the JVM. Nothing in the code has to change for it.
- **A class the client creates by name** (`lang.forName(...).getDeclaredConstructor()`) needs an
  entry in `jsMain/kotlin/lang/ClassRegistry.js.kt` to exist in a browser.
- **New code for a native class** goes in its `jvmMain` file, with the `expect` side updated if
  common code calls it.
- **A feature** goes in `com/jagex/game/runetek5/client/features`, with its hooks where
  [FEATURES.md](FEATURES.md) says.

## Native code is frozen

The native libraries (`jaggl`, `jagdx`, `jaclib`, `jagtheora`, `jagex3`) ship as prebuilt binaries
in the game cache. A native library finds its Java side by name: a JNI symbol encodes the package,
the class and the method, and native code looks fields and methods up by their literal names. A
rename there breaks at runtime, not at compile time. So never rename, move or change the signature
of:

- a class that declares a `native` method, or its package;
- a `native` method;
- a field a native library reads or writes, such as the `peer` handles;
- the Java methods the software renderer calls back by name: `oa.c(S)`, `oa.WA(S)`, `oa.g()` and
  `oa.OA()`;
- a root-package class with a one- or two-letter name, such as `a`, `ba` or `oa`;
- fields named `nativeid`, `peer`, `bar`, `width`, `height` and the other names the native
  libraries look up.

Code that calls these classes can change freely. The `@Original*` and `@Pc` annotations record
each declaration's place in the 2010 jar; leave them as they are.
