/*
 * Not part of the original client: the common declaration of the mouse support for Microsoft's JVM
 * (src/jvmMain/kotlin/com/jagex/game/runetek5/graphics/ms/MicrosoftJavaMouseCallback.kt), which is
 * built on com.ms.dll and com.ms.win32, so that SignLink can use it (milestone 3).
 */
package com.jagex.game.runetek5.graphics.ms

import awt.Component

expect class MicrosoftJavaMouseCallback {
	constructor()
	fun movemouse(x: Int, y: Int)
	fun setCursorVisible(delete: Boolean, component: Component?)
}
