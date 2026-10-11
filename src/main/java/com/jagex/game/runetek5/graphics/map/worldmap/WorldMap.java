package com.jagex.game.runetek5.graphics.map.worldmap;

import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.cache.node.MultiLocEntry;
import com.jagex.game.runetek5.entity.WorldMapTile;
import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.config.flu.FluType;
import com.jagex.game.runetek5.config.loctype.LocInteractivity;
import com.jagex.game.runetek5.config.mec.MECType;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapElement;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import com.jagex.game.runetek5.statics.WorldMapPolygonFill;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.JSObjectManager;
import com.jagex.game.runetek5.client.ServerEnvironment;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapRasterManager;
import com.jagex.game.runetek5.config.flo.FloTypeList;
import com.jagex.game.runetek5.config.flu.FluTypeList;
import com.jagex.game.runetek5.config.mapsprite.MapSpriteTypeList;
import com.jagex.game.runetek5.config.mec.MECTypeList;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.vardomain.VarDomain;
import com.jagex.game.runetek5.config.loctype.LocTypeList;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.config.flo.FloType;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.math.ColourUtils;

public final class WorldMap {

	@OriginalMember(owner = "client!lm", name = "z", descriptor = "I")
	public static int numInactivePlayerIndices = 0;

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(ILclient!ha;IIZI)V")
	public static void drawWorldmapOverview(@OriginalArg(0) int height, @OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int x, @OriginalArg(3) int width, @OriginalArg(5) int y) {
		toolkit.KA(x, y, width + x, y + height);
		toolkit.fillRect(width, x, y, height, -16777216);
		if (ComponentMinimap.mapAssetLoadingPercentage < 100) {
			return;
		}
		@Pc(33) float aspectRatio = (float) WorldMapRenderer.mapSizeY / (float) WorldMapRenderer.mapSizeX;
		@Pc(35) int scaledWidth = width;
		@Pc(37) int scaledHeight = height;
		if (aspectRatio < 1.0F) {
			scaledHeight = (int) ((float) width * aspectRatio);
		} else {
			scaledWidth = (int) ((float) height / aspectRatio);
		}
		@Pc(64) int adjustedY = y + (height - scaledHeight) / 2;
		@Pc(73) int adjustedX = x + (width - scaledWidth) / 2;
		if (ComponentMinimap.worldMapOverviewSprite == null || width != ComponentMinimap.worldMapOverviewSprite.getWidth() || height != ComponentMinimap.worldMapOverviewSprite.getHeight()) {
			WorldMap.setView(WorldMap.mapSquareAbsX, WorldMapRenderer.mapSizeY + WorldMap.mapSquareAbsY, WorldMap.mapSquareAbsX - -WorldMapRenderer.mapSizeX, WorldMap.mapSquareAbsY, adjustedX, adjustedY, scaledWidth + adjustedX, scaledHeight + adjustedY);
			MapList.drawTiles(toolkit);
			ComponentMinimap.worldMapOverviewSprite = toolkit.createNativeSprite(adjustedX, adjustedY, scaledWidth, scaledHeight, false);
		}
		ComponentMinimap.worldMapOverviewSprite.render(adjustedX, adjustedY);
		@Pc(125) int viewWidth = WorldMap.worldMapViewWidth * scaledWidth / WorldMapRenderer.mapSizeX;
		@Pc(131) int viewHeight = scaledHeight * WorldMap.worldMapViewHeight / WorldMapRenderer.mapSizeY;
		@Pc(140) int viewX = adjustedX + scaledWidth * Camera.worldMapViewMinX / WorldMapRenderer.mapSizeX;
		@Pc(164) int viewY = adjustedY + scaledHeight - viewHeight - scaledHeight * WorldMap.worldMapViewMinY / WorldMapRenderer.mapSizeY;
		@Pc(166) int viewColour = -1996554240;
		if (Game.STELLAR_DAWN == Game.CURRENT_GAME) {
			viewColour = -1996488705;
		}
		toolkit.aa(viewX, viewY, viewWidth, viewHeight, viewColour, 1);
		toolkit.outlineRect(viewX, viewY, viewWidth, viewHeight, viewColour, 0);
		if (JSObjectManager.highlightFlashCycles <= 0) {
			return;
		}
		@Pc(196) int flashAlpha;
		if (ServerEnvironment.flashCycleCounter <= 50) {
			flashAlpha = ServerEnvironment.flashCycleCounter * 5;
		} else {
			flashAlpha = 500 - ServerEnvironment.flashCycleCounter * 5;
		}
		for (@Pc(210) WorldMapCategory mapElementListEntry = (WorldMapCategory) MapList.worldMapCategories.first(); mapElementListEntry != null; mapElementListEntry = (WorldMapCategory) MapList.worldMapCategories.next()) {
			@Pc(218) MECType mapElementType = WorldMap.MECTYPE_LIST.list(mapElementListEntry.id);
			if (WorldMapDisplay.shouldDraw(mapElementType)) {
				@Pc(235) int markerX;
				@Pc(246) int markerY;
				if (WorldMapDisplay.highlightedCategoryId == mapElementListEntry.id) {
					markerX = adjustedX + scaledWidth * mapElementListEntry.x / WorldMapRenderer.mapSizeX;
					markerY = (WorldMapRenderer.mapSizeY - mapElementListEntry.z) * scaledHeight / WorldMapRenderer.mapSizeY + adjustedY;
					toolkit.fillRect(4, markerX - 2, markerY + -2, 4, flashAlpha << 24 | 0xFFFF00);
				} else if (WorldMapDisplay.someMecId != -1 && WorldMapDisplay.someMecId == mapElementType.categoryId) {
					markerX = adjustedX + mapElementListEntry.x * scaledWidth / WorldMapRenderer.mapSizeX;
					markerY = adjustedY + scaledHeight * (WorldMapRenderer.mapSizeY - mapElementListEntry.z) / WorldMapRenderer.mapSizeY;
					toolkit.fillRect(4, markerX - 2, markerY + -2, 4, flashAlpha << 24 | 0xFFFF00);
				}
			}
		}
	}

	@OriginalMember(owner = "client!kp", name = "B", descriptor = "[B")
	public static byte[] underlayColoursHigh;

	@OriginalMember(owner = "client!kp", name = "C", descriptor = "I")
	public static int tileViewEndX;

	@OriginalMember(owner = "client!kp", name = "F", descriptor = "I")
	public static int pixelViewEndY;

	@OriginalMember(owner = "client!kp", name = "H", descriptor = "[S")
	public static short[] underlayColoursLow;

	@OriginalMember(owner = "client!kp", name = "J", descriptor = "I")
	public static int pixelViewStartX;

	@OriginalMember(owner = "client!kp", name = "K", descriptor = "[B")
	public static byte[] underlayIds;

	@OriginalMember(owner = "client!kp", name = "L", descriptor = "I")
	public static int tileViewStartX;

	@OriginalMember(owner = "client!kp", name = "M", descriptor = "[B")
	public static byte[] overlayShapes;

	@OriginalMember(owner = "client!kp", name = "N", descriptor = "I")
	public static int screenX2;

	@OriginalMember(owner = "client!kp", name = "O", descriptor = "I")
	public static int tileViewEndY;

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(I)V")
	public static void setArea(@OriginalArg(0) int id) {
		WorldMap.area = (WorldMapElement) MapList.WORLDMAP_DEFS.get((long) id);
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(IIIIIIII)V")
	public static void setView(@OriginalArg(0) int x1, @OriginalArg(1) int z2, @OriginalArg(2) int x2, @OriginalArg(3) int z1, @OriginalArg(4) int left, @OriginalArg(5) int top, @OriginalArg(6) int right, @OriginalArg(7) int bottom) {
		tileViewStartX = x1 - WorldMap.mapSquareAbsX;
		tileViewEndY = z2 - WorldMap.mapSquareAbsY;
		tileViewEndX = x2 - WorldMap.mapSquareAbsX;
		tileViewStartY = z1 - WorldMap.mapSquareAbsY;
		pixelViewStartX = left;
		screenY1 = top;
		screenX2 = right;
		pixelViewEndY = bottom;
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;IIIIIII[S[BZ)V")
	public static void drawTile(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int width, @OriginalArg(4) int height, @OriginalArg(5) int underlayColour, @OriginalArg(6) int overlay, @OriginalArg(7) int tileShapeAndRotation, @OriginalArg(8) short[] locIds, @OriginalArg(9) byte[] locShapes, @OriginalArg(10) boolean opaque) {
		@Pc(20) int right;
		@Pc(32) int bottom;
		if (opaque || underlayColour != 0 || overlay > 0) {
			if (overlay == 0) {
				toolkit.aa(x, y, width, height, underlayColour, 0);
			} else {
				right = tileShapeAndRotation & 0x3F;
				if (right == 0 || width <= 1 || height <= 1) {
					bottom = overlayFloorColors[overlay];
					if (opaque || bottom != 0) {
						toolkit.aa(x, y, width, height, bottom, 0);
					}
				} else {
					bottom = opaque ? 0 : 1;
					WorldMap.drawTileShape(overlayFloorColors[overlay], underlayColour, toolkit, bottom, tileSize, tileShapeAndRotation >> 6 & 0x3, tileShapePatterns, width, x, right, height, y);
				}
			}
		}
		if (locIds == null) {
			return;
		}
		if (width == 1) {
			right = x;
		} else {
			right = x + width - 1;
		}
		if (height == 1) {
			bottom = y;
		} else {
			bottom = y + height - 1;
		}
		for (@Pc(100) int i = 0; i < locIds.length; i++) {
			@Pc(107) int shape = locShapes[i] & 0x3F;
			if (shape == 0 || shape == 2 || shape == 3 || shape == 9) {
				@Pc(127) LocType locType = MapList.LOCTYPE_LIST.list(locIds[i] & 0xFFFF);
				if (locType.mapIconId == -1) {
					@Pc(133) int wallColour = -3355444;
					if (locType.hasActions == LocInteractivity.INTERACTIVE) {
						wallColour = -3407872;
					}
					@Pc(147) int rotation = locShapes[i] >> 6 & 0x3;
					if (shape == 0) {
						if (rotation == 0) {
							toolkit.P(x, y, height, wallColour, 0);
						} else if (rotation == 1) {
							toolkit.U(x, y, width, wallColour, 0);
						} else if (rotation == 2) {
							toolkit.P(right, y, height, wallColour, 0);
						} else {
							toolkit.U(x, bottom, width, wallColour, 0);
						}
					} else if (shape == 2) {
						if (rotation == 0) {
							toolkit.P(x, y, height, -1, 0);
							toolkit.U(x, y, width, wallColour, 0);
						} else if (rotation == 1) {
							toolkit.P(right, y, height, -1, 0);
							toolkit.U(x, y, width, wallColour, 0);
						} else if (rotation == 2) {
							toolkit.P(right, y, height, -1, 0);
							toolkit.U(x, bottom, width, wallColour, 0);
						} else {
							toolkit.P(x, y, height, -1, 0);
							toolkit.U(x, bottom, width, wallColour, 0);
						}
					} else if (shape == 3) {
						if (rotation == 0) {
							toolkit.U(x, y, 1, wallColour, 0);
						} else if (rotation == 1) {
							toolkit.U(right, y, 1, wallColour, 0);
						} else if (rotation == 2) {
							toolkit.U(right, bottom, 1, wallColour, 0);
						} else {
							toolkit.U(x, bottom, 1, wallColour, 0);
						}
					} else if (shape == 9) {
						@Pc(313) int step;
						if (rotation == 0 || rotation == 2) {
							for (step = 0; step < height; step++) {
								toolkit.U(x + step, bottom - step, 1, wallColour, 0);
							}
						} else {
							for (step = 0; step < height; step++) {
								toolkit.U(x + step, y + step, 1, wallColour, 0);
							}
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;Lclient!lga;Lclient!ii;)V")
	public static void renderCategoryLines(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) WorldMapCategory category, @OriginalArg(2) MECType mecType) {
		if (mecType.offsets == null) {
			return;
		}
		@Pc(7) int[] polygonPoints = new int[mecType.offsets.length];
		@Pc(20) int local20;
		@Pc(32) int local32;
		for (@Pc(9) int i = 0; i < polygonPoints.length / 2; i++) {
			int tileX = mecType.offsets[i * 2] + category.x;
			int tileZ = mecType.offsets[i * 2 + 1] + category.z;
			polygonPoints[i * 2] = pixelViewStartX + (screenX2 - pixelViewStartX) * (tileX - tileViewStartX) / (tileViewEndX - tileViewStartX);
			polygonPoints[i * 2 + 1] = pixelViewEndY - (pixelViewEndY - screenY1) * (tileZ - tileViewStartY) / (tileViewEndY - tileViewStartY);
		}
		WorldMapRasterManager.fillLandmarkPolygon(toolkit, polygonPoints, mecType.lineColor);
		if (mecType.dashLineSpacing > 0) {
			@Pc(102) int startY;
			@Pc(110) int endX;
			@Pc(120) int endY;
			@Pc(125) int tmp;
			@Pc(127) int tmpY;
			for (int pointIndex = 0; pointIndex < polygonPoints.length / 2 - 1; pointIndex++) {
				int startX = polygonPoints[pointIndex * 2];
				startY = polygonPoints[pointIndex * 2 + 1];
				endX = polygonPoints[(pointIndex + 1) * 2];
				endY = polygonPoints[(pointIndex + 1) * 2 + 1];
				if (endX < startX) {
					tmp = startX;
					tmpY = startY;
					startX = endX;
					startY = endY;
					endX = tmp;
					endY = tmpY;
				} else if (endX == startX && endY < startY) {
					tmp = startY;
					startY = endY;
					endY = tmp;
				}
				toolkit.drawLine(startX, startY, endX, endY, mecType.colors[mecType.colorPointers[pointIndex] & 0xFF], mecType.dashLineSpacing, mecType.dashLineLength, mecType.dashLineOffset);
			}
			int startX = polygonPoints[polygonPoints.length - 2];
			startY = polygonPoints[polygonPoints.length - 1];
			endX = polygonPoints[0];
			endY = polygonPoints[1];
			if (endX < startX) {
				tmp = startX;
				tmpY = startY;
				startX = endX;
				startY = endY;
				endX = tmp;
				endY = tmpY;
			} else if (endX == startX && endY < startY) {
				tmp = startY;
				startY = endY;
				endY = tmp;
			}
			toolkit.drawLine(startX, startY, endX, endY, mecType.colors[mecType.colorPointers[mecType.colorPointers.length - 1] & 0xFF], mecType.dashLineSpacing, mecType.dashLineLength, mecType.dashLineOffset);
			return;
		}
		for (int pointIndex = 0; pointIndex < polygonPoints.length / 2 - 1; pointIndex++) {
			toolkit.drawOpaqueLine(polygonPoints[pointIndex * 2 + 1], polygonPoints[pointIndex * 2], polygonPoints[(pointIndex + 1) * 2], mecType.colors[mecType.colorPointers[pointIndex] & 0xFF], polygonPoints[(pointIndex + 1) * 2 + 1]);
		}
		toolkit.drawOpaqueLine(polygonPoints[polygonPoints.length - 1], polygonPoints[polygonPoints.length - 2], polygonPoints[0], mecType.colors[mecType.colorPointers[mecType.colorPointers.length - 1] & 0xFF], polygonPoints[1]);
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "([B[B[SII)V")
	public static void decodeUnderlayColors(@OriginalArg(0) byte[] tileUnderlayIds, @OriginalArg(1) byte[] tileRed, @OriginalArg(2) short[] tileGreenBlue, @OriginalArg(3) int hueOffset, @OriginalArg(4) int lightnessOffset) {
		@Pc(2) int[] underlayHue = new int[WorldMapRenderer.mapSizeY];
		@Pc(5) int[] underlaySaturation = new int[WorldMapRenderer.mapSizeY];
		@Pc(8) int[] underlayLightness = new int[WorldMapRenderer.mapSizeY];
		@Pc(11) int[] underlayDivisor = new int[WorldMapRenderer.mapSizeY];
		@Pc(14) int[] underlayTileCount = new int[WorldMapRenderer.mapSizeY];
		for (@Pc(16) int i = -5; i < WorldMapRenderer.mapSizeX; i++) {
			@Pc(21) int positiveX = i + 5;
			@Pc(25) int negativeX = i - 5;
			@Pc(41) int local41;
			for (@Pc(27) int j = 0; j < WorldMapRenderer.mapSizeY; j++) {
				@Pc(50) FluType floorUnderlayType;
				@Pc(86) int previousCount;
				if (positiveX < WorldMapRenderer.mapSizeX) {
					int underlayId = tileUnderlayIds[positiveX + j * WorldMapRenderer.mapSizeX] & 0xFF;
					if (underlayId > 0) {
						floorUnderlayType = MapList.FLUTYPE_LIST.list(underlayId - 1);
						underlayHue[j] += floorUnderlayType.hue;
						underlaySaturation[j] += floorUnderlayType.saturation;
						underlayLightness[j] += floorUnderlayType.lightness;
						underlayDivisor[j] += floorUnderlayType.hueMultiplier;
						previousCount = underlayTileCount[j]++;
					}
				}
				if (negativeX >= 0) {
					int underlayId = tileUnderlayIds[negativeX + j * WorldMapRenderer.mapSizeX] & 0xFF;
					if (underlayId > 0) {
						floorUnderlayType = MapList.FLUTYPE_LIST.list(underlayId - 1);
						underlayHue[j] -= floorUnderlayType.hue;
						underlaySaturation[j] -= floorUnderlayType.saturation;
						underlayLightness[j] -= floorUnderlayType.lightness;
						underlayDivisor[j] -= floorUnderlayType.hueMultiplier;
						previousCount = underlayTileCount[j]--;
					}
				}
			}
			if (i >= 0) {
				int hue = 0;
				@Pc(159) int saturation = 0;
				@Pc(161) int lightness = 0;
				@Pc(163) int divisor = 0;
				@Pc(165) int tileCount = 0;
				for (@Pc(167) int k = -5; k < WorldMapRenderer.mapSizeY; k++) {
					@Pc(172) int positiveY = k + 5;
					if (positiveY < WorldMapRenderer.mapSizeY) {
						hue += underlayHue[positiveY];
						saturation += underlaySaturation[positiveY];
						lightness += underlayLightness[positiveY];
						divisor += underlayDivisor[positiveY];
						tileCount += underlayTileCount[positiveY];
					}
					@Pc(209) int negativeY = k - 5;
					if (negativeY >= 0) {
						hue -= underlayHue[negativeY];
						saturation -= underlaySaturation[negativeY];
						lightness -= underlayLightness[negativeY];
						divisor -= underlayDivisor[negativeY];
						tileCount -= underlayTileCount[negativeY];
					}
					if (k >= 0 && tileCount > 0) {
						@Pc(261) int local261;
						if ((tileUnderlayIds[i + k * WorldMapRenderer.mapSizeX] & 0xFF) == 0) {
							int emptyTileIndex = i + k * WorldMapRenderer.mapSizeX;
							tileRed[emptyTileIndex] = 0;
							tileGreenBlue[emptyTileIndex] = 0;
						} else {
							int hsl = divisor == 0 ? 0 : ColourUtils.packHsl(lightness / tileCount, saturation / tileCount, hue * 256 / divisor);
							@Pc(294) int clampedLightness = (hsl & 0x7F) + lightnessOffset;
							if (clampedLightness < 0) {
								clampedLightness = 0;
							} else if (clampedLightness > 127) {
								clampedLightness = 127;
							}
							@Pc(316) int modifiedHsl = (hsl + hueOffset & 0xFC00) + (hsl & 0x380) + clampedLightness;
							@Pc(322) int tileIndex = i + k * WorldMapRenderer.mapSizeX;
							@Pc(333) int rgb = ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(ColorUtil.scaleHslLightness(modifiedHsl)) & 0xFFFF];
							tileRed[tileIndex] = (byte) (rgb >> 16 & 0xFF);
							tileGreenBlue[tileIndex] = (short) (rgb & 0xFFFF);
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "()V")
	public static void freeBuffers() {
		underlayIds = null;
		underlayColoursHigh = null;
		underlayColoursLow = null;
		overlayIds = null;
		overlayShapes = null;
		tileLocIds = null;
		tileLocShapes = null;
		MapList.multiLocs = null;
		MapList.tiles = null;
		overlayFloorColors = null;
	}

	@OriginalMember(owner = "client!kp", name = "c", descriptor = "()V")
	public static void buildMapCategories() {
		@Pc(4) int z;
		@Pc(15) int locId;
		@Pc(49) int mapelement;
		for (@Pc(1) int x = 0; x < WorldMapRenderer.mapSizeX; x++) {
			for (z = 0; z < WorldMapRenderer.mapSizeY; z++) {
				locId = tileLocIds[x + z * WorldMapRenderer.mapSizeX] & 0xFFFF;
				if (locId != 0) {
					@Pc(35) int locIndex;
					if (locId == 65535) {
						@Pc(31) MultiLocEntry locList = (MultiLocEntry) MapList.multiLocs.get((long) (x << 16 | z));
						if (locList != null) {
							for (locIndex = 0; locIndex < locList.locIds.length; locIndex++) {
								@Pc(46) LocType locType = MapList.LOCTYPE_LIST.list(locList.locIds[locIndex] & 0xFFFF);
								mapelement = locType.mapCategoryid;
								if (locType.alternativeIds != null) {
									locType = locType.getMultiLoc(MapList.PLAYER_VAR_PROVIDER);
									if (locType != null) {
										mapelement = locType.mapCategoryid;
									}
								}
								if (mapelement != -1) {
									@Pc(70) WorldMapCategory element = new WorldMapCategory(mapelement);
									element.x = x;
									element.z = z;
									MapList.worldMapCategories.addLast(element);
								}
							}
						}
					} else {
						@Pc(94) LocType locType2 = MapList.LOCTYPE_LIST.list(locId - 1);
						locIndex = locType2.mapCategoryid;
						if (locType2.alternativeIds != null) {
							locType2 = locType2.getMultiLoc(MapList.PLAYER_VAR_PROVIDER);
							if (locType2 != null) {
								locIndex = locType2.mapCategoryid;
							}
						}
						if (locIndex != -1) {
							@Pc(118) WorldMapCategory mapElementListEntry = new WorldMapCategory(locIndex);
							mapElementListEntry.x = x;
							mapElementListEntry.z = z;
							MapList.worldMapCategories.addLast(mapElementListEntry);
						}
					}
				}
			}
		}
		for (z = 0; z < 3; z++) {
			for (locId = 0; locId < MapList.tiles[0].length; locId++) {
				for (@Pc(144) int chunkZ = 0; chunkZ < MapList.tiles[0][0].length; chunkZ++) {
					@Pc(153) EntityNodeList chunkTiles = MapList.tiles[z][locId][chunkZ];
					if (chunkTiles != null) {
						for (@Pc(160) WorldMapTile tile = (WorldMapTile) chunkTiles.getFirst(); tile != null; tile = (WorldMapTile) chunkTiles.getNext()) {
							if (tile.locIds != null) {
								for (mapelement = 0; mapelement < tile.locIds.length; mapelement++) {
									@Pc(177) LocType locType3 = MapList.LOCTYPE_LIST.list(tile.locIds[mapelement] & 0xFFFF);
									@Pc(180) int elementId = locType3.mapCategoryid;
									if (locType3.alternativeIds != null) {
										locType3 = locType3.getMultiLoc(MapList.PLAYER_VAR_PROVIDER);
										if (locType3 != null) {
											elementId = locType3.mapCategoryid;
										}
									}
									if (elementId != -1) {
										@Pc(201) WorldMapCategory mapElementListEntry2 = new WorldMapCategory(elementId);
										mapElementListEntry2.x = (locId + (WorldMap.mapSquareAbsX >> 6)) * 64 + tile.x - WorldMap.mapSquareAbsX;
										mapElementListEntry2.z = (chunkZ + (WorldMap.mapSquareAbsY >> 6)) * 64 + tile.z - WorldMap.mapSquareAbsY;
										MapList.worldMapCategories.addLast(mapElementListEntry2);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!kp", name = "d", descriptor = "()V")
	public static void allocateBuffers() {
		underlayIds = new byte[WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY];
		overlayIds = new byte[WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY];
		overlayShapes = new byte[WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY];
		tileLocIds = new short[WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY];
		tileLocShapes = new byte[WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY];
		MapList.multiLocs = new IterableHashTable(1024);
		MapList.tiles = new EntityNodeList[3][WorldMapRenderer.mapSizeX >> 6][WorldMapRenderer.mapSizeY >> 6];
		overlayFloorColors = new int[MapList.FLOTYPE_LIST.totalFileCount + 1];
	}

	@OriginalMember(owner = "client!kp", name = "l", descriptor = "[B")
	public static final byte[] singleLocShapes = new byte[1];

	@OriginalMember(owner = "client!kp", name = "o", descriptor = "[[[B")
	public static byte[][][] tileShapePatterns;

	@OriginalMember(owner = "client!kp", name = "p", descriptor = "[S")
	public static final short[] singleLocIds = new short[1];

	@OriginalMember(owner = "client!kp", name = "s", descriptor = "I")
	public static int tileSize;

	@OriginalMember(owner = "client!kp", name = "t", descriptor = "[B")
	public static byte[] overlayIds;

	@OriginalMember(owner = "client!kp", name = "u", descriptor = "I")
	public static int tileViewStartY;

	@OriginalMember(owner = "client!kp", name = "v", descriptor = "[B")
	public static byte[] tileLocShapes;

	@OriginalMember(owner = "client!kp", name = "x", descriptor = "[I")
	public static int[] overlayFloorColors;

	@OriginalMember(owner = "client!kp", name = "y", descriptor = "[S")
	public static short[] tileLocIds;

	@OriginalMember(owner = "client!kp", name = "z", descriptor = "I")
	public static int screenY1;
	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!d;II)V")
	public static void initOverlayColours(@OriginalArg(0) MaterialProvider textureSource, @OriginalArg(1) int hueOffset, @OriginalArg(2) int lightnessOffset) {
		for (@Pc(1) int i = 0; i < MapList.FLOTYPE_LIST.totalFileCount; i++) {
			WorldMap.overlayFloorColors[i + 1] = overlayColour(textureSource, i, hueOffset, lightnessOffset);
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!d;III)I")
	public static int overlayColour(@OriginalArg(0) MaterialProvider textureSource, @OriginalArg(1) int id, @OriginalArg(2) int hueOffset, @OriginalArg(3) int lightnessOffset) {
		@Pc(4) FloType spotAnimType = MapList.FLOTYPE_LIST.list(id);
		if (spotAnimType == null) {
			return 0;
		}
		@Pc(11) int textureId = spotAnimType.texture;
		if (textureId >= 0 && textureSource.getMetrics(textureId).detailsOnly) {
			textureId = -1;
		}
		@Pc(68) int floorColor;
		@Pc(27) int colorHsl;
		@Pc(33) int lightness;
		@Pc(55) int modifiedHsl;
		if (spotAnimType.minimapColorHsl >= 0) {
			colorHsl = spotAnimType.minimapColorHsl;
			lightness = (colorHsl & 0x7F) + lightnessOffset;
			if (lightness < 0) {
				lightness = 0;
			} else if (lightness > 127) {
				lightness = 127;
			}
			modifiedHsl = (colorHsl + hueOffset & 0xFC00) + (colorHsl & 0x380) + lightness;
			floorColor = ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(WorldMap.scaleOverlayLightness(modifiedHsl)) & 0xFFFF] | 0xFF000000;
		} else if (textureId >= 0) {
			floorColor = ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(WorldMap.scaleOverlayLightness(textureSource.getMetrics(textureId).colorHsl)) & 0xFFFF] | 0xFF000000;
		} else if (spotAnimType.tileColorHsl == -1) {
			floorColor = 0;
		} else {
			colorHsl = spotAnimType.tileColorHsl;
			lightness = (colorHsl & 0x7F) + lightnessOffset;
			if (lightness < 0) {
				lightness = 0;
			} else if (lightness > 127) {
				lightness = 127;
			}
			modifiedHsl = (colorHsl + hueOffset & 0xFC00) + (colorHsl & 0x380) + lightness;
			floorColor = ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(WorldMap.scaleOverlayLightness(modifiedHsl)) & 0xFFFF] | 0xFF000000;
		}
		return floorColor;
	}

	@OriginalMember(owner = "client!kp", name = "g", descriptor = "Lclient!dfa;")
	public static WorldMapElement area;
	@OriginalMember(owner = "client!kp", name = "b", descriptor = "Lclient!on;")
	public static MECTypeList MECTYPE_LIST;

	@OriginalMember(owner = "client!kp", name = "A", descriptor = "I")
	public static int mapSquareAbsY;

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!in;Lclient!cb;Lclient!qe;Lclient!uha;Lclient!on;Lclient!oda;Lclient!dd;)V")
	public static void initialize(@OriginalArg(0) Js5ResourceProvider mapAreaResource, @OriginalArg(1) FloTypeList floorOverlayTypeList, @OriginalArg(2) FluTypeList floorUnderlayTypeList, @OriginalArg(3) LocTypeList locTypeList, @OriginalArg(4) MECTypeList mapElementTypeList, @OriginalArg(5) MapSpriteTypeList msiTypeList, @OriginalArg(6) VarDomain varDomain) {
		MapList.MAP_AREA_RESOURCE = mapAreaResource;
		MapList.FLOTYPE_LIST = floorOverlayTypeList;
		MapList.FLUTYPE_LIST = floorUnderlayTypeList;
		MapList.LOCTYPE_LIST = locTypeList;
		MECTYPE_LIST = mapElementTypeList;
		MapList.MAP_SPRITE_TYPE_LIST = msiTypeList;
		MapList.PLAYER_VAR_PROVIDER = varDomain;
		MapList.WORLDMAP_DEFS.clear();
		@Pc(21) int groupId = MapList.MAP_AREA_RESOURCE.getGroupId("details");
		@Pc(26) int[] fileIds = MapList.MAP_AREA_RESOURCE.getFileIds(groupId);
		if (fileIds != null) {
			for (@Pc(30) int i = 0; i < fileIds.length; i++) {
				@Pc(39) WorldMapElement worldMapArea = WorldMapElement.list(fileIds[i], MapList.MAP_AREA_RESOURCE, groupId);
				MapList.WORLDMAP_DEFS.put((long) worldMapArea.elementId, worldMapArea);
			}
		}
		ColorUtil.initColorTables(true, false);
	}
	@OriginalMember(owner = "client!kp", name = "k", descriptor = "F")
	public static float mapZoom;

	@OriginalMember(owner = "client!kp", name = "D", descriptor = "I")
	public static int mapSquareAbsX;

	@OriginalMember(owner = "client!bn", name = "b", descriptor = "Lclient!at;")
	public static Component worldmapComponent;
	@OriginalMember(owner = "client!aw", name = "I", descriptor = "Lclient!ej;")
	public static final ClientPacket RESUME_P_OBJDIALOG = new ClientPacket(28, 2);
	@OriginalMember(owner = "client!eq", name = "b", descriptor = "(I)Lclient!lga;")
	public static WorldMapCategory getNextActiveCategory() {
		if (MapList.worldMapCategories == null || WorldMapDisplay.categoryIterator == null) {
			return null;
		}
		for (@Pc(21) WorldMapCategory mapElementListEntry = (WorldMapCategory) WorldMapDisplay.categoryIterator.next(); mapElementListEntry != null; mapElementListEntry = (WorldMapCategory) WorldMapDisplay.categoryIterator.next()) {
			@Pc(29) MECType mapElementType = WorldMap.MECTYPE_LIST.list(mapElementListEntry.id);
			if (mapElementType != null && mapElementType.visible && mapElementType.shouldDraw(MapList.PLAYER_VAR_PROVIDER)) {
				return mapElementListEntry;
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(IILclient!ha;IIII[[[BIIIII)V")
	public static void drawTileShape(@OriginalArg(0) int overlayColour, @OriginalArg(1) int underlayColour, @OriginalArg(2) Toolkit toolkit, @OriginalArg(4) int blendMode, @OriginalArg(5) int tileSize, @OriginalArg(6) int rotation, @OriginalArg(7) byte[][][] shapePatterns, @OriginalArg(8) int width, @OriginalArg(9) int x, @OriginalArg(10) int shape, @OriginalArg(11) int height, @OriginalArg(12) int y) {
		if (shape == 0 || tileSize == 0) {
			return;
		}
		if (shape == 9) {
			shape = 1;
			rotation = rotation + 1 & 0x3;
		}
		if (shape == 10) {
			rotation = rotation + 3 & 0x3;
			shape = 1;
		}
		if (shape == 11) {
			rotation = rotation + 3 & 0x3;
			shape = 8;
		}
		toolkit.Q(x, y, width, height, underlayColour, overlayColour, shapePatterns[shape - 1][rotation], tileSize, blendMode);
	}
	@OriginalMember(owner = "client!as", name = "a", descriptor = "(III)I")
	public static int scaleOverlayLightness(@OriginalArg(2) int hsl) {
		if (hsl == -2) {
			return 12345678;
		} else if (hsl == -1) {
			return 96;
		} else {
			@Pc(42) int lightness = (hsl & 0x7F) * 96 >> 7;
			if (lightness < 2) {
				lightness = 2;
			} else if (lightness > 126) {
				lightness = 126;
			}
			return lightness + (hsl & 0xFF80);
		}
	}

	@OriginalMember(owner = "client!tq", name = "d", descriptor = "(I)Lclient!lga;")
	public static WorldMapCategory getFirstWorldMapCategory() {
		if (MapList.worldMapCategories == null || WorldMapDisplay.categoryIterator == null) {
			return null;
		}
		WorldMapDisplay.categoryIterator.setDeque(MapList.worldMapCategories);
		@Pc(18) WorldMapCategory mapElementListEntry = (WorldMapCategory) WorldMapDisplay.categoryIterator.first();
		if (mapElementListEntry == null) {
			return null;
		} else {
			@Pc(28) MECType mapElementType = WorldMap.MECTYPE_LIST.list(mapElementListEntry.id);
			return mapElementType != null && mapElementType.visible && mapElementType.shouldDraw(MapList.PLAYER_VAR_PROVIDER) ? mapElementListEntry : WorldMap.getNextActiveCategory();
		}
	}
	@OriginalMember(owner = "client!daa", name = "a", descriptor = "(II)[[[B")
	public static byte[][][] createTileShapePatterns(@OriginalArg(0) int tileSize) {
		@Pc(9) byte[][][] patterns = new byte[8][4][];
		@Pc(18) byte[] mask = new byte[tileSize * tileSize];
		@Pc(20) int index = 0;
		@Pc(26) int local26;
		for (@Pc(22) int i = 0; i < tileSize; i++) {
			for (int column = 0; column < tileSize; column++) {
				if (i >= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[0][0] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(77) int local77;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = 0; column < tileSize; column++) {
				if (column <= row) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[0][1] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(117) int local117;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (row <= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[0][2] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(165) int local165;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = 0; column < tileSize; column++) {
				if (column >= row) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[0][3] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(206) int local206;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = 0; column < tileSize; column++) {
				if (row >> 1 >= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[1][0] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(251) int local251;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (index >= 0 && index < mask.length) {
					if (column >= row << 1) {
						mask[index] = -1;
					}
					index++;
				} else {
					index++;
				}
			}
		}
		patterns[1][1] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(305) int local305;
		for (int row = 0; row < tileSize; row++) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (column <= row >> 1) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[1][2] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(347) int local347;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (row << 1 <= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[1][3] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(394) int local394;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (row >> 1 >= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[2][0] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(436) int local436;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = 0; column < tileSize; column++) {
				if (column >= row << 1) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[2][1] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(482) int local482;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (column <= row >> 1) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[2][2] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(531) int local531;
		for (int row = 0; row < tileSize; row++) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (column >= row << 1) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[2][3] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(575) int local575;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = 0; column < tileSize; column++) {
				if (column >= row >> 1) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[3][0] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(624) int local624;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (column <= row << 1) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[3][1] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(673) int local673;
		for (int row = 0; row < tileSize; row++) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (column >= row >> 1) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[3][2] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(719) int local719;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (column <= row << 1) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[3][3] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(766) int local766;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (row >> 1 <= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[4][0] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(808) int local808;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = 0; column < tileSize; column++) {
				if (row << 1 >= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[4][1] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(854) int local854;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (row >> 1 <= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[4][2] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(895) int local895;
		for (int row = 0; row < tileSize; row++) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (row << 1 >= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[4][3] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(940) int local940;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (tileSize / 2 >= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[5][0] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(979) int local979;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (row <= tileSize / 2) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[5][1] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(1022) int local1022;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (column >= tileSize / 2) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[5][2] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(1065) int local1065;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (row >= tileSize / 2) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[5][3] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(1108) int local1108;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (row - tileSize / 2 >= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[6][0] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(1155) int local1155;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = 0; column < tileSize; column++) {
				if (column <= row - tileSize / 2) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[6][1] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(1203) int local1203;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (row - tileSize / 2 >= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[6][2] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(1249) int local1249;
		for (int row = 0; row < tileSize; row++) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (row - tileSize / 2 >= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[6][3] = mask;
		mask = new byte[tileSize * tileSize];
		index = 0;
		@Pc(1297) int local1297;
		for (int row = 0; row < tileSize; row++) {
			for (int column = 0; column < tileSize; column++) {
				if (column >= row - tileSize / 2) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[7][0] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(1349) int local1349;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = 0; column < tileSize; column++) {
				if (row - tileSize / 2 <= column) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[7][1] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		@Pc(1401) int local1401;
		for (int row = tileSize - 1; row >= 0; row--) {
			for (int column = tileSize - 1; column >= 0; column--) {
				if (column >= row - tileSize / 2) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[7][2] = mask;
		index = 0;
		mask = new byte[tileSize * tileSize];
		for (int row = 0; row < tileSize; row++) {
			for (@Pc(1450) int j = tileSize - 1; j >= 0; j--) {
				if (j >= row - tileSize / 2) {
					mask[index] = -1;
				}
				index++;
			}
		}
		patterns[7][3] = mask;
		return patterns;
	}

	@OriginalMember(owner = "client!hq", name = "k", descriptor = "I")
	public static int worldMapViewWidth;

	@OriginalMember(owner = "client!sv", name = "U", descriptor = "I")
	public static int worldMapViewMinY;

	@OriginalMember(owner = "client!ed", name = "n", descriptor = "I")
	public static int dragStartCenterY;

	@OriginalMember(owner = "client!qe", name = "h", descriptor = "I")
	public static int dragStartCenterX;
	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(ZIII)I")
	public static int findNearestWorldMapElement(@OriginalArg(1) int categoryId, @OriginalArg(2) int x, @OriginalArg(3) int y) {
		if (ComponentMinimap.mapAssetLoadingPercentage < 100) {
			return -2;
		}
		@Pc(23) int bestCoord = -2;
		@Pc(25) int bestDistance = Integer.MAX_VALUE;
		@Pc(29) int relativeX = x - WorldMap.mapSquareAbsX;
		@Pc(33) int relativeY = y - WorldMap.mapSquareAbsY;
		for (@Pc(38) WorldMapCategory mapElementListEntry = (WorldMapCategory) MapList.worldMapCategories.first(); mapElementListEntry != null; mapElementListEntry = (WorldMapCategory) MapList.worldMapCategories.next()) {
			if (mapElementListEntry.id == categoryId) {
				@Pc(47) int elementX = mapElementListEntry.x;
				@Pc(50) int elementZ = mapElementListEntry.z;
				@Pc(61) int packedCoord = elementZ + WorldMap.mapSquareAbsY | WorldMap.mapSquareAbsX + elementX << 14;
				@Pc(80) int distanceSquared = (relativeY - elementZ) * (-elementZ + relativeY) + (relativeX - elementX) * (-elementX + relativeX);
				if (bestCoord < 0 || distanceSquared < bestDistance) {
					bestDistance = distanceSquared;
					bestCoord = packedCoord;
				}
			}
		}
		return bestCoord;
	}

	@OriginalMember(owner = "client!qca", name = "b", descriptor = "I")
	public static int worldMapViewHeight;

	@OriginalMember(owner = "client!mt", name = "f", descriptor = "I")
	public static int savedToolkitId = -1;

	@OriginalMember(owner = "client!mj", name = "o", descriptor = "Lclient!dfa;")
	public static WorldMapElement aClass2_Sub6_Sub4_3;
	@OriginalMember(owner = "client!sr", name = "b", descriptor = "Z")
	public static boolean worldMapTeleportPending = false;

	@OriginalMember(owner = "client!jf", name = "o", descriptor = "Z")
	public static boolean worldMapDragged = false;
}
