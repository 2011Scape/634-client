import com.jagex.game.runetek5.client.features.XpDrops
import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.window.WindowModeType
import com.jagex.game.runetek5.cache.node.ChangeLocationRequest
import com.jagex.game.runetek5.FriendsChatPlayer
import com.jagex.game.runetek5.UpdateZonePacket
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.graphics.texture.water.NativeWaveTextureData
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.QuickChatPhrase
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.network.packet.ServerProt
import com.jagex.game.runetek5.SoundManager
import com.jagex.game.runetek5.entity.SpotAnimation
import com.jagex.game.runetek5.config.sequence.PositionedGfx
import com.jagex.game.runetek5.ThirdLastPacket
import com.jagex.game.runetek5.PriorityIfItemUpdate
import com.jagex.game.runetek5.PlayerInfoBitDecoder
import com.jagex.game.runetek5.UpdateZoneFullFollowsProt
import com.jagex.game.runetek5.MusicTrackProt
import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.IntKeyedQuicksort
import com.jagex.game.runetek5.network.packet.StockMarketOffer
import com.jagex.game.runetek5.core.datastruct.LoginStateUpdate
import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.IFEvents
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.config.paramtype.SkillUpdateCycle
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import com.jagex.game.runetek5.statics.StatBaseLevels
import io.IOException
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.loading.screen.op.instance.PriorityIfPositionUpdate
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.client.web.ClientURLTools
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.interaction.FriendList
import com.jagex.game.runetek5.graphics.shader.ShaderVariableType
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.scene.LocAction
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.cache.node.lru.PulseEvent
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer
import com.jagex.game.runetek5.interaction.IgnoreList
import com.jagex.game.runetek5.botdetection.ReflectionCheckManager
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.NpcUpdate
import com.jagex.game.runetek5.config.quickchat.QuickchatMessageTypeList
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.ChatFilter
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.sound.player.song.SongPlayer
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.encoding.HuffmanList
import com.jagex.game.runetek5.EntitySceneBuilder
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.ObjStackEntity
import com.jagex.game.runetek5.PlayMusic
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.core.constants.LocLayer
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.camera.Shake
import com.jagex.game.runetek5.config.midi.VorbisNode
import com.jagex.game.runetek5.network.Connection
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.Chat
import com.jagex.game.runetek5.network.WorldConnection
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.MainLogicManager
import com.jagex.game.runetek5.statics.OutboundByteCounter
import kotlin.jvm.JvmStatic

object PacketDecoder {
	@OriginalMember(owner = "client!pf", name = "b", descriptor = "(Z)Z")
	@Throws(IOException::class)
	@JvmStatic fun decode(): Boolean {
		if (Connection.connection == null) {
			return false
		}
		@Pc(73) var local73: Int
		if (BufferedConnectionContext.currentPacket == null) {
			if (BufferedConnectionContext.readPadding) {
				if (!Connection.connection!!.hasAvailable(1)) {
					return false
				}
				Connection.connection!!.read(1, 0, BufferedConnectionContext.receivedBuffer.buffer)
				BufferedConnectionContext.idleReadPulses = 0
				BufferedConnectionContext.readPadding = false
				OutboundByteCounter.bytesRead++
			}
			BufferedConnectionContext.receivedBuffer.currentPosition = 0
			if (BufferedConnectionContext.receivedBuffer.largeOpcode()) {
				if (!Connection.connection!!.hasAvailable(1)) {
					return false
				}
				Connection.connection!!.read(1, 1, BufferedConnectionContext.receivedBuffer.buffer)
				OutboundByteCounter.bytesRead++
				BufferedConnectionContext.idleReadPulses = 0
			}
			BufferedConnectionContext.readPadding = true
			@Pc(69) val serverProts: Array<ServerProt?>? = ServerProt.serverProtValues()
			val opcode: Int = BufferedConnectionContext.receivedBuffer.readOpcode()
			if (opcode < 0 || serverProts!!.size <= opcode) {
				throw IOException("invo:" + opcode + " ip:" + BufferedConnectionContext.receivedBuffer.currentPosition)
			}
			BufferedConnectionContext.currentPacket = serverProts[opcode]
			// Not part of the original client: a server may size a packet differently (client.server, client.packets).
			BufferedConnectionContext.currentPacketSize = ClientFeatures.serverPacketSize(opcode, BufferedConnectionContext.currentPacket!!.size)
		}
		if (BufferedConnectionContext.currentPacketSize == -1) {
			if (!Connection.connection!!.hasAvailable(1)) {
				return false
			}
			Connection.connection!!.read(1, 0, BufferedConnectionContext.receivedBuffer.buffer)
			BufferedConnectionContext.currentPacketSize = BufferedConnectionContext.receivedBuffer.buffer!![0].toInt() and 0xFF
			OutboundByteCounter.bytesRead++
			BufferedConnectionContext.idleReadPulses = 0
		}
		if (BufferedConnectionContext.currentPacketSize == -2) {
			if (!Connection.connection!!.hasAvailable(2)) {
				return false
			}
			Connection.connection!!.read(2, 0, BufferedConnectionContext.receivedBuffer.buffer)
			BufferedConnectionContext.receivedBuffer.currentPosition = 0
			BufferedConnectionContext.currentPacketSize = BufferedConnectionContext.receivedBuffer.readUShort()
			OutboundByteCounter.bytesRead += 2
			BufferedConnectionContext.idleReadPulses = 0
		}
		if (BufferedConnectionContext.currentPacketSize > 0) {
			if (!Connection.connection!!.hasAvailable(BufferedConnectionContext.currentPacketSize)) {
				return false
			}
			BufferedConnectionContext.receivedBuffer.currentPosition = 0
			Connection.connection!!.read(BufferedConnectionContext.currentPacketSize, 0, BufferedConnectionContext.receivedBuffer.buffer)
			OutboundByteCounter.bytesRead += BufferedConnectionContext.currentPacketSize
			BufferedConnectionContext.idleReadPulses = 0
		}
		ThirdLastPacket.thirdLastPacket = BufferedConnectionContext.secondLastPacket
		BufferedConnectionContext.secondLastPacket = BufferedConnectionContext.lastPacket
		BufferedConnectionContext.lastPacket = BufferedConnectionContext.currentPacket
		if (ServerProt.UPDATE_SITESETTINGS_COOKIE === BufferedConnectionContext.currentPacket) {
			GameShell.setSiteSettingsCookie(BufferedConnectionContext.receivedBuffer.readString())
			BufferedConnectionContext.currentPacket = null
			return true
		}
		@Pc(233) var string: String?
		@Pc(220) var local220: Int
		@Pc(247) var string2: String?
		@Pc(261) var clanUserIndex: Int
		@Pc(251) var string3: String?
		@Pc(237) var string4: String?
		if (BufferedConnectionContext.currentPacket === ServerProt.ADD_IGNORE) {
			val ignoreFlags: Int = BufferedConnectionContext.receivedBuffer.readUByte()
			@Pc(229) val editDisplayName: Boolean = ignoreFlags and 0x1 == 1
			string = BufferedConnectionContext.receivedBuffer.readString()
			string4 = BufferedConnectionContext.receivedBuffer.readString()
			if (string4!!.equals("")) {
				string4 = string
			}
			string2 = BufferedConnectionContext.receivedBuffer.readString()
			string3 = BufferedConnectionContext.receivedBuffer.readString()
			if (string3!!.equals("")) {
				string3 = string2
			}
			if (editDisplayName) {
				var ignoreIndex: Int = 0
				while (ignoreIndex < IgnoreList.ignoreCount) {
					if (IgnoreList.ignoreNamesUnfiltered[ignoreIndex]!!.equals(string3)) {
						IgnoreList.ignoreNames[ignoreIndex] = string
						IgnoreList.ignoreNamesUnfiltered[ignoreIndex] = string4
						IgnoreList.ignorePreviousNames[ignoreIndex] = string2
						IgnoreList.ignorePreviousNamesUnfiltered[ignoreIndex] = string3
						break
					}
					ignoreIndex++
				}
			} else {
				IgnoreList.ignoreNames[IgnoreList.ignoreCount] = string
				IgnoreList.ignoreNamesUnfiltered[IgnoreList.ignoreCount] = string4
				IgnoreList.ignorePreviousNames[IgnoreList.ignoreCount] = string2
				IgnoreList.ignorePreviousNamesUnfiltered[IgnoreList.ignoreCount] = string3
				IgnoreList.ignoreIsTemporary[IgnoreList.ignoreCount] = ignoreFlags and 0x2 == 2
				IgnoreList.ignoreCount++
			}
			FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick
			BufferedConnectionContext.currentPacket = null
			return true
		} else if (BufferedConnectionContext.currentPacket === ServerProt.CREATE_OBJECT) {
			UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.CREATE_OBJECT)
			BufferedConnectionContext.currentPacket = null
			return true
		} else if (ServerProt.CLIENT_SETVARC_SMALL === BufferedConnectionContext.currentPacket) {
			@Pc(351) val varcValue: Byte = BufferedConnectionContext.receivedBuffer.readByte()
			val varcId: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
			PlayerVarDomain.requestVarVerifyid()
			PulseEvent.priorityClientvarcUpdate(varcId, varcValue.toInt())
			BufferedConnectionContext.currentPacket = null
			return true
		} else {
			@Pc(394) var subInterface: SubInterface?
			if (ServerProt.IF_MOVESUB === BufferedConnectionContext.currentPacket) {
				val targetComponentHash: Int = BufferedConnectionContext.receivedBuffer.readIntLE()
				val sourceComponentHash: Int = BufferedConnectionContext.receivedBuffer.readInt()
				PlayerVarDomain.requestVarVerifyid()
				@Pc(387) val subInterface2: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(sourceComponentHash.toLong()) as SubInterface?
				subInterface = InterfaceManager.OPEN_INTERFACES.get(targetComponentHash.toLong()) as SubInterface?
				if (subInterface != null) {
					SubInterfaceCloser.closeSubInterface(subInterface2 == null || subInterface2.interfaceId != subInterface.interfaceId, false, subInterface)
				}
				if (subInterface2 != null) {
					subInterface2.unlink()
					InterfaceManager.OPEN_INTERFACES.put(targetComponentHash.toLong(), subInterface2)
				}
				@Pc(429) var component: Component? = Interface.getComponent(sourceComponentHash)
				if (component != null) {
					Component.redrawComponent(component)
				}
				component = Interface.getComponent(targetComponentHash)
				if (component != null) {
					Component.redrawComponent(component)
					Component.layoutChildren(true, component)
				}
				if (ClientState.BASE_WINDOW_ID != -1) {
					Static683.runSubInterfaceHooks(ClientState.BASE_WINDOW_ID, 1)
				}
				BufferedConnectionContext.currentPacket = null
				return true
			} else if (BufferedConnectionContext.currentPacket === ServerProt.SET_CURSOR) {
				MiniMenu.walkHereText = if (BufferedConnectionContext.currentPacketSize > 2) BufferedConnectionContext.receivedBuffer.readString() else LocalizedText.WALK_HERE.get(ClientState.language)
				CursorManager.activeCursorId = if (BufferedConnectionContext.currentPacketSize <= 0) -1 else BufferedConnectionContext.receivedBuffer.readUShort()
				if (CursorManager.activeCursorId == 65535) {
					CursorManager.activeCursorId = -1
				}
				BufferedConnectionContext.currentPacket = null
				return true
			} else if (ServerProt.IF_SETHIDE === BufferedConnectionContext.currentPacket) {
				val hidden: Int = BufferedConnectionContext.receivedBuffer.readUByteAdd()
				val componentHash: Int = BufferedConnectionContext.receivedBuffer.readIntLE()
				PlayerVarDomain.requestVarVerifyid()
				PulseEvent.priorityIfHideUpdate(hidden, componentHash)
				BufferedConnectionContext.currentPacket = null
				return true
			} else {
				@Pc(543) var local543: Int
				@Pc(539) var local539: Int
				if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETPLAYERHEAD_IGNOREWORN) {
					val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
					val secondaryModelId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
					val primaryModelHigh: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
					val primaryModelLow: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
					PlayerVarDomain.requestVarVerifyid()
					PulseEvent.priorityIfcontentUpdate(7, componentHash, (primaryModelHigh shl 16) or primaryModelLow, secondaryModelId)
					BufferedConnectionContext.currentPacket = null
					return true
				} else if (ServerProt.PLAYER_WEIGHT === BufferedConnectionContext.currentPacket) {
					client.runWeight = BufferedConnectionContext.receivedBuffer.readShort()
					BufferedConnectionContext.currentPacket = null
					InterfaceManager.lastMiscUpdateTick = ClientState.serverTick
					return true
				} else if (BufferedConnectionContext.currentPacket === ServerProt.MIDI_SONG_LOCATION) {
					UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.MIDI_SONG_LOCATION)
					BufferedConnectionContext.currentPacket = null
					return true
				} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETPLAYERHEAD) {
					val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntMiddle()
					PlayerVarDomain.requestVarVerifyid()
					PulseEvent.priorityIfcontentUpdate(3, componentHash, -1, -1)
					BufferedConnectionContext.currentPacket = null
					return true
				} else {
					@Pc(645) var local645: Int
					@Pc(615) var local615: Boolean
					@Pc(619) var string5: String?
					@Pc(631) var local631: Long
					@Pc(636) var local636: Long
					@Pc(641) var hashLow: Long
					@Pc(655) var local655: Int
					if (BufferedConnectionContext.currentPacket === ServerProt.MESSAGE_FRIENDS_CHAT) {
						var hasDisplayName: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
						string5 = BufferedConnectionContext.receivedBuffer.readString()
						string = string5
						if (hasDisplayName) {
							string = BufferedConnectionContext.receivedBuffer.readString()
						}
						val encodedChannelName: Long = BufferedConnectionContext.receivedBuffer.readLong()
						val messageHashHigh: Long = BufferedConnectionContext.receivedBuffer.readUShort().toLong()
						hashLow = BufferedConnectionContext.receivedBuffer.readUMedium().toLong()
						val playerRights: Int = BufferedConnectionContext.receivedBuffer.readUByte()
						@Pc(651) val messageHash: Long = hashLow + (messageHashHigh shl 32)
						@Pc(653) var ignore: Boolean = false
						var hashIndex: Int = 0
						while (true) {
							if (hashIndex >= 100) {
								if (playerRights <= 1) {
									if (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly) {
										ignore = true
									} else if (IgnoreList.isIgnored(string)) {
										ignore = true
									}
								}
								break
							}
							if (messageHash == Chat.CHAT_MESSAGE_UNIQUE_HASHES[hashIndex]) {
								ignore = true
								break
							}
							hashIndex++
						}
						if (!ignore && MapRegion.inTutorialIsland == 0) {
							Chat.CHAT_MESSAGE_UNIQUE_HASHES[Camera.chatMessageHashIndex] = messageHash
							Camera.chatMessageHashIndex = (Camera.chatMessageHashIndex + 1) % 100
							@Pc(712) val string6: String? = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer)!!)
							if (playerRights == 2 || playerRights == 3) {
								ChatLineWriter.writeMessage("<img=1>" + string, string6, -1, "<img=1>" + string5, Utils.decodeUidName(encodedChannelName), 9, 0, string5)
							} else if (playerRights == 1) {
								ChatLineWriter.writeMessage("<img=0>" + string, string6, -1, "<img=0>" + string5, Utils.decodeUidName(encodedChannelName), 9, 0, string5)
							} else {
								ChatLineWriter.writeMessage(string, string6, -1, string5, Utils.decodeUidName(encodedChannelName), 9, 0, string5)
							}
						}
						BufferedConnectionContext.currentPacket = null
						return true
					} else if (BufferedConnectionContext.currentPacket === ServerProt.SET_VORBIS_SOUND) {
						val vorbisSoundId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
						PlayerVarDomain.requestVarVerifyid()
						VorbisNode.toggleVorbisPause(vorbisSoundId)
						BufferedConnectionContext.currentPacket = null
						return true
					} else if (BufferedConnectionContext.currentPacket === ServerProt.GROUND_ITEM_COUNT) {
						UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.GROUND_ITEM_COUNT)
						BufferedConnectionContext.currentPacket = null
						return true
					} else {
						@Pc(845) var local845: Int
						if (BufferedConnectionContext.currentPacket === ServerProt.CAM_SHAKE) {
							val shakeJitter: Int = BufferedConnectionContext.receivedBuffer.readUByteAdd()
							val shakeIndex: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
							val shakePhase: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
							val shakeAmplitude: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
							val shakeFrequency: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
							PlayerVarDomain.requestVarVerifyid()
							Camera.CAM_SHAKING[shakeIndex] = true
							Camera.CAM_SHAKE_JITTER[shakeIndex] = shakeJitter
							Shake.CAM_SHAKE_AMPLITUDE[shakeIndex] = shakeAmplitude
							Shake.CAM_SHAKE_FREQUENCY[shakeIndex] = shakeFrequency
							Shake.CAM_SHAKE_PHASE[shakeIndex] = shakePhase
							BufferedConnectionContext.currentPacket = null
							return true
						} else if (BufferedConnectionContext.currentPacket === ServerProt.UPDATE_ZONE_PARTIAL_FOLLOWS) {
							UpdateZonePacket.updateZoneY = BufferedConnectionContext.receivedBuffer.readByteInverse().toInt() shl 3
							UpdateZonePacketDecoder.updateZonePlane = BufferedConnectionContext.receivedBuffer.readUByteAdd()
							UpdateZonePacket.updateZoneX = BufferedConnectionContext.receivedBuffer.readByteAdd().toInt() shl 3
							BufferedConnectionContext.currentPacket = null
							return true
						} else if (BufferedConnectionContext.currentPacket === ServerProt.DYNAMIC_MAP_REGION) {
							ConstructedMapInit.initializeConstructedMap()
							BufferedConnectionContext.currentPacket = null
							return false
						} else if (ServerProt.SET_DRAW_ORDER === BufferedConnectionContext.currentPacket) {
							val drawOrder: Int = BufferedConnectionContext.receivedBuffer.readUByteAdd()
							PlayerVarDomain.requestVarVerifyid()
							EntitySceneBuilder.entityDrawOrder = drawOrder
							BufferedConnectionContext.currentPacket = null
							return true
						} else if (ServerProt.DESTROY_OBJECT === BufferedConnectionContext.currentPacket) {
							UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.DESTROY_OBJECT)
							BufferedConnectionContext.currentPacket = null
							return true
						} else if (BufferedConnectionContext.currentPacket === ServerProt.CAM_FORCEANGLE) {
							val angleX: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
							val angleY: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
							PlayerVarDomain.requestVarVerifyid()
							Camera.forceCameraAngle(angleX, angleY)
							BufferedConnectionContext.currentPacket = null
							return true
						} else if (ServerProt.IF_SETITEM === BufferedConnectionContext.currentPacket) {
							var itemId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
							if (itemId == 65535) {
								itemId = -1
							}
							val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
							val amount: Int = BufferedConnectionContext.receivedBuffer.readInt()
							PlayerVarDomain.requestVarVerifyid()
							PriorityIfItemUpdate.priorityIfItemUpdate(amount, itemId, componentHash)
							@Pc(986) val objType: ItemType? = TypeLists.OBJ!!.list(itemId)
							PulseEvent.priorityIfangleUpdate(objType!!.modelZoom, objType.modelRotationX, objType.modelRotationY, componentHash)
							PulseEvent.priorityIfOffsetUpdate(objType.modelOffsetX, objType.modelOffsetY, componentHash, objType.modelRotationZ)
							BufferedConnectionContext.currentPacket = null
							return true
						} else {
							@Pc(1046) var local1046: Int
							@Pc(1030) var local1030: Boolean
							if (ServerProt.UPDATE_INV_FULL === BufferedConnectionContext.currentPacket) {
								val containerId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
								val containerFlags: Int = BufferedConnectionContext.receivedBuffer.readUByte()
								val negativeKey: Boolean = containerFlags and 0x1 == 1
								ItemContainer.resetContainer(containerId, negativeKey)
								val slotCount: Int = BufferedConnectionContext.receivedBuffer.readUShort()
								for (slot in 0 until slotCount) {
									var itemAmount: Int = BufferedConnectionContext.receivedBuffer.readUByteAdd()
									if (itemAmount == 255) {
										itemAmount = BufferedConnectionContext.receivedBuffer.readUIntMiddle()
									}
									val itemIdPlusOne: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
									ItemContainer.setItemInContainer(containerId, negativeKey, slot, itemAmount, itemIdPlusOne - 1)
								}
								ItemContainer.RECENTLY_USED_ITEM_CONTAINERS[client.recentlyUsedItemContainersCursorPos++ and 0x1F] = containerId
								BufferedConnectionContext.currentPacket = null
								return true
							} else if (BufferedConnectionContext.currentPacket === ServerProt.LOGOUT_FULL) {
								ClientLogout.logout(false)
								BufferedConnectionContext.currentPacket = null
								return false
							} else if (BufferedConnectionContext.currentPacket === ServerProt.VARP_LARGE) {
								val varpValue: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
								val varpId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
								PlayerVarDomain.playerVarProvider!!.updateVarp(varpValue, varpId)
								BufferedConnectionContext.currentPacket = null
								return true
							} else {
								@Pc(1184) var insertIndex: Int
								@Pc(1778) var local1778: Int
								@Pc(1205) var local1205: Boolean
								@Pc(1213) var local1213: Int
								@Pc(1572) var loopType: Int
								@Pc(1152) var local1152: Boolean
								@Pc(1162) var local1162: Int
								@Pc(1489) var local1489: Boolean
								@Pc(1497) var local1497: Int
								if (BufferedConnectionContext.currentPacket === ServerProt.SPOT_ANIM_SPECIFIC) {
									val spotAnimSettings: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
									val spotAnimHeight: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
									var spotAnimId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
									val spotAnimDelay: Int = BufferedConnectionContext.receivedBuffer.readUShort()
									val spotAnimTarget: Int = BufferedConnectionContext.receivedBuffer.readUIntMiddle()
									val isSecondary: Boolean = spotAnimSettings and 0x80 != 0
									val spotAnimRotation: Int = spotAnimSettings and 0x7
									var spotAnimAdjustType: Int = (spotAnimSettings shr 3) and 0xF
									if (spotAnimAdjustType == 15) {
										spotAnimAdjustType = -1
									}
									if (spotAnimTarget shr 30 == 0) {
										@Pc(1296) var seqType: SeqType?
										@Pc(1233) var spotAnimationType: SpotAnimationType?
										@Pc(1247) var seqType2: SeqType?
										if (spotAnimTarget shr 29 != 0) {
											var npcIndex: Int = spotAnimTarget and 0xFFFF
											@Pc(1191) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(npcIndex.toLong()) as NPCEntityNode?
											if (npcEntityNode != null) {
												@Pc(1196) val npcEntity: NPCEntity? = npcEntityNode.npc
												if (spotAnimId == 65535) {
													spotAnimId = -1
												}
												var applySpotAnim: Boolean = true
												val currentSpotAnimId: Int = if (isSecondary) npcEntity!!.secondarySpotAnimation.spotAnimationId else npcEntity!!.primarySpotAnimation.spotAnimationId
												if (spotAnimId != -1 && currentSpotAnimId != -1) {
													if (spotAnimId == currentSpotAnimId) {
														spotAnimationType = TypeLists.SPOT_ANIM!!.list(spotAnimId)
														if (spotAnimationType.replay && spotAnimationType.sequenceId != -1) {
															seqType2 = TypeLists.SEQ!!.list(spotAnimationType.sequenceId)
															@Pc(1250) val reqLoopType: Int = seqType2.loopType
															if (reqLoopType == 0 || reqLoopType == 2) {
																applySpotAnim = false
															} else if (reqLoopType == 1) {
																applySpotAnim = true
															}
														}
													} else {
														spotAnimationType = TypeLists.SPOT_ANIM!!.list(spotAnimId)
														@Pc(1281) val spotAnimationType2: SpotAnimationType? = TypeLists.SPOT_ANIM!!.list(currentSpotAnimId)
														if (spotAnimationType.sequenceId != -1 && spotAnimationType2!!.sequenceId != -1) {
															seqType = TypeLists.SEQ!!.list(spotAnimationType.sequenceId)
															@Pc(1302) val seqType3: SeqType? = TypeLists.SEQ!!.list(spotAnimationType2.sequenceId)
															if (seqType.priority < seqType3!!.priority) {
																applySpotAnim = false
															}
														}
													}
												}
												if (applySpotAnim) {
													if (isSecondary) {
														npcEntity.secondarySpotAnimation.startCycle = spotAnimDelay + ClientState.clientCycle
														npcEntity.secondarySpotAnimation.height = spotAnimHeight
														npcEntity.secondarySpotAnimation.spotAnimationId = spotAnimId
														npcEntity.secondarySpotAnimation.adjustmentType = spotAnimAdjustType
														npcEntity.secondarySpotAnimation.rotation = spotAnimRotation
														npcEntity.secondarySpotAnimation.currentFrameDuration = 0
														npcEntity.secondarySpotAnimation.nextFrameId = 1
														npcEntity.secondarySpotAnimation.currentFrameId = 0
														if (npcEntity.secondarySpotAnimation.startCycle > ClientState.clientCycle) {
															npcEntity.secondarySpotAnimation.currentFrameId = -1
														}
														if (npcEntity.secondarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle == npcEntity.secondarySpotAnimation.startCycle) {
															val sequenceId: Int = TypeLists.SPOT_ANIM!!.list(npcEntity.secondarySpotAnimation.spotAnimationId).sequenceId
															if (sequenceId != -1) {
																seqType2 = TypeLists.SEQ!!.list(sequenceId)
																if (seqType2 != null && seqType2.frames != null && !npcEntity.isHidden) {
																	SoundManager.playSequenceSound(npcEntity, 0, seqType2)
																}
															}
														}
													} else {
														npcEntity.primarySpotAnimation.currentFrameDuration = 0
														npcEntity.primarySpotAnimation.height = spotAnimHeight
														npcEntity.primarySpotAnimation.spotAnimationId = spotAnimId
														npcEntity.primarySpotAnimation.currentFrameId = 0
														npcEntity.primarySpotAnimation.adjustmentType = spotAnimAdjustType
														npcEntity.primarySpotAnimation.startCycle = ClientState.clientCycle + spotAnimDelay
														npcEntity.primarySpotAnimation.rotation = spotAnimRotation
														npcEntity.primarySpotAnimation.nextFrameId = 1
														if (npcEntity.primarySpotAnimation.startCycle > ClientState.clientCycle) {
															npcEntity.primarySpotAnimation.currentFrameId = -1
														}
														if (npcEntity.primarySpotAnimation.spotAnimationId != -1 && npcEntity.primarySpotAnimation.startCycle == ClientState.clientCycle) {
															val sequenceId: Int = TypeLists.SPOT_ANIM!!.list(npcEntity.primarySpotAnimation.spotAnimationId).sequenceId
															if (sequenceId != -1) {
																seqType2 = TypeLists.SEQ!!.list(sequenceId)
																if (seqType2 != null && seqType2.frames != null && !npcEntity.isHidden) {
																	SoundManager.playSequenceSound(npcEntity, 0, seqType2)
																}
															}
														}
													}
												}
											}
										} else if (spotAnimTarget shr 28 != 0) {
											var playerIndex: Int = spotAnimTarget and 0xFFFF
											@Pc(1476) var playerEntity: PlayerEntity?
											if (PlayerUpdate.myPlayerIndex == playerIndex) {
												playerEntity = PlayerUpdate.localPlayer
											} else {
												playerEntity = PlayerUpdate.players[playerIndex]
											}
											if (playerEntity != null) {
												if (spotAnimId == 65535) {
													spotAnimId = -1
												}
												var applySpotAnim: Boolean = true
												val currentSpotAnimId: Int = if (isSecondary) playerEntity.secondarySpotAnimation.spotAnimationId else playerEntity.primarySpotAnimation.spotAnimationId
												@Pc(1569) var seqType4: SeqType?
												if (spotAnimId != -1 && currentSpotAnimId != -1) {
													@Pc(1516) var spotAnimationType3: SpotAnimationType?
													if (currentSpotAnimId == spotAnimId) {
														spotAnimationType3 = TypeLists.SPOT_ANIM!!.list(spotAnimId)
														if (spotAnimationType3.replay && spotAnimationType3.sequenceId != -1) {
															seqType4 = TypeLists.SEQ!!.list(spotAnimationType3.sequenceId)
															loopType = seqType4.loopType
															if (loopType == 0 || loopType == 2) {
																applySpotAnim = false
															} else if (loopType == 1) {
																applySpotAnim = true
															}
														}
													} else {
														spotAnimationType3 = TypeLists.SPOT_ANIM!!.list(spotAnimId)
														spotAnimationType = TypeLists.SPOT_ANIM!!.list(currentSpotAnimId)
														if (spotAnimationType3.sequenceId != -1 && spotAnimationType.sequenceId != -1) {
															seqType2 = TypeLists.SEQ!!.list(spotAnimationType3.sequenceId)
															seqType = TypeLists.SEQ!!.list(spotAnimationType.sequenceId)
															if (seqType2.priority < seqType.priority) {
																applySpotAnim = false
															}
														}
													}
												}
												if (applySpotAnim) {
													if (isSecondary) {
														playerEntity.secondarySpotAnimation.currentFrameId = 0
														playerEntity.secondarySpotAnimation.height = spotAnimHeight
														playerEntity.secondarySpotAnimation.adjustmentType = spotAnimAdjustType
														playerEntity.secondarySpotAnimation.currentFrameDuration = 0
														playerEntity.secondarySpotAnimation.nextFrameId = 1
														playerEntity.secondarySpotAnimation.spotAnimationId = spotAnimId
														playerEntity.secondarySpotAnimation.startCycle = spotAnimDelay + ClientState.clientCycle
														playerEntity.secondarySpotAnimation.rotation = spotAnimRotation
														if (playerEntity.secondarySpotAnimation.startCycle > ClientState.clientCycle) {
															playerEntity.secondarySpotAnimation.currentFrameId = -1
														}
														if (playerEntity.secondarySpotAnimation.spotAnimationId == 65535) {
															playerEntity.secondarySpotAnimation.spotAnimationId = -1
														}
														if (playerEntity.secondarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle == playerEntity.secondarySpotAnimation.startCycle) {
															val sequenceId: Int = TypeLists.SPOT_ANIM!!.list(playerEntity.secondarySpotAnimation.spotAnimationId).sequenceId
															if (sequenceId != -1) {
																seqType4 = TypeLists.SEQ!!.list(sequenceId)
																if (seqType4 != null && seqType4.frames != null && !playerEntity.isHidden) {
																	SoundManager.playSequenceSound(playerEntity, 0, seqType4)
																}
															}
														}
													} else {
														playerEntity.primarySpotAnimation.spotAnimationId = spotAnimId
														playerEntity.primarySpotAnimation.currentFrameDuration = 0
														playerEntity.primarySpotAnimation.height = spotAnimHeight
														playerEntity.primarySpotAnimation.currentFrameId = 0
														playerEntity.primarySpotAnimation.adjustmentType = spotAnimAdjustType
														playerEntity.primarySpotAnimation.rotation = spotAnimRotation
														playerEntity.primarySpotAnimation.nextFrameId = 1
														playerEntity.primarySpotAnimation.startCycle = spotAnimDelay + ClientState.clientCycle
														if (playerEntity.primarySpotAnimation.spotAnimationId == 65535) {
															playerEntity.primarySpotAnimation.spotAnimationId = -1
														}
														if (ClientState.clientCycle < playerEntity.primarySpotAnimation.startCycle) {
															playerEntity.primarySpotAnimation.currentFrameId = -1
														}
														if (playerEntity.primarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle == playerEntity.primarySpotAnimation.startCycle) {
															val sequenceId: Int = TypeLists.SPOT_ANIM!!.list(playerEntity.primarySpotAnimation.spotAnimationId).sequenceId
															if (sequenceId != -1) {
																seqType4 = TypeLists.SEQ!!.list(sequenceId)
																if (seqType4 != null && seqType4.frames != null && !playerEntity.isHidden) {
																	SoundManager.playSequenceSound(playerEntity, 0, seqType4)
																}
															}
														}
													}
												}
											}
										}
									} else {
										val plane: Int = (spotAnimTarget shr 28) and 0x3
										var localX: Int = ((spotAnimTarget shr 14) and 0x3FFF) - MapRegion.sceneBaseX
										var localY: Int = (spotAnimTarget and 0x3FFF) - MapRegion.sceneBaseY
										if (localX >= 0 && localY >= 0 && MapRegion.sceneSizeX > localX && MapRegion.sceneSizeY > localY) {
											val absoluteX: Int = localX * 512 + 256
											val absoluteY: Int = localY * 512 + 256
											var virtualLevel: Int = plane
											if (plane < 3 && SettingsBits.isBridge(localY, localX)) {
												virtualLevel = plane + 1
											}
											@Pc(1843) val spotAnimation: SpotAnimation = SpotAnimation(spotAnimId, spotAnimDelay, ClientState.clientCycle, plane, virtualLevel, absoluteX, Scene.tileHeightForAbsolutePosition(absoluteX, plane, absoluteY) - spotAnimHeight, absoluteY, localX, localX, localY, localY, spotAnimRotation)
											Renderers.activeToolkit.addLast(PositionedGfx(spotAnimation))
										}
									}
									BufferedConnectionContext.currentPacket = null
									return true
								} else if (BufferedConnectionContext.currentPacket === ServerProt.CLIENT_SETVARCSTR_LARGE) {
									val varcId: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
									string5 = BufferedConnectionContext.receivedBuffer.readString()
									PlayerVarDomain.requestVarVerifyid()
									PulseEvent.priorityVarcstringUpdate(string5, varcId)
									BufferedConnectionContext.currentPacket = null
									return true
								} else if (BufferedConnectionContext.currentPacket === ServerProt.UPDATE_DOB) {
									LoginManager.playerDob = BufferedConnectionContext.receivedBuffer.readMedium()
									LoginManager.userDetailQuickchat = BufferedConnectionContext.receivedBuffer.readUByte() == 1
									BufferedConnectionContext.currentPacket = null
									return true
								} else {
									@Pc(2017) var fcPlayer: FriendsChatPlayer?
									@Pc(2013) var string7: String?
									@Pc(1903) var string8: String?
									if (ServerProt.UPDATE_FRIENDCHAT_CHANNEL_SINGLEUSER === BufferedConnectionContext.currentPacket) {
										string8 = BufferedConnectionContext.receivedBuffer.readString()
										var hasDisplayName: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
										if (hasDisplayName) {
											string5 = BufferedConnectionContext.receivedBuffer.readString()
										} else {
											string5 = string8
										}
										val worldId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
										@Pc(1930) val rank: Byte = BufferedConnectionContext.receivedBuffer.readByte()
										var removeUser: Boolean = false
										if (rank.toInt() == -128) {
											removeUser = true
										}
										if (removeUser) {
											if (FriendsChat.clanChatUserCount == 0) {
												BufferedConnectionContext.currentPacket = null
												return true
											}
											clanUserIndex = 0
											while (FriendsChat.clanChatUserCount > clanUserIndex && (!FriendsChat.clanChatUsers!![clanUserIndex]!!.username!!.equals(string5) || worldId != FriendsChat.clanChatUsers!![clanUserIndex]!!.worldId)) {
												clanUserIndex++
											}
											if (clanUserIndex < FriendsChat.clanChatUserCount) {
												while (clanUserIndex < FriendsChat.clanChatUserCount - 1) {
													FriendsChat.clanChatUsers!![clanUserIndex] = FriendsChat.clanChatUsers!![clanUserIndex + 1]
													clanUserIndex++
												}
												FriendsChat.clanChatUserCount--
												FriendsChat.clanChatUsers!![FriendsChat.clanChatUserCount] = null
											}
										} else {
											string7 = BufferedConnectionContext.receivedBuffer.readString()
											fcPlayer = FriendsChatPlayer()
											fcPlayer.username = string5
											fcPlayer.displayName = string8
											fcPlayer.textName = Utils.formatDisplayName(fcPlayer.username)
											fcPlayer.worldName = string7
											fcPlayer.worldId = worldId
											fcPlayer.rank = rank
											insertIndex = FriendsChat.clanChatUserCount - 1
											while (insertIndex >= 0) {
												val nameCompare: Int = FriendsChat.clanChatUsers!![insertIndex]!!.textName!!.compareTo(fcPlayer.textName!!)
												if (nameCompare == 0) {
													FriendsChat.clanChatUsers!![insertIndex]!!.worldId = worldId
													FriendsChat.clanChatUsers!![insertIndex]!!.rank = rank
													FriendsChat.clanChatUsers!![insertIndex]!!.worldName = string7
													if (string5!!.equals(PlayerUpdate.localPlayer!!.displayName)) {
														FriendsChat.myFriendsChatRank = rank
													}
													FriendsChat.lastFriendsChatUpdateTick = ClientState.serverTick
													BufferedConnectionContext.currentPacket = null
													return true
												}
												if (nameCompare < 0) {
													break
												}
												insertIndex--
											}
											if (FriendsChat.clanChatUsers!!.size <= FriendsChat.clanChatUserCount) {
												BufferedConnectionContext.currentPacket = null
												return true
											}
											var userIndex: Int = FriendsChat.clanChatUserCount - 1
											while (userIndex > insertIndex) {
												FriendsChat.clanChatUsers!![userIndex + 1] = FriendsChat.clanChatUsers!![userIndex]
												userIndex--
											}
											if (FriendsChat.clanChatUserCount == 0) {
												FriendsChat.clanChatUsers = arrayOfNulls<FriendsChatPlayer>(100)
											}
											FriendsChat.clanChatUsers!![insertIndex + 1] = fcPlayer
											FriendsChat.clanChatUserCount++
											if (string5!!.equals(PlayerUpdate.localPlayer!!.displayName)) {
												FriendsChat.myFriendsChatRank = rank
											}
										}
										FriendsChat.lastFriendsChatUpdateTick = ClientState.serverTick
										BufferedConnectionContext.currentPacket = null
										return true
									} else if (ServerProt.HINT_ARROW === BufferedConnectionContext.currentPacket) {
										val hintArrowHeader: Int = BufferedConnectionContext.receivedBuffer.readUByte()
										val hintArrowSlot: Int = hintArrowHeader shr 5
										val hintTargetType: Int = hintArrowHeader and 0x1F
										if (hintTargetType == 0) {
											PlayerUpdate.HINT_ARROWS[hintArrowSlot] = null
											BufferedConnectionContext.currentPacket = null
											return true
										}
										@Pc(2182) val hintArrow: HintArrow = HintArrow()
										hintArrow.targetType = hintTargetType
										hintArrow.arrowSprite = BufferedConnectionContext.receivedBuffer.readUByte()
										if (hintArrow.arrowSprite >= 0 && hintArrow.arrowSprite < StaticMedia.hintHeadiconSprites!!.size) {
											if (hintArrow.targetType == 1 || hintArrow.targetType == 10) {
												hintArrow.targetIndex = BufferedConnectionContext.receivedBuffer.readUShort()
												BufferedConnectionContext.receivedBuffer.currentPosition += 6
											} else if (hintArrow.targetType >= 2 && hintArrow.targetType <= 6) {
												if (hintArrow.targetType == 2) {
													hintArrow.y = 256
													hintArrow.x = 256
												}
												if (hintArrow.targetType == 3) {
													hintArrow.x = 0
													hintArrow.y = 256
												}
												if (hintArrow.targetType == 4) {
													hintArrow.x = 512
													hintArrow.y = 256
												}
												if (hintArrow.targetType == 5) {
													hintArrow.x = 256
													hintArrow.y = 0
												}
												if (hintArrow.targetType == 6) {
													hintArrow.x = 256
													hintArrow.y = 512
												}
												hintArrow.targetType = 2
												hintArrow.plane = BufferedConnectionContext.receivedBuffer.readUByte()
												hintArrow.x += BufferedConnectionContext.receivedBuffer.readUShort() - MapRegion.sceneBaseX shl 9
												hintArrow.y += BufferedConnectionContext.receivedBuffer.readUShort() - MapRegion.sceneBaseY shl 9
												hintArrow.height = BufferedConnectionContext.receivedBuffer.readUByte() shl 2
												hintArrow.distance = BufferedConnectionContext.receivedBuffer.readUShort()
											}
											hintArrow.modelId = BufferedConnectionContext.receivedBuffer.readUShort()
											if (hintArrow.modelId == 65535) {
												hintArrow.modelId = -1
											}
											PlayerUpdate.HINT_ARROWS[hintArrowSlot] = hintArrow
										}
										BufferedConnectionContext.currentPacket = null
										return true
									} else if (ServerProt.LOGOUT_LOBBY === BufferedConnectionContext.currentPacket) {
										ClientLogout.logout(LoginManager.currentlyInLobby)
										BufferedConnectionContext.currentPacket = null
										return false
									} else if (ServerProt.PLAYER_UPDATE === BufferedConnectionContext.currentPacket) {
										PlayerUpdate.decodePlayerUpdate(BufferedConnectionContext.receivedBuffer, BufferedConnectionContext.currentPacketSize)
										BufferedConnectionContext.currentPacket = null
										return true
									} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETCOLOR) {
										val colour: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
										val componentHash: Int = BufferedConnectionContext.receivedBuffer.readIntLE()
										PlayerVarDomain.requestVarVerifyid()
										PulseEvent.priorityIfColourUpdate(colour, componentHash)
										BufferedConnectionContext.currentPacket = null
										return true
									} else if (ServerProt.CREATE_GROUND_ITEM === BufferedConnectionContext.currentPacket) {
										UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.CREATE_GROUND_ITEM)
										BufferedConnectionContext.currentPacket = null
										return true
									} else if (BufferedConnectionContext.currentPacket === ServerProt.OUTDATED_INTERFACE_PULSE_EVENT) {
										val componentHash: Int = BufferedConnectionContext.receivedBuffer.readInt()
										val valueLow: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
										val valueHigh: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
										PlayerVarDomain.requestVarVerifyid()
										PulseEvent.priorityT13Update(componentHash, valueLow + (valueHigh shl 16))
										BufferedConnectionContext.currentPacket = null
										return true
									} else if (BufferedConnectionContext.currentPacket === ServerProt.CLEAR_VARPS) {
										PlayerVarDomain.playerVarProvider!!.reset()
										PlayerVarDomain.pulseEventConfigsPtr += 32
										BufferedConnectionContext.currentPacket = null
										return true
									} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETPLAYERMODEL) {
										val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
										PlayerVarDomain.requestVarVerifyid()
										PulseEvent.priorityIfcontentUpdate(5, componentHash, PlayerUpdate.myPlayerIndex, 0)
										BufferedConnectionContext.currentPacket = null
										return true
									} else if (ServerProt.STOP_VORBIS_STREAM === BufferedConnectionContext.currentPacket) {
										val vorbisFileId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
										PlayerVarDomain.requestVarVerifyid()
										VorbisStreamStop.stopVorbisStream(vorbisFileId)
										BufferedConnectionContext.currentPacket = null
										return true
									} else {
										@Pc(2518) var local2518: Boolean
										if (ServerProt.MESSAGE_PLAYER_GROUP === BufferedConnectionContext.currentPacket) {
											var hasDisplayName: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
											string5 = BufferedConnectionContext.receivedBuffer.readString()
											string = string5
											if (hasDisplayName) {
												string = BufferedConnectionContext.receivedBuffer.readString()
											}
											val playerRights: Int = BufferedConnectionContext.receivedBuffer.readUByte()
											var ignored: Boolean = false
											if (playerRights <= 1) {
												if (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly) {
													ignored = true
												} else if (playerRights <= 1 && IgnoreList.isIgnored(string)) {
													ignored = true
												}
											}
											if (!ignored && MapRegion.inTutorialIsland == 0) {
												string3 = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer)!!)
												if (playerRights == 2) {
													ChatLineWriter.writeMessage("<img=1>" + string, string3, -1, "<img=1>" + string5, null, 24, 0, string5)
												} else if (playerRights == 1) {
													ChatLineWriter.writeMessage("<img=0>" + string, string3, -1, "<img=0>" + string5, null, 24, 0, string5)
												} else {
													ChatLineWriter.writeMessage(string, string3, -1, string5, null, 24, 0, string5)
												}
											}
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (ServerProt.MAP_PROJANIM_HALFSQ === BufferedConnectionContext.currentPacket) {
											UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.MAP_PROJANIM_HALFSQ)
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (BufferedConnectionContext.currentPacket === ServerProt.CAM_MOVETO) {
											val targetLocalY: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
											val constantSpeed: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
											val targetLocalX: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
											val variableSpeed: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
											val moveZ: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd() shl 2
											PlayerVarDomain.requestVarVerifyid()
											Camera.moveCamera(constantSpeed, targetLocalX, true, moveZ, variableSpeed, targetLocalY)
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (BufferedConnectionContext.currentPacket === ServerProt.RUN_CS2_SCRIPT) {
											string8 = BufferedConnectionContext.receivedBuffer.readString()
											@Pc(2694) val objects: Array<Any?> = arrayOfNulls<Any>(string8!!.length + 1)
											for (argIndex in string8.length - 1 downTo 0) {
												if (string8[argIndex] == 's') {
													objects[argIndex + 1] = BufferedConnectionContext.receivedBuffer.readString()
												} else {
													objects[argIndex + 1] = (BufferedConnectionContext.receivedBuffer.readInt())
												}
											}
											objects[0] = (BufferedConnectionContext.receivedBuffer.readInt())
											PlayerVarDomain.requestVarVerifyid()
											@Pc(2746) val hookRequest: HookRequest = HookRequest()
											hookRequest.params = objects
											CS2Interpreter.executeHookInner(hookRequest)
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (ServerProt.CAM_SMOOTHRESET === BufferedConnectionContext.currentPacket) {
											PlayerVarDomain.requestVarVerifyid()
											Camera.startSmoothTransition()
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETNPCHEAD) {
											val componentHash: Int = BufferedConnectionContext.receivedBuffer.readIntLE()
											var npcId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
											if (npcId == 65535) {
												npcId = -1
											}
											PlayerVarDomain.requestVarVerifyid()
											PulseEvent.priorityIfcontentUpdate(2, componentHash, npcId, -1)
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_CLOSESUB) {
											val componentHash: Int = BufferedConnectionContext.receivedBuffer.readInt()
											PlayerVarDomain.requestVarVerifyid()
											@Pc(2815) val subInterface3: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(componentHash.toLong()) as SubInterface?
											if (subInterface3 != null) {
												SubInterfaceCloser.closeSubInterface(true, false, subInterface3)
											}
											if (InterfaceManager.activeDialogueContinue != null) {
												Component.redrawComponent(InterfaceManager.activeDialogueContinue)
												InterfaceManager.activeDialogueContinue = null
											}
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (ServerProt.IF_SETGRAPHIC === BufferedConnectionContext.currentPacket) {
											val spriteId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
											val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
											PlayerVarDomain.requestVarVerifyid()
											PulseEvent.priorityIfGraphicUpdate(componentHash, spriteId)
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (ServerProt.REMOVE_GROUND_ITEM === BufferedConnectionContext.currentPacket) {
											UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.REMOVE_GROUND_ITEM)
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (BufferedConnectionContext.currentPacket === ServerProt.IDENTIFY_HOST_NAME) {
											val ipAddress: Int = BufferedConnectionContext.receivedBuffer.readInt()
											LoginManager.lastLoginHostLookup = GameShell.signLink!!.lookupHostname(ipAddress)
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (BufferedConnectionContext.currentPacket === ServerProt.MESSAGE_PUBLIC) {
											var playerIndex: Int = BufferedConnectionContext.receivedBuffer.readUShort()
											@Pc(2895) var playerEntity2: PlayerEntity?
											if (playerIndex == PlayerUpdate.myPlayerIndex) {
												playerEntity2 = PlayerUpdate.localPlayer
											} else {
												playerEntity2 = PlayerUpdate.players[playerIndex]
											}
											if (playerEntity2 == null) {
												BufferedConnectionContext.currentPacket = null
												return true
											}
											var chatEffects: Int = BufferedConnectionContext.receivedBuffer.readUShort()
											val playerRights: Int = BufferedConnectionContext.receivedBuffer.readUByte()
											val isQuickChat: Boolean = chatEffects and 0x8000 != 0
											if (playerEntity2.displayName != null && playerEntity2.playerModel != null) {
												var ignored: Boolean = false
												if (playerRights <= 1) {
													if (!isQuickChat && (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly)) {
														ignored = true
													} else if (IgnoreList.isIgnored(playerEntity2.displayName)) {
														ignored = true
													}
												}
												if (!ignored && MapRegion.inTutorialIsland == 0) {
													var quickChatId: Int = -1
													if (isQuickChat) {
														chatEffects = chatEffects and 0x7FFF
														@Pc(2978) val quickChatPhrase: QuickChatPhrase? = QuickchatMessageTypeList.decodeQuickchatMessage(BufferedConnectionContext.receivedBuffer)
														quickChatId = quickChatPhrase!!.id
														string7 = quickChatPhrase.type!!.fillDynamicValues(BufferedConnectionContext.receivedBuffer)
													} else {
														string7 = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer)!!)
													}
													playerEntity2.hintDetails.text = string7.trim()
													playerEntity2.hintDetails.colorType = chatEffects shr 8
													playerEntity2.hintDetails.drawType = chatEffects and 0xFF
													playerEntity2.hintDetails.ticksLeft = 150
													var chatMessageType: Int
													if (playerRights == 1 || playerRights == 2) {
														chatMessageType = if (isQuickChat) 17 else 1
													} else {
														chatMessageType = if (isQuickChat) 17 else 2
													}
													if (playerRights == 2) {
														ChatLineWriter.writeMessage("<img=1>" + playerEntity2.getDisplayName(), string7, quickChatId, "<img=1>" + playerEntity2.getUsernameWithTitle(), null, chatMessageType, 0, playerEntity2.username)
													} else if (playerRights == 1) {
														ChatLineWriter.writeMessage("<img=0>" + playerEntity2.getDisplayName(), string7, quickChatId, "<img=0>" + playerEntity2.getUsernameWithTitle(), null, chatMessageType, 0, playerEntity2.username)
													} else {
														ChatLineWriter.writeMessage(playerEntity2.getDisplayName(), string7, quickChatId, playerEntity2.getUsernameWithTitle(), null, chatMessageType, 0, playerEntity2.username)
													}
												}
											}
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (ServerProt.MESSAGE_PRIVATE === BufferedConnectionContext.currentPacket) {
											var hasDisplayName: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
											string5 = BufferedConnectionContext.receivedBuffer.readString()
											string = string5
											if (hasDisplayName) {
												string = BufferedConnectionContext.receivedBuffer.readString()
											}
											val messageHashHigh: Long = BufferedConnectionContext.receivedBuffer.readUShort().toLong()
											val messageHashLow: Long = BufferedConnectionContext.receivedBuffer.readUMedium().toLong()
											val playerRights: Int = BufferedConnectionContext.receivedBuffer.readUByte()
											@Pc(3166) val messageHash: Long = (messageHashHigh shl 32) + messageHashLow
											var ignored: Boolean = false
											var hashIndex: Int = 0
											while (true) {
												if (hashIndex >= 100) {
													if (playerRights <= 1) {
														if (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly) {
															ignored = true
														} else if (IgnoreList.isIgnored(string)) {
															ignored = true
														}
													}
													break
												}
												if (Chat.CHAT_MESSAGE_UNIQUE_HASHES[hashIndex] == messageHash) {
													ignored = true
													break
												}
												hashIndex++
											}
											if (!ignored && MapRegion.inTutorialIsland == 0) {
												Chat.CHAT_MESSAGE_UNIQUE_HASHES[Camera.chatMessageHashIndex] = messageHash
												Camera.chatMessageHashIndex = (Camera.chatMessageHashIndex + 1) % 100
												@Pc(3231) val string9: String? = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer)!!)
												if (playerRights == 2) {
													ChatLineWriter.writeMessage("<img=1>" + string, string9, -1, "<img=1>" + string5, null, 7, 0, string5)
												} else if (playerRights == 1) {
													ChatLineWriter.writeMessage("<img=0>" + string, string9, -1, "<img=0>" + string5, null, 7, 0, string5)
												} else {
													ChatLineWriter.writeMessage(string, string9, -1, string5, null, 3, 0, string5)
												}
											}
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (ServerProt.TRIGGER_ONDIALOGABORT === BufferedConnectionContext.currentPacket) {
											if (ClientState.BASE_WINDOW_ID != -1) {
												Static683.runSubInterfaceHooks(ClientState.BASE_WINDOW_ID, 0)
											}
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (ServerProt.CAM_LOOKAT === BufferedConnectionContext.currentPacket) {
											val targetLocalX: Int = BufferedConnectionContext.receivedBuffer.readUByte()
											val targetLocalY: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
											val constantSpeed: Int = BufferedConnectionContext.receivedBuffer.readUByte()
											val heightOffset: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle() shl 2
											val variableSpeed: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
											PlayerVarDomain.requestVarVerifyid()
											Camera.lookAt(targetLocalY, targetLocalX, heightOffset, variableSpeed, constantSpeed)
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (BufferedConnectionContext.currentPacket === ServerProt.PING) {
											BufferedConnectionContext.currentPacket = null
											return false
										} else if (ServerProt.REFLECTION_CHECK === BufferedConnectionContext.currentPacket) {
											ReflectionCheckManager.requestReflectionCheck(BufferedConnectionContext.currentPacketSize, BufferedConnectionContext.receivedBuffer, GameShell.signLink)
											BufferedConnectionContext.currentPacket = null
											return true
										} else if (ServerProt.WORLD_LIST === BufferedConnectionContext.currentPacket) {
											val refresh: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
											@Pc(3394) val data: ByteArray = ByteArray(BufferedConnectionContext.currentPacketSize - 1)
											BufferedConnectionContext.receivedBuffer.readBytes(0, BufferedConnectionContext.currentPacketSize - 1, data)
											WorldConnection.decodeWorldList(refresh, data)
											BufferedConnectionContext.currentPacket = null
											return true
										} else {
											@Pc(3421) var local3421: Byte
											if (BufferedConnectionContext.currentPacket === ServerProt.SET_SONG_VOLUME) {
												val volume: Int = BufferedConnectionContext.receivedBuffer.readUByte()
												val channel: Byte = BufferedConnectionContext.receivedBuffer.readByteAdd()
												PlayerVarDomain.requestVarVerifyid()
												SongPlayer.setSongVolume(channel.toInt(), volume)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.UPDATE_ZONE_PARTIAL_ENCLOSED === BufferedConnectionContext.currentPacket) {
												UpdateZonePacket.updateZoneX = BufferedConnectionContext.receivedBuffer.readByteInverse().toInt() shl 3
												UpdateZonePacketDecoder.updateZonePlane = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
												UpdateZonePacket.updateZoneY = BufferedConnectionContext.receivedBuffer.readByteSubtract().toInt() shl 3
												while (BufferedConnectionContext.currentPacketSize > BufferedConnectionContext.receivedBuffer.currentPosition) {
													@Pc(3460) val subPacket: UpdateZonePacket? = UpdateZonePacket.updateZonePacketValues()[BufferedConnectionContext.receivedBuffer.readUByte()]
													UpdateZonePacketDecoder.decodeUpdateZonePacket(subPacket)
												}
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.IF_SETPOSITION === BufferedConnectionContext.currentPacket) {
												val positionY: Int = BufferedConnectionContext.receivedBuffer.readShort()
												val componentHash: Int = BufferedConnectionContext.receivedBuffer.readInt()
												val positionX: Int = BufferedConnectionContext.receivedBuffer.readShortAdd()
												PlayerVarDomain.requestVarVerifyid()
												PriorityIfPositionUpdate.priorityIfPositionUpdate(positionX, positionY, componentHash)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.CLIENT_SETVARCSTR_SMALL === BufferedConnectionContext.currentPacket) {
												string8 = BufferedConnectionContext.receivedBuffer.readString()
												val varcId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
												PlayerVarDomain.requestVarVerifyid()
												PulseEvent.priorityVarcstringUpdate(string8, varcId)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (BufferedConnectionContext.currentPacket === ShaderVariableType.IF_SETVIDEO) {
												val videoId: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
												val componentHash: Int = BufferedConnectionContext.receivedBuffer.readInt()
												PlayerVarDomain.requestVarVerifyid()
												PulseEvent.priorityIfVideoUpdate(videoId, componentHash)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.MESSAGE_QUICKCHAT_FRIENDS_CHAT === BufferedConnectionContext.currentPacket) {
												var hasDisplayName: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
												string5 = BufferedConnectionContext.receivedBuffer.readString()
												string = string5
												if (hasDisplayName) {
													string = BufferedConnectionContext.receivedBuffer.readString()
												}
												val encodedChannelName: Long = BufferedConnectionContext.receivedBuffer.readLong()
												val messageHashHigh: Long = BufferedConnectionContext.receivedBuffer.readUShort().toLong()
												hashLow = BufferedConnectionContext.receivedBuffer.readUMedium().toLong()
												val playerRights: Int = BufferedConnectionContext.receivedBuffer.readUByte()
												val phraseId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
												@Pc(3599) val messageHash: Long = (messageHashHigh shl 32) + hashLow
												@Pc(3601) var ignore: Boolean = false
												loopType = 0
												while (true) {
													if (loopType >= 100) {
														if (playerRights <= 1 && IgnoreList.isIgnored(string)) {
															ignore = true
														}
														break
													}
													if (messageHash == Chat.CHAT_MESSAGE_UNIQUE_HASHES[loopType]) {
														ignore = true
														break
													}
													loopType++
												}
												if (!ignore && MapRegion.inTutorialIsland == 0) {
													Chat.CHAT_MESSAGE_UNIQUE_HASHES[Camera.chatMessageHashIndex] = messageHash
													Camera.chatMessageHashIndex = (Camera.chatMessageHashIndex + 1) % 100
													@Pc(3658) val string10: String? = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId).fillDynamicValues(BufferedConnectionContext.receivedBuffer)
													if (playerRights == 2) {
														ChatLineWriter.writeMessage("<img=1>" + string, string10, phraseId, "<img=1>" + string5, Utils.decodeUidName(encodedChannelName), 20, 0, string5)
													} else if (playerRights == 1) {
														ChatLineWriter.writeMessage("<img=0>" + string, string10, phraseId, "<img=0>" + string5, Utils.decodeUidName(encodedChannelName), 20, 0, string5)
													} else {
														ChatLineWriter.writeMessage(string, string10, phraseId, string5, Utils.decodeUidName(encodedChannelName), 20, 0, string5)
													}
												}
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.SET_CAMERA_TARGET === BufferedConnectionContext.currentPacket) {
												val targetCoord: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
												PlayerVarDomain.requestVarVerifyid()
												if (targetCoord == -1) {
													Camera.roofRemovalTargetX = -1
													Camera.roofRemovalTargetY = -1
												} else {
													var roofLocalX: Int = (targetCoord shr 14) and 0x3FFF
													roofLocalX -= MapRegion.sceneBaseX
													var roofLocalY: Int = targetCoord and 0x3FFF
													roofLocalY -= MapRegion.sceneBaseY
													if (roofLocalX < 0) {
														roofLocalX = 0
													} else if (MapRegion.sceneSizeX <= roofLocalX) {
														roofLocalX = MapRegion.sceneSizeX
													}
													Camera.roofRemovalTargetX = (roofLocalX shl 9) + 256
													if (roofLocalY < 0) {
														roofLocalY = 0
													} else if (roofLocalY >= MapRegion.sceneSizeY) {
														roofLocalY = MapRegion.sceneSizeY
													}
													Camera.roofRemovalTargetY = (roofLocalY shl 9) + 256
												}
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (BufferedConnectionContext.currentPacket === ServerProt.VARBIT_SMALL) {
												val varbitValue: Int = BufferedConnectionContext.receivedBuffer.readUByteAdd()
												val varbitId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
												PlayerVarDomain.playerVarProvider!!.setVarBitValueFromServer(varbitId, varbitValue)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.SPOT_ANIM === BufferedConnectionContext.currentPacket) {
												UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.SPOT_ANIM)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (BufferedConnectionContext.currentPacket === ServerProt.OBJ_ANIM_SPECIFIC) {
												var animationId: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
												if (animationId == 65535) {
													animationId = -1
												}
												val shapeAndRotation: Int = BufferedConnectionContext.receivedBuffer.readUByteAdd()
												val objectShape: Int = shapeAndRotation shr 2
												val objectRotation: Int = shapeAndRotation and 0x3
												val objectLayer: Int = LocLayer.OBJECT_TYPE_SLOTS[objectShape]
												val objectCoord: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
												val plane: Int = (objectCoord shr 28) and 0x3
												var localX: Int = (objectCoord shr 14) and 0x3FFF
												var localY: Int = objectCoord and 0x3FFF
												localX -= MapRegion.sceneBaseX
												localY -= MapRegion.sceneBaseY
												Scene.animateObject(localY, animationId, objectShape, localX, objectRotation, plane, objectLayer)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (BufferedConnectionContext.currentPacket === ServerProt.MESSAGE_QUICKCHAT_PRIVATE_ECHO) {
												string8 = BufferedConnectionContext.receivedBuffer.readString()
												val phraseId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
												string = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId).fillDynamicValues(BufferedConnectionContext.receivedBuffer)
												ChatLineWriter.writeMessage(string8, string, phraseId, string8, null, 19, 0, string8)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (BufferedConnectionContext.currentPacket === ServerProt.GROUND_ITEM_REVEAL) {
												UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.GROUND_ITEM_REVEAL)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.RESET_ALL_ANIMATIONS === BufferedConnectionContext.currentPacket) {
												var playerIndex: Int = 0
												while (playerIndex < PlayerUpdate.players.size) {
													if (PlayerUpdate.players[playerIndex] != null) {
														PlayerUpdate.players[playerIndex]!!.currentAnimations = null
														PlayerUpdate.players[playerIndex]!!.currentSequence.sequenceId = -1
													}
													playerIndex++
												}
												var npcIndex: Int = 0
												while (npcIndex < NPCList.npcCount) {
													NPCList.NPC_ARRAY[npcIndex]!!.npc!!.currentAnimations = null
													NPCList.NPC_ARRAY[npcIndex]!!.npc!!.currentSequence.sequenceId = -1
													npcIndex++
												}
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.IF_SETANGLE === BufferedConnectionContext.currentPacket) {
												val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
												val rotationX: Int = BufferedConnectionContext.receivedBuffer.readUShort()
												val rotationY: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
												val modelZoom: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
												PlayerVarDomain.requestVarVerifyid()
												PulseEvent.priorityIfangleUpdate(modelZoom, rotationX, rotationY, componentHash)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.CHAT_FILTER_SETTINGS_PRIVATECHAT === BufferedConnectionContext.currentPacket) {
												ChatBox.privateFilter = ChatFilter.chatFilterForId(BufferedConnectionContext.receivedBuffer.readUByte())
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.TILE_MESSAGE === BufferedConnectionContext.currentPacket) {
												UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.TILE_MESSAGE)
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.UPDATE_IGNORE_LIST === BufferedConnectionContext.currentPacket) {
												IgnoreList.ignoreCount = BufferedConnectionContext.receivedBuffer.readUByte()
												var ignoreIndex: Int = 0
												while (ignoreIndex < IgnoreList.ignoreCount) {
													IgnoreList.ignoreNames[ignoreIndex] = BufferedConnectionContext.receivedBuffer.readString()
													IgnoreList.ignoreNamesUnfiltered[ignoreIndex] = BufferedConnectionContext.receivedBuffer.readString()
													if (IgnoreList.ignoreNamesUnfiltered[ignoreIndex]!!.equals("")) {
														IgnoreList.ignoreNamesUnfiltered[ignoreIndex] = IgnoreList.ignoreNames[ignoreIndex]
													}
													IgnoreList.ignorePreviousNames[ignoreIndex] = BufferedConnectionContext.receivedBuffer.readString()
													IgnoreList.ignorePreviousNamesUnfiltered[ignoreIndex] = BufferedConnectionContext.receivedBuffer.readString()
													if (IgnoreList.ignorePreviousNamesUnfiltered[ignoreIndex]!!.equals("")) {
														IgnoreList.ignorePreviousNamesUnfiltered[ignoreIndex] = IgnoreList.ignorePreviousNames[ignoreIndex]
													}
													IgnoreList.ignoreIsTemporary[ignoreIndex] = false
													ignoreIndex++
												}
												FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (BufferedConnectionContext.currentPacket === ServerProt.PLAYER_OPTION) {
												var cursor: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
												if (cursor == 65535) {
													cursor = -1
												}
												string5 = BufferedConnectionContext.receivedBuffer.readString()
												val optionSlot: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
												val optionPriority: Int = BufferedConnectionContext.receivedBuffer.readUByteAdd()
												if (optionSlot >= 1 && optionSlot <= 8) {
													if (string5!!.equals("null", ignoreCase = true)) {
														string5 = null
													}
													MiniMenu.PLAYER_OPTIONS[optionSlot - 1] = string5
													MiniMenu.PLAYER_OPTION_CURSORS[optionSlot - 1] = cursor
													MiniMenu.PLAYER_OPTION_REDUCED_PRIORITY[optionSlot - 1] = optionPriority == 0
												}
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (ServerProt.GAME_MESSAGE === BufferedConnectionContext.currentPacket) {
												val messageType: Int = BufferedConnectionContext.receivedBuffer.readUSmart()
												val effectFlags: Int = BufferedConnectionContext.receivedBuffer.readInt()
												val nameFlags: Int = BufferedConnectionContext.receivedBuffer.readUByte()
												string4 = ""
												string2 = string4
												if (nameFlags and 0x1 != 0) {
													string4 = BufferedConnectionContext.receivedBuffer.readString()
													if (nameFlags and 0x2 == 0) {
														string2 = string4
													} else {
														string2 = BufferedConnectionContext.receivedBuffer.readString()
													}
												}
												string3 = BufferedConnectionContext.receivedBuffer.readString()
												if (messageType == 99) {
													Console.printToConsole(string3)
												} else if (ClientFeatures.CONSOLE && messageType == 98) {
													// Not part of the original client: the server sets the console input, to complete it.
													Console.commandString = string3
													Console.commandLength = string3!!.length
												} else if (string2!!.equals("") || !IgnoreList.isIgnored(string2)) {
													ChatLineWriter.writeMessage(string2, messageType, effectFlags, string3, string4, string4)
												} else {
													BufferedConnectionContext.currentPacket = null
													return true
												}
												BufferedConnectionContext.currentPacket = null
												return true
											} else if (BufferedConnectionContext.currentPacket === ServerProt.UPDATE_GE_SLOT) {
												val geSlot: Int = BufferedConnectionContext.receivedBuffer.readUByte()
												if (BufferedConnectionContext.receivedBuffer.readUByte() == 0) {
													StockMarketOffer.GRAND_EXCHANGE_SLOTS[geSlot] = StockMarketOffer()
												} else {
													BufferedConnectionContext.receivedBuffer.currentPosition--
													StockMarketOffer.GRAND_EXCHANGE_SLOTS[geSlot] = StockMarketOffer(BufferedConnectionContext.receivedBuffer)
												}
												BufferedConnectionContext.currentPacket = null
												client.lastGrandExchangeUpdateTick = ClientState.serverTick
												return true
											} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETANIM) {
												var animationId: Int = BufferedConnectionContext.receivedBuffer.readShort()
												val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntMiddle()
												PlayerVarDomain.requestVarVerifyid()
												PulseEvent.priorityIfAnimUpdate(animationId, componentHash)
												BufferedConnectionContext.currentPacket = null
												return true
											} else {
												@Pc(4419) var serverActiveProperties: IFEvents?
												@Pc(4403) var serverActiveProperties2: IFEvents?
												if (BufferedConnectionContext.currentPacket === ServerProt.IF_RESETTARGETPARAM) {
													val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntMiddle()
													val targetParam: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													var toSlot: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													if (toSlot == 65535) {
														toSlot = -1
													}
													var fromSlot: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
													PlayerVarDomain.requestVarVerifyid()
													if (fromSlot == 65535) {
														fromSlot = -1
													}
													for (slot in fromSlot..toSlot) {
														val slotKey: Long = (componentHash.toLong() shl 32) + slot.toLong()
														serverActiveProperties2 = IFEvents.ICOMPONENT_SETTINGS_SLOTS.get(slotKey) as IFEvents?
														if (serverActiveProperties2 != null) {
															serverActiveProperties = IFEvents(serverActiveProperties2.settingsHash, targetParam)
															serverActiveProperties2.unlink()
														} else if (slot == -1) {
															serverActiveProperties = IFEvents(Interface.getComponent(componentHash)!!.targetParams!!.settingsHash, targetParam)
														} else {
															serverActiveProperties = IFEvents(0, targetParam)
														}
														IFEvents.ICOMPONENT_SETTINGS_SLOTS.put(slotKey, serverActiveProperties)
													}
													BufferedConnectionContext.currentPacket = null
													return true
												}
												@Pc(4486) var local4486: Boolean
												if (BufferedConnectionContext.currentPacket === ServerProt.FRIEND_STATUS) {
													@Pc(4520) var local4520: Boolean
													while (BufferedConnectionContext.receivedBuffer.currentPosition < BufferedConnectionContext.currentPacketSize) {
														val nameChanged: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
														string5 = BufferedConnectionContext.receivedBuffer.readString()
														string = BufferedConnectionContext.receivedBuffer.readString()
														val friendWorldId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
														val friendRank: Int = BufferedConnectionContext.receivedBuffer.readUByte()
														string3 = ""
														var sameGame: Boolean = false
														if (friendWorldId > 0) {
															string3 = BufferedConnectionContext.receivedBuffer.readString()
															sameGame = BufferedConnectionContext.receivedBuffer.readUByte() == 1
														}
														var friendIndex: Int = 0
														while (friendIndex < FriendList.FRIEND_COUNT) {
															if (nameChanged) {
																if (string!!.equals(FriendList.friendNames[friendIndex])) {
																	FriendList.friendNames[friendIndex] = string5
																	FriendList.friendPreviousNames[friendIndex] = string
																	string5 = null
																	break
																}
															} else if (string5!!.equals(FriendList.friendNames[friendIndex])) {
																if (friendWorldId != FriendList.friendWorldIds[friendIndex]) {
																	var queueNotification: Boolean = true
																	@Pc(4525) var friendNotification: LoginStateUpdate? = client.PLAYER_LOGINSTATE_UPDATES!!.getFirst() as LoginStateUpdate?
																	while (friendNotification != null) {
																		if (friendNotification.name!!.equals(string5)) {
																			if (friendWorldId != 0 && friendNotification.actionType.toInt() == 0) {
																				queueNotification = false
																				friendNotification.unlinkNode()
																			} else if (friendWorldId == 0 && friendNotification.actionType.toInt() != 0) {
																				queueNotification = false
																				friendNotification.unlinkNode()
																			}
																		}
																		friendNotification = client.PLAYER_LOGINSTATE_UPDATES!!.getNext() as LoginStateUpdate?
																	}
																	if (queueNotification) {
																		client.PLAYER_LOGINSTATE_UPDATES!!.offer(LoginStateUpdate(string5, friendWorldId))
																	}
																	FriendList.friendWorldIds[friendIndex] = friendWorldId
																}
																FriendList.friendPreviousNames[friendIndex] = string
																FriendList.friendWorldNames[friendIndex] = string3
																FriendList.friendRanks[friendIndex] = friendRank
																string5 = null
																FriendList.friendSameGame[friendIndex] = sameGame
																break
															}
															friendIndex++
														}
														if (string5 != null && FriendList.FRIEND_COUNT < 200) {
															FriendList.friendNames[FriendList.FRIEND_COUNT] = string5
															FriendList.friendPreviousNames[FriendList.FRIEND_COUNT] = string
															FriendList.friendWorldIds[FriendList.FRIEND_COUNT] = friendWorldId
															FriendList.friendWorldNames[FriendList.FRIEND_COUNT] = string3
															FriendList.friendRanks[FriendList.FRIEND_COUNT] = friendRank
															FriendList.friendSameGame[FriendList.FRIEND_COUNT] = sameGame
															FriendList.FRIEND_COUNT++
														}
													}
													FriendList.friendListStatus = 2
													FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick
													var sortEnd: Int = FriendList.FRIEND_COUNT
													while (sortEnd > 0) {
														sortEnd--
														var sorted: Boolean = true
														for (friendSortIndex in 0 until sortEnd) {
															if (TcpPacket.CURRENT_REQUEST!!.worldId != FriendList.friendWorldIds[friendSortIndex] && TcpPacket.CURRENT_REQUEST!!.worldId == FriendList.friendWorldIds[friendSortIndex + 1] || FriendList.friendWorldIds[friendSortIndex] == 0 && FriendList.friendWorldIds[friendSortIndex + 1] != 0) {
																val swapWorldId: Int = FriendList.friendWorldIds[friendSortIndex]
																FriendList.friendWorldIds[friendSortIndex] = FriendList.friendWorldIds[friendSortIndex + 1]
																FriendList.friendWorldIds[friendSortIndex + 1] = swapWorldId
																string2 = FriendList.friendWorldNames[friendSortIndex]
																FriendList.friendWorldNames[friendSortIndex] = FriendList.friendWorldNames[friendSortIndex + 1]
																FriendList.friendWorldNames[friendSortIndex + 1] = string2
																string3 = FriendList.friendNames[friendSortIndex]
																FriendList.friendNames[friendSortIndex] = FriendList.friendNames[friendSortIndex + 1]
																FriendList.friendNames[friendSortIndex + 1] = string3
																string7 = FriendList.friendPreviousNames[friendSortIndex]
																FriendList.friendPreviousNames[friendSortIndex] = FriendList.friendPreviousNames[friendSortIndex + 1]
																FriendList.friendPreviousNames[friendSortIndex + 1] = string7
																val swapRank: Int = FriendList.friendRanks[friendSortIndex]
																FriendList.friendRanks[friendSortIndex] = FriendList.friendRanks[friendSortIndex + 1]
																FriendList.friendRanks[friendSortIndex + 1] = swapRank
																val swapSameGame: Boolean = FriendList.friendSameGame[friendSortIndex]
																FriendList.friendSameGame[friendSortIndex] = FriendList.friendSameGame[friendSortIndex + 1]
																sorted = false
																FriendList.friendSameGame[friendSortIndex + 1] = swapSameGame
															}
														}
														if (sorted) {
															break
														}
													}
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.OPEN_URL === BufferedConnectionContext.currentPacket) {
													if (WindowMode.fullScreenFrame != null) {
														WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid!!.fixedScreen!!.getValue()), -1, false, -1)
													}
													@Pc(4856) val urlBytes: ByteArray = ByteArray(BufferedConnectionContext.currentPacketSize)
													BufferedConnectionContext.receivedBuffer.readEncryptedBytes(BufferedConnectionContext.currentPacketSize, urlBytes)
													string5 = Utils.readString(urlBytes, BufferedConnectionContext.currentPacketSize, 0)
													ClientURLTools.openUrl(string5, GameShell.signLink, GamePreferences.uid!!.toolkit!!.getValue() == 1, true)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.SOUND_SYNTH === BufferedConnectionContext.currentPacket) {
													var soundId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													if (soundId == 65535) {
														soundId = -1
													}
													val loopCount: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													val delay: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													val volume: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													val rate: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													AreaSoundPlayer.playSoundSynth(volume, soundId, loopCount, delay, rate)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.VARP_SMALL) {
													val varpId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													val varpValue: Byte = BufferedConnectionContext.receivedBuffer.readByte()
													PlayerVarDomain.playerVarProvider!!.updateVarp(varpValue.toInt(), varpId)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETTEXTFONT) {
													var fontId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
													if (fontId == 65535) {
														fontId = -1
													}
													val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
													PlayerVarDomain.requestVarVerifyid()
													PulseEvent.priorityIfFontUpdate(fontId, componentHash)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.OBJECT_PREFETCH) {
													UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.OBJECT_PREFETCH)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.VORBIS_SOUND) {
													var soundId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													if (soundId == 65535) {
														soundId = -1
													}
													val loopCount: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													val delay: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													val volume: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													val rate: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													SoundManager.playSoundVorbis(loopCount, rate, false, volume, delay, soundId)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.REGION) {
													StaticRegionInit.initializeRegion()
													BufferedConnectionContext.currentPacket = null
													return false
												} else if (BufferedConnectionContext.currentPacket === ServerProt.VORBIS_PRELOAD_SOUND) {
													var soundId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
													val volume: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													PlayerVarDomain.requestVarVerifyid()
													VorbisStreamPreload.preloadVorbisSound(volume, true, soundId)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.MESSAGE_QUICKCHAT_PRIVATE === BufferedConnectionContext.currentPacket) {
													var hasDisplayName: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
													string5 = BufferedConnectionContext.receivedBuffer.readString()
													string = string5
													if (hasDisplayName) {
														string = BufferedConnectionContext.receivedBuffer.readString()
													}
													val messageHashHigh: Long = BufferedConnectionContext.receivedBuffer.readUShort().toLong()
													val messageHashLow: Long = BufferedConnectionContext.receivedBuffer.readUMedium().toLong()
													val playerRights: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													val phraseId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													@Pc(5108) val messageHash: Long = messageHashLow + (messageHashHigh shl 32)
													var ignored: Boolean = false
													var hashIndex: Int = 0
													while (true) {
														if (hashIndex >= 100) {
															if (playerRights <= 1 && IgnoreList.isIgnored(string)) {
																ignored = true
															}
															break
														}
														if (messageHash == Chat.CHAT_MESSAGE_UNIQUE_HASHES[hashIndex]) {
															ignored = true
															break
														}
														hashIndex++
													}
													if (!ignored && MapRegion.inTutorialIsland == 0) {
														Chat.CHAT_MESSAGE_UNIQUE_HASHES[Camera.chatMessageHashIndex] = messageHash
														Camera.chatMessageHashIndex = (Camera.chatMessageHashIndex + 1) % 100
														@Pc(5161) val string11: String? = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId).fillDynamicValues(BufferedConnectionContext.receivedBuffer)
														if (playerRights == 2) {
															ChatLineWriter.writeMessage("<img=1>" + string, string11, phraseId, "<img=1>" + string5, null, 18, 0, string5)
														} else if (playerRights == 1) {
															ChatLineWriter.writeMessage("<img=0>" + string, string11, phraseId, "<img=0>" + string5, null, 18, 0, string5)
														} else {
															ChatLineWriter.writeMessage(string, string11, phraseId, string5, null, 18, 0, string5)
														}
													}
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_OPENSUB) {
													val interfaceId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
													val componentHash: Int = BufferedConnectionContext.receivedBuffer.readIntLE()
													val openType: Int = BufferedConnectionContext.receivedBuffer.readUByteAdd()
													PlayerVarDomain.requestVarVerifyid()
													subInterface = InterfaceManager.OPEN_INTERFACES.get(componentHash.toLong()) as SubInterface?
													if (subInterface != null) {
														SubInterfaceCloser.closeSubInterface(subInterface.interfaceId != interfaceId, false, subInterface)
													}
													SubInterfaceOpener.openSubInterface(interfaceId, componentHash, SubInterfaceType.fromId(openType), false)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.FRIENDLIST_LOADED) {
													BufferedConnectionContext.currentPacket = null
													FriendList.friendListStatus = 1
													FriendList.lastFriendIgnoreUpdateTick = ClientState.serverTick
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.CAM_RESET) {
													PlayerVarDomain.requestVarVerifyid()
													Camera.reset()
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.DEBUG_SERVER_TRIGGERS === BufferedConnectionContext.currentPacket) {
													val interfaceId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													val startComponent: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													val endComponent: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													PlayerVarDomain.requestVarVerifyid()
													if (Interface.interfaces!![interfaceId] != null) {
														for (componentId in startComponent until endComponent) {
															val serverTriggers: Int = BufferedConnectionContext.receivedBuffer.readUMedium()
															if (componentId < Interface.interfaces!![interfaceId]!!.size && Interface.interfaces!![interfaceId]!![componentId] != null) {
																Interface.interfaces!![interfaceId]!![componentId]!!.serverTriggers = serverTriggers
															}
														}
													}
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.OBJ_ANIM) {
													UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.OBJ_ANIM)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (UpdateZoneFullFollowsProt.UPDATE_ZONE_FULL_FOLLOWS === BufferedConnectionContext.currentPacket) {
													UpdateZonePacketDecoder.updateZonePlane = BufferedConnectionContext.receivedBuffer.readUByteAdd()
													UpdateZonePacket.updateZoneY = BufferedConnectionContext.receivedBuffer.readByteInverse().toInt() shl 3
													UpdateZonePacket.updateZoneX = BufferedConnectionContext.receivedBuffer.readByteInverse().toInt() shl 3
													@Pc(5400) var objStack: ObjStack? = ObjStack.GROUND_ITEMS.first() as ObjStack?
													while (objStack != null) {
														val stackPlane: Int = ((objStack.key shr 28) and 0x3L).toInt()
														val absX: Int = (objStack.key and 0x3FFFL).toInt()
														var localX: Int = absX - MapRegion.sceneBaseX
														val absY: Int = ((objStack.key shr 14) and 0x3FFFL).toInt()
														var localY: Int = absY - MapRegion.sceneBaseY
														if (stackPlane == UpdateZonePacketDecoder.updateZonePlane && localX >= UpdateZonePacket.updateZoneX && localX < UpdateZonePacket.updateZoneX + 8 && localY >= UpdateZonePacket.updateZoneY && UpdateZonePacket.updateZoneY + 8 > localY) {
															objStack.unlink()
															if (localX >= 0 && localY >= 0 && localX < MapRegion.sceneSizeX && MapRegion.sceneSizeY > localY) {
																ObjStackEntity.refreshObjStack(localY, localX, UpdateZonePacketDecoder.updateZonePlane)
															}
														}
														objStack = ObjStack.GROUND_ITEMS.next() as ObjStack?
													}
													@Pc(5505) var changeLocationRequest: ChangeLocationRequest? = LocAction.pendingRemovals!!.first() as ChangeLocationRequest?
													while (changeLocationRequest != null) {
														if (changeLocationRequest.x >= UpdateZonePacket.updateZoneX && UpdateZonePacket.updateZoneX + 8 > changeLocationRequest.x && UpdateZonePacket.updateZoneY <= changeLocationRequest.y && changeLocationRequest.y < UpdateZonePacket.updateZoneY + 8 && changeLocationRequest.plane == UpdateZonePacketDecoder.updateZonePlane) {
															changeLocationRequest.revertDelay = 0
														}
														changeLocationRequest = LocAction.pendingRemovals!!.next() as ChangeLocationRequest?
													}
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.ANIMATE_NPC) {
													val speed: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
													@Pc(5567) val animationIds: IntArray = IntArray(4)
													for (animIndex in 0 until 4) {
														animationIds[animIndex] = BufferedConnectionContext.receivedBuffer.readUShort()
													}
													var npcIndex: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
													@Pc(5592) val npcEntityNode2: NPCEntityNode? = NPCList.NPC_MAP.get(npcIndex.toLong()) as NPCEntityNode?
													if (npcEntityNode2 != null) {
														NPCEntity.animateNpc(speed, animationIds, npcEntityNode2.npc)
													}
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.BLOCK_MINIMAP_STATE) {
													ComponentMinimap.minimapState = BufferedConnectionContext.receivedBuffer.readUByte()
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (MusicTrackProt.MUSIC_TRACK === BufferedConnectionContext.currentPacket) {
													val fadeDuration: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
													val volume: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
													var musicTrackId: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
													if (musicTrackId == 65535) {
														musicTrackId = -1
													}
													SongPlayer.playSong(volume, musicTrackId, fadeDuration)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.MUSIC_EFFECT === BufferedConnectionContext.currentPacket) {
													val delay: Int = BufferedConnectionContext.receivedBuffer.readUMediumLittle()
													var jingleId: Int = BufferedConnectionContext.receivedBuffer.readUShortAddLittle()
													if (jingleId == 65535) {
														jingleId = -1
													}
													val volume: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
													PlayMusic.playJingle(jingleId, volume, delay)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.MESSAGE_PRIVATE_ECHO === BufferedConnectionContext.currentPacket) {
													string8 = BufferedConnectionContext.receivedBuffer.readString()
													string5 = Utils.escapeTags(HuffmanList.decodeHuffmanString(BufferedConnectionContext.receivedBuffer)!!)
													ChatLineWriter.writeMessage(string8, 6, 0, string5, string8, string8)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETTEXT) {
													val componentHash: Int = BufferedConnectionContext.receivedBuffer.readIntLE()
													string5 = BufferedConnectionContext.receivedBuffer.readString()
													PlayerVarDomain.requestVarVerifyid()
													PulseEvent.priorityIfTextUpdate(componentHash, string5)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETPLAYERMODEL_OTHER) {
													val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntMiddle()
													val secondaryModelId: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
													val primaryModelId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
													PlayerVarDomain.requestVarVerifyid()
													PulseEvent.priorityIfcontentUpdate(5, componentHash, primaryModelId, secondaryModelId)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.SHOW_FACE_HERE === BufferedConnectionContext.currentPacket) {
													val showFaceHere: Boolean = BufferedConnectionContext.receivedBuffer.readUByteInverse() == 1
													PlayerVarDomain.requestVarVerifyid()
													MiniMenu.showFaceHere = showFaceHere
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.RESET_SOUNDS) {
													SoundManager.resetSounds()
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.CHAT_FILTER_SETTINGS === BufferedConnectionContext.currentPacket) {
													ChatBox.publicFilter = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
													ChatBox.tradeFilter = BufferedConnectionContext.receivedBuffer.readUByteAdd()
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_OPENTOP) {
													val openType: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
													val topInterfaceId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
													PlayerVarDomain.requestVarVerifyid()
													if (openType == 2) {
														Static671.closeWorldMap()
													}
													ClientState.BASE_WINDOW_ID = topInterfaceId
													Interface.resetComponentSequences(topInterfaceId)
													WindowMode.interfaceResize(false)
													CS2Interpreter.runInterfaceOnLoadScripts(ClientState.BASE_WINDOW_ID)
													for (componentIndex in 0 until 100) {
														InterfaceRectangles.IS_COMPONENT_ON_TOP[componentIndex] = true
													}
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.UPDATE_REBOOT_TIMER) {
													if (MainLogicManager.inLobby(ClientState.mainLogicStep)) {
														client.rebootTimer = (BufferedConnectionContext.receivedBuffer.readUShort().toFloat() * 2.5f).toInt()
													} else {
														client.rebootTimer = BufferedConnectionContext.receivedBuffer.readUShort() * 30
													}
													InterfaceManager.lastMiscUpdateTick = ClientState.serverTick
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.VORBIS_SPEECH_SOUND) {
													var soundId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													if (soundId == 65535) {
														soundId = -1
													}
													val loops: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													val delay: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													val volume: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													SoundManager.playSoundVorbis(loops, 256, true, volume, delay, soundId)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.IF_SETMODEL === BufferedConnectionContext.currentPacket) {
													var modelId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
													if (modelId == 65535) {
														modelId = -1
													}
													val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntMiddle()
													PlayerVarDomain.requestVarVerifyid()
													PulseEvent.priorityIfcontentUpdate(1, componentHash, modelId, -1)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.SOUND_AREA) {
													UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.SOUND_AREA)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.IF_SETTARGETPARAM) {
													var toSlot: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
													if (toSlot == 65535) {
														toSlot = -1
													}
													var fromSlot: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
													if (fromSlot == 65535) {
														fromSlot = -1
													}
													val componentHash: Int = BufferedConnectionContext.receivedBuffer.readInt()
													val settingsHash: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
													PlayerVarDomain.requestVarVerifyid()
													for (slot in fromSlot..toSlot) {
														val slotKey: Long = slot.toLong() + (componentHash.toLong() shl 32)
														serverActiveProperties2 = IFEvents.ICOMPONENT_SETTINGS_SLOTS.get(slotKey) as IFEvents?
														if (serverActiveProperties2 != null) {
															serverActiveProperties = IFEvents(settingsHash, serverActiveProperties2.interfaceId)
															serverActiveProperties2.unlink()
														} else if (slot == -1) {
															serverActiveProperties = IFEvents(settingsHash, Interface.getComponent(componentHash)!!.targetParams!!.interfaceId)
														} else {
															serverActiveProperties = IFEvents(settingsHash, -1)
														}
														IFEvents.ICOMPONENT_SETTINGS_SLOTS.put(slotKey, serverActiveProperties)
													}
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.VARBIT_LARGE) {
													val varbitId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
													val varbitValue: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
													PlayerVarDomain.playerVarProvider!!.setVarBitValueFromServer(varbitId, varbitValue)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.UPDATE_UID192) {
													BufferedConnectionContext.receivedBuffer.currentPosition += 28
													if (BufferedConnectionContext.receivedBuffer.checkCrc()) {
														GameShell.updateRandomDatUID192(BufferedConnectionContext.receivedBuffer.currentPosition - 28, BufferedConnectionContext.receivedBuffer)
													}
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.RUN_ENERGY) {
													ClientState.runEnergy = BufferedConnectionContext.receivedBuffer.readUByte()
													InterfaceManager.lastMiscUpdateTick = ClientState.serverTick
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.UPDATE_STAT === BufferedConnectionContext.currentPacket) {
													val level: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
													val skillId: Int = BufferedConnectionContext.receivedBuffer.readUByteInverse()
													val xp: Int = BufferedConnectionContext.receivedBuffer.readUIntMiddle()
													XpDrops.statUpdated(skillId, client.statXp!![skillId], xp) // Not part of the original client: see XpDrops.
													client.statXp!![skillId] = xp
													client.statLevels!![skillId] = level
													StatBaseLevels.statBaseLevels[skillId] = 1
													val maxLevel: Int = StatBaseLevels.SKILL_MAX_LEVEL[skillId] - 1
													for (levelIndex in 0 until maxLevel) {
														if (StatBaseLevels.SKILL_XP_FOR_LEVEL[levelIndex] <= xp) {
															StatBaseLevels.statBaseLevels[skillId] = levelIndex + 2
														}
													}
													SkillUpdateCycle.recentSkillUpdates[SkillUpdateCycle.skillUpdateCycle++ and 0x1F] = skillId
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.MAP_PROJANIM === BufferedConnectionContext.currentPacket) {
													UpdateZonePacketDecoder.decodeUpdateZonePacket(UpdateZonePacket.MAP_PROJANIM)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.UPDATE_INV_STOP_TRANSMIT === BufferedConnectionContext.currentPacket) {
													val containerId: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
													val flags: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
													val negativeKey: Boolean = flags and 0x1 == 1
													ItemContainer.deleteItemContainer(negativeKey, containerId)
													ItemContainer.RECENTLY_USED_ITEM_CONTAINERS[client.recentlyUsedItemContainersCursorPos++ and 0x1F] = containerId
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.UPDATE_INV_PARTIAL === BufferedConnectionContext.currentPacket) {
													val containerId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													val flags: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													val negativeKey: Boolean = flags and 0x1 == 1
													while (BufferedConnectionContext.receivedBuffer.currentPosition < BufferedConnectionContext.currentPacketSize) {
														var slot: Int = BufferedConnectionContext.receivedBuffer.readUSmart()
														var itemId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
														var itemAmount: Int = 0
														if (itemId != 0) {
															itemAmount = BufferedConnectionContext.receivedBuffer.readUByte()
															if (itemAmount == 255) {
																itemAmount = BufferedConnectionContext.receivedBuffer.readInt()
															}
														}
														ItemContainer.setItemInContainer(containerId, negativeKey, slot, itemAmount, itemId - 1)
													}
													ItemContainer.RECENTLY_USED_ITEM_CONTAINERS[client.recentlyUsedItemContainersCursorPos++ and 0x1F] = containerId
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.MINIMAP_FLAG === BufferedConnectionContext.currentPacket) {
													var flagX: Int = BufferedConnectionContext.receivedBuffer.readUByteSubtract()
													var flagY: Int = BufferedConnectionContext.receivedBuffer.readUByteAdd()
													if (flagX == 255) {
														flagY = -1
														flagX = -1
													}
													PulseEvent.priorityMinimapFlagUpdate(flagY, flagX)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.CLIENT_SETVARC_LARGE === BufferedConnectionContext.currentPacket) {
													val value: Int = BufferedConnectionContext.receivedBuffer.readIntLE()
													val varcId: Int = BufferedConnectionContext.receivedBuffer.readUShortLittle()
													PlayerVarDomain.requestVarVerifyid()
													PulseEvent.priorityClientvarcUpdate(varcId, value)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.NPC_UPDATE === BufferedConnectionContext.currentPacket) {
													NpcUpdate.decodeNpcUpdate()
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.MESSAGE_QUICKCHAT_PLAYER_GROUP === BufferedConnectionContext.currentPacket) {
													var hasDisplayName: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
													string5 = BufferedConnectionContext.receivedBuffer.readString()
													string = string5
													if (hasDisplayName) {
														string = BufferedConnectionContext.receivedBuffer.readString()
													}
													val playerRights: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													val phraseId: Int = BufferedConnectionContext.receivedBuffer.readUShort()
													var ignored: Boolean = false
													if (playerRights <= 1 && IgnoreList.isIgnored(string)) {
														ignored = true
													}
													if (!ignored && MapRegion.inTutorialIsland == 0) {
														string7 = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId).fillDynamicValues(BufferedConnectionContext.receivedBuffer)
														if (playerRights == 2) {
															ChatLineWriter.writeMessage("<img=1>" + string, string7, phraseId, "<img=1>" + string5, null, 25, 0, string5)
														} else if (playerRights == 1) {
															ChatLineWriter.writeMessage("<img=0>" + string, string7, phraseId, "<img=0>" + string5, null, 25, 0, string5)
														} else {
															ChatLineWriter.writeMessage(string, string7, phraseId, string5, null, 25, 0, string5)
														}
													}
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (ServerProt.IF_SETSCROLLPOS === BufferedConnectionContext.currentPacket) {
													val componentHash: Int = BufferedConnectionContext.receivedBuffer.readUIntInverseMiddle()
													val scrollPos: Int = BufferedConnectionContext.receivedBuffer.readUShortAdd()
													PlayerVarDomain.requestVarVerifyid()
													PulseEvent.priorityIfscrollposUpdate(componentHash, scrollPos)
													BufferedConnectionContext.currentPacket = null
													return true
												} else if (BufferedConnectionContext.currentPacket === ServerProt.FRIENDS_CHAT_CHANNEL) {
													FriendsChat.lastFriendsChatUpdateTick = ClientState.serverTick
													if (BufferedConnectionContext.currentPacketSize == 0) {
														FriendsChat.clanChatUserCount = 0
														FriendsChat.friendsChatName = null
														FriendsChat.clanChatUsers = null
														FriendsChat.clanChatOwnerName = null
														BufferedConnectionContext.currentPacket = null
														return true
													}
													FriendsChat.clanChatOwnerName = BufferedConnectionContext.receivedBuffer.readString()
													var hasDisplayName: Boolean = BufferedConnectionContext.receivedBuffer.readUByte() == 1
													if (hasDisplayName) {
														BufferedConnectionContext.receivedBuffer.readString()
													}
													@Pc(6550) val encodedName: Long = BufferedConnectionContext.receivedBuffer.readLong()
													FriendsChat.friendsChatName = Utils.nameForLong(encodedName)
													FriendsChat.friendChatMinKickRank = BufferedConnectionContext.receivedBuffer.readByte()
													val userCount: Int = BufferedConnectionContext.receivedBuffer.readUByte()
													if (userCount == 255) {
														BufferedConnectionContext.currentPacket = null
														return true
													}
													FriendsChat.clanChatUserCount = userCount
													@Pc(6574) val friendsChatPlayers: Array<FriendsChatPlayer?> = arrayOfNulls<FriendsChatPlayer>(100)
													var userIndex: Int = 0
													while (userIndex < FriendsChat.clanChatUserCount) {
														friendsChatPlayers[userIndex] = FriendsChatPlayer()
														friendsChatPlayers[userIndex]!!.displayName = BufferedConnectionContext.receivedBuffer.readString()
														hasDisplayName = BufferedConnectionContext.receivedBuffer.readUByte() == 1
														if (hasDisplayName) {
															friendsChatPlayers[userIndex]!!.username = BufferedConnectionContext.receivedBuffer.readString()
														} else {
															friendsChatPlayers[userIndex]!!.username = friendsChatPlayers[userIndex]!!.displayName
														}
														friendsChatPlayers[userIndex]!!.textName = Utils.formatDisplayName(friendsChatPlayers[userIndex]!!.username)
														friendsChatPlayers[userIndex]!!.worldId = BufferedConnectionContext.receivedBuffer.readUShort()
														friendsChatPlayers[userIndex]!!.rank = BufferedConnectionContext.receivedBuffer.readByte()
														friendsChatPlayers[userIndex]!!.worldName = BufferedConnectionContext.receivedBuffer.readString()
														if (friendsChatPlayers[userIndex]!!.username!!.equals(PlayerUpdate.localPlayer!!.displayName)) {
															FriendsChat.myFriendsChatRank = friendsChatPlayers[userIndex]!!.rank
														}
														userIndex++
													}
													var unsortedCount: Int = FriendsChat.clanChatUserCount
													while (unsortedCount > 0) {
														unsortedCount--
														var sorted: Boolean = true
														for (userSortIndex in 0 until unsortedCount) {
															if (friendsChatPlayers[userSortIndex]!!.textName!!.compareTo(friendsChatPlayers[userSortIndex + 1]!!.textName!!) > 0) {
																fcPlayer = friendsChatPlayers[userSortIndex]
																friendsChatPlayers[userSortIndex] = friendsChatPlayers[userSortIndex + 1]
																friendsChatPlayers[userSortIndex + 1] = fcPlayer
																sorted = false
															}
														}
														if (sorted) {
															break
														}
													}
													FriendsChat.clanChatUsers = friendsChatPlayers
													BufferedConnectionContext.currentPacket = null
													return true
												} else {
													ExplicitRuntimeException.reportError("T1 - " + (if (BufferedConnectionContext.currentPacket == null) -1 else BufferedConnectionContext.currentPacket!!.getOpcode()) + "," + (if (BufferedConnectionContext.secondLastPacket == null) -1 else BufferedConnectionContext.secondLastPacket!!.getOpcode()) + "," + (if (ThirdLastPacket.thirdLastPacket == null) -1 else ThirdLastPacket.thirdLastPacket!!.getOpcode()) + " - " + BufferedConnectionContext.currentPacketSize, null)
													ClientLogout.logout(false)
													return true
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
