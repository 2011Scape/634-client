package com.jagex.game.runetek5

import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.core.constants.HintArrowType
import com.jagex.game.runetek5.core.constants.LocShapes
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.scene.CollisionFlag
import com.jagex.game.runetek5.scene.CollisionMap
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.loctype.LocInteractivity
import com.jagex.game.runetek5.config.mec.MECType
import com.jagex.game.runetek5.config.npc.NPCType
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import com.jagex.game.runetek5.statics.WorldMapPolygonFill
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapRasterManager
import com.jagex.game.runetek5.interaction.FriendList
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.config.staticelements.StaticElements
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.cache.node.lru.PulseEvent
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.config.mapsprite.MapSpriteType
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.network.packet.TcpPacket
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.jClass

object Minimap {

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "(Lclient!at;ILclient!aa;IIIBILclient!ha;)V")
	@JvmStatic
	fun drawMec(@OriginalArg(0) component: Component?, @OriginalArg(1) clipTop: Int, @OriginalArg(2) clippingMask: ComponentSurface?, @OriginalArg(3) mectypeId: Int, @OriginalArg(4) offsetX: Int, @OriginalArg(5) clipLeft: Int, @OriginalArg(7) offsetY: Int, @OriginalArg(8) toolkit: Toolkit?) {
		@Pc(10) val mapElementType: MECType? = TypeLists.MEC!!.list(mectypeId)
		if (mapElementType == null || !mapElementType.displayedOnMinimap || !mapElementType.shouldDraw(PlayerVarDomain.playerVarProvider)) {
			return
		}
		@Pc(34) var height: Int
		if (mapElementType.offsets != null) {
			@Pc(32) val polygonPoints: IntArray = IntArray(mapElementType.offsets!!.size)
			@Pc(46) var local46: Int
			@Pc(57) var local57: Int
			@Pc(61) var local61: Int
			height = 0
			while (height < polygonPoints.size / 2) {
				var rotation: Int
				if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
					rotation = Camera.camAngleY.toInt() and 0x3FFF
				} else {
					rotation = (Minimap.minimapRotation + Camera.camAngleY.toInt()) and 0x3FFF
				}
				var sine: Int = TextureCombineSource.SINE[rotation]
				var cosine: Int = TextureCombineSource.COSINE[rotation]
				if (Camera.cameraCalculationType != CameraMode.MODE_FOLLOWCOORD) {
					sine = sine * 256 / (Minimap.minimapZoom + 256)
					cosine = cosine * 256 / (Minimap.minimapZoom + 256)
				}
				polygonPoints[height * 2] = ((sine * (mapElementType.offsets!![height * 2 + 1] * 4 + offsetY) + (offsetX + mapElementType.offsets!![height * 2] * 4) * cosine) shr 14) + (clipLeft + (component!!.width / 2))
				polygonPoints[height * 2 + 1] = clipTop + component.height / 2 - (((mapElementType.offsets!![height * 2 + 1] * 4 + offsetY) * cosine - (offsetX + mapElementType.offsets!![height * 2] * 4) * sine) shr 14)
				height++
			}
			WorldMapRasterManager.fillPolygonMasked(toolkit, polygonPoints, mapElementType.lineColor, component!!.rowStartX, component.rowWidth)
			if (mapElementType.dashLineSpacing > 0) {
				@Pc(218) var lineX1: Int
				@Pc(228) var lineY1: Int
				@Pc(243) var swapTemp: Int
				@Pc(255) var swapY: Int
				var pointIndex: Int = 0
				while (pointIndex < polygonPoints.size / 2 - 1) {
					var lineX0: Int = polygonPoints[pointIndex * 2]
					var lineY0: Int = polygonPoints[pointIndex * 2 + 1]
					lineX1 = polygonPoints[pointIndex * 2 + 2]
					lineY1 = polygonPoints[(pointIndex + 1) * 2 + 1]
					if (lineX0 > lineX1) {
						swapTemp = lineX0
						lineX0 = lineX1
						swapY = lineY0
						lineY0 = lineY1
						lineX1 = swapTemp
						lineY1 = swapY
					} else if (lineX1 == lineX0 && lineY0 > lineY1) {
						swapTemp = lineY0
						lineY0 = lineY1
						lineY1 = swapTemp
					}
					toolkit!!.drawTexturedLine(lineX0, lineY0, lineX1, lineY1, mapElementType.colors!![mapElementType.colorPointers!![pointIndex].toInt() and 0xFF], clippingMask, clipLeft, clipTop, mapElementType.dashLineSpacing, mapElementType.dashLineLength, mapElementType.dashLineOffset)
					pointIndex++
				}
				var lineX0: Int = polygonPoints[polygonPoints.size - 2]
				var lineY0: Int = polygonPoints[polygonPoints.size - 1]
				lineX1 = polygonPoints[0]
				lineY1 = polygonPoints[1]
				if (lineX1 < lineX0) {
					swapTemp = lineX0
					lineX0 = lineX1
					swapY = lineY0
					lineY0 = lineY1
					lineX1 = swapTemp
					lineY1 = swapY
				} else if (lineX0 == lineX1 && lineY0 > lineY1) {
					swapTemp = lineY0
					lineY0 = lineY1
					lineY1 = swapTemp
				}
				toolkit!!.drawTexturedLine(lineX0, lineY0, lineX1, lineY1, mapElementType.colors!![mapElementType.colorPointers!![mapElementType.colorPointers!!.size - 1].toInt() and 0xFF], clippingMask, clipLeft, clipTop, mapElementType.dashLineSpacing, mapElementType.dashLineLength, mapElementType.dashLineOffset)
			} else {
				var pointIndex: Int = 0
				while (pointIndex < polygonPoints.size / 2 - 1) {
					toolkit!!.drawSurfaceLine(polygonPoints[pointIndex * 2], polygonPoints[pointIndex * 2 + 1], polygonPoints[(pointIndex + 1) * 2], polygonPoints[pointIndex * 2 + 3], mapElementType.colors!![mapElementType.colorPointers!![pointIndex].toInt() and 0xFF], clippingMask, clipLeft, clipTop)
					pointIndex++
				}
				toolkit!!.drawSurfaceLine(polygonPoints[polygonPoints.size - 2], polygonPoints[polygonPoints.size - 1], polygonPoints[0], polygonPoints[1], mapElementType.colors!![mapElementType.colorPointers!![mapElementType.colorPointers!!.size - 1].toInt() and 0xFF], clippingMask, clipLeft, clipTop)
			}
		}
		@Pc(474) var sprite: NativeSprite? = null
		if (mapElementType.defaultIconArchive != -1) {
			sprite = mapElementType.getIcon(false, toolkit!!)
			if (sprite != null) {
				Minimap.drawMapDot(clipTop, sprite, clipLeft, component, clippingMask, offsetY, offsetX)
			}
		}
		if (mapElementType.areaName == null) {
			return
		}
		height = 0
		if (sprite != null) {
			height = sprite.getHeight()
		}
		@Pc(508) var font: AbstractFontRenderer? = Renderers.FONT_RENDERER
		@Pc(510) var fontMetrics: FontMetrics? = FontManager.worldMapAreaFont
		if (mapElementType.baseTextzoom == 1) {
			font = FontManager.aClass68_4
			fontMetrics = FontManager.messageboxFont
		}
		if (mapElementType.baseTextzoom == 2) {
			fontMetrics = FontMetrics.defaultFontMetrics
			font = MiniMenu.minimenuHeader
		}
		Minimap.drawMapElementText(fontMetrics, mapElementType.defaultTextColor, clippingMask, offsetY, clipLeft, mapElementType.areaName, clipTop, font, height, component, offsetX)
		return
	}

	@OriginalMember(owner = "client!ij", name = "a", descriptor = "(ILclient!ha;Z)Z")
	@JvmStatic
	fun buildMinimapSprite(@OriginalArg(0) fromLevel: Int, @OriginalArg(1) toolkit: Toolkit?): Boolean {
		@Pc(11) val startX: Int = (MapRegion.sceneSizeX - 104) / 2
		@Pc(17) val startZ: Int = (MapRegion.sceneSizeY - 104) / 2
		@Pc(19) var mapSpritesCached: Boolean = true
		@Pc(29) var local29: Int
		@Pc(40) var local40: Int
		@Pc(21) var i: Int = startX
		while (i < startX + 104) {
			@Pc(25) var j: Int = startZ
			while (j < startZ + 104) {
				var level: Int = fromLevel
				while (level <= 3) {
					if (SettingsBits.isVisibleFromLevel(j, i, fromLevel, level)) {
						var mapLevel: Int = level
						if (SettingsBits.isBridge(j, i)) {
							mapLevel = level - 1
						}
						if (mapLevel >= 0) {
							mapSpritesCached = mapSpritesCached and Minimap.areMapIconsReady(mapLevel, i, j)
						}
					}
					level++
				}
				j++
			}
			i++
		}
		if (!mapSpritesCached) {
			return false
		}
		@Pc(81) val minimapPixels: IntArray = IntArray(262144)
		var pixelIndex: Int = 0
		while (pixelIndex < minimapPixels.size) {
			minimapPixels[pixelIndex] = -16777216
			pixelIndex++
		}
		Minimap.aClass46_22 = toolkit!!.createNativeSprite(512, minimapPixels, 512, 512)
		Minimap.prepareLights()
		val colourNoActions: Int = ((((util.random() * 20.0).toInt() + 238 - 10) shl 8) + (((util.random() * 20.0).toInt() + 238 - 10) shl 16) + (util.random() * 20.0).toInt() + 228) or 0xFF000000.toInt()
		@Pc(153) val backgroundMinRandomise: Int = (((util.random() * 20.0).toInt() + 228) or 0xFF00) shl 16
		@Pc(172) val pixelClearColour: Int = (util.random() * 8.0).toInt() or ((util.random() * 8.0).toInt() shl 16) or ((util.random() * 8.0).toInt() shl 8)
		@Pc(182) val visibleTiles: Array<BooleanArray?> = Array<BooleanArray?>(InteractiveEntityContainer.poolSize + 3) { BooleanArray(InteractiveEntityContainer.poolSize + 1 + 2) }
		@Pc(188) var local188: Int
		@Pc(192) var local192: Int
		@Pc(194) var local194: Int
		@Pc(196) var local196: Int
		@Pc(205) var local205: Int
		@Pc(212) var local212: Int
		@Pc(223) var local223: Int
		@Pc(249) var local249: Int
		@Pc(253) var local253: Int
		@Pc(306) var xOffset: Int
		@Pc(312) var local312: Int
		@Pc(316) var local316: Int
		@Pc(335) var local335: Int
		@Pc(184) var k: Int = startX
		while (k < startX + 104) {
			var blockZ: Int = startZ
			while (blockZ < startZ + 104) {
				var pixelOffsetX: Int = 0
				var pixelOffsetY: Int = 0
				var minX: Int = k
				if (k > 0) {
					minX = k - 1
					pixelOffsetX += 4
				}
				var minZ: Int = blockZ
				if (blockZ > 0) {
					minZ = blockZ - 1
				}
				var maxX: Int = InteractiveEntityContainer.poolSize + k
				if (maxX < 104) {
					maxX++
				}
				var maxZ: Int = blockZ + InteractiveEntityContainer.poolSize
				if (maxZ < 104) {
					maxZ++
					pixelOffsetY += 4
				}
				toolkit.KA(0, 0, InteractiveEntityContainer.poolSize * 4 + pixelOffsetX, InteractiveEntityContainer.poolSize * 4 + pixelOffsetY)
				toolkit.GA(-16777216)
				@Pc(257) var local257: Int
				var level: Int = fromLevel
				while (level <= 3) {
					var dx: Int = 0
					while (dx <= InteractiveEntityContainer.poolSize) {
						var dz: Int = 0
						while (dz <= InteractiveEntityContainer.poolSize) {
							visibleTiles[dx]!![dz] = SettingsBits.isVisibleFromLevel(minZ + dz, minX + dx, fromLevel, level)
							dz++
						}
						dx++
					}
					Scene.aClass65Array2!![level]!!.drawMinimap(minX, minZ, maxX, maxZ, visibleTiles)
					if (!Minimap.showCollisionMap) {
						dx = -4
						while (dx < InteractiveEntityContainer.poolSize) {
							xOffset = -4
							while (xOffset < InteractiveEntityContainer.poolSize) {
								val tileX: Int = k + dx
								val tileZ: Int = xOffset + blockZ
								if (tileX >= startX && startZ <= tileZ && SettingsBits.isVisibleFromLevel(tileZ, tileX, fromLevel, level)) {
									var mapLevel: Int = level
									if (SettingsBits.isBridge(tileZ, tileX)) {
										mapLevel = level - 1
									}
									if (mapLevel >= 0) {
										Minimap.drawLocOnMinimap(backgroundMinRandomise, (InteractiveEntityContainer.poolSize - xOffset) * 4 + pixelOffsetY - 4, mapLevel, tileZ, tileX, toolkit, dx * 4 + pixelOffsetX, colourNoActions)
									}
								}
								xOffset++
							}
							dx++
						}
					}
					level++
				}
				if (Minimap.showCollisionMap) {
					@Pc(387) val collisionMap: CollisionMap? = MapRegion.aClass104Array3[fromLevel]
					var dx: Int = 0
					while (dx < InteractiveEntityContainer.poolSize) {
						xOffset = 0
						while (xOffset < InteractiveEntityContainer.poolSize) {
							val tileX: Int = k + dx
							val tileZ: Int = xOffset + blockZ
							val flags: Int = collisionMap!!.collisionFlags!![tileX - collisionMap.xOffset]!![tileZ - collisionMap.zOffset]
							if ((flags and 0x40240000) != 0) {
								toolkit.fillRect(4, dx * 4 + pixelOffsetX, (InteractiveEntityContainer.poolSize + -xOffset) * 4 + -4 + pixelOffsetY, 4, -1713569622)
							} else if ((flags and CollisionFlag.WALL_NORTH_BREAKROUTEFINDING) != 0) {
								toolkit.horizontalLine(4, pixelOffsetY + (-xOffset + InteractiveEntityContainer.poolSize) * 4 - 4, -1713569622, dx * 4 + pixelOffsetX)
							} else if ((flags and CollisionFlag.WALL_EAST_BREAKROUTEFINDING) != 0) {
								toolkit.verticalLine(pixelOffsetX + dx * 4 + 3, -1713569622, 4, (InteractiveEntityContainer.poolSize - xOffset) * 4 + pixelOffsetY - 4)
							} else if ((flags and CollisionFlag.WALL_SOUTH_BREAKROUTEFINDING) != 0) {
								toolkit.horizontalLine(4, pixelOffsetY + (InteractiveEntityContainer.poolSize - xOffset) * 4 + 3 - 4, -1713569622, pixelOffsetX + dx * 4)
							} else if ((flags and CollisionFlag.WALL_WEST_BLOCK_BREAKROUTEFINDING) != 0) {
								toolkit.verticalLine(pixelOffsetX + dx * 4, -1713569622, 4, (InteractiveEntityContainer.poolSize - xOffset) * 4 + pixelOffsetY - 4)
							}
							xOffset++
						}
						dx++
					}
				}
				toolkit.aa(pixelOffsetX, pixelOffsetY, InteractiveEntityContainer.poolSize * 4, InteractiveEntityContainer.poolSize * 4, pixelClearColour, 2)
				Minimap.aClass46_22!!.captureScreenRegion((k - startX) * 4 + 48, -((blockZ - startZ) * 4) + 464 + -(InteractiveEntityContainer.poolSize * 4), InteractiveEntityContainer.poolSize * 4, InteractiveEntityContainer.poolSize * 4, pixelOffsetX, pixelOffsetY)
				blockZ += InteractiveEntityContainer.poolSize
			}
			k += InteractiveEntityContainer.poolSize
		}
		toolkit.la()
		toolkit.GA(-16777215)
		InterfaceRectangles.invalidateAllRectangles()
		Minimap.mapElementCount = 0
		Minimap.aClass341_26.clear()
		if (!Minimap.showCollisionMap) {
			var tileX: Int = startX
			while (tileX < startX + 104) {
				var tileZ: Int = startZ
				while (tileZ < startZ + 104) {
					var level: Int = fromLevel
					while (level <= fromLevel + 1 && level <= 3) {
						if (SettingsBits.isVisibleFromLevel(tileZ, tileX, fromLevel, level)) {
							@Pc(678) var location: GameObject? = Scene.getGroundDecoration(level, tileX, tileZ) as GameObject?
							if (location == null) {
								location = Scene.getInteractiveOfType(level, tileX, tileZ, GameObject::class.jClass) as GameObject?
							}
							if (location == null) {
								location = Scene.getWall(level, tileX, tileZ) as GameObject?
							}
							if (location == null) {
								location = Scene.getWallDecoration(level, tileX, tileZ) as GameObject?
							}
							if (location != null) {
								@Pc(719) val locType: LocType? = TypeLists.LOC!!.list(location.getId())
								if (!locType!!.members || LoginManager.memberWorld) {
									var mapCategoryId: Int = locType.mapCategoryid
									if (locType.alternativeIds != null) {
										var alternativeIndex: Int = 0
										while (alternativeIndex < locType.alternativeIds!!.size) {
											if (locType.alternativeIds!![alternativeIndex] != -1) {
												@Pc(748) val locType2: LocType? = TypeLists.LOC!!.list(locType.alternativeIds!![alternativeIndex])
												if (locType2!!.mapCategoryid >= 0) {
													mapCategoryId = locType2.mapCategoryid
												}
											}
											alternativeIndex++
										}
									}
									if (mapCategoryId >= 0) {
										@Pc(768) var hasRandomOffset: Boolean = false
										if (mapCategoryId >= 0) {
											@Pc(775) val mapElementType: MECType? = TypeLists.MEC!!.list(mapCategoryId)
											if (mapElementType != null && mapElementType.hasRandomisedMinimapPosition) {
												hasRandomOffset = true
											}
										}
										var elementX: Int = tileX
										var elementZ: Int = tileZ
										if (hasRandomOffset) {
											@Pc(793) val collisionFlags: Array<IntArray?>? = MapRegion.aClass104Array3[level]!!.collisionFlags
											xOffset = MapRegion.aClass104Array3[level]!!.xOffset
											val zOffset: Int = MapRegion.aClass104Array3[level]!!.zOffset
											var attempt: Int = 0
											while (attempt < 10) {
												val direction: Int = (util.random() * 4.0).toInt()
												if (direction == 0 && startX < elementX && tileX - 3 < elementX && (collisionFlags!![elementX - xOffset - 1]!![elementZ - zOffset] and 0x2C0108) == 0) {
													elementX--
												}
												if (direction == 1 && startX + 104 - 1 > elementX && tileX + 3 > elementX && (collisionFlags!![elementX + 1 - xOffset]!![elementZ - zOffset] and 0x2C0180) == 0) {
													elementX++
												}
												if (direction == 2 && elementZ > startZ && elementZ > tileZ - 3 && (collisionFlags!![elementX - xOffset]!![elementZ - zOffset - 1] and 0x2C0102) == 0) {
													elementZ--
												}
												if (direction == 3 && elementZ < startZ + 104 - 1 && tileZ + 3 > elementZ && (collisionFlags!![elementX - xOffset]!![elementZ + 1 - zOffset] and 0x2C0120) == 0) {
													elementZ++
												}
												attempt++
											}
										}
										Minimap.mapElementLocIds[Minimap.mapElementCount] = locType.id
										Minimap.mapElementX[Minimap.mapElementCount] = elementX
										Minimap.mapElementY[Minimap.mapElementCount] = elementZ
										Minimap.mapElementCount++
									}
								}
							}
						}
						level++
					}
					tileZ++
				}
				tileX++
			}
			if (StaticElements.aClass333_3 != null) {
				Resource.CONFIG!!.loadType = 1
				TypeLists.MEC!!.setCacheSize(64, 1024)
				var elementIndex: Int = 0
				while (elementIndex < StaticElements.aClass333_3!!.elementCount) {
					val packedCoord: Int = StaticElements.aClass333_3!!.bitpackedPlacement!![elementIndex]
					if (PlayerUpdate.localPlayer!!.level.toInt() == (packedCoord shr 28)) {
						val sceneX: Int = ((packedCoord shr 14) and 0x3FFF) - MapRegion.sceneBaseX
						val sceneZ: Int = (packedCoord and 0x3FFF) - MapRegion.sceneBaseY
						if (sceneX >= 0 && sceneX < MapRegion.sceneSizeX && sceneZ >= 0 && sceneZ < MapRegion.sceneSizeY) {
							Minimap.aClass341_26.addLast(IntNode(elementIndex))
						} else {
							@Pc(1072) val mapElementType2: MECType? = TypeLists.MEC!!.list(StaticElements.aClass333_3!!.mapCategoryIds!![elementIndex])
							if (mapElementType2!!.offsets != null && sceneX + mapElementType2.maxOffsetX >= 0 && mapElementType2.minOffsetX + sceneX < MapRegion.sceneSizeX && sceneZ + mapElementType2.maxOffsetY >= 0 && MapRegion.sceneSizeY > sceneZ + mapElementType2.minOffsetY) {
								Minimap.aClass341_26.addLast(IntNode(elementIndex))
							}
						}
					}
					elementIndex++
				}
				TypeLists.MEC!!.setCacheSize(64, 128)
				Resource.CONFIG!!.loadType = 2
				Resource.CONFIG!!.discardUnpacked()
			}
		}
		return true
	}

	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(JIIIILclient!at;IILclient!aa;)V")
	@JvmStatic
	fun drawHintArrow(@OriginalArg(0) maxDistance: Long, @OriginalArg(1) clipTop: Int, @OriginalArg(3) offsetY: Int, @OriginalArg(4) clipLeft: Int, @OriginalArg(5) component: Component?, @OriginalArg(6) offsetX: Int, @OriginalArg(7) arrowSprite: Int, @OriginalArg(8) clippingMask: ComponentSurface?) {
		@Pc(11) val distanceSquared: Int = offsetY * offsetY + offsetX * offsetX
		if (distanceSquared.toLong() > maxDistance) {
			return
		}
		@Pc(39) var radius: Int = kotlin.math.min(component!!.width / 2, component.height / 2)
		if (distanceSquared <= radius * radius) {
			Minimap.drawMapDot(clipTop, StaticMedia.hintMapmarkerSprites!![arrowSprite], clipLeft, component, clippingMask, offsetY, offsetX)
			return
		}
		radius -= 10
		@Pc(53) val angle: Int
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			angle = Camera.camAngleY.toInt() and 0x3FFF
		} else {
			angle = (Camera.camAngleY.toInt() + Minimap.minimapRotation) and 0x3FFF
		}
		@Pc(66) var sinAngle: Int = TextureCombineSource.SINE[angle]
		@Pc(70) var cosAngle: Int = TextureCombineSource.COSINE[angle]
		if (Camera.cameraCalculationType != CameraMode.MODE_FOLLOWCOORD) {
			cosAngle = cosAngle * 256 / (Minimap.minimapZoom + 256)
			sinAngle = sinAngle * 256 / (Minimap.minimapZoom + 256)
		}
		@Pc(101) val rotatedX: Int = (cosAngle * offsetX + sinAngle * offsetY) shr 14
		@Pc(112) val rotatedY: Int = (cosAngle * offsetY - sinAngle * offsetX) shr 14
		@Pc(118) val theta: Double = kotlin.math.atan2(rotatedX.toDouble(), rotatedY.toDouble())
		@Pc(125) val edgeX: Int = (radius.toDouble() * kotlin.math.sin(theta)).toInt()
		@Pc(132) val edgeY: Int = (kotlin.math.cos(theta) * radius.toDouble()).toInt()
		StaticMedia.hintMapedgeSprites!![arrowSprite]!!.drawRotatedCenter(component.width.toFloat() / 2.0f + clipLeft.toFloat() + edgeX.toFloat(), clipTop.toFloat() + component.height.toFloat() / 2.0f - edgeY.toFloat(), 4096, (-theta / 6.283185307179586 * 65535.0).toInt())
	}

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(Lclient!at;Lclient!ha;III)V")
	@JvmStatic
	fun drawMinimap(@OriginalArg(0) component: Component?, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) clipLeft: Int, @OriginalArg(3) clipTop: Int) {
		@Pc(10) val clippingMask: ComponentSurface? = component!!.getClippingMask(toolkit)
		if (clippingMask == null) {
			return
		}
		toolkit!!.KA(clipLeft, clipTop, clipLeft + component.width, component.height + clipTop)
		if (ComponentMinimap.minimapState == 2 || ComponentMinimap.minimapState == 5 || Minimap.aClass46_22 == null) {
			toolkit.A(-16777216, clippingMask, clipLeft, clipTop)
			return
		}
		@Pc(64) val cameraTargetAbsX: Int
		@Pc(52) val cameraTargetAbsZ: Int
		@Pc(61) val rotation: Int
		@Pc(49) val zoom: Int
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			cameraTargetAbsZ = Camera.cameraTargetAbsZ
			cameraTargetAbsX = Camera.cameraTargetAbsX
			zoom = 4096
			rotation = (-Camera.camAngleY).toInt() and 0x3FFF
		} else {
			zoom = 4096 - Minimap.minimapZoom * 16
			cameraTargetAbsZ = PlayerUpdate.localPlayer!!.z
			rotation = ((-Camera.camAngleY).toInt() + Minimap.minimapRotation) and 0x3FFF
			cameraTargetAbsX = PlayerUpdate.localPlayer!!.x
		}
		@Pc(92) val pivotX: Int = cameraTargetAbsX / 128 + 48 + 208 - MapRegion.sceneSizeX * 2
		@Pc(108) val pivotY: Int = MapRegion.sceneSizeY * 4 + 208 + 48 - MapRegion.sceneSizeY * 2 - cameraTargetAbsZ / 128
		Minimap.aClass46_22!!.rotate(component.width.toFloat() / 2.0f + clipLeft.toFloat(), component.height.toFloat() / 2.0f + clipTop.toFloat(), pivotX.toFloat(), pivotY.toFloat(), zoom, rotation shl 2, clippingMask, clipLeft, clipTop)
		@Pc(161) var local161: Int
		@Pc(171) var local171: Int
		@Pc(182) var local182: Int
		@Pc(192) var local192: Int
		@Pc(144) var intNode: IntNode? = Minimap.aClass341_26.first() as IntNode?
		while (intNode != null) {
			@Pc(149) val index: Int = intNode.value
			val sceneX: Int = ((StaticElements.aClass333_3!!.bitpackedPlacement!![index] shr 14) and 0x3FFF) - MapRegion.sceneBaseX
			val sceneZ: Int = (StaticElements.aClass333_3!!.bitpackedPlacement!![index] and 0x3FFF) - MapRegion.sceneBaseY
			val offsetX: Int = sceneX * 4 + 2 - cameraTargetAbsX / 128
			val offsetY: Int = sceneZ * 4 + 2 - cameraTargetAbsZ / 128
			Minimap.drawMec(component, clipTop, clippingMask, StaticElements.aClass333_3!!.mapCategoryIds!![index], offsetX, clipLeft, offsetY, toolkit)
			intNode = Minimap.aClass341_26.next() as IntNode?
		}
		var elementIndex: Int = 0
		while (elementIndex < Minimap.mapElementCount) {
			val offsetX: Int = Minimap.mapElementX[elementIndex] * 4 + 2 - cameraTargetAbsX / 128
			val offsetY: Int = Minimap.mapElementY[elementIndex] * 4 + 2 - cameraTargetAbsZ / 128
			@Pc(248) var locType: LocType? = TypeLists.LOC!!.list(Minimap.mapElementLocIds[elementIndex])
			if (locType!!.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider)
				if (locType == null || locType.mapCategoryid == -1) {
					elementIndex++
					continue
				}
			}
			Minimap.drawMec(component, clipTop, clippingMask, locType.mapCategoryid, offsetX, clipLeft, offsetY, toolkit)
			elementIndex++
		}
		@Pc(335) var local335: Int
		@Pc(346) var local346: Int
		@Pc(291) var objStack: ObjStack? = ObjStack.GROUND_ITEMS.first() as ObjStack?
		while (objStack != null) {
			val plane: Int = ((objStack.key shr 28) and 0x3L).toInt()
			if (ComponentMinimap.MAP_PLANE == plane) {
				val sceneX: Int = (objStack.key and 0x3FFFL).toInt() - MapRegion.sceneBaseX
				@Pc(324) val sceneZ: Int = ((objStack.key shr 14) and 0x3FFFL).toInt() - MapRegion.sceneBaseY
				val offsetX: Int = sceneX * 4 + 2 - cameraTargetAbsX / 128
				val offsetY: Int = sceneZ * 4 + 2 - cameraTargetAbsZ / 128
				Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites!![0], clipLeft, component, clippingMask, offsetY, offsetX)
			}
			objStack = ObjStack.GROUND_ITEMS.next() as ObjStack?
		}
		@Pc(438) var local438: Int
		var npcIndex: Int = 0
		while (npcIndex < NPCList.NPC_UPDATE_INDEX) {
			@Pc(379) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[npcIndex].toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				@Pc(384) val npcEntity: NPCEntity? = npcEntityNode.npc
				if (npcEntity!!.hasType() && PlayerUpdate.localPlayer!!.level == npcEntity.level) {
					@Pc(400) var npcType: NPCType? = npcEntity.npcType
					if (npcType != null && npcType.alternativeIds != null) {
						npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
					}
					if (npcType != null && npcType.displayOnMinimap && npcType.visible) {
						val offsetX: Int = npcEntity.x / 128 - cameraTargetAbsX / 128
						val offsetY: Int = npcEntity.z / 128 - cameraTargetAbsZ / 128
						if (npcType.mecId == -1) {
							Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites!![1], clipLeft, component, clippingMask, offsetY, offsetX)
						} else {
							Minimap.drawMec(component, clipTop, clippingMask, npcType.mecId, offsetX, clipLeft, offsetY, toolkit)
						}
					}
				}
			}
			npcIndex++
		}
		val outPlayerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
		@Pc(479) val outPlayerIndices: IntArray? = PlayerUpdate.OUT_PLAYER_INDICES
		@Pc(530) var local530: Int
		@Pc(534) var local534: Int
		@Pc(566) var local566: Int
		var playerIndex: Int = 0
		while (playerIndex < outPlayerCount) {
			@Pc(489) val playerEntity: PlayerEntity? = PlayerUpdate.players!![outPlayerIndices!![playerIndex]]
			if (playerEntity != null && playerEntity.hasAppearance() && !playerEntity.hidden && PlayerUpdate.localPlayer !== playerEntity && PlayerUpdate.localPlayer!!.level == playerEntity.level) {
				val offsetX: Int = playerEntity.x / 128 - cameraTargetAbsX / 128
				val offsetY: Int = playerEntity.z / 128 - cameraTargetAbsZ / 128
				@Pc(532) var isFriend: Boolean = false
				var friendIndex: Int = 0
				while (friendIndex < FriendList.FRIEND_COUNT) {
					if (playerEntity.displayName!!.equals(FriendList.friendNames!![friendIndex]) && FriendList.friendWorldIds!![friendIndex] != 0) {
						isFriend = true
						break
					}
					friendIndex++
				}
				@Pc(564) var inFriendsChat: Boolean = false
				var memberIndex: Int = 0
				while (memberIndex < FriendsChat.clanChatUserCount) {
					if (playerEntity.displayName!!.equals(FriendsChat.clanChatUsers!![memberIndex]!!.username)) {
						inFriendsChat = true
						break
					}
					memberIndex++
				}
				@Pc(586) var onSameTeam: Boolean = false
				if (PlayerUpdate.localPlayer!!.teamId != 0 && playerEntity.teamId != 0 && playerEntity.teamId == PlayerUpdate.localPlayer!!.teamId) {
					onSameTeam = true
				}
				if (playerEntity.pMapdot) {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites!![6], clipLeft, component, clippingMask, offsetY, offsetX)
				} else if (isFriend) {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites!![3], clipLeft, component, clippingMask, offsetY, offsetX)
				} else if (inFriendsChat) {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites!![5], clipLeft, component, clippingMask, offsetY, offsetX)
				} else if (onSameTeam) {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites!![4], clipLeft, component, clippingMask, offsetY, offsetX)
				} else {
					Minimap.drawMapDot(clipTop, StaticMedia.mapdotSprites!![2], clipLeft, component, clippingMask, offsetY, offsetX)
				}
			}
			playerIndex++
		}
		@Pc(685) val hintArrows: Array<HintArrow?>? = PlayerUpdate.HINT_ARROWS
		@Pc(772) var local772: Int
		var arrowIndex: Int = 0
		while (arrowIndex < hintArrows!!.size) {
			@Pc(693) val hintArrow: HintArrow? = hintArrows[arrowIndex]
			if (hintArrow != null && hintArrow.targetType != HintArrowType.CLEAR && ClientState.clientCycle % 20 < 10) {
				@Pc(735) var local735: Int
				if (hintArrow.targetType == HintArrowType.NPC) {
					@Pc(720) val npcEntityNode2: NPCEntityNode? = NPCList.NPC_MAP.get(hintArrow.targetIndex.toLong()) as NPCEntityNode?
					if (npcEntityNode2 != null) {
						@Pc(725) val npcEntity2: NPCEntity? = npcEntityNode2.npc
						val offsetX: Int = npcEntity2!!.x / 128 - cameraTargetAbsX / 128
						val offsetY: Int = npcEntity2.z / 128 - cameraTargetAbsZ / 128
						Minimap.drawHintArrow(360000L, clipTop, offsetY, clipLeft, component, offsetX, hintArrow.arrowSprite, clippingMask)
					}
				}
				if (hintArrow.targetType == HintArrowType.TILE_CENTRE) {
					val offsetX: Int = hintArrow.x / 128 - cameraTargetAbsX / 128
					val offsetY: Int = hintArrow.y / 128 - cameraTargetAbsZ / 128
					@Pc(788) val distance: Long = (hintArrow.distance shl 7).toLong()
					@Pc(792) val maxDistance: Long = distance * distance
					Minimap.drawHintArrow(maxDistance, clipTop, offsetY, clipLeft, component, offsetX, hintArrow.arrowSprite, clippingMask)
				}
				if (hintArrow.targetType == HintArrowType.PLAYER && hintArrow.targetIndex >= 0 && PlayerUpdate.players!!.size > hintArrow.targetIndex) {
					@Pc(829) val playerEntity2: PlayerEntity? = PlayerUpdate.players!![hintArrow.targetIndex]
					if (playerEntity2 != null) {
						val offsetX: Int = playerEntity2.x / 128 - cameraTargetAbsX / 128
						val offsetY: Int = playerEntity2.z / 128 - cameraTargetAbsZ / 128
						Minimap.drawHintArrow(360000L, clipTop, offsetY, clipLeft, component, offsetX, hintArrow.arrowSprite, clippingMask)
					}
				}
			}
			arrowIndex++
		}
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			return
		}
		if (Minimap.minimapFlagX != 0) {
			val offsetX: Int = Minimap.minimapFlagX * 4 + PlayerUpdate.localPlayer!!.getSize() * 2 + 2 - cameraTargetAbsX / 128 - 2
			val offsetY: Int = Minimap.minimapFlagY * 4 + (PlayerUpdate.localPlayer!!.getSize() - 1) * 2 + 2 - cameraTargetAbsZ / 128
			Minimap.drawMapDot(clipTop, StaticMedia.mapflagSprites!![if (Minimap.minimapContainsFlag) 1 else 0], clipLeft, component, clippingMask, offsetY, offsetX)
		}
		if (!PlayerUpdate.localPlayer!!.hidden) {
			toolkit.fillRect(3, clipLeft + component.width / 2 - 1, component.height / 2 + -1 + clipTop, 3, -1)
			return
		}
	}

	@OriginalMember(owner = "client!tk", name = "a", descriptor = "I")
	@JvmField
	var visibleFromLevel: Int = -1

	@OriginalMember(owner = "client!uba", name = "a", descriptor = "(IIZIIILclient!ha;II)V")
	@JvmStatic
	fun drawLocOnMinimap(@OriginalArg(0) colourHasActions: Int, @OriginalArg(1) y: Int, @OriginalArg(3) level: Int, @OriginalArg(4) sceneZ: Int, @OriginalArg(5) sceneX: Int, @OriginalArg(6) toolkit: Toolkit?, @OriginalArg(7) x: Int, @OriginalArg(8) colourNoActions: Int) {
		@Pc(11) var location: GameObject? = Scene.getWall(level, sceneX, sceneZ) as GameObject?
		@Pc(20) var locType: LocType?
		@Pc(26) var rotation: Int
		@Pc(30) var shape: Int
		@Pc(46) var colour: Int
		if (location != null) {
			locType = TypeLists.LOC!!.list(location.getId())
			rotation = location.getRotation() and 0x3
			shape = location.getShape()
			if (locType!!.mapIconId == -1) {
				colour = colourNoActions
				if (locType.hasActions > LocInteractivity.NONINTERACTIVE) {
					colour = colourHasActions
				}
				if (shape == LocShapes.WALL_STRAIGHT || shape == LocShapes.WALL_L) {
					if (rotation == 0) {
						toolkit!!.verticalLine(x, colour, 4, y)
					} else if (rotation == 1) {
						toolkit!!.horizontalLine(4, y, colour, x)
					} else if (rotation == 2) {
						toolkit!!.verticalLine(x + 3, colour, 4, y)
					} else if (rotation == 3) {
						toolkit!!.horizontalLine(4, y + 3, colour, x)
					}
				}
				if (shape == LocShapes.WALL_SQUARECORNER) {
					if (rotation == 0) {
						toolkit!!.fillRect(1, x, y, 1, colour)
					} else if (rotation == 1) {
						toolkit!!.fillRect(1, x + 3, y, 1, colour)
					} else if (rotation == 2) {
						toolkit!!.fillRect(1, x + 3, y + 3, 1, colour)
					} else if (rotation == 3) {
						toolkit!!.fillRect(1, x, y + 3, 1, colour)
					}
				}
				if (shape == LocShapes.WALL_L) {
					if (rotation == 0) {
						toolkit!!.horizontalLine(4, y, colour, x)
					} else if (rotation == 1) {
						toolkit!!.verticalLine(x + 3, colour, 4, y)
					} else if (rotation == 2) {
						toolkit!!.horizontalLine(4, y + 3, colour, x)
					} else if (rotation == 3) {
						toolkit!!.verticalLine(x, colour, 4, y)
					}
				}
			} else {
				ComponentMinimap.drawMapIcon(x, locType, y, toolkit, rotation)
			}
		}
		location = Scene.getInteractiveOfType(level, sceneX, sceneZ, GameObject::class.jClass) as GameObject?
		if (location != null) {
			locType = TypeLists.LOC!!.list(location.getId())
			rotation = location.getRotation() and 0x3
			shape = location.getShape()
			if (locType!!.mapIconId != -1) {
				ComponentMinimap.drawMapIcon(x, locType, y, toolkit, rotation)
			} else if (shape == LocShapes.WALL_DIAGONAL) {
				colour = -1118482
				if (locType.hasActions > 0) {
					colour = -1179648
				}
				if (rotation == 0 || rotation == 2) {
					toolkit!!.drawOpaqueLine(y + 3, x, x + 3, colour, y)
				} else {
					toolkit!!.drawOpaqueLine(y, x, x + 3, colour, y + 3)
				}
			}
		}
		location = Scene.getGroundDecoration(level, sceneX, sceneZ) as GameObject?
		if (location == null) {
			return
		}
		locType = TypeLists.LOC!!.list(location.getId())
		rotation = location.getRotation() and 0x3
		if (locType!!.mapIconId != -1) {
			ComponentMinimap.drawMapIcon(x, locType, y, toolkit, rotation)
			return
		}
	}

	@OriginalMember(owner = "client!ok", name = "a", descriptor = "(ILclient!mi;ILclient!at;Lclient!aa;III)V")
	@JvmStatic
	fun drawMapDot(@OriginalArg(0) clipTop: Int, @OriginalArg(1) sprite: NativeSprite?, @OriginalArg(2) clipLeft: Int, @OriginalArg(3) component: Component?, @OriginalArg(4) clippingMask: ComponentSurface?, @OriginalArg(5) offsetY: Int, @OriginalArg(7) offsetX: Int) {
		if (sprite == null) {
			return
		}
		@Pc(17) val rotation: Int
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			rotation = Camera.camAngleY.toInt() and 0x3FFF
		} else {
			rotation = (Camera.camAngleY.toInt() + Minimap.minimapRotation) and 0x3FFF
		}
		@Pc(36) val maxRadius: Int = kotlin.math.max(component!!.width / 2, component.height / 2) + 10
		@Pc(45) val distanceSquared: Int = offsetX * offsetX + offsetY * offsetY
		if (maxRadius * maxRadius < distanceSquared) {
			return
		}
		@Pc(55) var sinAngle: Int = TextureCombineSource.SINE[rotation]
		@Pc(59) var cosAngle: Int = TextureCombineSource.COSINE[rotation]
		if (Camera.cameraCalculationType != CameraMode.MODE_FOLLOWCOORD) {
			cosAngle = cosAngle * 256 / (Minimap.minimapZoom + 256)
			sinAngle = sinAngle * 256 / (Minimap.minimapZoom + 256)
		}
		@Pc(88) val rotatedX: Int = (offsetX * cosAngle + offsetY * sinAngle) shr 14
		@Pc(99) val rotatedY: Int = (offsetY * cosAngle - sinAngle * offsetX) shr 14
		sprite.drawWithSurface(rotatedX + component.width / 2 + clipLeft - sprite.scaleWidth() / 2, clipTop - -(component.height / 2) + -rotatedY + -(sprite.scaleHeight() / 2), clippingMask, clipLeft, clipTop)
	}
	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(Lclient!oea;ILclient!aa;IILjava/lang/String;ILclient!da;ILclient!at;II)V")
	@JvmStatic
	fun drawMapElementText(@OriginalArg(0) fontMetrics: FontMetrics?, @OriginalArg(1) color: Int, @OriginalArg(2) clippingMask: ComponentSurface?, @OriginalArg(3) offsetZ: Int, @OriginalArg(4) clipLeft: Int, @OriginalArg(5) string: String?, @OriginalArg(6) clipTop: Int, @OriginalArg(7) font: AbstractFontRenderer?, @OriginalArg(8) iconHeight: Int, @OriginalArg(9) component: Component?, @OriginalArg(10) offsetX: Int) {
		@Pc(13) val angle: Int
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			angle = Camera.camAngleY.toInt() and 0x3FFF
		} else {
			angle = (Minimap.minimapRotation + Camera.camAngleY.toInt()) and 0x3FFF
		}
		@Pc(34) val maxDistance: Int = kotlin.math.max(component!!.width / 2, component.height / 2) + 10
		@Pc(43) val distanceSq: Int = offsetX * offsetX + offsetZ * offsetZ
		if (maxDistance * maxDistance < distanceSq) {
			return
		}
		@Pc(57) var sin: Int = TextureCombineSource.SINE[angle]
		@Pc(61) var cos: Int = TextureCombineSource.COSINE[angle]
		if (Camera.cameraCalculationType != CameraMode.MODE_FOLLOWCOORD) {
			cos = cos * 256 / (Minimap.minimapZoom + 256)
			sin = sin * 256 / (Minimap.minimapZoom + 256)
		}
		@Pc(90) val mapX: Int = (sin * offsetZ + offsetX * cos) shr 14
		@Pc(101) val mapY: Int = (cos * offsetZ - offsetX * sin) shr 14
		@Pc(108) val textWidth: Int = fontMetrics!!.getTextWidthMultiline(string, 100, null)
		@Pc(116) val textHeight: Int = fontMetrics.getTextHeightMultiline(null, 0, 100, string)
		@Pc(122) val textX: Int = mapX - textWidth / 2
		if (-component.width <= textX && component.width >= textX && -component.height <= mapY && mapY <= component.height) {
			font!!.drawText(null, 0, 0, null, color, 0, 50, clippingMask, clipTop + component.height / 2 - iconHeight - mapY - textHeight, clipLeft, clipTop, textWidth, component.width / 2 + clipLeft + textX, 1, string)
		}
	}

	@OriginalMember(owner = "client!il", name = "q", descriptor = "[I")
	@JvmField
	val mapElementX: IntArray = IntArray(1000)

	@OriginalMember(owner = "client!fi", name = "T", descriptor = "I")
	@JvmField
	var mapElementCount: Int = 0

	@OriginalMember(owner = "client!fd", name = "J", descriptor = "[I")
	@JvmField
	val mapElementY: IntArray = IntArray(1000)
	@OriginalMember(owner = "client!co", name = "W", descriptor = "Lclient!uh;")
	@JvmField
	val aClass341_26: Deque = Deque()
	@OriginalMember(owner = "client!mo", name = "i", descriptor = "Lclient!mi;")
	@JvmField
	var aClass46_22: NativeSprite? = null

	@OriginalMember(owner = "client!gf", name = "d", descriptor = "Z")
	@JvmField
	var showCollisionMap: Boolean = false

	@OriginalMember(owner = "client!dfa", name = "U", descriptor = "I")
	@JvmField
	var minimapZoomStep: Int = 1
	@OriginalMember(owner = "client!tha", name = "s", descriptor = "I")
	@JvmField
	var minimapFlagX: Int = -1

	@OriginalMember(owner = "client!uc", name = "J", descriptor = "I")
	@JvmField
	var minimapRotation: Int = 0

	@OriginalMember(owner = "client!gh", name = "Sb", descriptor = "I")
	@JvmField
	var minimapZoom: Int = 0

	@OriginalMember(owner = "client!uha", name = "e", descriptor = "[I")
	@JvmField
	val mapElementLocIds: IntArray = IntArray(1000)

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(ILclient!at;BI)V")
	@JvmStatic
	fun drawCompass(@OriginalArg(0) y: Int, @OriginalArg(1) component: Component?, @OriginalArg(3) x: Int) {
		@Pc(8) val clippingMask: ComponentSurface? = component!!.getClippingMask(ClientState.activeToolkit)
		if (clippingMask == null) {
			return
		}
		ClientState.activeToolkit!!.KA(x, y, component.width + x, y - -component.height)
		if (ComponentMinimap.minimapState >= 3) {
			ClientState.activeToolkit!!.A(-16777216, clippingMask, x, y)
		} else {
			StaticMedia.compassSprite!!.drawRotatedSprite(component.width.toFloat() / 2.0f + x.toFloat(), y.toFloat() + component.height.toFloat() / 2.0f, ((-Camera.camAngleY).toInt() and 0x3FFF) shl 2, clippingMask, x, y)
		}
	}

	@OriginalMember(owner = "client!mc", name = "e", descriptor = "I")
	@JvmField
	var minimapFlagY: Int = -1
	@OriginalMember(owner = "client!ie", name = "t", descriptor = "Z")
	@JvmField
	var minimapContainsFlag: Boolean = true

	@OriginalMember(owner = "client!le", name = "a", descriptor = "(IIIB)Z")
	@JvmStatic
	fun areMapIconsReady(@OriginalArg(0) level: Int, @OriginalArg(1) x: Int, @OriginalArg(2) z: Int): Boolean {
		@Pc(14) var location: GameObject? = Scene.getWall(level, x, z) as GameObject?
		@Pc(16) var ready: Boolean = true
		if (location != null) {
			ready = Minimap.isLocMapIconReady(location) and true
		}
		location = Scene.getInteractiveOfType(level, x, z, GameObject::class.jClass) as GameObject?
		if (location != null) {
			ready = ready and Minimap.isLocMapIconReady(location)
		}
		location = Scene.getGroundDecoration(level, x, z) as GameObject?
		if (location != null) {
			ready = ready and Minimap.isLocMapIconReady(location)
		}
		return ready
	}

	@OriginalMember(owner = "client!fd", name = "b", descriptor = "(B)V")
	@JvmStatic
	fun resetMinimapFlag() {
		Minimap.minimapFlagX = -1
		Minimap.minimapFlagY = -1
		ComponentMinimap.minimapState = 0
	}

	@OriginalMember(owner = "client!pda", name = "g", descriptor = "(I)V")
	@JvmStatic
	fun resetMinimapFlagAndPlane() {
		Minimap.minimapFlagX = -1
		Minimap.minimapFlagY = -1
		ComponentMinimap.MAP_PLANE = -1
		ComponentMinimap.minimapState = 0
	}

	@OriginalMember(owner = "client!ew", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun prepareLights() {
		EnvironmentManager.toolkit!!.xa((GamePreferences.uid!!.brightness!!.getValue().toFloat() * 0.1f + 0.7f) * 1.1523438f)
		EnvironmentManager.toolkit!!.ZA(EnvironmentManager.defaultSunColour, 0.69921875f, 1.2f, -200.0f, -240.0f, -200.0f)
		EnvironmentManager.toolkit!!.L(EnvironmentManager.defaultFogColour, -1, 0)
		EnvironmentManager.toolkit!!.setSunCubeTexture(EnvironmentManager.aClass78_4)
	}
	@OriginalMember(owner = "client!ij", name = "a", descriptor = "(IIBI)Lclient!wm;")
	@JvmStatic
	fun createMovePacket(@OriginalArg(0) localY: Int, @OriginalArg(1) localX: Int, @OriginalArg(3) moveType: Int): TcpPacket? {
		@Pc(5) var packet: TcpPacket? = null
		if (moveType == 0) {
			packet = TcpPacket.createPacket(ClientProt.MOVE_GAMECLICK, TcpPacket.isaac)
		}
		if (moveType == 1) {
			packet = TcpPacket.createPacket(ClientProt.MOVE_MINIMAPCLICK, TcpPacket.isaac)
		}
		packet!!.buffer!!.writeShortLittle(localY + MapRegion.sceneBaseY)
		packet.buffer!!.writeByteAdd(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
		packet.buffer!!.writeShortAdd(MapRegion.sceneBaseX + localX)
		Minimap.minimapFlagX = localX
		Minimap.minimapFlagY = localY
		Minimap.minimapContainsFlag = false
		PulseEvent.delayedMinimapFlagUpdate()
		return packet
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(Lclient!se;I)Z")
	@JvmStatic
	fun isLocMapIconReady(@OriginalArg(0) location: GameObject?): Boolean {
		@Pc(10) val locType: LocType? = TypeLists.LOC!!.list(location!!.getId())
		if (locType!!.mapIconId == -1) {
			return true
		} else {
			@Pc(22) val msiType: MapSpriteType? = TypeLists.renderer!!.list(locType.mapIconId)
			return if (msiType!!.fileId == -1) true else msiType.ready()
		}
	}

	@OriginalMember(owner = "client!vi", name = "s", descriptor = "I")
	@JvmField
	var minimapRandomizationCycle: Int = 0

	@OriginalMember(owner = "client!ct", name = "E", descriptor = "I")
	@JvmField
	var minimapRotationStep: Int = 2
}
