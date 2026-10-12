/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.theora

import NativeUnavailableException
import jagtheora.misc.SimplePeer
import nativeUnavailable

actual class Frame : SimplePeer {
	actual val b: Int = 0
	actual val a: Int = 0
	actual val pixels: IntArray
		get() = throw NativeUnavailableException("pixels")
	actual constructor(arg0: Int, arg1: Int) : super() {
		throw NativeUnavailableException("Frame")
	}
}
