/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/na.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

expect class na : ComponentSurface, SoftwareObject {
	var nativeid: Long
	constructor(arg0: oa?, arg1: ya?, arg2: Int, arg3: Int, arg4: IntArray?, arg5: IntArray?)
	override fun w(arg0: Boolean)
	fun finalize()
}
