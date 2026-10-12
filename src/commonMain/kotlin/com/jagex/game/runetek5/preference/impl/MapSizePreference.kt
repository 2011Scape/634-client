package com.jagex.game.runetek5.preference.impl

import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.Preference
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!kia")
class MapSizePreference : Preference {


	@OriginalMember(owner = "client!kia", name = "<init>", descriptor = "(ILclient!dh;)V")
	constructor(@OriginalArg(0) value: Int, @OriginalArg(1) clientOptions: GamePreferences?) : super(value, clientOptions)

	@OriginalMember(owner = "client!kia", name = "<init>", descriptor = "(Lclient!dh;)V")
	constructor(@OriginalArg(0) clientOptions: GamePreferences?) : super(clientOptions)

	@OriginalMember(owner = "client!kia", name = "b", descriptor = "(I)I")
	override fun getDefaultValue(): Int {
		return 0
	}

	@OriginalMember(owner = "client!kia", name = "d", descriptor = "(I)I")
	fun getValue(): Int {
		return this.value
	}

	@OriginalMember(owner = "client!kia", name = "e", descriptor = "(I)Z")
	fun canMod(): Boolean {
		@Pc(17) val heapSize: Int = this.gamePreferences!!.getProcessorSpecs().getMaxMemory()
		return heapSize >= 96
	}

	@OriginalMember(owner = "client!kia", name = "a", descriptor = "(Z)V")
	override fun validate() {
		@Pc(10) val heapSize: Int = this.gamePreferences!!.getProcessorSpecs().getMaxMemory()
		if (heapSize < 96) {
			this.value = 0
		}
		if (this.value > 1 && heapSize < 128) {
			this.value = 1
		}
		if (this.value > 2 && heapSize < 192) {
			this.value = 2
		}
		if (this.value < 0 || this.value > 3) {
			this.value = this.getDefaultValue()
		}
	}

	@OriginalMember(owner = "client!kia", name = "b", descriptor = "(II)I")
	override fun canSet(@OriginalArg(1) value: Int): Int {
		@Pc(12) val heapSize: Int = this.gamePreferences!!.getProcessorSpecs().getMaxMemory()
		if (heapSize < 96) {
			return 3
		} else if (value > 1 && heapSize < 128) {
			return 3
		} else if (value > 3 && heapSize < 192) {
			return 3
		} else {
			return 1
		}
	}

	@OriginalMember(owner = "client!kia", name = "a", descriptor = "(II)V")
	override fun setValue(@OriginalArg(1) value: Int) {
		this.value = value
	}

	companion object {
		@OriginalMember(owner = "client!kia", name = "w", descriptor = "[I")
		@JvmField val DECODE_TABLE: IntArray = IntArray(128)

		init {
			@Pc(4) var i: Int = 0
			while (i < DECODE_TABLE.size) {
				DECODE_TABLE[i] = -1
				i++
			}
			@Pc(20) var i2: Int = 65
			while (i2 <= 90) {
				DECODE_TABLE[i2] = i2 - 65
				i2++
			}
			@Pc(33) var i3: Int = 97
			while (i3 <= 122) {
				DECODE_TABLE[i3] = i3 + 26 - 97
				i3++
			}
			@Pc(48) var i4: Int = 48
			while (i4 <= 57) {
				DECODE_TABLE[i4] = i4 + 52 - 48
				i4++
			}
			DECODE_TABLE[43] = 62
			DECODE_TABLE[42] = 62
			DECODE_TABLE[47] = 63
			DECODE_TABLE[45] = 63
		}

		@OriginalMember(owner = "client!eh", name = "a", descriptor = "(II)I")
		@JvmStatic fun getMaxBuildArea(@OriginalArg(1) maxMemory: Int): Int {
			if (maxMemory < 96) {
				return 0
			} else if (maxMemory < 128) {
				return 2
			} else {
				return 3
			}
		}
	}
}
