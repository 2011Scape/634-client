/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import nativeUnavailable

actual class D3DDISPLAYMODE {
	actual constructor() {
		throw NativeUnavailableException("D3DDISPLAYMODE")
	}
	actual var Format: Int = 0
}
