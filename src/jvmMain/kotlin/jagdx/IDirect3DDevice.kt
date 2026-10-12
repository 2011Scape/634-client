package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jagdx/IDirect3DDevice")
actual class IDirect3DDevice : IUnknown {

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "b", descriptor = "Lclient!jaclib/peer/hb;")
	private val b: hb?

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "<init>", descriptor = "(Lclient!jaclib/peer/hb;)V")
	actual constructor(@OriginalArg(0) arg0: hb?) : super(arg0) {
		this.b = arg0
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "b", descriptor = "()Lclient!jagdx/IDirect3DEventQuery;")
	actual fun b(): IDirect3DEventQuery? {
		@Pc(5) val iDirect3DEventQuery: IDirect3DEventQuery = IDirect3DEventQuery(this.b)
		if (ue.a(this._CreateEventQuery(iDirect3DEventQuery), false)) {
			return if (iDirect3DEventQuery.a(true)) iDirect3DEventQuery else null
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "b", descriptor = "(I)Lclient!jagdx/IDirect3DSwapChain;")
	actual fun b(@OriginalArg(0) arg0: Int): IDirect3DSwapChain {
		@Pc(5) val iDirect3DSwapChain: IDirect3DSwapChain = IDirect3DSwapChain(this.b)
		@Pc(10) val local10: Int = this._GetSwapChain(arg0, iDirect3DSwapChain)
		if (ue.a(97.toByte(), local10)) {
			throw fda(java.lang.String.valueOf(local10))
		}
		return iDirect3DSwapChain
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetStreamSource", descriptor = "(ILclient!jagdx/IDirect3DVertexBuffer;II)I")
	actual external fun SetStreamSource(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IDirect3DVertexBuffer?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateEventQuery", descriptor = "(Lclient!jagdx/IDirect3DEventQuery;)I")
	private external fun _CreateEventQuery(@OriginalArg(0) arg0: IDirect3DEventQuery?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(I[F)I")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?): Int {
		return this.SetVertexShaderConstantF(arg0, arg1, arg1!!.size / 4)
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateCubeTexture", descriptor = "(IIIIILclient!jagdx/IDirect3DCubeTexture;)I")
	private external fun _CreateCubeTexture(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: IDirect3DCubeTexture?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(IZ)I")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Boolean): Int {
		return this.SetRenderStateb(arg0, arg1)
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetRenderStateb", descriptor = "(IZ)I")
	private external fun SetRenderStateb(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Boolean): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "Clear", descriptor = "(IIFI)I")
	actual external fun Clear(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateVertexBuffer", descriptor = "(IIIILclient!jagdx/IDirect3DVertexBuffer;)I")
	private external fun _CreateVertexBuffer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IDirect3DVertexBuffer?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(IIIIII)Lclient!jagdx/IDirect3DTexture;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int): IDirect3DTexture {
		@Pc(5) val iDirect3DTexture: IDirect3DTexture = IDirect3DTexture(this.b)
		@Pc(15) val local15: Int = this._CreateTexture(arg0, arg1, arg2, arg3, arg4, arg5, iDirect3DTexture)
		if (ue.a(97.toByte(), local15)) {
			throw fda(java.lang.String.valueOf(local15))
		}
		return iDirect3DTexture
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "EndScene", descriptor = "()I")
	actual external fun EndScene(): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateVolumeTexture", descriptor = "(IIIIIIILclient!jagdx/IDirect3DVolumeTexture;)I")
	private external fun _CreateVolumeTexture(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: IDirect3DVolumeTexture?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetTransform", descriptor = "(I[F)I")
	actual external fun SetTransform(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetRenderStatef", descriptor = "(IF)I")
	private external fun SetRenderStatef(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(IF)I")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float): Int {
		return this.SetRenderStatef(arg0, arg1)
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "BeginScene", descriptor = "()I")
	actual external fun BeginScene(): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_GetBackBuffer", descriptor = "(IIILclient!jagdx/IDirect3DSurface;)I")
	private external fun _GetBackBuffer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: IDirect3DSurface?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetViewport", descriptor = "(IIIIFF)I")
	actual external fun SetViewport(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Float, @OriginalArg(5) arg5: Float): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetLight", descriptor = "(ILclient!jagdx/D3DLIGHT;)I")
	actual external fun SetLight(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: D3DLIGHT?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "([B)Lclient!jagdx/IDirect3DPixelShader;")
	actual fun a(@OriginalArg(0) arg0: ByteArray?): IDirect3DPixelShader? {
		if (arg0 == null) {
			return null
		}
		@Pc(11) val iDirect3DPixelShader: IDirect3DPixelShader = IDirect3DPixelShader(this.b)
		@Pc(16) val local16: Int = this._CreatePixelShader(arg0, iDirect3DPixelShader)
		if (ue.a(97.toByte(), local16)) {
			throw fda(java.lang.String.valueOf(local16))
		}
		return iDirect3DPixelShader
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(IIIII)Lclient!jagdx/IDirect3DCubeTexture;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int): IDirect3DCubeTexture {
		@Pc(5) val iDirect3DCubeTexture: IDirect3DCubeTexture = IDirect3DCubeTexture(this.b)
		@Pc(14) val local14: Int = this._CreateCubeTexture(arg0, arg1, arg2, arg3, arg4, iDirect3DCubeTexture)
		if (ue.a(97.toByte(), local14)) {
			throw fda(java.lang.String.valueOf(local14))
		}
		return iDirect3DCubeTexture
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "Reset", descriptor = "(Lclient!jagdx/D3DPRESENT_PARAMETERS;)I")
	actual external fun Reset(@OriginalArg(0) arg0: D3DPRESENT_PARAMETERS?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetScissorRect", descriptor = "(IIII)I")
	actual external fun SetScissorRect(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "DrawIndexedPrimitive", descriptor = "(IIIIII)I")
	actual external fun DrawIndexedPrimitive(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetPixelShaderConstantF", descriptor = "(I[FI)I")
	private external fun SetPixelShaderConstantF(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?, @OriginalArg(2) arg2: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateDepthStencilSurface", descriptor = "(IIIIIZLclient!jagdx/IDirect3DSurface;)I")
	private external fun _CreateDepthStencilSurface(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean, @OriginalArg(6) arg6: IDirect3DSurface?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateVertexDeclaration", descriptor = "(Lclient!jagdx/VertexElementCollection;Lclient!jagdx/IDirect3DVertexDeclaration;)I")
	private external fun _CreateVertexDeclaration(@OriginalArg(0) arg0: VertexElementCollection?, @OriginalArg(1) arg1: IDirect3DVertexDeclaration?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_GetDepthStencilSurface", descriptor = "(Lclient!jagdx/IDirect3DSurface;)I")
	private external fun _GetDepthStencilSurface(@OriginalArg(0) arg0: IDirect3DSurface?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetTextureStageState", descriptor = "(III)I")
	actual external fun SetTextureStageState(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetSamplerState", descriptor = "(III)I")
	actual external fun SetSamplerState(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateRenderTarget", descriptor = "(IIIIIZLclient!jagdx/IDirect3DSurface;)I")
	private external fun _CreateRenderTarget(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean, @OriginalArg(6) arg6: IDirect3DSurface?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetFVF", descriptor = "(I)I")
	private external fun SetFVF(@OriginalArg(0) arg0: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetVertexDeclaration", descriptor = "(Lclient!jagdx/IDirect3DVertexDeclaration;)I")
	actual external fun SetVertexDeclaration(@OriginalArg(0) arg0: IDirect3DVertexDeclaration?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "DrawPrimitive", descriptor = "(III)I")
	actual external fun DrawPrimitive(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateTexture", descriptor = "(IIIIIILclient!jagdx/IDirect3DTexture;)I")
	private external fun _CreateTexture(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: IDirect3DTexture?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateIndexBuffer", descriptor = "(IIIILclient!jagdx/IDirect3DIndexBuffer;)I")
	private external fun _CreateIndexBuffer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IDirect3DIndexBuffer?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetPixelShader", descriptor = "(Lclient!jagdx/IDirect3DPixelShader;)I")
	actual external fun SetPixelShader(@OriginalArg(0) arg0: IDirect3DPixelShader?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetVertexShaderConstantF", descriptor = "(I[FI)I")
	actual external fun SetVertexShaderConstantF(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: FloatArray?, @OriginalArg(2) arg2: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "LightEnable", descriptor = "(IZ)Z")
	actual external fun LightEnable(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Boolean): Boolean

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "c", descriptor = "(I)Lclient!jagdx/IDirect3DSurface;")
	actual fun c(@OriginalArg(0) arg0: Int): IDirect3DSurface {
		@Pc(5) val iDirect3DSurface: IDirect3DSurface = IDirect3DSurface(this.b)
		@Pc(10) val local10: Int = this._GetRenderTarget(arg0, iDirect3DSurface)
		if (ue.a(97.toByte(), local10)) {
			throw fda(java.lang.String.valueOf(local10))
		}
		return iDirect3DSurface
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "c", descriptor = "()Lclient!jagdx/IDirect3DSurface;")
	actual fun c(): IDirect3DSurface {
		@Pc(5) val iDirect3DSurface: IDirect3DSurface = IDirect3DSurface(this.b)
		@Pc(9) val local9: Int = this._GetDepthStencilSurface(iDirect3DSurface)
		if (ue.a(97.toByte(), local9)) {
			throw fda(java.lang.String.valueOf(local9))
		}
		return iDirect3DSurface
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "StretchRect", descriptor = "(Lclient!jagdx/IDirect3DSurface;IIIILclient!jagdx/IDirect3DSurface;IIIII)I")
	actual external fun StretchRect(@OriginalArg(0) arg0: IDirect3DSurface?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: IDirect3DSurface?, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int, @OriginalArg(9) arg9: Int, @OriginalArg(10) arg10: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetIndices", descriptor = "(Lclient!jagdx/IDirect3DIndexBuffer;)I")
	actual external fun SetIndices(@OriginalArg(0) arg0: IDirect3DIndexBuffer?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(Lclient!jagdx/VertexElementCollection;Lclient!jagdx/IDirect3DVertexDeclaration;)Lclient!jagdx/IDirect3DVertexDeclaration;")
	actual fun a(@OriginalArg(0) vertexElementCollection: VertexElementCollection?, @OriginalArg(1) iDirect3DVertexDeclaration: IDirect3DVertexDeclaration?): IDirect3DVertexDeclaration {
		var iDirect3DVertexDeclaration: IDirect3DVertexDeclaration? = iDirect3DVertexDeclaration
		if (iDirect3DVertexDeclaration == null) {
			iDirect3DVertexDeclaration = IDirect3DVertexDeclaration(this.b)
		} else {
			iDirect3DVertexDeclaration.a(9275)
		}
		@Pc(18) val local18: Int = this._CreateVertexDeclaration(vertexElementCollection, iDirect3DVertexDeclaration)
		if (ue.a(97.toByte(), local18)) {
			throw fda(java.lang.String.valueOf(local18))
		}
		return iDirect3DVertexDeclaration
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "b", descriptor = "([B)Lclient!jagdx/IDirect3DVertexShader;")
	actual fun b(@OriginalArg(0) arg0: ByteArray?): IDirect3DVertexShader? {
		if (arg0 == null) {
			return null
		}
		@Pc(11) val iDirect3DVertexShader: IDirect3DVertexShader = IDirect3DVertexShader(this.b)
		@Pc(16) val local16: Int = this._CreateVertexShader(arg0, iDirect3DVertexShader)
		if (ue.a(97.toByte(), local16)) {
			throw fda(java.lang.String.valueOf(local16))
		}
		return iDirect3DVertexShader
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_GetRenderTarget", descriptor = "(ILclient!jagdx/IDirect3DSurface;)I")
	private external fun _GetRenderTarget(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IDirect3DSurface?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetVertexShader", descriptor = "(Lclient!jagdx/IDirect3DVertexShader;)I")
	actual external fun SetVertexShader(@OriginalArg(0) arg0: IDirect3DVertexShader?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(IIIILclient!jagdx/IDirect3DIndexBuffer;)Lclient!jagdx/IDirect3DIndexBuffer;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) iDirect3DIndexBuffer: IDirect3DIndexBuffer?): IDirect3DIndexBuffer {
		var iDirect3DIndexBuffer: IDirect3DIndexBuffer? = iDirect3DIndexBuffer
		if (iDirect3DIndexBuffer == null) {
			iDirect3DIndexBuffer = IDirect3DIndexBuffer(this.b)
		} else {
			iDirect3DIndexBuffer.a(9275)
		}
		@Pc(21) val local21: Int = this._CreateIndexBuffer(arg0, arg1, arg2, arg3, iDirect3DIndexBuffer)
		if (ue.a(97.toByte(), local21)) {
			throw fda(java.lang.String.valueOf(local21))
		}
		return iDirect3DIndexBuffer
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateVertexShader", descriptor = "([BLclient!jagdx/IDirect3DVertexShader;)I")
	private external fun _CreateVertexShader(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: IDirect3DVertexShader?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(IFFFF)I")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float): Int {
		c[0] = arg1
		c[2] = arg3
		c[3] = arg4
		c[1] = arg2
		return this.SetVertexShaderConstantF(arg0, c, 1)
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "b", descriptor = "(IFFFF)I")
	actual fun b(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float): Int {
		c[3] = arg4
		c[0] = arg1
		c[1] = arg2
		c[2] = arg3
		return this.SetPixelShaderConstantF(arg0, c, 1)
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(IIIIIII)Lclient!jagdx/IDirect3DVolumeTexture;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int): IDirect3DVolumeTexture {
		@Pc(5) val iDirect3DVolumeTexture: IDirect3DVolumeTexture = IDirect3DVolumeTexture(this.b)
		@Pc(16) val local16: Int = this._CreateVolumeTexture(arg0, arg1, arg2, arg3, arg4, arg5, arg6, iDirect3DVolumeTexture)
		if (ue.a(97.toByte(), local16)) {
			throw fda(java.lang.String.valueOf(local16))
		}
		return iDirect3DVolumeTexture
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(IIIIIZ)Lclient!jagdx/IDirect3DSurface;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean): IDirect3DSurface {
		@Pc(5) val iDirect3DSurface: IDirect3DSurface = IDirect3DSurface(this.b)
		@Pc(15) val local15: Int = this._CreateRenderTarget(arg0, arg1, arg2, arg3, arg4, arg5, iDirect3DSurface)
		if (ue.a(97.toByte(), local15)) {
			throw fda(java.lang.String.valueOf(local15))
		}
		return iDirect3DSurface
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "a", descriptor = "(IIIILclient!jagdx/IDirect3DVertexBuffer;)Lclient!jagdx/IDirect3DVertexBuffer;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) iDirect3DVertexBuffer: IDirect3DVertexBuffer?): IDirect3DVertexBuffer {
		var iDirect3DVertexBuffer: IDirect3DVertexBuffer? = iDirect3DVertexBuffer
		if (iDirect3DVertexBuffer == null) {
			iDirect3DVertexBuffer = IDirect3DVertexBuffer(this.b)
		} else {
			iDirect3DVertexBuffer.a(9275)
		}
		@Pc(20) val local20: Int = this._CreateVertexBuffer(arg0, arg1, arg2, arg3, iDirect3DVertexBuffer)
		if (ue.a(97.toByte(), local20)) {
			throw fda(java.lang.String.valueOf(local20))
		}
		iDirect3DVertexBuffer.b = arg0
		return iDirect3DVertexBuffer
	}

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetTexture", descriptor = "(ILclient!jagdx/IDirect3DBaseTexture;)I")
	actual external fun SetTexture(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IDirect3DBaseTexture?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "SetRenderState", descriptor = "(II)I")
	actual external fun SetRenderState(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "TestCooperativeLevel", descriptor = "()I")
	actual external fun TestCooperativeLevel(): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreateOffscreenPlainSurface", descriptor = "(IIIILclient!jagdx/IDirect3DSurface;)I")
	private external fun _CreateOffscreenPlainSurface(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IDirect3DSurface?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_CreatePixelShader", descriptor = "([BLclient!jagdx/IDirect3DPixelShader;)I")
	private external fun _CreatePixelShader(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: IDirect3DPixelShader?): Int

	@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "_GetSwapChain", descriptor = "(ILclient!jagdx/IDirect3DSwapChain;)I")
	private external fun _GetSwapChain(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IDirect3DSwapChain?): Int

	companion object {

		@OriginalMember(owner = "client!jagdx/IDirect3DDevice", name = "c", descriptor = "[F")
		private val c: FloatArray = FloatArray(4)
	}
}
