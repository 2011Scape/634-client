package com.jagex.game.runetek5.js5.request.remote

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.js5.request.Js5WorkerRequestMessage
import kotlin.jvm.JvmField

@OriginalClass("client!mba")
class Js5NetQueueRequest : Js5WorkerRequestMessage() {

	@OriginalMember(owner = "client!mba", name = "G", descriptor = "B")
	@JvmField
	var padding: Byte = 0

	@OriginalMember(owner = "client!mba", name = "L", descriptor = "Lclient!cea;")
	@JvmField
	var stream: Packet? = null

	@OriginalMember(owner = "client!mba", name = "O", descriptor = "I")
	@JvmField
	var blockBytesRead: Int = 0

	@OriginalMember(owner = "client!mba", name = "f", descriptor = "(I)[B")
	override fun getData(): ByteArray? {
		if (this.waiting || this.stream!!.currentPosition < this.stream!!.buffer!!.size - this.padding) {
			throw RuntimeException()
		}
		return this.stream!!.buffer
	}

	@OriginalMember(owner = "client!mba", name = "e", descriptor = "(I)I")
	override fun completePercentage(): Int {
		return if (this.stream == null) 0 else this.stream!!.currentPosition * 100 / (this.stream!!.buffer!!.size - this.padding)
	}
}
