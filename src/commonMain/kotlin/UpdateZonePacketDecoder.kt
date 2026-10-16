// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("SENSELESS_COMPARISON")

import com.jagex.game.runetek5.UpdateZonePacket
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.entity.ProjectileAnimation
import com.jagex.game.runetek5.cache.node.lru.ProjectileAnimationNode
import com.jagex.game.runetek5.entity.SpotAnimation
import com.jagex.game.runetek5.config.sequence.PositionedGfx
import com.jagex.game.runetek5.TextCoordList
import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.cache.node.ObjStackEntry
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.scene.LocAction
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.ObjStackEntity
import com.jagex.game.runetek5.core.constants.LocLayer
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object UpdateZonePacketDecoder {

	@OriginalMember(owner = "client!sfa", name = "a", descriptor = "(Lclient!kn;B)V")
	@JvmStatic
	fun decodeUpdateZonePacket(@OriginalArg(0) packet: UpdateZonePacket?) {
		@Pc(12) var local12: Int
		@Pc(16) var local16: Int
		@Pc(20) var local20: Int
		@Pc(24) var local24: Int
		@Pc(28) var local28: Int
		@Pc(32) var local32: Int
		@Pc(40) var local40: Int
		@Pc(47) var local47: Int
		if (packet === UpdateZonePacket.CREATE_OBJECT) {
			val shapeAndRotation: Int = BufferedConnectionContext.receivedBuffer!!.readUByteSubtract()
			val shape: Int = shapeAndRotation shr 2
			val rotation: Int = shapeAndRotation and 0x3
			val layer: Int = LocLayer.OBJECT_TYPE_SLOTS[shape]
			val locId: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
			val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByteAdd()
			val sceneX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 4 and 0x7)
			val sceneY: Int = UpdateZonePacket.updateZoneY + (packedOffset and 0x7)
			if (MapRegion.allowsOutOfBounds(MapRegion.areaMode) || sceneX >= 0 && sceneY >= 0 && sceneX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > sceneY) {
				LocAction.queueLocChange(UpdateZonePacketDecoder.updateZonePlane, sceneY, shape, layer, rotation, locId, sceneX)
			}
			return
		}
		@Pc(157) var local157: Int
		@Pc(155) var local155: Int
		if (packet === UpdateZonePacket.SPOT_ANIM) {
			val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val sceneX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 4 and 0x7)
			val sceneY: Int = UpdateZonePacket.updateZoneY + (packedOffset and 0x7)
			val spotAnimId: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
			val height: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val delay: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
			val rotation: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			if (sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && MapRegion.sceneSizeY > sceneY) {
				val fineX: Int = sceneX * 512 + 256
				val fineY: Int = sceneY * 512 + 256
				var virtualLevel: Int = UpdateZonePacketDecoder.updateZonePlane
				if (virtualLevel < 3 && SettingsBits.isBridge(sceneY, sceneX)) {
					virtualLevel++
				}
				@Pc(192) val spotAnimation: SpotAnimation = SpotAnimation(spotAnimId, delay, ClientState.clientCycle, UpdateZonePacketDecoder.updateZonePlane, virtualLevel, fineX, Scene.tileHeightForAbsolutePosition(fineX, UpdateZonePacketDecoder.updateZonePlane, fineY) - height, fineY, sceneX, sceneX, sceneY, sceneY, rotation)
				Renderers.activeToolkit.addLast(PositionedGfx(spotAnimation))
			}
			return
		}
		@Pc(282) var local282: Int
		if (packet === UpdateZonePacket.SOUND_AREA) {
			val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val sceneX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 4 and 0x7)
			val sceneY: Int = (packedOffset and 0x7) + UpdateZonePacket.updateZoneY
			var soundId: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
			if (soundId == 65535) {
				soundId = -1
			}
			val radiusAndLoops: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val radius: Int = radiusAndLoops shr 4 and 0xF
			val loops: Int = radiusAndLoops and 0x7
			val delay: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val volume: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val rate: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
			if (sceneX >= 0 && sceneY >= 0 && sceneX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > sceneY) {
				val range: Int = radius + 1
				if (sceneX - range <= PlayerUpdate.localPlayer!!.movementBaseX[0] && PlayerUpdate.localPlayer!!.movementBaseX[0] <= range + sceneX && sceneY - range <= PlayerUpdate.localPlayer!!.movementBaseY[0] && PlayerUpdate.localPlayer!!.movementBaseY[0] <= sceneY + range) {
					AreaSoundPlayer.playAreaSoundVorbis(soundId, volume, (sceneY shl 8) + (UpdateZonePacketDecoder.updateZonePlane shl 24) + (sceneX shl 16) + radius, loops, rate, delay)
				}
			}
		} else if (packet === UpdateZonePacket.OBJECT_PREFETCH) {
			val locId: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
			val shape: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			TypeLists.LOC!!.list(locId)!!.loadedModels(shape)
		} else if (UpdateZonePacket.MIDI_SONG_LOCATION === packet) {
			val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val sceneX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 4 and 0x7)
			val sceneY: Int = (packedOffset and 0x7) + UpdateZonePacket.updateZoneY
			var soundId: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
			if (soundId == 65535) {
				soundId = -1
			}
			val radiusAndLoops: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val radius: Int = radiusAndLoops shr 4 and 0xF
			val loops: Int = radiusAndLoops and 0x7
			val delay: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val volume: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
			val rate: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
			if (sceneX >= 0 && sceneY >= 0 && sceneX < MapRegion.sceneSizeX && sceneY < MapRegion.sceneSizeY) {
				val range: Int = radius + 1
				if (PlayerUpdate.localPlayer!!.movementBaseX[0] >= sceneX - range && PlayerUpdate.localPlayer!!.movementBaseX[0] <= range + sceneX && sceneY - range <= PlayerUpdate.localPlayer!!.movementBaseY[0] && sceneY + range >= PlayerUpdate.localPlayer!!.movementBaseY[0]) {
					AreaSoundPlayer.playAreaSoundSynth(loops, radius + (sceneY shl 8) + (UpdateZonePacketDecoder.updateZonePlane shl 24) + (sceneX shl 16), delay, volume, rate, soundId)
				}
			}
		} else if (UpdateZonePacket.OBJ_ANIM === packet) {
			var animationId: Int = BufferedConnectionContext.receivedBuffer!!.readUShortLittle()
			if (animationId == 65535) {
				animationId = -1
			}
			val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByteSubtract()
			val sceneX: Int = (packedOffset shr 4 and 0x7) + UpdateZonePacket.updateZoneX
			val sceneY: Int = UpdateZonePacket.updateZoneY + (packedOffset and 0x7)
			val shapeAndRotation: Int = BufferedConnectionContext.receivedBuffer!!.readUByteInverse()
			val shape: Int = shapeAndRotation shr 2
			val rotation: Int = shapeAndRotation and 0x3
			val layer: Int = LocLayer.OBJECT_TYPE_SLOTS[shape]
			Scene.animateObject(sceneY, animationId, shape, sceneX, rotation, UpdateZonePacketDecoder.updateZonePlane, layer)
		} else {
			@Pc(655) var local655: Int
			@Pc(659) var local659: Int
			@Pc(678) var local678: Int
			if (packet === UpdateZonePacket.MAP_PROJANIM_HALFSQ) {
				val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				var startX: Int = (packedOffset shr 4 and 0xF) + UpdateZonePacket.updateZoneX * 2
				var startY: Int = (packedOffset and 0xF) + UpdateZonePacket.updateZoneY * 2
				val flags: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				@Pc(613) val adjustToTerrainHeight: Boolean = (flags and 0x1) != 0
				@Pc(621) val adjustToBASAnimFrameHeight: Boolean = (flags and 0x2) != 0
				val wornSlot: Int = if (adjustToBASAnimFrameHeight) flags shr 2 else -1
				var endX: Int = BufferedConnectionContext.receivedBuffer!!.readByte() + startX
				var endY: Int = BufferedConnectionContext.receivedBuffer!!.readByte() + startY
				val sourceIndex: Int = BufferedConnectionContext.receivedBuffer!!.readShort()
				val targetIndex: Int = BufferedConnectionContext.receivedBuffer!!.readShort()
				val projectileId: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				var startHeight: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				if (adjustToBASAnimFrameHeight) {
					startHeight = startHeight.toByte().toInt()
				} else {
					startHeight *= 4
				}
				var endHeight: Int = BufferedConnectionContext.receivedBuffer!!.readUByte() * 4
				@Pc(684) val startTime: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				@Pc(688) val endTime: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				@Pc(692) var angle: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				@Pc(696) var slope: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				if (angle == 255) {
					angle = -1
				}
				if (startX >= 0 && startY >= 0 && MapRegion.sceneSizeX * 2 > startX && MapRegion.sceneSizeX * 2 > startY && endX >= 0 && endY >= 0 && MapRegion.sceneSizeY * 2 > endX && MapRegion.sceneSizeY * 2 > endY && projectileId != 65535) {
					endY *= 256
					endX = endX * 256
					endHeight = endHeight shl 0x2
					startX = startX * 256
					slope = slope shl 0x2
					startHeight = startHeight shl 0x2
					startY *= 256
					if (sourceIndex != 0 && wornSlot != -1) {
						@Pc(783) var pathingEntity: PathingEntity? = null
						@Pc(793) val entityIndex: Int
						if (sourceIndex < 0) {
							entityIndex = -sourceIndex - 1
							if (entityIndex == PlayerUpdate.myPlayerIndex) {
								pathingEntity = PlayerUpdate.localPlayer
							} else {
								pathingEntity = PlayerUpdate.players[entityIndex]
							}
						} else {
							entityIndex = sourceIndex - 1
							@Pc(819) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(entityIndex.toLong()) as NPCEntityNode?
							if (npcEntityNode != null) {
								pathingEntity = npcEntityNode.npc
							}
						}
						if (pathingEntity != null) {
							@Pc(830) val basType: BasType? = pathingEntity.getBasType()
							if (basType!!.equipmentTransforms != null && basType.equipmentTransforms!![wornSlot] != null) {
								startHeight -= basType.equipmentTransforms!![wornSlot]!![1]
							}
							if (basType.projectionOffset != null && basType.projectionOffset!![wornSlot] != null) {
								startHeight -= basType.projectionOffset!![wornSlot]!![1]
							}
						}
					}
					@Pc(888) val projectileAnimation: ProjectileAnimation = ProjectileAnimation(projectileId, UpdateZonePacketDecoder.updateZonePlane, UpdateZonePacketDecoder.updateZonePlane, startX, startY, startHeight, startTime + ClientState.clientCycle, endTime + ClientState.clientCycle, angle, slope, sourceIndex, targetIndex, endHeight, adjustToTerrainHeight, wornSlot)
					projectileAnimation.target(Scene.tileHeightForAbsolutePosition(endX, UpdateZonePacketDecoder.updateZonePlane, endY) - endHeight, endY, startTime + ClientState.clientCycle, endX)
					ProjectileAnimation.aClass341_24.addLast(ProjectileAnimationNode(projectileAnimation))
				}
			} else if (packet === UpdateZonePacket.REMOVE_GROUND_ITEM) {
				val itemId: Int = BufferedConnectionContext.receivedBuffer!!.readUShortAddLittle()
				val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByteSubtract()
				val sceneY: Int = UpdateZonePacket.updateZoneY + (packedOffset and 0x7)
				val absoluteY: Int = MapRegion.sceneBaseY + sceneY
				val sceneX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 4 and 0x7)
				val absoluteX: Int = MapRegion.sceneBaseX + sceneX
				@Pc(960) val objStack: ObjStack? = ObjStack.GROUND_ITEMS.get(((absoluteY shl 14) or (UpdateZonePacketDecoder.updateZonePlane shl 28) or absoluteX).toLong()) as ObjStack?
				if (objStack != null) {
					@Pc(968) var objStackEntry: ObjStackEntry? = objStack.groundItems.first() as ObjStackEntry?
					while (objStackEntry != null) {
						if (objStackEntry.id == (itemId and 0x7FFF)) {
							objStackEntry.unlink()
							break
						}
						objStackEntry = objStack.groundItems.next() as ObjStackEntry?
					}
					if (objStack.groundItems.isEmpty()) {
						objStack.unlink()
					}
					if (sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && MapRegion.sceneSizeY > sceneY) {
						ObjStackEntity.refreshObjStack(sceneY, sceneX, UpdateZonePacketDecoder.updateZonePlane)
					}
				}
			} else if (UpdateZonePacket.CREATE_GROUND_ITEM === packet) {
				val itemCount: Int = BufferedConnectionContext.receivedBuffer!!.readUShortLittle()
				val itemId: Int = BufferedConnectionContext.receivedBuffer!!.readUShortLittle()
				val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				val sceneY: Int = UpdateZonePacket.updateZoneY + (packedOffset and 0x7)
				val absoluteY: Int = sceneY + MapRegion.sceneBaseY
				val sceneX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 4 and 0x7)
				val absoluteX: Int = MapRegion.sceneBaseX + sceneX
				@Pc(1074) val withinScene: Boolean = sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && MapRegion.sceneSizeY > sceneY
				if (withinScene || MapRegion.allowsOutOfBounds(MapRegion.areaMode)) {
					ObjStack.addGroundItem(ObjStackEntry(itemId, itemCount), UpdateZonePacketDecoder.updateZonePlane, absoluteY, absoluteX)
					if (withinScene) {
						ObjStackEntity.refreshObjStack(sceneY, sceneX, UpdateZonePacketDecoder.updateZonePlane)
					}
				}
			} else if (packet === UpdateZonePacket.DESTROY_OBJECT) {
				val shapeAndRotation: Int = BufferedConnectionContext.receivedBuffer!!.readUByteAdd()
				val shape: Int = shapeAndRotation shr 2
				val rotation: Int = shapeAndRotation and 0x3
				val layer: Int = LocLayer.OBJECT_TYPE_SLOTS[shape]
				val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				val sceneX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 4 and 0x7)
				val sceneY: Int = UpdateZonePacket.updateZoneY + (packedOffset and 0x7)
				if (MapRegion.allowsOutOfBounds(MapRegion.areaMode) || sceneX >= 0 && sceneY >= 0 && sceneX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > sceneY) {
					LocAction.queueLocChange(UpdateZonePacketDecoder.updateZonePlane, sceneY, shape, layer, rotation, -1, sceneX)
				}
			} else if (UpdateZonePacket.GROUND_ITEM_COUNT === packet) {
				val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				val sceneY: Int = UpdateZonePacket.updateZoneY + (packedOffset and 0x7)
				val absoluteY: Int = sceneY + MapRegion.sceneBaseY
				val sceneX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 4 and 0x7)
				val absoluteX: Int = MapRegion.sceneBaseX + sceneX
				val itemId: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				val oldCount: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				val newCount: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				if (ObjStack.GROUND_ITEMS != null) {
					@Pc(1243) val objStack2: ObjStack? = ObjStack.GROUND_ITEMS.get((absoluteX or (UpdateZonePacketDecoder.updateZonePlane shl 28) or (absoluteY shl 14)).toLong()) as ObjStack?
					if (objStack2 != null) {
						@Pc(1251) var objStackEntry2: ObjStackEntry? = objStack2.groundItems.first() as ObjStackEntry?
						while (objStackEntry2 != null) {
							if (objStackEntry2.id == (itemId and 0x7FFF) && oldCount == objStackEntry2.count) {
								objStackEntry2.unlink()
								objStackEntry2.count = newCount
								ObjStack.addGroundItem(objStackEntry2, UpdateZonePacketDecoder.updateZonePlane, absoluteY, absoluteX)
								break
							}
							objStackEntry2 = objStack2.groundItems.next() as ObjStackEntry?
						}
						if (sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && sceneY < MapRegion.sceneSizeY) {
							ObjStackEntity.refreshObjStack(sceneY, sceneX, UpdateZonePacketDecoder.updateZonePlane)
						}
					}
				}
			} else if (packet === UpdateZonePacket.GROUND_ITEM_REVEAL) {
				val itemCount: Int = BufferedConnectionContext.receivedBuffer!!.readUShortLittle()
				val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				val sceneY: Int = (packedOffset and 0x7) + UpdateZonePacket.updateZoneY
				val absoluteY: Int = MapRegion.sceneBaseY + sceneY
				val sceneX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 4 and 0x7)
				val absoluteX: Int = MapRegion.sceneBaseX + sceneX
				val itemId: Int = BufferedConnectionContext.receivedBuffer!!.readUShortAdd()
				val ownerIndex: Int = BufferedConnectionContext.receivedBuffer!!.readUShortAdd()
				if (ownerIndex != PlayerUpdate.myPlayerIndex) {
					@Pc(1381) val withinScene: Boolean = sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && MapRegion.sceneSizeY > sceneY
					if (withinScene || MapRegion.allowsOutOfBounds(MapRegion.areaMode)) {
						ObjStack.addGroundItem(ObjStackEntry(itemId, itemCount), UpdateZonePacketDecoder.updateZonePlane, absoluteY, absoluteX)
						if (withinScene) {
							ObjStackEntity.refreshObjStack(sceneY, sceneX, UpdateZonePacketDecoder.updateZonePlane)
						}
					}
				}
			} else if (packet === UpdateZonePacket.MAP_PROJANIM) {
				val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				@Pc(1427) val useFloorHeight: Boolean = (packedOffset and 0x80) != 0
				var startX: Int = UpdateZonePacket.updateZoneX + (packedOffset shr 3 and 0x7)
				var startY: Int = (packedOffset and 0x7) + UpdateZonePacket.updateZoneY
				var endX: Int = startX + BufferedConnectionContext.receivedBuffer!!.readByte()
				var endY: Int = BufferedConnectionContext.receivedBuffer!!.readByte() + startY
				val targetIndex: Int = BufferedConnectionContext.receivedBuffer!!.readShort()
				val projectileId: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				var startHeight: Int = BufferedConnectionContext.receivedBuffer!!.readUByte() * 4
				var endHeight: Int = BufferedConnectionContext.receivedBuffer!!.readUByte() * 4
				val startTime: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				val endTime: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				var angle: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				if (angle == 255) {
					angle = -1
				}
				var slope: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				if (startX >= 0 && startY >= 0 && MapRegion.sceneSizeX > startX && startY < MapRegion.sceneSizeY && endX >= 0 && endY >= 0 && MapRegion.sceneSizeX > endX && endY < MapRegion.sceneSizeY && projectileId != 65535) {
					slope = slope shl 0x2
					endY = endY * 512 + 256
					endHeight = endHeight shl 0x2
					endX = endX * 512 + 256
					startY = startY * 512 + 256
					startX = startX * 512 + 256
					startHeight = startHeight shl 0x2
					@Pc(1605) val projectileAnimation2: ProjectileAnimation = ProjectileAnimation(projectileId, UpdateZonePacketDecoder.updateZonePlane, UpdateZonePacketDecoder.updateZonePlane, startX, startY, startHeight, startTime + ClientState.clientCycle, ClientState.clientCycle + endTime, angle, slope, 0, targetIndex, endHeight, useFloorHeight, -1)
					projectileAnimation2.target(Scene.tileHeightForAbsolutePosition(endX, UpdateZonePacketDecoder.updateZonePlane, endY) - endHeight, endY, startTime + ClientState.clientCycle, endX)
					ProjectileAnimation.aClass341_24.addLast(ProjectileAnimationNode(projectileAnimation2))
				}
			} else if (UpdateZonePacket.TILE_MESSAGE === packet) {
				BufferedConnectionContext.receivedBuffer!!.readUByte()
				val packedOffset: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				val sceneX: Int = (packedOffset shr 4 and 0x7) + UpdateZonePacket.updateZoneX
				val sceneY: Int = (packedOffset and 0x7) + UpdateZonePacket.updateZoneY
				val duration: Int = BufferedConnectionContext.receivedBuffer!!.readUShort()
				val tileHeight: Int = BufferedConnectionContext.receivedBuffer!!.readUByte()
				val color: Int = BufferedConnectionContext.receivedBuffer!!.readUMedium()
				@Pc(1674) val string: String? = BufferedConnectionContext.receivedBuffer!!.readString()
				TextCoordList.offer(duration, sceneX, color, string, tileHeight, sceneY, UpdateZonePacketDecoder.updateZonePlane)
			} else {
				ExplicitRuntimeException.reportError("T3 - " + packet, null)
				ClientLogout.logout(false)
			}
		}
	}

	@OriginalMember(owner = "client!aw", name = "T", descriptor = "I")
	@JvmField
	var updateZonePlane: Int = 0
}
