package com.jagex.game.runetek5.config.hitsplat

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

@OriginalClass("client!sb")
class HitSplatTypeList {

	@OriginalMember(owner = "client!sb", name = "a", descriptor = "Lclient!jr;")
	@JvmField
	val spriteCache: LRUCache = LRUCache(20)

	@OriginalMember(owner = "client!sb", name = "n", descriptor = "Lclient!jr;")
	private val hitsplatTypeCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!sb", name = "l", descriptor = "Lclient!in;")
	@JvmField
	val spriteResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!sb", name = "e", descriptor = "Lclient!in;")
	private val hitsplatResource: Js5ResourceProvider

	@OriginalMember(owner = "client!sb", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) hitsplatResource: Js5ResourceProvider, @OriginalArg(3) spriteResource: Js5ResourceProvider?) {
		this.spriteResource = spriteResource
		this.hitsplatResource = hitsplatResource
		this.hitsplatResource.getGroupCapacity(46)
	}

	@OriginalMember(owner = "client!sb", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(6) var referenceCache: LRUCache = this.hitsplatTypeCache
		withLock(this.hitsplatTypeCache) {
			this.hitsplatTypeCache.removeSoftReferences()
		}
		referenceCache = this.spriteCache
		withLock(this.spriteCache) {
			this.spriteCache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!sb", name = "a", descriptor = "(II)Lclient!ia;")
	fun list(@OriginalArg(1) id: Int): HitSplatType {
		@Pc(6) val referenceCache: LRUCache = this.hitsplatTypeCache
		@Pc(18) var hitmarkType: HitSplatType?
		withLock(this.hitsplatTypeCache) {
			hitmarkType = this.hitsplatTypeCache.get(id.toLong()) as HitSplatType?
		}
		if (hitmarkType != null) {
			return hitmarkType
		}
		@Pc(31) val hitsplatResource: Js5ResourceProvider = this.hitsplatResource
		@Pc(40) var data: ByteArray?
		withLock(this.hitsplatResource) {
			data = this.hitsplatResource.getFile(46, id)
		}
		hitmarkType = HitSplatType()
		hitmarkType.typelist = this
		if (data != null) {
			hitmarkType.decode(Packet(data))
		}
		@Pc(67) val referenceCache2: LRUCache = this.hitsplatTypeCache
		withLock(this.hitsplatTypeCache) {
			this.hitsplatTypeCache.put(hitmarkType, id.toLong())
			return hitmarkType
		}
	}

	@OriginalMember(owner = "client!sb", name = "b", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(2) var referenceCache: LRUCache = this.hitsplatTypeCache
		withLock(this.hitsplatTypeCache) {
			this.hitsplatTypeCache.reset()
		}
		referenceCache = this.spriteCache
		withLock(this.spriteCache) {
			this.spriteCache.reset()
		}
	}

	@OriginalMember(owner = "client!sb", name = "b", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(6) var referenceCache: LRUCache = this.hitsplatTypeCache
		withLock(this.hitsplatTypeCache) {
			this.hitsplatTypeCache.clean(5)
		}
		referenceCache = this.spriteCache
		withLock(this.spriteCache) {
			this.spriteCache.clean(5)
		}
	}
}
