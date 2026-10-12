/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.ogg

import NativeUnavailableException
import jagtheora.misc.SimplePeer
import nativeUnavailable

actual class OggStreamState : SimplePeer {
	actual constructor(arg0: Int) : super() {
		throw NativeUnavailableException("OggStreamState")
	}
	actual fun packetPeek(arg0: OggPacket?): Int {
		throw NativeUnavailableException("packetPeek")
	}
	actual fun pageIn(arg0: OggPage?): Boolean {
		throw NativeUnavailableException("pageIn")
	}
	actual fun packetOut(): Int {
		throw NativeUnavailableException("packetOut")
	}
	actual fun packetOut(arg0: OggPacket?): Int {
		throw NativeUnavailableException("packetOut")
	}
}
