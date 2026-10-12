package jagdx

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/D3DADAPTER_IDENTIFIER")
class D3DADAPTER_IDENTIFIER {

	@OriginalMember(owner = "client!jagdx/D3DADAPTER_IDENTIFIER", name = "DeviceName", descriptor = "Ljava/lang/String;")
	private var DeviceName: String? = null

	@OriginalMember(owner = "client!jagdx/D3DADAPTER_IDENTIFIER", name = "SubSysID", descriptor = "I")
	private var SubSysID: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DADAPTER_IDENTIFIER", name = "DeviceID", descriptor = "I")
	private var DeviceID: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DADAPTER_IDENTIFIER", name = "VendorID", descriptor = "I")
	@JvmField
	var VendorID: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DADAPTER_IDENTIFIER", name = "Description", descriptor = "Ljava/lang/String;")
	@JvmField
	var Description: String? = null

	@OriginalMember(owner = "client!jagdx/D3DADAPTER_IDENTIFIER", name = "DriverVersion", descriptor = "J")
	@JvmField
	var DriverVersion: Long = 0L

	@OriginalMember(owner = "client!jagdx/D3DADAPTER_IDENTIFIER", name = "Driver", descriptor = "Ljava/lang/String;")
	private var Driver: String? = null

	@OriginalMember(owner = "client!jagdx/D3DADAPTER_IDENTIFIER", name = "Revision", descriptor = "I")
	private var Revision: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DADAPTER_IDENTIFIER", name = "WHQLLevel", descriptor = "I")
	private var WHQLLevel: Int = 0
}
