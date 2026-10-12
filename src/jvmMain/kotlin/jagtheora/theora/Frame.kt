package jagtheora.theora

import jagtheora.misc.SimplePeer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagtheora/theora/Frame")
actual class Frame : SimplePeer {

	@OriginalMember(owner = "client!jagtheora/theora/Frame", name = "b", descriptor = "I")
	@JvmField
	actual val b: Int

	@OriginalMember(owner = "client!jagtheora/theora/Frame", name = "a", descriptor = "I")
	@JvmField
	actual val a: Int

	@OriginalMember(owner = "client!jagtheora/theora/Frame", name = "pixels", descriptor = "[I")
	@JvmField
	actual val pixels: IntArray

	@OriginalMember(owner = "client!jagtheora/theora/Frame", name = "<init>", descriptor = "(II)V")
	actual constructor(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) : super() {
		this.b = arg1
		this.a = arg0
		this.pixels = IntArray(this.a * this.b)
	}

	@OriginalMember(owner = "client!jagtheora/theora/Frame", name = "clear", descriptor = "()V")
	protected external override fun clear()

	companion object {

		init {
			init()
		}

		@OriginalMember(owner = "client!jagtheora/theora/Frame", name = "init", descriptor = "()V")
		@JvmStatic private external fun init()
	}
}
