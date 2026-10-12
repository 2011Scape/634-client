package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb
import java.awt.Canvas
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jagdx/IDirect3D")
actual class IDirect3D : IUnknown {

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "b", descriptor = "Lclient!jaclib/peer/hb;")
	private val b: hb?

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	private constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
		this.b = arg0
	}

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "a", descriptor = "(ILclient!jagdx/D3DDISPLAYMODE;)I")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) d3DDISPLAYMODE: D3DDISPLAYMODE?): Int {
		return this._GetAdapterDisplayMode(arg0, d3DDISPLAYMODE)
	}

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "a", descriptor = "(IILjava/awt/Canvas;ILclient!jagdx/D3DPRESENT_PARAMETERS;)Lclient!jagdx/IDirect3DDevice;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) canvas: Canvas?, @OriginalArg(3) arg3: Int, @OriginalArg(4) d3DPRESENT_PARAMETERS: D3DPRESENT_PARAMETERS?): IDirect3DDevice {
		@Pc(5) val iDirect3DDevice: IDirect3DDevice = IDirect3DDevice(this.b)
		@Pc(14) val local14: Int = this._CreateDevice(arg0, arg1, canvas, arg3, d3DPRESENT_PARAMETERS, iDirect3DDevice)
		if (ue.a(97.toByte(), local14)) {
			throw fda(java.lang.String.valueOf(local14))
		}
		return iDirect3DDevice
	}

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "a", descriptor = "(II)Lclient!jagdx/D3DADAPTER_IDENTIFIER;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): D3DADAPTER_IDENTIFIER {
		@Pc(3) val d3DADAPTER_IDENTIFIER: D3DADAPTER_IDENTIFIER = D3DADAPTER_IDENTIFIER()
		@Pc(9) val local9: Int = this._GetAdapterIdentifier(arg0, arg1, d3DADAPTER_IDENTIFIER)
		if (ue.a(97.toByte(), local9)) {
			throw fda(java.lang.String.valueOf(local9))
		}
		return d3DADAPTER_IDENTIFIER
	}

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "CheckDeviceType", descriptor = "(IIIIZ)I")
	actual external fun CheckDeviceType(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Boolean): Int

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "b", descriptor = "(II)Lclient!jagdx/D3DCAPS;")
	actual fun b(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): D3DCAPS {
		@Pc(3) val d3DCAPS: D3DCAPS = D3DCAPS()
		@Pc(9) val local9: Int = this._GetDeviceCaps(arg0, arg1, d3DCAPS)
		if (ue.a(97.toByte(), local9)) {
			throw fda(java.lang.String.valueOf(local9))
		}
		return d3DCAPS
	}

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "CheckDeviceMultiSampleType", descriptor = "(IIIZI)I")
	actual external fun CheckDeviceMultiSampleType(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Boolean, @OriginalArg(4) arg4: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "_GetAdapterDisplayMode", descriptor = "(ILclient!jagdx/D3DDISPLAYMODE;)I")
	private external fun _GetAdapterDisplayMode(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: D3DDISPLAYMODE?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "_CreateDevice", descriptor = "(IILjava/awt/Canvas;ILclient!jagdx/D3DPRESENT_PARAMETERS;Lclient!jagdx/IDirect3DDevice;)I")
	private external fun _CreateDevice(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Canvas?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: D3DPRESENT_PARAMETERS?, @OriginalArg(5) arg5: IDirect3DDevice?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "CheckDepthStencilMatch", descriptor = "(IIIII)I")
	actual external fun CheckDepthStencilMatch(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "CheckDeviceFormat", descriptor = "(IIIIII)I")
	actual external fun CheckDeviceFormat(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "_GetAdapterIdentifier", descriptor = "(IILclient!jagdx/D3DADAPTER_IDENTIFIER;)I")
	private external fun _GetAdapterIdentifier(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: D3DADAPTER_IDENTIFIER?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3D", name = "_GetDeviceCaps", descriptor = "(IILclient!jagdx/D3DCAPS;)I")
	private external fun _GetDeviceCaps(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: D3DCAPS?): Int

	actual companion object {

		@OriginalMember(owner = "client!jagdx/IDirect3D", name = "a", descriptor = "(ILclient!jaclib/peer/hb;)Lclient!jagdx/IDirect3D;")
		@JvmStatic actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: hb?): IDirect3D {
			@Pc(4) val iDirect3D: IDirect3D = IDirect3D(arg1)
			@Pc(8) val local8: Int = _Direct3DCreate(arg0, iDirect3D)
			if (ue.a(97.toByte(), local8)) {
				throw fda(java.lang.String.valueOf(local8))
			}
			return iDirect3D
		}

		@OriginalMember(owner = "client!jagdx/IDirect3D", name = "_Direct3DCreate", descriptor = "(ILclient!jagdx/IDirect3D;)I")
		@JvmStatic private external fun _Direct3DCreate(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IDirect3D?): Int
	}
}
