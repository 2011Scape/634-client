import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!dv")
class JavaMonoFont : AbstractFontRenderer {

	@OriginalMember(owner = "client!dv", name = "v", descriptor = "Lclient!ao;")
	private val toolkit: JavaToolkit?

	@OriginalMember(owner = "client!dv", name = "y", descriptor = "[I")
	private val glyphWidth: IntArray?

	@OriginalMember(owner = "client!dv", name = "x", descriptor = "[I")
	private val glyphHeight: IntArray?

	@OriginalMember(owner = "client!dv", name = "z", descriptor = "[[B")
	private val glyphRaster: Array<ByteArray?>

	@OriginalMember(owner = "client!dv", name = "w", descriptor = "[I")
	private val glyphOffsetY: IntArray

	@OriginalMember(owner = "client!dv", name = "A", descriptor = "[I")
	private val glyphOffsetX: IntArray

	@OriginalMember(owner = "client!dv", name = "<init>", descriptor = "(Lclient!ao;Lclient!oea;[Lclient!rh;[I[I)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) fontMetrics: FontMetrics?, @OriginalArg(2) indexedImages: Array<SpriteType?>?, @OriginalArg(3) glyphWidth: IntArray?, @OriginalArg(4) glyphHeight: IntArray?) : super(javaToolkit, fontMetrics) {
		this.toolkit = javaToolkit
		this.glyphWidth = glyphWidth
		this.glyphHeight = glyphHeight
		this.glyphRaster = arrayOfNulls<ByteArray>(indexedImages!!.size)
		this.glyphOffsetY = IntArray(indexedImages.size)
		this.glyphOffsetX = IntArray(indexedImages.size)
		for (@Pc(29) i in 0 until indexedImages.size) {
			this.glyphRaster[i] = indexedImages[i]!!.pixels
			this.glyphOffsetY[i] = indexedImages[i]!!.minY
			this.glyphOffsetX[i] = indexedImages[i]!!.minX
		}
	}

	@OriginalMember(owner = "client!dv", name = "fa", descriptor = "(CIIIZ)V")
	override fun fa(@OriginalArg(0) c: Char, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) shadow: Boolean) {
		var x: Int = x
		var y: Int = y
		x += this.glyphOffsetX[c.code]
		y += this.glyphOffsetY[c.code]
		@Pc(18) var width: Int = this.glyphWidth!![c.code]
		@Pc(23) var height: Int = this.glyphHeight!![c.code]
		@Pc(27) val surfaceWidth: Int = this.toolkit!!.surfaceWidth
		@Pc(33) var dstIndex: Int = x + y * surfaceWidth
		@Pc(37) var dstStep: Int = surfaceWidth - width
		@Pc(39) var srcStep: Int = 0
		@Pc(41) var srcIndex: Int = 0
		@Pc(52) var clip: Int
		if (y < this.toolkit!!.clipY1) {
			clip = this.toolkit!!.clipY1 - y
			height -= clip
			y = this.toolkit!!.clipY1
			srcIndex += clip * width
			dstIndex += clip * surfaceWidth
		}
		if (y + height > this.toolkit!!.clipY2) {
			height -= y + height - this.toolkit!!.clipY2
		}
		if (x < this.toolkit!!.clipX1) {
			clip = this.toolkit!!.clipX1 - x
			width -= clip
			x = this.toolkit!!.clipX1
			srcIndex += clip
			dstIndex += clip
			srcStep += clip
			dstStep += clip
		}
		if (x + width > this.toolkit!!.clipX2) {
			clip = x + width - this.toolkit!!.clipX2
			width -= clip
			srcStep += clip
			dstStep += clip
		}
		if (width <= 0 || height <= 0) {
			return
		}
		if ((colour and -16777216) == -16777216) {
			this.blitOpaque(this.glyphRaster[c.code]!!, this.toolkit!!.surfaceRaster!!, colour, srcIndex, dstIndex, width, height, dstStep, srcStep)
		} else if ((colour and 0xFF000000.toInt()) != 0) {
			this.blendImage(this.glyphRaster[c.code]!!, this.toolkit!!.surfaceRaster!!, colour, srcIndex, dstIndex, width, height, dstStep, srcStep)
		}
	}

	@OriginalMember(owner = "client!dv", name = "a", descriptor = "([B[IIIIIIII)V")
	private fun blendImage(@OriginalArg(0) glyphData: ByteArray, @OriginalArg(1) pixels: IntArray, @OriginalArg(2) color: Int, @OriginalArg(3) glyphOffset: Int, @OriginalArg(4) pixelOffset: Int, @OriginalArg(5) width: Int, @OriginalArg(6) height: Int, @OriginalArg(7) dstRowPadding: Int, @OriginalArg(8) srcRowPadding: Int) {
		var glyphOffset: Int = glyphOffset
		var pixelOffset: Int = pixelOffset
		@Pc(3) val alpha: Int = color ushr 24
		@Pc(7) val invAlpha: Int = 255 - alpha
		for (@Pc(10) row in -height until 0) {
			for (@Pc(14) column in -width until 0) {
				if (glyphData[glyphOffset++].toInt() == 0) {
					pixelOffset++
				} else {
					@Pc(38) val blendedColour: Int = (((color and 0xFF00FF) * alpha and 0xFF00FF00.toInt()) + ((color and 0xFF00) * alpha and 0xFF0000)) shr 8
					@Pc(42) val background: Int = pixels[pixelOffset]
					pixels[pixelOffset++] = ((((background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((background and 0xFF00) * invAlpha and 0xFF0000)) shr 8) + blendedColour
				}
			}
			pixelOffset += dstRowPadding
			glyphOffset += srcRowPadding
		}
	}

	@OriginalMember(owner = "client!dv", name = "a", descriptor = "([B[IIIIIIIIIIILclient!aa;II)V")
	private fun drawGlyphOpaqueMasked(@OriginalArg(0) glyphData: ByteArray, @OriginalArg(1) pixels: IntArray, @OriginalArg(2) color: Int, @OriginalArg(3) glyphOffset: Int, @OriginalArg(4) pixelOffset: Int, @OriginalArg(5) clippedWidth: Int, @OriginalArg(6) clippedHeight: Int, @OriginalArg(7) dstRowPadding: Int, @OriginalArg(8) srcRowPadding: Int, @OriginalArg(9) drawX: Int, @OriginalArg(10) drawY: Int, @OriginalArg(11) fullGlyphWidth: Int, @OriginalArg(12) surface: ComponentSurface?, @OriginalArg(13) surfaceOffsetX: Int, @OriginalArg(14) surfaceOffsetY: Int) {
		var glyphOffset: Int = glyphOffset
		var pixelOffset: Int = pixelOffset
		@Pc(2) val javaMask: JavaClippingMask = surface as JavaClippingMask
		@Pc(5) val lineOffsets: IntArray = javaMask.lineWidths!!
		@Pc(8) val lineWidths: IntArray? = javaMask.lineOffsets
		@Pc(10) var startY: Int = drawY
		if (surfaceOffsetY > drawY) {
			startY = surfaceOffsetY
			pixelOffset += (surfaceOffsetY - drawY) * this.toolkit!!.surfaceWidth
			glyphOffset += (surfaceOffsetY - drawY) * fullGlyphWidth
		}
		@Pc(50) val endY: Int = if (surfaceOffsetY + lineOffsets.size < drawY + clippedHeight) surfaceOffsetY + lineOffsets.size else drawY + clippedHeight
		for (@Pc(52) row in startY until endY) {
			@Pc(61) val lineX: Int = surfaceOffsetX + lineOffsets[row - surfaceOffsetY]
			@Pc(67) var lineWidth: Int = lineWidths!![row - surfaceOffsetY]
			@Pc(69) var remaining: Int = clippedWidth
			@Pc(76) var skip: Int
			if (drawX > lineX) {
				skip = drawX - lineX
				if (skip >= lineWidth) {
					glyphOffset += clippedWidth + srcRowPadding
					pixelOffset += clippedWidth + dstRowPadding
					continue
				}
				lineWidth -= skip
			} else {
				skip = lineX - drawX
				if (skip >= clippedWidth) {
					glyphOffset += clippedWidth + srcRowPadding
					pixelOffset += clippedWidth + dstRowPadding
					continue
				}
				glyphOffset += skip
				remaining = clippedWidth - skip
				pixelOffset += skip
			}
			skip = 0
			if (remaining < lineWidth) {
				lineWidth = remaining
			} else {
				skip = remaining - lineWidth
			}
			for (@Pc(143) column in 0 until lineWidth) {
				if (glyphData[glyphOffset++].toInt() == 0) {
					pixelOffset++
				} else {
					pixels[pixelOffset++] = color
				}
			}
			glyphOffset += skip + srcRowPadding
			pixelOffset += skip + dstRowPadding
		}
	}

	@OriginalMember(owner = "client!dv", name = "b", descriptor = "([B[IIIIIIII)V")
	private fun blitOpaque(@OriginalArg(0) src: ByteArray, @OriginalArg(1) dst: IntArray, @OriginalArg(2) colour: Int, @OriginalArg(3) srcIndex: Int, @OriginalArg(4) dstIndex: Int, @OriginalArg(5) width: Int, @OriginalArg(6) height: Int, @OriginalArg(7) dstStep: Int, @OriginalArg(8) srcStep: Int) {
		var srcIndex: Int = srcIndex
		var dstIndex: Int = dstIndex
		@Pc(4) val blocks: Int = -(width shr 2)
		@Pc(9) val remainder: Int = -(width and 0x3)
		for (@Pc(12) row in -height until 0) {
			for (@Pc(15) block in blocks until 0) {
				if (src[srcIndex++].toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = colour
				}
				if (src[srcIndex++].toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = colour
				}
				if (src[srcIndex++].toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = colour
				}
				if (src[srcIndex++].toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = colour
				}
			}
			for (@Pc(69) column in remainder until 0) {
				if (src[srcIndex++].toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = colour
				}
			}
			dstIndex += dstStep
			srcIndex += srcStep
		}
	}

	@OriginalMember(owner = "client!dv", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	override fun renderSymbol(@OriginalArg(0) c: Char, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) shadow: Boolean, @OriginalArg(5) mask: ComponentSurface?, @OriginalArg(6) offsetX: Int, @OriginalArg(7) offsetY: Int) {
		var x: Int = x
		var y: Int = y
		if (mask == null) {
			this.fa(c, x, y, colour, shadow)
			return
		}
		x += this.glyphOffsetX[c.code]
		y += this.glyphOffsetY[c.code]
		@Pc(28) var width: Int = this.glyphWidth!![c.code]
		@Pc(33) var height: Int = this.glyphHeight!![c.code]
		@Pc(37) val dstStride: Int = this.toolkit!!.surfaceWidth
		@Pc(43) var dstIndex: Int = x + y * dstStride
		@Pc(47) var dstStep: Int = dstStride - width
		@Pc(49) var srcStep: Int = 0
		@Pc(51) var srcIndex: Int = 0
		@Pc(62) var clip: Int
		if (y < this.toolkit!!.clipY1) {
			clip = this.toolkit!!.clipY1 - y
			height -= clip
			y = this.toolkit!!.clipY1
			srcIndex = clip * width
			dstIndex += clip * dstStride
		}
		if (y + height > this.toolkit!!.clipY2) {
			height -= y + height - this.toolkit!!.clipY2
		}
		if (x < this.toolkit!!.clipX1) {
			clip = this.toolkit!!.clipX1 - x
			width -= clip
			x = this.toolkit!!.clipX1
			srcIndex += clip
			dstIndex += clip
			srcStep = clip
			dstStep += clip
		}
		if (x + width > this.toolkit!!.clipX2) {
			clip = x + width - this.toolkit!!.clipX2
			width -= clip
			srcStep += clip
			dstStep += clip
		}
		if (width <= 0 || height <= 0) {
			return
		}
		if ((colour and -16777216) == -16777216) {
			this.drawGlyphOpaqueMasked(this.glyphRaster[c.code]!!, this.toolkit!!.surfaceRaster!!, colour, srcIndex, dstIndex, width, height, dstStep, srcStep, x, y, this.glyphWidth!![c.code], mask, offsetX, offsetY)
		} else {
			this.blitBlendedMasked(this.glyphRaster[c.code]!!, this.toolkit!!.surfaceRaster!!, colour, srcIndex, dstIndex, width, height, dstStep, srcStep, x, y, this.glyphWidth!![c.code], mask, offsetX, offsetY)
		}
	}

	@OriginalMember(owner = "client!dv", name = "b", descriptor = "([B[IIIIIIIIIIILclient!aa;II)V")
	private fun blitBlendedMasked(@OriginalArg(0) src: ByteArray, @OriginalArg(1) dst: IntArray, @OriginalArg(2) colour: Int, @OriginalArg(3) srcIndex: Int, @OriginalArg(4) dstIndex: Int, @OriginalArg(5) width: Int, @OriginalArg(6) height: Int, @OriginalArg(7) dstStep: Int, @OriginalArg(8) srcStep: Int, @OriginalArg(9) x: Int, @OriginalArg(10) y: Int, @OriginalArg(11) srcStride: Int, @OriginalArg(12) mask: ComponentSurface?, @OriginalArg(13) maskX: Int, @OriginalArg(14) maskY: Int) {
		var srcIndex: Int = srcIndex
		var dstIndex: Int = dstIndex
		@Pc(2) val javaMask: JavaClippingMask = mask as JavaClippingMask
		@Pc(5) val lineOffsets: IntArray = javaMask.lineWidths!!
		@Pc(8) val lineWidths: IntArray? = javaMask.lineOffsets
		@Pc(14) val clipOffsetX: Int = x - this.toolkit!!.clipX1
		@Pc(16) var startY: Int = y
		if (maskY > y) {
			startY = maskY
			dstIndex += (maskY - y) * this.toolkit!!.surfaceWidth
			srcIndex += (maskY - y) * srcStride
		}
		@Pc(56) val endY: Int = if (maskY + lineOffsets.size < y + height) maskY + lineOffsets.size else y + height
		@Pc(60) val alpha: Int = colour ushr 24
		@Pc(64) val invAlpha: Int = 255 - alpha
		for (@Pc(66) row in startY until endY) {
			@Pc(75) val lineX: Int = lineOffsets[row - maskY] + maskX
			@Pc(81) var lineWidth: Int = lineWidths!![row - maskY]
			@Pc(83) var remaining: Int = width
			@Pc(90) var skip: Int
			if (clipOffsetX > lineX) {
				skip = clipOffsetX - lineX
				if (skip >= lineWidth) {
					srcIndex += width + srcStep
					dstIndex += width + dstStep
					continue
				}
				lineWidth -= skip
			} else {
				skip = lineX - clipOffsetX
				if (skip >= width) {
					srcIndex += width + srcStep
					dstIndex += width + dstStep
					continue
				}
				srcIndex += skip
				remaining = width - skip
				dstIndex += skip
			}
			skip = 0
			if (remaining < lineWidth) {
				lineWidth = remaining
			} else {
				skip = remaining - lineWidth
			}
			for (@Pc(158) column in -lineWidth until 0) {
				if (src[srcIndex++].toInt() == 0) {
					dstIndex++
				} else {
					@Pc(182) val blendedColour: Int = (((colour and 0xFF00FF) * alpha and 0xFF00FF00.toInt()) + ((colour and 0xFF00) * alpha and 0xFF0000)) shr 8
					@Pc(186) val background: Int = dst[dstIndex]
					dst[dstIndex++] = ((((background and 0xFF00FF) * invAlpha and 0xFF00FF00.toInt()) + ((background and 0xFF00) * invAlpha and 0xFF0000)) shr 8) + blendedColour
				}
			}
			srcIndex += skip + srcStep
			dstIndex += skip + dstStep
		}
	}
}
