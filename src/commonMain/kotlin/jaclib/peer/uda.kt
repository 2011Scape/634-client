/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/peer/uda.kt), so that common code can use it (milestone 3).
 */
package jaclib.peer

expect open class uda(arg0: hb?) : Peer {
}
