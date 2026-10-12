package jagtheora.ogg

import jagtheora.misc.SimplePeer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/ogg/OggSyncState")
class OggSyncState : SimplePeer {

	@OriginalMember(owner = "client!jagtheora/ogg/OggSyncState", name = "<init>", descriptor = "()V")
	constructor() : super() {
		this.init()
		if (this.b()) {
			throw IllegalStateException()
		}
	}

	@OriginalMember(owner = "client!jagtheora/ogg/OggSyncState", name = "reset", descriptor = "()Z")
	private external fun reset(): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggSyncState", name = "init", descriptor = "()V")
	protected external fun init()

	@OriginalMember(owner = "client!jagtheora/ogg/OggSyncState", name = "clear", descriptor = "()V")
	protected external override fun clear()

	@OriginalMember(owner = "client!jagtheora/ogg/OggSyncState", name = "write", descriptor = "([BI)Z")
	external fun write(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: Int): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggSyncState", name = "pageOut", descriptor = "(Lclient!jagtheora/ogg/OggPage;)I")
	external fun pageOut(@OriginalArg(0) arg0: OggPage?): Int

	@OriginalMember(owner = "client!jagtheora/ogg/OggSyncState", name = "pageSeek", descriptor = "(Lclient!jagtheora/ogg/OggPage;)J")
	private external fun pageSeek(@OriginalArg(0) arg0: OggPage?): Long
}
