package com.jagex.game.runetek5.config.idktype

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.Js5ConfigGroup
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.identitykit.IdkType
import kotlin.jvm.JvmField
import withLock

@OriginalClass("client!oha")
class IDKTypeList {

	@OriginalMember(owner = "client!oha", name = "d", descriptor = "Lclient!jr;")
	private val recentUse: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!oha", name = "h", descriptor = "Lclient!in;")
	@JvmField
	val meshResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!oha", name = "b", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!oha", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider?, @OriginalArg(3) meshResource: Js5ResourceProvider?) {
		this.meshResource = meshResource
		this.configClient = configClient
		this.configClient!!.getGroupCapacity(Js5ConfigGroup.IDKTYPE)
	}

	@OriginalMember(owner = "client!oha", name = "a", descriptor = "(BI)Lclient!ic;")
	fun list(@OriginalArg(1) id: Int): IdkType {
		@Pc(12) val referenceCache: LRUCache = this.recentUse
		@Pc(22) var idkType: IdkType?
		withLock(this.recentUse) {
			idkType = this.recentUse.get(id.toLong()) as IdkType?
		}
		if (idkType != null) {
			return idkType!!
		}
		@Pc(35) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(46) var data: ByteArray?
		withLock(this.configClient!!) {
			data = this.configClient.getFile(Js5ConfigGroup.IDKTYPE, id)
		}
		val newType: IdkType = IdkType()
		idkType = newType
		newType.identityTypeList = this
		if (data != null) {
			newType.decode(Packet(data))
		}
		@Pc(73) val referenceCache2: LRUCache = this.recentUse
		withLock(this.recentUse) {
			this.recentUse.put(newType, id.toLong())
			return newType
		}
	}

	@OriginalMember(owner = "client!oha", name = "b", descriptor = "(I)V")
	fun cacheRemoveSoftReferences() {
		@Pc(2) val lock: LRUCache = this.recentUse
		withLock(this.recentUse) {
			this.recentUse.removeSoftReferences()
		}
	}

	@OriginalMember(owner = "client!oha", name = "a", descriptor = "(B)V")
	fun cacheReset() {
		@Pc(12) val lock: LRUCache = this.recentUse
		withLock(this.recentUse) {
			this.recentUse.reset()
		}
	}

	@OriginalMember(owner = "client!oha", name = "a", descriptor = "(II)V")
	fun cacheClean() {
		@Pc(10) val referenceCache: LRUCache = this.recentUse
		withLock(this.recentUse) {
			this.recentUse.clean(5)
		}
	}
}
