package jaclib.hardware_info

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

/*
 * Not part of the original client: without libjaclib there is no hardware report; the callers
 * (SystemInfo) treat null as unknown.
 */
@OriginalClass("client!jaclib/hardware_info/HardwareInfo")
actual class HardwareInfo @OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "<init>", descriptor = "()V") private actual constructor() {

	actual companion object {

		@OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "getDXDiagDisplayDevicesProps", descriptor = "()[[Ljava/lang/String;")
		@JvmStatic private fun getDXDiagDisplayDevicesProps(): Array<Array<String?>?>? {
			return null
		}

		@OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "getCPUInfo", descriptor = "()[I")
		@JvmStatic actual fun getCPUInfo(): IntArray? {
			return null
		}

		@OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "getOpenGLProps", descriptor = "()[Ljava/lang/String;")
		@JvmStatic private fun getOpenGLProps(): Array<String?>? {
			return null
		}

		@OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "getDXDiagSystemProps", descriptor = "()[Ljava/lang/String;")
		@JvmStatic private fun getDXDiagSystemProps(): Array<String?>? {
			return null
		}
	}
}
