/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaclib.memory.heap

import NativeUnavailableException
import jaclib.memory.Buffer
import jaclib.memory.Source
import nativeUnavailable

actual class NativeHeapBuffer actual constructor(nativeHeap: NativeHeap?, arg1: Int, arg2: Int) : Buffer, Source {
	init {
		throw NativeUnavailableException("NativeHeapBuffer")
	}
	actual val b: Int = 0
	actual override fun getAddress(): Long {
		throw NativeUnavailableException("getAddress")
	}
	actual override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("a")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual override fun getSize(): Int {
		throw NativeUnavailableException("getSize")
	}
}
