package com.jagex.game.runetek5.cache.node.lru

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!qia")
abstract class AbstractLRUNode @OriginalMember(owner = "client!qia", name = "<init>", descriptor = "(I)V") constructor(@OriginalArg(0) capacityRequirement: Int) : CacheableNode() {

	@OriginalMember(owner = "client!qia", name = "w", descriptor = "I")
	@JvmField
	val capacityRequirement: Int = capacityRequirement

	@OriginalMember(owner = "client!qia", name = "a", descriptor = "(I)Ljava/lang/Object;")
	abstract fun get(): Any?

	@OriginalMember(owner = "client!qia", name = "d", descriptor = "(I)Z")
	abstract fun isSoft(): Boolean
}
