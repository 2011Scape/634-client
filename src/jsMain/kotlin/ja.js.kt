/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

actual class ja : Matrix, SoftwareObject {
	actual var nativeid: Long = 0L
	actual constructor() : super()
	actual override fun translate(arg0: Int, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("translate")
	}
	actual override fun makeIdentity() {
		throw NativeUnavailableException("makeIdentity")
	}
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual override fun projectDirection(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?) {
		throw NativeUnavailableException("projectDirection")
	}
	actual override fun project(arg0: IntArray?) {
		throw NativeUnavailableException("project")
	}
	actual override fun rotateAxisZ(arg0: Int) {
		throw NativeUnavailableException("rotateAxisZ")
	}
	actual override fun createCamera(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
		throw NativeUnavailableException("createCamera")
	}
	actual override fun rotateAxisX(arg0: Int) {
		throw NativeUnavailableException("rotateAxisX")
	}
	actual override fun method4652(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?) {
		throw NativeUnavailableException("method4652")
	}
	actual override fun makeRotationZ(arg0: Int) {
		throw NativeUnavailableException("makeRotationZ")
	}
	actual override fun apply(matrix: Matrix?) {
		throw NativeUnavailableException("apply")
	}
	actual override fun projectRelative(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?) {
		throw NativeUnavailableException("projectRelative")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual override fun rotateAxisY(arg0: Int) {
		throw NativeUnavailableException("rotateAxisY")
	}
	actual override fun copy(): Matrix? {
		throw NativeUnavailableException("copy")
	}
	actual override fun rotate(arg0: Int) {
		throw NativeUnavailableException("rotate")
	}
	actual override fun applyTranslation(arg0: Int, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("applyTranslation")
	}
	actual override fun makeRotationX(arg0: Int) {
		throw NativeUnavailableException("makeRotationX")
	}
}
