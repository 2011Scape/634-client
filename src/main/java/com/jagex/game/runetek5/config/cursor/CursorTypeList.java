package com.jagex.game.runetek5.config.cursor;

import com.jagex.game.runetek5.config.cursor.CursorType;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!pg")
public final class CursorTypeList {

	@OriginalMember(owner = "client!pg", name = "h", descriptor = "Lclient!jr;")
	private final LRUCache cursorTypeCache = new LRUCache(64);

	@OriginalMember(owner = "client!pg", name = "m", descriptor = "Lclient!jr;")
	public final LRUCache spriteCache = new LRUCache(2);

	@OriginalMember(owner = "client!pg", name = "k", descriptor = "Lclient!in;")
	private final Js5ResourceProvider cursorResource;

	@OriginalMember(owner = "client!pg", name = "c", descriptor = "Lclient!in;")
	public final Js5ResourceProvider spriteResource;

	@OriginalMember(owner = "client!pg", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	public CursorTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider cursorResource, @OriginalArg(3) Js5ResourceProvider spriteResource) {
		this.cursorResource = cursorResource;
		this.spriteResource = spriteResource;
		this.cursorResource.getGroupCapacity(33);
	}

	@OriginalMember(owner = "client!pg", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(2) LRUCache referenceCache = this.cursorTypeCache;
		synchronized (this.cursorTypeCache) {
			this.cursorTypeCache.removeSoftReferences();
		}
		referenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!pg", name = "a", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(6) LRUCache referenceCache = this.cursorTypeCache;
		synchronized (this.cursorTypeCache) {
			this.cursorTypeCache.clean(5);
		}
		referenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.clean(5);
		}
	}

	@OriginalMember(owner = "client!pg", name = "a", descriptor = "(BI)Lclient!sda;")
	public CursorType list(@OriginalArg(1) int id) {
		@Pc(6) LRUCache referenceCache = this.cursorTypeCache;
		@Pc(18) CursorType cursorType;
		synchronized (this.cursorTypeCache) {
			cursorType = (CursorType) this.cursorTypeCache.get((long) id);
		}
		if (cursorType != null) {
			return cursorType;
		}
		@Pc(37) Js5ResourceProvider cursorResource = this.cursorResource;
		@Pc(46) byte[] data;
		synchronized (this.cursorResource) {
			data = this.cursorResource.getFile(33, id);
		}
		cursorType = new CursorType();
		cursorType.cursorTypeList = this;
		if (data != null) {
			cursorType.decode(new Packet(data));
		}
		@Pc(71) LRUCache referenceCache2 = this.cursorTypeCache;
		synchronized (this.cursorTypeCache) {
			this.cursorTypeCache.put(cursorType, (long) id);
			return cursorType;
		}
	}

	@OriginalMember(owner = "client!pg", name = "c", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(6) LRUCache referenceCache = this.cursorTypeCache;
		synchronized (this.cursorTypeCache) {
			this.cursorTypeCache.reset();
		}
		referenceCache = this.spriteCache;
		synchronized (this.spriteCache) {
			this.spriteCache.reset();
		}
	}
}
