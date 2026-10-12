/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.theora

import NativeUnavailableException
import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket
import nativeUnavailable

actual class SetupInfo : SimplePeer {
	actual constructor() : super() {
		throw NativeUnavailableException("SetupInfo")
	}
	actual fun decodeHeader(arg0: TheoraInfo?, arg1: TheoraComment?, arg2: OggPacket?): Int {
		throw NativeUnavailableException("decodeHeader")
	}
}
