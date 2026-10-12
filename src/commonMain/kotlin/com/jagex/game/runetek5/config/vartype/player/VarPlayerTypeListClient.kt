package com.jagex.game.runetek5.config.vartype.player

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

@OriginalClass("client!rv")
class VarPlayerTypeListClient {

	@OriginalMember(owner = "client!rv", name = "i", descriptor = "Lclient!jr;")
	private val cache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!rv", name = "c", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!rv", name = "f", descriptor = "I")
	@JvmField
	val num: Int

	@OriginalMember(owner = "client!rv", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider?) {
		this.configClient = configClient
		if (this.configClient == null) {
			this.num = 0
		} else {
			this.num = this.configClient.getGroupCapacity(16)
		}
	}

	@OriginalMember(owner = "client!rv", name = "a", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(8) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.clean(5)
		}
	}

	@OriginalMember(owner = "client!rv", name = "b", descriptor = "(II)Lclient!ow;")
	fun list(@OriginalArg(1) id: Int): VarPlayerType? {
		@Pc(6) val referenceCache: LRUCache = this.cache
		@Pc(16) var varPlayerType: VarPlayerType?
		withLock(this.cache) {
			varPlayerType = this.cache.get(id.toLong()) as VarPlayerType?
		}
		if (varPlayerType != null) {
			return varPlayerType
		}
		@Pc(29) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(38) var data: ByteArray?
		withLock(this.configClient!!) {
			data = this.configClient.getFile(16, id)
		}
		varPlayerType = VarPlayerType()
		if (data != null) {
			varPlayerType!!.decode(Packet(data))
		}
		@Pc(66) val referenceCache2: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.put(varPlayerType, id.toLong())
			return varPlayerType
		}
	}

	@OriginalMember(owner = "client!rv", name = "a", descriptor = "(B)V")
	fun cacheReset() {
		@Pc(6) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.reset()
		}
	}

	@OriginalMember(owner = "client!rv", name = "b", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(11) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.removeSoftReferences()
		}
	}
}
