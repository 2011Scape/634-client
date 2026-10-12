/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/n.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

expect class n : AbstractFontRenderer, SoftwareObject {
	constructor(arg0: oa?, arg1: ya?, fontMetrics: FontMetrics?, indexedImages: Array<SpriteType?>?, sprites: Array<NativeSprite?>?)
	override fun w(arg0: Boolean)
	fun finalize()
	override fun fa(arg0: Char, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean)
	override fun renderSymbol(arg0: Char, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean, clippingMask: ComponentSurface?, arg6: Int, arg7: Int)
}
