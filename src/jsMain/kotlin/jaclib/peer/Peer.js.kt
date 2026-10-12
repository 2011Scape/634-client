/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaclib.peer

import NativeUnavailableException
import nativeUnavailable

actual abstract class Peer protected actual constructor() {
	init {
		throw NativeUnavailableException("Peer")
	}
	actual fun a(arg0: Boolean): Boolean {
		throw NativeUnavailableException("a")
	}
	actual open fun a(): Long {
		throw NativeUnavailableException("a")
	}
}
