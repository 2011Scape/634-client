package com.jagex.game.runetek5.scene

import com.jagex.game.runetek5.core.constants.LocShapes
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.scene.CollisionFlag
import com.jagex.game.runetek5.collision.DirectionFlag
import com.jagex.game.runetek5.PathFinder

@OriginalClass("client!fe")
class CollisionMap {

	@OriginalMember(owner = "client!fe", name = "g", descriptor = "I")
	@JvmField
	var sizeX: Int = 0

	@OriginalMember(owner = "client!fe", name = "h", descriptor = "[[I")
	@JvmField
	var collisionFlags: Array<IntArray?>? = null

	@OriginalMember(owner = "client!fe", name = "k", descriptor = "I")
	@JvmField
	var zOffset: Int = 0

	@OriginalMember(owner = "client!fe", name = "m", descriptor = "I")
	@JvmField
	var sizeY: Int = 0

	@OriginalMember(owner = "client!fe", name = "w", descriptor = "I")
	@JvmField
	var xOffset: Int = 0

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIII)V")
	private fun removeFlag(@OriginalArg(0) flag: Int, @OriginalArg(2) x: Int, @OriginalArg(3) z: Int) {
		this.collisionFlags!![x]!![z] = this.collisionFlags!![x]!![z] and flag.inv()
	}

	@OriginalMember(owner = "client!fe", name = "b", descriptor = "(IIII)V")
	private fun addFlag(@OriginalArg(0) x: Int, @OriginalArg(1) flag: Int, @OriginalArg(2) z: Int) {
		this.collisionFlags!![x]!![z] = this.collisionFlags!![x]!![z] or flag
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIIIIIII)Z")
	fun canReachWall(@OriginalArg(0) destX: Int, @OriginalArg(1) srcZ: Int, @OriginalArg(2) size: Int, @OriginalArg(3) rotation: Int, @OriginalArg(4) srcX: Int, @OriginalArg(5) destZ: Int, @OriginalArg(6) shape: Int): Boolean {
		if (size == 1) {
			if (destX == srcX && destZ == srcZ) {
				return true
			}
		} else if (srcX <= destX && srcX + size - 1 >= destX && destZ <= destZ && size + destZ - 1 >= destZ) {
			return true
		}
		@Pc(67) var adjustedDestZ: Int = destZ - this.zOffset
		@Pc(72) var adjustedSrcZ: Int = srcZ - this.zOffset
		@Pc(77) var adjustedDestX: Int = destX - this.xOffset
		@Pc(82) var adjustedSrcX: Int = srcX - this.xOffset
		if (size == 1) {
			if (shape == LocShapes.WALL_STRAIGHT) {
				if (rotation == 0) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ) {
						return true
					}
					if (adjustedDestX == adjustedSrcX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0120) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX && adjustedSrcZ == adjustedDestZ - 1 && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0102) == 0) {
						return true
					}
				} else if (rotation == 1) {
					if (adjustedSrcX == adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ) {
						return true
					}
					if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0108) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ == adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0180) == 0) {
						return true
					}
				} else if (rotation == 2) {
					if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ == adjustedDestZ) {
						return true
					}
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0120) == 0) {
						return true
					}
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ - 1 && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0102) == 0) {
						return true
					}
				} else if (rotation == 3) {
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ - 1) {
						return true
					}
					if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0108) == 0) {
						return true
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0180) == 0) {
						return true
					}
				}
			}
			if (shape == LocShapes.WALL_L) {
				if (rotation == 0) {
					if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ) {
						return true
					}
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ + 1) {
						return true
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ == adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0180) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0102) == 0) {
						return true
					}
				} else if (rotation == 1) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0108) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ) {
						return true
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ == adjustedDestZ) {
						return true
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0102) == 0) {
						return true
					}
				} else if (rotation == 2) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedDestZ == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0108) == 0) {
						return true
					}
					if (adjustedDestX == adjustedSrcX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0120) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ == adjustedSrcZ) {
						return true
					}
					if (adjustedDestX == adjustedSrcX && adjustedDestZ - 1 == adjustedSrcZ) {
						return true
					}
				} else if (rotation == 3) {
					if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ) {
						return true
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0120) == 0) {
						return true
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and 0x2C0180) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ) {
						return true
					}
				}
			}
			if (shape == LocShapes.WALL_DIAGONAL) {
				if (adjustedSrcX == adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_SOUTH) == 0) {
					return true
				}
				if (adjustedDestX == adjustedSrcX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_NORTH) == 0) {
					return true
				}
				if (adjustedSrcX == adjustedDestX - 1 && adjustedDestZ == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_EAST) == 0) {
					return true
				}
				if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_WEST) == 0) {
					return true
				}
			}
		} else {
			@Pc(803) var srcEndX: Int = size + adjustedSrcX - 1
			@Pc(809) var srcEndZ: Int = size + adjustedSrcZ - 1
			if (shape == LocShapes.WALL_STRAIGHT) {
				if (rotation == 0) {
					if (adjustedDestX - size == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ) {
						return true
					}
					if (adjustedDestX >= adjustedSrcX && srcEndX >= adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedDestX]!![adjustedSrcZ] and 0x2C0120) == 0) {
						return true
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags!![adjustedDestX]!![srcEndZ] and 0x2C0102) == 0) {
						return true
					}
				} else if (rotation == 1) {
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ + 1) {
						return true
					}
					if (adjustedDestX - size == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && adjustedDestZ <= srcEndZ && (this.collisionFlags!![srcEndX]!![adjustedDestZ] and 0x2C0108) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags!![adjustedSrcX]!![adjustedDestZ] and 0x2C0180) == 0) {
						return true
					}
				} else if (rotation == 2) {
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ) {
						return true
					}
					if (adjustedDestX >= adjustedSrcX && srcEndX >= adjustedDestX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags!![adjustedDestX]!![adjustedSrcZ] and 0x2C0120) == 0) {
						return true
					}
					if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags!![adjustedDestX]!![srcEndZ] and 0x2C0102) == 0) {
						return true
					}
				} else if (rotation == 3) {
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ) {
						return true
					}
					if (adjustedSrcX == adjustedDestX - size && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags!![srcEndX]!![adjustedDestZ] and 0x2C0108) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedDestZ] and 0x2C0180) == 0) {
						return true
					}
				}
			}
			if (shape == LocShapes.WALL_L) {
				if (rotation == 0) {
					if (adjustedSrcX == adjustedDestX - size && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ) {
						return true
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedDestZ + 1 == adjustedSrcZ) {
						return true
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedDestZ] and 0x2C0180) == 0) {
						return true
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ && (this.collisionFlags!![adjustedDestX]!![srcEndZ] and 0x2C0102) == 0) {
						return true
					}
				} else if (rotation == 1) {
					if (adjustedDestX - size == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ && (this.collisionFlags!![srcEndX]!![adjustedDestZ] and 0x2C0108) == 0) {
						return true
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ + 1) {
						return true
					}
					if (adjustedSrcX == adjustedDestX + 1 && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ) {
						return true
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ && (this.collisionFlags!![adjustedDestX]!![srcEndZ] and 0x2C0102) == 0) {
						return true
					}
				} else if (rotation == 2) {
					if (adjustedSrcX == adjustedDestX - size && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags!![srcEndX]!![adjustedDestZ] and 0x2C0108) == 0) {
						return true
					}
					if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedDestX]!![adjustedSrcZ] and 0x2C0120) == 0) {
						return true
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ) {
						return true
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ) {
						return true
					}
				} else if (rotation == 3) {
					if (adjustedDestX - size == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ) {
						return true
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags!![adjustedDestX]!![adjustedSrcZ] and 0x2C0120) == 0) {
						return true
					}
					if (adjustedDestX + 1 == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags!![adjustedSrcX]!![adjustedDestZ] and 0x2C0180) == 0) {
						return true
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedDestZ - size == adjustedSrcZ) {
						return true
					}
				}
			}
			if (shape == LocShapes.WALL_DIAGONAL) {
				if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedDestX]!![adjustedSrcZ] and 0x2C0120) == 0) {
					return true
				}
				if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags!![adjustedDestX]!![srcEndZ] and 0x2C0102) == 0) {
					return true
				}
				if (adjustedDestX - size == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && adjustedDestZ <= srcEndZ && (this.collisionFlags!![srcEndX]!![adjustedDestZ] and 0x2C0108) == 0) {
					return true
				}
				if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedDestZ] and 0x2C0180) == 0) {
					return true
				}
			}
		}
		return false
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(III)V")
	fun flagFloorDecoration(@OriginalArg(0) z: Int, @OriginalArg(1) x: Int) {
		@Pc(4) var adjustedZ: Int = z - this.zOffset
		@Pc(25) var adjustedX: Int = x - this.xOffset
		this.collisionFlags!![adjustedX]!![adjustedZ] = this.collisionFlags!![adjustedX]!![adjustedZ] or 0x40000
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIIIIIIIII)Z")
	fun isOutsideRect(@OriginalArg(1) destX: Int, @OriginalArg(2) accessFlags: Int, @OriginalArg(3) srcSizeX: Int, @OriginalArg(4) x1: Int, @OriginalArg(5) srcX: Int, @OriginalArg(6) srcSizeZ: Int, @OriginalArg(7) srcZ: Int, @OriginalArg(8) destZ: Int, @OriginalArg(9) destSizeX: Int): Boolean {
		@Pc(24) var srcMaxX: Int = srcX + srcSizeX
		@Pc(28) var destZ1: Int = srcSizeZ + srcZ
		@Pc(33) var destMaxX: Int = destX + destSizeX
		@Pc(38) var destMaxZ: Int = destZ + x1
		@Pc(56) var z0: Int
		@Pc(67) var overlapMax: Int
		if (srcX == destMaxX && (accessFlags and DirectionFlag.EAST) == 0) {
			z0 = if (destZ < srcZ) srcZ else destZ
			overlapMax = if (destMaxZ > destZ1) destZ1 else destMaxZ
			while (z0 < overlapMax) {
				if ((this.collisionFlags!![destMaxX - this.xOffset - 1]!![z0 - this.zOffset] and CollisionFlag.WALL_EAST) == 0) {
					return true
				}
				z0++
			}
		} else if (srcMaxX == destX && (accessFlags and DirectionFlag.WEST) == 0) {
			z0 = if (destZ >= srcZ) destZ else srcZ
			overlapMax = if (destZ1 >= destMaxZ) destMaxZ else destZ1
			while (overlapMax > z0) {
				if ((this.collisionFlags!![destX - this.xOffset]!![z0 - this.zOffset] and CollisionFlag.WALL_WEST) == 0) {
					return true
				}
				z0++
			}
		} else if (destMaxZ == srcZ && (accessFlags and DirectionFlag.NORTH) == 0) {
			z0 = if (srcX > destX) srcX else destX
			overlapMax = if (srcMaxX < destMaxX) srcMaxX else destMaxX
			while (overlapMax > z0) {
				if ((this.collisionFlags!![z0 - this.xOffset]!![destMaxZ - this.zOffset - 1] and CollisionFlag.WALL_NORTH) == 0) {
					return true
				}
				z0++
			}
		} else if (destZ1 == destZ && (accessFlags and DirectionFlag.SOUTH) == 0) {
			z0 = if (destX < srcX) srcX else destX
			overlapMax = if (srcMaxX < destMaxX) srcMaxX else destMaxX
			while (overlapMax > z0) {
				if ((this.collisionFlags!![z0 - this.xOffset]!![destZ - this.zOffset] and CollisionFlag.WALL_SOUTH) == 0) {
					return true
				}
				z0++
			}
		}
		return false
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(BII)V")
	fun unflagFloorDecoration(@OriginalArg(1) x: Int, @OriginalArg(2) z: Int) {
		@Pc(4) var adjustedZ: Int = z - this.zOffset
		@Pc(9) var adjustedX: Int = x - this.xOffset
		this.collisionFlags!![adjustedX]!![adjustedZ] = this.collisionFlags!![adjustedX]!![adjustedZ] and 0xFFFBFFFF.toInt()
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(I)V")
	fun reset() {
		@Pc(3) var x: Int = 0
		while (x < this.sizeX) {
			@Pc(20) var z: Int = 0
			while (z < this.sizeY) {
				if (x == 0 || z == 0 || x >= this.sizeX - 5 || this.sizeY - 5 <= z) {
					this.collisionFlags!![x]!![z] = -1
				} else {
					this.collisionFlags!![x]!![z] = 2097152
				}
				z++
			}
			x++
		}
	}

	@OriginalMember(owner = "client!fe", name = "b", descriptor = "(BII)V")
	fun unflagBlocked(@OriginalArg(1) z: Int, @OriginalArg(2) x: Int) {
		@Pc(4) var z1: Int = x - this.xOffset
		@Pc(13) var x1: Int = z - this.zOffset
		this.collisionFlags!![z1]!![x1] = this.collisionFlags!![z1]!![x1] and 0xFFDFFFFF.toInt()
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IBIIIIIII)Z")
	fun isInsideOrOutsideRect(@OriginalArg(0) destZ1: Int, @OriginalArg(2) direction: Int, @OriginalArg(3) destX1: Int, @OriginalArg(4) x1: Int, @OriginalArg(5) size: Int, @OriginalArg(6) destLength: Int, @OriginalArg(7) destWidth: Int, @OriginalArg(8) z1: Int): Boolean {
		if (destX1 > 1) {
			return if (PathFinder.rectanglesOverlap(size, direction, destX1, destZ1, destWidth, z1, destX1, x1)) true else this.isOutsideRect(z1, destLength, destX1, x1, destZ1, destX1, destWidth, direction, size)
		}
		@Pc(46) var destX2: Int = size + z1 - 1
		@Pc(53) var destZ2: Int = direction + x1 - 1
		if (destZ1 >= z1 && destX2 >= destZ1 && destWidth >= direction && destWidth <= destZ2) {
			return true
		} else if (destZ1 == z1 - 1 && direction <= destWidth && destWidth <= destZ2 && (this.collisionFlags!![destZ1 - this.xOffset]!![destWidth - this.zOffset] and CollisionFlag.WALL_EAST) == 0 && (destLength and DirectionFlag.WEST) == 0) {
			return true
		} else if (destX2 + 1 == destZ1 && direction <= destWidth && destWidth <= destZ2 && (this.collisionFlags!![destZ1 - this.xOffset]!![destWidth - this.zOffset] and CollisionFlag.WALL_WEST) == 0 && (destLength and DirectionFlag.EAST) == 0) {
			return true
		} else if (direction - 1 == destWidth && destZ1 >= z1 && destX2 >= destZ1 && (this.collisionFlags!![destZ1 - this.xOffset]!![destWidth - this.zOffset] and CollisionFlag.WALL_NORTH) == 0 && (destLength and DirectionFlag.SOUTH) == 0) {
			return true
		} else {
			return destWidth == destZ2 + 1 && destZ1 >= z1 && destZ1 <= destX2 && (this.collisionFlags!![destZ1 - this.xOffset]!![destWidth - this.zOffset] and CollisionFlag.WALL_SOUTH) == 0 && (destLength and DirectionFlag.NORTH) == 0
		}
	}

	@OriginalMember(owner = "client!fe", name = "b", descriptor = "(IIIIIIII)Z")
	fun canReachWallDecoration(@OriginalArg(0) destZ: Int, @OriginalArg(1) srcZ: Int, @OriginalArg(2) destX: Int, @OriginalArg(3) srcX: Int, @OriginalArg(4) shape: Int, @OriginalArg(6) rotation: Int, @OriginalArg(7) size: Int): Boolean {
		var rotation: Int = rotation
		if (size == 1) {
			if (srcX == destX && destZ == srcZ) {
				return true
			}
		} else if (srcX <= destX && destX <= size + srcX - 1 && destZ <= destZ && destZ <= size + destZ - 1) {
			return true
		}
		@Pc(61) var adjustedDestX: Int = destX - this.xOffset
		@Pc(66) var adjustedSrcX: Int = srcX - this.xOffset
		@Pc(71) var adjustedDestZ: Int = destZ - this.zOffset
		@Pc(76) var adjustedSrcZ: Int = srcZ - this.zOffset
		if (size == 1) {
			if (shape == LocShapes.WALLDECOR_DIAGONAL_OFFSET || shape == LocShapes.WALLDECOR_DIAGONAL_NOOFFSET) {
				if (shape == 7) {
					rotation = (rotation + 2) and 0x3
				}
				if (rotation == 0) {
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_WEST) == 0) {
						return true
					}
					if (adjustedDestX == adjustedSrcX && adjustedSrcZ == adjustedDestZ - 1 && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_NORTH) == 0) {
						return true
					}
				} else if (rotation == 1) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedDestZ == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_EAST) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_NORTH) == 0) {
						return true
					}
				} else if (rotation == 2) {
					if (adjustedDestX - 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_EAST) == 0) {
						return true
					}
					if (adjustedDestX == adjustedSrcX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_SOUTH) == 0) {
						return true
					}
				} else if (rotation == 3) {
					if (adjustedDestX + 1 == adjustedSrcX && adjustedDestZ == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_WEST) == 0) {
						return true
					}
					if (adjustedSrcX == adjustedDestX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_SOUTH) == 0) {
						return true
					}
				}
			}
			if (shape == LocShapes.WALLDECOR_DIAGONAL_BOTH) {
				if (adjustedSrcX == adjustedDestX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_SOUTH) == 0) {
					return true
				}
				if (adjustedSrcX == adjustedDestX && adjustedDestZ - 1 == adjustedSrcZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_NORTH) == 0) {
					return true
				}
				if (adjustedSrcX == adjustedDestX - 1 && adjustedSrcZ == adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_EAST) == 0) {
					return true
				}
				if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ == adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedSrcZ] and CollisionFlag.WALL_WEST) == 0) {
					return true
				}
			}
		} else {
			@Pc(87) var srcEndX: Int = size + adjustedSrcX - 1
			@Pc(94) var srcEndZ: Int = adjustedSrcZ + size - 1
			if (shape == LocShapes.WALLDECOR_DIAGONAL_OFFSET || shape == LocShapes.WALLDECOR_DIAGONAL_NOOFFSET) {
				if (shape == LocShapes.WALLDECOR_DIAGONAL_NOOFFSET) {
					rotation = (rotation + 2) and 0x3
				}
				if (rotation == 0) {
					if (adjustedDestX + 1 == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags!![adjustedSrcX]!![adjustedDestZ] and CollisionFlag.WALL_WEST) == 0) {
						return true
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags!![adjustedDestX]!![srcEndZ] and CollisionFlag.WALL_NORTH) == 0) {
						return true
					}
				} else if (rotation == 1) {
					if (adjustedDestX - size == adjustedSrcX && adjustedSrcZ <= adjustedDestZ && srcEndZ >= adjustedDestZ && (this.collisionFlags!![srcEndX]!![adjustedDestZ] and CollisionFlag.WALL_EAST) == 0) {
						return true
					}
					if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags!![adjustedDestX]!![srcEndZ] and CollisionFlag.WALL_NORTH) == 0) {
						return true
					}
				} else if (rotation == 2) {
					if (adjustedDestX - size == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && srcEndZ >= adjustedDestZ && (this.collisionFlags!![srcEndX]!![adjustedDestZ] and CollisionFlag.WALL_EAST) == 0) {
						return true
					}
					if (adjustedSrcX <= adjustedDestX && adjustedDestX <= srcEndX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedDestX]!![adjustedSrcZ] and CollisionFlag.WALL_SOUTH) == 0) {
						return true
					}
				} else if (rotation == 3) {
					if (adjustedDestX + 1 == adjustedSrcX && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags!![adjustedSrcX]!![adjustedDestZ] and CollisionFlag.WALL_WEST) == 0) {
						return true
					}
					if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedSrcZ == adjustedDestZ + 1 && (this.collisionFlags!![adjustedDestX]!![adjustedSrcZ] and CollisionFlag.WALL_SOUTH) == 0) {
						return true
					}
				}
			}
			if (shape == LocShapes.WALLDECOR_DIAGONAL_BOTH) {
				if (adjustedSrcX <= adjustedDestX && srcEndX >= adjustedDestX && adjustedDestZ + 1 == adjustedSrcZ && (this.collisionFlags!![adjustedDestX]!![adjustedSrcZ] and CollisionFlag.WALL_SOUTH) == 0) {
					return true
				}
				if (adjustedDestX >= adjustedSrcX && adjustedDestX <= srcEndX && adjustedSrcZ == adjustedDestZ - size && (this.collisionFlags!![adjustedDestX]!![srcEndZ] and CollisionFlag.WALL_NORTH) == 0) {
					return true
				}
				if (adjustedSrcX == adjustedDestX - size && adjustedDestZ >= adjustedSrcZ && adjustedDestZ <= srcEndZ && (this.collisionFlags!![srcEndX]!![adjustedDestZ] and CollisionFlag.WALL_EAST) == 0) {
					return true
				}
				if (adjustedSrcX == adjustedDestX + 1 && adjustedSrcZ <= adjustedDestZ && adjustedDestZ <= srcEndZ && (this.collisionFlags!![adjustedSrcX]!![adjustedDestZ] and CollisionFlag.WALL_WEST) == 0) {
					return true
				}
			}
		}
		return false
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(ZIIZIZI)V")
	fun flagBlockedObject(@OriginalArg(0) blocksAltRoute: Boolean, @OriginalArg(1) sizeX: Int, @OriginalArg(2) z: Int, @OriginalArg(4) x: Int, @OriginalArg(5) blocksProjectiles: Boolean, @OriginalArg(6) sizeZ: Int) {
		@Pc(13) var flag: Int = 256
		if (blocksProjectiles) {
			flag = 131328
		}
		@Pc(24) var adjustedX: Int = x - this.xOffset
		@Pc(29) var adjustedZ: Int = z - this.zOffset
		if (blocksAltRoute) {
			flag = flag or 0x40000000
		}
		@Pc(37) var i: Int = adjustedX
		while (i < adjustedX + sizeX) {
			if (i >= 0 && this.sizeX > i) {
				@Pc(54) var j: Int = adjustedZ
				while (j < adjustedZ + sizeZ) {
					if (j >= 0 && this.sizeY > j) {
						this.addFlag(i, flag, j)
					}
					j++
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(ZIIIIZI)V")
	fun unflagBoundary(@OriginalArg(0) blocksProjectiles: Boolean, @OriginalArg(1) rotation: Int, @OriginalArg(2) x: Int, @OriginalArg(3) shape: Int, @OriginalArg(4) z: Int, @OriginalArg(5) blocksAltRoute: Boolean) {
		@Pc(8) var adjustedX: Int = x - this.xOffset
		@Pc(17) var adjustedZ: Int = z - this.zOffset
		if (shape == 0) {
			if (rotation == 0) {
				this.removeFlag(128, adjustedX, adjustedZ)
				this.removeFlag(8, adjustedX - 1, adjustedZ)
			}
			if (rotation == 1) {
				this.removeFlag(2, adjustedX, adjustedZ)
				this.removeFlag(32, adjustedX, adjustedZ + 1)
			}
			if (rotation == 2) {
				this.removeFlag(8, adjustedX, adjustedZ)
				this.removeFlag(128, adjustedX + 1, adjustedZ)
			}
			if (rotation == 3) {
				this.removeFlag(32, adjustedX, adjustedZ)
				this.removeFlag(2, adjustedX, adjustedZ - 1)
			}
		}
		if (shape == 1 || shape == 3) {
			if (rotation == 0) {
				this.removeFlag(1, adjustedX, adjustedZ)
				this.removeFlag(16, adjustedX - 1, adjustedZ + 1)
			}
			if (rotation == 1) {
				this.removeFlag(4, adjustedX, adjustedZ)
				this.removeFlag(64, adjustedX + 1, adjustedZ + 1)
			}
			if (rotation == 2) {
				this.removeFlag(16, adjustedX, adjustedZ)
				this.removeFlag(1, adjustedX + 1, adjustedZ + -1)
			}
			if (rotation == 3) {
				this.removeFlag(64, adjustedX, adjustedZ)
				this.removeFlag(4, adjustedX - 1, adjustedZ + -1)
			}
		}
		if (shape == 2) {
			if (rotation == 0) {
				this.removeFlag(130, adjustedX, adjustedZ)
				this.removeFlag(8, adjustedX - 1, adjustedZ)
				this.removeFlag(32, adjustedX, adjustedZ + 1)
			}
			if (rotation == 1) {
				this.removeFlag(10, adjustedX, adjustedZ)
				this.removeFlag(32, adjustedX, adjustedZ + 1)
				this.removeFlag(128, adjustedX + 1, adjustedZ)
			}
			if (rotation == 2) {
				this.removeFlag(40, adjustedX, adjustedZ)
				this.removeFlag(128, adjustedX + 1, adjustedZ)
				this.removeFlag(2, adjustedX, adjustedZ - 1)
			}
			if (rotation == 3) {
				this.removeFlag(160, adjustedX, adjustedZ)
				this.removeFlag(2, adjustedX, adjustedZ - 1)
				this.removeFlag(8, adjustedX - 1, adjustedZ)
			}
		}
		if (blocksProjectiles) {
			if (shape == 0) {
				if (rotation == 0) {
					this.removeFlag(65536, adjustedX, adjustedZ)
					this.removeFlag(4096, adjustedX - 1, adjustedZ)
				}
				if (rotation == 1) {
					this.removeFlag(1024, adjustedX, adjustedZ)
					this.removeFlag(16384, adjustedX, adjustedZ + 1)
				}
				if (rotation == 2) {
					this.removeFlag(4096, adjustedX, adjustedZ)
					this.removeFlag(65536, adjustedX + 1, adjustedZ)
				}
				if (rotation == 3) {
					this.removeFlag(16384, adjustedX, adjustedZ)
					this.removeFlag(1024, adjustedX, adjustedZ - 1)
				}
			}
			if (shape == 1 || shape == 3) {
				if (rotation == 0) {
					this.removeFlag(512, adjustedX, adjustedZ)
					this.removeFlag(8192, adjustedX - 1, adjustedZ + 1)
				}
				if (rotation == 1) {
					this.removeFlag(2048, adjustedX, adjustedZ)
					this.removeFlag(32768, adjustedX + 1, adjustedZ - -1)
				}
				if (rotation == 2) {
					this.removeFlag(8192, adjustedX, adjustedZ)
					this.removeFlag(512, adjustedX + 1, adjustedZ - 1)
				}
				if (rotation == 3) {
					this.removeFlag(32768, adjustedX, adjustedZ)
					this.removeFlag(2048, adjustedX - 1, adjustedZ - 1)
				}
			}
			if (shape == 2) {
				if (rotation == 0) {
					this.removeFlag(66560, adjustedX, adjustedZ)
					this.removeFlag(4096, adjustedX - 1, adjustedZ)
					this.removeFlag(16384, adjustedX, adjustedZ + 1)
				}
				if (rotation == 1) {
					this.removeFlag(5120, adjustedX, adjustedZ)
					this.removeFlag(16384, adjustedX, adjustedZ + 1)
					this.removeFlag(65536, adjustedX + 1, adjustedZ)
				}
				if (rotation == 2) {
					this.removeFlag(20480, adjustedX, adjustedZ)
					this.removeFlag(65536, adjustedX + 1, adjustedZ)
					this.removeFlag(1024, adjustedX, adjustedZ - 1)
				}
				if (rotation == 3) {
					this.removeFlag(81920, adjustedX, adjustedZ)
					this.removeFlag(1024, adjustedX, adjustedZ - 1)
					this.removeFlag(4096, adjustedX - 1, adjustedZ)
				}
			}
		}
		if (!blocksAltRoute) {
			return
		}
		if (shape == 0) {
			if (rotation == 0) {
				this.removeFlag(536870912, adjustedX, adjustedZ)
				this.removeFlag(33554432, adjustedX - 1, adjustedZ)
			}
			if (rotation == 1) {
				this.removeFlag(8388608, adjustedX, adjustedZ)
				this.removeFlag(134217728, adjustedX, adjustedZ + 1)
			}
			if (rotation == 2) {
				this.removeFlag(33554432, adjustedX, adjustedZ)
				this.removeFlag(536870912, adjustedX + 1, adjustedZ)
			}
			if (rotation == 3) {
				this.removeFlag(134217728, adjustedX, adjustedZ)
				this.removeFlag(8388608, adjustedX, adjustedZ - 1)
			}
		}
		if (shape == 1 || shape == 3) {
			if (rotation == 0) {
				this.removeFlag(4194304, adjustedX, adjustedZ)
				this.removeFlag(67108864, adjustedX - 1, adjustedZ + 1)
			}
			if (rotation == 1) {
				this.removeFlag(16777216, adjustedX, adjustedZ)
				this.removeFlag(268435456, adjustedX + 1, adjustedZ + 1)
			}
			if (rotation == 2) {
				this.removeFlag(67108864, adjustedX, adjustedZ)
				this.removeFlag(4194304, adjustedX + 1, adjustedZ + -1)
			}
			if (rotation == 3) {
				this.removeFlag(268435456, adjustedX, adjustedZ)
				this.removeFlag(16777216, adjustedX - 1, adjustedZ + -1)
			}
		}
		if (shape != 2) {
			return
		}
		if (rotation == 0) {
			this.removeFlag(545259520, adjustedX, adjustedZ)
			this.removeFlag(33554432, adjustedX - 1, adjustedZ)
			this.removeFlag(134217728, adjustedX, adjustedZ + 1)
		}
		if (rotation == 1) {
			this.removeFlag(41943040, adjustedX, adjustedZ)
			this.removeFlag(134217728, adjustedX, adjustedZ + 1)
			this.removeFlag(536870912, adjustedX + 1, adjustedZ)
		}
		if (rotation == 2) {
			this.removeFlag(167772160, adjustedX, adjustedZ)
			this.removeFlag(536870912, adjustedX + 1, adjustedZ)
			this.removeFlag(8388608, adjustedX, adjustedZ - 1)
		}
		if (rotation == 3) {
			this.removeFlag(671088640, adjustedX, adjustedZ)
			this.removeFlag(8388608, adjustedX, adjustedZ - 1)
			this.removeFlag(33554432, adjustedX - 1, adjustedZ)
			return
		}
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIB)V")
	fun flagBlocked(@OriginalArg(0) z: Int, @OriginalArg(1) x: Int) {
		@Pc(10) var x1: Int = z - this.zOffset
		@Pc(19) var z1: Int = x - this.xOffset
		this.collisionFlags!![z1]!![x1] = this.collisionFlags!![z1]!![x1] or CollisionFlag.BLOCKWALK
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IZZIIIII)V")
	fun unflagBlockedObject(@OriginalArg(0) rotation: Int, @OriginalArg(1) blocksAltRoute: Boolean, @OriginalArg(2) blocksProjectiles: Boolean, @OriginalArg(3) x: Int, @OriginalArg(4) z: Int, @OriginalArg(5) sizeZ: Int, @OriginalArg(7) sizeX: Int) {
		var sizeZ: Int = sizeZ
		var sizeX: Int = sizeX
		@Pc(11) var flag: Int = 256
		if (blocksProjectiles) {
			flag = 131328
		}
		@Pc(22) var adjustedX: Int = x - this.xOffset
		@Pc(27) var adjustedZ: Int = z - this.zOffset
		if (blocksAltRoute) {
			flag = flag or 0x40000000
		}
		@Pc(41) var local41: Int
		if (rotation == 1 || rotation == 3) {
			var swappedSize: Int = sizeX
			sizeX = sizeZ
			sizeZ = swappedSize
		}
		var tileX: Int = adjustedX
		while (tileX < sizeX + adjustedX) {
			if (tileX >= 0 && tileX < this.sizeX) {
				@Pc(60) var i: Int = adjustedZ
				while (i < adjustedZ + sizeZ) {
					if (i >= 0 && this.sizeY > i) {
						this.removeFlag(flag, tileX, i)
					}
					i++
				}
			}
			tileX++
		}
	}

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(IIIIIZZ)V")
	fun flagBoundary(@OriginalArg(0) z: Int, @OriginalArg(1) x: Int, @OriginalArg(3) shape: Int, @OriginalArg(4) rotation: Int, @OriginalArg(5) blocksAltRoute: Boolean, @OriginalArg(6) blocksProjectiles: Boolean) {
		@Pc(16) var adjustedZ: Int = z - this.zOffset
		@Pc(21) var adjustedX: Int = x - this.xOffset
		if (shape == 0) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 128, adjustedZ)
				this.addFlag(adjustedX - 1, 8, adjustedZ)
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 2, adjustedZ)
				this.addFlag(adjustedX, 32, adjustedZ + 1)
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 8, adjustedZ)
				this.addFlag(adjustedX + 1, 128, adjustedZ)
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 32, adjustedZ)
				this.addFlag(adjustedX, 2, adjustedZ - 1)
			}
		}
		if (shape == 1 || shape == 3) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 1, adjustedZ)
				this.addFlag(adjustedX - 1, 16, adjustedZ + 1)
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 4, adjustedZ)
				this.addFlag(adjustedX + 1, 64, adjustedZ + 1)
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 16, adjustedZ)
				this.addFlag(adjustedX + 1, 1, adjustedZ - 1)
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 64, adjustedZ)
				this.addFlag(adjustedX - 1, 4, adjustedZ - 1)
			}
		}
		if (shape == 2) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 130, adjustedZ)
				this.addFlag(adjustedX - 1, 8, adjustedZ)
				this.addFlag(adjustedX, 32, adjustedZ + 1)
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 10, adjustedZ)
				this.addFlag(adjustedX, 32, adjustedZ + 1)
				this.addFlag(adjustedX + 1, 128, adjustedZ)
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 40, adjustedZ)
				this.addFlag(adjustedX + 1, 128, adjustedZ)
				this.addFlag(adjustedX, 2, adjustedZ - 1)
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 160, adjustedZ)
				this.addFlag(adjustedX, 2, adjustedZ - 1)
				this.addFlag(adjustedX - 1, 8, adjustedZ)
			}
		}
		if (blocksProjectiles) {
			if (shape == 0) {
				if (rotation == 0) {
					this.addFlag(adjustedX, 65536, adjustedZ)
					this.addFlag(adjustedX - 1, 4096, adjustedZ)
				}
				if (rotation == 1) {
					this.addFlag(adjustedX, 1024, adjustedZ)
					this.addFlag(adjustedX, 16384, adjustedZ + 1)
				}
				if (rotation == 2) {
					this.addFlag(adjustedX, 4096, adjustedZ)
					this.addFlag(adjustedX + 1, 65536, adjustedZ)
				}
				if (rotation == 3) {
					this.addFlag(adjustedX, 16384, adjustedZ)
					this.addFlag(adjustedX, 1024, adjustedZ - 1)
				}
			}
			if (shape == 1 || shape == 3) {
				if (rotation == 0) {
					this.addFlag(adjustedX, 512, adjustedZ)
					this.addFlag(adjustedX - 1, 8192, adjustedZ + 1)
				}
				if (rotation == 1) {
					this.addFlag(adjustedX, 2048, adjustedZ)
					this.addFlag(adjustedX + 1, 32768, adjustedZ + 1)
				}
				if (rotation == 2) {
					this.addFlag(adjustedX, 8192, adjustedZ)
					this.addFlag(adjustedX + 1, 512, adjustedZ - 1)
				}
				if (rotation == 3) {
					this.addFlag(adjustedX, 32768, adjustedZ)
					this.addFlag(adjustedX - 1, 2048, adjustedZ - 1)
				}
			}
			if (shape == 2) {
				if (rotation == 0) {
					this.addFlag(adjustedX, 66560, adjustedZ)
					this.addFlag(adjustedX - 1, 4096, adjustedZ)
					this.addFlag(adjustedX, 16384, adjustedZ + 1)
				}
				if (rotation == 1) {
					this.addFlag(adjustedX, 5120, adjustedZ)
					this.addFlag(adjustedX, 16384, adjustedZ + 1)
					this.addFlag(adjustedX + 1, 65536, adjustedZ)
				}
				if (rotation == 2) {
					this.addFlag(adjustedX, 20480, adjustedZ)
					this.addFlag(adjustedX + 1, 65536, adjustedZ)
					this.addFlag(adjustedX, 1024, adjustedZ - 1)
				}
				if (rotation == 3) {
					this.addFlag(adjustedX, 81920, adjustedZ)
					this.addFlag(adjustedX, 1024, adjustedZ - 1)
					this.addFlag(adjustedX - 1, 4096, adjustedZ)
				}
			}
		}
		if (!blocksAltRoute) {
			return
		}
		if (shape == 0) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 536870912, adjustedZ)
				this.addFlag(adjustedX - 1, 33554432, adjustedZ)
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 8388608, adjustedZ)
				this.addFlag(adjustedX, 134217728, adjustedZ + 1)
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 33554432, adjustedZ)
				this.addFlag(adjustedX + 1, 536870912, adjustedZ)
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 134217728, adjustedZ)
				this.addFlag(adjustedX, 8388608, adjustedZ - 1)
			}
		}
		if (shape == 1 || shape == 3) {
			if (rotation == 0) {
				this.addFlag(adjustedX, 4194304, adjustedZ)
				this.addFlag(adjustedX - 1, 67108864, adjustedZ + 1)
			}
			if (rotation == 1) {
				this.addFlag(adjustedX, 16777216, adjustedZ)
				this.addFlag(adjustedX + 1, 268435456, adjustedZ + 1)
			}
			if (rotation == 2) {
				this.addFlag(adjustedX, 67108864, adjustedZ)
				this.addFlag(adjustedX + 1, 4194304, adjustedZ - 1)
			}
			if (rotation == 3) {
				this.addFlag(adjustedX, 268435456, adjustedZ)
				this.addFlag(adjustedX - 1, 16777216, adjustedZ - 1)
			}
		}
		if (shape != 2) {
			return
		}
		if (rotation == 0) {
			this.addFlag(adjustedX, 545259520, adjustedZ)
			this.addFlag(adjustedX - 1, 33554432, adjustedZ)
			this.addFlag(adjustedX, 134217728, adjustedZ + 1)
		}
		if (rotation == 1) {
			this.addFlag(adjustedX, 41943040, adjustedZ)
			this.addFlag(adjustedX, 134217728, adjustedZ + 1)
			this.addFlag(adjustedX + 1, 536870912, adjustedZ)
		}
		if (rotation == 2) {
			this.addFlag(adjustedX, 167772160, adjustedZ)
			this.addFlag(adjustedX + 1, 536870912, adjustedZ)
			this.addFlag(adjustedX, 8388608, adjustedZ - 1)
		}
		if (rotation == 3) {
			this.addFlag(adjustedX, 671088640, adjustedZ)
			this.addFlag(adjustedX, 8388608, adjustedZ - 1)
			this.addFlag(adjustedX - 1, 33554432, adjustedZ)
			return
		}
	}
	companion object {
		@OriginalMember(owner = "client!jd", name = "b", descriptor = "(III)Lclient!fe;")
		@JvmStatic
		fun create(@OriginalArg(0) sizeY: Int, @OriginalArg(2) sizeX: Int): CollisionMap {
			@Pc(7) val collisionMap: CollisionMap = CollisionMap()
			collisionMap.sizeX = sizeX + 6
			collisionMap.xOffset = -1
			collisionMap.sizeY = sizeY + 1 + 5
			collisionMap.zOffset = -1
			collisionMap.collisionFlags = Array<IntArray?>(collisionMap.sizeX) { IntArray(collisionMap.sizeY) }
			collisionMap.reset()
			return collisionMap
		}
	}
}
