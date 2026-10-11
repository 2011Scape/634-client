package com.jagex.game.runetek5.config.vartype.player;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!rv")
public final class VarPlayerTypeListClient {

	@OriginalMember(owner = "client!rv", name = "i", descriptor = "Lclient!jr;")
	private final LRUCache cache = new LRUCache(64);

	@OriginalMember(owner = "client!rv", name = "c", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!rv", name = "f", descriptor = "I")
	public final int num;

	@OriginalMember(owner = "client!rv", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public VarPlayerTypeListClient(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient) {
		this.configClient = configClient;
		if (this.configClient == null) {
			this.num = 0;
		} else {
			this.num = this.configClient.getGroupCapacity(16);
		}
	}

	@OriginalMember(owner = "client!rv", name = "a", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(8) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.clean(5);
		}
	}

	@OriginalMember(owner = "client!rv", name = "b", descriptor = "(II)Lclient!ow;")
	public VarPlayerType list(@OriginalArg(1) int id) {
		@Pc(6) LRUCache referenceCache = this.cache;
		@Pc(16) VarPlayerType varPlayerType;
		synchronized (this.cache) {
			varPlayerType = (VarPlayerType) this.cache.get((long) id);
		}
		if (varPlayerType != null) {
			return varPlayerType;
		}
		@Pc(29) Js5ResourceProvider configClient = this.configClient;
		@Pc(38) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(16, id);
		}
		varPlayerType = new VarPlayerType();
		if (data != null) {
			varPlayerType.decode(new Packet(data));
		}
		@Pc(66) LRUCache referenceCache2 = this.cache;
		synchronized (this.cache) {
			this.cache.put(varPlayerType, (long) id);
			return varPlayerType;
		}
	}

	@OriginalMember(owner = "client!rv", name = "a", descriptor = "(B)V")
	public void cacheReset() {
		@Pc(6) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.reset();
		}
	}

	@OriginalMember(owner = "client!rv", name = "b", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(11) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.removeSoftReferences();
		}
	}
}
