package com.jagex.game.runetek5

import jagdx.IDirect3DSurface
import jagdx.IDirect3DSwapChain
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!je")
class D3DSwapChain {

	@OriginalMember(owner = "client!je", name = "a", descriptor = "Lclient!jagdx/IDirect3DSwapChain;")
	private var iDirect3DSwapChain: IDirect3DSwapChain? = null

	@OriginalMember(owner = "client!je", name = "b", descriptor = "Lclient!jagdx/IDirect3DSurface;")
	private var iDirect3DSurface: IDirect3DSurface? = null

	@OriginalMember(owner = "client!je", name = "c", descriptor = "Lclient!jagdx/IDirect3DSurface;")
	private var backBuffer: IDirect3DSurface? = null

	@OriginalMember(owner = "client!je", name = "<init>", descriptor = "(Lclient!jagdx/IDirect3DSwapChain;Lclient!jagdx/IDirect3DSurface;)V")
	constructor(@OriginalArg(0) iDirect3DSwapChain: IDirect3DSwapChain?, @OriginalArg(1) iDirect3DSurface: IDirect3DSurface?) {
		this.setSwapChain(iDirect3DSwapChain, iDirect3DSurface)
	}

	@OriginalMember(owner = "client!je", name = "a", descriptor = "(BI)I")
	fun present(): Int {
		return this.iDirect3DSwapChain!!.Present(0)
	}

	@OriginalMember(owner = "client!je", name = "a", descriptor = "(B)V")
	fun release() {
		if (this.backBuffer != null) {
			this.backBuffer!!.a(9275)
			this.backBuffer = null
		}
		if (this.iDirect3DSurface != null) {
			this.iDirect3DSurface!!.a(9275)
			this.iDirect3DSurface = null
		}
		if (this.iDirect3DSwapChain != null) {
			this.iDirect3DSwapChain!!.a(9275)
			this.iDirect3DSwapChain = null
		}
	}

	@OriginalMember(owner = "client!je", name = "a", descriptor = "(Lclient!jagdx/IDirect3DSwapChain;BLclient!jagdx/IDirect3DSurface;)V")
	fun setSwapChain(@OriginalArg(0) iDirect3DSwapChain: IDirect3DSwapChain?, @OriginalArg(2) iDirect3DSurface: IDirect3DSurface?) {
		this.iDirect3DSurface = iDirect3DSurface
		this.iDirect3DSwapChain = iDirect3DSwapChain
		this.backBuffer = this.iDirect3DSwapChain!!.a(0, 0)
	}

	@OriginalMember(owner = "client!je", name = "a", descriptor = "(I)Z")
	fun isValid(): Boolean {
		return this.iDirect3DSwapChain != null
	}
}
