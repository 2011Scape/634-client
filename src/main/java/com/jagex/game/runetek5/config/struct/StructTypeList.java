package com.jagex.game.runetek5.config.struct;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.StructType;

@OriginalClass("client!kb")
public final class StructTypeList {

	@OriginalMember(owner = "client!kb", name = "h", descriptor = "Lclient!jr;")
	private final LRUCache cache = new LRUCache(256);

	@OriginalMember(owner = "client!kb", name = "d", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!kb", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public StructTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient) {
		this.configClient = configClient;
		this.configClient.getGroupCapacity(26);
	}

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(14) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(Z)V")
	public void cacheReset() {
		@Pc(7) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.reset();
		}
	}

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(10) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.clean(5);
		}
	}

	@OriginalMember(owner = "client!kb", name = "b", descriptor = "(II)Lclient!bda;")
	public StructType list(@OriginalArg(0) int id) {
		@Pc(14) LRUCache referenceCache = this.cache;
		@Pc(24) StructType structType;
		synchronized (this.cache) {
			structType = (StructType) this.cache.get((long) id);
		}
		if (structType != null) {
			return structType;
		}
		@Pc(37) Js5ResourceProvider configClient = this.configClient;
		@Pc(46) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(26, id);
		}
		structType = new StructType();
		if (data != null) {
			structType.decode(new Packet(data));
		}
		@Pc(68) LRUCache referenceCache2 = this.cache;
		synchronized (this.cache) {
			this.cache.put(structType, (long) id);
			return structType;
		}
	}
}
