package com.jagex.game.runetek5.config.flu;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qe")
public final class FluTypeList {

	@OriginalMember(owner = "client!qe", name = "b", descriptor = "Lclient!jr;")
	private final LRUCache cache = new LRUCache(128);

	@OriginalMember(owner = "client!qe", name = "e", descriptor = "Lclient!in;")
	private final Js5ResourceProvider underlayResource;

	@OriginalMember(owner = "client!qe", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public FluTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider underlayResource) {
		this.underlayResource = underlayResource;
		this.underlayResource.getGroupCapacity(1);
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(11) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(ZI)Lclient!hk;")
	public FluType list(@OriginalArg(1) int id) {
		@Pc(11) LRUCache referenceCache = this.cache;
		@Pc(21) FluType floorUnderlayType;
		synchronized (this.cache) {
			floorUnderlayType = (FluType) this.cache.get((long) id);
		}
		if (floorUnderlayType != null) {
			return floorUnderlayType;
		}
		@Pc(34) Js5ResourceProvider underlayResource = this.underlayResource;
		@Pc(43) byte[] data;
		synchronized (this.underlayResource) {
			data = this.underlayResource.getFile(1, id);
		}
		floorUnderlayType = new FluType();
		if (data != null) {
			floorUnderlayType.decode(new Packet(data));
		}
		@Pc(65) LRUCache referenceCache2 = this.cache;
		synchronized (this.cache) {
			this.cache.put(floorUnderlayType, (long) id);
			return floorUnderlayType;
		}
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(Z)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.reset();
		}
	}

	@OriginalMember(owner = "client!qe", name = "b", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(6) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.clean(5);
		}
	}
}
