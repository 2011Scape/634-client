package com.jagex.game.runetek5.network.packet;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!jea")
public final class StockMarketOffer {

	@OriginalMember(owner = "client!jea", name = "a", descriptor = "B")
	private byte statusAndType;

	@OriginalMember(owner = "client!jea", name = "b", descriptor = "I")
	public int amount;

	@OriginalMember(owner = "client!jea", name = "d", descriptor = "I")
	public int itemId;

	@OriginalMember(owner = "client!jea", name = "h", descriptor = "I")
	public int totalPrice;

	@OriginalMember(owner = "client!jea", name = "k", descriptor = "I")
	public int price;

	@OriginalMember(owner = "client!jea", name = "l", descriptor = "I")
	public int currentAmount;

	@OriginalMember(owner = "client!jea", name = "<init>", descriptor = "()V")
	public StockMarketOffer() {
	}

	@OriginalMember(owner = "client!jea", name = "<init>", descriptor = "(Lclient!cea;)V")
	public StockMarketOffer(@OriginalArg(0) Packet packet) {
		this.statusAndType = packet.readByte();
		this.itemId = packet.readUShort();
		this.price = packet.readInt();
		this.amount = packet.readInt();
		this.currentAmount = packet.readInt();
		this.totalPrice = packet.readInt();
	}

	@OriginalMember(owner = "client!jea", name = "a", descriptor = "(I)I")
	public int isBuyOffer() {
		return (this.statusAndType & 0x8) == 8 ? 1 : 0;
	}

	@OriginalMember(owner = "client!jea", name = "a", descriptor = "(B)I")
	public int getSlotState() {
		return this.statusAndType & 0x7;
	}
	@OriginalMember(owner = "client!fca", name = "o", descriptor = "[Lclient!jea;")
	public static final StockMarketOffer[] GRAND_EXCHANGE_SLOTS = new StockMarketOffer[6];
}
