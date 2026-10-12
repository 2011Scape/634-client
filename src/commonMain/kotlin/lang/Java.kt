package lang

/*
 * Not part of the original client: java.lang statics the client calls that Kotlin's common library
 * has no exact equivalent of (milestone 3). On the JVM each is the JDK's own, so behaviour is as
 * before: parseInt(null) still throws NumberFormatException, getBoolean ignores case, and so on.
 */

/* System.arraycopy; inline, so on the JVM the bytecode is the same call as before. */
expect inline fun arraycopy(src: Any?, srcPos: Int, dest: Any?, destPos: Int, length: Int)

/* System.err.println. */
expect fun printlnErr(message: Any?)

/* System.out.flush. */
expect fun flushOut()

/* Boolean.getBoolean: whether the system property is "true", ignoring case. */
expect fun getBoolean(name: String?): Boolean

/* Integer.getInteger. */
expect fun getInteger(name: String?, defaultValue: Int): Int

expect fun parseInt(s: String?): Int

expect fun parseInt(s: String?, radix: Int): Int

expect fun parseLong(s: String?): Long

expect fun parseDouble(s: String?): Double

expect fun parseBoolean(s: String?): Boolean

/* Long.toHexString: unsigned. */
expect fun toHexString(value: Long): String

/* The primitive types' classes (Integer.TYPE and so on), for reflection. */
expect val BOOLEAN_TYPE: Class<*>
expect val BYTE_TYPE: Class<*>
expect val CHAR_TYPE: Class<*>
expect val SHORT_TYPE: Class<*>
expect val INT_TYPE: Class<*>
expect val LONG_TYPE: Class<*>
expect val FLOAT_TYPE: Class<*>
expect val DOUBLE_TYPE: Class<*>

/* Runtime.getRuntime().halt. */
expect fun halt(status: Int)

/* java.lang.String.trim: strips every character up to ' ' (Kotlin's trim strips whitespace). */
expect fun String.javaTrim(): String

/* java.lang.String.split: a regular expression, with trailing empty strings dropped. */
expect fun String.javaSplit(regex: String): Array<String>

/* java.lang.String.toLowerCase(), in the default locale. */
expect fun String.javaToLowerCase(): String

/* java.lang.String.replaceAll. */
expect fun String.javaReplaceAll(regex: String, replacement: String): String

/* java.lang.String.indexOf(int ch, int fromIndex), with ch a code point. */
expect fun String.javaIndexOf(ch: Int, fromIndex: Int): Int

/* Throwable.printStackTrace(PrintWriter). */
expect fun Throwable.printStackTraceTo(writer: io.PrintWriter?)

/* The bytes of the class path resource at `path` (from the root, as "/dir/name"), or null. */
expect fun readResource(path: String): ByteArray?

/* java.lang.String.intern. */
expect fun String.javaIntern(): String

/* new String(bytes, Charset.defaultCharset()). */
expect fun decodeDefaultCharset(bytes: ByteArray?): String

/* Runtime.class and Runtime.getRuntime(), for code that reaches Runtime's methods by reflection. */
expect val RUNTIME_CLASS: Class<*>

expect fun runtimeInstance(): Any
