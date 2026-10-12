package jagdx

import jaclib.peer.hb
import jaclib.peer.uda
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/D3DLIGHT")
actual class D3DLIGHT : uda {

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	actual constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
		this.Init()
	}

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "SetAttenuation", descriptor = "(FFF)V")
	actual external fun SetAttenuation(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "SetDiffuse", descriptor = "(FFFF)V")
	actual external fun SetDiffuse(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float)

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "Init", descriptor = "()V")
	private external fun Init()

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "SetSpecular", descriptor = "(FFFF)V")
	private external fun SetSpecular(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float)

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "SetAmbient", descriptor = "(FFFF)V")
	actual external fun SetAmbient(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float)

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "SetSpotParams", descriptor = "(FFF)V")
	private external fun SetSpotParams(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "SetPosition", descriptor = "(FFF)V")
	actual external fun SetPosition(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "SetDirection", descriptor = "(FFF)V")
	actual external fun SetDirection(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float)

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "SetRange", descriptor = "(F)V")
	actual external fun SetRange(@OriginalArg(0) arg0: Float)

	@OriginalMember(owner = "client!jagdx/D3DLIGHT", name = "SetType", descriptor = "(I)V")
	actual external fun SetType(@OriginalArg(0) arg0: Int)
}
