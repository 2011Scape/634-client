package com.jagex.game.runetek5

import com.jagex.game.runetek5.cache.node.lru.PulseEvent
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.collision.DirectionFlag
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.core.constants.LocShapes
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.scene.CollisionMap
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object PathFinder {

	@OriginalMember(owner = "client!qj", name = "a", descriptor = "(IIIIIIIIILclient!fe;II)Z")
	@JvmStatic
	fun performCalculationSX(@OriginalArg(0) destSizeX: Int, @OriginalArg(1) rotation: Int, @OriginalArg(3) srcZ: Int, @OriginalArg(4) destSizeZ: Int, @OriginalArg(5) destX: Int, @OriginalArg(6) srcX: Int, @OriginalArg(7) accessBlockFlag: Int, @OriginalArg(8) entitySize: Int, @OriginalArg(9) collisionMap: CollisionMap?, @OriginalArg(10) shape: Int, @OriginalArg(11) destZ: Int): Boolean {
		@Pc(7) var currentX: Int = srcX
		@Pc(9) var currentZ: Int = srcZ
		@Pc(17) val graphBaseX: Int = srcX - 64
		PathFinder.directions[64][64] = 99
		@Pc(45) val graphBaseZ: Int = srcZ - 64
		PathFinder.distances[64][64] = 0
		@Pc(53) val startIndex: Byte = 0
		PathFinder.queueX[0] = srcX
		@Pc(59) var read: Int = 0
		@Pc(62) var write: Int = startIndex + 1
		PathFinder.queueZ[0] = srcZ
		@Pc(67) val collisionFlags: Array<IntArray?>? = collisionMap!!.collisionFlags
		while (true) {
			label282@ while (true) {
				@Pc(82) var graphX: Int
				@Pc(87) var graphZ: Int
				@Pc(98) var clipX: Int
				@Pc(103) var clipZ: Int
				@Pc(246) var nextDistance: Int
				@Pc(282) var step: Int
				do {
					do {
						do {
							label259@ do {
								if (read == write) {
									PathFinder.routeExitZ = currentZ
									PathFinder.routeExitX = currentX
									return false
								}
								currentZ = PathFinder.queueZ[read]
								currentX = PathFinder.queueX[read]
								graphX = currentX - graphBaseX
								graphZ = currentZ - graphBaseZ
								read = (read + 1) and 0xFFF
								clipX = currentX - collisionMap.xOffset
								clipZ = currentZ - collisionMap.zOffset
								if (shape == -4) {
									if (currentX == destX && destZ == currentZ) {
										PathFinder.routeExitX = currentX
										PathFinder.routeExitZ = currentZ
										return true
									}
								} else if (shape == -3) {
									if (PathFinder.rectanglesOverlap(destSizeX, destZ, entitySize, currentX, currentZ, destX, entitySize, destSizeZ)) {
										PathFinder.routeExitZ = currentZ
										PathFinder.routeExitX = currentX
										return true
									}
								} else if (shape == -2) {
									if (collisionMap.isOutsideRect(destX, accessBlockFlag, entitySize, destSizeZ, currentX, entitySize, currentZ, destZ, destSizeX)) {
										PathFinder.routeExitX = currentX
										PathFinder.routeExitZ = currentZ
										return true
									}
								} else if (shape == -1) {
									if (collisionMap.isInsideOrOutsideRect(currentX, destZ, entitySize, destSizeZ, destSizeX, accessBlockFlag, currentZ, destX)) {
										PathFinder.routeExitX = currentX
										PathFinder.routeExitZ = currentZ
										return true
									}
								} else if (shape == LocShapes.WALL_STRAIGHT || shape == LocShapes.WALL_DIAGONALCORNER || shape == LocShapes.WALL_L || shape == LocShapes.WALL_SQUARECORNER || shape == LocShapes.WALL_DIAGONAL) {
									if (collisionMap.canReachWall(destX, currentZ, entitySize, rotation, currentX, destZ, shape)) {
										PathFinder.routeExitX = currentX
										PathFinder.routeExitZ = currentZ
										return true
									}
								} else if (collisionMap.canReachWallDecoration(destZ, currentZ, destX, currentX, shape, rotation, entitySize)) {
									PathFinder.routeExitZ = currentZ
									PathFinder.routeExitX = currentX
									return true
								}
								nextDistance = PathFinder.distances[graphX][graphZ] + 1
								if (graphX > 0 && PathFinder.directions[graphX - 1][graphZ] == 0 && (collisionFlags!![clipX - 1]!![clipZ] and 0x43A40000) == 0 && (collisionFlags[clipX - 1]!![entitySize + clipZ - 1] and 0x4E240000) == 0) {
									step = 1
									while (true) {
										if (entitySize - 1 <= step) {
											PathFinder.queueX[write] = currentX - 1
											PathFinder.queueZ[write] = currentZ
											write = (write + 1) and 0xFFF
											PathFinder.directions[graphX - 1][graphZ] = 2
											PathFinder.distances[graphX - 1][graphZ] = nextDistance
											break
										}
										if ((collisionFlags[clipX - 1]!![step + clipZ] and 0x4FA40000) != 0) {
											break
										}
										step++
									}
								}
								if (graphX < 128 - entitySize && PathFinder.directions[graphX + 1][graphZ] == 0 && (collisionFlags!![clipX + entitySize]!![clipZ] and 0x60E40000) == 0 && (collisionFlags[entitySize + clipX]!![clipZ + entitySize - 1] and 0x78240000) == 0) {
									step = 1
									while (true) {
										if (entitySize - 1 <= step) {
											PathFinder.queueX[write] = currentX + 1
											PathFinder.queueZ[write] = currentZ
											PathFinder.directions[graphX + 1][graphZ] = 8
											write = (write + 1) and 0xFFF
											PathFinder.distances[graphX + 1][graphZ] = nextDistance
											break
										}
										if ((collisionFlags[entitySize + clipX]!![clipZ + step] and 0x78E40000) != 0) {
											break
										}
										step++
									}
								}
								if (graphZ > 0 && PathFinder.directions[graphX][graphZ - 1] == 0 && (collisionFlags!![clipX]!![clipZ - 1] and 0x43A40000) == 0 && (collisionFlags[clipX + entitySize - 1]!![clipZ - 1] and 0x60E40000) == 0) {
									step = 1
									while (true) {
										if (step >= entitySize - 1) {
											PathFinder.queueX[write] = currentX
											PathFinder.queueZ[write] = currentZ - 1
											PathFinder.directions[graphX][graphZ - 1] = 1
											write = (write + 1) and 0xFFF
											PathFinder.distances[graphX][graphZ - 1] = nextDistance
											break
										}
										if ((collisionFlags[clipX + step]!![clipZ - 1] and 0x63E40000) != 0) {
											break
										}
										step++
									}
								}
								if (graphZ < 128 - entitySize && PathFinder.directions[graphX][graphZ + 1] == 0 && (collisionFlags!![clipX]!![clipZ + entitySize] and 0x4E240000) == 0 && (collisionFlags[entitySize + clipX - 1]!![clipZ + entitySize] and 0x78240000) == 0) {
									step = 1
									while (true) {
										if (entitySize - 1 <= step) {
											PathFinder.queueX[write] = currentX
											PathFinder.queueZ[write] = currentZ + 1
											PathFinder.directions[graphX][graphZ + 1] = 4
											write = (write + 1) and 0xFFF
											PathFinder.distances[graphX][graphZ + 1] = nextDistance
											break
										}
										if ((collisionFlags[clipX + step]!![entitySize + clipZ] and 0x7E240000) != 0) {
											break
										}
										step++
									}
								}
								if (graphX > 0 && graphZ > 0 && PathFinder.directions[graphX - 1][graphZ - 1] == 0 && (collisionFlags!![clipX - 1]!![clipZ - 1] and 0x43A40000) == 0) {
									step = 1
									while (true) {
										if (entitySize <= step) {
											PathFinder.queueX[write] = currentX - 1
											PathFinder.queueZ[write] = currentZ - 1
											write = (write + 1) and 0xFFF
											PathFinder.directions[graphX - 1][graphZ - 1] = 3
											PathFinder.distances[graphX - 1][graphZ - 1] = nextDistance
											break
										}
										if ((collisionFlags[clipX - 1]!![clipZ + step - 1] and 0x4FA40000) != 0 || (collisionFlags[clipX + step - 1]!![clipZ - 1] and 0x63E40000) != 0) {
											break
										}
										step++
									}
								}
								if (graphX < 128 - entitySize && graphZ > 0 && PathFinder.directions[graphX + 1][graphZ - 1] == 0 && (collisionFlags!![clipX + entitySize]!![clipZ - 1] and 0x60E40000) == 0) {
									step = 1
									while (true) {
										if (entitySize <= step) {
											PathFinder.queueX[write] = currentX + 1
											PathFinder.queueZ[write] = currentZ - 1
											PathFinder.directions[graphX + 1][graphZ - 1] = 9
											write = (write + 1) and 0xFFF
											PathFinder.distances[graphX + 1][graphZ - 1] = nextDistance
											break
										}
										if ((collisionFlags[clipX + entitySize]!![step + clipZ - 1] and 0x78E40000) != 0 || (collisionFlags[clipX + step]!![clipZ - 1] and 0x63E40000) != 0) {
											break
										}
										step++
									}
								}
								if (graphX > 0 && graphZ < 128 - entitySize && PathFinder.directions[graphX - 1][graphZ + 1] == 0 && (collisionFlags!![clipX - 1]!![entitySize + clipZ] and 0x4E240000) == 0) {
									step = 1
									while (step < entitySize) {
										if ((collisionFlags[clipX - 1]!![step + clipZ] and 0x4FA40000) != 0 || (collisionFlags[step + clipX - 1]!![clipZ + entitySize] and 0x7E240000) != 0) {
											continue@label259
										}
										step++
									}
									PathFinder.queueX[write] = currentX - 1
									PathFinder.queueZ[write] = currentZ + 1
									PathFinder.directions[graphX - 1][graphZ + 1] = 6
									write = (write + 1) and 0xFFF
									PathFinder.distances[graphX - 1][graphZ + 1] = nextDistance
								}
							} while (128 - entitySize <= graphX)
						} while (graphZ >= 128 - entitySize)
					} while (PathFinder.directions[graphX + 1][graphZ + 1] != 0)
				} while ((collisionFlags!![entitySize + clipX]!![clipZ + entitySize] and 0x78240000) != 0)
				step = 1
				while (step < entitySize) {
					if ((collisionFlags!![clipX + step]!![clipZ + entitySize] and 0x7E240000) != 0 || (collisionFlags[entitySize + clipX]!![clipZ + step] and 0x78E40000) != 0) {
						continue@label282
					}
					step++
				}
				PathFinder.queueX[write] = currentX + 1
				PathFinder.queueZ[write] = currentZ + 1
				write = (write + 1) and 0xFFF
				PathFinder.directions[graphX + 1][graphZ + 1] = 12
				PathFinder.distances[graphX + 1][graphZ + 1] = nextDistance
			}
		}
	}

	@OriginalMember(owner = "client!wd", name = "a", descriptor = "(III[IZBIII[IILclient!fe;III)I")
	@JvmStatic
	fun findRoute(@OriginalArg(0) destX: Int, @OriginalArg(1) entitySize: Int, @OriginalArg(2) destSizeZ: Int, @OriginalArg(3) stepsZ: IntArray?, @OriginalArg(4) findAlternative: Boolean, @OriginalArg(6) shape: Int, @OriginalArg(7) srcX: Int, @OriginalArg(8) srcZ: Int, @OriginalArg(9) stepsX: IntArray?, @OriginalArg(10) destZ: Int, @OriginalArg(11) collisionMap: CollisionMap?, @OriginalArg(12) destSizeX: Int, @OriginalArg(13) accessBlockFlag: Int, @OriginalArg(14) rotation: Int): Int {
		@Pc(3) var i: Int = 0
		while (i < 128) {
			@Pc(7) var j: Int = 0
			while (j < 128) {
				PathFinder.directions[i][j] = 0
				PathFinder.distances[i][j] = 99999999
				j++
			}
			i++
		}
		@Pc(58) val canReach: Boolean
		if (entitySize == 1) {
			canReach = PathFinder.performCalculationS1(destX, destZ, srcZ, rotation, accessBlockFlag, destSizeX, collisionMap, shape, destSizeZ, srcX)
		} else if (entitySize == 2) {
			canReach = PathFinder.performCalculationS2(shape, destZ, collisionMap, destX, srcZ, destSizeX, destSizeZ, accessBlockFlag, srcX, rotation)
		} else {
			canReach = PathFinder.performCalculationSX(destSizeX, rotation, srcZ, destSizeZ, destX, srcX, accessBlockFlag, entitySize, collisionMap, shape, destZ)
		}
		@Pc(93) val graphBaseX: Int = srcX - 64
		@Pc(97) val graphBaseZ: Int = srcZ - 64
		@Pc(99) var exitX: Int = PathFinder.routeExitX
		@Pc(101) var exitZ: Int = PathFinder.routeExitZ
		@Pc(109) var local109: Int
		@Pc(111) var local111: Int
		@Pc(118) var local118: Int
		if (!canReach) {
			if (!findAlternative) {
				return -1
			}
			var bestDistanceSquared: Int = Int.MAX_VALUE
			var bestCost: Int = Int.MAX_VALUE
			var candidateX: Int = destX - 10
			while (candidateX <= destX + 10) {
				@Pc(124) var k: Int = destZ - 10
				while (k <= destZ + 10) {
					@Pc(130) val graphX: Int = candidateX - graphBaseX
					@Pc(135) val graphZ: Int = k - graphBaseZ
					if (graphX >= 0 && graphZ >= 0 && graphX < 128 && graphZ < 128 && PathFinder.distances[graphX][graphZ] < 100) {
						@Pc(157) var dx: Int = 0
						if (candidateX < destX) {
							dx = destX - candidateX
						} else if (destSizeX + destX - 1 < candidateX) {
							dx = candidateX + 1 - destX - destSizeX
						}
						@Pc(188) var dz: Int = 0
						if (k < destZ) {
							dz = destZ - k
						} else if (k > destSizeZ + destZ - 1) {
							dz = k + 1 - destSizeZ - destZ
						}
						@Pc(226) val distanceSquared: Int = dx * dx + dz * dz
						if (bestDistanceSquared > distanceSquared || bestDistanceSquared == distanceSquared && bestCost > PathFinder.distances[graphX][graphZ]) {
							bestDistanceSquared = distanceSquared
							exitX = candidateX
							bestCost = PathFinder.distances[graphX][graphZ]
							exitZ = k
						}
					}
					k++
				}
				candidateX++
			}
			if (bestDistanceSquared == Int.MAX_VALUE) {
				return -1
			}
		}
		if (exitX == srcX && srcZ == exitZ) {
			return 0
		}
		@Pc(300) val startIndex: Byte = 0
		PathFinder.queueX[0] = exitX
		var queueSize: Int = startIndex + 1
		PathFinder.queueZ[0] = exitZ
		@Pc(327) var lastDirection: Int
		lastDirection = PathFinder.directions[exitX - graphBaseX][exitZ - graphBaseZ]
		var direction: Int = lastDirection
		while (srcX != exitX || exitZ != srcZ) {
			if (direction != lastDirection) {
				PathFinder.queueX[queueSize] = exitX
				lastDirection = direction
				PathFinder.queueZ[queueSize++] = exitZ
			}
			if ((direction and DirectionFlag.EAST) != 0) {
				exitX++
			} else if ((direction and DirectionFlag.WEST) != 0) {
				exitX--
			}
			if ((direction and DirectionFlag.NORTH) != 0) {
				exitZ++
			} else if ((direction and DirectionFlag.SOUTH) != 0) {
				exitZ--
			}
			direction = PathFinder.directions[exitX - graphBaseX][exitZ - graphBaseZ]
		}
		var stepCount: Int = 0
		while (queueSize-- > 0) {
			stepsX!![stepCount] = PathFinder.queueX[queueSize]
			stepsZ!![stepCount++] = PathFinder.queueZ[queueSize]
			if (stepsX.size <= stepCount) {
				break
			}
		}
		return stepCount
	}

	@OriginalMember(owner = "client!fs", name = "a", descriptor = "(IIIIIIIII)Z")
	@JvmStatic
	fun rectanglesOverlap(@OriginalArg(0) destSizeX: Int, @OriginalArg(1) destZ: Int, @OriginalArg(3) srcSizeZ: Int, @OriginalArg(4) srcX: Int, @OriginalArg(5) srcZ: Int, @OriginalArg(6) destX: Int, @OriginalArg(7) srcSizeX: Int, @OriginalArg(8) destSizeZ: Int): Boolean {
		if (destSizeX + destX > srcX && destX < srcX + srcSizeX) {
			return srcZ < destZ + destSizeZ && srcSizeZ + srcZ > destZ
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!ad", name = "h", descriptor = "[[I")
	@JvmField
	val distances: Array<IntArray> = Array(128) { IntArray(128) }

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(IIILclient!fe;IIIIIII)Z")
	@JvmStatic
	fun performCalculationS2(@OriginalArg(0) shape: Int, @OriginalArg(2) destZ: Int, @OriginalArg(3) collisionMap: CollisionMap?, @OriginalArg(4) destX: Int, @OriginalArg(5) srcZ: Int, @OriginalArg(6) destSizeX: Int, @OriginalArg(7) destSizeZ: Int, @OriginalArg(8) accessBlockFlag: Int, @OriginalArg(9) srcX: Int, @OriginalArg(10) rotation: Int): Boolean {
		@Pc(7) var currentX: Int = srcX
		@Pc(9) var currentZ: Int = srcZ
		@Pc(18) val graphBaseX: Int = srcX - 64
		@Pc(23) val graphBaseZ: Int = srcZ - 64
		PathFinder.directions[64][64] = 99
		PathFinder.distances[64][64] = 0
		@Pc(42) val startIndex: Byte = 0
		PathFinder.queueX[0] = srcX
		@Pc(48) var read: Int = 0
		@Pc(51) var write: Int = startIndex + 1
		PathFinder.queueZ[0] = srcZ
		@Pc(56) val collisionFlags: Array<IntArray?>? = collisionMap!!.collisionFlags
		while (write != read) {
			currentZ = PathFinder.queueZ[read]
			currentX = PathFinder.queueX[read]
			read = (read + 1) and 0xFFF
			@Pc(76) val graphX: Int = currentX - graphBaseX
			@Pc(81) val graphZ: Int = currentZ - graphBaseZ
			@Pc(87) val clipX: Int = currentX - collisionMap.xOffset
			@Pc(93) val clipZ: Int = currentZ - collisionMap.zOffset
			if (shape == -4) {
				if (destX == currentX && destZ == currentZ) {
					PathFinder.routeExitX = currentX
					PathFinder.routeExitZ = currentZ
					return true
				}
			} else if (shape == -3) {
				if (PathFinder.rectanglesOverlap(destSizeX, destZ, 2, currentX, currentZ, destX, 2, destSizeZ)) {
					PathFinder.routeExitZ = currentZ
					PathFinder.routeExitX = currentX
					return true
				}
			} else if (shape == -2) {
				if (collisionMap.isOutsideRect(destX, accessBlockFlag, 2, destSizeZ, currentX, 2, currentZ, destZ, destSizeX)) {
					PathFinder.routeExitX = currentX
					PathFinder.routeExitZ = currentZ
					return true
				}
			} else if (shape == -1) {
				if (collisionMap.isInsideOrOutsideRect(currentX, destZ, 2, destSizeZ, destSizeX, accessBlockFlag, currentZ, destX)) {
					PathFinder.routeExitX = currentX
					PathFinder.routeExitZ = currentZ
					return true
				}
			} else if (shape == 0 || shape == 1 || shape == 2 || shape == 3 || shape == 9) {
				if (collisionMap.canReachWall(destX, currentZ, 2, rotation, currentX, destZ, shape)) {
					PathFinder.routeExitX = currentX
					PathFinder.routeExitZ = currentZ
					return true
				}
			} else if (collisionMap.canReachWallDecoration(destZ, currentZ, destX, currentX, shape, rotation, 2)) {
				PathFinder.routeExitX = currentX
				PathFinder.routeExitZ = currentZ
				return true
			}
			@Pc(246) val nextDistance: Int = PathFinder.distances[graphX][graphZ] + 1
			if (graphX > 0 && PathFinder.directions[graphX - 1][graphZ] == 0 && (collisionFlags!![clipX - 1]!![clipZ] and 0x43A40000) == 0 && (collisionFlags[clipX - 1]!![clipZ + 1] and 0x4E240000) == 0) {
				PathFinder.queueX[write] = currentX - 1
				PathFinder.queueZ[write] = currentZ
				PathFinder.directions[graphX - 1][graphZ] = 2
				write = (write + 1) and 0xFFF
				PathFinder.distances[graphX - 1][graphZ] = nextDistance
			}
			if (graphX < 126 && PathFinder.directions[graphX + 1][graphZ] == 0 && (collisionFlags!![clipX + 2]!![clipZ] and 0x60E40000) == 0 && (collisionFlags[clipX + 2]!![clipZ + 1] and 0x78240000) == 0) {
				PathFinder.queueX[write] = currentX + 1
				PathFinder.queueZ[write] = currentZ
				PathFinder.directions[graphX + 1][graphZ] = 8
				write = (write + 1) and 0xFFF
				PathFinder.distances[graphX + 1][graphZ] = nextDistance
			}
			if (graphZ > 0 && PathFinder.directions[graphX][graphZ - 1] == 0 && (collisionFlags!![clipX]!![clipZ - 1] and 0x43A40000) == 0 && (collisionFlags[clipX + 1]!![clipZ - 1] and 0x60E40000) == 0) {
				PathFinder.queueX[write] = currentX
				PathFinder.queueZ[write] = currentZ - 1
				PathFinder.directions[graphX][graphZ - 1] = 1
				write = (write + 1) and 0xFFF
				PathFinder.distances[graphX][graphZ - 1] = nextDistance
			}
			if (graphZ < 126 && PathFinder.directions[graphX][graphZ + 1] == 0 && (collisionFlags!![clipX]!![clipZ + 2] and 0x4E240000) == 0 && (collisionFlags[clipX + 1]!![clipZ + 2] and 0x78240000) == 0) {
				PathFinder.queueX[write] = currentX
				PathFinder.queueZ[write] = currentZ + 1
				write = (write + 1) and 0xFFF
				PathFinder.directions[graphX][graphZ + 1] = 4
				PathFinder.distances[graphX][graphZ + 1] = nextDistance
			}
			if (graphX > 0 && graphZ > 0 && PathFinder.directions[graphX - 1][graphZ - 1] == 0 && (collisionFlags!![clipX - 1]!![clipZ] and 0x4FA40000) == 0 && (collisionFlags[clipX - 1]!![clipZ - 1] and 0x43A40000) == 0 && (collisionFlags[clipX]!![clipZ - 1] and 0x63E40000) == 0) {
				PathFinder.queueX[write] = currentX - 1
				PathFinder.queueZ[write] = currentZ - 1
				write = (write + 1) and 0xFFF
				PathFinder.directions[graphX - 1][graphZ - 1] = 3
				PathFinder.distances[graphX - 1][graphZ - 1] = nextDistance
			}
			if (graphX < 126 && graphZ > 0 && PathFinder.directions[graphX + 1][graphZ - 1] == 0 && (collisionFlags!![clipX + 1]!![clipZ - 1] and 0x63E40000) == 0 && (collisionFlags[clipX + 2]!![clipZ - 1] and 0x60E40000) == 0 && (collisionFlags[clipX + 2]!![clipZ] and 0x78E40000) == 0) {
				PathFinder.queueX[write] = currentX + 1
				PathFinder.queueZ[write] = currentZ - 1
				PathFinder.directions[graphX + 1][graphZ - 1] = 9
				write = (write + 1) and 0xFFF
				PathFinder.distances[graphX + 1][graphZ - 1] = nextDistance
			}
			if (graphX > 0 && graphZ < 126 && PathFinder.directions[graphX - 1][graphZ + 1] == 0 && (collisionFlags!![clipX - 1]!![clipZ + 1] and 0x4FA40000) == 0 && (collisionFlags[clipX - 1]!![clipZ + 2] and 0x4E240000) == 0 && (collisionFlags[clipX]!![clipZ + 2] and 0x7E240000) == 0) {
				PathFinder.queueX[write] = currentX - 1
				PathFinder.queueZ[write] = currentZ + 1
				PathFinder.directions[graphX - 1][graphZ + 1] = 6
				write = (write + 1) and 0xFFF
				PathFinder.distances[graphX - 1][graphZ + 1] = nextDistance
			}
			if (graphX < 126 && graphZ < 126 && PathFinder.directions[graphX + 1][graphZ + 1] == 0 && (collisionFlags!![clipX + 1]!![clipZ + 2] and 0x7E240000) == 0 && (collisionFlags[clipX + 2]!![clipZ + 2] and 0x78240000) == 0 && (collisionFlags[clipX + 2]!![clipZ + 1] and 0x78E40000) == 0) {
				PathFinder.queueX[write] = currentX + 1
				PathFinder.queueZ[write] = currentZ + 1
				write = (write + 1) and 0xFFF
				PathFinder.directions[graphX + 1][graphZ + 1] = 12
				PathFinder.distances[graphX + 1][graphZ + 1] = nextDistance
			}
		}
		PathFinder.routeExitX = currentX
		PathFinder.routeExitZ = currentZ
		return false
	}

	@OriginalMember(owner = "client!or", name = "p", descriptor = "[I")
	@JvmField
	val queueX: IntArray = IntArray(4096)

	@OriginalMember(owner = "client!rfa", name = "j", descriptor = "[I")
	@JvmField
	val queueZ: IntArray = IntArray(4096)

	@OriginalMember(owner = "client!mv", name = "c", descriptor = "I")
	@JvmField
	var routeExitZ: Int = 0

	@OriginalMember(owner = "client!dfa", name = "A", descriptor = "[I")
	@JvmField
	val WALKING_STEPS_X: IntArray = IntArray(50)

	@OriginalMember(owner = "client!wf", name = "a", descriptor = "(ZIIBIIIII)Z")
	@JvmStatic
	fun calculateRoute(@OriginalArg(0) findAlternative: Boolean, @OriginalArg(1) destX: Int, @OriginalArg(2) destZ: Int, @OriginalArg(4) destSizeZ: Int, @OriginalArg(5) accessBlockFlag: Int, @OriginalArg(6) destSizeX: Int, @OriginalArg(7) shape: Int, @OriginalArg(8) rotation: Int): Boolean {
		@Pc(8) val srcX: Int = PlayerUpdate.localPlayer!!.movementBaseX[0]
		@Pc(13) val srcZ: Int = PlayerUpdate.localPlayer!!.movementBaseY[0]
		if (srcX < 0 || MapRegion.sceneSizeX <= srcX || srcZ < 0 || srcZ >= MapRegion.sceneSizeY) {
			return false
		} else if (destX >= 0 && destX < MapRegion.sceneSizeX && destZ >= 0 && destZ < MapRegion.sceneSizeY) {
			@Pc(80) val stepCount: Int = PathFinder.findRoute(destX, PlayerUpdate.localPlayer!!.getSize(), destSizeZ, Camera.WALKING_STEPS_Y, findAlternative, shape, srcX, srcZ, PathFinder.WALKING_STEPS_X, destZ, MapRegion.aClass104Array3[PlayerUpdate.localPlayer!!.level.toInt()], destSizeX, accessBlockFlag, rotation)
			if (stepCount < 1) {
				return false
			}
			Minimap.minimapFlagX = PathFinder.WALKING_STEPS_X[stepCount - 1]
			Minimap.minimapFlagY = Camera.WALKING_STEPS_Y[stepCount - 1]
			Minimap.minimapContainsFlag = false
			PulseEvent.delayedMinimapFlagUpdate()
			return true
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IIIIIILclient!fe;IIII)Z")
	@JvmStatic
	fun performCalculationS1(@OriginalArg(0) destX: Int, @OriginalArg(1) destZ: Int, @OriginalArg(2) srcZ: Int, @OriginalArg(3) rotation: Int, @OriginalArg(4) accessBlockFlag: Int, @OriginalArg(5) destSizeX: Int, @OriginalArg(6) collisionMap: CollisionMap?, @OriginalArg(7) shape: Int, @OriginalArg(8) destSizeZ: Int, @OriginalArg(9) srcX: Int): Boolean {
		@Pc(7) var currentX: Int = srcX
		@Pc(9) var currentZ: Int = srcZ
		@Pc(18) val graphBaseX: Int = srcX - 64
		PathFinder.directions[64][64] = 99
		@Pc(29) val graphBaseZ: Int = srcZ - 64
		PathFinder.distances[64][64] = 0
		@Pc(37) val seedIndex: Byte = 0
		@Pc(39) var readIndex: Int = 0
		PathFinder.queueX[0] = srcX
		@Pc(46) var writeIndex: Int = seedIndex + 1
		PathFinder.queueZ[0] = srcZ
		@Pc(51) val collisionFlags: Array<IntArray?>? = collisionMap!!.collisionFlags
		while (writeIndex != readIndex) {
			currentZ = PathFinder.queueZ[readIndex]
			currentX = PathFinder.queueX[readIndex]
			@Pc(67) val localZ: Int = currentZ - collisionMap.zOffset
			@Pc(72) val graphX: Int = currentX - graphBaseX
			@Pc(76) val graphZ: Int = currentZ - graphBaseZ
			@Pc(82) val localX: Int = currentX - collisionMap.xOffset
			readIndex = (readIndex + 1) and 0xFFF
			if (shape == -4) {
				if (currentX == destX && destZ == currentZ) {
					PathFinder.routeExitX = currentX
					PathFinder.routeExitZ = currentZ
					return true
				}
			} else if (shape == -3) {
				if (PathFinder.rectanglesOverlap(destSizeX, destZ, 1, currentX, currentZ, destX, 1, destSizeZ)) {
					PathFinder.routeExitZ = currentZ
					PathFinder.routeExitX = currentX
					return true
				}
			} else if (shape == -2) {
				if (collisionMap.isOutsideRect(destX, accessBlockFlag, 1, destSizeZ, currentX, 1, currentZ, destZ, destSizeX)) {
					PathFinder.routeExitZ = currentZ
					PathFinder.routeExitX = currentX
					return true
				}
			} else if (shape == -1) {
				if (collisionMap.isInsideOrOutsideRect(currentX, destZ, 1, destSizeZ, destSizeX, accessBlockFlag, currentZ, destX)) {
					PathFinder.routeExitX = currentX
					PathFinder.routeExitZ = currentZ
					return true
				}
			} else if (shape == 0 || shape == 1 || shape == 2 || shape == 3 || shape == 9) {
				if (collisionMap.canReachWall(destX, currentZ, 1, rotation, currentX, destZ, shape)) {
					PathFinder.routeExitZ = currentZ
					PathFinder.routeExitX = currentX
					return true
				}
			} else if (collisionMap.canReachWallDecoration(destZ, currentZ, destX, currentX, shape, rotation, 1)) {
				PathFinder.routeExitX = currentX
				PathFinder.routeExitZ = currentZ
				return true
			}
			@Pc(238) val nextDistance: Int = PathFinder.distances[graphX][graphZ] + 1
			if (graphX > 0 && PathFinder.directions[graphX - 1][graphZ] == 0 && (collisionFlags!![localX - 1]!![localZ] and 0x42240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX - 1
				PathFinder.queueZ[writeIndex] = currentZ
				writeIndex = (writeIndex + 1) and 0xFFF
				PathFinder.directions[graphX - 1][graphZ] = 2
				PathFinder.distances[graphX - 1][graphZ] = nextDistance
			}
			if (graphX < 127 && PathFinder.directions[graphX + 1][graphZ] == 0 && (collisionFlags!![localX + 1]!![localZ] and 0x60240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX + 1
				PathFinder.queueZ[writeIndex] = currentZ
				PathFinder.directions[graphX + 1][graphZ] = 8
				writeIndex = (writeIndex + 1) and 0xFFF
				PathFinder.distances[graphX + 1][graphZ] = nextDistance
			}
			if (graphZ > 0 && PathFinder.directions[graphX][graphZ - 1] == 0 && (collisionFlags!![localX]!![localZ - 1] and 0x40A40000) == 0) {
				PathFinder.queueX[writeIndex] = currentX
				PathFinder.queueZ[writeIndex] = currentZ - 1
				PathFinder.directions[graphX][graphZ - 1] = 1
				writeIndex = (writeIndex + 1) and 0xFFF
				PathFinder.distances[graphX][graphZ - 1] = nextDistance
			}
			if (graphZ < 127 && PathFinder.directions[graphX][graphZ + 1] == 0 && (collisionFlags!![localX]!![localZ + 1] and 0x48240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX
				PathFinder.queueZ[writeIndex] = currentZ + 1
				PathFinder.directions[graphX][graphZ + 1] = 4
				writeIndex = (writeIndex + 1) and 0xFFF
				PathFinder.distances[graphX][graphZ + 1] = nextDistance
			}
			if (graphX > 0 && graphZ > 0 && PathFinder.directions[graphX - 1][graphZ - 1] == 0 && (collisionFlags!![localX - 1]!![localZ - 1] and 0x43A40000) == 0 && (collisionFlags[localX - 1]!![localZ] and 0x42240000) == 0 && (collisionFlags[localX]!![localZ - 1] and 0x40A40000) == 0) {
				PathFinder.queueX[writeIndex] = currentX - 1
				PathFinder.queueZ[writeIndex] = currentZ - 1
				writeIndex = (writeIndex + 1) and 0xFFF
				PathFinder.directions[graphX - 1][graphZ - 1] = 3
				PathFinder.distances[graphX - 1][graphZ - 1] = nextDistance
			}
			if (graphX < 127 && graphZ > 0 && PathFinder.directions[graphX + 1][graphZ - 1] == 0 && (collisionFlags!![localX + 1]!![localZ - 1] and 0x60E40000) == 0 && (collisionFlags[localX + 1]!![localZ] and 0x60240000) == 0 && (collisionFlags[localX]!![localZ - 1] and 0x40A40000) == 0) {
				PathFinder.queueX[writeIndex] = currentX + 1
				PathFinder.queueZ[writeIndex] = currentZ - 1
				writeIndex = (writeIndex + 1) and 0xFFF
				PathFinder.directions[graphX + 1][graphZ - 1] = 9
				PathFinder.distances[graphX + 1][graphZ - 1] = nextDistance
			}
			if (graphX > 0 && graphZ < 127 && PathFinder.directions[graphX - 1][graphZ + 1] == 0 && (collisionFlags!![localX - 1]!![localZ + 1] and 0x4E240000) == 0 && (collisionFlags[localX - 1]!![localZ] and 0x42240000) == 0 && (collisionFlags[localX]!![localZ + 1] and 0x48240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX - 1
				PathFinder.queueZ[writeIndex] = currentZ + 1
				PathFinder.directions[graphX - 1][graphZ + 1] = 6
				writeIndex = (writeIndex + 1) and 0xFFF
				PathFinder.distances[graphX - 1][graphZ + 1] = nextDistance
			}
			if (graphX < 127 && graphZ < 127 && PathFinder.directions[graphX + 1][graphZ + 1] == 0 && (collisionFlags!![localX + 1]!![localZ + 1] and 0x78240000) == 0 && (collisionFlags[localX + 1]!![localZ] and 0x60240000) == 0 && (collisionFlags[localX]!![localZ + 1] and 0x48240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX + 1
				PathFinder.queueZ[writeIndex] = currentZ + 1
				PathFinder.directions[graphX + 1][graphZ + 1] = 12
				writeIndex = (writeIndex + 1) and 0xFFF
				PathFinder.distances[graphX + 1][graphZ + 1] = nextDistance
			}
		}
		PathFinder.routeExitX = currentX
		PathFinder.routeExitZ = currentZ
		return false
	}

	@OriginalMember(owner = "client!r", name = "D", descriptor = "[[I")
	@JvmField
	val directions: Array<IntArray> = Array(128) { IntArray(128) }

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(IJII)V")
	@JvmStatic
	fun walkToLoc(@OriginalArg(0) destZ: Int, @OriginalArg(1) locUid: Long, @OriginalArg(2) destX: Int) {
		@Pc(17) val shape: Int = (locUid.toInt() shr 14) and 0x1F
		@Pc(24) val rotation: Int = (locUid.toInt() shr 20) and 0x3
		@Pc(31) val locId: Int = Int.MAX_VALUE and (locUid ushr 32).toInt()
		if (shape != 10 && shape != 11 && shape != 22) {
			PathFinder.calculateRoute(true, destX, destZ, 0, 0, 0, shape, rotation)
			return
		}
		@Pc(59) val locType: LocType? = TypeLists.LOC!!.list(locId)
		@Pc(75) val sizeX: Int
		@Pc(72) val sizeZ: Int
		if (rotation == 0 || rotation == 2) {
			sizeZ = locType!!.sizeY
			sizeX = locType.sizeX
		} else {
			sizeZ = locType!!.sizeX
			sizeX = locType.sizeY
		}
		@Pc(86) var accessBlockFlags: Int = locType.accessBlockFlags
		if (rotation != 0) {
			accessBlockFlags = ((accessBlockFlags shl rotation) and 0xF) + (accessBlockFlags shr (4 - rotation))
		}
		PathFinder.calculateRoute(true, destX, destZ, sizeZ, accessBlockFlags, sizeX, 0, 0)
	}

	@OriginalMember(owner = "client!vj", name = "N", descriptor = "I")
	@JvmField
	var routeExitX: Int = 0

	@OriginalMember(owner = "client!jh", name = "b", descriptor = "(III)V")
	@JvmStatic
	fun routeToGroundItem(@OriginalArg(1) localY: Int, @OriginalArg(2) localX: Int) {
		if (Game.CURRENT_GAME === Game.STELLAR_DAWN) {
			if (!PathFinder.calculateRoute(false, localX, localY, 1, 0, 1, -2, 0)) {
				PathFinder.calculateRoute(false, localX, localY, 1, 0, 1, -3, 0)
			}
		} else if (!PathFinder.calculateRoute(false, localX, localY, 1, 0, 1, -3, 0)) {
			PathFinder.calculateRoute(false, localX, localY, 1, 0, 1, -2, 0)
		}
	}
}
