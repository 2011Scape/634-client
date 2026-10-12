package com.jagex.game.runetek5.preference

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.Game

@OriginalClass("client!kba")
class GroundDecorOption : Preference {

	companion object {
		@OriginalMember(owner = "client!kba", name = "q", descriptor = "[F")
		@JvmField val SINE: FloatArray = FloatArray(16384)

		@OriginalMember(owner = "client!kba", name = "s", descriptor = "[F")
		@JvmField val COSINE: FloatArray = FloatArray(16384)

		init {
			@Pc(7) val angle: Double = 3.834951969714103E-4
			@Pc(9) var index: Int = 0
			while (index < 16384) {
				SINE[index] = java.lang.Math.sin(angle * index.toDouble()).toFloat()
				COSINE[index] = java.lang.Math.cos(index.toDouble() * angle).toFloat()
				index++
			}
		}
	}

	@OriginalMember(owner = "client!kba", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!kba", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!kba", name = "a", descriptor = "(Z)V")
	override fun validate() {
		if (this.gamePreferences!!.getGame() !== Game.RUNESCAPE) {
			this.value = 1
		} else if (this.gamePreferences!!.isLowMemoryJava()) {
			this.value = 0
		}
		if (this.value != 0 && this.value != 1) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!kba", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	@OriginalMember(owner = "client!kba", name = "d", descriptor = "(I)Z")
	fun canMod(): Boolean {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			return false
		} else {
			return this.gamePreferences!!.getGame() === Game.RUNESCAPE
		}
	}

	@OriginalMember(owner = "client!kba", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return 1
	}

	@OriginalMember(owner = "client!kba", name = "e", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!kba", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		if (this.gamePreferences!!.isLowMemoryJava()) {
			return 3
		} else if (this.gamePreferences!!.getGame() === Game.RUNESCAPE) {
			return 1
		} else {
			return 3
		}
	}
}
