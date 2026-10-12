package com.jagex.game.runetek5.statics

import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.cache.node.MultiLocEntry
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.entity.WorldMapTile
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object WorldMapRenderer {

	@OriginalMember(owner = "client!kp", name = "m", descriptor = "F")
	@JvmField
	var requestedMapZoom: Float = 0.0f

	@OriginalMember(owner = "client!kp", name = "w", descriptor = "I")
	@JvmField
	var mapSizeX: Int = 0

	@OriginalMember(owner = "client!kp", name = "E", descriptor = "I")
	@JvmField
	var mapSizeY: Int = 0

	@OriginalMember(owner = "client!kp", name = "n", descriptor = "I")
	@JvmField
	var hueRandomisation: Int = (Math.random() * 11.0).toInt() - 5

	@OriginalMember(owner = "client!kp", name = "q", descriptor = "I")
	@JvmField
	var lightnessRandomisation: Int = (Math.random() * 17.0).toInt() - 8

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;Lclient!lga;IIII)V")
	@JvmStatic
	private fun positionElement(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) entry: WorldMapCategory?, @OriginalArg(2) scaleX: Int, @OriginalArg(3) scaleZ: Int) {
		entry!!.screenX = WorldMap.pixelViewStartX + ((scaleX * (entry.x - WorldMap.tileViewStartX)) shr 16)
		entry.screenY = WorldMap.pixelViewEndY - ((scaleZ * (entry.z - WorldMap.tileViewStartY)) shr 16)
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;IIII)Lclient!uh;")
	@JvmStatic
	fun positionElements(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) scaleX: Int, @OriginalArg(2) scaleZ: Int): Deque? {
		@Pc(4) var entry: WorldMapCategory? = MapList.worldMapCategories!!.first() as WorldMapCategory?
		while (entry != null) {
			positionElement(toolkit, entry, scaleX, scaleZ)
			entry = MapList.worldMapCategories!!.next() as WorldMapCategory?
		}
		return MapList.worldMapCategories
	}

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "(Lclient!ha;IIII)V")
	@JvmStatic
	fun drawTiles(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) scaleX: Int, @OriginalArg(2) scaleZ: Int) {
		@Pc(3) var tileColumns: Int = WorldMap.tileViewEndX - WorldMap.tileViewStartX
		@Pc(7) var tileRows: Int = WorldMap.tileViewEndY - WorldMap.tileViewStartY
		if (WorldMap.tileViewEndX < mapSizeX) {
			tileColumns++
		}
		if (WorldMap.tileViewEndY < mapSizeY) {
			tileRows++
		}
		@Pc(28) var local28: Int
		@Pc(40) var local40: Int
		@Pc(44) var local44: Int
		@Pc(50) var local50: Int
		@Pc(57) var local57: Int
		@Pc(70) var local70: Int
		@Pc(80) var local80: Int
		@Pc(84) var local84: Int
		@Pc(93) var local93: Int
		@Pc(173) var local173: Int
		@Pc(175) var local175: Int
		@Pc(177) var local177: Int
		@Pc(179) var local179: Int
		@Pc(17) var i: Int = 0
		while (i < tileColumns) {
			val columnStartX: Int = ((scaleX * i) shr 16) + WorldMap.pixelViewStartX
			val columnEndX: Int = ((scaleX * (i + 1)) shr 16) + WorldMap.pixelViewStartX
			val columnWidth: Int = columnEndX - columnStartX
			if (columnWidth > 0) {
				val tileX: Int = WorldMap.tileViewStartX + i
				if (tileX >= 0 && tileX < mapSizeX) {
					var row: Int = 0
					while (row < tileRows) {
						val rowTopY: Int = WorldMap.pixelViewEndY - ((scaleZ * (row + 1)) shr 16)
						val rowBottomY: Int = WorldMap.pixelViewEndY - ((scaleZ * row) shr 16)
						val rowHeight: Int = rowBottomY - rowTopY
						if (rowHeight > 0) {
							val tileY: Int = row + WorldMap.tileViewStartY
							val tileIndex: Int = tileX + tileY * mapSizeX
							var underlayColour: Int = 0
							var overlayId: Int = 0
							var locId: Int = 0
							if (tileY >= 0 && tileY < mapSizeY) {
								underlayColour = ((WorldMap.underlayColoursHigh!![tileIndex].toInt() and 0xFF) shl 16) or (WorldMap.underlayColoursLow!![tileIndex].toInt() and 0xFFFF)
								if (underlayColour != 0) {
									underlayColour = underlayColour or 0xFF000000.toInt()
								}
								overlayId = WorldMap.overlayIds!![tileIndex].toInt() and 0xFF
								locId = WorldMap.tileLocIds!![tileIndex].toInt() and 0xFFFF
							}
							if (underlayColour == 0 && overlayId == 0 && locId == 0) {
								if (WorldMap.area!!.color != -1) {
									underlayColour = WorldMap.area!!.color or 0xFF000000.toInt()
								} else if (((i + WorldMap.tileViewStartX) and 0x4) == ((row + WorldMap.tileViewEndY) and 0x4)) {
									underlayColour = WorldMap.overlayFloorColors!![MapList.FLOTYPE_LIST!!.lastLoadedOverlayId + 1]
								} else {
									underlayColour = -11840664
								}
								if (underlayColour == 0) {
									underlayColour = -16777216
								}
								toolkit!!.aa(columnStartX, rowTopY, columnWidth, rowHeight, underlayColour, 0)
							} else if (locId <= 0) {
								WorldMap.drawTile(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, underlayColour, overlayId, WorldMap.overlayShapes!![tileIndex].toInt(), null, null, true)
							} else if (locId == 65535) {
								@Pc(282) val multiLocEntry: MultiLocEntry? = MapList.multiLocs!!.get(((tileX shl 16) or tileY).toLong()) as MultiLocEntry?
								if (multiLocEntry != null) {
									WorldMap.drawTile(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, underlayColour, overlayId, WorldMap.overlayShapes!![tileIndex].toInt(), multiLocEntry.locIds, multiLocEntry.locShapes, true)
								}
							} else {
								WorldMap.singleLocIds[0] = (locId - 1).toShort()
								WorldMap.singleLocShapes[0] = WorldMap.tileLocShapes!![tileIndex]
								WorldMap.drawTile(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, underlayColour, overlayId, WorldMap.overlayShapes!![tileIndex].toInt(), WorldMap.singleLocIds, WorldMap.singleLocShapes, true)
							}
						}
						row++
					}
				} else {
					var row: Int = 0
					while (row < tileRows) {
						val rowTopY: Int = WorldMap.pixelViewEndY - ((scaleZ * (row + 1)) shr 16)
						val rowBottomY: Int = WorldMap.pixelViewEndY - ((scaleZ * row) shr 16)
						val rowHeight: Int = rowBottomY - rowTopY
						var fillColour: Int
						if (WorldMap.area!!.color != -1) {
							fillColour = WorldMap.area!!.color or 0xFF000000.toInt()
						} else if (((i + WorldMap.tileViewStartX) and 0x4) == ((row + WorldMap.tileViewEndY) and 0x4)) {
							fillColour = WorldMap.overlayFloorColors!![MapList.FLOTYPE_LIST!!.lastLoadedOverlayId + 1]
						} else {
							fillColour = -11840664
						}
						if (fillColour == 0) {
							fillColour = -16777216
						}
						toolkit!!.aa(columnStartX, rowTopY, columnWidth, rowHeight, fillColour, 0)
						row++
					}
				}
			}
			i++
		}
		var column: Int = -16
		while (column < tileColumns + 16) {
			val columnStartX: Int = ((scaleX * column) shr 16) + WorldMap.pixelViewStartX
			val columnEndX: Int = ((scaleX * (column + 1)) shr 16) + WorldMap.pixelViewStartX
			val columnWidth: Int = columnEndX - columnStartX
			if (columnWidth > 0) {
				val tileX: Int = column + WorldMap.tileViewStartX
				if (tileX >= 0 && tileX < mapSizeX) {
					var row: Int = -16
					while (row < tileRows + 16) {
						val rowTopY: Int = WorldMap.pixelViewEndY - ((scaleZ * (row + 1)) shr 16)
						val rowBottomY: Int = WorldMap.pixelViewEndY - ((scaleZ * row) shr 16)
						val rowHeight: Int = rowBottomY - rowTopY
						if (rowHeight > 0) {
							val tileY: Int = row + WorldMap.tileViewStartY
							if (tileY >= 0 && tileY < mapSizeY) {
								val locId: Int = WorldMap.tileLocIds!![tileX + tileY * mapSizeX].toInt() and 0xFFFF
								if (locId <= 0) {
									MapList.drawMsiMultiple(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, null, null)
								} else if (locId == 65535) {
									@Pc(459) val multiLocEntry: MultiLocEntry? = MapList.multiLocs!!.get(((tileX shl 16) or tileY).toLong()) as MultiLocEntry?
									if (multiLocEntry != null) {
										MapList.drawMsiMultiple(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, multiLocEntry.locIds, multiLocEntry.locShapes)
									}
								} else {
									WorldMap.singleLocIds[0] = (locId - 1).toShort()
									WorldMap.singleLocShapes[0] = WorldMap.tileLocShapes!![tileX + tileY * mapSizeX]
									MapList.drawMsiMultiple(toolkit, columnStartX, rowTopY, columnWidth, rowHeight, WorldMap.singleLocIds, WorldMap.singleLocShapes)
								}
							}
						}
						row++
					}
				}
			}
			column++
		}
		var minSquareX: Int = WorldMap.tileViewStartX shr 6
		var minSquareY: Int = WorldMap.tileViewStartY shr 6
		if (minSquareX < 0) {
			minSquareX = 0
		}
		if (minSquareY < 0) {
			minSquareY = 0
		}
		var maxSquareX: Int = WorldMap.tileViewEndX shr 6
		var maxSquareY: Int = WorldMap.tileViewEndY shr 6
		if (maxSquareX >= MapList.tiles!![0]!!.size) {
			maxSquareX = MapList.tiles!![0]!!.size - 1
		}
		if (maxSquareY >= MapList.tiles!![0]!![0]!!.size) {
			maxSquareY = MapList.tiles!![0]!![0]!!.size - 1
		}
		var level: Int = 0
		while (level < 3) {
			@Pc(641) var local641: Int
			@Pc(653) var local653: Int
			@Pc(665) var local665: Int
			@Pc(675) var local675: Int
			@Pc(631) var local631: Int
			var squareX: Int = minSquareX
			while (squareX <= maxSquareX) {
				var squareY: Int = minSquareY
				while (squareY <= maxSquareY) {
					@Pc(589) val linkedList: EntityNodeList? = MapList.tiles!![level]!![squareX]!![squareY]
					if (linkedList != null) {
						val squareBaseX: Int = (squareX + (WorldMap.mapSquareAbsX shr 6)) * 64
						val squareBaseY: Int = (squareY + (WorldMap.mapSquareAbsY shr 6)) * 64
						@Pc(612) var worldMapTile: WorldMapTile? = linkedList.getFirst() as WorldMapTile?
						while (worldMapTile != null) {
							val relTileX: Int = squareBaseX + worldMapTile.x - WorldMap.mapSquareAbsX - WorldMap.tileViewStartX
							val relTileY: Int = squareBaseY + worldMapTile.z - WorldMap.mapSquareAbsY - WorldMap.tileViewStartY
							val tileLeftX: Int = ((scaleX * relTileX) shr 16) + WorldMap.pixelViewStartX
							val tileRightX: Int = ((scaleX * (relTileX + 1)) shr 16) + WorldMap.pixelViewStartX
							val tileTopY: Int = WorldMap.pixelViewEndY - ((scaleZ * (relTileY + 1)) shr 16)
							val tileBottomY: Int = WorldMap.pixelViewEndY - ((scaleZ * relTileY) shr 16)
							WorldMap.drawTile(toolkit, tileLeftX, tileTopY, tileRightX - tileLeftX, tileBottomY - tileTopY, worldMapTile.underlayIdOrColour, worldMapTile.overlayId.toInt() and 0xFF, worldMapTile.shape.toInt(), worldMapTile.locIds, worldMapTile.locShapes, false)
							worldMapTile = linkedList.getNext() as WorldMapTile?
						}
					}
					squareY++
				}
				squareX++
			}
			squareX = minSquareX
			while (squareX <= maxSquareX) {
				var squareY: Int = minSquareY
				while (squareY <= maxSquareY) {
					@Pc(727) val linkedList2: EntityNodeList? = MapList.tiles!![level]!![squareX]!![squareY]
					if (linkedList2 != null) {
						val squareBaseX: Int = (squareX + (WorldMap.mapSquareAbsX shr 6)) * 64
						val squareBaseY: Int = (squareY + (WorldMap.mapSquareAbsY shr 6)) * 64
						@Pc(750) var worldMapTile2: WorldMapTile? = linkedList2.getFirst() as WorldMapTile?
						while (worldMapTile2 != null) {
							val relTileX: Int = squareBaseX + worldMapTile2.x - WorldMap.mapSquareAbsX - WorldMap.tileViewStartX
							val relTileY: Int = squareBaseY + worldMapTile2.z - WorldMap.mapSquareAbsY - WorldMap.tileViewStartY
							val tileLeftX: Int = ((scaleX * relTileX) shr 16) + WorldMap.pixelViewStartX
							val tileRightX: Int = ((scaleX * (relTileX + 1)) shr 16) + WorldMap.pixelViewStartX
							val tileTopY: Int = WorldMap.pixelViewEndY - ((scaleZ * (relTileY + 1)) shr 16)
							@Pc(813) val tileBottomY: Int = WorldMap.pixelViewEndY - ((scaleZ * relTileY) shr 16)
							MapList.drawMsiMultiple(toolkit, tileLeftX, tileTopY, tileRightX - tileLeftX, tileBottomY - tileTopY, worldMapTile2.locIds, worldMapTile2.locShapes)
							worldMapTile2 = linkedList2.getNext() as WorldMapTile?
						}
					}
					squareY++
				}
				squareX++
			}
			level++
		}
	}

	@OriginalMember(owner = "client!le", name = "b", descriptor = "(I)I")
	@JvmStatic
	fun getWorldMapZoom(): Int {
		if (WorldMapRenderer.requestedMapZoom.toDouble() == 3.0) {
			return 37
		} else if (WorldMapRenderer.requestedMapZoom.toDouble() == 4.0) {
			return 50
		} else if (WorldMapRenderer.requestedMapZoom.toDouble() == 6.0) {
			return 75
		} else if (WorldMapRenderer.requestedMapZoom.toDouble() == 8.0) {
			return 100
		} else {
			return 200
		}
	}
}
