package com.jagex.game.runetek5.config.cursor

import com.jagex.game.runetek5.config.cursor.CursorType
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

@OriginalClass("client!pg")
class CursorTypeList {

	@OriginalMember(owner = "client!pg", name = "h", descriptor = "Lclient!jr;")
	private val cursorTypeCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!pg", name = "m", descriptor = "Lclient!jr;")
	@JvmField
	val spriteCache: LRUCache = LRUCache(2)

	@OriginalMember(owner = "client!pg", name = "k", descriptor = "Lclient!in;")
	private val cursorResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!pg", name = "c", descriptor = "Lclient!in;")
	@JvmField
	val spriteResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!pg", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) cursorResource: Js5ResourceProvider?, @OriginalArg(3) spriteResource: Js5ResourceProvider?) {
		this.cursorResource = cursorResource
		this.spriteResource = spriteResource
		this.cursorResource!!.getGroupCapacity(33)
	}

	@OriginalMember(owner = "client!pg", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(2) var referenceCache: LRUCache = this.cursorTypeCache
		withLock(this.cursorTypeCache) {
			this.cursorTypeCache.removeSoftReferences()
		}
		referenceCache = this.spriteCache
		withLock(this.spriteCache) {
			this.spriteCache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!pg", name = "a", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(6) var referenceCache: LRUCache = this.cursorTypeCache
		withLock(this.cursorTypeCache) {
			this.cursorTypeCache.clean(5)
		}
		referenceCache = this.spriteCache
		withLock(this.spriteCache) {
			this.spriteCache.clean(5)
		}
	}

	@OriginalMember(owner = "client!pg", name = "a", descriptor = "(BI)Lclient!sda;")
	fun list(@OriginalArg(1) id: Int): CursorType? {
		@Pc(6) val referenceCache: LRUCache = this.cursorTypeCache
		@Pc(18) var cursorType: CursorType?
		withLock(this.cursorTypeCache) {
			cursorType = this.cursorTypeCache.get(id.toLong()) as CursorType?
		}
		if (cursorType != null) {
			return cursorType
		}
		@Pc(37) val cursorResource: Js5ResourceProvider? = this.cursorResource
		@Pc(46) var data: ByteArray?
		withLock(this.cursorResource!!) {
			data = this.cursorResource.getFile(33, id)
		}
		cursorType = CursorType()
		cursorType!!.cursorTypeList = this
		if (data != null) {
			cursorType!!.decode(Packet(data))
		}
		@Pc(71) val referenceCache2: LRUCache = this.cursorTypeCache
		withLock(this.cursorTypeCache) {
			this.cursorTypeCache.put(cursorType, id.toLong())
			return cursorType
		}
	}

	@OriginalMember(owner = "client!pg", name = "c", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(6) var referenceCache: LRUCache = this.cursorTypeCache
		withLock(this.cursorTypeCache) {
			this.cursorTypeCache.reset()
		}
		referenceCache = this.spriteCache
		withLock(this.spriteCache) {
			this.spriteCache.reset()
		}
	}
}
