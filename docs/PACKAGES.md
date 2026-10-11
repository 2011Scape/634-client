# Code layout

The client's code lives in three places:

| Where | What |
|---|---|
| `com/jagex/game/runetek5/**` | Everything that can live in a package, grouped by subsystem (`config`, `entity`, `js5`, `network`, `scene`, `graphics`, `client`, ...). |
| The root package (164 files) | The native classes, `client`, the software renderer and loading screen that call native code, and six holders of statics those classes use. |
| `jaclib`, `jaggl`, `jagdx`, `jagtheora`, `jagex3` | The Java side of the native libraries. |

A class in a package cannot import a root-package class, so code that touches a native class
belongs in the root package. The statics the whole client uses (the cycle and tick counters, the
main logic step, the active toolkit) are in `client/ClientState`; every other static sits in the
class it serves.

## Where new code goes

- **New code for a native class** goes in the root package.
- **Anything else** goes in the `runetek5` package of the class it extends or works with.
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
