package com.jagex.game.runetek5.graphics.font

import com.jagex.game.runetek5.config.rsinterface.GlClippingMask
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.toolkit.opengl.GlRenderList
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.texture.OpenGl2DImageTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit

@OriginalClass("client!th")
class GlFont : AbstractFontRenderer {

	@OriginalMember(owner = "client!th", name = "v", descriptor = "Lclient!qo;")
	private val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!th", name = "x", descriptor = "Lclient!eba;")
	private val texture: OpenGl2DImageTexture

	@OriginalMember(owner = "client!th", name = "y", descriptor = "Z")
	private val isAlphaOnlyTexture: Boolean

	@OriginalMember(owner = "client!th", name = "w", descriptor = "Lclient!jv;")
	private val glyphLists: GlRenderList

	@OriginalMember(owner = "client!th", name = "<init>", descriptor = "(Lclient!qo;Lclient!oea;[Lclient!rh;Z)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) fontMetrics: FontMetrics?, @OriginalArg(2) indexedImages: Array<SpriteType?>?, @OriginalArg(3) isAlphaOnly: Boolean) : super(glToolkit, fontMetrics) {
		this.glToolkit = glToolkit
		@Pc(8) var maxDimension: Int = 0
		for (@Pc(10) i in 0 until 256) {
			@Pc(15) val indexedImage: SpriteType? = indexedImages!![i]
			if (indexedImage!!.height > maxDimension) {
				maxDimension = indexedImage.height
			}
			if (indexedImage.width > maxDimension) {
				maxDimension = indexedImage.width
			}
		}
		@Pc(37) val atlasSize: Int = maxDimension * 16
		@Pc(44) val pixels: ByteArray = ByteArray(atlasSize * atlasSize)
		@Pc(57) val local57: Int
		@Pc(63) val local63: Int
		@Pc(69) val local69: Int
		@Pc(75) val local75: Int
		for (@Pc(46) j in 0 until 256) {
			@Pc(51) val indexedImage2: SpriteType? = indexedImages!![j]
			@Pc(54) val height: Int = indexedImage2!!.height
			val width: Int = indexedImage2.width
			val cellX: Int = j % 16 * maxDimension
			val cellY: Int = j / 16 * maxDimension
			var destIndex: Int = cellY * atlasSize + cellX
			@Pc(77) var srcIndex: Int = 0
			@Pc(83) var srcBytes: ByteArray?
			@Pc(85) var y: Int
			@Pc(88) var x: Int
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
		this.texture = OpenGl2DImageTexture.createSurface(pixels, atlasSize, glToolkit, atlasSize)
		this.isAlphaOnlyTexture = true
		this.texture.setLinearFilter(false)
		this.glyphLists = GlRenderList(glToolkit, 256)
		@Pc(334) val texelScaleS: Float = this.texture.s / this.texture.singleWidth.toFloat()
		@Pc(343) val texelScaleT: Float = this.texture.t / this.texture.singleLength.toFloat()
		for (@Pc(345) k in 0 until 256) {
			@Pc(350) val indexedImage3: SpriteType? = indexedImages!![k]
			val glyphHeight: Int = indexedImage3!!.height
			val glyphWidth: Int = indexedImage3.width
			val glyphOffsetY: Int = indexedImage3.minY
			val glyphOffsetX: Int = indexedImage3.minX
			@Pc(369) val atlasX: Float = (k % 16 * maxDimension).toFloat()
			@Pc(376) val atlasY: Float = (k / 16 * maxDimension).toFloat()
			@Pc(380) val u0: Float = atlasX * texelScaleS
			@Pc(384) val v0: Float = atlasY * texelScaleT
			@Pc(391) val u1: Float = (atlasX + glyphWidth.toFloat()) * texelScaleS
			@Pc(398) val v1: Float = (atlasY + glyphHeight.toFloat()) * texelScaleT
			this.glyphLists.newList(k)
			OpenGL.glBegin(OpenGLConstants.GL_QUADS)
			OpenGL.glTexCoord2f(u0, this.texture.t - v0)
			OpenGL.glVertex2i(glyphOffsetX, glyphOffsetY)
			OpenGL.glTexCoord2f(u0, this.texture.t - v1)
			OpenGL.glVertex2i(glyphOffsetX, glyphOffsetY + glyphHeight)
			OpenGL.glTexCoord2f(u1, this.texture.t - v1)
			OpenGL.glVertex2i(glyphOffsetX + glyphWidth, glyphOffsetY + glyphHeight)
			OpenGL.glTexCoord2f(u1, this.texture.t - v0)
			OpenGL.glVertex2i(glyphOffsetX + glyphWidth, glyphOffsetY)
			OpenGL.glEnd()
			this.glyphLists.endList()
		}
	}

	@OriginalMember(owner = "client!th", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	override fun renderSymbol(@OriginalArg(0) c: Char, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) shadow: Boolean, @OriginalArg(5) mask: ComponentSurface?, @OriginalArg(6) offsetX: Int, @OriginalArg(7) offsetY: Int) {
		@Pc(2) val clippingMask: GlClippingMask? = mask as GlClippingMask?
		@Pc(5) val surface: OpenGl2DImageTexture? = clippingMask!!.surface
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
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		OpenGL.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR)
		@Pc(78) val texelScaleS: Float = surface!!.s / surface.singleWidth.toFloat()
		@Pc(85) val texelScaleT: Float = surface.t / surface.singleLength.toFloat()
		OpenGL.glTexGenfv(OpenGLConstants.GL_S, OpenGLConstants.GL_EYE_PLANE, floatArrayOf(texelScaleS, 0.0f, 0.0f, (-offsetX).toFloat() * texelScaleS), 0)
		OpenGL.glTexGenfv(OpenGLConstants.GL_T, OpenGLConstants.GL_EYE_PLANE, floatArrayOf(0.0f, texelScaleT, 0.0f, (-offsetY).toFloat() * texelScaleT), 0)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S)
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T)
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		OpenGL.glTranslatef(x.toFloat(), y.toFloat(), 0.0f)
		this.glyphLists.callList(c)
		OpenGL.glLoadIdentity()
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S)
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T)
		this.glToolkit.setTexture(5890, 768, 0)
		this.glToolkit.loadTextureCombineMode(8448, 8448)
		this.glToolkit.setActiveTexture(null)
		this.glToolkit.activateTexture(0)
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.setTexture(5890, 768, 0)
		}
	}

	@OriginalMember(owner = "client!th", name = "fa", descriptor = "(CIIIZ)V")
	override fun fa(@OriginalArg(0) c: Char, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) shadow: Boolean) {
		this.glToolkit!!.setupTextRendering()
		this.glToolkit.setActiveTexture(this.texture)
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.loadTextureCombineMode(7681, 8448)
			this.glToolkit.setTexture(34168, 768, 0)
		} else {
			this.glToolkit.loadTextureCombineMode(7681, 7681)
		}
		OpenGL.glColor4ub((colour shr 16).toByte(), (colour shr 8).toByte(), colour.toByte(), (colour shr 24).toByte())
		OpenGL.glTranslatef(x.toFloat(), y.toFloat(), 0.0f)
		this.glyphLists.callList(c)
		OpenGL.glLoadIdentity()
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.setTexture(5890, 768, 0)
		}
	}
}
