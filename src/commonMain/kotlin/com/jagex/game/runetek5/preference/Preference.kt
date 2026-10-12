package com.jagex.game.runetek5.preference

import com.jagex.game.runetek5.preference.GamePreferences
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!su")
abstract class Preference {

	@OriginalMember(owner = "client!su", name = "c", descriptor = "Lclient!dh;")
	@JvmField val gamePreferences: GamePreferences?

	@OriginalMember(owner = "client!su", name = "e", descriptor = "I")
	@JvmField var value: Int = 0

	@OriginalMember(owner = "client!su", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) {
		this.gamePreferences = clientOptions
		this.value = this.getDefaultValue()
	}

	@OriginalMember(owner = "client!su", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) {
		this.gamePreferences = clientOptions
		this.value = value
	}

	@OriginalMember(owner = "client!su", name = "b", descriptor = "(I)I")
	abstract fun getDefaultValue(): Int

	@OriginalMember(owner = "client!su", name = "a", descriptor = "(II)V")
	abstract fun setValue(@OriginalArg(1) value: Int)

	@OriginalMember(owner = "client!su", name = "b", descriptor = "(II)I")
	abstract fun canSet(@OriginalArg(1) value: Int): Int

	@OriginalMember(owner = "client!su", name = "a", descriptor = "(Z)V")
	abstract fun validate()

	@OriginalMember(owner = "client!su", name = "c", descriptor = "(II)V")
	fun setSafeValue(@OriginalArg(0) value: Int) {
		if (this.canSet(value) != 3) {
			this.setValue(value)
		}
	}
}
