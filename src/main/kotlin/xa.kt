import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!xa")
class xa : DepthBuffer, SoftwareObject {

	// nativeid is final with the constant initialiser 0L, so javac inlined 0L at every read of it;
	// the natives still set the field. The reads below are written as 0L to keep that behaviour.
	@OriginalMember(owner = "client!xa", name = "nativeid", descriptor = "J")
	@JvmField
	val nativeid: Long = 0L

	@OriginalMember(owner = "client!xa", name = "<init>", descriptor = "(II)V")
	constructor(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
		this.r(arg0, arg1)
	}

	@OriginalMember(owner = "client!xa", name = "r", descriptor = "(II)V")
	private external fun r(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!xa", name = "va", descriptor = "(JZ)V")
	private external fun va(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Boolean)

	@OriginalMember(owner = "client!xa", name = "w", descriptor = "(Z)V")
	override fun w(@OriginalArg(0) arg0: Boolean) {
		this.va(0L, arg0)
	}

	@OriginalMember(owner = "client!xa", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (0L != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}
}

