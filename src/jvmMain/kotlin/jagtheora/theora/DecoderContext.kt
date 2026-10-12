package jagtheora.theora

import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/theora/DecoderContext")
actual class DecoderContext : SimplePeer {

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "<init>", descriptor = "(Lclient!jagtheora/theora/TheoraInfo;Lclient!jagtheora/theora/SetupInfo;)V")
	actual constructor(@OriginalArg(0) theoraInfo: TheoraInfo?, @OriginalArg(1) setupInfo: SetupInfo?) : super() {
		this.init(theoraInfo, setupInfo)
		if (this.b()) {
			throw IllegalArgumentException("")
		}
	}

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "granuleFrame", descriptor = "(Lclient!jagtheora/theora/GranulePos;)J")
	actual external fun granuleFrame(@OriginalArg(0) arg0: GranulePos?): Long

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "clear", descriptor = "()V")
	protected external override fun clear()

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "setPostProcessingLevel", descriptor = "(I)I")
	actual external fun setPostProcessingLevel(@OriginalArg(0) arg0: Int): Int

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "decodePacketIn", descriptor = "(Lclient!jagtheora/ogg/OggPacket;Lclient!jagtheora/theora/GranulePos;)I")
	actual external fun decodePacketIn(@OriginalArg(0) arg0: OggPacket?, @OriginalArg(1) arg1: GranulePos?): Int

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "getMaxPostProcessingLevel", descriptor = "()I")
	actual external fun getMaxPostProcessingLevel(): Int

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "granuleTime", descriptor = "(Lclient!jagtheora/theora/GranulePos;)D")
	actual external fun granuleTime(@OriginalArg(0) arg0: GranulePos?): Double

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "setGranulePosition", descriptor = "(J)I")
	private external fun setGranulePosition(@OriginalArg(0) arg0: Long): Int

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "init", descriptor = "(Lclient!jagtheora/theora/TheoraInfo;Lclient!jagtheora/theora/SetupInfo;)V")
	private external fun init(@OriginalArg(0) arg0: TheoraInfo?, @OriginalArg(1) arg1: SetupInfo?)

	@OriginalMember(owner = "client!jagtheora/theora/DecoderContext", name = "decodeFrame", descriptor = "(Lclient!jagtheora/theora/Frame;)I")
	actual external fun decodeFrame(@OriginalArg(0) arg0: Frame?): Int
}
