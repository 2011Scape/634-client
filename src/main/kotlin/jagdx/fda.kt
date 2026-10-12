package jagdx

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/fda")
class fda : RuntimeException {

	@OriginalMember(owner = "client!jagdx/fda", name = "<init>", descriptor = "()V")
	constructor() : super() {
	}

	@OriginalMember(owner = "client!jagdx/fda", name = "<init>", descriptor = "(Ljava/lang/String;)V")
	constructor(@OriginalArg(0) string: String?) : super(string) {
	}
}
