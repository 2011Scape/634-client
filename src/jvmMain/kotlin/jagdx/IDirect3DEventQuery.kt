package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/IDirect3DEventQuery")
actual class IDirect3DEventQuery : IUnknown {

	@OriginalMember(owner = "client!jagdx/IDirect3DEventQuery", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	actual constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DEventQuery", name = "Issue", descriptor = "()I")
	actual external fun Issue(): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DEventQuery", name = "IsSignaled", descriptor = "()I")
	actual external fun IsSignaled(): Int
}
