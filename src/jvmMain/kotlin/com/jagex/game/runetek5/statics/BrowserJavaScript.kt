package com.jagex.game.runetek5.statics

import com.jagex.game.runetek5.client.GameShell
import java.applet.Applet
import java.lang.reflect.Method
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

actual object BrowserJavaScript {

	@OriginalMember(owner = "client!bt", name = "a", descriptor = "(Ljava/applet/Applet;ILjava/lang/String;)V")
	@JvmStatic
	@Throws(Throwable::class)
	actual fun evalJavaScript(@OriginalArg(0) applet: Applet?, @OriginalArg(2) string: String?) {
		// Not part of the original client, which called JSObject.getWindow(arg0).eval(arg1) here.
		invokeOnWindow(applet, "eval", arrayOf<Class<*>>(String::class.java), arrayOf<Any?>(string))
	}

	@OriginalMember(owner = "client!bt", name = "a", descriptor = "(Ljava/lang/String;[Ljava/lang/Object;Ljava/applet/Applet;B)Ljava/lang/Object;")
	@JvmStatic
	@Throws(Throwable::class)
	actual fun callJavaScriptWithArgs(@OriginalArg(0) string: String?, @OriginalArg(1) objects: Array<Any?>?, @OriginalArg(2) applet: Applet?): Any? {
		// Not part of the original client, which called JSObject.getWindow(arg2).call(arg0, arg1) here.
		return invokeOnWindow(applet, "call", arrayOf<Class<*>>(String::class.java, Array<Any>::class.java), arrayOf<Any?>(string, objects))
	}

	@OriginalMember(owner = "client!bt", name = "a", descriptor = "(BLjava/applet/Applet;Ljava/lang/String;)Ljava/lang/Object;")
	@JvmStatic
	@Throws(Throwable::class)
	actual fun callJavaScript(@OriginalArg(1) applet: Applet?, @OriginalArg(2) string: String?): Any? {
		// Not part of the original client, which called JSObject.getWindow(arg0).call(arg1, (Object[]) null) here.
		return invokeOnWindow(applet, "call", arrayOf<Class<*>>(String::class.java, Array<Any>::class.java), arrayOf<Any?>(string, null))
	}

	/**
	 * Calls a method of the browser window, which the client reaches through
	 * {@code JSObject.getWindow}.
	 *
	 * The window is reached by reflection because the {@code JSObject} in current JDKs has no
	 * {@code getWindow}, and it takes the place of the one the client was built against. A direct
	 * call does not compile.
	 *
	 * This method is not part of the original client.
	 */
	@JvmStatic
	@Throws(Throwable::class)
	private fun invokeOnWindow(applet: Applet?, name: String, types: Array<Class<*>>, args: Array<Any?>): Any? {
		val clazz: Class<*> = Class.forName("netscape.javascript.JSObject")
		val getWindow: Method = clazz.getMethod("getWindow", Applet::class.java)
		val method: Method = clazz.getMethod(name, *types)
		return method.invoke(getWindow.invoke(null, applet), *args)
	}

	@OriginalMember(owner = "client!vs", name = "b", descriptor = "Z")
	@JvmField
	actual var javaScriptEnabled: Boolean = false

	@OriginalMember(owner = "client!tfa", name = "b", descriptor = "(I)Z")
	@JvmStatic
	actual fun isNotShowingVideoAd(): Boolean {
		if (BrowserJavaScript.javaScriptEnabled) {
			try {
				if (BrowserJavaScript.callJavaScript(GameShell.applet, "showingVideoAd") as Boolean) {
					return false
				}
				return true
			} catch (@Pc(26) ex: Throwable) {
			}
		}
		return true
	}

	@OriginalMember(owner = "client!kd", name = "c", descriptor = "(I)Z")
	@JvmStatic
	actual fun showVideoAd(): Boolean {
		if (BrowserJavaScript.javaScriptEnabled) {
			try {
				BrowserJavaScript.callJavaScript(GameShell.applet, "showVideoAd")
				return true
			} catch (@Pc(13) ex: Throwable) {
			}
		}
		return false
	}
}
