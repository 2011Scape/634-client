import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ba")
actual class ba : Shadow, SoftwareObject {

	@OriginalMember(owner = "client!ba", name = "nativeid", descriptor = "J")
	private var nativeid: Long = 0L

	@OriginalMember(owner = "client!ba", name = "<init>", descriptor = "(Lclient!oa;)V")
	actual constructor(@OriginalArg(0) arg0: oa?) {
	}

	@OriginalMember(owner = "client!ba", name = "finalize", descriptor = "()V")
	actual fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!ba", name = "w", descriptor = "(Z)V")
	actual external override fun w(@OriginalArg(0) arg0: Boolean)
}

