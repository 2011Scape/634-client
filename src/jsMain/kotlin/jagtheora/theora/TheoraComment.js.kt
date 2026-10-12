/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.theora

import NativeUnavailableException
import jagtheora.misc.SimplePeer
import nativeUnavailable

actual class TheoraComment : SimplePeer {
	actual constructor() : super() {
		throw NativeUnavailableException("TheoraComment")
	}
}
