package jagtheora.vorbis

import jagtheora.misc.SimplePeer
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/vorbis/VorbisComment")
class VorbisComment : SimplePeer {

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisComment", name = "<init>", descriptor = "()V")
	constructor() : super() {
		this.init()
		if (this.b()) {
			throw IllegalStateException()
		}
	}

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisComment", name = "clear", descriptor = "()V")
	protected external override fun clear()

	@OriginalMember(owner = "client!jagtheora/vorbis/VorbisComment", name = "init", descriptor = "()V")
	protected external fun init()
}
