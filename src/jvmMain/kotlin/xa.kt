import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!xa")
actual class xa : DepthBuffer, SoftwareObject {

	// The deobfuscated Java declared this `final long nativeid = 0L`, so javac replaced every read with 0
	// and the client never saw the id the natives store here. The original client (and void-client) keep
	// it a plain field and read it; so does this one.
	@OriginalMember(owner = "client!xa", name = "nativeid", descriptor = "J")
	@JvmField
	actual var nativeid: Long = 0L

	@OriginalMember(owner = "client!xa", name = "<init>", descriptor = "(II)V")
	actual constructor(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
		this.r(arg0, arg1)
	}

	@OriginalMember(owner = "client!xa", name = "r", descriptor = "(II)V")
	private external fun r(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!xa", name = "va", descriptor = "(JZ)V")
	private external fun va(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Boolean)

	@OriginalMember(owner = "client!xa", name = "w", descriptor = "(Z)V")
	actual override fun w(@OriginalArg(0) arg0: Boolean) {
		this.va(this.nativeid, arg0)
	}

	@OriginalMember(owner = "client!xa", name = "finalize", descriptor = "()V")
	actual fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}
}

