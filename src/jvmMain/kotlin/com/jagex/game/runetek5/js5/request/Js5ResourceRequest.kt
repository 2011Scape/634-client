package com.jagex.game.runetek5.js5.request

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.Decompressor

@OriginalClass("client!qh")
class Js5ResourceRequest : Js5WorkerRequestMessage() {

	@OriginalMember(owner = "client!qh", name = "A", descriptor = "I")
	@JvmField
	var type: Int = 0

	@OriginalMember(owner = "client!qh", name = "D", descriptor = "Lclient!nw;")
	@JvmField
	var fileSystem: Decompressor? = null

	@OriginalMember(owner = "client!qh", name = "I", descriptor = "[B")
	@JvmField
	var data: ByteArray? = null

	@OriginalMember(owner = "client!qh", name = "e", descriptor = "(I)I")
	override fun completePercentage(): Int {
		return if (this.waiting) 0 else 100
	}

	@OriginalMember(owner = "client!qh", name = "f", descriptor = "(I)[B")
	override fun getData(): ByteArray? {
		if (this.waiting) {
			throw RuntimeException()
		}
		return this.data
	}
}
