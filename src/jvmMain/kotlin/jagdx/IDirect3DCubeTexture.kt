package jagdx

import jaclib.peer.hb
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/IDirect3DCubeTexture")
class IDirect3DCubeTexture : IDirect3DBaseTexture {

	@OriginalMember(owner = "client!jagdx/IDirect3DCubeTexture", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DCubeTexture", name = "LockRect", descriptor = "(IIIIIIILclient!jagdx/PixelBuffer;)I")
	external fun LockRect(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: PixelBuffer?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DCubeTexture", name = "UnlockRect", descriptor = "(II)Z")
	external fun UnlockRect(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): Boolean
}
