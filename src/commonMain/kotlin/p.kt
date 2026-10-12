/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/p.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import com.jagex.game.runetek5.statics.NativeBlitErrorTimer
import awt.Canvas
import awt.Dimension
import awt.Rectangle
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils

expect class p : Node, SoftwareObject {
	var canvas: Canvas?
	var anInt7778: Int
	var anInt7779: Int
	constructor(arg0: oa?, canvas: Canvas?, arg2: Int, arg3: Int)
	override fun w(arg0: Boolean)
	fun method6664(arg0: Int, arg1: Int)
	fun finalize()
	fun method6665(rectangles: Array<Rectangle?>?, arg1: Int, arg2: Int, arg3: Int)
	fun method6666(canvas: Canvas?, arg1: Int, arg2: Int)
	fun method6667()
}
