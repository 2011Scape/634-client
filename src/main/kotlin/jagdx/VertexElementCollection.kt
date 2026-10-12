package jagdx

import jaclib.peer.hb
import jaclib.peer.uda
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/VertexElementCollection")
class VertexElementCollection : uda {

	@OriginalMember(owner = "client!jagdx/VertexElementCollection", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
		this.init()
	}

	@OriginalMember(owner = "client!jagdx/VertexElementCollection", name = "init", descriptor = "()V")
	private external fun init()

	@OriginalMember(owner = "client!jagdx/VertexElementCollection", name = "finish", descriptor = "()V")
	external fun finish()

	@OriginalMember(owner = "client!jagdx/VertexElementCollection", name = "addElement", descriptor = "(IIIIII)V")
	external fun addElement(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int)

	@OriginalMember(owner = "client!jagdx/VertexElementCollection", name = "reset", descriptor = "()V")
	private external fun reset()
}
