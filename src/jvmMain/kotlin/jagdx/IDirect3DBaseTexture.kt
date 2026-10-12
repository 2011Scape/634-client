package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/IDirect3DBaseTexture")
actual open class IDirect3DBaseTexture : IUnknown {

	@OriginalMember(owner = "client!jagdx/IDirect3DBaseTexture", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	actual constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
	}
}
