/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.misc

import NativeUnavailableException
import nativeUnavailable

actual abstract class SimplePeer {
	actual constructor() {
		throw NativeUnavailableException("SimplePeer")
	}
	actual fun a() {
		throw NativeUnavailableException("a")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual fun b(): Boolean {
		throw NativeUnavailableException("b")
	}
}
