/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/IDirect3DBaseTexture.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.peer.IUnknown
import jaclib.peer.hb

expect open class IDirect3DBaseTexture : IUnknown {
	constructor(arg0: hb?)
}
