package com.jagex.game.runetek5.preference.impl

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.ClientState

@OriginalClass("client!bl")
class MaxScreenSizeOption : Preference {

	@OriginalMember(owner = "client!bl", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!bl", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!bl", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			return 2
		} else if (this.gamePreferences!!.toolkit!!.isActive() && MaxScreenSizeOption.isSoftwareToolkit(this.gamePreferences!!.toolkit!!.getValue())) {
			return 1
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!bl", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			this.value = 2
		}
		if (this.value < 0 || this.value > 2) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!bl", name = "d", descriptor = "(I)Z")
	fun canMod(): Boolean {
		return !this.gamePreferences!!.isLowMemoryJava()
	}

	@OriginalMember(owner = "client!bl", name = "e", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!bl", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		return if (this.gamePreferences!!.isLowMemoryJava()) 3 else 1
	}

	@OriginalMember(owner = "client!bl", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	companion object {
		@OriginalMember(owner = "client!tw", name = "a", descriptor = "(II)Z")
		@JvmStatic fun isSoftwareToolkit(@OriginalArg(0) toolkitId: Int): Boolean {
			return toolkitId == 0 || toolkitId == 2
		}
	}
}
