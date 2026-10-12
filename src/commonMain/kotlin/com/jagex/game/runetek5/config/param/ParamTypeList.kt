package com.jagex.game.runetek5.config.param

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import withLock

@OriginalClass("client!dc")
class ParamTypeList {

	@OriginalMember(owner = "client!dc", name = "e", descriptor = "Lclient!jr;")
	private val cache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!dc", name = "c", descriptor = "Lclient!in;")
	private val paramResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!dc", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) paramResource: Js5ResourceProvider?) {
		this.paramResource = paramResource
		if (this.paramResource != null) {
			this.paramResource.getGroupCapacity(11)
		}
	}

	@OriginalMember(owner = "client!dc", name = "a", descriptor = "(II)Lclient!uaa;")
	fun list(@OriginalArg(0) id: Int): ParamType? {
		@Pc(6) val referenceCache: LRUCache = this.cache
		@Pc(16) var paramType: ParamType?
		withLock(this.cache) {
			paramType = this.cache.get(id.toLong()) as ParamType?
		}
		if (paramType != null) {
			return paramType
		}
		@Pc(34) val paramResource: Js5ResourceProvider? = this.paramResource
		@Pc(43) var data: ByteArray?
		withLock(this.paramResource!!) {
			data = this.paramResource.getFile(11, id)
		}
		paramType = ParamType()
		if (data != null) {
			paramType!!.decode(Packet(data))
		}
		@Pc(65) val referenceCache2: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.put(paramType, id.toLong())
			return paramType
		}
	}

	@OriginalMember(owner = "client!dc", name = "a", descriptor = "(B)V")
	fun cacheReset() {
		@Pc(2) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.reset()
		}
	}

	@OriginalMember(owner = "client!dc", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(6) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!dc", name = "b", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(2) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.clean(5)
		}
	}
}
