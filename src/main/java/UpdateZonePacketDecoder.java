import com.jagex.game.runetek5.UpdateZonePacket;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.PathingEntity;
import com.jagex.game.runetek5.entity.ProjectileAnimation;
import com.jagex.game.runetek5.cache.node.lru.ProjectileAnimationNode;
import com.jagex.game.runetek5.entity.SpotAnimation;
import com.jagex.game.runetek5.config.sequence.PositionedGfx;
import com.jagex.game.runetek5.TextCoordList;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.cache.node.ObjStackEntry;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.ObjStack;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.scene.LocAction;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.ObjStackEntity;
import com.jagex.game.runetek5.core.constants.LocLayer;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.client.ClientState;

public final class UpdateZonePacketDecoder {

	@OriginalMember(owner = "client!sfa", name = "a", descriptor = "(Lclient!kn;B)V")
	public static void decodeUpdateZonePacket(@OriginalArg(0) UpdateZonePacket packet) {
		@Pc(12) int local12;
		@Pc(16) int local16;
		@Pc(20) int local20;
		@Pc(24) int local24;
		@Pc(28) int local28;
		@Pc(32) int local32;
		@Pc(40) int local40;
		@Pc(47) int local47;
		if (packet == UpdateZonePacket.CREATE_OBJECT) {
			int shapeAndRotation = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
			int shape = shapeAndRotation >> 2;
			int rotation = shapeAndRotation & 0x3;
			int layer = LocLayer.OBJECT_TYPE_SLOTS[shape];
			int locId = BufferedConnectionContext.receivedBuffer.readUShort();
			int packedOffset = BufferedConnectionContext.receivedBuffer.readUByteAdd();
			int sceneX = UpdateZonePacket.updateZoneX + (packedOffset >> 4 & 0x7);
			int sceneY = UpdateZonePacket.updateZoneY + (packedOffset & 0x7);
			if (MapRegion.allowsOutOfBounds(MapRegion.areaMode) || sceneX >= 0 && sceneY >= 0 && sceneX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > sceneY) {
				LocAction.queueLocChange(UpdateZonePacketDecoder.updateZonePlane, sceneY, shape, layer, rotation, locId, sceneX);
			}
			return;
		}
		@Pc(157) int local157;
		@Pc(155) int local155;
		if (packet == UpdateZonePacket.SPOT_ANIM) {
			int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
			int sceneX = UpdateZonePacket.updateZoneX + (packedOffset >> 4 & 0x7);
			int sceneY = UpdateZonePacket.updateZoneY + (packedOffset & 0x7);
			int spotAnimId = BufferedConnectionContext.receivedBuffer.readUShort();
			int height = BufferedConnectionContext.receivedBuffer.readUByte();
			int delay = BufferedConnectionContext.receivedBuffer.readUShort();
			int rotation = BufferedConnectionContext.receivedBuffer.readUByte();
			if (sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && MapRegion.sceneSizeY > sceneY) {
				int fineX = sceneX * 512 + 256;
				int fineY = sceneY * 512 + 256;
				int virtualLevel = UpdateZonePacketDecoder.updateZonePlane;
				if (virtualLevel < 3 && SettingsBits.isBridge(sceneY, sceneX)) {
					virtualLevel++;
				}
				@Pc(192) SpotAnimation spotAnimation = new SpotAnimation(spotAnimId, delay, ClientState.clientCycle, UpdateZonePacketDecoder.updateZonePlane, virtualLevel, fineX, Scene.tileHeightForAbsolutePosition(fineX, UpdateZonePacketDecoder.updateZonePlane, fineY) - height, fineY, sceneX, sceneX, sceneY, sceneY, rotation);
				Renderers.activeToolkit.addLast(new PositionedGfx(spotAnimation));
			}
			return;
		}
		@Pc(282) int local282;
		if (packet == UpdateZonePacket.SOUND_AREA) {
			int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
			int sceneX = UpdateZonePacket.updateZoneX + (packedOffset >> 4 & 0x7);
			int sceneY = (packedOffset & 0x7) + UpdateZonePacket.updateZoneY;
			int soundId = BufferedConnectionContext.receivedBuffer.readUShort();
			if (soundId == 65535) {
				soundId = -1;
			}
			int radiusAndLoops = BufferedConnectionContext.receivedBuffer.readUByte();
			int radius = radiusAndLoops >> 4 & 0xF;
			int loops = radiusAndLoops & 0x7;
			int delay = BufferedConnectionContext.receivedBuffer.readUByte();
			int volume = BufferedConnectionContext.receivedBuffer.readUByte();
			int rate = BufferedConnectionContext.receivedBuffer.readUShort();
			if (sceneX >= 0 && sceneY >= 0 && sceneX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > sceneY) {
				int range = radius + 1;
				if (sceneX - range <= PlayerUpdate.localPlayer.movementBaseX[0] && PlayerUpdate.localPlayer.movementBaseX[0] <= range + sceneX && sceneY - range <= PlayerUpdate.localPlayer.movementBaseY[0] && PlayerUpdate.localPlayer.movementBaseY[0] <= sceneY + range) {
					AreaSoundPlayer.playAreaSoundVorbis(soundId, volume, (sceneY << 8) + (UpdateZonePacketDecoder.updateZonePlane << 24) + (sceneX << 16) + radius, loops, rate, delay);
				}
			}
		} else if (packet == UpdateZonePacket.OBJECT_PREFETCH) {
			int locId = BufferedConnectionContext.receivedBuffer.readUShort();
			int shape = BufferedConnectionContext.receivedBuffer.readUByte();
			TypeLists.LOC.list(locId).loadedModels(shape);
		} else if (UpdateZonePacket.MIDI_SONG_LOCATION == packet) {
			int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
			int sceneX = UpdateZonePacket.updateZoneX + (packedOffset >> 4 & 0x7);
			int sceneY = (packedOffset & 0x7) + UpdateZonePacket.updateZoneY;
			int soundId = BufferedConnectionContext.receivedBuffer.readUShort();
			if (soundId == 65535) {
				soundId = -1;
			}
			int radiusAndLoops = BufferedConnectionContext.receivedBuffer.readUByte();
			int radius = radiusAndLoops >> 4 & 0xF;
			int loops = radiusAndLoops & 0x7;
			int delay = BufferedConnectionContext.receivedBuffer.readUByte();
			int volume = BufferedConnectionContext.receivedBuffer.readUByte();
			int rate = BufferedConnectionContext.receivedBuffer.readUShort();
			if (sceneX >= 0 && sceneY >= 0 && sceneX < MapRegion.sceneSizeX && sceneY < MapRegion.sceneSizeY) {
				int range = radius + 1;
				if (PlayerUpdate.localPlayer.movementBaseX[0] >= sceneX - range && PlayerUpdate.localPlayer.movementBaseX[0] <= range + sceneX && sceneY - range <= PlayerUpdate.localPlayer.movementBaseY[0] && sceneY + range >= PlayerUpdate.localPlayer.movementBaseY[0]) {
					AreaSoundPlayer.playAreaSoundSynth(loops, radius + (sceneY << 8) + (UpdateZonePacketDecoder.updateZonePlane << 24) + (sceneX << 16), delay, volume, rate, soundId);
				}
			}
		} else if (UpdateZonePacket.OBJ_ANIM == packet) {
			int animationId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
			if (animationId == 65535) {
				animationId = -1;
			}
			int packedOffset = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
			int sceneX = (packedOffset >> 4 & 0x7) + UpdateZonePacket.updateZoneX;
			int sceneY = UpdateZonePacket.updateZoneY + (packedOffset & 0x7);
			int shapeAndRotation = BufferedConnectionContext.receivedBuffer.readUByteInverse();
			int shape = shapeAndRotation >> 2;
			int rotation = shapeAndRotation & 0x3;
			int layer = LocLayer.OBJECT_TYPE_SLOTS[shape];
			Scene.animateObject(sceneY, animationId, shape, sceneX, rotation, UpdateZonePacketDecoder.updateZonePlane, layer);
		} else {
			@Pc(655) int local655;
			@Pc(659) int local659;
			@Pc(678) int local678;
			if (packet == UpdateZonePacket.MAP_PROJANIM_HALFSQ) {
				int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
				int startX = (packedOffset >> 4 & 0xF) + UpdateZonePacket.updateZoneX * 2;
				int startY = (packedOffset & 0xF) + UpdateZonePacket.updateZoneY * 2;
				int flags = BufferedConnectionContext.receivedBuffer.readUByte();
				@Pc(613) boolean adjustToTerrainHeight = (flags & 0x1) != 0;
				@Pc(621) boolean adjustToBASAnimFrameHeight = (flags & 0x2) != 0;
				int wornSlot = adjustToBASAnimFrameHeight ? flags >> 2 : -1;
				int endX = BufferedConnectionContext.receivedBuffer.readByte() + startX;
				int endY = BufferedConnectionContext.receivedBuffer.readByte() + startY;
				int sourceIndex = BufferedConnectionContext.receivedBuffer.readShort();
				int targetIndex = BufferedConnectionContext.receivedBuffer.readShort();
				int projectileId = BufferedConnectionContext.receivedBuffer.readUShort();
				int startHeight = BufferedConnectionContext.receivedBuffer.readUByte();
				if (adjustToBASAnimFrameHeight) {
					startHeight = (byte) startHeight;
				} else {
					startHeight *= 4;
				}
				int endHeight = BufferedConnectionContext.receivedBuffer.readUByte() * 4;
				@Pc(684) int startTime = BufferedConnectionContext.receivedBuffer.readUShort();
				@Pc(688) int endTime = BufferedConnectionContext.receivedBuffer.readUShort();
				@Pc(692) int angle = BufferedConnectionContext.receivedBuffer.readUByte();
				@Pc(696) int slope = BufferedConnectionContext.receivedBuffer.readUShort();
				if (angle == 255) {
					angle = -1;
				}
				if (startX >= 0 && startY >= 0 && MapRegion.sceneSizeX * 2 > startX && MapRegion.sceneSizeX * 2 > startY && endX >= 0 && endY >= 0 && MapRegion.sceneSizeY * 2 > endX && MapRegion.sceneSizeY * 2 > endY && projectileId != 65535) {
					endY *= 256;
					endX = endX * 256;
					endHeight <<= 0x2;
					startX = startX * 256;
					slope <<= 0x2;
					startHeight <<= 0x2;
					startY *= 256;
					if (sourceIndex != 0 && wornSlot != -1) {
						@Pc(783) PathingEntity pathingEntity = null;
						@Pc(793) int entityIndex;
						if (sourceIndex < 0) {
							entityIndex = -sourceIndex - 1;
							if (entityIndex == PlayerUpdate.myPlayerIndex) {
								pathingEntity = PlayerUpdate.localPlayer;
							} else {
								pathingEntity = PlayerUpdate.players[entityIndex];
							}
						} else {
							entityIndex = sourceIndex - 1;
							@Pc(819) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) entityIndex);
							if (npcEntityNode != null) {
								pathingEntity = npcEntityNode.npc;
							}
						}
						if (pathingEntity != null) {
							@Pc(830) BasType basType = pathingEntity.getBasType();
							if (basType.equipmentTransforms != null && basType.equipmentTransforms[wornSlot] != null) {
								startHeight -= basType.equipmentTransforms[wornSlot][1];
							}
							if (basType.projectionOffset != null && basType.projectionOffset[wornSlot] != null) {
								startHeight -= basType.projectionOffset[wornSlot][1];
							}
						}
					}
					@Pc(888) ProjectileAnimation projectileAnimation = new ProjectileAnimation(projectileId, UpdateZonePacketDecoder.updateZonePlane, UpdateZonePacketDecoder.updateZonePlane, startX, startY, startHeight, startTime + ClientState.clientCycle, endTime + ClientState.clientCycle, angle, slope, sourceIndex, targetIndex, endHeight, adjustToTerrainHeight, wornSlot);
					projectileAnimation.target(Scene.tileHeightForAbsolutePosition(endX, UpdateZonePacketDecoder.updateZonePlane, endY) - endHeight, endY, startTime + ClientState.clientCycle, endX);
					ProjectileAnimation.aClass341_24.addLast(new ProjectileAnimationNode(projectileAnimation));
				}
			} else if (packet == UpdateZonePacket.REMOVE_GROUND_ITEM) {
				int itemId = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
				int packedOffset = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
				int sceneY = UpdateZonePacket.updateZoneY + (packedOffset & 0x7);
				int absoluteY = MapRegion.sceneBaseY + sceneY;
				int sceneX = UpdateZonePacket.updateZoneX + (packedOffset >> 4 & 0x7);
				int absoluteX = MapRegion.sceneBaseX + sceneX;
				@Pc(960) ObjStack objStack = (ObjStack) ObjStack.GROUND_ITEMS.get((long) (absoluteY << 14 | UpdateZonePacketDecoder.updateZonePlane << 28 | absoluteX));
				if (objStack != null) {
					for (@Pc(968) ObjStackEntry objStackEntry = (ObjStackEntry) objStack.groundItems.first(); objStackEntry != null; objStackEntry = (ObjStackEntry) objStack.groundItems.next()) {
						if (objStackEntry.id == (itemId & 0x7FFF)) {
							objStackEntry.unlink();
							break;
						}
					}
					if (objStack.groundItems.isEmpty()) {
						objStack.unlink();
					}
					if (sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && MapRegion.sceneSizeY > sceneY) {
						ObjStackEntity.refreshObjStack(sceneY, sceneX, UpdateZonePacketDecoder.updateZonePlane);
					}
				}
			} else if (UpdateZonePacket.CREATE_GROUND_ITEM == packet) {
				int itemCount = BufferedConnectionContext.receivedBuffer.readUShortLittle();
				int itemId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
				int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
				int sceneY = UpdateZonePacket.updateZoneY + (packedOffset & 0x7);
				int absoluteY = sceneY + MapRegion.sceneBaseY;
				int sceneX = UpdateZonePacket.updateZoneX + (packedOffset >> 4 & 0x7);
				int absoluteX = MapRegion.sceneBaseX + sceneX;
				@Pc(1074) boolean withinScene = sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && MapRegion.sceneSizeY > sceneY;
				if (withinScene || MapRegion.allowsOutOfBounds(MapRegion.areaMode)) {
					ObjStack.addGroundItem(new ObjStackEntry(itemId, itemCount), UpdateZonePacketDecoder.updateZonePlane, absoluteY, absoluteX);
					if (withinScene) {
						ObjStackEntity.refreshObjStack(sceneY, sceneX, UpdateZonePacketDecoder.updateZonePlane);
					}
				}
			} else if (packet == UpdateZonePacket.DESTROY_OBJECT) {
				int shapeAndRotation = BufferedConnectionContext.receivedBuffer.readUByteAdd();
				int shape = shapeAndRotation >> 2;
				int rotation = shapeAndRotation & 0x3;
				int layer = LocLayer.OBJECT_TYPE_SLOTS[shape];
				int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
				int sceneX = UpdateZonePacket.updateZoneX + (packedOffset >> 4 & 0x7);
				int sceneY = UpdateZonePacket.updateZoneY + (packedOffset & 0x7);
				if (MapRegion.allowsOutOfBounds(MapRegion.areaMode) || sceneX >= 0 && sceneY >= 0 && sceneX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > sceneY) {
					LocAction.queueLocChange(UpdateZonePacketDecoder.updateZonePlane, sceneY, shape, layer, rotation, -1, sceneX);
				}
			} else if (UpdateZonePacket.GROUND_ITEM_COUNT == packet) {
				int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
				int sceneY = UpdateZonePacket.updateZoneY + (packedOffset & 0x7);
				int absoluteY = sceneY + MapRegion.sceneBaseY;
				int sceneX = UpdateZonePacket.updateZoneX + (packedOffset >> 4 & 0x7);
				int absoluteX = MapRegion.sceneBaseX + sceneX;
				int itemId = BufferedConnectionContext.receivedBuffer.readUShort();
				int oldCount = BufferedConnectionContext.receivedBuffer.readUShort();
				int newCount = BufferedConnectionContext.receivedBuffer.readUShort();
				if (ObjStack.GROUND_ITEMS != null) {
					@Pc(1243) ObjStack objStack2 = (ObjStack) ObjStack.GROUND_ITEMS.get((long) (absoluteX | UpdateZonePacketDecoder.updateZonePlane << 28 | absoluteY << 14));
					if (objStack2 != null) {
						for (@Pc(1251) ObjStackEntry objStackEntry2 = (ObjStackEntry) objStack2.groundItems.first(); objStackEntry2 != null; objStackEntry2 = (ObjStackEntry) objStack2.groundItems.next()) {
							if (objStackEntry2.id == (itemId & 0x7FFF) && oldCount == objStackEntry2.count) {
								objStackEntry2.unlink();
								objStackEntry2.count = newCount;
								ObjStack.addGroundItem(objStackEntry2, UpdateZonePacketDecoder.updateZonePlane, absoluteY, absoluteX);
								break;
							}
						}
						if (sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && sceneY < MapRegion.sceneSizeY) {
							ObjStackEntity.refreshObjStack(sceneY, sceneX, UpdateZonePacketDecoder.updateZonePlane);
						}
					}
				}
			} else if (packet == UpdateZonePacket.GROUND_ITEM_REVEAL) {
				int itemCount = BufferedConnectionContext.receivedBuffer.readUShortLittle();
				int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
				int sceneY = (packedOffset & 0x7) + UpdateZonePacket.updateZoneY;
				int absoluteY = MapRegion.sceneBaseY + sceneY;
				int sceneX = UpdateZonePacket.updateZoneX + (packedOffset >> 4 & 0x7);
				int absoluteX = MapRegion.sceneBaseX + sceneX;
				int itemId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
				int ownerIndex = BufferedConnectionContext.receivedBuffer.readUShortAdd();
				if (ownerIndex != PlayerUpdate.myPlayerIndex) {
					@Pc(1381) boolean withinScene = sceneX >= 0 && sceneY >= 0 && MapRegion.sceneSizeX > sceneX && MapRegion.sceneSizeY > sceneY;
					if (withinScene || MapRegion.allowsOutOfBounds(MapRegion.areaMode)) {
						ObjStack.addGroundItem(new ObjStackEntry(itemId, itemCount), UpdateZonePacketDecoder.updateZonePlane, absoluteY, absoluteX);
						if (withinScene) {
							ObjStackEntity.refreshObjStack(sceneY, sceneX, UpdateZonePacketDecoder.updateZonePlane);
						}
					}
				}
			} else if (packet == UpdateZonePacket.MAP_PROJANIM) {
				int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
				@Pc(1427) boolean useFloorHeight = (packedOffset & 0x80) != 0;
				int startX = UpdateZonePacket.updateZoneX + (packedOffset >> 3 & 0x7);
				int startY = (packedOffset & 0x7) + UpdateZonePacket.updateZoneY;
				int endX = startX + BufferedConnectionContext.receivedBuffer.readByte();
				int endY = BufferedConnectionContext.receivedBuffer.readByte() + startY;
				int targetIndex = BufferedConnectionContext.receivedBuffer.readShort();
				int projectileId = BufferedConnectionContext.receivedBuffer.readUShort();
				int startHeight = BufferedConnectionContext.receivedBuffer.readUByte() * 4;
				int endHeight = BufferedConnectionContext.receivedBuffer.readUByte() * 4;
				int startTime = BufferedConnectionContext.receivedBuffer.readUShort();
				int endTime = BufferedConnectionContext.receivedBuffer.readUShort();
				int angle = BufferedConnectionContext.receivedBuffer.readUByte();
				if (angle == 255) {
					angle = -1;
				}
				int slope = BufferedConnectionContext.receivedBuffer.readUShort();
				if (startX >= 0 && startY >= 0 && MapRegion.sceneSizeX > startX && startY < MapRegion.sceneSizeY && endX >= 0 && endY >= 0 && MapRegion.sceneSizeX > endX && endY < MapRegion.sceneSizeY && projectileId != 65535) {
					slope <<= 0x2;
					endY = endY * 512 + 256;
					endHeight <<= 0x2;
					endX = endX * 512 + 256;
					startY = startY * 512 + 256;
					startX = startX * 512 + 256;
					startHeight <<= 0x2;
					@Pc(1605) ProjectileAnimation projectileAnimation2 = new ProjectileAnimation(projectileId, UpdateZonePacketDecoder.updateZonePlane, UpdateZonePacketDecoder.updateZonePlane, startX, startY, startHeight, startTime + ClientState.clientCycle, ClientState.clientCycle + endTime, angle, slope, 0, targetIndex, endHeight, useFloorHeight, -1);
					projectileAnimation2.target(Scene.tileHeightForAbsolutePosition(endX, UpdateZonePacketDecoder.updateZonePlane, endY) - endHeight, endY, startTime + ClientState.clientCycle, endX);
					ProjectileAnimation.aClass341_24.addLast(new ProjectileAnimationNode(projectileAnimation2));
				}
			} else if (UpdateZonePacket.TILE_MESSAGE == packet) {
				BufferedConnectionContext.receivedBuffer.readUByte();
				int packedOffset = BufferedConnectionContext.receivedBuffer.readUByte();
				int sceneX = (packedOffset >> 4 & 0x7) + UpdateZonePacket.updateZoneX;
				int sceneY = (packedOffset & 0x7) + UpdateZonePacket.updateZoneY;
				int duration = BufferedConnectionContext.receivedBuffer.readUShort();
				int tileHeight = BufferedConnectionContext.receivedBuffer.readUByte();
				int color = BufferedConnectionContext.receivedBuffer.readUMedium();
				@Pc(1674) String string = BufferedConnectionContext.receivedBuffer.readString();
				TextCoordList.offer(duration, sceneX, color, string, tileHeight, sceneY, UpdateZonePacketDecoder.updateZonePlane);
			} else {
				ExplicitRuntimeException.reportError("T3 - " + packet, null);
				ClientLogout.logout(false);
			}
		}
	}
	@OriginalMember(owner = "client!aw", name = "T", descriptor = "I")
	public static int updateZonePlane;
}
