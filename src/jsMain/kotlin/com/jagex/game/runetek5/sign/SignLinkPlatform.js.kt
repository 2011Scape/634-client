package com.jagex.game.runetek5.sign

import NativeUnavailableException
import awt.Component
import awt.Frame
import awt.Point
import lang.forName
import lang.getDeclaredConstructor
import lang.reflect.Method

/*
 * Not part of the original client: the browser side of SignLinkPlatform (milestone 4). There is no
 * reflection here: a class is created from the registry (lang/ClassRegistry.js.kt) or fails with
 * ClassNotFoundException, and the other calls throw; SignLink handles both as an unsupported
 * feature, as it does on a JVM without it.
 */
actual fun focusTraversalKeysEnabledMethod(): Method = throw NativeUnavailableException("Component.setFocusTraversalKeysEnabled")

actual fun focusCycleRootMethod(): Method = throw NativeUnavailableException("Container.setFocusCycleRoot")

actual fun newInstanceOf(className: String): Any = forName(className).getDeclaredConstructor().newInstance()!!

actual fun windowModeListModes(windowMode: Any?): Any? = throw NativeUnavailableException("fullscreen")

actual fun windowModeEnter(windowMode: Any?, frame: Frame?, width: Int, height: Int, bitDepth: Int, refreshRate: Int) {
	throw NativeUnavailableException("fullscreen")
}

actual fun windowModeExit(windowMode: Any?) {
	throw NativeUnavailableException("fullscreen")
}

actual fun cursorManagerMoveMouse(cursorManager: Any?, x: Int, y: Int) {
	throw NativeUnavailableException("CursorManager.movemouse")
}

actual fun cursorManagerShowCursor(cursorManager: Any?, component: Component?, visible: Boolean) {
	throw NativeUnavailableException("CursorManager.showcursor")
}

actual fun cursorManagerSetCustomCursor(cursorManager: Any?, component: Component?, pixels: IntArray?, width: Int, height: Int, hotspot: Point?) {
	throw NativeUnavailableException("CursorManager.setcustomcursor")
}

actual fun openUrlWithCmd(url: String?) {
	throw NativeUnavailableException("cmd /c start")
}
