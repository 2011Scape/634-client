/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.cache.node.MemoryPool
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

actual class ya : MemoryPool, SoftwareObject {
	actual constructor(arg0: oa?, arg1: Int) : super()
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual fun ga() {
		throw NativeUnavailableException("ga")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual fun r() {
		throw NativeUnavailableException("r")
	}
}
