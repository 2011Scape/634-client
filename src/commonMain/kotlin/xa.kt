/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/xa.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

expect class xa : DepthBuffer, SoftwareObject {
	var nativeid: Long
	constructor(arg0: Int, arg1: Int)
	override fun w(arg0: Boolean)
	fun finalize()
}
