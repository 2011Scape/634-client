/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package jagdx

import NativeUnavailableException
import awt.Canvas
import nativeUnavailable

actual class D3DPRESENT_PARAMETERS {
	actual var Windowed: Boolean = false
	actual var EnableAutoDepthStencil: Boolean = false
	actual var BackBufferFormat: Int = 0
	actual var MultiSampleQuality: Int = 0
	actual var MultiSampleType: Int = 0
	actual var AutoDepthStencilFormat: Int = 0
	actual var BackBufferHeight: Int = 0
	actual var PresentationInterval: Int = 0
	actual var BackBufferWidth: Int = 0
	actual constructor(canvas: Canvas?) {
		throw NativeUnavailableException("D3DPRESENT_PARAMETERS")
	}
}
