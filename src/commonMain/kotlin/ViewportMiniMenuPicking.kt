import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.ByteArrayPool
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.ObjStackEntity
import com.jagex.game.runetek5.PickList
import com.jagex.game.runetek5.entity.PickableEntity
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity
import com.jagex.game.runetek5.network.world.WorldList
import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.config.param.ParamType
import com.jagex.game.runetek5.cache.node.ObjStackEntry
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.loading.screen.op.instance.PriorityIfPositionUpdate
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.scene.MapLoader
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmField

object ViewportMiniMenuPicking {

	@OriginalMember(owner = "client!dba", name = "a", descriptor = "(Lclient!ha;IIB)V")
	@JvmStatic
	fun tileHovered(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseX: Int, @OriginalArg(2) mouseY: Int) {
		if (mouseX < 0 || mouseY < 0 || Camera.projectionScaleX == 0 || ByteArrayPool.projectionScaleY == 0) {
			return
		}
		@Pc(56) val matrix: Matrix?
		@Pc(45) val projectionCenterX: Int
		@Pc(41) val projectionCenterY: Int
		@Pc(47) var projectionScaleX: Int
		@Pc(43) var projectionScaleY: Int
		@Pc(71) val adjustedMouseX: Int
		@Pc(69) val adjustedMouseY: Int
		if (OrthoMode.orthoEnabled) {
			OrthoMode.applyOrthoViewport(false)
			matrix = toolkit!!.camera()
			@Pc(82) val projectionParams: IntArray? = toolkit.Y()
			projectionCenterX = projectionParams!![0]
			projectionCenterY = projectionParams[1]
			projectionScaleY = projectionParams[3]
			projectionScaleX = projectionParams[2]
			adjustedMouseX = mouseX + OrthoMode.getOrthoPickOffsetX(false)
			adjustedMouseY = OrthoMode.getOrthoPickOffsetY(false) + mouseY
		} else {
			toolkit!!.DA(Camera.pickingProjectionCenterX, Camera.pickingProjectionCenterY, Camera.projectionScaleX, ByteArrayPool.projectionScaleY)
			projectionCenterY = Camera.pickingProjectionCenterY
			projectionScaleY = ByteArrayPool.projectionScaleY
			projectionCenterX = Camera.pickingProjectionCenterX
			projectionScaleX = Camera.projectionScaleX
			toolkit.KA(InterfaceComponentRenderer.chatDisplayY, InterfaceComponentRenderer.chatDisplayX, Camera.projectionScaleX, ByteArrayPool.projectionScaleY)
			matrix = toolkit.createMatrix()
			matrix!!.createCamera(Camera.pickingCameraX, Camera.pickingCameraZ, Camera.pickingCameraY, Camera.pickingCameraPitch, Camera.pickingCameraYaw, Camera.pickingCameraRoll)
			toolkit.setCamera(matrix)
			adjustedMouseY = mouseY
			adjustedMouseX = mouseX
		}
		MapRegion.applyViewDepthRange(true)
		if (projectionScaleX == 0) {
			projectionScaleX = 1
		}
		if (projectionScaleY == 0) {
			projectionScaleY = 1
		}
		@Pc(142) var local142: Int
		@Pc(153) var nearX: Int
		@Pc(171) var farY: Int
		@Pc(280) var local280: Int
		@Pc(289) var local289: Int
		@Pc(300) var local300: Int
		@Pc(311) var local311: Int
		@Pc(134) var local134: Int
		@Pc(357) var local357: Int
		if (Scene.aClass65Array2 != null && (!MiniMenu.interfaceSelectionActive || (MiniMenu.useTargetMask and 0x40) != 0)) {
			var tileX: Int = -1
			@Pc(136) var tileY: Int = -1
			@Pc(139) val nearZ: Int = toolkit.i()
			val farZ: Int = toolkit.XA()
			@Pc(162) val nearY: Int
			@Pc(179) val farX: Int
			if (OrthoMode.orthographicProjection) {
				farX = HintArrow.scaledOrthoZoom * (adjustedMouseX - projectionCenterX) / projectionScaleX
				nearX = farX
				farY = HintArrow.scaledOrthoZoom * (adjustedMouseY - projectionCenterY) / projectionScaleY
				nearY = farY
			} else {
				nearX = nearZ * (adjustedMouseX - projectionCenterX) / projectionScaleX
				nearY = nearZ * (adjustedMouseY - projectionCenterY) / projectionScaleY
				farY = farZ * (adjustedMouseY - projectionCenterY) / projectionScaleY
				farX = (adjustedMouseX - projectionCenterX) * farZ / projectionScaleX
			}
			@Pc(218) val nearPoint: IntArray = intArrayOf(nearX, nearY, nearZ)
			@Pc(233) val farPoint: IntArray = intArrayOf(farX, farY, farZ)
			matrix!!.project(nearPoint)
			matrix.project(farPoint)
			@Pc(267) val rayT: Float = ViewportMiniMenuPicking.traceRayToGround(nearPoint[1].toFloat(), farPoint[0].toFloat(), nearPoint[2].toFloat(), farPoint[2].toFloat(), 4, nearPoint[0].toFloat(), farPoint[1].toFloat())
			if (rayT > 0.0f) {
				val rayDeltaX: Int = farPoint[0] - nearPoint[0]
				val rayDeltaZ: Int = farPoint[2] - nearPoint[2]
				val groundHitX: Int = (nearPoint[0].toFloat() + rayDeltaX.toFloat() * rayT).toInt()
				val groundHitZ: Int = (nearPoint[2].toFloat() + rayDeltaZ.toFloat() * rayT).toInt()
				tileX = (((PlayerUpdate.localPlayer!!.getSize() - 1) shl 8) + groundHitX) shr 9
				tileY = (groundHitZ + ((PlayerUpdate.localPlayer!!.getSize() - 1) shl 8)) shr 9
				PluginRuntime.hoveredTile(tileX, tileY) // Not part of the original client: the plugins' hovered tile.
				@Pc(339) val level: Byte = PlayerUpdate.localPlayer!!.level
				if (level < 3 && (SettingsBits.tileFunctionMasks!![1]!![groundHitX shr 9]!![groundHitZ shr 9].toInt() and 0x2) != 0) {
					val bridgeLevel: Int = level + 1
				}
			}
			if (tileX != -1 && tileY != -1) {
				if (MiniMenu.interfaceSelectionActive && (MiniMenu.useTargetMask and 0x40) != 0) {
					@Pc(423) val component: Component? = Component.getComponentWithSlot(MiniMenu.slotId, MiniMenuBuilder.USE_OPTIONS_FLAGS)
					if (component == null) {
						Static657.cancelInterfaceSelection()
					} else {
						MiniMenu.addEntry(false, " ->", tileY, true, tileX, -1, true, MiniMenuAction.TGT_GROUND, ((tileX shl 0) or tileY).toLong(), MiniMenu.useOnType, 0L, MiniMenu.useOnCursor)
					}
				} else {
					if (MiniMenu.showFaceHere) {
						MiniMenu.addEntry(false, "", tileY, true, tileX, -1, true, MiniMenuAction.FACE_SQUARE, (tileY or (tileX shl 0)).toLong(), LocalizedText.aClass351_29.get(ClientState.language), 0L, -1)
					}
					// Not part of the original client: the plugins' entries for the tile (PluginRuntime).
					PluginRuntime.menuTarget(MenuTarget.TILE, tileX, tileY, PlayerUpdate.localPlayer!!.level.toInt(), 0, 0L)
					MiniMenu.addEntry(false, "", tileY, true, tileX, -1, true, MiniMenuAction.WALK, (tileY or (tileX shl 0)).toLong(), MiniMenu.walkHereText, 0L, CursorManager.activeCursorId)
				}
			}
		}
		if (OrthoMode.orthoEnabled) {
			OrthoMode.restoreOrthoCamera()
		}
		var passIndex: Int = 0
		while ((if (OrthoMode.orthoEnabled) 2 else 1) > passIndex) {
			@Pc(462) val firstPass: Boolean = passIndex == 0
			@Pc(468) val pickList: PickList? = if (firstPass) PickList.aClass182_1 else PickList.aClass182_3
			var pickMouseX: Int = mouseX
			var pickMouseY: Int = mouseY
			if (OrthoMode.orthoEnabled) {
				OrthoMode.applyOrthoViewport(firstPass)
				pickMouseX = mouseX + OrthoMode.getOrthoPickOffsetX(firstPass)
				pickMouseY = mouseY + OrthoMode.getOrthoPickOffsetY(firstPass)
			}
			@Pc(492) val linkedList: EntityNodeList? = pickList!!.entities
			@Pc(497) var pickableEntity: PickableEntity? = linkedList!!.getFirst() as PickableEntity?
			while (pickableEntity != null) {
				if ((ViewportMiniMenuPicking.showAdvert || PlayerUpdate.localPlayer!!.level == pickableEntity.entity!!.level) && pickableEntity.hovered(pickMouseY, pickMouseX, toolkit)) {
					@Pc(527) val startZ: Int
					val startX: Int
					if (pickableEntity.entity is AbstractInteractiveEntity) {
						startZ = (pickableEntity.entity as AbstractInteractiveEntity).startZ.toInt()
						startX = (pickableEntity.entity as AbstractInteractiveEntity).startX.toInt()
					} else {
						startZ = pickableEntity.entity!!.z shr 9
						startX = pickableEntity.entity!!.x shr 9
					}
					@Pc(664) var local664: Int
					@Pc(676) var local676: Int
					@Pc(787) var local787: Int
					@Pc(558) var local558: Int
					if (pickableEntity.entity is PlayerEntity) {
						@Pc(554) val playerEntity: PlayerEntity = pickableEntity.entity as PlayerEntity
						val playerSize: Int = playerEntity.getSize()
						if ((playerSize and 0x1) == 0 && (playerEntity.x and 0x1FF) == 0 && (playerEntity.z and 0x1FF) == 0 || (playerSize and 0x1) == 1 && (playerEntity.x and 0x1FF) == 256 && (playerEntity.z and 0x1FF) == 256) {
							val playerStartX: Int = playerEntity.x - ((playerEntity.getSize() - 1) shl 8)
							val playerStartZ: Int = playerEntity.z - ((playerEntity.getSize() - 1) shl 8)
							var npcIndex: Int = 0
							while (npcIndex < NPCList.NPC_UPDATE_INDEX) {
								@Pc(635) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[npcIndex].toLong()) as NPCEntityNode?
								if (npcEntityNode != null) {
									@Pc(640) val npcEntity: NPCEntity? = npcEntityNode.npc
									if (npcEntity!!.clientCycles != ClientState.clientCycle && npcEntity.isHidden) {
										val otherNpcStartX: Int = npcEntity.x - ((npcEntity.npcType!!.size - 1) shl 8)
										val otherNpcStartZ: Int = npcEntity.z - ((npcEntity.npcType!!.size - 1) shl 8)
										if (playerStartX <= otherNpcStartX && npcEntity.npcType!!.size <= playerEntity.getSize() - ((otherNpcStartX - playerStartX) shr 9) && playerStartZ <= otherNpcStartZ && npcEntity.npcType!!.size <= playerEntity.getSize() - ((otherNpcStartZ - playerStartZ) shr 9)) {
											MiniMenuBuilder.npcHovered(PlayerUpdate.localPlayer!!.level != pickableEntity.entity!!.level, npcEntity)
											npcEntity.clientCycles = ClientState.clientCycle
										}
									}
								}
								npcIndex++
							}
							val outPlayerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
							@Pc(751) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
							var outPlayerIndex: Int = 0
							while (outPlayerIndex < outPlayerCount) {
								@Pc(761) val playerEntity2: PlayerEntity? = PlayerUpdate.players[outPlayerIndices[outPlayerIndex]]
								if (playerEntity2 != null && ClientState.clientCycle != playerEntity2.clientCycles && playerEntity !== playerEntity2 && playerEntity2.isHidden) {
									val otherStartX: Int = playerEntity2.x - ((playerEntity2.getSize() - 1) shl 8)
									@Pc(799) val otherStartZ: Int = playerEntity2.z - ((playerEntity2.getSize() - 1) shl 8)
									if (playerStartX <= otherStartX && playerEntity2.getSize() <= playerEntity.getSize() - ((otherStartX - playerStartX) shr 9) && playerStartZ <= otherStartZ && playerEntity2.getSize() <= playerEntity.getSize() - ((otherStartZ - playerStartZ) shr 9)) {
										MiniMenu.playerHovered(PlayerUpdate.localPlayer!!.level != pickableEntity.entity!!.level, playerEntity2)
										playerEntity2.clientCycles = ClientState.clientCycle
									}
								}
								outPlayerIndex++
							}
						}
						if (ClientState.clientCycle == playerEntity.clientCycles) {
							pickableEntity = linkedList.getNext() as PickableEntity?
							continue
						}
						MiniMenu.playerHovered(PlayerUpdate.localPlayer!!.level != pickableEntity.entity!!.level, playerEntity)
						playerEntity.clientCycles = ClientState.clientCycle
					}
					if (pickableEntity.entity is NPCEntity) {
						@Pc(902) val npcEntity2: NPCEntity = pickableEntity.entity as NPCEntity
						if (npcEntity2.npcType != null) {
							if ((npcEntity2.npcType!!.size and 0x1) == 0 && (npcEntity2.x and 0x1FF) == 0 && (npcEntity2.z and 0x1FF) == 0 || (npcEntity2.npcType!!.size and 0x1) == 1 && (npcEntity2.x and 0x1FF) == 256 && (npcEntity2.z and 0x1FF) == 256) {
								val npcStartX: Int = npcEntity2.x - ((npcEntity2.npcType!!.size - 1) shl 8)
								val npcStartZ: Int = npcEntity2.z - ((npcEntity2.npcType!!.size - 1) shl 8)
								var npcIndex: Int = 0
								while (npcIndex < NPCList.NPC_UPDATE_INDEX) {
									@Pc(981) val npcEntityNode2: NPCEntityNode? = NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[npcIndex].toLong()) as NPCEntityNode?
									if (npcEntityNode2 != null) {
										@Pc(986) val npcEntity3: NPCEntity? = npcEntityNode2.npc
										if (ClientState.clientCycle != npcEntity3!!.clientCycles && npcEntity2 !== npcEntity3 && npcEntity3.isHidden) {
											val otherNpcStartX: Int = npcEntity3.x - ((npcEntity3.npcType!!.size - 1) shl 8)
											val otherNpcStartZ: Int = npcEntity3.z - ((npcEntity3.npcType!!.size - 1) shl 8)
											if (otherNpcStartX >= npcStartX && npcEntity2.npcType!!.size - ((otherNpcStartX - npcStartX) shr 9) >= npcEntity3.npcType!!.size && otherNpcStartZ >= npcStartZ && npcEntity2.npcType!!.size - ((otherNpcStartZ - npcStartZ) shr 9) >= npcEntity3.npcType!!.size) {
												MiniMenuBuilder.npcHovered(PlayerUpdate.localPlayer!!.level != pickableEntity.entity!!.level, npcEntity3)
												npcEntity3.clientCycles = ClientState.clientCycle
											}
										}
									}
									npcIndex++
								}
								val outPlayerCount: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
								@Pc(1100) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
								var outPlayerIndex: Int = 0
								while (outPlayerIndex < outPlayerCount) {
									@Pc(1110) val playerEntity3: PlayerEntity? = PlayerUpdate.players[outPlayerIndices[outPlayerIndex]]
									if (playerEntity3 != null && playerEntity3.clientCycles != ClientState.clientCycle && playerEntity3.isHidden) {
										val otherPlayerStartX: Int = playerEntity3.x - ((playerEntity3.getSize() - 1) shl 8)
										val otherPlayerStartZ: Int = playerEntity3.z - ((playerEntity3.getSize() - 1) shl 8)
										if (npcStartX <= otherPlayerStartX && playerEntity3.getSize() <= npcEntity2.npcType!!.size - ((otherPlayerStartX - npcStartX) shr 9) && npcStartZ <= otherPlayerStartZ && playerEntity3.getSize() <= npcEntity2.npcType!!.size - ((otherPlayerStartZ - npcStartZ) shr 9)) {
											MiniMenu.playerHovered(PlayerUpdate.localPlayer!!.level != pickableEntity.entity!!.level, playerEntity3)
											playerEntity3.clientCycles = ClientState.clientCycle
										}
									}
									outPlayerIndex++
								}
							}
							if (npcEntity2.clientCycles == ClientState.clientCycle) {
								pickableEntity = linkedList.getNext() as PickableEntity?
								continue
							}
							MiniMenuBuilder.npcHovered(pickableEntity.entity!!.level != PlayerUpdate.localPlayer!!.level, npcEntity2)
							npcEntity2.clientCycles = ClientState.clientCycle
						}
					}
					if (pickableEntity.entity is ObjStackEntity) {
						@Pc(1253) val absX: Int = MapRegion.sceneBaseX + startX
						val absZ: Int = startZ + MapRegion.sceneBaseY
						@Pc(1275) val objStack: ObjStack? = ObjStack.GROUND_ITEMS.get(((pickableEntity.entity!!.level.toInt() shl 28) or (absZ shl 14) or absX).toLong()) as ObjStack?
						if (objStack != null) {
							var objEntryIndex: Int = 0
							@Pc(1285) var objStackEntry: ObjStackEntry? = objStack.groundItems.last() as ObjStackEntry?
							while (objStackEntry != null) {
								@Pc(1293) val objType: ItemType? = TypeLists.OBJ!!.list(objStackEntry.id)
								if (MiniMenu.interfaceSelectionActive && pickableEntity.entity!!.level == PlayerUpdate.localPlayer!!.level) {
									@Pc(1315) val paramType: ParamType? = if (MiniMenu.targetParamId == -1) null else TypeLists.PARAMS!!.list(MiniMenu.targetParamId)
									if ((MiniMenu.useTargetMask and 0x1) != 0 && (paramType == null || objType!!.getIntParam(paramType.defaultInt, MiniMenu.targetParamId) != paramType.defaultInt)) {
										MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=ff9040>" + objType!!.name, startZ, false, startX, -1, true, MiniMenuAction.TGT_OBJ, objEntryIndex.toLong(), MiniMenu.useOnType, objStackEntry.id.toLong(), MiniMenu.useOnCursor)
									}
								}
								if (pickableEntity.entity!!.level == PlayerUpdate.localPlayer!!.level) {
									@Pc(1374) val strings: Array<String?>? = objType!!.groundActions
									var objOpIndex: Int = 4
									while (objOpIndex >= 0) {
										if (strings != null && strings[objOpIndex] != null) {
											@Pc(1386) var clickType: Byte = 0
											if (objOpIndex == 0) {
												clickType = MiniMenuAction.OPOBJ1.toByte()
											}
											var objCursor: Int = CursorManager.defaultAttackCursor
											if (objOpIndex == 1) {
												clickType = MiniMenuAction.OPOBJ2.toByte()
											}
											if (objOpIndex == 2) {
												clickType = MiniMenuAction.OPOBJ3.toByte()
											}
											if (objOpIndex == 3) {
												clickType = MiniMenuAction.OPOBJ4.toByte()
											}
											if (objType.primaryCursorActionIndex == objOpIndex) {
												objCursor = objType.primaryCursor
											}
											if (objOpIndex == 4) {
												clickType = MiniMenuAction.OPOBJ5.toByte()
											}
											if (objOpIndex == objType.secondaryCursorActionIndex) {
												objCursor = objType.secondaryCursor
											}
											MiniMenu.addEntry(false, "<col=ff9040>" + objType.name, startZ, false, startX, -1, true, clickType.toInt(), objEntryIndex.toLong(), strings[objOpIndex], objStackEntry.id.toLong(), objCursor)
										}
										objOpIndex--
									}
								}
								// Not part of the original client: the debug suffix, empty unless client.debug is set.
								MiniMenu.addEntry(pickableEntity.entity!!.level != PlayerUpdate.localPlayer!!.level, "<col=ff9040>" + objType!!.name + ClientFeatures.debugSuffix(objType.id), startZ, false, startX, -1, true, MiniMenuAction.OPOBJ6, objEntryIndex.toLong(), LocalizedText.aClass351_24.get(ClientState.language), objStackEntry.id.toLong(), MiniMenu.examineCursor)
								// Not part of the original client: the plugins' entries for the item (PluginRuntime).
								PluginRuntime.menuTarget(MenuTarget.GROUND_ITEM, startX, startZ, pickableEntity.entity!!.level.toInt(), objStackEntry.id, objStackEntry.id.toLong())
								objStackEntry = objStack.groundItems.previous() as ObjStackEntry?
								objEntryIndex++
							}
						}
					}
					if (pickableEntity.entity is GameObject) {
						@Pc(1534) val location: GameObject = pickableEntity.entity as GameObject
						@Pc(1541) var locType: LocType? = TypeLists.LOC!!.list(location.getId())
						if (locType!!.alternativeIds != null) {
							locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider)
						}
						if (locType != null) {
							if (MiniMenu.interfaceSelectionActive && PlayerUpdate.localPlayer!!.level == pickableEntity.entity!!.level) {
								@Pc(1569) val paramType2: ParamType? = if (MiniMenu.targetParamId == -1) null else TypeLists.PARAMS!!.list(MiniMenu.targetParamId)
								if ((MiniMenu.useTargetMask and 0x4) != 0 && (paramType2 == null || locType.getIntParam(MiniMenu.targetParamId, paramType2.defaultInt) != paramType2.defaultInt)) {
									MiniMenu.addEntry(false, MiniMenu.useOnTarget + " -> <col=00ffff>" + locType.name, startZ, false, startX, -1, true, MiniMenuAction.TGT_LOC, location.hashCode().toLong(), MiniMenu.useOnType, MapLoader.getLocMenuUid(location, startZ, startX), MiniMenu.useOnCursor)
								}
							}
							if (PlayerUpdate.localPlayer!!.level == pickableEntity.entity!!.level) {
								@Pc(1638) val strings2: Array<String?>? = locType.actions
								if (strings2 != null) {
									var locOpIndex: Int = 4
									while (locOpIndex >= 0) {
										if (strings2[locOpIndex] != null) {
											@Pc(1650) var clickType: Short = 0
											if (locOpIndex == 0) {
												clickType = MiniMenuAction.OPLOC1.toShort()
											}
											var locCursor: Int = CursorManager.defaultAttackCursor
											if (locOpIndex == 1) {
												clickType = MiniMenuAction.OPLOC2.toShort()
											}
											if (locOpIndex == 2) {
												clickType = MiniMenuAction.OPLOC3.toShort()
											}
											if (locOpIndex == 3) {
												clickType = MiniMenuAction.OPLOC4.toShort()
											}
											if (locOpIndex == 4) {
												clickType = MiniMenuAction.OPLOC5.toShort()
											}
											if (locType.primaryCursorActionIndex == locOpIndex) {
												locCursor = locType.primaryCursor
											}
											if (locOpIndex == locType.secondaryCursorActionIndex) {
												locCursor = locType.secondaryCursor
											}
											MiniMenu.addEntry(false, "<col=00ffff>" + locType.name, startZ, false, startX, -1, true, clickType.toInt(), location.hashCode().toLong(), strings2[locOpIndex], MapLoader.getLocMenuUid(location, startZ, startX), locCursor)
										}
										locOpIndex--
									}
								}
								// Not part of the original client: the debug suffix, empty unless client.debug is set.
								MiniMenu.addEntry(pickableEntity.entity!!.level != PlayerUpdate.localPlayer!!.level, "<col=00ffff>" + locType.name + ClientFeatures.debugSuffix(locType.id, startX + MapRegion.sceneBaseX, startZ + MapRegion.sceneBaseY, pickableEntity.entity!!.level.toInt()), startZ, false, startX, -1, true, MiniMenuAction.OPLOC6, location.hashCode().toLong(), LocalizedText.aClass351_24.get(ClientState.language), locType.id.toLong(), MiniMenu.examineCursor)
								// Not part of the original client: the plugins' entries for the location (PluginRuntime).
								PluginRuntime.menuTarget(MenuTarget.LOC, startX, startZ, pickableEntity.entity!!.level.toInt(), locType.id, locType.id.toLong())
							}
						}
					}
				}
				pickableEntity = linkedList.getNext() as PickableEntity?
			}
			if (OrthoMode.orthoEnabled) {
				OrthoMode.restoreOrthoCamera()
			}
			passIndex++
		}
		MapRegion.applyViewDepthRange(false)
	}

	@OriginalMember(owner = "client!mo", name = "a", descriptor = "(FFFFIFBF)F")
	@JvmStatic
	fun traceRayToGround(@OriginalArg(0) startY: Float, @OriginalArg(1) endX: Float, @OriginalArg(2) startZ: Float, @OriginalArg(3) endZ: Float, @OriginalArg(4) depth: Int, @OriginalArg(5) startX: Float, @OriginalArg(7) endY: Float): Float {
		@Pc(7) var progress: Float = 0.0f
		@Pc(12) val deltaX: Float = endX - startX
		@Pc(17) val deltaY: Float = endY - startY
		@Pc(22) val deltaZ: Float = endZ - startZ
		@Pc(31) var prevX: Float = 0.0f
		@Pc(33) var prevY: Float = 0.0f
		@Pc(35) var prevZ: Float = 0.0f
		while (progress < 1.1f) {
			@Pc(43) val x: Float = deltaX * progress + startX
			@Pc(49) val y: Float = startY + progress * deltaY
			@Pc(55) val z: Float = startZ + deltaZ * progress
			@Pc(60) val tileX: Int = x.toInt() shr 9
			@Pc(65) val tileZ: Int = z.toInt() shr 9
			if (tileX > 0 && tileZ > 0 && tileX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > tileZ) {
				@Pc(89) var level: Int = PlayerUpdate.localPlayer!!.level.toInt()
				if (level < 3 && (SettingsBits.tileFunctionMasks!![1]!![tileX]!![tileZ].toInt() and 0x2) != 0) {
					level++
				}
				@Pc(118) val groundHeight: Int = Scene.aClass65Array2!![level]!!.averageHeight(x.toInt(), z.toInt())
				if (y > groundHeight.toFloat()) {
					if (depth < 2) {
						return progress
					}
					return progress + traceRayToGround(prevY, x, prevZ, z, depth - 1, prevX, y) * 0.1f - 0.1f
				}
			}
			prevX = x
			prevY = y
			progress += 0.1f
			prevZ = z
		}
		return -1.0f
	}

	@OriginalMember(owner = "client!kg", name = "f", descriptor = "Z")
	@JvmField
	var showAdvert: Boolean = true
}
