/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/ogg/OggPacket.kt), so that common code can use it (milestone 3).
 */
package jagtheora.ogg

import jagtheora.misc.SimplePeer

expect class OggPacket : SimplePeer {
	constructor()
	fun isTheora(): Boolean
	fun getData(): ByteArray?
	fun isKeyFrame(): Int
	fun isVorbis(): Boolean
}
