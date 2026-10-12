package com.jagex.game.runetek5.config.flu

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!hk")
class FluType {

	@OriginalMember(owner = "client!hk", name = "e", descriptor = "I")
	@JvmField
	var saturation: Int = 0

	@OriginalMember(owner = "client!hk", name = "h", descriptor = "I")
	@JvmField
	var lightness: Int = 0

	@OriginalMember(owner = "client!hk", name = "n", descriptor = "I")
	@JvmField
	var hue: Int = 0

	@OriginalMember(owner = "client!hk", name = "p", descriptor = "I")
	@JvmField
	var hueMultiplier: Int = 0

	@OriginalMember(owner = "client!hk", name = "d", descriptor = "I")
	@JvmField
	var texture: Int = -1

	@OriginalMember(owner = "client!hk", name = "i", descriptor = "I")
	@JvmField
	var scale: Int = 512

	@OriginalMember(owner = "client!hk", name = "j", descriptor = "I")
	private var rgb: Int = 0

	@OriginalMember(owner = "client!hk", name = "g", descriptor = "Z")
	@JvmField
	var occlude: Boolean = true

	@OriginalMember(owner = "client!hk", name = "o", descriptor = "Z")
	@JvmField
	var shadowed: Boolean = true

	@OriginalMember(owner = "client!hk", name = "a", descriptor = "(Lclient!cea;I)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		while (true) {
			@Pc(11) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}

	@OriginalMember(owner = "client!hk", name = "a", descriptor = "(IILclient!cea;)V")
	private fun decode(@OriginalArg(0) opcode: Int, @OriginalArg(2) packet: Packet?) {
		if (opcode == 1) {
			this.rgb = packet!!.readUMedium()
			this.computeHsl(this.rgb)
		} else if (opcode == 2) {
			this.texture = packet!!.readUShort()
			if (this.texture == 65535) {
				this.texture = -1
			}
		} else if (opcode == 3) {
			this.scale = packet!!.readUShort() shl 2
		} else if (opcode == 4) {
			this.shadowed = false
		} else if (opcode == 5) {
			this.occlude = false
		}
	}

	@OriginalMember(owner = "client!hk", name = "a", descriptor = "(IB)V")
	private fun computeHsl(@OriginalArg(0) rgb: Int) {
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
		@Pc(62) var saturation: Double = 0.0
		@Pc(68) val lightness: Double = (min + max) / 2.0
		if (max != min) {
			if (lightness < 0.5) {
				saturation = (max - min) / (min + max)
			}
			if (red == max) {
				hue = (green - blue) / (max - min)
			} else if (max == green) {
				hue = (blue - red) / (max - min) + 2.0
			} else if (max == blue) {
				hue = (red - green) / (-min + max) + 4.0
			}
			if (lightness >= 0.5) {
				saturation = (max - min) / ((2.0 - max) - min)
			}
		}
		this.saturation = (saturation * 256.0).toInt()
		hue /= 6.0
		this.lightness = (lightness * 256.0).toInt()
		if (this.saturation < 0) {
			this.saturation = 0
		} else if (this.saturation > 255) {
			this.saturation = 255
		}
		if (lightness > 0.5) {
			this.hueMultiplier = ((1.0 - lightness) * 512.0 * saturation).toInt()
		} else {
			this.hueMultiplier = (lightness * 512.0 * saturation).toInt()
		}
		if (this.lightness < 0) {
			this.lightness = 0
		} else if (this.lightness > 255) {
			this.lightness = 255
		}
		if (this.hueMultiplier < 1) {
			this.hueMultiplier = 1
		}
		this.hue = (hue * this.hueMultiplier.toDouble()).toInt()
	}
}
