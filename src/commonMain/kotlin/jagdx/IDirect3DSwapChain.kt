/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/IDirect3DSwapChain.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb

expect class IDirect3DSwapChain : IUnknown {
	constructor(arg0: hb?)
	fun a(arg0: Int, arg1: Int): IDirect3DSurface
	fun Present(arg0: Int): Int
}
