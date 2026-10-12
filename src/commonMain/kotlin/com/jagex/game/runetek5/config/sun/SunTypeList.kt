package com.jagex.game.runetek5.config.sun

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.sun.SunType
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import withLock

@OriginalClass("client!hq")
class SunTypeList {

	@OriginalMember(owner = "client!hq", name = "a", descriptor = "Lclient!jr;")
	private val cache: LRUCache = LRUCache(16)

	@OriginalMember(owner = "client!hq", name = "g", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider

	@OriginalMember(owner = "client!hq", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider) {
		this.configClient = configClient
		this.configClient.getGroupCapacity(30)
	}

	@OriginalMember(owner = "client!hq", name = "a", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(8) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.clean(5)
		}
	}

	@OriginalMember(owner = "client!hq", name = "a", descriptor = "(B)V")
	fun cacheReset() {
		@Pc(14) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.reset()
		}
	}

	@OriginalMember(owner = "client!hq", name = "b", descriptor = "(II)Lclient!iea;")
	fun list(@OriginalArg(0) id: Int): SunType {
		@Pc(6) val referenceCache: LRUCache = this.cache
		@Pc(16) var varcType: SunType?
		withLock(this.cache) {
			varcType = this.cache.get(id.toLong()) as SunType?
		}
		if (varcType != null) {
			return varcType
		}
		@Pc(29) val configClient: Js5ResourceProvider = this.configClient
		@Pc(38) var data: ByteArray?
		withLock(this.configClient) {
			data = this.configClient.getFile(30, id)
		}
		varcType = SunType()
		if (data != null) {
			varcType.decode(Packet(data))
		}
		@Pc(66) val referenceCache2: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.put(varcType, id.toLong())
			return varcType
		}
	}

	@OriginalMember(owner = "client!hq", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(2) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.removeSoftReferences()
		}
	}
}
