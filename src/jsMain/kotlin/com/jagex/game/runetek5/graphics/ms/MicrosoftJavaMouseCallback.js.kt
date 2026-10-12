/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package com.jagex.game.runetek5.graphics.ms

import NativeUnavailableException
import awt.Component
import nativeUnavailable

actual class MicrosoftJavaMouseCallback {
	actual constructor() {
		throw NativeUnavailableException("MicrosoftJavaMouseCallback")
	}
	actual fun movemouse(x: Int, y: Int) {
		throw NativeUnavailableException("movemouse")
	}
	actual fun setCursorVisible(delete: Boolean, component: Component?) {
		throw NativeUnavailableException("setCursorVisible")
	}
}
