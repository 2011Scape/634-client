/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaclib.peer

import NativeUnavailableException
import nativeUnavailable

actual open class IUnknown actual constructor(arg0: hb?) : Peer() {
	init {
		throw NativeUnavailableException("IUnknown")
	}
	actual fun a(arg0: Int): Long {
		throw NativeUnavailableException("a")
	}
}
