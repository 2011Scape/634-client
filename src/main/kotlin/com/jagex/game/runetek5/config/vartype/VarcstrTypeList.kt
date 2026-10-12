package com.jagex.game.runetek5.config.vartype

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.config.Js5ConfigGroup
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!lba")
class VarcstrTypeList {

	@OriginalMember(owner = "client!lba", name = "c", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!lba", name = "f", descriptor = "I")
	@JvmField
	val num: Int

	@OriginalMember(owner = "client!lba", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) configClient: Js5ResourceProvider?) {
		LRUCache(64)
		this.configClient = configClient
		this.num = this.configClient!!.getGroupCapacity(Js5ConfigGroup.VARCSTR)
	}
}
