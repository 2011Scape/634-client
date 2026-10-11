package com.jagex.game.runetek5.config.quest;

import com.jagex.game.runetek5.config.quest.QuestType;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!qga")
public final class QuestTypeList {

	@OriginalMember(owner = "client!qga", name = "d", descriptor = "Lclient!jr;")
	private final LRUCache cache = new LRUCache(64);

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!qga", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public QuestTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient) {
		this.configClient = configClient;
		if (this.configClient != null) {
			this.configClient.getGroupCapacity(35);
		}
	}

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "(B)V")
	public void cacheReset() {
		@Pc(2) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.reset();
		}
	}

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "(II)Lclient!cq;")
	public QuestType list(@OriginalArg(1) int id) {
		@Pc(6) LRUCache referenceCache = this.cache;
		@Pc(16) QuestType questType;
		synchronized (this.cache) {
			questType = (QuestType) this.cache.get((long) id);
		}
		if (questType != null) {
			return questType;
		}
		@Pc(29) Js5ResourceProvider configClient = this.configClient;
		@Pc(38) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(35, id);
		}
		questType = new QuestType();
		if (data != null) {
			questType.decode(new Packet(data));
		}
		questType.postDecode();
		@Pc(69) LRUCache referenceCache2 = this.cache;
		synchronized (this.cache) {
			this.cache.put(questType, (long) id);
			return questType;
		}
	}

	@OriginalMember(owner = "client!qga", name = "b", descriptor = "(II)V")
	public void cacheClean() {
		@Pc(2) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.clean(5);
		}
	}

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "(I)V")
	public void cacheRemoveSoftReferences() {
		@Pc(2) LRUCache referenceCache = this.cache;
		synchronized (this.cache) {
			this.cache.removeSoftReferences();
		}
	}
}
