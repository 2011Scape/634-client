package jagdx

import java.awt.Canvas
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/D3DPRESENT_PARAMETERS")
class D3DPRESENT_PARAMETERS {

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "Windowed", descriptor = "Z")
	@JvmField
	var Windowed: Boolean = false

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "Flags", descriptor = "I")
	private var Flags: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "FullScreen_RefreshRateInHz", descriptor = "I")
	private var FullScreen_RefreshRateInHz: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "EnableAutoDepthStencil", descriptor = "Z")
	@JvmField
	var EnableAutoDepthStencil: Boolean = false

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "SwapEffect", descriptor = "I")
	private val SwapEffect: Int = 1

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "BackBufferFormat", descriptor = "I")
	@JvmField
	var BackBufferFormat: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "MultiSampleQuality", descriptor = "I")
	@JvmField
	var MultiSampleQuality: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "MultiSampleType", descriptor = "I")
	@JvmField
	var MultiSampleType: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "AutoDepthStencilFormat", descriptor = "I")
	@JvmField
	var AutoDepthStencilFormat: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "BackBufferHeight", descriptor = "I")
	@JvmField
	var BackBufferHeight: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "PresentationInterval", descriptor = "I")
	@JvmField
	var PresentationInterval: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "BackBufferCount", descriptor = "I")
	private val BackBufferCount: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "BackBufferWidth", descriptor = "I")
	@JvmField
	var BackBufferWidth: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "DeviceWindow", descriptor = "Ljava/awt/Canvas;")
	private val DeviceWindow: Canvas?

	@OriginalMember(owner = "client!jagdx/D3DPRESENT_PARAMETERS", name = "<init>", descriptor = "(Ljava/awt/Canvas;)V")
	constructor(@OriginalArg(0) canvas: Canvas?) {
		this.DeviceWindow = canvas
	}
}
