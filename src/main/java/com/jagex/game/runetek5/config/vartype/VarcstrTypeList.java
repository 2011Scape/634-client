package com.jagex.game.runetek5.config.vartype;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.config.Js5ConfigGroup;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!lba")
public final class VarcstrTypeList {

	@OriginalMember(owner = "client!lba", name = "c", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!lba", name = "f", descriptor = "I")
	public final int num;

	@OriginalMember(owner = "client!lba", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public VarcstrTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient) {
		new LRUCache(64);
		this.configClient = configClient;
		this.num = this.configClient.getGroupCapacity(Js5ConfigGroup.VARCSTR);
	}
}
