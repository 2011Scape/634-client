/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/memory/Source.kt), so that common code can use it (milestone 3).
 */
package jaclib.memory

expect interface Source {
	fun getSize(): Int
	fun getAddress(): Long
}
