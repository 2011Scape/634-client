package jagtheora.vorbis

import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/vorbis/VorbisBlock")
class VorbisBlock : SimplePeer {

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisBlock", name = "<init>", descriptor = "(Lclient!jagtheora/vorbis/DSPState;)V")
	constructor(@OriginalArg(0) dspState: DSPState?) : super() {
		this.init(dspState)
		if (this.b()) {
			throw IllegalStateException()
		}
	}

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisBlock", name = "clear", descriptor = "()V")
	protected external override fun clear()

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisBlock", name = "init", descriptor = "(Lclient!jagtheora/vorbis/DSPState;)V")
	private external fun init(@OriginalArg(0) arg0: DSPState?)

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisBlock", name = "synthesis", descriptor = "(Lclient!jagtheora/ogg/OggPacket;)I")
	external fun synthesis(@OriginalArg(0) arg0: OggPacket?): Int
}
