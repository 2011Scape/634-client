/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/memory/NativeBuffer.kt), so that common code can use it (milestone 3).
 */
package jaclib.memory

expect open class NativeBuffer : Buffer, Source {
	constructor()
	final override fun getAddress(): Long
	override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int)
	final override fun getSize(): Int
	fun a(arg0: Long, arg1: Int)
}
