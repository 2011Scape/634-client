package com.jagex.game.runetek5.config.flo;

import com.jagex.game.runetek5.config.flo.FloType;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!cb")
public final class FloTypeList {

	@OriginalMember(owner = "client!cb", name = "e", descriptor = "Lclient!jr;")
	private final LRUCache overlayCache = new LRUCache(64);

	@OriginalMember(owner = "client!cb", name = "s", descriptor = "I")
	public int lastLoadedOverlayId = 0;

	@OriginalMember(owner = "client!cb", name = "n", descriptor = "Lclient!in;")
	private final Js5ResourceProvider floResource;

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "I")
	public final int totalFileCount;

	@OriginalMember(owner = "client!cb", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public FloTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider floResource) {
		this.floResource = floResource;
		this.totalFileCount = this.floResource.getGroupCapacity(4);
	}

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(2) LRUCache referenceCache = this.overlayCache;
		synchronized (this.overlayCache) {
			this.overlayCache.clean(5);
		}
	}

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(B)V")
	public void cacheRemoveSoftReferences() {
		@Pc(6) LRUCache referenceCache = this.overlayCache;
		synchronized (this.overlayCache) {
			this.overlayCache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(IZ)Lclient!vd;")
	public FloType list(@OriginalArg(0) int id) {
		@Pc(11) LRUCache referenceCache = this.overlayCache;
		@Pc(21) FloType spotAnimType;
		synchronized (this.overlayCache) {
			spotAnimType = (FloType) this.overlayCache.get((long) id);
		}
		if (spotAnimType != null) {
			return spotAnimType;
		}
		@Pc(34) Js5ResourceProvider floResource = this.floResource;
		@Pc(43) byte[] data;
		synchronized (this.floResource) {
			data = this.floResource.getFile(4, id);
		}
		spotAnimType = new FloType();
		spotAnimType.overlayId = id;
		spotAnimType.floTypeList = this;
		if (data != null) {
			spotAnimType.decode(new Packet(data));
		}
		spotAnimType.postDecode();
		@Pc(74) LRUCache referenceCache2 = this.overlayCache;
		synchronized (this.overlayCache) {
			this.overlayCache.put(spotAnimType, (long) id);
			return spotAnimType;
		}
	}

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.overlayCache;
		synchronized (this.overlayCache) {
			this.overlayCache.reset();
		}
	}
}
