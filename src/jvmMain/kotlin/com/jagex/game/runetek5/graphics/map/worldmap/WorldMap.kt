package com.jagex.game.runetek5.graphics.map.worldmap

import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.cache.node.MultiLocEntry
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.client.JSObjectManager
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.config.flo.FloType
import com.jagex.game.runetek5.config.flo.FloTypeList
import com.jagex.game.runetek5.config.flu.FluType
import com.jagex.game.runetek5.config.flu.FluTypeList
import com.jagex.game.runetek5.config.loctype.LocInteractivity
import com.jagex.game.runetek5.config.loctype.LocTypeList
import com.jagex.game.runetek5.config.mapsprite.MapSpriteTypeList
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.config.mec.MECType
import com.jagex.game.runetek5.config.mec.MECTypeList
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.entity.WorldMapTile
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.math.ColourUtils
import com.jagex.game.runetek5.statics.WorldMapRenderer
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.vardomain.VarDomain
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object WorldMap {

	@OriginalMember(owner = "client!lm", name = "z", descriptor = "I")
	@JvmField
	var numInactivePlayerIndices: Int = 0

	@OriginalMember(owner = "client!fe", name = "a", descriptor = "(ILclient!ha;IIZI)V")
	@JvmStatic
	fun drawWorldmapOverview(@OriginalArg(0) height: Int, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) x: Int, @OriginalArg(3) width: Int, @OriginalArg(5) y: Int) {
		toolkit!!.KA(x, y, width + x, y + height)
		toolkit.fillRect(width, x, y, height, -16777216)
		if (ComponentMinimap.mapAssetLoadingPercentage < 100) {
			return
		}
		@Pc(33) val aspectRatio: Float = WorldMapRenderer.mapSizeY.toFloat() / WorldMapRenderer.mapSizeX.toFloat()
		@Pc(35) var scaledWidth: Int = width
		@Pc(37) var scaledHeight: Int = height
		if (aspectRatio < 1.0f) {
			scaledHeight = (width.toFloat() * aspectRatio).toInt()
		} else {
			scaledWidth = (height.toFloat() / aspectRatio).toInt()
		}
		@Pc(64) val adjustedY: Int = y + (height - scaledHeight) / 2
		@Pc(73) val adjustedX: Int = x + (width - scaledWidth) / 2
		if (ComponentMinimap.worldMapOverviewSprite == null || width != ComponentMinimap.worldMapOverviewSprite!!.getWidth() || height != ComponentMinimap.worldMapOverviewSprite!!.getHeight()) {
			WorldMap.setView(WorldMap.mapSquareAbsX, WorldMapRenderer.mapSizeY + WorldMap.mapSquareAbsY, WorldMap.mapSquareAbsX - -WorldMapRenderer.mapSizeX, WorldMap.mapSquareAbsY, adjustedX, adjustedY, scaledWidth + adjustedX, scaledHeight + adjustedY)
			MapList.drawTiles(toolkit)
			ComponentMinimap.worldMapOverviewSprite = toolkit.createNativeSprite(adjustedX, adjustedY, scaledWidth, scaledHeight, false)
		}
		ComponentMinimap.worldMapOverviewSprite!!.render(adjustedX, adjustedY)
		@Pc(125) val viewWidth: Int = WorldMap.worldMapViewWidth * scaledWidth / WorldMapRenderer.mapSizeX
		@Pc(131) val viewHeight: Int = scaledHeight * WorldMap.worldMapViewHeight / WorldMapRenderer.mapSizeY
		@Pc(140) val viewX: Int = adjustedX + scaledWidth * Camera.worldMapViewMinX / WorldMapRenderer.mapSizeX
		@Pc(164) val viewY: Int = adjustedY + scaledHeight - viewHeight - scaledHeight * WorldMap.worldMapViewMinY / WorldMapRenderer.mapSizeY
		@Pc(166) var viewColour: Int = -1996554240
		if (Game.STELLAR_DAWN === Game.CURRENT_GAME) {
			viewColour = -1996488705
		}
		toolkit.aa(viewX, viewY, viewWidth, viewHeight, viewColour, 1)
		toolkit.outlineRect(viewX, viewY, viewWidth, viewHeight, viewColour, 0)
		if (JSObjectManager.highlightFlashCycles <= 0) {
			return
		}
		@Pc(196) val flashAlpha: Int
		if (ServerEnvironment.flashCycleCounter <= 50) {
			flashAlpha = ServerEnvironment.flashCycleCounter * 5
		} else {
			flashAlpha = 500 - ServerEnvironment.flashCycleCounter * 5
		}
		@Pc(210) var mapElementListEntry: WorldMapCategory? = MapList.worldMapCategories.first() as WorldMapCategory?
		while (mapElementListEntry != null) {
			@Pc(218) val mapElementType: MECType? = WorldMap.MECTYPE_LIST!!.list(mapElementListEntry.id)
			if (WorldMapDisplay.shouldDraw(mapElementType)) {
				@Pc(235) var markerX: Int
				@Pc(246) var markerY: Int
				if (WorldMapDisplay.highlightedCategoryId == mapElementListEntry.id) {
					markerX = adjustedX + scaledWidth * mapElementListEntry.x / WorldMapRenderer.mapSizeX
					markerY = (WorldMapRenderer.mapSizeY - mapElementListEntry.z) * scaledHeight / WorldMapRenderer.mapSizeY + adjustedY
					toolkit.fillRect(4, markerX - 2, markerY + -2, 4, (flashAlpha shl 24) or 0xFFFF00)
				} else if (WorldMapDisplay.someMecId != -1 && WorldMapDisplay.someMecId == mapElementType!!.categoryId) {
					markerX = adjustedX + mapElementListEntry.x * scaledWidth / WorldMapRenderer.mapSizeX
					markerY = adjustedY + scaledHeight * (WorldMapRenderer.mapSizeY - mapElementListEntry.z) / WorldMapRenderer.mapSizeY
					toolkit.fillRect(4, markerX - 2, markerY + -2, 4, (flashAlpha shl 24) or 0xFFFF00)
				}
			}
			mapElementListEntry = MapList.worldMapCategories.next() as WorldMapCategory?
		}
	}

	@OriginalMember(owner = "client!kp", name = "B", descriptor = "[B")
	@JvmField
	var underlayColoursHigh: ByteArray? = null

	@OriginalMember(owner = "client!kp", name = "C", descriptor = "I")
	@JvmField
	var tileViewEndX: Int = 0

	@OriginalMember(owner = "client!kp", name = "F", descriptor = "I")
	@JvmField
	var pixelViewEndY: Int = 0

	@OriginalMember(owner = "client!kp", name = "H", descriptor = "[S")
	@JvmField
	var underlayColoursLow: ShortArray? = null

	@OriginalMember(owner = "client!kp", name = "J", descriptor = "I")
	@JvmField
	var pixelViewStartX: Int = 0

	@OriginalMember(owner = "client!kp", name = "K", descriptor = "[B")
	@JvmField
	var underlayIds: ByteArray? = null

	@OriginalMember(owner = "client!kp", name = "L", descriptor = "I")
	@JvmField
	var tileViewStartX: Int = 0

	@OriginalMember(owner = "client!kp", name = "M", descriptor = "[B")
	@JvmField
	var overlayShapes: ByteArray? = null

	@OriginalMember(owner = "client!kp", name = "N", descriptor = "I")
	@JvmField
	var screenX2: Int = 0

	@OriginalMember(owner = "client!kp", name = "O", descriptor = "I")
	@JvmField
	var tileViewEndY: Int = 0

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun setArea(@OriginalArg(0) id: Int) {
		WorldMap.area = MapList.WORLDMAP_DEFS.get(id.toLong()) as WorldMapElement?
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(IIIIIIII)V")
	@JvmStatic
	fun setView(@OriginalArg(0) x1: Int, @OriginalArg(1) z2: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) z1: Int, @OriginalArg(4) left: Int, @OriginalArg(5) top: Int, @OriginalArg(6) right: Int, @OriginalArg(7) bottom: Int) {
		tileViewStartX = x1 - WorldMap.mapSquareAbsX
		tileViewEndY = z2 - WorldMap.mapSquareAbsY
		tileViewEndX = x2 - WorldMap.mapSquareAbsX
		tileViewStartY = z1 - WorldMap.mapSquareAbsY
		pixelViewStartX = left
		screenY1 = top
		screenX2 = right
		pixelViewEndY = bottom
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;IIIIIII[S[BZ)V")
	@JvmStatic
	fun drawTile(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int, @OriginalArg(5) underlayColour: Int, @OriginalArg(6) overlay: Int, @OriginalArg(7) tileShapeAndRotation: Int, @OriginalArg(8) locIds: ShortArray?, @OriginalArg(9) locShapes: ByteArray?, @OriginalArg(10) opaque: Boolean) {
		@Pc(20) var right: Int
		@Pc(32) var bottom: Int
		if (opaque || underlayColour != 0 || overlay > 0) {
			if (overlay == 0) {
				toolkit!!.aa(x, y, width, height, underlayColour, 0)
			} else {
				right = tileShapeAndRotation and 0x3F
				if (right == 0 || width <= 1 || height <= 1) {
					bottom = overlayFloorColors!![overlay]
					if (opaque || bottom != 0) {
						toolkit!!.aa(x, y, width, height, bottom, 0)
					}
				} else {
					bottom = if (opaque) 0 else 1
					WorldMap.drawTileShape(overlayFloorColors!![overlay], underlayColour, toolkit, bottom, tileSize, (tileShapeAndRotation shr 6) and 0x3, tileShapePatterns, width, x, right, height, y)
				}
			}
		}
		if (locIds == null) {
			return
		}
		if (width == 1) {
			right = x
		} else {
			right = x + width - 1
		}
		if (height == 1) {
			bottom = y
		} else {
			bottom = y + height - 1
		}
		@Pc(100) var i: Int = 0
		while (i < locIds.size) {
			@Pc(107) val shape: Int = locShapes!![i].toInt() and 0x3F
			if (shape == 0 || shape == 2 || shape == 3 || shape == 9) {
				@Pc(127) val locType: LocType = MapList.LOCTYPE_LIST!!.list(locIds[i].toInt() and 0xFFFF)
				if (locType.mapIconId == -1) {
					@Pc(133) var wallColour: Int = -3355444
					if (locType.hasActions == LocInteractivity.INTERACTIVE) {
						wallColour = -3407872
					}
					@Pc(147) val rotation: Int = (locShapes[i].toInt() shr 6) and 0x3
					if (shape == 0) {
						if (rotation == 0) {
							toolkit!!.P(x, y, height, wallColour, 0)
						} else if (rotation == 1) {
							toolkit!!.U(x, y, width, wallColour, 0)
						} else if (rotation == 2) {
							toolkit!!.P(right, y, height, wallColour, 0)
						} else {
							toolkit!!.U(x, bottom, width, wallColour, 0)
						}
					} else if (shape == 2) {
						if (rotation == 0) {
							toolkit!!.P(x, y, height, -1, 0)
							toolkit.U(x, y, width, wallColour, 0)
						} else if (rotation == 1) {
							toolkit!!.P(right, y, height, -1, 0)
							toolkit.U(x, y, width, wallColour, 0)
						} else if (rotation == 2) {
							toolkit!!.P(right, y, height, -1, 0)
							toolkit.U(x, bottom, width, wallColour, 0)
						} else {
							toolkit!!.P(x, y, height, -1, 0)
							toolkit.U(x, bottom, width, wallColour, 0)
						}
					} else if (shape == 3) {
						if (rotation == 0) {
							toolkit!!.U(x, y, 1, wallColour, 0)
						} else if (rotation == 1) {
							toolkit!!.U(right, y, 1, wallColour, 0)
						} else if (rotation == 2) {
							toolkit!!.U(right, bottom, 1, wallColour, 0)
						} else {
							toolkit!!.U(x, bottom, 1, wallColour, 0)
						}
					} else if (shape == 9) {
						@Pc(313) var step: Int
						if (rotation == 0 || rotation == 2) {
							step = 0
							while (step < height) {
								toolkit!!.U(x + step, bottom - step, 1, wallColour, 0)
								step++
							}
						} else {
							step = 0
							while (step < height) {
								toolkit!!.U(x + step, y + step, 1, wallColour, 0)
								step++
							}
						}
					}
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!ha;Lclient!lga;Lclient!ii;)V")
	@JvmStatic
	fun renderCategoryLines(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) category: WorldMapCategory?, @OriginalArg(2) mecType: MECType?) {
		if (mecType!!.offsets == null) {
			return
		}
		@Pc(7) val polygonPoints: IntArray = IntArray(mecType.offsets!!.size)
		@Pc(20) var local20: Int
		@Pc(32) var local32: Int
		@Pc(9) var i: Int = 0
		while (i < polygonPoints.size / 2) {
			val tileX: Int = mecType.offsets!![i * 2] + category!!.x
			val tileZ: Int = mecType.offsets!![i * 2 + 1] + category.z
			polygonPoints[i * 2] = pixelViewStartX + (screenX2 - pixelViewStartX) * (tileX - tileViewStartX) / (tileViewEndX - tileViewStartX)
			polygonPoints[i * 2 + 1] = pixelViewEndY - (pixelViewEndY - screenY1) * (tileZ - tileViewStartY) / (tileViewEndY - tileViewStartY)
			i++
		}
		WorldMapRasterManager.fillLandmarkPolygon(toolkit, polygonPoints, mecType.lineColor)
		if (mecType.dashLineSpacing > 0) {
			@Pc(102) var startY: Int
			@Pc(110) var endX: Int
			@Pc(120) var endY: Int
			@Pc(125) var tmp: Int
			@Pc(127) var tmpY: Int
			var pointIndex: Int = 0
			while (pointIndex < polygonPoints.size / 2 - 1) {
				var startX: Int = polygonPoints[pointIndex * 2]
				startY = polygonPoints[pointIndex * 2 + 1]
				endX = polygonPoints[(pointIndex + 1) * 2]
				endY = polygonPoints[(pointIndex + 1) * 2 + 1]
				if (endX < startX) {
					tmp = startX
					tmpY = startY
					startX = endX
					startY = endY
					endX = tmp
					endY = tmpY
				} else if (endX == startX && endY < startY) {
					tmp = startY
					startY = endY
					endY = tmp
				}
				toolkit!!.drawLine(startX, startY, endX, endY, mecType.colors!![mecType.colorPointers!![pointIndex].toInt() and 0xFF], mecType.dashLineSpacing, mecType.dashLineLength, mecType.dashLineOffset)
				pointIndex++
			}
			var startX: Int = polygonPoints[polygonPoints.size - 2]
			startY = polygonPoints[polygonPoints.size - 1]
			endX = polygonPoints[0]
			endY = polygonPoints[1]
			if (endX < startX) {
				tmp = startX
				tmpY = startY
				startX = endX
				startY = endY
				endX = tmp
				endY = tmpY
			} else if (endX == startX && endY < startY) {
				tmp = startY
				startY = endY
				endY = tmp
			}
			toolkit!!.drawLine(startX, startY, endX, endY, mecType.colors!![mecType.colorPointers!![mecType.colorPointers!!.size - 1].toInt() and 0xFF], mecType.dashLineSpacing, mecType.dashLineLength, mecType.dashLineOffset)
			return
		}
		var pointIndex: Int = 0
		while (pointIndex < polygonPoints.size / 2 - 1) {
			toolkit!!.drawOpaqueLine(polygonPoints[pointIndex * 2 + 1], polygonPoints[pointIndex * 2], polygonPoints[(pointIndex + 1) * 2], mecType.colors!![mecType.colorPointers!![pointIndex].toInt() and 0xFF], polygonPoints[(pointIndex + 1) * 2 + 1])
			pointIndex++
		}
		toolkit!!.drawOpaqueLine(polygonPoints[polygonPoints.size - 1], polygonPoints[polygonPoints.size - 2], polygonPoints[0], mecType.colors!![mecType.colorPointers!![mecType.colorPointers!!.size - 1].toInt() and 0xFF], polygonPoints[1])
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "([B[B[SII)V")
	@JvmStatic
	fun decodeUnderlayColors(@OriginalArg(0) tileUnderlayIds: ByteArray?, @OriginalArg(1) tileRed: ByteArray?, @OriginalArg(2) tileGreenBlue: ShortArray?, @OriginalArg(3) hueOffset: Int, @OriginalArg(4) lightnessOffset: Int) {
		@Pc(2) val underlayHue: IntArray = IntArray(WorldMapRenderer.mapSizeY)
		@Pc(5) val underlaySaturation: IntArray = IntArray(WorldMapRenderer.mapSizeY)
		@Pc(8) val underlayLightness: IntArray = IntArray(WorldMapRenderer.mapSizeY)
		@Pc(11) val underlayDivisor: IntArray = IntArray(WorldMapRenderer.mapSizeY)
		@Pc(14) val underlayTileCount: IntArray = IntArray(WorldMapRenderer.mapSizeY)
		@Pc(16) var i: Int = -5
		while (i < WorldMapRenderer.mapSizeX) {
			@Pc(21) val positiveX: Int = i + 5
			@Pc(25) val negativeX: Int = i - 5
			@Pc(41) var local41: Int
			@Pc(27) var j: Int = 0
			while (j < WorldMapRenderer.mapSizeY) {
				@Pc(50) var floorUnderlayType: FluType?
				@Pc(86) var previousCount: Int
				if (positiveX < WorldMapRenderer.mapSizeX) {
					val underlayId: Int = tileUnderlayIds!![positiveX + j * WorldMapRenderer.mapSizeX].toInt() and 0xFF
					if (underlayId > 0) {
						floorUnderlayType = MapList.FLUTYPE_LIST!!.list(underlayId - 1)
						underlayHue[j] += floorUnderlayType!!.hue
						underlaySaturation[j] += floorUnderlayType.saturation
						underlayLightness[j] += floorUnderlayType.lightness
						underlayDivisor[j] += floorUnderlayType.hueMultiplier
						previousCount = underlayTileCount[j]++
					}
				}
				if (negativeX >= 0) {
					val underlayId: Int = tileUnderlayIds!![negativeX + j * WorldMapRenderer.mapSizeX].toInt() and 0xFF
					if (underlayId > 0) {
						floorUnderlayType = MapList.FLUTYPE_LIST!!.list(underlayId - 1)
						underlayHue[j] -= floorUnderlayType!!.hue
						underlaySaturation[j] -= floorUnderlayType.saturation
						underlayLightness[j] -= floorUnderlayType.lightness
						underlayDivisor[j] -= floorUnderlayType.hueMultiplier
						previousCount = underlayTileCount[j]--
					}
				}
				j++
			}
			if (i >= 0) {
				var hue: Int = 0
				@Pc(159) var saturation: Int = 0
				@Pc(161) var lightness: Int = 0
				@Pc(163) var divisor: Int = 0
				@Pc(165) var tileCount: Int = 0
				@Pc(167) var k: Int = -5
				while (k < WorldMapRenderer.mapSizeY) {
					@Pc(172) val positiveY: Int = k + 5
					if (positiveY < WorldMapRenderer.mapSizeY) {
						hue += underlayHue[positiveY]
						saturation += underlaySaturation[positiveY]
						lightness += underlayLightness[positiveY]
						divisor += underlayDivisor[positiveY]
						tileCount += underlayTileCount[positiveY]
					}
					@Pc(209) val negativeY: Int = k - 5
					if (negativeY >= 0) {
						hue -= underlayHue[negativeY]
						saturation -= underlaySaturation[negativeY]
						lightness -= underlayLightness[negativeY]
						divisor -= underlayDivisor[negativeY]
						tileCount -= underlayTileCount[negativeY]
					}
					if (k >= 0 && tileCount > 0) {
						@Pc(261) var local261: Int
						if ((tileUnderlayIds!![i + k * WorldMapRenderer.mapSizeX].toInt() and 0xFF) == 0) {
							val emptyTileIndex: Int = i + k * WorldMapRenderer.mapSizeX
							tileRed!![emptyTileIndex] = 0
							tileGreenBlue!![emptyTileIndex] = 0
						} else {
							val hsl: Int = if (divisor == 0) 0 else ColourUtils.packHsl(lightness / tileCount, saturation / tileCount, hue * 256 / divisor)
							@Pc(294) var clampedLightness: Int = (hsl and 0x7F) + lightnessOffset
							if (clampedLightness < 0) {
								clampedLightness = 0
							} else if (clampedLightness > 127) {
								clampedLightness = 127
							}
							@Pc(316) val modifiedHsl: Int = ((hsl + hueOffset) and 0xFC00) + (hsl and 0x380) + clampedLightness
							@Pc(322) val tileIndex: Int = i + k * WorldMapRenderer.mapSizeX
							@Pc(333) val rgb: Int = ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(ColorUtil.scaleHslLightness(modifiedHsl)).toInt() and 0xFFFF]
							tileRed!![tileIndex] = ((rgb shr 16) and 0xFF).toByte()
							tileGreenBlue!![tileIndex] = (rgb and 0xFFFF).toShort()
						}
					}
					k++
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "()V")
	@JvmStatic
	fun freeBuffers() {
		underlayIds = null
		underlayColoursHigh = null
		underlayColoursLow = null
		overlayIds = null
		overlayShapes = null
		tileLocIds = null
		tileLocShapes = null
		MapList.multiLocs = null
		MapList.tiles = null
		overlayFloorColors = null
	}

	@OriginalMember(owner = "client!kp", name = "c", descriptor = "()V")
	@JvmStatic
	fun buildMapCategories() {
		@Pc(4) var z: Int
		@Pc(15) var locId: Int
		@Pc(49) var mapelement: Int
		@Pc(1) var x: Int = 0
		while (x < WorldMapRenderer.mapSizeX) {
			z = 0
			while (z < WorldMapRenderer.mapSizeY) {
				locId = tileLocIds!![x + z * WorldMapRenderer.mapSizeX].toInt() and 0xFFFF
				if (locId != 0) {
					@Pc(35) var locIndex: Int
					if (locId == 65535) {
						@Pc(31) val locList: MultiLocEntry? = MapList.multiLocs!!.get(((x shl 16) or z).toLong()) as MultiLocEntry?
						if (locList != null) {
							locIndex = 0
							while (locIndex < locList.locIds!!.size) {
								@Pc(46) var locType: LocType? = MapList.LOCTYPE_LIST!!.list(locList.locIds[locIndex].toInt() and 0xFFFF)
								mapelement = locType!!.mapCategoryid
								if (locType.alternativeIds != null) {
									locType = locType.getMultiLoc(MapList.PLAYER_VAR_PROVIDER)
									if (locType != null) {
										mapelement = locType.mapCategoryid
									}
								}
								if (mapelement != -1) {
									@Pc(70) val element: WorldMapCategory = WorldMapCategory(mapelement)
									element.x = x
									element.z = z
									MapList.worldMapCategories.addLast(element)
								}
								locIndex++
							}
						}
					} else {
						@Pc(94) var locType2: LocType? = MapList.LOCTYPE_LIST!!.list(locId - 1)
						locIndex = locType2!!.mapCategoryid
						if (locType2.alternativeIds != null) {
							locType2 = locType2.getMultiLoc(MapList.PLAYER_VAR_PROVIDER)
							if (locType2 != null) {
								locIndex = locType2.mapCategoryid
							}
						}
						if (locIndex != -1) {
							@Pc(118) val mapElementListEntry: WorldMapCategory = WorldMapCategory(locIndex)
							mapElementListEntry.x = x
							mapElementListEntry.z = z
							MapList.worldMapCategories.addLast(mapElementListEntry)
						}
					}
				}
				z++
			}
			x++
		}
		z = 0
		while (z < 3) {
			locId = 0
			while (locId < MapList.tiles!![0]!!.size) {
				@Pc(144) var chunkZ: Int = 0
				while (chunkZ < MapList.tiles!![0]!![0]!!.size) {
					@Pc(153) val chunkTiles: EntityNodeList? = MapList.tiles!![z]!![locId]!![chunkZ]
					if (chunkTiles != null) {
						@Pc(160) var tile: WorldMapTile? = chunkTiles.getFirst() as WorldMapTile?
						while (tile != null) {
							if (tile.locIds != null) {
								mapelement = 0
								while (mapelement < tile.locIds.size) {
									@Pc(177) var locType3: LocType? = MapList.LOCTYPE_LIST!!.list(tile.locIds[mapelement].toInt() and 0xFFFF)
									@Pc(180) var elementId: Int = locType3!!.mapCategoryid
									if (locType3.alternativeIds != null) {
										locType3 = locType3.getMultiLoc(MapList.PLAYER_VAR_PROVIDER)
										if (locType3 != null) {
											elementId = locType3.mapCategoryid
										}
									}
									if (elementId != -1) {
										@Pc(201) val mapElementListEntry2: WorldMapCategory = WorldMapCategory(elementId)
										mapElementListEntry2.x = (locId + (WorldMap.mapSquareAbsX shr 6)) * 64 + tile.x - WorldMap.mapSquareAbsX
										mapElementListEntry2.z = (chunkZ + (WorldMap.mapSquareAbsY shr 6)) * 64 + tile.z - WorldMap.mapSquareAbsY
										MapList.worldMapCategories.addLast(mapElementListEntry2)
									}
									mapelement++
								}
							}
							tile = chunkTiles.getNext() as WorldMapTile?
						}
					}
					chunkZ++
				}
				locId++
			}
			z++
		}
	}

	@OriginalMember(owner = "client!kp", name = "d", descriptor = "()V")
	@JvmStatic
	fun allocateBuffers() {
		underlayIds = ByteArray(WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY)
		overlayIds = ByteArray(WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY)
		overlayShapes = ByteArray(WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY)
		tileLocIds = ShortArray(WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY)
		tileLocShapes = ByteArray(WorldMapRenderer.mapSizeX * WorldMapRenderer.mapSizeY)
		MapList.multiLocs = IterableHashTable(1024)
		val chunksX: Int = WorldMapRenderer.mapSizeX shr 6
		val chunksZ: Int = WorldMapRenderer.mapSizeY shr 6
		MapList.tiles = Array<Array<Array<EntityNodeList?>?>?>(3) { Array<Array<EntityNodeList?>?>(chunksX) { arrayOfNulls<EntityNodeList>(chunksZ) } }
		overlayFloorColors = IntArray(MapList.FLOTYPE_LIST!!.totalFileCount + 1)
	}

	@OriginalMember(owner = "client!kp", name = "l", descriptor = "[B")
	@JvmField
	val singleLocShapes: ByteArray = ByteArray(1)

	@OriginalMember(owner = "client!kp", name = "o", descriptor = "[[[B")
	@JvmField
	var tileShapePatterns: Array<Array<ByteArray?>?>? = null

	@OriginalMember(owner = "client!kp", name = "p", descriptor = "[S")
	@JvmField
	val singleLocIds: ShortArray = ShortArray(1)

	@OriginalMember(owner = "client!kp", name = "s", descriptor = "I")
	@JvmField
	var tileSize: Int = 0

	@OriginalMember(owner = "client!kp", name = "t", descriptor = "[B")
	@JvmField
	var overlayIds: ByteArray? = null

	@OriginalMember(owner = "client!kp", name = "u", descriptor = "I")
	@JvmField
	var tileViewStartY: Int = 0

	@OriginalMember(owner = "client!kp", name = "v", descriptor = "[B")
	@JvmField
	var tileLocShapes: ByteArray? = null

	@OriginalMember(owner = "client!kp", name = "x", descriptor = "[I")
	@JvmField
	var overlayFloorColors: IntArray? = null

	@OriginalMember(owner = "client!kp", name = "y", descriptor = "[S")
	@JvmField
	var tileLocIds: ShortArray? = null

	@OriginalMember(owner = "client!kp", name = "z", descriptor = "I")
	@JvmField
	var screenY1: Int = 0

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!d;II)V")
	@JvmStatic
	fun initOverlayColours(@OriginalArg(0) textureSource: MaterialProvider?, @OriginalArg(1) hueOffset: Int, @OriginalArg(2) lightnessOffset: Int) {
		@Pc(1) var i: Int = 0
		while (i < MapList.FLOTYPE_LIST!!.totalFileCount) {
			WorldMap.overlayFloorColors!![i + 1] = overlayColour(textureSource, i, hueOffset, lightnessOffset)
			i++
		}
	}

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!d;III)I")
	@JvmStatic
	fun overlayColour(@OriginalArg(0) textureSource: MaterialProvider?, @OriginalArg(1) id: Int, @OriginalArg(2) hueOffset: Int, @OriginalArg(3) lightnessOffset: Int): Int {
		@Pc(4) val spotAnimType: FloType? = MapList.FLOTYPE_LIST!!.list(id)
		if (spotAnimType == null) {
			return 0
		}
		@Pc(11) var textureId: Int = spotAnimType.texture
		if (textureId >= 0 && textureSource!!.getMetrics(textureId)!!.detailsOnly) {
			textureId = -1
		}
		@Pc(68) val floorColor: Int
		@Pc(27) var colorHsl: Int
		@Pc(33) var lightness: Int
		@Pc(55) var modifiedHsl: Int
		if (spotAnimType.minimapColorHsl >= 0) {
			colorHsl = spotAnimType.minimapColorHsl
			lightness = (colorHsl and 0x7F) + lightnessOffset
			if (lightness < 0) {
				lightness = 0
			} else if (lightness > 127) {
				lightness = 127
			}
			modifiedHsl = ((colorHsl + hueOffset) and 0xFC00) + (colorHsl and 0x380) + lightness
			floorColor = ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(WorldMap.scaleOverlayLightness(modifiedHsl)).toInt() and 0xFFFF] or 0xFF000000.toInt()
		} else if (textureId >= 0) {
			floorColor = ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(WorldMap.scaleOverlayLightness(textureSource!!.getMetrics(textureId)!!.colorHsl.toInt())).toInt() and 0xFFFF] or 0xFF000000.toInt()
		} else if (spotAnimType.tileColorHsl == -1) {
			floorColor = 0
		} else {
			colorHsl = spotAnimType.tileColorHsl
			lightness = (colorHsl and 0x7F) + lightnessOffset
			if (lightness < 0) {
				lightness = 0
			} else if (lightness > 127) {
				lightness = 127
			}
			modifiedHsl = ((colorHsl + hueOffset) and 0xFC00) + (colorHsl and 0x380) + lightness
			floorColor = ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(WorldMap.scaleOverlayLightness(modifiedHsl)).toInt() and 0xFFFF] or 0xFF000000.toInt()
		}
		return floorColor
	}

	@OriginalMember(owner = "client!kp", name = "g", descriptor = "Lclient!dfa;")
	@JvmField
	var area: WorldMapElement? = null

	@OriginalMember(owner = "client!kp", name = "b", descriptor = "Lclient!on;")
	@JvmField
	var MECTYPE_LIST: MECTypeList? = null

	@OriginalMember(owner = "client!kp", name = "A", descriptor = "I")
	@JvmField
	var mapSquareAbsY: Int = 0

	@OriginalMember(owner = "client!kp", name = "a", descriptor = "(Lclient!in;Lclient!cb;Lclient!qe;Lclient!uha;Lclient!on;Lclient!oda;Lclient!dd;)V")
	@JvmStatic
	fun initialize(@OriginalArg(0) mapAreaResource: Js5ResourceProvider?, @OriginalArg(1) floorOverlayTypeList: FloTypeList?, @OriginalArg(2) floorUnderlayTypeList: FluTypeList?, @OriginalArg(3) locTypeList: LocTypeList?, @OriginalArg(4) mapElementTypeList: MECTypeList?, @OriginalArg(5) msiTypeList: MapSpriteTypeList?, @OriginalArg(6) varDomain: VarDomain?) {
		MapList.MAP_AREA_RESOURCE = mapAreaResource
		MapList.FLOTYPE_LIST = floorOverlayTypeList
		MapList.FLUTYPE_LIST = floorUnderlayTypeList
		MapList.LOCTYPE_LIST = locTypeList
		MECTYPE_LIST = mapElementTypeList
		MapList.MAP_SPRITE_TYPE_LIST = msiTypeList
		MapList.PLAYER_VAR_PROVIDER = varDomain
		MapList.WORLDMAP_DEFS.clear()
		@Pc(21) val groupId: Int = MapList.MAP_AREA_RESOURCE!!.getGroupId("details")
		@Pc(26) val fileIds: IntArray? = MapList.MAP_AREA_RESOURCE!!.getFileIds(groupId)
		if (fileIds != null) {
			@Pc(30) var i: Int = 0
			while (i < fileIds.size) {
				@Pc(39) val worldMapArea: WorldMapElement = WorldMapElement.list(fileIds[i], MapList.MAP_AREA_RESOURCE, groupId)
				MapList.WORLDMAP_DEFS.put(worldMapArea.elementId.toLong(), worldMapArea)
				i++
			}
		}
		ColorUtil.initColorTables(true, false)
	}

	@OriginalMember(owner = "client!kp", name = "k", descriptor = "F")
	@JvmField
	var mapZoom: Float = 0.0f

	@OriginalMember(owner = "client!kp", name = "D", descriptor = "I")
	@JvmField
	var mapSquareAbsX: Int = 0

	@OriginalMember(owner = "client!bn", name = "b", descriptor = "Lclient!at;")
	@JvmField
	var worldmapComponent: Component? = null

	@OriginalMember(owner = "client!aw", name = "I", descriptor = "Lclient!ej;")
	@JvmField
	val RESUME_P_OBJDIALOG: ClientPacket = ClientPacket(28, 2)

	@OriginalMember(owner = "client!eq", name = "b", descriptor = "(I)Lclient!lga;")
	@JvmStatic
	fun getNextActiveCategory(): WorldMapCategory? {
		if (MapList.worldMapCategories == null || WorldMapDisplay.categoryIterator == null) {
			return null
		}
		@Pc(21) var mapElementListEntry: WorldMapCategory? = WorldMapDisplay.categoryIterator.next() as WorldMapCategory?
		while (mapElementListEntry != null) {
			@Pc(29) val mapElementType: MECType? = WorldMap.MECTYPE_LIST!!.list(mapElementListEntry.id)
			if (mapElementType != null && mapElementType.visible && mapElementType.shouldDraw(MapList.PLAYER_VAR_PROVIDER)) {
				return mapElementListEntry
			}
			mapElementListEntry = WorldMapDisplay.categoryIterator.next() as WorldMapCategory?
		}
		return null
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(IILclient!ha;IIII[[[BIIIII)V")
	@JvmStatic
	fun drawTileShape(@OriginalArg(0) overlayColour: Int, @OriginalArg(1) underlayColour: Int, @OriginalArg(2) toolkit: Toolkit?, @OriginalArg(4) blendMode: Int, @OriginalArg(5) tileSize: Int, @OriginalArg(6) rotation: Int, @OriginalArg(7) shapePatterns: Array<Array<ByteArray?>?>?, @OriginalArg(8) width: Int, @OriginalArg(9) x: Int, @OriginalArg(10) shape: Int, @OriginalArg(11) height: Int, @OriginalArg(12) y: Int) {
		var rotation: Int = rotation
		var shape: Int = shape
		if (shape == 0 || tileSize == 0) {
			return
		}
		if (shape == 9) {
			shape = 1
			rotation = (rotation + 1) and 0x3
		}
		if (shape == 10) {
			rotation = (rotation + 3) and 0x3
			shape = 1
		}
		if (shape == 11) {
			rotation = (rotation + 3) and 0x3
			shape = 8
		}
		toolkit!!.Q(x, y, width, height, underlayColour, overlayColour, shapePatterns!![shape - 1]!![rotation], tileSize, blendMode)
	}

	@OriginalMember(owner = "client!as", name = "a", descriptor = "(III)I")
	@JvmStatic
	fun scaleOverlayLightness(@OriginalArg(2) hsl: Int): Int {
		if (hsl == -2) {
			return 12345678
		} else if (hsl == -1) {
			return 96
		} else {
			@Pc(42) var lightness: Int = ((hsl and 0x7F) * 96) shr 7
			if (lightness < 2) {
				lightness = 2
			} else if (lightness > 126) {
				lightness = 126
			}
			return lightness + (hsl and 0xFF80)
		}
	}

	@OriginalMember(owner = "client!tq", name = "d", descriptor = "(I)Lclient!lga;")
	@JvmStatic
	fun getFirstWorldMapCategory(): WorldMapCategory? {
		if (MapList.worldMapCategories == null || WorldMapDisplay.categoryIterator == null) {
			return null
		}
		WorldMapDisplay.categoryIterator.setDeque(MapList.worldMapCategories)
		@Pc(18) val mapElementListEntry: WorldMapCategory? = WorldMapDisplay.categoryIterator.first() as WorldMapCategory?
		if (mapElementListEntry == null) {
			return null
		} else {
			@Pc(28) val mapElementType: MECType? = WorldMap.MECTYPE_LIST!!.list(mapElementListEntry.id)
			return if (mapElementType != null && mapElementType.visible && mapElementType.shouldDraw(MapList.PLAYER_VAR_PROVIDER)) mapElementListEntry else WorldMap.getNextActiveCategory()
		}
	}

	@OriginalMember(owner = "client!daa", name = "a", descriptor = "(II)[[[B")
	@JvmStatic
	fun createTileShapePatterns(@OriginalArg(0) tileSize: Int): Array<Array<ByteArray?>?> {
		@Pc(9) val patterns: Array<Array<ByteArray?>?> = Array<Array<ByteArray?>?>(8) { arrayOfNulls<ByteArray>(4) }
		@Pc(18) var mask: ByteArray = ByteArray(tileSize * tileSize)
		@Pc(20) var index: Int = 0
		@Pc(26) var local26: Int
		@Pc(22) var i: Int = 0
		while (i < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (i >= column) {
					mask[index] = -1
				}
				index++
				column++
			}
			i++
		}
		patterns[0]!![0] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(77) var local77: Int
		var row: Int = tileSize - 1
		while (row >= 0) {
			var column: Int = 0
			while (column < tileSize) {
				if (column <= row) {
					mask[index] = -1
				}
				index++
				column++
			}
			row--
		}
		patterns[0]!![1] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(117) var local117: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (row <= column) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[0]!![2] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(165) var local165: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = 0
			while (column < tileSize) {
				if (column >= row) {
					mask[index] = -1
				}
				index++
				column++
			}
			row--
		}
		patterns[0]!![3] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(206) var local206: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = 0
			while (column < tileSize) {
				if ((row shr 1) >= column) {
					mask[index] = -1
				}
				index++
				column++
			}
			row--
		}
		patterns[1]!![0] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(251) var local251: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (index >= 0 && index < mask.size) {
					if (column >= (row shl 1)) {
						mask[index] = -1
					}
					index++
				} else {
					index++
				}
				column++
			}
			row++
		}
		patterns[1]!![1] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(305) var local305: Int
		row = 0
		while (row < tileSize) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if (column <= (row shr 1)) {
					mask[index] = -1
				}
				index++
				column--
			}
			row++
		}
		patterns[1]!![2] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(347) var local347: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if ((row shl 1) <= column) {
					mask[index] = -1
				}
				index++
				column--
			}
			row--
		}
		patterns[1]!![3] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(394) var local394: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if ((row shr 1) >= column) {
					mask[index] = -1
				}
				index++
				column--
			}
			row--
		}
		patterns[2]!![0] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(436) var local436: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = 0
			while (column < tileSize) {
				if (column >= (row shl 1)) {
					mask[index] = -1
				}
				index++
				column++
			}
			row--
		}
		patterns[2]!![1] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(482) var local482: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (column <= (row shr 1)) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[2]!![2] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(531) var local531: Int
		row = 0
		while (row < tileSize) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if (column >= (row shl 1)) {
					mask[index] = -1
				}
				index++
				column--
			}
			row++
		}
		patterns[2]!![3] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(575) var local575: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = 0
			while (column < tileSize) {
				if (column >= (row shr 1)) {
					mask[index] = -1
				}
				index++
				column++
			}
			row--
		}
		patterns[3]!![0] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(624) var local624: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (column <= (row shl 1)) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[3]!![1] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(673) var local673: Int
		row = 0
		while (row < tileSize) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if (column >= (row shr 1)) {
					mask[index] = -1
				}
				index++
				column--
			}
			row++
		}
		patterns[3]!![2] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(719) var local719: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if (column <= (row shl 1)) {
					mask[index] = -1
				}
				index++
				column--
			}
			row--
		}
		patterns[3]!![3] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(766) var local766: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if ((row shr 1) <= column) {
					mask[index] = -1
				}
				index++
				column--
			}
			row--
		}
		patterns[4]!![0] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(808) var local808: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = 0
			while (column < tileSize) {
				if ((row shl 1) >= column) {
					mask[index] = -1
				}
				index++
				column++
			}
			row--
		}
		patterns[4]!![1] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(854) var local854: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if ((row shr 1) <= column) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[4]!![2] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(895) var local895: Int
		row = 0
		while (row < tileSize) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if ((row shl 1) >= column) {
					mask[index] = -1
				}
				index++
				column--
			}
			row++
		}
		patterns[4]!![3] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(940) var local940: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (tileSize / 2 >= column) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[5]!![0] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(979) var local979: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (row <= tileSize / 2) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[5]!![1] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(1022) var local1022: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (column >= tileSize / 2) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[5]!![2] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(1065) var local1065: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (row >= tileSize / 2) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[5]!![3] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(1108) var local1108: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (row - tileSize / 2 >= column) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[6]!![0] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(1155) var local1155: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = 0
			while (column < tileSize) {
				if (column <= row - tileSize / 2) {
					mask[index] = -1
				}
				index++
				column++
			}
			row--
		}
		patterns[6]!![1] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(1203) var local1203: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if (row - tileSize / 2 >= column) {
					mask[index] = -1
				}
				index++
				column--
			}
			row--
		}
		patterns[6]!![2] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(1249) var local1249: Int
		row = 0
		while (row < tileSize) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if (row - tileSize / 2 >= column) {
					mask[index] = -1
				}
				index++
				column--
			}
			row++
		}
		patterns[6]!![3] = mask
		mask = ByteArray(tileSize * tileSize)
		index = 0
		@Pc(1297) var local1297: Int
		row = 0
		while (row < tileSize) {
			var column: Int = 0
			while (column < tileSize) {
				if (column >= row - tileSize / 2) {
					mask[index] = -1
				}
				index++
				column++
			}
			row++
		}
		patterns[7]!![0] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(1349) var local1349: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = 0
			while (column < tileSize) {
				if (row - tileSize / 2 <= column) {
					mask[index] = -1
				}
				index++
				column++
			}
			row--
		}
		patterns[7]!![1] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		@Pc(1401) var local1401: Int
		row = tileSize - 1
		while (row >= 0) {
			var column: Int = tileSize - 1
			while (column >= 0) {
				if (column >= row - tileSize / 2) {
					mask[index] = -1
				}
				index++
				column--
			}
			row--
		}
		patterns[7]!![2] = mask
		index = 0
		mask = ByteArray(tileSize * tileSize)
		row = 0
		while (row < tileSize) {
			@Pc(1450) var j: Int = tileSize - 1
			while (j >= 0) {
				if (j >= row - tileSize / 2) {
					mask[index] = -1
				}
				index++
				j--
			}
			row++
		}
		patterns[7]!![3] = mask
		return patterns
	}

	@OriginalMember(owner = "client!hq", name = "k", descriptor = "I")
	@JvmField
	var worldMapViewWidth: Int = 0

	@OriginalMember(owner = "client!sv", name = "U", descriptor = "I")
	@JvmField
	var worldMapViewMinY: Int = 0

	@OriginalMember(owner = "client!ed", name = "n", descriptor = "I")
	@JvmField
	var dragStartCenterY: Int = 0

	@OriginalMember(owner = "client!qe", name = "h", descriptor = "I")
	@JvmField
	var dragStartCenterX: Int = 0

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(ZIII)I")
	@JvmStatic
	fun findNearestWorldMapElement(@OriginalArg(1) categoryId: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int): Int {
		if (ComponentMinimap.mapAssetLoadingPercentage < 100) {
			return -2
		}
		@Pc(23) var bestCoord: Int = -2
		@Pc(25) var bestDistance: Int = Int.MAX_VALUE
		@Pc(29) val relativeX: Int = x - WorldMap.mapSquareAbsX
		@Pc(33) val relativeY: Int = y - WorldMap.mapSquareAbsY
		@Pc(38) var mapElementListEntry: WorldMapCategory? = MapList.worldMapCategories.first() as WorldMapCategory?
		while (mapElementListEntry != null) {
			if (mapElementListEntry.id == categoryId) {
				@Pc(47) val elementX: Int = mapElementListEntry.x
				@Pc(50) val elementZ: Int = mapElementListEntry.z
				@Pc(61) val packedCoord: Int = (elementZ + WorldMap.mapSquareAbsY) or ((WorldMap.mapSquareAbsX + elementX) shl 14)
				@Pc(80) val distanceSquared: Int = (relativeY - elementZ) * (-elementZ + relativeY) + (relativeX - elementX) * (-elementX + relativeX)
				if (bestCoord < 0 || distanceSquared < bestDistance) {
					bestDistance = distanceSquared
					bestCoord = packedCoord
				}
			}
			mapElementListEntry = MapList.worldMapCategories.next() as WorldMapCategory?
		}
		return bestCoord
	}

	@OriginalMember(owner = "client!qca", name = "b", descriptor = "I")
	@JvmField
	var worldMapViewHeight: Int = 0

	@OriginalMember(owner = "client!mt", name = "f", descriptor = "I")
	@JvmField
	var savedToolkitId: Int = -1

	@OriginalMember(owner = "client!mj", name = "o", descriptor = "Lclient!dfa;")
	@JvmField
	var aClass2_Sub6_Sub4_3: WorldMapElement? = null

	@OriginalMember(owner = "client!sr", name = "b", descriptor = "Z")
	@JvmField
	var worldMapTeleportPending: Boolean = false

	@OriginalMember(owner = "client!jf", name = "o", descriptor = "Z")
	@JvmField
	var worldMapDragged: Boolean = false
}
