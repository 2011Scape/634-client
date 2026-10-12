package com.jagex.game.runetek5.client

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!sj")
class Game @OriginalMember(owner = "client!sj", name = "<init>", descriptor = "(Ljava/lang/String;I)V") constructor(@OriginalArg(0) string: String?, @OriginalArg(1) id: Int) {

	@OriginalMember(owner = "client!sj", name = "d", descriptor = "Ljava/lang/String;")
	@JvmField
	val domainName: String?

	@OriginalMember(owner = "client!sj", name = "f", descriptor = "I")
	@JvmField
	val id: Int

	init {
		this.domainName = string
		this.id = id
	}

	@OriginalMember(owner = "client!sj", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!gt", name = "o", descriptor = "Lclient!sj;")
		@JvmField
		val RUNESCAPE: Game = Game("runescape", 0)
		@OriginalMember(owner = "client!tia", name = "e", descriptor = "Lclient!sj;")
		@JvmField
		val GAME3: Game = Game("game3", 2)

		@OriginalMember(owner = "client!tw", name = "C", descriptor = "Lclient!sj;")
		@JvmField
		val STELLAR_DAWN: Game = Game("stellardawn", 1)
		@OriginalMember(owner = "client!mm", name = "v", descriptor = "Lclient!sj;")
		@JvmField
		val GAME4: Game = Game("game4", 3)
		@OriginalMember(owner = "client!pf", name = "M", descriptor = "Lclient!sj;")
		@JvmField
		var CURRENT_GAME: Game? = null
	}
}
