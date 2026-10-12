package com.jagex.game.runetek5.client

import com.jagex.game.runetek5.client.features.GroundItemLabels
import com.jagex.game.runetek5.client.features.NpcLabels
import com.jagex.game.runetek5.client.features.XpDrops
import com.jagex.game.runetek5.client.features.TileMarkers
import com.jagex.game.runetek5.ByteArrayPool
import com.jagex.game.runetek5.Cursor
import com.jagex.game.runetek5.EntitySceneBuilder
import com.jagex.game.runetek5.EnvironmentSamplerCacheReset
import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.FriendChat
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.LoadingScreenManager
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.MaterialManager
import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.ModeWhere
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.OverlayManager
import com.jagex.game.runetek5.PathFinder
import com.jagex.game.runetek5.PickList
import com.jagex.game.runetek5.PlayerList
import com.jagex.game.runetek5.SceneCameraZ
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.ScriptRunner
import com.jagex.game.runetek5.Skybox
import com.jagex.game.runetek5.Sprites
import com.jagex.game.runetek5.SunPositionZ
import com.jagex.game.runetek5.Terrain
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.buffer.CircularBuffer
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.FileOnDisk
import com.jagex.game.runetek5.cache.JFS32
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.cache.node.lru.ClientScript
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.cache.node.lru.PulseEvent
import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.camera.Shake
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.web.ClientURLTools
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.fonttype.FontType
import com.jagex.game.runetek5.config.mapsprite.MapSpriteType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.config.material.texture.ColorImageCacheEntry
import com.jagex.game.runetek5.config.material.texture.TextureOpRasterizer
import com.jagex.game.runetek5.config.material.texture.operation.EllipseRasterizer
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPerlinNoise
import com.jagex.game.runetek5.config.meltype.OccludedWallCount
import com.jagex.game.runetek5.config.particle.ParticleProducerType
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer
import com.jagex.game.runetek5.config.varc.VarcTypeList
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.core.constants.TileFlag
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.entity.GroundDecor
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.entity.PickableEntity
import com.jagex.game.runetek5.entity.ProjectileAnimation
import com.jagex.game.runetek5.entity.SpotAnimation
import com.jagex.game.runetek5.entity.StaticLocation
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.entity.grounditem.AbstractGroundItem
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.OffscreenSurface
import com.jagex.game.runetek5.graphics.TextureOpRaster
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontCombo
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.graphics.light.StaticPointLight
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.network.world.WorldMeta
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.preference.impl.MapSizePreference
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.scene.culling.CullingCluster
import com.jagex.game.runetek5.scene.environment.Environment
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.scene.tile.Tile
import com.jagex.game.runetek5.scene.tile.TileMessage
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import com.jagex.game.runetek5.sound.SceneCullingActive
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import com.jagex.game.runetek5.sound.player.song.SongPlayer
import com.jagex.game.runetek5.statics.BrowserJavaScript
import com.jagex.game.runetek5.statics.CS2State
import com.jagex.game.runetek5.statics.SceneProjectionCenterY
import com.jagex.game.runetek5.toolkit.AbstractToolkit
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.utils.Utils
import java.awt.Canvas
import java.awt.Color
import java.awt.Container
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Insets
import java.awt.Rectangle
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.math.BigInteger
import java.util.Enumeration
import java.util.Hashtable
import java.util.Random
import java.util.Vector
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object ClientState {

	@OriginalMember(owner = "client!or", name = "o", descriptor = "I")
	@JvmField
	var clientCycle: Int = 0

	@OriginalMember(owner = "client!raa", name = "B", descriptor = "I")
	@JvmField
	var language: Int = 0

	@OriginalMember(owner = "client!r", name = "B", descriptor = "I")
	@Volatile
	@JvmField
	var BASE_WINDOW_ID: Int = -1

	@OriginalMember(owner = "client!ta", name = "c", descriptor = "I")
	@JvmField
	var mainLogicStep: MainLogicStep? = MainLogicStep.STEP_STARTUP

	@OriginalMember(owner = "client!cf", name = "k", descriptor = "I")
	@JvmField
	var runEnergy: Int = 0

	@OriginalMember(owner = "client!gia", name = "o", descriptor = "Lclient!ha;")
	@JvmField
	var activeToolkit: Toolkit? = null

	@OriginalMember(owner = "client!gea", name = "o", descriptor = "Z")
	@JvmField
	var awaitingPriorityRequests: Boolean = true

	@OriginalMember(owner = "client!sc", name = "a", descriptor = "(IZIIII)V")
	@JvmStatic
	fun drawGameArea(@OriginalArg(1) loginBackground: Boolean, @OriginalArg(2) componentWidth: Int, @OriginalArg(3) x: Int, @OriginalArg(4) componentHeight: Int, @OriginalArg(5) y: Int) {
		if (Scene.aClass100ArrayArrayArray3 == null) {
			ClientState.activeToolkit!!.fillRect(componentWidth, x, y, componentHeight, -16777216)
		} else if (PlayerUpdate.localPlayer!!.x >= 0 && PlayerUpdate.localPlayer!!.x < MapRegion.sceneSizeX * 512 && PlayerUpdate.localPlayer!!.z >= 0 && PlayerUpdate.localPlayer!!.z < MapRegion.sceneSizeY * 512) {
			SceneDrawer.timesSceneRendered++
			if (PlayerUpdate.localPlayer != null && (PlayerUpdate.localPlayer!!.x + 256 - PlayerUpdate.localPlayer!!.getSize() * 256 shr 9) == Minimap.minimapFlagX && (PlayerUpdate.localPlayer!!.z + 256 - PlayerUpdate.localPlayer!!.getSize() * 256 shr 9) == Minimap.minimapFlagY) {
				Minimap.minimapFlagY = -1
				Minimap.minimapFlagX = -1
				PulseEvent.delayedMinimapFlagUpdate()
			}
			EntitySceneBuilder.positionAllEntities()
			if (!loginBackground) {
				ProjectileAnimation.updateProjectiles()
			}
			SpotAnimation.addSpotAnimationsToScene()
			AbstractToolkit.calculateGameArea(x, y, componentWidth, true, componentHeight)
			@Pc(117) val areaY: Int = Camera.gameAreaY
			@Pc(119) val areaX: Int = Camera.gameAreaX
			@Pc(121) val height: Int = Camera.gameAreaHeight
			HintArrow.scaledOrthoZoom = OrthoMode.orthoZoom
			@Pc(125) val width: Int = GameShell.gameAreaWidth
			@Pc(131) var local131: Int
			@Pc(168) var local168: Int
			if (Camera.cameraCalculationType == CameraMode.MODE_DEFAULT) {
				var pitch: Int = Camera.camAngleX.toInt()
				if (pitch < NPCList.newSize shr 8) {
					pitch = NPCList.newSize shr 8
				}
				if (Camera.CAM_SHAKING[4] && Shake.CAM_SHAKE_AMPLITUDE[4] + 128 > pitch) {
					pitch = Shake.CAM_SHAKE_AMPLITUDE[4] + 128
				}
				val yaw: Int = Camera.camAngleY.toInt() + Camera.cameraRandomisationA and 0x3FFF
				Camera.setCameraPosition(pitch, Scene.tileHeightForAbsolutePosition(PlayerUpdate.localPlayer!!.x, PlayerUpdate.currentLevel, PlayerUpdate.localPlayer!!.z) - 200, yaw, Camera.cameraBaseZ, height, Camera.cameraBaseX, (pitch shr 3) * 3 + 600 shl 2)
			} else if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
				var pitch: Int = Camera.camAngleX.toInt()
				if (pitch < NPCList.newSize shr 8) {
					pitch = NPCList.newSize shr 8
				}
				if (Camera.CAM_SHAKING[4] && Shake.CAM_SHAKE_AMPLITUDE[4] + 128 > pitch) {
					pitch = Shake.CAM_SHAKE_AMPLITUDE[4] + 128
				}
				val yaw: Int = Camera.camAngleY.toInt() and 0x3FFF
				Camera.setCameraPosition(pitch, Scene.tileHeightForAbsolutePosition(Camera.cameraTargetAbsX, PlayerUpdate.currentLevel, Camera.cameraTargetAbsZ) - 200, yaw, Camera.cameraBaseZ, height, Camera.cameraBaseX, (pitch shr 3) * 3 + 600 shl 2)
			} else if (Camera.cameraCalculationType == CameraMode.MODE_SMOOTH_RESET) {
				Camera.updateSmoothTransitionCamera(height)
			}
			// Not part of the original client: with client.fps, the camera is shown on its way between cycles.
			ClientFeatures.blendCamera(ClientState.clientCycle, Camera.cameraCalculationType)
			ClientFeatures.blendEntities(ClientState.clientCycle)
			val savedCameraX: Int = Camera.cameraPositionAbsoluteX
			val savedCameraZ: Int = Camera.cameraPositionAbsoluteZ
			@Pc(274) val menuBackgroundAlpha: Int = Camera.cameraPositionAbsoluteY
			@Pc(276) val savedPitch: Int = Camera.cameraPitch
			@Pc(278) val savedYaw: Int = Camera.cameraYaw
			@Pc(321) var local321: Int
			@Pc(280) var i: Int = 0
			while (i < Shake.DIRECTIONS) {
				if (Camera.CAM_SHAKING[i]) {
					val shakeOffset: Int = (Math.random() * (Camera.CAM_SHAKE_JITTER[i] * 2 + 1).toDouble() - Camera.CAM_SHAKE_JITTER[i].toDouble() + Math.sin(Shake.CAM_SHAKE_PHASE[i].toDouble() * (Shake.CAM_SHAKE_FREQUENCY[i].toDouble() / 100.0)) * Shake.CAM_SHAKE_AMPLITUDE[i].toDouble()).toInt()
					if (i == 4) {
						Camera.cameraPitch += shakeOffset
						if (Camera.cameraPitch < 1024) {
							Camera.cameraPitch = 1024
						} else if (Camera.cameraPitch > 3072) {
							Camera.cameraPitch = 3072
						}
					}
					if (i == 0) {
						Camera.cameraPositionAbsoluteX += shakeOffset shl 2
					}
					if (i == 3) {
						Camera.cameraYaw = shakeOffset + Camera.cameraYaw and 0x3FFF
					}
					if (i == 1) {
						Camera.cameraPositionAbsoluteZ += shakeOffset shl 2
					}
					if (i == 2) {
						Camera.cameraPositionAbsoluteY += shakeOffset shl 2
					}
				}
				i++
			}
			if (Camera.cameraPositionAbsoluteX < 0) {
				Camera.cameraPositionAbsoluteX = 0
			}
			if ((Scene.sceneWidth shl 9) - 1 < Camera.cameraPositionAbsoluteX) {
				Camera.cameraPositionAbsoluteX = (Scene.sceneWidth shl 9) - 1
			}
			if (Camera.cameraPositionAbsoluteY < 0) {
				Camera.cameraPositionAbsoluteY = 0
			}
			if ((Camera.sceneLength shl 9) - 1 < Camera.cameraPositionAbsoluteY) {
				Camera.cameraPositionAbsoluteY = (Camera.sceneLength shl 9) - 1
			}
			MapRegion.handleRoofDisplay()
			EnvironmentManager.updateAtmosphere()
			ClientState.activeToolkit!!.KA(areaX, areaY, areaX + width, areaY + height)
			MapRegion.applyViewDepthRange(true)
			if (OrthoMode.orthoEnabled) {
				InterfaceComponentRenderer.setDeferredDragOffsetY(EnvironmentManager.fogColour)
				if (OrthoMode.lastOrthoZoom != HintArrow.scaledOrthoZoom) {
					Scene.staticSceneDirty = true
				}
				OrthoMode.lastOrthoZoom = HintArrow.scaledOrthoZoom
			} else {
				// Not part of the original client: with client.msaa the 3D view is drawn in its own framebuffer.
				ClientFeatures.beginScene(ClientState.activeToolkit, areaX, areaY, width, height)
				ClientState.activeToolkit!!.ya()
				val clearColour: Int = EnvironmentManager.fogColour
				if (Skybox.configNpcJs5 == null) {
					ClientState.activeToolkit!!.GA(clearColour)
				} else {
					Skybox.configNpcJs5!!.renderSkybox(Skybox.skyboxAngle shl 3, ClientState.activeToolkit, areaX, width, areaY, Camera.cameraYaw, Camera.cameraPitch, clearColour, height)
				}
			}
			SoundEffectPlayer.updatePlayers()
			ClientState.aClass30_3!!.createCamera(Camera.cameraPositionAbsoluteX, Camera.cameraPositionAbsoluteZ, Camera.cameraPositionAbsoluteY, -Camera.cameraPitch and 0x3FFF, -Camera.cameraYaw and 0x3FFF, -Camera.cameraRoll and 0x3FFF)
			ClientState.activeToolkit!!.setCamera(ClientState.aClass30_3)
			ClientState.activeToolkit!!.DA(width / 2 + areaX, areaY + height / 2, Camera.viewportScaledFov shl 1, Camera.viewportScaledFov shl 1)
			Camera.setProjection(Camera.viewportScaledFov shl 1, Camera.viewportScaledFov shl 1, areaX + width / 2, areaY + height / 2)
			Camera.storePickingCamera(-Camera.cameraPitch and 0x3FFF, Camera.cameraPositionAbsoluteY, Camera.cameraPositionAbsoluteZ, -Camera.cameraYaw and 0x3FFF, Camera.cameraPositionAbsoluteX, -Camera.cameraRoll and 0x3FFF)
			@Pc(582) val roofPreference: Byte = if (GamePreferences.uid!!.lb!!.getValue() == 2) SceneDrawer.timesSceneRendered.toByte() else 1
			if (OrthoMode.orthoEnabled) {
				OrthoMode.setOrthoRotation(-Camera.cameraYaw and 0x3FFF, -Camera.cameraRoll and 0x3FFF, -Camera.cameraPitch and 0x3FFF)
				OrthoMode.drawOrthoScene(MapRegion.roofMinZ, MapRegion.roofStamps, PlayerUpdate.localPlayer!!.z shr 9, PlayerUpdate.localPlayer!!.level + 1, MapRegion.visibleBelowDrawheight, Camera.cameraPositionAbsoluteX, Camera.cameraPositionAbsoluteY, GamePreferences.uid!!.flickeringEffects!!.getValue() == 0, Camera.cameraPositionAbsoluteZ, PlayerUpdate.localPlayer!!.x shr 9, MapRegion.roofMinX, MapRegion.roofMaxX, roofPreference, MapRegion.roofMaxZ, ClientState.clientCycle, HintArrow.scaledOrthoZoom)
			} else {
				SceneDrawer.drawScene(ClientState.clientCycle, Camera.cameraPositionAbsoluteX, Camera.cameraPositionAbsoluteZ, Camera.cameraPositionAbsoluteY, MapRegion.roofStamps, MapRegion.visibleBelowDrawheight, MapRegion.roofMinX, MapRegion.roofMaxX, MapRegion.roofMaxZ, MapRegion.roofMinZ, PlayerUpdate.localPlayer!!.level + 1, roofPreference, PlayerUpdate.localPlayer!!.x shr 9, PlayerUpdate.localPlayer!!.z shr 9, GamePreferences.uid!!.flickeringEffects!!.getValue() == 0, if (OrthoMode.orthographicProjection) HintArrow.scaledOrthoZoom else -1, 0, false)
				ClientFeatures.endScene(ClientState.activeToolkit) // Not part of the original client: see beginScene.
			}
			SoundEffectPlayer.updatePlayers()
			if (ClientState.mainLogicStep == MainLogicStep.STEP_GAME_SCREEN) {
				TileMessage.drawTileMessages(width, height, areaX, areaY)
				OverlayManager.drawEntities(areaY, height, areaX, width)
				TileMarkers.draw(areaX, areaY) // Not part of the original client: see TileMarkers.
				NpcLabels.draw(areaX, areaY, width, height) // Not part of the original client: see NpcLabels.
				GroundItemLabels.draw(areaX, areaY) // Not part of the original client: see GroundItemLabels.
				XpDrops.draw(areaX, areaY, width) // Not part of the original client: see XpDrops.
				HintArrow.drawHintArrows(areaX, areaY, width, height)
				StaticMedia.drawClickCross(height, width, areaY, areaX)
			}
			Scene.clearDynamicInteractives()
			Camera.cameraPositionAbsoluteX = savedCameraX
			Camera.cameraPitch = savedPitch
			Camera.cameraYaw = savedYaw
			Camera.cameraPositionAbsoluteZ = savedCameraZ
			Camera.cameraPositionAbsoluteY = menuBackgroundAlpha
			ClientFeatures.restoreCamera()
			ClientFeatures.restoreEntities()
			if (ClientState.awaitingPriorityRequests && Resource.remoteFileRequesterJs5!!.getUrgentRequestCount() == 0) {
				ClientState.awaitingPriorityRequests = false
			}
			if (ClientState.awaitingPriorityRequests) {
				ClientState.activeToolkit!!.fillRect(width, areaX, areaY, height, -16777216)
				MessageBox.draw(false, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_14!!.get(ClientState.language), ClientState.activeToolkit)
			}
			MapRegion.applyViewDepthRange(false)
		} else {
			ClientState.activeToolkit!!.fillRect(componentWidth, x, y, componentHeight, -16777216)
		}
	}

	@OriginalMember(owner = "client!dh", name = "ob", descriptor = "I")
	@JvmField
	var animatingStep: Int = 0

	@OriginalMember(owner = "client!ae", name = "v", descriptor = "I")
	@JvmField
	var serverTick: Int = 1

	@OriginalMember(owner = "client!bj", name = "c", descriptor = "Lclient!bca;")
	@JvmField
	var aClass30_3: Matrix? = null
}
