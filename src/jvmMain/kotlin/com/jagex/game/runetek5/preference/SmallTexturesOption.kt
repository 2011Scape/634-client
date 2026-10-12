package com.jagex.game.runetek5.preference

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!gga")
class SmallTexturesOption : Preference {

	@OriginalMember(owner = "client!gga", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!gga", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!gga", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		return 3
	}

	@OriginalMember(owner = "client!gga", name = "a", descriptor = "(Z)V")
	override fun validate() {
		this.value = this.getDefaultValue()
	}

	@OriginalMember(owner = "client!gga", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return if (this.gamePreferences!!.isLowMemoryJava()) 1 else 0
	}

	@OriginalMember(owner = "client!gga", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	@OriginalMember(owner = "client!gga", name = "f", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}
}
