package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!vj")
class TextureOpHslAdjust : TextureOperation {

	@OriginalMember(owner = "client!vj", name = "L", descriptor = "I")
	private var outputRed: Int = 0

	@OriginalMember(owner = "client!vj", name = "O", descriptor = "I")
	private var lightness: Int = 0

	@OriginalMember(owner = "client!vj", name = "S", descriptor = "I")
	private var outputBlue: Int = 0

	@OriginalMember(owner = "client!vj", name = "W", descriptor = "I")
	private var outputGreen: Int = 0

	@OriginalMember(owner = "client!vj", name = "ab", descriptor = "I")
	private var hue: Int = 0

	@OriginalMember(owner = "client!vj", name = "bb", descriptor = "I")
	private var saturation: Int = 0

	@OriginalMember(owner = "client!vj", name = "P", descriptor = "I")
	private var saturationAdjust: Int = 0

	@OriginalMember(owner = "client!vj", name = "Y", descriptor = "I")
	private var lightnessAdjust: Int = 0

	@OriginalMember(owner = "client!vj", name = "cb", descriptor = "I")
	private var hueAdjust: Int = 0

	@OriginalMember(owner = "client!vj", name = "<init>", descriptor = "()V")
	constructor() : super(1, false)

	@OriginalMember(owner = "client!vj", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) row: Int): Array<IntArray?> {
		@Pc(11) val output: Array<IntArray?> = this.colorImageCache!!.get(row)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(21) val input: Array<IntArray?> = this.getChildColorOutput(row, 0)!!
			@Pc(25) val inRed: IntArray = input[0]!!
			@Pc(29) val inGreen: IntArray = input[1]!!
			@Pc(33) val inBlue: IntArray = input[2]!!
			@Pc(37) val outRed: IntArray = output[0]!!
			@Pc(41) val outGreen: IntArray = output[1]!!
			@Pc(45) val outBlue: IntArray = output[2]!!
			@Pc(47) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				this.rgbToHsl(inRed[i], inGreen[i], inBlue[i])
				this.saturation += this.saturationAdjust
				this.hue += this.hueAdjust
				this.lightness += this.lightnessAdjust
				while (this.hue < 0) {
					this.hue += 4096
				}
				if (this.saturation < 0) {
					this.saturation = 0
				}
				while (this.hue > 4096) {
					this.hue -= 4096
				}
				if (this.lightness < 0) {
					this.lightness = 0
				}
				if (this.saturation > 4096) {
					this.saturation = 4096
				}
				if (this.lightness > 4096) {
					this.lightness = 4096
				}
				this.hslToRgb(this.lightness, this.hue, this.saturation)
				outRed[i] = this.outputRed
				outGreen[i] = this.outputGreen
				outBlue[i] = this.outputBlue
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!vj", name = "a", descriptor = "(IIBI)V")
	private fun rgbToHsl(@OriginalArg(0) red: Int, @OriginalArg(1) green: Int, @OriginalArg(3) blue: Int) {
		@Pc(12) val maxRedGreen: Int = if (green >= red) green else red
		@Pc(23) val minRedGreen: Int = if (blue <= maxRedGreen) maxRedGreen else blue
		@Pc(34) val max: Int = if (green > red) red else green
		@Pc(41) val min: Int = if (blue >= max) max else blue
		this.lightness = (minRedGreen + min) / 2
		@Pc(52) val range: Int = minRedGreen - min
		if (range > 0) {
			@Pc(66) val redDistance: Int = ((minRedGreen - red) shl 12) / range
			@Pc(75) val greenDistance: Int = ((minRedGreen - green) shl 12) / range
			@Pc(84) val blueDistance: Int = ((minRedGreen - blue) shl 12) / range
			if (red == minRedGreen) {
				this.hue = if (min == green) blueDistance + 20480 else -greenDistance + 4096
			} else if (minRedGreen == green) {
				this.hue = if (blue == min) redDistance + 4096 else -blueDistance + 12288
			} else {
				this.hue = if (red == min) greenDistance + 12288 else -redDistance + 20480
			}
			this.hue /= 6
		} else {
			this.hue = 0
		}
		if (this.lightness > 0 && this.lightness < 4096) {
			this.saturation = (range shl 12) / (if (this.lightness > 2048) 8192 - this.lightness * 2 else this.lightness * 2)
		} else {
			this.saturation = 0
		}
	}

	@OriginalMember(owner = "client!vj", name = "b", descriptor = "(IIBI)V")
	private fun hslToRgb(@OriginalArg(0) outputBlue: Int, @OriginalArg(1) hue: Int, @OriginalArg(3) saturation: Int) {
		@Pc(35) val max: Int = if (outputBlue > 2048) saturation + outputBlue - ((outputBlue * saturation) shr 12) else (outputBlue * (4096 - -saturation)) shr 12
		if (max <= 0) {
			this.outputBlue = outputBlue
			this.outputGreen = this.outputBlue
			this.outputRed = this.outputGreen
			return
		}
		@Pc(51) val scaledHue: Int = hue * 6
		@Pc(59) val min: Int = outputBlue + outputBlue - max
		@Pc(68) val delta: Int = ((max - min) shl 12) / max
		@Pc(72) val sector: Int = scaledHue shr 12
		@Pc(79) val remainder: Int = scaledHue - (sector shl 12)
		@Pc(87) val span: Int = (max * delta) shr 12
		@Pc(93) val step: Int = (span * remainder) shr 12
		@Pc(97) val rising: Int = step + min
		@Pc(101) val falling: Int = max - step
		if (sector == 0) {
			this.outputGreen = rising
			this.outputRed = max
			this.outputBlue = min
		} else if (sector == 1) {
			this.outputBlue = min
			this.outputRed = falling
			this.outputGreen = max
		} else if (sector == 2) {
			this.outputBlue = rising
			this.outputRed = min
			this.outputGreen = max
		} else if (sector == 3) {
			this.outputGreen = falling
			this.outputRed = min
			this.outputBlue = max
		} else if (sector == 4) {
			this.outputGreen = min
			this.outputRed = rising
			this.outputBlue = max
		} else if (sector == 5) {
			this.outputRed = max
			this.outputBlue = falling
			this.outputGreen = min
		}
	}

	@OriginalMember(owner = "client!vj", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.hueAdjust = packet!!.readShort()
		} else if (opcode == 1) {
			this.saturationAdjust = (packet!!.readByte().toInt() shl 12) / 100
		} else if (opcode == 2) {
			this.lightnessAdjust = (packet!!.readByte().toInt() shl 12) / 100
		}
	}
}
