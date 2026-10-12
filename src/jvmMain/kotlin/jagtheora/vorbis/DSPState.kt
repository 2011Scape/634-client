package jagtheora.vorbis

import jagtheora.misc.SimplePeer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/vorbis/DSPState")
actual class DSPState : SimplePeer {

	@OriginalMember(owner = "client!jagtheora/vorbis/DSPState", name = "<init>", descriptor = "(Lclient!jagtheora/vorbis/VorbisInfo;)V")
	actual constructor(@OriginalArg(0) vorbisInfo: VorbisInfo?) : super() {
		this.init(vorbisInfo)
		if (this.b()) {
			throw IllegalStateException()
		}
	}

	@OriginalMember(owner = "client!jagtheora/vorbis/DSPState", name = "pcmOut", descriptor = "(I)[[F")
	actual external fun pcmOut(@OriginalArg(0) arg0: Int): Array<FloatArray?>?

	@OriginalMember(owner = "client!jagtheora/vorbis/DSPState", name = "init", descriptor = "(Lclient!jagtheora/vorbis/VorbisInfo;)V")
	private external fun init(@OriginalArg(0) arg0: VorbisInfo?)

	@OriginalMember(owner = "client!jagtheora/vorbis/DSPState", name = "read", descriptor = "(I)V")
	actual external fun read(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!jagtheora/vorbis/DSPState", name = "blockIn", descriptor = "(Lclient!jagtheora/vorbis/VorbisBlock;)V")
	actual external fun blockIn(@OriginalArg(0) arg0: VorbisBlock?)

	@OriginalMember(owner = "client!jagtheora/vorbis/DSPState", name = "clear", descriptor = "()V")
	protected external override fun clear()

	@OriginalMember(owner = "client!jagtheora/vorbis/DSPState", name = "granuleTime", descriptor = "()D")
	actual external fun granuleTime(): Double
}
