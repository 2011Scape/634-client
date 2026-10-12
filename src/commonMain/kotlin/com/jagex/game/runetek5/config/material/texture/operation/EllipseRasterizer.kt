package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.texture.TextureOpShapeRaster
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.graphics.TextureOpRaster
import kotlin.jvm.JvmStatic

@OriginalClass("client!gb")
class EllipseRasterizer @OriginalMember(owner = "client!gb", name = "<init>", descriptor = "(IIIIIII)V") constructor(@OriginalArg(0) centerX: Int, @OriginalArg(1) centerY: Int, @OriginalArg(2) radiusX: Int, @OriginalArg(3) radiusY: Int, @OriginalArg(4) fillColor: Int, @OriginalArg(5) strokeColor: Int, @OriginalArg(6) lineWidth: Int) : TextureShapeRasterizer(fillColor, strokeColor, lineWidth) {

	@OriginalMember(owner = "client!gb", name = "k", descriptor = "I")
	private val centerX: Int

	@OriginalMember(owner = "client!gb", name = "s", descriptor = "I")
	private val radiusY: Int

	@OriginalMember(owner = "client!gb", name = "j", descriptor = "I")
	private val radiusX: Int

	@OriginalMember(owner = "client!gb", name = "i", descriptor = "I")
	private val centerY: Int

	init {
		this.centerX = centerX
		this.radiusY = radiusY
		this.radiusX = radiusX
		this.centerY = centerY
	}

	@OriginalMember(owner = "client!gb", name = "a", descriptor = "(III)V")
	override fun drawFilledAndStroked(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int) {
		@Pc(15) val scaledCenterX: Int = (this.centerX * width) shr 12
		@Pc(22) val scaledRadiusX: Int = (width * this.radiusX) shr 12
		@Pc(29) val scaledCenterY: Int = (this.centerY * height) shr 12
		@Pc(36) val scaledRadiusY: Int = (height * this.radiusY) shr 12
		EllipseRasterizer.drawEllipse(scaledRadiusY, scaledCenterX, this.strokeColor, this.fillColor, this.strokeWidth, scaledCenterY, scaledRadiusX)
	}

	@OriginalMember(owner = "client!gb", name = "c", descriptor = "(III)V")
	override fun drawStroked(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int) {
	}

	@OriginalMember(owner = "client!gb", name = "b", descriptor = "(III)V")
	override fun drawFilled(@OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
		@Pc(16) val scaledCenterX: Int = (width * this.centerX) shr 12
		@Pc(23) val scaledRadiusX: Int = (this.radiusX * width) shr 12
		@Pc(30) val scaledCenterY: Int = (this.centerY * height) shr 12
		@Pc(37) val scaledRadiusY: Int = (height * this.radiusY) shr 12
		EllipseRasterizer.drawFilledEllipse(scaledCenterY, scaledRadiusY, this.fillColor, scaledRadiusX, scaledCenterX)
	}

	companion object {
		@OriginalMember(owner = "client!uk", name = "a", descriptor = "(Lclient!cea;I)Lclient!gb;")
		@JvmStatic
		fun decode(@OriginalArg(0) packet: Packet?): EllipseRasterizer {
			return EllipseRasterizer(packet!!.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readUMedium(), packet.readUMedium(), packet.readUByte())
		}

		@OriginalMember(owner = "client!vc", name = "a", descriptor = "(IIIIIIII)V")
		@JvmStatic
		fun drawEllipse(@OriginalArg(1) radiusY: Int, @OriginalArg(2) centerX: Int, @OriginalArg(3) strokeColor: Int, @OriginalArg(4) fillColor: Int, @OriginalArg(5) thickness: Int, @OriginalArg(6) centerY: Int, @OriginalArg(7) radiusX: Int) {
			if (radiusY == radiusX) {
				TextureOpRaster.fillCircleWithStroke(strokeColor, radiusX, thickness, centerY, fillColor, centerX)
			} else if (TextureOpRasterizer.textureOpClipMinX <= centerX - radiusX && radiusX + centerX <= TextureOpRasterizer.clipMaxX && centerY - radiusY >= TextureOpRasterizer.clipMinY && radiusY + centerY <= TextureOpRasterizer.clipMaxY) {
				EllipseRasterizer.drawUnclippedEllipse(radiusY, fillColor, radiusX, strokeColor, centerX, centerY, thickness)
			} else {
				TextureOpShapeRaster.drawClippedEllipse(radiusX, centerX, centerY, radiusY, fillColor, strokeColor, thickness)
			}
		}

		@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIBIII)V")
		@JvmStatic
		fun drawFilledEllipse(@OriginalArg(0) centerY: Int, @OriginalArg(1) radiusY: Int, @OriginalArg(3) color: Int, @OriginalArg(4) radiusX: Int, @OriginalArg(5) centerX: Int) {
			if (radiusY == radiusX) {
				TextureOpRaster.drawFilledCircle(centerY, color, centerX, radiusX)
			} else if (TextureOpRasterizer.textureOpClipMinX <= centerX - radiusX && centerX + radiusX <= TextureOpRasterizer.clipMaxX && TextureOpRasterizer.clipMinY <= centerY - radiusY && TextureOpRasterizer.clipMaxY >= centerY + radiusY) {
				TextureOpRaster.fillEllipseUnclipped(color, centerX, centerY, radiusY, radiusX)
			} else {
				TextureOpRaster.fillEllipseClipped(centerX, radiusY, centerY, color, radiusX)
			}
		}

		@OriginalMember(owner = "client!pi", name = "a", descriptor = "(IIIIIBII)V")
		@JvmStatic
		fun drawUnclippedEllipse(@OriginalArg(0) outerRy: Int, @OriginalArg(1) fillColor: Int, @OriginalArg(2) outerRx: Int, @OriginalArg(3) strokeColor: Int, @OriginalArg(4) cx: Int, @OriginalArg(6) cy: Int, @OriginalArg(7) thickness: Int) {
			@Pc(7) var outerX: Int = 0
			@Pc(9) var outerY: Int = outerRy
			@Pc(11) var innerX: Int = 0
			@Pc(16) val innerRx: Int = outerRx - thickness
			@Pc(21) val innerRy: Int = outerRy - thickness
			@Pc(25) val outerRxSq: Int = outerRx * outerRx
			@Pc(29) val outerRySq: Int = outerRy * outerRy
			@Pc(33) val innerRxSq: Int = innerRx * innerRx
			@Pc(37) val innerRySq: Int = innerRy * innerRy
			@Pc(41) val outerRySq2: Int = outerRySq shl 1
			@Pc(45) val outerRxSq2: Int = outerRxSq shl 1
			@Pc(49) val innerRySq2: Int = innerRySq shl 1
			@Pc(53) val innerRxSq2: Int = innerRxSq shl 1
			@Pc(57) val outerDiam: Int = outerRy shl 1
			@Pc(61) val innerDiam: Int = innerRy shl 1
			@Pc(70) var outerDecA: Int = outerRySq2 + (1 - outerDiam) * outerRxSq
			@Pc(79) var outerDecB: Int = outerRySq - outerRxSq2 * (outerDiam - 1)
			@Pc(88) var innerDecA: Int = innerRySq2 + (1 - innerDiam) * innerRxSq
			@Pc(97) var innerDecB: Int = innerRySq - (innerDiam - 1) * innerRxSq2
			@Pc(101) val outerRxSq4: Int = outerRxSq shl 2
			@Pc(105) val outerRySq4: Int = outerRySq shl 2
			@Pc(109) val innerRxSq4: Int = innerRxSq shl 2
			@Pc(113) val innerRySq4: Int = innerRySq shl 2
			@Pc(117) var outerGradA: Int = outerRySq2 * 3
			@Pc(123) var outerGradB: Int = (outerDiam - 3) * outerRxSq2
			@Pc(127) var innerGradA: Int = innerRySq2 * 3
			@Pc(133) var innerGradB: Int = innerRxSq2 * (innerDiam - 3)
			@Pc(135) var outerStepA: Int = outerRySq4
			@Pc(141) var outerStepB: Int = outerRxSq4 * (outerRy - 1)
			@Pc(151) var innerStepA: Int = innerRySq4
			@Pc(157) var innerStepB: Int = (innerRy - 1) * innerRxSq4
			@Pc(161) val centerRow: IntArray? = TextureOpRaster.textureOpPixelRows!![cy]
			ArrayUtils.setArrayValuesTo(cx - innerRx, centerRow, cx - outerRx, strokeColor)
			ArrayUtils.setArrayValuesTo(cx + innerRx, centerRow, cx - innerRx, fillColor)
			ArrayUtils.setArrayValuesTo(outerRx + cx, centerRow, cx + innerRx, strokeColor)
			while (outerY > 0) {
				@Pc(202) val withinInner: Boolean = innerRy >= outerY
				if (outerDecA < 0) {
					while (outerDecA < 0) {
						outerDecB += outerStepA
						outerDecA += outerGradA
						outerStepA += outerRySq4
						outerX++
						outerGradA += outerRySq4
					}
				}
				if (withinInner) {
					if (innerDecA < 0) {
						while (innerDecA < 0) {
							innerDecB += innerStepA
							innerDecA += innerGradA
							innerX++
							innerStepA += innerRySq4
							innerGradA += innerRySq4
						}
					}
					if (innerDecB < 0) {
						innerDecB += innerStepA
						innerDecA += innerGradA
						innerGradA += innerRySq4
						innerX++
						innerStepA += innerRySq4
					}
					innerDecB += -innerGradB
					innerDecA += -innerStepB
					innerGradB -= innerRxSq4
					innerStepB -= innerRxSq4
				}
				if (outerDecB < 0) {
					outerDecB += outerStepA
					outerDecA += outerGradA
					outerStepA += outerRySq4
					outerX++
					outerGradA += outerRySq4
				}
				outerDecB += -outerGradB
				outerDecA += -outerStepB
				outerGradB -= outerRxSq4
				outerY--
				outerStepB -= outerRxSq4
				@Pc(342) val topRow: Int = cy - outerY
				@Pc(347) val bottomRow: Int = cy + outerY
				@Pc(351) val rightEdge: Int = outerX + cx
				@Pc(356) val leftEdge: Int = cx - outerX
				if (withinInner) {
					@Pc(362) val innerRightEdge: Int = cx + innerX
					@Pc(367) val innerLeftEdge: Int = cx - innerX
					ArrayUtils.setArrayValuesTo(innerLeftEdge, TextureOpRaster.textureOpPixelRows!![topRow], leftEdge, strokeColor)
					ArrayUtils.setArrayValuesTo(innerRightEdge, TextureOpRaster.textureOpPixelRows!![topRow], innerLeftEdge, fillColor)
					ArrayUtils.setArrayValuesTo(rightEdge, TextureOpRaster.textureOpPixelRows!![topRow], innerRightEdge, strokeColor)
					ArrayUtils.setArrayValuesTo(innerLeftEdge, TextureOpRaster.textureOpPixelRows!![bottomRow], leftEdge, strokeColor)
					ArrayUtils.setArrayValuesTo(innerRightEdge, TextureOpRaster.textureOpPixelRows!![bottomRow], innerLeftEdge, fillColor)
					ArrayUtils.setArrayValuesTo(rightEdge, TextureOpRaster.textureOpPixelRows!![bottomRow], innerRightEdge, strokeColor)
				} else {
					ArrayUtils.setArrayValuesTo(rightEdge, TextureOpRaster.textureOpPixelRows!![topRow], leftEdge, strokeColor)
					ArrayUtils.setArrayValuesTo(rightEdge, TextureOpRaster.textureOpPixelRows!![bottomRow], leftEdge, strokeColor)
				}
			}
		}

		@OriginalMember(owner = "client!go", name = "a", descriptor = "(IIIII)V")
		@JvmStatic
		fun fillClippedCircle(@OriginalArg(1) centerX: Int, @OriginalArg(2) color: Int, @OriginalArg(3) centerY: Int, @OriginalArg(4) radius: Int) {
			@Pc(7) var dy: Int = 0
			@Pc(14) var dx: Int = radius
			@Pc(17) var error: Int = -radius
			@Pc(19) var errorStep: Int = -1
			@Pc(27) val right: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, radius + centerX, TextureOpRasterizer.textureOpClipMinX)
			@Pc(36) val left: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - radius, TextureOpRasterizer.textureOpClipMinX)
			ArrayUtils.setArrayValuesTo(right, TextureOpRaster.textureOpPixelRows!![centerY], left, color)
			while (dx > dy) {
				errorStep += 2
				error += errorStep
				@Pc(68) var topRow: Int
				@Pc(72) var bottomRow: Int
				@Pc(94) var spanRight: Int
				@Pc(103) var spanLeft: Int
				if (error > 0) {
					dx--
					error -= dx shl 1
					topRow = centerY - dx
					bottomRow = dx + centerY
					if (TextureOpRasterizer.clipMinY <= bottomRow && topRow <= TextureOpRasterizer.clipMaxY) {
						spanRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, dy + centerX, TextureOpRasterizer.textureOpClipMinX)
						spanLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - dy, TextureOpRasterizer.textureOpClipMinX)
						if (bottomRow <= TextureOpRasterizer.clipMaxY) {
							ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows!![bottomRow], spanLeft, color)
						}
						if (topRow >= TextureOpRasterizer.clipMinY) {
							ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows!![topRow], spanLeft, color)
						}
					}
				}
				dy++
				topRow = centerY - dy
				bottomRow = centerY + dy
				if (bottomRow >= TextureOpRasterizer.clipMinY && TextureOpRasterizer.clipMaxY >= topRow) {
					spanRight = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX + dx, TextureOpRasterizer.textureOpClipMinX)
					spanLeft = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, centerX - dx, TextureOpRasterizer.textureOpClipMinX)
					if (TextureOpRasterizer.clipMaxY >= bottomRow) {
						ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows!![bottomRow], spanLeft, color)
					}
					if (TextureOpRasterizer.clipMinY <= topRow) {
						ArrayUtils.setArrayValuesTo(spanRight, TextureOpRaster.textureOpPixelRows!![topRow], spanLeft, color)
					}
				}
			}
		}
	}
}
