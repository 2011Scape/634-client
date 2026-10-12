/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/ue.kt), so that common code can use it (milestone 3).
 */
package jagdx

expect class ue {
	constructor()
	companion object {
		fun a(arg0: Byte, arg1: Int): Boolean
		fun a(arg0: Int, arg1: Boolean): Boolean
	}
}
