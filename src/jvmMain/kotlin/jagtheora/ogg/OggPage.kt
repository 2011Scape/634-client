package jagtheora.ogg

import jagtheora.misc.SimplePeer
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/ogg/OggPage")
class OggPage : SimplePeer() {

	@OriginalMember(owner = "client!jagtheora/ogg/OggPage", name = "getVersion", descriptor = "()I")
	private external fun getVersion(): Int

	@OriginalMember(owner = "client!jagtheora/ogg/OggPage", name = "isContinued", descriptor = "()Z")
	private external fun isContinued(): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggPage", name = "getSerialNumber", descriptor = "()I")
	external fun getSerialNumber(): Int

	@OriginalMember(owner = "client!jagtheora/ogg/OggPage", name = "isEOS", descriptor = "()Z")
	private external fun isEOS(): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggPage", name = "getGranulePos", descriptor = "()J")
	private external fun getGranulePos(): Long

	@OriginalMember(owner = "client!jagtheora/ogg/OggPage", name = "isBOS", descriptor = "()Z")
	external fun isBOS(): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggPage", name = "clear", descriptor = "()V")
	protected external override fun clear()

	@OriginalMember(owner = "client!jagtheora/ogg/OggPage", name = "getPageNumber", descriptor = "()J")
	private external fun getPageNumber(): Long

	@OriginalMember(owner = "client!jagtheora/ogg/OggPage", name = "getCompletedPackets", descriptor = "()I")
	private external fun getCompletedPackets(): Int
}
