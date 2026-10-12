import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!n")
class n : AbstractFontRenderer, SoftwareObject {

	@OriginalMember(owner = "client!n", name = "nativeid", descriptor = "J")
	private var nativeid: Long = 0L

	@OriginalMember(owner = "client!n", name = "<init>", descriptor = "(Lclient!oa;Lclient!ya;Lclient!oea;[Lclient!rh;[Lclient!mi;)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: ya?, @OriginalArg(2) fontMetrics: FontMetrics?, @OriginalArg(3) indexedImages: Array<SpriteType?>?, @OriginalArg(4) sprites: Array<NativeSprite?>?) : super(arg0, fontMetrics) {
		@Pc(7) val local7: Array<ByteArray?> = arrayOfNulls<ByteArray>(indexedImages!!.size)
		@Pc(11) val local11: IntArray = IntArray(indexedImages.size)
		@Pc(15) val local15: IntArray = IntArray(indexedImages.size)
		@Pc(19) val local19: IntArray = IntArray(indexedImages.size)
		@Pc(23) val local23: IntArray = IntArray(indexedImages.size)
		@Pc(25) var i: Int = 0
		while (i < indexedImages.size) {
			local7[i] = indexedImages[i]!!.pixels
			local11[i] = indexedImages[i]!!.width
			local15[i] = indexedImages[i]!!.height
			local19[i] = indexedImages[i]!!.minX
			local23[i] = indexedImages[i]!!.minY
			i++
		}
		this.S(arg0, arg1, local7, indexedImages[0]!!.pallete, local11, local15, local19, local23)
	}

	@OriginalMember(owner = "client!n", name = "S", descriptor = "(Lclient!oa;Lclient!ya;[[B[I[I[I[I[I)V")
	private external fun S(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: ya?, @OriginalArg(2) arg2: Array<ByteArray?>?, @OriginalArg(3) arg3: IntArray?, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: IntArray?, @OriginalArg(6) arg6: IntArray?, @OriginalArg(7) arg7: IntArray?)

	@OriginalMember(owner = "client!n", name = "w", descriptor = "(Z)V")
	external override fun w(@OriginalArg(0) arg0: Boolean)

	@OriginalMember(owner = "client!n", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!n", name = "fa", descriptor = "(CIIIZ)V")
	external override fun fa(@OriginalArg(0) arg0: Char, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Boolean)

	@OriginalMember(owner = "client!n", name = "PA", descriptor = "(CIIIZLclient!aa;II)V")
	private external fun PA(@OriginalArg(0) arg0: Char, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Boolean, @OriginalArg(5) arg5: ComponentSurface?, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int)

	@OriginalMember(owner = "client!n", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	override fun renderSymbol(@OriginalArg(0) arg0: Char, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Boolean, @OriginalArg(5) clippingMask: ComponentSurface?, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int) {
		this.PA(arg0, arg1, arg2, arg3, arg4, clippingMask, arg6, arg7)
	}
}

