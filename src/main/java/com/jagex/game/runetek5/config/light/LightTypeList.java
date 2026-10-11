package com.jagex.game.runetek5.config.light;

import com.jagex.game.runetek5.config.light.LightType;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qf")
public final class LightTypeList {

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "Lclient!jr;")
	private final LRUCache recentUse = new LRUCache(64);

	@OriginalMember(owner = "client!qf", name = "b", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!qf", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public LightTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient) {
		this.configClient = configClient;
		this.configClient.getGroupCapacity(31);
	}

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(2) LRUCache referenceCache = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(BI)Lclient!u;")
	public LightType list(@OriginalArg(1) int id) {
		@Pc(6) LRUCache referenceCache = this.recentUse;
		@Pc(16) LightType lightType;
		synchronized (this.recentUse) {
			lightType = (LightType) this.recentUse.get((long) id);
		}
		if (lightType != null) {
			return lightType;
		}
		@Pc(29) Js5ResourceProvider configClient = this.configClient;
		@Pc(38) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(31, id);
		}
		lightType = new LightType();
		if (data != null) {
			lightType.decode(new Packet(data));
		}
		@Pc(68) LRUCache referenceCache2 = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.put(lightType, (long) id);
			return lightType;
		}
	}

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(2) LRUCache referenceCache = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.clean(5);
		}
	}

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(B)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.reset();
		}
	}
}
