actual inline fun <T> platformLock(lock: Any, block: () -> T): T = synchronized(lock, block)


@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
actual fun lockWait(lock: Any?) {
    (lock as java.lang.Object).wait()
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
actual fun lockNotify(lock: Any?) {
    (lock as java.lang.Object).notify()
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
actual fun lockNotifyAll(lock: Any?) {
    (lock as java.lang.Object).notifyAll()
}
