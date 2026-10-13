/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlFont (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics

import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext

class HdFont : AbstractFontRenderer {

	private val glToolkit: HdToolkit?

	private val texture: Hd2DImageTexture

	private val isAlphaOnlyTexture: Boolean

	private val glyphLists: HdRenderList

	constructor(glToolkit: HdToolkit?, fontMetrics: FontMetrics?, indexedImages: Array<SpriteType?>?, isAlphaOnly: Boolean) : super(glToolkit, fontMetrics) {
		this.glToolkit = glToolkit
		var maxDimension: Int = 0
		for (i in 0 until 256) {
			val indexedImage: SpriteType? = indexedImages!![i]
			if (indexedImage!!.height > maxDimension) {
				maxDimension = indexedImage.height
			}
			if (indexedImage.width > maxDimension) {
				maxDimension = indexedImage.width
			}
		}
		val atlasSize: Int = maxDimension * 16
		val pixels: ByteArray = ByteArray(atlasSize * atlasSize)
		val local57: Int
		val local63: Int
		val local69: Int
		val local75: Int
		for (j in 0 until 256) {
			val indexedImage2: SpriteType? = indexedImages!![j]
			val height: Int = indexedImage2!!.height
			val width: Int = indexedImage2.width
			val cellX: Int = j % 16 * maxDimension
			val cellY: Int = j / 16 * maxDimension
			var destIndex: Int = cellY * atlasSize + cellX
			var srcIndex: Int = 0
			var srcBytes: ByteArray?
			var y: Int
			var x: Int
			if (indexedImage2.alpha == null) {
				srcBytes = indexedImage2.pixels
				y = 0
				while (y < height) {
					x = 0
					while (x < width) {
						pixels[destIndex++] = (if (srcBytes!![srcIndex++].toInt() == 0) 0 else -1).toByte()
						x++
					}
					destIndex += atlasSize - width
					y++
				}
			} else {
				srcBytes = indexedImage2.alpha
				y = 0
				while (y < height) {
					x = 0
					while (x < width) {
						pixels[destIndex++] = srcBytes!![srcIndex++]
						x++
					}
					destIndex += atlasSize - width
					y++
				}
			}
		}
		this.texture = Hd2DImageTexture.createSurface(pixels, atlasSize, glToolkit, atlasSize)
		this.isAlphaOnlyTexture = true
		this.texture.setLinearFilter(false)
		this.glyphLists = HdRenderList(glToolkit, 256)
		val texelScaleS: Float = this.texture.s / this.texture.singleWidth.toFloat()
		val texelScaleT: Float = this.texture.t / this.texture.singleLength.toFloat()
		for (k in 0 until 256) {
			val indexedImage3: SpriteType? = indexedImages!![k]
			val glyphHeight: Int = indexedImage3!!.height
			val glyphWidth: Int = indexedImage3.width
			val glyphOffsetY: Int = indexedImage3.minY
			val glyphOffsetX: Int = indexedImage3.minX
			val atlasX: Float = (k % 16 * maxDimension).toFloat()
			val atlasY: Float = (k / 16 * maxDimension).toFloat()
			val u0: Float = atlasX * texelScaleS
			val v0: Float = atlasY * texelScaleT
			val u1: Float = (atlasX + glyphWidth.toFloat()) * texelScaleS
			val v1: Float = (atlasY + glyphHeight.toFloat()) * texelScaleT
			this.glyphLists.newList(k)
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			HdGl.glTexCoord2f(u0, this.texture.t - v0)
			HdGl.glVertex2i(glyphOffsetX, glyphOffsetY)
			HdGl.glTexCoord2f(u0, this.texture.t - v1)
			HdGl.glVertex2i(glyphOffsetX, glyphOffsetY + glyphHeight)
			HdGl.glTexCoord2f(u1, this.texture.t - v1)
			HdGl.glVertex2i(glyphOffsetX + glyphWidth, glyphOffsetY + glyphHeight)
			HdGl.glTexCoord2f(u1, this.texture.t - v0)
			HdGl.glVertex2i(glyphOffsetX + glyphWidth, glyphOffsetY)
			HdGl.glEnd()
			this.glyphLists.endList()
		}
	}

	override fun renderSymbol(c: Char, x: Int, y: Int, colour: Int, shadow: Boolean, mask: ComponentSurface?, offsetX: Int, offsetY: Int) {
		val clippingMask: HdClippingMask? = mask as HdClippingMask?
		val surface: Hd2DImageTexture? = clippingMask!!.surface
		this.glToolkit!!.setupTextRendering()
		this.glToolkit.setActiveTexture(this.texture)
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.loadTextureCombineMode(7681, 8448)
			this.glToolkit.setTexture(34168, 768, 0)
		} else {
			this.glToolkit.loadTextureCombineMode(7681, 7681)
		}
		this.glToolkit.activateTexture(1)
		this.glToolkit.setActiveTexture(surface)
		this.glToolkit.loadTextureCombineMode(7681, 8448)
		this.glToolkit.setTexture(34168, 768, 0)
		HdGl.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		HdGl.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		val texelScaleS: Float = surface!!.s / surface.singleWidth.toFloat()
		val texelScaleT: Float = surface.t / surface.singleLength.toFloat()
		HdGl.glTexGenfv(OpenGLConstants.GL_S, OpenGLConstants.GL_EYE_PLANE, floatArrayOf(texelScaleS, 0.0f, 0.0f, (-offsetX).toFloat() * texelScaleS), 0)
		HdGl.glTexGenfv(OpenGLConstants.GL_T, OpenGLConstants.GL_EYE_PLANE, floatArrayOf(0.0f, texelScaleT, 0.0f, (-offsetY).toFloat() * texelScaleT), 0)
		HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
		HdGl.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T)
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		HdGl.glTranslatef(x.toFloat(), y.toFloat(), 0.0f)
		this.glyphLists.callList(c)
		HdGl.glLoadIdentity()
		HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
		HdGl.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T)
		this.glToolkit.setTexture(5890, 768, 0)
		this.glToolkit.loadTextureCombineMode(8448, 8448)
		this.glToolkit.setActiveTexture(null)
		this.glToolkit.activateTexture(0)
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.setTexture(5890, 768, 0)
		}
	}

	override fun fa(c: Char, x: Int, y: Int, colour: Int, shadow: Boolean) {
		this.glToolkit!!.setupTextRendering()
		this.glToolkit.setActiveTexture(this.texture)
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.loadTextureCombineMode(7681, 8448)
			this.glToolkit.setTexture(34168, 768, 0)
		} else {
			this.glToolkit.loadTextureCombineMode(7681, 7681)
		}
		HdGl.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		HdGl.glTranslatef(x.toFloat(), y.toFloat(), 0.0f)
		this.glyphLists.callList(c)
		HdGl.glLoadIdentity()
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.setTexture(5890, 768, 0)
		}
	}
}
