package jagdx

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/fda")
actual class fda : RuntimeException {

	@OriginalMember(owner = "client!jagdx/fda", name = "<init>", descriptor = "()V")
	actual constructor() : super() {
	}

	@OriginalMember(owner = "client!jagdx/fda", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	actual constructor(@OriginalArg(0) string: String?) : super(string) {
	}
}
