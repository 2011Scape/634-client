package com.jagex.game.runetek5.scene.culling

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.scene.Scene
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ea")
class CullingCluster {

	@OriginalMember(owner = "client!ea", name = "d", descriptor = "[I")
	@JvmField
	val tileCullingsZ: IntArray = IntArray(4)

	@OriginalMember(owner = "client!ea", name = "k", descriptor = "[I")
	@JvmField
	val drawHeights: IntArray = IntArray(4)

	@OriginalMember(owner = "client!ea", name = "l", descriptor = "B")
	@JvmField
	val type: Byte

	@OriginalMember(owner = "client!ea", name = "a", descriptor = "B")
	@JvmField
	val plane: Byte

	@OriginalMember(owner = "client!ea", name = "g", descriptor = "[I")
	@JvmField
	val tileCullingsX: IntArray

	@OriginalMember(owner = "client!ea", name = "e", descriptor = "S")
	@JvmField
	val startX: Short

	@OriginalMember(owner = "client!ea", name = "b", descriptor = "S")
	@JvmField
	val endX: Short

	@OriginalMember(owner = "client!ea", name = "m", descriptor = "S")
	@JvmField
	val startY: Short

	@OriginalMember(owner = "client!ea", name = "j", descriptor = "S")
	@JvmField
	val endY: Short

	@OriginalMember(owner = "client!ea", name = "h", descriptor = "[S")
	@JvmField
	val projectionX: ShortArray

	@OriginalMember(owner = "client!ea", name = "c", descriptor = "[S")
	@JvmField
	val projectionY: ShortArray

	@OriginalMember(owner = "client!ea", name = "n", descriptor = "[S")
	@JvmField
	val projectionZ: ShortArray

	@OriginalMember(owner = "client!ea", name = "<init>", descriptor = "(IIIIIIIIIIIIII)V")
	constructor(@OriginalArg(0) type: Int, @OriginalArg(1) plane: Int, @OriginalArg(2) tileEndX: Int, @OriginalArg(3) tileStartX: Int, @OriginalArg(4) tileStartX2: Int, @OriginalArg(5) tileEndX2: Int, @OriginalArg(6) heightSouthWest: Int, @OriginalArg(7) heightNorthEast: Int, @OriginalArg(8) heightNorthWest: Int, @OriginalArg(9) heightSouthEast: Int, @OriginalArg(10) tileEndY: Int, @OriginalArg(11) tileStartY: Int, @OriginalArg(12) tileStartY2: Int, @OriginalArg(13) tileEndY2: Int) {
		this.type = type.toByte()
		this.plane = plane.toByte()
		this.tileCullingsX = IntArray(4)
		this.tileCullingsX[0] = tileEndX
		this.tileCullingsX[1] = tileStartX
		this.tileCullingsX[3] = tileEndX2
		this.tileCullingsX[2] = tileStartX2
		this.drawHeights[0] = heightSouthWest
		this.drawHeights[2] = heightNorthWest
		this.drawHeights[3] = heightSouthEast
		this.drawHeights[1] = heightNorthEast
		this.tileCullingsZ[1] = tileStartY
		this.startX = (tileEndX shr Scene.tileUnits).toShort()
		this.tileCullingsZ[3] = tileEndY2
		this.tileCullingsZ[0] = tileEndY
		this.tileCullingsZ[2] = tileStartY2
		this.endX = (tileStartX2 shr Scene.tileUnits).toShort()
		this.startY = (tileEndY shr Scene.tileUnits).toShort()
		this.endY = (tileStartY2 shr Scene.tileUnits).toShort()
		this.projectionX = ShortArray(4)
		this.projectionY = ShortArray(4)
		this.projectionZ = ShortArray(4)
	}

	companion object {
		@OriginalMember(owner = "client!eo", name = "a", descriptor = "(IILclient!ea;)Z")
		@JvmStatic
		fun projectCorner(@OriginalArg(0) corner: Int, @OriginalArg(2) locOccluder: CullingCluster?): Boolean {
			Culling.aClass30_7!!.method4652(locOccluder!!.tileCullingsX[corner], locOccluder.drawHeights[corner], locOccluder.tileCullingsZ[corner], Culling.occlusionProjection)
			@Pc(22) val depth: Int = Culling.occlusionProjection[2]
			if (depth < 50) {
				return false
			} else {
				locOccluder.projectionZ[corner] = (Culling.occlusionCenterX + Culling.occlusionProjection[0] * Culling.occlusionScaleX / depth).toShort()
				locOccluder.projectionY[corner] = (Culling.occlusionScaleY * Culling.occlusionProjection[1] / depth + Culling.occlusionCenterY).toShort()
				locOccluder.projectionX[corner] = depth.toShort()
				return true
			}
		}
	}
}
