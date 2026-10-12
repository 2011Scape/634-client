/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/ogg/OggSyncState.kt), so that common code can use it (milestone 3).
 */
package jagtheora.ogg

import jagtheora.misc.SimplePeer

expect class OggSyncState : SimplePeer {
	constructor()
	fun write(arg0: ByteArray?, arg1: Int): Boolean
	fun pageOut(arg0: OggPage?): Int
}
