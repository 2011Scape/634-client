/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaclib.memory

import NativeUnavailableException
import nativeUnavailable

actual open class NativeBuffer : Buffer, Source {
	actual constructor() : super() {
		throw NativeUnavailableException("NativeBuffer")
	}
	actual final override fun getAddress(): Long {
		throw NativeUnavailableException("getAddress")
	}
	actual override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("a")
	}
	actual final override fun getSize(): Int {
		throw NativeUnavailableException("getSize")
	}
	actual fun a(arg0: Long, arg1: Int) {
		throw NativeUnavailableException("a")
	}
}
