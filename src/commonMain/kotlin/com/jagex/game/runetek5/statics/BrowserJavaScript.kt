/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/com/jagex/game/runetek5/statics/BrowserJavaScript.kt), so that common code can use it (milestone 3).
 */
package com.jagex.game.runetek5.statics

import com.jagex.game.runetek5.client.GameShell
import applet.Applet
import lang.reflect.Method

expect object BrowserJavaScript {
	fun evalJavaScript(applet: Applet?, string: String?)
	fun callJavaScriptWithArgs(string: String?, objects: Array<Any?>?, applet: Applet?): Any?
	fun callJavaScript(applet: Applet?, string: String?): Any?
	var javaScriptEnabled: Boolean
	fun isNotShowingVideoAd(): Boolean
	fun showVideoAd(): Boolean
}
