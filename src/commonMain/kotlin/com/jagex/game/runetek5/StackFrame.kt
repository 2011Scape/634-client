package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.lru.ClientScript
import kotlin.jvm.JvmField

@OriginalClass("client!qea")
class StackFrame {

	@OriginalMember(owner = "client!qea", name = "a", descriptor = "[I")
	@JvmField
	var intVars: IntArray? = null

	@OriginalMember(owner = "client!qea", name = "f", descriptor = "[Ljava/lang/String;")
	@JvmField
	var stringVars: Array<String?>? = null

	@OriginalMember(owner = "client!qea", name = "g", descriptor = "Lclient!jk;")
	@JvmField
	var script: ClientScript? = null

	@OriginalMember(owner = "client!qea", name = "j", descriptor = "I")
	@JvmField
	var pc: Int = -1
}
