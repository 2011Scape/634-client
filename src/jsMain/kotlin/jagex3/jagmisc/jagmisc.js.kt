/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagex3.jagmisc

import NativeUnavailableException
import nativeUnavailable

actual class jagmisc private actual constructor() {
	init {
		throw NativeUnavailableException("jagmisc")
	}
	actual companion object {
		actual fun getAvailablePhysicalMemory(): Long {
			throw NativeUnavailableException("getAvailablePhysicalMemory")
		}
		actual fun init(): Boolean {
			throw NativeUnavailableException("init")
		}
		actual fun nanoTime(): Long {
			throw NativeUnavailableException("nanoTime")
		}
		actual fun quit() {
			throw NativeUnavailableException("quit")
		}
		actual fun ping(arg0: Byte, arg1: Byte, arg2: Byte, arg3: Byte, arg4: Long): Int {
			throw NativeUnavailableException("ping")
		}
	}
}
