/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import jaclib.peer.hb
import jaclib.peer.uda
import nativeUnavailable

actual class VertexElementCollection : uda {
	actual constructor(arg0: hb?) : super(nativeUnavailable())
	actual fun finish() {
		throw NativeUnavailableException("finish")
	}
	actual fun addElement(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
		throw NativeUnavailableException("addElement")
	}
}
