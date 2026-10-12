/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/peer/IUnknown.kt), so that common code can use it (milestone 3).
 */
package jaclib.peer

expect open class IUnknown(arg0: hb?) : Peer {
	fun a(arg0: Int): Long
}
