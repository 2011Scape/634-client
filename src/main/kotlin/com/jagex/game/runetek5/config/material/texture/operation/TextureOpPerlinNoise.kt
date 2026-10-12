package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.EnvironmentContext
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import com.jagex.game.runetek5.config.material.texture.TextureRaster
import com.jagex.game.runetek5.utils.ArrayUtils

@OriginalClass("client!jq")
class TextureOpPerlinNoise : TextureOperation {

	@OriginalMember(owner = "client!jq", name = "T", descriptor = "[S")
	private var stepMultipliers: ShortArray? = null

	@OriginalMember(owner = "client!jq", name = "W", descriptor = "[S")
	private var amplitudeMultipliers: ShortArray? = null

	@OriginalMember(owner = "client!jq", name = "J", descriptor = "I")
	@JvmField
	var noiseAmplitude: Int = 1638

	@OriginalMember(owner = "client!jq", name = "M", descriptor = "[B")
	private var noiseArray: ByteArray? = ByteArray(512)

	@OriginalMember(owner = "client!jq", name = "U", descriptor = "Z")
	@JvmField
	var useFogEffect: Boolean = true

	@OriginalMember(owner = "client!jq", name = "K", descriptor = "I")
	@JvmField
	var numNoiseSteps: Int = 4

	@OriginalMember(owner = "client!jq", name = "Q", descriptor = "I")
	@JvmField
	var randomSeed: Int = 0

	@OriginalMember(owner = "client!jq", name = "Y", descriptor = "I")
	@JvmField
	var texCoordScaleY: Int = 4

	@OriginalMember(owner = "client!jq", name = "S", descriptor = "I")
	@JvmField
	var texCoordScaleX: Int = 4

	@OriginalMember(owner = "client!jq", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!jq", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.useFogEffect = packet!!.readUByte() == 1
		} else if (opcode == 1) {
			this.numNoiseSteps = packet!!.readUByte()
		} else if (opcode == 2) {
			this.noiseAmplitude = packet!!.readShort()
			if (this.noiseAmplitude < 0) {
				this.stepMultipliers = ShortArray(this.numNoiseSteps)
				@Pc(70) var i: Int = 0
				while (i < this.numNoiseSteps) {
					this.stepMultipliers!![i] = packet.readShort().toShort()
					i++
				}
			}
		} else if (opcode == 3) {
			val scale: Int = packet!!.readUByte()
			this.texCoordScaleY = scale
			this.texCoordScaleX = scale
		} else if (opcode == 4) {
			this.randomSeed = packet!!.readUByte()
		} else if (opcode == 5) {
			this.texCoordScaleX = packet!!.readUByte()
		} else if (opcode == 6) {
			this.texCoordScaleY = packet!!.readUByte()
		}
	}

	@OriginalMember(owner = "client!jq", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) y: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(y)!!
		if (this.monochromeImageCache!!.dirty) {
			this.computeNoiseRow(y, output)
		}
		return output
	}

	@OriginalMember(owner = "client!jq", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		this.noiseArray = ArrayUtils.getPermutationTable(this.randomSeed)
		this.computeMultipliers()
		@Pc(19) var i: Int = this.numNoiseSteps - 1
		while (i >= 1) {
			@Pc(31) val stepMultiplier: Short = this.stepMultipliers!![i]
			if (stepMultiplier > 8) {
				return
			}
			if (stepMultiplier < -8) {
				return
			}
			this.numNoiseSteps--
			i--
		}
	}

	@OriginalMember(owner = "client!jq", name = "b", descriptor = "(B)V")
	private fun computeMultipliers() {
		@Pc(27) var i: Int
		if (this.noiseAmplitude > 0) {
			this.amplitudeMultipliers = ShortArray(this.numNoiseSteps)
			this.stepMultipliers = ShortArray(this.numNoiseSteps)
			i = 0
			while (i < this.numNoiseSteps) {
				this.stepMultipliers!![i] = (Math.pow((this.noiseAmplitude.toFloat() / 4096.0f).toDouble(), i.toDouble()) * 4096.0).toInt().toShort()
				this.amplitudeMultipliers!![i] = Math.pow(2.0, i.toDouble()).toInt().toShort()
				i++
			}
		} else if (this.stepMultipliers != null && this.numNoiseSteps == this.stepMultipliers!!.size) {
			this.amplitudeMultipliers = ShortArray(this.numNoiseSteps)
			i = 0
			while (i < this.numNoiseSteps) {
				this.amplitudeMultipliers!![i] = Math.pow(2.0, i.toDouble()).toInt().toShort()
				i++
			}
		}
	}

	@OriginalMember(owner = "client!jq", name = "a", descriptor = "(I[IB)V")
	fun computeNoiseRow(@OriginalArg(0) y: Int, @OriginalArg(1) pixels: IntArray?) {
		@Pc(12) val scaledY: Int = TextureRaster.verticalTable!![y] * this.texCoordScaleY
		@Pc(112) var scaledX: Int
		@Pc(126) var noiseValue: Int
		@Pc(42) var octaveScale: Int
		@Pc(27) var stepMultiplier: Short
		@Pc(103) var x: Int
		@Pc(48) var local48: Int
		@Pc(55) var rangeX: Int
		@Pc(62) var rangeY: Int
		@Pc(101) var lerpY: Int
		@Pc(66) var gridY: Int
		@Pc(70) var nextGridY: Int
		@Pc(97) var noiseRowA: Int
		@Pc(88) var noiseRowB: Int
		if (this.numNoiseSteps == 1) {
			octaveScale = this.amplitudeMultipliers!![0].toInt() shl 12
			stepMultiplier = this.stepMultipliers!![0]
			val octaveY: Int = (octaveScale * scaledY) shr 12
			rangeX = (octaveScale * this.texCoordScaleX) shr 12
			rangeY = (this.texCoordScaleY * octaveScale) shr 12
			gridY = octaveY shr 12
			nextGridY = gridY + 1
			@Pc(376) val fracY: Int = octaveY and 0xFFF
			if (nextGridY >= rangeY) {
				nextGridY = 0
			}
			noiseRowB = this.noiseArray!![nextGridY and 0xFF].toInt() and 0xFF
			lerpY = EnvironmentContext.FADE_TABLE!![fracY]
			noiseRowA = this.noiseArray!![gridY and 0xFF].toInt() and 0xFF
			if (this.useFogEffect) {
				x = 0
				while (x < EnvironmentLight.textureOpWidth) {
					scaledX = this.texCoordScaleX * EnvironmentLight.horizontalTable!![x]
					noiseValue = this.computeNoise(noiseRowB, fracY, rangeX, (scaledX * octaveScale) shr 12, lerpY, noiseRowA)
					@Pc(475) val weightedNoise: Int = (stepMultiplier * noiseValue) shr 12
					pixels!![x] = (weightedNoise shr 1) + 2048
					x++
				}
			} else {
				x = 0
				while (x < EnvironmentLight.textureOpWidth) {
					scaledX = this.texCoordScaleX * EnvironmentLight.horizontalTable!![x]
					noiseValue = this.computeNoise(noiseRowB, fracY, rangeX, (octaveScale * scaledX) shr 12, lerpY, noiseRowA)
					pixels!![x] = (noiseValue * stepMultiplier) shr 12
					x++
				}
			}
			return
		}
		stepMultiplier = this.stepMultipliers!![0]
		if (stepMultiplier > 8 || stepMultiplier < -8) {
			octaveScale = this.amplitudeMultipliers!![0].toInt() shl 12
			var octaveY: Int = (octaveScale * scaledY) shr 12
			rangeX = (this.texCoordScaleX * octaveScale) shr 12
			rangeY = (this.texCoordScaleY * octaveScale) shr 12
			gridY = octaveY shr 12
			nextGridY = gridY + 1
			if (nextGridY >= rangeY) {
				nextGridY = 0
			}
			octaveY = octaveY and 0xFFF
			noiseRowB = this.noiseArray!![nextGridY and 0xFF].toInt() and 0xFF
			noiseRowA = this.noiseArray!![gridY and 0xFF].toInt() and 0xFF
			lerpY = EnvironmentContext.FADE_TABLE!![octaveY]
			x = 0
			while (x < EnvironmentLight.textureOpWidth) {
				scaledX = EnvironmentLight.horizontalTable!![x] * this.texCoordScaleX
				noiseValue = this.computeNoise(noiseRowB, octaveY, rangeX, (octaveScale * scaledX) shr 12, lerpY, noiseRowA)
				pixels!![x] = (stepMultiplier * noiseValue) shr 12
				x++
			}
		}
		@Pc(140) var i: Int = 1
		while (i < this.numNoiseSteps) {
			stepMultiplier = this.stepMultipliers!![i]
			if (stepMultiplier > 8 || stepMultiplier < -8) {
				octaveScale = this.amplitudeMultipliers!![i].toInt() shl 12
				var octaveY: Int = (octaveScale * scaledY) shr 12
				rangeX = (this.texCoordScaleX * octaveScale) shr 12
				rangeY = (this.texCoordScaleY * octaveScale) shr 12
				gridY = octaveY shr 12
				nextGridY = gridY + 1
				octaveY = octaveY and 0xFFF
				if (nextGridY >= rangeY) {
					nextGridY = 0
				}
				noiseRowA = this.noiseArray!![gridY and 0xFF].toInt() and 0xFF
				lerpY = EnvironmentContext.FADE_TABLE!![octaveY]
				noiseRowB = this.noiseArray!![nextGridY and 0xFF].toInt() and 0xFF
				if (this.useFogEffect && this.numNoiseSteps - 1 == i) {
					x = 0
					while (x < EnvironmentLight.textureOpWidth) {
						scaledX = EnvironmentLight.horizontalTable!![x] * this.texCoordScaleX
						noiseValue = this.computeNoise(noiseRowB, octaveY, rangeX, (scaledX * octaveScale) shr 12, lerpY, noiseRowA)
						noiseValue = pixels!![x] + ((noiseValue * stepMultiplier) shr 12)
						pixels[x] = (noiseValue shr 1) + 2048
						x++
					}
				} else {
					x = 0
					while (x < EnvironmentLight.textureOpWidth) {
						scaledX = this.texCoordScaleX * EnvironmentLight.horizontalTable!![x]
						noiseValue = this.computeNoise(noiseRowB, octaveY, rangeX, (scaledX * octaveScale) shr 12, lerpY, noiseRowA)
						pixels!![x] += (stepMultiplier * noiseValue) shr 12
						x++
					}
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!jq", name = "a", descriptor = "(IIIIIZI)I")
	private fun computeNoise(@OriginalArg(0) noiseRowB: Int, @OriginalArg(1) fracY: Int, @OriginalArg(2) rangeX: Int, @OriginalArg(3) coordX: Int, @OriginalArg(4) lerpY: Int, @OriginalArg(6) noiseRowA: Int): Int {
		@Pc(9) val cellX: Int = coordX shr 12
		@Pc(13) var nextCellX: Int = cellX + 1
		@Pc(17) val cellIndexX: Int = cellX and 0xFF
		@Pc(21) val fracX: Int = coordX and 0xFFF
		if (nextCellX >= rangeX) {
			nextCellX = 0
		}
		nextCellX = nextCellX and 0xFF
		@Pc(38) val negFracX: Int = fracX - 4096
		@Pc(42) val negFracY: Int = fracY - 4096
		@Pc(51) var gradientType: Int = this.noiseArray!![noiseRowA + cellIndexX].toInt() and 0x3
		@Pc(55) val lerpX: Int = EnvironmentContext.FADE_TABLE!![fracX]
		@Pc(73) var gradientA: Int
		if (gradientType > 1) {
			gradientA = if (gradientType == 2) fracX - fracY else -fracY + -fracX
		} else {
			gradientA = if (gradientType == 0) fracX + fracY else fracY - fracX
		}
		gradientType = this.noiseArray!![noiseRowA + nextCellX].toInt() and 0x3
		@Pc(113) var gradientB: Int
		if (gradientType <= 1) {
			gradientB = if (gradientType == 0) negFracX + fracY else fracY + -negFracX
		} else {
			gradientB = if (gradientType == 2) negFracX - fracY else -fracY + -negFracX
		}
		gradientType = this.noiseArray!![noiseRowB + cellIndexX].toInt() and 0x3
		@Pc(155) val lerpRowA: Int = gradientA + ((lerpX * (gradientB - gradientA)) shr 12)
		if (gradientType > 1) {
			gradientA = if (gradientType == 2) fracX - negFracY else -fracX - negFracY
		} else {
			gradientA = if (gradientType == 0) fracX + negFracY else -fracX + negFracY
		}
		gradientType = this.noiseArray!![noiseRowB + nextCellX].toInt() and 0x3
		if (gradientType > 1) {
			gradientB = if (gradientType == 2) negFracX - negFracY else -negFracX - negFracY
		} else {
			gradientB = if (gradientType == 0) negFracX + negFracY else negFracY + -negFracX
		}
		@Pc(239) val lerpRowB: Int = (((gradientB - gradientA) * lerpX) shr 12) + gradientA
		return lerpRowA + ((lerpY * (lerpRowB - lerpRowA)) shr 12)
	}

	companion object {
		@OriginalMember(owner = "client!al", name = "a", descriptor = "(BIIIFIZI)[I")
		@JvmStatic
		fun generateNoiseRow(@OriginalArg(4) amplitude: Float): IntArray {
			@Pc(6) val noiseRow: IntArray = IntArray(2048)
			@Pc(10) val textureOp34: TextureOpPerlinNoise = TextureOpPerlinNoise()
			textureOp34.noiseAmplitude = (amplitude * 4096.0f).toInt()
			textureOp34.texCoordScaleY = 8
			textureOp34.numNoiseSteps = 4
			textureOp34.useFogEffect = true
			textureOp34.texCoordScaleX = 8
			textureOp34.randomSeed = 35
			textureOp34.postDecode()
			TextureRaster.setDimensions(1, 2048)
			textureOp34.computeNoiseRow(0, noiseRow)
			return noiseRow
		}
	}
}
