/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/theora/Frame.kt), so that common code can use it (milestone 3).
 */
package jagtheora.theora

import jagtheora.misc.SimplePeer

expect class Frame : SimplePeer {
	val b: Int
	val a: Int
	val pixels: IntArray
	constructor(arg0: Int, arg1: Int)
}
