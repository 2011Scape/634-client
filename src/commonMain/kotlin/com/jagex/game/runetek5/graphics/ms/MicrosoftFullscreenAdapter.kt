/*
 * Not part of the original client: the common declaration of the fullscreen support for Microsoft's
 * JVM (src/jvmMain/kotlin/com/jagex/game/runetek5/graphics/ms/MicrosoftFullscreenAdapter.kt), which
 * is built on com.ms.directX, so that SignLink can use it (milestone 3).
 */
package com.jagex.game.runetek5.graphics.ms

import awt.Frame

expect class MicrosoftFullscreenAdapter {
	constructor()
	fun listModes(): IntArray?
	fun enter(height: Int, bits: Int, width: Int, refreshRate: Int, frame: Frame?)
	fun exit(frame: Frame?)
}
