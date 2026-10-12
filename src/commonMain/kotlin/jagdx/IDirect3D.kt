/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/IDirect3D.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb
import awt.Canvas

expect class IDirect3D : IUnknown {
	fun a(arg0: Int, d3DDISPLAYMODE: D3DDISPLAYMODE?): Int
	fun a(arg0: Int, arg1: Int, canvas: Canvas?, arg3: Int, d3DPRESENT_PARAMETERS: D3DPRESENT_PARAMETERS?): IDirect3DDevice
	fun a(arg0: Int, arg1: Int): D3DADAPTER_IDENTIFIER
	fun CheckDeviceType(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean): Int
	fun b(arg0: Int, arg1: Int): D3DCAPS
	fun CheckDeviceMultiSampleType(arg0: Int, arg1: Int, arg2: Int, arg3: Boolean, arg4: Int): Int
	fun CheckDepthStencilMatch(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int): Int
	fun CheckDeviceFormat(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): Int
	companion object {
		fun a(arg0: Int, arg1: hb?): IDirect3D
	}
}
