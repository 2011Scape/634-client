/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/j.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

expect class j : NativeSprite, SoftwareObject {
	var nativeid: Long
	constructor(arg0: oa?, arg1: Int, arg2: Int)
	constructor(arg0: oa?, arg1: IntArray?, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Boolean)
	constructor(arg0: oa?, arg1: IntArray?, arg2: ByteArray?, arg3: ByteArray?, arg4: Int, arg5: Int, arg6: Int, arg7: Int)
	constructor(arg0: oa?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean)
	override fun render(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int)
	override fun drawWithSurface(arg0: Int, arg1: Int, clippingMask: ComponentSurface?, arg3: Int, arg4: Int)
	override fun projectOffsets(arg0: IntArray?)
	override fun w(arg0: Boolean)
	override fun scaleHeight(): Int
	override fun setOffsets(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	override fun copyAlpha(arg0: Int)
	override fun renderParallelogramImpl(arg0: Float, arg1: Float, arg2: Float, arg3: Float, arg4: Float, arg5: Float, clippingMask: ComponentSurface?, arg7: Int, arg8: Int)
	override fun getHeight(): Int
	override fun renderImpl(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int)
	override fun getWidth(): Int
	fun finalize()
	override fun captureScreenRegion(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int)
	override fun drawScaledColored(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int)
	override fun scaleWidth(): Int
	override fun method6201(arg0: Float, arg1: Float, arg2: Float, arg3: Float, arg4: Float, arg5: Float, arg6: Int, arg7: Int)
}
