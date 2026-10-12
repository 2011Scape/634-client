package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jagdx/IDirect3DSwapChain")
actual class IDirect3DSwapChain : IUnknown {

	@OriginalMember(owner = "client!jagdx/IDirect3DSwapChain", name = "b", descriptor = "Lclient!jaclib/peer/hb;")
	private val b: hb?

	@OriginalMember(owner = "client!jagdx/IDirect3DSwapChain", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	actual constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
		this.b = arg0
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DSwapChain", name = "a", descriptor = "(II)Lclient!jagdx/IDirect3DSurface;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): IDirect3DSurface {
		@Pc(5) val iDirect3DSurface: IDirect3DSurface = IDirect3DSurface(this.b)
		@Pc(11) val local11: Int = this._GetBackBuffer(arg0, arg1, iDirect3DSurface)
		if (ue.a(97.toByte(), local11)) {
			throw fda(java.lang.String.valueOf(local11))
		}
		return iDirect3DSurface
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DSwapChain", name = "_GetBackBuffer", descriptor = "(IILclient!jagdx/IDirect3DSurface;)I")
	private external fun _GetBackBuffer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IDirect3DSurface?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DSwapChain", name = "Present", descriptor = "(I)I")
	actual external fun Present(@OriginalArg(0) arg0: Int): Int
}
