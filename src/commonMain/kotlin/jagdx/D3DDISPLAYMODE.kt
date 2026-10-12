/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagdx/D3DDISPLAYMODE.kt), so that common code can use it (milestone 3).
 */
package jagdx

expect class D3DDISPLAYMODE {
	constructor()
	var Format: Int
}
