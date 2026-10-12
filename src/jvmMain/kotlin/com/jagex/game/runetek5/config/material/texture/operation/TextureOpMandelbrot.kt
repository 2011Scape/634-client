package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureRaster

@OriginalClass("client!tga")
class TextureOpMandelbrot : TextureOperation {

	@OriginalMember(owner = "client!tga", name = "L", descriptor = "I")
	private var centerY: Int = 0

	@OriginalMember(owner = "client!tga", name = "M", descriptor = "I")
	private var centerX: Int = 0

	@OriginalMember(owner = "client!tga", name = "N", descriptor = "I")
	private var zoom: Int = 1365

	@OriginalMember(owner = "client!tga", name = "Q", descriptor = "I")
	private var maxIterations: Int = 20

	@OriginalMember(owner = "client!tga", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!tga", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.zoom = packet!!.readUShort()
		} else if (opcode == 1) {
			this.maxIterations = packet!!.readUShort()
		} else if (opcode == 2) {
			this.centerX = packet!!.readUShort()
		} else if (opcode == 3) {
			this.centerY = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!tga", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(22) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				@Pc(36) val cReal: Int = this.centerX + (EnvironmentLight.horizontalTable!![i] shl 12) / this.zoom
				@Pc(48) val cImaginary: Int = this.centerY + (TextureRaster.verticalTable!![row] shl 12) / this.zoom
				@Pc(54) var real: Int = cReal
				@Pc(56) var imaginary: Int = cImaginary
				@Pc(62) var realSquared: Int = (cReal * cReal) shr 12
				@Pc(68) var imaginarySquared: Int = (cImaginary * cImaginary) shr 12
				@Pc(70) var iterations: Int = 0
				while (imaginarySquared + realSquared < 16384 && iterations < this.maxIterations) {
					imaginary = cImaginary + ((imaginary * real) shr 12) * 2
					real = cReal + realSquared - imaginarySquared
					iterations++
					imaginarySquared = (imaginary * imaginary) shr 12
					realSquared = (real * real) shr 12
				}
				output[i] = if (this.maxIterations - 1 <= iterations) 0 else (iterations shl 12) / this.maxIterations
				i++
			}
		}
		return output
	}
}
