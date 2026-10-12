/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.vorbis

import NativeUnavailableException
import jagtheora.misc.SimplePeer
import nativeUnavailable

actual class DSPState : SimplePeer {
	actual constructor(vorbisInfo: VorbisInfo?) : super() {
		throw NativeUnavailableException("DSPState")
	}
	actual fun pcmOut(arg0: Int): Array<FloatArray?>? {
		throw NativeUnavailableException("pcmOut")
	}
	actual fun read(arg0: Int) {
		throw NativeUnavailableException("read")
	}
	actual fun blockIn(arg0: VorbisBlock?) {
		throw NativeUnavailableException("blockIn")
	}
	actual fun granuleTime(): Double {
		throw NativeUnavailableException("granuleTime")
	}
}
