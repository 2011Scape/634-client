/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaclib.memory

import NativeUnavailableException
import nativeUnavailable

actual interface Buffer {
	actual fun getSize(): Int
	actual fun getAddress(): Long
	actual fun a(arg0: ByteArray?, arg1: Int, arg2: Int, arg3: Int)
}
