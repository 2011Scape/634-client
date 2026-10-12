package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!hr")
class TextureOpPolarDistortion : TextureOperation {

	@OriginalMember(owner = "client!hr", name = "K", descriptor = "I")
	private var distortionStrength: Int = 32768

	@OriginalMember(owner = "client!hr", name = "<init>", descriptor = "()V")
	constructor() : super(3, false)

	@OriginalMember(owner = "client!hr", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) y: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(y)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(31) val angles: IntArray = this.getChildMonochromeOutput(y, 1)!!
			@Pc(39) val lengths: IntArray = this.getChildMonochromeOutput(y, 2)!!
			@Pc(41) var x: Int = 0
			while (x < EnvironmentLight.textureOpWidth) {
				@Pc(51) val angle: Int = (angles[x] shr 4) and 0xFF
				@Pc(60) val length: Int = (this.distortionStrength * lengths[x]) shr 12
				@Pc(68) val offsetX: Int = (TextureRaster.PALETTE_COS!![angle] * length) shr 12
				@Pc(76) val offsetY: Int = (TextureRaster.PALETTE_SIN!![angle] * length) shr 12
				@Pc(85) val sampleX: Int = (x + (offsetX shr 12)) and TextureRaster.widthMask
				@Pc(94) val sampleY: Int = (y + (offsetY shr 12)) and TextureOperation.columnEnd
				@Pc(100) val source: IntArray = this.getChildMonochromeOutput(sampleY, 0)!!
				output[x] = source[sampleX]
				x++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!hr", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.distortionStrength = packet!!.readUShort() shl 4
		} else if (opcode == 1) {
			this.monochrome = packet!!.readUByte() == 1
		}
	}

	@OriginalMember(owner = "client!hr", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		TextureRaster.calculateTrigTables()
	}

	@OriginalMember(owner = "client!hr", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) y: Int): Array<IntArray?> {
		@Pc(13) val output: Array<IntArray?> = this.colorImageCache!!.get(y)!!
		if (this.colorImageCache!!.dirty) {
			@Pc(28) val angles: IntArray = this.getChildMonochromeOutput(y, 1)!!
			@Pc(34) val lengths: IntArray = this.getChildMonochromeOutput(y, 2)!!
			@Pc(38) val outputRed: IntArray = output[0]!!
			@Pc(42) val outputGreen: IntArray = output[1]!!
			@Pc(46) val outputBlue: IntArray = output[2]!!
			@Pc(48) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(60) val angle: Int = ((angles[i] * 255) shr 12) and 0xFF
				@Pc(69) val length: Int = (this.distortionStrength * lengths[i]) shr 12
				@Pc(77) val offsetX: Int = (length * TextureRaster.PALETTE_COS!![angle]) shr 12
				@Pc(85) val offsetY: Int = (length * TextureRaster.PALETTE_SIN!![angle]) shr 12
				@Pc(93) val sampleX: Int = (i + (offsetX shr 12)) and TextureRaster.widthMask
				@Pc(101) val sampleY: Int = ((offsetY shr 12) + y) and TextureOperation.columnEnd
				@Pc(107) val source: Array<IntArray?> = this.getChildColorOutput(sampleY, 0)!!
				outputRed[i] = source[0]!![sampleX]
				outputGreen[i] = source[1]!![sampleX]
				outputBlue[i] = source[2]!![sampleX]
				i++
			}
		}
		return output
	}
}
