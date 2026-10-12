package com.jagex.game.runetek5.config.npctype

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.npc.NPCType

@OriginalClass("client!vda")
class NPCTypeList {

	@OriginalMember(owner = "client!vda", name = "r", descriptor = "I")
	@JvmField
	var featureMask: Int = 0

	@OriginalMember(owner = "client!vda", name = "b", descriptor = "Lclient!jr;")
	private val typeCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!vda", name = "o", descriptor = "Lclient!jr;")
	@JvmField
	val modelCache: LRUCache = LRUCache(50)

	@OriginalMember(owner = "client!vda", name = "q", descriptor = "Lclient!jr;")
	@JvmField
	val headModels: LRUCache = LRUCache(5)

	@OriginalMember(owner = "client!vda", name = "h", descriptor = "Z")
	@JvmField
	var allowMembers: Boolean = false

	@OriginalMember(owner = "client!vda", name = "j", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "Lclient!in;")
	@JvmField
	val meshes: Js5ResourceProvider?

	@OriginalMember(owner = "client!vda", name = "c", descriptor = "Lclient!sj;")
	@JvmField
	val game: Game?

	@OriginalMember(owner = "client!vda", name = "<init>", descriptor = "(Lclient!sj;IZLclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) allowMembers: Boolean, @OriginalArg(3) configClient: Js5ResourceProvider?, @OriginalArg(4) meshes: Js5ResourceProvider?) {
		this.allowMembers = allowMembers
		this.configClient = configClient
		this.meshes = meshes
		this.game = modeGame
		if (this.configClient != null) {
			@Pc(41) val lastGroupId: Int = this.configClient.getCapacity() - 1
			this.configClient.getGroupCapacity(lastGroupId)
		}
	}

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "(BZ)V")
	fun setAllowMembers(@OriginalArg(1) allowMembers: Boolean) {
		if (this.allowMembers != allowMembers) {
			this.allowMembers = allowMembers
			this.cacheReset()
		}
	}

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "(II)V")
	fun setFeatureMask(@OriginalArg(1) featureMask: Int) {
		this.featureMask = featureMask
		@Pc(9) var referenceCache: LRUCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
		referenceCache = this.headModels
		synchronized(this.headModels) {
			this.headModels.reset()
		}
	}

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "(I)V")
	fun clearModels() {
		@Pc(2) var referenceCache: LRUCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
		referenceCache = this.headModels
		synchronized(this.headModels) {
			this.headModels.reset()
		}
	}

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "(IZ)V")
	fun cacheClean() {
		@Pc(6) var referenceCache: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.clean(5)
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.clean(5)
		}
		referenceCache = this.headModels
		synchronized(this.headModels) {
			this.headModels.clean(5)
		}
	}

	@OriginalMember(owner = "client!vda", name = "b", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(6) var referenceCache: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.reset()
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
		referenceCache = this.headModels
		synchronized(this.headModels) {
			this.headModels.reset()
		}
	}

	@OriginalMember(owner = "client!vda", name = "b", descriptor = "(II)Lclient!bb;")
	fun list(@OriginalArg(0) id: Int): NPCType {
		@Pc(6) val referenceCache: LRUCache = this.typeCache
		@Pc(16) var npcType: NPCType?
		synchronized(this.typeCache) {
			npcType = this.typeCache.get(id.toLong()) as NPCType?
		}
		if (npcType != null) {
			return npcType!!
		}
		@Pc(29) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(44) var data: ByteArray?
		synchronized(this.configClient!!) {
			data = this.configClient.getFile(NPCTypeList.getGroupId(id), NPCTypeList.getFileId(id))
		}
		val newType: NPCType = NPCType()
		npcType = newType
		newType.id = id
		newType.npcTypeList = this
		if (data != null) {
			newType.decode(Packet(data))
		}
		newType.postDecode()
		@Pc(75) val referenceCache2: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.put(newType, id.toLong())
			return newType
		}
	}

	@OriginalMember(owner = "client!vda", name = "c", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(6) var referenceCache: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.removeSoftReferences()
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.removeSoftReferences()
		}
		referenceCache = this.headModels
		synchronized(this.headModels) {
			this.headModels.removeSoftReferences()
		}
	}

	companion object {
		@OriginalMember(owner = "client!mv", name = "a", descriptor = "(IB)I")
		@JvmStatic
		fun getGroupId(@OriginalArg(0) id: Int): Int {
			return id ushr 7
		}

		@OriginalMember(owner = "client!u", name = "a", descriptor = "(II)I")
		@JvmStatic
		fun getFileId(@OriginalArg(1) id: Int): Int {
			return id and 0x7F
		}
	}
}
