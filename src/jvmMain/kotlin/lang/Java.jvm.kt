package lang

@Suppress("NOTHING_TO_INLINE")
actual inline fun arraycopy(src: Any?, srcPos: Int, dest: Any?, destPos: Int, length: Int) {
    java.lang.System.arraycopy(src, srcPos, dest, destPos, length)
}

actual fun printlnErr(message: Any?) {
    java.lang.System.err.println(message)
}

actual fun flushOut() {
    java.lang.System.out.flush()
}

actual fun getBoolean(name: String?): Boolean = java.lang.Boolean.getBoolean(name)

actual fun getInteger(name: String?, defaultValue: Int): Int = java.lang.Integer.getInteger(name, defaultValue)

actual fun parseInt(s: String?): Int = java.lang.Integer.parseInt(s)

actual fun parseInt(s: String?, radix: Int): Int = java.lang.Integer.parseInt(s, radix)

actual fun parseLong(s: String?): Long = java.lang.Long.parseLong(s)

actual fun parseDouble(s: String?): Double = java.lang.Double.parseDouble(s)

actual fun parseBoolean(s: String?): Boolean = java.lang.Boolean.parseBoolean(s)

actual fun toHexString(value: Long): String = java.lang.Long.toHexString(value)

actual val BOOLEAN_TYPE: Class<*> = java.lang.Boolean.TYPE
actual val BYTE_TYPE: Class<*> = java.lang.Byte.TYPE
actual val CHAR_TYPE: Class<*> = java.lang.Character.TYPE
actual val SHORT_TYPE: Class<*> = java.lang.Short.TYPE
actual val INT_TYPE: Class<*> = java.lang.Integer.TYPE
actual val LONG_TYPE: Class<*> = java.lang.Long.TYPE
actual val FLOAT_TYPE: Class<*> = java.lang.Float.TYPE
actual val DOUBLE_TYPE: Class<*> = java.lang.Double.TYPE

actual fun halt(status: Int) {
    java.lang.Runtime.getRuntime().halt(status)
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
actual fun String.javaTrim(): String = (this as java.lang.String).trim()

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
actual fun String.javaSplit(regex: String): Array<String> = (this as java.lang.String).split(regex)

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
actual fun String.javaToLowerCase(): String = (this as java.lang.String).toLowerCase()

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
actual fun String.javaReplaceAll(regex: String, replacement: String): String = (this as java.lang.String).replaceAll(regex, replacement)

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
actual fun String.javaIndexOf(ch: Int, fromIndex: Int): Int = (this as java.lang.String).indexOf(ch, fromIndex)

actual fun Throwable.printStackTraceTo(writer: io.PrintWriter?) {
    this.printStackTrace(writer)
}

private object ResourceAnchor

actual fun readResource(path: String): ByteArray? =
    ResourceAnchor::class.java.getResourceAsStream(path)?.use { it.readBytes() }

actual fun requestResource(path: String) {
    // on the class path already
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
actual fun String.javaIntern(): String = (this as java.lang.String).intern()

actual fun decodeDefaultCharset(bytes: ByteArray?): String = String(bytes!!, java.nio.charset.Charset.defaultCharset())

actual val RUNTIME_CLASS: Class<*> = java.lang.Runtime::class.java

actual fun runtimeInstance(): Any = java.lang.Runtime.getRuntime()
