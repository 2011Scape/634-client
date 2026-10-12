/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagtheora.ogg

import NativeUnavailableException
import jagtheora.misc.SimplePeer
import nativeUnavailable

actual class OggPacket : SimplePeer {
	actual constructor() : super() {
		throw NativeUnavailableException("OggPacket")
	}
	actual fun isTheora(): Boolean {
		throw NativeUnavailableException("isTheora")
	}
	actual fun getData(): ByteArray? {
		throw NativeUnavailableException("getData")
	}
	actual fun isKeyFrame(): Int {
		throw NativeUnavailableException("isKeyFrame")
	}
	actual fun isVorbis(): Boolean {
		throw NativeUnavailableException("isVorbis")
	}
}
