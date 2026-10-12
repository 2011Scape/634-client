package com.jagex.game.runetek5.sign

import java.awt.Component
import java.awt.Frame
import java.awt.Point
import java.lang.reflect.Method

/* Not part of the original client: the JVM side of SignLinkPlatform, SignLink's original code. */

actual fun focusTraversalKeysEnabledMethod(): Method =
	Class.forName("java.awt.Component").getDeclaredMethod("setFocusTraversalKeysEnabled", java.lang.Boolean.TYPE)

actual fun focusCycleRootMethod(): Method =
	Class.forName("java.awt.Container").getDeclaredMethod("setFocusCycleRoot", java.lang.Boolean.TYPE)

actual fun newInstanceOf(className: String): Any =
	Class.forName(className).getDeclaredConstructor().newInstance()

actual fun windowModeListModes(windowMode: Any?): Any? =
	Class.forName("com.jagex.game.runetek5.client.window.WindowMode").getMethod("listmodes").invoke(windowMode)

@Suppress("DEPRECATION")
actual fun windowModeEnter(windowMode: Any?, frame: Frame?, width: Int, height: Int, bitDepth: Int, refreshRate: Int) {
	Class.forName("com.jagex.game.runetek5.client.window.WindowMode").getMethod("enter", Frame::class.java, java.lang.Integer.TYPE, java.lang.Integer.TYPE, java.lang.Integer.TYPE, java.lang.Integer.TYPE).invoke(windowMode, frame, Integer.valueOf(width), java.lang.Integer(height), Integer.valueOf(bitDepth), java.lang.Integer(refreshRate))
}

actual fun windowModeExit(windowMode: Any?) {
	Class.forName("com.jagex.game.runetek5.client.window.WindowMode").getMethod("exit").invoke(windowMode)
}

@Suppress("DEPRECATION")
actual fun cursorManagerMoveMouse(cursorManager: Any?, x: Int, y: Int) {
	Class.forName("com.jagex.game.runetek5.client.input.mouse.CursorManager").getDeclaredMethod("movemouse", java.lang.Integer.TYPE, java.lang.Integer.TYPE).invoke(cursorManager, Integer.valueOf(x), java.lang.Integer(y))
}

actual fun cursorManagerShowCursor(cursorManager: Any?, component: Component?, visible: Boolean) {
	Class.forName("com.jagex.game.runetek5.client.input.mouse.CursorManager").getDeclaredMethod("showcursor", java.awt.Component::class.java, java.lang.Boolean.TYPE).invoke(cursorManager, component, java.lang.Boolean.valueOf(visible))
}

@Suppress("DEPRECATION")
actual fun cursorManagerSetCustomCursor(cursorManager: Any?, component: Component?, pixels: IntArray?, width: Int, height: Int, hotspot: Point?) {
	Class.forName("com.jagex.game.runetek5.client.input.mouse.CursorManager").getDeclaredMethod("setcustomcursor", java.awt.Component::class.java, IntArray::class.java, java.lang.Integer.TYPE, java.lang.Integer.TYPE, Point::class.java).invoke(cursorManager, component, pixels, Integer.valueOf(width), java.lang.Integer(height), hotspot)
}

@Suppress("DEPRECATION")
actual fun openUrlWithCmd(url: String?) {
	Runtime.getRuntime().exec("cmd /c start \"j\" \"" + url + "\"")
}
