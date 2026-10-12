import com.jagex.game.runetek5.graphics.OffscreenSurface
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!wa")
class wa : OffscreenSurface {

	@OriginalMember(owner = "client!wa", name = "c", descriptor = "J")
	private var nativeid: Long = 0L

	@OriginalMember(owner = "client!wa", name = "a", descriptor = "Lclient!j;")
	@JvmField
	val aJ1: j?

	@OriginalMember(owner = "client!wa", name = "b", descriptor = "Lclient!xa;")
	@JvmField
	val aXa1: xa?

	@OriginalMember(owner = "client!wa", name = "<init>", descriptor = "(Lclient!oa;Lclient!j;Lclient!xa;)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: j?, @OriginalArg(2) arg2: xa?) {
		this.nativeid = arg0!!.nativeid
		this.aJ1 = arg1
		this.aXa1 = arg2
	}

	@OriginalMember(owner = "client!wa", name = "a", descriptor = "(IIIIIIZZ)V")
	override fun copyToSurface(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int) {
		this.Z(this.nativeid, this.aJ1!!.nativeid, this.aXa1!!.nativeid, arg0, arg1, arg2, arg3, arg4, arg5, true, true)
	}

	@OriginalMember(owner = "client!wa", name = "X", descriptor = "(JJJIIIIIIZZ)V")
	private external fun X(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Long, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: Boolean, @OriginalArg(10) arg10: Boolean)

	@OriginalMember(owner = "client!wa", name = "Z", descriptor = "(JJJIIIIIIZZ)V")
	private external fun Z(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long, @OriginalArg(2) arg2: Long, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: Boolean, @OriginalArg(10) arg10: Boolean)

	@OriginalMember(owner = "client!wa", name = "b", descriptor = "(IIIIIIZZ)V")
	override fun copyFromSurface(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(6) arg4: Boolean) {
		this.X(this.nativeid, this.aJ1!!.nativeid, this.aXa1!!.nativeid, arg0, arg1, arg2, arg3, 0, 0, arg4, true)
	}
}

