import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!j")
class j : NativeSprite, SoftwareObject {

	@OriginalMember(owner = "client!j", name = "nativeid", descriptor = "J")
	@JvmField
	var nativeid: Long = 0L

	@OriginalMember(owner = "client!j", name = "<init>", descriptor = "(Lclient!oa;II)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
		this.EA(arg0, arg1, arg2)
	}

	@OriginalMember(owner = "client!j", name = "<init>", descriptor = "(Lclient!oa;[IIIIIZ)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Boolean) {
		this.ua(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
	}

	@OriginalMember(owner = "client!j", name = "<init>", descriptor = "(Lclient!oa;[I[B[BIIII)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: ByteArray?, @OriginalArg(3) arg3: ByteArray?, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int) {
		this.ma(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
	}

	@OriginalMember(owner = "client!j", name = "<init>", descriptor = "(Lclient!oa;IIIIZ)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean) {
		this.h(arg0, arg1, arg2, arg3, arg4, arg5)
	}

	@OriginalMember(owner = "client!j", name = "R", descriptor = "(JZ)V")
	private external fun R(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Boolean)

	@OriginalMember(owner = "client!j", name = "P", descriptor = "(JIIIIIII)V")
	private external fun P(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int)

	@OriginalMember(owner = "client!j", name = "a", descriptor = "(IIIII)V")
	override fun render(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
		this.W(this.nativeid, arg0, arg1, arg2, arg3, arg4)
	}

	@OriginalMember(owner = "client!j", name = "RA", descriptor = "(JIIIIIIII)V")
	private external fun RA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int)

	@OriginalMember(owner = "client!j", name = "UA", descriptor = "(JFFFFFFIJII)V")
	private external fun UA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float, @OriginalArg(5) arg5: Float, @OriginalArg(6) arg6: Float, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Long, @OriginalArg(9) arg9: Int, @OriginalArg(10) arg10: Int)

	@OriginalMember(owner = "client!j", name = "wa", descriptor = "(J)I")
	private external fun wa(@OriginalArg(0) arg0: Long): Int

	@OriginalMember(owner = "client!j", name = "M", descriptor = "(J)I")
	private external fun M(@OriginalArg(0) arg0: Long): Int

	@OriginalMember(owner = "client!j", name = "a", descriptor = "(IILclient!aa;II)V")
	override fun drawWithSurface(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) clippingMask: ComponentSurface?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
		this.V(this.nativeid, arg0, arg1, (clippingMask as na).nativeid, arg3, arg4)
	}

	@OriginalMember(owner = "client!j", name = "a", descriptor = "([I)V")
	override fun projectOffsets(@OriginalArg(0) arg0: IntArray?) {
		this.CA(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!j", name = "w", descriptor = "(Z)V")
	override fun w(@OriginalArg(0) arg0: Boolean) {
		this.R(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!j", name = "d", descriptor = "()I")
	override fun scaleHeight(): Int {
		return this.JA(this.nativeid)
	}

	@OriginalMember(owner = "client!j", name = "EA", descriptor = "(Lclient!oa;II)V")
	private external fun EA(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

	@OriginalMember(owner = "client!j", name = "c", descriptor = "(IIII)V")
	override fun setOffsets(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		this.A(this.nativeid, arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!j", name = "ma", descriptor = "(Lclient!oa;[I[B[BIIII)V")
	private external fun ma(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: ByteArray?, @OriginalArg(3) arg3: ByteArray?, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int)

	@OriginalMember(owner = "client!j", name = "a", descriptor = "(III)V")
	override fun copyAlpha(@OriginalArg(2) arg0: Int) {
		this.N(this.nativeid, 0, 0, arg0)
	}

	@OriginalMember(owner = "client!j", name = "a", descriptor = "(FFFFFFILclient!aa;II)V")
	override fun renderParallelogramImpl(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float, @OriginalArg(5) arg5: Float, @OriginalArg(7) clippingMask: ComponentSurface?, @OriginalArg(8) arg7: Int, @OriginalArg(9) arg8: Int) {
		this.UA(this.nativeid, arg0, arg1, arg2, arg3, arg4, arg5, 1, (clippingMask as na).nativeid, arg7, arg8)
	}

	@OriginalMember(owner = "client!j", name = "b", descriptor = "()I")
	override fun getHeight(): Int {
		return this.I(this.nativeid)
	}

	@OriginalMember(owner = "client!j", name = "CA", descriptor = "(J[I)V")
	private external fun CA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: IntArray?)

	@OriginalMember(owner = "client!j", name = "W", descriptor = "(JIIIII)V")
	private external fun W(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int)

	@OriginalMember(owner = "client!j", name = "a", descriptor = "(IIIIIIII)V")
	override fun renderImpl(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int) {
		this.RA(this.nativeid, arg0, arg1, arg2, arg3, arg4, arg5, arg6, 1)
	}

	@OriginalMember(owner = "client!j", name = "c", descriptor = "()I")
	override fun getWidth(): Int {
		return this.M(this.nativeid)
	}

	@OriginalMember(owner = "client!j", name = "b", descriptor = "(JFFFFFFIIII)V")
	private external fun b(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float, @OriginalArg(5) arg5: Float, @OriginalArg(6) arg6: Float, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: Int, @OriginalArg(10) arg10: Int)

	@OriginalMember(owner = "client!j", name = "A", descriptor = "(JIIII)V")
	private external fun A(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

	@OriginalMember(owner = "client!j", name = "V", descriptor = "(JIIJII)V")
	private external fun V(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Long, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int)

	@OriginalMember(owner = "client!j", name = "N", descriptor = "(JIII)V")
	private external fun N(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!j", name = "YA", descriptor = "(JIIIIII)V")
	private external fun YA(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int)

	@OriginalMember(owner = "client!j", name = "I", descriptor = "(J)I")
	private external fun I(@OriginalArg(0) arg0: Long): Int

	@OriginalMember(owner = "client!j", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!j", name = "ua", descriptor = "(Lclient!oa;[IIIIIZ)V")
	private external fun ua(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Boolean)

	@OriginalMember(owner = "client!j", name = "a", descriptor = "(IIIIII)V")
	override fun captureScreenRegion(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int) {
		this.YA(this.nativeid, arg0, arg1, arg2, arg3, arg4, arg5)
	}

	@OriginalMember(owner = "client!j", name = "h", descriptor = "(Lclient!oa;IIIIZ)V")
	private external fun h(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean)

	@OriginalMember(owner = "client!j", name = "a", descriptor = "(IIIIIII)V")
	override fun drawScaledColored(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int) {
		this.P(this.nativeid, arg0, arg1, arg2, arg3, arg4, arg5, arg6)
	}

	@OriginalMember(owner = "client!j", name = "a", descriptor = "()I")
	override fun scaleWidth(): Int {
		return this.wa(this.nativeid)
	}

	@OriginalMember(owner = "client!j", name = "a", descriptor = "(FFFFFFIIII)V")
	override fun method6201(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float, @OriginalArg(5) arg5: Float, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int) {
		this.b(this.nativeid, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, 1, 1)
	}

	@OriginalMember(owner = "client!j", name = "JA", descriptor = "(J)I")
	private external fun JA(@OriginalArg(0) arg0: Long): Int
}

