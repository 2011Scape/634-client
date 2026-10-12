/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

actual class xa : DepthBuffer, SoftwareObject {
	actual var nativeid: Long = 0L
	actual constructor(arg0: Int, arg1: Int) {
		throw NativeUnavailableException("xa")
	}
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
}
