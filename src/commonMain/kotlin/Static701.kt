import com.jagex.game.runetek5.WorldConnectionInfo
import com.jagex.game.runetek5.client.login.LoginStep
import com.jagex.game.runetek5.client.window.WindowModeType
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.DraggedComponentLayer
import com.jagex.game.runetek5.EmailStatus
import com.jagex.game.runetek5.core.constants.LoginResponseCode
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.tools.Isaac
import com.jagex.game.runetek5.buffer.BitPacket
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.invtype.LoginDisallowReason
import com.jagex.game.runetek5.sign.SignedResourceStatus
import com.jagex.game.runetek5.statics.BrowserJavaScript
import io.IOException
import net.Socket
import net.URL
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.systeminfo.SystemInfo
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.network.packet.ServerProt
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.ModeWhere
import com.jagex.game.runetek5.client.login.LoginProt
import com.jagex.game.runetek5.network.AsyncConnection
import com.jagex.game.runetek5.network.WorldConnection
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.LobbyManager
import com.jagex.game.runetek5.NativeLibraryOwner
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.network.Connection
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.InterfaceDrag
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import kotlin.jvm.JvmStatic

object Static701 {

	@OriginalMember(owner = "client!lh", name = "a", descriptor = "(Z)V")
	@JvmStatic
	fun method5594() {
		DraggedComponentLayer.components = null
		if (OrthoMode.orthoEnabled && WindowMode.getWindowMode() !== WindowModeType.FIXED) {
			ClientState.drawGameArea(ClientState.mainLogicStep === MainLogicStep.STEP_LOGIN_SCREEN || ClientState.mainLogicStep === MainLogicStep.STEP_LOBBY_SCREEN, OrthoMode.getOrthoSurfaceWidth(), 0, OrthoMode.getOrthoSurfaceHeight(), 0)
		}
		@Pc(34) var offsetX: Int = 0
		@Pc(42) var offsetY: Int = 0
		if (OrthoMode.orthoEnabled) {
			offsetX = OrthoMode.getOrthoOffsetX()
			offsetY = OrthoMode.getOrthoDrawOffsetY()
		}
		InterfaceRootDraw.drawInterface(offsetY, ClientState.BASE_WINDOW_ID, offsetY, -1, offsetX, offsetX + GameShell.screenCanvasWidth, offsetY - -GameShell.screenCanvasHeight, offsetX)
		if (DraggedComponentLayer.components != null) {
			InterfaceComponentDraw.drawComponents(offsetX, InterfaceDrag.draggedComponentOffsetY, true, InterfaceDrag.draggedComponentOffsetX, offsetY, InterfaceDrag.aClass24_7!!.cacheReference, offsetX + GameShell.screenCanvasWidth, DraggedComponentLayer.components, -1412584499, offsetY + GameShell.screenCanvasHeight)
			DraggedComponentLayer.components = null
		}
	}

	@OriginalMember(owner = "client!lh", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun method5595() {
		if (LoginManager.loginStep === LoginStep.IDLE || LoginManager.loginStep === LoginStep.SHOWING_ADVERTISEMENT) {
			return
		}
		try {
			@Pc(17) val timeout: Short
			if (LoginManager.connectionAttempts == 0) {
				timeout = 250
			} else {
				timeout = 2000
			}
			if (timeout < ++LoginManager.delay) {
				if (Connection.connection != null) {
					Connection.connection!!.close()
					Connection.connection = null
				}
				if (LoginManager.connectionAttempts >= 3) {
					LoginManager.loginStep = LoginStep.IDLE
					LoginManager.setLoginResponse(-5)
					return
				}
				if (LoginManager.lobbyStage == 2) {
					TcpPacket.CURRENT_REQUEST!!.cycleConnectionMode()
				} else {
					LoginManager.CURRENT_CONNECTION_CONTEXT!!.cycleConnectionMode()
				}
				LoginManager.connectionAttempts++
				LoginManager.loginStep = LoginStep.CONNECT
				LoginManager.delay = 0
			}
			if (LoginManager.loginStep === LoginStep.CONNECT) {
				if (LoginManager.lobbyStage == 2) {
					LoginManager.aClass240_3 = TcpPacket.CURRENT_REQUEST!!.openSocket(GameShell.signLink)
				} else {
					LoginManager.aClass240_3 = LoginManager.CURRENT_CONNECTION_CONTEXT!!.openSocket(GameShell.signLink)
				}
				LoginManager.loginStep = LoginStep.WAIT_FOR_CONNECTION_ACK
			}
			@Pc(117) var tcpPacket: TcpPacket?
			if (LoginManager.loginStep === LoginStep.WAIT_FOR_CONNECTION_ACK) {
				if (LoginManager.aClass240_3!!.status == SignedResourceStatus.ERROR) {
					throw IOException()
				}
				if (LoginManager.aClass240_3!!.status != SignedResourceStatus.SUCCESS) {
					return
				}
				Connection.connection = AsyncConnection.create(LoginManager.aClass240_3!!.result as Socket?)
				LoginManager.aClass240_3 = null
				BufferedConnectionContext.resetQueue()
				tcpPacket = TcpPacket.createLoginPacket()
				tcpPacket!!.buffer!!.writeByte(LoginProt.aClass141_1!!.id)
				BufferedConnectionContext.queuePacket(tcpPacket)
				BufferedConnectionContext.flush()
				LoginManager.loginStep = LoginStep.SEND_LOGIN_REQUEST
			}
			@Pc(156) var local156: Int
			if (LoginManager.loginStep === LoginStep.SEND_LOGIN_REQUEST) {
				if (!Connection.connection!!.hasAvailable(1)) {
					return
				}
				Connection.connection!!.read(1, 0, BufferedConnectionContext.receivedBuffer!!.buffer)
				val responseCode: Int = BufferedConnectionContext.receivedBuffer!!.buffer!![0].toInt() and 0xFF
				if (responseCode != LoginResponseCode.EXCHANGE_KEYS) {
					LoginManager.loginStep = LoginStep.IDLE
					LoginManager.setLoginResponse(responseCode)
					Connection.connection!!.close()
					Connection.connection = null
					LoginStepRollback.revertLoginStep()
					return
				}
				BufferedConnectionContext.receivedBuffer!!.currentPosition = 0
				@Pc(179) val packet: Packet = Packet(518)
				@Pc(182) val clientSeed: IntArray = intArrayOf((util.random() * 9.9999999E7).toInt(), (util.random() * 9.9999999E7).toInt(), (util.random() * 9.9999999E7).toInt(), (util.random() * 9.9999999E7).toInt())
				packet.writeByte(10)
				packet.writeInt(clientSeed[0])
				packet.writeInt(clientSeed[1])
				packet.writeInt(clientSeed[2])
				packet.writeInt(clientSeed[3])
				packet.writeLong(0L)
				packet.writeString(LoginManager.loginPassword)
				packet.writeLong(LoginManager.ssKeyNonce)
				packet.writeLong(LoginManager.clientRandomSeed)
				packet.applyRSA(LoginManager.rsaModulus, LoginManager.rsaExponent)
				BufferedConnectionContext.resetQueue()
				@Pc(264) val loginTcpPacket: TcpPacket? = TcpPacket.createLoginPacket()
				@Pc(267) val bitPacket: BitPacket? = loginTcpPacket!!.buffer
				@Pc(284) var currentPosition: Int
				@Pc(299) val xteaStart: Int
				if (LoginManager.lobbyStage == 2) {
					if (ClientState.mainLogicStep === MainLogicStep.STEP_RECONNECTING) {
						bitPacket!!.writeByte(LoginProt.aClass141_5!!.id)
					} else {
						bitPacket!!.writeByte(LoginProt.aClass141_3!!.id)
					}
					bitPacket.writeShort(0)
					currentPosition = bitPacket.currentPosition
					bitPacket.writeInt(634)
					bitPacket.writeBytes(packet.currentPosition, 0, packet.buffer)
					xteaStart = bitPacket.currentPosition
					bitPacket.writeString(LoginManager.loginUsername)
					bitPacket.writeByte(LoginManager.anInt6172)
					bitPacket.writeByte(WindowMode.getWindowMode()!!.id)
					bitPacket.writeShort(GameShell.screenCanvasWidth)
					bitPacket.writeShort(GameShell.screenCanvasHeight)
					bitPacket.writeByte(GamePreferences.uid!!.antiAliasingPreview!!.getValue())
					LoginManager.pushRandomDat(bitPacket)
					bitPacket.writeString(GameShell.siteSettings)
					bitPacket.writeInt(GameShell.AFFILIATE)
					@Pc(423) val packet2: Packet? = GamePreferences.uid!!.encode()
					bitPacket.writeByte(packet2!!.currentPosition)
					bitPacket.writeBytes(packet2.currentPosition, 0, packet2.buffer)
					GamePreferences.preferencesSavedOnServer = true
					@Pc(445) val packet3: Packet = Packet(SystemInfo.SYSTEM_INFO!!.size())
					SystemInfo.SYSTEM_INFO!!.writeMachineInformation(packet3)
					bitPacket.writeBytes(packet3.buffer!!.size, 0, packet3.buffer)
					bitPacket.writeShort(PlayerVarDomain.varVerifyId)
					bitPacket.writeLong(GameShell.machineUid)
					bitPacket.writeByte(if (GameShell.additionalInfo == null) 0 else 1)
					if (GameShell.additionalInfo != null) {
						bitPacket.writeString(GameShell.additionalInfo)
					}
					bitPacket.writeByte(if (NativeLibraryOwner.isLibraryLoaded("jagtheora")) 1 else 0)
					Resource.writeIndexCrcs(bitPacket)
					bitPacket.encryptXtea(xteaStart, clientSeed, bitPacket.currentPosition)
					bitPacket.writeShortLength(bitPacket.currentPosition - currentPosition)
				} else {
					bitPacket!!.writeByte(LoginProt.aClass141_6!!.id)
					bitPacket.writeShort(0)
					currentPosition = bitPacket.currentPosition
					bitPacket.writeInt(634)
					bitPacket.writeBytes(packet.currentPosition, 0, packet.buffer)
					xteaStart = bitPacket.currentPosition
					bitPacket.writeString(LoginManager.loginUsername)
					bitPacket.writeByte(Game.CURRENT_GAME!!.id)
					bitPacket.writeByte(ClientState.language)
					LoginManager.pushRandomDat(bitPacket)
					bitPacket.writeString(GameShell.siteSettings)
					bitPacket.writeInt(GameShell.AFFILIATE)
					Resource.writeIndexCrcs(bitPacket)
					bitPacket.encryptXtea(xteaStart, clientSeed, bitPacket.currentPosition)
					bitPacket.writeShortLength(bitPacket.currentPosition - currentPosition)
				}
				BufferedConnectionContext.queuePacket(loginTcpPacket)
				BufferedConnectionContext.flush()
				TcpPacket.isaac = Isaac(clientSeed)
				currentPosition = 0
				while (currentPosition < 4) {
					clientSeed[currentPosition] += 50
					currentPosition++
				}
				BufferedConnectionContext.receivedBuffer!!.initIsaac(clientSeed)
				LoginManager.loginStep = LoginStep.READ_RESPONSE
			}
			if (LoginManager.loginStep === LoginStep.READ_RESPONSE) {
				if (!Connection.connection!!.hasAvailable(1)) {
					return
				}
				Connection.connection!!.read(1, 0, BufferedConnectionContext.receivedBuffer!!.buffer)
				val responseCode: Int = BufferedConnectionContext.receivedBuffer!!.buffer!![0].toInt() and 0xFF
				if (responseCode == LoginResponseCode.HOP_BLOCKED) {
					LoginManager.loginStep = LoginStep.READ_HOP_BLOCKED_TIME
				} else if (responseCode == LoginResponseCode.DISALLOWED_BY_SCRIPT) {
					LoginManager.loginStep = LoginStep.READ_DISALLOWED_REASON
				} else if (responseCode == LoginResponseCode.VIDEO_ADVERTISEMENT) {
					LoginManager.loginStep = LoginStep.SHOWING_ADVERTISEMENT
					LoginManager.setLoginResponse(responseCode)
					return
				} else if (responseCode == LoginResponseCode.OK) {
					LoginManager.loginStep = LoginStep.READ_LOGIN_DETAILS_SIZE
				} else if (responseCode == LoginResponseCode.RECONNECT_OK) {
					LoginManager.loginStep = LoginStep.READ_RECONNECT_PACKET
					BufferedConnectionContext.currentPacketSize = -2
				} else if (responseCode == LoginResponseCode.LOGIN_SERVER_NO_REPLY && LoginManager.connectionAttempts < 3) {
					LoginManager.loginStep = LoginStep.CONNECT
					LoginManager.connectionAttempts++
					LoginManager.delay = 0
					Connection.connection!!.close()
					Connection.connection = null
					return
				} else {
					LoginManager.loginStep = LoginStep.IDLE
					LoginManager.setLoginResponse(responseCode)
					Connection.connection!!.close()
					Connection.connection = null
					LoginStepRollback.revertLoginStep()
					return
				}
			}
			if (LoginManager.loginStep === LoginStep.ADVERTISEMENT_FINISHED) {
				BufferedConnectionContext.resetQueue()
				tcpPacket = TcpPacket.createLoginPacket()
				@Pc(656) val bitPacket2: BitPacket? = tcpPacket!!.buffer
				bitPacket2!!.setRandom(TcpPacket.isaac)
				bitPacket2.startPacket(LoginProt.aClass141_12!!.id)
				BufferedConnectionContext.queuePacket(tcpPacket)
				BufferedConnectionContext.flush()
				LoginManager.loginStep = LoginStep.READ_RESPONSE
			} else if (LoginManager.loginStep === LoginStep.READ_HOP_BLOCKED_TIME) {
				if (Connection.connection!!.hasAvailable(1)) {
					Connection.connection!!.read(1, 0, BufferedConnectionContext.receivedBuffer!!.buffer)
					val hopBlockedMinutes: Int = BufferedConnectionContext.receivedBuffer!!.buffer!![0].toInt() and 0xFF
					LoginManager.loginStep = LoginStep.IDLE
					LoginManager.loginHopTime = (hopBlockedMinutes + 3) * 60
					LoginManager.setLoginResponse(LoginResponseCode.HOP_BLOCKED)
					Connection.connection!!.close()
					Connection.connection = null
					LoginStepRollback.revertLoginStep()
				}
			} else if (LoginManager.loginStep === LoginStep.READ_DISALLOWED_REASON) {
				if (Connection.connection!!.hasAvailable(1)) {
					Connection.connection!!.read(1, 0, BufferedConnectionContext.receivedBuffer!!.buffer)
					LoginManager.loginStep = LoginStep.IDLE
					LoginDisallowReason.loginDisallowReason = BufferedConnectionContext.receivedBuffer!!.buffer!![0].toInt() and 0xFF
					LoginManager.setLoginResponse(29)
					Connection.connection!!.close()
					Connection.connection = null
					LoginStepRollback.revertLoginStep()
				}
			} else if (LoginManager.loginStep !== LoginStep.READ_LOGIN_DETAILS_SIZE) {
				@Pc(789) var bitPacket3: BitPacket?
				if (LoginManager.loginStep === LoginStep.READ_LOGIN_DETAILS) {
					bitPacket3 = BufferedConnectionContext.receivedBuffer
					if (LoginManager.lobbyStage == 2) {
						if (!Connection.connection!!.hasAvailable(LoginManager.loginPayloadSize)) {
							return
						}
						Connection.connection!!.read(LoginManager.loginPayloadSize, 0, bitPacket3!!.buffer)
						bitPacket3.currentPosition = 0
						LoginManager.staffModLevel = bitPacket3.readUByte()
						LoginManager.playerModLevel = bitPacket3.readUByte()
						LoginManager.userDetailQuickchat = bitPacket3.readUByte() == 1
						LoginManager.verifiedEmailAddress = bitPacket3.readUByte() == 1
						LoginManager.hasAccountSecurity = bitPacket3.readUByte() == 1
						LoginManager.quickchatOnly = bitPacket3.readUByte() == 1
						PlayerUpdate.myPlayerIndex = bitPacket3.readUShort()
						LoginManager.isMember = bitPacket3.readUByte() == 1
						LoginManager.playerDob = bitPacket3.readMedium()
						LoginManager.memberWorld = bitPacket3.readUByte() == 1
						TypeLists.LOC!!.setAllowMembers(LoginManager.memberWorld)
						TypeLists.OBJ!!.setAllowMembers(LoginManager.memberWorld)
						TypeLists.NPC!!.setAllowMembers(LoginManager.memberWorld)
					} else if (Connection.connection!!.hasAvailable(LoginManager.loginPayloadSize)) {
						Connection.connection!!.read(LoginManager.loginPayloadSize, 0, bitPacket3!!.buffer)
						bitPacket3.currentPosition = 0
						LoginManager.staffModLevel = bitPacket3.readUByte()
						LoginManager.playerModLevel = bitPacket3.readUByte()
						LoginManager.userDetailQuickchat = bitPacket3.readUByte() == 1
						LoginManager.verifiedEmailAddress = bitPacket3.readUByte() == 1
						LoginManager.hasAccountSecurity = bitPacket3.readUByte() == 1
						client.lobbyMembership = bitPacket3.readShort()
						LoginManager.isMember = client.lobbyMembership > 0
						LoginManager.recoveryQuestionsSetDay = bitPacket3.readUShort()
						LoginManager.unreadMessages = bitPacket3.readUShort()
						LoginManager.lastLoginDay = bitPacket3.readUShort()
						LoginManager.lastLoginIpAddress = bitPacket3.readInt()
						LoginManager.lastLoginHostLookup = GameShell.signLink!!.lookupHostname(LoginManager.lastLoginIpAddress)
						EmailStatus.emailStatus = bitPacket3.readUByte()
						LoginManager.lobbyCreditCardExpiry = bitPacket3.readUShort()
						LoginManager.lobbyGraceExpiry = bitPacket3.readUShort()
						LoginManager.isDobRequested = bitPacket3.readUByte() == 1
						val username: String? = bitPacket3.readJagString()
						PlayerUpdate.localUsername = username
						PlayerUpdate.localPlayer!!.username = username
						PlayerUpdate.localPlayer!!.displayName = username
						LobbyManager.lobbyMembershipStatus = bitPacket3.readUByte()
						LoginManager.lobbyPlayAge = bitPacket3.readInt()
						WorldConnection.aClass255_4 = WorldConnectionInfo()
						WorldConnection.aClass255_4!!.worldId = bitPacket3.readUShort()
						if (WorldConnection.aClass255_4!!.worldId == 65535) {
							WorldConnection.aClass255_4!!.worldId = -1
						}
						WorldConnection.aClass255_4!!.host = bitPacket3.readJagString()
						if (ModeWhere.WTRC !== ServerEnvironment.LIVE) {
							WorldConnection.aClass255_4!!.primaryPort = WorldConnection.aClass255_4!!.worldId + 50000
							WorldConnection.aClass255_4!!.secondaryPort = WorldConnection.aClass255_4!!.worldId + 40000
						}
						if (ServerEnvironment.LIVE !== ModeWhere.aClass107_5 && (TcpPacket.CURRENT_REQUEST!!.isSameServer(WorldConnection.aClass255_3) || TcpPacket.CURRENT_REQUEST!!.isSameServer(WorldConnection.aClass255_1))) {
							HostPreparation.prepareHost()
						}
					} else {
						return
					}
					if (LoginManager.userDetailQuickchat && !LoginManager.hasAccountSecurity || LoginManager.isMember) {
						try {
							BrowserJavaScript.callJavaScript(GameShell.applet, "zap")
						} catch (@Pc(1101) ex: Throwable) {
							if (GameShell.advertEnabled) {
								try {
									GameShell.applet!!.getAppletContext()!!.showDocument(URL(GameShell.applet!!.getCodeBase(), "blank.ws"), "tbi")
								} catch (@Pc(1115) ex2: Exception) {
								}
							}
						}
					} else {
						try {
							BrowserJavaScript.callJavaScript(GameShell.applet, "unzap")
						} catch (@Pc(1092) ex3: Throwable) {
						}
					}
					if (ServerEnvironment.LIVE === ModeWhere.WTRC) {
						try {
							BrowserJavaScript.callJavaScript(GameShell.applet, "loggedin")
						} catch (@Pc(1126) ex4: Throwable) {
						}
					}
					if (LoginManager.lobbyStage != 2) {
						LoginManager.loginStep = LoginStep.IDLE
						LoginManager.setLoginResponse(LoginResponseCode.OK)
						LoginSessionReset.resetSession()
						MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOBBY_SCREEN)
						BufferedConnectionContext.currentPacket = null
						return
					}
					LoginManager.loginStep = LoginStep.READ_FIRST_PACKET_HEADER
				}
				if (LoginManager.loginStep === LoginStep.READ_FIRST_PACKET_HEADER) {
					if (!Connection.connection!!.hasAvailable(3)) {
						return
					}
					Connection.connection!!.read(3, 0, BufferedConnectionContext.receivedBuffer!!.buffer)
					LoginManager.loginStep = LoginStep.READ_FIRST_PACKET_SIZE
				}
				if (LoginManager.loginStep === LoginStep.READ_FIRST_PACKET_SIZE) {
					bitPacket3 = BufferedConnectionContext.receivedBuffer
					bitPacket3!!.currentPosition = 0
					if (bitPacket3.largeOpcode()) {
						if (!Connection.connection!!.hasAvailable(1)) {
							return
						}
						Connection.connection!!.read(1, 3, bitPacket3.buffer)
					}
					BufferedConnectionContext.currentPacket = ServerProt.serverProtValues()[bitPacket3.readOpcode()]
					BufferedConnectionContext.currentPacketSize = bitPacket3.readUShort()
					LoginManager.loginStep = LoginStep.READ_FIRST_PACKET
				}
				if (LoginManager.loginStep === LoginStep.READ_FIRST_PACKET) {
					if (Connection.connection!!.hasAvailable(BufferedConnectionContext.currentPacketSize)) {
						Connection.connection!!.read(BufferedConnectionContext.currentPacketSize, 0, BufferedConnectionContext.receivedBuffer!!.buffer)
						BufferedConnectionContext.receivedBuffer!!.currentPosition = 0
						LoginManager.loginStep = LoginStep.IDLE
						val packetSize: Int = BufferedConnectionContext.currentPacketSize
						LoginManager.setLoginResponse(LoginResponseCode.OK)
						Static671.method597()
						PlayerUpdate.decodeOwnPlayer(BufferedConnectionContext.receivedBuffer)
						MapRegion.mapCenterChunkX = -1
						if (BufferedConnectionContext.currentPacket === ServerProt.DYNAMIC_MAP_REGION) {
							ConstructedMapInit.initializeConstructedMap()
						} else {
							StaticRegionInit.initializeRegion()
						}
						if (BufferedConnectionContext.receivedBuffer!!.currentPosition != packetSize) {
							throw RuntimeException("lswp pos:" + BufferedConnectionContext.receivedBuffer!!.currentPosition + " psize:" + packetSize)
						}
						BufferedConnectionContext.currentPacket = null
					}
				} else if (LoginManager.loginStep === LoginStep.READ_RECONNECT_PACKET) {
					if (BufferedConnectionContext.currentPacketSize == -2) {
						if (!Connection.connection!!.hasAvailable(2)) {
							return
						}
						Connection.connection!!.read(2, 0, BufferedConnectionContext.receivedBuffer!!.buffer)
						BufferedConnectionContext.receivedBuffer!!.currentPosition = 0
						BufferedConnectionContext.currentPacketSize = BufferedConnectionContext.receivedBuffer!!.readUShort()
					}
					if (Connection.connection!!.hasAvailable(BufferedConnectionContext.currentPacketSize)) {
						Connection.connection!!.read(BufferedConnectionContext.currentPacketSize, 0, BufferedConnectionContext.receivedBuffer!!.buffer)
						BufferedConnectionContext.receivedBuffer!!.currentPosition = 0
						@Pc(1334) val packetSize: Int = BufferedConnectionContext.currentPacketSize
						LoginManager.loginStep = LoginStep.IDLE
						LoginManager.setLoginResponse(LoginResponseCode.RECONNECT_OK)
						ReconnectStateReset.onReconnect()
						PlayerUpdate.decodeOwnPlayer(BufferedConnectionContext.receivedBuffer)
						if (BufferedConnectionContext.receivedBuffer!!.currentPosition != packetSize) {
							throw RuntimeException("lswpr pos:" + BufferedConnectionContext.receivedBuffer!!.currentPosition + " psize:" + packetSize)
						}
						BufferedConnectionContext.currentPacket = null
					}
				}
			} else if (Connection.connection!!.hasAvailable(1)) {
				Connection.connection!!.read(1, 0, BufferedConnectionContext.receivedBuffer!!.buffer)
				LoginManager.loginPayloadSize = BufferedConnectionContext.receivedBuffer!!.buffer!![0].toInt() and 0xFF
				LoginManager.loginStep = LoginStep.READ_LOGIN_DETAILS
			}
		} catch (@Pc(1372) ex5: IOException) {
			if (Connection.connection != null) {
				Connection.connection!!.close()
				Connection.connection = null
			}
			if (LoginManager.connectionAttempts >= 3) {
				LoginManager.loginStep = LoginStep.IDLE
				LoginManager.setLoginResponse(-4)
				LoginStepRollback.revertLoginStep()
			} else {
				if (LoginManager.lobbyStage == 2) {
					TcpPacket.CURRENT_REQUEST!!.cycleConnectionMode()
				} else {
					LoginManager.CURRENT_CONNECTION_CONTEXT!!.cycleConnectionMode()
				}
				LoginManager.connectionAttempts++
				LoginManager.delay = 0
				LoginManager.loginStep = LoginStep.CONNECT
			}
		}
	}
}
