/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

actual class na : ComponentSurface, SoftwareObject {
	actual var nativeid: Long = 0L
	actual constructor(arg0: oa?, arg1: ya?, arg2: Int, arg3: Int, arg4: IntArray?, arg5: IntArray?) : super()
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
}
