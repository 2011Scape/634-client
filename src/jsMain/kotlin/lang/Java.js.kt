package lang

import haltPage

/* The browser side of lang/Java.kt: the java.lang statics, with the JDK's behaviour. */

@Suppress("NOTHING_TO_INLINE")
actual inline fun arraycopy(src: Any?, srcPos: Int, dest: Any?, destPos: Int, length: Int) {
    arraycopyImpl(src, srcPos, dest, destPos, length)
}

/* System.arraycopy: null checks, bounds checks, and copying as if through a temporary array. */
fun arraycopyImpl(src: Any?, srcPos: Int, dest: Any?, destPos: Int, length: Int) {
    if (src == null || dest == null) throw NullPointerException()
    if (length < 0 || srcPos < 0 || destPos < 0) throw ArrayIndexOutOfBoundsException("arraycopy: length " + length)
    val end = srcPos + length
    when {
        src is ByteArray && dest is ByteArray -> src.copyInto(dest, destPos, srcPos, end)
        src is IntArray && dest is IntArray -> src.copyInto(dest, destPos, srcPos, end)
        src is ShortArray && dest is ShortArray -> src.copyInto(dest, destPos, srcPos, end)
        src is CharArray && dest is CharArray -> src.copyInto(dest, destPos, srcPos, end)
        src is LongArray && dest is LongArray -> src.copyInto(dest, destPos, srcPos, end)
        src is FloatArray && dest is FloatArray -> src.copyInto(dest, destPos, srcPos, end)
        src is DoubleArray && dest is DoubleArray -> src.copyInto(dest, destPos, srcPos, end)
        src is BooleanArray && dest is BooleanArray -> src.copyInto(dest, destPos, srcPos, end)
        src is Array<*> && dest is Array<*> -> {
            @Suppress("UNCHECKED_CAST")
            (src as Array<Any?>).copyInto(dest as Array<Any?>, destPos, srcPos, end)
        }
        else -> throw ArrayStoreException("arraycopy: type mismatch")
    }
}

class ArrayStoreException(message: String?) : RuntimeException(message)

actual fun printlnErr(message: Any?) {
    console.error(message.toString())
}

actual fun flushOut() {
}

actual fun getBoolean(name: String?): Boolean {
    if (name == null) return false
    return getProperty(name).equals("true", ignoreCase = true)
}

actual fun getInteger(name: String?, defaultValue: Int): Int {
    if (name == null) return defaultValue
    val value = getProperty(name) ?: return defaultValue
    return try {
        decodeInt(value)
    } catch (ex: NumberFormatException) {
        defaultValue
    }
}

/* Integer.decode: an optional sign, then 0x, 0X or # for hex, 0 for octal, else decimal. */
private fun decodeInt(s: String): Int {
    var i = 0
    var negative = false
    if (s.startsWith("-")) { negative = true; i = 1 } else if (s.startsWith("+")) { i = 1 }
    var radix = 10
    if (s.startsWith("0x", i) || s.startsWith("0X", i)) { radix = 16; i += 2 }
    else if (s.startsWith("#", i)) { radix = 16; i += 1 }
    else if (s.startsWith("0", i) && s.length > i + 1) { radix = 8; i += 1 }
    val v = (if (negative) "-" else "") + s.substring(i)
    return parseInt(v, radix)
}

actual fun parseInt(s: String?): Int = parseInt(s, 10)

actual fun parseInt(s: String?, radix: Int): Int {
    if (s == null) throw NumberFormatException("Cannot parse null string: null")
    return s.toIntOrNull(radix) ?: throw NumberFormatException("For input string: \"" + s + "\"")
}

actual fun parseLong(s: String?): Long {
    if (s == null) throw NumberFormatException("Cannot parse null string: null")
    return s.toLongOrNull() ?: throw NumberFormatException("For input string: \"" + s + "\"")
}

actual fun parseDouble(s: String?): Double {
    if (s == null) throw NullPointerException()
    var t = s.javaTrim()
    if (t.endsWith("d") || t.endsWith("D") || t.endsWith("f") || t.endsWith("F")) t = t.substring(0, t.length - 1)
    return t.toDoubleOrNull() ?: throw NumberFormatException("For input string: \"" + s + "\"")
}

actual fun parseBoolean(s: String?): Boolean = s != null && s.equals("true", ignoreCase = true)

actual fun toHexString(value: Long): String = value.toULong().toString(16)

actual val BOOLEAN_TYPE: Class<*> = Boolean::class.jClass
actual val BYTE_TYPE: Class<*> = Byte::class.jClass
actual val CHAR_TYPE: Class<*> = Char::class.jClass
actual val SHORT_TYPE: Class<*> = Short::class.jClass
actual val INT_TYPE: Class<*> = Int::class.jClass
actual val LONG_TYPE: Class<*> = Long::class.jClass
actual val FLOAT_TYPE: Class<*> = Float::class.jClass
actual val DOUBLE_TYPE: Class<*> = Double::class.jClass

/* The page cannot exit: it reports the end and stops the client (main.js.kt), then unwinds the caller. */
actual fun halt(status: Int) {
    haltPage(status)
    throw HaltSignal(status)
}

/* Thrown by halt to leave the code that called it; runLoop drops it once halted is set. */
class HaltSignal(status: Int) : Error("halt($status)")

actual fun String.javaTrim(): String = this.trim { it <= ' ' }

actual fun String.javaSplit(regex: String): Array<String> {
    val parts = this.split(Regex(regex))
    if (parts.size == 1) return arrayOf(this)
    var end = parts.size
    while (end > 0 && parts[end - 1].isEmpty()) end--
    return parts.subList(0, end).toTypedArray()
}

actual fun String.javaToLowerCase(): String = this.lowercase()

actual fun String.javaReplaceAll(regex: String, replacement: String): String = Regex(regex).replace(this, replacement)

actual fun String.javaIndexOf(ch: Int, fromIndex: Int): Int {
    val from = if (fromIndex < 0) 0 else fromIndex
    if (from >= this.length) return -1
    if (ch < 0x10000) return this.indexOf(ch.toChar(), from)
    val c = ch - 0x10000
    val pair = charArrayOf((0xD800 + (c shr 10)).toChar(), (0xDC00 + (c and 0x3FF)).toChar()).concatToString()
    return this.indexOf(pair, from)
}

actual fun Throwable.printStackTraceTo(writer: io.PrintWriter?) {
    writer!!.println(this.stackTraceToString())
}

/*
 * Class path resources the page fetched before the client started (main.js.kt: the SMAA files, with
 * client.smaa); any other path is not found, which the callers handle.
 */
internal val pageResources: MutableMap<String, ByteArray> = mutableMapOf()

actual fun readResource(path: String): ByteArray? = pageResources[path]

/* The paths fetched or being fetched: each asked for once. */
private val requestedResources: MutableSet<String> = mutableSetOf()

actual fun requestResource(path: String) {
    if (pageResources.containsKey(path) || !requestedResources.add(path)) {
        return
    }
    // from beside the bundle, the class path's root being the page's folder ("/shortest-path/x" is "shortest-path/x")
    val fetched: dynamic = kotlinx.browser.window.fetch(path.removePrefix("/"))
    fetched.then({ response: dynamic ->
        if (response.ok == true) {
            (response.arrayBuffer() as kotlin.js.Promise<org.khronos.webgl.ArrayBuffer>).then { buffer ->
                pageResources[path] = org.khronos.webgl.Int8Array(buffer).unsafeCast<ByteArray>()
            }
        }
    }, { _: dynamic -> })
}

actual fun String.javaIntern(): String = this

actual fun decodeDefaultCharset(bytes: ByteArray?): String = bytes!!.decodeToString()

/* Runtime's methods are reached by reflection, which the browser has not; the callers fall back. */
actual val RUNTIME_CLASS: Class<*> = PlatformRuntime::class.jClass

actual fun runtimeInstance(): Any = PlatformRuntime
