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

actual class IDirect3DSwapChain : IUnknown {
	actual constructor(arg0: hb?) : super(nativeUnavailable())
	actual fun a(arg0: Int, arg1: Int): IDirect3DSurface {
		throw NativeUnavailableException("a")
	}
	actual fun Present(arg0: Int): Int {
		throw NativeUnavailableException("Present")
	}
}
