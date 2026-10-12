/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.ogg

import NativeUnavailableException
import jagtheora.misc.SimplePeer
import nativeUnavailable

actual class OggSyncState : SimplePeer {
	actual constructor() : super() {
		throw NativeUnavailableException("OggSyncState")
	}
	actual fun write(arg0: ByteArray?, arg1: Int): Boolean {
		throw NativeUnavailableException("write")
	}
	actual fun pageOut(arg0: OggPage?): Int {
		throw NativeUnavailableException("pageOut")
	}
}
