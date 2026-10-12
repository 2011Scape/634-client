import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!na")
class na : ComponentSurface, SoftwareObject {

	@OriginalMember(owner = "client!na", name = "nativeid", descriptor = "J")
	@JvmField
	var nativeid: Long = 0L

	@OriginalMember(owner = "client!na", name = "<init>", descriptor = "(Lclient!oa;Lclient!ya;II[I[I)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: ya?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: IntArray?) {
		this.ma(arg0, arg1, arg2, arg3, arg4, arg5)
	}

	@OriginalMember(owner = "client!na", name = "w", descriptor = "(Z)V")
	external override fun w(@OriginalArg(0) arg0: Boolean)

	@OriginalMember(owner = "client!na", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!na", name = "ma", descriptor = "(Lclient!oa;Lclient!ya;II[I[I)V")
	private external fun ma(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: ya?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: IntArray?)
}

