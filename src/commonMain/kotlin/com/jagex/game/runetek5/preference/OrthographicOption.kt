package com.jagex.game.runetek5.preference

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.ToolkitType
import kotlin.jvm.JvmStatic

@OriginalClass("client!jaa")
class OrthographicOption : Preference {

	@OriginalMember(owner = "client!jaa", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!jaa", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	@OriginalMember(owner = "client!jaa", name = "d", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!jaa", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return 0
	}

	@OriginalMember(owner = "client!jaa", name = "e", descriptor = "(I)Z")
	fun canMod(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.isEnabled()) {
			if (this.gamePreferences!!.toolkit!!.isActive() && !ToolkitType.isSoftwareToolkit(this.gamePreferences!!.toolkit!!.getValue())) {
				this.value = 1
			}
			if (this.gamePreferences!!.fullScreen!!.getValue() == 1) {
				this.value = 1
			}
		}
		if (this.value == 3) {
			this.value = 2
		}
		if (this.value < 0 || this.value > 3) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!jaa", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		if (OrthographicOption.isOrthographicMode(value)) {
			if (this.gamePreferences!!.toolkit!!.isActive() && !ToolkitType.isSoftwareToolkit(this.gamePreferences!!.toolkit!!.getValue())) {
				return 3
			}
			if (this.gamePreferences!!.fullScreen!!.getValue() == 1) {
				return 3
			}
		}
		if (value == 3) {
			return 3
		} else if (OrthographicOption.isOrthographicMode(value)) {
			return 2
		} else {
			return 1
		}
	}

	@OriginalMember(owner = "client!jaa", name = "b", descriptor = "(Z)Z")
	fun isEnabled(): Boolean {
		return OrthographicOption.isOrthographicMode(this.value)
	}

	companion object {
		@OriginalMember(owner = "client!jr", name = "a", descriptor = "(II)Z")
		@JvmStatic fun isOrthographicMode(@OriginalArg(0) mode: Int): Boolean {
			return mode == 2 || mode == 3
		}
	}
}
