package jagtheora.theora

import jagtheora.misc.SimplePeer
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/theora/GranulePos")
class GranulePos : SimplePeer() {

	@OriginalMember(owner = "client!jagtheora/theora/GranulePos", name = "position", descriptor = "J")
	private var position: Long = 0L

	@OriginalMember(owner = "client!jagtheora/theora/GranulePos", name = "clear", descriptor = "()V")
	protected external override fun clear()

	companion object {

		init {
			init()
		}

		@OriginalMember(owner = "client!jagtheora/theora/GranulePos", name = "init", descriptor = "()V")
		@JvmStatic private external fun init()
	}
}
