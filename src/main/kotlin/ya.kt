import com.jagex.game.runetek5.cache.node.MemoryPool
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ya")
class ya : MemoryPool, SoftwareObject {

	@OriginalMember(owner = "client!ya", name = "nativeid", descriptor = "J")
	private var nativeid: Long = 0L

	@OriginalMember(owner = "client!ya", name = "<init>", descriptor = "(Lclient!oa;I)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: Int) {
		this.aa(arg0, arg1)
	}

	@OriginalMember(owner = "client!ya", name = "w", descriptor = "(Z)V")
	external override fun w(@OriginalArg(0) arg0: Boolean)

	@OriginalMember(owner = "client!ya", name = "ga", descriptor = "()V")
	external fun ga()

	@OriginalMember(owner = "client!ya", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!ya", name = "aa", descriptor = "(Lclient!oa;I)V")
	private external fun aa(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!ya", name = "r", descriptor = "()V")
	external fun r()
}

