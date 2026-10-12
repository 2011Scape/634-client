package com.jagex.game.runetek5.core.constants

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object LocShapes {

	const val WALL_STRAIGHT: Int = 0

	const val WALL_DIAGONALCORNER: Int = 1

	const val WALL_L: Int = 2

	const val WALL_SQUARECORNER: Int = 3

	const val WALL_DIAGONAL: Int = 9

	const val WALLDECOR_STRAIGHT_NOOFFSET: Int = 4

	const val WALLDECOR_STRAIGHT_OFFSET: Int = 5

	const val WALLDECOR_DIAGONAL_OFFSET: Int = 6

	const val WALLDECOR_DIAGONAL_NOOFFSET: Int = 7

	const val WALLDECOR_DIAGONAL_BOTH: Int = 8

	const val ROOFEDGE_STRAIGHT: Int = 18

	const val ROOFEDGE_DIAGONALCORNER: Int = 19

	const val ROOFEDGE_SQUARECORNER: Int = 21

	const val CENTREPIECE_STRAIGHT: Int = 10

	const val CENTREPIECE_DIAGONAL: Int = 11

	const val GROUNDDECOR: Int = 22

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(II)Z")
	@JvmStatic
	fun isWallDecoration(@OriginalArg(1) shape: Int): Boolean {
		return shape >= 4 && shape <= 8
	}

	@OriginalMember(owner = "client!jn", name = "t", descriptor = "[I")
	@JvmField
	val CORNER_WALL_SIDE_MASKS: IntArray = intArrayOf(16, 32, 64, 128)

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(IBI)I")
	@JvmStatic
	fun getWallSideMask(@OriginalArg(0) rotation: Int, @OriginalArg(2) shape: Int): Int {
		return if (shape == 1 || shape == 3) LocShapes.CORNER_WALL_SIDE_MASKS[rotation and 0x3] else LocShapes.STRAIGHT_WALL_SIDE_MASKS[rotation and 0x3]
	}

	@OriginalMember(owner = "client!sk", name = "a", descriptor = "(II)Z")
	@JvmStatic
	fun isRoof(@OriginalArg(0) shape: Int): Boolean {
		return shape >= 12 && shape <= 17
	}

	@OriginalMember(owner = "client!hf", name = "b", descriptor = "(III)B")
	@JvmStatic
	fun getDiagonalOrientation(@OriginalArg(0) rotation: Int, @OriginalArg(1) shape: Int): Byte {
		if (shape == 9) {
			return (if ((rotation and 0x1) == 0) 1 else 2).toByte()
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!f", name = "a", descriptor = "(II)I")
	@JvmStatic
	fun getRoofEdgeSideMask(@OriginalArg(0) shapeAndRotation: Int): Int {
		@Pc(7) val shape: Int = shapeAndRotation and 0x3F
		@Pc(13) val rotation: Int = (shapeAndRotation shr 6) and 0x3
		if (shape == LocShapes.ROOFEDGE_STRAIGHT) {
			if (rotation == 0) {
				return 1
			}
			if (rotation == 1) {
				return 2
			}
			if (rotation == 2) {
				return 4
			}
			if (rotation == 3) {
				return 8
			}
		} else if (shape == LocShapes.ROOFEDGE_DIAGONALCORNER || shape == LocShapes.ROOFEDGE_SQUARECORNER) {
			if (rotation == 0) {
				return 16
			}
			if (rotation == 1) {
				return 32
			}
			if (rotation == 2) {
				return 64
			}
			if (rotation == 3) {
				return 128
			}
		}
		return 0
	}

	@OriginalMember(owner = "client!jo", name = "I", descriptor = "[I")
	@JvmField
	val WALL_ROTATION_FLAGS: IntArray = intArrayOf(1, 2, 4, 8)

	@OriginalMember(owner = "client!lca", name = "b", descriptor = "[I")
	@JvmField
	val STRAIGHT_WALL_SIDE_MASKS: IntArray = intArrayOf(1, 2, 4, 8)

	@OriginalMember(owner = "client!td", name = "I", descriptor = "[I")
	@JvmField
	val DYNAMIC_CORNER_WALL_SIDE_MASKS: IntArray = intArrayOf(16, 32, 64, 128)

	@OriginalMember(owner = "client!maa", name = "a", descriptor = "(IIZ)I")
	@JvmStatic
	fun getDynamicWallSideMask(@OriginalArg(0) shape: Int, @OriginalArg(1) rotation: Int): Int {
		return if (shape == 1 || shape == 3) LocShapes.DYNAMIC_CORNER_WALL_SIDE_MASKS[rotation and 0x3] else LocShapes.WALL_ROTATION_FLAGS[rotation and 0x3]
	}

	@OriginalMember(owner = "client!oc", name = "a", descriptor = "(BI)Z")
	@JvmStatic
	fun isWallShape(@OriginalArg(1) shape: Int): Boolean {
		return shape >= 0 && shape <= 3 || shape == 9
	}

	@OriginalMember(owner = "client!sca", name = "a", descriptor = "(III)B")
	@JvmStatic
	fun getDiagonalRotationFlag(@OriginalArg(0) rotation: Int, @OriginalArg(2) shape: Int): Byte {
		if (shape == 9) {
			return (if ((rotation and 0x1) == 0) 1 else 2).toByte()
		} else {
			return 0
		}
	}
}
