/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/ba.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

expect class ba : Shadow, SoftwareObject {
	constructor(arg0: oa?)
	fun finalize()
	override fun w(arg0: Boolean)
}
