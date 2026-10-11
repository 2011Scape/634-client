package com.jagex.game.runetek5;

import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.core.constants.HintArrowType;
import com.jagex.game.runetek5.core.constants.LocShapes;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.entity.gameobject.GameObject;
import com.jagex.game.runetek5.camera.CameraMode;
import com.jagex.game.runetek5.scene.CollisionFlag;
import com.jagex.game.runetek5.scene.CollisionMap;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.loctype.LocInteractivity;
import com.jagex.game.runetek5.config.mec.MECType;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import com.jagex.game.runetek5.statics.WorldMapPolygonFill;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.ObjStack;
import com.jagex.game.runetek5.entity.InteractiveEntityContainer;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapRasterManager;
import com.jagex.game.runetek5.interaction.FriendList;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.config.staticelements.StaticElements;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.cache.node.lru.PulseEvent;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder;
import com.jagex.game.runetek5.config.mapsprite.MapSpriteType;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.network.packet.TcpPacket;

public final class Minimap {

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "(Lclient!at;ILclient!aa;IIIBILclient!ha;)V")
	public static void drawMec(@OriginalArg(0) Component component, @OriginalArg(1) int clipTop, @OriginalArg(2) ComponentSurface clippingMask, @OriginalArg(3) int mectypeId, @OriginalArg(4) int offsetX, @OriginalArg(5) int clipLeft, @OriginalArg(7) int offsetY, @OriginalArg(8) Toolkit toolkit) {
		@Pc(10) MECType mapElementType = TypeLists.MEC.list(mectypeId);
		if (mapElementType == null || !mapElementType.displayedOnMinimap || !mapElementType.shouldDraw(PlayerVarDomain.playerVarProvider)) {
			return;
		}
		@Pc(34) int height;
		if (mapElementType.offsets != null) {
			@Pc(32) int[] polygonPoints = new int[mapElementType.offsets.length];
			@Pc(46) int local46;
			@Pc(57) int local57;
			@Pc(61) int local61;
			for (height = 0; height < polygonPoints.length / 2; height++) {
				int rotation;
				if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
					rotation = (int) Camera.camAngleY & 0x3FFF;
				} else {
					rotation = Minimap.minimapRotation + (int) Camera.camAngleY & 0x3FFF;
				}
				int sine = TextureCombineSource.SINE[rotation];
				int cosine = TextureCombineSource.COSINE[rotation];
				if (Camera.cameraCalculationType != CameraMode.MODE_FOLLOWCOORD) {
					sine = sine * 256 / (Minimap.minimapZoom + 256);
					cosine = cosine * 256 / (Minimap.minimapZoom + 256);
				}
				polygonPoints[height * 2] = (sine * (mapElementType.offsets[height * 2 + 1] * 4 + offsetY) + (offsetX + mapElementType.offsets[height * 2] * 4) * cosine >> 14) + (clipLeft + (component.width / 2));
				polygonPoints[height * 2 + 1] = clipTop + component.height / 2 - ((mapElementType.offsets[height * 2 + 1] * 4 + offsetY) * cosine - (offsetX + mapElementType.offsets[height * 2] * 4) * sine >> 14);
			}
			WorldMapRasterManager.fillPolygonMasked(toolkit, polygonPoints, mapElementType.lineColor, component.rowStartX, component.rowWidth);
			if (mapElementType.dashLineSpacing > 0) {
				@Pc(218) int lineX1;
				@Pc(228) int lineY1;
				@Pc(243) int swapTemp;
				@Pc(255) int swapY;
				for (int pointIndex = 0; pointIndex < polygonPoints.length / 2 - 1; pointIndex++) {
					int lineX0 = polygonPoints[pointIndex * 2];
					int lineY0 = polygonPoints[pointIndex * 2 + 1];
					lineX1 = polygonPoints[pointIndex * 2 + 2];
					lineY1 = polygonPoints[(pointIndex + 1) * 2 + 1];
					if (lineX0 > lineX1) {
						swapTemp = lineX0;
						lineX0 = lineX1;
						swapY = lineY0;
						lineY0 = lineY1;
						lineX1 = swapTemp;
						lineY1 = swapY;
					} else if (lineX1 == lineX0 && lineY0 > lineY1) {
						swapTemp = lineY0;
						lineY0 = lineY1;
						lineY1 = swapTemp;
					}
					toolkit.drawTexturedLine(lineX0, lineY0, lineX1, lineY1, mapElementType.colors[mapElementType.colorPointers[pointIndex] & 0xFF], clippingMask, clipLeft, clipTop, mapElementType.dashLineSpacing, mapElementType.dashLineLength, mapElementType.dashLineOffset);
				}
				int lineX0 = polygonPoints[polygonPoints.length - 2];
				int lineY0 = polygonPoints[polygonPoints.length - 1];
				lineX1 = polygonPoints[0];
				lineY1 = polygonPoints[1];
				if (lineX1 < lineX0) {
					swapTemp = lineX0;
					lineX0 = lineX1;
					swapY = lineY0;
					lineY0 = lineY1;
					lineX1 = swapTemp;
					lineY1 = swapY;
				} else if (lineX0 == lineX1 && lineY0 > lineY1) {
					swapTemp = lineY0;
					lineY0 = lineY1;
					lineY1 = swapTemp;
				}
				toolkit.drawTexturedLine(lineX0, lineY0, lineX1, lineY1, mapElementType.colors[mapElementType.colorPointers[mapElementType.colorPointers.length - 1] & 0xFF], clippingMask, clipLeft, clipTop, mapElementType.dashLineSpacing, mapElementType.dashLineLength, mapElementType.dashLineOffset);
			} else {
				for (int pointIndex = 0; pointIndex < polygonPoints.length / 2 - 1; pointIndex++) {
					toolkit.drawSurfaceLine(polygonPoints[pointIndex * 2], polygonPoints[pointIndex * 2 + 1], polygonPoints[(pointIndex + 1) * 2], polygonPoints[pointIndex * 2 + 3], mapElementType.colors[mapElementType.colorPointers[pointIndex] & 0xFF], clippingMask, clipLeft, clipTop);
				}
				toolkit.drawSurfaceLine(polygonPoints[polygonPoints.length - 2], polygonPoints[polygonPoints.length - 1], polygonPoints[0], polygonPoints[1], mapElementType.colors[mapElementType.colorPointers[mapElementType.colorPointers.length - 1] & 0xFF], clippingMask, clipLeft, clipTop);
			}
		}
		@Pc(474) NativeSprite sprite = null;
		if (mapElementType.defaultIconArchive != -1) {
			sprite = mapElementType.getIcon(false, toolkit);
			if (sprite != null) {
				Minimap.drawMapDot(clipTop, sprite, clipLeft, component, clippingMask, offsetY, offsetX);
			}
		}
		if (mapElementType.areaName == null) {
			return;
		}
		height = 0;
		if (sprite != null) {
			height = sprite.getHeight();
		}
		@Pc(508) AbstractFontRenderer font = Renderers.FONT_RENDERER;
		@Pc(510) FontMetrics fontMetrics = FontManager.worldMapAreaFont;
		if (mapElementType.baseTextzoom == 1) {
			font = FontManager.aClass68_4;
			fontMetrics = FontManager.messageboxFont;
		}
		if (mapElementType.baseTextzoom == 2) {
			fontMetrics = FontMetrics.defaultFontMetrics;
			font = MiniMenu.minimenuHeader;
		}
		Minimap.drawMapElementText(fontMetrics, mapElementType.defaultTextColor, clippingMask, offsetY, clipLeft, mapElementType.areaName, clipTop, font, height, component, offsetX);
		return;
	}

	@OriginalMember(owner = "client!ij", name = "a", descriptor = "(ILclient!ha;Z)Z")
	public static boolean buildMinimapSprite(@OriginalArg(0) int fromLevel, @OriginalArg(1) Toolkit toolkit) {
		@Pc(11) int startX = (MapRegion.sceneSizeX - 104) / 2;
		@Pc(17) int startZ = (MapRegion.sceneSizeY - 104) / 2;
		@Pc(19) boolean mapSpritesCached = true;
		@Pc(29) int local29;
		@Pc(40) int local40;
		for (@Pc(21) int i = startX; i < startX + 104; i++) {
			for (@Pc(25) int j = startZ; j < startZ + 104; j++) {
				for (int level = fromLevel; level <= 3; level++) {
					if (SettingsBits.isVisibleFromLevel(j, i, fromLevel, level)) {
						int mapLevel = level;
						if (SettingsBits.isBridge(j, i)) {
							mapLevel = level - 1;
						}
						if (mapLevel >= 0) {
							mapSpritesCached &= Minimap.areMapIconsReady(mapLevel, i, j);
						}
					}
				}
			}
		}
		if (!mapSpritesCached) {
			return false;
		}
		@Pc(81) int[] minimapPixels = new int[262144];
		for (int pixelIndex = 0; pixelIndex < minimapPixels.length; pixelIndex++) {
			minimapPixels[pixelIndex] = -16777216;
		}
		Minimap.aClass46_22 = toolkit.createNativeSprite(512, minimapPixels, 512, 512);
		Minimap.prepareLights();
		int colourNoActions = ((int) (Math.random() * 20.0D) + 238 - 10 << 8) + ((int) (Math.random() * 20.0D) + 238 - 10 << 16) + (int) (Math.random() * 20.0D) + 228 | 0xFF000000;
		@Pc(153) int backgroundMinRandomise = ((int) (Math.random() * 20.0D) + 228 | 0xFF00) << 16;
		@Pc(172) int pixelClearColour = (int) (Math.random() * 8.0D) | (int) (Math.random() * 8.0D) << 16 | (int) (Math.random() * 8.0D) << 8;
		@Pc(182) boolean[][] visibleTiles = new boolean[InteractiveEntityContainer.poolSize + 3][InteractiveEntityContainer.poolSize + 1 + 2];
		@Pc(188) int local188;
		@Pc(192) int local192;
		@Pc(194) int local194;
		@Pc(196) int local196;
		@Pc(205) int local205;
		@Pc(212) int local212;
		@Pc(223) int local223;
		@Pc(249) int local249;
		@Pc(253) int local253;
		@Pc(306) int xOffset;
		@Pc(312) int local312;
		@Pc(316) int local316;
		@Pc(335) int local335;
		for (@Pc(184) int k = startX; k < startX + 104; k += InteractiveEntityContainer.poolSize) {
			for (int blockZ = startZ; blockZ < startZ + 104; blockZ += InteractiveEntityContainer.poolSize) {
				int pixelOffsetX = 0;
				int pixelOffsetY = 0;
				int minX = k;
				if (k > 0) {
					minX = k - 1;
					pixelOffsetX += 4;
				}
				int minZ = blockZ;
				if (blockZ > 0) {
					minZ = blockZ - 1;
				}
				int maxX = InteractiveEntityContainer.poolSize + k;
				if (maxX < 104) {
					maxX++;
				}
				int maxZ = blockZ + InteractiveEntityContainer.poolSize;
				if (maxZ < 104) {
					maxZ++;
					pixelOffsetY += 4;
				}
				toolkit.KA(0, 0, InteractiveEntityContainer.poolSize * 4 + pixelOffsetX, InteractiveEntityContainer.poolSize * 4 + pixelOffsetY);
				toolkit.GA(-16777216);
				@Pc(257) int local257;
				for (int level = fromLevel; level <= 3; level++) {
					for (int dx = 0; dx <= InteractiveEntityContainer.poolSize; dx++) {
						for (int dz = 0; dz <= InteractiveEntityContainer.poolSize; dz++) {
							visibleTiles[dx][dz] = SettingsBits.isVisibleFromLevel(minZ + dz, minX + dx, fromLevel, level);
						}
					}
					Scene.aClass65Array2[level].drawMinimap(minX, minZ, maxX, maxZ, visibleTiles);
					if (!Minimap.showCollisionMap) {
						for (int dx = -4; dx < InteractiveEntityContainer.poolSize; dx++) {
							for (xOffset = -4; xOffset < InteractiveEntityContainer.poolSize; xOffset++) {
								int tileX = k + dx;
								int tileZ = xOffset + blockZ;
								if (tileX >= startX && startZ <= tileZ && SettingsBits.isVisibleFromLevel(tileZ, tileX, fromLevel, level)) {
									int mapLevel = level;
									if (SettingsBits.isBridge(tileZ, tileX)) {
										mapLevel = level - 1;
									}
									if (mapLevel >= 0) {
										Minimap.drawLocOnMinimap(backgroundMinRandomise, (InteractiveEntityContainer.poolSize - xOffset) * 4 + pixelOffsetY - 4, mapLevel, tileZ, tileX, toolkit, dx * 4 + pixelOffsetX, colourNoActions);
									}
								}
							}
						}
					}
				}
				if (Minimap.showCollisionMap) {
					@Pc(387) CollisionMap collisionMap = MapRegion.aClass104Array3[fromLevel];
					for (int dx = 0; dx < InteractiveEntityContainer.poolSize; dx++) {
						for (xOffset = 0; xOffset < InteractiveEntityContainer.poolSize; xOffset++) {
							int tileX = k + dx;
							int tileZ = xOffset + blockZ;
							int flags = collisionMap.collisionFlags[tileX - collisionMap.xOffset][tileZ - collisionMap.zOffset];
							if ((flags & 0x40240000) != 0) {
								toolkit.fillRect(4, dx * 4 + pixelOffsetX, (InteractiveEntityContainer.poolSize + -xOffset) * 4 + -4 + pixelOffsetY, 4, -1713569622);
							} else if ((flags & CollisionFlag.WALL_NORTH_BREAKROUTEFINDING) != 0) {
								toolkit.horizontalLine(4, pixelOffsetY + (-xOffset + InteractiveEntityContainer.poolSize) * 4 - 4, -1713569622, dx * 4 + pixelOffsetX);
							} else if ((flags & CollisionFlag.WALL_EAST_BREAKROUTEFINDING) != 0) {
								toolkit.verticalLine(pixelOffsetX + dx * 4 + 3, -1713569622, 4, (InteractiveEntityContainer.poolSize - xOffset) * 4 + pixelOffsetY - 4);
							} else if ((flags & CollisionFlag.WALL_SOUTH_BREAKROUTEFINDING) != 0) {
								toolkit.horizontalLine(4, pixelOffsetY + (InteractiveEntityContainer.poolSize - xOffset) * 4 + 3 - 4, -1713569622, pixelOffsetX + dx * 4);
							} else if ((flags & CollisionFlag.WALL_WEST_BLOCK_BREAKROUTEFINDING) != 0) {
								toolkit.verticalLine(pixelOffsetX + dx * 4, -1713569622, 4, (InteractiveEntityContainer.poolSize - xOffset) * 4 + pixelOffsetY - 4);
							}
						}
					}
				}
				toolkit.aa(pixelOffsetX, pixelOffsetY, InteractiveEntityContainer.poolSize * 4, InteractiveEntityContainer.poolSize * 4, pixelClearColour, 2);
				Minimap.aClass46_22.captureScreenRegion((k - startX) * 4 + 48, -((blockZ - startZ) * 4) + 464 + -(InteractiveEntityContainer.poolSize * 4), InteractiveEntityContainer.poolSize * 4, InteractiveEntityContainer.poolSize * 4, pixelOffsetX, pixelOffsetY);
			}
		}
		toolkit.la();
		toolkit.GA(-16777215);
		InterfaceRectangles.invalidateAllRectangles();
		Minimap.mapElementCount = 0;
		Minimap.aClass341_26.clear();
		if (!Minimap.showCollisionMap) {
			for (int tileX = startX; tileX < startX + 104; tileX++) {
				for (int tileZ = startZ; tileZ < startZ + 104; tileZ++) {
					for (int level = fromLevel; level <= fromLevel + 1 && level <= 3; level++) {
						if (SettingsBits.isVisibleFromLevel(tileZ, tileX, fromLevel, level)) {
							@Pc(678) GameObject location = (GameObject) Scene.getGroundDecoration(level, tileX, tileZ);
							if (location == null) {
								location = (GameObject) Scene.getInteractiveOfType(level, tileX, tileZ, GameObject.class);
							}
							if (location == null) {
								location = (GameObject) Scene.getWall(level, tileX, tileZ);
							}
							if (location == null) {
								location = (GameObject) Scene.getWallDecoration(level, tileX, tileZ);
							}
							if (location != null) {
								@Pc(719) LocType locType = TypeLists.LOC.list(location.getId());
								if (!locType.members || LoginManager.memberWorld) {
									int mapCategoryId = locType.mapCategoryid;
									if (locType.alternativeIds != null) {
										for (int alternativeIndex = 0; alternativeIndex < locType.alternativeIds.length; alternativeIndex++) {
											if (locType.alternativeIds[alternativeIndex] != -1) {
												@Pc(748) LocType locType2 = TypeLists.LOC.list(locType.alternativeIds[alternativeIndex]);
												if (locType2.mapCategoryid >= 0) {
													mapCategoryId = locType2.mapCategoryid;
												}
											}
										}
									}
									if (mapCategoryId >= 0) {
										@Pc(768) boolean hasRandomOffset = false;
										if (mapCategoryId >= 0) {
											@Pc(775) MECType mapElementType = TypeLists.MEC.list(mapCategoryId);
											if (mapElementType != null && mapElementType.hasRandomisedMinimapPosition) {
												hasRandomOffset = true;
											}
										}
										int elementX = tileX;
										int elementZ = tileZ;
										if (hasRandomOffset) {
											@Pc(793) int[][] collisionFlags = MapRegion.aClass104Array3[level].collisionFlags;
											xOffset = MapRegion.aClass104Array3[level].xOffset;
											int zOffset = MapRegion.aClass104Array3[level].zOffset;
											for (int attempt = 0; attempt < 10; attempt++) {
												int direction = (int) (Math.random() * 4.0D);
												if (direction == 0 && startX < elementX && tileX - 3 < elementX && (collisionFlags[elementX - xOffset - 1][elementZ - zOffset] & 0x2C0108) == 0) {
													elementX--;
												}
												if (direction == 1 && startX + 104 - 1 > elementX && tileX + 3 > elementX && (collisionFlags[elementX + 1 - xOffset][elementZ - zOffset] & 0x2C0180) == 0) {
													elementX++;
												}
												if (direction == 2 && elementZ > startZ && elementZ > tileZ - 3 && (collisionFlags[elementX - xOffset][elementZ - zOffset - 1] & 0x2C0102) == 0) {
													elementZ--;
												}
												if (direction == 3 && elementZ < startZ + 104 - 1 && tileZ + 3 > elementZ && (collisionFlags[elementX - xOffset][elementZ + 1 - zOffset] & 0x2C0120) == 0) {
													elementZ++;
												}
											}
										}
										Minimap.mapElementLocIds[Minimap.mapElementCount] = locType.id;
										Minimap.mapElementX[Minimap.mapElementCount] = elementX;
										Minimap.mapElementY[Minimap.mapElementCount] = elementZ;
										Minimap.mapElementCount++;
									}
								}
							}
						}
					}
				}
			}
			if (StaticElements.aClass333_3 != null) {
				Resource.CONFIG.loadType = 1;
				TypeLists.MEC.setCacheSize(64, 1024);
				for (int elementIndex = 0; elementIndex < StaticElements.aClass333_3.elementCount; elementIndex++) {
					int packedCoord = StaticElements.aClass333_3.bitpackedPlacement[elementIndex];
					if (PlayerUpdate.localPlayer.level == packedCoord >> 28) {
						int sceneX = (packedCoord >> 14 & 0x3FFF) - MapRegion.sceneBaseX;
						int sceneZ = (packedCoord & 0x3FFF) - MapRegion.sceneBaseY;
						if (sceneX >= 0 && sceneX < MapRegion.sceneSizeX && sceneZ >= 0 && sceneZ < MapRegion.sceneSizeY) {
							Minimap.aClass341_26.addLast(new IntNode(elementIndex));
						} else {
							@Pc(1072) MECType mapElementType2 = TypeLists.MEC.list(StaticElements.aClass333_3.mapCategoryIds[elementIndex]);
							if (mapElementType2.offsets != null && sceneX + mapElementType2.maxOffsetX >= 0 && mapElementType2.minOffsetX + sceneX < MapRegion.sceneSizeX && sceneZ + mapElementType2.maxOffsetY >= 0 && MapRegion.sceneSizeY > sceneZ + mapElementType2.minOffsetY) {
								Minimap.aClass341_26.addLast(new IntNode(elementIndex));
							}
						}
					}
				}
				TypeLists.MEC.setCacheSize(64, 128);
				Resource.CONFIG.loadType = 2;
				Resource.CONFIG.discardUnpacked();
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(JIIIILclient!at;IILclient!aa;)V")
	public static void drawHintArrow(@OriginalArg(0) long maxDistance, @OriginalArg(1) int clipTop, @OriginalArg(3) int offsetY, @OriginalArg(4) int clipLeft, @OriginalArg(5) Component component, @OriginalArg(6) int offsetX, @OriginalArg(7) int arrowSprite, @OriginalArg(8) ComponentSurface clippingMask) {
		@Pc(11) int distanceSquared = offsetY * offsetY + offsetX * offsetX;
		if ((long) distanceSquared > maxDistance) {
			return;
		}
		@Pc(39) int radius = Math.min(component.width / 2, component.height / 2);
		if (distanceSquared <= radius * radius) {
			Minimap.drawMapDot(clipTop, StaticMedia.hintMapmarkerSprites[arrowSprite], clipLeft, component, clippingMask, offsetY, offsetX);
			return;
		}
		radius -= 10;
		@Pc(53) int angle;
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			angle = (int) Camera.camAngleY & 0x3FFF;
		} else {
			angle = (int) Camera.camAngleY + Minimap.minimapRotation & 0x3FFF;
		}
		@Pc(66) int sinAngle = TextureCombineSource.SINE[angle];
		@Pc(70) int cosAngle = TextureCombineSource.COSINE[angle];
		if (Camera.cameraCalculationType != CameraMode.MODE_FOLLOWCOORD) {
			cosAngle = cosAngle * 256 / (Minimap.minimapZoom + 256);
			sinAngle = sinAngle * 256 / (Minimap.minimapZoom + 256);
		}
		@Pc(101) int rotatedX = cosAngle * offsetX + sinAngle * offsetY >> 14;
		@Pc(112) int rotatedY = cosAngle * offsetY - sinAngle * offsetX >> 14;
		@Pc(118) double theta = Math.atan2((double) rotatedX, (double) rotatedY);
		@Pc(125) int edgeX = (int) ((double) radius * Math.sin(theta));
		@Pc(132) int edgeY = (int) (Math.cos(theta) * (double) radius);
		StaticMedia.hintMapedgeSprites[arrowSprite].drawRotatedCenter((float) component.width / 2.0F + (float) clipLeft + (float) edgeX, (float) clipTop + (float) component.height / 2.0F - (float) edgeY, 4096, (int) (-theta / 6.283185307179586D * 65535.0D));
	}

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(Lclient!at;Lclient!ha;III)V")
	public static void drawMinimap(@OriginalArg(0) Component component, @OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int clipLeft, @OriginalArg(3) int clipTop) {
		@Pc(10) ComponentSurface clippingMask = component.getClippingMask(toolkit);
		if (clippingMask == null) {
			return;
		}
		toolkit.KA(clipLeft, clipTop, clipLeft + component.width, component.height + clipTop);
		if (ComponentMinimap.minimapState == 2 || ComponentMinimap.minimapState == 5 || Minimap.aClass46_22 == null) {
			toolkit.A(-16777216, clippingMask, clipLeft, clipTop);
			return;
		}
		@Pc(64) int cameraTargetAbsX;
		@Pc(52) int cameraTargetAbsZ;
		@Pc(61) int rotation;
		@Pc(49) int zoom;
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			cameraTargetAbsZ = Camera.cameraTargetAbsZ;
			cameraTargetAbsX = Camera.cameraTargetAbsX;
			zoom = 4096;
			rotation = (int) -Camera.camAngleY & 0x3FFF;
		} else {
			zoom = 4096 - Minimap.minimapZoom * 16;
			cameraTargetAbsZ = PlayerUpdate.localPlayer.z;
			rotation = (int) -Camera.camAngleY + Minimap.minimapRotation & 0x3FFF;
			cameraTargetAbsX = PlayerUpdate.localPlayer.x;
		}
		@Pc(92) int pivotX = cameraTargetAbsX / 128 + 48 + 208 - MapRegion.sceneSizeX * 2;
		@Pc(108) int pivotY = MapRegion.sceneSizeY * 4 + 208 + 48 - MapRegion.sceneSizeY * 2 - cameraTargetAbsZ / 128;
		Minimap.aClass46_22.rotate((float) component.width / 2.0F + (float) clipLeft, (float) component.height / 2.0F + (float) clipTop, (float) pivotX, (float) pivotY, zoom, rotation << 2, clippingMask, clipLeft, clipTop);
		@Pc(161) int local161;
		@Pc(171) int local171;
		@Pc(182) int local182;
		@Pc(192) int local192;
		for (@Pc(144) IntNode intNode = (IntNode) Minimap.aClass341_26.first(); intNode != null; intNode = (IntNode) Minimap.aClass341_26.next()) {
			@Pc(149) int index = intNode.value;
			int sceneX = (StaticElements.aClass333_3.bitpackedPlacement[index] >> 14 & 0x3FFF) - MapRegion.sceneBaseX;
			int sceneZ = (StaticElements.aClass333_3.bitpackedPlacement[index] & 0x3FFF) - MapRegion.sceneBaseY;
			int offsetX = sceneX * 4 + 2 - cameraTargetAbsX / 128;
			int offsetY = sceneZ * 4 + 2 - cameraTargetAbsZ / 128;
			Minimap.drawMec(component, clipTop, clippingMask, StaticElements.aClass333_3.mapCategoryIds[index], offsetX, clipLeft, offsetY, toolkit);
		}
		for (int elementIndex = 0; elementIndex < Minimap.mapElementCount; elementIndex++) {
			int offsetX = Minimap.mapElementX[elementIndex] * 4 + 2 - cameraTargetAbsX / 128;
			int offsetY = Minimap.mapElementY[elementIndex] * 4 + 2 - cameraTargetAbsZ / 128;
			@Pc(248) LocType locType = TypeLists.LOC.list(Minimap.mapElementLocIds[elementIndex]);
			if (locType.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider);
				if (locType == null || locType.mapCategoryid == -1) {
					continue;
				}
			}
			Minimap.drawMec(component, clipTop, clippingMask, locType.mapCategoryid, offsetX, clipLeft, offsetY, toolkit);
		}
		@Pc(335) int local335;
		@Pc(346) int local346;
		for (@Pc(291) ObjStack objStack = (ObjStack) ObjStack.GROUND_ITEMS.first(); objStack != null; objStack = (ObjStack) ObjStack.GROUND_ITEMS.next()) {
			int plane = (int) (objStack.key >> 28 & 0x3L);
			if (ComponentMinimap.MAP_PLANE == plane) {
				int sceneX = (int) (objStack.key & 0x3FFFL) - MapRegion.sceneBaseX;
				@Pc(324) int sceneZ = (int) (objStack.key >> 14 & 0x3FFFL) - MapRegion.sceneBaseY;
				int offsetX = sceneX * 4 + 2 - cameraTargetAbsX / 128;
				int offsetY = sceneZ * 4 + 2 - cameraTargetAbsZ / 128;
				Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites[0], clipLeft, component, clippingMask, offsetY, offsetX);
			}
		}
		@Pc(438) int local438;
		for (int npcIndex = 0; npcIndex < NPCList.NPC_UPDATE_INDEX; npcIndex++) {
			@Pc(379) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[npcIndex]);
			if (npcEntityNode != null) {
				@Pc(384) NPCEntity npcEntity = npcEntityNode.npc;
				if (npcEntity.hasType() && PlayerUpdate.localPlayer.level == npcEntity.level) {
					@Pc(400) NPCType npcType = npcEntity.npcType;
					if (npcType != null && npcType.alternativeIds != null) {
						npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
					}
					if (npcType != null && npcType.displayOnMinimap && npcType.visible) {
						int offsetX = npcEntity.x / 128 - cameraTargetAbsX / 128;
						int offsetY = npcEntity.z / 128 - cameraTargetAbsZ / 128;
						if (npcType.mecId == -1) {
							Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites[1], clipLeft, component, clippingMask, offsetY, offsetX);
						} else {
							Minimap.drawMec(component, clipTop, clippingMask, npcType.mecId, offsetX, clipLeft, offsetY, toolkit);
						}
					}
				}
			}
		}
		int outPlayerCount = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		@Pc(479) int[] outPlayerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		@Pc(530) int local530;
		@Pc(534) int local534;
		@Pc(566) int local566;
		for (int playerIndex = 0; playerIndex < outPlayerCount; playerIndex++) {
			@Pc(489) PlayerEntity playerEntity = PlayerUpdate.players[outPlayerIndices[playerIndex]];
			if (playerEntity != null && playerEntity.hasAppearance() && !playerEntity.hidden && PlayerUpdate.localPlayer != playerEntity && PlayerUpdate.localPlayer.level == playerEntity.level) {
				int offsetX = playerEntity.x / 128 - cameraTargetAbsX / 128;
				int offsetY = playerEntity.z / 128 - cameraTargetAbsZ / 128;
				@Pc(532) boolean isFriend = false;
				for (int friendIndex = 0; friendIndex < FriendList.FRIEND_COUNT; friendIndex++) {
					if (playerEntity.displayName.equals(FriendList.friendNames[friendIndex]) && FriendList.friendWorldIds[friendIndex] != 0) {
						isFriend = true;
						break;
					}
				}
				@Pc(564) boolean inFriendsChat = false;
				for (int memberIndex = 0; memberIndex < FriendsChat.clanChatUserCount; memberIndex++) {
					if (playerEntity.displayName.equals(FriendsChat.clanChatUsers[memberIndex].username)) {
						inFriendsChat = true;
						break;
					}
				}
				@Pc(586) boolean onSameTeam = false;
				if (PlayerUpdate.localPlayer.teamId != 0 && playerEntity.teamId != 0 && playerEntity.teamId == PlayerUpdate.localPlayer.teamId) {
					onSameTeam = true;
				}
				if (playerEntity.pMapdot) {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites[6], clipLeft, component, clippingMask, offsetY, offsetX);
				} else if (isFriend) {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites[3], clipLeft, component, clippingMask, offsetY, offsetX);
				} else if (inFriendsChat) {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites[5], clipLeft, component, clippingMask, offsetY, offsetX);
				} else if (onSameTeam) {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites[4], clipLeft, component, clippingMask, offsetY, offsetX);
				} else {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites[2], clipLeft, component, clippingMask, offsetY, offsetX);
				}
			}
		}
		@Pc(685) HintArrow[] hintArrows = PlayerUpdate.HINT_ARROWS;
		@Pc(772) int local772;
		for (int arrowIndex = 0; arrowIndex < hintArrows.length; arrowIndex++) {
			@Pc(693) HintArrow hintArrow = hintArrows[arrowIndex];
			if (hintArrow != null && hintArrow.targetType != HintArrowType.CLEAR && ClientState.clientCycle % 20 < 10) {
				@Pc(735) int local735;
				if (hintArrow.targetType == HintArrowType.NPC) {
					@Pc(720) NPCEntityNode npcEntityNode2 = (NPCEntityNode) NPCList.NPC_MAP.get((long) hintArrow.targetIndex);
					if (npcEntityNode2 != null) {
						@Pc(725) NPCEntity npcEntity2 = npcEntityNode2.npc;
						int offsetX = npcEntity2.x / 128 - cameraTargetAbsX / 128;
						int offsetY = npcEntity2.z / 128 - cameraTargetAbsZ / 128;
						Minimap.drawHintArrow(360000L, clipTop, offsetY, clipLeft, component, offsetX, hintArrow.arrowSprite, clippingMask);
					}
				}
				if (hintArrow.targetType == HintArrowType.TILE_CENTRE) {
					int offsetX = hintArrow.x / 128 - cameraTargetAbsX / 128;
					int offsetY = hintArrow.y / 128 - cameraTargetAbsZ / 128;
					@Pc(788) long distance = (long) (hintArrow.distance << 7);
					@Pc(792) long maxDistance = distance * distance;
					Minimap.drawHintArrow(maxDistance, clipTop, offsetY, clipLeft, component, offsetX, hintArrow.arrowSprite, clippingMask);
				}
				if (hintArrow.targetType == HintArrowType.PLAYER && hintArrow.targetIndex >= 0 && PlayerUpdate.players.length > hintArrow.targetIndex) {
					@Pc(829) PlayerEntity playerEntity2 = PlayerUpdate.players[hintArrow.targetIndex];
					if (playerEntity2 != null) {
						int offsetX = playerEntity2.x / 128 - cameraTargetAbsX / 128;
						int offsetY = playerEntity2.z / 128 - cameraTargetAbsZ / 128;
						Minimap.drawHintArrow(360000L, clipTop, offsetY, clipLeft, component, offsetX, hintArrow.arrowSprite, clippingMask);
					}
				}
			}
		}
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			return;
		}
		if (Minimap.minimapFlagX != 0) {
			int offsetX = Minimap.minimapFlagX * 4 + PlayerUpdate.localPlayer.getSize() * 2 + 2 - cameraTargetAbsX / 128 - 2;
			int offsetY = Minimap.minimapFlagY * 4 + (PlayerUpdate.localPlayer.getSize() - 1) * 2 + 2 - cameraTargetAbsZ / 128;
			Minimap.drawMapDot(clipTop, StaticMedia.mapflagSprites[Minimap.minimapContainsFlag ? 1 : 0], clipLeft, component, clippingMask, offsetY, offsetX);
		}
		if (!PlayerUpdate.localPlayer.hidden) {
			toolkit.fillRect(3, clipLeft + component.width / 2 - 1, component.height / 2 + -1 + clipTop, 3, -1);
			return;
		}
	}

	@OriginalMember(owner = "client!tk", name = "a", descriptor = "I")
	public static int visibleFromLevel = -1;

	@OriginalMember(owner = "client!uba", name = "a", descriptor = "(IIZIIILclient!ha;II)V")
	public static void drawLocOnMinimap(@OriginalArg(0) int colourHasActions, @OriginalArg(1) int y, @OriginalArg(3) int level, @OriginalArg(4) int sceneZ, @OriginalArg(5) int sceneX, @OriginalArg(6) Toolkit toolkit, @OriginalArg(7) int x, @OriginalArg(8) int colourNoActions) {
		@Pc(11) GameObject location = (GameObject) Scene.getWall(level, sceneX, sceneZ);
		@Pc(20) LocType locType;
		@Pc(26) int rotation;
		@Pc(30) int shape;
		@Pc(46) int colour;
		if (location != null) {
			locType = TypeLists.LOC.list(location.getId());
			rotation = location.getRotation() & 0x3;
			shape = location.getShape();
			if (locType.mapIconId == -1) {
				colour = colourNoActions;
				if (locType.hasActions > LocInteractivity.NONINTERACTIVE) {
					colour = colourHasActions;
				}
				if (shape == LocShapes.WALL_STRAIGHT || shape == LocShapes.WALL_L) {
					if (rotation == 0) {
						toolkit.verticalLine(x, colour, 4, y);
					} else if (rotation == 1) {
						toolkit.horizontalLine(4, y, colour, x);
					} else if (rotation == 2) {
						toolkit.verticalLine(x + 3, colour, 4, y);
					} else if (rotation == 3) {
						toolkit.horizontalLine(4, y + 3, colour, x);
					}
				}
				if (shape == LocShapes.WALL_SQUARECORNER) {
					if (rotation == 0) {
						toolkit.fillRect(1, x, y, 1, colour);
					} else if (rotation == 1) {
						toolkit.fillRect(1, x + 3, y, 1, colour);
					} else if (rotation == 2) {
						toolkit.fillRect(1, x + 3, y + 3, 1, colour);
					} else if (rotation == 3) {
						toolkit.fillRect(1, x, y + 3, 1, colour);
					}
				}
				if (shape == LocShapes.WALL_L) {
					if (rotation == 0) {
						toolkit.horizontalLine(4, y, colour, x);
					} else if (rotation == 1) {
						toolkit.verticalLine(x + 3, colour, 4, y);
					} else if (rotation == 2) {
						toolkit.horizontalLine(4, y + 3, colour, x);
					} else if (rotation == 3) {
						toolkit.verticalLine(x, colour, 4, y);
					}
				}
			} else {
				ComponentMinimap.drawMapIcon(x, locType, y, toolkit, rotation);
			}
		}
		location = (GameObject) Scene.getInteractiveOfType(level, sceneX, sceneZ, GameObject.class);
		if (location != null) {
			locType = TypeLists.LOC.list(location.getId());
			rotation = location.getRotation() & 0x3;
			shape = location.getShape();
			if (locType.mapIconId != -1) {
				ComponentMinimap.drawMapIcon(x, locType, y, toolkit, rotation);
			} else if (shape == LocShapes.WALL_DIAGONAL) {
				colour = -1118482;
				if (locType.hasActions > 0) {
					colour = -1179648;
				}
				if (rotation == 0 || rotation == 2) {
					toolkit.drawOpaqueLine(y + 3, x, x + 3, colour, y);
				} else {
					toolkit.drawOpaqueLine(y, x, x + 3, colour, y + 3);
				}
			}
		}
		location = (GameObject) Scene.getGroundDecoration(level, sceneX, sceneZ);
		if (location == null) {
			return;
		}
		locType = TypeLists.LOC.list(location.getId());
		rotation = location.getRotation() & 0x3;
		if (locType.mapIconId != -1) {
			ComponentMinimap.drawMapIcon(x, locType, y, toolkit, rotation);
			return;
		}
	}

	@OriginalMember(owner = "client!ok", name = "a", descriptor = "(ILclient!mi;ILclient!at;Lclient!aa;III)V")
	public static void drawMapDot(@OriginalArg(0) int clipTop, @OriginalArg(1) NativeSprite sprite, @OriginalArg(2) int clipLeft, @OriginalArg(3) Component component, @OriginalArg(4) ComponentSurface clippingMask, @OriginalArg(5) int offsetY, @OriginalArg(7) int offsetX) {
		if (sprite == null) {
			return;
		}
		@Pc(17) int rotation;
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			rotation = (int) Camera.camAngleY & 0x3FFF;
		} else {
			rotation = (int) Camera.camAngleY + Minimap.minimapRotation & 0x3FFF;
		}
		@Pc(36) int maxRadius = Math.max(component.width / 2, component.height / 2) + 10;
		@Pc(45) int distanceSquared = offsetX * offsetX + offsetY * offsetY;
		if (maxRadius * maxRadius < distanceSquared) {
			return;
		}
		@Pc(55) int sinAngle = TextureCombineSource.SINE[rotation];
		@Pc(59) int cosAngle = TextureCombineSource.COSINE[rotation];
		if (Camera.cameraCalculationType != CameraMode.MODE_FOLLOWCOORD) {
			cosAngle = cosAngle * 256 / (Minimap.minimapZoom + 256);
			sinAngle = sinAngle * 256 / (Minimap.minimapZoom + 256);
		}
		@Pc(88) int rotatedX = offsetX * cosAngle + offsetY * sinAngle >> 14;
		@Pc(99) int rotatedY = offsetY * cosAngle - sinAngle * offsetX >> 14;
		sprite.drawWithSurface(rotatedX + component.width / 2 + clipLeft - sprite.scaleWidth() / 2, clipTop - -(component.height / 2) + -rotatedY + -(sprite.scaleHeight() / 2), clippingMask, clipLeft, clipTop);
	}
	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(Lclient!oea;ILclient!aa;IILjava/lang/String;ILclient!da;ILclient!at;II)V")
	public static void drawMapElementText(@OriginalArg(0) FontMetrics fontMetrics, @OriginalArg(1) int color, @OriginalArg(2) ComponentSurface clippingMask, @OriginalArg(3) int offsetZ, @OriginalArg(4) int clipLeft, @OriginalArg(5) String string, @OriginalArg(6) int clipTop, @OriginalArg(7) AbstractFontRenderer font, @OriginalArg(8) int iconHeight, @OriginalArg(9) Component component, @OriginalArg(10) int offsetX) {
		@Pc(13) int angle;
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			angle = (int) Camera.camAngleY & 0x3FFF;
		} else {
			angle = Minimap.minimapRotation + (int) Camera.camAngleY & 0x3FFF;
		}
		@Pc(34) int maxDistance = Math.max(component.width / 2, component.height / 2) + 10;
		@Pc(43) int distanceSq = offsetX * offsetX + offsetZ * offsetZ;
		if (maxDistance * maxDistance < distanceSq) {
			return;
		}
		@Pc(57) int sin = TextureCombineSource.SINE[angle];
		@Pc(61) int cos = TextureCombineSource.COSINE[angle];
		if (Camera.cameraCalculationType != CameraMode.MODE_FOLLOWCOORD) {
			cos = cos * 256 / (Minimap.minimapZoom + 256);
			sin = sin * 256 / (Minimap.minimapZoom + 256);
		}
		@Pc(90) int mapX = sin * offsetZ + offsetX * cos >> 14;
		@Pc(101) int mapY = cos * offsetZ - offsetX * sin >> 14;
		@Pc(108) int textWidth = fontMetrics.getTextWidthMultiline(string, 100, null);
		@Pc(116) int textHeight = fontMetrics.getTextHeightMultiline(null, 0, 100, string);
		@Pc(122) int textX = mapX - textWidth / 2;
		if (-component.width <= textX && component.width >= textX && -component.height <= mapY && mapY <= component.height) {
			font.drawText(null, 0, 0, null, color, 0, 50, clippingMask, clipTop + component.height / 2 - iconHeight - mapY - textHeight, clipLeft, clipTop, textWidth, component.width / 2 + clipLeft + textX, 1, string);
		}
	}

	@OriginalMember(owner = "client!il", name = "q", descriptor = "[I")
	public static final int[] mapElementX = new int[1000];

	@OriginalMember(owner = "client!fi", name = "T", descriptor = "I")
	public static int mapElementCount = 0;

	@OriginalMember(owner = "client!fd", name = "J", descriptor = "[I")
	public static final int[] mapElementY = new int[1000];
	@OriginalMember(owner = "client!co", name = "W", descriptor = "Lclient!uh;")
	public static final Deque aClass341_26 = new Deque();
	@OriginalMember(owner = "client!mo", name = "i", descriptor = "Lclient!mi;")
	public static NativeSprite aClass46_22;

	@OriginalMember(owner = "client!gf", name = "d", descriptor = "Z")
	public static boolean showCollisionMap = false;

	@OriginalMember(owner = "client!dfa", name = "U", descriptor = "I")
	public static int minimapZoomStep = 1;
	@OriginalMember(owner = "client!tha", name = "s", descriptor = "I")
	public static int minimapFlagX = -1;

	@OriginalMember(owner = "client!uc", name = "J", descriptor = "I")
	public static int minimapRotation = 0;

	@OriginalMember(owner = "client!gh", name = "Sb", descriptor = "I")
	public static int minimapZoom = 0;

	@OriginalMember(owner = "client!uha", name = "e", descriptor = "[I")
	public static final int[] mapElementLocIds = new int[1000];

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(ILclient!at;BI)V")
	public static void drawCompass(@OriginalArg(0) int y, @OriginalArg(1) Component component, @OriginalArg(3) int x) {
		@Pc(8) ComponentSurface clippingMask = component.getClippingMask(ClientState.activeToolkit);
		if (clippingMask == null) {
			return;
		}
		ClientState.activeToolkit.KA(x, y, component.width + x, y - -component.height);
		if (ComponentMinimap.minimapState >= 3) {
			ClientState.activeToolkit.A(-16777216, clippingMask, x, y);
		} else {
			StaticMedia.compassSprite.drawRotatedSprite((float) component.width / 2.0F + (float) x, (float) y + (float) component.height / 2.0F, ((int) -Camera.camAngleY & 0x3FFF) << 2, clippingMask, x, y);
		}
	}

	@OriginalMember(owner = "client!mc", name = "e", descriptor = "I")
	public static int minimapFlagY = -1;
	@OriginalMember(owner = "client!ie", name = "t", descriptor = "Z")
	public static boolean minimapContainsFlag = true;

	@OriginalMember(owner = "client!le", name = "a", descriptor = "(IIIB)Z")
	public static boolean areMapIconsReady(@OriginalArg(0) int level, @OriginalArg(1) int x, @OriginalArg(2) int z) {
		@Pc(14) GameObject location = (GameObject) Scene.getWall(level, x, z);
		@Pc(16) boolean ready = true;
		if (location != null) {
			ready = Minimap.isLocMapIconReady(location) & true;
		}
		location = (GameObject) Scene.getInteractiveOfType(level, x, z, GameObject.class);
		if (location != null) {
			ready &= Minimap.isLocMapIconReady(location);
		}
		location = (GameObject) Scene.getGroundDecoration(level, x, z);
		if (location != null) {
			ready &= Minimap.isLocMapIconReady(location);
		}
		return ready;
	}

	@OriginalMember(owner = "client!fd", name = "b", descriptor = "(B)V")
	public static void resetMinimapFlag() {
		Minimap.minimapFlagX = -1;
		Minimap.minimapFlagY = -1;
		ComponentMinimap.minimapState = 0;
	}

	@OriginalMember(owner = "client!pda", name = "g", descriptor = "(I)V")
	public static void resetMinimapFlagAndPlane() {
		Minimap.minimapFlagX = -1;
		Minimap.minimapFlagY = -1;
		ComponentMinimap.MAP_PLANE = -1;
		ComponentMinimap.minimapState = 0;
	}

	@OriginalMember(owner = "client!ew", name = "b", descriptor = "(I)V")
	public static void prepareLights() {
		EnvironmentManager.toolkit.xa(((float) GamePreferences.uid.brightness.getValue() * 0.1F + 0.7F) * 1.1523438F);
		EnvironmentManager.toolkit.ZA(EnvironmentManager.defaultSunColour, 0.69921875F, 1.2F, -200.0F, -240.0F, -200.0F);
		EnvironmentManager.toolkit.L(EnvironmentManager.defaultFogColour, -1, 0);
		EnvironmentManager.toolkit.setSunCubeTexture(EnvironmentManager.aClass78_4);
	}
	@OriginalMember(owner = "client!ij", name = "a", descriptor = "(IIBI)Lclient!wm;")
	public static TcpPacket createMovePacket(@OriginalArg(0) int localY, @OriginalArg(1) int localX, @OriginalArg(3) int moveType) {
		@Pc(5) TcpPacket packet = null;
		if (moveType == 0) {
			packet = TcpPacket.createPacket(ClientProt.MOVE_GAMECLICK, TcpPacket.isaac);
		}
		if (moveType == 1) {
			packet = TcpPacket.createPacket(ClientProt.MOVE_MINIMAPCLICK, TcpPacket.isaac);
		}
		packet.buffer.writeShortLittle(localY + MapRegion.sceneBaseY);
		packet.buffer.writeByteAdd(KeyRecorder.keyRecorder.isPressed(82) ? 1 : 0);
		packet.buffer.writeShortAdd(MapRegion.sceneBaseX + localX);
		Minimap.minimapFlagX = localX;
		Minimap.minimapFlagY = localY;
		Minimap.minimapContainsFlag = false;
		PulseEvent.delayedMinimapFlagUpdate();
		return packet;
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(Lclient!se;I)Z")
	public static boolean isLocMapIconReady(@OriginalArg(0) GameObject location) {
		@Pc(10) LocType locType = TypeLists.LOC.list(location.getId());
		if (locType.mapIconId == -1) {
			return true;
		} else {
			@Pc(22) MapSpriteType msiType = TypeLists.renderer.list(locType.mapIconId);
			return msiType.fileId == -1 ? true : msiType.ready();
		}
	}

	@OriginalMember(owner = "client!vi", name = "s", descriptor = "I")
	public static int minimapRandomizationCycle = 0;

	@OriginalMember(owner = "client!ct", name = "E", descriptor = "I")
	public static int minimapRotationStep = 2;
}
