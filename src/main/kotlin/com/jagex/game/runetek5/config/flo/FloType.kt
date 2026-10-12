package com.jagex.game.runetek5.config.flo

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.flo.FloTypeList
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.graphics.Toolkit

@OriginalClass("client!vd")
class FloType {

	@OriginalMember(owner = "client!vd", name = "e", descriptor = "Lclient!cb;")
	@JvmField
	var floTypeList: FloTypeList? = null

	@OriginalMember(owner = "client!vd", name = "p", descriptor = "I")
	@JvmField
	var overlayId: Int = 0

	@OriginalMember(owner = "client!vd", name = "a", descriptor = "I")
	@JvmField
	var waterFogDepth: Int = 64

	@OriginalMember(owner = "client!vd", name = "d", descriptor = "Z")
	@JvmField
	var occlude: Boolean = true

	@OriginalMember(owner = "client!vd", name = "g", descriptor = "I")
	@JvmField
	var slot: Int = 8

	@OriginalMember(owner = "client!vd", name = "f", descriptor = "I")
	@JvmField
	var tileColorHsl: Int = 0

	@OriginalMember(owner = "client!vd", name = "i", descriptor = "Z")
	@JvmField
	var shadowed: Boolean = true

	@OriginalMember(owner = "client!vd", name = "c", descriptor = "Z")
	@JvmField
	var blendsWithUnderlay: Boolean = false

	@OriginalMember(owner = "client!vd", name = "l", descriptor = "I")
	@JvmField
	var minimapColorHsl: Int = -1

	@OriginalMember(owner = "client!vd", name = "m", descriptor = "I")
	@JvmField
	var textureScale: Int = 512

	@OriginalMember(owner = "client!vd", name = "b", descriptor = "I")
	@JvmField
	var waterColor: Int = 1190717

	@OriginalMember(owner = "client!vd", name = "r", descriptor = "I")
	@JvmField
	var texture: Int = -1

	@OriginalMember(owner = "client!vd", name = "q", descriptor = "I")
	@JvmField
	var waterIntensity: Int = 127

	@OriginalMember(owner = "client!vd", name = "a", descriptor = "(I)V")
	fun postDecode() {
		this.slot = (this.slot shl 8) or this.overlayId
	}

	@OriginalMember(owner = "client!vd", name = "a", descriptor = "(ILclient!cea;I)V")
	private fun decode(@OriginalArg(1) packet: Packet?, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.tileColorHsl = FloType.rgbToHslOrTransparent(packet!!.readUMedium())
		} else if (opcode == 2) {
			this.texture = packet!!.readUByte()
			return
		} else if (opcode == 3) {
			this.texture = packet!!.readUShort()
			if (this.texture == 65535) {
				this.texture = -1
				return
			}
		} else if (opcode == 5) {
			this.occlude = false
			return
		} else if (opcode == 7) {
			this.minimapColorHsl = FloType.rgbToHslOrTransparent(packet!!.readUMedium())
			return
		} else if (opcode == 8) {
			this.floTypeList!!.lastLoadedOverlayId = this.overlayId
			return
		} else if (opcode == 9) {
			this.textureScale = packet!!.readUShort() shl 2
			return
		} else if (opcode == 10) {
			this.shadowed = false
			return
		} else if (opcode == 11) {
			this.slot = packet!!.readUByte()
			return
		} else if (opcode == 12) {
			this.blendsWithUnderlay = true
			return
		} else if (opcode == 13) {
			this.waterColor = packet!!.readUMedium()
			return
		} else {
			if (opcode == 14) {
				this.waterFogDepth = packet!!.readUByte() shl 2
			} else if (opcode == 16) {
				this.waterIntensity = packet!!.readUByte()
				return
			}
			return
		}
	}

	@OriginalMember(owner = "client!vd", name = "a", descriptor = "(ZLclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(11) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	companion object {
		@OriginalMember(owner = "client!aa", name = "a", descriptor = "(II)I")
		@JvmStatic
		fun rgbToHsl(@OriginalArg(1) rgb: Int): Int {
			@Pc(14) val red: Double = ((rgb shr 16) and 0xFF).toDouble() / 256.0
			@Pc(23) val green: Double = ((rgb shr 8) and 0xFF).toDouble() / 256.0
			@Pc(30) val blue: Double = (rgb and 0xFF).toDouble() / 256.0
			@Pc(32) var min: Double = red
			if (green < red) {
				min = green
			}
			if (blue < min) {
				min = blue
			}
			@Pc(46) var max: Double = red
			if (green > red) {
				max = green
			}
			if (blue > max) {
				max = blue
			}
			@Pc(60) var hue: Double = 0.0
			@Pc(67) var saturation: Double = 0.0
			@Pc(73) val lightness: Double = (min + max) / 2.0
			if (max != min) {
				if (lightness < 0.5) {
					saturation = (max - min) / (min + max)
				}
				if (max == red) {
					hue = (green - blue) / (-min + max)
				} else if (green == max) {
					hue = (blue - red) / (max - min) + 2.0
				} else if (blue == max) {
					hue = (red - green) / (max - min) + 4.0
				}
				if (lightness >= 0.5) {
					saturation = (max - min) / (2.0 - min - max)
				}
			}
			hue /= 6.0
			@Pc(164) val scaledHue: Int = (hue * 256.0).toInt()
			@Pc(169) var scaledSaturation: Int = (saturation * 256.0).toInt()
			@Pc(174) var scaledLightness: Int = (lightness * 256.0).toInt()
			if (scaledSaturation < 0) {
				scaledSaturation = 0
			} else if (scaledSaturation > 255) {
				scaledSaturation = 255
			}
			if (scaledLightness < 0) {
				scaledLightness = 0
			} else if (scaledLightness > 255) {
				scaledLightness = 255
			}
			if (scaledLightness > 243) {
				scaledSaturation = scaledSaturation shr 0x4
			} else if (scaledLightness > 217) {
				scaledSaturation = scaledSaturation shr 0x3
			} else if (scaledLightness > 192) {
				scaledSaturation = scaledSaturation shr 0x2
			} else if (scaledLightness > 179) {
				scaledSaturation = scaledSaturation shr 0x1
			}
			return (scaledLightness shr 1) + (((scaledHue and 0xFF) shr 2) shl 10) + ((scaledSaturation shr 5) shl 7)
		}

		@OriginalMember(owner = "client!cfa", name = "e", descriptor = "(II)I")
		@JvmStatic
		fun rgbToHslOrTransparent(@OriginalArg(1) rgb: Int): Int {
			return if (rgb == 16711935) -1 else FloType.rgbToHsl(rgb)
		}

		@OriginalMember(owner = "client!gw", name = "a", descriptor = "(Lclient!ha;ILclient!vd;)I")
		@JvmStatic
		fun getMinimapColorHsl(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(2) spotAnimType: FloType?): Int {
			if (spotAnimType!!.minimapColorHsl != -1) {
				return spotAnimType.minimapColorHsl
			}
			if (spotAnimType.texture != -1) {
				@Pc(19) val textureMetrics: MaterialType? = toolkit!!.materialProvider!!.getMetrics(spotAnimType.texture)
				if (!textureMetrics!!.detailsOnly) {
					return textureMetrics.colorHsl.toInt()
				}
			}
			return spotAnimType.tileColorHsl
		}
	}
}
