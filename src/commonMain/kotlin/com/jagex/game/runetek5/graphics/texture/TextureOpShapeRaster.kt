package com.jagex.game.runetek5.graphics.texture

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer
import com.jagex.game.runetek5.config.material.texture.operation.RectangleRasterizer
import com.jagex.game.runetek5.graphics.TextureOpRaster
import com.jagex.game.runetek5.utils.ArrayUtils
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object TextureOpShapeRaster {

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(Lclient!cea;I)Lclient!gfa;")
	@JvmStatic
	fun decodeRectangle(@OriginalArg(0) packet: Packet?): RectangleRasterizer {
		return RectangleRasterizer(packet!!.readShort(), packet.readShort(), packet.readShort(), packet.readShort(), packet.readUMedium(), packet.readUMedium(), packet.readUByte())
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(IIIIIIII)V")
	@JvmStatic
	fun drawClippedEllipse(@OriginalArg(0) outerRx: Int, @OriginalArg(1) cx: Int, @OriginalArg(2) cy: Int, @OriginalArg(4) outerRy: Int, @OriginalArg(5) fillColor: Int, @OriginalArg(6) strokeColor: Int, @OriginalArg(7) thickness: Int) {
		@Pc(7) var outerX: Int = 0
		@Pc(9) var outerY: Int = outerRy
		@Pc(11) var innerX: Int = 0
		@Pc(16) val innerRx: Int = outerRx - thickness
		@Pc(20) val innerRy: Int = outerRy - thickness
		@Pc(24) val outerRxSq: Int = outerRx * outerRx
		@Pc(28) val outerRySq: Int = outerRy * outerRy
		@Pc(32) val innerRxSq: Int = innerRx * innerRx
		@Pc(36) val innerRySq: Int = innerRy * innerRy
		@Pc(40) val outerRySq2: Int = outerRySq shl 1
		@Pc(44) val outerRxSq2: Int = outerRxSq shl 1
		@Pc(48) val innerRySq2: Int = innerRySq shl 1
		@Pc(52) val innerRxSq2: Int = innerRxSq shl 1
		@Pc(56) val outerDiam: Int = outerRy shl 1
		@Pc(60) val innerDiam: Int = innerRy shl 1
		@Pc(69) var outerDecA: Int = outerRySq2 + outerRxSq * (1 - outerDiam)
		@Pc(78) var outerDecB: Int = outerRySq - outerRxSq2 * (outerDiam - 1)
		@Pc(86) var innerDecA: Int = (1 - innerDiam) * innerRxSq + innerRySq2
		@Pc(95) var innerDecB: Int = innerRySq - innerRxSq2 * (innerDiam - 1)
		@Pc(99) val outerRxSq4: Int = outerRxSq shl 2
		@Pc(103) val outerRySq4: Int = outerRySq shl 2
		@Pc(107) val innerRxSq4: Int = innerRxSq shl 2
		@Pc(111) val innerRySq4: Int = innerRySq shl 2
		@Pc(115) var outerGradA: Int = outerRySq2 * 3
		@Pc(121) var outerGradB: Int = outerRxSq2 * (outerDiam - 3)
		@Pc(125) var innerGradA: Int = innerRySq2 * 3
		@Pc(131) var innerGradB: Int = (innerDiam - 3) * innerRxSq2
		@Pc(133) var outerStepA: Int = outerRySq4
		@Pc(139) var outerStepB: Int = outerRxSq4 * (outerRy - 1)
		@Pc(141) var innerStepA: Int = innerRySq4
		@Pc(147) var innerStepB: Int = innerRxSq4 * (innerRy - 1)
		@Pc(170) var local170: Int
		@Pc(178) var local178: Int
		@Pc(189) var local189: Int
		@Pc(197) var local197: Int
		if (TextureOpRasterizer.clipMinY <= cy && TextureOpRasterizer.clipMaxY >= cy) {
			@Pc(161) val centerRow: IntArray? = TextureOpRaster.textureOpPixelRows!![cy]
			val outerLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx - outerRx, TextureOpRasterizer.textureOpClipMinX)
			val outerRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, outerRx + cx, TextureOpRasterizer.textureOpClipMinX)
			val innerLeft: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx - innerRx, TextureOpRasterizer.textureOpClipMinX)
			val innerRight: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx + innerRx, TextureOpRasterizer.textureOpClipMinX)
			ArrayUtils.setArrayValuesTo(innerLeft, centerRow, outerLeft, strokeColor)
			ArrayUtils.setArrayValuesTo(innerRight, centerRow, innerLeft, fillColor)
			ArrayUtils.setArrayValuesTo(outerRight, centerRow, innerRight, strokeColor)
		}
		while (outerY > 0) {
			@Pc(224) val withinInner: Boolean = innerRy >= outerY
			if (withinInner) {
				if (innerDecA < 0) {
					while (innerDecA < 0) {
						innerDecA += innerGradA
						innerDecB += innerStepA
						innerStepA += innerRySq4
						innerX++
						innerGradA += innerRySq4
					}
				}
				if (innerDecB < 0) {
					innerDecA += innerGradA
					innerDecB += innerStepA
					innerStepA += innerRySq4
					innerGradA += innerRySq4
					innerX++
				}
				innerDecA += -innerStepB
				innerDecB += -innerGradB
				innerStepB -= innerRxSq4
				innerGradB -= innerRxSq4
			}
			if (outerDecA < 0) {
				while (outerDecA < 0) {
					outerDecA += outerGradA
					outerDecB += outerStepA
					outerX++
					outerStepA += outerRySq4
					outerGradA += outerRySq4
				}
			}
			if (outerDecB < 0) {
				outerDecB += outerStepA
				outerDecA += outerGradA
				outerGradA += outerRySq4
				outerX++
				outerStepA += outerRySq4
			}
			outerDecA += -outerStepB
			outerDecB += -outerGradB
			outerY--
			outerGradB -= outerRxSq4
			outerStepB -= outerRxSq4
			val topY: Int = cy - outerY
			val bottomY: Int = outerY + cy
			if (TextureOpRasterizer.clipMinY <= bottomY && topY <= TextureOpRasterizer.clipMaxY) {
				val outerRightEdge: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, outerX + cx, TextureOpRasterizer.textureOpClipMinX)
				val outerLeftEdge: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx - outerX, TextureOpRasterizer.textureOpClipMinX)
				if (withinInner) {
					@Pc(440) val innerRightEdge: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx + innerX, TextureOpRasterizer.textureOpClipMinX)
					@Pc(449) val innerLeftEdge: Int = ArrayUtils.clamp(TextureOpRasterizer.clipMaxX, cx - innerX, TextureOpRasterizer.textureOpClipMinX)
					@Pc(460) var row: IntArray?
					if (topY >= TextureOpRasterizer.clipMinY) {
						row = TextureOpRaster.textureOpPixelRows!![topY]
						ArrayUtils.setArrayValuesTo(innerLeftEdge, row, outerLeftEdge, strokeColor)
						ArrayUtils.setArrayValuesTo(innerRightEdge, row, innerLeftEdge, fillColor)
						ArrayUtils.setArrayValuesTo(outerRightEdge, row, innerRightEdge, strokeColor)
					}
					if (TextureOpRasterizer.clipMaxY >= bottomY) {
						row = TextureOpRaster.textureOpPixelRows!![bottomY]
						ArrayUtils.setArrayValuesTo(innerLeftEdge, row, outerLeftEdge, strokeColor)
						ArrayUtils.setArrayValuesTo(innerRightEdge, row, innerLeftEdge, fillColor)
						ArrayUtils.setArrayValuesTo(outerRightEdge, row, innerRightEdge, strokeColor)
					}
				} else {
					if (TextureOpRasterizer.clipMinY <= topY) {
						ArrayUtils.setArrayValuesTo(outerRightEdge, TextureOpRaster.textureOpPixelRows!![topY], outerLeftEdge, strokeColor)
					}
					if (bottomY <= TextureOpRasterizer.clipMaxY) {
						ArrayUtils.setArrayValuesTo(outerRightEdge, TextureOpRaster.textureOpPixelRows!![bottomY], outerLeftEdge, strokeColor)
					}
				}
			}
		}
	}
}
