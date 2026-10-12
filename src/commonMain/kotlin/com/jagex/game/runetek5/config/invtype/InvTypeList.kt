package com.jagex.game.runetek5.config.invtype

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.InvType
import withLock

@OriginalClass("client!ut")
class InvTypeList {

	@OriginalMember(owner = "client!ut", name = "a", descriptor = "Lclient!jr;")
	private val recentUse: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!ut", name = "c", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!ut", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider?) {
		this.configClient = configClient
		this.configClient!!.getGroupCapacity(5)
	}

	@OriginalMember(owner = "client!ut", name = "a", descriptor = "(II)Lclient!pv;")
	fun list(@OriginalArg(1) id: Int): InvType? {
		@Pc(6) val referenceCache: LRUCache = this.recentUse
		@Pc(16) var invType: InvType?
		withLock(this.recentUse) {
			invType = this.recentUse.get(id.toLong()) as InvType?
		}
		if (invType != null) {
			return invType
		}
		@Pc(29) val configClient: Js5ResourceProvider? = this.configClient
		@Pc(38) var data: ByteArray?
		withLock(this.configClient!!) {
			data = this.configClient.getFile(5, id)
		}
		invType = InvType()
		if (data != null) {
			invType!!.decode(Packet(data))
		}
		@Pc(64) val referenceCache2: LRUCache = this.recentUse
		withLock(this.recentUse) {
			this.recentUse.put(invType, id.toLong())
			return invType
		}
	}
}
