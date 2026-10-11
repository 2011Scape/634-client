package com.jagex.game.runetek5;

import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.PathingEntity;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.entity.EntityOverhead;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;

public final class EntitySceneBuilder {

	@OriginalMember(owner = "client!client", name = "d", descriptor = "()V")
	public static void calculateEntityPriorities() {
		@Pc(1) int playerCount = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		@Pc(3) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		@Pc(8) int idleAnimationsSetting = GamePreferences.uid.idleAnimations.getValue();
		@Pc(23) boolean suppressMovingAnimations = idleAnimationsSetting == 1 && playerCount > 200 || idleAnimationsSetting == 0 && playerCount > 50;
		@Pc(85) int local85;
		for (@Pc(25) int i = 0; i < playerCount; i++) {
			@Pc(32) PlayerEntity playerEntity = PlayerUpdate.players[outPlayerIndices[i]];
			if (!playerEntity.hasAppearance()) {
				playerEntity.worldPriority = -1;
			} else if (playerEntity.hidden) {
				playerEntity.worldPriority = -1;
			} else {
				playerEntity.updateBounds();
				if (playerEntity.startX >= 0 && playerEntity.startZ >= 0 && playerEntity.endX < MapRegion.sceneSizeX && playerEntity.endZ < MapRegion.sceneSizeY) {
					playerEntity.shouldSuppressMovingAnimation = playerEntity.movingSequence.hasType ? suppressMovingAnimations : false;
					if (playerEntity == PlayerUpdate.localPlayer) {
						playerEntity.worldPriority = Integer.MAX_VALUE;
					} else {
						int drawPriority = 0;
						if (!playerEntity.isHidden) {
							drawPriority++;
						}
						if (playerEntity.healthBarEndCycle > ClientState.clientCycle) {
							drawPriority += 2;
						}
						drawPriority += 5 - playerEntity.getSize() << 2;
						if (playerEntity.pMapdot) {
							drawPriority += 512;
						} else {
							if (EntitySceneBuilder.entityDrawOrder == 0) {
								drawPriority += 32;
							} else {
								drawPriority += 128;
							}
							drawPriority += 256;
						}
						playerEntity.worldPriority = drawPriority + 1;
					}
				} else {
					playerEntity.worldPriority = -1;
				}
			}
		}
		for (@Pc(126) int j = 0; j < NPCList.NPC_UPDATE_INDEX; j++) {
			@Pc(137) NPCEntity npcEntity = ((NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[j])).npc;
			if (npcEntity.hasType() && npcEntity.npcType.resolvesToNpc(PlayerVarDomain.playerVarProvider)) {
				npcEntity.updateBounds();
				if (npcEntity.startX >= 0 && npcEntity.startZ >= 0 && npcEntity.endX < MapRegion.sceneSizeX && npcEntity.endZ < MapRegion.sceneSizeY) {
					@Pc(174) int drawPriority = 0;
					if (!npcEntity.isHidden) {
						drawPriority++;
					}
					if (npcEntity.healthBarEndCycle > ClientState.clientCycle) {
						drawPriority += 2;
					}
					drawPriority += 5 - npcEntity.getSize() << 2;
					if (EntitySceneBuilder.entityDrawOrder == 0) {
						if (npcEntity.npcType.lowPriority) {
							drawPriority += 64;
						} else {
							drawPriority += 128;
						}
					} else if (EntitySceneBuilder.entityDrawOrder == 1) {
						if (npcEntity.npcType.lowPriority) {
							drawPriority += 32;
						} else {
							drawPriority += 64;
						}
					}
					if (npcEntity.npcType.highPriority) {
						drawPriority += 1024;
					} else if (!npcEntity.npcType.mediumPriority) {
						drawPriority += 256;
					}
					npcEntity.worldPriority = drawPriority + 1;
				} else {
					npcEntity.worldPriority = -1;
				}
			} else {
				npcEntity.worldPriority = -1;
			}
		}
		for (int hintArrowIndex = 0; hintArrowIndex < PlayerUpdate.HINT_ARROWS.length; hintArrowIndex++) {
			@Pc(240) HintArrow hintArrow = PlayerUpdate.HINT_ARROWS[hintArrowIndex];
			if (hintArrow != null) {
				if (hintArrow.targetType == 1) {
					@Pc(254) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) hintArrow.targetIndex);
					if (npcEntityNode != null) {
						@Pc(259) NPCEntity npcEntity2 = npcEntityNode.npc;
						if (npcEntity2.worldPriority >= 0) {
							npcEntity2.worldPriority += 2048;
						}
					}
				} else if (hintArrow.targetType == 10) {
					@Pc(278) PlayerEntity playerEntity2 = PlayerUpdate.players[hintArrow.targetIndex];
					if (playerEntity2 != null && playerEntity2 != PlayerUpdate.localPlayer && playerEntity2.worldPriority >= 0) {
						playerEntity2.worldPriority += 2048;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!client", name = "j", descriptor = "(I)V")
	public static void positionEntitiesOnPlane(@OriginalArg(0) int level) {
		@Pc(1) int playerCount = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		@Pc(3) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		@Pc(11) int totalIndices = NPCList.hideNpcs ? playerCount : playerCount + NPCList.NPC_UPDATE_INDEX;
		for (@Pc(13) int i = 0; i < totalIndices; i++) {
			@Pc(23) PathingEntity pathingEntity;
			if (i < playerCount) {
				pathingEntity = PlayerUpdate.players[outPlayerIndices[i]];
			} else {
				pathingEntity = ((NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[i - playerCount])).npc;
			}
			if (pathingEntity.level == level) {
				pathingEntity.overheadStackCount = 0;
				if (pathingEntity.worldPriority < 0) {
					pathingEntity.isHidden = false;
				} else {
					@Pc(54) int size = pathingEntity.getSize();
					if ((size & 0x1) == 0) {
						if ((pathingEntity.x & 0x1FF) != 0 || (pathingEntity.z & 0x1FF) != 0) {
							pathingEntity.isHidden = false;
							continue;
						}
					} else if ((pathingEntity.x & 0x1FF) != 256 || (pathingEntity.z & 0x1FF) != 256) {
						pathingEntity.isHidden = false;
						continue;
					}
					@Pc(96) int local96;
					@Pc(101) int local101;
					@Pc(126) int previousCount;
					if (size == 1) {
						int tileX = pathingEntity.x >> 9;
						int tileZ = pathingEntity.z >> 9;
						if (pathingEntity.worldPriority != EntitySceneBuilder.tileDrawPriority[tileX][tileZ]) {
							pathingEntity.isHidden = true;
							continue;
						}
						if (EntitySceneBuilder.tileDrawPrioritySharedBy[tileX][tileZ] > 1) {
							previousCount = EntitySceneBuilder.tileDrawPrioritySharedBy[tileX][tileZ]--;
							pathingEntity.isHidden = true;
							continue;
						}
					} else {
						int sizeOffset = (size - 1) * 256 + 252;
						int startX = pathingEntity.x - sizeOffset >> 9;
						@Pc(155) int startZ = pathingEntity.z - sizeOffset >> 9;
						@Pc(162) int endX = pathingEntity.x + sizeOffset >> 9;
						@Pc(169) int endZ = pathingEntity.z + sizeOffset >> 9;
						if (!EntitySceneBuilder.hasExclusiveDrawPriorityTile(startZ, endX, pathingEntity.worldPriority, startX, endZ)) {
							for (@Pc(180) int j = startX; j <= endX; j++) {
								for (@Pc(183) int k = startZ; k <= endZ; k++) {
									if (pathingEntity.worldPriority == EntitySceneBuilder.tileDrawPriority[j][k]) {
										previousCount = EntitySceneBuilder.tileDrawPrioritySharedBy[j][k]--;
									}
								}
							}
							pathingEntity.isHidden = true;
							continue;
						}
					}
					pathingEntity.isHidden = false;
					pathingEntity.y = Scene.tileHeightForAbsolutePosition(pathingEntity.x, pathingEntity.level, pathingEntity.z);
					Scene.addInteractive(pathingEntity, true);
				}
			}
		}
	}

	@OriginalMember(owner = "client!client", name = "m", descriptor = "(I)V")
	public static void updateDrawPriority(@OriginalArg(0) int level) {
		@Pc(1) int playerCount = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		@Pc(3) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		for (@Pc(5) int i = 0; i < playerCount + NPCList.NPC_UPDATE_INDEX; i++) {
			@Pc(15) PathingEntity pathingEntity;
			if (i < playerCount) {
				pathingEntity = PlayerUpdate.players[outPlayerIndices[i]];
			} else {
				pathingEntity = ((NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[i - playerCount])).npc;
			}
			if (pathingEntity.level == level && pathingEntity.worldPriority >= 0) {
				@Pc(39) int size = pathingEntity.getSize();
				if ((size & 0x1) == 0) {
					if ((pathingEntity.x & 0x1FF) != 0 || (pathingEntity.z & 0x1FF) != 0) {
						continue;
					}
				} else if ((pathingEntity.x & 0x1FF) != 256 || (pathingEntity.z & 0x1FF) != 256) {
					continue;
				}
				@Pc(75) int local75;
				@Pc(80) int local80;
				@Pc(116) int previousCount;
				if (size == 1) {
					int tileX = pathingEntity.x >> 9;
					int tileZ = pathingEntity.z >> 9;
					if (pathingEntity.worldPriority > EntitySceneBuilder.tileDrawPriority[tileX][tileZ]) {
						EntitySceneBuilder.tileDrawPriority[tileX][tileZ] = pathingEntity.worldPriority;
						EntitySceneBuilder.tileDrawPrioritySharedBy[tileX][tileZ] = 1;
					} else if (pathingEntity.worldPriority == EntitySceneBuilder.tileDrawPriority[tileX][tileZ]) {
						previousCount = EntitySceneBuilder.tileDrawPrioritySharedBy[tileX][tileZ]++;
					}
				} else {
					int sizeOffset = (size - 1) * 256 + 60;
					int startX = pathingEntity.x - sizeOffset >> 9;
					@Pc(142) int startZ = pathingEntity.z - sizeOffset >> 9;
					@Pc(149) int endX = pathingEntity.x + sizeOffset >> 9;
					@Pc(156) int endZ = pathingEntity.z + sizeOffset >> 9;
					for (@Pc(158) int j = startX; j <= endX; j++) {
						for (@Pc(161) int k = startZ; k <= endZ; k++) {
							if (pathingEntity.worldPriority > EntitySceneBuilder.tileDrawPriority[j][k]) {
								EntitySceneBuilder.tileDrawPriority[j][k] = pathingEntity.worldPriority;
								EntitySceneBuilder.tileDrawPrioritySharedBy[j][k] = 1;
							} else if (pathingEntity.worldPriority == EntitySceneBuilder.tileDrawPriority[j][k]) {
								previousCount = EntitySceneBuilder.tileDrawPrioritySharedBy[j][k]++;
							}
						}
					}
				}
			}
		}
	}
	@OriginalMember(owner = "client!ida", name = "f", descriptor = "I")
	public static int entityDrawOrder = 0;
	@OriginalMember(owner = "client!qq", name = "a", descriptor = "(I)V")
	public static void positionAllEntities() {
		for (@Pc(10) EntityOverhead overhead = (EntityOverhead) EntityOverheadAllocator.aClass325_7.removeFirst(); overhead != null; overhead = (EntityOverhead) EntityOverheadAllocator.aClass325_7.removeFirst()) {
			EntityOverhead.release(overhead);
		}
		@Pc(32) int startLevel;
		@Pc(31) int endLevel;
		if (GamePreferences.uid.animateBackgroundOverride.getValue() == 1) {
			startLevel = 0;
			endLevel = 3;
		} else {
			endLevel = MapRegion.mapBuildLevel;
			startLevel = MapRegion.mapBuildLevel;
		}
		EntitySceneBuilder.calculateEntityPriorities();
		for (@Pc(41) int i = startLevel; i <= endLevel; i++) {
			EntitySceneBuilder.resetDrawPriorities();
			EntitySceneBuilder.updateDrawPriority(i);
			EntitySceneBuilder.positionEntitiesOnPlane(i);
		}
		PathingEntity.buildOverlappingEntityList();
		PlayerUpdate.positionMisalignedEntities();
	}
	@OriginalMember(owner = "client!mfa", name = "y", descriptor = "[[I")
	public static int[][] tileDrawPrioritySharedBy;

	@OriginalMember(owner = "client!gw", name = "F", descriptor = "[[I")
	public static int[][] tileDrawPriority;
	@OriginalMember(owner = "client!mh", name = "a", descriptor = "(IIIBII)Z")
	public static boolean hasExclusiveDrawPriorityTile(@OriginalArg(0) int startZ, @OriginalArg(1) int endX, @OriginalArg(2) int priority, @OriginalArg(4) int startX, @OriginalArg(5) int endZ) {
		for (@Pc(12) int i = startX; i <= endX; i++) {
			for (@Pc(16) int j = startZ; j <= endZ; j++) {
				if (priority == EntitySceneBuilder.tileDrawPriority[i][j] && EntitySceneBuilder.tileDrawPrioritySharedBy[i][j] <= 1) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!client", name = "b", descriptor = "()V")
	public static void resetDrawPriorities() {
		for (@Pc(1) int i = 0; i < MapRegion.sceneSizeX; i++) {
			@Pc(6) int[] column = EntitySceneBuilder.tileDrawPriority[i];
			for (@Pc(8) int j = 0; j < MapRegion.sceneSizeY; j++) {
				column[j] = 0;
			}
		}
	}
}
