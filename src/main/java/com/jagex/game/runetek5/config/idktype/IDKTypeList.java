package com.jagex.game.runetek5.config.idktype;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.Js5ConfigGroup;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.identitykit.IdkType;

@OriginalClass("client!oha")
public final class IDKTypeList {

	@OriginalMember(owner = "client!oha", name = "d", descriptor = "Lclient!jr;")
	private final LRUCache recentUse = new LRUCache(64);

	@OriginalMember(owner = "client!oha", name = "h", descriptor = "Lclient!in;")
	public final Js5ResourceProvider meshResource;

	@OriginalMember(owner = "client!oha", name = "b", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!oha", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	public IDKTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient, @OriginalArg(3) Js5ResourceProvider meshResource) {
		this.meshResource = meshResource;
		this.configClient = configClient;
		this.configClient.getGroupCapacity(Js5ConfigGroup.IDKTYPE);
	}

	@OriginalMember(owner = "client!oha", name = "a", descriptor = "(BI)Lclient!ic;")
	public IdkType list(@OriginalArg(1) int id) {
		@Pc(12) LRUCache referenceCache = this.recentUse;
		@Pc(22) IdkType idkType;
		synchronized (this.recentUse) {
			idkType = (IdkType) this.recentUse.get((long) id);
		}
		if (idkType != null) {
			return idkType;
		}
		@Pc(35) Js5ResourceProvider configClient = this.configClient;
		@Pc(46) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(Js5ConfigGroup.IDKTYPE, id);
		}
		idkType = new IdkType();
		idkType.identityTypeList = this;
		if (data != null) {
			idkType.decode(new Packet(data));
		}
		@Pc(73) LRUCache referenceCache2 = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.put(idkType, (long) id);
			return idkType;
		}
	}

	@OriginalMember(owner = "client!oha", name = "b", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(2) LRUCache lock = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!oha", name = "a", descriptor = "(B)V")
	public void cacheReset() {
		@Pc(12) LRUCache lock = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.reset();
		}
	}

	@OriginalMember(owner = "client!oha", name = "a", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(10) LRUCache referenceCache = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.clean(5);
		}
	}
}
