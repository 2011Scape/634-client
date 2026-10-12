/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

actual class j : NativeSprite, SoftwareObject {
	actual var nativeid: Long = 0L
	actual constructor(arg0: oa?, arg1: Int, arg2: Int) : super()
	actual constructor(arg0: oa?, arg1: IntArray?, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Boolean) : super()
	actual constructor(arg0: oa?, arg1: IntArray?, arg2: ByteArray?, arg3: ByteArray?, arg4: Int, arg5: Int, arg6: Int, arg7: Int) : super()
	actual constructor(arg0: oa?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean) : super()
	actual override fun render(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) {
		throw NativeUnavailableException("render")
	}
	actual override fun drawWithSurface(arg0: Int, arg1: Int, clippingMask: ComponentSurface?, arg3: Int, arg4: Int) {
		throw NativeUnavailableException("drawWithSurface")
	}
	actual override fun projectOffsets(arg0: IntArray?) {
		throw NativeUnavailableException("projectOffsets")
	}
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual override fun scaleHeight(): Int {
		throw NativeUnavailableException("scaleHeight")
	}
	actual override fun setOffsets(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("setOffsets")
	}
	actual override fun copyAlpha(arg0: Int) {
		throw NativeUnavailableException("copyAlpha")
	}
	actual override fun renderParallelogramImpl(arg0: Float, arg1: Float, arg2: Float, arg3: Float, arg4: Float, arg5: Float, clippingMask: ComponentSurface?, arg7: Int, arg8: Int) {
		throw NativeUnavailableException("renderParallelogramImpl")
	}
	actual override fun getHeight(): Int {
		throw NativeUnavailableException("getHeight")
	}
	actual override fun renderImpl(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int) {
		throw NativeUnavailableException("renderImpl")
	}
	actual override fun getWidth(): Int {
		throw NativeUnavailableException("getWidth")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual override fun captureScreenRegion(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
		throw NativeUnavailableException("captureScreenRegion")
	}
	actual override fun drawScaledColored(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int) {
		throw NativeUnavailableException("drawScaledColored")
	}
	actual override fun scaleWidth(): Int {
		throw NativeUnavailableException("scaleWidth")
	}
	actual override fun method6201(arg0: Float, arg1: Float, arg2: Float, arg3: Float, arg4: Float, arg5: Float, arg6: Int, arg7: Int) {
		throw NativeUnavailableException("method6201")
	}
}
