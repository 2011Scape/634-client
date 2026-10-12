/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/ja.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

expect class ja : Matrix, SoftwareObject {
	var nativeid: Long
	constructor()
	override fun translate(arg0: Int, arg1: Int, arg2: Int)
	override fun makeIdentity()
	override fun w(arg0: Boolean)
	override fun projectDirection(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?)
	override fun project(arg0: IntArray?)
	override fun rotateAxisZ(arg0: Int)
	override fun createCamera(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int)
	override fun rotateAxisX(arg0: Int)
	override fun method4652(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?)
	override fun makeRotationZ(arg0: Int)
	override fun apply(matrix: Matrix?)
	override fun projectRelative(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?)
	fun finalize()
	override fun rotateAxisY(arg0: Int)
	override fun copy(): Matrix?
	override fun rotate(arg0: Int)
	override fun applyTranslation(arg0: Int, arg1: Int, arg2: Int)
	override fun makeRotationX(arg0: Int)
}
