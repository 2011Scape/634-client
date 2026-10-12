package com.jagex.game.runetek5.config.flo

import com.jagex.game.runetek5.config.flo.FloType
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!cb")
class FloTypeList {

	@OriginalMember(owner = "client!cb", name = "e", descriptor = "Lclient!jr;")
	private val overlayCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!cb", name = "s", descriptor = "I")
	@JvmField
	var lastLoadedOverlayId: Int = 0

	@OriginalMember(owner = "client!cb", name = "n", descriptor = "Lclient!in;")
	private val floResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "I")
	@JvmField
	val totalFileCount: Int

	@OriginalMember(owner = "client!cb", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) floResource: Js5ResourceProvider?) {
		this.floResource = floResource
		this.totalFileCount = this.floResource!!.getGroupCapacity(4)
	}

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(2) val referenceCache: LRUCache = this.overlayCache
		synchronized(this.overlayCache) {
			this.overlayCache.clean(5)
		}
	}

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(B)V")
	fun cacheRemoveSoftReferences() {
		@Pc(6) val referenceCache: LRUCache = this.overlayCache
		synchronized(this.overlayCache) {
			this.overlayCache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(IZ)Lclient!vd;")
	fun list(@OriginalArg(0) id: Int): FloType? {
		@Pc(11) val referenceCache: LRUCache = this.overlayCache
		@Pc(21) var spotAnimType: FloType?
		synchronized(this.overlayCache) {
			spotAnimType = this.overlayCache.get(id.toLong()) as FloType?
		}
		if (spotAnimType != null) {
			return spotAnimType
		}
		@Pc(34) val floResource: Js5ResourceProvider? = this.floResource
		@Pc(43) var data: ByteArray?
		synchronized(this.floResource!!) {
			data = this.floResource.getFile(4, id)
		}
		spotAnimType = FloType()
		spotAnimType!!.overlayId = id
		spotAnimType!!.floTypeList = this
		if (data != null) {
			spotAnimType!!.decode(Packet(data))
		}
		spotAnimType!!.postDecode()
		@Pc(74) val referenceCache2: LRUCache = this.overlayCache
		synchronized(this.overlayCache) {
			this.overlayCache.put(spotAnimType, id.toLong())
			return spotAnimType
		}
	}

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(2) val referenceCache: LRUCache = this.overlayCache
		synchronized(this.overlayCache) {
			this.overlayCache.reset()
		}
	}
}
