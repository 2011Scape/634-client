package com.jagex.game.runetek5.preference

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.ToolkitType

@OriginalClass("client!av")
class BloomOption : Preference {

	@OriginalMember(owner = "client!av", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!av", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!av", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		return if (ToolkitType.isHardwareToolkit(this.gamePreferences!!.toolkit!!.getValue())) 1 else 3
	}

	@OriginalMember(owner = "client!av", name = "d", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!av", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	@OriginalMember(owner = "client!av", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.gamePreferences!!.toolkit!!.isActive() && !ToolkitType.isHardwareToolkit(this.gamePreferences!!.toolkit!!.getValue())) {
			this.value = 0
		}
		if (this.value < 0 || this.value > 1) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!av", name = "f", descriptor = "(I)Z")
	fun canMod(): Boolean {
		return ToolkitType.isHardwareToolkit(this.gamePreferences!!.toolkit!!.getValue())
	}

	@OriginalMember(owner = "client!av", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return 0
	}
}
