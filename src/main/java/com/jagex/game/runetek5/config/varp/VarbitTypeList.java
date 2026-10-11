package com.jagex.game.runetek5.config.varp;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qs")
public final class VarbitTypeList {

	@OriginalMember(owner = "client!qs", name = "d", descriptor = "Lclient!jr;")
	private LRUCache cache = new LRUCache(64);

	@OriginalMember(owner = "client!qs", name = "g", descriptor = "Lclient!in;")
	private final Js5ResourceProvider varbitResource;

	@OriginalMember(owner = "client!qs", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public VarbitTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider varbitResource) {
		this.varbitResource = varbitResource;
		if (this.varbitResource != null) {
			@Pc(20) int lastGroupId = this.varbitResource.getCapacity() - 1;
			this.varbitResource.getGroupCapacity(lastGroupId);
		}
	}

	@OriginalMember(owner = "client!qs", name = "a", descriptor = "(IB)V")
	public void cacheClean() {
		@Pc(6) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.clean(5);
		}
	}

	@OriginalMember(owner = "client!qs", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(14) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!qs", name = "a", descriptor = "(II)V")
	public void setCacheSize(@OriginalArg(0) int capacity) {
		@Pc(14) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.reset();
			this.cache = new LRUCache(capacity);
		}
	}

	@OriginalMember(owner = "client!qs", name = "b", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(10) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.reset();
		}
	}

	@OriginalMember(owner = "client!qs", name = "b", descriptor = "(II)Lclient!bia;")
	public VarbitType list(@OriginalArg(0) int id) {
		@Pc(6) LRUCache referenceCache = this.cache;
		@Pc(16) VarbitType varbitType;
		synchronized (this.cache) {
			varbitType = (VarbitType) this.cache.get((long) id);
		}
		if (varbitType != null) {
			return varbitType;
		}
		@Pc(29) Js5ResourceProvider varbitResource = this.varbitResource;
		@Pc(44) byte[] data;
		synchronized (this.varbitResource) {
			data = this.varbitResource.getFile(VarbitTypeList.getGroupId(id), VarbitTypeList.getFileId(id));
		}
		varbitType = new VarbitType();
		if (data != null) {
			varbitType.decode(new Packet(data));
		}
		@Pc(73) LRUCache referenceCache2 = this.cache;
		synchronized (this.cache) {
			this.cache.put(varbitType, (long) id);
			return varbitType;
		}
	}
	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(ZI)I")
	public static int getGroupId(@OriginalArg(1) int id) {
		return id >>> 10;
	}
	@OriginalMember(owner = "client!ij", name = "a", descriptor = "(ZI)I")
	public static int getFileId(@OriginalArg(1) int id) {
		return id & 0x3FF;
	}
}
