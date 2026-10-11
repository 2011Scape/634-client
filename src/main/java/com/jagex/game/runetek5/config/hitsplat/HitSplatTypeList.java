package com.jagex.game.runetek5.config.hitsplat;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!sb")
public final class HitSplatTypeList {

	@OriginalMember(owner = "client!sb", name = "a", descriptor = "Lclient!jr;")
	public final LRUCache spriteCache = new LRUCache(20);

	@OriginalMember(owner = "client!sb", name = "n", descriptor = "Lclient!jr;")
	private final LRUCache hitsplatTypeCache = new LRUCache(64);

	@OriginalMember(owner = "client!sb", name = "l", descriptor = "Lclient!in;")
	public final Js5ResourceProvider spriteResource;

	@OriginalMember(owner = "client!sb", name = "e", descriptor = "Lclient!in;")
	private final Js5ResourceProvider hitsplatResource;

	@OriginalMember(owner = "client!sb", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	public HitSplatTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider hitsplatResource, @OriginalArg(3) Js5ResourceProvider spriteResource) {
		this.spriteResource = spriteResource;
		this.hitsplatResource = hitsplatResource;
		this.hitsplatResource.getGroupCapacity(46);
	}

	@OriginalMember(owner = "client!sb", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(6) LRUCache referenceCache = this.hitsplatTypeCache;
		synchronized (this.hitsplatTypeCache) {
			this.hitsplatTypeCache.removeSoftReferences();
		}
		referenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!sb", name = "a", descriptor = "(II)Lclient!ia;")
	public HitSplatType list(@OriginalArg(1) int id) {
		@Pc(6) LRUCache referenceCache = this.hitsplatTypeCache;
		@Pc(18) HitSplatType hitmarkType;
		synchronized (this.hitsplatTypeCache) {
			hitmarkType = (HitSplatType) this.hitsplatTypeCache.get((long) id);
		}
		if (hitmarkType != null) {
			return hitmarkType;
		}
		@Pc(31) Js5ResourceProvider hitsplatResource = this.hitsplatResource;
		@Pc(40) byte[] data;
		synchronized (this.hitsplatResource) {
			data = this.hitsplatResource.getFile(46, id);
		}
		hitmarkType = new HitSplatType();
		hitmarkType.typelist = this;
		if (data != null) {
			hitmarkType.decode(new Packet(data));
		}
		@Pc(67) LRUCache referenceCache2 = this.hitsplatTypeCache;
		synchronized (this.hitsplatTypeCache) {
			this.hitsplatTypeCache.put(hitmarkType, (long) id);
			return hitmarkType;
		}
	}

	@OriginalMember(owner = "client!sb", name = "b", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.hitsplatTypeCache;
		synchronized (this.hitsplatTypeCache) {
			this.hitsplatTypeCache.reset();
		}
		referenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.reset();
		}
	}

	@OriginalMember(owner = "client!sb", name = "b", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(6) LRUCache referenceCache = this.hitsplatTypeCache;
		synchronized (this.hitsplatTypeCache) {
			this.hitsplatTypeCache.clean(5);
		}
		referenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.clean(5);
		}
	}
}
