/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/t.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

expect class t : Ground, SoftwareObject {
	var nativeid: Long
	constructor(arg0: oa?, arg1: ya?, arg2: Int, arg3: Int, tileHeights: Array<IntArray?>?, arg5: Array<IntArray?>?, arg6: Int, arg7: Int, arg8: Int)
	override fun renderFloor(arg0: Int, arg1: Int, arg2: Int, arg3: Array<BooleanArray?>?, arg4: Boolean, arg5: Int, arg6: Int)
	override fun wa(arg0: Shadow?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean)
	override fun renderTile(arg0: Int, arg1: Int)
	override fun w(arg0: Boolean)
	override fun addTile(arg0: Int, arg1: Int, arg2: IntArray?, arg3: IntArray?, arg4: IntArray?, arg5: IntArray?, arg6: IntArray?, arg7: IntArray?, arg8: IntArray?, arg9: IntArray?, arg10: IntArray?, arg11: IntArray?, arg12: IntArray?, arg13: Int, arg14: Int, arg15: Int)
	override fun addLight(pointLight: PointLight?, arg1: IntArray?)
	override fun CA(arg0: Shadow?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean)
	override fun ka(arg0: Int, arg1: Int, arg2: Int)
	override fun method8002(shadow: Shadow?, arg1: Int, arg2: Int, arg3: Int): Boolean
	override fun YA()
	override fun renderTiles(arg0: Int, arg1: Int, arg2: Int, arg3: Array<BooleanArray?>?, arg4: Boolean, arg5: Int)
	override fun fa(arg0: Int, arg1: Int, arg2: Shadow?): Shadow?
	fun finalize()
	override fun U(arg0: Int, arg1: Int, arg2: IntArray?, arg3: IntArray?, arg4: IntArray?, arg5: IntArray?, arg6: IntArray?, arg7: IntArray?, arg8: IntArray?, arg9: IntArray?, arg10: Int, arg11: Int, arg12: Int, arg13: Boolean)
	override fun drawMinimap(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Array<BooleanArray?>?)
}
