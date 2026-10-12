package com.jagex.game.runetek5.config.flu

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!qe")
class FluTypeList {

	@OriginalMember(owner = "client!qe", name = "b", descriptor = "Lclient!jr;")
	private val cache: LRUCache = LRUCache(128)

	@OriginalMember(owner = "client!qe", name = "e", descriptor = "Lclient!in;")
	private val underlayResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!qe", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) underlayResource: Js5ResourceProvider?) {
		this.underlayResource = underlayResource
		this.underlayResource!!.getGroupCapacity(1)
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(11) val referenceCache: LRUCache = this.cache
		synchronized(this.cache) {
			this.cache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(ZI)Lclient!hk;")
	fun list(@OriginalArg(1) id: Int): FluType? {
		@Pc(11) val referenceCache: LRUCache = this.cache
		@Pc(21) var floorUnderlayType: FluType?
		synchronized(this.cache) {
			floorUnderlayType = this.cache.get(id.toLong()) as FluType?
		}
		if (floorUnderlayType != null) {
			return floorUnderlayType
		}
		@Pc(34) val underlayResource: Js5ResourceProvider? = this.underlayResource
		@Pc(43) var data: ByteArray?
		synchronized(this.underlayResource!!) {
			data = this.underlayResource.getFile(1, id)
		}
		floorUnderlayType = FluType()
		if (data != null) {
			floorUnderlayType!!.decode(Packet(data))
		}
		@Pc(65) val referenceCache2: LRUCache = this.cache
		synchronized(this.cache) {
			this.cache.put(floorUnderlayType, id.toLong())
			return floorUnderlayType
		}
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(Z)V")
	fun cacheReset() {
		@Pc(2) val referenceCache: LRUCache = this.cache
		synchronized(this.cache) {
			this.cache.reset()
		}
	}

	@OriginalMember(owner = "client!qe", name = "b", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(6) val referenceCache: LRUCache = this.cache
		synchronized(this.cache) {
			this.cache.clean(5)
		}
	}
}
