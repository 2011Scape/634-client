/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import jaclib.peer.hb
import jaclib.peer.uda
import nativeUnavailable

actual class D3DLIGHT : uda {
	actual constructor(arg0: hb?) : super(nativeUnavailable())
	actual fun SetAttenuation(arg0: Float, arg1: Float, arg2: Float) {
		throw NativeUnavailableException("SetAttenuation")
	}
	actual fun SetDiffuse(arg0: Float, arg1: Float, arg2: Float, arg3: Float) {
		throw NativeUnavailableException("SetDiffuse")
	}
	actual fun SetAmbient(arg0: Float, arg1: Float, arg2: Float, arg3: Float) {
		throw NativeUnavailableException("SetAmbient")
	}
	actual fun SetPosition(arg0: Float, arg1: Float, arg2: Float) {
		throw NativeUnavailableException("SetPosition")
	}
	actual fun SetDirection(arg0: Float, arg1: Float, arg2: Float) {
		throw NativeUnavailableException("SetDirection")
	}
	actual fun SetRange(arg0: Float) {
		throw NativeUnavailableException("SetRange")
	}
	actual fun SetType(arg0: Int) {
		throw NativeUnavailableException("SetType")
	}
}
