/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/com/jagex/game/runetek5/client/features/WindowIcons.kt), so that common code can use it (milestone 3).
 */
package com.jagex.game.runetek5.client.features

import awt.Frame


expect object WindowIcons {
	fun apply(frame: Frame?)
}
