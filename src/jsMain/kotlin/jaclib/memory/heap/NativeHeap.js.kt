/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaclib.memory.heap

import NativeUnavailableException
import nativeUnavailable

actual class NativeHeap actual constructor(arg0: Int) {
	init {
		throw NativeUnavailableException("NativeHeap")
	}
	actual fun a(): Boolean {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Boolean): NativeHeapBuffer {
		throw NativeUnavailableException("a")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual fun b() {
		throw NativeUnavailableException("b")
	}
	actual fun put(arg0: Int, arg1: ByteArray?, arg2: Int, arg3: Int, arg4: Int) {
		throw NativeUnavailableException("put")
	}
	actual fun getBufferAddress(arg0: Int): Long {
		throw NativeUnavailableException("getBufferAddress")
	}
	actual fun deallocateBuffer(arg0: Int) {
		throw NativeUnavailableException("deallocateBuffer")
	}
}
