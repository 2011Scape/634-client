package com.jagex.game.runetek5.graphics.map.worldmap

import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.client.JSObjectManager
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.config.mec.MECType
import com.jagex.game.runetek5.core.datastruct.key.DequeIterator
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object WorldMapDisplay {
	@OriginalMember(owner = "client!ic", name = "t", descriptor = "I")
	@JvmField
	var someMecId: Int = -1

	@OriginalMember(owner = "client!oda", name = "l", descriptor = "I")
	@JvmField
	var sceneCameraX: Int = 0

	@OriginalMember(owner = "client!ca", name = "b", descriptor = "Z")
	@JvmField
	var isInteractive: Boolean = false

	@OriginalMember(owner = "client!vj", name = "Z", descriptor = "I")
	@JvmField
	var highlightedCategoryId: Int = -1

	@OriginalMember(owner = "client!ph", name = "q", descriptor = "I")
	@JvmField
	var pendingMapElementId: Int = 0

	@OriginalMember(owner = "client!te", name = "b", descriptor = "I")
	@JvmField
	var mapCenterY: Int = 0

	@OriginalMember(owner = "client!iq", name = "m", descriptor = "I")
	@JvmField
	var scrollTargetY: Int = -1

	@OriginalMember(owner = "client!wga", name = "bd", descriptor = "[Ljava/lang/String;")
	@JvmField
	val RENDERED_WORLDMAP_AREA_NAMELINES: Array<String?> = arrayOfNulls<String>(5)

	@OriginalMember(owner = "client!ct", name = "D", descriptor = "Lclient!eq;")
	@JvmField
	val hiddenCategoryIds: IterableHashTable = IterableHashTable(8)

	@OriginalMember(owner = "client!ot", name = "d", descriptor = "Lclient!eq;")
	@JvmField
	val hiddenMecIds: IterableHashTable = IterableHashTable(8)

	@OriginalMember(owner = "client!r", name = "w", descriptor = "Lclient!cga;")
	@JvmField
	val categoryIterator: DequeIterator = DequeIterator()

	@OriginalMember(owner = "client!bg", name = "L", descriptor = "Z")
	@JvmField
	var hideIcons: Boolean = false

	@OriginalMember(owner = "client!hc", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun clearHiddenElements() {
		WorldMapDisplay.hiddenMecIds.clear()
		WorldMapDisplay.hiddenCategoryIds.clear()
	}

	@OriginalMember(owner = "client!eaa", name = "e", descriptor = "I")
	@JvmField
	var worldMapRequestedScrollY: Int = -1

	@OriginalMember(owner = "client!rk", name = "y", descriptor = "I")
	@JvmField
	var mapCenterX: Int = 0

	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(BI)V")
	@JvmStatic
	fun flashWorldMapElementCategory(@OriginalArg(1) someMecId: Int) {
		WorldMapDisplay.someMecId = someMecId
		JSObjectManager.highlightFlashCycles = 3
		ServerEnvironment.flashCycleCounter = 100
		WorldMapDisplay.highlightedCategoryId = -1
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(III)V")
	@JvmStatic
	fun setWorldMapScrollTarget(@OriginalArg(0) x: Int, @OriginalArg(2) y: Int) {
		WorldMapDisplay.scrollTargetX = x - WorldMap.mapSquareAbsX
		WorldMapDisplay.scrollTargetY = y - WorldMap.mapSquareAbsY
	}

	@OriginalMember(owner = "client!pe", name = "a", descriptor = "(I)Lclient!dfa;")
	@JvmStatic
	fun getWorldMapArea(): WorldMapElement? {
		return WorldMap.area
	}

	@OriginalMember(owner = "client!ec", name = "a", descriptor = "(IIIBI)V")
	@JvmStatic
	fun markDestination(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) x: Int, @OriginalArg(4) y: Int) {
		@Pc(9) val aspectRatio: Float = WorldMapRenderer.mapSizeY.toFloat() / WorldMapRenderer.mapSizeX.toFloat()
		@Pc(11) var scaledWidth: Int = width
		@Pc(13) var scaledHeight: Int = height
		if (aspectRatio < 1.0f) {
			scaledHeight = (aspectRatio * width.toFloat()).toInt()
		} else {
			scaledWidth = (height.toFloat() / aspectRatio).toInt()
		}
		@Pc(40) val offsetX: Int = x - (width - scaledWidth) / 2
		@Pc(49) val offsetY: Int = y - (height - scaledHeight) / 2
		WorldMapDisplay.scrollTargetY = -1
		WorldMapDisplay.mapCenterY = WorldMapRenderer.mapSizeY - WorldMapRenderer.mapSizeY * offsetY / scaledHeight
		WorldMapDisplay.mapCenterX = WorldMapRenderer.mapSizeX * offsetX / scaledWidth
		WorldMapDisplay.scrollTargetX = -1
		WorldMapDisplay.clampMapCenter()
	}

	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(IB)V")
	@JvmStatic
	fun setWorldMapZoom(@OriginalArg(0) zoom: Int) {
		WorldMapDisplay.scrollTargetY = -1
		if (zoom == 37) {
			WorldMapRenderer.requestedMapZoom = 3.0f
		} else if (zoom == 50) {
			WorldMapRenderer.requestedMapZoom = 4.0f
		} else if (zoom == 75) {
			WorldMapRenderer.requestedMapZoom = 6.0f
		} else if (zoom == 100) {
			WorldMapRenderer.requestedMapZoom = 8.0f
		} else if (zoom == 200) {
			WorldMapRenderer.requestedMapZoom = 16.0f
		}
		WorldMapDisplay.scrollTargetY = -1
	}

	@OriginalMember(owner = "client!uv", name = "a", descriptor = "(IB)V")
	@JvmStatic
	fun setMapCenterY(@OriginalArg(0) mapCenterY: Int) {
		WorldMapDisplay.scrollTargetY = -1
		WorldMapDisplay.mapCenterY = mapCenterY
		WorldMapDisplay.scrollTargetY = -1
		WorldMapDisplay.clampMapCenter()
	}

	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(Lclient!ii;I)Z")
	@JvmStatic
	fun shouldDraw(@OriginalArg(0) mapElementType: MECType?): Boolean {
		if (mapElementType == null) {
			return false
		} else if (!mapElementType.displayedOnWorldmap) {
			return false
		} else if (!mapElementType.shouldDraw(MapList.PLAYER_VAR_PROVIDER)) {
			return false
		} else if (WorldMapDisplay.hiddenMecIds.get(mapElementType.mectypeId.toLong()) == null) {
			return WorldMapDisplay.hiddenCategoryIds.get(mapElementType.categoryId.toLong()) == null
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!paa", name = "b", descriptor = "(BI)V")
	@JvmStatic
	fun setMapCenterX(@OriginalArg(1) mapCenterX: Int) {
		WorldMapDisplay.scrollTargetY = -1
		WorldMapDisplay.scrollTargetX = -1
		WorldMapDisplay.mapCenterX = mapCenterX
		WorldMapDisplay.clampMapCenter()
	}

	@OriginalMember(owner = "client!np", name = "a", descriptor = "(II)V")
	@JvmStatic
	fun flashWorldMapElement(@OriginalArg(0) highlightedCategoryId: Int) {
		JSObjectManager.highlightFlashCycles = 3
		ServerEnvironment.flashCycleCounter = 100
		WorldMapDisplay.someMecId = -1
		WorldMapDisplay.highlightedCategoryId = highlightedCategoryId
	}

	@OriginalMember(owner = "client!tda", name = "a", descriptor = "I")
	@JvmField
	var scrollTargetX: Int = -1

	@OriginalMember(owner = "client!il", name = "a", descriptor = "(Z)V")
	@JvmStatic
	fun clampMapCenter() {
		if (WorldMapDisplay.mapCenterX < 0) {
			WorldMapDisplay.mapCenterX = 0
			WorldMapDisplay.scrollTargetX = -1
			WorldMapDisplay.scrollTargetY = -1
		}
		if (WorldMapDisplay.mapCenterX > WorldMapRenderer.mapSizeX) {
			WorldMapDisplay.mapCenterX = WorldMapRenderer.mapSizeX
			WorldMapDisplay.scrollTargetY = -1
			WorldMapDisplay.scrollTargetX = -1
		}
		if (WorldMapDisplay.mapCenterY < 0) {
			WorldMapDisplay.scrollTargetY = -1
			WorldMapDisplay.mapCenterY = 0
			WorldMapDisplay.scrollTargetX = -1
		}
		if (WorldMapDisplay.mapCenterY > WorldMapRenderer.mapSizeY) {
			WorldMapDisplay.mapCenterY = WorldMapRenderer.mapSizeY
			WorldMapDisplay.scrollTargetX = -1
			WorldMapDisplay.scrollTargetY = -1
		}
	}
}
