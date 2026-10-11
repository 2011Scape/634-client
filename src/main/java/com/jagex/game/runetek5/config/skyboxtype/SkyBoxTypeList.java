package com.jagex.game.runetek5.config.skyboxtype;

import com.jagex.game.runetek5.Skybox;
import com.jagex.game.runetek5.config.skybox.SkyboxType;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.sun.SunTypeList;
import com.jagex.game.runetek5.config.sun.SunType;
import com.jagex.game.runetek5.config.sun.Sun;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!lca")
public final class SkyBoxTypeList {

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "Lclient!jr;")
	private final LRUCache cache = new LRUCache(16);

	@OriginalMember(owner = "client!lca", name = "l", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!lca", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public SkyBoxTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient) {
		this.configClient = configClient;
		this.configClient.getGroupCapacity(29);
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(Z)V")
	public void cacheRemoveSoftReferences() {
		@Pc(6) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(BI)V")
	public void cacheClean() {
		@Pc(2) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.clean(5);
		}
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(II)Lclient!wia;")
	private SkyboxType list(@OriginalArg(0) int id) {
		@Pc(6) LRUCache referenceCache = this.cache;
		@Pc(16) SkyboxType skyboxType;
		synchronized (this.cache) {
			skyboxType = (SkyboxType) this.cache.get((long) id);
		}
		if (skyboxType != null) {
			return skyboxType;
		}
		@Pc(29) Js5ResourceProvider configClient = this.configClient;
		@Pc(38) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(29, id);
		}
		skyboxType = new SkyboxType();
		if (data != null) {
			skyboxType.decode(new Packet(data));
		}
		@Pc(60) LRUCache referenceCache2 = this.cache;
		synchronized (this.cache) {
			this.cache.put(skyboxType, (long) id);
			return skyboxType;
		}
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(B)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.reset();
		}
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(IIIILclient!hq;I)Lclient!wr;")
	public Skybox generateSkybox(@OriginalArg(0) int positionY, @OriginalArg(1) int positionZ, @OriginalArg(2) int positionX, @OriginalArg(4) SunTypeList skyBoxSphereTypeList, @OriginalArg(5) int skyboxId) {
		@Pc(7) Sun[] skyBoxSpheres = null;
		@Pc(12) SkyboxType skyboxType = this.list(skyboxId);
		if (skyboxType.sunDefinitionIds != null) {
			skyBoxSpheres = new Sun[skyboxType.sunDefinitionIds.length];
			for (@Pc(27) int i = 0; i < skyBoxSpheres.length; i++) {
				@Pc(37) SunType varcType = skyBoxSphereTypeList.list(skyboxType.sunDefinitionIds[i]);
				skyBoxSpheres[i] = new Sun(varcType.actionType, varcType.materialId, varcType.positionX, varcType.positionY, varcType.positionZ, varcType.positionOffsetStep, varcType.displayColor, varcType.invertPosition);
			}
		}
		return new Skybox(skyboxType.materialId, skyBoxSpheres, skyboxType.defaultSunIndex, positionX, positionY, positionZ);
	}
}
