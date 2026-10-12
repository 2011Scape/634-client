/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/theora/DecoderContext.kt), so that common code can use it (milestone 3).
 */
package jagtheora.theora

import jagtheora.misc.SimplePeer
import jagtheora.ogg.OggPacket

expect class DecoderContext : SimplePeer {
	constructor(theoraInfo: TheoraInfo?, setupInfo: SetupInfo?)
	fun granuleFrame(arg0: GranulePos?): Long
	fun setPostProcessingLevel(arg0: Int): Int
	fun decodePacketIn(arg0: OggPacket?, arg1: GranulePos?): Int
	fun getMaxPostProcessingLevel(): Int
	fun granuleTime(arg0: GranulePos?): Double
	fun decodeFrame(arg0: Frame?): Int
}
