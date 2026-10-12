/*
 * Not part of the original client: the browser side of NativeHeapBuffer (milestone 5). The same logic
 * as the JVM class; the heap it writes to is NativeHeap's byte arrays.
 */
package jaclib.memory.heap

import jaclib.memory.Buffer
import jaclib.memory.Source

actual class NativeHeapBuffer actual constructor(nativeHeap: NativeHeap?, arg1: Int, arg2: Int) : Buffer, Source {

	private var c: Boolean = true

	actual val b: Int = arg2

	private val a: NativeHeap? = nativeHeap

	private val d: Int = arg1

	actual override fun getAddress(): Long {
		return this.a!!.getBufferAddress(this.d)
	}

	private fun a(): Boolean {
		return this.a!!.a() && this.c
	}

	private fun b() {
		if (this.a()) {
			this.a!!.deallocateBuffer(this.d)
		}
		this.c = false
	}

	actual override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int) {
		if ((arg1 < 0) or (arg0 == null) or !this.a() or (arg3 + arg1 > arg0!!.size) or (arg2 < 0) or (this.b < arg2 + arg3)) {
			throw RuntimeException()
		}
		this.a!!.put(this.d, arg0, arg1, arg2, arg3)
	}

	actual fun finalize() {
		this.b()
	}

	actual override fun getSize(): Int {
		return this.b
	}
}
