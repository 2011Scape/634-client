/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject

actual class n : AbstractFontRenderer, SoftwareObject {
	actual constructor(arg0: oa?, arg1: ya?, fontMetrics: FontMetrics?, indexedImages: Array<SpriteType?>?, sprites: Array<NativeSprite?>?) : super(nativeUnavailable(), nativeUnavailable())
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual override fun fa(arg0: Char, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean) {
		throw NativeUnavailableException("fa")
	}
	actual override fun renderSymbol(arg0: Char, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean, clippingMask: ComponentSurface?, arg6: Int, arg7: Int) {
		throw NativeUnavailableException("renderSymbol")
	}
}
