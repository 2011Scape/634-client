/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaclib.peer

import NativeUnavailableException
import nativeUnavailable

actual class hb {
	actual constructor() {
		throw NativeUnavailableException("hb")
	}
	actual fun a(arg0: Byte) {
		throw NativeUnavailableException("a")
	}
	actual fun b(arg0: Byte) {
		throw NativeUnavailableException("b")
	}
}
