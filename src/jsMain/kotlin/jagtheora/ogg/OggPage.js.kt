/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.ogg

import NativeUnavailableException
import jagtheora.misc.SimplePeer
import nativeUnavailable

actual class OggPage : SimplePeer {
	actual constructor() : super() {
		throw NativeUnavailableException("OggPage")
	}
	actual fun getSerialNumber(): Int {
		throw NativeUnavailableException("getSerialNumber")
	}
	actual fun isBOS(): Boolean {
		throw NativeUnavailableException("isBOS")
	}
}
