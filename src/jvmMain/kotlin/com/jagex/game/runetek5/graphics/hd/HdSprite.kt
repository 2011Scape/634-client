/*
 * Not part of the original client: 634HD (toolkit 6), copied from SpriteGL (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.graphics.sprite.NativeSprite

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext

class HdSprite : NativeSprite {

	private var channelMaskTexture: Hd2DImageTexture? = null

	private var channelMaskIndex: Int = 0

	private var trimBottom: Int = 0

	private var stretched: Boolean = false

	private var translateY: Int = 0

	private var translateX: Int = 0

	private var trimRight: Int = 0

	private val toolkit: HdToolkit?

	private val texture2d: Hd2DImageTexture?

	constructor(glToolkit: HdToolkit?, width: Int, height: Int, hasAlpha: Boolean) : super() {
		this.toolkit = glToolkit
		this.texture2d = Hd2DImageTexture.createEmpty(glToolkit, if (hasAlpha) 6408 else 6407, width, height)
	}

	constructor(glToolkit: HdToolkit?, x: Int, y: Int, width: Int, height: Int) : super() {
		this.toolkit = glToolkit
		this.texture2d = Hd2DImageTexture.createFromScreenCapture(glToolkit, x, width, y, height)
	}

	constructor(glToolkit: HdToolkit?, width: Int, height: Int, pixels: IntArray?, pixelOffset: Int, rowLength: Int) : super() {
		this.toolkit = glToolkit
		this.texture2d = Hd2DImageTexture.createFromPixels(pixels, pixelOffset, rowLength, height, width, glToolkit)
	}

	override fun method6201(topLeftX: Float, topLeftY: Float, topRightX: Float, topRightY: Float, bottomLeftX: Float, bottomLeftY: Float, combineMode: Int, colorArgb: Int) {
		var topLeftX: Float = topLeftX
		var topLeftY: Float = topLeftY
		var topRightX: Float = topRightX
		var topRightY: Float = topRightY
		var bottomLeftX: Float = bottomLeftX
		var bottomLeftY: Float = bottomLeftY
		val local10: Float
		val local14: Float
		if (this.stretched) {
			val spriteWidth: Float = this.scaleWidth().toFloat()
			val spriteHeight: Float = this.scaleHeight().toFloat()
			val uX: Float = (topRightX - topLeftX) / spriteWidth
			val uY: Float = (topRightY - topLeftY) / spriteWidth
			val vX: Float = (bottomLeftX - topLeftX) / spriteHeight
			val vY: Float = (bottomLeftY - topLeftY) / spriteHeight
			val padTopX: Float = this.translateY.toFloat() * vX
			val padTopY: Float = this.translateY.toFloat() * vY
			val padLeftX: Float = this.translateX.toFloat() * uX
			val padLeftY: Float = this.translateX.toFloat() * uY
			val trimRightX: Float = -uX * this.trimRight.toFloat()
			val trimRightY: Float = this.trimRight.toFloat() * -uY
			val trimBottomX: Float = -vX * this.trimBottom.toFloat()
			bottomLeftX = trimBottomX + bottomLeftX + padLeftX
			topRightX = topRightX + trimRightX + padTopX
			val trimBottomY: Float = this.trimBottom.toFloat() * -vY
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
		HdGl.glColor4ub((colorArgb shr 16).toByte(), (colorArgb shr 8).toByte(), colorArgb.toByte(), (colorArgb shr 24).toByte())
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		HdGl.glTexCoord2f(0.0f, this.texture2d.t)
		HdGl.glVertex2f(topLeftX, topLeftY)
		HdGl.glTexCoord2f(0.0f, 0.0f)
		HdGl.glVertex2f(bottomLeftX, bottomLeftY)
		HdGl.glTexCoord2f(this.texture2d.s, 0.0f)
		HdGl.glVertex2f(bottomRightX, bottomRightY)
		HdGl.glTexCoord2f(this.texture2d.s, this.texture2d.t)
		HdGl.glVertex2f(topRightX, topRightY)
		HdGl.glEnd()
	}

	override fun getHeight(): Int {
		return this.texture2d!!.singleLength
	}

	override fun copyAlpha(channel: Int) {
		HdGl.glPixelTransferf(OpenGLConstants.GL_RED_SCALE, 0.5f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_RED_BIAS, 0.499f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_GREEN_SCALE, 0.5f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_GREEN_BIAS, 0.499f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_BLUE_SCALE, 0.5f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_BLUE_BIAS, 0.499f)
		this.channelMaskTexture = Hd2DImageTexture.createFromScreenCapture(this.toolkit, 0, this.texture2d!!.singleWidth, 0, this.texture2d.singleLength)
		this.channelMaskIndex = channel
		HdGl.glPixelTransferf(OpenGLConstants.GL_RED_SCALE, 1.0f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_RED_BIAS, 0.0f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_GREEN_SCALE, 1.0f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_GREEN_BIAS, 0.0f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_BLUE_SCALE, 1.0f)
		HdGl.glPixelTransferf(OpenGLConstants.GL_BLUE_BIAS, 0.0f)
	}

	override fun projectOffsets(destination: IntArray?) {
		destination!![1] = this.translateY
		destination[3] = this.trimBottom
		destination[0] = this.translateX
		destination[2] = this.trimRight
	}

	override fun scaleHeight(): Int {
		return this.translateY + this.texture2d!!.singleLength + this.trimBottom
	}

	override fun captureScreenRegion(x: Int, y: Int, width: Int, height: Int, op: Int, colour: Int) {
		this.texture2d!!.copyTextureSubImage(x, height, colour, y, op, width)
	}

	override fun drawWithSurface(x: Int, y: Int, clippingMask: ComponentSurface?, surfaceOffsetX: Int, surfaceOffsetY: Int) {
		val glMask: HdClippingMask? = clippingMask as HdClippingMask?
		val surface: Hd2DImageTexture? = glMask!!.surface
		this.texture2d!!.setLinearFilter(false)
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setActiveTexture(this.texture2d)
		this.toolkit.setColourOp(1)
		this.toolkit.activateTexture(1)
		this.toolkit.setActiveTexture(surface)
		this.toolkit.loadTextureCombineMode(7681, 8448)
		this.toolkit.setTexture(34168, 768, 0)
		this.toolkit.setBlendMode(1)
		val drawX: Int = x + this.translateX
		val drawY: Int = y + this.translateY
		val endX: Int = this.texture2d.singleWidth + drawX
		val endY: Int = drawY + this.texture2d.singleLength
		val sPerPixel: Float = surface!!.s / surface.singleWidth.toFloat()
		val tPerPixel: Float = surface.t / surface.singleLength.toFloat()
		val s0: Float = sPerPixel * (drawX - surfaceOffsetX).toFloat()
		val s1: Float = sPerPixel * (endX - surfaceOffsetX).toFloat()
		val t0: Float = surface.t - tPerPixel * (drawY - surfaceOffsetY).toFloat()
		val t1: Float = surface.t - (endY - surfaceOffsetY).toFloat() * tPerPixel
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		HdGl.glColor3f(1.0f, 1.0f, 1.0f)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0f, this.texture2d.t)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s0, t0)
		HdGl.glVertex2i(drawX, drawY)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0f, 0.0f)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s0, t1)
		HdGl.glVertex2i(drawX, drawY + this.texture2d.singleLength)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, 0.0f)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s1, t1)
		HdGl.glVertex2i(drawX + this.texture2d.singleWidth, this.texture2d.singleLength + drawY)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, this.texture2d.t)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s1, t0)
		HdGl.glVertex2i(this.texture2d.singleWidth + drawX, drawY)
		HdGl.glEnd()
		this.toolkit.setTexture(5890, 768, 0)
		this.toolkit.setColourOp(0)
		this.toolkit.setActiveTexture(null)
		this.toolkit.activateTexture(0)
	}

	override fun setOffsets(x1: Int, y1: Int, x2: Int, y2: Int) {
		this.translateX = x1
		this.trimRight = x2
		this.translateY = y1
		this.trimBottom = y2
		this.stretched = this.translateX != 0 || this.translateY != 0 || this.trimRight != 0 || this.trimBottom != 0
	}

	private fun resetChannelMaskState() {
		this.toolkit!!.activateTexture(1)
		this.toolkit.setActiveTexture(null)
		this.toolkit.loadTextureCombineMode(8448, 8448)
		this.toolkit.setTexture(34168, 768, 1)
		this.toolkit.setTextureTransparency(0, 5890)
		this.toolkit.activateTexture(0)
		this.toolkit.setTexture(34168, 768, 1)
	}

	override fun renderParallelogramImpl(x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float, mask: ComponentSurface?, maskX: Int, maskY: Int) {
		var x0: Float = x0
		var y0: Float = y0
		var x1: Float = x1
		var y1: Float = y1
		var x2: Float = x2
		var y2: Float = y2
		val surface: Hd2DImageTexture? = (mask as HdClippingMask?)!!.surface
		val local14: Float
		val local18: Float
		val local25: Float
		val local32: Float
		if (this.stretched) {
			val spriteWidth: Float = this.scaleWidth().toFloat()
			val spriteHeight: Float = this.scaleHeight().toFloat()
			val uX: Float = (x1 - x0) / spriteWidth
			val uY: Float = (y1 - y0) / spriteWidth
			val vX: Float = (x2 - x0) / spriteHeight
			val vY: Float = (y2 - y0) / spriteHeight
			val padTopX: Float = vX * this.translateY.toFloat()
			val padTopY: Float = vY * this.translateY.toFloat()
			val padLeftX: Float = this.translateX.toFloat() * uX
			val padLeftY: Float = this.translateX.toFloat() * uY
			val trimRightX: Float = -uX * this.trimRight.toFloat()
			val trimRightY: Float = -uY * this.trimRight.toFloat()
			val trimBottomX: Float = -vX * this.trimBottom.toFloat()
			y1 = y1 + trimRightY + padTopY
			x1 = trimRightX + x1 + padTopX
			x2 = trimBottomX + padLeftX + x2
			x0 = padLeftX + x0 + padTopX
			y0 = padTopY + y0 + padLeftY
			val trimBottomY: Float = -vY * this.trimBottom.toFloat()
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
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		HdGl.glColor3f(1.0f, 1.0f, 1.0f)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0f, this.texture2d.t)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, maskScaleS * (x0 - maskX.toFloat()), surface.t - maskScaleT * ((-maskY).toFloat() + y0))
		HdGl.glVertex2f(x0, y0)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0f, 0.0f)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, ((-maskX).toFloat() + x2) * maskScaleS, surface.t - maskScaleT * (y2 - maskY.toFloat()))
		HdGl.glVertex2f(x2, y2)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, 0.0f)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, (x3 - maskX.toFloat()) * maskScaleS, surface.t - maskScaleT * (y3 - maskY.toFloat()))
		HdGl.glVertex2f(x3, y3)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, this.texture2d.t)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, maskScaleS * ((-maskX).toFloat() + x1), surface.t - maskScaleT * (y1 - maskY.toFloat()))
		HdGl.glVertex2f(x1, y1)
		HdGl.glEnd()
		this.toolkit.setTexture(5890, 768, 0)
		this.toolkit.setColourOp(0)
		this.toolkit.setActiveTexture(null)
		this.toolkit.activateTexture(0)
	}

	override fun scaleWidth(): Int {
		return this.translateX + this.texture2d!!.singleWidth + this.trimRight
	}

	override fun render(x: Int, y: Int, op: Int, color: Int, mode: Int) {
		this.texture2d!!.setLinearFilter(false)
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setBlendMode(mode)
		HdGl.glColor4ub((color shr 16).toByte(), (color shr 8).toByte(), color.toByte(), (color shr 24).toByte())
		val drawX: Int = x + this.translateX
		val drawY: Int = y + this.translateY
		if (this.channelMaskTexture == null) {
			this.toolkit.setActiveTexture(this.texture2d)
			this.toolkit.setColourOp(op)
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			HdGl.glTexCoord2f(0.0f, this.texture2d.t)
			HdGl.glVertex2i(drawX, drawY)
			HdGl.glTexCoord2f(0.0f, 0.0f)
			HdGl.glVertex2i(drawX, drawY + this.texture2d.singleLength)
			HdGl.glTexCoord2f(this.texture2d.s, 0.0f)
			HdGl.glVertex2i(this.texture2d.singleWidth + drawX, this.texture2d.singleLength + drawY)
			HdGl.glTexCoord2f(this.texture2d.s, this.texture2d.t)
			HdGl.glVertex2i(this.texture2d.singleWidth + drawX, drawY)
			HdGl.glEnd()
			return
		}
		this.setupChannelMaskRendering(op)
		this.channelMaskTexture!!.setLinearFilter(false)
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, this.texture2d.t)
		HdGl.glTexCoord2f(0.0f, this.texture2d.t)
		HdGl.glVertex2i(drawX, drawY)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
		HdGl.glTexCoord2f(0.0f, 0.0f)
		HdGl.glVertex2i(drawX, drawY + this.texture2d.singleLength)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, 0.0f)
		HdGl.glTexCoord2f(this.texture2d.s, 0.0f)
		HdGl.glVertex2i(this.texture2d.singleWidth + drawX, this.texture2d.singleLength + drawY)
		HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, this.texture2d.t)
		HdGl.glTexCoord2f(this.texture2d.s, this.texture2d.t)
		HdGl.glVertex2i(drawX + this.texture2d.singleWidth, drawY)
		HdGl.glEnd()
		this.resetChannelMaskState()
	}

	private fun setupChannelMaskRendering(combineMode: Int) {
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

	override fun drawScaledColored(x: Int, y: Int, width: Int, height: Int, op: Int, colour: Int, mode: Int) {
		val endX: Int = width + x
		val endY: Int = height + y
		this.texture2d!!.setLinearFilter(false)
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setActiveTexture(this.texture2d)
		this.toolkit.setBlendMode(mode)
		this.toolkit.setColourOp(op)
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		if (this.texture2d.tiled && !this.stretched) {
			val tileT: Float = this.texture2d.t * height.toFloat() / this.texture2d.singleLength.toFloat()
			val tileS: Float = width.toFloat() * this.texture2d.s / this.texture2d.singleWidth.toFloat()
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			HdGl.glTexCoord2f(0.0f, tileT)
			HdGl.glVertex2i(x, y)
			HdGl.glTexCoord2f(0.0f, 0.0f)
			HdGl.glVertex2i(x, endY)
			HdGl.glTexCoord2f(tileS, 0.0f)
			HdGl.glVertex2i(endX, endY)
			HdGl.glTexCoord2f(tileS, tileT)
			HdGl.glVertex2i(endX, y)
			HdGl.glEnd()
			return
		}
		HdGl.glPushMatrix()
		HdGl.glTranslatef(this.translateX.toFloat(), this.translateY.toFloat(), 0.0f)
		val spriteWidth: Int = this.scaleWidth()
		val spriteHeight: Int = this.scaleHeight()
		var tileEndY: Int = y + this.texture2d.singleLength
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		var tileY: Int = y
		val local146: Int
		while (tileEndY <= endY) {
			var tileRightX: Int = x + this.texture2d.singleWidth
			var tileX: Int = x
			while (endX >= tileRightX) {
				HdGl.glTexCoord2f(0.0f, this.texture2d.t)
				HdGl.glVertex2i(tileX, tileY)
				HdGl.glTexCoord2f(0.0f, 0.0f)
				HdGl.glVertex2i(tileX, tileEndY)
				HdGl.glTexCoord2f(this.texture2d.s, 0.0f)
				HdGl.glVertex2i(tileRightX, tileEndY)
				HdGl.glTexCoord2f(this.texture2d.s, this.texture2d.t)
				HdGl.glVertex2i(tileRightX, tileY)
				tileX += spriteWidth
				tileRightX += spriteWidth
			}
			if (tileX < endX) {
				val partialS: Float = (endX - tileX).toFloat() * this.texture2d.s / this.texture2d.singleWidth.toFloat()
				HdGl.glTexCoord2f(0.0f, this.texture2d.t)
				HdGl.glVertex2i(tileX, tileY)
				HdGl.glTexCoord2f(0.0f, 0.0f)
				HdGl.glVertex2i(tileX, tileEndY)
				HdGl.glTexCoord2f(partialS, 0.0f)
				HdGl.glVertex2i(endX, tileEndY)
				HdGl.glTexCoord2f(partialS, this.texture2d.t)
				HdGl.glVertex2i(endX, tileY)
			}
			tileY += spriteHeight
			tileEndY += spriteHeight
		}
		if (endY > tileY) {
			val partialT: Float = this.texture2d.t * (this.texture2d.singleLength + tileY - endY).toFloat() / this.texture2d.singleLength.toFloat()
			var tileEndX: Int = x + this.texture2d.singleWidth
			var tileX: Int = x
			while (tileEndX <= endX) {
				HdGl.glTexCoord2f(0.0f, this.texture2d.t)
				HdGl.glVertex2i(tileX, tileY)
				HdGl.glTexCoord2f(0.0f, partialT)
				HdGl.glVertex2i(tileX, endY)
				HdGl.glTexCoord2f(this.texture2d.s, partialT)
				HdGl.glVertex2i(tileEndX, endY)
				HdGl.glTexCoord2f(this.texture2d.s, this.texture2d.t)
				HdGl.glVertex2i(tileEndX, tileY)
				tileEndX += spriteWidth
				tileX += spriteWidth
			}
			if (endX > tileX) {
				val partialS: Float = (endX - tileX).toFloat() * this.texture2d.s / this.texture2d.singleWidth.toFloat()
				HdGl.glTexCoord2f(0.0f, this.texture2d.t)
				HdGl.glVertex2i(tileX, tileY)
				HdGl.glTexCoord2f(0.0f, partialT)
				HdGl.glVertex2i(tileX, endY)
				HdGl.glTexCoord2f(partialS, partialT)
				HdGl.glVertex2i(endX, endY)
				HdGl.glTexCoord2f(partialS, this.texture2d.t)
				HdGl.glVertex2i(endX, tileY)
			}
		}
		HdGl.glEnd()
		HdGl.glPopMatrix()
	}

	override fun getWidth(): Int {
		return this.texture2d!!.singleWidth
	}

	override fun renderImpl(x: Int, y: Int, width: Int, height: Int, combineMode: Int, colorArgb: Int, blendMode: Int) {
		this.texture2d!!.setLinearFilter(true)
		this.toolkit!!.setupSpriteRendering()
		this.toolkit.setBlendMode(blendMode)
		HdGl.glColor4ub((colorArgb shr 16).toByte(), (colorArgb shr 8).toByte(), colorArgb.toByte(), (colorArgb shr 24).toByte())
		if (this.stretched) {
			val scaleX: Float = width.toFloat() / this.scaleWidth().toFloat()
			val scaleY: Float = height.toFloat() / this.scaleHeight().toFloat()
			val x0: Float = x.toFloat() + scaleX * this.translateX.toFloat()
			val y0: Float = this.translateY.toFloat() * scaleY + y.toFloat()
			val x1: Float = x0 + scaleX * this.texture2d.singleWidth.toFloat()
			val y1: Float = y0 + this.texture2d.singleLength.toFloat() * scaleY
			if (this.channelMaskTexture == null) {
				this.toolkit.setActiveTexture(this.texture2d)
				this.toolkit.setColourOp(combineMode)
				HdGl.glBegin(OpenGLConstants.GL_QUADS)
				HdGl.glTexCoord2f(0.0f, this.texture2d.t)
				HdGl.glVertex2f(x0, y0)
				HdGl.glTexCoord2f(0.0f, 0.0f)
				HdGl.glVertex2f(x0, y1)
				HdGl.glTexCoord2f(this.texture2d.s, 0.0f)
				HdGl.glVertex2f(x1, y1)
				HdGl.glTexCoord2f(this.texture2d.s, this.texture2d.t)
				HdGl.glVertex2f(x1, y0)
				HdGl.glEnd()
			} else {
				this.setupChannelMaskRendering(combineMode)
				this.channelMaskTexture!!.setLinearFilter(true)
				HdGl.glBegin(OpenGLConstants.GL_QUADS)
				HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, this.texture2d.t)
				HdGl.glTexCoord2f(0.0f, this.texture2d.t)
				HdGl.glVertex2f(x0, y0)
				HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
				HdGl.glTexCoord2f(0.0f, 0.0f)
				HdGl.glVertex2f(x0, y1)
				HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, 0.0f)
				HdGl.glTexCoord2f(this.texture2d.s, 0.0f)
				HdGl.glVertex2f(x1, y1)
				HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, this.texture2d.t)
				HdGl.glTexCoord2f(this.texture2d.s, this.texture2d.t)
				HdGl.glVertex2f(x1, y0)
				HdGl.glEnd()
				this.resetChannelMaskState()
			}
		} else if (this.channelMaskTexture == null) {
			this.toolkit.setActiveTexture(this.texture2d)
			this.toolkit.setColourOp(combineMode)
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			HdGl.glTexCoord2f(0.0f, this.texture2d.t)
			HdGl.glVertex2i(x, y)
			HdGl.glTexCoord2f(0.0f, 0.0f)
			HdGl.glVertex2i(x, y + height)
			HdGl.glTexCoord2f(this.texture2d.s, 0.0f)
			HdGl.glVertex2i(width + x, height + y)
			HdGl.glTexCoord2f(this.texture2d.s, this.texture2d.t)
			HdGl.glVertex2i(x + width, y)
			HdGl.glEnd()
		} else {
			this.setupChannelMaskRendering(combineMode)
			this.channelMaskTexture!!.setLinearFilter(true)
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, this.texture2d.t)
			HdGl.glTexCoord2f(0.0f, this.texture2d.t)
			HdGl.glVertex2i(x, y)
			HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0f, 0.0f)
			HdGl.glTexCoord2f(0.0f, 0.0f)
			HdGl.glVertex2i(x, height + y)
			HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, 0.0f)
			HdGl.glTexCoord2f(this.texture2d.s, 0.0f)
			HdGl.glVertex2i(x + width, y + height)
			HdGl.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, this.texture2d.t)
			HdGl.glTexCoord2f(this.texture2d.s, this.texture2d.t)
			HdGl.glVertex2i(width + x, y)
			HdGl.glEnd()
			this.resetChannelMaskState()
		}
	}
}
