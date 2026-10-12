package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.water.WaterTextureGenerator
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!rc")
class EnvironmentContext @OriginalMember(owner = "client!rc", name = "<init>", descriptor = "(ZIIZ)V") constructor(@OriginalArg(0) signed: Boolean, @OriginalArg(1) maxMemory: Int, @OriginalArg(2) processorCount: Int, @OriginalArg(3) arm: Boolean) {

	@OriginalMember(owner = "client!rc", name = "c", descriptor = "I")
	private val processorCount: Int = processorCount

	@OriginalMember(owner = "client!rc", name = "b", descriptor = "Z")
	private val arm: Boolean = arm

	@OriginalMember(owner = "client!rc", name = "l", descriptor = "I")
	private val maxMemory: Int = maxMemory

	@OriginalMember(owner = "client!rc", name = "f", descriptor = "Z")
	private val signed: Boolean = signed

	@OriginalMember(owner = "client!rc", name = "a", descriptor = "(B)Z")
	fun isArm(): Boolean {
		return this.arm
	}

	@OriginalMember(owner = "client!rc", name = "a", descriptor = "(I)I")
	fun getMaxMemory(): Int {
		return this.maxMemory
	}

	@OriginalMember(owner = "client!rc", name = "b", descriptor = "(I)Z")
	fun isSigned(): Boolean {
		return this.signed
	}

	@OriginalMember(owner = "client!rc", name = "c", descriptor = "(I)I")
	fun getProcessorCount(): Int {
		return this.processorCount
	}

	companion object {
		@OriginalMember(owner = "client!rc", name = "h", descriptor = "[I")
		@JvmField
		val FADE_TABLE: IntArray = IntArray(4096)

		init {
			for (@Pc(4) i in 0 until 4096) {
				FADE_TABLE[i] = WaterTextureGenerator.fade(i)
			}
		}
	}
}
