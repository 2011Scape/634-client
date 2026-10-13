/*
 * Not part of the original client: the platform seam for the client's long-lived loops, from
 * void-client's multiplatform-shim branch.
 *
 * The main loop (GameShell.run) and the worker loops (SignLink.run, the resource loader) are
 * infinite non-suspending `while` loops. That is fine on the JVM, where each owns a real thread,
 * but a browser has one event loop and such a loop never yields. runLoop moves the loop construct
 * itself behind expect/actual: the JVM actual is the original blocking loop, a browser's will be a
 * setTimeout chain.
 *
 * prelude, onError and onFinally exist because the call sites wrap their loop in try/catch/finally,
 * and in a browser the finally must not run when the first step is merely scheduled.
 *
 * prelude: one-time setup; returning false skips the loop (onFinally still runs).
 * step: one iteration; returning false ends the loop.
 */
expect fun runLoop(
    prelude: () -> Boolean,
    step: () -> Boolean,
    onError: (Throwable) -> Unit,
    onFinally: () -> Unit,
)

/*
 * Whether SignLink runs the tasks it is given on the caller's thread instead of its worker thread.
 * Callers submit a task and then wait for its result with sleeps; in a browser the worker never gets
 * to run during those sleeps, so it would deadlock. On the JVM the worker thread runs them, as in
 * the original.
 */
expect val executeWorkerTasksInline: Boolean

/*
 * Whether a thread may block waiting for another (Object.wait). True on the JVM. Where there are no
 * threads a worker's wait would spin for ever, so a worker loop checks this first and, when false,
 * ends its turn of runLoop instead; the next turn finds the work the wait was for.
 */
expect val canBlock: Boolean

/*
 * How many JS5 blocks Js5WorkerThread reads in one call. The original reads at most 100 (about
 * 51 KB a tick): on the JVM the socket can refill while the loop runs, and the cap keeps one call
 * from starving the tick. In a browser the WebSocket delivers only between turns, so available()
 * cannot grow during the loop and reading all of it is already bounded; the cap only slows down
 * filling the cache.
 */
expect val js5ReadsPerTick: Int

/*
 * Whether OpenGL can draw from client-side vertex arrays (glVertexPointer with a memory address).
 * True on the JVM. The browser's WebGL 2 emulator draws only from buffer objects, so there the Java
 * OpenGL toolkit keeps its changing (dynamic) vertex data in buffer objects too, as it does on AMD.
 */
expect val clientArraysAvailable: Boolean

/*
 * Whether text drawn into an offscreen image comes out antialiased. False on the JVM, where AWT draws
 * it aliased. A browser canvas always antialiases (fillText), which leaves grey edge pixels that an
 * exact-black colour key misses (HelveticaFont).
 */
expect val textAntialiased: Boolean

/*
 * Whether a class's fields can be listed (Class.getDeclaredFields). True on the JVM; a browser has
 * no reflection, so there GamePreferences sizes its save without counting its fields.
 */
expect val classFieldsAvailable: Boolean

/*
 * The version line GlSmaaFilter starts its shaders with. "#version 130" on the JVM. In a browser it
 * is empty: the WebGL 2 emulator then translates the shaders to GLSL ES 3.00 (it leaves a source
 * that names its own version as it is, and WebGL has no GLSL 1.30).
 */
expect val smaaGlslVersion: String

/*
 * At most how many game ticks one frame catches up when client.fps paces the frames
 * (ClientFeatures.ticksDue). 10 on the JVM. In a browser a slow frame that then runs ten ticks makes
 * the next frame slower still; there it is 4, and further ticks are dropped.
 */
expect val maxTicksPerFrame: Int

/*
 * Whether the software renderer remembers textures within a frame. False on the JVM, which looks a
 * texture up for every textured triangle as the original does. In a browser that lookup (a lock and a
 * hash table keyed by Long, which Kotlin/JS emulates) cost more than drawing the triangle; there the
 * Rasterizer keeps the texture its slot last used, and JavaToolkit a front cache by id, both cleared
 * at every JavaToolkit.tick, where animated textures change.
 */
expect val rememberTextures: Boolean

/*
 * Whether the software renderer takes its browser shortcuts: the hottest span (Rasterizer.
 * drawTexturedSpanRgb) through a twin loop that keeps fields in locals and truncates with spanToInt,
 * and the depth and colour clears (JavaToolkit.ya, aa) through the typed array's native fill. False
 * on the JVM, which runs the original loops. Kotlin/JS reads a field and checks it for null on every
 * use, and turns every Float.toInt into a call; the shortcuts do neither, with the same results.
 */
expect val fastRaster: Boolean

/*
 * value.toInt() for a span value known to fit an Int (a texel coordinate, a colour channel). The JVM
 * truncates as toInt does; a browser uses value | 0, which wraps instead of saturating outside the Int
 * range, where toInt is a function call per pixel.
 */
expect fun spanToInt(value: Float): Int

/*
 * Whether Long is emulated, so that code can keep a 64-bit key as two Ints instead: true in a
 * browser, where Kotlin/JS makes every Long operation an object allocation; false on the JVM, which
 * runs the original Long code. JavaModel's face sort uses IntPairSort then.
 */
expect val emulatedLong: Boolean

/*
 * How long the current turn of runLoop has run, in milliseconds, or -1 where it is not measured (the
 * JVM). SmokeTest's benchmark reads it at the end of a frame for the frame's work, apart from waiting.
 */
expect fun turnElapsedMs(): Double

/*
 * Not part of the original client: a login seed (the ISAAC and XTEA keys sent in the RSA block), 0 to
 * 99999998 as the original's util.random() * 9.9999999E7, but from the platform's cryptographic random
 * source, so it cannot be predicted from earlier output of the shared generator.
 */
expect fun secureSeed(): Int
