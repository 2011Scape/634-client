/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/ya.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.cache.node.MemoryPool
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

expect class ya : MemoryPool, SoftwareObject {
	constructor(arg0: oa?, arg1: Int)
	override fun w(arg0: Boolean)
	fun ga()
	fun finalize()
	fun r()
}
