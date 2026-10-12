/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import nativeUnavailable

actual class ue {
	actual constructor() {
		throw NativeUnavailableException("ue")
	}
	actual companion object {
		actual fun a(arg0: Byte, arg1: Int): Boolean {
			throw NativeUnavailableException("a")
		}
		actual fun a(arg0: Int, arg1: Boolean): Boolean {
			throw NativeUnavailableException("a")
		}
	}
}
