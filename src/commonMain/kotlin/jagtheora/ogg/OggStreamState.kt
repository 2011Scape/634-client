/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/ogg/OggStreamState.kt), so that common code can use it (milestone 3).
 */
package jagtheora.ogg

import jagtheora.misc.SimplePeer

expect class OggStreamState : SimplePeer {
	constructor(arg0: Int)
	fun packetPeek(arg0: OggPacket?): Int
	fun pageIn(arg0: OggPage?): Boolean
	fun packetOut(): Int
	fun packetOut(arg0: OggPacket?): Int
}
