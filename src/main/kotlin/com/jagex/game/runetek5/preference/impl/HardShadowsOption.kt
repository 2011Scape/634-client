package com.jagex.game.runetek5.preference.impl

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!mca")
class HardShadowsOption : Preference {

	@OriginalMember(owner = "client!mca", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!mca", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!mca", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			return 3
		} else if (this.gamePreferences!!.textures!!.getValue() == 0) {
			return 3
		} else {
			return 1
		}
	}

	@OriginalMember(owner = "client!mca", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			this.value = 0
		}
		if (this.gamePreferences!!.textures!!.getValue() == 0) {
			this.value = 0
		}
		if (this.value < 0 || this.value > 2) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!mca", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	@OriginalMember(owner = "client!mca", name = "d", descriptor = "(I)Z")
	fun canMod(): Boolean {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			return false
		} else {
			return this.gamePreferences!!.textures!!.getValue() != 0
		}
	}

	@OriginalMember(owner = "client!mca", name = "e", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!mca", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return 2
	}
}
