import com.jagex.game.runetek5.client.features.XpDrops;
import com.jagex.game.runetek5.config.iftype.SubInterfaceType;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.window.WindowModeType;
import com.jagex.game.runetek5.cache.node.ChangeLocationRequest;
import com.jagex.game.runetek5.FriendsChatPlayer;
import com.jagex.game.runetek5.UpdateZonePacket;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.graphics.texture.water.NativeWaveTextureData;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.QuickChatPhrase;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.network.packet.ServerProt;
import com.jagex.game.runetek5.SoundManager;
import com.jagex.game.runetek5.entity.SpotAnimation;
import com.jagex.game.runetek5.config.sequence.PositionedGfx;
import com.jagex.game.runetek5.ThirdLastPacket;
import com.jagex.game.runetek5.PriorityIfItemUpdate;
import com.jagex.game.runetek5.PlayerInfoBitDecoder;
import com.jagex.game.runetek5.UpdateZoneFullFollowsProt;
import com.jagex.game.runetek5.MusicTrackProt;
import com.jagex.game.runetek5.ChatLineWriter;
import com.jagex.game.runetek5.IntKeyedQuicksort;
import com.jagex.game.runetek5.network.packet.StockMarketOffer;
import com.jagex.game.runetek5.core.datastruct.LoginStateUpdate;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.IFEvents;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.config.paramtype.SkillUpdateCycle;
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType;
import com.jagex.game.runetek5.config.rsinterface.HookRequest;
import com.jagex.game.runetek5.statics.StatBaseLevels;
import java.io.IOException;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.client.loading.screen.op.instance.PriorityIfPositionUpdate;
import com.jagex.game.runetek5.cache.node.ObjStack;
import com.jagex.game.runetek5.client.web.ClientURLTools;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.client.input.console.Console;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.interaction.FriendList;
import com.jagex.game.runetek5.graphics.shader.ShaderVariableType;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.scene.LocAction;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.cache.node.lru.PulseEvent;
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer;
import com.jagex.game.runetek5.interaction.IgnoreList;
import com.jagex.game.runetek5.botdetection.ReflectionCheckManager;
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.NpcUpdate;
import com.jagex.game.runetek5.config.quickchat.QuickchatMessageTypeList;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.ChatFilter;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.input.mouse.CursorManager;
import com.jagex.game.runetek5.client.window.WindowMode;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;
import com.jagex.game.runetek5.interaction.chat.ChatBox;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.sound.player.song.SongPlayer;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import com.jagex.game.runetek5.encoding.HuffmanList;
import com.jagex.game.runetek5.EntitySceneBuilder;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.ObjStackEntity;
import com.jagex.game.runetek5.PlayMusic;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.core.constants.LocLayer;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.camera.Shake;
import com.jagex.game.runetek5.config.midi.VorbisNode;
import com.jagex.game.runetek5.network.Connection;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.Chat;
import com.jagex.game.runetek5.network.WorldConnection;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.MainLogicManager;
import com.jagex.game.runetek5.statics.OutboundByteCounter;

public final class PacketDecoder {

	@OriginalMember(owner = "client!pf", name = "b", descriptor = "(Z)Z")
	public static boolean decode() throws IOException {
		if (Connection.connection == null) {
			return false;
		}
		@Pc(73) int local73;
		if (BufferedConnectionContext.currentPacket == null) {
			if (BufferedConnectionContext.readPadding) {
				if (!Connection.connection.hasAvailable(1)) {
					return false;
				}
				Connection.connection.read(1, 0, BufferedConnectionContext.receivedBuffer.buffer);
				BufferedConnectionContext.idleReadPulses = 0;
				BufferedConnectionContext.readPadding = false;
				OutboundByteCounter.bytesRead++;
			}
			BufferedConnectionContext.receivedBuffer.currentPosition = 0;
			if (BufferedConnectionContext.receivedBuffer.largeOpcode()) {
				if (!Connection.connection.hasAvailable(1)) {
					return false;
				}
				Connection.connection.read(1, 1, BufferedConnectionContext.receivedBuffer.buffer);
				OutboundByteCounter.bytesRead++;
				BufferedConnectionContext.idleReadPulses = 0;
			}
			BufferedConnectionContext.readPadding = true;
			@Pc(69) ServerProt[] serverProts = ServerProt.serverProtValues();
			int opcode = BufferedConnectionContext.receivedBuffer.readOpcode();
			if (opcode < 0 || serverProts.length <= opcode) {
				throw new IOException("invo:" + opcode + " ip:" + BufferedConnectionContext.receivedBuffer.currentPosition);
			}
			BufferedConnectionContext.currentPacket = serverProts[opcode];
			// Not part of the original client: a server may size a packet differently (client.server, client.packets).
			BufferedConnectionContext.currentPacketSize = ClientFeatures.serverPacketSize(opcode, BufferedConnectionContext.currentPacket.size);
		}
		if (BufferedConnectionContext.currentPacketSize == -1) {
			if (!Connection.connection.hasAvailable(1)) {
				return false;
			}
			Connection.connection.read(1, 0, BufferedConnectionContext.receivedBuffer.buffer);
			BufferedConnectionContext.currentPacketSize = BufferedConnectionContext.receivedBuffer.buffer[0] & 0xFF;
			OutboundByteCounter.bytesRead++;
			BufferedConnectionContext.idleReadPulses = 0;
		}
		if (BufferedConnectionContext.currentPacketSize == -2) {
			if (!Connection.connection.hasAvailable(2)) {
				return false;
			}
			Connection.connection.read(2, 0, BufferedConnectionContext.receivedBuffer.buffer);
			BufferedConnectionContext.receivedBuffer.currentPosition = 0;
			BufferedConnectionContext.currentPacketSize = BufferedConnectionContext.receivedBuffer.readUShort();
			OutboundByteCounter.bytesRead += 2;
			BufferedConnectionContext.idleReadPulses = 0;
		}
		if (BufferedConnectionContext.currentPacketSize > 0) {
			if (!Connection.connection.hasAvailable(BufferedConnectionContext.currentPacketSize)) {
				return false;
			}
			BufferedConnectionContext.receivedBuffer.currentPosition = 0;
			Connection.connection.read(BufferedConnectionContext.currentPacketSize, 0, BufferedConnectionContext.receivedBuffer.buffer);
			OutboundByteCounter.bytesRead += BufferedConnectionContext.currentPacketSize;
			BufferedConnectionContext.idleReadPulses = 0;
		}
		ThirdLastPacket.thirdLastPacket = BufferedConnectionContext.secondLastPacket;
		BufferedConnectionContext.secondLastPacket = BufferedConnectionContext.lastPacket;
		BufferedConnectionContext.lastPacket = BufferedConnectionContext.currentPacket;
		if (ServerProt.UPDATE_SITESETTINGS_COOKIE == BufferedConnectionContext.currentPacket) {
			GameShell.setSiteSettingsCookie(BufferedConnectionContext.receivedBuffer.readString());
			BufferedConnectionContext.currentPacket = null;
			return true;
		}
		@Pc(233) String string;
		@Pc(220) int local220;
		@Pc(247) String string2;
		@Pc(261) int clanUserIndex;
		@Pc(251) String string3;
		@Pc(237) String string4;
		if (BufferedConnectionContext.currentPacket == ServerProt.ADD_IGNORE) {
			int ignoreFlags = BufferedConnectionContext.receivedBuffer.readUByte();
			@Pc(229) boolean editDisplayName = (ignoreFlags & 0x1) == 1;
			string = BufferedConnectionContext.receivedBuffer.readString();
			string4 = BufferedConnectionContext.receivedBuffer.readString();
			if (string4.equals("")) {
				string4 = string;
			}
			string2 = BufferedConnectionContext.receivedBuffer.readString();
			string3 = BufferedConnectionContext.receivedBuffer.readString();
			if (string3.equals("")) {
				string3 = string2;
			}
			if (editDisplayName) {
				for (int ignoreIndex = 0; ignoreIndex < IgnoreList.ignoreCount; ignoreIndex++) {
					if (IgnoreList.ignoreNamesUnfiltered[ignoreIndex].equals(string3)) {
						IgnoreList.ignoreNames[ignoreIndex] = string;
						IgnoreList.ignoreNamesUnfiltered[ignoreIndex] = string4;
						IgnoreList.ignorePreviousNames[ignoreIndex] = string2;
						IgnoreList.ignorePreviousNamesUnfiltered[ignoreIndex] = string3;
						break;
					}
				}
			} else {
				IgnoreList.ignoreNames[IgnoreList.ignoreCount] = string;
				IgnoreList.ignoreNamesUnfiltered[IgnoreList.ignoreCount] = string4;
				IgnoreList.ignorePreviousNames[IgnoreList.ignoreCount] = string2;
				IgnoreList.ignorePreviousNamesUnfiltered[IgnoreList.ignoreCount] = string3;
				IgnoreList.ignoreIsTemporary[IgnoreList.ignoreCount] = (ignoreFlags & 0x2) == 2;
				IgnoreList.ignoreCount++;
			}
			FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick;
			BufferedConnectionContext.currentPacket = null;
			return true;
		} else if (BufferedConnectionContext.currentPacket == ServerProt.CREATE_OBJECT) {
			UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.CREATE_OBJECT);
			BufferedConnectionContext.currentPacket = null;
			return true;
		} else if (ServerProt.CLIENT_SETVARC_SMALL == BufferedConnectionContext.currentPacket) {
			@Pc(351) byte varcValue = BufferedConnectionContext.receivedBuffer.readByte();
			int varcId = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
			PlayerVarDomain.requestVarVerifyid();
			PulseEvent.priorityClientvarcUpdate(varcId, varcValue);
			BufferedConnectionContext.currentPacket = null;
			return true;
		} else {
			@Pc(394) SubInterface subInterface;
			if (ServerProt.IF_MOVESUB == BufferedConnectionContext.currentPacket) {
				int targetComponentHash = BufferedConnectionContext.receivedBuffer.readIntLE();
				int sourceComponentHash = BufferedConnectionContext.receivedBuffer.readInt();
				PlayerVarDomain.requestVarVerifyid();
				@Pc(387) SubInterface subInterface2 = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) sourceComponentHash);
				subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) targetComponentHash);
				if (subInterface != null) {
					SubInterfaceCloser.closeSubInterface(subInterface2 == null || subInterface2.interfaceId != subInterface.interfaceId, false, subInterface);
				}
				if (subInterface2 != null) {
					subInterface2.unlink();
					InterfaceManager.OPEN_INTERFACES.put((long) targetComponentHash, subInterface2);
				}
				@Pc(429) Component component = Interface.getComponent(sourceComponentHash);
				if (component != null) {
					Component.redrawComponent(component);
				}
				component = Interface.getComponent(targetComponentHash);
				if (component != null) {
					Component.redrawComponent(component);
					Component.layoutChildren(true, component);
				}
				if (ClientState.BASE_WINDOW_ID != -1) {
					Static683.runSubInterfaceHooks(ClientState.BASE_WINDOW_ID, 1);
				}
				BufferedConnectionContext.currentPacket = null;
				return true;
			} else if (BufferedConnectionContext.currentPacket == ServerProt.SET_CURSOR) {
				MiniMenu.walkHereText = BufferedConnectionContext.currentPacketSize > 2 ? BufferedConnectionContext.receivedBuffer.readString() : LocalizedText.WALK_HERE.get(ClientState.language);
				CursorManager.activeCursorId = BufferedConnectionContext.currentPacketSize <= 0 ? -1 : BufferedConnectionContext.receivedBuffer.readUShort();
				if (CursorManager.activeCursorId == 65535) {
					CursorManager.activeCursorId = -1;
				}
				BufferedConnectionContext.currentPacket = null;
				return true;
			} else if (ServerProt.IF_SETHIDE == BufferedConnectionContext.currentPacket) {
				int hidden = BufferedConnectionContext.receivedBuffer.readUByteAdd();
				int componentHash = BufferedConnectionContext.receivedBuffer.readIntLE();
				PlayerVarDomain.requestVarVerifyid();
				PulseEvent.priorityIfHideUpdate(hidden, componentHash);
				BufferedConnectionContext.currentPacket = null;
				return true;
			} else {
				@Pc(543) int local543;
				@Pc(539) int local539;
				if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETPLAYERHEAD_IGNOREWORN) {
					int componentHash = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
					int secondaryModelId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
					int primaryModelHigh = BufferedConnectionContext.receivedBuffer.readUShortAdd();
					int primaryModelLow = BufferedConnectionContext.receivedBuffer.readUShortLittle();
					PlayerVarDomain.requestVarVerifyid();
					PulseEvent.priorityIfcontentUpdate(7, componentHash, primaryModelHigh << 16 | primaryModelLow, secondaryModelId);
					BufferedConnectionContext.currentPacket = null;
					return true;
				} else if (ServerProt.PLAYER_WEIGHT == BufferedConnectionContext.currentPacket) {
					client.runWeight = BufferedConnectionContext.receivedBuffer.readShort();
					BufferedConnectionContext.currentPacket = null;
					InterfaceManager.lastMiscUpdateTick = ClientState.serverTick;
					return true;
				} else if (BufferedConnectionContext.currentPacket == ServerProt.MIDI_SONG_LOCATION) {
					UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.MIDI_SONG_LOCATION);
					BufferedConnectionContext.currentPacket = null;
					return true;
				} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETPLAYERHEAD) {
					int componentHash = BufferedConnectionContext.receivedBuffer.readUIntMiddle();
					PlayerVarDomain.requestVarVerifyid();
					PulseEvent.priorityIfcontentUpdate(3, componentHash, -1, -1);
					BufferedConnectionContext.currentPacket = null;
					return true;
				} else {
					@Pc(645) int local645;
					@Pc(615) boolean local615;
					@Pc(619) String string5;
					@Pc(631) long local631;
					@Pc(636) long local636;
					@Pc(641) long hashLow;
					@Pc(655) int local655;
					if (BufferedConnectionContext.currentPacket == ServerProt.MESSAGE_FRIENDS_CHAT) {
						boolean hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
						string5 = BufferedConnectionContext.receivedBuffer.readString();
						string = string5;
						if (hasDisplayName) {
							string = BufferedConnectionContext.receivedBuffer.readString();
						}
						long encodedChannelName = BufferedConnectionContext.receivedBuffer.readLong();
						long messageHashHigh = BufferedConnectionContext.receivedBuffer.readUShort();
						hashLow = BufferedConnectionContext.receivedBuffer.readUMedium();
						int playerRights = BufferedConnectionContext.receivedBuffer.readUByte();
						@Pc(651) long messageHash = hashLow + (messageHashHigh << 32);
						@Pc(653) boolean ignore = false;
						int hashIndex = 0;
						while (true) {
							if (hashIndex >= 100) {
								if (playerRights <= 1) {
									if (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly) {
										ignore = true;
									} else if (IgnoreList.isIgnored(string)) {
										ignore = true;
									}
								}
								break;
							}
							if (messageHash == Chat.CHAT_MESSAGE_UNIQUE_HASHES[hashIndex]) {
								ignore = true;
								break;
							}
							hashIndex++;
						}
						if (!ignore && MapRegion.inTutorialIsland == 0) {
							Chat.CHAT_MESSAGE_UNIQUE_HASHES[Camera.chatMessageHashIndex] = messageHash;
							Camera.chatMessageHashIndex = (Camera.chatMessageHashIndex + 1) % 100;
							@Pc(712) String string6 = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer));
							if (playerRights == 2 || playerRights == 3) {
								ChatLineWriter.writeMessage("<img=1>" + string, string6, -1, "<img=1>" + string5, Utils.decodeUidName(encodedChannelName), 9, 0, string5);
							} else if (playerRights == 1) {
								ChatLineWriter.writeMessage("<img=0>" + string, string6, -1, "<img=0>" + string5, Utils.decodeUidName(encodedChannelName), 9, 0, string5);
							} else {
								ChatLineWriter.writeMessage(string, string6, -1, string5, Utils.decodeUidName(encodedChannelName), 9, 0, string5);
							}
						}
						BufferedConnectionContext.currentPacket = null;
						return true;
					} else if (BufferedConnectionContext.currentPacket == ServerProt.SET_VORBIS_SOUND) {
						int vorbisSoundId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
						PlayerVarDomain.requestVarVerifyid();
						VorbisNode.toggleVorbisPause(vorbisSoundId);
						BufferedConnectionContext.currentPacket = null;
						return true;
					} else if (BufferedConnectionContext.currentPacket == ServerProt.GROUND_ITEM_COUNT) {
						UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.GROUND_ITEM_COUNT);
						BufferedConnectionContext.currentPacket = null;
						return true;
					} else {
						@Pc(845) int local845;
						if (BufferedConnectionContext.currentPacket == ServerProt.CAM_SHAKE) {
							int shakeJitter = BufferedConnectionContext.receivedBuffer.readUByteAdd();
							int shakeIndex = BufferedConnectionContext.receivedBuffer.readUByteInverse();
							int shakePhase = BufferedConnectionContext.receivedBuffer.readUShortAdd();
							int shakeAmplitude = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
							int shakeFrequency = BufferedConnectionContext.receivedBuffer.readUByteInverse();
							PlayerVarDomain.requestVarVerifyid();
							Camera.CAM_SHAKING[shakeIndex] = true;
							Camera.CAM_SHAKE_JITTER[shakeIndex] = shakeJitter;
							Shake.CAM_SHAKE_AMPLITUDE[shakeIndex] = shakeAmplitude;
							Shake.CAM_SHAKE_FREQUENCY[shakeIndex] = shakeFrequency;
							Shake.CAM_SHAKE_PHASE[shakeIndex] = shakePhase;
							BufferedConnectionContext.currentPacket = null;
							return true;
						} else if (BufferedConnectionContext.currentPacket == ServerProt.UPDATE_ZONE_PARTIAL_FOLLOWS) {
							UpdateZonePacket.updateZoneY = BufferedConnectionContext.receivedBuffer.readByteInverse() << 3;
							UpdateZonePacketDecoder.updateZonePlane = BufferedConnectionContext.receivedBuffer.readUByteAdd();
							UpdateZonePacket.updateZoneX = BufferedConnectionContext.receivedBuffer.readByteAdd() << 3;
							BufferedConnectionContext.currentPacket = null;
							return true;
						} else if (BufferedConnectionContext.currentPacket == ServerProt.DYNAMIC_MAP_REGION) {
							ConstructedMapInit.initializeConstructedMap();
							BufferedConnectionContext.currentPacket = null;
							return false;
						} else if (ServerProt.SET_DRAW_ORDER == BufferedConnectionContext.currentPacket) {
							int drawOrder = BufferedConnectionContext.receivedBuffer.readUByteAdd();
							PlayerVarDomain.requestVarVerifyid();
							EntitySceneBuilder.entityDrawOrder = drawOrder;
							BufferedConnectionContext.currentPacket = null;
							return true;
						} else if (ServerProt.DESTROY_OBJECT == BufferedConnectionContext.currentPacket) {
							UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.DESTROY_OBJECT);
							BufferedConnectionContext.currentPacket = null;
							return true;
						} else if (BufferedConnectionContext.currentPacket == ServerProt.CAM_FORCEANGLE) {
							int angleX = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
							int angleY = BufferedConnectionContext.receivedBuffer.readUShortAdd();
							PlayerVarDomain.requestVarVerifyid();
							Camera.forceCameraAngle(angleX, angleY);
							BufferedConnectionContext.currentPacket = null;
							return true;
						} else if (ServerProt.IF_SETITEM == BufferedConnectionContext.currentPacket) {
							int itemId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
							if (itemId == 65535) {
								itemId = -1;
							}
							int componentHash = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
							int amount = BufferedConnectionContext.receivedBuffer.readInt();
							PlayerVarDomain.requestVarVerifyid();
							PriorityIfItemUpdate.priorityIfItemUpdate(amount, itemId, componentHash);
							@Pc(986) ItemType objType = TypeLists.OBJ.list(itemId);
							PulseEvent.priorityIfangleUpdate(objType.modelZoom, objType.modelRotationX, objType.modelRotationY, componentHash);
							PulseEvent.priorityIfOffsetUpdate(objType.modelOffsetX, objType.modelOffsetY, componentHash, objType.modelRotationZ);
							BufferedConnectionContext.currentPacket = null;
							return true;
						} else {
							@Pc(1046) int local1046;
							@Pc(1030) boolean local1030;
							if (ServerProt.UPDATE_INV_FULL == BufferedConnectionContext.currentPacket) {
								int containerId = BufferedConnectionContext.receivedBuffer.readUShort();
								int containerFlags = BufferedConnectionContext.receivedBuffer.readUByte();
								boolean negativeKey = (containerFlags & 0x1) == 1;
								ItemContainer.resetContainer(containerId, negativeKey);
								int slotCount = BufferedConnectionContext.receivedBuffer.readUShort();
								for (int slot = 0; slot < slotCount; slot++) {
									int itemAmount = BufferedConnectionContext.receivedBuffer.readUByteAdd();
									if (itemAmount == 255) {
										itemAmount = BufferedConnectionContext.receivedBuffer.readUIntMiddle();
									}
									int itemIdPlusOne = BufferedConnectionContext.receivedBuffer.readUShortAdd();
									ItemContainer.setItemInContainer(containerId, negativeKey, slot, itemAmount, itemIdPlusOne - 1);
								}
								ItemContainer.RECENTLY_USED_ITEM_CONTAINERS[client.recentlyUsedItemContainersCursorPos++ & 0x1F] = containerId;
								BufferedConnectionContext.currentPacket = null;
								return true;
							} else if (BufferedConnectionContext.currentPacket == ServerProt.LOGOUT_FULL) {
								ClientLogout.logout(false);
								BufferedConnectionContext.currentPacket = null;
								return false;
							} else if (BufferedConnectionContext.currentPacket == ServerProt.VARP_LARGE) {
								int varpValue = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
								int varpId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
								PlayerVarDomain.playerVarProvider.updateVarp(varpValue, varpId);
								BufferedConnectionContext.currentPacket = null;
								return true;
							} else {
								@Pc(1184) int insertIndex;
								@Pc(1778) int local1778;
								@Pc(1205) boolean local1205;
								@Pc(1213) int local1213;
								@Pc(1572) int loopType;
								@Pc(1152) boolean local1152;
								@Pc(1162) int local1162;
								@Pc(1489) boolean local1489;
								@Pc(1497) int local1497;
								if (BufferedConnectionContext.currentPacket == ServerProt.SPOT_ANIM_SPECIFIC) {
									int spotAnimSettings = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
									int spotAnimHeight = BufferedConnectionContext.receivedBuffer.readUShortAdd();
									int spotAnimId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
									int spotAnimDelay = BufferedConnectionContext.receivedBuffer.readUShort();
									int spotAnimTarget = BufferedConnectionContext.receivedBuffer.readUIntMiddle();
									boolean isSecondary = (spotAnimSettings & 0x80) != 0;
									int spotAnimRotation = spotAnimSettings & 0x7;
									int spotAnimAdjustType = spotAnimSettings >> 3 & 0xF;
									if (spotAnimAdjustType == 15) {
										spotAnimAdjustType = -1;
									}
									if (spotAnimTarget >> 30 == 0) {
										@Pc(1296) SeqType seqType;
										@Pc(1233) SpotAnimationType spotAnimationType;
										@Pc(1247) SeqType seqType2;
										if (spotAnimTarget >> 29 != 0) {
											int npcIndex = spotAnimTarget & 0xFFFF;
											@Pc(1191) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) npcIndex);
											if (npcEntityNode != null) {
												@Pc(1196) NPCEntity npcEntity = npcEntityNode.npc;
												if (spotAnimId == 65535) {
													spotAnimId = -1;
												}
												boolean applySpotAnim = true;
												int currentSpotAnimId = isSecondary ? npcEntity.secondarySpotAnimation.spotAnimationId : npcEntity.primarySpotAnimation.spotAnimationId;
												if (spotAnimId != -1 && currentSpotAnimId != -1) {
													if (spotAnimId == currentSpotAnimId) {
														spotAnimationType = TypeLists.SPOT_ANIM.list(spotAnimId);
														if (spotAnimationType.replay && spotAnimationType.sequenceId != -1) {
															seqType2 = TypeLists.SEQ.list(spotAnimationType.sequenceId);
															@Pc(1250) int reqLoopType = seqType2.loopType;
															if (reqLoopType == 0 || reqLoopType == 2) {
																applySpotAnim = false;
															} else if (reqLoopType == 1) {
																applySpotAnim = true;
															}
														}
													} else {
														spotAnimationType = TypeLists.SPOT_ANIM.list(spotAnimId);
														@Pc(1281) SpotAnimationType spotAnimationType2 = TypeLists.SPOT_ANIM.list(currentSpotAnimId);
														if (spotAnimationType.sequenceId != -1 && spotAnimationType2.sequenceId != -1) {
															seqType = TypeLists.SEQ.list(spotAnimationType.sequenceId);
															@Pc(1302) SeqType seqType3 = TypeLists.SEQ.list(spotAnimationType2.sequenceId);
															if (seqType.priority < seqType3.priority) {
																applySpotAnim = false;
															}
														}
													}
												}
												if (applySpotAnim) {
													if (isSecondary) {
														npcEntity.secondarySpotAnimation.startCycle = spotAnimDelay + ClientState.clientCycle;
														npcEntity.secondarySpotAnimation.height = spotAnimHeight;
														npcEntity.secondarySpotAnimation.spotAnimationId = spotAnimId;
														npcEntity.secondarySpotAnimation.adjustmentType = spotAnimAdjustType;
														npcEntity.secondarySpotAnimation.rotation = spotAnimRotation;
														npcEntity.secondarySpotAnimation.currentFrameDuration = 0;
														npcEntity.secondarySpotAnimation.nextFrameId = 1;
														npcEntity.secondarySpotAnimation.currentFrameId = 0;
														if (npcEntity.secondarySpotAnimation.startCycle > ClientState.clientCycle) {
															npcEntity.secondarySpotAnimation.currentFrameId = -1;
														}
														if (npcEntity.secondarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle == npcEntity.secondarySpotAnimation.startCycle) {
															int sequenceId = TypeLists.SPOT_ANIM.list(npcEntity.secondarySpotAnimation.spotAnimationId).sequenceId;
															if (sequenceId != -1) {
																seqType2 = TypeLists.SEQ.list(sequenceId);
																if (seqType2 != null && seqType2.frames != null && !npcEntity.isHidden) {
																	SoundManager.playSequenceSound(npcEntity, 0, seqType2);
																}
															}
														}
													} else {
														npcEntity.primarySpotAnimation.currentFrameDuration = 0;
														npcEntity.primarySpotAnimation.height = spotAnimHeight;
														npcEntity.primarySpotAnimation.spotAnimationId = spotAnimId;
														npcEntity.primarySpotAnimation.currentFrameId = 0;
														npcEntity.primarySpotAnimation.adjustmentType = spotAnimAdjustType;
														npcEntity.primarySpotAnimation.startCycle = ClientState.clientCycle + spotAnimDelay;
														npcEntity.primarySpotAnimation.rotation = spotAnimRotation;
														npcEntity.primarySpotAnimation.nextFrameId = 1;
														if (npcEntity.primarySpotAnimation.startCycle > ClientState.clientCycle) {
															npcEntity.primarySpotAnimation.currentFrameId = -1;
														}
														if (npcEntity.primarySpotAnimation.spotAnimationId != -1 && npcEntity.primarySpotAnimation.startCycle == ClientState.clientCycle) {
															int sequenceId = TypeLists.SPOT_ANIM.list(npcEntity.primarySpotAnimation.spotAnimationId).sequenceId;
															if (sequenceId != -1) {
																seqType2 = TypeLists.SEQ.list(sequenceId);
																if (seqType2 != null && seqType2.frames != null && !npcEntity.isHidden) {
																	SoundManager.playSequenceSound(npcEntity, 0, seqType2);
																}
															}
														}
													}
												}
											}
										} else if (spotAnimTarget >> 28 != 0) {
											int playerIndex = spotAnimTarget & 0xFFFF;
											@Pc(1476) PlayerEntity playerEntity;
											if (PlayerUpdate.myPlayerIndex == playerIndex) {
												playerEntity = PlayerUpdate.localPlayer;
											} else {
												playerEntity = PlayerUpdate.players[playerIndex];
											}
											if (playerEntity != null) {
												if (spotAnimId == 65535) {
													spotAnimId = -1;
												}
												boolean applySpotAnim = true;
												int currentSpotAnimId = isSecondary ? playerEntity.secondarySpotAnimation.spotAnimationId : playerEntity.primarySpotAnimation.spotAnimationId;
												@Pc(1569) SeqType seqType4;
												if (spotAnimId != -1 && currentSpotAnimId != -1) {
													@Pc(1516) SpotAnimationType spotAnimationType3;
													if (currentSpotAnimId == spotAnimId) {
														spotAnimationType3 = TypeLists.SPOT_ANIM.list(spotAnimId);
														if (spotAnimationType3.replay && spotAnimationType3.sequenceId != -1) {
															seqType4 = TypeLists.SEQ.list(spotAnimationType3.sequenceId);
															loopType = seqType4.loopType;
															if (loopType == 0 || loopType == 2) {
																applySpotAnim = false;
															} else if (loopType == 1) {
																applySpotAnim = true;
															}
														}
													} else {
														spotAnimationType3 = TypeLists.SPOT_ANIM.list(spotAnimId);
														spotAnimationType = TypeLists.SPOT_ANIM.list(currentSpotAnimId);
														if (spotAnimationType3.sequenceId != -1 && spotAnimationType.sequenceId != -1) {
															seqType2 = TypeLists.SEQ.list(spotAnimationType3.sequenceId);
															seqType = TypeLists.SEQ.list(spotAnimationType.sequenceId);
															if (seqType2.priority < seqType.priority) {
																applySpotAnim = false;
															}
														}
													}
												}
												if (applySpotAnim) {
													if (isSecondary) {
														playerEntity.secondarySpotAnimation.currentFrameId = 0;
														playerEntity.secondarySpotAnimation.height = spotAnimHeight;
														playerEntity.secondarySpotAnimation.adjustmentType = spotAnimAdjustType;
														playerEntity.secondarySpotAnimation.currentFrameDuration = 0;
														playerEntity.secondarySpotAnimation.nextFrameId = 1;
														playerEntity.secondarySpotAnimation.spotAnimationId = spotAnimId;
														playerEntity.secondarySpotAnimation.startCycle = spotAnimDelay + ClientState.clientCycle;
														playerEntity.secondarySpotAnimation.rotation = spotAnimRotation;
														if (playerEntity.secondarySpotAnimation.startCycle > ClientState.clientCycle) {
															playerEntity.secondarySpotAnimation.currentFrameId = -1;
														}
														if (playerEntity.secondarySpotAnimation.spotAnimationId == 65535) {
															playerEntity.secondarySpotAnimation.spotAnimationId = -1;
														}
														if (playerEntity.secondarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle == playerEntity.secondarySpotAnimation.startCycle) {
															int sequenceId = TypeLists.SPOT_ANIM.list(playerEntity.secondarySpotAnimation.spotAnimationId).sequenceId;
															if (sequenceId != -1) {
																seqType4 = TypeLists.SEQ.list(sequenceId);
																if (seqType4 != null && seqType4.frames != null && !playerEntity.isHidden) {
																	SoundManager.playSequenceSound(playerEntity, 0, seqType4);
																}
															}
														}
													} else {
														playerEntity.primarySpotAnimation.spotAnimationId = spotAnimId;
														playerEntity.primarySpotAnimation.currentFrameDuration = 0;
														playerEntity.primarySpotAnimation.height = spotAnimHeight;
														playerEntity.primarySpotAnimation.currentFrameId = 0;
														playerEntity.primarySpotAnimation.adjustmentType = spotAnimAdjustType;
														playerEntity.primarySpotAnimation.rotation = spotAnimRotation;
														playerEntity.primarySpotAnimation.nextFrameId = 1;
														playerEntity.primarySpotAnimation.startCycle = spotAnimDelay + ClientState.clientCycle;
														if (playerEntity.primarySpotAnimation.spotAnimationId == 65535) {
															playerEntity.primarySpotAnimation.spotAnimationId = -1;
														}
														if (ClientState.clientCycle < playerEntity.primarySpotAnimation.startCycle) {
															playerEntity.primarySpotAnimation.currentFrameId = -1;
														}
														if (playerEntity.primarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle == playerEntity.primarySpotAnimation.startCycle) {
															int sequenceId = TypeLists.SPOT_ANIM.list(playerEntity.primarySpotAnimation.spotAnimationId).sequenceId;
															if (sequenceId != -1) {
																seqType4 = TypeLists.SEQ.list(sequenceId);
																if (seqType4 != null && seqType4.frames != null && !playerEntity.isHidden) {
																	SoundManager.playSequenceSound(playerEntity, 0, seqType4);
																}
															}
														}
													}
												}
											}
										}
									} else {
										int plane = spotAnimTarget >> 28 & 0x3;
										int localX = (spotAnimTarget >> 14 & 0x3FFF) - MapRegion.sceneBaseX;
										int localY = (spotAnimTarget & 0x3FFF) - MapRegion.sceneBaseY;
										if (localX >= 0 && localY >= 0 && MapRegion.sceneSizeX > localX && MapRegion.sceneSizeY > localY) {
											int absoluteX = localX * 512 + 256;
											int absoluteY = localY * 512 + 256;
											int virtualLevel = plane;
											if (plane < 3 && SettingsBits.isBridge(localY, localX)) {
												virtualLevel = plane + 1;
											}
											@Pc(1843) SpotAnimation spotAnimation = new SpotAnimation(spotAnimId, spotAnimDelay, ClientState.clientCycle, plane, virtualLevel, absoluteX, Scene.tileHeightForAbsolutePosition(absoluteX, plane, absoluteY) - spotAnimHeight, absoluteY, localX, localX, localY, localY, spotAnimRotation);
											Renderers.activeToolkit.addLast(new PositionedGfx(spotAnimation));
										}
									}
									BufferedConnectionContext.currentPacket = null;
									return true;
								} else if (BufferedConnectionContext.currentPacket == ServerProt.CLIENT_SETVARCSTR_LARGE) {
									int varcId = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
									string5 = BufferedConnectionContext.receivedBuffer.readString();
									PlayerVarDomain.requestVarVerifyid();
									PulseEvent.priorityVarcstringUpdate(string5, varcId);
									BufferedConnectionContext.currentPacket = null;
									return true;
								} else if (BufferedConnectionContext.currentPacket == ServerProt.UPDATE_DOB) {
									LoginManager.playerDob = BufferedConnectionContext.receivedBuffer.readMedium();
									LoginManager.userDetailQuickchat = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
									BufferedConnectionContext.currentPacket = null;
									return true;
								} else {
									@Pc(2017) FriendsChatPlayer fcPlayer;
									@Pc(2013) String string7;
									@Pc(1903) String string8;
									if (ServerProt.UPDATE_FRIENDCHAT_CHANNEL_SINGLEUSER == BufferedConnectionContext.currentPacket) {
										string8 = BufferedConnectionContext.receivedBuffer.readString();
										boolean hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
										if (hasDisplayName) {
											string5 = BufferedConnectionContext.receivedBuffer.readString();
										} else {
											string5 = string8;
										}
										int worldId = BufferedConnectionContext.receivedBuffer.readUShort();
										@Pc(1930) byte rank = BufferedConnectionContext.receivedBuffer.readByte();
										boolean removeUser = false;
										if (rank == -128) {
											removeUser = true;
										}
										if (removeUser) {
											if (FriendsChat.clanChatUserCount == 0) {
												BufferedConnectionContext.currentPacket = null;
												return true;
											}
											for (clanUserIndex = 0; FriendsChat.clanChatUserCount > clanUserIndex && (!FriendsChat.clanChatUsers[clanUserIndex].username.equals(string5) || worldId != FriendsChat.clanChatUsers[clanUserIndex].worldId); clanUserIndex++) {
											}
											if (clanUserIndex < FriendsChat.clanChatUserCount) {
												while (clanUserIndex < FriendsChat.clanChatUserCount - 1) {
													FriendsChat.clanChatUsers[clanUserIndex] = FriendsChat.clanChatUsers[clanUserIndex + 1];
													clanUserIndex++;
												}
												FriendsChat.clanChatUserCount--;
												FriendsChat.clanChatUsers[FriendsChat.clanChatUserCount] = null;
											}
										} else {
											string7 = BufferedConnectionContext.receivedBuffer.readString();
											fcPlayer = new FriendsChatPlayer();
											fcPlayer.username = string5;
											fcPlayer.displayName = string8;
											fcPlayer.textName = Utils.formatDisplayName(fcPlayer.username);
											fcPlayer.worldName = string7;
											fcPlayer.worldId = worldId;
											fcPlayer.rank = rank;
											for (insertIndex = FriendsChat.clanChatUserCount - 1; insertIndex >= 0; insertIndex--) {
												int nameCompare = FriendsChat.clanChatUsers[insertIndex].textName.compareTo(fcPlayer.textName);
												if (nameCompare == 0) {
													FriendsChat.clanChatUsers[insertIndex].worldId = worldId;
													FriendsChat.clanChatUsers[insertIndex].rank = rank;
													FriendsChat.clanChatUsers[insertIndex].worldName = string7;
													if (string5.equals(PlayerUpdate.localPlayer.displayName)) {
														FriendsChat.myFriendsChatRank = rank;
													}
													FriendsChat.lastFriendsChatUpdateTick = ClientState.serverTick;
													BufferedConnectionContext.currentPacket = null;
													return true;
												}
												if (nameCompare < 0) {
													break;
												}
											}
											if (FriendsChat.clanChatUsers.length <= FriendsChat.clanChatUserCount) {
												BufferedConnectionContext.currentPacket = null;
												return true;
											}
											for (int userIndex = FriendsChat.clanChatUserCount - 1; userIndex > insertIndex; userIndex--) {
												FriendsChat.clanChatUsers[userIndex + 1] = FriendsChat.clanChatUsers[userIndex];
											}
											if (FriendsChat.clanChatUserCount == 0) {
												FriendsChat.clanChatUsers = new FriendsChatPlayer[100];
											}
											FriendsChat.clanChatUsers[insertIndex + 1] = fcPlayer;
											FriendsChat.clanChatUserCount++;
											if (string5.equals(PlayerUpdate.localPlayer.displayName)) {
												FriendsChat.myFriendsChatRank = rank;
											}
										}
										FriendsChat.lastFriendsChatUpdateTick = ClientState.serverTick;
										BufferedConnectionContext.currentPacket = null;
										return true;
									} else if (ServerProt.HINT_ARROW == BufferedConnectionContext.currentPacket) {
										int hintArrowHeader = BufferedConnectionContext.receivedBuffer.readUByte();
										int hintArrowSlot = hintArrowHeader >> 5;
										int hintTargetType = hintArrowHeader & 0x1F;
										if (hintTargetType == 0) {
											PlayerUpdate.HINT_ARROWS[hintArrowSlot] = null;
											BufferedConnectionContext.currentPacket = null;
											return true;
										}
										@Pc(2182) HintArrow hintArrow = new HintArrow();
										hintArrow.targetType = hintTargetType;
										hintArrow.arrowSprite = BufferedConnectionContext.receivedBuffer.readUByte();
										if (hintArrow.arrowSprite >= 0 && hintArrow.arrowSprite < StaticMedia.hintHeadiconSprites.length) {
											if (hintArrow.targetType == 1 || hintArrow.targetType == 10) {
												hintArrow.targetIndex = BufferedConnectionContext.receivedBuffer.readUShort();
												BufferedConnectionContext.receivedBuffer.currentPosition += 6;
											} else if (hintArrow.targetType >= 2 && hintArrow.targetType <= 6) {
												if (hintArrow.targetType == 2) {
													hintArrow.y = 256;
													hintArrow.x = 256;
												}
												if (hintArrow.targetType == 3) {
													hintArrow.x = 0;
													hintArrow.y = 256;
												}
												if (hintArrow.targetType == 4) {
													hintArrow.x = 512;
													hintArrow.y = 256;
												}
												if (hintArrow.targetType == 5) {
													hintArrow.x = 256;
													hintArrow.y = 0;
												}
												if (hintArrow.targetType == 6) {
													hintArrow.x = 256;
													hintArrow.y = 512;
												}
												hintArrow.targetType = 2;
												hintArrow.plane = BufferedConnectionContext.receivedBuffer.readUByte();
												hintArrow.x += BufferedConnectionContext.receivedBuffer.readUShort() - MapRegion.sceneBaseX << 9;
												hintArrow.y += BufferedConnectionContext.receivedBuffer.readUShort() - MapRegion.sceneBaseY << 9;
												hintArrow.height = BufferedConnectionContext.receivedBuffer.readUByte() << 2;
												hintArrow.distance = BufferedConnectionContext.receivedBuffer.readUShort();
											}
											hintArrow.modelId = BufferedConnectionContext.receivedBuffer.readUShort();
											if (hintArrow.modelId == 65535) {
												hintArrow.modelId = -1;
											}
											PlayerUpdate.HINT_ARROWS[hintArrowSlot] = hintArrow;
										}
										BufferedConnectionContext.currentPacket = null;
										return true;
									} else if (ServerProt.LOGOUT_LOBBY == BufferedConnectionContext.currentPacket) {
										ClientLogout.logout(LoginManager.currentlyInLobby);
										BufferedConnectionContext.currentPacket = null;
										return false;
									} else if (ServerProt.PLAYER_UPDATE == BufferedConnectionContext.currentPacket) {
										PlayerUpdate.decodePlayerUpdate(BufferedConnectionContext.receivedBuffer, BufferedConnectionContext.currentPacketSize);
										BufferedConnectionContext.currentPacket = null;
										return true;
									} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETCOLOR) {
										int colour = BufferedConnectionContext.receivedBuffer.readUShortAdd();
										int componentHash = BufferedConnectionContext.receivedBuffer.readIntLE();
										PlayerVarDomain.requestVarVerifyid();
										PulseEvent.priorityIfColourUpdate(colour, componentHash);
										BufferedConnectionContext.currentPacket = null;
										return true;
									} else if (ServerProt.CREATE_GROUND_ITEM == BufferedConnectionContext.currentPacket) {
										UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.CREATE_GROUND_ITEM);
										BufferedConnectionContext.currentPacket = null;
										return true;
									} else if (BufferedConnectionContext.currentPacket == ServerProt.OUTDATED_INTERFACE_PULSE_EVENT) {
										int componentHash = BufferedConnectionContext.receivedBuffer.readInt();
										int valueLow = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
										int valueHigh = BufferedConnectionContext.receivedBuffer.readUShortAdd();
										PlayerVarDomain.requestVarVerifyid();
										PulseEvent.priorityT13Update(componentHash, valueLow + (valueHigh << 16));
										BufferedConnectionContext.currentPacket = null;
										return true;
									} else if (BufferedConnectionContext.currentPacket == ServerProt.CLEAR_VARPS) {
										PlayerVarDomain.playerVarProvider.reset();
										PlayerVarDomain.pulseEventConfigsPtr += 32;
										BufferedConnectionContext.currentPacket = null;
										return true;
									} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETPLAYERMODEL) {
										int componentHash = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
										PlayerVarDomain.requestVarVerifyid();
										PulseEvent.priorityIfcontentUpdate(5, componentHash, PlayerUpdate.myPlayerIndex, 0);
										BufferedConnectionContext.currentPacket = null;
										return true;
									} else if (ServerProt.STOP_VORBIS_STREAM == BufferedConnectionContext.currentPacket) {
										int vorbisFileId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
										PlayerVarDomain.requestVarVerifyid();
										VorbisStreamStop.stopVorbisStream(vorbisFileId);
										BufferedConnectionContext.currentPacket = null;
										return true;
									} else {
										@Pc(2518) boolean local2518;
										if (ServerProt.MESSAGE_PLAYER_GROUP == BufferedConnectionContext.currentPacket) {
											boolean hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
											string5 = BufferedConnectionContext.receivedBuffer.readString();
											string = string5;
											if (hasDisplayName) {
												string = BufferedConnectionContext.receivedBuffer.readString();
											}
											int playerRights = BufferedConnectionContext.receivedBuffer.readUByte();
											boolean ignored = false;
											if (playerRights <= 1) {
												if (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly) {
													ignored = true;
												} else if (playerRights <= 1 && IgnoreList.isIgnored(string)) {
													ignored = true;
												}
											}
											if (!ignored && MapRegion.inTutorialIsland == 0) {
												string3 = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer));
												if (playerRights == 2) {
													ChatLineWriter.writeMessage("<img=1>" + string, string3, -1, "<img=1>" + string5, null, 24, 0, string5);
												} else if (playerRights == 1) {
													ChatLineWriter.writeMessage("<img=0>" + string, string3, -1, "<img=0>" + string5, null, 24, 0, string5);
												} else {
													ChatLineWriter.writeMessage(string, string3, -1, string5, null, 24, 0, string5);
												}
											}
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (ServerProt.MAP_PROJANIM_HALFSQ == BufferedConnectionContext.currentPacket) {
											UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.MAP_PROJANIM_HALFSQ);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (BufferedConnectionContext.currentPacket == ServerProt.CAM_MOVETO) {
											int targetLocalY = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
											int constantSpeed = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
											int targetLocalX = BufferedConnectionContext.receivedBuffer.readUByteInverse();
											int variableSpeed = BufferedConnectionContext.receivedBuffer.readUByteInverse();
											int moveZ = BufferedConnectionContext.receivedBuffer.readUShortAdd() << 2;
											PlayerVarDomain.requestVarVerifyid();
											Camera.moveCamera(constantSpeed, targetLocalX, true, moveZ, variableSpeed, targetLocalY);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (BufferedConnectionContext.currentPacket == ServerProt.RUN_CS2_SCRIPT) {
											string8 = BufferedConnectionContext.receivedBuffer.readString();
											@Pc(2694) Object[] objects = new Object[string8.length() + 1];
											for (int argIndex = string8.length() - 1; argIndex >= 0; argIndex--) {
												if (string8.charAt(argIndex) == 's') {
													objects[argIndex + 1] = BufferedConnectionContext.receivedBuffer.readString();
												} else {
													objects[argIndex + 1] = Integer.valueOf(BufferedConnectionContext.receivedBuffer.readInt());
												}
											}
											objects[0] = Integer.valueOf(BufferedConnectionContext.receivedBuffer.readInt());
											PlayerVarDomain.requestVarVerifyid();
											@Pc(2746) HookRequest hookRequest = new HookRequest();
											hookRequest.params = objects;
											CS2Interpreter.executeHookInner(hookRequest);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (ServerProt.CAM_SMOOTHRESET == BufferedConnectionContext.currentPacket) {
											PlayerVarDomain.requestVarVerifyid();
											Camera.startSmoothTransition();
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETNPCHEAD) {
											int componentHash = BufferedConnectionContext.receivedBuffer.readIntLE();
											int npcId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
											if (npcId == 65535) {
												npcId = -1;
											}
											PlayerVarDomain.requestVarVerifyid();
											PulseEvent.priorityIfcontentUpdate(2, componentHash, npcId, -1);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_CLOSESUB) {
											int componentHash = BufferedConnectionContext.receivedBuffer.readInt();
											PlayerVarDomain.requestVarVerifyid();
											@Pc(2815) SubInterface subInterface3 = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) componentHash);
											if (subInterface3 != null) {
												SubInterfaceCloser.closeSubInterface(true, false, subInterface3);
											}
											if (InterfaceManager.activeDialogueContinue != null) {
												Component.redrawComponent(InterfaceManager.activeDialogueContinue);
												InterfaceManager.activeDialogueContinue = null;
											}
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (ServerProt.IF_SETGRAPHIC == BufferedConnectionContext.currentPacket) {
											int spriteId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
											int componentHash = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
											PlayerVarDomain.requestVarVerifyid();
											PulseEvent.priorityIfGraphicUpdate(componentHash, spriteId);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (ServerProt.REMOVE_GROUND_ITEM == BufferedConnectionContext.currentPacket) {
											UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.REMOVE_GROUND_ITEM);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (BufferedConnectionContext.currentPacket == ServerProt.IDENTIFY_HOST_NAME) {
											int ipAddress = BufferedConnectionContext.receivedBuffer.readInt();
											LoginManager.lastLoginHostLookup = GameShell.signLink.lookupHostname(ipAddress);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (BufferedConnectionContext.currentPacket == ServerProt.MESSAGE_PUBLIC) {
											int playerIndex = BufferedConnectionContext.receivedBuffer.readUShort();
											@Pc(2895) PlayerEntity playerEntity2;
											if (playerIndex == PlayerUpdate.myPlayerIndex) {
												playerEntity2 = PlayerUpdate.localPlayer;
											} else {
												playerEntity2 = PlayerUpdate.players[playerIndex];
											}
											if (playerEntity2 == null) {
												BufferedConnectionContext.currentPacket = null;
												return true;
											}
											int chatEffects = BufferedConnectionContext.receivedBuffer.readUShort();
											int playerRights = BufferedConnectionContext.receivedBuffer.readUByte();
											boolean isQuickChat = (chatEffects & 0x8000) != 0;
											if (playerEntity2.displayName != null && playerEntity2.playerModel != null) {
												boolean ignored = false;
												if (playerRights <= 1) {
													if (!isQuickChat && (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly)) {
														ignored = true;
													} else if (IgnoreList.isIgnored(playerEntity2.displayName)) {
														ignored = true;
													}
												}
												if (!ignored && MapRegion.inTutorialIsland == 0) {
													int quickChatId = -1;
													if (isQuickChat) {
														chatEffects &= 0x7FFF;
														@Pc(2978) QuickChatPhrase quickChatPhrase = QuickchatMessageTypeList.decodeQuickchatMessage(BufferedConnectionContext.receivedBuffer);
														quickChatId = quickChatPhrase.id;
														string7 = quickChatPhrase.type.fillDynamicValues(BufferedConnectionContext.receivedBuffer);
													} else {
														string7 = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer));
													}
													playerEntity2.hintDetails.text = string7.trim();
													playerEntity2.hintDetails.colorType = chatEffects >> 8;
													playerEntity2.hintDetails.drawType = chatEffects & 0xFF;
													playerEntity2.hintDetails.ticksLeft = 150;
													int chatMessageType;
													if (playerRights == 1 || playerRights == 2) {
														chatMessageType = isQuickChat ? 17 : 1;
													} else {
														chatMessageType = isQuickChat ? 17 : 2;
													}
													if (playerRights == 2) {
														ChatLineWriter.writeMessage("<img=1>" + playerEntity2.getDisplayName(), string7, quickChatId, "<img=1>" + playerEntity2.getUsernameWithTitle(), null, chatMessageType, 0, playerEntity2.username);
													} else if (playerRights == 1) {
														ChatLineWriter.writeMessage("<img=0>" + playerEntity2.getDisplayName(), string7, quickChatId, "<img=0>" + playerEntity2.getUsernameWithTitle(), null, chatMessageType, 0, playerEntity2.username);
													} else {
														ChatLineWriter.writeMessage(playerEntity2.getDisplayName(), string7, quickChatId, playerEntity2.getUsernameWithTitle(), null, chatMessageType, 0, playerEntity2.username);
													}
												}
											}
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (ServerProt.MESSAGE_PRIVATE == BufferedConnectionContext.currentPacket) {
											boolean hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
											string5 = BufferedConnectionContext.receivedBuffer.readString();
											string = string5;
											if (hasDisplayName) {
												string = BufferedConnectionContext.receivedBuffer.readString();
											}
											long messageHashHigh = BufferedConnectionContext.receivedBuffer.readUShort();
											long messageHashLow = BufferedConnectionContext.receivedBuffer.readUMedium();
											int playerRights = BufferedConnectionContext.receivedBuffer.readUByte();
											@Pc(3166) long messageHash = (messageHashHigh << 32) + messageHashLow;
											boolean ignored = false;
											int hashIndex = 0;
											while (true) {
												if (hashIndex >= 100) {
													if (playerRights <= 1) {
														if (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly) {
															ignored = true;
														} else if (IgnoreList.isIgnored(string)) {
															ignored = true;
														}
													}
													break;
												}
												if (Chat.CHAT_MESSAGE_UNIQUE_HASHES[hashIndex] == messageHash) {
													ignored = true;
													break;
												}
												hashIndex++;
											}
											if (!ignored && MapRegion.inTutorialIsland == 0) {
												Chat.CHAT_MESSAGE_UNIQUE_HASHES[Camera.chatMessageHashIndex] = messageHash;
												Camera.chatMessageHashIndex = (Camera.chatMessageHashIndex + 1) % 100;
												@Pc(3231) String string9 = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer));
												if (playerRights == 2) {
													ChatLineWriter.writeMessage("<img=1>" + string, string9, -1, "<img=1>" + string5, null, 7, 0, string5);
												} else if (playerRights == 1) {
													ChatLineWriter.writeMessage("<img=0>" + string, string9, -1, "<img=0>" + string5, null, 7, 0, string5);
												} else {
													ChatLineWriter.writeMessage(string, string9, -1, string5, null, 3, 0, string5);
												}
											}
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (ServerProt.TRIGGER_ONDIALOGABORT == BufferedConnectionContext.currentPacket) {
											if (ClientState.BASE_WINDOW_ID != -1) {
												Static683.runSubInterfaceHooks(ClientState.BASE_WINDOW_ID, 0);
											}
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (ServerProt.CAM_LOOKAT == BufferedConnectionContext.currentPacket) {
											int targetLocalX = BufferedConnectionContext.receivedBuffer.readUByte();
											int targetLocalY = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
											int constantSpeed = BufferedConnectionContext.receivedBuffer.readUByte();
											int heightOffset = BufferedConnectionContext.receivedBuffer.readUShortLittle() << 2;
											int variableSpeed = BufferedConnectionContext.receivedBuffer.readUByteInverse();
											PlayerVarDomain.requestVarVerifyid();
											Camera.lookAt(targetLocalY, targetLocalX, heightOffset, variableSpeed, constantSpeed);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (BufferedConnectionContext.currentPacket == ServerProt.PING) {
											BufferedConnectionContext.currentPacket = null;
											return false;
										} else if (ServerProt.REFLECTION_CHECK == BufferedConnectionContext.currentPacket) {
											ReflectionCheckManager.requestReflectionCheck(BufferedConnectionContext.currentPacketSize, BufferedConnectionContext.receivedBuffer, GameShell.signLink);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else if (ServerProt.WORLD_LIST == BufferedConnectionContext.currentPacket) {
											boolean refresh = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
											@Pc(3394) byte[] data = new byte[BufferedConnectionContext.currentPacketSize - 1];
											BufferedConnectionContext.receivedBuffer.readBytes(0, BufferedConnectionContext.currentPacketSize - 1, data);
											WorldConnection.decodeWorldList(refresh, data);
											BufferedConnectionContext.currentPacket = null;
											return true;
										} else {
											@Pc(3421) byte local3421;
											if (BufferedConnectionContext.currentPacket == ServerProt.SET_SONG_VOLUME) {
												int volume = BufferedConnectionContext.receivedBuffer.readUByte();
												byte channel = BufferedConnectionContext.receivedBuffer.readByteAdd();
												PlayerVarDomain.requestVarVerifyid();
												SongPlayer.setSongVolume(channel, volume);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.UPDATE_ZONE_PARTIAL_ENCLOSED == BufferedConnectionContext.currentPacket) {
												UpdateZonePacket.updateZoneX = BufferedConnectionContext.receivedBuffer.readByteInverse() << 3;
												UpdateZonePacketDecoder.updateZonePlane = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
												UpdateZonePacket.updateZoneY = BufferedConnectionContext.receivedBuffer.readByteSubtract() << 3;
												while (BufferedConnectionContext.currentPacketSize > BufferedConnectionContext.receivedBuffer.currentPosition) {
													@Pc(3460) UpdateZonePacket subPacket = UpdateZonePacket.updateZonePacketValues()[BufferedConnectionContext.receivedBuffer.readUByte()];
													UpdateZonePacketDecoder.decodeUpdateZonePacket(subPacket);
												}
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.IF_SETPOSITION == BufferedConnectionContext.currentPacket) {
												int positionY = BufferedConnectionContext.receivedBuffer.readShort();
												int componentHash = BufferedConnectionContext.receivedBuffer.readInt();
												int positionX = BufferedConnectionContext.receivedBuffer.readShortAdd();
												PlayerVarDomain.requestVarVerifyid();
												PriorityIfPositionUpdate.priorityIfPositionUpdate(positionX, positionY, componentHash);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.CLIENT_SETVARCSTR_SMALL == BufferedConnectionContext.currentPacket) {
												string8 = BufferedConnectionContext.receivedBuffer.readString();
												int varcId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
												PlayerVarDomain.requestVarVerifyid();
												PulseEvent.priorityVarcstringUpdate(string8, varcId);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (BufferedConnectionContext.currentPacket == ShaderVariableType.IF_SETVIDEO) {
												int videoId = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
												int componentHash = BufferedConnectionContext.receivedBuffer.readInt();
												PlayerVarDomain.requestVarVerifyid();
												PulseEvent.priorityIfVideoUpdate(videoId, componentHash);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.MESSAGE_QUICKCHAT_FRIENDS_CHAT == BufferedConnectionContext.currentPacket) {
												boolean hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
												string5 = BufferedConnectionContext.receivedBuffer.readString();
												string = string5;
												if (hasDisplayName) {
													string = BufferedConnectionContext.receivedBuffer.readString();
												}
												long encodedChannelName = BufferedConnectionContext.receivedBuffer.readLong();
												long messageHashHigh = BufferedConnectionContext.receivedBuffer.readUShort();
												hashLow = BufferedConnectionContext.receivedBuffer.readUMedium();
												int playerRights = BufferedConnectionContext.receivedBuffer.readUByte();
												int phraseId = BufferedConnectionContext.receivedBuffer.readUShort();
												@Pc(3599) long messageHash = (messageHashHigh << 32) + hashLow;
												@Pc(3601) boolean ignore = false;
												loopType = 0;
												while (true) {
													if (loopType >= 100) {
														if (playerRights <= 1 && IgnoreList.isIgnored(string)) {
															ignore = true;
														}
														break;
													}
													if (messageHash == Chat.CHAT_MESSAGE_UNIQUE_HASHES[loopType]) {
														ignore = true;
														break;
													}
													loopType++;
												}
												if (!ignore && MapRegion.inTutorialIsland == 0) {
													Chat.CHAT_MESSAGE_UNIQUE_HASHES[Camera.chatMessageHashIndex] = messageHash;
													Camera.chatMessageHashIndex = (Camera.chatMessageHashIndex + 1) % 100;
													@Pc(3658) String string10 = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId).fillDynamicValues(BufferedConnectionContext.receivedBuffer);
													if (playerRights == 2) {
														ChatLineWriter.writeMessage("<img=1>" + string, string10, phraseId, "<img=1>" + string5, Utils.decodeUidName(encodedChannelName), 20, 0, string5);
													} else if (playerRights == 1) {
														ChatLineWriter.writeMessage("<img=0>" + string, string10, phraseId, "<img=0>" + string5, Utils.decodeUidName(encodedChannelName), 20, 0, string5);
													} else {
														ChatLineWriter.writeMessage(string, string10, phraseId, string5, Utils.decodeUidName(encodedChannelName), 20, 0, string5);
													}
												}
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.SET_CAMERA_TARGET == BufferedConnectionContext.currentPacket) {
												int targetCoord = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
												PlayerVarDomain.requestVarVerifyid();
												if (targetCoord == -1) {
													Camera.roofRemovalTargetX = -1;
													Camera.roofRemovalTargetY = -1;
												} else {
													int roofLocalX = targetCoord >> 14 & 0x3FFF;
													roofLocalX -= MapRegion.sceneBaseX;
													int roofLocalY = targetCoord & 0x3FFF;
													roofLocalY -= MapRegion.sceneBaseY;
													if (roofLocalX < 0) {
														roofLocalX = 0;
													} else if (MapRegion.sceneSizeX <= roofLocalX) {
														roofLocalX = MapRegion.sceneSizeX;
													}
													Camera.roofRemovalTargetX = (roofLocalX << 9) + 256;
													if (roofLocalY < 0) {
														roofLocalY = 0;
													} else if (roofLocalY >= MapRegion.sceneSizeY) {
														roofLocalY = MapRegion.sceneSizeY;
													}
													Camera.roofRemovalTargetY = (roofLocalY << 9) + 256;
												}
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (BufferedConnectionContext.currentPacket == ServerProt.VARBIT_SMALL) {
												int varbitValue = BufferedConnectionContext.receivedBuffer.readUByteAdd();
												int varbitId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
												PlayerVarDomain.playerVarProvider.setVarBitValueFromServer(varbitId, varbitValue);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.SPOT_ANIM == BufferedConnectionContext.currentPacket) {
												UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.SPOT_ANIM);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (BufferedConnectionContext.currentPacket == ServerProt.OBJ_ANIM_SPECIFIC) {
												int animationId = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
												if (animationId == 65535) {
													animationId = -1;
												}
												int shapeAndRotation = BufferedConnectionContext.receivedBuffer.readUByteAdd();
												int objectShape = shapeAndRotation >> 2;
												int objectRotation = shapeAndRotation & 0x3;
												int objectLayer = LocLayer.OBJECT_TYPE_SLOTS[objectShape];
												int objectCoord = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
												int plane = objectCoord >> 28 & 0x3;
												int localX = objectCoord >> 14 & 0x3FFF;
												int localY = objectCoord & 0x3FFF;
												localX -= MapRegion.sceneBaseX;
												localY -= MapRegion.sceneBaseY;
												Scene.animateObject(localY, animationId, objectShape, localX, objectRotation, plane, objectLayer);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (BufferedConnectionContext.currentPacket == ServerProt.MESSAGE_QUICKCHAT_PRIVATE_ECHO) {
												string8 = BufferedConnectionContext.receivedBuffer.readString();
												int phraseId = BufferedConnectionContext.receivedBuffer.readUShort();
												string = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId).fillDynamicValues(BufferedConnectionContext.receivedBuffer);
												ChatLineWriter.writeMessage(string8, string, phraseId, string8, null, 19, 0, string8);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (BufferedConnectionContext.currentPacket == ServerProt.GROUND_ITEM_REVEAL) {
												UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.GROUND_ITEM_REVEAL);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.RESET_ALL_ANIMATIONS == BufferedConnectionContext.currentPacket) {
												for (int playerIndex = 0; playerIndex < PlayerUpdate.players.length; playerIndex++) {
													if (PlayerUpdate.players[playerIndex] != null) {
														PlayerUpdate.players[playerIndex].currentAnimations = null;
														PlayerUpdate.players[playerIndex].currentSequence.sequenceId = -1;
													}
												}
												for (int npcIndex = 0; npcIndex < NPCList.npcCount; npcIndex++) {
													NPCList.NPC_ARRAY[npcIndex].npc.currentAnimations = null;
													NPCList.NPC_ARRAY[npcIndex].npc.currentSequence.sequenceId = -1;
												}
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.IF_SETANGLE == BufferedConnectionContext.currentPacket) {
												int componentHash = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
												int rotationX = BufferedConnectionContext.receivedBuffer.readUShort();
												int rotationY = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
												int modelZoom = BufferedConnectionContext.receivedBuffer.readUShortAdd();
												PlayerVarDomain.requestVarVerifyid();
												PulseEvent.priorityIfangleUpdate(modelZoom, rotationX, rotationY, componentHash);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.CHAT_FILTER_SETTINGS_PRIVATECHAT == BufferedConnectionContext.currentPacket) {
												ChatBox.privateFilter = ChatFilter.chatFilterForId(BufferedConnectionContext.receivedBuffer.readUByte());
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.TILE_MESSAGE == BufferedConnectionContext.currentPacket) {
												UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.TILE_MESSAGE);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.UPDATE_IGNORE_LIST == BufferedConnectionContext.currentPacket) {
												IgnoreList.ignoreCount = BufferedConnectionContext.receivedBuffer.readUByte();
												for (int ignoreIndex = 0; ignoreIndex < IgnoreList.ignoreCount; ignoreIndex++) {
													IgnoreList.ignoreNames[ignoreIndex] = BufferedConnectionContext.receivedBuffer.readString();
													IgnoreList.ignoreNamesUnfiltered[ignoreIndex] = BufferedConnectionContext.receivedBuffer.readString();
													if (IgnoreList.ignoreNamesUnfiltered[ignoreIndex].equals("")) {
														IgnoreList.ignoreNamesUnfiltered[ignoreIndex] = IgnoreList.ignoreNames[ignoreIndex];
													}
													IgnoreList.ignorePreviousNames[ignoreIndex] = BufferedConnectionContext.receivedBuffer.readString();
													IgnoreList.ignorePreviousNamesUnfiltered[ignoreIndex] = BufferedConnectionContext.receivedBuffer.readString();
													if (IgnoreList.ignorePreviousNamesUnfiltered[ignoreIndex].equals("")) {
														IgnoreList.ignorePreviousNamesUnfiltered[ignoreIndex] = IgnoreList.ignorePreviousNames[ignoreIndex];
													}
													IgnoreList.ignoreIsTemporary[ignoreIndex] = false;
												}
												FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick;
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (BufferedConnectionContext.currentPacket == ServerProt.PLAYER_OPTION) {
												int cursor = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
												if (cursor == 65535) {
													cursor = -1;
												}
												string5 = BufferedConnectionContext.receivedBuffer.readString();
												int optionSlot = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
												int optionPriority = BufferedConnectionContext.receivedBuffer.readUByteAdd();
												if (optionSlot >= 1 && optionSlot <= 8) {
													if (string5.equalsIgnoreCase("null")) {
														string5 = null;
													}
													MiniMenu.PLAYER_OPTIONS[optionSlot - 1] = string5;
													MiniMenu.PLAYER_OPTION_CURSORS[optionSlot - 1] = cursor;
													MiniMenu.PLAYER_OPTION_REDUCED_PRIORITY[optionSlot - 1] = optionPriority == 0;
												}
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (ServerProt.GAME_MESSAGE == BufferedConnectionContext.currentPacket) {
												int messageType = BufferedConnectionContext.receivedBuffer.readUSmart();
												int effectFlags = BufferedConnectionContext.receivedBuffer.readInt();
												int nameFlags = BufferedConnectionContext.receivedBuffer.readUByte();
												string4 = "";
												string2 = string4;
												if ((nameFlags & 0x1) != 0) {
													string4 = BufferedConnectionContext.receivedBuffer.readString();
													if ((nameFlags & 0x2) == 0) {
														string2 = string4;
													} else {
														string2 = BufferedConnectionContext.receivedBuffer.readString();
													}
												}
												string3 = BufferedConnectionContext.receivedBuffer.readString();
												if (messageType == 99) {
													Console.printToConsole(string3);
												} else if (ClientFeatures.CONSOLE && messageType == 98) {
													// Not part of the original client: the server sets the console input, to complete it.
													Console.commandString = string3;
													Console.commandLength = string3.length();
												} else if (string2.equals("") || !IgnoreList.isIgnored(string2)) {
													ChatLineWriter.writeMessage(string2, messageType, effectFlags, string3, string4, string4);
												} else {
													BufferedConnectionContext.currentPacket = null;
													return true;
												}
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else if (BufferedConnectionContext.currentPacket == ServerProt.UPDATE_GE_SLOT) {
												int geSlot = BufferedConnectionContext.receivedBuffer.readUByte();
												if (BufferedConnectionContext.receivedBuffer.readUByte() == 0) {
													StockMarketOffer.GRAND_EXCHANGE_SLOTS[geSlot] = new StockMarketOffer();
												} else {
													BufferedConnectionContext.receivedBuffer.currentPosition--;
													StockMarketOffer.GRAND_EXCHANGE_SLOTS[geSlot] = new StockMarketOffer(BufferedConnectionContext.receivedBuffer);
												}
												BufferedConnectionContext.currentPacket = null;
												client.lastGrandExchangeUpdateTick = ClientState.serverTick;
												return true;
											} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETANIM) {
												int animationId = BufferedConnectionContext.receivedBuffer.readShort();
												int componentHash = BufferedConnectionContext.receivedBuffer.readUIntMiddle();
												PlayerVarDomain.requestVarVerifyid();
												PulseEvent.priorityIfAnimUpdate(animationId, componentHash);
												BufferedConnectionContext.currentPacket = null;
												return true;
											} else {
												@Pc(4419) IFEvents serverActiveProperties;
												@Pc(4403) IFEvents serverActiveProperties2;
												if (BufferedConnectionContext.currentPacket == ServerProt.IF_RESETTARGETPARAM) {
													int componentHash = BufferedConnectionContext.receivedBuffer.readUIntMiddle();
													int targetParam = BufferedConnectionContext.receivedBuffer.readUShort();
													int toSlot = BufferedConnectionContext.receivedBuffer.readUShort();
													if (toSlot == 65535) {
														toSlot = -1;
													}
													int fromSlot = BufferedConnectionContext.receivedBuffer.readUShortAdd();
													PlayerVarDomain.requestVarVerifyid();
													if (fromSlot == 65535) {
														fromSlot = -1;
													}
													for (int slot = fromSlot; slot <= toSlot; slot++) {
														long slotKey = ((long) componentHash << 32) + ((long) slot);
														serverActiveProperties2 = (IFEvents) IFEvents.ICOMPONENT_SETTINGS_SLOTS.get(slotKey);
														if (serverActiveProperties2 != null) {
															serverActiveProperties = new IFEvents(serverActiveProperties2.settingsHash, targetParam);
															serverActiveProperties2.unlink();
														} else if (slot == -1) {
															serverActiveProperties = new IFEvents(Interface.getComponent(componentHash).targetParams.settingsHash, targetParam);
														} else {
															serverActiveProperties = new IFEvents(0, targetParam);
														}
														IFEvents.ICOMPONENT_SETTINGS_SLOTS.put(slotKey, serverActiveProperties);
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												}
												@Pc(4486) boolean local4486;
												if (BufferedConnectionContext.currentPacket == ServerProt.FRIEND_STATUS) {
													@Pc(4520) boolean local4520;
													while (BufferedConnectionContext.receivedBuffer.currentPosition < BufferedConnectionContext.currentPacketSize) {
														boolean nameChanged = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
														string5 = BufferedConnectionContext.receivedBuffer.readString();
														string = BufferedConnectionContext.receivedBuffer.readString();
														int friendWorldId = BufferedConnectionContext.receivedBuffer.readUShort();
														int friendRank = BufferedConnectionContext.receivedBuffer.readUByte();
														string3 = "";
														boolean sameGame = false;
														if (friendWorldId > 0) {
															string3 = BufferedConnectionContext.receivedBuffer.readString();
															sameGame = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
														}
														for (int friendIndex = 0; friendIndex < FriendList.FRIEND_COUNT; friendIndex++) {
															if (nameChanged) {
																if (string.equals(FriendList.friendNames[friendIndex])) {
																	FriendList.friendNames[friendIndex] = string5;
																	FriendList.friendPreviousNames[friendIndex] = string;
																	string5 = null;
																	break;
																}
															} else if (string5.equals(FriendList.friendNames[friendIndex])) {
																if (friendWorldId != FriendList.friendWorldIds[friendIndex]) {
																	boolean queueNotification = true;
																	for (@Pc(4525) LoginStateUpdate friendNotification = (LoginStateUpdate) client.PLAYER_LOGINSTATE_UPDATES.getFirst(); friendNotification != null; friendNotification = (LoginStateUpdate) client.PLAYER_LOGINSTATE_UPDATES.getNext()) {
																		if (friendNotification.name.equals(string5)) {
																			if (friendWorldId != 0 && friendNotification.actionType == 0) {
																				queueNotification = false;
																				friendNotification.unlinkNode();
																			} else if (friendWorldId == 0 && friendNotification.actionType != 0) {
																				queueNotification = false;
																				friendNotification.unlinkNode();
																			}
																		}
																	}
																	if (queueNotification) {
																		client.PLAYER_LOGINSTATE_UPDATES.offer(new LoginStateUpdate(string5, friendWorldId));
																	}
																	FriendList.friendWorldIds[friendIndex] = friendWorldId;
																}
																FriendList.friendPreviousNames[friendIndex] = string;
																FriendList.friendWorldNames[friendIndex] = string3;
																FriendList.friendRanks[friendIndex] = friendRank;
																string5 = null;
																FriendList.friendSameGame[friendIndex] = sameGame;
																break;
															}
														}
														if (string5 != null && FriendList.FRIEND_COUNT < 200) {
															FriendList.friendNames[FriendList.FRIEND_COUNT] = string5;
															FriendList.friendPreviousNames[FriendList.FRIEND_COUNT] = string;
															FriendList.friendWorldIds[FriendList.FRIEND_COUNT] = friendWorldId;
															FriendList.friendWorldNames[FriendList.FRIEND_COUNT] = string3;
															FriendList.friendRanks[FriendList.FRIEND_COUNT] = friendRank;
															FriendList.friendSameGame[FriendList.FRIEND_COUNT] = sameGame;
															FriendList.FRIEND_COUNT++;
														}
													}
													FriendList.friendListStatus = 2;
													FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick;
													int sortEnd = FriendList.FRIEND_COUNT;
													while (sortEnd > 0) {
														sortEnd--;
														boolean sorted = true;
														for (int friendSortIndex = 0; friendSortIndex < sortEnd; friendSortIndex++) {
															if (TcpPacket.CURRENT_REQUEST.worldId != FriendList.friendWorldIds[friendSortIndex] && TcpPacket.CURRENT_REQUEST.worldId == FriendList.friendWorldIds[friendSortIndex + 1] || FriendList.friendWorldIds[friendSortIndex] == 0 && FriendList.friendWorldIds[friendSortIndex + 1] != 0) {
																int swapWorldId = FriendList.friendWorldIds[friendSortIndex];
																FriendList.friendWorldIds[friendSortIndex] = FriendList.friendWorldIds[friendSortIndex + 1];
																FriendList.friendWorldIds[friendSortIndex + 1] = swapWorldId;
																string2 = FriendList.friendWorldNames[friendSortIndex];
																FriendList.friendWorldNames[friendSortIndex] = FriendList.friendWorldNames[friendSortIndex + 1];
																FriendList.friendWorldNames[friendSortIndex + 1] = string2;
																string3 = FriendList.friendNames[friendSortIndex];
																FriendList.friendNames[friendSortIndex] = FriendList.friendNames[friendSortIndex + 1];
																FriendList.friendNames[friendSortIndex + 1] = string3;
																string7 = FriendList.friendPreviousNames[friendSortIndex];
																FriendList.friendPreviousNames[friendSortIndex] = FriendList.friendPreviousNames[friendSortIndex + 1];
																FriendList.friendPreviousNames[friendSortIndex + 1] = string7;
																int swapRank = FriendList.friendRanks[friendSortIndex];
																FriendList.friendRanks[friendSortIndex] = FriendList.friendRanks[friendSortIndex + 1];
																FriendList.friendRanks[friendSortIndex + 1] = swapRank;
																boolean swapSameGame = FriendList.friendSameGame[friendSortIndex];
																FriendList.friendSameGame[friendSortIndex] = FriendList.friendSameGame[friendSortIndex + 1];
																sorted = false;
																FriendList.friendSameGame[friendSortIndex + 1] = swapSameGame;
															}
														}
														if (sorted) {
															break;
														}
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.OPEN_URL == BufferedConnectionContext.currentPacket) {
													if (WindowMode.fullScreenFrame != null) {
														WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid.fixedScreen.getValue()), -1, false, -1);
													}
													@Pc(4856) byte[] urlBytes = new byte[BufferedConnectionContext.currentPacketSize];
													BufferedConnectionContext.receivedBuffer.readEncryptedBytes(BufferedConnectionContext.currentPacketSize, urlBytes);
													string5 = Utils.readString(urlBytes, BufferedConnectionContext.currentPacketSize, 0);
													ClientURLTools.openUrl(string5, GameShell.signLink, GamePreferences.uid.toolkit.getValue() == 1, true);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.SOUND_SYNTH == BufferedConnectionContext.currentPacket) {
													int soundId = BufferedConnectionContext.receivedBuffer.readUShort();
													if (soundId == 65535) {
														soundId = -1;
													}
													int loopCount = BufferedConnectionContext.receivedBuffer.readUByte();
													int delay = BufferedConnectionContext.receivedBuffer.readUShort();
													int volume = BufferedConnectionContext.receivedBuffer.readUByte();
													int rate = BufferedConnectionContext.receivedBuffer.readUShort();
													AreaSoundPlayer.playSoundSynth(volume, soundId, loopCount, delay, rate);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.VARP_SMALL) {
													int varpId = BufferedConnectionContext.receivedBuffer.readUShort();
													byte varpValue = BufferedConnectionContext.receivedBuffer.readByte();
													PlayerVarDomain.playerVarProvider.updateVarp(varpValue, varpId);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETTEXTFONT) {
													int fontId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
													if (fontId == 65535) {
														fontId = -1;
													}
													int componentHash = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
													PlayerVarDomain.requestVarVerifyid();
													PulseEvent.priorityIfFontUpdate(fontId, componentHash);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.OBJECT_PREFETCH) {
													UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.OBJECT_PREFETCH);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.VORBIS_SOUND) {
													int soundId = BufferedConnectionContext.receivedBuffer.readUShort();
													if (soundId == 65535) {
														soundId = -1;
													}
													int loopCount = BufferedConnectionContext.receivedBuffer.readUByte();
													int delay = BufferedConnectionContext.receivedBuffer.readUShort();
													int volume = BufferedConnectionContext.receivedBuffer.readUByte();
													int rate = BufferedConnectionContext.receivedBuffer.readUShort();
													SoundManager.playSoundVorbis(loopCount, rate, false, volume, delay, soundId);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.REGION) {
													StaticRegionInit.initializeRegion();
													BufferedConnectionContext.currentPacket = null;
													return false;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.VORBIS_PRELOAD_SOUND) {
													int soundId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
													int volume = BufferedConnectionContext.receivedBuffer.readUByte();
													PlayerVarDomain.requestVarVerifyid();
													VorbisStreamPreload.preloadVorbisSound(volume, true, soundId);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.MESSAGE_QUICKCHAT_PRIVATE == BufferedConnectionContext.currentPacket) {
													boolean hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
													string5 = BufferedConnectionContext.receivedBuffer.readString();
													string = string5;
													if (hasDisplayName) {
														string = BufferedConnectionContext.receivedBuffer.readString();
													}
													long messageHashHigh = BufferedConnectionContext.receivedBuffer.readUShort();
													long messageHashLow = BufferedConnectionContext.receivedBuffer.readUMedium();
													int playerRights = BufferedConnectionContext.receivedBuffer.readUByte();
													int phraseId = BufferedConnectionContext.receivedBuffer.readUShort();
													@Pc(5108) long messageHash = messageHashLow + (messageHashHigh << 32);
													boolean ignored = false;
													int hashIndex = 0;
													while (true) {
														if (hashIndex >= 100) {
															if (playerRights <= 1 && IgnoreList.isIgnored(string)) {
																ignored = true;
															}
															break;
														}
														if (messageHash == Chat.CHAT_MESSAGE_UNIQUE_HASHES[hashIndex]) {
															ignored = true;
															break;
														}
														hashIndex++;
													}
													if (!ignored && MapRegion.inTutorialIsland == 0) {
														Chat.CHAT_MESSAGE_UNIQUE_HASHES[Camera.chatMessageHashIndex] = messageHash;
														Camera.chatMessageHashIndex = (Camera.chatMessageHashIndex + 1) % 100;
														@Pc(5161) String string11 = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId).fillDynamicValues(BufferedConnectionContext.receivedBuffer);
														if (playerRights == 2) {
															ChatLineWriter.writeMessage("<img=1>" + string, string11, phraseId, "<img=1>" + string5, null, 18, 0, string5);
														} else if (playerRights == 1) {
															ChatLineWriter.writeMessage("<img=0>" + string, string11, phraseId, "<img=0>" + string5, null, 18, 0, string5);
														} else {
															ChatLineWriter.writeMessage(string, string11, phraseId, string5, null, 18, 0, string5);
														}
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_OPENSUB) {
													int interfaceId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
													int componentHash = BufferedConnectionContext.receivedBuffer.readIntLE();
													int openType = BufferedConnectionContext.receivedBuffer.readUByteAdd();
													PlayerVarDomain.requestVarVerifyid();
													subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) componentHash);
													if (subInterface != null) {
														SubInterfaceCloser.closeSubInterface(subInterface.interfaceId != interfaceId, false, subInterface);
													}
													SubInterfaceOpener.openSubInterface(interfaceId, componentHash, SubInterfaceType.fromId(openType), false);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.FRIENDLIST_LOADED) {
													BufferedConnectionContext.currentPacket = null;
													FriendList.friendListStatus = 1;
													FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.CAM_RESET) {
													PlayerVarDomain.requestVarVerifyid();
													Camera.reset();
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.DEBUG_SERVER_TRIGGERS == BufferedConnectionContext.currentPacket) {
													int interfaceId = BufferedConnectionContext.receivedBuffer.readUShort();
													int startComponent = BufferedConnectionContext.receivedBuffer.readUShort();
													int endComponent = BufferedConnectionContext.receivedBuffer.readUShort();
													PlayerVarDomain.requestVarVerifyid();
													if (Interface.interfaces[interfaceId] != null) {
														for (int componentId = startComponent; componentId < endComponent; componentId++) {
															int serverTriggers = BufferedConnectionContext.receivedBuffer.readUMedium();
															if (componentId < Interface.interfaces[interfaceId].length && Interface.interfaces[interfaceId][componentId] != null) {
																Interface.interfaces[interfaceId][componentId].serverTriggers = serverTriggers;
															}
														}
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.OBJ_ANIM) {
													UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.OBJ_ANIM);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (UpdateZoneFullFollowsProt.UPDATE_ZONE_FULL_FOLLOWS == BufferedConnectionContext.currentPacket) {
													UpdateZonePacketDecoder.updateZonePlane = BufferedConnectionContext.receivedBuffer.readUByteAdd();
													UpdateZonePacket.updateZoneY = BufferedConnectionContext.receivedBuffer.readByteInverse() << 3;
													UpdateZonePacket.updateZoneX = BufferedConnectionContext.receivedBuffer.readByteInverse() << 3;
													for (@Pc(5400) ObjStack objStack = (ObjStack) ObjStack.GROUND_ITEMS.first(); objStack != null; objStack = (ObjStack) ObjStack.GROUND_ITEMS.next()) {
														int stackPlane = (int) (objStack.key >> 28 & 0x3L);
														int absX = (int) (objStack.key & 0x3FFFL);
														int localX = absX - MapRegion.sceneBaseX;
														int absY = (int) (objStack.key >> 14 & 0x3FFFL);
														int localY = absY - MapRegion.sceneBaseY;
														if (stackPlane == UpdateZonePacketDecoder.updateZonePlane && localX >= UpdateZonePacket.updateZoneX && localX < UpdateZonePacket.updateZoneX + 8 && localY >= UpdateZonePacket.updateZoneY && UpdateZonePacket.updateZoneY + 8 > localY) {
															objStack.unlink();
															if (localX >= 0 && localY >= 0 && localX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > localY) {
																ObjStackEntity.refreshObjStack(localY, localX, UpdateZonePacketDecoder.updateZonePlane);
															}
														}
													}
													for (@Pc(5505) ChangeLocationRequest changeLocationRequest = (ChangeLocationRequest) LocAction.pendingRemovals.first(); changeLocationRequest != null; changeLocationRequest = (ChangeLocationRequest) LocAction.pendingRemovals.next()) {
														if (changeLocationRequest.x >= UpdateZonePacket.updateZoneX && UpdateZonePacket.updateZoneX + 8 > changeLocationRequest.x && UpdateZonePacket.updateZoneY <= changeLocationRequest.y && changeLocationRequest.y < UpdateZonePacket.updateZoneY + 8 && changeLocationRequest.plane == UpdateZonePacketDecoder.updateZonePlane) {
															changeLocationRequest.revertDelay = 0;
														}
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.ANIMATE_NPC) {
													int speed = BufferedConnectionContext.receivedBuffer.readUByteInverse();
													@Pc(5567) int[] animationIds = new int[4];
													for (int animIndex = 0; animIndex < 4; animIndex++) {
														animationIds[animIndex] = BufferedConnectionContext.receivedBuffer.readUShort();
													}
													int npcIndex = BufferedConnectionContext.receivedBuffer.readUShortLittle();
													@Pc(5592) NPCEntityNode npcEntityNode2 = (NPCEntityNode) NPCList.NPC_MAP.get((long) npcIndex);
													if (npcEntityNode2 != null) {
														NPCEntity.animateNpc(speed, animationIds, npcEntityNode2.npc);
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.BLOCK_MINIMAP_STATE) {
													ComponentMinimap.minimapState = BufferedConnectionContext.receivedBuffer.readUByte();
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (MusicTrackProt.MUSIC_TRACK == BufferedConnectionContext.currentPacket) {
													int fadeDuration = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
													int volume = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
													int musicTrackId = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
													if (musicTrackId == 65535) {
														musicTrackId = -1;
													}
													SongPlayer.playSong(volume, musicTrackId, fadeDuration);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.MUSIC_EFFECT == BufferedConnectionContext.currentPacket) {
													int delay = BufferedConnectionContext.receivedBuffer.readUMediumLittle();
													int jingleId = BufferedConnectionContext.receivedBuffer.readUShortAddLittle();
													if (jingleId == 65535) {
														jingleId = -1;
													}
													int volume = BufferedConnectionContext.receivedBuffer.readUByteInverse();
													PlayMusic.playJingle(jingleId, volume, delay);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.MESSAGE_PRIVATE_ECHO == BufferedConnectionContext.currentPacket) {
													string8 = BufferedConnectionContext.receivedBuffer.readString();
													string5 = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer));
													ChatLineWriter.writeMessage(string8, 6, 0, string5, string8, string8);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETTEXT) {
													int componentHash = BufferedConnectionContext.receivedBuffer.readIntLE();
													string5 = BufferedConnectionContext.receivedBuffer.readString();
													PlayerVarDomain.requestVarVerifyid();
													PulseEvent.priorityIfTextUpdate(componentHash, string5);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETPLAYERMODEL_OTHER) {
													int componentHash = BufferedConnectionContext.receivedBuffer.readUIntMiddle();
													int secondaryModelId = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
													int primaryModelId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
													PlayerVarDomain.requestVarVerifyid();
													PulseEvent.priorityIfcontentUpdate(5, componentHash, primaryModelId, secondaryModelId);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.SHOW_FACE_HERE == BufferedConnectionContext.currentPacket) {
													boolean showFaceHere = BufferedConnectionContext.receivedBuffer.readUByteInverse() == 1;
													PlayerVarDomain.requestVarVerifyid();
													MiniMenu.showFaceHere = showFaceHere;
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.RESET_SOUNDS) {
													SoundManager.resetSounds();
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.CHAT_FILTER_SETTINGS == BufferedConnectionContext.currentPacket) {
													ChatBox.publicFilter = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
													ChatBox.tradeFilter = BufferedConnectionContext.receivedBuffer.readUByteAdd();
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_OPENTOP) {
													int openType = BufferedConnectionContext.receivedBuffer.readUByteInverse();
													int topInterfaceId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
													PlayerVarDomain.requestVarVerifyid();
													if (openType == 2) {
														Static671.closeWorldMap();
													}
													ClientState.BASE_WINDOW_ID = topInterfaceId;
													Interface.resetComponentSequences(topInterfaceId);
													WindowMode.interfaceResize(false);
													CS2Interpreter.runInterfaceOnLoadScripts(ClientState.BASE_WINDOW_ID);
													for (int componentIndex = 0; componentIndex < 100; componentIndex++) {
														InterfaceRectangles.IS_COMPONENT_ON_TOP[componentIndex] = true;
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.UPDATE_REBOOT_TIMER) {
													if (MainLogicManager.inLobby(ClientState.mainLogicStep)) {
														client.rebootTimer = (int) ((float) BufferedConnectionContext.receivedBuffer.readUShort() * 2.5F);
													} else {
														client.rebootTimer = BufferedConnectionContext.receivedBuffer.readUShort() * 30;
													}
													InterfaceManager.lastMiscUpdateTick = ClientState.serverTick;
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.VORBIS_SPEECH_SOUND) {
													int soundId = BufferedConnectionContext.receivedBuffer.readUShort();
													if (soundId == 65535) {
														soundId = -1;
													}
													int loops = BufferedConnectionContext.receivedBuffer.readUByte();
													int delay = BufferedConnectionContext.receivedBuffer.readUShort();
													int volume = BufferedConnectionContext.receivedBuffer.readUByte();
													SoundManager.playSoundVorbis(loops, 256, true, volume, delay, soundId);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.IF_SETMODEL == BufferedConnectionContext.currentPacket) {
													int modelId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
													if (modelId == 65535) {
														modelId = -1;
													}
													int componentHash = BufferedConnectionContext.receivedBuffer.readUIntMiddle();
													PlayerVarDomain.requestVarVerifyid();
													PulseEvent.priorityIfcontentUpdate(1, componentHash, modelId, -1);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.SOUND_AREA) {
													UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.SOUND_AREA);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.IF_SETTARGETPARAM) {
													int toSlot = BufferedConnectionContext.receivedBuffer.readUShortAdd();
													if (toSlot == 65535) {
														toSlot = -1;
													}
													int fromSlot = BufferedConnectionContext.receivedBuffer.readUShortLittle();
													if (fromSlot == 65535) {
														fromSlot = -1;
													}
													int componentHash = BufferedConnectionContext.receivedBuffer.readInt();
													int settingsHash = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
													PlayerVarDomain.requestVarVerifyid();
													for (int slot = fromSlot; slot <= toSlot; slot++) {
														long slotKey = (long) slot + ((long) componentHash << 32);
														serverActiveProperties2 = (IFEvents) IFEvents.ICOMPONENT_SETTINGS_SLOTS.get(slotKey);
														if (serverActiveProperties2 != null) {
															serverActiveProperties = new IFEvents(settingsHash, serverActiveProperties2.interfaceId);
															serverActiveProperties2.unlink();
														} else if (slot == -1) {
															serverActiveProperties = new IFEvents(settingsHash, Interface.getComponent(componentHash).targetParams.interfaceId);
														} else {
															serverActiveProperties = new IFEvents(settingsHash, -1);
														}
														IFEvents.ICOMPONENT_SETTINGS_SLOTS.put(slotKey, serverActiveProperties);
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.VARBIT_LARGE) {
													int varbitId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
													int varbitValue = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
													PlayerVarDomain.playerVarProvider.setVarBitValueFromServer(varbitId, varbitValue);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.UPDATE_UID192) {
													BufferedConnectionContext.receivedBuffer.currentPosition += 28;
													if (BufferedConnectionContext.receivedBuffer.checkCrc()) {
														GameShell.updateRandomDatUID192(BufferedConnectionContext.receivedBuffer.currentPosition - 28, BufferedConnectionContext.receivedBuffer);
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.RUN_ENERGY) {
													ClientState.runEnergy = BufferedConnectionContext.receivedBuffer.readUByte();
													InterfaceManager.lastMiscUpdateTick = ClientState.serverTick;
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.UPDATE_STAT == BufferedConnectionContext.currentPacket) {
													int level = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
													int skillId = BufferedConnectionContext.receivedBuffer.readUByteInverse();
													int xp = BufferedConnectionContext.receivedBuffer.readUIntMiddle();
													XpDrops.statUpdated(skillId, client.statXp[skillId], xp); // Not part of the original client: see XpDrops.
													client.statXp[skillId] = xp;
													client.statLevels[skillId] = level;
													StatBaseLevels.statBaseLevels[skillId] = 1;
													int maxLevel = StatBaseLevels.SKILL_MAX_LEVEL[skillId] - 1;
													for (int levelIndex = 0; levelIndex < maxLevel; levelIndex++) {
														if (StatBaseLevels.SKILL_XP_FOR_LEVEL[levelIndex] <= xp) {
															StatBaseLevels.statBaseLevels[skillId] = levelIndex + 2;
														}
													}
													SkillUpdateCycle.recentSkillUpdates[SkillUpdateCycle.skillUpdateCycle++ & 0x1F] = skillId;
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.MAP_PROJANIM == BufferedConnectionContext.currentPacket) {
													UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.MAP_PROJANIM);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.UPDATE_INV_STOP_TRANSMIT == BufferedConnectionContext.currentPacket) {
													int containerId = BufferedConnectionContext.receivedBuffer.readUShortAdd();
													int flags = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
													boolean negativeKey = (flags & 0x1) == 1;
													ItemContainer.deleteItemContainer(negativeKey, containerId);
													ItemContainer.RECENTLY_USED_ITEM_CONTAINERS[client.recentlyUsedItemContainersCursorPos++ & 0x1F] = containerId;
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.UPDATE_INV_PARTIAL == BufferedConnectionContext.currentPacket) {
													int containerId = BufferedConnectionContext.receivedBuffer.readUShort();
													int flags = BufferedConnectionContext.receivedBuffer.readUByte();
													boolean negativeKey = (flags & 0x1) == 1;
													while (BufferedConnectionContext.receivedBuffer.currentPosition < BufferedConnectionContext.currentPacketSize) {
														int slot = BufferedConnectionContext.receivedBuffer.readUSmart();
														int itemId = BufferedConnectionContext.receivedBuffer.readUShort();
														int itemAmount = 0;
														if (itemId != 0) {
															itemAmount = BufferedConnectionContext.receivedBuffer.readUByte();
															if (itemAmount == 255) {
																itemAmount = BufferedConnectionContext.receivedBuffer.readInt();
															}
														}
														ItemContainer.setItemInContainer(containerId, negativeKey, slot, itemAmount, itemId - 1);
													}
													ItemContainer.RECENTLY_USED_ITEM_CONTAINERS[client.recentlyUsedItemContainersCursorPos++ & 0x1F] = containerId;
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.MINIMAP_FLAG == BufferedConnectionContext.currentPacket) {
													int flagX = BufferedConnectionContext.receivedBuffer.readUByteSubtract();
													int flagY = BufferedConnectionContext.receivedBuffer.readUByteAdd();
													if (flagX == 255) {
														flagY = -1;
														flagX = -1;
													}
													PulseEvent.priorityMinimapFlagUpdate(flagY, flagX);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.CLIENT_SETVARC_LARGE == BufferedConnectionContext.currentPacket) {
													int value = BufferedConnectionContext.receivedBuffer.readIntLE();
													int varcId = BufferedConnectionContext.receivedBuffer.readUShortLittle();
													PlayerVarDomain.requestVarVerifyid();
													PulseEvent.priorityClientvarcUpdate(varcId, value);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.NPC_UPDATE == BufferedConnectionContext.currentPacket) {
													NpcUpdate.decodeNpcUpdate();
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.MESSAGE_QUICKCHAT_PLAYER_GROUP == BufferedConnectionContext.currentPacket) {
													boolean hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
													string5 = BufferedConnectionContext.receivedBuffer.readString();
													string = string5;
													if (hasDisplayName) {
														string = BufferedConnectionContext.receivedBuffer.readString();
													}
													int playerRights = BufferedConnectionContext.receivedBuffer.readUByte();
													int phraseId = BufferedConnectionContext.receivedBuffer.readUShort();
													boolean ignored = false;
													if (playerRights <= 1 && IgnoreList.isIgnored(string)) {
														ignored = true;
													}
													if (!ignored && MapRegion.inTutorialIsland == 0) {
														string7 = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId).fillDynamicValues(BufferedConnectionContext.receivedBuffer);
														if (playerRights == 2) {
															ChatLineWriter.writeMessage("<img=1>" + string, string7, phraseId, "<img=1>" + string5, null, 25, 0, string5);
														} else if (playerRights == 1) {
															ChatLineWriter.writeMessage("<img=0>" + string, string7, phraseId, "<img=0>" + string5, null, 25, 0, string5);
														} else {
															ChatLineWriter.writeMessage(string, string7, phraseId, string5, null, 25, 0, string5);
														}
													}
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (ServerProt.IF_SETSCROLLPOS == BufferedConnectionContext.currentPacket) {
													int componentHash = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle();
													int scrollPos = BufferedConnectionContext.receivedBuffer.readUShortAdd();
													PlayerVarDomain.requestVarVerifyid();
													PulseEvent.priorityIfscrollposUpdate(componentHash, scrollPos);
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else if (BufferedConnectionContext.currentPacket == ServerProt.FRIENDS_CHAT_CHANNEL) {
													FriendsChat.lastFriendsChatUpdateTick = ClientState.serverTick;
													if (BufferedConnectionContext.currentPacketSize == 0) {
														FriendsChat.clanChatUserCount = 0;
														FriendsChat.friendsChatName = null;
														FriendsChat.clanChatUsers = null;
														FriendsChat.clanChatOwnerName = null;
														BufferedConnectionContext.currentPacket = null;
														return true;
													}
													FriendsChat.clanChatOwnerName = BufferedConnectionContext.receivedBuffer.readString();
													boolean hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
													if (hasDisplayName) {
														BufferedConnectionContext.receivedBuffer.readString();
													}
													@Pc(6550) long encodedName = BufferedConnectionContext.receivedBuffer.readLong();
													FriendsChat.friendsChatName = Utils.nameForLong(encodedName);
													FriendsChat.friendChatMinKickRank = BufferedConnectionContext.receivedBuffer.readByte();
													int userCount = BufferedConnectionContext.receivedBuffer.readUByte();
													if (userCount == 255) {
														BufferedConnectionContext.currentPacket = null;
														return true;
													}
													FriendsChat.clanChatUserCount = userCount;
													@Pc(6574) FriendsChatPlayer[] friendsChatPlayers = new FriendsChatPlayer[100];
													for (int userIndex = 0; userIndex < FriendsChat.clanChatUserCount; userIndex++) {
														friendsChatPlayers[userIndex] = new FriendsChatPlayer();
														friendsChatPlayers[userIndex].displayName = BufferedConnectionContext.receivedBuffer.readString();
														hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1;
														if (hasDisplayName) {
															friendsChatPlayers[userIndex].username = BufferedConnectionContext.receivedBuffer.readString();
														} else {
															friendsChatPlayers[userIndex].username = friendsChatPlayers[userIndex].displayName;
														}
														friendsChatPlayers[userIndex].textName = Utils.formatDisplayName(friendsChatPlayers[userIndex].username);
														friendsChatPlayers[userIndex].worldId = BufferedConnectionContext.receivedBuffer.readUShort();
														friendsChatPlayers[userIndex].rank = BufferedConnectionContext.receivedBuffer.readByte();
														friendsChatPlayers[userIndex].worldName = BufferedConnectionContext.receivedBuffer.readString();
														if (friendsChatPlayers[userIndex].username.equals(PlayerUpdate.localPlayer.displayName)) {
															FriendsChat.myFriendsChatRank = friendsChatPlayers[userIndex].rank;
														}
													}
													int unsortedCount = FriendsChat.clanChatUserCount;
													while (unsortedCount > 0) {
														unsortedCount--;
														boolean sorted = true;
														for (int userSortIndex = 0; userSortIndex < unsortedCount; userSortIndex++) {
															if (friendsChatPlayers[userSortIndex].textName.compareTo(friendsChatPlayers[userSortIndex + 1].textName) > 0) {
																fcPlayer = friendsChatPlayers[userSortIndex];
																friendsChatPlayers[userSortIndex] = friendsChatPlayers[userSortIndex + 1];
																friendsChatPlayers[userSortIndex + 1] = fcPlayer;
																sorted = false;
															}
														}
														if (sorted) {
															break;
														}
													}
													FriendsChat.clanChatUsers = friendsChatPlayers;
													BufferedConnectionContext.currentPacket = null;
													return true;
												} else {
													ExplicitRuntimeException.reportError("T1 - " + (BufferedConnectionContext.currentPacket == null ? -1 : BufferedConnectionContext.currentPacket.getOpcode()) + "," + (BufferedConnectionContext.secondLastPacket == null ? -1 : BufferedConnectionContext.secondLastPacket.getOpcode()) + "," + (ThirdLastPacket.thirdLastPacket == null ? -1 : ThirdLastPacket.thirdLastPacket.getOpcode()) + " - " + BufferedConnectionContext.currentPacketSize, null);
													ClientLogout.logout(false);
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
