package com.jagex.game.runetek5.config.rsinterface

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.config.rsinterface.Component
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!rk")
class HookRequest : Node() {

	@OriginalMember(owner = "client!rk", name = "o", descriptor = "I")
	@JvmField
	var typedKeyChar: Int = 0

	@OriginalMember(owner = "client!rk", name = "p", descriptor = "Lclient!at;")
	@JvmField
	var targetComponent: Component? = null

	@OriginalMember(owner = "client!rk", name = "q", descriptor = "I")
	@JvmField
	var mouseX: Int = 0

	@OriginalMember(owner = "client!rk", name = "s", descriptor = "I")
	@JvmField
	var clickedOption: Int = 0

	@OriginalMember(owner = "client!rk", name = "t", descriptor = "[Ljava/lang/Object;")
	@JvmField
	var params: Array<Any?>? = null

	@OriginalMember(owner = "client!rk", name = "u", descriptor = "I")
	@JvmField
	var id: Int = 0

	@OriginalMember(owner = "client!rk", name = "v", descriptor = "Lclient!at;")
	@JvmField
	var source: Component? = null

	@OriginalMember(owner = "client!rk", name = "x", descriptor = "I")
	@JvmField
	var typedKeyCode: Int = 0

	@OriginalMember(owner = "client!rk", name = "z", descriptor = "Z")
	@JvmField
	var hasMousePosition: Boolean = false

	@OriginalMember(owner = "client!rk", name = "A", descriptor = "Ljava/lang/String;")
	@JvmField
	var optionName: String? = null

	@OriginalMember(owner = "client!rk", name = "B", descriptor = "I")
	@JvmField
	var mouseY: Int = 0
}
