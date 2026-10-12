/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.graphics.OffscreenSurface

actual class wa : OffscreenSurface {
	actual val aJ1: j? = null
	actual val aXa1: xa? = null
	actual constructor(arg0: oa?, arg1: j?, arg2: xa?) {
		throw NativeUnavailableException("wa")
	}
	actual override fun copyToSurface(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
		throw NativeUnavailableException("copyToSurface")
	}
	actual override fun copyFromSurface(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean) {
		throw NativeUnavailableException("copyFromSurface")
	}
}
