/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/wa.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.graphics.OffscreenSurface

expect class wa : OffscreenSurface {
	val aJ1: j?
	val aXa1: xa?
	constructor(arg0: oa?, arg1: j?, arg2: xa?)
	override fun copyToSurface(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int)
	override fun copyFromSurface(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean)
}
