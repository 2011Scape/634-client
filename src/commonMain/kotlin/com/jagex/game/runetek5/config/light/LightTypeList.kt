package com.jagex.game.runetek5.config.light

import com.jagex.game.runetek5.config.light.LightType
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import withLock

@OriginalClass("client!qf")
class LightTypeList {

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "Lclient!jr;")
	private val recentUse: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!qf", name = "b", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!qf", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider?) {
		this.configClient = configClient
		this.configClient!!.getGroupCapacity(31)
	}

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(2) val referenceCache: LRUCache = this.recentUse
		withLock(this.recentUse) {
			this.recentUse.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(BI)Lclient!u;")
	fun list(@OriginalArg(1) id: Int): LightType? {
		@Pc(6) val referenceCache: LRUCache = this.recentUse
		@Pc(16) var lightType: LightType?
		withLock(this.recentUse) {
			lightType = this.recentUse.get(id.toLong()) as LightType?
		}
		if (lightType != null) {
			return lightType
		}
		@Pc(29) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(38) var data: ByteArray?
		withLock(this.configClient!!) {
			data = this.configClient.getFile(31, id)
		}
		lightType = LightType()
		if (data != null) {
			lightType!!.decode(Packet(data))
		}
		@Pc(68) val referenceCache2: LRUCache = this.recentUse
		withLock(this.recentUse) {
			this.recentUse.put(lightType, id.toLong())
			return lightType
		}
	}

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(2) val referenceCache: LRUCache = this.recentUse
		withLock(this.recentUse) {
			this.recentUse.clean(5)
		}
	}

	@OriginalMember(owner = "client!qf", name = "a", descriptor = "(B)V")
	fun cacheReset() {
		@Pc(2) val referenceCache: LRUCache = this.recentUse
		withLock(this.recentUse) {
			this.recentUse.reset()
		}
	}
}
