package jaclib.hardware_info

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/hardware_info/HardwareInfo")
class HardwareInfo @OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "<init>", descriptor = "()V") private constructor() {

	companion object {

		@OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "getDXDiagDisplayDevicesProps", descriptor = "()[[Ljava/lang/String;")
		@JvmStatic private external fun getDXDiagDisplayDevicesProps(): Array<Array<String?>?>?

		@OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "getCPUInfo", descriptor = "()[I")
		@JvmStatic external fun getCPUInfo(): IntArray?

		@OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "getOpenGLProps", descriptor = "()[Ljava/lang/String;")
		@JvmStatic private external fun getOpenGLProps(): Array<String?>?

		@OriginalMember(owner = "client!jaclib/hardware_info/HardwareInfo", name = "getDXDiagSystemProps", descriptor = "()[Ljava/lang/String;")
		@JvmStatic private external fun getDXDiagSystemProps(): Array<String?>?
	}
}
