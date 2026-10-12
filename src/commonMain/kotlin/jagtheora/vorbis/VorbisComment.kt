/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/vorbis/VorbisComment.kt), so that common code can use it (milestone 3).
 */
package jagtheora.vorbis

import jagtheora.misc.SimplePeer

expect class VorbisComment : SimplePeer {
	constructor()
}
