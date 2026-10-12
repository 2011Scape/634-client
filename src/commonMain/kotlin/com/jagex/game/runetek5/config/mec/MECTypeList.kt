package com.jagex.game.runetek5.config.mec

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import withLock

@OriginalClass("client!on")
class MECTypeList {

	@OriginalMember(owner = "client!on", name = "e", descriptor = "Lclient!jr;")
	private var definitionCache: LRUCache = LRUCache(128)

	@OriginalMember(owner = "client!on", name = "k", descriptor = "Lclient!jr;")
	@JvmField
	var spriteCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!on", name = "f", descriptor = "Lclient!in;")
	private val definitionResource: Js5ResourceProvider

	@OriginalMember(owner = "client!on", name = "i", descriptor = "Lclient!in;")
	@JvmField
	val spriteResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!on", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) definitionResource: Js5ResourceProvider, @OriginalArg(3) spriteResource: Js5ResourceProvider?) {
		this.definitionResource = definitionResource
		this.spriteResource = spriteResource
		this.definitionResource.getGroupCapacity(36)
	}

	@OriginalMember(owner = "client!on", name = "a", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(6) var referenceCache: LRUCache = this.definitionCache
		withLock(this.definitionCache) {
			this.definitionCache.reset()
		}
		referenceCache = this.spriteCache
		withLock(this.spriteCache) {
			this.spriteCache.reset()
		}
	}

	@OriginalMember(owner = "client!on", name = "a", descriptor = "(BII)V")
	fun setCacheSize(@OriginalArg(1) spriteCacheSize: Int, @OriginalArg(2) definitionCacheSize: Int) {
		this.definitionCache = LRUCache(definitionCacheSize)
		this.spriteCache = LRUCache(spriteCacheSize)
	}

	@OriginalMember(owner = "client!on", name = "a", descriptor = "(Z)V")
	fun cacheRemoveSoftReferences() {
		@Pc(8) var referenceCache: LRUCache = this.definitionCache
		withLock(this.definitionCache) {
			this.definitionCache.removeSoftReferences()
		}
		referenceCache = this.spriteCache
		withLock(this.spriteCache) {
			this.spriteCache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!on", name = "a", descriptor = "(IB)V")
	fun cacheClean() {
		@Pc(2) var referenceCache: LRUCache = this.definitionCache
		withLock(this.definitionCache) {
			this.definitionCache.clean(5)
		}
		referenceCache = this.spriteCache
		withLock(this.spriteCache) {
			this.spriteCache.clean(5)
		}
	}

	@OriginalMember(owner = "client!on", name = "b", descriptor = "(IB)Lclient!ii;")
	fun list(@OriginalArg(0) mectypeId: Int): MECType {
		@Pc(6) val referenceCache: LRUCache = this.definitionCache
		@Pc(16) var mapElementType: MECType?
		withLock(this.definitionCache) {
			mapElementType = this.definitionCache.get(mectypeId.toLong()) as MECType?
		}
		if (mapElementType != null) {
			return mapElementType
		}
		@Pc(34) val definitionResource: Js5ResourceProvider = this.definitionResource
		@Pc(43) var data: ByteArray?
		withLock(this.definitionResource) {
			data = this.definitionResource.getFile(36, mectypeId)
		}
		mapElementType = MECType()
		mapElementType.initialType = this
		mapElementType.mectypeId = mectypeId
		if (data != null) {
			mapElementType.decode(Packet(data))
		}
		mapElementType.postDecode()
		@Pc(74) val referenceCache2: LRUCache = this.definitionCache
		withLock(this.definitionCache) {
			this.definitionCache.put(mapElementType, mectypeId.toLong())
			return mapElementType
		}
	}
}
