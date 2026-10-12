package com.jagex.game.runetek5.config.bas

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import withLock

@OriginalClass("client!uga")
class BasTypeList {

	@OriginalMember(owner = "client!uga", name = "i", descriptor = "Lclient!jr;")
	private val basCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!uga", name = "g", descriptor = "Lclient!in;")
	private val basResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!uga", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) basResource: Js5ResourceProvider?) {
		this.basResource = basResource
		this.basResource!!.getGroupCapacity(32)
	}

	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(II)Lclient!sg;")
	fun list(@OriginalArg(0) id: Int): BasType? {
		@Pc(6) val referenceCache: LRUCache = this.basCache
		@Pc(16) var basType: BasType?
		withLock(this.basCache) {
			basType = this.basCache.get(id.toLong()) as BasType?
		}
		if (basType != null) {
			return basType
		}
		@Pc(29) val basResource: Js5ResourceProvider? = this.basResource
		@Pc(38) var data: ByteArray?
		withLock(this.basResource!!) {
			data = this.basResource.getFile(32, id)
		}
		basType = BasType()
		if (data != null) {
			basType!!.decode(Packet(data))
		}
		@Pc(60) val referenceCache2: LRUCache = this.basCache
		withLock(this.basCache) {
			this.basCache.put(basType, id.toLong())
			return basType
		}
	}

	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(BI)V")
	fun cacheClean() {
		@Pc(2) val referenceCache: LRUCache = this.basCache
		withLock(this.basCache) {
			this.basCache.clean(5)
		}
	}

	@OriginalMember(owner = "client!uga", name = "b", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(6) val referenceCache: LRUCache = this.basCache
		withLock(this.basCache) {
			this.basCache.reset()
		}
	}

	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(B)V")
	fun cacheRemoveSoftReferences() {
		@Pc(2) val referenceCache: LRUCache = this.basCache
		withLock(this.basCache) {
			this.basCache.removeSoftReferences()
		}
	}
}
