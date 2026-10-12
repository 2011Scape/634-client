/*
 * Not part of the original client: what the browser's native classes throw (milestone 4). The
 * client meets a missing native library on the JVM too, and falls back from it; this is an
 * Exception, never TODO(), so its catch blocks still work.
 */
class NativeUnavailableException(what: String) : RuntimeException("native code is not available in a browser: " + what)

/* Throws NativeUnavailableException; typed Nothing so it can stand for any constructor argument. */
fun nativeUnavailable(): Nothing = throw NativeUnavailableException("constructor")
