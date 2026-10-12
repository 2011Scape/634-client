/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/IDirect3DDevice.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb

expect class IDirect3DDevice : IUnknown {
	constructor(arg0: hb?)
	fun b(): IDirect3DEventQuery?
	fun b(arg0: Int): IDirect3DSwapChain
	fun SetStreamSource(arg0: Int, arg1: IDirect3DVertexBuffer?, arg2: Int, arg3: Int): Int
	fun a(arg0: Int, arg1: FloatArray?): Int
	fun a(arg0: Int, arg1: Boolean): Int
	fun Clear(arg0: Int, arg1: Int, arg2: Float, arg3: Int): Int
	fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): IDirect3DTexture
	fun EndScene(): Int
	fun SetTransform(arg0: Int, arg1: FloatArray?): Int
	fun a(arg0: Int, arg1: Float): Int
	fun BeginScene(): Int
	fun SetViewport(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Float, arg5: Float): Int
	fun SetLight(arg0: Int, arg1: D3DLIGHT?): Int
	fun a(arg0: ByteArray?): IDirect3DPixelShader?
	fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int): IDirect3DCubeTexture
	fun Reset(arg0: D3DPRESENT_PARAMETERS?): Int
	fun SetScissorRect(arg0: Int, arg1: Int, arg2: Int, arg3: Int): Int
	fun DrawIndexedPrimitive(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): Int
	fun SetTextureStageState(arg0: Int, arg1: Int, arg2: Int): Int
	fun SetSamplerState(arg0: Int, arg1: Int, arg2: Int): Int
	fun SetVertexDeclaration(arg0: IDirect3DVertexDeclaration?): Int
	fun DrawPrimitive(arg0: Int, arg1: Int, arg2: Int): Int
	fun SetPixelShader(arg0: IDirect3DPixelShader?): Int
	fun SetVertexShaderConstantF(arg0: Int, arg1: FloatArray?, arg2: Int): Int
	fun LightEnable(arg0: Int, arg1: Boolean): Boolean
	fun c(arg0: Int): IDirect3DSurface
	fun c(): IDirect3DSurface
	fun StretchRect(arg0: IDirect3DSurface?, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: IDirect3DSurface?, arg6: Int, arg7: Int, arg8: Int, arg9: Int, arg10: Int): Int
	fun SetIndices(arg0: IDirect3DIndexBuffer?): Int
	fun a(vertexElementCollection: VertexElementCollection?, iDirect3DVertexDeclaration: IDirect3DVertexDeclaration?): IDirect3DVertexDeclaration
	fun b(arg0: ByteArray?): IDirect3DVertexShader?
	fun SetVertexShader(arg0: IDirect3DVertexShader?): Int
	fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, iDirect3DIndexBuffer: IDirect3DIndexBuffer?): IDirect3DIndexBuffer
	fun a(arg0: Int, arg1: Float, arg2: Float, arg3: Float, arg4: Float): Int
	fun b(arg0: Int, arg1: Float, arg2: Float, arg3: Float, arg4: Float): Int
	fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int): IDirect3DVolumeTexture
	fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean): IDirect3DSurface
	fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int, iDirect3DVertexBuffer: IDirect3DVertexBuffer?): IDirect3DVertexBuffer
	fun SetTexture(arg0: Int, arg1: IDirect3DBaseTexture?): Int
	fun SetRenderState(arg0: Int, arg1: Int): Int
	fun TestCooperativeLevel(): Int
}
