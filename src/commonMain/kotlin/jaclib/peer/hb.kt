/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/peer/hb.kt), so that common code can use it (milestone 3).
 */
package jaclib.peer

expect class hb {
	constructor()
	fun a(arg0: Byte)
	fun b(arg0: Byte)
}
