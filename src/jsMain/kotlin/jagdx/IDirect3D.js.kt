/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import awt.Canvas
import jaclib.peer.IUnknown
import jaclib.peer.hb
import nativeUnavailable

actual class IDirect3D private constructor() : IUnknown(nativeUnavailable()) {
	actual fun a(arg0: Int, d3DDISPLAYMODE: D3DDISPLAYMODE?): Int {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Int, canvas: Canvas?, arg3: Int, d3DPRESENT_PARAMETERS: D3DPRESENT_PARAMETERS?): IDirect3DDevice {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Int): D3DADAPTER_IDENTIFIER {
		throw NativeUnavailableException("a")
	}
	actual fun CheckDeviceType(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean): Int {
		throw NativeUnavailableException("CheckDeviceType")
	}
	actual fun b(arg0: Int, arg1: Int): D3DCAPS {
		throw NativeUnavailableException("b")
	}
	actual fun CheckDeviceMultiSampleType(arg0: Int, arg1: Int, arg2: Int, arg3: Boolean, arg4: Int): Int {
		throw NativeUnavailableException("CheckDeviceMultiSampleType")
	}
	actual fun CheckDepthStencilMatch(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int): Int {
		throw NativeUnavailableException("CheckDepthStencilMatch")
	}
	actual fun CheckDeviceFormat(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): Int {
		throw NativeUnavailableException("CheckDeviceFormat")
	}
	actual companion object {
		actual fun a(arg0: Int, arg1: hb?): IDirect3D {
			throw NativeUnavailableException("a")
		}
	}
}
