/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import com.jagex.game.runetek5.scene.ground.Ground

actual class t : Ground, SoftwareObject {
	actual var nativeid: Long = 0L
	actual constructor(arg0: oa?, arg1: ya?, arg2: Int, arg3: Int, tileHeights: Array<IntArray?>?, arg5: Array<IntArray?>?, arg6: Int, arg7: Int, arg8: Int) : super(nativeUnavailable(), nativeUnavailable(), nativeUnavailable(), nativeUnavailable())
	actual override fun renderFloor(arg0: Int, arg1: Int, arg2: Int, arg3: Array<BooleanArray?>?, arg4: Boolean, arg5: Int, arg6: Int) {
		throw NativeUnavailableException("renderFloor")
	}
	actual override fun wa(arg0: Shadow?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean) {
		throw NativeUnavailableException("wa")
	}
	actual override fun renderTile(arg0: Int, arg1: Int) {
		throw NativeUnavailableException("renderTile")
	}
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual override fun addTile(arg0: Int, arg1: Int, arg2: IntArray?, arg3: IntArray?, arg4: IntArray?, arg5: IntArray?, arg6: IntArray?, arg7: IntArray?, arg8: IntArray?, arg9: IntArray?, arg10: IntArray?, arg11: IntArray?, arg12: IntArray?, arg13: Int, arg14: Int, arg15: Int) {
		throw NativeUnavailableException("addTile")
	}
	actual override fun addLight(pointLight: PointLight?, arg1: IntArray?) {
		throw NativeUnavailableException("addLight")
	}
	actual override fun CA(arg0: Shadow?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean) {
		throw NativeUnavailableException("CA")
	}
	actual override fun ka(arg0: Int, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("ka")
	}
	actual override fun method8002(shadow: Shadow?, arg1: Int, arg2: Int, arg3: Int): Boolean {
		throw NativeUnavailableException("method8002")
	}
	actual override fun YA() {
		throw NativeUnavailableException("YA")
	}
	actual override fun renderTiles(arg0: Int, arg1: Int, arg2: Int, arg3: Array<BooleanArray?>?, arg4: Boolean, arg5: Int) {
		throw NativeUnavailableException("renderTiles")
	}
	actual override fun fa(arg0: Int, arg1: Int, arg2: Shadow?): Shadow? {
		throw NativeUnavailableException("fa")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual override fun U(arg0: Int, arg1: Int, arg2: IntArray?, arg3: IntArray?, arg4: IntArray?, arg5: IntArray?, arg6: IntArray?, arg7: IntArray?, arg8: IntArray?, arg9: IntArray?, arg10: Int, arg11: Int, arg12: Int, arg13: Boolean) {
		throw NativeUnavailableException("U")
	}
	actual override fun drawMinimap(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Array<BooleanArray?>?) {
		throw NativeUnavailableException("drawMinimap")
	}
}
