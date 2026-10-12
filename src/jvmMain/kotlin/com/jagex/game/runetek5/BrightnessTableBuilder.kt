package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.material.texture.TextureTypeList
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object BrightnessTableBuilder {

	@OriginalMember(owner = "client!vq", name = "a", descriptor = "(DB)V")
	@JvmStatic
	fun setBrightness(@OriginalArg(0) currentBrightness: Double) {
		if (currentBrightness == TextureTypeList.currentBrightness) {
			return
		}
		for (@Pc(11) i in 0 until 256) {
			@Pc(23) val value: Int = (Math.pow(i.toDouble() / 255.0, currentBrightness) * 255.0).toInt()
			TextureTypeList.BRIGHTNESS_TABLE[i] = if (value > 255) 255 else value
		}
		TextureTypeList.currentBrightness = currentBrightness
	}
}
