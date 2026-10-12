package com.jagex.game.runetek5.scene

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.loctype.LocInteractivity
import com.jagex.game.runetek5.entity.gameobject.GameObject
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc

object MapLoader {
	@OriginalMember(owner = "client!vw", name = "b", descriptor = "I")
	@JvmField
	var hdShadows: Int = 0

	@OriginalMember(owner = "client!iia", name = "i", descriptor = "[I")
	@JvmField
	val UNDERLAY_FACE_COUNT: IntArray = intArrayOf(0, 1, 2, 2, 1, 1, 2, 3, 1, 3, 3, 4, 2, 0, 4)

	@OriginalMember(owner = "client!gt", name = "c", descriptor = "[[I")
	@JvmField
	val SHAPE_VERTEX_A: Array<IntArray> = arrayOf(intArrayOf(0, 2), intArrayOf(0, 2), intArrayOf(0, 0, 2), intArrayOf(2, 0, 0), intArrayOf(0, 2, 0), intArrayOf(0, 0, 2), intArrayOf(0, 5, 1, 4), intArrayOf(0, 4, 4, 4), intArrayOf(4, 4, 4, 0), intArrayOf(6, 6, 6, 2, 2, 2), intArrayOf(2, 2, 2, 6, 6, 6), intArrayOf(0, 11, 6, 6, 6, 4), intArrayOf(0, 2), intArrayOf(0, 4, 4, 4), intArrayOf(0, 4, 4, 4))

	@OriginalMember(owner = "client!bda", name = "D", descriptor = "[[Z")
	@JvmField
	val UNDERLAY_SHAPE_SUPPORTS_HEIGHT: Array<BooleanArray> = arrayOf(BooleanArray(13), booleanArrayOf(false, false, true, true, true, true, true, false, false, false, false, false, true), booleanArrayOf(true, true, true, true, true, true, false, false, false, false, false, false, false), booleanArrayOf(true, true, true, false, false, true, true, true, false, false, false, false, false), booleanArrayOf(true, false, false, false, false, true, true, true, false, false, false, false, false), booleanArrayOf(false, false, true, true, true, true, false, false, false, false, false, false, false), booleanArrayOf(false, true, true, true, true, true, false, false, false, false, false, false, true), booleanArrayOf(false, true, true, true, true, true, true, true, false, false, false, false, true), booleanArrayOf(true, true, false, false, false, false, false, true, false, false, false, false, false), booleanArrayOf(true, true, true, true, true, false, false, false, true, true, false, false, false), booleanArrayOf(true, false, false, false, true, true, true, true, true, true, false, false, false), booleanArrayOf(true, false, true, true, true, true, true, true, false, false, true, true, false), booleanArrayOf(true, true, true, true, true, true, true, true, true, true, true, true, true), BooleanArray(13), booleanArrayOf(true, true, true, true, true, true, true, true, true, true, true, true, true))

	@OriginalMember(owner = "client!baa", name = "m", descriptor = "[[I")
	@JvmField
	val SHAPE_VERTEX_B: Array<IntArray> = arrayOf(intArrayOf(2, 4), intArrayOf(2, 4), intArrayOf(5, 2, 4), intArrayOf(4, 5, 2), intArrayOf(2, 4, 5), intArrayOf(5, 2, 4), intArrayOf(1, 6, 2, 5), intArrayOf(1, 6, 7, 1), intArrayOf(6, 7, 1, 1), intArrayOf(0, 8, 9, 8, 9, 4), intArrayOf(8, 9, 4, 0, 8, 9), intArrayOf(2, 10, 0, 10, 11, 11), intArrayOf(2, 4), intArrayOf(1, 6, 7, 1), intArrayOf(1, 6, 7, 1))

	@OriginalMember(owner = "client!pa", name = "e", descriptor = "[[Z")
	@JvmField
	val OVERLAY_SHAPE_SUPPORTS_HEIGHT: Array<BooleanArray> = arrayOf(booleanArrayOf(true, true, true, true, true, true, true, true, true, true, true, true, true), booleanArrayOf(true, true, true, false, false, false, true, true, false, false, false, false, true), booleanArrayOf(true, false, false, false, false, true, true, true, false, false, false, false, false), booleanArrayOf(false, false, true, true, true, true, false, false, false, false, false, false, false), booleanArrayOf(true, true, true, true, true, true, false, false, false, false, false, false, false), booleanArrayOf(true, true, true, false, false, true, true, true, false, false, false, false, false), booleanArrayOf(true, true, false, false, false, true, true, true, false, false, false, false, true), booleanArrayOf(true, true, false, false, false, false, false, true, false, false, false, false, false), booleanArrayOf(false, true, true, true, true, true, true, true, false, false, false, false, false), booleanArrayOf(true, false, false, false, true, true, true, true, true, true, false, false, false), booleanArrayOf(true, true, true, true, true, false, false, false, true, true, false, false, false), booleanArrayOf(true, true, true, false, false, false, false, false, false, false, true, true, false), BooleanArray(13), booleanArrayOf(true, true, true, true, true, true, true, true, true, true, true, true, true), BooleanArray(13))

	@OriginalMember(owner = "client!ar", name = "u", descriptor = "[[I")
	@JvmField
	val SHAPE_VERTEX_C: Array<IntArray> = arrayOf(intArrayOf(6, 6), intArrayOf(6, 6), intArrayOf(6, 5, 5), intArrayOf(5, 6, 5), intArrayOf(5, 5, 6), intArrayOf(6, 5, 5), intArrayOf(5, 0, 4, 1), intArrayOf(7, 7, 1, 2), intArrayOf(7, 1, 2, 7), intArrayOf(8, 9, 4, 0, 8, 9), intArrayOf(0, 8, 9, 8, 9, 4), intArrayOf(11, 0, 10, 11, 4, 2), intArrayOf(6, 6), intArrayOf(7, 7, 1, 2), intArrayOf(7, 7, 1, 2))

	@OriginalMember(owner = "client!ma", name = "N", descriptor = "[[I")
	@JvmField
	val BLENDED_FACE_MAPPING: Array<IntArray> = arrayOf(intArrayOf(0, 1, 2, 3), intArrayOf(1, 2, 3, 0), intArrayOf(1, 2, -1, 0), intArrayOf(2, 0, -1, 1), intArrayOf(0, 1, -1, 2), intArrayOf(1, 2, -1, 0), intArrayOf(-1, 4, -1, 1), intArrayOf(-1, 1, 3, -1), intArrayOf(-1, 0, 2, -1), intArrayOf(3, 5, 2, 0), intArrayOf(0, 2, 5, 3), intArrayOf(0, 2, 3, 5), intArrayOf(0, 1, 2, 3))

	@OriginalMember(owner = "client!vha", name = "n", descriptor = "[[I")
	@JvmField
	val BLENDED_SHAPE_VERTEX_B: Array<IntArray> = arrayOf(intArrayOf(2, 4, 6, 0), intArrayOf(0, 2, 4, 6), intArrayOf(0, 2, 4), intArrayOf(4, 0, 2), intArrayOf(2, 4, 0), intArrayOf(0, 2, 4), intArrayOf(6, 0, 1, 2, 4, 5), intArrayOf(0, 4, 7, 6), intArrayOf(4, 7, 6, 0), intArrayOf(0, 8, 6, 2, 9, 4), intArrayOf(2, 9, 4, 0, 8, 6), intArrayOf(2, 11, 4, 6, 10, 0), intArrayOf(2, 4, 6, 0))

	@OriginalMember(owner = "client!pe", name = "g", descriptor = "[[I")
	@JvmField
	val BLENDING_SHAPE_VERTEX_B: Array<IntArray> = arrayOf(intArrayOf(2, 4, 6, 0), intArrayOf(0, 2, 3, 5, 6, 4), intArrayOf(0, 1, 4, 5), intArrayOf(4, 6, 0, 2), intArrayOf(2, 4, 0), intArrayOf(0, 2, 4), intArrayOf(6, 0, 1, 2, 4, 5), intArrayOf(0, 1, 2, 4, 6, 7), intArrayOf(4, 7, 6, 0), intArrayOf(0, 8, 6, 1, 9, 2, 9, 4), intArrayOf(2, 9, 4, 0, 8, 6), intArrayOf(2, 11, 3, 7, 10, 10, 6, 6), intArrayOf(2, 4, 6, 0))

	@OriginalMember(owner = "client!rp", name = "a", descriptor = "(BLclient!se;II)J")
	@JvmStatic
	fun getLocMenuUid(@OriginalArg(1) location: GameObject?, @OriginalArg(2) z: Int, @OriginalArg(3) x: Int): Long {
		@Pc(5) val supportsItemsFlag: Long = 4194304L
		@Pc(7) val minValue: Long = Long.MIN_VALUE
		@Pc(14) val locType: LocType = TypeLists.LOC!!.list(location!!.getId())
		@Pc(35) var uid: Long = ((location.getShape() shl 14) or x or (z shl 7) or (location.getRotation() shl 20) or 0x40000000).toLong()
		if (locType.hasActions == LocInteractivity.NONINTERACTIVE) {
			uid = uid or minValue
		}
		if (locType.supportsItems == 1) {
			uid = uid or supportsItemsFlag
		}
		return uid or (location.getId().toLong() shl 32)
	}

	@OriginalMember(owner = "client!wn", name = "c", descriptor = "[[I")
	@JvmField
	val BLENDED_SHAPE_VERTEX_A: Array<IntArray> = arrayOf(intArrayOf(0, 2, 4, 6), intArrayOf(6, 0, 2, 4), intArrayOf(6, 0, 2), intArrayOf(2, 6, 0), intArrayOf(0, 2, 6), intArrayOf(6, 0, 2), intArrayOf(5, 6, 0, 1, 2, 4), intArrayOf(7, 2, 4, 4), intArrayOf(2, 4, 4, 7), intArrayOf(6, 6, 4, 0, 2, 2), intArrayOf(0, 2, 2, 6, 6, 4), intArrayOf(0, 2, 2, 4, 6, 6), intArrayOf(0, 2, 4, 6))
}
