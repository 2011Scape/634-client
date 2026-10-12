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

actual class VorbisBlock : SimplePeer {
	actual constructor(dspState: DSPState?) : super() {
		throw NativeUnavailableException("VorbisBlock")
	}
	actual fun synthesis(arg0: OggPacket?): Int {
		throw NativeUnavailableException("synthesis")
	}
}
