package com.jagex.game.runetek5.scene;

import com.jagex.game.runetek5.core.constants.LocShapes;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.scene.CollisionFlag;
import com.jagex.game.runetek5.collision.DirectionFlag;
import com.jagex.game.runetek5.PathFinder;

@OriginalClass("client!fe")
public final class CollisionMap {

	@OriginalMember(owner = "client!fe", name = "g", descriptor = "I")
	public int sizeX;

	@OriginalMember(owner = "client!fe", name = "h", descriptor = "[[I")
	public int[][] collisionFlags;

	@OriginalMember(owner = "client!fe", name = "k", descriptor = "I")
	public int zOffset;

	@OriginalMember(owner = "client!fe", name = "m", descriptor = "I")
	public int sizeY;

	@OriginalMember(owner = "client!fe", name = "w", descriptor = "I")
	public int xOffset;

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIII)V")
	private void removeFlag(@OriginalArg(0) int flag, @OriginalArg(2) int x, @OriginalArg(3) int z) {
		this.collisionFlags[x][z] &= ~flag;
	}

	@OriginalMember(owner = "client!fe", name = "b", descriptor = "(IIII)V")
	private void addFlag(@OriginalArg(0) int x, @OriginalArg(1) int flag, @OriginalArg(2) int z) {
		this.collisionFlags[x][z] |= flag;
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIIIIIII)Z")
	public boolean canReachWall(@OriginalArg(0) int destX, @OriginalArg(1) int srcZ, @OriginalArg(2) int size, @OriginalArg(3) int rotation, @OriginalArg(4) int srcX, @OriginalArg(5) int destZ, @OriginalArg(6) int shape) {
		if (size == 1) {
			if (destX == srcX && destZ == srcZ) {
				return true;
			}
		} else if (srcX <= destX && srcX + size - 1 >= destX && destZ <= destZ && size + destZ - 1 >= destZ) {
			return true;
		}
		@Pc(67) int adjustedDestZ = destZ - this.zOffset;
		@Pc(72) int adjustedSrcZ = srcZ - this.zOffset;
		@Pc(77) int adjustedDestX = destX - this.xOffset;
		@Pc(82) int adjustedSrcX = srcX - this.xOffset;
		if (size == 1) {
			if (shape == LocShapes.WALL_STRAIGHT) {
				if (rotation == 0) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ) {
						return true;
					}
					if (adjustedDestX == adjustedSrcX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0120) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX && adjustedSrcZ == adjustedDestZ - 1 && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0102) == 0) {
						return true;
					}
				} else if (rotation == 1) {
					if (adjustedSrcX == adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0108) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ == adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0180) == 0) {
						return true;
					}
				} else if (rotation == 2) {
					if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ == adjustedDestZ) {
						return true;
					}
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0120) == 0) {
						return true;
					}
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ - 1 && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0102) == 0) {
						return true;
					}
				} else if (rotation == 3) {
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ - 1) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0108) == 0) {
						return true;
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0180) == 0) {
						return true;
					}
				}
			}
			if (shape == LocShapes.WALL_L) {
				if (rotation == 0) {
					if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ) {
						return true;
					}
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ + 1) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ == adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0180) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0102) == 0) {
						return true;
					}
				} else if (rotation == 1) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0108) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ == adjustedDestZ) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0102) == 0) {
						return true;
					}
				} else if (rotation == 2) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedDestZ == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0108) == 0) {
						return true;
					}
					if (adjustedDestX == adjustedSrcX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0120) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ == adjustedSrcZ) {
						return true;
					}
					if (adjustedDestX == adjustedSrcX && adjustedDestZ - 1 == adjustedSrcZ) {
						return true;
					}
				} else if (rotation == 3) {
					if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0120) == 0) {
						return true;
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & 0x2C0180) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ) {
						return true;
					}
				}
			}
			if (shape == LocShapes.WALL_DIAGONAL) {
				if (adjustedSrcX == adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_SOUTH) == 0) {
					return true;
				}
				if (adjustedDestX == adjustedSrcX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_NORTH) == 0) {
					return true;
				}
				if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_EAST) == 0) {
					return true;
				}
				if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_WEST) == 0) {
					return true;
				}
			}
		} else {
			@Pc(803) int srcEndX = size + adjustedSrcX - 1;
			@Pc(809) int srcEndZ = size + adjustedSrcZ - 1;
			if (shape == LocShapes.WALL_STRAIGHT) {
				if (rotation == 0) {
					if (adjustedDestX - size == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ) {
						return true;
					}
					if (adjustedDestX >= adjustedSrcX && srcEndX >= adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedDestX][adjustedSrcZ] & 0x2C0120) == 0) {
						return true;
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags[adjustedDestX][srcEndZ] & 0x2C0102) == 0) {
						return true;
					}
				} else if (rotation == 1) {
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ + 1) {
						return true;
					}
					if (adjustedDestX - size == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && adjustedDestZ <= srcEndZ && (this.collisionFlags[srcEndX][adjustedDestZ] & 0x2C0108) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags[adjustedSrcX][adjustedDestZ] & 0x2C0180) == 0) {
						return true;
					}
				} else if (rotation == 2) {
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ) {
						return true;
					}
					if (adjustedDestX >= adjustedSrcX && srcEndX >= adjustedDestX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags[adjustedDestX][adjustedSrcZ] & 0x2C0120) == 0) {
						return true;
					}
					if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags[adjustedDestX][srcEndZ] & 0x2C0102) == 0) {
						return true;
					}
				} else if (rotation == 3) {
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX - size && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags[srcEndX][adjustedDestZ] & 0x2C0108) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedDestZ] & 0x2C0180) == 0) {
						return true;
					}
				}
			}
			if (shape == LocShapes.WALL_L) {
				if (rotation == 0) {
					if (adjustedSrcX == adjustedDestX - size && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ) {
						return true;
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedDestZ + 1 == adjustedSrcZ) {
						return true;
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedDestZ] & 0x2C0180) == 0) {
						return true;
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ && (this.collisionFlags[adjustedDestX][srcEndZ] & 0x2C0102) == 0) {
						return true;
					}
				} else if (rotation == 1) {
					if (adjustedDestX - size == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ && (this.collisionFlags[srcEndX][adjustedDestZ] & 0x2C0108) == 0) {
						return true;
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ + 1) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ) {
						return true;
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ && (this.collisionFlags[adjustedDestX][srcEndZ] & 0x2C0102) == 0) {
						return true;
					}
				} else if (rotation == 2) {
					if (adjustedSrcX == adjustedDestX - size && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags[srcEndX][adjustedDestZ] & 0x2C0108) == 0) {
						return true;
					}
					if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedDestX][adjustedSrcZ] & 0x2C0120) == 0) {
						return true;
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ) {
						return true;
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ) {
						return true;
					}
				} else if (rotation == 3) {
					if (adjustedDestX - size == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ) {
						return true;
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags[adjustedDestX][adjustedSrcZ] & 0x2C0120) == 0) {
						return true;
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags[adjustedSrcX][adjustedDestZ] & 0x2C0180) == 0) {
						return true;
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ) {
						return true;
					}
				}
			}
			if (shape == LocShapes.WALL_DIAGONAL) {
				if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedDestX][adjustedSrcZ] & 0x2C0120) == 0) {
					return true;
				}
				if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags[adjustedDestX][srcEndZ] & 0x2C0102) == 0) {
					return true;
				}
				if (adjustedDestX - size == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && adjustedDestZ <= srcEndZ && (this.collisionFlags[srcEndX][adjustedDestZ] & 0x2C0108) == 0) {
					return true;
				}
				if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedDestZ] & 0x2C0180) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(III)V")
	public void flagFloorDecoration(@OriginalArg(0) int z, @OriginalArg(1) int x) {
		@Pc(4) int adjustedZ = z - this.zOffset;
		@Pc(25) int adjustedX = x - this.xOffset;
		this.collisionFlags[adjustedX][adjustedZ] |= 0x40000;
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIIIIIIIII)Z")
	public boolean isOutsideRect(@OriginalArg(1) int destX, @OriginalArg(2) int accessFlags, @OriginalArg(3) int srcSizeX, @OriginalArg(4) int x1, @OriginalArg(5) int srcX, @OriginalArg(6) int srcSizeZ, @OriginalArg(7) int srcZ, @OriginalArg(8) int destZ, @OriginalArg(9) int destSizeX) {
		@Pc(24) int srcMaxX = srcX + srcSizeX;
		@Pc(28) int destZ1 = srcSizeZ + srcZ;
		@Pc(33) int destMaxX = destX + destSizeX;
		@Pc(38) int destMaxZ = destZ + x1;
		@Pc(56) int z0;
		@Pc(67) int overlapMax;
		if (srcX == destMaxX && (accessFlags & DirectionFlag.EAST) == 0) {
			z0 = destZ < srcZ ? srcZ : destZ;
			overlapMax = destMaxZ > destZ1 ? destZ1 : destMaxZ;
			while (z0 < overlapMax) {
				if ((this.collisionFlags[destMaxX - this.xOffset - 1][z0 - this.zOffset] & CollisionFlag.WALL_EAST) == 0) {
					return true;
				}
				z0++;
			}
		} else if (srcMaxX == destX && (accessFlags & DirectionFlag.WEST) == 0) {
			z0 = destZ >= srcZ ? destZ : srcZ;
			overlapMax = destZ1 >= destMaxZ ? destMaxZ : destZ1;
			while (overlapMax > z0) {
				if ((this.collisionFlags[destX - this.xOffset][z0 - this.zOffset] & CollisionFlag.WALL_WEST) == 0) {
					return true;
				}
				z0++;
			}
		} else if (destMaxZ == srcZ && (accessFlags & DirectionFlag.NORTH) == 0) {
			z0 = srcX > destX ? srcX : destX;
			overlapMax = srcMaxX < destMaxX ? srcMaxX : destMaxX;
			while (overlapMax > z0) {
				if ((this.collisionFlags[z0 - this.xOffset][destMaxZ - this.zOffset - 1] & CollisionFlag.WALL_NORTH) == 0) {
					return true;
				}
				z0++;
			}
		} else if (destZ1 == destZ && (accessFlags & DirectionFlag.SOUTH) == 0) {
			z0 = destX < srcX ? srcX : destX;
			overlapMax = srcMaxX < destMaxX ? srcMaxX : destMaxX;
			while (overlapMax > z0) {
				if ((this.collisionFlags[z0 - this.xOffset][destZ - this.zOffset] & CollisionFlag.WALL_SOUTH) == 0) {
					return true;
				}
				z0++;
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(BII)V")
	public void unflagFloorDecoration(@OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(4) int adjustedZ = z - this.zOffset;
		@Pc(9) int adjustedX = x - this.xOffset;
		this.collisionFlags[adjustedX][adjustedZ] &= 0xFFFBFFFF;
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(I)V")
	public void reset() {
		for (@Pc(3) int x = 0; x < this.sizeX; x++) {
			for (@Pc(20) int z = 0; z < this.sizeY; z++) {
				if (x == 0 || z == 0 || x >= this.sizeX - 5 || this.sizeY - 5 <= z) {
					this.collisionFlags[x][z] = -1;
				} else {
					this.collisionFlags[x][z] = 2097152;
				}
			}
		}
	}

	@OriginalMember(owner = "client!fe", name = "b", descriptor = "(BII)V")
	public void unflagBlocked(@OriginalArg(1) int z, @OriginalArg(2) int x) {
		@Pc(4) int z1 = x - this.xOffset;
		@Pc(13) int x1 = z - this.zOffset;
		this.collisionFlags[z1][x1] &= 0xFFDFFFFF;
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IBIIIIIII)Z")
	public boolean isInsideOrOutsideRect(@OriginalArg(0) int destZ1, @OriginalArg(2) int direction, @OriginalArg(3) int destX1, @OriginalArg(4) int x1, @OriginalArg(5) int size, @OriginalArg(6) int destLength, @OriginalArg(7) int destWidth, @OriginalArg(8) int z1) {
		if (destX1 > 1) {
			return PathFinder.rectanglesOverlap(size, direction, destX1, destZ1, destWidth, z1, destX1, x1) ? true : this.isOutsideRect(z1, destLength, destX1, x1, destZ1, destX1, destWidth, direction, size);
		}
		@Pc(46) int destX2 = size + z1 - 1;
		@Pc(53) int destZ2 = direction + x1 - 1;
		if (destZ1 >= z1 && destX2 >= destZ1 && destWidth >= direction && destWidth <= destZ2) {
			return true;
		} else if (destZ1 == z1 - 1 && direction <= destWidth && destWidth <= destZ2 && (this.collisionFlags[destZ1 - this.xOffset][destWidth - this.zOffset] & CollisionFlag.WALL_EAST) == 0 && (destLength & DirectionFlag.WEST) == 0) {
			return true;
		} else if (destX2 + 1 == destZ1 && direction <= destWidth && destWidth <= destZ2 && (this.collisionFlags[destZ1 - this.xOffset][destWidth - this.zOffset] & CollisionFlag.WALL_WEST) == 0 && (destLength & DirectionFlag.EAST) == 0) {
			return true;
		} else if (direction - 1 == destWidth && destZ1 >= z1 && destX2 >= destZ1 && (this.collisionFlags[destZ1 - this.xOffset][destWidth - this.zOffset] & CollisionFlag.WALL_NORTH) == 0 && (destLength & DirectionFlag.SOUTH) == 0) {
			return true;
		} else {
			return destWidth == destZ2 + 1 && destZ1 >= z1 && destZ1 <= destX2 && (this.collisionFlags[destZ1 - this.xOffset][destWidth - this.zOffset] & CollisionFlag.WALL_SOUTH) == 0 && (destLength & DirectionFlag.NORTH) == 0;
		}
	}

	@OriginalMember(owner = "client!fe", name = "b", descriptor = "(IIIIIIII)Z")
	public boolean canReachWallDecoration(@OriginalArg(0) int destZ, @OriginalArg(1) int srcZ, @OriginalArg(2) int destX, @OriginalArg(3) int srcX, @OriginalArg(4) int shape, @OriginalArg(6) int rotation, @OriginalArg(7) int size) {
		if (size == 1) {
			if (srcX == destX && destZ == srcZ) {
				return true;
			}
		} else if (srcX <= destX && destX <= size + srcX - 1 && destZ <= destZ && destZ <= size + destZ - 1) {
			return true;
		}
		@Pc(61) int adjustedDestX = destX - this.xOffset;
		@Pc(66) int adjustedSrcX = srcX - this.xOffset;
		@Pc(71) int adjustedDestZ = destZ - this.zOffset;
		@Pc(76) int adjustedSrcZ = srcZ - this.zOffset;
		if (size == 1) {
			if (shape == LocShapes.WALLDECOR_DIAGONAL_OFFSET || shape == LocShapes.WALLDECOR_DIAGONAL_NOOFFSET) {
				if (shape == 7) {
					rotation = rotation + 2 & 0x3;
				}
				if (rotation == 0) {
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_WEST) == 0) {
						return true;
					}
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ - 1 && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_NORTH) == 0) {
						return true;
					}
				} else if (rotation == 1) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedDestZ == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_EAST) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_NORTH) == 0) {
						return true;
					}
				} else if (rotation == 2) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_EAST) == 0) {
						return true;
					}
					if (adjustedDestX == adjustedSrcX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_SOUTH) == 0) {
						return true;
					}
				} else if (rotation == 3) {
					if (adjustedDestX + 1 == adjustedSrcX && adjustedDestZ == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_WEST) == 0) {
						return true;
					}
					if (adjustedSrcX == adjustedDestX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_SOUTH) == 0) {
						return true;
					}
				}
			}
			if (shape == LocShapes.WALLDECOR_DIAGONAL_BOTH) {
				if (adjustedSrcX == adjustedDestX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_SOUTH) == 0) {
					return true;
				}
				if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_NORTH) == 0) {
					return true;
				}
				if (adjustedSrcX == adjustedDestX - 1 && adjustedSrcZ == adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_EAST) == 0) {
					return true;
				}
				if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedSrcZ] & CollisionFlag.WALL_WEST) == 0) {
					return true;
				}
			}
		} else {
			@Pc(87) int srcEndX = size + adjustedSrcX - 1;
			@Pc(94) int srcEndZ = adjustedSrcZ + size - 1;
			if (shape == LocShapes.WALLDECOR_DIAGONAL_OFFSET || shape == LocShapes.WALLDECOR_DIAGONAL_NOOFFSET) {
				if (shape == LocShapes.WALLDECOR_DIAGONAL_NOOFFSET) {
					rotation = rotation + 2 & 0x3;
				}
				if (rotation == 0) {
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags[adjustedSrcX][adjustedDestZ] & CollisionFlag.WALL_WEST) == 0) {
						return true;
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags[adjustedDestX][srcEndZ] & CollisionFlag.WALL_NORTH) == 0) {
						return true;
					}
				} else if (rotation == 1) {
					if (adjustedDestX - size == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags[srcEndX][adjustedDestZ] & CollisionFlag.WALL_EAST) == 0) {
						return true;
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags[adjustedDestX][srcEndZ] & CollisionFlag.WALL_NORTH) == 0) {
						return true;
					}
				} else if (rotation == 2) {
					if (adjustedDestX - size == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ && (this.collisionFlags[srcEndX][adjustedDestZ] & CollisionFlag.WALL_EAST) == 0) {
						return true;
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedDestX][adjustedSrcZ] & CollisionFlag.WALL_SOUTH) == 0) {
						return true;
					}
				} else if (rotation == 3) {
					if (adjustedDestX + 1 == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags[adjustedSrcX][adjustedDestZ] & CollisionFlag.WALL_WEST) == 0) {
						return true;
					}
					if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags[adjustedDestX][adjustedSrcZ] & CollisionFlag.WALL_SOUTH) == 0) {
						return true;
					}
				}
			}
			if (shape == LocShapes.WALLDECOR_DIAGONAL_BOTH) {
				if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags[adjustedDestX][adjustedSrcZ] & CollisionFlag.WALL_SOUTH) == 0) {
					return true;
				}
				if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags[adjustedDestX][srcEndZ] & CollisionFlag.WALL_NORTH) == 0) {
					return true;
				}
				if (adjustedSrcX == adjustedDestX - size && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags[srcEndX][adjustedDestZ] & CollisionFlag.WALL_EAST) == 0) {
					return true;
				}
				if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ <= adjustedDestZ && adjustedDestZ <= srcEndZ && (this.collisionFlags[adjustedSrcX][adjustedDestZ] & CollisionFlag.WALL_WEST) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(ZIIZIZI)V")
	public void flagBlockedObject(@OriginalArg(0) boolean blocksAltRoute, @OriginalArg(1) int sizeX, @OriginalArg(2) int z, @OriginalArg(4) int x, @OriginalArg(5) boolean blocksProjectiles, @OriginalArg(6) int sizeZ) {
		@Pc(13) int flag = 256;
		if (blocksProjectiles) {
			flag = 131328;
		}
		@Pc(24) int adjustedX = x - this.xOffset;
		@Pc(29) int adjustedZ = z - this.zOffset;
		if (blocksAltRoute) {
			flag |= 0x40000000;
		}
		for (@Pc(37) int i = adjustedX; i < adjustedX + sizeX; i++) {
			if (i >= 0 && this.sizeX > i) {
				for (@Pc(54) int j = adjustedZ; j < adjustedZ + sizeZ; j++) {
					if (j >= 0 && this.sizeY > j) {
						this.addFlag(i, flag, j);
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(ZIIIIZI)V")
	public void unflagBoundary(@OriginalArg(0) boolean blocksProjectiles, @OriginalArg(1) int rotation, @OriginalArg(2) int x, @OriginalArg(3) int shape, @OriginalArg(4) int z, @OriginalArg(5) boolean blocksAltRoute) {
		@Pc(8) int adjustedX = x - this.xOffset;
		@Pc(17) int adjustedZ = z - this.zOffset;
		if (shape == 0) {
			if (rotation == 0) {
				this.removeFlag(128, adjustedX, adjustedZ);
				this.removeFlag(8, adjustedX - 1, adjustedZ);
			}
			if (rotation == 1) {
				this.removeFlag(2, adjustedX, adjustedZ);
				this.removeFlag(32, adjustedX, adjustedZ + 1);
			}
			if (rotation == 2) {
				this.removeFlag(8, adjustedX, adjustedZ);
				this.removeFlag(128, adjustedX + 1, adjustedZ);
			}
			if (rotation == 3) {
				this.removeFlag(32, adjustedX, adjustedZ);
				this.removeFlag(2, adjustedX, adjustedZ - 1);
			}
		}
		if (shape == 1 || shape == 3) {
			if (rotation == 0) {
				this.removeFlag(1, adjustedX, adjustedZ);
				this.removeFlag(16, adjustedX - 1, adjustedZ + 1);
			}
			if (rotation == 1) {
				this.removeFlag(4, adjustedX, adjustedZ);
				this.removeFlag(64, adjustedX + 1, adjustedZ + 1);
			}
			if (rotation == 2) {
				this.removeFlag(16, adjustedX, adjustedZ);
				this.removeFlag(1, adjustedX + 1, adjustedZ + -1);
			}
			if (rotation == 3) {
				this.removeFlag(64, adjustedX, adjustedZ);
				this.removeFlag(4, adjustedX - 1, adjustedZ + -1);
			}
		}
		if (shape == 2) {
			if (rotation == 0) {
				this.removeFlag(130, adjustedX, adjustedZ);
				this.removeFlag(8, adjustedX - 1, adjustedZ);
				this.removeFlag(32, adjustedX, adjustedZ + 1);
			}
			if (rotation == 1) {
				this.removeFlag(10, adjustedX, adjustedZ);
				this.removeFlag(32, adjustedX, adjustedZ + 1);
				this.removeFlag(128, adjustedX + 1, adjustedZ);
			}
			if (rotation == 2) {
				this.removeFlag(40, adjustedX, adjustedZ);
				this.removeFlag(128, adjustedX + 1, adjustedZ);
				this.removeFlag(2, adjustedX, adjustedZ - 1);
			}
			if (rotation == 3) {
				this.removeFlag(160, adjustedX, adjustedZ);
				this.removeFlag(2, adjustedX, adjustedZ - 1);
				this.removeFlag(8, adjustedX - 1, adjustedZ);
			}
		}
		if (blocksProjectiles) {
			if (shape == 0) {
				if (rotation == 0) {
					this.removeFlag(65536, adjustedX, adjustedZ);
					this.removeFlag(4096, adjustedX - 1, adjustedZ);
				}
				if (rotation == 1) {
					this.removeFlag(1024, adjustedX, adjustedZ);
					this.removeFlag(16384, adjustedX, adjustedZ + 1);
				}
				if (rotation == 2) {
					this.removeFlag(4096, adjustedX, adjustedZ);
					this.removeFlag(65536, adjustedX + 1, adjustedZ);
				}
				if (rotation == 3) {
					this.removeFlag(16384, adjustedX, adjustedZ);
					this.removeFlag(1024, adjustedX, adjustedZ - 1);
				}
			}
			if (shape == 1 || shape == 3) {
				if (rotation == 0) {
					this.removeFlag(512, adjustedX, adjustedZ);
					this.removeFlag(8192, adjustedX - 1, adjustedZ + 1);
				}
				if (rotation == 1) {
					this.removeFlag(2048, adjustedX, adjustedZ);
					this.removeFlag(32768, adjustedX + 1, adjustedZ - -1);
				}
				if (rotation == 2) {
					this.removeFlag(8192, adjustedX, adjustedZ);
					this.removeFlag(512, adjustedX + 1, adjustedZ - 1);
				}
				if (rotation == 3) {
					this.removeFlag(32768, adjustedX, adjustedZ);
					this.removeFlag(2048, adjustedX - 1, adjustedZ - 1);
				}
			}
			if (shape == 2) {
				if (rotation == 0) {
					this.removeFlag(66560, adjustedX, adjustedZ);
					this.removeFlag(4096, adjustedX - 1, adjustedZ);
					this.removeFlag(16384, adjustedX, adjustedZ + 1);
				}
				if (rotation == 1) {
					this.removeFlag(5120, adjustedX, adjustedZ);
					this.removeFlag(16384, adjustedX, adjustedZ + 1);
					this.removeFlag(65536, adjustedX + 1, adjustedZ);
				}
				if (rotation == 2) {
					this.removeFlag(20480, adjustedX, adjustedZ);
					this.removeFlag(65536, adjustedX + 1, adjustedZ);
					this.removeFlag(1024, adjustedX, adjustedZ - 1);
				}
				if (rotation == 3) {
					this.removeFlag(81920, adjustedX, adjustedZ);
					this.removeFlag(1024, adjustedX, adjustedZ - 1);
					this.removeFlag(4096, adjustedX - 1, adjustedZ);
				}
			}
		}
		if (!blocksAltRoute) {
			return;
		}
		if (shape == 0) {
			if (rotation == 0) {
				this.removeFlag(536870912, adjustedX, adjustedZ);
				this.removeFlag(33554432, adjustedX - 1, adjustedZ);
			}
			if (rotation == 1) {
				this.removeFlag(8388608, adjustedX, adjustedZ);
				this.removeFlag(134217728, adjustedX, adjustedZ + 1);
			}
			if (rotation == 2) {
				this.removeFlag(33554432, adjustedX, adjustedZ);
				this.removeFlag(536870912, adjustedX + 1, adjustedZ);
			}
			if (rotation == 3) {
				this.removeFlag(134217728, adjustedX, adjustedZ);
				this.removeFlag(8388608, adjustedX, adjustedZ - 1);
			}
		}
		if (shape == 1 || shape == 3) {
			if (rotation == 0) {
				this.removeFlag(4194304, adjustedX, adjustedZ);
				this.removeFlag(67108864, adjustedX - 1, adjustedZ + 1);
			}
			if (rotation == 1) {
				this.removeFlag(16777216, adjustedX, adjustedZ);
				this.removeFlag(268435456, adjustedX + 1, adjustedZ + 1);
			}
			if (rotation == 2) {
				this.removeFlag(67108864, adjustedX, adjustedZ);
				this.removeFlag(4194304, adjustedX + 1, adjustedZ + -1);
			}
			if (rotation == 3) {
				this.removeFlag(268435456, adjustedX, adjustedZ);
				this.removeFlag(16777216, adjustedX - 1, adjustedZ + -1);
			}
		}
		if (shape != 2) {
			return;
		}
		if (rotation == 0) {
			this.removeFlag(545259520, adjustedX, adjustedZ);
			this.removeFlag(33554432, adjustedX - 1, adjustedZ);
			this.removeFlag(134217728, adjustedX, adjustedZ + 1);
		}
		if (rotation == 1) {
			this.removeFlag(41943040, adjustedX, adjustedZ);
			this.removeFlag(134217728, adjustedX, adjustedZ + 1);
			this.removeFlag(536870912, adjustedX + 1, adjustedZ);
		}
		if (rotation == 2) {
			this.removeFlag(167772160, adjustedX, adjustedZ);
			this.removeFlag(536870912, adjustedX + 1, adjustedZ);
			this.removeFlag(8388608, adjustedX, adjustedZ - 1);
		}
		if (rotation == 3) {
			this.removeFlag(671088640, adjustedX, adjustedZ);
			this.removeFlag(8388608, adjustedX, adjustedZ - 1);
			this.removeFlag(33554432, adjustedX - 1, adjustedZ);
			return;
		}
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIB)V")
	public void flagBlocked(@OriginalArg(0) int z, @OriginalArg(1) int x) {
		@Pc(10) int x1 = z - this.zOffset;
		@Pc(19) int z1 = x - this.xOffset;
		this.collisionFlags[z1][x1] |= CollisionFlag.BLOCKWALK;
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IZZIIIII)V")
	public void unflagBlockedObject(@OriginalArg(0) int rotation, @OriginalArg(1) boolean blocksAltRoute, @OriginalArg(2) boolean blocksProjectiles, @OriginalArg(3) int x, @OriginalArg(4) int z, @OriginalArg(5) int sizeZ, @OriginalArg(7) int sizeX) {
		@Pc(11) int flag = 256;
		if (blocksProjectiles) {
			flag = 131328;
		}
		@Pc(22) int adjustedX = x - this.xOffset;
		@Pc(27) int adjustedZ = z - this.zOffset;
		if (blocksAltRoute) {
			flag |= 0x40000000;
		}
		@Pc(41) int local41;
		if (rotation == 1 || rotation == 3) {
			int swappedSize = sizeX;
			sizeX = sizeZ;
			sizeZ = swappedSize;
		}
		for (int tileX = adjustedX; tileX < sizeX + adjustedX; tileX++) {
			if (tileX >= 0 && tileX < this.sizeX) {
				for (@Pc(60) int i = adjustedZ; i < adjustedZ + sizeZ; i++) {
					if (i >= 0 && this.sizeY > i) {
						this.removeFlag(flag, tileX, i);
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIIIIZZ)V")
	public void flagBoundary(@OriginalArg(0) int z, @OriginalArg(1) int x, @OriginalArg(3) int shape, @OriginalArg(4) int rotation, @OriginalArg(5) boolean blocksAltRoute, @OriginalArg(6) boolean blocksProjectiles) {
		@Pc(16) int adjustedZ = z - this.zOffset;
		@Pc(21) int adjustedX = x - this.xOffset;
		if (shape == 0) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 128, adjustedZ);
				this.addFlag(adjustedX - 1, 8, adjustedZ);
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 2, adjustedZ);
				this.addFlag(adjustedX, 32, adjustedZ + 1);
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 8, adjustedZ);
				this.addFlag(adjustedX + 1, 128, adjustedZ);
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 32, adjustedZ);
				this.addFlag(adjustedX, 2, adjustedZ - 1);
			}
		}
		if (shape == 1 || shape == 3) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 1, adjustedZ);
				this.addFlag(adjustedX - 1, 16, adjustedZ + 1);
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 4, adjustedZ);
				this.addFlag(adjustedX + 1, 64, adjustedZ + 1);
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 16, adjustedZ);
				this.addFlag(adjustedX + 1, 1, adjustedZ - 1);
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 64, adjustedZ);
				this.addFlag(adjustedX - 1, 4, adjustedZ - 1);
			}
		}
		if (shape == 2) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 130, adjustedZ);
				this.addFlag(adjustedX - 1, 8, adjustedZ);
				this.addFlag(adjustedX, 32, adjustedZ + 1);
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 10, adjustedZ);
				this.addFlag(adjustedX, 32, adjustedZ + 1);
				this.addFlag(adjustedX + 1, 128, adjustedZ);
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 40, adjustedZ);
				this.addFlag(adjustedX + 1, 128, adjustedZ);
				this.addFlag(adjustedX, 2, adjustedZ - 1);
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 160, adjustedZ);
				this.addFlag(adjustedX, 2, adjustedZ - 1);
				this.addFlag(adjustedX - 1, 8, adjustedZ);
			}
		}
		if (blocksProjectiles) {
			if (shape == 0) {
				if (rotation == 0) {
					this.addFlag(adjustedX, 65536, adjustedZ);
					this.addFlag(adjustedX - 1, 4096, adjustedZ);
				}
				if (rotation == 1) {
					this.addFlag(adjustedX, 1024, adjustedZ);
					this.addFlag(adjustedX, 16384, adjustedZ + 1);
				}
				if (rotation == 2) {
					this.addFlag(adjustedX, 4096, adjustedZ);
					this.addFlag(adjustedX + 1, 65536, adjustedZ);
				}
				if (rotation == 3) {
					this.addFlag(adjustedX, 16384, adjustedZ);
					this.addFlag(adjustedX, 1024, adjustedZ - 1);
				}
			}
			if (shape == 1 || shape == 3) {
				if (rotation == 0) {
					this.addFlag(adjustedX, 512, adjustedZ);
					this.addFlag(adjustedX - 1, 8192, adjustedZ + 1);
				}
				if (rotation == 1) {
					this.addFlag(adjustedX, 2048, adjustedZ);
					this.addFlag(adjustedX + 1, 32768, adjustedZ + 1);
				}
				if (rotation == 2) {
					this.addFlag(adjustedX, 8192, adjustedZ);
					this.addFlag(adjustedX + 1, 512, adjustedZ - 1);
				}
				if (rotation == 3) {
					this.addFlag(adjustedX, 32768, adjustedZ);
					this.addFlag(adjustedX - 1, 2048, adjustedZ - 1);
				}
			}
			if (shape == 2) {
				if (rotation == 0) {
					this.addFlag(adjustedX, 66560, adjustedZ);
					this.addFlag(adjustedX - 1, 4096, adjustedZ);
					this.addFlag(adjustedX, 16384, adjustedZ + 1);
				}
				if (rotation == 1) {
					this.addFlag(adjustedX, 5120, adjustedZ);
					this.addFlag(adjustedX, 16384, adjustedZ + 1);
					this.addFlag(adjustedX + 1, 65536, adjustedZ);
				}
				if (rotation == 2) {
					this.addFlag(adjustedX, 20480, adjustedZ);
					this.addFlag(adjustedX + 1, 65536, adjustedZ);
					this.addFlag(adjustedX, 1024, adjustedZ - 1);
				}
				if (rotation == 3) {
					this.addFlag(adjustedX, 81920, adjustedZ);
					this.addFlag(adjustedX, 1024, adjustedZ - 1);
					this.addFlag(adjustedX - 1, 4096, adjustedZ);
				}
			}
		}
		if (!blocksAltRoute) {
			return;
		}
		if (shape == 0) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 536870912, adjustedZ);
				this.addFlag(adjustedX - 1, 33554432, adjustedZ);
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 8388608, adjustedZ);
				this.addFlag(adjustedX, 134217728, adjustedZ + 1);
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 33554432, adjustedZ);
				this.addFlag(adjustedX + 1, 536870912, adjustedZ);
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 134217728, adjustedZ);
				this.addFlag(adjustedX, 8388608, adjustedZ - 1);
			}
		}
		if (shape == 1 || shape == 3) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 4194304, adjustedZ);
				this.addFlag(adjustedX - 1, 67108864, adjustedZ + 1);
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 16777216, adjustedZ);
				this.addFlag(adjustedX + 1, 268435456, adjustedZ + 1);
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 67108864, adjustedZ);
				this.addFlag(adjustedX + 1, 4194304, adjustedZ - 1);
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 268435456, adjustedZ);
				this.addFlag(adjustedX - 1, 16777216, adjustedZ - 1);
			}
		}
		if (shape != 2) {
			return;
		}
		if (rotation == 0) {
			this.addFlag(adjustedX, 545259520, adjustedZ);
			this.addFlag(adjustedX - 1, 33554432, adjustedZ);
			this.addFlag(adjustedX, 134217728, adjustedZ + 1);
		}
		if (rotation == 1) {
			this.addFlag(adjustedX, 41943040, adjustedZ);
			this.addFlag(adjustedX, 134217728, adjustedZ + 1);
			this.addFlag(adjustedX + 1, 536870912, adjustedZ);
		}
		if (rotation == 2) {
			this.addFlag(adjustedX, 167772160, adjustedZ);
			this.addFlag(adjustedX + 1, 536870912, adjustedZ);
			this.addFlag(adjustedX, 8388608, adjustedZ - 1);
		}
		if (rotation == 3) {
			this.addFlag(adjustedX, 671088640, adjustedZ);
			this.addFlag(adjustedX, 8388608, adjustedZ - 1);
			this.addFlag(adjustedX - 1, 33554432, adjustedZ);
			return;
		}
	}
	@OriginalMember(owner = "client!jd", name = "b", descriptor = "(III)Lclient!fe;")
	public static CollisionMap create(@OriginalArg(0) int sizeY, @OriginalArg(2) int sizeX) {
		@Pc(7) CollisionMap collisionMap = new CollisionMap();
		collisionMap.sizeX = sizeX + 6;
		collisionMap.xOffset = -1;
		collisionMap.sizeY = sizeY + 1 + 5;
		collisionMap.zOffset = -1;
		collisionMap.collisionFlags = new int[collisionMap.sizeX][collisionMap.sizeY];
		collisionMap.reset();
		return collisionMap;
	}
}
