package com.jagex.game.runetek5.config.struct

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.StructType

@OriginalClass("client!kb")
class StructTypeList {

	@OriginalMember(owner = "client!kb", name = "h", descriptor = "Lclient!jr;")
	private val cache: LRUCache = LRUCache(256)

	@OriginalMember(owner = "client!kb", name = "d", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!kb", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider?) {
		this.configClient = configClient
		this.configClient!!.getGroupCapacity(26)
	}

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(14) val referenceCache: LRUCache = this.cache
		synchronized(this.cache) {
			this.cache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(Z)V")
	fun cacheReset() {
		@Pc(7) val referenceCache: LRUCache = this.cache
		synchronized(this.cache) {
			this.cache.reset()
		}
	}

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(10) val referenceCache: LRUCache = this.cache
		synchronized(this.cache) {
			this.cache.clean(5)
		}
	}

	@OriginalMember(owner = "client!kb", name = "b", descriptor = "(II)Lclient!bda;")
	fun list(@OriginalArg(0) id: Int): StructType? {
		@Pc(14) val referenceCache: LRUCache = this.cache
		@Pc(24) var structType: StructType?
		synchronized(this.cache) {
			structType = this.cache.get(id.toLong()) as StructType?
		}
		if (structType != null) {
			return structType
		}
		@Pc(37) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(46) var data: ByteArray?
		synchronized(this.configClient!!) {
			data = this.configClient.getFile(26, id)
		}
		structType = StructType()
		if (data != null) {
			structType!!.decode(Packet(data))
		}
		@Pc(68) val referenceCache2: LRUCache = this.cache
		synchronized(this.cache) {
			this.cache.put(structType, id.toLong())
			return structType
		}
	}
}
