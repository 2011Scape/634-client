// JS is single-threaded and this block is synchronous (not suspend), so no other coroutine
// can interleave mid-block: run-to-completion execution already gives the mutual exclusion
// that `synchronized` provides on the JVM. No lock is needed here.
actual inline fun <T> platformLock(lock: Any, block: () -> T): T = block()

// With one thread nothing else can run while a caller waits, so a wait returns at once; callers
// loop on their condition, and the worker loops run their requests inline (executeWorkerTasksInline).
actual fun lockWait(lock: Any?) {
}

actual fun lockNotify(lock: Any?) {
}

actual fun lockNotifyAll(lock: Any?) {
}
