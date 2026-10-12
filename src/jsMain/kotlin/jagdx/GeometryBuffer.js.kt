/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import jaclib.memory.Buffer
import jaclib.peer.hb
import jaclib.peer.uda
import nativeUnavailable

actual class GeometryBuffer : uda, Buffer {
	actual constructor(arg0: hb?) : super(nativeUnavailable())
	actual override fun getAddress(): Long {
		throw NativeUnavailableException("getAddress")
	}
	actual override fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("a")
	}
	actual override fun getSize(): Int {
		throw NativeUnavailableException("getSize")
	}
}
