/*
 * Not part of the original client: the browser side of a native class (milestone 4). The native
 * library behind it is not available in a browser, so it throws NativeUnavailableException, which
 * the client treats like a missing library.
 */
package com.jagex.game.runetek5.statics

import NativeUnavailableException
import applet.Applet
import com.jagex.game.runetek5.client.GameShell
import lang.reflect.Method
import nativeUnavailable

actual object BrowserJavaScript {
	actual fun evalJavaScript(applet: Applet?, string: String?) {
		throw NativeUnavailableException("evalJavaScript")
	}
	actual fun callJavaScriptWithArgs(string: String?, objects: Array<Any?>?, applet: Applet?): Any? {
		throw NativeUnavailableException("callJavaScriptWithArgs")
	}
	actual fun callJavaScript(applet: Applet?, string: String?): Any? {
		throw NativeUnavailableException("callJavaScript")
	}
	actual var javaScriptEnabled: Boolean = false
	actual fun isNotShowingVideoAd(): Boolean {
		throw NativeUnavailableException("isNotShowingVideoAd")
	}
	actual fun showVideoAd(): Boolean {
		throw NativeUnavailableException("showVideoAd")
	}
}
