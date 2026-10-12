package com.jagex.game.runetek5.network.packet

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!jea")
class StockMarketOffer {

	@OriginalMember(owner = "client!jea", name = "a", descriptor = "B")
	private var statusAndType: Byte = 0

	@OriginalMember(owner = "client!jea", name = "b", descriptor = "I")
	@JvmField
	var amount: Int = 0

	@OriginalMember(owner = "client!jea", name = "d", descriptor = "I")
	@JvmField
	var itemId: Int = 0

	@OriginalMember(owner = "client!jea", name = "h", descriptor = "I")
	@JvmField
	var totalPrice: Int = 0

	@OriginalMember(owner = "client!jea", name = "k", descriptor = "I")
	@JvmField
	var price: Int = 0

	@OriginalMember(owner = "client!jea", name = "l", descriptor = "I")
	@JvmField
	var currentAmount: Int = 0

	@OriginalMember(owner = "client!jea", name = "<init>", descriptor = "()V")
	constructor() {
	}

	@OriginalMember(owner = "client!jea", name = "<init>", descriptor = "(Lclient!cea;)V")
	constructor(@OriginalArg(0) packet: Packet?) {
		this.statusAndType = packet!!.readByte()
		this.itemId = packet.readUShort()
		this.price = packet.readInt()
		this.amount = packet.readInt()
		this.currentAmount = packet.readInt()
		this.totalPrice = packet.readInt()
	}

	@OriginalMember(owner = "client!jea", name = "a", descriptor = "(I)I")
	fun isBuyOffer(): Int {
		return if ((this.statusAndType.toInt() and 0x8) == 8) 1 else 0
	}

	@OriginalMember(owner = "client!jea", name = "a", descriptor = "(B)I")
	fun getSlotState(): Int {
		return this.statusAndType.toInt() and 0x7
	}

	companion object {
		@OriginalMember(owner = "client!fca", name = "o", descriptor = "[Lclient!jea;")
		@JvmField
		val GRAND_EXCHANGE_SLOTS: Array<StockMarketOffer?> = arrayOfNulls<StockMarketOffer>(6)
	}
}
