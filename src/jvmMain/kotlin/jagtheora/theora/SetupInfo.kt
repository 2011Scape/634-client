package jagtheora.theora

import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/theora/SetupInfo")
actual class SetupInfo actual constructor() : SimplePeer() {

	@OriginalMember(owner = "client!jagtheora/theora/SetupInfo", name = "decodeHeader", descriptor = "(Lclient!jagtheora/theora/TheoraInfo;Lclient!jagtheora/theora/TheoraComment;Lclient!jagtheora/ogg/OggPacket;)I")
	actual external fun decodeHeader(@OriginalArg(0) arg0: TheoraInfo?, @OriginalArg(1) arg1: TheoraComment?, @OriginalArg(2) arg2: OggPacket?): Int

	@OriginalMember(owner = "client!jagtheora/theora/SetupInfo", name = "clear", descriptor = "()V")
	protected external override fun clear()
}
