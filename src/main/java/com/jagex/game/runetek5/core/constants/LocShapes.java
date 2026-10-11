package com.jagex.game.runetek5.core.constants;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

public final class LocShapes {

	public static final int WALL_STRAIGHT = 0;

	public static final int WALL_DIAGONALCORNER = 1;

	public static final int WALL_L = 2;

	public static final int WALL_SQUARECORNER = 3;

	public static final int WALL_DIAGONAL = 9;

	public static final int WALLDECOR_STRAIGHT_NOOFFSET = 4;

	public static final int WALLDECOR_STRAIGHT_OFFSET = 5;

	public static final int WALLDECOR_DIAGONAL_OFFSET = 6;

	public static final int WALLDECOR_DIAGONAL_NOOFFSET = 7;

	public static final int WALLDECOR_DIAGONAL_BOTH = 8;

	public static final int ROOFEDGE_STRAIGHT = 18;

	public static final int ROOFEDGE_DIAGONALCORNER = 19;

	public static final int ROOFEDGE_SQUARECORNER = 21;

	public static final int CENTREPIECE_STRAIGHT = 10;

	public static final int CENTREPIECE_DIAGONAL = 11;

	public static final int GROUNDDECOR = 22;

	private LocShapes() {
		/* empty */
	}
	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(II)Z")
	public static boolean isWallDecoration(@OriginalArg(1) int shape) {
		return shape >= 4 && shape <= 8;
	}

	@OriginalMember(owner = "client!jn", name = "t", descriptor = "[I")
	public static final int[] CORNER_WALL_SIDE_MASKS = new int[] { 16, 32, 64, 128 };

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(IBI)I")
	public static int getWallSideMask(@OriginalArg(0) int rotation, @OriginalArg(2) int shape) {
		return shape == 1 || shape == 3 ? LocShapes.CORNER_WALL_SIDE_MASKS[rotation & 0x3] : LocShapes.STRAIGHT_WALL_SIDE_MASKS[rotation & 0x3];
	}

	@OriginalMember(owner = "client!sk", name = "a", descriptor = "(II)Z")
	public static boolean isRoof(@OriginalArg(0) int shape) {
		return shape >= 12 && shape <= 17;
	}
	@OriginalMember(owner = "client!hf", name = "b", descriptor = "(III)B")
	public static byte getDiagonalOrientation(@OriginalArg(0) int rotation, @OriginalArg(1) int shape) {
		if (shape == 9) {
			return (byte) ((rotation & 0x1) == 0 ? 1 : 2);
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!f", name = "a", descriptor = "(II)I")
	public static int getRoofEdgeSideMask(@OriginalArg(0) int shapeAndRotation) {
		@Pc(7) int shape = shapeAndRotation & 0x3F;
		@Pc(13) int rotation = shapeAndRotation >> 6 & 0x3;
		if (shape == LocShapes.ROOFEDGE_STRAIGHT) {
			if (rotation == 0) {
				return 1;
			}
			if (rotation == 1) {
				return 2;
			}
			if (rotation == 2) {
				return 4;
			}
			if (rotation == 3) {
				return 8;
			}
		} else if (shape == LocShapes.ROOFEDGE_DIAGONALCORNER || shape == LocShapes.ROOFEDGE_SQUARECORNER) {
			if (rotation == 0) {
				return 16;
			}
			if (rotation == 1) {
				return 32;
			}
			if (rotation == 2) {
				return 64;
			}
			if (rotation == 3) {
				return 128;
			}
		}
		return 0;
	}
	@OriginalMember(owner = "client!jo", name = "I", descriptor = "[I")
	public static final int[] WALL_ROTATION_FLAGS = new int[] { 1, 2, 4, 8 };

	@OriginalMember(owner = "client!lca", name = "b", descriptor = "[I")
	public static final int[] STRAIGHT_WALL_SIDE_MASKS = new int[] { 1, 2, 4, 8 };

	@OriginalMember(owner = "client!td", name = "I", descriptor = "[I")
	public static final int[] DYNAMIC_CORNER_WALL_SIDE_MASKS = new int[] { 16, 32, 64, 128 };

	@OriginalMember(owner = "client!maa", name = "a", descriptor = "(IIZ)I")
	public static int getDynamicWallSideMask(@OriginalArg(0) int shape, @OriginalArg(1) int rotation) {
		return shape == 1 || shape == 3 ? LocShapes.DYNAMIC_CORNER_WALL_SIDE_MASKS[rotation & 0x3] : LocShapes.WALL_ROTATION_FLAGS[rotation & 0x3];
	}

	@OriginalMember(owner = "client!oc", name = "a", descriptor = "(BI)Z")
	public static boolean isWallShape(@OriginalArg(1) int shape) {
		return shape >= 0 && shape <= 3 || shape == 9;
	}

	@OriginalMember(owner = "client!sca", name = "a", descriptor = "(III)B")
	public static byte getDiagonalRotationFlag(@OriginalArg(0) int rotation, @OriginalArg(2) int shape) {
		if (shape == 9) {
			return (byte) ((rotation & 0x1) == 0 ? 1 : 2);
		} else {
			return 0;
		}
	}
}
