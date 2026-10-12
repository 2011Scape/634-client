/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/memory/heap/NativeHeap.kt), so that common code can use it (milestone 3).
 */
package jaclib.memory.heap

expect class NativeHeap(arg0: Int) {
	fun a(): Boolean
	fun a(arg0: Int, arg1: Boolean): NativeHeapBuffer
	fun finalize()
	fun b()
	fun put(arg0: Int, arg1: ByteArray?, arg2: Int, arg3: Int, arg4: Int)
	fun getBufferAddress(arg0: Int): Long
	fun deallocateBuffer(arg0: Int)
}
