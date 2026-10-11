package com.jagex.game.runetek5;

import jagdx.IDirect3DSurface;
import jagdx.IDirect3DSwapChain;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!je")
public final class D3DSwapChain {

	@OriginalMember(owner = "client!je", name = "a", descriptor = "Lclient!jagdx/IDirect3DSwapChain;")
	private IDirect3DSwapChain iDirect3DSwapChain;

	@OriginalMember(owner = "client!je", name = "b", descriptor = "Lclient!jagdx/IDirect3DSurface;")
	private IDirect3DSurface iDirect3DSurface;

	@OriginalMember(owner = "client!je", name = "c", descriptor = "Lclient!jagdx/IDirect3DSurface;")
	private IDirect3DSurface backBuffer;

	@OriginalMember(owner = "client!je", name = "<init>", descriptor = "(Lclient!jagdx/IDirect3DSwapChain;Lclient!jagdx/IDirect3DSurface;)V")
	public D3DSwapChain(@OriginalArg(0) IDirect3DSwapChain iDirect3DSwapChain, @OriginalArg(1) IDirect3DSurface iDirect3DSurface) {
		this.setSwapChain(iDirect3DSwapChain, iDirect3DSurface);
	}

	@OriginalMember(owner = "client!je", name = "a", descriptor = "(BI)I")
	public int present() {
		return this.iDirect3DSwapChain.Present(0);
	}

	@OriginalMember(owner = "client!je", name = "a", descriptor = "(B)V")
	public void release() {
		if (this.backBuffer != null) {
			this.backBuffer.a(9275);
			this.backBuffer = null;
		}
		if (this.iDirect3DSurface != null) {
			this.iDirect3DSurface.a(9275);
			this.iDirect3DSurface = null;
		}
		if (this.iDirect3DSwapChain != null) {
			this.iDirect3DSwapChain.a(9275);
			this.iDirect3DSwapChain = null;
		}
	}

	@OriginalMember(owner = "client!je", name = "a", descriptor = "(Lclient!jagdx/IDirect3DSwapChain;BLclient!jagdx/IDirect3DSurface;)V")
	public void setSwapChain(@OriginalArg(0) IDirect3DSwapChain iDirect3DSwapChain, @OriginalArg(2) IDirect3DSurface iDirect3DSurface) {
		this.iDirect3DSurface = iDirect3DSurface;
		this.iDirect3DSwapChain = iDirect3DSwapChain;
		this.backBuffer = this.iDirect3DSwapChain.a(0, 0);
	}

	@OriginalMember(owner = "client!je", name = "a", descriptor = "(I)Z")
	public boolean isValid() {
		return this.iDirect3DSwapChain != null;
	}
}
