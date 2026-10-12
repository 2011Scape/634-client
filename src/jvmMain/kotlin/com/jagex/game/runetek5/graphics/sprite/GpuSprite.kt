package com.jagex.game.runetek5.graphics.sprite

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.NativeMatrix
import com.jagex.game.runetek5.toolkit.MaskedTextureSurface
import com.jagex.game.runetek5.graphics.texture.sampler.SampleMode
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.TextureCombineFunction
import com.jagex.game.runetek5.FilterMode
import com.jagex.game.runetek5.TextureTransformMode
import com.jagex.game.runetek5.PixelFormat

@OriginalClass("client!jd")
class GpuSprite : NativeSprite {

	@OriginalMember(owner = "client!jd", name = "h", descriptor = "I")
	private var trimBottom: Int = 0

	@OriginalMember(owner = "client!jd", name = "A", descriptor = "I")
	private var trimRight: Int = 0

	@OriginalMember(owner = "client!jd", name = "D", descriptor = "I")
	private var translateY: Int = 0

	@OriginalMember(owner = "client!jd", name = "C", descriptor = "Z")
	private var stretched: Boolean = false

	@OriginalMember(owner = "client!jd", name = "k", descriptor = "I")
	private var translateX: Int = 0

	@OriginalMember(owner = "client!jd", name = "B", descriptor = "I")
	private val height: Int

	@OriginalMember(owner = "client!jd", name = "v", descriptor = "I")
	private val width: Int

	@OriginalMember(owner = "client!jd", name = "r", descriptor = "Lclient!wga;")
	private val nativeToolkit: AbstractNativeToolkit

	@OriginalMember(owner = "client!jd", name = "n", descriptor = "Lclient!wfa;")
	private val texture: GpuTexture?

	@OriginalMember(owner = "client!jd", name = "u", descriptor = "Z")
	private val widthPadded: Boolean

	@OriginalMember(owner = "client!jd", name = "s", descriptor = "Z")
	private val heightPadded: Boolean

	@OriginalMember(owner = "client!jd", name = "g", descriptor = "Z")
	private val repeatX: Boolean

	@OriginalMember(owner = "client!jd", name = "b", descriptor = "Z")
	private val repeatY: Boolean

	@OriginalMember(owner = "client!jd", name = "<init>", descriptor = "(Lclient!wga;IIZ)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) hasAlpha: Boolean) : super() {
		this.height = height
		this.width = width
		this.nativeToolkit = nativeToolkit
		this.texture = nativeToolkit.createNativeTexture2D(height, width, GLFormat.RGBA, if (hasAlpha) GLFormat.HDR else PixelFormat.aClass375_38)
		this.texture!!.setWarp(true, true)
		this.widthPadded = this.texture!!.getTextureWidth() != width
		this.heightPadded = this.texture!!.getTextureHeight() != height
		this.repeatX = !this.widthPadded && this.texture!!.supportsTextureRepeat()
		this.repeatY = !this.heightPadded && this.texture!!.supportsTextureRepeat()
	}

	@OriginalMember(owner = "client!jd", name = "<init>", descriptor = "(Lclient!wga;II[III)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) pixels: IntArray?, @OriginalArg(4) pixelOffset: Int, @OriginalArg(5) rowLength: Int) : super() {
		this.nativeToolkit = nativeToolkit
		this.width = width
		this.height = height
		this.texture = nativeToolkit.createTextureSampler(width, pixels, false, pixelOffset, rowLength, height)!!
		this.texture!!.setWarp(true, true)
		this.widthPadded = this.texture!!.getTextureWidth() != width
		this.heightPadded = this.texture!!.getTextureHeight() != height
		this.repeatX = !this.widthPadded && this.texture!!.supportsTextureRepeat()
		this.repeatY = !this.heightPadded && this.texture!!.supportsTextureRepeat()
	}

	@OriginalMember(owner = "client!jd", name = "d", descriptor = "()I")
	override fun scaleHeight(): Int {
		return this.height + this.translateY + this.trimBottom
	}

	@OriginalMember(owner = "client!jd", name = "c", descriptor = "(IIII)V")
	override fun setOffsets(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int) {
		this.translateY = y1
		this.trimBottom = y2
		this.translateX = x1
		this.trimRight = x2
		this.stretched = this.translateX != 0 || this.translateY != 0 || this.trimRight != 0 || this.trimBottom != 0
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIII[III)V")
	private fun updateTextureRegion(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) pixels: IntArray?, @OriginalArg(6) rowLength: Int) {
		this.texture!!.updateTextureRegion(rowLength, pixels, x, width, y, height)
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "()I")
	override fun scaleWidth(): Int {
		return this.trimRight + this.translateX + this.width
	}

	@OriginalMember(owner = "client!jd", name = "b", descriptor = "()I")
	override fun getHeight(): Int {
		return this.height
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(FFFFFFIIII)V")
	override fun method6201(@OriginalArg(0) topLeftX: Float, @OriginalArg(1) topLeftY: Float, @OriginalArg(2) topRightX: Float, @OriginalArg(3) topRightY: Float, @OriginalArg(4) bottomLeftX: Float, @OriginalArg(5) bottomLeftY: Float, @OriginalArg(6) combineMode: Int, @OriginalArg(7) colorArgb: Int) {
		var topLeftX: Float = topLeftX
		var topLeftY: Float = topLeftY
		var topRightX: Float = topRightX
		var topRightY: Float = topRightY
		var bottomLeftX: Float = bottomLeftX
		var bottomLeftY: Float = bottomLeftY
		@Pc(8) val modelMatrix = this.nativeToolkit.getModelMatrix()
		@Pc(13) val textureMatrix = this.nativeToolkit.getCurrentTextureMatrix()
		this.texture!!.setFilterMode(if (this.widthPadded || this.heightPadded) FilterMode.aClass298_10 else SampleMode.LINEAR)
		this.nativeToolkit.setupUiRenderState()
		this.nativeToolkit.setTextureSampler(this.texture)
		this.nativeToolkit.toggleBlending(1)
		this.nativeToolkit.setColourType(combineMode)
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit.setColourValue(colorArgb)
		if (this.stretched) {
			@Pc(77) val spriteWidth: Float = this.scaleWidth().toFloat()
			@Pc(81) val spriteHeight: Float = this.scaleHeight().toFloat()
			@Pc(88) val uX: Float = (topRightX - topLeftX) / spriteWidth
			@Pc(95) val uY: Float = (topRightY - topLeftY) / spriteWidth
			@Pc(101) val vX: Float = (bottomLeftX - topLeftX) / spriteHeight
			@Pc(107) val vY: Float = (bottomLeftY - topLeftY) / spriteHeight
			@Pc(113) val padTopX: Float = this.translateY.toFloat() * vX
			@Pc(119) val padTopY: Float = this.translateY.toFloat() * vY
			@Pc(125) val padLeftX: Float = uX * this.translateX.toFloat()
			@Pc(131) val padLeftY: Float = uY * this.translateX.toFloat()
			@Pc(138) val trimRightX: Float = -uX * this.trimRight.toFloat()
			@Pc(145) val trimRightY: Float = -uY * this.trimRight.toFloat()
			@Pc(152) val trimBottomX: Float = -vX * this.trimBottom.toFloat()
			@Pc(159) val trimBottomY: Float = this.trimBottom.toFloat() * -vY
			topLeftX = padTopX + topLeftX + padLeftX
			bottomLeftX = padLeftX + bottomLeftX + trimBottomX
			topRightY = topRightY + trimRightY + padTopY
			topRightX = padTopX + topRightX + trimRightX
			topLeftY = padTopY + topLeftY + padLeftY
			bottomLeftY = bottomLeftY + padLeftY + trimBottomY
		}
		modelMatrix.setRotation(1.0f, topRightY - topLeftY, -topLeftX + bottomLeftX, 0.0f, 0.0f, bottomLeftY - topLeftY, 0.0f, 0.0f, topRightX - topLeftX)
		modelMatrix.translate(topLeftY, 0.0f, topLeftX)
		textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(this.height.toFloat()), this.texture!!.toTextureCoordS(this.width.toFloat()))
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
		this.nativeToolkit.commitModelMatrix()
		this.nativeToolkit.drawQuadTriangleFan()
		this.nativeToolkit.resetCurrentTextureMatrix()
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS)
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS)
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIIIIII)V")
	override fun drawScaledColored(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) op: Int, @OriginalArg(5) colour: Int, @OriginalArg(6) mode: Int) {
		@Pc(10) val modelMatrix = this.nativeToolkit.getModelMatrix()
		@Pc(15) val textureMatrix = this.nativeToolkit.getCurrentTextureMatrix()
		this.texture!!.setFilterMode(FilterMode.aClass298_10)
		this.nativeToolkit.setupUiRenderState()
		this.nativeToolkit.setTextureSampler(this.texture)
		this.nativeToolkit.toggleBlending(mode)
		this.nativeToolkit.setColourType(op)
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit.setColourValue(colour)
		@Pc(73) val tileVertically: Boolean = this.repeatY && this.translateY == 0 && this.trimBottom == 0
		@Pc(92) val tileHorizontally: Boolean = this.repeatX && this.translateX == 0 && this.trimRight == 0
		if (tileHorizontally and tileVertically) {
			textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(height.toFloat()), this.texture!!.toTextureCoordS(width.toFloat()))
			modelMatrix.setScale(0.0f, height.toFloat(), width.toFloat())
			modelMatrix.translate(x, y, 0)
			this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
			this.nativeToolkit.commitModelMatrix()
			this.nativeToolkit.drawQuadTriangleFan()
		} else {
			@Pc(102) val end: Int
			@Pc(105) val local105: Int
			@Pc(132) val local132: Int
			@Pc(138) val local138: Int
			@Pc(184) val local184: Int
			if (tileHorizontally) {
				end = height + y
				val stepY: Int = this.scaleHeight()
				textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(this.height.toFloat()), this.texture!!.toTextureCoordS(width.toFloat()))
				this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
				var tileY: Int = this.translateY + y
				var tileEndY: Int = tileY + this.height
				while (tileEndY <= end) {
					modelMatrix.setScale(0.0f, this.height.toFloat(), width.toFloat())
					modelMatrix.translate(x, tileY, 0)
					this.nativeToolkit.commitModelMatrix()
					tileEndY += stepY
					tileY += stepY
					this.nativeToolkit.drawQuadTriangleFan()
				}
				if (tileY < end) {
					val remainingHeight: Int = end - tileY
					textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(remainingHeight.toFloat()), this.texture!!.toTextureCoordS(width.toFloat()))
					this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
					modelMatrix.setScale(0.0f, remainingHeight.toFloat(), width.toFloat())
					modelMatrix.translate(x, tileY, 0)
					this.nativeToolkit.commitModelMatrix()
					this.nativeToolkit.drawQuadTriangleFan()
				}
			} else if (tileVertically) {
				end = x + width
				val stepX: Int = this.scaleWidth()
				textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(height.toFloat()), this.texture!!.toTextureCoordS(this.width.toFloat()))
				this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
				var tileX: Int = x + this.translateX
				var tileEndX: Int = this.width + tileX
				while (tileEndX <= end) {
					modelMatrix.setScale(0.0f, height.toFloat(), this.width.toFloat())
					modelMatrix.translate(tileX, y, 0)
					this.nativeToolkit.commitModelMatrix()
					tileX += stepX
					this.nativeToolkit.drawQuadTriangleFan()
					tileEndX += stepX
				}
				if (tileX < end) {
					val remainingWidth: Int = end - tileX
					textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(height.toFloat()), this.texture!!.toTextureCoordS(remainingWidth.toFloat()))
					this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
					modelMatrix.setScale(0.0f, height.toFloat(), remainingWidth.toFloat())
					modelMatrix.translate(tileX, y, 0)
					this.nativeToolkit.commitModelMatrix()
					this.nativeToolkit.drawQuadTriangleFan()
				}
			} else {
				end = height + y
				val endX: Int = x + width
				val stepX: Int = this.scaleWidth()
				val stepY: Int = this.scaleHeight()
				var tileY: Int = this.translateY + y
				@Pc(254) var tileEndY: Int = tileY + this.height
				@Pc(284) val local284: Int
				@Pc(290) val local290: Int
				@Pc(338) val local338: Int
				while (end >= tileEndY) {
					textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(this.height.toFloat()), this.texture!!.toTextureCoordS(this.width.toFloat()))
					this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
					var tileX: Int = this.translateX + x
					var tileEndX: Int = tileX + this.width
					while (tileEndX <= endX) {
						modelMatrix.setScale(0.0f, this.height.toFloat(), this.width.toFloat())
						modelMatrix.translate(tileX, tileY, 0)
						this.nativeToolkit.commitModelMatrix()
						tileX += stepX
						this.nativeToolkit.drawQuadTriangleFan()
						tileEndX += stepX
					}
					if (endX > tileX) {
						val remainingWidth: Int = endX - tileX
						textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(this.height.toFloat()), this.texture!!.toTextureCoordS(remainingWidth.toFloat()))
						this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
						modelMatrix.setScale(0.0f, this.height.toFloat(), remainingWidth.toFloat())
						modelMatrix.translate(tileX, tileY, 0)
						this.nativeToolkit.commitModelMatrix()
						this.nativeToolkit.drawQuadTriangleFan()
					}
					tileEndY += stepY
					tileY += stepY
				}
				if (end > tileY) {
					val remainingHeight: Int = end - tileY
					textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(remainingHeight.toFloat()), this.texture!!.toTextureCoordS(this.width.toFloat()))
					this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
					var tileX: Int = this.translateX + x
					var tileEndX: Int = this.width + tileX
					while (endX >= tileEndX) {
						modelMatrix.setScale(0.0f, remainingHeight.toFloat(), this.width.toFloat())
						modelMatrix.translate(tileX, tileY, 0)
						this.nativeToolkit.commitModelMatrix()
						tileX += stepX
						tileEndX += stepX
						this.nativeToolkit.drawQuadTriangleFan()
					}
					if (tileX < endX) {
						@Pc(483) val remainingWidth: Int = endX - tileX
						textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(remainingHeight.toFloat()), this.texture!!.toTextureCoordS(remainingWidth.toFloat()))
						this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
						modelMatrix.setScale(0.0f, remainingHeight.toFloat(), remainingWidth.toFloat())
						modelMatrix.translate(tileX, tileY, 0)
						this.nativeToolkit.commitModelMatrix()
						this.nativeToolkit.drawQuadTriangleFan()
					}
				}
			}
		}
		this.nativeToolkit.resetCurrentTextureMatrix()
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS)
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS)
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(FFFFFFILclient!aa;II)V")
	override fun renderParallelogramImpl(@OriginalArg(0) topLeftX: Float, @OriginalArg(1) topLeftY: Float, @OriginalArg(2) topRightX: Float, @OriginalArg(3) topRightY: Float, @OriginalArg(4) bottomLeftX: Float, @OriginalArg(5) bottomLeftY: Float, @OriginalArg(7) mask: ComponentSurface?, @OriginalArg(8) maskX: Int, @OriginalArg(9) maskY: Int) {
		@Pc(8) val modelMatrix = this.nativeToolkit.getModelMatrix()
		@Pc(13) val textureMatrix = this.nativeToolkit.getCurrentTextureMatrix()
		@Pc(16) val maskSurface: MaskedTextureSurface? = mask as MaskedTextureSurface?
		@Pc(19) val gpuTexture = maskSurface!!.nativeTextureSampler
		this.texture!!.setFilterMode(if (this.widthPadded || this.heightPadded) FilterMode.aClass298_10 else SampleMode.LINEAR)
		this.nativeToolkit.setupUiRenderState()
		this.nativeToolkit.setTextureSampler(this.texture)
		this.nativeToolkit.toggleBlending(1)
		this.nativeToolkit.setColourType(1)
		if (this.stretched) {
			@Pc(67) val scaleX: Float = this.width.toFloat() / this.scaleWidth().toFloat()
			@Pc(75) val scaleY: Float = this.height.toFloat() / this.scaleHeight().toFloat()
			modelMatrix.setRotation(1.0f, scaleX * (topRightY - topLeftY), scaleY * (bottomLeftX - topLeftX), 0.0f, 0.0f, scaleY * (bottomLeftY - topLeftY), 0.0f, 0.0f, (topRightX - topLeftX) * scaleX)
			modelMatrix.translate((this.translateY.toFloat() + topLeftY) * scaleY, 0.0f, (this.translateX.toFloat() + topLeftX) * scaleX)
		} else {
			modelMatrix.setRotation(1.0f, topRightY - topLeftY, bottomLeftX - topLeftX, 0.0f, 0.0f, bottomLeftY - topLeftY, 0.0f, 0.0f, topRightX - topLeftX)
			modelMatrix.translate(topLeftY, 0.0f, topLeftX)
		}
		textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(this.height.toFloat()), this.texture!!.toTextureCoordS(this.width.toFloat()))
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
		this.nativeToolkit.setTextureSlot(1)
		this.nativeToolkit.setTextureSampler(gpuTexture)
		this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_4)
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS)
		@Pc(203) val maskTextureMatrix = this.nativeToolkit.getCurrentTextureMatrix()
		maskTextureMatrix!!.apply(modelMatrix)
		maskTextureMatrix!!.translate(-maskX, -maskY, 0)
		maskTextureMatrix!!.scale(gpuTexture!!.toTextureCoordT(1.0f), 1.0f, gpuTexture!!.toTextureCoordS(1.0f))
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
		this.nativeToolkit.commitModelMatrix()
		this.nativeToolkit.drawQuadTriangleFan()
		this.nativeToolkit.resetCurrentTextureMatrix()
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
		this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
		this.nativeToolkit.setTextureSampler(null)
		this.nativeToolkit.setTextureSlot(0)
		this.nativeToolkit.resetCurrentTextureMatrix()
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(III)V")
	override fun copyAlpha(@OriginalArg(2) channel: Int) {
		@Pc(15) val screenPixels: IntArray? = this.nativeToolkit.na(0, 0, this.width, this.height)
		@Pc(22) val pixels: IntArray = IntArray(this.height * this.width)
		this.texture!!.readPixels(pixels, this.width, this.height)
		@Pc(41) var y: Int
		@Pc(48) var rowOffset: Int
		@Pc(50) var x: Int
		if (channel == 0) {
			y = 0
			while (y < this.height) {
				rowOffset = y * this.width
				x = 0
				while (x < this.width) {
					pixels[rowOffset + x] = ((screenPixels!![rowOffset + x] and 0x6CFF0000) shl 8) or (pixels[rowOffset + x] and 0xFFFFFF)
					x++
				}
				y++
			}
		} else if (channel == 3) {
			y = 0
			while (y < this.height) {
				rowOffset = y * this.width
				x = 0
				while (x < this.width) {
					pixels[x + rowOffset] = (if (screenPixels!![x + rowOffset] == 0) 0 else -16777216) or (pixels[x + rowOffset] and 0xFFFFFF)
					x++
				}
				y++
			}
		}
		this.updateTextureRegion(0, 0, this.width, this.height, pixels, this.width)
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIIIIIII)V")
	override fun renderImpl(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) combineMode: Int, @OriginalArg(5) colorArgb: Int, @OriginalArg(6) blendMode: Int) {
		var x: Int = x
		var y: Int = y
		var width: Int = width
		var height: Int = height
		@Pc(8) val modelMatrix = this.nativeToolkit.getModelMatrix()
		@Pc(13) val textureMatrix = this.nativeToolkit.getCurrentTextureMatrix()
		this.texture!!.setFilterMode(if (this.widthPadded || this.heightPadded) FilterMode.aClass298_10 else SampleMode.LINEAR)
		this.nativeToolkit.setupUiRenderState()
		this.nativeToolkit.setTextureSampler(this.texture)
		this.nativeToolkit.toggleBlending(blendMode)
		this.nativeToolkit.setColourType(combineMode)
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit.setColourValue(colorArgb)
		textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(this.height.toFloat()), this.texture!!.toTextureCoordS(this.width.toFloat()))
		if (this.stretched) {
			width = width * this.width / this.scaleWidth()
			height = this.height * height / this.scaleHeight()
			x += this.translateX * width / this.width
			y += this.translateY * height / this.height
		}
		modelMatrix.setScale(0.0f, height.toFloat(), width.toFloat())
		modelMatrix.translate(x, y, 0)
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
		this.nativeToolkit.commitModelMatrix()
		this.nativeToolkit.drawQuadTriangleFan()
		this.nativeToolkit.resetCurrentTextureMatrix()
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS)
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS)
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "([I)V")
	override fun projectOffsets(@OriginalArg(0) destination: IntArray?) {
		destination!![0] = this.translateX
		destination[1] = this.translateY
		destination[3] = this.trimBottom
		destination[2] = this.trimRight
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIIII)V")
	override fun render(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) op: Int, @OriginalArg(3) color: Int, @OriginalArg(4) mode: Int) {
		@Pc(8) val modelMatrix = this.nativeToolkit.getModelMatrix()
		@Pc(13) val textureMatrix = this.nativeToolkit.getCurrentTextureMatrix()
		@Pc(18) val drawX: Int = x + this.translateX
		@Pc(23) val drawY: Int = y + this.translateY
		this.texture!!.setFilterMode(FilterMode.aClass298_10)
		this.nativeToolkit.setupUiRenderState()
		this.nativeToolkit.setTextureSampler(this.texture)
		this.nativeToolkit.toggleBlending(mode)
		this.nativeToolkit.setColourType(op)
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit.setColourValue(color)
		modelMatrix.setScale(0.0f, this.height.toFloat(), this.width.toFloat())
		modelMatrix.translate(drawX, drawY, 0)
		textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(this.height.toFloat()), this.texture!!.toTextureCoordS(this.width.toFloat()))
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
		this.nativeToolkit.commitModelMatrix()
		this.nativeToolkit.drawQuadTriangleFan()
		this.nativeToolkit.resetCurrentTextureMatrix()
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS)
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS)
	}

	@OriginalMember(owner = "client!jd", name = "c", descriptor = "()I")
	override fun getWidth(): Int {
		return this.width
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IIIIII)V")
	override fun captureScreenRegion(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) op: Int, @OriginalArg(5) colour: Int) {
		@Pc(13) val pixels: IntArray? = this.nativeToolkit.na(op, colour, width, height)
		if (pixels != null) {
			for (@Pc(17) i in 0 until pixels.size) {
				pixels[i] = pixels[i] or 0xFF000000.toInt()
			}
			this.updateTextureRegion(x, y, width, height, pixels, width)
		}
	}

	@OriginalMember(owner = "client!jd", name = "a", descriptor = "(IILclient!aa;II)V")
	override fun drawWithSurface(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) clippingMask: ComponentSurface?, @OriginalArg(3) surfaceOffsetX: Int, @OriginalArg(4) surfaceOffsetY: Int) {
		@Pc(6) val maskSurface: MaskedTextureSurface? = clippingMask as MaskedTextureSurface?
		@Pc(11) val drawY: Int = y + this.translateY
		@Pc(16) val drawX: Int = x + this.translateX
		@Pc(19) val gpuTexture = maskSurface!!.nativeTextureSampler
		this.texture!!.setFilterMode(FilterMode.aClass298_10)
		this.nativeToolkit.setupUiRenderState()
		this.nativeToolkit.setTextureSampler(this.texture)
		this.nativeToolkit.toggleBlending(1)
		this.nativeToolkit.setColourType(1)
		@Pc(49) val modelMatrix = this.nativeToolkit.getModelMatrix()
		modelMatrix.setScale(0.0f, this.height.toFloat(), this.width.toFloat())
		modelMatrix.translate(drawX, drawY, 0)
		this.nativeToolkit.commitModelMatrix()
		@Pc(73) val textureMatrix = this.nativeToolkit.getCurrentTextureMatrix()
		textureMatrix!!.setScale(1.0f, this.texture!!.toTextureCoordT(this.height.toFloat()), this.texture!!.toTextureCoordS(this.width.toFloat()))
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
		this.nativeToolkit.setTextureSlot(1)
		this.nativeToolkit.setTextureSampler(gpuTexture)
		this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_4)
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS)
		@Pc(123) val maskTextureMatrix = this.nativeToolkit.getCurrentTextureMatrix()
		maskTextureMatrix!!.setScale(1.0f, gpuTexture!!.toTextureCoordT(this.height.toFloat()), gpuTexture!!.toTextureCoordS(this.width.toFloat()))
		maskTextureMatrix!!.translate(gpuTexture!!.toTextureCoordT((drawY - surfaceOffsetY).toFloat()), 0.0f, gpuTexture!!.toTextureCoordS((drawX - surfaceOffsetX).toFloat()))
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6)
		this.nativeToolkit.drawQuadTriangleFan()
		this.nativeToolkit.resetCurrentTextureMatrix()
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
		this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
		this.nativeToolkit.setTextureSampler(null)
		this.nativeToolkit.setTextureSlot(0)
		this.nativeToolkit.resetCurrentTextureMatrix()
	}
}
