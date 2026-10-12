/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/memory/heap/NativeHeapBuffer.kt), so that common code can use it (milestone 3).
 */
package jaclib.memory.heap

import jaclib.memory.Buffer
import jaclib.memory.Source

expect class NativeHeapBuffer(nativeHeap: NativeHeap?, arg1: Int, arg2: Int) : Buffer, Source {
	val b: Int
	override fun getAddress(): Long
	override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int)
	fun finalize()
	override fun getSize(): Int
}
