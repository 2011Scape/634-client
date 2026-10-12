package com.jagex.game.runetek5.graphics.texture.water

import java.util.Random
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!la")
class PerlinNoiseGenerator : NoiseGenerator {

	@OriginalMember(owner = "client!la", name = "i", descriptor = "[I")
	private val permutations: IntArray = IntArray(512)

	@OriginalMember(owner = "client!la", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) seed: Int) : super() {
		@Pc(13) val random: Random = Random(seed.toLong())
		@Pc(15) var i: Int = 0
		while (i < 256) {
			this.permutations[i + 256] = i
			this.permutations[i] = i
			i++
		}
		@Pc(37) var j: Int = 0
		while (j < 256) {
			@Pc(44) val randomIndex: Int = random.nextInt() and 0xFF
			@Pc(49) val swap: Int = this.permutations[randomIndex]
			this.permutations[randomIndex + 256] = this.permutations[j]
			this.permutations[randomIndex] = this.permutations[randomIndex + 256]
			this.permutations[j + 256] = swap
			this.permutations[j] = swap
			j++
		}
	}

	@OriginalMember(owner = "client!la", name = "a", descriptor = "(IIFFIFIFII[F)V")
	override fun generate(@OriginalArg(2) scaleX: Float, @OriginalArg(3) scaleZ: Float, @OriginalArg(4) offset: Int, @OriginalArg(5) amplitude: Float, @OriginalArg(7) scaleY: Float, @OriginalArg(9) layer: Int, @OriginalArg(10) output: FloatArray?) {
		var offset: Int = offset
		@Pc(13) val xRange: Int = (scaleX * 128.toFloat() - 1.0f).toInt()
		@Pc(17) val xMask: Int = xRange and 0xFF
		@Pc(25) val yRange: Int = (128.toFloat() * scaleY - 1.0f).toInt()
		@Pc(29) val yMask: Int = yRange and 0xFF
		@Pc(37) val zRange: Int = (16.toFloat() * scaleZ - 1.0f).toInt()
		@Pc(41) val zMask: Int = zRange and 0xFF
		@Pc(46) val layerZ: Float = layer.toFloat() * scaleZ
		@Pc(49) val z0: Int = layerZ.toInt()
		@Pc(53) val z1: Int = z0 + 1
		@Pc(58) val zFrac: Float = layerZ - z0.toFloat()
		@Pc(63) val zInvFrac: Float = 1.toFloat() - zFrac
		@Pc(67) val zFade: Float = PerlinNoiseGenerator.fade(zFrac)
		@Pc(71) val z1Wrapped: Int = z1 and zMask
		@Pc(75) val z0Wrapped: Int = z0 and zMask
		@Pc(80) val hash0: Int = this.permutations[z0Wrapped]
		@Pc(85) val hash1: Int = this.permutations[z1Wrapped]
		@Pc(87) var i: Int = 0
		while (i < 128) {
			@Pc(94) val yPos: Float = scaleY * i.toFloat()
			@Pc(97) val y0: Int = yPos.toInt()
			@Pc(101) val y1: Int = y0 + 1
			@Pc(107) val yFrac: Float = (-y0).toFloat() + yPos
			@Pc(112) val yInvFrac: Float = 1.0f - yFrac
			@Pc(118) val yFade: Float = PerlinNoiseGenerator.fade(yFrac)
			@Pc(122) val y0Wrapped: Int = y0 and yMask
			@Pc(126) val y1Wrapped: Int = y1 and yMask
			@Pc(133) val hash00: Int = this.permutations[hash0 + y0Wrapped]
			@Pc(140) val hash01: Int = this.permutations[hash0 + y1Wrapped]
			@Pc(147) val hash10: Int = this.permutations[y0Wrapped + hash1]
			@Pc(155) val hash11: Int = this.permutations[y1Wrapped + hash1]
			@Pc(157) var j: Int = 0
			while (j < 128) {
				@Pc(164) val xPos: Float = scaleX * j.toFloat()
				@Pc(167) val x0: Int = xPos.toInt()
				@Pc(171) val x1: Int = x0 + 1
				@Pc(176) val xFrac: Float = xPos - x0.toFloat()
				@Pc(180) val xInvFrac: Float = 1.0f - xFrac
				@Pc(184) val x0Wrapped: Int = x0 and xMask
				@Pc(188) val x1Wrapped: Int = x1 and xMask
				@Pc(194) val xFade: Float = PerlinNoiseGenerator.fade(xFrac)
				output!![offset++] = amplitude * PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.gradientDot(yInvFrac, this.permutations[hash00 + x0Wrapped] and 0x7, zInvFrac, xInvFrac), PerlinNoiseGenerator.gradientDot(yInvFrac, this.permutations[x1Wrapped + hash00] and 0x7, zInvFrac, xFrac), xFade), PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.gradientDot(yFrac, this.permutations[x0Wrapped + hash01] and 0x7, zInvFrac, xInvFrac), PerlinNoiseGenerator.gradientDot(yFrac, this.permutations[hash01 + x1Wrapped] and 0x7, zInvFrac, xFrac), xFade), yFade), PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.gradientDot(yInvFrac, this.permutations[hash10 + x0Wrapped] and 0x7, zFrac, xInvFrac), PerlinNoiseGenerator.gradientDot(yInvFrac, this.permutations[hash10 + x1Wrapped] and 0x7, zFrac, xFrac), xFade), PerlinNoiseGenerator.lerp(PerlinNoiseGenerator.gradientDot(yFrac, this.permutations[hash11 + x0Wrapped] and 0x7, zFrac, xInvFrac), PerlinNoiseGenerator.gradientDot(yFrac, this.permutations[hash11 + x1Wrapped] and 0x7, zFrac, xFrac), xFade), yFade), zFade)
				j++
			}
			i++
		}
	}

	companion object {
		@OriginalMember(owner = "client!ci", name = "a", descriptor = "(FZFF)F")
		@JvmStatic
		fun lerp(@OriginalArg(0) start: Float, @OriginalArg(2) end: Float, @OriginalArg(3) t: Float): Float {
			return start + (end - start) * t
		}

		@OriginalMember(owner = "client!eca", name = "a", descriptor = "(IF)F")
		@JvmStatic
		fun fade(@OriginalArg(1) t: Float): Float {
			return (t * (t * 6.0f - 15.0f) + 10.0f) * t * t * t
		}

		@OriginalMember(owner = "client!vi", name = "a", descriptor = "(FIFIF)F")
		@JvmStatic
		fun gradientDot(@OriginalArg(0) y: Float, @OriginalArg(1) hash: Int, @OriginalArg(2) z: Float, @OriginalArg(4) x: Float): Float {
			@Pc(17) val gradient: FloatArray = PerlinNoiseGenerator.PERLIN_GRADIENTS[hash]!!
			return gradient[1] * y + x * gradient[0] + gradient[2] * z
		}

		@OriginalMember(owner = "client!dw", name = "b", descriptor = "[[F")
		@JvmField
		val PERLIN_GRADIENTS: Array<FloatArray?> = arrayOf(floatArrayOf(-0.333333f, -0.333333f, -0.333333f), floatArrayOf(0.333333f, -0.333333f, -0.333333f), floatArrayOf(-0.333333f, 0.333333f, -0.333333f), floatArrayOf(0.333333f, 0.333333f, -0.333333f), floatArrayOf(-0.333333f, -0.333333f, 0.333333f), floatArrayOf(0.333333f, -0.333333f, 0.333333f), floatArrayOf(-0.333333f, 0.333333f, 0.333333f), floatArrayOf(0.333333f, 0.333333f, 0.333333f))
	}
}
