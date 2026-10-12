/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jagex3/jagmisc/jagmisc.kt), so that common code can use it (milestone 3).
 */
package jagex3.jagmisc

expect class jagmisc private constructor() {
	companion object {
		fun getAvailablePhysicalMemory(): Long
		fun init(): Boolean
		fun nanoTime(): Long
		fun quit()
		fun ping(arg0: Byte, arg1: Byte, arg2: Byte, arg3: Byte, arg4: Long): Int
	}
}
