package com.jagex.game.runetek5.math

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object ColourUtils {

	@OriginalMember(owner = "client!w", name = "g", descriptor = "I")
	@JvmField
	var cameraLookAtPathIndex: Int = -1

	@OriginalMember(owner = "client!wf", name = "a", descriptor = "(IIII)I")
	@JvmStatic
	fun blendColours(@OriginalArg(0) colour: Int, @OriginalArg(2) alpha: Int, @OriginalArg(3) fogColour: Int): Int {
		@Pc(13) val inverseAlpha: Int = 255 - alpha
		@Pc(31) val blendedFog: Int = ((((fogColour and 0xFF00FF) * alpha) and 0xFF00FF00.toInt()) or (((fogColour and 0xFF00) * alpha) and 0xFF0000)) ushr 8
		return blendedFog + ((((inverseAlpha * (colour and 0xFF00FF)) and 0xFF00FF00.toInt()) or ((inverseAlpha * (colour and 0xFF00)) and 0xFF0000)) ushr 8)
	}

	@OriginalMember(owner = "client!tfa", name = "a", descriptor = "(IBII)I")
	@JvmStatic
	fun packHsl(@OriginalArg(0) lightness: Int, @OriginalArg(2) saturation: Int, @OriginalArg(3) hue: Int): Int {
		var saturation: Int = saturation
		if (lightness > 243) {
			saturation = saturation shr 0x4
		} else if (lightness > 217) {
			saturation = saturation shr 0x3
		} else if (lightness > 192) {
			saturation = saturation shr 0x2
		} else if (lightness > 179) {
			saturation = saturation shr 0x1
		}
		return (lightness shr 1) + ((saturation shr 5) shl 7) + (((hue shr 2) and 0x3F) shl 10)
	}
}
