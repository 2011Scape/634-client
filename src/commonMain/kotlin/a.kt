/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/a.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import lang.Runnable

expect class a : SoftwareObject {
	var aRunnable1: Runnable?
	constructor(arg0: oa?, arg1: Int, arg2: Int)
	fun method1(ground: Ground?, arg1: Int, arg2: Int)
	fun method2(model: Model?, arg1: Int, arg2: Int, matrix: Matrix?, arg4: Boolean, arg5: Int): Boolean
	fun method3(model: Model?, model2: Model?, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean)
	fun finalize()
	fun method4(model: Model?, matrix: Matrix?, arg2: IntArray?, arg3: Int)
	fun method5(model: Model?, arg1: Int, arg2: Int, matrix: Matrix?, arg4: Boolean): Boolean
	fun method6(ground: Ground?, arg1: Int, arg2: Int, arg3: Int)
	override fun w(arg0: Boolean)
	fun method7(ground: Ground?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Array<BooleanArray?>?)
	fun method8(model: Model?, matrix: Matrix?, arg2: IntArray?, arg3: Int, arg4: Int)
	fun method9(toolkit: Toolkit?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int)
	fun method10(model: Model?, arg1: IntArray?, matrix: Matrix?)
	fun method11(arg0: i?, arg1: Byte, arg2: Int, arg3: Boolean): Model?
	fun method13()
	fun method14(toolkit: Toolkit?, arg1: IntArray?, arg2: IntArray?, arg3: IntArray?, arg4: ShortArray?, arg5: Int)
	fun method15()
}
