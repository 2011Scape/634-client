package jagtheora.vorbis

import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/vorbis/VorbisInfo")
actual class VorbisInfo : SimplePeer {

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisInfo", name = "rate", descriptor = "I")
	@JvmField
	actual var rate: Int = 0

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisInfo", name = "channels", descriptor = "I")
	@JvmField
	actual var channels: Int = 0

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisInfo", name = "<init>", descriptor = "()V")
	actual constructor() : super() {
		this.init()
		if (this.b()) {
			throw IllegalStateException()
		}
	}

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisInfo", name = "init", descriptor = "()V")
	protected external fun init()

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisInfo", name = "headerIn", descriptor = "(Lclient!jagtheora/vorbis/VorbisComment;Lclient!jagtheora/ogg/OggPacket;)I")
	actual external fun headerIn(@OriginalArg(0) arg0: VorbisComment?, @OriginalArg(1) arg1: OggPacket?): Int

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisInfo", name = "clear", descriptor = "()V")
	protected external override fun clear()

	companion object {

		init {
			initFields()
		}

		@OriginalMember(owner = "client!jagtheora/vorbis/VorbisInfo", name = "initFields", descriptor = "()V")
		@JvmStatic private external fun initFields()
	}
}
