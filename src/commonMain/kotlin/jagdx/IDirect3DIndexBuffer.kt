/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/IDirect3DIndexBuffer.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb

expect class IDirect3DIndexBuffer : IUnknown {
	constructor(arg0: hb?)
	override fun a(): Long
	fun Unlock(): Int
	fun Lock(arg0: Int, arg1: Int, arg2: Int, arg3: GeometryBuffer?): Int
}
