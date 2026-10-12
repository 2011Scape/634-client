/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagtheora/misc/SimplePeer.kt), so that common code can use it (milestone 3).
 */
package jagtheora.misc

expect abstract class SimplePeer {
	constructor()
	protected abstract fun clear()
	fun a()
	fun finalize()
	fun b(): Boolean
}
