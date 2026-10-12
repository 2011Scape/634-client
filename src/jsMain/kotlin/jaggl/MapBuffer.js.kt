/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaggl

import NativeUnavailableException
import jaclib.memory.NativeBuffer
import nativeUnavailable

actual class MapBuffer : NativeBuffer {
	actual constructor() : super() {
		throw NativeUnavailableException("MapBuffer")
	}
	actual override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Int, arg2: Int): Boolean {
		throw NativeUnavailableException("a")
	}
	actual fun a(): Boolean {
		throw NativeUnavailableException("a")
	}
	actual fun b(): Boolean {
		throw NativeUnavailableException("b")
	}
}
