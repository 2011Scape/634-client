package com.jagex.game.runetek5.config.bas;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!uga")
public final class BasTypeList {

	@OriginalMember(owner = "client!uga", name = "i", descriptor = "Lclient!jr;")
	private final LRUCache basCache = new LRUCache(64);

	@OriginalMember(owner = "client!uga", name = "g", descriptor = "Lclient!in;")
	private final Js5ResourceProvider basResource;

	@OriginalMember(owner = "client!uga", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public BasTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider basResource) {
		this.basResource = basResource;
		this.basResource.getGroupCapacity(32);
	}

	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(II)Lclient!sg;")
	public BasType list(@OriginalArg(0) int id) {
		@Pc(6) LRUCache referenceCache = this.basCache;
		@Pc(16) BasType basType;
		synchronized (this.basCache) {
			basType = (BasType) this.basCache.get((long) id);
		}
		if (basType != null) {
			return basType;
		}
		@Pc(29) Js5ResourceProvider basResource = this.basResource;
		@Pc(38) byte[] data;
		synchronized (this.basResource) {
			data = this.basResource.getFile(32, id);
		}
		basType = new BasType();
		if (data != null) {
			basType.decode(new Packet(data));
		}
		@Pc(60) LRUCache referenceCache2 = this.basCache;
		synchronized (this.basCache) {
			this.basCache.put(basType, (long) id);
			return basType;
		}
	}

	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(BI)V")
	public void cacheClean() {
		@Pc(2) LRUCache referenceCache = this.basCache;
		synchronized (this.basCache) {
			this.basCache.clean(5);
		}
	}

	@OriginalMember(owner = "client!uga", name = "b", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(6) LRUCache referenceCache = this.basCache;
		synchronized (this.basCache) {
			this.basCache.reset();
		}
	}

	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(B)V")
	public void cacheRemoveSoftReferences() {
		@Pc(2) LRUCache referenceCache = this.basCache;
		synchronized (this.basCache) {
			this.basCache.removeSoftReferences();
		}
	}
}
