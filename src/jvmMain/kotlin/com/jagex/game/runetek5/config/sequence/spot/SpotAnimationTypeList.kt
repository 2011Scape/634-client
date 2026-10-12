package com.jagex.game.runetek5.config.sequence.spot

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!cn")
class SpotAnimationTypeList {

	@OriginalMember(owner = "client!cn", name = "n", descriptor = "I")
	@JvmField
	var featureMask: Int = 0

	@OriginalMember(owner = "client!cn", name = "l", descriptor = "Lclient!jr;")
	private val definitionCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!cn", name = "m", descriptor = "Lclient!jr;")
	@JvmField
	val modelCache: LRUCache = LRUCache(60)

	@OriginalMember(owner = "client!cn", name = "d", descriptor = "Lclient!in;")
	@JvmField
	val modelResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!cn", name = "b", descriptor = "Lclient!in;")
	private val gfxResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!cn", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) gfxResource: Js5ResourceProvider?, @OriginalArg(3) modelResource: Js5ResourceProvider?) {
		this.modelResource = modelResource
		this.gfxResource = gfxResource
		@Pc(26) val lastGroupId: Int = this.gfxResource!!.getCapacity() - 1
		this.gfxResource.getGroupCapacity(lastGroupId)
	}

	@OriginalMember(owner = "client!cn", name = "a", descriptor = "(II)V")
	fun setFeatureMask(@OriginalArg(1) featureMask: Int) {
		this.featureMask = featureMask
		@Pc(9) val referenceCache: LRUCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
	}

	@OriginalMember(owner = "client!cn", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(2) var referenceCache: LRUCache = this.definitionCache
		synchronized(this.definitionCache) {
			this.definitionCache.removeSoftReferences()
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!cn", name = "a", descriptor = "(BI)Lclient!fk;")
	fun list(@OriginalArg(1) currentId: Int): SpotAnimationType {
		@Pc(12) val referenceCache: LRUCache = this.definitionCache
		@Pc(22) var spotAnimationType: SpotAnimationType?
		synchronized(this.definitionCache) {
			spotAnimationType = this.definitionCache.get(currentId.toLong()) as SpotAnimationType?
		}
		if (spotAnimationType != null) {
			return spotAnimationType!!
		}
		@Pc(35) val gfxResource: Js5ResourceProvider? = this.gfxResource
		@Pc(48) var data: ByteArray?
		synchronized(this.gfxResource!!) {
			data = this.gfxResource.getFile(SpotAnimationTypeList.getGroupId(currentId), SpotAnimationTypeList.getFileId(currentId))
		}
		val newType: SpotAnimationType = SpotAnimationType()
		spotAnimationType = newType
		newType.gfxTypeList = this
		newType.currentId = currentId
		if (data != null) {
			newType.decode(Packet(data))
		}
		@Pc(76) val referenceCache2: LRUCache = this.definitionCache
		synchronized(this.definitionCache) {
			this.definitionCache.put(newType, currentId.toLong())
			return newType
		}
	}

	@OriginalMember(owner = "client!cn", name = "a", descriptor = "(IB)V")
	fun cacheClean() {
		@Pc(2) var referenceCache: LRUCache = this.definitionCache
		synchronized(this.definitionCache) {
			this.definitionCache.clean(5)
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.clean(5)
		}
	}

	@OriginalMember(owner = "client!cn", name = "b", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(6) var referenceCache: LRUCache = this.definitionCache
		synchronized(this.definitionCache) {
			this.definitionCache.reset()
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
	}

	companion object {
		@OriginalMember(owner = "client!dga", name = "c", descriptor = "(II)I")
		@JvmStatic
		fun getFileId(@OriginalArg(0) id: Int): Int {
			return id and 0xFF
		}

		@OriginalMember(owner = "client!dm", name = "a", descriptor = "(BI)I")
		@JvmStatic
		fun getGroupId(@OriginalArg(1) value: Int): Int {
			return value ushr 8
		}
	}
}
