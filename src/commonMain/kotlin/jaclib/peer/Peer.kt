/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/peer/Peer.kt), so that common code can use it (milestone 3).
 */
package jaclib.peer

expect abstract class Peer protected constructor() {
	fun a(arg0: Boolean): Boolean
	open fun a(): Long
}
