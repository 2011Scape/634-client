import com.jagex.game.runetek5.core.constants.MiniMenuAction;
import com.jagex.game.runetek5.config.iftype.SubInterfaceType;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.entity.EntityOverhead;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.Preferences;
import com.jagex.game.runetek5.DragOriginY;
import com.jagex.game.runetek5.EntityHoverMenuOptions;
import com.jagex.game.runetek5.network.world.WorldList;
import com.jagex.game.runetek5.core.io.connection.TickHookRequests;
import com.jagex.game.runetek5.camera.CameraMode;
import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.IFEvents;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.config.iftype.TargetMask;
import com.jagex.game.runetek5.config.paramtype.SkillUpdateCycle;
import com.jagex.game.runetek5.config.rsinterface.HookRequest;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapElement;
import com.jagex.game.runetek5.graphics.MiniMenuReset;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.event.mouse.MouseLog;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.input.console.CommandExecutor;
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceRenderer;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder;
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.client.input.console.Console;
import com.jagex.game.runetek5.client.input.mouse.CursorManager;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.Cursor;
import com.jagex.game.runetek5.InterfaceDrag;
import com.jagex.game.runetek5.EntityOverheadAllocator;
import com.jagex.game.runetek5.Minimap;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.Skybox;
import com.jagex.game.runetek5.core.datastruct.key.MouseOverTextY;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.client.input.mouse.MouseManager;
import com.jagex.game.runetek5.interaction.FriendList;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer;
import com.jagex.game.runetek5.config.varc.VarcTypeList;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;

public final class InterfaceComponentInput {

	@OriginalMember(owner = "client!client", name = "a", descriptor = "([Lclient!at;IIIIIIIIIII)V")
	public static void processInterfaceCycle(@OriginalArg(0) Component[] components, @OriginalArg(1) int idHash, @OriginalArg(2) int clipStartX, @OriginalArg(3) int clipStartY, @OriginalArg(4) int clipEndX, @OriginalArg(5) int clipEndY, @OriginalArg(6) int parentOffsetX, @OriginalArg(7) int parentOffsetY, @OriginalArg(8) int rawMouseX, @OriginalArg(9) int rawMouseY, @OriginalArg(10) int mouseX, @OriginalArg(11) int mouseY) {
		label800: for (@Pc(1) int i = 0; i < components.length; i++) {
			@Pc(6) Component component = components[i];
			if (component != null && component.parentId == idHash) {
				@Pc(17) int componentX = component.x + parentOffsetX;
				@Pc(22) int componentY = component.y + parentOffsetY;
				@Pc(28) int clipMinX;
				@Pc(30) int clipMinY;
				@Pc(32) int clipMaxX;
				@Pc(34) int clipMaxY;
				@Pc(40) int local40;
				@Pc(45) int local45;
				if (component.type == ComponentType.TYPE_2) {
					clipMinX = clipStartX;
					clipMinY = clipStartY;
					clipMaxX = clipEndX;
					clipMaxY = clipEndY;
				} else {
					int componentEndX = componentX + component.width;
					int componentEndY = componentY + component.height;
					if (component.type == ComponentType.LINE) {
						componentEndX++;
						componentEndY++;
					}
					clipMinX = componentX > clipStartX ? componentX : clipStartX;
					clipMinY = componentY > clipStartY ? componentY : clipStartY;
					clipMaxX = componentEndX < clipEndX ? componentEndX : clipEndX;
					clipMaxY = componentEndY < clipEndY ? componentEndY : clipEndY;
				}
				if (component.type == ComponentType.CONTAINER || component.hasScript || InterfaceManager.getComponentSettings(component).settingsHash != 0 || component == InterfaceDrag.aClass24_7 || component.contentType == Component.CONTENT_TYPE_MINIMAP || component.contentType == Component.CONTENT_TYPE_MOUSE_OVER_TEXT) {
					if (!InterfaceRenderer.isComponentHidden(component)) {
						int orthoOffsetX = 0;
						int orthoOffsetY = 0;
						if (OrthoMode.orthoEnabled) {
							orthoOffsetX = OrthoMode.getOrthoOffsetX();
							orthoOffsetY = OrthoMode.getOrthoDrawOffsetY();
						}
						if (component == InterfaceDrag.component && InterfaceDrag.getDragParent(InterfaceDrag.component) != null) {
							InterfaceDrag.dragComponentFound = true;
							InterfaceDrag.dragOriginX = componentX;
							DragOriginY.dragOriginY = componentY;
						}
						if (component.hasInteraction || clipMinX < clipMaxX && clipMinY < clipMaxY) {
							if (component.preventClickThrough && mouseX >= clipMinX && mouseY >= clipMinY && mouseX < clipMaxX && mouseY < clipMaxY) {
								for (@Pc(168) HookRequest hookRequest = (HookRequest) InterfaceManager.PENDING_HOOK_REQUESTS.first(); hookRequest != null; hookRequest = (HookRequest) InterfaceManager.PENDING_HOOK_REQUESTS.next()) {
									if (hookRequest.hasMousePosition) {
										hookRequest.unlink();
										hookRequest.source.isHooked = false;
									}
								}
								if (InterfaceRenderer.dragTickCount == 0) {
									InterfaceDrag.component = null;
									InterfaceDrag.aClass24_7 = null;
								}
								Preferences.favoriteWorlds = 0;
								WorldMapDisplay.isInteractive = false;
								WorldMap.worldMapTeleportPending = false;
								if (!MiniMenu.isOpen) {
									MiniMenuReset.resetEntries();
								}
							}
							@Pc(232) boolean withinBoundaries;
							if (MouseRecord.mouseRecorder.getMouseX() + orthoOffsetX >= clipMinX && MouseRecord.mouseRecorder.getMouseY() + orthoOffsetY >= clipMinY && MouseRecord.mouseRecorder.getMouseX() + orthoOffsetX < clipMaxX && MouseRecord.mouseRecorder.getMouseY() + orthoOffsetY < clipMaxY) {
								withinBoundaries = true;
							} else {
								withinBoundaries = false;
							}
							if (!MiniMenu.interfaceSelectionActive && withinBoundaries) {
								if (component.moveOverCursor >= 0) {
									Cursor.interfaceCursorId = component.moveOverCursor;
								} else if (component.preventClickThrough) {
									Cursor.interfaceCursorId = -1;
								}
							}
							if (ClientFeatures.ZOOM && component.isHooked && (idHash == 48889856 || idHash == 35913731)) {
								// Not part of the original client: the pointer is over the game view of the fixed or resizable layout.
								ClientFeatures.overGameScreen = true;
							}
							if (!MiniMenu.isOpen && mouseX >= clipMinX && mouseY >= clipMinY && mouseX < clipMaxX && mouseY < clipMaxY) {
								MiniMenuBuilder.interfaceComponentHovered(mouseY - componentY, mouseX - componentX, component);
							}
							@Pc(276) boolean leftButtonHeld = false;
							if (MouseRecord.mouseRecorder.isLeftButtonDown() && withinBoundaries) {
								leftButtonHeld = true;
							}
							@Pc(286) boolean clickTriggered = false;
							@Pc(291) MouseRecord mouseEvent2 = (MouseRecord) MouseManager.MOUSE_MOVEMENTS.first();
							if (mouseEvent2 != null && mouseEvent2.getClickType() == MouseLog.TYPE_PRESS_LEFT && mouseEvent2.getMouseX() + orthoOffsetX >= clipMinX && mouseEvent2.getMouseY() + orthoOffsetY >= clipMinY && mouseEvent2.getMouseX() + orthoOffsetX < clipMaxX && mouseEvent2.getMouseY() + orthoOffsetY < clipMaxY) {
								clickTriggered = true;
							}
							@Pc(335) int local335;
							@Pc(469) int local469;
							if (component.keyTriggeringAction != null && !Console.isOpen()) {
								for (int keyActionIndex = 0; keyActionIndex < component.keyTriggeringAction.length; keyActionIndex++) {
									if (KeyRecorder.keyRecorder.isPressed(component.keyTriggeringAction[keyActionIndex])) {
										if (component.cycles == null || ClientState.clientCycle >= component.cycles[keyActionIndex]) {
											@Pc(367) byte modifierMask = component.keyTriggerParams[keyActionIndex];
											if (modifierMask == 0 || ((modifierMask & 0x8) == 0 || !KeyRecorder.keyRecorder.isPressed(86) && !KeyRecorder.keyRecorder.isPressed(82) && !KeyRecorder.keyRecorder.isPressed(81)) && ((modifierMask & 0x2) == 0 || KeyRecorder.keyRecorder.isPressed(86)) && ((modifierMask & 0x1) == 0 || KeyRecorder.keyRecorder.isPressed(82)) && ((modifierMask & 0x4) == 0 || KeyRecorder.keyRecorder.isPressed(81))) {
												if (keyActionIndex < 10) {
													InterfaceOptionInteract.ifOptionInteract(-1, "", keyActionIndex + 1, component.idHash);
												} else if (keyActionIndex == 10) {
													Static657.cancelInterfaceSelection();
													@Pc(438) IFEvents serverActiveProperties = InterfaceManager.getComponentSettings(component);
													InterfaceSelectionStart.startInterfaceSelection(serverActiveProperties.interfaceId, serverActiveProperties.getUseOptionFlags(), component);
													MiniMenu.useOnType = Component.getUseOptionString(component);
													if (MiniMenu.useOnType == null) {
														MiniMenu.useOnType = "Null";
													}
													MiniMenu.useOnTarget = component.opBase + "<col=ffffff>";
												}
												int cycleStep = component.cycleSteps[keyActionIndex];
												if (component.cycles == null) {
													component.cycles = new int[component.keyTriggeringAction.length];
												}
												if (cycleStep == 0) {
													component.cycles[keyActionIndex] = Integer.MAX_VALUE;
												} else {
													component.cycles[keyActionIndex] = ClientState.clientCycle + cycleStep;
												}
											}
										}
									} else if (component.cycles != null) {
										component.cycles[keyActionIndex] = 0;
									}
								}
							}
							if (clickTriggered) {
								InterfaceManager.startDragging(component, orthoOffsetX + mouseEvent2.getMouseX() - componentX, orthoOffsetY + mouseEvent2.getMouseY() - componentY);
							}
							if (InterfaceDrag.component != null && InterfaceDrag.component != component && withinBoundaries && InterfaceManager.getComponentSettings(component).dragEnabled()) {
								InterfaceDrag.aClass24_13 = component;
							}
							if (component == InterfaceDrag.aClass24_7) {
								InterfaceDrag.dragParentFound = true;
								InterfaceDrag.dragParentX = componentX;
								FriendsChat.dragParentY = componentY;
							}
							if (component.hasScript || component.contentType != 0) {
								@Pc(558) HookRequest hookRequest2;
								if (withinBoundaries && client.mouseWheelDelta != 0 && component.onMouseMove != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.hasMousePosition = true;
									hookRequest2.source = component;
									hookRequest2.mouseY = client.mouseWheelDelta;
									hookRequest2.params = component.onMouseMove;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
									if (ClientFeatures.ZOOM) {
										// Not part of the original client: an interface that scrolls takes the wheel from the zoom.
										ClientFeatures.overGameScreen = false;
									}
								}
								if (InterfaceDrag.component != null || MiniMenu.isOpen || component.contentType != Component.CONTENT_TYPE_WORLDMAP && Preferences.favoriteWorlds > 0) {
									clickTriggered = false;
									leftButtonHeld = false;
									withinBoundaries = false;
								}
								@Pc(708) int local708;
								if (component.contentType != 0) {
									if (component.contentType == Component.CONTENT_TYPE_GAME_WINDOW || component.contentType == Component.anInt2217) {
										InterfaceManager.gameScreenComponent = component;
										if (Skybox.configNpcJs5 != null) {
											Skybox.configNpcJs5.updateForHeight(ClientState.activeToolkit, component.height);
										}
										if (component.contentType == Component.CONTENT_TYPE_GAME_WINDOW) {
											if (MiniMenu.isOpen || mouseX < clipMinX || mouseY < clipMinY || mouseX >= clipMaxX || mouseY >= clipMaxY) {
												continue;
											}
											ViewportMiniMenuPicking.tileHovered(ClientState.activeToolkit, rawMouseX, rawMouseY);
											@Pc(641) EntityOverhead overhead = (EntityOverhead) EntityOverheadAllocator.aClass325_7.getFirst();
											while (true) {
												if (overhead == null) {
													continue label800;
												}
												if (mouseX >= overhead.minX && mouseX < overhead.maxX && mouseY >= overhead.minY && mouseY < overhead.maxY) {
													MiniMenuReset.resetEntries();
													EntityHoverMenuOptions.entityHovered(overhead.pathingEntity);
												}
												overhead = (EntityOverhead) EntityOverheadAllocator.aClass325_7.getNext();
											}
										}
									}
									@Pc(747) int local747;
									if (component.contentType == Component.CONTENT_TYPE_MINIMAP) {
										if (component.getClippingMask(ClientState.activeToolkit) == null || ComponentMinimap.minimapState != 0 && ComponentMinimap.minimapState != 3 || MiniMenu.isOpen || mouseX < clipMinX || mouseY < clipMinY || mouseX >= clipMaxX || mouseY >= clipMaxY) {
											continue;
										}
										int relativeX = mouseX - componentX;
										int relativeY = mouseY - componentY;
										int rowStartX = component.rowStartX[relativeY];
										if (relativeX < rowStartX || relativeX > rowStartX + component.rowWidth[relativeY]) {
											continue;
										}
										relativeX -= component.width / 2;
										relativeY -= component.height / 2;
										int minimapAngle;
										if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
											minimapAngle = (int) Camera.camAngleY & 0x3FFF;
										} else {
											minimapAngle = (int) Camera.camAngleY + Minimap.minimapRotation & 0x3FFF;
										}
										@Pc(759) int sinAngleY = TextureCombineSource.SINE[minimapAngle];
										@Pc(763) int cosAngleY = TextureCombineSource.COSINE[minimapAngle];
										if (Camera.cameraCalculationType != CameraMode.MODE_FOLLOWCOORD) {
											sinAngleY = sinAngleY * (Minimap.minimapZoom + 256) >> 8;
											cosAngleY = cosAngleY * (Minimap.minimapZoom + 256) >> 8;
										}
										@Pc(792) int rotatedX = relativeY * sinAngleY + relativeX * cosAngleY >> 14;
										@Pc(802) int rotatedY = relativeY * cosAngleY - relativeX * sinAngleY >> 14;
										@Pc(813) int mapX;
										@Pc(821) int mapY;
										if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
											mapX = (Camera.cameraTargetAbsX >> 9) + (rotatedX >> 2);
											mapY = (Camera.cameraTargetAbsZ >> 9) - (rotatedY >> 2);
										} else {
											@Pc(830) int playerSizeOffset = (PlayerUpdate.localPlayer.getSize() - 1) * 256;
											mapX = (PlayerUpdate.localPlayer.x - playerSizeOffset >> 9) + (rotatedX >> 2);
											mapY = (PlayerUpdate.localPlayer.z - playerSizeOffset >> 9) - (rotatedY >> 2);
										}
										if (MiniMenu.interfaceSelectionActive && (MiniMenu.useTargetMask & TargetMask.TGT_GROUND) != 0) {
											@Pc(863) Component component2 = Component.getComponentWithSlot(MiniMenu.slotId, MiniMenuBuilder.USE_OPTIONS_FLAGS);
											if (component2 == null) {
												Static657.cancelInterfaceSelection();
											} else {
												MiniMenu.addEntry(false, " ->", mapY, true, mapX, component.containerItemId, true, MiniMenuAction.TGT_GROUND, (long) (component.slotId << 0 | component.idHash), MiniMenu.useOnType, 1L, MiniMenu.useOnCursor);
											}
											continue;
										}
										if (Game.CURRENT_GAME == Game.STELLAR_DAWN) {
											MiniMenu.addEntry(false, "", mapY, true, mapX, -1, true, MiniMenuAction.FACE_SQUARE, 0L, LocalizedText.aClass351_29.get(ClientState.language), 1L, -1);
										}
										MiniMenu.addEntry(false, "", mapY, true, mapX, -1, true, MiniMenuAction.WALK, 0L, MiniMenu.walkHereText, 1L, CursorManager.activeCursorId);
										continue;
									}
									if (component.contentType == Component.CONTENT_TYPE_WORLDMAP) {
										WorldMap.worldmapComponent = component;
										if (withinBoundaries) {
											WorldMapDisplay.isInteractive = true;
										}
										if (clickTriggered) {
											int mapOffsetX = (int) ((double) (orthoOffsetX + mouseEvent2.getMouseX() - componentX - component.width / 2) * 2.0D / (double) WorldMap.mapZoom);
											int mapOffsetY = (int) -((double) (orthoOffsetY + mouseEvent2.getMouseY() - componentY - component.height / 2) * 2.0D / (double) WorldMap.mapZoom);
											int worldMapX = WorldMapDisplay.mapCenterX + mapOffsetX + WorldMap.mapSquareAbsX;
											int worldMapY = WorldMapDisplay.mapCenterY + mapOffsetY + WorldMap.mapSquareAbsY;
											@Pc(994) WorldMapElement worldMapArea = WorldMapDisplay.getWorldMapArea();
											if (worldMapArea == null) {
												continue;
											}
											@Pc(999) int[] worldPosition = new int[3];
											worldMapArea.mapToGameCoords(worldMapY, worldMapX, worldPosition);
											if (worldPosition != null) {
												if (KeyRecorder.keyRecorder.isPressed(82) && LoginManager.staffModLevel > 0) {
													StaffTeleportCommand.executeTeleportCommand(worldPosition[2], worldPosition[0], worldPosition[1]);
													continue;
												}
												WorldMap.worldMapTeleportPending = true;
												CommandExecutor.teleportCommandPositionX = worldPosition[0];
												CommandExecutor.teleportCommandPositionZ = worldPosition[1];
												CommandExecutor.teleportCommandPositionY = worldPosition[2];
											}
											Preferences.favoriteWorlds = 1;
											WorldMap.worldMapDragged = false;
											InterfaceDrag.dragOffsetX = MouseRecord.mouseRecorder.getMouseX();
											InterfaceDrag.dragOffsetY = MouseRecord.mouseRecorder.getMouseY();
											continue;
										}
										if (leftButtonHeld && Preferences.favoriteWorlds > 0) {
											if (Preferences.favoriteWorlds == 1 && (InterfaceDrag.dragOffsetX != MouseRecord.mouseRecorder.getMouseX() || InterfaceDrag.dragOffsetY != MouseRecord.mouseRecorder.getMouseY())) {
												WorldMap.dragStartCenterX = WorldMapDisplay.mapCenterX;
												WorldMap.dragStartCenterY = WorldMapDisplay.mapCenterY;
												Preferences.favoriteWorlds = 2;
											}
											if (Preferences.favoriteWorlds == 2) {
												WorldMap.worldMapDragged = true;
												WorldMapDisplay.setMapCenterX(WorldMap.dragStartCenterX + (int) ((double) (InterfaceDrag.dragOffsetX - MouseRecord.mouseRecorder.getMouseX()) * 2.0D / (double) WorldMapRenderer.requestedMapZoom));
												WorldMapDisplay.setMapCenterY(WorldMap.dragStartCenterY - (int) ((double) (InterfaceDrag.dragOffsetY - MouseRecord.mouseRecorder.getMouseY()) * 2.0D / (double) WorldMapRenderer.requestedMapZoom));
											}
											continue;
										}
										if (Preferences.favoriteWorlds > 0 && !WorldMap.worldMapDragged) {
											if ((MiniMenu.menuOpenMode == 1 || MiniMenu.isTopEntryInterfaceAction()) && MiniMenu.entryCount > 2) {
												MiniMenuDeferredClick.processDeferredClick(InterfaceDrag.dragOffsetX, InterfaceDrag.dragOffsetY);
											} else if (MiniMenu.hasEntries()) {
												MiniMenuDeferredClick.processDeferredClick(InterfaceDrag.dragOffsetX, InterfaceDrag.dragOffsetY);
											}
										}
										Preferences.favoriteWorlds = 0;
										continue;
									}
									if (component.contentType == Component.WORLD_LIST_VERSION) {
										if (leftButtonHeld) {
											WorldMapDisplay.markDestination(component.width, component.height, orthoOffsetX + MouseRecord.mouseRecorder.getMouseX() - componentX, orthoOffsetY + MouseRecord.mouseRecorder.getMouseY() - componentY);
										}
										continue;
									}
									if (component.contentType == Component.CONTENT_TYPE_MOUSE_OVER_TEXT) {
										MouseOverTextY.setMouseOverText(componentY, component, componentX);
										continue;
									}
								}
								if (!component.isPressed && clickTriggered) {
									component.isPressed = true;
									if (component.onClick != null) {
										hookRequest2 = new HookRequest();
										hookRequest2.hasMousePosition = true;
										hookRequest2.source = component;
										hookRequest2.mouseX = orthoOffsetX + mouseEvent2.getMouseX() - componentX;
										hookRequest2.mouseY = orthoOffsetY + mouseEvent2.getMouseY() - componentY;
										hookRequest2.params = component.onClick;
										InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
									}
								}
								if (component.isPressed && leftButtonHeld && component.onClickRepeat != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.hasMousePosition = true;
									hookRequest2.source = component;
									hookRequest2.mouseX = orthoOffsetX + MouseRecord.mouseRecorder.getMouseX() - componentX;
									hookRequest2.mouseY = orthoOffsetY + MouseRecord.mouseRecorder.getMouseY() - componentY;
									hookRequest2.params = component.onClickRepeat;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
								}
								if (component.isPressed && !leftButtonHeld) {
									component.isPressed = false;
									if (component.onRelease != null) {
										hookRequest2 = new HookRequest();
										hookRequest2.hasMousePosition = true;
										hookRequest2.source = component;
										hookRequest2.mouseX = orthoOffsetX + MouseRecord.mouseRecorder.getMouseX() - componentX;
										hookRequest2.mouseY = orthoOffsetY + MouseRecord.mouseRecorder.getMouseY() - componentY;
										hookRequest2.params = component.onRelease;
										TickHookRequests.MOUSE_HOOK_REQUESTS.addLast(hookRequest2);
									}
								}
								if (leftButtonHeld && component.onHold != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.hasMousePosition = true;
									hookRequest2.source = component;
									hookRequest2.mouseX = orthoOffsetX + MouseRecord.mouseRecorder.getMouseX() - componentX;
									hookRequest2.mouseY = orthoOffsetY + MouseRecord.mouseRecorder.getMouseY() - componentY;
									hookRequest2.params = component.onHold;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
								}
								if (!component.isHooked && withinBoundaries) {
									component.isHooked = true;
									if (component.onMouseOver != null) {
										hookRequest2 = new HookRequest();
										hookRequest2.hasMousePosition = true;
										hookRequest2.source = component;
										hookRequest2.mouseX = orthoOffsetX + MouseRecord.mouseRecorder.getMouseX() - componentX;
										hookRequest2.mouseY = orthoOffsetY + MouseRecord.mouseRecorder.getMouseY() - componentY;
										hookRequest2.params = component.onMouseOver;
										InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
									}
								}
								if (component.isHooked && withinBoundaries && component.popupScript != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.hasMousePosition = true;
									hookRequest2.source = component;
									hookRequest2.mouseX = orthoOffsetX + MouseRecord.mouseRecorder.getMouseX() - componentX;
									hookRequest2.mouseY = orthoOffsetY + MouseRecord.mouseRecorder.getMouseY() - componentY;
									hookRequest2.params = component.popupScript;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
								}
								if (component.isHooked && !withinBoundaries) {
									component.isHooked = false;
									if (component.onMouseLeaveScript != null) {
										hookRequest2 = new HookRequest();
										hookRequest2.hasMousePosition = true;
										hookRequest2.source = component;
										hookRequest2.mouseX = orthoOffsetX + MouseRecord.mouseRecorder.getMouseX() - componentX;
										hookRequest2.mouseY = orthoOffsetY + MouseRecord.mouseRecorder.getMouseY() - componentY;
										hookRequest2.params = component.onMouseLeaveScript;
										TickHookRequests.MOUSE_HOOK_REQUESTS.addLast(hookRequest2);
									}
								}
								if (component.onTimer != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.source = component;
									hookRequest2.params = component.onTimer;
									TickHookRequests.TICK_HOOK_REQUESTS.addLast(hookRequest2);
								}
								@Pc(1570) HookRequest hookRequest3;
								if (component.onKey != null && VarcTypeList.varcUpdateCycle > component.lastKeyCycle) {
									if (component.keyPressArray == null || VarcTypeList.varcUpdateCycle - component.lastKeyCycle > 32) {
										hookRequest2 = new HookRequest();
										hookRequest2.source = component;
										hookRequest2.params = component.onKey;
										InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
									} else {
										label687: for (int keyCycle = component.lastKeyCycle; keyCycle < VarcTypeList.varcUpdateCycle; keyCycle++) {
											int updatedVarc = client.recentVarcUpdates[keyCycle & 0x1F];
											for (int triggerIndex = 0; triggerIndex < component.keyPressArray.length; triggerIndex++) {
												if (component.keyPressArray[triggerIndex] == updatedVarc) {
													hookRequest3 = new HookRequest();
													hookRequest3.source = component;
													hookRequest3.params = component.onKey;
													InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest3);
													break label687;
												}
											}
										}
									}
									component.lastKeyCycle = VarcTypeList.varcUpdateCycle;
								}
								if (component.onScrollWheel != null && VarcTypeList.varcStrUpdateCycle > component.lastMouseWheelCycle) {
									if (component.mouseWheelArray == null || VarcTypeList.varcStrUpdateCycle - component.lastMouseWheelCycle > 32) {
										hookRequest2 = new HookRequest();
										hookRequest2.source = component;
										hookRequest2.params = component.onScrollWheel;
										InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
									} else {
										label663: for (int wheelCycle = component.lastMouseWheelCycle; wheelCycle < VarcTypeList.varcStrUpdateCycle; wheelCycle++) {
											int updatedVarcStr = VarcTypeList.recentVarcStrUpdates[wheelCycle & 0x1F];
											for (int triggerIndex = 0; triggerIndex < component.mouseWheelArray.length; triggerIndex++) {
												if (component.mouseWheelArray[triggerIndex] == updatedVarcStr) {
													hookRequest3 = new HookRequest();
													hookRequest3.source = component;
													hookRequest3.params = component.onScrollWheel;
													InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest3);
													break label663;
												}
											}
										}
									}
									component.lastMouseWheelCycle = VarcTypeList.varcStrUpdateCycle;
								}
								if (component.onVarpTransmit != null && PlayerVarDomain.pulseEventConfigsPtr > component.lastVarpTransmitCycle) {
									if (component.varps == null || PlayerVarDomain.pulseEventConfigsPtr - component.lastVarpTransmitCycle > 32) {
										hookRequest2 = new HookRequest();
										hookRequest2.source = component;
										hookRequest2.params = component.onVarpTransmit;
										InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
									} else {
										label639: for (int varpCycle = component.lastVarpTransmitCycle; varpCycle < PlayerVarDomain.pulseEventConfigsPtr; varpCycle++) {
											int updatedVarp = PlayerVarDomain.LOGIN_XTEAS[varpCycle & 0x1F];
											for (int triggerIndex = 0; triggerIndex < component.varps.length; triggerIndex++) {
												if (component.varps[triggerIndex] == updatedVarp) {
													hookRequest3 = new HookRequest();
													hookRequest3.source = component;
													hookRequest3.params = component.onVarpTransmit;
													InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest3);
													break label639;
												}
											}
										}
									}
									component.lastVarpTransmitCycle = PlayerVarDomain.pulseEventConfigsPtr;
								}
								if (component.mouseLeaveScript != null && client.recentlyUsedItemContainersCursorPos > component.lastContainerTransmitCycle) {
									if (component.mouseLeaveArrayParams == null || client.recentlyUsedItemContainersCursorPos - component.lastContainerTransmitCycle > 32) {
										hookRequest2 = new HookRequest();
										hookRequest2.source = component;
										hookRequest2.params = component.mouseLeaveScript;
										InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
									} else {
										label615: for (int containerCycle = component.lastContainerTransmitCycle; containerCycle < client.recentlyUsedItemContainersCursorPos; containerCycle++) {
											int updatedContainer = ItemContainer.RECENTLY_USED_ITEM_CONTAINERS[containerCycle & 0x1F];
											for (int triggerIndex = 0; triggerIndex < component.mouseLeaveArrayParams.length; triggerIndex++) {
												if (component.mouseLeaveArrayParams[triggerIndex] == updatedContainer) {
													hookRequest3 = new HookRequest();
													hookRequest3.source = component;
													hookRequest3.params = component.mouseLeaveScript;
													InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest3);
													break label615;
												}
											}
										}
									}
									component.lastContainerTransmitCycle = client.recentlyUsedItemContainersCursorPos;
								}
								if (component.onStatTransmit != null && SkillUpdateCycle.skillUpdateCycle > component.lastStatTransmitCycle) {
									if (component.statTransmitFilter == null || SkillUpdateCycle.skillUpdateCycle - component.lastStatTransmitCycle > 32) {
										hookRequest2 = new HookRequest();
										hookRequest2.source = component;
										hookRequest2.params = component.onStatTransmit;
										InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
									} else {
										label591: for (int statCycle = component.lastStatTransmitCycle; statCycle < SkillUpdateCycle.skillUpdateCycle; statCycle++) {
											int updatedStat = SkillUpdateCycle.recentSkillUpdates[statCycle & 0x1F];
											for (int triggerIndex = 0; triggerIndex < component.statTransmitFilter.length; triggerIndex++) {
												if (component.statTransmitFilter[triggerIndex] == updatedStat) {
													hookRequest3 = new HookRequest();
													hookRequest3.source = component;
													hookRequest3.params = component.onStatTransmit;
													InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest3);
													break label591;
												}
											}
										}
									}
									component.lastStatTransmitCycle = SkillUpdateCycle.skillUpdateCycle;
								}
								if (MiniMenu.lastChatMessageTick > component.lastServerTick && component.onChatTransmit != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.source = component;
									hookRequest2.params = component.onChatTransmit;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
								}
								if (FriendList.lastFriendIgnoreUpdateTick > component.lastServerTick && component.onFriendTransmit != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.source = component;
									hookRequest2.params = component.onFriendTransmit;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
								}
								if (FriendsChat.lastFriendsChatUpdateTick > component.lastServerTick && component.onClanTransmit != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.source = component;
									hookRequest2.params = component.onClanTransmit;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
								}
								if (client.lastGrandExchangeUpdateTick > component.lastServerTick && component.onStockTransmit != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.source = component;
									hookRequest2.params = component.onStockTransmit;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
								}
								if (InterfaceManager.lastMiscUpdateTick > component.lastServerTick && component.onMiscTransmit != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.source = component;
									hookRequest2.params = component.onMiscTransmit;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
								}
								component.lastServerTick = ClientState.serverTick;
								if (component.onKeyType != null) {
									for (int heldKeyIndex = 0; heldKeyIndex < client.maximumHeldKeys; heldKeyIndex++) {
										@Pc(2038) HookRequest hookRequest4 = new HookRequest();
										hookRequest4.source = component;
										hookRequest4.typedKeyCode = client.KEY_RECORDS[heldKeyIndex].getKeyCode();
										hookRequest4.typedKeyChar = client.KEY_RECORDS[heldKeyIndex].getKeyChar();
										hookRequest4.params = component.onKeyType;
										InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest4);
									}
								}
								if (Camera.cameraSplineFinished && component.onCamFinished != null) {
									hookRequest2 = new HookRequest();
									hookRequest2.source = component;
									hookRequest2.params = component.onCamFinished;
									InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest2);
								}
							}
							if (component.type == ComponentType.SPRITE && component.skyboxId != -1) {
								component.calculateLighting(TypeLists.SUN, TypeLists.SKYBOX).updateForHeight(ClientState.activeToolkit, component.height);
							}
							InterfaceComponentRenderer.requestItemSprite(component);
							if (component.type == ComponentType.CONTAINER) {
								processInterfaceCycle(components, component.idHash, clipMinX, clipMinY, clipMaxX, clipMaxY, componentX - component.scrollX, componentY - component.scrollY, rawMouseX, rawMouseY, mouseX, mouseY);
								if (component.slotChildren != null) {
									processInterfaceCycle(component.slotChildren, component.idHash, clipMinX, clipMinY, clipMaxX, clipMaxY, componentX - component.scrollX, componentY - component.scrollY, rawMouseX, rawMouseY, mouseX, mouseY);
								}
								@Pc(2164) SubInterface subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) component.idHash);
								if (subInterface != null) {
									if (Game.CURRENT_GAME == Game.RUNESCAPE && subInterface.openType == SubInterfaceType.MODAL && !MiniMenu.isOpen && withinBoundaries && !InterfaceManager.qaOpTest) {
										MiniMenuReset.resetEntries();
									}
									InterfaceRootInput.processInterfaceInput(subInterface.interfaceId, componentX, clipMinX, clipMaxX, rawMouseY, rawMouseX, mouseX, clipMaxY, componentY, mouseY, clipMinY);
								}
							}
						}
					}
				} else if (clipMinX < clipMaxX && clipMinY < clipMaxY) {
					InterfaceComponentRenderer.requestItemSprite(component);
				}
			}
		}
	}
}
