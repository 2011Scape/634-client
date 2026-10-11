package com.jagex.game.runetek5.config.mapsprite;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!oda")
public final class MapSpriteTypeList {

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "Lclient!jr;")
	private LRUCache mapspriteTypeCache = new LRUCache(64);

	@OriginalMember(owner = "client!oda", name = "n", descriptor = "Lclient!jr;")
	public LRUCache mapspriteCache = new LRUCache(64);

	@OriginalMember(owner = "client!oda", name = "c", descriptor = "Lclient!in;")
	public final Js5ResourceProvider mapspriteResource;

	@OriginalMember(owner = "client!oda", name = "i", descriptor = "Lclient!in;")
	private final Js5ResourceProvider mapspriteTypeResource;

	@OriginalMember(owner = "client!oda", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	public MapSpriteTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider mapspriteTypeResource, @OriginalArg(3) Js5ResourceProvider mapspriteResource) {
		this.mapspriteResource = mapspriteResource;
		this.mapspriteTypeResource = mapspriteTypeResource;
		this.mapspriteTypeResource.getGroupCapacity(34);
	}

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "(BI)Lclient!rw;")
	public MapSpriteType list(@OriginalArg(1) int id) {
		@Pc(6) LRUCache referenceCache = this.mapspriteTypeCache;
		@Pc(16) MapSpriteType msiType;
		synchronized (this.mapspriteTypeCache) {
			msiType = (MapSpriteType) this.mapspriteTypeCache.get((long) id);
		}
		if (msiType != null) {
			return msiType;
		}
		@Pc(29) Js5ResourceProvider mapspriteTypeResource = this.mapspriteTypeResource;
		@Pc(40) byte[] data;
		synchronized (this.mapspriteTypeResource) {
			data = this.mapspriteTypeResource.getFile(34, id);
		}
		msiType = new MapSpriteType();
		msiType.mapspriteTypeList = this;
		if (data != null) {
			msiType.decode(new Packet(data));
		}
		@Pc(70) LRUCache referenceCache2 = this.mapspriteTypeCache;
		synchronized (this.mapspriteTypeCache) {
			this.mapspriteTypeCache.put(msiType, (long) id);
			return msiType;
		}
	}

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "(B)V")
	public void cacheRemoveSoftReferences() {
		@Pc(2) LRUCache referenceCache = this.mapspriteTypeCache;
		synchronized (this.mapspriteTypeCache) {
			this.mapspriteTypeCache.removeSoftReferences();
		}
		referenceCache = this.mapspriteCache;
		synchronized (this.mapspriteCache) {
			this.mapspriteCache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!oda", name = "b", descriptor = "(B)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.mapspriteTypeCache;
		synchronized (this.mapspriteTypeCache) {
			this.mapspriteTypeCache.reset();
		}
		referenceCache = this.mapspriteCache;
		synchronized (this.mapspriteCache) {
			this.mapspriteCache.reset();
		}
	}

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "(IB)V")
	public void cacheClean() {
		@Pc(7) LRUCache referenceCache = this.mapspriteTypeCache;
		synchronized (this.mapspriteTypeCache) {
			this.mapspriteTypeCache.clean(5);
		}
		referenceCache = this.mapspriteCache;
		synchronized (this.mapspriteCache) {
			this.mapspriteCache.clean(5);
		}
	}

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "(III)V")
	public void setCacheSize(@OriginalArg(0) int spriteCacheSize, @OriginalArg(2) int typeCacheSize) {
		this.mapspriteTypeCache = new LRUCache(typeCacheSize);
		this.mapspriteCache = new LRUCache(spriteCacheSize);
	}
}
