package com.jagex.game.runetek5.config.invtype;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.InvType;

@OriginalClass("client!ut")
public final class InvTypeList {

	@OriginalMember(owner = "client!ut", name = "a", descriptor = "Lclient!jr;")
	private final LRUCache recentUse = new LRUCache(64);

	@OriginalMember(owner = "client!ut", name = "c", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!ut", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public InvTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient) {
		this.configClient = configClient;
		this.configClient.getGroupCapacity(5);
	}

	@OriginalMember(owner = "client!ut", name = "a", descriptor = "(II)Lclient!pv;")
	public InvType list(@OriginalArg(1) int id) {
		@Pc(6) LRUCache referenceCache = this.recentUse;
		@Pc(16) InvType invType;
		synchronized (this.recentUse) {
			invType = (InvType) this.recentUse.get((long) id);
		}
		if (invType != null) {
			return invType;
		}
		@Pc(29) Js5ResourceProvider configClient = this.configClient;
		@Pc(38) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(5, id);
		}
		invType = new InvType();
		if (data != null) {
			invType.decode(new Packet(data));
		}
		@Pc(64) LRUCache referenceCache2 = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.put(invType, (long) id);
			return invType;
		}
	}
}
