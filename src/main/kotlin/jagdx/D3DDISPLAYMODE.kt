package jagdx

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/D3DDISPLAYMODE")
class D3DDISPLAYMODE {

	@OriginalMember(owner = "client!jagdx/D3DDISPLAYMODE", name = "Width", descriptor = "I")
	private var Width: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DDISPLAYMODE", name = "RefreshRate", descriptor = "I")
	private var RefreshRate: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DDISPLAYMODE", name = "Height", descriptor = "I")
	private var Height: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DDISPLAYMODE", name = "Format", descriptor = "I")
	@JvmField
	var Format: Int = 0
}
