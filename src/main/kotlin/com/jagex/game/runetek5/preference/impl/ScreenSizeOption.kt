package com.jagex.game.runetek5.preference.impl

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!mfa")
class ScreenSizeOption : Preference {

	@OriginalMember(owner = "client!mfa", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!mfa", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!mfa", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return if (this.gamePreferences!!.getProcessorSpecs().isArm()) 3 else 2
	}

	@OriginalMember(owner = "client!mfa", name = "d", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	@OriginalMember(owner = "client!mfa", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		return 1
	}

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.value < 1 || this.value > 3) {
			this.value = this.getDefaultValue()
		}
	}
}
