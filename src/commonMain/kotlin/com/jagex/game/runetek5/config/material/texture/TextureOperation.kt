package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpBinary
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpBoxBlur
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpBricks
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpBrightness
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpClamp
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpColorEdgeDetector
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpColorFill
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpColorGradient
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpColorize
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpCombine
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpConstant
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpCurve
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpDiagonalWaves
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpEmboss
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpGreyscale
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpHerringbone
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpHslAdjust
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpInterpolate
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpInvert
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpIrregularBricks
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpLineNoise
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpMandelbrot
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpMonochromeEdgeDetector
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpNoise
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPerlinNoise
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPolarDistortion
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpRange
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpSprite
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpSquareWaveform
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpTexture
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpTile
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpTiledSprite
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpVoronoiNoise
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpWaveform
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!tf")
abstract class TextureOperation : Node {

	@OriginalMember(owner = "client!tf", name = "u", descriptor = "Lclient!qk;")
	@JvmField
	var monochromeImageCache: MonochromeImageCache? = null

	@OriginalMember(owner = "client!tf", name = "v", descriptor = "Lclient!afa;")
	@JvmField
	var colorImageCache: ColorImageCache? = null

	@OriginalMember(owner = "client!tf", name = "y", descriptor = "I")
	@JvmField
	var imageCacheCapacity: Int = 0

	@OriginalMember(owner = "client!tf", name = "t", descriptor = "[Lclient!tf;")
	@JvmField
	val operations: Array<TextureOperation?>

	@OriginalMember(owner = "client!tf", name = "H", descriptor = "Z")
	@JvmField
	var monochrome: Boolean = false

	@OriginalMember(owner = "client!tf", name = "<init>", descriptor = "(IZ)V")
	constructor(@OriginalArg(0) size: Int, @OriginalArg(1) monochrome: Boolean) : super() {
		this.operations = arrayOfNulls<TextureOperation>(size)
		this.monochrome = monochrome
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(I)I")
	open fun getSpriteId(): Int {
		return -1
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(BII)[[I")
	fun getChildColorOutput(@OriginalArg(1) row: Int, @OriginalArg(2) childIndex: Int): Array<IntArray?>? {
		if (this.operations[childIndex]!!.monochrome) {
			@Pc(25) val monochromeOutput: IntArray? = this.operations[childIndex]!!.monochromeOutput(row)
			return arrayOf<IntArray?>(monochromeOutput, monochromeOutput, monochromeOutput)
		} else {
			return this.operations[childIndex]!!.getColorOutput(row)
		}
	}

	@OriginalMember(owner = "client!tf", name = "c", descriptor = "(II)[I")
	open fun monochromeOutput(@OriginalArg(0) row: Int): IntArray? {
		throw IllegalStateException("This operation does not have a monochrome output")
	}

	@OriginalMember(owner = "client!tf", name = "d", descriptor = "(I)I")
	open fun getTextureId(): Int {
		return -1
	}

	@OriginalMember(owner = "client!tf", name = "e", descriptor = "(I)V")
	open fun postDecode() {
	}

	@OriginalMember(owner = "client!tf", name = "b", descriptor = "(III)V")
	open fun initCache(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int) {
		@Pc(14) val size: Int = if (this.imageCacheCapacity == 255) width else this.imageCacheCapacity
		if (this.monochrome) {
			this.monochromeImageCache = MonochromeImageCache(size, width, height)
		} else {
			this.colorImageCache = ColorImageCache(size, width, height)
		}
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(B)V")
	open fun cacheReset() {
		if (this.monochrome) {
			this.monochromeImageCache!!.reset()
			this.monochromeImageCache = null
		} else {
			this.colorImageCache!!.reset()
			this.colorImageCache = null
		}
	}

	@OriginalMember(owner = "client!tf", name = "d", descriptor = "(II)[[I")
	open fun getColorOutput(@OriginalArg(0) row: Int): Array<IntArray?>? {
		throw IllegalStateException("This operation does not have a colour output")
	}

	@OriginalMember(owner = "client!tf", name = "c", descriptor = "(III)[I")
	fun getChildMonochromeOutput(@OriginalArg(0) row: Int, @OriginalArg(2) childIndex: Int): IntArray? {
		return if (this.operations[childIndex]!!.monochrome) this.operations[childIndex]!!.monochromeOutput(row) else this.operations[childIndex]!!.getColorOutput(row)!![0]
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(Lclient!cea;II)V")
	open fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
	}

	companion object {
		@OriginalMember(owner = "client!hha", name = "a", descriptor = "(IB)Lclient!tf;")
		@JvmStatic
		fun implementationForType(@OriginalArg(0) type: Int): TextureOperation? {
			if (type == 0) {
				return TextureOpConstant()
			} else if (type == 1) {
				return TextureOpColorFill()
			} else if (type == 2) {
				return TextureOpHorizontalGradient()
			} else if (type == 3) {
				return TextureOpVerticalGradient()
			} else if (type == 4) {
				return TextureOpBricks()
			} else if (type == 5) {
				return TextureOpBoxBlur()
			} else if (type == 6) {
				return TextureOpClamp()
			} else if (type == 7) {
				return TextureOpCombine()
			} else if (type == 8) {
				return TextureOpCurve()
			} else if (type == 9) {
				return TextureOpFlip()
			} else if (type == 10) {
				return TextureOpColorGradient()
			} else if (type == 11) {
				return TextureOpColorize()
			} else if (type == 12) {
				return TextureOpWaveform()
			} else if (type == 13) {
				return TextureOpNoise()
			} else if (type == 14) {
				return TextureOpWeave()
			} else if (type == 15) {
				return TextureOpVoronoiNoise()
			} else if (type == 16) {
				return TextureOpHerringbone()
			} else if (type == 17) {
				return TextureOpHslAdjust()
			} else if (type == 18) {
				return TextureOpTiledSprite()
			} else if (type == 19) {
				return TextureOpPolarDistortion()
			} else if (type == 20) {
				return TextureOpTile()
			} else if (type == 21) {
				return TextureOpInterpolate()
			} else if (type == 22) {
				return TextureOpInvert()
			} else if (type == 23) {
				return TextureOpKaleidoscope()
			} else if (type == 24) {
				return TextureOpGreyscale()
			} else if (type == 25) {
				return TextureOpBrightness()
			} else if (type == 26) {
				return TextureOpBinary()
			} else if (type == 27) {
				return TextureOpSquareWaveform()
			} else if (type == 28) {
				return TextureOpIrregularBricks()
			} else if (type == 29) {
				return TextureOpRasterizer()
			} else if (type == 30) {
				return TextureOpRange()
			} else if (type == 31) {
				return TextureOpMandelbrot()
			} else if (type == 32) {
				return TextureOpEmboss()
			} else if (type == 33) {
				return TextureOpColorEdgeDetector()
			} else if (type == 34) {
				return TextureOpPerlinNoise()
			} else if (type == 35) {
				return TextureOpMonochromeEdgeDetector()
			} else if (type == 36) {
				return TextureOpTexture()
			} else if (type == 37) {
				return TextureOpDiagonalWaves()
			} else if (type == 38) {
				return TextureOpLineNoise()
			} else if (type == 39) {
				return TextureOpSprite()
			} else {
				return null
			}
		}

		@OriginalMember(owner = "client!vc", name = "e", descriptor = "I")
		@JvmField
		var columnEnd: Int = 0

		@OriginalMember(owner = "client!di", name = "n", descriptor = "I")
		@JvmField
		var currentHeight: Int = 0
	}
}
