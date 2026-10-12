/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/IDirect3DCubeTexture.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.peer.hb

expect class IDirect3DCubeTexture : IDirect3DBaseTexture {
	constructor(arg0: hb?)
	fun LockRect(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: PixelBuffer?): Int
	fun UnlockRect(arg0: Int, arg1: Int): Boolean
}
