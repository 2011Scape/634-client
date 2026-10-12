/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/D3DLIGHT.kt), so that common code can use it (milestone 3).
 */
package jagdx

import jaclib.peer.hb
import jaclib.peer.uda

expect class D3DLIGHT : uda {
	constructor(arg0: hb?)
	fun SetAttenuation(arg0: Float, arg1: Float, arg2: Float)
	fun SetDiffuse(arg0: Float, arg1: Float, arg2: Float, arg3: Float)
	fun SetAmbient(arg0: Float, arg1: Float, arg2: Float, arg3: Float)
	fun SetPosition(arg0: Float, arg1: Float, arg2: Float)
	fun SetDirection(arg0: Float, arg1: Float, arg2: Float)
	fun SetRange(arg0: Float)
	fun SetType(arg0: Int)
}
