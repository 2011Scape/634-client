/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import jaclib.peer.IUnknown
import jaclib.peer.hb
import nativeUnavailable

actual class IDirect3DDevice : IUnknown {
	actual constructor(arg0: hb?) : super(nativeUnavailable())
	actual fun b(): IDirect3DEventQuery? {
		throw NativeUnavailableException("b")
	}
	actual fun b(arg0: Int): IDirect3DSwapChain {
		throw NativeUnavailableException("b")
	}
	actual fun SetStreamSource(arg0: Int, arg1: IDirect3DVertexBuffer?, arg2: Int, arg3: Int): Int {
		throw NativeUnavailableException("SetStreamSource")
	}
	actual fun a(arg0: Int, arg1: FloatArray?): Int {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Boolean): Int {
		throw NativeUnavailableException("a")
	}
	actual fun Clear(arg0: Int, arg1: Int, arg2: Float, arg3: Int): Int {
		throw NativeUnavailableException("Clear")
	}
	actual fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): IDirect3DTexture {
		throw NativeUnavailableException("a")
	}
	actual fun EndScene(): Int {
		throw NativeUnavailableException("EndScene")
	}
	actual fun SetTransform(arg0: Int, arg1: FloatArray?): Int {
		throw NativeUnavailableException("SetTransform")
	}
	actual fun a(arg0: Int, arg1: Float): Int {
		throw NativeUnavailableException("a")
	}
	actual fun BeginScene(): Int {
		throw NativeUnavailableException("BeginScene")
	}
	actual fun SetViewport(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Float, arg5: Float): Int {
		throw NativeUnavailableException("SetViewport")
	}
	actual fun SetLight(arg0: Int, arg1: D3DLIGHT?): Int {
		throw NativeUnavailableException("SetLight")
	}
	actual fun a(arg0: ByteArray?): IDirect3DPixelShader? {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int): IDirect3DCubeTexture {
		throw NativeUnavailableException("a")
	}
	actual fun Reset(arg0: D3DPRESENT_PARAMETERS?): Int {
		throw NativeUnavailableException("Reset")
	}
	actual fun SetScissorRect(arg0: Int, arg1: Int, arg2: Int, arg3: Int): Int {
		throw NativeUnavailableException("SetScissorRect")
	}
	actual fun DrawIndexedPrimitive(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): Int {
		throw NativeUnavailableException("DrawIndexedPrimitive")
	}
	actual fun SetTextureStageState(arg0: Int, arg1: Int, arg2: Int): Int {
		throw NativeUnavailableException("SetTextureStageState")
	}
	actual fun SetSamplerState(arg0: Int, arg1: Int, arg2: Int): Int {
		throw NativeUnavailableException("SetSamplerState")
	}
	actual fun SetVertexDeclaration(arg0: IDirect3DVertexDeclaration?): Int {
		throw NativeUnavailableException("SetVertexDeclaration")
	}
	actual fun DrawPrimitive(arg0: Int, arg1: Int, arg2: Int): Int {
		throw NativeUnavailableException("DrawPrimitive")
	}
	actual fun SetPixelShader(arg0: IDirect3DPixelShader?): Int {
		throw NativeUnavailableException("SetPixelShader")
	}
	actual fun SetVertexShaderConstantF(arg0: Int, arg1: FloatArray?, arg2: Int): Int {
		throw NativeUnavailableException("SetVertexShaderConstantF")
	}
	actual fun LightEnable(arg0: Int, arg1: Boolean): Boolean {
		throw NativeUnavailableException("LightEnable")
	}
	actual fun c(arg0: Int): IDirect3DSurface {
		throw NativeUnavailableException("c")
	}
	actual fun c(): IDirect3DSurface {
		throw NativeUnavailableException("c")
	}
	actual fun StretchRect(arg0: IDirect3DSurface?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: IDirect3DSurface?, arg6: Int, arg7: Int, arg8: Int, arg9: Int, arg10: Int): Int {
		throw NativeUnavailableException("StretchRect")
	}
	actual fun SetIndices(arg0: IDirect3DIndexBuffer?): Int {
		throw NativeUnavailableException("SetIndices")
	}
	actual fun a(vertexElementCollection: VertexElementCollection?, iDirect3DVertexDeclaration: IDirect3DVertexDeclaration?): IDirect3DVertexDeclaration {
		throw NativeUnavailableException("a")
	}
	actual fun b(arg0: ByteArray?): IDirect3DVertexShader? {
		throw NativeUnavailableException("b")
	}
	actual fun SetVertexShader(arg0: IDirect3DVertexShader?): Int {
		throw NativeUnavailableException("SetVertexShader")
	}
	actual fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, iDirect3DIndexBuffer: IDirect3DIndexBuffer?): IDirect3DIndexBuffer {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Float, arg2: Float, arg3: Float, arg4: Float): Int {
		throw NativeUnavailableException("a")
	}
	actual fun b(arg0: Int, arg1: Float, arg2: Float, arg3: Float, arg4: Float): Int {
		throw NativeUnavailableException("b")
	}
	actual fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int): IDirect3DVolumeTexture {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean): IDirect3DSurface {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, iDirect3DVertexBuffer: IDirect3DVertexBuffer?): IDirect3DVertexBuffer {
		throw NativeUnavailableException("a")
	}
	actual fun SetTexture(arg0: Int, arg1: IDirect3DBaseTexture?): Int {
		throw NativeUnavailableException("SetTexture")
	}
	actual fun SetRenderState(arg0: Int, arg1: Int): Int {
		throw NativeUnavailableException("SetRenderState")
	}
	actual fun TestCooperativeLevel(): Int {
		throw NativeUnavailableException("TestCooperativeLevel")
	}
}
