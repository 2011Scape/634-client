/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import jaclib.memory.Source
import jaclib.peer.IUnknown
import jaclib.peer.hb
import nativeUnavailable

actual class IDirect3DVertexBuffer : IUnknown {
	actual var b: Int = 0
	actual constructor(arg0: hb?) : super(nativeUnavailable())
	actual fun a(source: Source?, arg1: Int, arg2: Int, arg3: Int, arg4: Int): Boolean {
		throw NativeUnavailableException("a")
	}
	actual fun Unlock(): Int {
		throw NativeUnavailableException("Unlock")
	}
	actual override fun a(): Long {
		throw NativeUnavailableException("a")
	}
	actual fun Lock(arg0: Int, arg1: Int, arg2: Int, arg3: GeometryBuffer?): Int {
		throw NativeUnavailableException("Lock")
	}
}
