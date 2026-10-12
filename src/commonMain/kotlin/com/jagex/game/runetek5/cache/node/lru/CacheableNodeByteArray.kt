package com.jagex.game.runetek5.cache.node.lru

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!lk")
class CacheableNodeByteArray @OriginalMember(owner = "client!lk", name = "<init>", descriptor = "([B)V") constructor(@OriginalArg(0) data: ByteArray?) : CacheableNode() {

	@OriginalMember(owner = "client!lk", name = "x", descriptor = "[B")
	@JvmField
	val value: ByteArray? = data
}
