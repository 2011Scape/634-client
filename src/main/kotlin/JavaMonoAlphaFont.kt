import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!rj")
class JavaMonoAlphaFont : AbstractFontRenderer {

	@OriginalMember(owner = "client!rj", name = "y", descriptor = "Lclient!ao;")
	private var toolkit: JavaToolkit?

	@OriginalMember(owner = "client!rj", name = "v", descriptor = "[I")
	private val glyphWidth: IntArray?

	@OriginalMember(owner = "client!rj", name = "x", descriptor = "[I")
	private val glyphHeight: IntArray?

	@OriginalMember(owner = "client!rj", name = "w", descriptor = "[[B")
	private val glyphAlpha: Array<ByteArray?>

	@OriginalMember(owner = "client!rj", name = "A", descriptor = "[I")
	private val glyphOffsetY: IntArray

	@OriginalMember(owner = "client!rj", name = "z", descriptor = "[I")
	private val glyphOffsetX: IntArray

	@OriginalMember(owner = "client!rj", name = "<init>", descriptor = "(Lclient!ao;Lclient!oea;[Lclient!rh;[I[I)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) fontMetrics: FontMetrics?, @OriginalArg(2) indexedImages: Array<SpriteType?>?, @OriginalArg(3) glyphWidth: IntArray?, @OriginalArg(4) glyphHeight: IntArray?) : super(javaToolkit, fontMetrics) {
		this.toolkit = javaToolkit
		this.toolkit = javaToolkit
		this.glyphWidth = glyphWidth
		this.glyphHeight = glyphHeight
		this.glyphAlpha = arrayOfNulls<ByteArray>(indexedImages!!.size)
		this.glyphOffsetY = IntArray(indexedImages.size)
		this.glyphOffsetX = IntArray(indexedImages.size)
		for (@Pc(32) i in 0 until indexedImages.size) {
			@Pc(37) val indexedImage: SpriteType = indexedImages[i]!!
			if (indexedImage.alpha == null) {
				@Pc(50) val pixels: ByteArray = indexedImage.pixels!!
				@Pc(59) val alpha: ByteArray = ByteArray(pixels.size)
				this.glyphAlpha[i] = alpha
				for (@Pc(61) j in 0 until pixels.size) {
					alpha[j] = (if (pixels[j].toInt() == 0) 0 else -1).toByte()
				}
			} else {
				this.glyphAlpha[i] = indexedImage.alpha
			}
			this.glyphOffsetY[i] = indexedImage.minY
			this.glyphOffsetX[i] = indexedImage.minX
		}
	}

	@OriginalMember(owner = "client!rj", name = "a", descriptor = "([B[IIIIIIIIIIILclient!aa;II)V")
	private fun blitMasked(@OriginalArg(0) src: ByteArray, @OriginalArg(1) dst: IntArray, @OriginalArg(2) colour: Int, @OriginalArg(3) srcIndex: Int, @OriginalArg(4) dstIndex: Int, @OriginalArg(5) width: Int, @OriginalArg(6) height: Int, @OriginalArg(7) dstStep: Int, @OriginalArg(8) srcStep: Int, @OriginalArg(9) x: Int, @OriginalArg(10) y: Int, @OriginalArg(11) srcStride: Int, @OriginalArg(12) mask: ComponentSurface?, @OriginalArg(13) maskX: Int, @OriginalArg(14) maskY: Int) {
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
		for (@Pc(58) row in startY until endY) {
			@Pc(67) val lineX: Int = lineOffsets[row - maskY] + maskX
			@Pc(73) var lineWidth: Int = lineWidths!![row - maskY]
			@Pc(75) var remaining: Int = width
			@Pc(82) var skip: Int
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
			for (@Pc(150) column in -lineWidth until 0) {
				@Pc(158) var alpha: Int = src[srcIndex++].toInt() and 0xFF
				if (alpha == 0) {
					dstIndex++
				} else {
					@Pc(178) val blendedColour: Int = (((colour and 0xFF00FF) * alpha and 0xFF00FF00.toInt()) + ((colour and 0xFF00) * alpha and 0xFF0000)) shr 8
					alpha = 256 - alpha
					@Pc(186) val background: Int = dst[dstIndex]
					dst[dstIndex++] = ((((background and 0xFF00FF) * alpha and 0xFF00FF00.toInt()) + ((background and 0xFF00) * alpha and 0xFF0000)) shr 8) + blendedColour
				}
			}
			srcIndex += skip + srcStep
			dstIndex += skip + dstStep
		}
	}

	@OriginalMember(owner = "client!rj", name = "a", descriptor = "([B[IIIIIIII)V")
	private fun blendGlyphPixels(@OriginalArg(0) glyphData: ByteArray, @OriginalArg(1) pixels: IntArray, @OriginalArg(2) color: Int, @OriginalArg(3) glyphOffset: Int, @OriginalArg(4) pixelOffset: Int, @OriginalArg(5) clippedWidth: Int, @OriginalArg(6) clippedHeight: Int, @OriginalArg(7) dstRowPadding: Int, @OriginalArg(8) srcRowPadding: Int) {
		var glyphOffset: Int = glyphOffset
		var pixelOffset: Int = pixelOffset
		for (@Pc(2) row in -clippedHeight until 0) {
			for (@Pc(6) column in -clippedWidth until 0) {
				@Pc(14) var alpha: Int = glyphData[glyphOffset++].toInt() and 0xFF
				if (alpha == 0) {
					pixelOffset++
				} else {
					@Pc(34) val blendedColour: Int = (((color and 0xFF00FF) * alpha and 0xFF00FF00.toInt()) + ((color and 0xFF00) * alpha and 0xFF0000)) shr 8
					alpha = 256 - alpha
					@Pc(42) val background: Int = pixels[pixelOffset]
					pixels[pixelOffset++] = ((((background and 0xFF00FF) * alpha and 0xFF00FF00.toInt()) + ((background and 0xFF00) * alpha and 0xFF0000)) shr 8) + blendedColour
				}
			}
			pixelOffset += dstRowPadding
			glyphOffset += srcRowPadding
		}
	}

	@OriginalMember(owner = "client!rj", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
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
		if (width > 0 && height > 0) {
			this.blitMasked(this.glyphAlpha[c.code]!!, this.toolkit!!.surfaceRaster!!, colour, srcIndex, dstIndex, width, height, dstStep, srcStep, x, y, this.glyphWidth!![c.code], mask, offsetX, offsetY)
		}
	}

	@OriginalMember(owner = "client!rj", name = "fa", descriptor = "(CIIIZ)V")
	override fun fa(@OriginalArg(0) c: Char, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) color: Int, @OriginalArg(4) shadow: Boolean) {
		var x: Int = x
		var y: Int = y
		x += this.glyphOffsetX[c.code]
		y += this.glyphOffsetY[c.code]
		@Pc(18) var width: Int = this.glyphWidth!![c.code]
		@Pc(23) var height: Int = this.glyphHeight!![c.code]
		@Pc(27) val surfaceWidth: Int = this.toolkit!!.surfaceWidth
		@Pc(33) var pixelOffset: Int = x + y * surfaceWidth
		@Pc(37) var dstRowPadding: Int = surfaceWidth - width
		@Pc(39) var srcRowPadding: Int = 0
		@Pc(41) var glyphOffset: Int = 0
		@Pc(52) var clip: Int
		if (y < this.toolkit!!.clipY1) {
			clip = this.toolkit!!.clipY1 - y
			height -= clip
			y = this.toolkit!!.clipY1
			glyphOffset += clip * width
			pixelOffset += clip * surfaceWidth
		}
		if (y + height > this.toolkit!!.clipY2) {
			height -= y + height - this.toolkit!!.clipY2
		}
		if (x < this.toolkit!!.clipX1) {
			clip = this.toolkit!!.clipX1 - x
			width -= clip
			x = this.toolkit!!.clipX1
			glyphOffset += clip
			pixelOffset += clip
			srcRowPadding += clip
			dstRowPadding += clip
		}
		if (x + width > this.toolkit!!.clipX2) {
			clip = x + width - this.toolkit!!.clipX2
			width -= clip
			srcRowPadding += clip
			dstRowPadding += clip
		}
		if (width > 0 && height > 0) {
			this.blendGlyphPixels(this.glyphAlpha[c.code]!!, this.toolkit!!.surfaceRaster!!, color, glyphOffset, pixelOffset, width, height, dstRowPadding, srcRowPadding)
		}
	}
}
