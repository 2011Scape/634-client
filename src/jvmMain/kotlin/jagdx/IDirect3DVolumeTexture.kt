package jagdx

import jaclib.peer.hb
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/IDirect3DVolumeTexture")
class IDirect3DVolumeTexture : IDirect3DBaseTexture {

	@OriginalMember(owner = "client!jagdx/IDirect3DVolumeTexture", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DVolumeTexture", name = "UnlockBox", descriptor = "(I)Z")
	external fun UnlockBox(@OriginalArg(0) arg0: Int): Boolean

	@OriginalMember(owner = "client!jagdx/IDirect3DVolumeTexture", name = "LockBox", descriptor = "(IIIIIIIILclient!jagdx/PixelBuffer;)I")
	external fun LockBox(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: PixelBuffer?): Int
}
