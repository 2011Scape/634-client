/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import jaclib.peer.hb
import nativeUnavailable

actual class IDirect3DVolumeTexture : IDirect3DBaseTexture {
	actual constructor(arg0: hb?) : super(nativeUnavailable())
	actual fun UnlockBox(arg0: Int): Boolean {
		throw NativeUnavailableException("UnlockBox")
	}
	actual fun LockBox(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int, arg8: PixelBuffer?): Int {
		throw NativeUnavailableException("LockBox")
	}
}
