/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/jaclib/hardware_info/HardwareInfo.kt), so that common code can use it (milestone 3).
 */
package jaclib.hardware_info

expect class HardwareInfo private constructor() {
	companion object {
		fun getCPUInfo(): IntArray?
	}
}
