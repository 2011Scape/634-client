package com.jagex.game.runetek5.config.sun;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.sun.SunType;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!hq")
public final class SunTypeList {

	@OriginalMember(owner = "client!hq", name = "a", descriptor = "Lclient!jr;")
	private final LRUCache cache = new LRUCache(16);

	@OriginalMember(owner = "client!hq", name = "g", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!hq", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public SunTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient) {
		this.configClient = configClient;
		this.configClient.getGroupCapacity(30);
	}

	@OriginalMember(owner = "client!hq", name = "a", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(8) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.clean(5);
		}
	}

	@OriginalMember(owner = "client!hq", name = "a", descriptor = "(B)V")
	public void cacheReset() {
		@Pc(14) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.reset();
		}
	}

	@OriginalMember(owner = "client!hq", name = "b", descriptor = "(II)Lclient!iea;")
	public SunType list(@OriginalArg(0) int id) {
		@Pc(6) LRUCache referenceCache = this.cache;
		@Pc(16) SunType varcType;
		synchronized (this.cache) {
			varcType = (SunType) this.cache.get((long) id);
		}
		if (varcType != null) {
			return varcType;
		}
		@Pc(29) Js5ResourceProvider configClient = this.configClient;
		@Pc(38) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(30, id);
		}
		varcType = new SunType();
		if (data != null) {
			varcType.decode(new Packet(data));
		}
		@Pc(66) LRUCache referenceCache2 = this.cache;
		synchronized (this.cache) {
			this.cache.put(varcType, (long) id);
			return varcType;
		}
	}

	@OriginalMember(owner = "client!hq", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(2) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.removeSoftReferences();
		}
	}
}
