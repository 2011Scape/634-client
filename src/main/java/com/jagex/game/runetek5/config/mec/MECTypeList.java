package com.jagex.game.runetek5.config.mec;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!on")
public final class MECTypeList {

	@OriginalMember(owner = "client!on", name = "e", descriptor = "Lclient!jr;")
	private LRUCache definitionCache = new LRUCache(128);

	@OriginalMember(owner = "client!on", name = "k", descriptor = "Lclient!jr;")
	public LRUCache spriteCache = new LRUCache(64);

	@OriginalMember(owner = "client!on", name = "f", descriptor = "Lclient!in;")
	private final Js5ResourceProvider definitionResource;

	@OriginalMember(owner = "client!on", name = "i", descriptor = "Lclient!in;")
	public final Js5ResourceProvider spriteResource;

	@OriginalMember(owner = "client!on", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	public MECTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider definitionResource, @OriginalArg(3) Js5ResourceProvider spriteResource) {
		this.definitionResource = definitionResource;
		this.spriteResource = spriteResource;
		this.definitionResource.getGroupCapacity(36);
	}

	@OriginalMember(owner = "client!on", name = "a", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(6) LRUCache referenceCache = this.definitionCache;
		synchronized (this.definitionCache) {
			this.definitionCache.reset();
		}
		referenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.reset();
		}
	}

	@OriginalMember(owner = "client!on", name = "a", descriptor = "(BII)V")
	public void setCacheSize(@OriginalArg(1) int spriteCacheSize, @OriginalArg(2) int definitionCacheSize) {
		this.definitionCache = new LRUCache(definitionCacheSize);
		this.spriteCache = new LRUCache(spriteCacheSize);
	}

	@OriginalMember(owner = "client!on", name = "a", descriptor = "(Z)V")
	public void cacheRemoveSoftReferences() {
		@Pc(8) LRUCache referenceCache = this.definitionCache;
		synchronized (this.definitionCache) {
			this.definitionCache.removeSoftReferences();
		}
		referenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!on", name = "a", descriptor = "(IB)V")
	public void cacheClean() {
		@Pc(2) LRUCache referenceCache = this.definitionCache;
		synchronized (this.definitionCache) {
			this.definitionCache.clean(5);
		}
		referenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.clean(5);
		}
	}

	@OriginalMember(owner = "client!on", name = "b", descriptor = "(IB)Lclient!ii;")
	public MECType list(@OriginalArg(0) int mectypeId) {
		@Pc(6) LRUCache referenceCache = this.definitionCache;
		@Pc(16) MECType mapElementType;
		synchronized (this.definitionCache) {
			mapElementType = (MECType) this.definitionCache.get((long) mectypeId);
		}
		if (mapElementType != null) {
			return mapElementType;
		}
		@Pc(34) Js5ResourceProvider definitionResource = this.definitionResource;
		@Pc(43) byte[] data;
		synchronized (this.definitionResource) {
			data = this.definitionResource.getFile(36, mectypeId);
		}
		mapElementType = new MECType();
		mapElementType.initialType = this;
		mapElementType.mectypeId = mectypeId;
		if (data != null) {
			mapElementType.decode(new Packet(data));
		}
		mapElementType.postDecode();
		@Pc(74) LRUCache referenceCache2 = this.definitionCache;
		synchronized (this.definitionCache) {
			this.definitionCache.put(mapElementType, (long) mectypeId);
			return mapElementType;
		}
	}
}
