import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!m")
class JavaFont : AbstractFontRenderer {

	@OriginalMember(owner = "client!m", name = "y", descriptor = "Lclient!ao;")
	private var toolkit: JavaToolkit?

	@OriginalMember(owner = "client!m", name = "z", descriptor = "[I")
	private val glyphWidth: IntArray?

	@OriginalMember(owner = "client!m", name = "w", descriptor = "[I")
	private val glyphHeight: IntArray?

	@OriginalMember(owner = "client!m", name = "D", descriptor = "[[B")
	private val glyphRaster: Array<ByteArray?>

	@OriginalMember(owner = "client!m", name = "B", descriptor = "[I")
	private val glyphOffsetY: IntArray

	@OriginalMember(owner = "client!m", name = "x", descriptor = "[I")
	private val glyphOffsetX: IntArray

	@OriginalMember(owner = "client!m", name = "v", descriptor = "[I")
	private val palette: IntArray?

	@OriginalMember(owner = "client!m", name = "<init>", descriptor = "(Lclient!ao;Lclient!oea;[Lclient!rh;[I[I)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) fontMetrics: FontMetrics?, @OriginalArg(2) indexedImages: Array<SpriteType?>?, @OriginalArg(3) glyphWidth: IntArray?, @OriginalArg(4) glyphHeight: IntArray?) : super(javaToolkit, fontMetrics) {
		this.toolkit = javaToolkit
		this.toolkit = javaToolkit
		this.glyphWidth = glyphWidth
		this.glyphHeight = glyphHeight
		this.glyphRaster = arrayOfNulls<ByteArray>(indexedImages!!.size)
		this.glyphOffsetY = IntArray(indexedImages.size)
		this.glyphOffsetX = IntArray(indexedImages.size)
		for (@Pc(32) i in 0 until indexedImages.size) {
			this.glyphRaster[i] = indexedImages[i]!!.pixels
			this.glyphOffsetY[i] = indexedImages[i]!!.minY
			this.glyphOffsetX[i] = indexedImages[i]!!.minX
		}
		this.palette = indexedImages[0]!!.pallete
	}

	@OriginalMember(owner = "client!m", name = "a", descriptor = "([B[IIIIIIII)V")
	private fun blitSolid(@OriginalArg(0) src: ByteArray, @OriginalArg(1) dst: IntArray, @OriginalArg(2) colour: Int, @OriginalArg(3) srcIndex: Int, @OriginalArg(4) dstIndex: Int, @OriginalArg(5) width: Int, @OriginalArg(6) height: Int, @OriginalArg(7) dstStep: Int, @OriginalArg(8) srcStep: Int) {
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

	@OriginalMember(owner = "client!m", name = "a", descriptor = "([B[I[IIIIIII)V")
	private fun blitPalette(@OriginalArg(0) src: ByteArray, @OriginalArg(1) dst: IntArray, @OriginalArg(2) palette: IntArray?, @OriginalArg(3) srcIndex: Int, @OriginalArg(4) dstIndex: Int, @OriginalArg(5) width: Int, @OriginalArg(6) height: Int, @OriginalArg(7) dstStep: Int, @OriginalArg(8) srcStep: Int) {
		var srcIndex: Int = srcIndex
		var dstIndex: Int = dstIndex
		@Pc(4) val blocks: Int = -(width shr 2)
		@Pc(9) val remainder: Int = -(width and 0x3)
		for (@Pc(14) row in -height until 0) {
			for (@Pc(17) block in blocks until 0) {
				@Pc(24) val pixel0: Byte = src[srcIndex++]
				if (pixel0.toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = palette!![pixel0.toInt() and 0xFF]
				}
				@Pc(42) val pixel1: Byte = src[srcIndex++]
				if (pixel1.toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = palette!![pixel1.toInt() and 0xFF]
				}
				@Pc(60) val pixel2: Byte = src[srcIndex++]
				if (pixel2.toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = palette!![pixel2.toInt() and 0xFF]
				}
				@Pc(78) val pixel3: Byte = src[srcIndex++]
				if (pixel3.toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = palette!![pixel3.toInt() and 0xFF]
				}
			}
			for (@Pc(95) column in remainder until 0) {
				@Pc(102) val pixel: Byte = src[srcIndex++]
				if (pixel.toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = palette!![pixel.toInt() and 0xFF]
				}
			}
			dstIndex += dstStep
			srcIndex += srcStep
		}
	}

	@OriginalMember(owner = "client!m", name = "a", descriptor = "([B[IIIIIIIIIIILclient!aa;II)V")
	private fun blitSolidMasked(@OriginalArg(0) src: ByteArray, @OriginalArg(1) dst: IntArray, @OriginalArg(2) colour: Int, @OriginalArg(3) srcIndex: Int, @OriginalArg(4) dstIndex: Int, @OriginalArg(5) width: Int, @OriginalArg(6) height: Int, @OriginalArg(7) dstStep: Int, @OriginalArg(8) srcStep: Int, @OriginalArg(9) x: Int, @OriginalArg(10) y: Int, @OriginalArg(11) srcStride: Int, @OriginalArg(12) mask: ComponentSurface?, @OriginalArg(13) maskX: Int, @OriginalArg(14) maskY: Int) {
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
				if (src[srcIndex++].toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = colour
				}
			}
			srcIndex += skip + srcIndex
			dstIndex += skip + dstStep
		}
	}

	@OriginalMember(owner = "client!m", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
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
		if (shadow) {
			this.blitSolidMasked(this.glyphRaster[c.code]!!, this.toolkit!!.surfaceRaster!!, colour, srcIndex, dstIndex, width, height, dstStep, srcStep, x, y, this.glyphWidth!![c.code], mask, offsetX, offsetY)
		} else {
			this.blitPaletteMasked(this.glyphRaster[c.code]!!, this.toolkit!!.surfaceRaster!!, this.palette, colour, srcIndex, dstIndex, width, height, dstStep, srcStep, x, y, this.glyphWidth!![c.code], mask, offsetX, offsetY)
		}
	}

	@OriginalMember(owner = "client!m", name = "fa", descriptor = "(CIIIZ)V")
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
		if (shadow) {
			this.blitSolid(this.glyphRaster[c.code]!!, this.toolkit!!.surfaceRaster!!, colour, srcIndex, dstIndex, width, height, dstStep, srcStep)
		} else {
			this.blitPalette(this.glyphRaster[c.code]!!, this.toolkit!!.surfaceRaster!!, this.palette, srcIndex, dstIndex, width, height, dstStep, srcStep)
		}
	}

	@OriginalMember(owner = "client!m", name = "a", descriptor = "([B[I[IIIIIIIIIIILclient!aa;II)V")
	private fun blitPaletteMasked(@OriginalArg(0) src: ByteArray, @OriginalArg(1) dst: IntArray, @OriginalArg(2) palette: IntArray?, @OriginalArg(3) colour: Int, @OriginalArg(4) srcIndex: Int, @OriginalArg(5) dstIndex: Int, @OriginalArg(6) width: Int, @OriginalArg(7) height: Int, @OriginalArg(8) dstStep: Int, @OriginalArg(9) srcStep: Int, @OriginalArg(10) x: Int, @OriginalArg(11) y: Int, @OriginalArg(12) srcStride: Int, @OriginalArg(13) mask: ComponentSurface?, @OriginalArg(14) maskX: Int, @OriginalArg(15) maskY: Int) {
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
		for (@Pc(60) row in startY until endY) {
			@Pc(69) val lineX: Int = lineOffsets[row - maskY] + maskX
			@Pc(75) var lineWidth: Int = lineWidths!![row - maskY]
			@Pc(77) var remaining: Int = width
			@Pc(84) var skip: Int
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
			for (@Pc(152) column in -lineWidth until 0) {
				@Pc(159) val pixel: Byte = src[srcIndex++]
				if (pixel.toInt() == 0) {
					dstIndex++
				} else {
					dst[dstIndex++] = palette!![pixel.toInt() and 0xFF]
				}
			}
			srcIndex += skip + srcStep
			dstIndex += skip + dstStep
		}
	}
}
