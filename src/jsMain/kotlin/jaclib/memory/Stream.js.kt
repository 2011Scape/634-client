/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaclib.memory

import NativeUnavailableException
import nativeUnavailable

actual class Stream private actual constructor(arg0: Int) {
	init {
		throw NativeUnavailableException("Stream")
	}
	actual constructor() : this(nativeUnavailable() as Int)
	actual constructor(buffer: Buffer?) : this(nativeUnavailable() as Int)
	actual constructor(buffer: Buffer?, arg1: Int, arg2: Int) : this(nativeUnavailable() as Int)
	actual fun a(arg0: Int) {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Float) {
		throw NativeUnavailableException("a")
	}
	actual fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("a")
	}
	actual fun a(buffer: Buffer?) {
		throw NativeUnavailableException("a")
	}
	actual fun b(arg0: Int) {
		throw NativeUnavailableException("b")
	}
	actual fun a() {
		throw NativeUnavailableException("a")
	}
	actual fun b(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("b")
	}
	actual fun c(arg0: Int) {
		throw NativeUnavailableException("c")
	}
	actual fun b(): Int {
		throw NativeUnavailableException("b")
	}
	actual fun d(arg0: Int) {
		throw NativeUnavailableException("d")
	}
	actual fun e(arg0: Int) {
		throw NativeUnavailableException("e")
	}
	actual fun b(arg0: Float) {
		throw NativeUnavailableException("b")
	}
	actual fun f(arg0: Int) {
		throw NativeUnavailableException("f")
	}
	actual companion object {
		actual fun floatToRawIntBits(arg0: Float): Int {
			throw NativeUnavailableException("floatToRawIntBits")
		}
		actual fun c(): Boolean {
			throw NativeUnavailableException("c")
		}
	}
}
