/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/com/jagex/game/runetek5/DirectSoundPcmDevice.kt), so that common code can use it (milestone 3).
 */
package com.jagex.game.runetek5

import awt.Component

expect class DirectSoundPcmDevice : PcmDevice {
	constructor()
	override fun discardBuffer(index: Int)
	override fun close(index: Int)
	override fun write(index: Int, samples: IntArray?)
	override fun position(index: Int): Int
	override fun init(sampleRate: Int, stereo: Boolean, component: Component?)
	override fun open(bufferSize: Int, index: Int)
}
