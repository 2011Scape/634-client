/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import awt.Canvas
import awt.Dimension
import awt.Rectangle
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import com.jagex.game.runetek5.statics.NativeBlitErrorTimer
import com.jagex.game.runetek5.utils.Utils

actual class p : Node, SoftwareObject {
	actual var canvas: Canvas? = null
	actual var anInt7778: Int = 0
	actual var anInt7779: Int = 0
	actual constructor(arg0: oa?, canvas: Canvas?, arg2: Int, arg3: Int) : super()
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual fun method6664(arg0: Int, arg1: Int) {
		throw NativeUnavailableException("method6664")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual fun method6665(rectangles: Array<Rectangle?>?, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("method6665")
	}
	actual fun method6666(canvas: Canvas?, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("method6666")
	}
	actual fun method6667() {
		throw NativeUnavailableException("method6667")
	}
}
