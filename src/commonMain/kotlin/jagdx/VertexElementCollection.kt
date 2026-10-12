/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/VertexElementCollection.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.peer.hb
import jaclib.peer.uda

expect class VertexElementCollection : uda {
	constructor(arg0: hb?)
	fun finish()
	fun addElement(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int)
}
