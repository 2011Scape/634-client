package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/IDirect3DSurface")
actual class IDirect3DSurface : IUnknown {

	@OriginalMember(owner = "client!jagdx/IDirect3DSurface", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	actual constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DSurface", name = "LockRect", descriptor = "(IIIIILclient!jagdx/PixelBuffer;)I")
	actual external fun LockRect(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: PixelBuffer?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DSurface", name = "UnlockRect", descriptor = "()Z")
	actual external fun UnlockRect(): Boolean
}
