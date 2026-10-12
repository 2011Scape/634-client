/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/vorbis/DSPState.kt), so that common code can use it (milestone 3).
 */
package jagtheora.vorbis

import jagtheora.misc.SimplePeer

expect class DSPState : SimplePeer {
	constructor(vorbisInfo: VorbisInfo?)
	fun pcmOut(arg0: Int): Array<FloatArray?>?
	fun read(arg0: Int)
	fun blockIn(arg0: VorbisBlock?)
	fun granuleTime(): Double
}
