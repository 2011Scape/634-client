import com.jagex.game.runetek5.cache.node.ChangeLocationRequest
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.core.constants.AreaMode
import com.jagex.game.runetek5.core.constants.PlayerListConstants
import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.scene.LocAction
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.config.staticelements.StaticElements
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.tile.TileMessage
import com.jagex.game.runetek5.entity.ProjectileAnimation
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.client.ClientState

object MapBuildStart {

	@OriginalMember(owner = "client!uba", name = "a", descriptor = "(IBIIZ)V")
	@JvmStatic
	fun performUpdate(@OriginalArg(0) baseY: Int, @OriginalArg(2) baseX: Int, @OriginalArg(3) step: MainLogicStep?, @OriginalArg(4) forcedPositionUpdate: Boolean) {
		if (!forcedPositionUpdate && baseX == MapRegion.mapCenterChunkX && baseY == MapRegion.mapBaseChunkY && (PlayerUpdate.currentLevel == MapRegion.mapBuildLevel || GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 1)) {
			return
		}
		MapRegion.mapCenterChunkX = baseX
		MapRegion.mapBaseChunkY = baseY
		MapRegion.mapBuildLevel = PlayerUpdate.currentLevel
		if (GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 1) {
			MapRegion.mapBuildLevel = 0
		}
		MainLogicStepTransition.setMainLogicStep(step)
		MessageBox.draw(true, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_14.get(ClientState.language), ClientState.activeToolkit)
		@Pc(56) val oldSceneBaseX: Int = MapRegion.sceneBaseX
		@Pc(58) val oldSceneBaseY: Int = MapRegion.sceneBaseY
		MapRegion.sceneBaseX = (MapRegion.mapCenterChunkX - (MapRegion.sceneSizeX shr 4)) * 8
		MapRegion.sceneBaseY = (MapRegion.mapBaseChunkY - (MapRegion.sceneSizeY shr 4)) * 8
		WorldMap.aClass2_Sub6_Sub4_3 = MapList.getMapDef(MapRegion.mapCenterChunkX * 8, MapRegion.mapBaseChunkY * 8)
		StaticElements.aClass333_3 = null
		@Pc(89) val deltaX: Int = MapRegion.sceneBaseX - oldSceneBaseX
		@Pc(94) val deltaY: Int = MapRegion.sceneBaseY - oldSceneBaseY
		@Pc(99) var index: Int
		@Pc(112) var local112: Int
		@Pc(236) var local236: Int
		@Pc(174) var local174: Int
		if (step === MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD) {
			index = 0
			while (index < NPCList.npcCount) {
				@Pc(105) val npcEntityNode: NPCEntityNode? = NPCList.NPC_ARRAY[index]
				if (npcEntityNode != null) {
					@Pc(110) val npcEntity: NPCEntity? = npcEntityNode.npc
					var stepIndex: Int = 0
					while (stepIndex < 10) {
						npcEntity!!.movementBaseX[stepIndex] -= deltaX
						npcEntity.movementBaseY[stepIndex] -= deltaY
						stepIndex++
					}
					npcEntity!!.z -= deltaY * 512
					npcEntity.x -= deltaX * 512
				}
				index++
			}
		} else {
			NPCList.NPC_UPDATE_INDEX = 0
			@Pc(162) var npcsRemoved: Boolean = false
			@Pc(168) val maxX: Int = MapRegion.sceneSizeX * 512 - 512
			val maxZ: Int = (MapRegion.sceneSizeY - 1) * 512
			var npcIndex: Int = 0
			while (npcIndex < NPCList.npcCount) {
				@Pc(182) val npcEntityNode2: NPCEntityNode? = NPCList.NPC_ARRAY[npcIndex]
				if (npcEntityNode2 != null) {
					@Pc(187) val npcEntity2: NPCEntity? = npcEntityNode2.npc
					npcEntity2!!.z -= deltaY * 512
					npcEntity2.x -= deltaX * 512
					if (npcEntity2.x >= 0 && npcEntity2.x <= maxX && npcEntity2.z >= 0 && maxZ >= npcEntity2.z) {
						@Pc(234) var stepsInScene: Boolean = true
						var stepIndex: Int = 0
						while (stepIndex < 10) {
							npcEntity2.movementBaseX[stepIndex] -= deltaX
							npcEntity2.movementBaseY[stepIndex] -= deltaY
							if (npcEntity2.movementBaseX[stepIndex] < 0 || MapRegion.sceneSizeX <= npcEntity2.movementBaseX[stepIndex] || npcEntity2.movementBaseY[stepIndex] < 0 || npcEntity2.movementBaseY[stepIndex] >= MapRegion.sceneSizeY) {
								stepsInScene = false
							}
							stepIndex++
						}
						if (stepsInScene) {
							NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcEntity2.playerIndex
						} else {
							npcEntity2.setNpcType(null)
							npcsRemoved = true
							npcEntityNode2.unlink()
						}
					} else {
						npcEntity2.setNpcType(null)
						npcEntityNode2.unlink()
						npcsRemoved = true
					}
				}
				npcIndex++
			}
			if (npcsRemoved) {
				NPCList.npcCount = NPCList.NPC_MAP.size()
				NPCList.NPC_MAP.toArray(NPCList.NPC_ARRAY as Array<Node?>)
			}
		}
		index = 0
		while (index < PlayerListConstants.COUNT) {
			@Pc(348) val playerEntity: PlayerEntity? = PlayerUpdate.players[index]
			if (playerEntity != null) {
				var stepIndex: Int = 0
				while (stepIndex < 10) {
					playerEntity.movementBaseX[stepIndex] -= deltaX
					playerEntity.movementBaseY[stepIndex] -= deltaY
					stepIndex++
				}
				playerEntity.x -= deltaX * 512
				playerEntity.z -= deltaY * 512
			}
			index++
		}
		@Pc(398) val hintArrows: Array<HintArrow?> = PlayerUpdate.HINT_ARROWS
		var arrowIndex: Int = 0
		while (arrowIndex < hintArrows.size) {
			@Pc(406) val hintArrow: HintArrow? = hintArrows[arrowIndex]
			if (hintArrow != null) {
				hintArrow.y -= deltaY * 512
				hintArrow.x -= deltaX * 512
			}
			arrowIndex++
		}
		@Pc(438) var changeLocationRequest: ChangeLocationRequest? = LocAction.pendingRemovals!!.first() as ChangeLocationRequest?
		while (changeLocationRequest != null) {
			changeLocationRequest.y -= deltaY
			changeLocationRequest.x -= deltaX
			if (MapRegion.areaMode != 4 && (changeLocationRequest.x < 0 || changeLocationRequest.y < 0 || MapRegion.sceneSizeX <= changeLocationRequest.x || MapRegion.sceneSizeY <= changeLocationRequest.y)) {
				changeLocationRequest.unlink()
			}
			changeLocationRequest = LocAction.pendingRemovals!!.next() as ChangeLocationRequest?
		}
		if (MapRegion.areaMode != AreaMode.RETAIN_OUT_OF_BOUNDS) {
			@Pc(495) var objStack: ObjStack? = ObjStack.GROUND_ITEMS.first() as ObjStack?
			while (objStack != null) {
				@Pc(503) val tileX: Int = (objStack.key and 0x3FFFL).toInt()
				@Pc(508) val localX: Int = tileX - MapRegion.sceneBaseX
				val tileY: Int = (objStack.key shr 14 and 0x3FFFL).toInt()
				@Pc(521) val localY: Int = tileY - MapRegion.sceneBaseY
				if (localX < 0 || localY < 0 || MapRegion.sceneSizeX <= localX || localY >= MapRegion.sceneSizeY) {
					objStack.unlink()
				}
				objStack = ObjStack.GROUND_ITEMS.next() as ObjStack?
			}
		}
		if (Minimap.minimapFlagX != 0) {
			Minimap.minimapFlagX -= deltaX
			Minimap.minimapFlagY -= deltaY
		}
		AreaSoundPlayer.resetAreaSounds()
		if (step !== MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD) {
			Camera.moveToLocalY -= deltaY
			Camera.cameraPositionAbsoluteX -= deltaX * 512
			Camera.lookAtLocalX -= deltaX
			Camera.lookAtLocalY -= deltaY
			Camera.moveToLocalX -= deltaX
			Camera.cameraPositionAbsoluteY -= deltaY * 512
			if (java.lang.Math.abs(deltaX) > MapRegion.sceneSizeX || java.lang.Math.abs(deltaY) > MapRegion.sceneSizeY) {
				EnvironmentManager.requestDirectEnvironmentTransition()
			}
		} else if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			Camera.cameraTargetAbsZ -= deltaY * 512
			Camera.cameraBaseX -= deltaX * 512
			Camera.cameraBaseZ -= deltaY * 512
			Camera.cameraTargetAbsX -= deltaX * 512
		} else {
			Camera.cameraCalculationType = CameraMode.MODE_DEFAULT
			Camera.roofRemovalTargetY = -1
			Camera.roofRemovalTargetX = -CameraMode.MODE_DEFAULT
		}
		MiniMenu.reset()
		ComponentMinimap.resetMinimap()
		Renderers.activeToolkit.clear()
		ProjectileAnimation.aClass341_24.clear()
		TileMessage.aClass325_8.clear()
		ParticleSystemManager.destroy()
	}
}
