package com.jagex.game.runetek5.config.loctype

import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!uha")
class LocTypeList {

	@OriginalMember(owner = "client!uha", name = "u", descriptor = "I")
	@JvmField
	var featureMask: Int = 0

	@OriginalMember(owner = "client!uha", name = "m", descriptor = "Z")
	@JvmField
	var lowDetail: Boolean = false

	@OriginalMember(owner = "client!uha", name = "h", descriptor = "Lclient!jr;")
	private var typeCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!uha", name = "r", descriptor = "Lclient!jr;")
	@JvmField
	val modelsByShapeCache: LRUCache = LRUCache(500)

	@OriginalMember(owner = "client!uha", name = "s", descriptor = "Lclient!jr;")
	@JvmField
	val modelAndShadowCache: LRUCache = LRUCache(30)

	@OriginalMember(owner = "client!uha", name = "t", descriptor = "Lclient!jr;")
	@JvmField
	val modelCache: LRUCache = LRUCache(50)

	@OriginalMember(owner = "client!uha", name = "q", descriptor = "Z")
	@JvmField
	var allowMembers: Boolean = false

	@OriginalMember(owner = "client!uha", name = "c", descriptor = "Lclient!in;")
	@JvmField
	val meshResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!uha", name = "<init>", descriptor = "(Lclient!sj;IZLclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) allowMembers: Boolean, @OriginalArg(3) configClient: Js5ResourceProvider?, @OriginalArg(4) meshResource: Js5ResourceProvider?) {
		this.allowMembers = allowMembers
		this.meshResource = meshResource
		this.configClient = configClient
		if (this.configClient != null) {
			@Pc(47) val lastGroupId: Int = this.configClient.getCapacity() - 1
			this.configClient.getGroupCapacity(lastGroupId)
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(ZB)V")
	fun setAllowMembers(@OriginalArg(0) allowMembers: Boolean) {
		if (allowMembers != this.allowMembers) {
			this.allowMembers = allowMembers
			this.cacheReset()
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(II)Lclient!iv;")
	fun list(@OriginalArg(1) id: Int): LocType {
		@Pc(6) val referenceCache: LRUCache = this.typeCache
		@Pc(18) var locType: LocType?
		synchronized(this.typeCache) {
			locType = this.typeCache.get(id.toLong()) as LocType?
		}
		if (locType != null) {
			return locType!!
		}
		@Pc(31) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(46) var data: ByteArray?
		synchronized(this.configClient!!) {
			data = this.configClient.getFile(LocTypeList.getGroupId(id), LocTypeList.getFileId(id))
		}
		val newType: LocType = LocType()
		locType = newType
		newType.id = id
		newType.locTypeList = this
		if (data != null) {
			newType.decode(Packet(data))
		}
		newType.postDecode()
		if (!this.allowMembers && newType.members) {
			newType.quests = null
			newType.actions = null
		}
		if (newType.ignoreClipOnAltRoute) {
			newType.blocksMovement = 0
			newType.blocksProjectiles = false
		}
		@Pc(103) val referenceCache2: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.put(newType, id.toLong())
			return newType
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(10) var referenceCache: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.removeSoftReferences()
		}
		referenceCache = this.modelsByShapeCache
		synchronized(this.modelsByShapeCache) {
			this.modelsByShapeCache.removeSoftReferences()
		}
		referenceCache = this.modelAndShadowCache
		synchronized(this.modelAndShadowCache) {
			this.modelAndShadowCache.removeSoftReferences()
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(IZ)V")
	fun setLowDetail(@OriginalArg(1) lowDetail: Boolean) {
		if (this.lowDetail != lowDetail) {
			this.lowDetail = lowDetail
			this.cacheReset()
		}
	}

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(IB)V")
	fun setFeatureMask(@OriginalArg(0) featureMask: Int) {
		this.featureMask = featureMask
		@Pc(9) var referenceCache: LRUCache = this.modelsByShapeCache
		synchronized(this.modelsByShapeCache) {
			this.modelsByShapeCache.reset()
		}
		referenceCache = this.modelAndShadowCache
		synchronized(this.modelAndShadowCache) {
			this.modelAndShadowCache.reset()
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
	}

	@OriginalMember(owner = "client!uha", name = "b", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(6) var referenceCache: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.clean(5)
		}
		referenceCache = this.modelsByShapeCache
		synchronized(this.modelsByShapeCache) {
			this.modelsByShapeCache.clean(5)
		}
		referenceCache = this.modelAndShadowCache
		synchronized(this.modelAndShadowCache) {
			this.modelAndShadowCache.clean(5)
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.clean(5)
		}
	}

	@OriginalMember(owner = "client!uha", name = "b", descriptor = "(I)V")
	fun cacheReset() {
		@Pc(2) var referenceCache: LRUCache = this.typeCache
		synchronized(this.typeCache) {
			this.typeCache.reset()
		}
		referenceCache = this.modelsByShapeCache
		synchronized(this.modelsByShapeCache) {
			this.modelsByShapeCache.reset()
		}
		referenceCache = this.modelAndShadowCache
		synchronized(this.modelAndShadowCache) {
			this.modelAndShadowCache.reset()
		}
		referenceCache = this.modelCache
		synchronized(this.modelCache) {
			this.modelCache.reset()
		}
	}

	@OriginalMember(owner = "client!uha", name = "b", descriptor = "(IZ)V")
	fun setCacheSize(@OriginalArg(0) capacity: Int) {
		this.typeCache = LRUCache(capacity)
	}

	companion object {
		@OriginalMember(owner = "client!uha", name = "j", descriptor = "[C")
		private val BASE64_CHARS: CharArray = CharArray(64)

		init {
			@Pc(7) var i: Int = 0
			while (i < 26) {
				BASE64_CHARS[i] = (i + 65).toChar()
				i++
			}
			@Pc(23) var i2: Int = 26
			while (i2 < 52) {
				BASE64_CHARS[i2] = (i2 + 97 - 26).toChar()
				i2++
			}
			@Pc(39) var i3: Int = 52
			while (i3 < 62) {
				BASE64_CHARS[i3] = (i3 + 48 - 52).toChar()
				i3++
			}
			BASE64_CHARS[62] = '+'
			BASE64_CHARS[63] = '/'
		}

		@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IB)I")
		@JvmStatic
		fun getFileId(@OriginalArg(0) id: Int): Int {
			return id and 0xFF
		}

		@OriginalMember(owner = "client!fv", name = "d", descriptor = "(II)I")
		@JvmStatic
		fun getGroupId(@OriginalArg(0) value: Int): Int {
			return value ushr 8
		}
	}
}
