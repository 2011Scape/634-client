package com.jagex.game.runetek5.config.param;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!dc")
public final class ParamTypeList {

	@OriginalMember(owner = "client!dc", name = "e", descriptor = "Lclient!jr;")
	private final LRUCache cache = new LRUCache(64);

	@OriginalMember(owner = "client!dc", name = "c", descriptor = "Lclient!in;")
	private final Js5ResourceProvider paramResource;

	@OriginalMember(owner = "client!dc", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public ParamTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider paramResource) {
		this.paramResource = paramResource;
		if (this.paramResource != null) {
			this.paramResource.getGroupCapacity(11);
		}
	}

	@OriginalMember(owner = "client!dc", name = "a", descriptor = "(II)Lclient!uaa;")
	public ParamType list(@OriginalArg(0) int id) {
		@Pc(6) LRUCache referenceCache = this.cache;
		@Pc(16) ParamType paramType;
		synchronized (this.cache) {
			paramType = (ParamType) this.cache.get((long) id);
		}
		if (paramType != null) {
			return paramType;
		}
		@Pc(34) Js5ResourceProvider paramResource = this.paramResource;
		@Pc(43) byte[] data;
		synchronized (this.paramResource) {
			data = this.paramResource.getFile(11, id);
		}
		paramType = new ParamType();
		if (data != null) {
			paramType.decode(new Packet(data));
		}
		@Pc(65) LRUCache referenceCache2 = this.cache;
		synchronized (this.cache) {
			this.cache.put(paramType, (long) id);
			return paramType;
		}
	}

	@OriginalMember(owner = "client!dc", name = "a", descriptor = "(B)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.reset();
		}
	}

	@OriginalMember(owner = "client!dc", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(6) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!dc", name = "b", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(2) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.clean(5);
		}
	}
}
