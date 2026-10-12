/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/ogg/OggPage.kt), so that common code can use it (milestone 3).
 */
package jagtheora.ogg

import jagtheora.misc.SimplePeer

expect class OggPage : SimplePeer {
	constructor()
	fun getSerialNumber(): Int
	fun isBOS(): Boolean
}
