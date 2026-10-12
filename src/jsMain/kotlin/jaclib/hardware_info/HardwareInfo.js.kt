/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jaclib.hardware_info

import NativeUnavailableException
import nativeUnavailable

actual class HardwareInfo private actual constructor() {
	init {
		throw NativeUnavailableException("HardwareInfo")
	}
	actual companion object {
		actual fun getCPUInfo(): IntArray? {
			throw NativeUnavailableException("getCPUInfo")
		}
	}
}
