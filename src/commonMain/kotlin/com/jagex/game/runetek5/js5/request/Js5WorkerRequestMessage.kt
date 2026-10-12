package com.jagex.game.runetek5.js5.request

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmField

@OriginalClass("client!ffa")
abstract class Js5WorkerRequestMessage : CacheableNode() {

	@OriginalMember(owner = "client!ffa", name = "y", descriptor = "Z")
	@JvmField
	var highPriority: Boolean = false

	@OriginalMember(owner = "client!ffa", name = "E", descriptor = "Z")
	@JvmField
	var isCompleted: Boolean = false

	@OriginalMember(owner = "client!ffa", name = "z", descriptor = "Z")
	@Volatile
	@JvmField
	var waiting: Boolean = true

	@OriginalMember(owner = "client!ffa", name = "e", descriptor = "(I)I")
	abstract fun completePercentage(): Int

	@OriginalMember(owner = "client!ffa", name = "f", descriptor = "(I)[B")
	abstract fun getData(): ByteArray?
}
