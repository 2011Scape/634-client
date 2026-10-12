package com.jagex.game.runetek5.config.quest

import com.jagex.game.runetek5.config.quest.QuestType
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import withLock

@OriginalClass("client!qga")
class QuestTypeList {

	@OriginalMember(owner = "client!qga", name = "d", descriptor = "Lclient!jr;")
	private val cache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!qga", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider?) {
		this.configClient = configClient
		if (this.configClient != null) {
			this.configClient.getGroupCapacity(35)
		}
	}

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "(B)V")
	fun cacheReset() {
		@Pc(2) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.reset()
		}
	}

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "(II)Lclient!cq;")
	fun list(@OriginalArg(1) id: Int): QuestType {
		@Pc(6) val referenceCache: LRUCache = this.cache
		@Pc(16) var questType: QuestType?
		withLock(this.cache) {
			questType = this.cache.get(id.toLong()) as QuestType?
		}
		if (questType != null) {
			return questType
		}
		@Pc(29) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(38) var data: ByteArray?
		withLock(this.configClient!!) {
			data = this.configClient.getFile(35, id)
		}
		questType = QuestType()
		if (data != null) {
			questType.decode(Packet(data))
		}
		questType.postDecode()
		@Pc(69) val referenceCache2: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.put(questType, id.toLong())
			return questType
		}
	}

	@OriginalMember(owner = "client!qga", name = "b", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(2) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.clean(5)
		}
	}

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(2) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.removeSoftReferences()
		}
	}
}
