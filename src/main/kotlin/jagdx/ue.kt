package jagdx

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/ue")
class ue {

	companion object {

		@OriginalMember(owner = "client!jagdx/ue", name = "a", descriptor = "(BI)Z")
		@JvmStatic fun a(@OriginalArg(0) arg0: Byte, @OriginalArg(1) arg1: Int): Boolean {
			return arg1 < 0
		}

		@OriginalMember(owner = "client!jagdx/ue", name = "a", descriptor = "(IZ)Z")
		@JvmStatic fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Boolean): Boolean {
			return arg0 >= 0
		}
	}
}
