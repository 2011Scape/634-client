package com.jagex.game.runetek5.config.mapsprite

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!oda")
class MapSpriteTypeList {

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "Lclient!jr;")
	private var mapspriteTypeCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!oda", name = "n", descriptor = "Lclient!jr;")
	@JvmField
	var mapspriteCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!oda", name = "c", descriptor = "Lclient!in;")
	@JvmField
	val mapspriteResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!oda", name = "i", descriptor = "Lclient!in;")
	private val mapspriteTypeResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!oda", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) mapspriteTypeResource: Js5ResourceProvider?, @OriginalArg(3) mapspriteResource: Js5ResourceProvider?) {
		this.mapspriteResource = mapspriteResource
		this.mapspriteTypeResource = mapspriteTypeResource
		this.mapspriteTypeResource!!.getGroupCapacity(34)
	}

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "(BI)Lclient!rw;")
	fun list(@OriginalArg(1) id: Int): MapSpriteType? {
		@Pc(6) val referenceCache: LRUCache = this.mapspriteTypeCache
		@Pc(16) var msiType: MapSpriteType?
		synchronized(this.mapspriteTypeCache) {
			msiType = this.mapspriteTypeCache.get(id.toLong()) as MapSpriteType?
		}
		if (msiType != null) {
			return msiType
		}
		@Pc(29) val mapspriteTypeResource: Js5ResourceProvider? = this.mapspriteTypeResource
		@Pc(40) var data: ByteArray?
		synchronized(this.mapspriteTypeResource!!) {
			data = this.mapspriteTypeResource.getFile(34, id)
		}
		msiType = MapSpriteType()
		msiType!!.mapspriteTypeList = this
		if (data != null) {
			msiType!!.decode(Packet(data))
		}
		@Pc(70) val referenceCache2: LRUCache = this.mapspriteTypeCache
		synchronized(this.mapspriteTypeCache) {
			this.mapspriteTypeCache.put(msiType, id.toLong())
			return msiType
		}
	}

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "(B)V")
	fun cacheRemoveSoftReferences() {
		@Pc(2) var referenceCache: LRUCache = this.mapspriteTypeCache
		synchronized(this.mapspriteTypeCache) {
			this.mapspriteTypeCache.removeSoftReferences()
		}
		referenceCache = this.mapspriteCache
		synchronized(this.mapspriteCache) {
			this.mapspriteCache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!oda", name = "b", descriptor = "(B)V")
	fun cacheReset() {
		@Pc(2) var referenceCache: LRUCache = this.mapspriteTypeCache
		synchronized(this.mapspriteTypeCache) {
			this.mapspriteTypeCache.reset()
		}
		referenceCache = this.mapspriteCache
		synchronized(this.mapspriteCache) {
			this.mapspriteCache.reset()
		}
	}

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "(IB)V")
	fun cacheClean() {
		@Pc(7) var referenceCache: LRUCache = this.mapspriteTypeCache
		synchronized(this.mapspriteTypeCache) {
			this.mapspriteTypeCache.clean(5)
		}
		referenceCache = this.mapspriteCache
		synchronized(this.mapspriteCache) {
			this.mapspriteCache.clean(5)
		}
	}

	@OriginalMember(owner = "client!oda", name = "a", descriptor = "(III)V")
	fun setCacheSize(@OriginalArg(0) spriteCacheSize: Int, @OriginalArg(2) typeCacheSize: Int) {
		this.mapspriteTypeCache = LRUCache(typeCacheSize)
		this.mapspriteCache = LRUCache(spriteCacheSize)
	}
}
