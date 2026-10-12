package com.jagex.game.runetek5.preference.impl

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager

@OriginalClass("client!pr")
class ParticlesOption : Preference {

	@OriginalMember(owner = "client!pr", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions) {
		ParticleSystemManager.setDetail(this.value)
	}

	@OriginalMember(owner = "client!pr", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions) {
		ParticleSystemManager.setDetail(this.value)
	}

	@OriginalMember(owner = "client!pr", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.gamePreferences!!.getProcessorSpecs().getMaxMemory() < 96) {
			this.value = 0
		}
		if (this.value < 0 || this.value > 2) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!pr", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		return if (this.gamePreferences!!.getProcessorSpecs().getMaxMemory() < 96) 3 else 1
	}

	@OriginalMember(owner = "client!pr", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
		ParticleSystemManager.setDetail(this.value)
	}

	@OriginalMember(owner = "client!pr", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return if (this.gamePreferences!!.getProcessorSpecs().getMaxMemory() < 96) 0 else 2
	}

	@OriginalMember(owner = "client!pr", name = "d", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!pr", name = "f", descriptor = "(I)Z")
	fun canMod(): Boolean {
		return this.gamePreferences!!.getProcessorSpecs().getMaxMemory() >= 96
	}
}
