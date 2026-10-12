/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/memory/Buffer.kt), so that common code can use it (milestone 3).
 */
package jaclib.memory

expect interface Buffer {
	fun getSize(): Int
	fun getAddress(): Long
	fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int)
}
