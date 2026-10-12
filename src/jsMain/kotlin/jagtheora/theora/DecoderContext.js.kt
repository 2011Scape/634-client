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

actual class DecoderContext : SimplePeer {
	actual constructor(theoraInfo: TheoraInfo?, setupInfo: SetupInfo?) : super() {
		throw NativeUnavailableException("DecoderContext")
	}
	actual fun granuleFrame(arg0: GranulePos?): Long {
		throw NativeUnavailableException("granuleFrame")
	}
	actual fun setPostProcessingLevel(arg0: Int): Int {
		throw NativeUnavailableException("setPostProcessingLevel")
	}
	actual fun decodePacketIn(arg0: OggPacket?, arg1: GranulePos?): Int {
		throw NativeUnavailableException("decodePacketIn")
	}
	actual fun getMaxPostProcessingLevel(): Int {
		throw NativeUnavailableException("getMaxPostProcessingLevel")
	}
	actual fun granuleTime(arg0: GranulePos?): Double {
		throw NativeUnavailableException("granuleTime")
	}
	actual fun decodeFrame(arg0: Frame?): Int {
		throw NativeUnavailableException("decodeFrame")
	}
}
