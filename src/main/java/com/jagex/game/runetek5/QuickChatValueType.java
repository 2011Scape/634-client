package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!oaa")
public final class QuickChatValueType {

	@OriginalMember(owner = "client!oaa", name = "d", descriptor = "I")
	public final int serverTransmitSize;

	@OriginalMember(owner = "client!oaa", name = "f", descriptor = "I")
	public final int clientTransmitSize;

	@OriginalMember(owner = "client!oaa", name = "b", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!oaa", name = "e", descriptor = "I")
	public final int parameterCount;

	@OriginalMember(owner = "client!oaa", name = "<init>", descriptor = "(IIII)V")
	public QuickChatValueType(@OriginalArg(0) int id, @OriginalArg(1) int clientTransmitSize, @OriginalArg(2) int serverTransmitSize, @OriginalArg(3) int parameterCount) {
		this.serverTransmitSize = serverTransmitSize;
		this.clientTransmitSize = clientTransmitSize;
		this.id = id;
		this.parameterCount = parameterCount;
	}

	@OriginalMember(owner = "client!oaa", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!jca", name = "h", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_13 = new QuickChatValueType(12, 0, 1, 0);

	@OriginalMember(owner = "client!kga", name = "f", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_14 = new QuickChatValueType(11, 0, 1, 2);
	@OriginalMember(owner = "client!cu", name = "v", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_5 = new QuickChatValueType(14, 0, 4, 1);

	@OriginalMember(owner = "client!za", name = "o", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_26 = new QuickChatValueType(10, 2, 2, 0);

	@OriginalMember(owner = "client!wha", name = "s", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_25 = new QuickChatValueType(9, 0, 4, 1);

	@OriginalMember(owner = "client!so", name = "i", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_23 = new QuickChatValueType(0, 2, 2, 1);

	@OriginalMember(owner = "client!da", name = "r", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_24 = new QuickChatValueType(2, 4, 4, 0);

	@OriginalMember(owner = "client!sda", name = "c", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType PACKET_28 = new QuickChatValueType(4, 1, 1, 1);

	@OriginalMember(owner = "client!gh", name = "Qb", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_11 = new QuickChatValueType(7, 0, 1, 1);

	@OriginalMember(owner = "client!dp", name = "e", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_20 = new QuickChatValueType(1, 2, 2, 0);

	@OriginalMember(owner = "client!fba", name = "c", descriptor = "(II)Lclient!oaa;")
	public static QuickChatValueType forId(@OriginalArg(0) int id) {
		@Pc(8) QuickChatValueType[] types = QuickChatValueType.values();
		for (@Pc(10) int i = 0; i < types.length; i++) {
			if (id == types[i].id) {
				return types[i];
			}
		}
		return null;
	}
	@OriginalMember(owner = "client!bga", name = "Jh", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_3 = new QuickChatValueType(15, 0, 1, 0);

	@OriginalMember(owner = "client!lr", name = "G", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_16 = new QuickChatValueType(6, 0, 4, 2);

	@OriginalMember(owner = "client!mq", name = "Z", descriptor = "Lclient!oaa;")
	public static QuickChatValueType aClass234_18 = new QuickChatValueType(13, 0, 1, 0);

	@OriginalMember(owner = "client!mk", name = "a", descriptor = "(B)[Lclient!oaa;")
	public static QuickChatValueType[] values() {
		return new QuickChatValueType[] { QuickChatValueType.aClass234_23, QuickChatValueType.aClass234_20, QuickChatValueType.aClass234_24, QuickChatValueType.PACKET_28, QuickChatValueType.aClass234_16, QuickChatValueType.aClass234_11, QuickChatValueType.aClass234_7, QuickChatValueType.aClass234_25, QuickChatValueType.aClass234_26, QuickChatValueType.aClass234_14, QuickChatValueType.aClass234_13, QuickChatValueType.aClass234_18, QuickChatValueType.aClass234_5, QuickChatValueType.aClass234_3 };
	}
	@OriginalMember(owner = "client!daa", name = "q", descriptor = "Lclient!oaa;")
	public static final QuickChatValueType aClass234_7 = new QuickChatValueType(8, 0, 4, 1);
}
