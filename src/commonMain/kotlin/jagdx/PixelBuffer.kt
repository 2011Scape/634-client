/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/PixelBuffer.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.memory.Buffer
import jaclib.peer.hb
import jaclib.peer.uda

expect class PixelBuffer : uda, Buffer {
	constructor(arg0: hb?)
	fun getRowPitch(): Int
	override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int)
	fun a(arg0: IntArray?, arg1: Int, arg2: Int, arg3: Int)
	override fun getAddress(): Long
	override fun getSize(): Int
	fun b(arg0: IntArray?, arg1: Int, arg2: Int, arg3: Int)
	fun getSlicePitch(): Int
}
