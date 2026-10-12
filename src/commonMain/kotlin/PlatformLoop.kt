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
