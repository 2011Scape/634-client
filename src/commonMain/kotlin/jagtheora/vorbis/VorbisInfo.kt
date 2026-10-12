/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/vorbis/VorbisInfo.kt), so that common code can use it (milestone 3).
 */
package jagtheora.vorbis

import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket

expect class VorbisInfo : SimplePeer {
	var rate: Int
	var channels: Int
	constructor()
	fun headerIn(arg0: VorbisComment?, arg1: OggPacket?): Int
}
