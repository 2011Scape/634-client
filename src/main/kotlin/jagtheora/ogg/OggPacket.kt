package jagtheora.ogg

import jagtheora.misc.SimplePeer
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/ogg/OggPacket")
class OggPacket : SimplePeer() {

	@OriginalMember(owner = "client!jagtheora/ogg/OggPacket", name = "isTheora", descriptor = "()Z")
	external fun isTheora(): Boolean

	@OriginalMember(owner = "client!jagtheora/ogg/OggPacket", name = "clear", descriptor = "()V")
	protected external override fun clear()

	@OriginalMember(owner = "client!jagtheora/ogg/OggPacket", name = "getData", descriptor = "()[B")
	external fun getData(): ByteArray?

	@OriginalMember(owner = "client!jagtheora/ogg/OggPacket", name = "isHeader", descriptor = "()I")
	private external fun isHeader(): Int

	@OriginalMember(owner = "client!jagtheora/ogg/OggPacket", name = "isKeyFrame", descriptor = "()I")
	external fun isKeyFrame(): Int

	@OriginalMember(owner = "client!jagtheora/ogg/OggPacket", name = "isVorbis", descriptor = "()Z")
	external fun isVorbis(): Boolean
}
