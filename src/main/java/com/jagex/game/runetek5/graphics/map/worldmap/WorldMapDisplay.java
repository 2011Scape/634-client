package com.jagex.game.runetek5.graphics.map.worldmap;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.datastruct.key.DequeIterator;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.client.JSObjectManager;
import com.jagex.game.runetek5.client.ServerEnvironment;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.config.mec.MECType;

public final class WorldMapDisplay {
	@OriginalMember(owner = "client!ic", name = "t", descriptor = "I")
	public static int someMecId = -1;

	@OriginalMember(owner = "client!oda", name = "l", descriptor = "I")
	public static int sceneCameraX;

	@OriginalMember(owner = "client!ca", name = "b", descriptor = "Z")
	public static boolean isInteractive = false;

	@OriginalMember(owner = "client!vj", name = "Z", descriptor = "I")
	public static int highlightedCategoryId = -1;

	@OriginalMember(owner = "client!ph", name = "q", descriptor = "I")
	public static int pendingMapElementId;

	@OriginalMember(owner = "client!te", name = "b", descriptor = "I")
	public static int mapCenterY;

	private WorldMapDisplay() {
		/* empty */
	}
	@OriginalMember(owner = "client!iq", name = "m", descriptor = "I")
	public static int scrollTargetY = -1;
	@OriginalMember(owner = "client!wga", name = "bd", descriptor = "[Ljava/lang/String;")
	public static final String[] RENDERED_WORLDMAP_AREA_NAMELINES = new String[5];

	@OriginalMember(owner = "client!ct", name = "D", descriptor = "Lclient!eq;")
	public static final IterableHashTable hiddenCategoryIds = new IterableHashTable(8);

	@OriginalMember(owner = "client!ot", name = "d", descriptor = "Lclient!eq;")
	public static final IterableHashTable hiddenMecIds = new IterableHashTable(8);

	@OriginalMember(owner = "client!r", name = "w", descriptor = "Lclient!cga;")
	public static final DequeIterator categoryIterator = new DequeIterator();

	@OriginalMember(owner = "client!bg", name = "L", descriptor = "Z")
	public static boolean hideIcons = false;
	@OriginalMember(owner = "client!hc", name = "b", descriptor = "(I)V")
	public static void clearHiddenElements() {
		WorldMapDisplay.hiddenMecIds.clear();
		WorldMapDisplay.hiddenCategoryIds.clear();
	}
	@OriginalMember(owner = "client!eaa", name = "e", descriptor = "I")
	public static int worldMapRequestedScrollY = -1;

	@OriginalMember(owner = "client!rk", name = "y", descriptor = "I")
	public static int mapCenterX;
	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(BI)V")
	public static void flashWorldMapElementCategory(@OriginalArg(1) int someMecId) {
		WorldMapDisplay.someMecId = someMecId;
		JSObjectManager.highlightFlashCycles = 3;
		ServerEnvironment.flashCycleCounter = 100;
		WorldMapDisplay.highlightedCategoryId = -1;
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(III)V")
	public static void setWorldMapScrollTarget(@OriginalArg(0) int x, @OriginalArg(2) int y) {
		WorldMapDisplay.scrollTargetX = x - WorldMap.mapSquareAbsX;
		WorldMapDisplay.scrollTargetY = y - WorldMap.mapSquareAbsY;
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "(I)Lclient!dfa;")
	public static WorldMapElement getWorldMapArea() {
		return WorldMap.area;
	}

	@OriginalMember(owner = "client!ec", name = "a", descriptor = "(IIIBI)V")
	public static void markDestination(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) int x, @OriginalArg(4) int y) {
		@Pc(9) float aspectRatio = (float) WorldMapRenderer.mapSizeY / (float) WorldMapRenderer.mapSizeX;
		@Pc(11) int scaledWidth = width;
		@Pc(13) int scaledHeight = height;
		if (aspectRatio < 1.0F) {
			scaledHeight = (int) (aspectRatio * (float) width);
		} else {
			scaledWidth = (int) ((float) height / aspectRatio);
		}
		@Pc(40) int offsetX = x - (width - scaledWidth) / 2;
		@Pc(49) int offsetY = y - (height - scaledHeight) / 2;
		WorldMapDisplay.scrollTargetY = -1;
		WorldMapDisplay.mapCenterY = WorldMapRenderer.mapSizeY - WorldMapRenderer.mapSizeY * offsetY / scaledHeight;
		WorldMapDisplay.mapCenterX = WorldMapRenderer.mapSizeX * offsetX / scaledWidth;
		WorldMapDisplay.scrollTargetX = -1;
		WorldMapDisplay.clampMapCenter();
	}

	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(IB)V")
	public static void setWorldMapZoom(@OriginalArg(0) int zoom) {
		WorldMapDisplay.scrollTargetY = -1;
		if (zoom == 37) {
			WorldMapRenderer.requestedMapZoom = 3.0F;
		} else if (zoom == 50) {
			WorldMapRenderer.requestedMapZoom = 4.0F;
		} else if (zoom == 75) {
			WorldMapRenderer.requestedMapZoom = 6.0F;
		} else if (zoom == 100) {
			WorldMapRenderer.requestedMapZoom = 8.0F;
		} else if (zoom == 200) {
			WorldMapRenderer.requestedMapZoom = 16.0F;
		}
		WorldMapDisplay.scrollTargetY = -1;
	}
	@OriginalMember(owner = "client!uv", name = "a", descriptor = "(IB)V")
	public static void setMapCenterY(@OriginalArg(0) int mapCenterY) {
		WorldMapDisplay.scrollTargetY = -1;
		WorldMapDisplay.mapCenterY = mapCenterY;
		WorldMapDisplay.scrollTargetY = -1;
		WorldMapDisplay.clampMapCenter();
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(Lclient!ii;I)Z")
	public static boolean shouldDraw(@OriginalArg(0) MECType mapElementType) {
		if (mapElementType == null) {
			return false;
		} else if (!mapElementType.displayedOnWorldmap) {
			return false;
		} else if (!mapElementType.shouldDraw(MapList.PLAYER_VAR_PROVIDER)) {
			return false;
		} else if (WorldMapDisplay.hiddenMecIds.get((long) mapElementType.mectypeId) == null) {
			return WorldMapDisplay.hiddenCategoryIds.get((long) mapElementType.categoryId) == null;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!paa", name = "b", descriptor = "(BI)V")
	public static void setMapCenterX(@OriginalArg(1) int mapCenterX) {
		WorldMapDisplay.scrollTargetY = -1;
		WorldMapDisplay.scrollTargetX = -1;
		WorldMapDisplay.mapCenterX = mapCenterX;
		WorldMapDisplay.clampMapCenter();
	}

	@OriginalMember(owner = "client!np", name = "a", descriptor = "(II)V")
	public static void flashWorldMapElement(@OriginalArg(0) int highlightedCategoryId) {
		JSObjectManager.highlightFlashCycles = 3;
		ServerEnvironment.flashCycleCounter = 100;
		WorldMapDisplay.someMecId = -1;
		WorldMapDisplay.highlightedCategoryId = highlightedCategoryId;
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "I")
	public static int scrollTargetX = -1;
	@OriginalMember(owner = "client!il", name = "a", descriptor = "(Z)V")
	public static void clampMapCenter() {
		if (WorldMapDisplay.mapCenterX < 0) {
			WorldMapDisplay.mapCenterX = 0;
			WorldMapDisplay.scrollTargetX = -1;
			WorldMapDisplay.scrollTargetY = -1;
		}
		if (WorldMapDisplay.mapCenterX > WorldMapRenderer.mapSizeX) {
			WorldMapDisplay.mapCenterX = WorldMapRenderer.mapSizeX;
			WorldMapDisplay.scrollTargetY = -1;
			WorldMapDisplay.scrollTargetX = -1;
		}
		if (WorldMapDisplay.mapCenterY < 0) {
			WorldMapDisplay.scrollTargetY = -1;
			WorldMapDisplay.mapCenterY = 0;
			WorldMapDisplay.scrollTargetX = -1;
		}
		if (WorldMapDisplay.mapCenterY > WorldMapRenderer.mapSizeY) {
			WorldMapDisplay.mapCenterY = WorldMapRenderer.mapSizeY;
			WorldMapDisplay.scrollTargetX = -1;
			WorldMapDisplay.scrollTargetY = -1;
		}
	}
}
