package com.jagex.game.runetek5

import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.config.staticelements.StaticElements
import com.jagex.game.runetek5.cache.node.MultiLocEntry
import com.jagex.game.runetek5.entity.WorldMapTile
import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Queue
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.flo.FloTypeList
import com.jagex.game.runetek5.config.flu.FluTypeList
import com.jagex.game.runetek5.config.loctype.LocTypeList
import com.jagex.game.runetek5.config.mec.MECTypeList
import com.jagex.game.runetek5.config.mapsprite.MapSpriteType
import com.jagex.game.runetek5.config.mapsprite.MapSpriteTypeList
import com.jagex.game.runetek5.vardomain.VarDomain
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapElement
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object MapList {

	@OriginalMember(owner = "client!kp", name = "G", descriptor = "[[[Lclient!tca;")
	@JvmField
	var tiles: Array<Array<Array<EntityNodeList?>?>?>? = null

	@OriginalMember(owner = "client!kp", name = "I", descriptor = "Lclient!eq;")
	@JvmField
	var multiLocs: IterableHashTable? = null

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(II)Lclient!dfa;")
	@JvmStatic
	fun getMapDef(@OriginalArg(0) coordX: Int, @OriginalArg(1) coordY: Int): WorldMapElement? {
		@Pc(4) var area: WorldMapElement? = WORLDMAP_DEFS.first() as WorldMapElement?
		while (area != null) {
			if (area.shouldRender && area.sourceContains(coordX, coordY)) {
				return area
			}
			area = WORLDMAP_DEFS.next() as WorldMapElement?
		}
		return null
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;)V")
	@JvmStatic
	fun drawTiles(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(3) val viewWidth: Int = WorldMap.tileViewEndX - WorldMap.tileViewStartX
		@Pc(7) val viewHeight: Int = WorldMap.tileViewEndY - WorldMap.tileViewStartY
		@Pc(15) val scaleX: Int = ((WorldMap.screenX2 - WorldMap.pixelViewStartX) shl 16) / viewWidth
		@Pc(23) val scaleZ: Int = ((WorldMap.pixelViewEndY - WorldMap.screenY1) shl 16) / viewHeight
		WorldMapRenderer.drawTiles(toolkit, scaleX, scaleZ)
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;II)V")
	@JvmStatic
	fun renderMap(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) pixelsPerTileX: Int, @OriginalArg(2) pixelsPerTileY: Int) {
		@Pc(9) val packet: Packet = Packet(MAP_AREA_RESOURCE!!.getFile(WorldMap.area!!.filenamePrefix, "area"))
		@Pc(13) val underlayCount: Int = packet.readUByte()
		@Pc(16) val underlayIdTable: IntArray = IntArray(underlayCount)
		@Pc(18) var i: Int = 0
		while (i < underlayCount) {
			underlayIdTable[i] = packet.readUByte()
			i++
		}
		@Pc(33) val overlayCount: Int = packet.readUByte()
		@Pc(36) val overlayIdTable: IntArray = IntArray(overlayCount)
		@Pc(38) var j: Int = 0
		while (j < overlayCount) {
			overlayIdTable[j] = packet.readUByte()
			j++
		}
		while (true) {
			@Pc(58) var local58: Int
			@Pc(64) var local64: Int
			@Pc(67) var local67: Int
			@Pc(76) var local76: Int
			@Pc(148) var local148: Int
			while (packet.currentPosition < packet.buffer!!.size) {
				@Pc(62) var mapSquareY: Int
				@Pc(84) var local84: Int
				if (packet.readUByte() == 0) {
					val mapSquareX: Int = packet.readUByte()
					mapSquareY = packet.readUByte()
					var localX: Int = 0
					while (localX < 64) {
						var localZ: Int = 0
						while (localZ < 64) {
							val tileX: Int = mapSquareX * 64 + localX - WorldMap.mapSquareAbsX
							val tileZ: Int = mapSquareY * 64 + localZ - WorldMap.mapSquareAbsY
							decodeTile(toolkit, packet, mapSquareX, mapSquareY, tileX, tileZ, underlayIdTable, overlayIdTable)
							localZ++
						}
						localX++
					}
				} else {
					val mapSquareX: Int = packet.readUByte()
					mapSquareY = packet.readUByte()
					val chunkX: Int = packet.readUByte()
					val chunkZ: Int = packet.readUByte()
					var offsetX: Int = 0
					while (offsetX < 8) {
						var offsetZ: Int = 0
						while (offsetZ < 8) {
							@Pc(136) val x: Int = mapSquareX * 64 + chunkX * 8 + offsetX - WorldMap.mapSquareAbsX
							val z: Int = mapSquareY * 64 + chunkZ * 8 + offsetZ - WorldMap.mapSquareAbsY
							decodeTile(toolkit, packet, mapSquareX, mapSquareY, x, z, underlayIdTable, overlayIdTable)
							offsetZ++
						}
						offsetX++
					}
				}
			}
			WorldMap.underlayColoursHigh = ByteArray(WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY)
			WorldMap.underlayColoursLow = ShortArray(WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY)
			var level: Int = 0
			while (level < 3) {
				@Pc(191) val underlays: ByteArray = ByteArray(WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY)
				var squareX: Int = 0
				while (squareX < tiles!![level]!!.size) {
					var squareZ: Int = 0
					while (squareZ < tiles!![level]!![0]!!.size) {
						@Pc(205) val linkedList: EntityNodeList? = tiles!![level]!![squareX]!![squareZ]
						if (linkedList != null) {
							@Pc(212) var worldMapTile: WorldMapTile? = linkedList.getFirst() as WorldMapTile?
							while (worldMapTile != null) {
								underlays[squareX * 64 + worldMapTile.x + (squareZ * 64 + worldMapTile.z) * WorldMapRenderer.mapSizeX] = worldMapTile.underlayIdOrColour.toByte()
								worldMapTile = linkedList.getNext() as WorldMapTile?
							}
						}
						squareZ++
					}
					squareX++
				}
				WorldMap.decodeUnderlayColors(underlays, WorldMap.underlayColoursHigh, WorldMap.underlayColoursLow, pixelsPerTileX, pixelsPerTileY)
				squareX = 0
				while (squareX < tiles!![level]!!.size) {
					var squareZ: Int = 0
					while (squareZ < tiles!![level]!![0]!!.size) {
						@Pc(276) val linkedList2: EntityNodeList? = tiles!![level]!![squareX]!![squareZ]
						if (linkedList2 != null) {
							@Pc(283) var worldMapTile2: WorldMapTile? = linkedList2.getFirst() as WorldMapTile?
							while (worldMapTile2 != null) {
								val tileIndex: Int = squareX * 64 + worldMapTile2.x + (squareZ * 64 + worldMapTile2.z) * WorldMapRenderer.mapSizeX
								worldMapTile2.underlayIdOrColour = ((WorldMap.underlayColoursHigh!![tileIndex].toInt() and 0xFF) shl 16) or (WorldMap.underlayColoursLow!![tileIndex].toInt() and 0xFFFF)
								if (worldMapTile2.underlayIdOrColour != 0) {
									worldMapTile2.underlayIdOrColour = worldMapTile2.underlayIdOrColour or 0xFF000000.toInt()
								}
								worldMapTile2 = linkedList2.getNext() as WorldMapTile?
							}
						}
						squareZ++
					}
					squareX++
				}
				level++
			}
			WorldMap.decodeUnderlayColors(WorldMap.underlayIds, WorldMap.underlayColoursHigh, WorldMap.underlayColoursLow, pixelsPerTileX, pixelsPerTileY)
			WorldMap.underlayIds = null
			WorldMap.buildMapCategories()
			return
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;IIII[S[B)V")
	@JvmStatic
	fun drawMsiMultiple(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) drawX: Int, @OriginalArg(2) drawY: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int, @OriginalArg(5) locIds: ShortArray?, @OriginalArg(6) rotations: ByteArray?) {
		if (locIds == null) {
			return
		}
		@Pc(4) var i: Int = 0
		while (i < locIds.size) {
			@Pc(14) val locType: LocType = LOCTYPE_LIST!!.list(locIds[i].toInt() and 0xFFFF)
			@Pc(17) val msi: Int = locType.mapIconId
			if (msi != -1) {
				@Pc(25) val msiType: MapSpriteType = MAP_SPRITE_TYPE_LIST!!.list(msi)!!
				@Pc(49) val sprite: NativeSprite? = msiType.createRotatedNativeSprite(if (locType.mapIconRotates) (rotations!![i].toInt() shr 6) and 0x3 else 0, if (locType.mapIconFlipped) locType.inverted else false, toolkit)
				if (sprite != null) {
					@Pc(58) var spriteWidth: Int = (width * sprite.scaleWidth()) shr 2
					@Pc(65) var spriteHeight: Int = (height * sprite.scaleHeight()) shr 2
					if (msiType.requiresUpscaling) {
						@Pc(71) var locWidth: Int = locType.sizeX
						@Pc(74) var locLength: Int = locType.sizeY
						if (((rotations!![i].toInt() shr 6) and 0x1) == 1) {
							@Pc(85) val temp: Int = locWidth
							locWidth = locLength
							locLength = temp
						}
						spriteWidth = locWidth * width
						spriteHeight = locLength * height
					}
					if (spriteWidth != 0 && spriteHeight != 0) {
						if (msiType.backgroundColor == 0) {
							sprite.drawTintedScaled(drawX, drawY + height - spriteHeight, spriteWidth, spriteHeight)
						} else {
							sprite.drawCircle(drawX, drawY + height - spriteHeight, spriteWidth, spriteHeight, 0, msiType.backgroundColor or 0xFF000000.toInt(), 1)
						}
					}
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;Lclient!cea;IIII[I[I)V")
	@JvmStatic
	fun decodeTile(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) packet: Packet?, @OriginalArg(2) chunkX: Int, @OriginalArg(3) chunkZ: Int, @OriginalArg(4) x: Int, @OriginalArg(5) z: Int, @OriginalArg(6) underlayPalette: IntArray?, @OriginalArg(7) overlayPalette: IntArray?) {
		@Pc(3) val flags: Int = packet!!.readUByte()
		if ((flags and 0x1) == 0) {
			@Pc(15) val underlay: Boolean = (flags and 0x2) == 0
			@Pc(21) var id: Int = (flags shr 2) and 0x3F
			if (id != 62) {
				if (id == 63) {
					id = packet.readUByte()
				} else if (underlay) {
					id = underlayPalette!![id]
				} else {
					id = overlayPalette!![id]
				}
				if (underlay) {
					WorldMap.underlayIds!![x + z * WorldMapRenderer.mapSizeX] = id.toByte()
					WorldMap.overlayIds!![x + z * WorldMapRenderer.mapSizeX] = 0.toByte()
				} else {
					WorldMap.overlayIds!![x + z * WorldMapRenderer.mapSizeX] = id.toByte()
					WorldMap.overlayShapes!![x + z * WorldMapRenderer.mapSizeX] = 0.toByte()
					WorldMap.underlayIds!![x + z * WorldMapRenderer.mapSizeX] = packet.readByte()
				}
			}
			return
		}
		@Pc(100) val levels: Int = ((flags shr 1) and 0x3) + 1
		@Pc(108) val hasOverlay: Boolean = (flags and 0x8) != 0
		@Pc(116) val hasLocs: Boolean = (flags and 0x10) != 0
		@Pc(118) var level: Int = 0
		while (level < levels) {
			@Pc(123) val underlayId: Int = packet.readUByte()
			@Pc(125) var overlayId: Int = 0
			@Pc(127) var shape: Int = 0
			if (hasOverlay) {
				overlayId = packet.readUByte()
				shape = packet.readUByte()
			}
			@Pc(139) var locCount: Int = 0
			if (hasLocs) {
				locCount = packet.readUByte()
			}
			@Pc(215) var levelLocIds: ShortArray?
			@Pc(218) var levelLocShapes: ByteArray?
			@Pc(220) var i: Int
			if (level == 0) {
				WorldMap.underlayIds!![x + z * WorldMapRenderer.mapSizeX] = underlayId.toByte()
				WorldMap.overlayIds!![x + z * WorldMapRenderer.mapSizeX] = overlayId.toByte()
				WorldMap.overlayShapes!![x + z * WorldMapRenderer.mapSizeX] = shape.toByte()
				if (locCount == 1) {
					WorldMap.tileLocIds!![x + z * WorldMapRenderer.mapSizeX] = (packet.readUShort() + 1).toShort()
					WorldMap.tileLocShapes!![x + z * WorldMapRenderer.mapSizeX] = packet.readByte()
				} else if (locCount > 1) {
					WorldMap.tileLocIds!![x + z * WorldMapRenderer.mapSizeX] = (-1).toShort()
					levelLocIds = ShortArray(locCount)
					levelLocShapes = ByteArray(locCount)
					i = 0
					while (i < locCount) {
						levelLocIds[i] = packet.readUShort().toShort()
						levelLocShapes[i] = packet.readByte()
						i++
					}
					multiLocs!!.put(((x shl 16) or z).toLong(), MultiLocEntry(levelLocIds, levelLocShapes))
				}
			} else {
				levelLocIds = null
				levelLocShapes = null
				if (locCount > 0) {
					levelLocIds = ShortArray(locCount)
					levelLocShapes = ByteArray(locCount)
					i = 0
					while (i < locCount) {
						levelLocIds[i] = packet.readUShort().toShort()
						levelLocShapes[i] = packet.readByte()
						i++
					}
				}
				if (tiles!![level - 1]!![chunkX - (WorldMap.mapSquareAbsX shr 6)]!![chunkZ - (WorldMap.mapSquareAbsY shr 6)] == null) {
					tiles!![level - 1]!![chunkX - (WorldMap.mapSquareAbsX shr 6)]!![chunkZ - (WorldMap.mapSquareAbsY shr 6)] = EntityNodeList()
				}
				@Pc(338) val tile: WorldMapTile = WorldMapTile(x and 0x3F, z and 0x3F, underlayId, overlayId, shape, levelLocIds, levelLocShapes)
				tiles!![level - 1]!![chunkX - (WorldMap.mapSquareAbsX shr 6)]!![chunkZ - (WorldMap.mapSquareAbsY shr 6)]!!.offer(tile)
			}
			level++
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "Lclient!in;")
	@JvmField
	var MAP_AREA_RESOURCE: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "(I)Lclient!dfa;")
	@JvmStatic
	fun getArea(@OriginalArg(0) id: Int): WorldMapElement? {
		return WORLDMAP_DEFS.get(id.toLong()) as WorldMapElement?
	}

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "(II)Lclient!mn;")
	@JvmStatic
	fun getRenderedElements(@OriginalArg(0) coordX: Int, @OriginalArg(1) coordY: Int): Queue? {
		@Pc(3) val queue: Queue = Queue()
		@Pc(8) var area: WorldMapElement? = WORLDMAP_DEFS.first() as WorldMapElement?
		while (area != null) {
			if (area.shouldRender && area.sourceContains(coordX, coordY)) {
				queue.insert(area)
			}
			area = WORLDMAP_DEFS.next() as WorldMapElement?
		}
		return queue
	}

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "(Lclient!ha;)Lclient!uh;")
	@JvmStatic
	fun positionElements(@OriginalArg(0) toolkit: Toolkit?): Deque? {
		@Pc(3) val viewWidth: Int = WorldMap.tileViewEndX - WorldMap.tileViewStartX
		@Pc(7) val viewHeight: Int = WorldMap.tileViewEndY - WorldMap.tileViewStartY
		@Pc(15) val scaleX: Int = ((WorldMap.screenX2 - WorldMap.pixelViewStartX) shl 16) / viewWidth
		@Pc(23) val scaleZ: Int = ((WorldMap.pixelViewEndY - WorldMap.screenY1) shl 16) / viewHeight
		return WorldMapRenderer.positionElements(toolkit, scaleX, scaleZ)
	}

	@OriginalMember(owner = "client!kp", name = "c", descriptor = "Lclient!cb;")
	@JvmField
	var FLOTYPE_LIST: FloTypeList? = null

	@OriginalMember(owner = "client!kp", name = "d", descriptor = "Lclient!qe;")
	@JvmField
	var FLUTYPE_LIST: FluTypeList? = null

	@OriginalMember(owner = "client!kp", name = "e", descriptor = "()V")
	@JvmStatic
	fun loadWorldMapCategories() {
		@Pc(2) val displayCoords: IntArray = IntArray(3)
		@Pc(4) var i: Int = 0
		while (i < staticElements!!.elementCount) {
			@Pc(32) val converted: Boolean = WorldMap.area!!.convertSourceToDisplay(displayCoords, (staticElements!!.bitpackedPlacement[i] shr 14) and 0x3FFF, staticElements!!.bitpackedPlacement[i] and 0x3FFF, (staticElements!!.bitpackedPlacement[i] shr 28) and 0x3)
			if (converted) {
				@Pc(42) val mapElementListEntry: WorldMapCategory = WorldMapCategory(staticElements!!.mapCategoryIds[i])
				mapElementListEntry.x = displayCoords[1] - WorldMap.mapSquareAbsX
				mapElementListEntry.z = displayCoords[2] - WorldMap.mapSquareAbsY
				worldMapCategories.addLast(mapElementListEntry)
			}
			i++
		}
	}

	@OriginalMember(owner = "client!kp", name = "e", descriptor = "Lclient!eq;")
	@JvmField
	val WORLDMAP_DEFS: IterableHashTable = IterableHashTable(16)

	@OriginalMember(owner = "client!kp", name = "f", descriptor = "Lclient!oda;")
	@JvmField
	var MAP_SPRITE_TYPE_LIST: MapSpriteTypeList? = null

	@OriginalMember(owner = "client!kp", name = "h", descriptor = "Lclient!dd;")
	@JvmField
	var PLAYER_VAR_PROVIDER: VarDomain? = null

	@OriginalMember(owner = "client!kp", name = "i", descriptor = "Lclient!uha;")
	@JvmField
	var LOCTYPE_LIST: LocTypeList? = null

	@OriginalMember(owner = "client!kp", name = "j", descriptor = "Lclient!tu;")
	@JvmField
	var staticElements: StaticElements? = null

	@OriginalMember(owner = "client!kp", name = "r", descriptor = "Lclient!uh;")
	@JvmField
	val worldMapCategories: Deque = Deque()
}
