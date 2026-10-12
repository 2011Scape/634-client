/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaggl/MapBuffer.kt), so that common code can use it (milestone 3).
 */
package jaggl

import jaclib.memory.NativeBuffer

expect class MapBuffer : NativeBuffer {
	constructor()
	override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int)
	fun a(arg0: Int, arg1: Int, arg2: Int): Boolean
	fun a(): Boolean
	fun b(): Boolean
}
