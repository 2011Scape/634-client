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
