package com.jagex.game.runetek5.statics;

import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.config.staticelements.StaticElements;
import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.cache.node.MultiLocEntry;
import com.jagex.game.runetek5.config.flo.FloType;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.entity.WorldMapTile;
import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Queue;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.flo.FloTypeList;
import com.jagex.game.runetek5.config.flu.FluType;
import com.jagex.game.runetek5.config.flu.FluTypeList;
import com.jagex.game.runetek5.config.loctype.LocInteractivity;
import com.jagex.game.runetek5.config.loctype.LocTypeList;
import com.jagex.game.runetek5.config.mec.MECType;
import com.jagex.game.runetek5.config.mec.MECTypeList;
import com.jagex.game.runetek5.config.mapsprite.MapSpriteType;
import com.jagex.game.runetek5.config.mapsprite.MapSpriteTypeList;
import com.jagex.game.runetek5.vardomain.VarDomain;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapElement;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class WorldMapRenderer {

	@OriginalMember(owner = "client!kp", name = "m", descriptor = "F")
	public static float requestedMapZoom;

	@OriginalMember(owner = "client!kp", name = "w", descriptor = "I")
	public static int mapSizeX;

	@OriginalMember(owner = "client!kp", name = "E", descriptor = "I")
	public static int mapSizeY;

	@OriginalMember(owner = "client!kp", name = "n", descriptor = "I")
	public static int hueRandomisation = (int) (Math.random() * 11.0D) - 5;

	@OriginalMember(owner = "client!kp", name = "q", descriptor = "I")
	public static int lightnessRandomisation = (int) (Math.random() * 17.0D) - 8;

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;Lclient!lga;IIII)V")
	private static void positionElement(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) WorldMapCategory entry, @OriginalArg(2) int scaleX, @OriginalArg(3) int scaleZ) {
		entry.screenX = WorldMap.pixelViewStartX + (scaleX * (entry.x - WorldMap.tileViewStartX) >> 16);
		entry.screenY = WorldMap.pixelViewEndY - (scaleZ * (entry.z - WorldMap.tileViewStartY) >> 16);
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;IIII)Lclient!uh;")
	public static Deque positionElements(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int scaleX, @OriginalArg(2) int scaleZ) {
		for (@Pc(4) WorldMapCategory entry = (WorldMapCategory) MapList.worldMapCategories.first(); entry != null; entry = (WorldMapCategory) MapList.worldMapCategories.next()) {
			positionElement(toolkit, entry, scaleX, scaleZ);
		}
		return MapList.worldMapCategories;
	}

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "(Lclient!ha;IIII)V")
	public static void drawTiles(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int scaleX, @OriginalArg(2) int scaleZ) {
		@Pc(3) int tileColumns = WorldMap.tileViewEndX - WorldMap.tileViewStartX;
		@Pc(7) int tileRows = WorldMap.tileViewEndY - WorldMap.tileViewStartY;
		if (WorldMap.tileViewEndX < mapSizeX) {
			tileColumns++;
		}
		if (WorldMap.tileViewEndY < mapSizeY) {
			tileRows++;
		}
		@Pc(28) int local28;
		@Pc(40) int local40;
		@Pc(44) int local44;
		@Pc(50) int local50;
		@Pc(57) int local57;
		@Pc(70) int local70;
		@Pc(80) int local80;
		@Pc(84) int local84;
		@Pc(93) int local93;
		@Pc(173) int local173;
		@Pc(175) int local175;
		@Pc(177) int local177;
		@Pc(179) int local179;
		for (@Pc(17) int i = 0; i < tileColumns; i++) {
			int columnStartX = (scaleX * i >> 16) + WorldMap.pixelViewStartX;
			int columnEndX = (scaleX * (i + 1) >> 16) + WorldMap.pixelViewStartX;
			int columnWidth = columnEndX - columnStartX;
			if (columnWidth > 0) {
				int tileX = WorldMap.tileViewStartX + i;
				if (tileX >= 0 && tileX < mapSizeX) {
					for (int row = 0; row < tileRows; row++) {
						int rowTopY = WorldMap.pixelViewEndY - (scaleZ * (row + 1) >> 16);
						int rowBottomY = WorldMap.pixelViewEndY - (scaleZ * row >> 16);
						int rowHeight = rowBottomY - rowTopY;
						if (rowHeight > 0) {
							int tileY = row + WorldMap.tileViewStartY;
							int tileIndex = tileX + tileY * mapSizeX;
							int underlayColour = 0;
							int overlayId = 0;
							int locId = 0;
							if (tileY >= 0 && tileY < mapSizeY) {
								underlayColour = (WorldMap.underlayColoursHigh[tileIndex] & 0xFF) << 16 | WorldMap.underlayColoursLow[tileIndex] & 0xFFFF;
								if (underlayColour != 0) {
									underlayColour |= 0xFF000000;
								}
								overlayId = WorldMap.overlayIds[tileIndex] & 0xFF;
								locId = WorldMap.tileLocIds[tileIndex] & 0xFFFF;
							}
							if (underlayColour == 0 && overlayId == 0 && locId == 0) {
								if (WorldMap.area.color != -1) {
									underlayColour = WorldMap.area.color | 0xFF000000;
								} else if ((i + WorldMap.tileViewStartX & 0x4) == (row + WorldMap.tileViewEndY & 0x4)) {
									underlayColour = WorldMap.overlayFloorColors[MapList.FLOTYPE_LIST.lastLoadedOverlayId + 1];
								} else {
									underlayColour = -11840664;
								}
								if (underlayColour == 0) {
									underlayColour = -16777216;
								}
								toolkit.aa(columnStartX, rowTopY, columnWidth, rowHeight, underlayColour, 0);
							} else if (locId <= 0) {
								WorldMap.drawTile(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, underlayColour, overlayId, WorldMap.overlayShapes[tileIndex], null, null, true);
							} else if (locId == 65535) {
								@Pc(282) MultiLocEntry multiLocEntry = (MultiLocEntry) MapList.multiLocs.get((long) (tileX << 16 | tileY));
								if (multiLocEntry != null) {
									WorldMap.drawTile(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, underlayColour, overlayId, WorldMap.overlayShapes[tileIndex], multiLocEntry.locIds, multiLocEntry.locShapes, true);
								}
							} else {
								WorldMap.singleLocIds[0] = (short) (locId - 1);
								WorldMap.singleLocShapes[0] = WorldMap.tileLocShapes[tileIndex];
								WorldMap.drawTile(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, underlayColour, overlayId, WorldMap.overlayShapes[tileIndex], WorldMap.singleLocIds, WorldMap.singleLocShapes, true);
							}
						}
					}
				} else {
					for (int row = 0; row < tileRows; row++) {
						int rowTopY = WorldMap.pixelViewEndY - (scaleZ * (row + 1) >> 16);
						int rowBottomY = WorldMap.pixelViewEndY - (scaleZ * row >> 16);
						int rowHeight = rowBottomY - rowTopY;
						int fillColour;
						if (WorldMap.area.color != -1) {
							fillColour = WorldMap.area.color | 0xFF000000;
						} else if ((i + WorldMap.tileViewStartX & 0x4) == (row + WorldMap.tileViewEndY & 0x4)) {
							fillColour = WorldMap.overlayFloorColors[MapList.FLOTYPE_LIST.lastLoadedOverlayId + 1];
						} else {
							fillColour = -11840664;
						}
						if (fillColour == 0) {
							fillColour = -16777216;
						}
						toolkit.aa(columnStartX, rowTopY, columnWidth, rowHeight, fillColour, 0);
					}
				}
			}
		}
		for (int column = -16; column < tileColumns + 16; column++) {
			int columnStartX = (scaleX * column >> 16) + WorldMap.pixelViewStartX;
			int columnEndX = (scaleX * (column + 1) >> 16) + WorldMap.pixelViewStartX;
			int columnWidth = columnEndX - columnStartX;
			if (columnWidth > 0) {
				int tileX = column + WorldMap.tileViewStartX;
				if (tileX >= 0 && tileX < mapSizeX) {
					for (int row = -16; row < tileRows + 16; row++) {
						int rowTopY = WorldMap.pixelViewEndY - (scaleZ * (row + 1) >> 16);
						int rowBottomY = WorldMap.pixelViewEndY - (scaleZ * row >> 16);
						int rowHeight = rowBottomY - rowTopY;
						if (rowHeight > 0) {
							int tileY = row + WorldMap.tileViewStartY;
							if (tileY >= 0 && tileY < mapSizeY) {
								int locId = WorldMap.tileLocIds[tileX + tileY * mapSizeX] & 0xFFFF;
								if (locId <= 0) {
									MapList.drawMsiMultiple(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, null, null);
								} else if (locId == 65535) {
									@Pc(459) MultiLocEntry multiLocEntry = (MultiLocEntry) MapList.multiLocs.get((long) (tileX << 16 | tileY));
									if (multiLocEntry != null) {
										MapList.drawMsiMultiple(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, multiLocEntry.locIds, multiLocEntry.locShapes);
									}
								} else {
									WorldMap.singleLocIds[0] = (short) (locId - 1);
									WorldMap.singleLocShapes[0] = WorldMap.tileLocShapes[tileX + tileY * mapSizeX];
									MapList.drawMsiMultiple(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, WorldMap.singleLocIds, WorldMap.singleLocShapes);
								}
							}
						}
					}
				}
			}
		}
		int minSquareX = WorldMap.tileViewStartX >> 6;
		int minSquareY = WorldMap.tileViewStartY >> 6;
		if (minSquareX < 0) {
			minSquareX = 0;
		}
		if (minSquareY < 0) {
			minSquareY = 0;
		}
		int maxSquareX = WorldMap.tileViewEndX >> 6;
		int maxSquareY = WorldMap.tileViewEndY >> 6;
		if (maxSquareX >= MapList.tiles[0].length) {
			maxSquareX = MapList.tiles[0].length - 1;
		}
		if (maxSquareY >= MapList.tiles[0][0].length) {
			maxSquareY = MapList.tiles[0][0].length - 1;
		}
		for (int level = 0; level < 3; level++) {
			@Pc(641) int local641;
			@Pc(653) int local653;
			@Pc(665) int local665;
			@Pc(675) int local675;
			@Pc(631) int local631;
			for (int squareX = minSquareX; squareX <= maxSquareX; squareX++) {
				for (int squareY = minSquareY; squareY <= maxSquareY; squareY++) {
					@Pc(589) EntityNodeList linkedList = MapList.tiles[level][squareX][squareY];
					if (linkedList != null) {
						int squareBaseX = (squareX + (WorldMap.mapSquareAbsX >> 6)) * 64;
						int squareBaseY = (squareY + (WorldMap.mapSquareAbsY >> 6)) * 64;
						for (@Pc(612) WorldMapTile worldMapTile = (WorldMapTile) linkedList.getFirst(); worldMapTile != null; worldMapTile = (WorldMapTile) linkedList.getNext()) {
							int relTileX = squareBaseX + worldMapTile.x - WorldMap.mapSquareAbsX - WorldMap.tileViewStartX;
							int relTileY = squareBaseY + worldMapTile.z - WorldMap.mapSquareAbsY - WorldMap.tileViewStartY;
							int tileLeftX = (scaleX * relTileX >> 16) + WorldMap.pixelViewStartX;
							int tileRightX = (scaleX * (relTileX + 1) >> 16) + WorldMap.pixelViewStartX;
							int tileTopY = WorldMap.pixelViewEndY - (scaleZ * (relTileY + 1) >> 16);
							int tileBottomY = WorldMap.pixelViewEndY - (scaleZ * relTileY >> 16);
							WorldMap.drawTile(toolkit, tileLeftX, tileTopY, tileRightX - tileLeftX, tileBottomY - tileTopY, worldMapTile.underlayIdOrColour, worldMapTile.overlayId & 0xFF, worldMapTile.shape, worldMapTile.locIds, worldMapTile.locShapes, false);
						}
					}
				}
			}
			for (int squareX = minSquareX; squareX <= maxSquareX; squareX++) {
				for (int squareY = minSquareY; squareY <= maxSquareY; squareY++) {
					@Pc(727) EntityNodeList linkedList2 = MapList.tiles[level][squareX][squareY];
					if (linkedList2 != null) {
						int squareBaseX = (squareX + (WorldMap.mapSquareAbsX >> 6)) * 64;
						int squareBaseY = (squareY + (WorldMap.mapSquareAbsY >> 6)) * 64;
						for (@Pc(750) WorldMapTile worldMapTile2 = (WorldMapTile) linkedList2.getFirst(); worldMapTile2 != null; worldMapTile2 = (WorldMapTile) linkedList2.getNext()) {
							int relTileX = squareBaseX + worldMapTile2.x - WorldMap.mapSquareAbsX - WorldMap.tileViewStartX;
							int relTileY = squareBaseY + worldMapTile2.z - WorldMap.mapSquareAbsY - WorldMap.tileViewStartY;
							int tileLeftX = (scaleX * relTileX >> 16) + WorldMap.pixelViewStartX;
							int tileRightX = (scaleX * (relTileX + 1) >> 16) + WorldMap.pixelViewStartX;
							int tileTopY = WorldMap.pixelViewEndY - (scaleZ * (relTileY + 1) >> 16);
							@Pc(813) int tileBottomY = WorldMap.pixelViewEndY - (scaleZ * relTileY >> 16);
							MapList.drawMsiMultiple(toolkit, tileLeftX, tileTopY, tileRightX - tileLeftX, tileBottomY - tileTopY, worldMapTile2.locIds, worldMapTile2.locShapes);
						}
					}
				}
			}
		}
	}
	@OriginalMember(owner = "client!le", name = "b", descriptor = "(I)I")
	public static int getWorldMapZoom() {
		if ((double) WorldMapRenderer.requestedMapZoom == 3.0D) {
			return 37;
		} else if ((double) WorldMapRenderer.requestedMapZoom == 4.0D) {
			return 50;
		} else if ((double) WorldMapRenderer.requestedMapZoom == 6.0D) {
			return 75;
		} else if ((double) WorldMapRenderer.requestedMapZoom == 8.0D) {
			return 100;
		} else {
			return 200;
		}
	}
}
