/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.vorbis

import NativeUnavailableException
import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket
import nativeUnavailable

actual class VorbisInfo : SimplePeer {
	actual var rate: Int = 0
	actual var channels: Int = 0
	actual constructor() : super() {
		throw NativeUnavailableException("VorbisInfo")
	}
	actual fun headerIn(arg0: VorbisComment?, arg1: OggPacket?): Int {
		throw NativeUnavailableException("headerIn")
	}
}
