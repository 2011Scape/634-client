package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!og")
public final class ChatLine {

	@OriginalMember(owner = "client!og", name = "k", descriptor = "I")
	public int identifier = ChatLine.nextIdentifier();

	@OriginalMember(owner = "client!og", name = "h", descriptor = "Ljava/lang/String;")
	public String message;

	@OriginalMember(owner = "client!og", name = "l", descriptor = "I")
	public int type;

	@OriginalMember(owner = "client!og", name = "m", descriptor = "Ljava/lang/String;")
	public String name;

	@OriginalMember(owner = "client!og", name = "e", descriptor = "Ljava/lang/String;")
	public String simpleName;

	@OriginalMember(owner = "client!og", name = "b", descriptor = "Ljava/lang/String;")
	public String clan;

	@OriginalMember(owner = "client!og", name = "j", descriptor = "I")
	public int time;

	@OriginalMember(owner = "client!og", name = "d", descriptor = "Ljava/lang/String;")
	public String crownedName;

	@OriginalMember(owner = "client!og", name = "g", descriptor = "I")
	public int effectFlags;

	@OriginalMember(owner = "client!og", name = "f", descriptor = "I")
	public int quickchatMessageId;

	@OriginalMember(owner = "client!og", name = "<init>", descriptor = "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V")
	public ChatLine(@OriginalArg(0) int type, @OriginalArg(1) int effectFlags, @OriginalArg(2) String crownedName, @OriginalArg(3) String name, @OriginalArg(4) String simpleName, @OriginalArg(5) String clan, @OriginalArg(6) int quickchatMessageId, @OriginalArg(7) String message) {
		this.message = message;
		this.type = type;
		this.name = name;
		this.simpleName = simpleName;
		this.clan = clan;
		this.time = ClientState.clientCycle;
		this.crownedName = crownedName;
		this.effectFlags = effectFlags;
		this.quickchatMessageId = quickchatMessageId;
	}

	@OriginalMember(owner = "client!og", name = "a", descriptor = "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;)V")
	public void set(@OriginalArg(0) int type, @OriginalArg(1) int effectFlags, @OriginalArg(2) String simpleName, @OriginalArg(3) String name, @OriginalArg(4) String clan, @OriginalArg(6) int quickchatMessageId, @OriginalArg(7) String crownedName, @OriginalArg(8) String message) {
		this.identifier = ChatLine.nextIdentifier();
		this.name = name;
		this.crownedName = crownedName;
		this.time = ClientState.clientCycle;
		this.simpleName = simpleName;
		this.type = type;
		this.clan = clan;
		this.message = message;
		this.effectFlags = effectFlags;
		this.quickchatMessageId = quickchatMessageId;
	}
	@OriginalMember(owner = "client!jb", name = "d", descriptor = "(B)I")
	public static int nextIdentifier() {
		return ChatLine.nextChatLineId++;
	}
	@OriginalMember(owner = "client!vn", name = "P", descriptor = "I")
	public static int nextChatLineId = 0;
}
