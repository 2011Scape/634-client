package com.jagex.game.runetek5.config.sequence.spot;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!cn")
public final class SpotAnimationTypeList {

	@OriginalMember(owner = "client!cn", name = "n", descriptor = "I")
	public int featureMask;

	@OriginalMember(owner = "client!cn", name = "l", descriptor = "Lclient!jr;")
	private final LRUCache definitionCache = new LRUCache(64);

	@OriginalMember(owner = "client!cn", name = "m", descriptor = "Lclient!jr;")
	public final LRUCache modelCache = new LRUCache(60);

	@OriginalMember(owner = "client!cn", name = "d", descriptor = "Lclient!in;")
	public final Js5ResourceProvider modelResource;

	@OriginalMember(owner = "client!cn", name = "b", descriptor = "Lclient!in;")
	private final Js5ResourceProvider gfxResource;

	@OriginalMember(owner = "client!cn", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	public SpotAnimationTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider gfxResource, @OriginalArg(3) Js5ResourceProvider modelResource) {
		this.modelResource = modelResource;
		this.gfxResource = gfxResource;
		@Pc(26) int lastGroupId = this.gfxResource.getCapacity() - 1;
		this.gfxResource.getGroupCapacity(lastGroupId);
	}

	@OriginalMember(owner = "client!cn", name = "a", descriptor = "(II)V")
	public void setFeatureMask(@OriginalArg(1) int featureMask) {
		this.featureMask = featureMask;
		@Pc(9) LRUCache referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
	}

	@OriginalMember(owner = "client!cn", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(2) LRUCache referenceCache = this.definitionCache;
		synchronized (this.definitionCache) {
			this.definitionCache.removeSoftReferences();
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.removeSoftReferences();
		}
	}

	@OriginalMember(owner = "client!cn", name = "a", descriptor = "(BI)Lclient!fk;")
	public SpotAnimationType list(@OriginalArg(1) int currentId) {
		@Pc(12) LRUCache referenceCache = this.definitionCache;
		@Pc(22) SpotAnimationType spotAnimationType;
		synchronized (this.definitionCache) {
			spotAnimationType = (SpotAnimationType) this.definitionCache.get((long) currentId);
		}
		if (spotAnimationType != null) {
			return spotAnimationType;
		}
		@Pc(35) Js5ResourceProvider gfxResource = this.gfxResource;
		@Pc(48) byte[] data;
		synchronized (this.gfxResource) {
			data = this.gfxResource.getFile(SpotAnimationTypeList.getGroupId(currentId), SpotAnimationTypeList.getFileId(currentId));
		}
		spotAnimationType = new SpotAnimationType();
		spotAnimationType.gfxTypeList = this;
		spotAnimationType.currentId = currentId;
		if (data != null) {
			spotAnimationType.decode(new Packet(data));
		}
		@Pc(76) LRUCache referenceCache2 = this.definitionCache;
		synchronized (this.definitionCache) {
			this.definitionCache.put(spotAnimationType, (long) currentId);
			return spotAnimationType;
		}
	}

	@OriginalMember(owner = "client!cn", name = "a", descriptor = "(IB)V")
	public void cacheClean() {
		@Pc(2) LRUCache referenceCache = this.definitionCache;
		synchronized (this.definitionCache) {
			this.definitionCache.clean(5);
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.clean(5);
		}
	}

	@OriginalMember(owner = "client!cn", name = "b", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(6) LRUCache referenceCache = this.definitionCache;
		synchronized (this.definitionCache) {
			this.definitionCache.reset();
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
	}
	@OriginalMember(owner = "client!dga", name = "c", descriptor = "(II)I")
	public static int getFileId(@OriginalArg(0) int id) {
		return id & 0xFF;
	}
	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(BI)I")
	public static int getGroupId(@OriginalArg(1) int value) {
		return value >>> 8;
	}
}
