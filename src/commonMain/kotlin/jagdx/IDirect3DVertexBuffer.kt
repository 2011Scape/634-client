/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/IDirect3DVertexBuffer.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.memory.Source
import jaclib.peer.IUnknown
import jaclib.peer.hb

expect class IDirect3DVertexBuffer : IUnknown {
	var b: Int
	constructor(arg0: hb?)
	fun a(source: Source?, arg1: Int, arg2: Int, arg3: Int, arg4: Int): Boolean
	fun Unlock(): Int
	override fun a(): Long
	fun Lock(arg0: Int, arg1: Int, arg2: Int, arg3: GeometryBuffer?): Int
}
