/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/memory/Stream.kt), so that common code can use it (milestone 3).
 */
package jaclib.memory

expect class Stream private constructor(arg0: Int) {
	constructor()
	constructor(buffer: Buffer?)
	constructor(buffer: Buffer?, arg1: Int, arg2: Int)
	fun a(arg0: Int)
	fun a(arg0: Float)
	fun a(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	fun a(buffer: Buffer?)
	fun b(arg0: Int)
	fun a()
	fun b(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	fun c(arg0: Int)
	fun b(): Int
	fun d(arg0: Int)
	fun e(arg0: Int)
	fun b(arg0: Float)
	fun f(arg0: Int)
	companion object {
		fun floatToRawIntBits(arg0: Float): Int
		fun c(): Boolean
	}
}
