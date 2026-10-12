/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/theora/SetupInfo.kt), so that common code can use it (milestone 3).
 */
package jagtheora.theora

import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket

expect class SetupInfo : SimplePeer {
	constructor()
	fun decodeHeader(arg0: TheoraInfo?, arg1: TheoraComment?, arg2: OggPacket?): Int
}
