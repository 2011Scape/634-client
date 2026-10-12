package com.jagex.game.runetek5.preference

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!vu")
class CharacterShadowPreference : Preference {

	@OriginalMember(owner = "client!vu", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!vu", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!vu", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return 1
	}

	@OriginalMember(owner = "client!vu", name = "d", descriptor = "(I)Z")
	fun canMod(): Boolean {
		return !this.gamePreferences!!.isLowMemoryJava()
	}

	@OriginalMember(owner = "client!vu", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	@OriginalMember(owner = "client!vu", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		return if (this.gamePreferences!!.isLowMemoryJava()) 3 else 1
	}

	@OriginalMember(owner = "client!vu", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			this.value = 0
		}
		if (this.value != 1 && this.value != 0) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!vu", name = "e", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}
}
