package com.jagex.game.runetek5.client;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!sj")
public final class Game {

	@OriginalMember(owner = "client!sj", name = "d", descriptor = "Ljava/lang/String;")
	public final String domainName;

	@OriginalMember(owner = "client!sj", name = "f", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!sj", name = "<init>", descriptor = "(Ljava/lang/String;I)V")
	public Game(@OriginalArg(0) String string, @OriginalArg(1) int id) {
		this.domainName = string;
		this.id = id;
	}

	@OriginalMember(owner = "client!sj", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!gt", name = "o", descriptor = "Lclient!sj;")
	public static final Game RUNESCAPE = new Game("runescape", 0);
	@OriginalMember(owner = "client!tia", name = "e", descriptor = "Lclient!sj;")
	public static final Game GAME3 = new Game("game3", 2);

	@OriginalMember(owner = "client!tw", name = "C", descriptor = "Lclient!sj;")
	public static final Game STELLAR_DAWN = new Game("stellardawn", 1);
	@OriginalMember(owner = "client!mm", name = "v", descriptor = "Lclient!sj;")
	public static final Game GAME4 = new Game("game4", 3);
	@OriginalMember(owner = "client!pf", name = "M", descriptor = "Lclient!sj;")
	public static Game CURRENT_GAME = null;
}
