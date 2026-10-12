/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/theora/TheoraInfo.kt), so that common code can use it (milestone 3).
 */
package jagtheora.theora

import jagtheora.misc.SimplePeer

expect class TheoraInfo : SimplePeer {
	var fpsNumerator: Int
	var fpsDenominator: Int
	var frameWidth: Int
	var frameHeight: Int
	constructor()
}
