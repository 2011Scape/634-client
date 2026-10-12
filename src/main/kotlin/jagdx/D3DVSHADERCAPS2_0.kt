package jagdx

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jagdx/D3DVSHADERCAPS2_0")
class D3DVSHADERCAPS2_0 {

	@OriginalMember(owner = "client!jagdx/D3DVSHADERCAPS2_0", name = "NumTemps", descriptor = "I")
	private var NumTemps: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DVSHADERCAPS2_0", name = "DynamicFlowControlDepth", descriptor = "I")
	private var DynamicFlowControlDepth: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DVSHADERCAPS2_0", name = "StaticFlowControlDepth", descriptor = "I")
	private var StaticFlowControlDepth: Int = 0

	@OriginalMember(owner = "client!jagdx/D3DVSHADERCAPS2_0", name = "Caps", descriptor = "I")
	private var Caps: Int = 0
}
