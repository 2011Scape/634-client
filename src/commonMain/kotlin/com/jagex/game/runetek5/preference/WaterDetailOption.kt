package com.jagex.game.runetek5.preference

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!tl")
class WaterDetailOption : Preference {

	@OriginalMember(owner = "client!tl", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!tl", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!tl", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			return 3
		} else if (value == 0 || this.gamePreferences!!.groundBlending!!.getValue() == 1) {
			return 1
		} else {
			return 2
		}
	}

	@OriginalMember(owner = "client!tl", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return 1
	}

	@OriginalMember(owner = "client!tl", name = "e", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!tl", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			this.value = 0
		}
		if (this.value < 0 && this.value > 2) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!tl", name = "f", descriptor = "(I)Z")
	fun canMod(): Boolean {
		return !this.gamePreferences!!.isLowMemoryJava()
	}

	@OriginalMember(owner = "client!tl", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}
}
