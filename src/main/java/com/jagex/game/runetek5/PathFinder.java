package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.constants.LocShapes;
import com.jagex.game.runetek5.scene.CollisionMap;
import com.jagex.game.runetek5.collision.DirectionFlag;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.PulseEvent;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.client.Game;

public final class PathFinder {

	@OriginalMember(owner = "client!qj", name = "a", descriptor = "(IIIIIIIIILclient!fe;II)Z")
	public static boolean performCalculationSX(@OriginalArg(0) int destSizeX, @OriginalArg(1) int rotation, @OriginalArg(3) int srcZ, @OriginalArg(4) int destSizeZ, @OriginalArg(5) int destX, @OriginalArg(6) int srcX, @OriginalArg(7) int accessBlockFlag, @OriginalArg(8) int entitySize, @OriginalArg(9) CollisionMap collisionMap, @OriginalArg(10) int shape, @OriginalArg(11) int destZ) {
		@Pc(7) int currentX = srcX;
		@Pc(9) int currentZ = srcZ;
		@Pc(17) int graphBaseX = srcX - 64;
		PathFinder.directions[64][64] = 99;
		@Pc(45) int graphBaseZ = srcZ - 64;
		PathFinder.distances[64][64] = 0;
		@Pc(53) byte startIndex = 0;
		PathFinder.queueX[0] = srcX;
		@Pc(59) int read = 0;
		@Pc(62) int write = startIndex + 1;
		PathFinder.queueZ[0] = srcZ;
		@Pc(67) int[][] collisionFlags = collisionMap.collisionFlags;
		while (true) {
			label282: while (true) {
				@Pc(82) int graphX;
				@Pc(87) int graphZ;
				@Pc(98) int clipX;
				@Pc(103) int clipZ;
				@Pc(246) int nextDistance;
				@Pc(282) int step;
				do {
					do {
						do {
							label259: do {
								if (read == write) {
									PathFinder.routeExitZ = currentZ;
									PathFinder.routeExitX = currentX;
									return false;
								}
								currentZ = PathFinder.queueZ[read];
								currentX = PathFinder.queueX[read];
								graphX = currentX - graphBaseX;
								graphZ = currentZ - graphBaseZ;
								read = read + 1 & 0xFFF;
								clipX = currentX - collisionMap.xOffset;
								clipZ = currentZ - collisionMap.zOffset;
								if (shape == -4) {
									if (currentX == destX && destZ == currentZ) {
										PathFinder.routeExitX = currentX;
										PathFinder.routeExitZ = currentZ;
										return true;
									}
								} else if (shape == -3) {
									if (PathFinder.rectanglesOverlap(destSizeX, destZ, entitySize, currentX, currentZ, destX, entitySize, destSizeZ)) {
										PathFinder.routeExitZ = currentZ;
										PathFinder.routeExitX = currentX;
										return true;
									}
								} else if (shape == -2) {
									if (collisionMap.isOutsideRect(destX, accessBlockFlag, entitySize, destSizeZ, currentX, entitySize, currentZ, destZ, destSizeX)) {
										PathFinder.routeExitX = currentX;
										PathFinder.routeExitZ = currentZ;
										return true;
									}
								} else if (shape == -1) {
									if (collisionMap.isInsideOrOutsideRect(currentX, destZ, entitySize, destSizeZ, destSizeX, accessBlockFlag, currentZ, destX)) {
										PathFinder.routeExitX = currentX;
										PathFinder.routeExitZ = currentZ;
										return true;
									}
								} else if (shape == LocShapes.WALL_STRAIGHT || shape == LocShapes.WALL_DIAGONALCORNER || shape == LocShapes.WALL_L || shape == LocShapes.WALL_SQUARECORNER || shape == LocShapes.WALL_DIAGONAL) {
									if (collisionMap.canReachWall(destX, currentZ, entitySize, rotation, currentX, destZ, shape)) {
										PathFinder.routeExitX = currentX;
										PathFinder.routeExitZ = currentZ;
										return true;
									}
								} else if (collisionMap.canReachWallDecoration(destZ, currentZ, destX, currentX, shape, rotation, entitySize)) {
									PathFinder.routeExitZ = currentZ;
									PathFinder.routeExitX = currentX;
									return true;
								}
								nextDistance = PathFinder.distances[graphX][graphZ] + 1;
								if (graphX > 0 && PathFinder.directions[graphX - 1][graphZ] == 0 && (collisionFlags[clipX - 1][clipZ] & 0x43A40000) == 0 && (collisionFlags[clipX - 1][entitySize + clipZ - 1] & 0x4E240000) == 0) {
									step = 1;
									while (true) {
										if (entitySize - 1 <= step) {
											PathFinder.queueX[write] = currentX - 1;
											PathFinder.queueZ[write] = currentZ;
											write = write + 1 & 0xFFF;
											PathFinder.directions[graphX - 1][graphZ] = 2;
											PathFinder.distances[graphX - 1][graphZ] = nextDistance;
											break;
										}
										if ((collisionFlags[clipX - 1][step + clipZ] & 0x4FA40000) != 0) {
											break;
										}
										step++;
									}
								}
								if (graphX < 128 - entitySize && PathFinder.directions[graphX + 1][graphZ] == 0 && (collisionFlags[clipX + entitySize][clipZ] & 0x60E40000) == 0 && (collisionFlags[entitySize + clipX][clipZ + entitySize - 1] & 0x78240000) == 0) {
									step = 1;
									while (true) {
										if (entitySize - 1 <= step) {
											PathFinder.queueX[write] = currentX + 1;
											PathFinder.queueZ[write] = currentZ;
											PathFinder.directions[graphX + 1][graphZ] = 8;
											write = write + 1 & 0xFFF;
											PathFinder.distances[graphX + 1][graphZ] = nextDistance;
											break;
										}
										if ((collisionFlags[entitySize + clipX][clipZ + step] & 0x78E40000) != 0) {
											break;
										}
										step++;
									}
								}
								if (graphZ > 0 && PathFinder.directions[graphX][graphZ - 1] == 0 && (collisionFlags[clipX][clipZ - 1] & 0x43A40000) == 0 && (collisionFlags[clipX + entitySize - 1][clipZ - 1] & 0x60E40000) == 0) {
									step = 1;
									while (true) {
										if (step >= entitySize - 1) {
											PathFinder.queueX[write] = currentX;
											PathFinder.queueZ[write] = currentZ - 1;
											PathFinder.directions[graphX][graphZ - 1] = 1;
											write = write + 1 & 0xFFF;
											PathFinder.distances[graphX][graphZ - 1] = nextDistance;
											break;
										}
										if ((collisionFlags[clipX + step][clipZ - 1] & 0x63E40000) != 0) {
											break;
										}
										step++;
									}
								}
								if (graphZ < 128 - entitySize && PathFinder.directions[graphX][graphZ + 1] == 0 && (collisionFlags[clipX][clipZ + entitySize] & 0x4E240000) == 0 && (collisionFlags[entitySize + clipX - 1][clipZ + entitySize] & 0x78240000) == 0) {
									step = 1;
									while (true) {
										if (entitySize - 1 <= step) {
											PathFinder.queueX[write] = currentX;
											PathFinder.queueZ[write] = currentZ + 1;
											PathFinder.directions[graphX][graphZ + 1] = 4;
											write = write + 1 & 0xFFF;
											PathFinder.distances[graphX][graphZ + 1] = nextDistance;
											break;
										}
										if ((collisionFlags[clipX + step][entitySize + clipZ] & 0x7E240000) != 0) {
											break;
										}
										step++;
									}
								}
								if (graphX > 0 && graphZ > 0 && PathFinder.directions[graphX - 1][graphZ - 1] == 0 && (collisionFlags[clipX - 1][clipZ - 1] & 0x43A40000) == 0) {
									step = 1;
									while (true) {
										if (entitySize <= step) {
											PathFinder.queueX[write] = currentX - 1;
											PathFinder.queueZ[write] = currentZ - 1;
											write = write + 1 & 0xFFF;
											PathFinder.directions[graphX - 1][graphZ - 1] = 3;
											PathFinder.distances[graphX - 1][graphZ - 1] = nextDistance;
											break;
										}
										if ((collisionFlags[clipX - 1][clipZ + step - 1] & 0x4FA40000) != 0 || (collisionFlags[clipX + step - 1][clipZ - 1] & 0x63E40000) != 0) {
											break;
										}
										step++;
									}
								}
								if (graphX < 128 - entitySize && graphZ > 0 && PathFinder.directions[graphX + 1][graphZ - 1] == 0 && (collisionFlags[clipX + entitySize][clipZ - 1] & 0x60E40000) == 0) {
									step = 1;
									while (true) {
										if (entitySize <= step) {
											PathFinder.queueX[write] = currentX + 1;
											PathFinder.queueZ[write] = currentZ - 1;
											PathFinder.directions[graphX + 1][graphZ - 1] = 9;
											write = write + 1 & 0xFFF;
											PathFinder.distances[graphX + 1][graphZ - 1] = nextDistance;
											break;
										}
										if ((collisionFlags[clipX + entitySize][step + clipZ - 1] & 0x78E40000) != 0 || (collisionFlags[clipX + step][clipZ - 1] & 0x63E40000) != 0) {
											break;
										}
										step++;
									}
								}
								if (graphX > 0 && graphZ < 128 - entitySize && PathFinder.directions[graphX - 1][graphZ + 1] == 0 && (collisionFlags[clipX - 1][entitySize + clipZ] & 0x4E240000) == 0) {
									for (step = 1; step < entitySize; step++) {
										if ((collisionFlags[clipX - 1][step + clipZ] & 0x4FA40000) != 0 || (collisionFlags[step + clipX - 1][clipZ + entitySize] & 0x7E240000) != 0) {
											continue label259;
										}
									}
									PathFinder.queueX[write] = currentX - 1;
									PathFinder.queueZ[write] = currentZ + 1;
									PathFinder.directions[graphX - 1][graphZ + 1] = 6;
									write = write + 1 & 0xFFF;
									PathFinder.distances[graphX - 1][graphZ + 1] = nextDistance;
								}
							} while (128 - entitySize <= graphX);
						} while (graphZ >= 128 - entitySize);
					} while (PathFinder.directions[graphX + 1][graphZ + 1] != 0);
				} while ((collisionFlags[entitySize + clipX][clipZ + entitySize] & 0x78240000) != 0);
				for (step = 1; step < entitySize; step++) {
					if ((collisionFlags[clipX + step][clipZ + entitySize] & 0x7E240000) != 0 || (collisionFlags[entitySize + clipX][clipZ + step] & 0x78E40000) != 0) {
						continue label282;
					}
				}
				PathFinder.queueX[write] = currentX + 1;
				PathFinder.queueZ[write] = currentZ + 1;
				write = write + 1 & 0xFFF;
				PathFinder.directions[graphX + 1][graphZ + 1] = 12;
				PathFinder.distances[graphX + 1][graphZ + 1] = nextDistance;
			}
		}
	}

	@OriginalMember(owner = "client!wd", name = "a", descriptor = "(III[IZBIII[IILclient!fe;III)I")
	public static int findRoute(@OriginalArg(0) int destX, @OriginalArg(1) int entitySize, @OriginalArg(2) int destSizeZ, @OriginalArg(3) int[] stepsZ, @OriginalArg(4) boolean findAlternative, @OriginalArg(6) int shape, @OriginalArg(7) int srcX, @OriginalArg(8) int srcZ, @OriginalArg(9) int[] stepsX, @OriginalArg(10) int destZ, @OriginalArg(11) CollisionMap collisionMap, @OriginalArg(12) int destSizeX, @OriginalArg(13) int accessBlockFlag, @OriginalArg(14) int rotation) {
		for (@Pc(3) int i = 0; i < 128; i++) {
			for (@Pc(7) int j = 0; j < 128; j++) {
				PathFinder.directions[i][j] = 0;
				PathFinder.distances[i][j] = 99999999;
			}
		}
		@Pc(58) boolean canReach;
		if (entitySize == 1) {
			canReach = PathFinder.performCalculationS1(destX, destZ, srcZ, rotation, accessBlockFlag, destSizeX, collisionMap, shape, destSizeZ, srcX);
		} else if (entitySize == 2) {
			canReach = PathFinder.performCalculationS2(shape, destZ, collisionMap, destX, srcZ, destSizeX, destSizeZ, accessBlockFlag, srcX, rotation);
		} else {
			canReach = PathFinder.performCalculationSX(destSizeX, rotation, srcZ, destSizeZ, destX, srcX, accessBlockFlag, entitySize, collisionMap, shape, destZ);
		}
		@Pc(93) int graphBaseX = srcX - 64;
		@Pc(97) int graphBaseZ = srcZ - 64;
		@Pc(99) int exitX = PathFinder.routeExitX;
		@Pc(101) int exitZ = PathFinder.routeExitZ;
		@Pc(109) int local109;
		@Pc(111) int local111;
		@Pc(118) int local118;
		if (!canReach) {
			if (!findAlternative) {
				return -1;
			}
			int bestDistanceSquared = Integer.MAX_VALUE;
			int bestCost = Integer.MAX_VALUE;
			for (int candidateX = destX - 10; candidateX <= destX + 10; candidateX++) {
				for (@Pc(124) int k = destZ - 10; k <= destZ + 10; k++) {
					@Pc(130) int graphX = candidateX - graphBaseX;
					@Pc(135) int graphZ = k - graphBaseZ;
					if (graphX >= 0 && graphZ >= 0 && graphX < 128 && graphZ < 128 && PathFinder.distances[graphX][graphZ] < 100) {
						@Pc(157) int dx = 0;
						if (candidateX < destX) {
							dx = destX - candidateX;
						} else if (destSizeX + destX - 1 < candidateX) {
							dx = candidateX + 1 - destX - destSizeX;
						}
						@Pc(188) int dz = 0;
						if (k < destZ) {
							dz = destZ - k;
						} else if (k > destSizeZ + destZ - 1) {
							dz = k + 1 - destSizeZ - destZ;
						}
						@Pc(226) int distanceSquared = dx * dx + dz * dz;
						if (bestDistanceSquared > distanceSquared || bestDistanceSquared == distanceSquared && bestCost > PathFinder.distances[graphX][graphZ]) {
							bestDistanceSquared = distanceSquared;
							exitX = candidateX;
							bestCost = PathFinder.distances[graphX][graphZ];
							exitZ = k;
						}
					}
				}
			}
			if (bestDistanceSquared == Integer.MAX_VALUE) {
				return -1;
			}
		}
		if (exitX == srcX && srcZ == exitZ) {
			return 0;
		}
		@Pc(300) byte startIndex = 0;
		PathFinder.queueX[0] = exitX;
		int queueSize = startIndex + 1;
		PathFinder.queueZ[0] = exitZ;
		@Pc(327) int lastDirection;
		int direction = lastDirection = PathFinder.directions[exitX - graphBaseX][exitZ - graphBaseZ];
		while (srcX != exitX || exitZ != srcZ) {
			if (direction != lastDirection) {
				PathFinder.queueX[queueSize] = exitX;
				lastDirection = direction;
				PathFinder.queueZ[queueSize++] = exitZ;
			}
			if ((direction & DirectionFlag.EAST) != 0) {
				exitX++;
			} else if ((direction & DirectionFlag.WEST) != 0) {
				exitX--;
			}
			if ((direction & DirectionFlag.NORTH) != 0) {
				exitZ++;
			} else if ((direction & DirectionFlag.SOUTH) != 0) {
				exitZ--;
			}
			direction = PathFinder.directions[exitX - graphBaseX][exitZ - graphBaseZ];
		}
		int stepCount = 0;
		while (queueSize-- > 0) {
			stepsX[stepCount] = PathFinder.queueX[queueSize];
			stepsZ[stepCount++] = PathFinder.queueZ[queueSize];
			if (stepsX.length <= stepCount) {
				break;
			}
		}
		return stepCount;
	}
	@OriginalMember(owner = "client!fs", name = "a", descriptor = "(IIIIIIIII)Z")
	public static boolean rectanglesOverlap(@OriginalArg(0) int destSizeX, @OriginalArg(1) int destZ, @OriginalArg(3) int srcSizeZ, @OriginalArg(4) int srcX, @OriginalArg(5) int srcZ, @OriginalArg(6) int destX, @OriginalArg(7) int srcSizeX, @OriginalArg(8) int destSizeZ) {
		if (destSizeX + destX > srcX && destX < srcX + srcSizeX) {
			return srcZ < destZ + destSizeZ && srcSizeZ + srcZ > destZ;
		} else {
			return false;
		}
	}
	@OriginalMember(owner = "client!ad", name = "h", descriptor = "[[I")
	public static final int[][] distances = new int[128][128];

	@OriginalMember(owner = "client!uha", name = "a", descriptor = "(IIILclient!fe;IIIIIII)Z")
	public static boolean performCalculationS2(@OriginalArg(0) int shape, @OriginalArg(2) int destZ, @OriginalArg(3) CollisionMap collisionMap, @OriginalArg(4) int destX, @OriginalArg(5) int srcZ, @OriginalArg(6) int destSizeX, @OriginalArg(7) int destSizeZ, @OriginalArg(8) int accessBlockFlag, @OriginalArg(9) int srcX, @OriginalArg(10) int rotation) {
		@Pc(7) int currentX = srcX;
		@Pc(9) int currentZ = srcZ;
		@Pc(18) int graphBaseX = srcX - 64;
		@Pc(23) int graphBaseZ = srcZ - 64;
		PathFinder.directions[64][64] = 99;
		PathFinder.distances[64][64] = 0;
		@Pc(42) byte startIndex = 0;
		PathFinder.queueX[0] = srcX;
		@Pc(48) int read = 0;
		@Pc(51) int write = startIndex + 1;
		PathFinder.queueZ[0] = srcZ;
		@Pc(56) int[][] collisionFlags = collisionMap.collisionFlags;
		while (write != read) {
			currentZ = PathFinder.queueZ[read];
			currentX = PathFinder.queueX[read];
			read = read + 1 & 0xFFF;
			@Pc(76) int graphX = currentX - graphBaseX;
			@Pc(81) int graphZ = currentZ - graphBaseZ;
			@Pc(87) int clipX = currentX - collisionMap.xOffset;
			@Pc(93) int clipZ = currentZ - collisionMap.zOffset;
			if (shape == -4) {
				if (destX == currentX && destZ == currentZ) {
					PathFinder.routeExitX = currentX;
					PathFinder.routeExitZ = currentZ;
					return true;
				}
			} else if (shape == -3) {
				if (PathFinder.rectanglesOverlap(destSizeX, destZ, 2, currentX, currentZ, destX, 2, destSizeZ)) {
					PathFinder.routeExitZ = currentZ;
					PathFinder.routeExitX = currentX;
					return true;
				}
			} else if (shape == -2) {
				if (collisionMap.isOutsideRect(destX, accessBlockFlag, 2, destSizeZ, currentX, 2, currentZ, destZ, destSizeX)) {
					PathFinder.routeExitX = currentX;
					PathFinder.routeExitZ = currentZ;
					return true;
				}
			} else if (shape == -1) {
				if (collisionMap.isInsideOrOutsideRect(currentX, destZ, 2, destSizeZ, destSizeX, accessBlockFlag, currentZ, destX)) {
					PathFinder.routeExitX = currentX;
					PathFinder.routeExitZ = currentZ;
					return true;
				}
			} else if (shape == 0 || shape == 1 || shape == 2 || shape == 3 || shape == 9) {
				if (collisionMap.canReachWall(destX, currentZ, 2, rotation, currentX, destZ, shape)) {
					PathFinder.routeExitX = currentX;
					PathFinder.routeExitZ = currentZ;
					return true;
				}
			} else if (collisionMap.canReachWallDecoration(destZ, currentZ, destX, currentX, shape, rotation, 2)) {
				PathFinder.routeExitX = currentX;
				PathFinder.routeExitZ = currentZ;
				return true;
			}
			@Pc(246) int nextDistance = PathFinder.distances[graphX][graphZ] + 1;
			if (graphX > 0 && PathFinder.directions[graphX - 1][graphZ] == 0 && (collisionFlags[clipX - 1][clipZ] & 0x43A40000) == 0 && (collisionFlags[clipX - 1][clipZ + 1] & 0x4E240000) == 0) {
				PathFinder.queueX[write] = currentX - 1;
				PathFinder.queueZ[write] = currentZ;
				PathFinder.directions[graphX - 1][graphZ] = 2;
				write = write + 1 & 0xFFF;
				PathFinder.distances[graphX - 1][graphZ] = nextDistance;
			}
			if (graphX < 126 && PathFinder.directions[graphX + 1][graphZ] == 0 && (collisionFlags[clipX + 2][clipZ] & 0x60E40000) == 0 && (collisionFlags[clipX + 2][clipZ + 1] & 0x78240000) == 0) {
				PathFinder.queueX[write] = currentX + 1;
				PathFinder.queueZ[write] = currentZ;
				PathFinder.directions[graphX + 1][graphZ] = 8;
				write = write + 1 & 0xFFF;
				PathFinder.distances[graphX + 1][graphZ] = nextDistance;
			}
			if (graphZ > 0 && PathFinder.directions[graphX][graphZ - 1] == 0 && (collisionFlags[clipX][clipZ - 1] & 0x43A40000) == 0 && (collisionFlags[clipX + 1][clipZ - 1] & 0x60E40000) == 0) {
				PathFinder.queueX[write] = currentX;
				PathFinder.queueZ[write] = currentZ - 1;
				PathFinder.directions[graphX][graphZ - 1] = 1;
				write = write + 1 & 0xFFF;
				PathFinder.distances[graphX][graphZ - 1] = nextDistance;
			}
			if (graphZ < 126 && PathFinder.directions[graphX][graphZ + 1] == 0 && (collisionFlags[clipX][clipZ + 2] & 0x4E240000) == 0 && (collisionFlags[clipX + 1][clipZ + 2] & 0x78240000) == 0) {
				PathFinder.queueX[write] = currentX;
				PathFinder.queueZ[write] = currentZ + 1;
				write = write + 1 & 0xFFF;
				PathFinder.directions[graphX][graphZ + 1] = 4;
				PathFinder.distances[graphX][graphZ + 1] = nextDistance;
			}
			if (graphX > 0 && graphZ > 0 && PathFinder.directions[graphX - 1][graphZ - 1] == 0 && (collisionFlags[clipX - 1][clipZ] & 0x4FA40000) == 0 && (collisionFlags[clipX - 1][clipZ - 1] & 0x43A40000) == 0 && (collisionFlags[clipX][clipZ - 1] & 0x63E40000) == 0) {
				PathFinder.queueX[write] = currentX - 1;
				PathFinder.queueZ[write] = currentZ - 1;
				write = write + 1 & 0xFFF;
				PathFinder.directions[graphX - 1][graphZ - 1] = 3;
				PathFinder.distances[graphX - 1][graphZ - 1] = nextDistance;
			}
			if (graphX < 126 && graphZ > 0 && PathFinder.directions[graphX + 1][graphZ - 1] == 0 && (collisionFlags[clipX + 1][clipZ - 1] & 0x63E40000) == 0 && (collisionFlags[clipX + 2][clipZ - 1] & 0x60E40000) == 0 && (collisionFlags[clipX + 2][clipZ] & 0x78E40000) == 0) {
				PathFinder.queueX[write] = currentX + 1;
				PathFinder.queueZ[write] = currentZ - 1;
				PathFinder.directions[graphX + 1][graphZ - 1] = 9;
				write = write + 1 & 0xFFF;
				PathFinder.distances[graphX + 1][graphZ - 1] = nextDistance;
			}
			if (graphX > 0 && graphZ < 126 && PathFinder.directions[graphX - 1][graphZ + 1] == 0 && (collisionFlags[clipX - 1][clipZ + 1] & 0x4FA40000) == 0 && (collisionFlags[clipX - 1][clipZ + 2] & 0x4E240000) == 0 && (collisionFlags[clipX][clipZ + 2] & 0x7E240000) == 0) {
				PathFinder.queueX[write] = currentX - 1;
				PathFinder.queueZ[write] = currentZ + 1;
				PathFinder.directions[graphX - 1][graphZ + 1] = 6;
				write = write + 1 & 0xFFF;
				PathFinder.distances[graphX - 1][graphZ + 1] = nextDistance;
			}
			if (graphX < 126 && graphZ < 126 && PathFinder.directions[graphX + 1][graphZ + 1] == 0 && (collisionFlags[clipX + 1][clipZ + 2] & 0x7E240000) == 0 && (collisionFlags[clipX + 2][clipZ + 2] & 0x78240000) == 0 && (collisionFlags[clipX + 2][clipZ + 1] & 0x78E40000) == 0) {
				PathFinder.queueX[write] = currentX + 1;
				PathFinder.queueZ[write] = currentZ + 1;
				write = write + 1 & 0xFFF;
				PathFinder.directions[graphX + 1][graphZ + 1] = 12;
				PathFinder.distances[graphX + 1][graphZ + 1] = nextDistance;
			}
		}
		PathFinder.routeExitX = currentX;
		PathFinder.routeExitZ = currentZ;
		return false;
	}
	@OriginalMember(owner = "client!or", name = "p", descriptor = "[I")
	public static final int[] queueX = new int[4096];
	@OriginalMember(owner = "client!rfa", name = "j", descriptor = "[I")
	public static final int[] queueZ = new int[4096];

	@OriginalMember(owner = "client!mv", name = "c", descriptor = "I")
	public static int routeExitZ;

	@OriginalMember(owner = "client!dfa", name = "A", descriptor = "[I")
	public static final int[] WALKING_STEPS_X = new int[50];

	@OriginalMember(owner = "client!wf", name = "a", descriptor = "(ZIIBIIIII)Z")
	public static boolean calculateRoute(@OriginalArg(0) boolean findAlternative, @OriginalArg(1) int destX, @OriginalArg(2) int destZ, @OriginalArg(4) int destSizeZ, @OriginalArg(5) int accessBlockFlag, @OriginalArg(6) int destSizeX, @OriginalArg(7) int shape, @OriginalArg(8) int rotation) {
		@Pc(8) int srcX = PlayerUpdate.localPlayer.movementBaseX[0];
		@Pc(13) int srcZ = PlayerUpdate.localPlayer.movementBaseY[0];
		if (srcX < 0 || MapRegion.sceneSizeX <= srcX || srcZ < 0 || srcZ >= MapRegion.sceneSizeY) {
			return false;
		} else if (destX >= 0 && destX < MapRegion.sceneSizeX && destZ >= 0 && destZ < MapRegion.sceneSizeY) {
			@Pc(80) int stepCount = PathFinder.findRoute(destX, PlayerUpdate.localPlayer.getSize(), destSizeZ, Camera.WALKING_STEPS_Y, findAlternative, shape, srcX, srcZ, PathFinder.WALKING_STEPS_X, destZ, MapRegion.aClass104Array3[PlayerUpdate.localPlayer.level], destSizeX, accessBlockFlag, rotation);
			if (stepCount < 1) {
				return false;
			}
			Minimap.minimapFlagX = PathFinder.WALKING_STEPS_X[stepCount - 1];
			Minimap.minimapFlagY = Camera.WALKING_STEPS_Y[stepCount - 1];
			Minimap.minimapContainsFlag = false;
			PulseEvent.delayedMinimapFlagUpdate();
			return true;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IIIIIILclient!fe;IIII)Z")
	public static boolean performCalculationS1(@OriginalArg(0) int destX, @OriginalArg(1) int destZ, @OriginalArg(2) int srcZ, @OriginalArg(3) int rotation, @OriginalArg(4) int accessBlockFlag, @OriginalArg(5) int destSizeX, @OriginalArg(6) CollisionMap collisionMap, @OriginalArg(7) int shape, @OriginalArg(8) int destSizeZ, @OriginalArg(9) int srcX) {
		@Pc(7) int currentX = srcX;
		@Pc(9) int currentZ = srcZ;
		@Pc(18) int graphBaseX = srcX - 64;
		PathFinder.directions[64][64] = 99;
		@Pc(29) int graphBaseZ = srcZ - 64;
		PathFinder.distances[64][64] = 0;
		@Pc(37) byte seedIndex = 0;
		@Pc(39) int readIndex = 0;
		PathFinder.queueX[0] = srcX;
		@Pc(46) int writeIndex = seedIndex + 1;
		PathFinder.queueZ[0] = srcZ;
		@Pc(51) int[][] collisionFlags = collisionMap.collisionFlags;
		while (writeIndex != readIndex) {
			currentZ = PathFinder.queueZ[readIndex];
			currentX = PathFinder.queueX[readIndex];
			@Pc(67) int localZ = currentZ - collisionMap.zOffset;
			@Pc(72) int graphX = currentX - graphBaseX;
			@Pc(76) int graphZ = currentZ - graphBaseZ;
			@Pc(82) int localX = currentX - collisionMap.xOffset;
			readIndex = readIndex + 1 & 0xFFF;
			if (shape == -4) {
				if (currentX == destX && destZ == currentZ) {
					PathFinder.routeExitX = currentX;
					PathFinder.routeExitZ = currentZ;
					return true;
				}
			} else if (shape == -3) {
				if (PathFinder.rectanglesOverlap(destSizeX, destZ, 1, currentX, currentZ, destX, 1, destSizeZ)) {
					PathFinder.routeExitZ = currentZ;
					PathFinder.routeExitX = currentX;
					return true;
				}
			} else if (shape == -2) {
				if (collisionMap.isOutsideRect(destX, accessBlockFlag, 1, destSizeZ, currentX, 1, currentZ, destZ, destSizeX)) {
					PathFinder.routeExitZ = currentZ;
					PathFinder.routeExitX = currentX;
					return true;
				}
			} else if (shape == -1) {
				if (collisionMap.isInsideOrOutsideRect(currentX, destZ, 1, destSizeZ, destSizeX, accessBlockFlag, currentZ, destX)) {
					PathFinder.routeExitX = currentX;
					PathFinder.routeExitZ = currentZ;
					return true;
				}
			} else if (shape == 0 || shape == 1 || shape == 2 || shape == 3 || shape == 9) {
				if (collisionMap.canReachWall(destX, currentZ, 1, rotation, currentX, destZ, shape)) {
					PathFinder.routeExitZ = currentZ;
					PathFinder.routeExitX = currentX;
					return true;
				}
			} else if (collisionMap.canReachWallDecoration(destZ, currentZ, destX, currentX, shape, rotation, 1)) {
				PathFinder.routeExitX = currentX;
				PathFinder.routeExitZ = currentZ;
				return true;
			}
			@Pc(238) int nextDistance = PathFinder.distances[graphX][graphZ] + 1;
			if (graphX > 0 && PathFinder.directions[graphX - 1][graphZ] == 0 && (collisionFlags[localX - 1][localZ] & 0x42240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX - 1;
				PathFinder.queueZ[writeIndex] = currentZ;
				writeIndex = writeIndex + 1 & 0xFFF;
				PathFinder.directions[graphX - 1][graphZ] = 2;
				PathFinder.distances[graphX - 1][graphZ] = nextDistance;
			}
			if (graphX < 127 && PathFinder.directions[graphX + 1][graphZ] == 0 && (collisionFlags[localX + 1][localZ] & 0x60240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX + 1;
				PathFinder.queueZ[writeIndex] = currentZ;
				PathFinder.directions[graphX + 1][graphZ] = 8;
				writeIndex = writeIndex + 1 & 0xFFF;
				PathFinder.distances[graphX + 1][graphZ] = nextDistance;
			}
			if (graphZ > 0 && PathFinder.directions[graphX][graphZ - 1] == 0 && (collisionFlags[localX][localZ - 1] & 0x40A40000) == 0) {
				PathFinder.queueX[writeIndex] = currentX;
				PathFinder.queueZ[writeIndex] = currentZ - 1;
				PathFinder.directions[graphX][graphZ - 1] = 1;
				writeIndex = writeIndex + 1 & 0xFFF;
				PathFinder.distances[graphX][graphZ - 1] = nextDistance;
			}
			if (graphZ < 127 && PathFinder.directions[graphX][graphZ + 1] == 0 && (collisionFlags[localX][localZ + 1] & 0x48240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX;
				PathFinder.queueZ[writeIndex] = currentZ + 1;
				PathFinder.directions[graphX][graphZ + 1] = 4;
				writeIndex = writeIndex + 1 & 0xFFF;
				PathFinder.distances[graphX][graphZ + 1] = nextDistance;
			}
			if (graphX > 0 && graphZ > 0 && PathFinder.directions[graphX - 1][graphZ - 1] == 0 && (collisionFlags[localX - 1][localZ - 1] & 0x43A40000) == 0 && (collisionFlags[localX - 1][localZ] & 0x42240000) == 0 && (collisionFlags[localX][localZ - 1] & 0x40A40000) == 0) {
				PathFinder.queueX[writeIndex] = currentX - 1;
				PathFinder.queueZ[writeIndex] = currentZ - 1;
				writeIndex = writeIndex + 1 & 0xFFF;
				PathFinder.directions[graphX - 1][graphZ - 1] = 3;
				PathFinder.distances[graphX - 1][graphZ - 1] = nextDistance;
			}
			if (graphX < 127 && graphZ > 0 && PathFinder.directions[graphX + 1][graphZ - 1] == 0 && (collisionFlags[localX + 1][localZ - 1] & 0x60E40000) == 0 && (collisionFlags[localX + 1][localZ] & 0x60240000) == 0 && (collisionFlags[localX][localZ - 1] & 0x40A40000) == 0) {
				PathFinder.queueX[writeIndex] = currentX + 1;
				PathFinder.queueZ[writeIndex] = currentZ - 1;
				writeIndex = writeIndex + 1 & 0xFFF;
				PathFinder.directions[graphX + 1][graphZ - 1] = 9;
				PathFinder.distances[graphX + 1][graphZ - 1] = nextDistance;
			}
			if (graphX > 0 && graphZ < 127 && PathFinder.directions[graphX - 1][graphZ + 1] == 0 && (collisionFlags[localX - 1][localZ + 1] & 0x4E240000) == 0 && (collisionFlags[localX - 1][localZ] & 0x42240000) == 0 && (collisionFlags[localX][localZ + 1] & 0x48240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX - 1;
				PathFinder.queueZ[writeIndex] = currentZ + 1;
				PathFinder.directions[graphX - 1][graphZ + 1] = 6;
				writeIndex = writeIndex + 1 & 0xFFF;
				PathFinder.distances[graphX - 1][graphZ + 1] = nextDistance;
			}
			if (graphX < 127 && graphZ < 127 && PathFinder.directions[graphX + 1][graphZ + 1] == 0 && (collisionFlags[localX + 1][localZ + 1] & 0x78240000) == 0 && (collisionFlags[localX + 1][localZ] & 0x60240000) == 0 && (collisionFlags[localX][localZ + 1] & 0x48240000) == 0) {
				PathFinder.queueX[writeIndex] = currentX + 1;
				PathFinder.queueZ[writeIndex] = currentZ + 1;
				PathFinder.directions[graphX + 1][graphZ + 1] = 12;
				writeIndex = writeIndex + 1 & 0xFFF;
				PathFinder.distances[graphX + 1][graphZ + 1] = nextDistance;
			}
		}
		PathFinder.routeExitX = currentX;
		PathFinder.routeExitZ = currentZ;
		return false;
	}
	@OriginalMember(owner = "client!r", name = "D", descriptor = "[[I")
	public static final int[][] directions = new int[128][128];

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(IJII)V")
	public static void walkToLoc(@OriginalArg(0) int destZ, @OriginalArg(1) long locUid, @OriginalArg(2) int destX) {
		@Pc(17) int shape = (int) locUid >> 14 & 0x1F;
		@Pc(24) int rotation = (int) locUid >> 20 & 0x3;
		@Pc(31) int locId = Integer.MAX_VALUE & (int) (locUid >>> 32);
		if (shape != 10 && shape != 11 && shape != 22) {
			PathFinder.calculateRoute(true, destX, destZ, 0, 0, 0, shape, rotation);
			return;
		}
		@Pc(59) LocType locType = TypeLists.LOC.list(locId);
		@Pc(75) int sizeX;
		@Pc(72) int sizeZ;
		if (rotation == 0 || rotation == 2) {
			sizeZ = locType.sizeY;
			sizeX = locType.sizeX;
		} else {
			sizeZ = locType.sizeX;
			sizeX = locType.sizeY;
		}
		@Pc(86) int accessBlockFlags = locType.accessBlockFlags;
		if (rotation != 0) {
			accessBlockFlags = (accessBlockFlags << rotation & 0xF) + (accessBlockFlags >> 4 - rotation);
		}
		PathFinder.calculateRoute(true, destX, destZ, sizeZ, accessBlockFlags, sizeX, 0, 0);
	}

	@OriginalMember(owner = "client!vj", name = "N", descriptor = "I")
	public static int routeExitX;
	@OriginalMember(owner = "client!jh", name = "b", descriptor = "(III)V")
	public static void routeToGroundItem(@OriginalArg(1) int localY, @OriginalArg(2) int localX) {
		if (Game.CURRENT_GAME == Game.STELLAR_DAWN) {
			if (!PathFinder.calculateRoute(false, localX, localY, 1, 0, 1, -2, 0)) {
				PathFinder.calculateRoute(false, localX, localY, 1, 0, 1, -3, 0);
			}
		} else if (!PathFinder.calculateRoute(false, localX, localY, 1, 0, 1, -3, 0)) {
			PathFinder.calculateRoute(false, localX, localY, 1, 0, 1, -2, 0);
		}
	}
}
