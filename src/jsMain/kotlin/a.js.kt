/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import com.jagex.game.runetek5.scene.ground.Ground
import lang.Runnable

actual class a : SoftwareObject {
	actual var aRunnable1: Runnable? = null
	actual constructor(arg0: oa?, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("a")
	}
	actual fun method1(ground: Ground?, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("method1")
	}
	actual fun method2(model: Model?, arg1: Int, arg2: Int, matrix: Matrix?, arg4: Boolean, arg5: Int): Boolean {
		throw NativeUnavailableException("method2")
	}
	actual fun method3(model: Model?, model2: Model?, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean) {
		throw NativeUnavailableException("method3")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual fun method4(model: Model?, matrix: Matrix?, arg2: IntArray?, arg3: Int) {
		throw NativeUnavailableException("method4")
	}
	actual fun method5(model: Model?, arg1: Int, arg2: Int, matrix: Matrix?, arg4: Boolean): Boolean {
		throw NativeUnavailableException("method5")
	}
	actual fun method6(ground: Ground?, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("method6")
	}
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual fun method7(ground: Ground?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Array<BooleanArray?>?) {
		throw NativeUnavailableException("method7")
	}
	actual fun method8(model: Model?, matrix: Matrix?, arg2: IntArray?, arg3: Int, arg4: Int) {
		throw NativeUnavailableException("method8")
	}
	actual fun method9(toolkit: Toolkit?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int) {
		throw NativeUnavailableException("method9")
	}
	actual fun method10(model: Model?, arg1: IntArray?, matrix: Matrix?) {
		throw NativeUnavailableException("method10")
	}
	actual fun method11(arg0: i?, arg1: Byte, arg2: Int, arg3: Boolean): Model? {
		throw NativeUnavailableException("method11")
	}
	actual fun method13() {
		throw NativeUnavailableException("method13")
	}
	actual fun method14(toolkit: Toolkit?, arg1: IntArray?, arg2: IntArray?, arg3: IntArray?, arg4: ShortArray?, arg5: Int) {
		throw NativeUnavailableException("method14")
	}
	actual fun method15() {
		throw NativeUnavailableException("method15")
	}
}
