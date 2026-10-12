package com.jagex.game.runetek5.utils

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.Chat
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc

object ColorUtil {
	@OriginalMember(owner = "client!gt", name = "h", descriptor = "[I")
	@JvmField
	var hslToRgb: IntArray? = null

	@OriginalMember(owner = "client!nia", name = "c", descriptor = "[I")
	@JvmField
	var hsvToRgbTable: IntArray? = null

	@OriginalMember(owner = "client!uv", name = "a", descriptor = "(ZIZ)V")
	@JvmStatic
	fun releaseColorTables(@OriginalArg(0) releaseHslTable: Boolean, @OriginalArg(2) releaseHsvTable: Boolean) {
		if (releaseHslTable) {
			ColorUtil.hslToRgbUsers--
			if (ColorUtil.hslToRgbUsers == 0) {
				ColorUtil.hslToRgb = null
			}
		}
		if (!releaseHsvTable) {
			return
		}
		Chat.hsvToRgbUsers--
		if (Chat.hsvToRgbUsers == 0) {
			ColorUtil.hsvToRgbTable = null
			return
		}
	}

	@OriginalMember(owner = "client!gh", name = "a", descriptor = "(IIB)I")
	@JvmStatic
	fun scaleHslLightness(@OriginalArg(0) hsl: Int): Int {
		if (hsl == -1) {
			return 12345678
		}
		@Pc(21) var lightness: Int = ((hsl and 0x7F) * 96) shr 7
		if (lightness < 2) {
			lightness = 2
		} else if (lightness > 126) {
			lightness = 126
		}
		return lightness + (hsl and 0xFF80)
	}

	@OriginalMember(owner = "client!hq", name = "c", descriptor = "(II)S")
	@JvmStatic
	fun hslToHsv(@OriginalArg(0) hsl: Int): Short {
		@Pc(9) val hue: Int = (hsl shr 10) and 0x3F
		@Pc(15) val saturation: Int = (hsl shr 3) and 0x70
		@Pc(19) val lightness: Int = hsl and 0x7F
		@Pc(39) val delta: Int = if (lightness <= 64) (lightness * saturation) shr 7 else (saturation * (127 - lightness)) shr 7
		@Pc(43) val value: Int = delta + lightness
		@Pc(51) val hsvSaturation: Int
		if (value == 0) {
			hsvSaturation = delta shl 1
		} else {
			hsvSaturation = (delta shl 8) / value
		}
		return (value or ((hsvSaturation shr 4) shl 7) or (hue shl 10)).toShort()
	}

	@OriginalMember(owner = "client!gba", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun initHslToRgbTable() {
		if (ColorUtil.hslToRgb != null) {
			return
		}
		ColorUtil.hslToRgb = IntArray(65536)
		@Pc(20) val gammaCorrection: Double = Math.random() * 0.03 + 0.7 - 0.015
		for (@Pc(22) i in 0 until 65536) {
			@Pc(36) val hue: Double = ((i shr 10) and 0x3F).toDouble() / 64.0 + 0.0078125
			@Pc(47) val saturation: Double = ((i shr 7) and 0x7).toDouble() / 8.0 + 0.0625
			@Pc(54) val lightness: Double = (i and 0x7F).toDouble() / 128.0
			@Pc(56) var red: Double = lightness
			@Pc(58) var green: Double = lightness
			@Pc(60) var blue: Double = lightness
			if (saturation != 0.0) {
				@Pc(74) val chroma: Double
				if (lightness < 0.5) {
					chroma = lightness * (saturation + 1.0)
				} else {
					chroma = lightness + saturation - lightness * saturation
				}
				@Pc(91) val minComponent: Double = lightness * 2.0 - chroma
				@Pc(95) var hueRed: Double = hue + 0.3333333333333333
				if (hueRed > 1.0) {
					hueRed--
				}
				@Pc(109) var hueBlue: Double = hue - 0.3333333333333333
				if (hueBlue < 0.0) {
					hueBlue++
				}
				if (hueRed * 6.0 < 1.0) {
					red = minComponent + (chroma - minComponent) * 6.0 * hueRed
				} else if (hueRed * 2.0 < 1.0) {
					red = chroma
				} else if (hueRed * 3.0 < 2.0) {
					red = (chroma - minComponent) * 6.0 * (0.6666666666666666 - hueRed) + minComponent
				} else {
					red = minComponent
				}
				if (hue * 6.0 < 1.0) {
					green = minComponent + hue * 6.0 * (chroma - minComponent)
				} else if (hue * 2.0 < 1.0) {
					green = chroma
				} else if (hue * 3.0 < 2.0) {
					green = (0.6666666666666666 - hue) * (chroma - minComponent) * 6.0 + minComponent
				} else {
					green = minComponent
				}
				if (hueBlue * 6.0 < 1.0) {
					blue = minComponent + (chroma - minComponent) * 6.0 * hueBlue
				} else if (hueBlue * 2.0 < 1.0) {
					blue = chroma
				} else if (hueBlue * 3.0 < 2.0) {
					blue = minComponent + (chroma - minComponent) * (0.6666666666666666 - hueBlue) * 6.0
				} else {
					blue = minComponent
				}
			}
			red = Math.pow(red, gammaCorrection)
			green = Math.pow(green, gammaCorrection)
			blue = Math.pow(blue, gammaCorrection)
			@Pc(288) val redInt: Int = (red * 256.0).toInt()
			@Pc(293) val greenInt: Int = (green * 256.0).toInt()
			@Pc(298) val blueInt: Int = (blue * 256.0).toInt()
			@Pc(309) val rgb: Int = (greenInt shl 8) + (redInt shl 16) + blueInt
			ColorUtil.hslToRgb!![i] = rgb
		}
	}

	@OriginalMember(owner = "client!eea", name = "d", descriptor = "(I)V")
	@JvmStatic
	fun initHsvToRgbTable() {
		if (ColorUtil.hsvToRgbTable != null) {
			return
		}
		ColorUtil.hsvToRgbTable = IntArray(65536)
		@Pc(20) val gammaCorrection: Double = Math.random() * 0.03 + 0.7 - 0.015
		@Pc(22) var tableIndex: Int = 0
		for (@Pc(28) i in 0 until 512) {
			@Pc(42) val hueAngle: Float = ((i shr 3).toFloat() / 64.0f + 0.0078125f) * 360.0f
			@Pc(51) val saturation: Float = (i and 0x7).toFloat() / 8.0f + 0.0625f
			for (@Pc(53) j in 0 until 128) {
				@Pc(60) val value: Float = j.toFloat() / 128.0f
				@Pc(62) var red: Float = 0.0f
				@Pc(64) var green: Float = 0.0f
				@Pc(66) var blue: Float = 0.0f
				@Pc(70) val hueSegment: Float = hueAngle / 60.0f
				@Pc(73) val hueSegmentInt: Int = hueSegment.toInt()
				@Pc(77) val hueRegion: Int = hueSegmentInt % 6
				@Pc(83) val fractionalHue: Float = (-hueSegmentInt).toFloat() + hueSegment
				@Pc(90) val minComponent: Float = value * (1.0f - saturation)
				@Pc(98) val fallingComponent: Float = value * (1.0f - fractionalHue * saturation)
				@Pc(108) val risingComponent: Float = (1.0f - (1.0f - fractionalHue) * saturation) * value
				if (hueRegion == 0) {
					red = value
					green = risingComponent
					blue = minComponent
				} else if (hueRegion == 1) {
					green = value
					red = fallingComponent
					blue = minComponent
				} else if (hueRegion == 2) {
					blue = risingComponent
					red = minComponent
					green = value
				} else if (hueRegion == 3) {
					blue = value
					green = fallingComponent
					red = minComponent
				} else if (hueRegion == 4) {
					blue = value
					green = minComponent
					red = risingComponent
				} else if (hueRegion == 5) {
					red = value
					green = minComponent
					blue = fallingComponent
				}
				red = Math.pow(red.toDouble(), gammaCorrection).toFloat()
				green = Math.pow(green.toDouble(), gammaCorrection).toFloat()
				blue = Math.pow(blue.toDouble(), gammaCorrection).toFloat()
				@Pc(203) val redInt: Int = (red * 256.0f).toInt()
				@Pc(208) val greenInt: Int = (green * 256.0f).toInt()
				@Pc(213) val blueInt: Int = (blue * 256.0f).toInt()
				@Pc(225) val rgb: Int = (greenInt shl 8) + (redInt shl 16) + blueInt - 16777216
				ColorUtil.hsvToRgbTable!![tableIndex++] = rgb
			}
		}
	}

	@OriginalMember(owner = "client!go", name = "a", descriptor = "(III)I")
	@JvmStatic
	fun alphaBlend(@OriginalArg(0) background: Int, @OriginalArg(1) foreground: Int): Int {
		@Pc(7) val alpha: Int = foreground ushr 24
		@Pc(12) val inverseAlpha: Int = 255 - alpha
		@Pc(30) val blendedForeground: Int = ((((foreground and 0xFF00) * alpha) and 0xFF0000) or (((foreground and 0xFF00FF) * alpha) and 0xFF00FF00.toInt())) ushr 8
		return blendedForeground + (((((background and 0xFF00) * inverseAlpha) and 0xFF0000) or (((background and 0xFF00FF) * inverseAlpha) and 0xFF00FF00.toInt())) ushr 8)
	}

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(IIII)I")
	@JvmStatic
	fun blendHsl(@OriginalArg(0) fromHsl: Int, @OriginalArg(1) toHsl: Int, @OriginalArg(3) blend: Int): Int {
		if (fromHsl == toHsl) {
			return fromHsl
		}
		@Pc(13) val inverseBlend: Int = 128 - blend
		@Pc(32) val lightness: Int = ((toHsl and 0x7F) * blend + inverseBlend * (fromHsl and 0x7F)) shr 7
		@Pc(46) val saturation: Int = ((toHsl and 0x380) * blend + (fromHsl and 0x380) * inverseBlend) shr 7
		@Pc(60) val hue: Int = ((toHsl and 0xFC00) * blend + inverseBlend * (fromHsl and 0xFC00)) shr 7
		return (hue and 0xFC00) or (saturation and 0x380) or (lightness and 0x7F)
	}

	@OriginalMember(owner = "client!dha", name = "a", descriptor = "(ZZB)V")
	@JvmStatic
	fun initColorTables(@OriginalArg(0) initHsvTable: Boolean, @OriginalArg(1) initHslTable: Boolean) {
		if (initHsvTable) {
			Chat.hsvToRgbUsers++
			ColorUtil.initHsvToRgbTable()
		}
		if (initHslTable) {
			ColorUtil.hslToRgbUsers++
			ColorUtil.initHslToRgbTable()
		}
	}

	@OriginalMember(owner = "client!hs", name = "k", descriptor = "I")
	@JvmField
	var hslToRgbUsers: Int = 0
}
