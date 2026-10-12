package jagtheora.theora

import jagtheora.misc.SimplePeer
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/theora/TheoraInfo")
actual class TheoraInfo : SimplePeer {

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "colourSpace", descriptor = "I")
	private var colourSpace: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "versionSubMinor", descriptor = "B")
	private var versionSubMinor: Byte = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "fpsNumerator", descriptor = "I")
	@JvmField
	actual var fpsNumerator: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "pixelFormat", descriptor = "I")
	private var pixelFormat: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "picY", descriptor = "I")
	private var picY: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "aspectDenominator", descriptor = "I")
	private var aspectDenominator: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "picWidth", descriptor = "I")
	private var picWidth: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "versionMinor", descriptor = "B")
	private var versionMinor: Byte = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "picX", descriptor = "I")
	private var picX: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "picHeight", descriptor = "I")
	private var picHeight: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "fpsDenominator", descriptor = "I")
	@JvmField
	actual var fpsDenominator: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "versionMajor", descriptor = "B")
	private var versionMajor: Byte = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "frameWidth", descriptor = "I")
	@JvmField
	actual var frameWidth: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "aspectNumerator", descriptor = "I")
	private var aspectNumerator: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "frameHeight", descriptor = "I")
	@JvmField
	actual var frameHeight: Int = 0

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "<init>", descriptor = "()V")
	actual constructor() : super() {
		this.init()
		if (this.b()) {
			throw IllegalStateException()
		}
	}

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "clear", descriptor = "()V")
	protected external override fun clear()

	@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "init", descriptor = "()V")
	protected external fun init()

	companion object {

		init {
			initFields()
		}

		@OriginalMember(owner = "client!jagtheora/theora/TheoraInfo", name = "initFields", descriptor = "()V")
		@JvmStatic private external fun initFields()
	}
}
