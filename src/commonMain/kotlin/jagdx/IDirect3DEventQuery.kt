/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/IDirect3DEventQuery.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb

expect class IDirect3DEventQuery : IUnknown {
	constructor(arg0: hb?)
	fun Issue(): Int
	fun IsSignaled(): Int
}
