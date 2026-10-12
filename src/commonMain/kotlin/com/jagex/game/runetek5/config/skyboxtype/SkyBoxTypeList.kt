package com.jagex.game.runetek5.config.skyboxtype

import com.jagex.game.runetek5.Skybox
import com.jagex.game.runetek5.config.skybox.SkyboxType
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.sun.SunTypeList
import com.jagex.game.runetek5.config.sun.SunType
import com.jagex.game.runetek5.config.sun.Sun
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import withLock

@OriginalClass("client!lca")
class SkyBoxTypeList {

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "Lclient!jr;")
	private val cache: LRUCache = LRUCache(16)

	@OriginalMember(owner = "client!lca", name = "l", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!lca", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider?) {
		this.configClient = configClient
		this.configClient!!.getGroupCapacity(29)
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(Z)V")
	fun cacheRemoveSoftReferences() {
		@Pc(6) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(BI)V")
	fun cacheClean() {
		@Pc(2) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.clean(5)
		}
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(II)Lclient!wia;")
	private fun list(@OriginalArg(0) id: Int): SkyboxType? {
		@Pc(6) val referenceCache: LRUCache = this.cache
		@Pc(16) var skyboxType: SkyboxType?
		withLock(this.cache) {
			skyboxType = this.cache.get(id.toLong()) as SkyboxType?
		}
		if (skyboxType != null) {
			return skyboxType
		}
		@Pc(29) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(38) var data: ByteArray?
		withLock(this.configClient!!) {
			data = this.configClient.getFile(29, id)
		}
		skyboxType = SkyboxType()
		if (data != null) {
			skyboxType!!.decode(Packet(data))
		}
		@Pc(60) val referenceCache2: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.put(skyboxType, id.toLong())
			return skyboxType
		}
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(B)V")
	fun cacheReset() {
		@Pc(2) val referenceCache: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.reset()
		}
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(IIIILclient!hq;I)Lclient!wr;")
	fun generateSkybox(@OriginalArg(0) positionY: Int, @OriginalArg(1) positionZ: Int, @OriginalArg(2) positionX: Int, @OriginalArg(4) skyBoxSphereTypeList: SunTypeList?, @OriginalArg(5) skyboxId: Int): Skybox? {
		@Pc(7) var skyBoxSpheres: Array<Sun?>? = null
		@Pc(12) val skyboxType: SkyboxType? = this.list(skyboxId)
		if (skyboxType!!.sunDefinitionIds != null) {
			skyBoxSpheres = arrayOfNulls<Sun>(skyboxType.sunDefinitionIds!!.size)
			@Pc(27) var i: Int = 0
			while (i < skyBoxSpheres.size) {
				@Pc(37) val varcType: SunType? = skyBoxSphereTypeList!!.list(skyboxType.sunDefinitionIds!![i])
				skyBoxSpheres[i] = Sun(varcType!!.actionType, varcType.materialId, varcType.positionX, varcType.positionY, varcType.positionZ, varcType.positionOffsetStep, varcType.displayColor, varcType.invertPosition)
				i++
			}
		}
		return Skybox(skyboxType.materialId, skyBoxSpheres, skyboxType.defaultSunIndex, positionX, positionY, positionZ)
	}
}
