/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagex3.graphics2.hw

import NativeUnavailableException
import nativeUnavailable

actual class NativeInterface actual constructor(arg0: Int, arg1: Int) {
	init {
		throw NativeUnavailableException("NativeInterface")
	}
	actual fun copyLighting(arg0: ShortArray?, arg1: ByteArray?, arg2: ShortArray?, arg3: ShortArray?, arg4: ShortArray?, arg5: ShortArray?, arg6: ByteArray?, arg7: Int, arg8: Int, arg9: ShortArray?, arg10: Int, arg11: Int, arg12: Int, arg13: Long) {
		throw NativeUnavailableException("copyLighting")
	}
	actual fun copyNormals(arg0: ShortArray?, arg1: ShortArray?, arg2: ShortArray?, arg3: ByteArray?, arg4: Float, arg5: Float, arg6: Int, arg7: Int, arg8: Int, arg9: Long) {
		throw NativeUnavailableException("copyNormals")
	}
	actual fun copyPositions(arg0: IntArray?, arg1: IntArray?, arg2: IntArray?, arg3: ShortArray?, arg4: Int, arg5: Int, arg6: Int, arg7: Long) {
		throw NativeUnavailableException("copyPositions")
	}
	actual fun setSunColour(arg0: Float, arg1: Float, arg2: Float, arg3: Float, arg4: Float) {
		throw NativeUnavailableException("setSunColour")
	}
	actual fun initTextureMetrics(arg0: Int, arg1: Byte, arg2: Byte) {
		throw NativeUnavailableException("initTextureMetrics")
	}
	actual fun release() {
		throw NativeUnavailableException("release")
	}
	actual fun setAmbient(arg0: Float) {
		throw NativeUnavailableException("setAmbient")
	}
	actual fun copyTexCoords(arg0: FloatArray?, arg1: FloatArray?, arg2: Int, arg3: Int, arg4: Int, arg5: Long) {
		throw NativeUnavailableException("copyTexCoords")
	}
	actual fun copyColours(arg0: ShortArray?, arg1: ByteArray?, arg2: ShortArray?, arg3: Int, arg4: ShortArray?, arg5: Int, arg6: Int, arg7: Int, arg8: Long) {
		throw NativeUnavailableException("copyColours")
	}
	actual fun setSunDirection(arg0: Float, arg1: Float, arg2: Float) {
		throw NativeUnavailableException("setSunDirection")
	}
}
