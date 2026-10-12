package jagtheora.ogg

import jagtheora.misc.SimplePeer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/ogg/OggStreamState")
actual class OggStreamState : SimplePeer {

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "<init>", descriptor = "(I)V")
	actual constructor(@OriginalArg(0) arg0: Int) : super() {
		if (!this.init(arg0)) {
			throw IllegalStateException()
		}
	}

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "packetPeek", descriptor = "(Lclient!jagtheora/ogg/OggPacket;)I")
	actual external fun packetPeek(@OriginalArg(0) arg0: OggPacket?): Int

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "packetPeek", descriptor = "()I")
	private external fun packetPeek(): Int

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "init", descriptor = "(I)Z")
	private external fun init(@OriginalArg(0) arg0: Int): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "isEOS", descriptor = "()Z")
	private external fun isEOS(): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "clear", descriptor = "()V")
	protected external override fun clear()

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "pageIn", descriptor = "(Lclient!jagtheora/ogg/OggPage;)Z")
	actual external fun pageIn(@OriginalArg(0) arg0: OggPage?): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "reset", descriptor = "()Z")
	private external fun reset(): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "packetOut", descriptor = "()I")
	actual external fun packetOut(): Int

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "packetOut", descriptor = "(Lclient!jagtheora/ogg/OggPacket;)I")
	actual external fun packetOut(@OriginalArg(0) arg0: OggPacket?): Int

	@OriginalMember(owner = "client!jagtheora/ogg/OggStreamState", name = "resetSerialNo", descriptor = "(I)Z")
	private external fun resetSerialNo(@OriginalArg(0) arg0: Int): Boolean
}
