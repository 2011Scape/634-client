package com.jagex.game.runetek5.config.npctype;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.npc.NPCType;

@OriginalClass("client!vda")
public final class NPCTypeList {

	@OriginalMember(owner = "client!vda", name = "r", descriptor = "I")
	public int featureMask;

	@OriginalMember(owner = "client!vda", name = "b", descriptor = "Lclient!jr;")
	private final LRUCache typeCache = new LRUCache(64);

	@OriginalMember(owner = "client!vda", name = "o", descriptor = "Lclient!jr;")
	public final LRUCache modelCache = new LRUCache(50);

	@OriginalMember(owner = "client!vda", name = "q", descriptor = "Lclient!jr;")
	public final LRUCache headModels = new LRUCache(5);

	@OriginalMember(owner = "client!vda", name = "h", descriptor = "Z")
	public boolean allowMembers;

	@OriginalMember(owner = "client!vda", name = "j", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "Lclient!in;")
	public final Js5ResourceProvider meshes;

	@OriginalMember(owner = "client!vda", name = "c", descriptor = "Lclient!sj;")
	public final Game game;

	@OriginalMember(owner = "client!vda", name = "<init>", descriptor = "(Lclient!sj;IZLclient!in;Lclient!in;)V")
	public NPCTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) boolean allowMembers, @OriginalArg(3) Js5ResourceProvider configClient, @OriginalArg(4) Js5ResourceProvider meshes) {
		this.allowMembers = allowMembers;
		this.configClient = configClient;
		this.meshes = meshes;
		this.game = modeGame;
		if (this.configClient != null) {
			@Pc(41) int lastGroupId = this.configClient.getCapacity() - 1;
			this.configClient.getGroupCapacity(lastGroupId);
		}
	}

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "(BZ)V")
	public void setAllowMembers(@OriginalArg(1) boolean allowMembers) {
		if (this.allowMembers != allowMembers) {
			this.allowMembers = allowMembers;
			this.cacheReset();
		}
	}

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "(II)V")
	public void setFeatureMask(@OriginalArg(1) int featureMask) {
		this.featureMask = featureMask;
		@Pc(9) LRUCache referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
		referenceCache = this.headModels;
		synchronized (this.headModels) {
			this.headModels.reset();
		}
	}

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "(I)V")
	public void clearModels() {
		@Pc(2) LRUCache referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
		referenceCache = this.headModels;
		synchronized (this.headModels) {
			this.headModels.reset();
		}
	}

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "(IZ)V")
	public void cacheClean() {
		@Pc(6) LRUCache referenceCache = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.clean(5);
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.clean(5);
		}
		referenceCache = this.headModels;
		synchronized (this.headModels) {
			this.headModels.clean(5);
		}
	}

	@OriginalMember(owner = "client!vda", name = "b", descriptor = "(I)V")
	public void cacheReset() {
		@Pc(6) LRUCache referenceCache = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.reset();
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.reset();
		}
		referenceCache = this.headModels;
		synchronized (this.headModels) {
			this.headModels.reset();
		}
	}

	@OriginalMember(owner = "client!vda", name = "b", descriptor = "(II)Lclient!bb;")
	public NPCType list(@OriginalArg(0) int id) {
		@Pc(6) LRUCache referenceCache = this.typeCache;
		@Pc(16) NPCType npcType;
		synchronized (this.typeCache) {
			npcType = (NPCType) this.typeCache.get((long) id);
		}
		if (npcType != null) {
			return npcType;
		}
		@Pc(29) Js5ResourceProvider configClient = this.configClient;
		@Pc(44) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(NPCTypeList.getGroupId(id), NPCTypeList.getFileId(id));
		}
		npcType = new NPCType();
		npcType.id = id;
		npcType.npcTypeList = this;
		if (data != null) {
			npcType.decode(new Packet(data));
		}
		npcType.postDecode();
		@Pc(75) LRUCache referenceCache2 = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.put(npcType, (long) id);
			return npcType;
		}
	}

	@OriginalMember(owner = "client!vda", name = "c", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(6) LRUCache referenceCache = this.typeCache;
		synchronized (this.typeCache) {
			this.typeCache.removeSoftReferences();
		}
		referenceCache = this.modelCache;
		synchronized (this.modelCache) {
			this.modelCache.removeSoftReferences();
		}
		referenceCache = this.headModels;
		synchronized (this.headModels) {
			this.headModels.removeSoftReferences();
		}
	}
	@OriginalMember(owner = "client!mv", name = "a", descriptor = "(IB)I")
	public static int getGroupId(@OriginalArg(0) int id) {
		return id >>> 7;
	}
	@OriginalMember(owner = "client!u", name = "a", descriptor = "(II)I")
	public static int getFileId(@OriginalArg(1) int id) {
		return id & 0x7F;
	}
}
