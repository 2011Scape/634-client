package com.jagex.game.runetek5.preference.impl

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.Game

@OriginalClass("client!ph")
class IdleAnimationsOption : Preference {

	@OriginalMember(owner = "client!ph", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!ph", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!ph", name = "d", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!ph", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.gamePreferences!!.getGame() === Game.STELLAR_DAWN) {
			this.value = 2
		}
		if (this.value < 0 || this.value > 2) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!ph", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		return 1
	}

	@OriginalMember(owner = "client!ph", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	@OriginalMember(owner = "client!ph", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return 1
	}
}
