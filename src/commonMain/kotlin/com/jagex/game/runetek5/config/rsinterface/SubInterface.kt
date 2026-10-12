package com.jagex.game.runetek5.config.rsinterface

import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!uba")
class SubInterface : Node() {

	@OriginalMember(owner = "client!uba", name = "s", descriptor = "I")
	@JvmField
	var interfaceId: Int = 0

	@OriginalMember(owner = "client!uba", name = "v", descriptor = "I")
	@JvmField
	var openType: SubInterfaceType? = null
}
