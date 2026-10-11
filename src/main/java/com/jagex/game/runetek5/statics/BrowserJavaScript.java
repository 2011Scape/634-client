package com.jagex.game.runetek5.statics;

import java.applet.Applet;
import java.lang.reflect.Method;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.GameShell;
import org.openrs2.deob.annotation.Pc;

public final class BrowserJavaScript {

	@OriginalMember(owner = "client!bt", name = "a", descriptor = "(Ljava/applet/Applet;ILjava/lang/String;)V")
	public static void evalJavaScript(@OriginalArg(0) Applet applet, @OriginalArg(2) String string) throws Throwable {
		// Not part of the original client, which called JSObject.getWindow(arg0).eval(arg1) here.
		invokeOnWindow(applet, "eval", new Class<?>[] { String.class }, new Object[] { string });
	}

	@OriginalMember(owner = "client!bt", name = "a", descriptor = "(Ljava/lang/String;[Ljava/lang/Object;Ljava/applet/Applet;B)Ljava/lang/Object;")
	public static Object callJavaScriptWithArgs(@OriginalArg(0) String string, @OriginalArg(1) Object[] objects, @OriginalArg(2) Applet applet) throws Throwable {
		// Not part of the original client, which called JSObject.getWindow(arg2).call(arg0, arg1) here.
		return invokeOnWindow(applet, "call", new Class<?>[] { String.class, Object[].class }, new Object[] { string, objects });
	}

	@OriginalMember(owner = "client!bt", name = "a", descriptor = "(BLjava/applet/Applet;Ljava/lang/String;)Ljava/lang/Object;")
	public static Object callJavaScript(@OriginalArg(1) Applet applet, @OriginalArg(2) String string) throws Throwable {
		// Not part of the original client, which called JSObject.getWindow(arg0).call(arg1, (Object[]) null) here.
		return invokeOnWindow(applet, "call", new Class<?>[] { String.class, Object[].class }, new Object[] { string, null });
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
	private static Object invokeOnWindow(Applet applet, String name, Class<?>[] types, Object[] args) throws Throwable {
		Class<?> clazz = Class.forName("netscape.javascript.JSObject");
		Method getWindow = clazz.getMethod("getWindow", Applet.class);
		Method method = clazz.getMethod(name, types);
		return method.invoke(getWindow.invoke(null, applet), args);
	}
	@OriginalMember(owner = "client!vs", name = "b", descriptor = "Z")
	public static boolean javaScriptEnabled = false;

	@OriginalMember(owner = "client!tfa", name = "b", descriptor = "(I)Z")
	public static boolean isNotShowingVideoAd() {
		if (BrowserJavaScript.javaScriptEnabled) {
			try {
				if ((Boolean) BrowserJavaScript.callJavaScript(GameShell.applet, "showingVideoAd")) {
					return false;
				}
				return true;
			} catch (@Pc(26) Throwable ex) {
			}
		}
		return true;
	}
	@OriginalMember(owner = "client!kd", name = "c", descriptor = "(I)Z")
	public static boolean showVideoAd() {
		if (BrowserJavaScript.javaScriptEnabled) {
			try {
				BrowserJavaScript.callJavaScript(GameShell.applet, "showVideoAd");
				return true;
			} catch (@Pc(13) Throwable ex) {
			}
		}
		return false;
	}
}
