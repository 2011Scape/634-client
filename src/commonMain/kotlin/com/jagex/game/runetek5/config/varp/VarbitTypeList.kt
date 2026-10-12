package com.jagex.game.runetek5.config.varp

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic
import withLock

@OriginalClass("client!qs")
class VarbitTypeList {

	@OriginalMember(owner = "client!qs", name = "d", descriptor = "Lclient!jr;")
	private var cache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!qs", name = "g", descriptor = "Lclient!in;")
	private val varbitResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!qs", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) varbitResource: Js5ResourceProvider?) {
		this.varbitResource = varbitResource
		if (this.varbitResource != null) {
			@Pc(20) val lastGroupId: Int = this.varbitResource.getCapacity() - 1
			this.varbitResource.getGroupCapacity(lastGroupId)
		}
	}

	@OriginalMember(owner = "client!qs", name = "a", descriptor = "(IB)V")
	fun cacheClean() {
		@Pc(6) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.clean(5)
		}
	}

	@OriginalMember(owner = "client!qs", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(14) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!qs", name = "a", descriptor = "(II)V")
	fun setCacheSize(@OriginalArg(0) capacity: Int) {
		@Pc(14) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.reset()
			this.cache = LRUCache(capacity)
		}
	}

	@OriginalMember(owner = "client!qs", name = "b", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(10) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.reset()
		}
	}

	@OriginalMember(owner = "client!qs", name = "b", descriptor = "(II)Lclient!bia;")
	fun list(@OriginalArg(0) id: Int): VarbitType? {
		@Pc(6) val referenceCache: LRUCache = this.cache
		@Pc(16) var varbitType: VarbitType?
		withLock(this.cache) {
			varbitType = this.cache.get(id.toLong()) as VarbitType?
		}
		if (varbitType != null) {
			return varbitType
		}
		@Pc(29) val varbitResource: Js5ResourceProvider? = this.varbitResource
		@Pc(44) var data: ByteArray?
		withLock(this.varbitResource!!) {
			data = this.varbitResource.getFile(VarbitTypeList.getGroupId(id), VarbitTypeList.getFileId(id))
		}
		varbitType = VarbitType()
		if (data != null) {
			varbitType!!.decode(Packet(data))
		}
		@Pc(73) val referenceCache2: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.put(varbitType, id.toLong())
			return varbitType
		}
	}

	companion object {
		@OriginalMember(owner = "client!bo", name = "a", descriptor = "(ZI)I")
		@JvmStatic
		fun getGroupId(@OriginalArg(1) id: Int): Int {
			return id ushr 10
		}

		@OriginalMember(owner = "client!ij", name = "a", descriptor = "(ZI)I")
		@JvmStatic
		fun getFileId(@OriginalArg(1) id: Int): Int {
			return id and 0x3FF
		}
	}
}
