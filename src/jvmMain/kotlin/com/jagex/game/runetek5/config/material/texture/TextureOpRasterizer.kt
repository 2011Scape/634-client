package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.material.texture.operation.BezierCurveRasterizer
import com.jagex.game.runetek5.config.material.texture.operation.EllipseRasterizer
import com.jagex.game.runetek5.config.material.texture.operation.LineRasterizer
import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.graphics.TextureOpRaster
import com.jagex.game.runetek5.graphics.texture.TextureOpShapeRaster
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ft")
class TextureOpRasterizer : TextureOperation {

	@OriginalMember(owner = "client!ft", name = "I", descriptor = "[Lclient!iu;")
	private var shapes: Array<TextureShapeRasterizer?>? = null

	@OriginalMember(owner = "client!ft", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!ft", name = "a", descriptor = "([[IB)V")
	private fun render(@OriginalArg(0) dest: Array<IntArray?>?) {
		@Pc(12) val width: Int = EnvironmentLight.textureOpWidth
		@Pc(14) val height: Int = TextureOperation.currentHeight
		TextureOpRaster.setPixelBuffer(dest)
		TextureOpRasterizer.setClipBounds(TextureRaster.widthMask, TextureOperation.columnEnd)
		if (this.shapes == null) {
			return
		}
		@Pc(30) var index: Int = 0
		while (index < this.shapes!!.size) {
			@Pc(37) val shape: TextureShapeRasterizer? = this.shapes!![index]
			@Pc(40) val fillColour: Int = shape!!.fillColor
			@Pc(43) val lineColour: Int = shape.strokeColor
			if (fillColour < 0) {
				if (lineColour >= 0) {
					shape.drawStroked(height, width)
				}
			} else if (lineColour < 0) {
				shape.drawFilled(width, height)
			} else {
				shape.drawFilledAndStroked(width, height)
			}
			index++
		}
	}

	@OriginalMember(owner = "client!ft", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray? {
		@Pc(11) val output: IntArray? = super.monochromeImageCache!!.get(row)
		if (super.monochromeImageCache!!.dirty) {
			this.render(super.monochromeImageCache!!.getAllPaletteData())
		}
		return output
	}

	@OriginalMember(owner = "client!ft", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.shapes = arrayOfNulls<TextureShapeRasterizer>(packet!!.readUByte())
			@Pc(16) var i: Int = 0
			while (i < this.shapes!!.size) {
				@Pc(22) val shapeType: Int = packet.readUByte()
				if (shapeType == 0) {
					this.shapes!![i] = LineRasterizer.decode(packet)
				} else if (shapeType == 1) {
					this.shapes!![i] = BezierCurveRasterizer.decode(packet)
				} else if (shapeType == 2) {
					this.shapes!![i] = TextureOpShapeRaster.decodeRectangle(packet)
				} else if (shapeType == 3) {
					this.shapes!![i] = EllipseRasterizer.decode(packet)
				}
				i++
			}
		} else if (opcode == 1) {
			super.monochrome = packet!!.readUByte() == 1
		}
	}

	@OriginalMember(owner = "client!ft", name = "d", descriptor = "(II)[[I")
	override fun getColorOutput(@OriginalArg(0) count: Int): Array<IntArray?>? {
		@Pc(13) val output: Array<IntArray?>? = super.colorImageCache!!.get(count)
		if (super.colorImageCache!!.dirty) {
			@Pc(24) val textureOpWidth: Int = EnvironmentLight.textureOpWidth
			@Pc(26) val textureOpHeight: Int = TextureOperation.currentHeight
			@Pc(30) val pixels: Array<IntArray?> = Array<IntArray?>(textureOpHeight) { IntArray(textureOpWidth) }
			@Pc(35) val rgbData: Array<Array<IntArray?>?>? = super.colorImageCache!!.getAllPalettes()
			this.render(pixels)
			@Pc(41) var i: Int = 0
			while (i < TextureOperation.currentHeight) {
				@Pc(47) val pixelRow: IntArray? = pixels[i]
				@Pc(51) val rgbRow: Array<IntArray?>? = rgbData!![i]
				@Pc(55) val redChannel: IntArray? = rgbRow!![0]
				@Pc(59) val greenChannel: IntArray? = rgbRow[1]
				@Pc(63) val blueChannel: IntArray? = rgbRow[2]
				@Pc(65) var j: Int = 0
				while (j < EnvironmentLight.textureOpWidth) {
					@Pc(71) val rgb: Int = pixelRow!![j]
					blueChannel!![j] = (rgb and 0xFF) shl 4
					greenChannel!![j] = (rgb shr 4) and 0xFF0
					redChannel!![j] = (rgb shr 12) and 0xFF0
					j++
				}
				i++
			}
		}
		return output
	}

	companion object {
		@OriginalMember(owner = "client!mk", name = "a", descriptor = "(IIIII)V")
		@JvmStatic
		fun setClipBounds(@OriginalArg(0) maxX: Int, @OriginalArg(2) maxY: Int) {
			TextureOpRasterizer.textureOpClipMinX = 0
			TextureOpRasterizer.clipMaxY = maxY
			TextureOpRasterizer.clipMaxX = maxX
			TextureOpRasterizer.clipMinY = 0
		}

		@OriginalMember(owner = "client!bf", name = "g", descriptor = "I")
		@JvmField
		var clipMaxX: Int = 100

		@OriginalMember(owner = "client!fn", name = "b", descriptor = "I")
		@JvmField
		var textureOpClipMinX: Int = 0

		@OriginalMember(owner = "client!iea", name = "q", descriptor = "I")
		@JvmField
		var clipMaxY: Int = 100

		@OriginalMember(owner = "client!nr", name = "f", descriptor = "I")
		@JvmField
		var clipMinY: Int = 0
	}
}
