package com.jagex.game.runetek5.graphics.sprite

import com.jagex.game.runetek5.config.rsinterface.GlClippingMask
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.texture.OpenGl2DImageTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit

@OriginalClass("client!nda")
class SpriteGL : NativeSprite {

	@OriginalMember(owner = "client!nda", name = "h", descriptor = "Lclient!eba;")
	private var channelMaskTexture: OpenGl2DImageTexture? = null

	@OriginalMember(owner = "client!nda", name = "f", descriptor = "I")
	private var channelMaskIndex: Int = 0

	@OriginalMember(owner = "client!nda", name = "i", descriptor = "I")
	private var trimBottom: Int = 0

	@OriginalMember(owner = "client!nda", name = "d", descriptor = "Z")
	private var stretched: Boolean = false

	@OriginalMember(owner = "client!nda", name = "w", descriptor = "I")
	private var translateY: Int = 0

	@OriginalMember(owner = "client!nda", name = "x", descriptor = "I")
	private var translateX: Int = 0

	@OriginalMember(owner = "client!nda", name = "z", descriptor = "I")
	private var trimRight: Int = 0

	@OriginalMember(owner = "client!nda", name = "g", descriptor = "Lclient!qo;")
	private val toolkit: GlToolkit?

	@OriginalMember(owner = "client!nda", name = "k", descriptor = "Lclient!eba;")
	private val texture2d: OpenGl2DImageTexture?

	@OriginalMember(owner = "client!nda", name = "<init>", descriptor = "(Lclient!qo;IIZ)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) hasAlpha: Boolean) : super() {
		this.toolkit = glToolkit
		this.texture2d = OpenGl2DImageTexture.createEmpty(glToolkit, if (hasAlpha) 6408 else 6407, width, height)
	}

	@OriginalMember(owner = "client!nda", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int) : super() {
		this.toolkit = glToolkit
		this.texture2d = OpenGl2DImageTexture.createFromScreenCapture(glToolkit, x, width, y, height)
	}

	@OriginalMember(owner = "client!nda", name = "<init>", descriptor = "(Lclient!qo;II[III)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) pixels: IntArray?, @OriginalArg(4) pixelOffset: Int, @OriginalArg(5) rowLength: Int) : super() {
		this.toolkit = glToolkit
		this.texture2d = OpenGl2DImageTexture.createFromPixels(pixels, pixelOffset, rowLength, height, width, glToolkit)
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(FFFFFFIIII)V")
	override fun method6201(@OriginalArg(0) topLeftX: Float, @OriginalArg(1) topLeftY: Float, @OriginalArg(2) topRightX: Float, @OriginalArg(3) topRightY: Float, @OriginalArg(4) bottomLeftX: Float, @OriginalArg(5) bottomLeftY: Float, @OriginalArg(6) combineMode: Int, @OriginalArg(7) colorArgb: Int) {
		var topLeftX: Float = topLeftX
		var topLeftY: Float = topLeftY
		var topRightX: Float = topRightX
		var topRightY: Float = topRightY
		var bottomLeftX: Float = bottomLeftX
		var bottomLeftY: Float = bottomLeftY
		@Pc(10) val local10: Float
		@Pc(14) val local14: Float
		if (this.stretched) {
			val spriteWidth: Float = this.scaleWidth().toFloat()
			val spriteHeight: Float = this.scaleHeight().toFloat()
			@Pc(20) val uX: Float = (topRightX - topLeftX) / spriteWidth
			@Pc(26) val uY: Float = (topRightY - topLeftY) / spriteWidth
			@Pc(33) val vX: Float = (bottomLeftX - topLeftX) / spriteHeight
			@Pc(40) val vY: Float = (bottomLeftY - topLeftY) / spriteHeight
			@Pc(46) val padTopX: Float = this.translateY.toFloat() * vX
			@Pc(52) val padTopY: Float = this.translateY.toFloat() * vY
			@Pc(58) val padLeftX: Float = this.translateX.toFloat() * uX
			@Pc(64) val padLeftY: Float = this.translateX.toFloat() * uY
			@Pc(71) val trimRightX: Float = -uX * this.trimRight.toFloat()
			@Pc(78) val trimRightY: Float = this.trimRight.toFloat() * -uY
			@Pc(85) val trimBottomX: Float = -vX * this.trimBottom.toFloat()
			bottomLeftX = trimBottomX + bottomLeftX + padLeftX
			topRightX = topRightX + trimRightX + padTopX
			@Pc(104) val trimBottomY: Float = this.trimBottom.toFloat() * -vY
			topRightY = topRightY + trimRightY + padTopY
			topLeftX = topLeftX + padLeftX + padTopX
			topLeftY = padTopY + padLeftY + topLeftY
			bottomLeftY = trimBottomY + bottomLeftY + padLeftY
		}
		val bottomRightX: Float = topRightX + bottomLeftX - topLeftX
		val bottomRightY: Float = bottomLeftY + topRightY - topLeftY
		this.texture2d!!.setLinearFilter(true)
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setActiveTexture(this.texture2d)
		this.toolkit.setBlendMode(1)
		this.toolkit.setColourOp(combineMode)
		OpenGL.glColor4ub((colorArgb shr 16).toByte(), (colorArgb shr 8).toByte(), colorArgb.toByte(), (colorArgb shr 24).toByte())
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
		OpenGL.glVertex2f(topLeftX, topLeftY)
		OpenGL.glTexCoord2f(0.0f, 0.0f)
		OpenGL.glVertex2f(bottomLeftX, bottomLeftY)
		OpenGL.glTexCoord2f(this.texture2d.s, 0.0f)
		OpenGL.glVertex2f(bottomRightX, bottomRightY)
		OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t)
		OpenGL.glVertex2f(topRightX, topRightY)
		OpenGL.glEnd()
	}

	@OriginalMember(owner = "client!nda", name = "b", descriptor = "()I")
	override fun getHeight(): Int {
		return this.texture2d!!.singleLength
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(III)V")
	override fun copyAlpha(@OriginalArg(2) channel: Int) {
		OpenGL.glPixelTransferf(OpenGLConstants.GL_RED_SCALE, 0.5f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_RED_BIAS, 0.499f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_GREEN_SCALE, 0.5f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_GREEN_BIAS, 0.499f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_BLUE_SCALE, 0.5f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_BLUE_BIAS, 0.499f)
		this.channelMaskTexture = OpenGl2DImageTexture.createFromScreenCapture(this.toolkit, 0, this.texture2d!!.singleWidth, 0, this.texture2d.singleLength)
		this.channelMaskIndex = channel
		OpenGL.glPixelTransferf(OpenGLConstants.GL_RED_SCALE, 1.0f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_RED_BIAS, 0.0f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_GREEN_SCALE, 1.0f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_GREEN_BIAS, 0.0f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_BLUE_SCALE, 1.0f)
		OpenGL.glPixelTransferf(OpenGLConstants.GL_BLUE_BIAS, 0.0f)
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "([I)V")
	override fun projectOffsets(@OriginalArg(0) destination: IntArray?) {
		destination!![1] = this.translateY
		destination[3] = this.trimBottom
		destination[0] = this.translateX
		destination[2] = this.trimRight
	}

	@OriginalMember(owner = "client!nda", name = "d", descriptor = "()I")
	override fun scaleHeight(): Int {
		return this.translateY + this.texture2d!!.singleLength + this.trimBottom
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IIIIII)V")
	override fun captureScreenRegion(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) op: Int, @OriginalArg(5) colour: Int) {
		this.texture2d!!.copyTextureSubImage(x, height, colour, y, op, width)
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IILclient!aa;II)V")
	override fun drawWithSurface(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) clippingMask: ComponentSurface?, @OriginalArg(3) surfaceOffsetX: Int, @OriginalArg(4) surfaceOffsetY: Int) {
		@Pc(6) val glMask: GlClippingMask? = clippingMask as GlClippingMask?
		@Pc(9) val surface: OpenGl2DImageTexture? = glMask!!.surface
		this.texture2d!!.setLinearFilter(false)
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setActiveTexture(this.texture2d)
		this.toolkit.setColourOp(1)
		this.toolkit.activateTexture(1)
		this.toolkit.setActiveTexture(surface)
		this.toolkit.loadTextureCombineMode(7681, 8448)
		this.toolkit.setTexture(34168, 768, 0)
		this.toolkit.setBlendMode(1)
		@Pc(62) val drawX: Int = x + this.translateX
		@Pc(67) val drawY: Int = y + this.translateY
		@Pc(73) val endX: Int = this.texture2d.singleWidth + drawX
		@Pc(80) val endY: Int = drawY + this.texture2d.singleLength
		@Pc(87) val sPerPixel: Float = surface!!.s / surface.singleWidth.toFloat()
		@Pc(94) val tPerPixel: Float = surface.t / surface.singleLength.toFloat()
		@Pc(102) val s0: Float = sPerPixel * (drawX - surfaceOffsetX).toFloat()
		@Pc(109) val s1: Float = sPerPixel * (endX - surfaceOffsetX).toFloat()
		@Pc(119) val t0: Float = surface.t - tPerPixel * (drawY - surfaceOffsetY).toFloat()
		@Pc(131) val t1: Float = surface.t - (endY - surfaceOffsetY).toFloat() * tPerPixel
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		OpenGL.glColor3f(1.0f, 1.0f, 1.0f)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0f, this.texture2d.t)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s0, t0)
		OpenGL.glVertex2i(drawX, drawY)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0f, 0.0f)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s0, t1)
		OpenGL.glVertex2i(drawX, drawY + this.texture2d.singleLength)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, 0.0f)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s1, t1)
		OpenGL.glVertex2i(drawX + this.texture2d.singleWidth, this.texture2d.singleLength + drawY)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, this.texture2d.t)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s1, t0)
		OpenGL.glVertex2i(this.texture2d.singleWidth + drawX, drawY)
		OpenGL.glEnd()
		this.toolkit.setTexture(5890, 768, 0)
		this.toolkit.setColourOp(0)
		this.toolkit.setActiveTexture(null)
		this.toolkit.activateTexture(0)
	}

	@OriginalMember(owner = "client!nda", name = "c", descriptor = "(IIII)V")
	override fun setOffsets(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int) {
		this.translateX = x1
		this.trimRight = x2
		this.translateY = y1
		this.trimBottom = y2
		this.stretched = this.translateX != 0 || this.translateY != 0 || this.trimRight != 0 || this.trimBottom != 0
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(B)V")
	private fun resetChannelMaskState() {
		this.toolkit!!.activateTexture(1)
		this.toolkit.setActiveTexture(null)
		this.toolkit.loadTextureCombineMode(8448, 8448)
		this.toolkit.setTexture(34168, 768, 1)
		this.toolkit.setTextureTransparency(0, 5890)
		this.toolkit.activateTexture(0)
		this.toolkit.setTexture(34168, 768, 1)
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(FFFFFFILclient!aa;II)V")
	override fun renderParallelogramImpl(@OriginalArg(0) x0: Float, @OriginalArg(1) y0: Float, @OriginalArg(2) x1: Float, @OriginalArg(3) y1: Float, @OriginalArg(4) x2: Float, @OriginalArg(5) y2: Float, @OriginalArg(7) mask: ComponentSurface?, @OriginalArg(8) maskX: Int, @OriginalArg(9) maskY: Int) {
		var x0: Float = x0
		var y0: Float = y0
		var x1: Float = x1
		var y1: Float = y1
		var x2: Float = x2
		var y2: Float = y2
		@Pc(7) val surface: OpenGl2DImageTexture? = (mask as GlClippingMask?)!!.surface
		@Pc(14) val local14: Float
		@Pc(18) val local18: Float
		@Pc(25) val local25: Float
		@Pc(32) val local32: Float
		if (this.stretched) {
			val spriteWidth: Float = this.scaleWidth().toFloat()
			val spriteHeight: Float = this.scaleHeight().toFloat()
			val uX: Float = (x1 - x0) / spriteWidth
			val uY: Float = (y1 - y0) / spriteWidth
			@Pc(39) val vX: Float = (x2 - x0) / spriteHeight
			@Pc(46) val vY: Float = (y2 - y0) / spriteHeight
			@Pc(52) val padTopX: Float = vX * this.translateY.toFloat()
			@Pc(58) val padTopY: Float = vY * this.translateY.toFloat()
			@Pc(64) val padLeftX: Float = this.translateX.toFloat() * uX
			@Pc(70) val padLeftY: Float = this.translateX.toFloat() * uY
			@Pc(77) val trimRightX: Float = -uX * this.trimRight.toFloat()
			@Pc(84) val trimRightY: Float = -uY * this.trimRight.toFloat()
			@Pc(91) val trimBottomX: Float = -vX * this.trimBottom.toFloat()
			y1 = y1 + trimRightY + padTopY
			x1 = trimRightX + x1 + padTopX
			x2 = trimBottomX + padLeftX + x2
			x0 = padLeftX + x0 + padTopX
			y0 = padTopY + y0 + padLeftY
			@Pc(128) val trimBottomY: Float = -vY * this.trimBottom.toFloat()
			y2 = padLeftY + y2 + trimBottomY
		}
		val x3: Float = x1 + x2 - x0
		this.texture2d!!.setLinearFilter(true)
		val y3: Float = y2 + y1 - y0
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setActiveTexture(this.texture2d)
		this.toolkit.setColourOp(1)
		this.toolkit.activateTexture(1)
		this.toolkit.setActiveTexture(surface)
		this.toolkit.loadTextureCombineMode(7681, 8448)
		this.toolkit.setTexture(34168, 768, 0)
		this.toolkit.setBlendMode(1)
		val maskScaleS: Float = surface!!.s / surface.singleWidth.toFloat()
		val maskScaleT: Float = surface.t / surface.singleLength.toFloat()
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		OpenGL.glColor3f(1.0f, 1.0f, 1.0f)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0f, this.texture2d.t)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, maskScaleS * (x0 - maskX.toFloat()), surface.t - maskScaleT * ((-maskY).toFloat() + y0))
		OpenGL.glVertex2f(x0, y0)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0f, 0.0f)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, ((-maskX).toFloat() + x2) * maskScaleS, surface.t - maskScaleT * (y2 - maskY.toFloat()))
		OpenGL.glVertex2f(x2, y2)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, 0.0f)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, (x3 - maskX.toFloat()) * maskScaleS, surface.t - maskScaleT * (y3 - maskY.toFloat()))
		OpenGL.glVertex2f(x3, y3)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, this.texture2d.t)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, maskScaleS * ((-maskX).toFloat() + x1), surface.t - maskScaleT * (y1 - maskY.toFloat()))
		OpenGL.glVertex2f(x1, y1)
		OpenGL.glEnd()
		this.toolkit.setTexture(5890, 768, 0)
		this.toolkit.setColourOp(0)
		this.toolkit.setActiveTexture(null)
		this.toolkit.activateTexture(0)
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "()I")
	override fun scaleWidth(): Int {
		return this.translateX + this.texture2d!!.singleWidth + this.trimRight
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IIIII)V")
	override fun render(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) op: Int, @OriginalArg(3) color: Int, @OriginalArg(4) mode: Int) {
		this.texture2d!!.setLinearFilter(false)
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setBlendMode(mode)
		OpenGL.glColor4ub((color shr 16).toByte(), (color shr 8).toByte(), color.toByte(), (color shr 24).toByte())
		@Pc(37) val drawX: Int = x + this.translateX
		@Pc(42) val drawY: Int = y + this.translateY
		if (this.channelMaskTexture == null) {
			this.toolkit.setActiveTexture(this.texture2d)
			this.toolkit.setColourOp(op)
			OpenGL.glBegin(OpenGLConstants.GL_QUADS)
			OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
			OpenGL.glVertex2i(drawX, drawY)
			OpenGL.glTexCoord2f(0.0f, 0.0f)
			OpenGL.glVertex2i(drawX, drawY + this.texture2d.singleLength)
			OpenGL.glTexCoord2f(this.texture2d.s, 0.0f)
			OpenGL.glVertex2i(this.texture2d.singleWidth + drawX, this.texture2d.singleLength + drawY)
			OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t)
			OpenGL.glVertex2i(this.texture2d.singleWidth + drawX, drawY)
			OpenGL.glEnd()
			return
		}
		this.setupChannelMaskRendering(op)
		this.channelMaskTexture!!.setLinearFilter(false)
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, this.texture2d.t)
		OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
		OpenGL.glVertex2i(drawX, drawY)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
		OpenGL.glTexCoord2f(0.0f, 0.0f)
		OpenGL.glVertex2i(drawX, drawY + this.texture2d.singleLength)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, 0.0f)
		OpenGL.glTexCoord2f(this.texture2d.s, 0.0f)
		OpenGL.glVertex2i(this.texture2d.singleWidth + drawX, this.texture2d.singleLength + drawY)
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, this.texture2d.t)
		OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t)
		OpenGL.glVertex2i(drawX + this.texture2d.singleWidth, drawY)
		OpenGL.glEnd()
		this.resetChannelMaskState()
	}

	@OriginalMember(owner = "client!nda", name = "b", descriptor = "(II)V")
	private fun setupChannelMaskRendering(@OriginalArg(0) combineMode: Int) {
		this.toolkit!!.activateTexture(1)
		this.toolkit.setActiveTexture(this.texture2d)
		this.toolkit.loadTextureCombineMode(this.toolkit.getCombineModeEffectType(combineMode), 7681)
		this.toolkit.setTexture(34167, 768, 1)
		this.toolkit.setTextureTransparency(0, 34168)
		this.toolkit.activateTexture(0)
		this.toolkit.setActiveTexture(this.channelMaskTexture)
		this.toolkit.loadTextureCombineMode(34479, 7681)
		this.toolkit.setTexture(34166, 768, 1)
		if (this.channelMaskIndex == 0) {
			this.toolkit.updatePointSizeRange(0.0f, 1.0f, 0.5f, 0.5f)
		} else if (this.channelMaskIndex == 1) {
			this.toolkit.updatePointSizeRange(0.0f, 0.5f, 0.5f, 1.0f)
		} else if (this.channelMaskIndex == 2) {
			this.toolkit.updatePointSizeRange(0.0f, 0.5f, 1.0f, 0.5f)
		} else if (this.channelMaskIndex == 3) {
			this.toolkit.updatePointSizeRange(0.0f, 128.5f, 128.5f, 128.5f)
			return
		}
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IIIIIII)V")
	override fun drawScaledColored(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) op: Int, @OriginalArg(5) colour: Int, @OriginalArg(6) mode: Int) {
		@Pc(9) val endX: Int = width + x
		@Pc(13) val endY: Int = height + y
		this.texture2d!!.setLinearFilter(false)
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setActiveTexture(this.texture2d)
		this.toolkit.setBlendMode(mode)
		this.toolkit.setColourOp(op)
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		if (this.texture2d.tiled && !this.stretched) {
			@Pc(72) val tileT: Float = this.texture2d.t * height.toFloat() / this.texture2d.singleLength.toFloat()
			@Pc(84) val tileS: Float = width.toFloat() * this.texture2d.s / this.texture2d.singleWidth.toFloat()
			OpenGL.glBegin(OpenGLConstants.GL_QUADS)
			OpenGL.glTexCoord2f(0.0f, tileT)
			OpenGL.glVertex2i(x, y)
			OpenGL.glTexCoord2f(0.0f, 0.0f)
			OpenGL.glVertex2i(x, endY)
			OpenGL.glTexCoord2f(tileS, 0.0f)
			OpenGL.glVertex2i(endX, endY)
			OpenGL.glTexCoord2f(tileS, tileT)
			OpenGL.glVertex2i(endX, y)
			OpenGL.glEnd()
			return
		}
		OpenGL.glPushMatrix()
		OpenGL.glTranslatef(this.translateX.toFloat(), this.translateY.toFloat(), 0.0f)
		@Pc(124) val spriteWidth: Int = this.scaleWidth()
		@Pc(127) val spriteHeight: Int = this.scaleHeight()
		@Pc(134) var tileEndY: Int = y + this.texture2d.singleLength
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		@Pc(138) var tileY: Int = y
		@Pc(146) val local146: Int
		while (tileEndY <= endY) {
			var tileRightX: Int = x + this.texture2d.singleWidth
			@Pc(148) var tileX: Int = x
			while (endX >= tileRightX) {
				OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
				OpenGL.glVertex2i(tileX, tileY)
				OpenGL.glTexCoord2f(0.0f, 0.0f)
				OpenGL.glVertex2i(tileX, tileEndY)
				OpenGL.glTexCoord2f(this.texture2d.s, 0.0f)
				OpenGL.glVertex2i(tileRightX, tileEndY)
				OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t)
				OpenGL.glVertex2i(tileRightX, tileY)
				tileX += spriteWidth
				tileRightX += spriteWidth
			}
			if (tileX < endX) {
				@Pc(211) val partialS: Float = (endX - tileX).toFloat() * this.texture2d.s / this.texture2d.singleWidth.toFloat()
				OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
				OpenGL.glVertex2i(tileX, tileY)
				OpenGL.glTexCoord2f(0.0f, 0.0f)
				OpenGL.glVertex2i(tileX, tileEndY)
				OpenGL.glTexCoord2f(partialS, 0.0f)
				OpenGL.glVertex2i(endX, tileEndY)
				OpenGL.glTexCoord2f(partialS, this.texture2d.t)
				OpenGL.glVertex2i(endX, tileY)
			}
			tileY += spriteHeight
			tileEndY += spriteHeight
		}
		if (endY > tileY) {
			@Pc(276) val partialT: Float = this.texture2d.t * (this.texture2d.singleLength + tileY - endY).toFloat() / this.texture2d.singleLength.toFloat()
			@Pc(282) var tileEndX: Int = x + this.texture2d.singleWidth
			var tileX: Int = x
			while (tileEndX <= endX) {
				OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
				OpenGL.glVertex2i(tileX, tileY)
				OpenGL.glTexCoord2f(0.0f, partialT)
				OpenGL.glVertex2i(tileX, endY)
				OpenGL.glTexCoord2f(this.texture2d.s, partialT)
				OpenGL.glVertex2i(tileEndX, endY)
				OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t)
				OpenGL.glVertex2i(tileEndX, tileY)
				tileEndX += spriteWidth
				tileX += spriteWidth
			}
			if (endX > tileX) {
				@Pc(350) val partialS: Float = (endX - tileX).toFloat() * this.texture2d.s / this.texture2d.singleWidth.toFloat()
				OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
				OpenGL.glVertex2i(tileX, tileY)
				OpenGL.glTexCoord2f(0.0f, partialT)
				OpenGL.glVertex2i(tileX, endY)
				OpenGL.glTexCoord2f(partialS, partialT)
				OpenGL.glVertex2i(endX, endY)
				OpenGL.glTexCoord2f(partialS, this.texture2d.t)
				OpenGL.glVertex2i(endX, tileY)
			}
		}
		OpenGL.glEnd()
		OpenGL.glPopMatrix()
	}

	@OriginalMember(owner = "client!nda", name = "c", descriptor = "()I")
	override fun getWidth(): Int {
		return this.texture2d!!.singleWidth
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IIIIIIII)V")
	override fun renderImpl(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) combineMode: Int, @OriginalArg(5) colorArgb: Int, @OriginalArg(6) blendMode: Int) {
		this.texture2d!!.setLinearFilter(true)
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setBlendMode(blendMode)
		OpenGL.glColor4ub((colorArgb shr 16).toByte(), (colorArgb shr 8).toByte(), colorArgb.toByte(), (colorArgb shr 24).toByte())
		if (this.stretched) {
			@Pc(48) val scaleX: Float = width.toFloat() / this.scaleWidth().toFloat()
			@Pc(55) val scaleY: Float = height.toFloat() / this.scaleHeight().toFloat()
			@Pc(64) val x0: Float = x.toFloat() + scaleX * this.translateX.toFloat()
			@Pc(73) val y0: Float = this.translateY.toFloat() * scaleY + y.toFloat()
			@Pc(82) val x1: Float = x0 + scaleX * this.texture2d.singleWidth.toFloat()
			@Pc(91) val y1: Float = y0 + this.texture2d.singleLength.toFloat() * scaleY
			if (this.channelMaskTexture == null) {
				this.toolkit.setActiveTexture(this.texture2d)
				this.toolkit.setColourOp(combineMode)
				OpenGL.glBegin(OpenGLConstants.GL_QUADS)
				OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
				OpenGL.glVertex2f(x0, y0)
				OpenGL.glTexCoord2f(0.0f, 0.0f)
				OpenGL.glVertex2f(x0, y1)
				OpenGL.glTexCoord2f(this.texture2d.s, 0.0f)
				OpenGL.glVertex2f(x1, y1)
				OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t)
				OpenGL.glVertex2f(x1, y0)
				OpenGL.glEnd()
			} else {
				this.setupChannelMaskRendering(combineMode)
				this.channelMaskTexture!!.setLinearFilter(true)
				OpenGL.glBegin(OpenGLConstants.GL_QUADS)
				OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, this.texture2d.t)
				OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
				OpenGL.glVertex2f(x0, y0)
				OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
				OpenGL.glTexCoord2f(0.0f, 0.0f)
				OpenGL.glVertex2f(x0, y1)
				OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, 0.0f)
				OpenGL.glTexCoord2f(this.texture2d.s, 0.0f)
				OpenGL.glVertex2f(x1, y1)
				OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, this.texture2d.t)
				OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t)
				OpenGL.glVertex2f(x1, y0)
				OpenGL.glEnd()
				this.resetChannelMaskState()
			}
		} else if (this.channelMaskTexture == null) {
			this.toolkit.setActiveTexture(this.texture2d)
			this.toolkit.setColourOp(combineMode)
			OpenGL.glBegin(OpenGLConstants.GL_QUADS)
			OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
			OpenGL.glVertex2i(x, y)
			OpenGL.glTexCoord2f(0.0f, 0.0f)
			OpenGL.glVertex2i(x, y + height)
			OpenGL.glTexCoord2f(this.texture2d.s, 0.0f)
			OpenGL.glVertex2i(width + x, height + y)
			OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t)
			OpenGL.glVertex2i(x + width, y)
			OpenGL.glEnd()
		} else {
			this.setupChannelMaskRendering(combineMode)
			this.channelMaskTexture!!.setLinearFilter(true)
			OpenGL.glBegin(OpenGLConstants.GL_QUADS)
			OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, this.texture2d.t)
			OpenGL.glTexCoord2f(0.0f, this.texture2d.t)
			OpenGL.glVertex2i(x, y)
			OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
			OpenGL.glTexCoord2f(0.0f, 0.0f)
			OpenGL.glVertex2i(x, height + y)
			OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, 0.0f)
			OpenGL.glTexCoord2f(this.texture2d.s, 0.0f)
			OpenGL.glVertex2i(x + width, y + height)
			OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, this.texture2d.t)
			OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t)
			OpenGL.glVertex2i(width + x, y)
			OpenGL.glEnd()
			this.resetChannelMaskState()
		}
	}
}
