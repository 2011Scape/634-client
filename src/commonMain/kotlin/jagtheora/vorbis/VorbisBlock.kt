/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/vorbis/VorbisBlock.kt), so that common code can use it (milestone 3).
 */
package jagtheora.vorbis

import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket

expect class VorbisBlock : SimplePeer {
	constructor(dspState: DSPState?)
	fun synthesis(arg0: OggPacket?): Int
}
