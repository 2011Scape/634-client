package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!lo")
class NPCEntityNode : Node {

	@OriginalMember(owner = "client!lo", name = "q", descriptor = "Lclient!ff;")
	@JvmField
	val npc: NPCEntity?

	@OriginalMember(owner = "client!lo", name = "<init>", descriptor = "(Lclient!ff;)V")
	constructor(@OriginalArg(0) npcEntity: NPCEntity?) : super() {
		this.npc = npcEntity
	}
}
