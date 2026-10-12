/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package com.jagex.game.runetek5.graphics.ms

import NativeUnavailableException
import awt.Frame
import nativeUnavailable

actual class MicrosoftFullscreenAdapter {
	actual constructor() {
		throw NativeUnavailableException("MicrosoftFullscreenAdapter")
	}
	actual fun listModes(): IntArray? {
		throw NativeUnavailableException("listModes")
	}
	actual fun enter(height: Int, bits: Int, width: Int, refreshRate: Int, frame: Frame?) {
		throw NativeUnavailableException("enter")
	}
	actual fun exit(frame: Frame?) {
		throw NativeUnavailableException("exit")
	}
}
