import com.jagex.game.runetek5.client.features.TileMarkers;
import com.jagex.game.runetek5.core.constants.MiniMenuAction;
import com.jagex.game.runetek5.ByteArrayPool;
import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.ObjStackEntity;
import com.jagex.game.runetek5.PickList;
import com.jagex.game.runetek5.entity.PickableEntity;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity;
import com.jagex.game.runetek5.network.world.WorldList;
import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.entity.gameobject.GameObject;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.config.param.ParamType;
import com.jagex.game.runetek5.cache.node.ObjStackEntry;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.client.loading.screen.op.instance.PriorityIfPositionUpdate;
import com.jagex.game.runetek5.cache.node.ObjStack;
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.client.input.mouse.CursorManager;
import com.jagex.game.runetek5.scene.MapLoader;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;

public final class ViewportMiniMenuPicking {

	@OriginalMember(owner = "client!dba", name = "a", descriptor = "(Lclient!ha;IIB)V")
	public static void tileHovered(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseX, @OriginalArg(2) int mouseY) {
		if (mouseX < 0 || mouseY < 0 || Camera.projectionScaleX == 0 || ByteArrayPool.projectionScaleY == 0) {
			return;
		}
		@Pc(56) Matrix matrix;
		@Pc(45) int projectionCenterX;
		@Pc(41) int projectionCenterY;
		@Pc(47) int projectionScaleX;
		@Pc(43) int projectionScaleY;
		@Pc(71) int adjustedMouseX;
		@Pc(69) int adjustedMouseY;
		if (OrthoMode.orthoEnabled) {
			OrthoMode.applyOrthoViewport(false);
			matrix = toolkit.camera();
			@Pc(82) int[] projectionParams = toolkit.Y();
			projectionCenterX = projectionParams[0];
			projectionCenterY = projectionParams[1];
			projectionScaleY = projectionParams[3];
			projectionScaleX = projectionParams[2];
			adjustedMouseX = mouseX + OrthoMode.getOrthoPickOffsetX(false);
			adjustedMouseY = OrthoMode.getOrthoPickOffsetY(false) + mouseY;
		} else {
			toolkit.DA(Camera.pickingProjectionCenterX, Camera.pickingProjectionCenterY, Camera.projectionScaleX, ByteArrayPool.projectionScaleY);
			projectionCenterY = Camera.pickingProjectionCenterY;
			projectionScaleY = ByteArrayPool.projectionScaleY;
			projectionCenterX = Camera.pickingProjectionCenterX;
			projectionScaleX = Camera.projectionScaleX;
			toolkit.KA(InterfaceComponentRenderer.chatDisplayY, InterfaceComponentRenderer.chatDisplayX, Camera.projectionScaleX, ByteArrayPool.projectionScaleY);
			matrix = toolkit.createMatrix();
			matrix.createCamera(Camera.pickingCameraX, Camera.pickingCameraZ, Camera.pickingCameraY, Camera.pickingCameraPitch, Camera.pickingCameraYaw, Camera.pickingCameraRoll);
			toolkit.setCamera(matrix);
			adjustedMouseY = mouseY;
			adjustedMouseX = mouseX;
		}
		MapRegion.applyViewDepthRange(true);
		if (projectionScaleX == 0) {
			projectionScaleX = 1;
		}
		if (projectionScaleY == 0) {
			projectionScaleY = 1;
		}
		@Pc(142) int local142;
		@Pc(153) int nearX;
		@Pc(171) int farY;
		@Pc(280) int local280;
		@Pc(289) int local289;
		@Pc(300) int local300;
		@Pc(311) int local311;
		@Pc(134) int local134;
		@Pc(357) int local357;
		if (Scene.aClass65Array2 != null && (!MiniMenu.interfaceSelectionActive || (MiniMenu.useTargetMask & 0x40) != 0)) {
			int tileX = -1;
			@Pc(136) int tileY = -1;
			@Pc(139) int nearZ = toolkit.i();
			int farZ = toolkit.XA();
			@Pc(162) int nearY;
			@Pc(179) int farX;
			if (OrthoMode.orthographicProjection) {
				nearX = farX = HintArrow.scaledOrthoZoom * (adjustedMouseX - projectionCenterX) / projectionScaleX;
				nearY = farY = HintArrow.scaledOrthoZoom * (adjustedMouseY - projectionCenterY) / projectionScaleY;
			} else {
				nearX = nearZ * (adjustedMouseX - projectionCenterX) / projectionScaleX;
				nearY = nearZ * (adjustedMouseY - projectionCenterY) / projectionScaleY;
				farY = farZ * (adjustedMouseY - projectionCenterY) / projectionScaleY;
				farX = (adjustedMouseX - projectionCenterX) * farZ / projectionScaleX;
			}
			@Pc(218) int[] nearPoint = new int[] { nearX, nearY, nearZ };
			@Pc(233) int[] farPoint = new int[] { farX, farY, farZ };
			matrix.project(nearPoint);
			matrix.project(farPoint);
			@Pc(267) float rayT = ViewportMiniMenuPicking.traceRayToGround((float) nearPoint[1], (float) farPoint[0], (float) nearPoint[2], (float) farPoint[2], 4, (float) nearPoint[0], (float) farPoint[1]);
			if (rayT > 0.0F) {
				int rayDeltaX = farPoint[0] - nearPoint[0];
				int rayDeltaZ = farPoint[2] - nearPoint[2];
				int groundHitX = (int) ((float) nearPoint[0] + (float) rayDeltaX * rayT);
				int groundHitZ = (int) ((float) nearPoint[2] + (float) rayDeltaZ * rayT);
				tileX = (PlayerUpdate.localPlayer.getSize() - 1 << 8) + groundHitX >> 9;
				tileY = groundHitZ + (PlayerUpdate.localPlayer.getSize() - 1 << 8) >> 9;
				@Pc(339) byte level = PlayerUpdate.localPlayer.level;
				if (level < 3 && (SettingsBits.tileFunctionMasks[1][groundHitX >> 9][groundHitZ >> 9] & 0x2) != 0) {
					int bridgeLevel = level + 1;
				}
			}
			if (tileX != -1 && tileY != -1) {
				if (MiniMenu.interfaceSelectionActive && (MiniMenu.useTargetMask & 0x40) != 0) {
					@Pc(423) Component component = Component.getComponentWithSlot(MiniMenu.slotId, MiniMenuBuilder.USE_OPTIONS_FLAGS);
					if (component == null) {
						Static657.cancelInterfaceSelection();
					} else {
						MiniMenu.addEntry(false, " ->", tileY, true, tileX, -1, true, MiniMenuAction.TGT_GROUND, (long) (tileX << 0 | tileY), MiniMenu.useOnType, 0L, MiniMenu.useOnCursor);
					}
				} else {
					if (MiniMenu.showFaceHere) {
						MiniMenu.addEntry(false, "", tileY, true, tileX, -1, true, MiniMenuAction.FACE_SQUARE, (long) (tileY | tileX << 0), LocalizedText.aClass351_29.get(ClientState.language), 0L, -1);
					}
					// Not part of the original client: the tile marker entry, below Walk here (TileMarkers).
					if (TileMarkers.enabled) {
						MiniMenu.addEntry(false, "", tileY, true, tileX, -1, true, MiniMenuAction.TILE_MARKER, (long) (tileY | tileX << 0), TileMarkers.isMarked(tileX, tileY) ? "Unmark tile" : "Mark tile", 0L, -1);
					}
					MiniMenu.addEntry(false, "", tileY, true, tileX, -1, true, MiniMenuAction.WALK, (long) (tileY | tileX << 0), MiniMenu.walkHereText, 0L, CursorManager.activeCursorId);
				}
			}
		}
		if (OrthoMode.orthoEnabled) {
			OrthoMode.restoreOrthoCamera();
		}
		for (int passIndex = 0; (OrthoMode.orthoEnabled ? 2 : 1) > passIndex; passIndex++) {
			@Pc(462) boolean firstPass = passIndex == 0;
			@Pc(468) PickList pickList = firstPass ? PickList.aClass182_1 : PickList.aClass182_3;
			int pickMouseX = mouseX;
			int pickMouseY = mouseY;
			if (OrthoMode.orthoEnabled) {
				OrthoMode.applyOrthoViewport(firstPass);
				pickMouseX = mouseX + OrthoMode.getOrthoPickOffsetX(firstPass);
				pickMouseY = mouseY + OrthoMode.getOrthoPickOffsetY(firstPass);
			}
			@Pc(492) EntityNodeList linkedList = pickList.entities;
			for (@Pc(497) PickableEntity pickableEntity = (PickableEntity) linkedList.getFirst(); pickableEntity != null; pickableEntity = (PickableEntity) linkedList.getNext()) {
				if ((ViewportMiniMenuPicking.showAdvert || PlayerUpdate.localPlayer.level == pickableEntity.entity.level) && pickableEntity.hovered(pickMouseY, pickMouseX, toolkit)) {
					@Pc(527) int startZ;
					int startX;
					if (pickableEntity.entity instanceof AbstractInteractiveEntity) {
						startZ = ((AbstractInteractiveEntity) pickableEntity.entity).startZ;
						startX = ((AbstractInteractiveEntity) pickableEntity.entity).startX;
					} else {
						startZ = pickableEntity.entity.z >> 9;
						startX = pickableEntity.entity.x >> 9;
					}
					@Pc(664) int local664;
					@Pc(676) int local676;
					@Pc(787) int local787;
					@Pc(558) int local558;
					if (pickableEntity.entity instanceof PlayerEntity) {
						@Pc(554) PlayerEntity playerEntity = (PlayerEntity) pickableEntity.entity;
						int playerSize = playerEntity.getSize();
						if ((playerSize & 0x1) == 0 && (playerEntity.x & 0x1FF) == 0 && (playerEntity.z & 0x1FF) == 0 || (playerSize & 0x1) == 1 && (playerEntity.x & 0x1FF) == 256 && (playerEntity.z & 0x1FF) == 256) {
							int playerStartX = playerEntity.x - (playerEntity.getSize() - 1 << 8);
							int playerStartZ = playerEntity.z - (playerEntity.getSize() - 1 << 8);
							for (int npcIndex = 0; npcIndex < NPCList.NPC_UPDATE_INDEX; npcIndex++) {
								@Pc(635) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[npcIndex]);
								if (npcEntityNode != null) {
									@Pc(640) NPCEntity npcEntity = npcEntityNode.npc;
									if (npcEntity.clientCycles != ClientState.clientCycle && npcEntity.isHidden) {
										int otherNpcStartX = npcEntity.x - (npcEntity.npcType.size - 1 << 8);
										int otherNpcStartZ = npcEntity.z - (npcEntity.npcType.size - 1 << 8);
										if (playerStartX <= otherNpcStartX && npcEntity.npcType.size <= playerEntity.getSize() - (otherNpcStartX - playerStartX >> 9) && playerStartZ <= otherNpcStartZ && npcEntity.npcType.size <= playerEntity.getSize() - (otherNpcStartZ - playerStartZ >> 9)) {
											MiniMenuBuilder.npcHovered(PlayerUpdate.localPlayer.level != pickableEntity.entity.level, npcEntity);
											npcEntity.clientCycles = ClientState.clientCycle;
										}
									}
								}
							}
							int outPlayerCount = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
							@Pc(751) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
							for (int outPlayerIndex = 0; outPlayerIndex < outPlayerCount; outPlayerIndex++) {
								@Pc(761) PlayerEntity playerEntity2 = PlayerUpdate.players[outPlayerIndices[outPlayerIndex]];
								if (playerEntity2 != null && ClientState.clientCycle != playerEntity2.clientCycles && playerEntity != playerEntity2 && playerEntity2.isHidden) {
									int otherStartX = playerEntity2.x - (playerEntity2.getSize() - 1 << 8);
									@Pc(799) int otherStartZ = playerEntity2.z - (playerEntity2.getSize() - 1 << 8);
									if (playerStartX <= otherStartX && playerEntity2.getSize() <= playerEntity.getSize() - (otherStartX - playerStartX >> 9) && playerStartZ <= otherStartZ && playerEntity2.getSize() <= playerEntity.getSize() - (otherStartZ - playerStartZ >> 9)) {
										MiniMenu.playerHovered(PlayerUpdate.localPlayer.level != pickableEntity.entity.level, playerEntity2);
										playerEntity2.clientCycles = ClientState.clientCycle;
									}
								}
							}
						}
						if (ClientState.clientCycle == playerEntity.clientCycles) {
							continue;
						}
						MiniMenu.playerHovered(PlayerUpdate.localPlayer.level != pickableEntity.entity.level, playerEntity);
						playerEntity.clientCycles = ClientState.clientCycle;
					}
					if (pickableEntity.entity instanceof NPCEntity) {
						@Pc(902) NPCEntity npcEntity2 = (NPCEntity) pickableEntity.entity;
						if (npcEntity2.npcType != null) {
							if ((npcEntity2.npcType.size & 0x1) == 0 && (npcEntity2.x & 0x1FF) == 0 && (npcEntity2.z & 0x1FF) == 0 || (npcEntity2.npcType.size & 0x1) == 1 && (npcEntity2.x & 0x1FF) == 256 && (npcEntity2.z & 0x1FF) == 256) {
								int npcStartX = npcEntity2.x - (npcEntity2.npcType.size - 1 << 8);
								int npcStartZ = npcEntity2.z - (npcEntity2.npcType.size - 1 << 8);
								for (int npcIndex = 0; npcIndex < NPCList.NPC_UPDATE_INDEX; npcIndex++) {
									@Pc(981) NPCEntityNode npcEntityNode2 = (NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[npcIndex]);
									if (npcEntityNode2 != null) {
										@Pc(986) NPCEntity npcEntity3 = npcEntityNode2.npc;
										if (ClientState.clientCycle != npcEntity3.clientCycles && npcEntity2 != npcEntity3 && npcEntity3.isHidden) {
											int otherNpcStartX = npcEntity3.x - (npcEntity3.npcType.size - 1 << 8);
											int otherNpcStartZ = npcEntity3.z - (npcEntity3.npcType.size - 1 << 8);
											if (otherNpcStartX >= npcStartX && npcEntity2.npcType.size - (otherNpcStartX - npcStartX >> 9) >= npcEntity3.npcType.size && otherNpcStartZ >= npcStartZ && npcEntity2.npcType.size - (otherNpcStartZ - npcStartZ >> 9) >= npcEntity3.npcType.size) {
												MiniMenuBuilder.npcHovered(PlayerUpdate.localPlayer.level != pickableEntity.entity.level, npcEntity3);
												npcEntity3.clientCycles = ClientState.clientCycle;
											}
										}
									}
								}
								int outPlayerCount = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
								@Pc(1100) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
								for (int outPlayerIndex = 0; outPlayerIndex < outPlayerCount; outPlayerIndex++) {
									@Pc(1110) PlayerEntity playerEntity3 = PlayerUpdate.players[outPlayerIndices[outPlayerIndex]];
									if (playerEntity3 != null && playerEntity3.clientCycles != ClientState.clientCycle && playerEntity3.isHidden) {
										int otherPlayerStartX = playerEntity3.x - (playerEntity3.getSize() - 1 << 8);
										int otherPlayerStartZ = playerEntity3.z - (playerEntity3.getSize() - 1 << 8);
										if (npcStartX <= otherPlayerStartX && playerEntity3.getSize() <= npcEntity2.npcType.size - (otherPlayerStartX - npcStartX >> 9) && npcStartZ <= otherPlayerStartZ && playerEntity3.getSize() <= npcEntity2.npcType.size - (otherPlayerStartZ - npcStartZ >> 9)) {
											MiniMenu.playerHovered(PlayerUpdate.localPlayer.level != pickableEntity.entity.level, playerEntity3);
											playerEntity3.clientCycles = ClientState.clientCycle;
										}
									}
								}
							}
							if (npcEntity2.clientCycles == ClientState.clientCycle) {
								continue;
							}
							MiniMenuBuilder.npcHovered(pickableEntity.entity.level != PlayerUpdate.localPlayer.level, npcEntity2);
							npcEntity2.clientCycles = ClientState.clientCycle;
						}
					}
					if (pickableEntity.entity instanceof ObjStackEntity) {
						@Pc(1253) int absX = MapRegion.sceneBaseX + startX;
						int absZ = startZ + MapRegion.sceneBaseY;
						@Pc(1275) ObjStack objStack = (ObjStack) ObjStack.GROUND_ITEMS.get((long) (pickableEntity.entity.level << 28 | absZ << 14 | absX));
						if (objStack != null) {
							int objEntryIndex = 0;
							@Pc(1285) ObjStackEntry objStackEntry = (ObjStackEntry) objStack.groundItems.last();
							while (objStackEntry != null) {
								@Pc(1293) ItemType objType = TypeLists.OBJ.list(objStackEntry.id);
								if (MiniMenu.interfaceSelectionActive && pickableEntity.entity.level == PlayerUpdate.localPlayer.level) {
									@Pc(1315) ParamType paramType = MiniMenu.targetParamId == -1 ? null : TypeLists.PARAMS.list(MiniMenu.targetParamId);
									if ((MiniMenu.useTargetMask & 0x1) != 0 && (paramType == null || objType.getIntParam(paramType.defaultInt, MiniMenu.targetParamId) != paramType.defaultInt)) {
										MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=ff9040>" + objType.name, startZ, false, startX, -1, true, MiniMenuAction.TGT_OBJ, (long) objEntryIndex, MiniMenu.useOnType, (long) objStackEntry.id, MiniMenu.useOnCursor);
									}
								}
								if (pickableEntity.entity.level == PlayerUpdate.localPlayer.level) {
									@Pc(1374) String[] strings = objType.groundActions;
									for (int objOpIndex = 4; objOpIndex >= 0; objOpIndex--) {
										if (strings != null && strings[objOpIndex] != null) {
											@Pc(1386) byte clickType = 0;
											if (objOpIndex == 0) {
												clickType = MiniMenuAction.OPOBJ1;
											}
											int objCursor = CursorManager.defaultAttackCursor;
											if (objOpIndex == 1) {
												clickType = MiniMenuAction.OPOBJ2;
											}
											if (objOpIndex == 2) {
												clickType = MiniMenuAction.OPOBJ3;
											}
											if (objOpIndex == 3) {
												clickType = MiniMenuAction.OPOBJ4;
											}
											if (objType.primaryCursorActionIndex == objOpIndex) {
												objCursor = objType.primaryCursor;
											}
											if (objOpIndex == 4) {
												clickType = MiniMenuAction.OPOBJ5;
											}
											if (objOpIndex == objType.secondaryCursorActionIndex) {
												objCursor = objType.secondaryCursor;
											}
											MiniMenu.addEntry(false, "<col=ff9040>" + objType.name, startZ, false, startX, -1, true, clickType, (long) objEntryIndex, strings[objOpIndex], (long) objStackEntry.id, objCursor);
										}
									}
								}
								// Not part of the original client: the debug suffix, empty unless client.debug is set.
								MiniMenu.addEntry(pickableEntity.entity.level != PlayerUpdate.localPlayer.level, "<col=ff9040>" + objType.name + ClientFeatures.debugSuffix(objType.id), startZ, false, startX, -1, true, MiniMenuAction.OPOBJ6, (long) objEntryIndex, LocalizedText.aClass351_24.get(ClientState.language), (long) objStackEntry.id, MiniMenu.examineCursor);
								objStackEntry = (ObjStackEntry) objStack.groundItems.previous();
								objEntryIndex++;
							}
						}
					}
					if (pickableEntity.entity instanceof GameObject) {
						@Pc(1534) GameObject location = (GameObject) pickableEntity.entity;
						@Pc(1541) LocType locType = TypeLists.LOC.list(location.getId());
						if (locType.alternativeIds != null) {
							locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider);
						}
						if (locType != null) {
							if (MiniMenu.interfaceSelectionActive && PlayerUpdate.localPlayer.level == pickableEntity.entity.level) {
								@Pc(1569) ParamType paramType2 = MiniMenu.targetParamId == -1 ? null : TypeLists.PARAMS.list(MiniMenu.targetParamId);
								if ((MiniMenu.useTargetMask & 0x4) != 0 && (paramType2 == null || locType.getIntParam(MiniMenu.targetParamId, paramType2.defaultInt) != paramType2.defaultInt)) {
									MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=00ffff>" + locType.name, startZ, false, startX, -1, true, MiniMenuAction.TGT_LOC, (long) location.hashCode(), MiniMenu.useOnType, MapLoader.getLocMenuUid(location, startZ, startX), MiniMenu.useOnCursor);
								}
							}
							if (PlayerUpdate.localPlayer.level == pickableEntity.entity.level) {
								@Pc(1638) String[] strings2 = locType.actions;
								if (strings2 != null) {
									for (int locOpIndex = 4; locOpIndex >= 0; locOpIndex--) {
										if (strings2[locOpIndex] != null) {
											@Pc(1650) short clickType = 0;
											if (locOpIndex == 0) {
												clickType = MiniMenuAction.OPLOC1;
											}
											int locCursor = CursorManager.defaultAttackCursor;
											if (locOpIndex == 1) {
												clickType = MiniMenuAction.OPLOC2;
											}
											if (locOpIndex == 2) {
												clickType = MiniMenuAction.OPLOC3;
											}
											if (locOpIndex == 3) {
												clickType = MiniMenuAction.OPLOC4;
											}
											if (locOpIndex == 4) {
												clickType = MiniMenuAction.OPLOC5;
											}
											if (locType.primaryCursorActionIndex == locOpIndex) {
												locCursor = locType.primaryCursor;
											}
											if (locOpIndex == locType.secondaryCursorActionIndex) {
												locCursor = locType.secondaryCursor;
											}
											MiniMenu.addEntry(false, "<col=00ffff>" + locType.name, startZ, false, startX, -1, true, clickType, (long) location.hashCode(), strings2[locOpIndex], MapLoader.getLocMenuUid(location, startZ, startX), locCursor);
										}
									}
								}
								// Not part of the original client: the debug suffix, empty unless client.debug is set.
								MiniMenu.addEntry(pickableEntity.entity.level != PlayerUpdate.localPlayer.level, "<col=00ffff>" + locType.name + ClientFeatures.debugSuffix(locType.id, startX + MapRegion.sceneBaseX, startZ + MapRegion.sceneBaseY, pickableEntity.entity.level), startZ, false, startX, -1, true, MiniMenuAction.OPLOC6, (long) location.hashCode(), LocalizedText.aClass351_24.get(ClientState.language), (long) locType.id, MiniMenu.examineCursor);
							}
						}
					}
				}
			}
			if (OrthoMode.orthoEnabled) {
				OrthoMode.restoreOrthoCamera();
			}
		}
		MapRegion.applyViewDepthRange(false);
	}
	@OriginalMember(owner = "client!mo", name = "a", descriptor = "(FFFFIFBF)F")
	public static float traceRayToGround(@OriginalArg(0) float startY, @OriginalArg(1) float endX, @OriginalArg(2) float startZ, @OriginalArg(3) float endZ, @OriginalArg(4) int depth, @OriginalArg(5) float startX, @OriginalArg(7) float endY) {
		@Pc(7) float progress = 0.0F;
		@Pc(12) float deltaX = endX - startX;
		@Pc(17) float deltaY = endY - startY;
		@Pc(22) float deltaZ = endZ - startZ;
		@Pc(31) float prevX = 0.0F;
		@Pc(33) float prevY = 0.0F;
		@Pc(35) float prevZ = 0.0F;
		while (progress < 1.1F) {
			@Pc(43) float x = deltaX * progress + startX;
			@Pc(49) float y = startY + progress * deltaY;
			@Pc(55) float z = startZ + deltaZ * progress;
			@Pc(60) int tileX = (int) x >> 9;
			@Pc(65) int tileZ = (int) z >> 9;
			if (tileX > 0 && tileZ > 0 && tileX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > tileZ) {
				@Pc(89) int level = PlayerUpdate.localPlayer.level;
				if (level < 3 && (SettingsBits.tileFunctionMasks[1][tileX][tileZ] & 0x2) != 0) {
					level++;
				}
				@Pc(118) int groundHeight = Scene.aClass65Array2[level].averageHeight((int) x, (int) z);
				if (y > (float) groundHeight) {
					if (depth < 2) {
						return progress;
					}
					return progress + traceRayToGround(prevY, x, prevZ, z, depth - 1, prevX, y) * 0.1F - 0.1F;
				}
			}
			prevX = x;
			prevY = y;
			progress += 0.1F;
			prevZ = z;
		}
		return -1.0F;
	}
	@OriginalMember(owner = "client!kg", name = "f", descriptor = "Z")
	public static boolean showAdvert = true;
}
