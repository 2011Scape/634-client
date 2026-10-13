import com.jagex.game.runetek5.config.rsinterface.MediaType
import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.login.LoginStep
import com.jagex.game.runetek5.client.window.WindowModeType
import com.jagex.game.runetek5.ChatHistory
import com.jagex.game.runetek5.ChatLine
import com.jagex.game.runetek5.DisplayModeDetails
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.ClientTrigger
import com.jagex.game.runetek5.cache.node.lru.ClientScript
import com.jagex.game.runetek5.Country
import com.jagex.game.runetek5.Find
import com.jagex.game.runetek5.Fullscreen
import com.jagex.game.runetek5.interaction.IgnoreList
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.LobbyManager
import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.QuickChatPhrase
import com.jagex.game.runetek5.ScriptRunner
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.SoundManager
import com.jagex.game.runetek5.StackFrame
import com.jagex.game.runetek5.ChatLineCount
import com.jagex.game.runetek5.PlayMusic
import com.jagex.game.runetek5.FontMetricsLoader
import com.jagex.game.runetek5.EmailStatus
import com.jagex.game.runetek5.WorldListSorter
import com.jagex.game.runetek5.MidiChannelVolumeReset
import com.jagex.game.runetek5.ThreeDView
import com.jagex.game.runetek5.config.rsinterface.cs2.ClientScriptOpCode
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.Queue
import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.quickchat.QuickchatMessageType
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.config.enumtype.EnumType
import com.jagex.game.runetek5.config.rsinterface.IFEvents
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.config.invtype.LoginDisallowReason
import com.jagex.game.runetek5.config.mec.MECType
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.config.param.ParamType
import com.jagex.game.runetek5.config.quickchat.QuickChatCategoryType
import com.jagex.game.runetek5.config.vartype.player.ItemFindQuery
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import com.jagex.game.runetek5.network.world.WorldDescriptor
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapElement
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.math.ColourUtils
import com.jagex.game.runetek5.statics.CS2State
import com.jagex.game.runetek5.statics.StatBaseLevels
import com.jagex.game.runetek5.statics.BrowserJavaScript
import awt.datatransfer.DataFlavor
import awt.datatransfer.Transferable
import util.Date
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.event.keyboard.SimpleKeyboardMonitorConstants
import com.jagex.game.runetek5.client.event.keyboard.FriendOrSelfCheck
import com.jagex.game.runetek5.client.event.mouse.DelayedIfTextUpdate
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.web.ClientURLTools
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.config.DefaultsFile
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.network.WorldConnection
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import com.jagex.game.runetek5.systeminfo.SystemInfo
import com.jagex.game.runetek5.config.varc.VarcTypeList
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.interaction.FriendList
import com.jagex.game.runetek5.trigger.ClientTriggerType
import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.cache.node.lru.PulseEvent
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.ChatFilter
import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.config.flo.FloType
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.sound.SoundResourceManager
import com.jagex.game.runetek5.sound.player.song.SongPlayer
import com.jagex.game.runetek5.toolkit.AbstractToolkit
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.core.constants.ModeWhat
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.InterfaceDrag
import com.jagex.game.runetek5.NativeLibraryOwner
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.config.midi.VorbisNode
import com.jagex.game.runetek5.config.quickchat.QuickchatMessageTypeList
import com.jagex.game.runetek5.encoding.HuffmanList
import com.jagex.game.runetek5.network.Connection
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.MainLogicManager
import com.jagex.game.runetek5.config.rsinterface.cs2.CS2Executor
import com.jagex.game.runetek5.network.packet.StockMarketOffer
import com.jagex.game.runetek5.network.world.WorldMeta
import com.jagex.game.runetek5.statics.WorldMapRenderer
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.LoadingStage
import com.jagex.game.runetek5.preference.impl.ToolkitOption
import kotlin.jvm.JvmStatic
import lang.StringBuffer
import util.Locale
import kotlin.jvm.JvmField
import kotlin.math.pow
import util.lowercase
import lang.javaIndexOf

object CS2Interpreter {

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Lclient!uw;II)V")
	@JvmStatic fun executeTrigger(@OriginalArg(0) triggerType: ClientTrigger?, @OriginalArg(1) v1: Int, @OriginalArg(2) v2: Int) {
		@Pc(5) var clientScript: ClientScript? = ClientScript.getTriggerScript(v1, v2, triggerType)
		if (clientScript == null) {
			return
		}
		ScriptRunner.intLocals = IntArray(clientScript.intLocalsCount)
		ScriptRunner.stringVars = arrayOfNulls<String>(clientScript.stringLocalsCount)
		if (clientScript.triggerType === ClientTriggerType.MEC_MOUSE_ENTER || clientScript.triggerType === ClientTriggerType.MEC_MOUSE_OVER || clientScript.triggerType === ClientTriggerType.MEC_MOUSE_LEAVE) {
			@Pc(30) var worldMapOffsetX: Int = 0
			@Pc(32) var worldMapOffsetY: Int = 0
			if (WorldMap.worldmapComponent != null) {
				worldMapOffsetX = WorldMap.worldmapComponent!!.x
				worldMapOffsetY = WorldMap.worldmapComponent!!.y
			}
			ScriptRunner.intLocals!![0] = MouseRecord.mouseRecorder!!.getMouseX() - worldMapOffsetX
			ScriptRunner.intLocals!![1] = MouseRecord.mouseRecorder!!.getMouseY() - worldMapOffsetY
		}
		executeScript(clientScript, 200000)
	}

	@OriginalMember(owner = "client!kca", name = "b", descriptor = "(I)V")
	@JvmStatic fun runInterfaceOnLoadScripts(@OriginalArg(0) interfaceId: Int) {
		if (interfaceId == -1 || !InterfaceManager.loadInterface(interfaceId)) {
			return
		}
		@Pc(12) var components: Array<Component?>? = Interface.interfaces!![interfaceId]
		@Pc(14) var i: Int = 0
		while (i < components!!.size) {
			@Pc(19) var component: Component? = components!![i]
			if (component!!.onLoadScript != null) {
				@Pc(26) var hookRequest: HookRequest? = HookRequest()
				hookRequest!!.source = component
				hookRequest!!.params = component!!.onLoadScript
				executeHookInternal(hookRequest, 2000000)
			}
			i++
		}
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(IZ)V")
	@JvmStatic fun executeOpFrom5000(@OriginalArg(0) opcode: Int, @OriginalArg(1) hasIntOperand: Boolean) {
		@Pc(83) var string: String?
		@Pc(1496) var local1496: Boolean
		@Pc(97) var local97: Int
		@Pc(175) var local175: Int
		@Pc(776) var local776: Int
		@Pc(181) var string2: String?
		@Pc(103) var local103: Int
		@Pc(52) var packet: TcpPacket?
		@Pc(89) var string3: String?
		if (opcode < 5100) {
			if (opcode == ClientScriptOpCode.CHAT_GETFILTER_PUBLIC) {
				ScriptRunner.intStack[CS2State.intStackPtr++] = ChatBox.publicFilter
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_SETFILTER) {
				CS2State.intStackPtr -= 3
				ChatBox.publicFilter = ScriptRunner.intStack[CS2State.intStackPtr]
				ChatBox.privateFilter = ChatFilter.chatFilterForId(ScriptRunner.intStack[CS2State.intStackPtr + 1])
				if (ChatBox.privateFilter == null) {
					ChatBox.privateFilter = ChatFilter.FRIENDS_ONLY
				}
				ChatBox.tradeFilter = ScriptRunner.intStack[CS2State.intStackPtr + 2]
				packet = TcpPacket.createPacket(ClientProt.CHAT_SETFILTER, TcpPacket.isaac)
				packet.buffer!!.writeByte(ChatBox.publicFilter)
				packet.buffer!!.writeByte(ChatBox.privateFilter!!.id)
				packet.buffer!!.writeByte(ChatBox.tradeFilter)
				BufferedConnectionContext.queuePacket(packet)
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_SENDABUSEREPORT) {
				CS2State.stringStackPtr -= 2
				string = CS2State.stringStack[CS2State.stringStackPtr]
				string3 = CS2State.stringStack[CS2State.stringStackPtr + 1]
				CS2State.intStackPtr -= 2
				var reportType: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var mute: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				if (string3 == null) {
					string3 = ""
				}
				if (string3.length > 80) {
					string3 = string3.substring(0, 80)
				}
				@Pc(125) var reportPacket: TcpPacket? = TcpPacket.createPacket(ClientProt.REPORT_ABUSE, TcpPacket.isaac)
				reportPacket!!.buffer!!.writeByte(Utils.getEncodedStringLength(string!!) + Utils.getEncodedStringLength(string3) + 2)
				reportPacket!!.buffer!!.writeString(string)
				reportPacket!!.buffer!!.writeByte(reportType - 1)
				reportPacket!!.buffer!!.writeByte(mute)
				reportPacket!!.buffer!!.writeString(string3)
				BufferedConnectionContext.queuePacket(reportPacket)
				return
			}
			@Pc(179) var chatLine: ChatLine?
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORY_BYTYPEANDLINE) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				string2 = ""
				if (chatLine != null && chatLine.message != null) {
					string2 = chatLine.message
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORY_BYUID) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				var messageType: Int = -1
				if (chatLine != null) {
					messageType = chatLine.type
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = messageType
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_GETFILTER_PRIVATE) {
				if (ChatBox.privateFilter == null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = ChatBox.privateFilter!!.id
				return
			}
			@Pc(269) var packet2: TcpPacket?
			if (opcode == ClientScriptOpCode.CHAT_SETMODE) {
				var chatMode: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				packet2 = TcpPacket.createPacket(ClientProt.CHAT_TYPE, TcpPacket.isaac)
				packet2.buffer!!.writeByte(chatMode)
				BufferedConnectionContext.queuePacket(packet2)
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_SENDPUBLIC) {
				string = CS2State.stringStack[--CS2State.stringStackPtr]
				ScriptRunner.sendPublicChat(string, opcode)
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_SENDPRIVATE) {
				CS2State.stringStackPtr -= 2
				string = CS2State.stringStack[CS2State.stringStackPtr]
				string3 = CS2State.stringStack[CS2State.stringStackPtr + 1]
				if (LoginManager.staffModLevel != 0 || (!LoginManager.userDetailQuickchat || LoginManager.verifiedEmailAddress) && !LoginManager.quickchatOnly) {
					@Pc(328) var privatePacket: TcpPacket? = TcpPacket.createPacket(ClientProt.SEND_PRIVATE_MESSAGE, TcpPacket.isaac)
					privatePacket!!.buffer!!.writeByte(0)
					var startPosition: Int = privatePacket!!.buffer!!.currentPosition
					privatePacket!!.buffer!!.writeString(string)
					HuffmanList.writeChatMessage(privatePacket!!.buffer, string3)
					privatePacket!!.buffer!!.writeIndex(privatePacket!!.buffer!!.currentPosition - startPosition)
					BufferedConnectionContext.queuePacket(privatePacket)
					return
				}
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORYNAME) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				string2 = ""
				if (chatLine != null && chatLine.crownedName != null) {
					string2 = chatLine.crownedName
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORYCLAN) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				string2 = ""
				if (chatLine != null && chatLine.clan != null) {
					string2 = chatLine.clan
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORYPHRASE) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				var quickChatId: Int = -1
				if (chatLine != null) {
					quickChatId = chatLine.quickchatMessageId
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatId
				return
			}
			if (opcode == ClientScriptOpCode.CHAT_PLAYERNAME) {
				if (PlayerUpdate.localPlayer == null || PlayerUpdate.localPlayer!!.username == null) {
					string = ""
				} else {
					string = PlayerUpdate.localPlayer!!.getUsernameWithTitle()
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string
				return
			}
			if (opcode == 5016) {
				ScriptRunner.intStack[CS2State.intStackPtr++] = ChatBox.tradeFilter
				return
			}
			if (opcode == 5017) {
				ScriptRunner.intStack[CS2State.intStackPtr++] = ChatLineCount.getChatLineCount()
				return
			}
			if (opcode == 5018) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				var effectFlags: Int = 0
				if (chatLine != null) {
					effectFlags = chatLine.effectFlags
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = effectFlags
				return
			}
			if (opcode == 5019) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				string2 = ""
				if (chatLine != null && chatLine.name != null) {
					string2 = chatLine.name
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2
				return
			}
			if (opcode == 5020) {
				if (PlayerUpdate.localPlayer == null || PlayerUpdate.localPlayer!!.username == null) {
					string = ""
				} else {
					string = PlayerUpdate.localPlayer!!.getDisplayName()
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string
				return
			}
			if (opcode == 5023) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				var messageIdentifier: Int = -1
				if (chatLine != null) {
					messageIdentifier = chatLine.identifier
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = messageIdentifier
				return
			}
			if (opcode == 5024) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				var messageTime: Int = -1
				if (chatLine != null) {
					messageTime = chatLine.time
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = messageTime
				return
			}
			if (opcode == 5025) {
				var lineIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				chatLine = ChatBox.getChatLine(lineIndex)
				string2 = ""
				if (chatLine != null && chatLine.simpleName != null) {
					string2 = chatLine.simpleName
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2
				return
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETDESC) {
				var categoryId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY!!.list(categoryId).name
				return
			}
			@Pc(736) var quickChatCatType: QuickChatCategoryType?
			if (opcode == ClientScriptOpCode.CHATCAT_GETSUBCATCOUNT) {
				var categoryId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				quickChatCatType = TypeLists.QUICK_CHAT_CATEGORY!!.list(categoryId)
				if (quickChatCatType.subCategories == null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatCatType.subCategories!!.size
				return
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETSUBCAT) {
				CS2State.intStackPtr -= 2
				var categoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var subCategoryIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				@Pc(781) var quickChatCatType2: QuickChatCategoryType? = TypeLists.QUICK_CHAT_CATEGORY!!.list(categoryId)
				var subCategoryId: Int = quickChatCatType2!!.subCategories!![subCategoryIndex]
				ScriptRunner.intStack[CS2State.intStackPtr++] = subCategoryId
				return
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETPHRASECOUNT) {
				var categoryId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				quickChatCatType = TypeLists.QUICK_CHAT_CATEGORY!!.list(categoryId)
				if (quickChatCatType.messages == null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatCatType.messages!!.size
				return
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETPHRASE) {
				CS2State.intStackPtr -= 2
				var categoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var phraseIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY!!.list(categoryId).messages!![phraseIndex]
				return
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETTEXT) {
				var phraseId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId).getText()
				return
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETAUTORESPONSECOUNT) {
				var phraseId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				@Pc(907) var quickChatPhraseType: QuickchatMessageType? = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId)
				if (quickChatPhraseType!!.responses == null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatPhraseType!!.responses!!.size
				return
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETAUTORESPONSE) {
				CS2State.intStackPtr -= 2
				var phraseId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var responseIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId).responses!![responseIndex]
				return
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_PREPARE) {
				CS2State.currentQuickChatPhrase = QuickChatPhrase()
				CS2State.currentQuickChatPhrase!!.id = ScriptRunner.intStack[--CS2State.intStackPtr]
				CS2State.currentQuickChatPhrase!!.type = TypeLists.QUICK_CHAT_MESSAGE!!.list(CS2State.currentQuickChatPhrase!!.id)
				CS2State.currentQuickChatPhrase!!.values = IntArray(CS2State.currentQuickChatPhrase!!.type!!.getDynamicCommandCount())
				return
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SENDPUBLIC) {
				packet = TcpPacket.createPacket(ClientProt.QUICKCHAT_PUBLIC, TcpPacket.isaac)
				packet.buffer!!.writeByte(0)
				var startPosition: Int = packet.buffer!!.currentPosition
				packet.buffer!!.writeByte(0)
				packet.buffer!!.writeShort(CS2State.currentQuickChatPhrase!!.id)
				CS2State.currentQuickChatPhrase!!.type!!.encodeValues(CS2State.currentQuickChatPhrase!!.values, packet.buffer)
				packet.buffer!!.writeIndex(packet.buffer!!.currentPosition - startPosition)
				BufferedConnectionContext.queuePacket(packet)
				return
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SENDPRIVATE) {
				string = CS2State.stringStack[--CS2State.stringStackPtr]
				packet2 = TcpPacket.createPacket(ClientProt.QUICKCHAT_PRIVATE, TcpPacket.isaac)
				packet2.buffer!!.writeByte(0)
				var payloadStart: Int = packet2.buffer!!.currentPosition
				packet2.buffer!!.writeString(string)
				packet2.buffer!!.writeShort(CS2State.currentQuickChatPhrase!!.id)
				CS2State.currentQuickChatPhrase!!.type!!.encodeValues(CS2State.currentQuickChatPhrase!!.values, packet2.buffer)
				packet2.buffer!!.writeIndex(packet2.buffer!!.currentPosition - payloadStart)
				BufferedConnectionContext.queuePacket(packet2)
				return
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SENDCLAN) {
				packet = TcpPacket.createPacket(ClientProt.QUICKCHAT_PUBLIC, TcpPacket.isaac)
				packet.buffer!!.writeByte(0)
				var startPosition: Int = packet.buffer!!.currentPosition
				packet.buffer!!.writeByte(1)
				packet.buffer!!.writeShort(CS2State.currentQuickChatPhrase!!.id)
				CS2State.currentQuickChatPhrase!!.type!!.encodeValues(CS2State.currentQuickChatPhrase!!.values, packet.buffer)
				packet.buffer!!.writeIndex(packet.buffer!!.currentPosition - startPosition)
				BufferedConnectionContext.queuePacket(packet)
				return
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETSUBCATSHORTCUT) {
				CS2State.intStackPtr -= 2
				var categoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var subCategoryIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY!!.list(categoryId).subCategoryHotkeys!![subCategoryIndex].code
				return
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETPHRASESHORTCUT) {
				CS2State.intStackPtr -= 2
				var categoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var phraseIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY!!.list(categoryId).messageHotkeys!![phraseIndex].code
				return
			}
			if (opcode == ClientScriptOpCode.CHATCAT_FINDSUBCATBYSHORTCUT) {
				CS2State.intStackPtr -= 2
				var categoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var shortcutKey: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				if (shortcutKey == -1) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY!!.list(categoryId).findSubcategoryByShortcut(shortcutKey.toChar())
				return
			}
			if (opcode == ClientScriptOpCode.CHATCAT_FINDPHRASEBYSHORTCUT) {
				CS2State.intStackPtr -= 2
				var categoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var shortcutKey: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				if (shortcutKey == -1) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY!!.list(categoryId).findPhraseByShortcut(shortcutKey.toChar())
				return
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETDYNAMICCOMMANDCOUNT) {
				var phraseId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId).getDynamicCommandCount()
				return
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETDYNAMICCOMMAND) {
				CS2State.intStackPtr -= 2
				var phraseId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var commandIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				var commandTypeId: Int = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId).getValueType(commandIndex)!!.id
				ScriptRunner.intStack[CS2State.intStackPtr++] = commandTypeId
				return
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SETDYNAMICINT) {
				CS2State.intStackPtr -= 2
				var valueIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var intValue: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				CS2State.currentQuickChatPhrase!!.values!![valueIndex] = intValue
				return
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SETDYNAMICOBJ) {
				CS2State.intStackPtr -= 2
				var valueIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var objId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				CS2State.currentQuickChatPhrase!!.values!![valueIndex] = objId
				return
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETDYNAMICCOMMANDPARAM_ENUM) {
				CS2State.intStackPtr -= 3
				var phraseId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var commandIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				var paramTypeId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
				@Pc(1448) var quickChatPhraseType2: QuickchatMessageType? = TypeLists.QUICK_CHAT_MESSAGE!!.list(phraseId)
				if (quickChatPhraseType2!!.getValueType(commandIndex)!!.id != 0) {
					throw RuntimeException("bad command")
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatPhraseType2!!.getDynamicCommandParam(paramTypeId, commandIndex)
				return
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_FIND) {
				string = CS2State.stringStack[--CS2State.stringStackPtr]
				var isMenuSearch: Boolean = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
				QuickchatMessageTypeList.searchQuickChatPhrases(isMenuSearch, string!!)
				ScriptRunner.intStack[CS2State.intStackPtr++] = Find.findResultCount
				return
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_FINDNEXT) {
				if (Find.queryResults != null && MessageBox.queryResultsCursor < Find.findResultCount) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = Find.queryResults!![MessageBox.queryResultsCursor++].toInt() and 0xFFFF
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = -1
				return
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_FINDRESTART) {
				MessageBox.queryResultsCursor = 0
				return
			}
		} else if (opcode < 5200) {
			if (opcode == ClientScriptOpCode.KEYHELD_ALT) {
				if (KeyRecorder.keyRecorder!!.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_ALT)) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0
				return
			}
			if (opcode == ClientScriptOpCode.KEYHELD_CTRL) {
				if (KeyRecorder.keyRecorder!!.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_CONTROL)) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0
				return
			}
			if (opcode == ClientScriptOpCode.KEYHELD_SHIFT) {
				if (KeyRecorder.keyRecorder!!.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_SHIFT)) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0
				return
			}
		} else {
			@Pc(2833) var displayModeIndex: Int
			@Pc(2107) var local2107: Boolean
			if (opcode < 5300) {
				if (opcode == ClientScriptOpCode.WORLDMAP_SETZOOM) {
					WorldMapDisplay.setWorldMapZoom(ScriptRunner.intStack[--CS2State.intStackPtr])
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETZOOM) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMapRenderer.getWorldMapZoom()
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_SETMAP) {
					WorldMapOpener.setWorldMap(false, -1, -1, ScriptRunner.intStack[--CS2State.intStackPtr])
					return
				}
				@Pc(1696) var worldMapArea: WorldMapElement?
				if (opcode == ClientScriptOpCode.WORLDMAP_GETMAP) {
					var coord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					worldMapArea = MapList.getMapDef((coord shr 14) and 0x3FFF, coord and 0x3FFF)
					if (worldMapArea == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.elementId
					return
				}
				@Pc(1729) var worldMapArea2: WorldMapElement?
				if (opcode == ClientScriptOpCode.ORLDMAP_GETMAPNAME) {
					worldMapArea2 = MapList.getArea(ScriptRunner.intStack[--CS2State.intStackPtr])
					if (worldMapArea2 != null && worldMapArea2.displayName != null) {
						CS2State.stringStack[CS2State.stringStackPtr++] = worldMapArea2.displayName
						return
					}
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETSIZE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMap.worldMapViewWidth
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMap.worldMapViewHeight
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISPLAYPOSITION) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMapDisplay.mapCenterX + WorldMap.mapSquareAbsX
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMapDisplay.mapCenterY + WorldMap.mapSquareAbsY
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETCONFIGORIGIN) {
					var areaId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					worldMapArea = MapList.getArea(areaId)
					if (worldMapArea == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = (worldMapArea.bitpackedPlacement shr 14) and 0x3FFF
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.bitpackedPlacement and 0x3FFF
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETCONFIGBOUNDS) {
					var areaId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					worldMapArea = MapList.getArea(areaId)
					if (worldMapArea == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.displayMaxX - worldMapArea.displayMinZ
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.displayZ2 - worldMapArea.boundsMinX
					return
				}
				@Pc(1919) var mapElementListEntry: WorldMapCategory?
				if (opcode == ClientScriptOpCode.WORLDMAP_LISTELEMENT_START) {
					mapElementListEntry = WorldMap.getFirstWorldMapCategory()
					if (mapElementListEntry == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementListEntry.id
					var coord: Int = (mapElementListEntry.level shl 28) or (mapElementListEntry.x + WorldMap.mapSquareAbsX shl 14) or mapElementListEntry.z + WorldMap.mapSquareAbsY
					ScriptRunner.intStack[CS2State.intStackPtr++] = coord
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_LISTELEMENT_NEXT) {
					mapElementListEntry = WorldMap.getNextActiveCategory()
					if (mapElementListEntry == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementListEntry.id
					var coord: Int = (mapElementListEntry.level shl 28) or (mapElementListEntry.x + WorldMap.mapSquareAbsX shl 14) or mapElementListEntry.z + WorldMap.mapSquareAbsY
					ScriptRunner.intStack[CS2State.intStackPtr++] = coord
					return
				}
				@Pc(2067) var converted: Boolean
				if (opcode == ClientScriptOpCode.WORLDMAP_JUMPTOSOURCECOORD) {
					var sourceCoord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					worldMapArea = WorldMapDisplay.getWorldMapArea()
					if (worldMapArea != null) {
						converted = worldMapArea.convertSourceToDisplay(CS2State.coordConversionBuffer, (sourceCoord shr 14) and 0x3FFF, sourceCoord and 0x3FFF, (sourceCoord shr 28) and 0x3)
						if (converted) {
							WorldMapDisplay.setWorldMapScrollTarget(CS2State.coordConversionBuffer[1], CS2State.coordConversionBuffer[2])
						}
					}
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_COORDINMAP) {
					CS2State.intStackPtr -= 2
					var coord: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var elementId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					@Pc(2105) var queue: Queue? = MapList.getRenderedElements((coord shr 14) and 0x3FFF, coord and 0x3FFF)
					var found: Boolean = false
					@Pc(2112) var worldMapArea3: WorldMapElement? = (queue!!.first() as WorldMapElement?)
					while (worldMapArea3 != null) {
						if (worldMapArea3.elementId == elementId) {
							found = true
							break
						}
						worldMapArea3 = (queue!!.next() as WorldMapElement?)
					}
					if (found) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = 1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETCONFIGZOOM) {
					var areaId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					worldMapArea = MapList.getArea(areaId)
					if (worldMapArea == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.defaultZoom
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_ISLOADED) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (ComponentMinimap.mapAssetLoadingPercentage == 100) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_JUMPTODISPLAYCOORD) {
					var displayCoord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					WorldMapDisplay.setWorldMapScrollTarget((displayCoord shr 14) and 0x3FFF, displayCoord and 0x3FFF)
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETSOURCEPOSITION) {
					worldMapArea2 = WorldMapDisplay.getWorldMapArea()
					if (worldMapArea2 != null) {
						var coordsMapped: Boolean = worldMapArea2.mapToGameCoords(WorldMapDisplay.mapCenterY + WorldMap.mapSquareAbsY, WorldMapDisplay.mapCenterX + WorldMap.mapSquareAbsX, CS2State.coordConversionBuffer)
						if (coordsMapped) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[1]
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[2]
							return
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_SETMAP_COORD) {
					CS2State.intStackPtr -= 2
					var areaId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var coord: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					WorldMapOpener.setWorldMap(false, coord and 0x3FFF, (coord shr 14) and 0x3FFF, areaId)
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISPLAYCOORD) {
					var sourceCoord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					worldMapArea = WorldMapDisplay.getWorldMapArea()
					if (worldMapArea != null) {
						converted = worldMapArea.convertSourceToDisplay(CS2State.coordConversionBuffer, (sourceCoord shr 14) and 0x3FFF, sourceCoord and 0x3FFF, (sourceCoord shr 28) and 0x3)
						if (converted) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[1]
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[2]
							return
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETSOURCECOORD) {
					var displayCoord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					worldMapArea = WorldMapDisplay.getWorldMapArea()
					if (worldMapArea != null) {
						converted = worldMapArea.mapToGameCoords(displayCoord and 0x3FFF, (displayCoord shr 14) and 0x3FFF, CS2State.coordConversionBuffer)
						if (converted) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[1]
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[2]
							return
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_FLASHELEMENT) {
					WorldMapDisplay.flashWorldMapElement(ScriptRunner.intStack[--CS2State.intStackPtr])
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_SETMAP_COORD_OVERRIDE) {
					CS2State.intStackPtr -= 2
					var areaId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var coord: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					WorldMapOpener.setWorldMap(true, coord and 0x3FFF, (coord shr 14) and 0x3FFF, areaId)
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_DISABLEELEMENTS) {
					WorldMapDisplay.hideIcons = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISABLEELEMENTS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (WorldMapDisplay.hideIcons) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_FLASHELEMENTCATEGORY) {
					var categoryId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					WorldMapDisplay.flashWorldMapElementCategory(categoryId)
					return
				}
				@Pc(2625) var node: Node?
				if (opcode == ClientScriptOpCode.WORLDMAP_DISABLEELEMENTCATEGORY) {
					CS2State.intStackPtr -= 2
					var categoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var disabled: Boolean = ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1
					if (WorldMapDisplay.hiddenCategoryIds != null) {
						node = WorldMapDisplay.hiddenCategoryIds.get(categoryId.toLong())
						if (node != null && !disabled) {
							node.unlink()
							return
						}
						if (node == null && disabled) {
							node = Node()
							WorldMapDisplay.hiddenCategoryIds.put(categoryId.toLong(), node)
						}
					}
					return
				}
				@Pc(2667) var node2: Node?
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISABLEELEMENTCATEGORY) {
					var categoryId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (WorldMapDisplay.hiddenCategoryIds != null) {
						node2 = WorldMapDisplay.hiddenCategoryIds.get(categoryId.toLong())
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (node2 == null) 0 else 1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_DISABLEELEMENT) {
					CS2State.intStackPtr -= 2
					var elementId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var disabled: Boolean = ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1
					if (WorldMapDisplay.hiddenMecIds != null) {
						node = WorldMapDisplay.hiddenMecIds.get(elementId.toLong())
						if (node != null && !disabled) {
							node.unlink()
							return
						}
						if (node == null && disabled) {
							node = Node()
							WorldMapDisplay.hiddenMecIds.put(elementId.toLong(), node)
						}
					}
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISABLEELEMENT) {
					var elementId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (WorldMapDisplay.hiddenMecIds != null) {
						node2 = WorldMapDisplay.hiddenMecIds.get(elementId.toLong())
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (node2 == null) 0 else 1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETCURRENTMAP) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (WorldMap.area == null) -1 else WorldMap.area!!.elementId
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_FINDNEARESTELEMENT) {
					CS2State.intStackPtr -= 2
					var categoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var coord: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					var coordX: Int = (coord shr 14) and 0x3FFF
					var coordZ: Int = coord and 0x3FFF
					var nearestElementId: Int = WorldMap.findNearestWorldMapElement(categoryId, coordX, coordZ)
					if (nearestElementId < 0) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = nearestElementId
					return
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_CLOSEMAP) {
					Static671.closeWorldMap()
					return
				}
			} else if (opcode < 5400) {
				if (opcode == ClientScriptOpCode.FULLSCREEN_ENTER) {
					CS2State.intStackPtr -= 2
					var width: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var height: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					WindowModeRequest.requestWindowMode(WindowModeType.FULLSCREEN, width, false, height)
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (WindowMode.fullScreenFrame == null) 0 else 1
					return
				}
				if (opcode == ClientScriptOpCode.FULLSCREEN_EXIT) {
					if (WindowMode.fullScreenFrame != null) {
						WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid!!.fixedScreen!!.getValue()), -1, false, -1)
					}
					return
				}
				if (opcode == ClientScriptOpCode.FULLSCREEN_MODECOUNT) {
					@Pc(2919) var displayModes: Array<DisplayModeDetails?>? = Fullscreen.getActiveDisplayModes()
					ScriptRunner.intStack[CS2State.intStackPtr++] = displayModes!!.size
					return
				}
				if (opcode == ClientScriptOpCode.FULLSCREEN_GETMODE) {
					var modeIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					@Pc(2943) var displayModes: Array<DisplayModeDetails?>? = Fullscreen.getActiveDisplayModes()
					ScriptRunner.intStack[CS2State.intStackPtr++] = displayModes!![modeIndex]!!.width
					ScriptRunner.intStack[CS2State.intStackPtr++] = displayModes!![modeIndex]!!.height
					return
				}
				if (opcode == ClientScriptOpCode.FULLSCREEN_LASTMODE) {
					var width: Int = WindowMode.fullScreenWidth
					var height: Int = WindowMode.fullScreenHeight
					var modeIndex: Int = -1
					@Pc(2978) var displayModes: Array<DisplayModeDetails?>? = Fullscreen.getActiveDisplayModes()
					displayModeIndex = 0
					while (displayModeIndex < displayModes!!.size) {
						@Pc(2985) var displayMode: DisplayModeDetails? = displayModes!![displayModeIndex]
						if (displayMode!!.width == width && displayMode!!.height == height) {
							modeIndex = displayModeIndex
							break
						}
						displayModeIndex++
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = modeIndex
					return
				}
				if (opcode == ClientScriptOpCode.GETWINDOWMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WindowMode.getWindowMode()!!.id
					return
				}
				if (opcode == ClientScriptOpCode.SETWINDOWMODE) {
					var modeId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (modeId >= 1 && modeId <= 2) {
						WindowModeRequest.requestWindowMode(WindowModeType.fromId(modeId), -1, false, -1)
						return
					}
					return
				}
				if (opcode == ClientScriptOpCode.GETDEFAULTWINDOWMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.fixedScreen!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.SETDEFAULTWINDOWMODE) {
					var modeId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (modeId >= 1 && modeId <= 2) {
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.fixedScreen, modeId)
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.fullScreen, modeId)
						GamePreferences.savePreferences()
						return
					}
					return
				}
			} else if (opcode < 5500) {
				if (opcode == ClientScriptOpCode.OPENURL) {
					CS2State.stringStackPtr -= 2
					string = CS2State.stringStack[CS2State.stringStackPtr]
					string3 = CS2State.stringStack[CS2State.stringStackPtr + 1]
					var urlFlag: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					@Pc(3133) var urlPacket: TcpPacket? = TcpPacket.createPacket(ClientProt.OPEN_URL, TcpPacket.isaac)
					urlPacket!!.buffer!!.writeByte(Utils.getEncodedStringLength(string!!) + Utils.getEncodedStringLength(string3!!) + 1)
					urlPacket!!.buffer!!.writeString(string)
					urlPacket!!.buffer!!.writeString(string3)
					urlPacket!!.buffer!!.writeByte(urlFlag)
					BufferedConnectionContext.queuePacket(urlPacket)
					return
				}
				if (opcode == ClientScriptOpCode.SETCLIENTPALETTE) {
					CS2State.intStackPtr -= 2
					TypeLists.recolorPalette!![ScriptRunner.intStack[CS2State.intStackPtr]] = FloType.rgbToHsl(ScriptRunner.intStack[CS2State.intStackPtr + 1]).toShort()
					TypeLists.OBJ!!.modelCacheReset()
					TypeLists.OBJ!!.spriteCacheReset()
					TypeLists.NPC!!.clearModels()
					InterfaceRectangles.invalidateAllRectangles()
					return
				}
				if (opcode == ClientScriptOpCode.SPLINE_NEW) {
					CS2State.intStackPtr -= 2
					var pathIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var pointCount: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					if (pathIndex >= 0 && pathIndex < 2) {
						Camera.cameraSplinePaths[pathIndex] = Array<IntArray?>(pointCount shl 1) { IntArray(4) }
					}
					return
				}
				if (opcode == ClientScriptOpCode.SPLINE_ADDPOINT) {
					CS2State.intStackPtr -= 7
					var pathIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var pointIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1] shl 1
					var pointCoord: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
					var cameraHeight: Int = ScriptRunner.intStack[CS2State.intStackPtr + 3]
					var lookAtCoord: Int = ScriptRunner.intStack[CS2State.intStackPtr + 4]
					@Pc(3271) var lookAtHeight: Int = ScriptRunner.intStack[CS2State.intStackPtr + 5]
					@Pc(3277) var pitch: Int = ScriptRunner.intStack[CS2State.intStackPtr + 6]
					if (pathIndex >= 0 && pathIndex < 2 && Camera.cameraSplinePaths[pathIndex] != null && pointIndex >= 0 && pointIndex < Camera.cameraSplinePaths[pathIndex]!!.size) {
						Camera.cameraSplinePaths!![pathIndex]!![pointIndex] = intArrayOf(((pointCoord shr 14) and 0x3FFF) shl 9, cameraHeight shl 2, (pointCoord and 0x3FFF) shl 9, pitch)
						Camera.cameraSplinePaths!![pathIndex]!![pointIndex + 1] = intArrayOf(((lookAtCoord shr 14) and 0x3FFF) shl 9, lookAtHeight shl 2, (lookAtCoord and 0x3FFF) shl 9)
					}
					return
				}
				if (opcode == ClientScriptOpCode.SPLINE_LENGTH) {
					var pointCount: Int = Camera.cameraSplinePaths[ScriptRunner.intStack[--CS2State.intStackPtr]]!!.size shr 1
					ScriptRunner.intStack[CS2State.intStackPtr++] = pointCount
					return
				}
				if (opcode == ClientScriptOpCode.QUIT) {
					if (WindowMode.fullScreenFrame != null) {
						WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid!!.fixedScreen!!.getValue()), -1, false, -1)
					}
					if (GameShell.engineFrame != null) {
						VarcTypeList.savePersistentVarcs()
						lang.exit(0)
						return
					}
					string = if (GameShell.quitUrl == null) ServerEnvironment.getUrl() else GameShell.quitUrl
					ClientURLTools.openUrl(string, GameShell.signLink, GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.GL, false)
					return
				}
				if (opcode == ClientScriptOpCode.LASTLOGIN) {
					string = ""
					if (LoginManager.lastLoginHostLookup != null) {
						if (LoginManager.lastLoginHostLookup!!.result == null) {
							string = Utils.ipToString(LoginManager.lastLoginHostLookup!!.intData1)
						} else {
							string = (LoginManager.lastLoginHostLookup!!.result as String?)
						}
					}
					CS2State.stringStack[CS2State.stringStackPtr++] = string
					return
				}
				if (opcode == 5420) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GameShell.signLink!!.signed) 0 else 1
					return
				}
				if (opcode == ClientScriptOpCode.OPENURL_NOLOGIN) {
					if (WindowMode.fullScreenFrame != null) {
						WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid!!.fixedScreen!!.getValue()), -1, false, -1)
					}
					string = CS2State.stringStack[--CS2State.stringStackPtr]
					var newWindow: Boolean = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
					string2 = ServerEnvironment.getUrl() + string
					ClientURLTools.openUrl(string2, GameShell.signLink, GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.GL, newWindow)
					return
				}
				if (opcode == 5422) {
					CS2State.stringStackPtr -= 2
					string = CS2State.stringStack[CS2State.stringStackPtr]
					string3 = CS2State.stringStack[CS2State.stringStackPtr + 1]
					var affixIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (string!!.length > 0) {
						if (PlayerEntity.playerNamePrefixes == null) {
							PlayerEntity.playerNamePrefixes = arrayOfNulls<String>(PlayerEntity.playerNameAffixCounts[Game.CURRENT_GAME!!.id])
						}
						PlayerEntity.playerNamePrefixes!![affixIndex] = string
					}
					if (string3!!.length > 0) {
						if (PlayerEntity.playerNameSuffixes == null) {
							PlayerEntity.playerNameSuffixes = arrayOfNulls<String>(PlayerEntity.playerNameAffixCounts[Game.CURRENT_GAME!!.id])
						}
						PlayerEntity.playerNameSuffixes!![affixIndex] = string3
					}
					return
				}
				if (opcode == ClientScriptOpCode.WRITECONSOLE) {
					println(CS2State.stringStack[--CS2State.stringStackPtr])
					return
				}
				if (opcode == ClientScriptOpCode.FORMATMINIMENU) {
					CS2State.intStackPtr -= 11
					MiniMenu.menuBackgroundColour = ScriptRunner.intStack[CS2State.intStackPtr]
					MiniMenu.menuBackgroundTransparency = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					MiniMenu.menuHighlightColour = ScriptRunner.intStack[CS2State.intStackPtr + 2]
					MiniMenu.menuHighlightTransparency = ScriptRunner.intStack[CS2State.intStackPtr + 3]
					MiniMenu.menuHeaderSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 4]
					MiniMenu.menuHeaderCornerSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 5]
					Find.menuBottomBorderSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 6]
					StaticMedia.menuSideBorderSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 7]
					ScriptRunner.menuCornerSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 8]
					MiniMenu.menuTextColour = ScriptRunner.intStack[CS2State.intStackPtr + 9]
					MiniMenu.menuHoverTextColour = ScriptRunner.intStack[CS2State.intStackPtr + 10]
					Resource.SPRITES!!.isFileReady(MiniMenu.menuHeaderSpriteId)
					Resource.SPRITES!!.isFileReady(MiniMenu.menuHeaderCornerSpriteId)
					Resource.SPRITES!!.isFileReady(Find.menuBottomBorderSpriteId)
					Resource.SPRITES!!.isFileReady(StaticMedia.menuSideBorderSpriteId)
					Resource.SPRITES!!.isFileReady(ScriptRunner.menuCornerSpriteId)
					StaticMedia.menuBottomBorderSprite = null
					StaticMedia.menuBottomRightCornerSprite = null
					StaticMedia.menuBottomLeftCornerSprite = null
					MiniMenu.menuHeaderRightCornerSprite = null
					StaticMedia.menuHeaderLeftCornerSprite = null
					MiniMenu.menuHeaderSprite = null
					StaticMedia.menuRightSideBorderSprite = null
					StaticMedia.menuLeftSideBorderSprite = null
					FontMetrics.useBoldFont = true
					return
				}
				if (opcode == ClientScriptOpCode.DEFAULTMINIMENU) {
					MiniMenu.resetMiniMenuSprites()
					FontMetrics.useBoldFont = false
					return
				}
				if (opcode == ClientScriptOpCode.SETDEFAULTCURSORS) {
					CS2State.intStackPtr -= 2
					CursorManager.defaultCursorId = ScriptRunner.intStack[CS2State.intStackPtr]
					CursorManager.defaultAttackCursor = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					return
				}
				if (opcode == ClientScriptOpCode.SETHARDCODEDOPCURSORS) {
					CS2State.intStackPtr -= 2
					MiniMenu.examineCursor = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					return
				}
				if (opcode == ClientScriptOpCode.MINIMENUOPEN) {
					CS2State.intStackPtr -= 2
					var componentHash: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var slot: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (MiniMenu.isOpenForComponent(slot, componentHash)) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DOCHEAT) {
					// Not part of the original client: a script's command never runs a developer command here.
					ClientCommandProcessor.executeScriptCommand(CS2State.stringStack[--CS2State.stringStackPtr])
					return
				}
				if (opcode == ClientScriptOpCode.NOTIFY_ACCOUNTCREATED) {
					try {
						BrowserJavaScript.callJavaScript(GameShell.applet, "accountcreated")
						return
					} catch (@Pc(3819) ex: Throwable) {
						return
					}
				}
				if (opcode == ClientScriptOpCode.NOTIFY_ACCOUNTCREATESTARTED) {
					try {
						BrowserJavaScript.callJavaScript(GameShell.applet, "accountcreatestarted")
						return
					} catch (@Pc(3830) ex2: Throwable) {
						return
					}
				}
				if (opcode == ClientScriptOpCode.GETCLIPBOARD) {
					string = ""
					if (Console.clipboard != null) {
						@Pc(3842) var transferable: Transferable? = Console.clipboard!!.getContents(null)
						if (transferable != null) {
							try {
								string = (transferable.getTransferData(awt.datatransfer.STRING_FLAVOR) as String?)
								if (string == null) {
									string = ""
								}
							} catch (@Pc(3855) ex3: Exception) {
							}
						}
					}
					CS2State.stringStack[CS2State.stringStackPtr++] = string
					return
				}
				if (opcode == ClientScriptOpCode.SETSUBMENUMINLENGTH) {
					MiniMenu.maxVisibleEntries = ScriptRunner.intStack[--CS2State.intStackPtr]
					return
				}
			} else if (opcode < 5600) {
				if (opcode == ClientScriptOpCode.CAM_MOVETO) {
					CS2State.intStackPtr -= 4
					var coord: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var height: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					var constantSpeed: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
					var variableSpeed: Int = ScriptRunner.intStack[CS2State.intStackPtr + 3]
					Camera.moveCamera(constantSpeed, ((coord shr 14) and 0x3FFF) - MapRegion.sceneBaseX, false, height shl 2, variableSpeed, (coord and 0x3FFF) - MapRegion.sceneBaseY)
					return
				}
				if (opcode == 5501) {
					CS2State.intStackPtr -= 4
					var coord: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var height: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					var constantSpeed: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
					var variableSpeed: Int = ScriptRunner.intStack[CS2State.intStackPtr + 3]
					Camera.lookAt((coord and 0x3FFF) - MapRegion.sceneBaseY, ((coord shr 14) and 0x3FFF) - MapRegion.sceneBaseX, height shl 2, variableSpeed, constantSpeed)
					return
				}
				if (opcode == ClientScriptOpCode.CAM_MOVEALONG) {
					CS2State.intStackPtr -= 6
					var positionPathIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					if (positionPathIndex >= 2) {
						throw RuntimeException()
					}
					Camera.cameraPositionPathIndex = positionPathIndex
					var positionKeyframe: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					if (positionKeyframe + 1 >= Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!!.size shr 1) {
						throw RuntimeException()
					}
					Camera.cameraPositionKeyframe = positionKeyframe
					Camera.cameraSplineProgress = 0
					Camera.cameraSplineSpeedStart = ScriptRunner.intStack[CS2State.intStackPtr + 2]
					Camera.cameraSplineSpeedEnd = ScriptRunner.intStack[CS2State.intStackPtr + 3]
					var lookAtPathIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 4]
					if (lookAtPathIndex >= 2) {
						throw RuntimeException()
					}
					ColourUtils.cameraLookAtPathIndex = lookAtPathIndex
					var lookAtKeyframe: Int = ScriptRunner.intStack[CS2State.intStackPtr + 5]
					if (lookAtKeyframe + 1 >= Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex]!!.size shr 1) {
						throw RuntimeException()
					}
					Camera.cameraLookAtKeyframe = lookAtKeyframe
					Camera.cameraCalculationType = CameraMode.MODE_SPLINE
					Camera.roofRemovalTargetY = -1
					Camera.roofRemovalTargetX = -1
					return
				}
				if (opcode == ClientScriptOpCode.CAM_RESET) {
					Camera.reset()
					return
				}
				if (opcode == ClientScriptOpCode.CAM_FORCEANGLE) {
					CS2State.intStackPtr -= 2
					Camera.forceCameraAngle(ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1])
					return
				}
				if (opcode == ClientScriptOpCode.CAM_GETANGLE_XA) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.camAngleX.toInt() shr 3
					return
				}
				if (opcode == ClientScriptOpCode.CAM_GETANGLE_YA) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.camAngleY.toInt() shr 3
					return
				}
				if (opcode == ClientScriptOpCode.CAM_INC_Y) {
					Camera.applyCameraPitchUp()
					return
				}
				if (opcode == ClientScriptOpCode.CAM_DEC_Y) {
					Camera.applyCameraPitchDown()
					return
				}
				if (opcode == ClientScriptOpCode.CAM_INC_X) {
					Camera.applyCameraYawRight()
					return
				}
				if (opcode == ClientScriptOpCode.CAM_DEC_X) {
					Camera.applyCameraYawLeft()
					return
				}
				if (opcode == ClientScriptOpCode.CAM_FOLLOWCOORD) {
					var coord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					var localX: Int = (coord shr 14) and 0x3FFF
					var sceneZ: Int = coord and 0x3FFF
					localX -= MapRegion.sceneBaseX
					if (localX < 0) {
						localX = 0
					} else if (localX >= MapRegion.sceneSizeX) {
						localX = MapRegion.sceneSizeX
					}
					sceneZ -= MapRegion.sceneBaseY
					if (sceneZ < 0) {
						sceneZ = 0
					} else if (sceneZ >= MapRegion.sceneSizeY) {
						sceneZ = MapRegion.sceneSizeY
					}
					Camera.cameraTargetAbsX = (localX shl 9) + 256
					Camera.cameraTargetAbsZ = (sceneZ shl 9) + 256
					Camera.cameraCalculationType = CameraMode.MODE_FOLLOWCOORD
					Camera.roofRemovalTargetY = -1
					Camera.roofRemovalTargetX = -1
					return
				}
				if (opcode == ClientScriptOpCode.CAM_SMOOTH_RESET) {
					Camera.startSmoothTransition()
					return
				}
				if (opcode == 5514) {
					OrthoMode.orthoZoom = ScriptRunner.intStack[--CS2State.intStackPtr]
					return
				}
				if (opcode == 5516) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = OrthoMode.orthoZoom
					return
				}
				if (opcode == ClientScriptOpCode.CAM_REMOVEROOF) {
					var coord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (coord == -1) {
						var localX: Int = (coord shr 14) and 0x3FFF
						var sceneZ: Int = coord and 0x3FFF
						localX -= MapRegion.sceneBaseX
						if (localX < 0) {
							localX = 0
						} else if (localX >= MapRegion.sceneSizeX) {
							localX = MapRegion.sceneSizeX
						}
						sceneZ -= MapRegion.sceneBaseY
						if (sceneZ < 0) {
							sceneZ = 0
						} else if (sceneZ >= MapRegion.sceneSizeY) {
							sceneZ = MapRegion.sceneSizeY
						}
						Camera.roofRemovalTargetX = (localX shl 9) + 256
						Camera.roofRemovalTargetY = (sceneZ shl 9) + 256
						return
					}
					Camera.roofRemovalTargetX = -1
					Camera.roofRemovalTargetY = -1
					return
				}
				if (opcode == 5547 && ClientFeatures.VOID) {
					// Not part of the original client. Void's scripts ask whether the camera is in its default mode.
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (Camera.cameraCalculationType == CameraMode.MODE_DEFAULT) 1 else 0
					return
				}
			} else if (opcode < 5700) {
				if (opcode == ClientScriptOpCode.LOGIN_REQUEST) {
					CS2State.stringStackPtr -= 2
					string = CS2State.stringStack[CS2State.stringStackPtr]
					string3 = CS2State.stringStack[CS2State.stringStackPtr + 1]
					var loginFlag: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (string!!.length > 320) {
						return
					}
					if (ClientState.mainLogicStep !== MainLogicStep.STEP_LOGIN_SCREEN) {
						return
					}
					if (LoginManager.loginStep === LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						LoginManager.loginUsername = string
						LoginManager.loginPassword = string3
						LoginManager.anInt6172 = loginFlag
						MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_GAME)
						return
					}
					return
				}
				if (opcode == ClientScriptOpCode.LOGIN_CONTINUE) {
					LoginManager.confirmLoginContinue()
					return
				}
				if (opcode == ClientScriptOpCode.LOGIN_RESETREPLY) {
					if (LoginManager.loginStep === LoginStep.IDLE) {
						LoginManager.lobbyLoginResponse = -2
						LoginManager.worldLoginResponse = -2
					}
					return
				}
				if (opcode == ClientScriptOpCode.CREATE_AVAILABLEREQUEST) {
					CS2State.stringStackPtr--
					if (ClientState.mainLogicStep !== MainLogicStep.STEP_LOGIN_SCREEN) {
						return
					}
					if (LoginManager.loginStep === LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						LobbyManager.sendCreateAvailableRequest(CS2State.stringStack[CS2State.stringStackPtr])
						return
					}
					return
				}
				if (opcode == ClientScriptOpCode.CREATE_CONNECTREQUEST) {
					CS2State.stringStackPtr -= 2
					CS2State.intStackPtr -= 2
					if (ClientState.mainLogicStep !== MainLogicStep.STEP_LOGIN_SCREEN) {
						return
					}
					if (LoginManager.loginStep === LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						LobbyManager.sendCreateConnectRequest(CS2State.stringStack[CS2State.stringStackPtr + 1], CS2State.stringStack[CS2State.stringStackPtr], ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1)
						return
					}
					return
				}
				if (opcode == 5606) {
					if (LobbyManager.accountCreationStep == 0) {
						LobbyManager.accountCreationReply = -2
					}
					return
				}
				if (opcode == ClientScriptOpCode.LOGIN_REPLY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.worldLoginResponse
					return
				}
				if (opcode == ClientScriptOpCode.LOGIN_HOPTIME) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.loginHopTime
					return
				}
				if (opcode == ClientScriptOpCode.CREATE_REPLY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LobbyManager.accountCreationReply
					return
				}
				if (opcode == ClientScriptOpCode.LOGIN_DISALLOWRESULT) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginDisallowReason.loginDisallowReason
					return
				}
				if (opcode == ClientScriptOpCode.LOBBY_ENTERGAME) {
					var loginFlag: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (ClientState.mainLogicStep !== MainLogicStep.STEP_LOBBY_SCREEN) {
						return
					}
					if (LoginManager.loginStep === LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						if (Connection.connection != null) {
							Connection.connection!!.close()
							Connection.connection = null
						}
						LoginManager.anInt6172 = loginFlag
						MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGGING_IN_FROM_LOBBYSCREEN_TO_GAME)
						return
					}
					return
				}
				if (opcode == ClientScriptOpCode.LOBBY_ENTERGAME_REPLY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.worldLoginResponse
					return
				}
				if (opcode == ClientScriptOpCode.LOBBY_ENTERLOBBY) {
					CS2State.stringStackPtr -= 2
					string = CS2State.stringStack[CS2State.stringStackPtr]
					string3 = CS2State.stringStack[CS2State.stringStackPtr + 1]
					if (string!!.length > 320) {
						return
					}
					if (ClientState.mainLogicStep !== MainLogicStep.STEP_LOGIN_SCREEN) {
						return
					}
					if (LoginManager.loginStep === LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						if (Connection.connection != null) {
							Connection.connection!!.close()
							Connection.connection = null
						}
						LoginManager.loginUsername = string
						LoginManager.loginPassword = string3
						MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_LOBBY)
						return
					}
					return
				}
				if (opcode == ClientScriptOpCode.LOBBY_LEAVELOBBY) {
					ClientLogout.logout(false)
					return
				}
				if (opcode == ClientScriptOpCode.LOBBY_ENTERLOBBYREPLY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lobbyLoginResponse
					return
				}
				if (opcode == 5618) {
					CS2State.intStackPtr--
					return
				}
				if (opcode == 5619) {
					CS2State.intStackPtr--
					return
				}
				if (opcode == 5620) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					return
				}
				if (opcode == 5621) {
					CS2State.stringStackPtr -= 2
					CS2State.intStackPtr -= 2
					return
				}
				if (opcode == 5622) {
					return
				}
				if (opcode == 5623) {
					if (LoginManager.ssKey != null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = 1
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					return
				}
				if (opcode == ClientScriptOpCode.USERFLOW_FLAGS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = (GameShell.machineUid shr 32).toInt()
					ScriptRunner.intStack[CS2State.intStackPtr++] = (GameShell.machineUid and 0xFFFFL).toInt()
					return
				}
				if (opcode == ClientScriptOpCode.CREATE_UNDER13) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (LobbyManager.isUnder13) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.CREATE_SETUNDER13) {
					LobbyManager.isUnder13 = true
					LobbyManager.setUnder13Cookie()
					return
				}
			} else if (opcode < 6100) {
				if (opcode == ClientScriptOpCode.DETAIL_BRIGHTNESS) {
					var brightness: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.brightness, brightness)
					MapRebuild.rebuildMap()
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				@Pc(4786) var enabled: Boolean
				if (opcode == 6002) {
					enabled = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.animateBackground, if (enabled) 1 else 0)
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.animateBackgroundOverride, if (enabled) 1 else 0)
					MapRebuild.rebuildMap()
					Scene.setRoofOverride()
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_REMOVEROOFS_OPTION) {
					enabled = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.removeRoofs, if (enabled) 2 else 1)
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.lb, if (enabled) 2 else 1)
					Scene.setRoofOverride()
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_GROUNDDECOR_ON) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.groundDecor, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 1 else 0)
					MapRebuild.rebuildMap()
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_IDLEANIMS_MANY) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.idleAnimations, ScriptRunner.intStack[--CS2State.intStackPtr])
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_FLICKERING_ON) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.flickeringEffects, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 1 else 0)
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_SPOTSHADOWS_ON) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.characterShadows, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 1 else 0)
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_HARDSHADOWS) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.hardShadows, ScriptRunner.intStack[--CS2State.intStackPtr])
					MapRebuild.rebuildMap()
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_LIGHTDETAIL_HIGH) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.lightDetail, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 1 else 0)
					GraphicCacheReset.resetGraphicCaches()
					EnvironmentManager.requestDirectEnvironmentTransition()
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_WATERDETAIL_HIGH) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.water, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 2 else 0)
					MapRebuild.rebuildMap()
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_FOG_ON) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.fog, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 1 else 0)
					MapRebuild.rebuildMap()
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_ANTIALIASING_QUALITY) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.antiAliasingPreview, ScriptRunner.intStack[--CS2State.intStackPtr])
					ToolkitSwitch.loadAssets(false, GamePreferences.uid!!.toolkit!!.getValue())
					GamePreferences.savePreferences()
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_STEREO) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.monoStereo, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 1 else 0)
					SoundManager.reloadSoundSystem()
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_SOUNDVOL) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.soundEffectVolume, ScriptRunner.intStack[--CS2State.intStackPtr])
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_MUSICVOL) {
					var musicVolume: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					var currentVolume: Int = GamePreferences.uid!!.ingameMusicVolume!!.getValue()
					if (musicVolume != currentVolume) {
						if (MainLogicManager.isInGameStep(ClientState.mainLogicStep)) {
							if (currentVolume == 0 && AreaSoundPlayer.currentMusicArchive != -1) {
								PlayMusic.playMusic(AreaSoundPlayer.currentMusicArchive, Resource.MUSIC, musicVolume)
								MidiChannelVolumeReset.resetChannelVolumes()
								SoundEffectPlayer.soundJinglePlaying = false
							} else if (musicVolume == 0) {
								SoundResourceManager.stopMusic()
								SoundEffectPlayer.soundJinglePlaying = false
							} else {
								SoundEffectPlayer.setMusicVolume(musicVolume)
							}
						}
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.ingameMusicVolume, musicVolume)
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
					}
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_BGSOUNDVOL) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.ambientSoundVolume, ScriptRunner.intStack[--CS2State.intStackPtr])
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_REMOVEROOFS_OPTION_OVERRIDE) {
					var removeRoofsValue: Int = GamePreferences.uid!!.removeRoofs!!.getValue()
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.lb, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 0 else removeRoofsValue)
					Scene.setRoofOverride()
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_PARTICLES) {
					var particlesLevel: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.particles, particlesLevel)
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_ANTIALIASING_DEFAULT) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.antiAliasing, ScriptRunner.intStack[--CS2State.intStackPtr])
					GamePreferences.savePreferences()
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_BUILDAREA) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.buildArea, ScriptRunner.intStack[--CS2State.intStackPtr])
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_BLOOM) {
					var bloomSetting: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (bloomSetting < 0 || bloomSetting > 1) {
						bloomSetting = 0
					}
					GamePreferences.setBloom(bloomSetting == 1)
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_CUSTOMCURSORS) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.customCursor, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 0) 0 else 1)
					GamePreferences.savePreferences()
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_IDLEANIMS) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.idleAnimations, ScriptRunner.intStack[--CS2State.intStackPtr])
					GamePreferences.savePreferences()
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_GROUNDBLOUNDING) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.groundBlending, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 0) 0 else 1)
					GamePreferences.savePreferences()
					MapRebuild.rebuildMap()
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_TOOLKIT) {
					var toolkitId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (toolkitId < ToolkitType.JAVA || toolkitId > ToolkitType.GLX) {
						toolkitId = ToolkitType.SSE
					}
					ToolkitSwitch.loadAssets(false, toolkitId)
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_TOOLKIT_DEFAULT) {
					CS2State.intStackPtr -= 2
					var toolkitId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var keepGraphicsPreset: Boolean = ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.toolkitDefault, toolkitId)
					if (!keepGraphicsPreset) {
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.graphics, 0)
					}
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_CPUUSAGE) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.cpu, ScriptRunner.intStack[--CS2State.intStackPtr])
					GamePreferences.savePreferences()
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_TEXTURING) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.textures, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 1 else 0)
					GamePreferences.savePreferences()
					GraphicCacheReset.resetGraphicCaches()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_ANIMDETAIL) {
					var animateBackgroundValue: Int = GamePreferences.uid!!.animateBackground!!.getValue()
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.animateBackgroundOverride, if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) 1 else animateBackgroundValue)
					MapRebuild.rebuildMap()
					Scene.setRoofOverride()
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_MAXSCREENSIZE) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.maxScreenSize, ScriptRunner.intStack[--CS2State.intStackPtr])
					GamePreferences.savePreferences()
					WindowMode.needsResize = true
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_SPEECHVOL) {
					GamePreferences.uid!!.setValue(GamePreferences.uid!!.voiceOverVolume, ScriptRunner.intStack[--CS2State.intStackPtr])
					GamePreferences.savePreferences()
					GamePreferences.preferencesSavedOnServer = false
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_LOGINVOL) {
					var loginVolume: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					var currentVolume: Int = GamePreferences.uid!!.musicVolume!!.getValue()
					if (loginVolume != currentVolume && AreaSoundPlayer.currentMusicArchive == DefaultsFile.fileId) {
						if (!MainLogicManager.isInGameStep(ClientState.mainLogicStep)) {
							if (currentVolume == 0) {
								PlayMusic.playMusic(AreaSoundPlayer.currentMusicArchive, Resource.MUSIC, loginVolume)
								MidiChannelVolumeReset.resetChannelVolumes()
								SoundEffectPlayer.soundJinglePlaying = false
							} else if (loginVolume == 0) {
								SoundResourceManager.stopMusic()
								SoundEffectPlayer.soundJinglePlaying = false
							} else {
								SoundEffectPlayer.setMusicVolume(loginVolume)
							}
						}
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.musicVolume, loginVolume)
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
					}
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_LOADINGSCREENTYPE) {
					var loadingScreenType: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (loadingScreenType > 255 || loadingScreenType < 0) {
						loadingScreenType = 0
					}
					if (loadingScreenType != GamePreferences.uid!!.gametipStartStagePreference!!.getValue()) {
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.gametipStartStagePreference, loadingScreenType)
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
					}
					return
				}
				if (opcode == ClientScriptOpCode.DETAIL_ORTHOGRAPHIC) {
					var orthographicValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					if (orthographicValue != GamePreferences.uid!!.orthographic!!.getValue()) {
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.orthographic, orthographicValue)
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
						OrthoMode.applyOrthographicMode()
					}
					return
				}
			} else if (opcode < 6200) {
				if (opcode == ClientScriptOpCode.DETAILGET_BRIGHTNESS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.brightness!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_ANIMDETAIL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.animateBackground!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_REMOVEROOFS_OPTIONS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.removeRoofs!!.getValue() == 2) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_GROUNDDECOR_ON) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.groundDecor!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_IDLEANIMS_MANY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.idleAnimations!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_FLICKERING_ON) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.flickeringEffects!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_SPOTSHADOWS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.characterShadows!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_HARDSHADOWS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.hardShadows!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_LIGHTDETAIL_HIGH) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.lightDetail!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_WATERDETAIL_HIGH) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.water!!.getValue() == 2) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_FOG_ON) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.fog!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_ANTIALIASING_QUALITY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.antiAliasingPreview!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_STEREO) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.monoStereo!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_SOUNDVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.soundEffectVolume!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_MUSICVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.ingameMusicVolume!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_BGSOUNDVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.ambientSoundVolume!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_PARTICLES) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = ChatHistory.getParticleDetailLevel()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_ANTIALIASING) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.antiAliasing!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_BUILDAREA) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.buildArea!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_BLOOM) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.bloom!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_CUSTOMCURSORS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.customCursor!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_IDLEANIMS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.idleAnimations!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_GROUNDBLENDING) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.groundBlending!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_TOOLKIT) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.toolkit!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_TOOLKIT_DEFAULT) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.toolkitDefault!!.getValue()
					return
				}
				if (opcode == 6133) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GameShell.signLink!!.signed && !GameShell.signLink!!.microsoftJava) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_CPUUSAGE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.cpu!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_TEXTURING) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.textures!!.getValue() == 1) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_PERFORMANCE_METRIC) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = ToolkitProfiler.getProfilingPerformance(GamePreferences.uid!!.toolkit!!.getValue(), 200)
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_MAXSCREENSIZE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.maxScreenSize!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_SPEECHVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.voiceOverVolume!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_LOGINVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.musicVolume!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_SAFEMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (client.isForcedSafemode) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_LOADINGSCREENTYPE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.gametipStartStagePreference!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_ORTHOGRAPHIC) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.orthographic!!.getValue()
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_CANCHOOSESAFEMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (SystemInfo.SYSTEM_INFO!!.ram < 512 || client.isForcedSafemode || ToolkitOption.renderWithCpu) 1 else 0
					return
				}
				if (opcode == ClientScriptOpCode.DETAILGET_CHOSESAFEMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (LoadingStage.safeModeChosenAtStartup) 1 else 0
					return
				}
			} else if (opcode < 6300) {
				if (opcode == ClientScriptOpCode.VIEWPORT_SETFOV) {
					CS2State.intStackPtr -= 2
					Camera.viewportFovMin = ScriptRunner.intStack[CS2State.intStackPtr].toShort()
					if (Camera.viewportFovMin <= 0) {
						Camera.viewportFovMin = 256
					}
					Camera.viewportFovMax = ScriptRunner.intStack[CS2State.intStackPtr + 1].toShort()
					if (Camera.viewportFovMax <= 0) {
						Camera.viewportFovMax = 205
					}
					return
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_SETZOOM) {
					CS2State.intStackPtr -= 2
					Camera.cameraZoomMin = ScriptRunner.intStack[CS2State.intStackPtr].toShort()
					if (Camera.cameraZoomMin <= 0) {
						Camera.cameraZoomMin = 256
					}
					Camera.cameraZoomMax = ScriptRunner.intStack[CS2State.intStackPtr + 1].toShort()
					if (Camera.cameraZoomMax <= 0) {
						Camera.cameraZoomMax = 320
					}
					return
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_CLAMPFOV) {
					CS2State.intStackPtr -= 4
					Camera.fovScaleMin = ScriptRunner.intStack[CS2State.intStackPtr].toShort()
					if (Camera.fovScaleMin <= 0) {
						Camera.fovScaleMin = 1
					}
					Camera.fovScaleMax = ScriptRunner.intStack[CS2State.intStackPtr + 1].toShort()
					if (Camera.fovScaleMax <= 0) {
						Camera.fovScaleMax = 32767
					} else if (Camera.fovScaleMax < Camera.fovScaleMin) {
						Camera.fovScaleMax = Camera.fovScaleMin
					}
					Camera.aspectFovMin = ScriptRunner.intStack[CS2State.intStackPtr + 2].toShort()
					if (Camera.aspectFovMin <= 0) {
						Camera.aspectFovMin = 1
					}
					Camera.aspectFovMax = ScriptRunner.intStack[CS2State.intStackPtr + 3].toShort()
					if (Camera.aspectFovMax <= 0) {
						Camera.aspectFovMax = 32767
						return
					}
					if (Camera.aspectFovMax < Camera.aspectFovMin) {
						Camera.aspectFovMax = Camera.aspectFovMin
					}
					return
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_GETEFFECTIVESIZE) {
					AbstractToolkit.calculateGameArea(0, 0, InterfaceManager.gameScreenComponent!!.width, false, InterfaceManager.gameScreenComponent!!.height)
					ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.gameAreaWidth
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.gameAreaHeight
					return
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_GETZOOM) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.cameraZoomMin.toInt()
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.cameraZoomMax.toInt()
					return
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_GETFOV) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.viewportFovMin.toInt()
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.viewportFovMax.toInt()
					return
				}
			} else if (opcode < 6400) {
				if (opcode == ClientScriptOpCode.DATE_MINUTES) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = (Utils.currentTimeMillis() / TimeUtils.MILLISECONDS_PER_MINUTE).toInt()
					return
				}
				if (opcode == ClientScriptOpCode.DATE_RUNEDAY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = (Utils.currentTimeMillis() / TimeUtils.MILLISECONDS_PER_DAY).toInt() - TimeUtils.RUNEDAYS_SINCE_UNIX_EPOCH
					return
				}
				if (opcode == ClientScriptOpCode.DATE_RUNEDAY_FROMDATE) {
					CS2State.intStackPtr -= 3
					var day: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var month: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					var year: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
					CS2State.LOCAL_CALENDAR.clear()
					CS2State.LOCAL_CALENDAR.set(11, 12)
					CS2State.LOCAL_CALENDAR.set(year, month, day)
					var runeDay: Int = (CS2State.LOCAL_CALENDAR.getTime().getTime() / 86400000L).toInt() - 11745
					if (year < 1970) {
						runeDay--
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = runeDay
					return
				}
				if (opcode == ClientScriptOpCode.DATE_YEAR) {
					CS2State.LOCAL_CALENDAR.clear()
					CS2State.LOCAL_CALENDAR.setTime(Date(Utils.currentTimeMillis()))
					ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.LOCAL_CALENDAR.get(1)
					return
				}
				if (opcode == ClientScriptOpCode.DATE_ISLEAPYEAR) {
					var year: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					var isLeapYear: Boolean = true
					if (year < 0) {
						isLeapYear = (year + 1) % 4 == 0
					} else if (year < 1582) {
						isLeapYear = year % 4 == 0
					} else if (year % 4 != 0) {
						isLeapYear = false
					} else if (year % 100 != 0) {
						isLeapYear = true
					} else if (year % 400 != 0) {
						isLeapYear = false
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (isLeapYear) 1 else 0
					return
				}
			} else if (opcode < 6500) {
				if (opcode == 6405) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (BrowserJavaScript.showVideoAd()) 1 else 0
					return
				}
				if (opcode == 6406) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = if (BrowserJavaScript.isNotShowingVideoAd()) 1 else 0
					return
				}
			} else if (opcode < 6600) {
				if (opcode == ClientScriptOpCode.WORLDLIST_FETCH) {
					if (ClientState.mainLogicStep === MainLogicStep.STEP_LOBBY_SCREEN && LoginManager.loginStep === LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						if (WorldConnection.requestingWorldList) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 0
							return
						}
						if (WorldConnection.worldListReceivedTime > Utils.currentTimeMillis() - 1000L) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 1
							return
						}
						WorldConnection.requestingWorldList = true
						packet = TcpPacket.createPacket(ClientProt.REQUEST_WORLD_LIST, TcpPacket.isaac)
						packet.buffer!!.writeInt(WorldDescriptor.port)
						BufferedConnectionContext.queuePacket(packet)
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1
					return
				}
				@Pc(6862) var country: Country?
				@Pc(6829) var gameWorld: WorldDescriptor?
				if (opcode == ClientScriptOpCode.WORLDLIST_START) {
					gameWorld = WorldDescriptor.getFirstWorldDescriptor()
					if (gameWorld != null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.worldNumber
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.flags
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld.address
						country = gameWorld.getCountry()
						ScriptRunner.intStack[CS2State.intStackPtr++] = country!!.flag
						CS2State.stringStack[CS2State.stringStackPtr++] = country!!.name
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.population
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.ping
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld.activity
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					return
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_NEXT) {
					gameWorld = WorldConnection.getNextWorldDescriptor()
					if (gameWorld != null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.worldNumber
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.flags
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld.address
						country = gameWorld.getCountry()
						ScriptRunner.intStack[CS2State.intStackPtr++] = country!!.flag
						CS2State.stringStack[CS2State.stringStackPtr++] = country!!.name
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.population
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.ping
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld.activity
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					return
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_SWITCH) {
					var worldId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					string3 = CS2State.stringStack[--CS2State.stringStackPtr]
					if (ClientState.mainLogicStep === MainLogicStep.STEP_LOBBY_SCREEN && LoginManager.loginStep === LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GameHostSelection.setGameHost(string3, worldId)) 1 else 0
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					return
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_SPECIFIC) {
					var worldId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
					@Pc(7189) var gameWorld2: WorldDescriptor? = WorldMeta.getWorldDescriptor(worldId)
					if (gameWorld2 != null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld2.flags
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld2.address
						@Pc(7213) var country2: Country? = gameWorld2.getCountry()
						ScriptRunner.intStack[CS2State.intStackPtr++] = country2!!.flag
						CS2State.stringStack[CS2State.stringStackPtr++] = country2!!.name
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld2.population
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld2.ping
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld2.activity
						return
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0
					CS2State.stringStack[CS2State.stringStackPtr++] = ""
					return
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_SORT) {
					CS2State.intStackPtr -= 4
					var sortMode: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var ascending: Boolean = ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1
					var secondarySortMode: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
					var secondaryAscending: Boolean = ScriptRunner.intStack[CS2State.intStackPtr + 3] == 1
					WorldListSorter.sortWorldList(secondaryAscending, secondarySortMode, sortMode, ascending)
					return
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_AUTOWORLD) {
					HostPreparation.prepareHost()
					return
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_PINGWORLDS) {
					if (ClientState.mainLogicStep !== MainLogicStep.STEP_LOBBY_SCREEN) {
						return
					}
					MiniMenu.debugOps = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
					return
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_SPECIFIC_THISWORLD) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.worldFlags
					return
				}
			} else if (opcode >= 6700) {
				if (opcode < 6800 && client.buildEnvironment === ModeWhat.WIP) {
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETOPENIFCOUNT) {
						var openCount: Int = InterfaceManager.OPEN_INTERFACES.size()
						if (ClientState.BASE_WINDOW_ID != -1) {
							openCount++
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = openCount
						return
					}
					if (opcode == ClientScriptOpCode.IF_GETTOP) {
						var ifIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (ClientState.BASE_WINDOW_ID != -1) {
							if (ifIndex == 0) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = ClientState.BASE_WINDOW_ID
								return
							}
							ifIndex--
						}
						@Pc(7461) var subInterface: SubInterface? = (InterfaceManager.OPEN_INTERFACES.first() as SubInterface?)
						while (ifIndex-- > 0) {
							subInterface = (InterfaceManager.OPEN_INTERFACES.next() as SubInterface?)
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = subInterface!!.interfaceId
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETNAME) {
						var interfaceId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (Interface.interfaces!![interfaceId] == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = ""
							return
						}
						string3 = Interface.interfaces!![interfaceId]!![0]!!.name
						if (string3 == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = ""
							return
						}
						CS2State.stringStack[CS2State.stringStackPtr++] = string3.substring(0, string3.indexOf(58.toChar()))
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETCOMCOUNT) {
						var interfaceId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (Interface.interfaces!![interfaceId] == null) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 0
							return
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = Interface.interfaces!![interfaceId]!!.size
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETCOMNAME) {
						CS2State.intStackPtr -= 2
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						if (Interface.interfaces!![interfaceId] == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = ""
							return
						}
						string2 = Interface.interfaces!![interfaceId]!![componentId]!!.name
						if (string2 == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = ""
							return
						}
						CS2State.stringStack[CS2State.stringStackPtr++] = string2
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETSERVERTRIGGERS) {
						CS2State.intStackPtr -= 2
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						if (Interface.interfaces!![interfaceId] == null) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 0
							return
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = Interface.interfaces!![interfaceId]!![componentId]!!.serverTriggers
						return
					}
					if (opcode == 6706) {
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON1) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 1, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON2) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 2, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON3) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 3, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON4) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 4, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON5) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 5, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON6) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 6, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON7) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 7, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON8) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 8, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON9) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 9, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON10) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 10, (interfaceId shl 16) or componentId)
						return
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_TARGET) {
						CS2State.intStackPtr -= 3
						var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var componentId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						var slotId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						@Pc(8049) var component: Component? = Component.getComponentWithSlot(slotId, (interfaceId shl 16) or componentId)
						Static657.cancelInterfaceSelection()
						@Pc(8054) var serverActiveProperties: IFEvents? = InterfaceManager.getComponentSettings(component)
						InterfaceSelectionStart.startInterfaceSelection(serverActiveProperties!!.interfaceId, serverActiveProperties!!.getUseOptionFlags(), component)
						return
					}
				} else if (opcode < 6900) {
					@Pc(8082) var mapElementType: MECType?
					if (opcode == ClientScriptOpCode.MEC_TEXT) {
						var mecId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						mapElementType = TypeLists.MEC!!.list(mecId)
						if (mapElementType.areaName == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = ""
							return
						}
						CS2State.stringStack[CS2State.stringStackPtr++] = mapElementType.areaName
						return
					}
					if (opcode == ClientScriptOpCode.MEC_SPRITE) {
						var mecId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						mapElementType = TypeLists.MEC!!.list(mecId)
						ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementType.defaultIconArchive
						return
					}
					if (opcode == ClientScriptOpCode.MEC_TEXTSIZE) {
						var mecId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						mapElementType = TypeLists.MEC!!.list(mecId)
						ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementType.baseTextzoom
						return
					}
					if (opcode == ClientScriptOpCode.MEC_CATEGORY) {
						var mecId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						mapElementType = TypeLists.MEC!!.list(mecId)
						ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementType.categoryId
						return
					}
					if (opcode == ClientScriptOpCode.MEC_PARAM) {
						CS2State.intStackPtr -= 2
						var mecId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						@Pc(8204) var paramType: ParamType? = TypeLists.PARAMS!!.list(paramId)
						if (paramType!!.isString()) {
							CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.MEC!!.list(mecId).getStringParam(paramType!!.typeName, paramId)
							return
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.MEC!!.list(mecId).getIntParam(paramType!!.defaultInt, paramId)
						return
					}
				} else if (opcode < 7000) {
					if (opcode == ClientScriptOpCode.USERDETAIL_QUICKCHAT) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_MEMBERSHIP) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = client.lobbyMembership
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_RECOVERYDAY) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.recoveryQuestionsSetDay
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_UNREADMESSAGES) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.unreadMessages
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_LASTLOGINDAY) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lastLoginDay
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_LASTLOGINADDRESS) {
						string = ""
						if (LoginManager.lastLoginHostLookup != null) {
							if (LoginManager.lastLoginHostLookup!!.result == null) {
								string = Utils.ipToString(LoginManager.lastLoginHostLookup!!.intData1)
							} else {
								string = (LoginManager.lastLoginHostLookup!!.result as String?)
							}
						}
						CS2State.stringStack[CS2State.stringStackPtr++] = string
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_EMAILSTATUS) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = EmailStatus.emailStatus
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_CCEXPIRY) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lobbyCreditCardExpiry
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_GRACEEXPIRY) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lobbyGraceExpiry
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_DOBREQUESTED) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (LoginManager.isDobRequested) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.UDETAIL_DOB) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.playerDob
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_MEMBERSSTATS) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LobbyManager.lobbyMembershipStatus
						return
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_PLAYAGE) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lobbyPlayAge
						return
					}
				} else if (opcode < 7100) {
					if (opcode == ClientScriptOpCode.AUTOSETUP_DOSETUP) {
						var graphicsLevel: Int = GraphicsAutoDetect.calculateGraphics()
						val autoSetupToolkit: Int = GamePreferences.uid!!.toolkit!!.getValue()
						ToolkitOption.autoSetupToolkit = autoSetupToolkit
						ScriptRunner.intStack[CS2State.intStackPtr++] = autoSetupToolkit
						ScriptRunner.intStack[CS2State.intStackPtr++] = graphicsLevel
						MapRebuild.rebuildMap()
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
						return
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETHIGH) {
						HighDetailPreset.loadHighDetailPreferences()
						MapRebuild.rebuildMap()
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
						return
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETMEDIUM) {
						MediumDetailPreset.loadMediumDetailPreferences()
						MapRebuild.rebuildMap()
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
						return
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETLOW) {
						LowDetailPreset.loadLowDetailPreferences()
						MapRebuild.rebuildMap()
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
						return
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETMIN) {
						MinDetailPreset.loadMinDetailPreferences()
						MapRebuild.rebuildMap()
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
						return
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETCUSTOM) {
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.graphics, 0)
						GamePreferences.savePreferences()
						GamePreferences.preferencesSavedOnServer = false
						return
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_BLACKFLAGLAST) {
						if (ToolkitOption.autoSetupToolkit == 2) {
							GameShell.softwareMode = true
							return
						}
						if (ToolkitOption.autoSetupToolkit == 1) {
							GameShell.openglMode = true
							return
						}
						if (ToolkitOption.autoSetupToolkit == 3) {
							GameShell.directxMode = true
						}
						return
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_GETLEVEL) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.graphics!!.getValue()
						return
					}
				} else if (opcode < 7200) {
					if (opcode == ClientScriptOpCode.VIDEO_ADVERT_PLAY) {
						CS2State.intStackPtr -= 2
						var fileId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var volume: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						if (fileId != -1) {
							if (volume > 255) {
								volume = 255
							} else if (volume < 0) {
								volume = 0
							}
							VorbisStreamPreload.preloadVorbisSound(volume, false, fileId)
						}
						return
					}
					if (opcode == ClientScriptOpCode.VIDEO_ADVERT_FORCE_REMOVE) {
						var streamId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (streamId != -1) {
							VorbisStreamStop.stopVorbisStream(streamId)
						}
						return
					}
					if (opcode == ClientScriptOpCode.VIDEO_ADVERT_ALLOW_SKIP) {
						var streamId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (streamId != -1) {
							VorbisNode.toggleVorbisPause(streamId)
						}
						return
					}
					if (opcode == 7103) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (NativeLibraryOwner.isLibraryLoaded("jagtheora")) 1 else 0
						return
					}
				} else if (opcode < 7300) {
					if (opcode == ClientScriptOpCode.DETAILCANMOD_GROUNDDECOR) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.groundDecor!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_SPOTSHADOWS) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.characterShadows!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_HARDSHADOWS) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.hardShadows!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_WATERDETAIL) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.water!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_ANTIALIASING) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.antiAliasing!!.canMod() && ClientState.activeToolkit!!.supportsAntiAliasing()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_PARTICLES) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.particles!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_BUILDAREA) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.buildArea!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_BLOOM) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.bloom!!.canMod() && ClientState.activeToolkit!!.supportsBloom()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_GROUNDBLENDING) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.groundBlending!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_TEXTURING) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.textures!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_MAXSCREENSIZE) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.maxScreenSize!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_FOG) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.fog!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_ORTHOGRAPHIC) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.orthographic!!.canMod()) 1 else 0
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_TOOLKIT_DEFAULT) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = if (GamePreferences.uid!!.toolkitDefault!!.canMod()) 1 else 0
						return
					}
				} else if (opcode < 7400) {
					if (opcode == ClientScriptOpCode.DETAILCANSET_GROUNDDECOR) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.groundDecor!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_SPOTSHADOWS) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.characterShadows!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_HARDSHADOWS) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.hardShadows!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_WATERDETAIL) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.water!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_ANTIALIASING) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (!ClientState.activeToolkit!!.supportsAntiAliasing()) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 3
							return
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.antiAliasing!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_PARTICLES) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.particles!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_BUILDAREA) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.buildArea!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_BLOOM) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (!ClientState.activeToolkit!!.supportsBloom()) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 3
							return
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.bloom!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_GROUNDBLENDING) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.groundBlending!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_TEXTURING) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.textures!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_MAXSCREENSIZE) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.maxScreenSize!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_FOG) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.fog!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_ORTHOGRAPHIC) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.orthographic!!.canSet(preferenceValue)
						return
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_TOOLKIT_DEFAULT) {
						var preferenceValue: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid!!.toolkitDefault!!.canSet(preferenceValue)
						return
					}
				}
			}
		}
		throw IllegalStateException(opcode.toString())
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Lclient!rk;)V")
	@JvmStatic fun executeHookInner(@OriginalArg(0) hookRequest: HookRequest?) {
		executeHookInternal(hookRequest, 200000)
	}

	@OriginalMember(owner = "client!kca", name = "b", descriptor = "(IZ)V")
	@JvmStatic fun executeOpBelow5000(@OriginalArg(0) opcode: Int, @OriginalArg(1) hasIntOperand: Boolean) {
		@Suppress("NAME_SHADOWING") var opcode: Int = opcode
		@Pc(137) var component: Component?
		@Pc(25) var local25: Int
		@Pc(56) var slotChildIndex: Int
		@Pc(19) var keyTriggerIndex: Int
		@Pc(35) var component2: Component?
		@Pc(13) var local13: Int
		@Pc(210) var component3: Component?
		@Pc(158) var component4: Component?
		if (opcode < 300) {
			if (opcode == 100) {
				CS2State.intStackPtr -= 3
				var componentHash: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var componentTypeId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				var childSlot: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
				if (componentTypeId == 0) {
					throw RuntimeException()
				}
				component2 = Interface.getComponent(componentHash)
				if (component2!!.slotChildren == null) {
					component2!!.slotChildren = arrayOfNulls<Component>(childSlot + 1)
				}
				if (component2!!.slotChildren!!.size <= childSlot) {
					@Pc(54) var components: Array<Component?>? = arrayOfNulls<Component>(childSlot + 1)
					slotChildIndex = 0
					while (slotChildIndex < component2!!.slotChildren!!.size) {
						components!![slotChildIndex] = component2!!.slotChildren!![slotChildIndex]
						slotChildIndex++
					}
					component2!!.slotChildren = components
				}
				if (childSlot > 0 && component2!!.slotChildren!![childSlot - 1] == null) {
					throw RuntimeException("Gap at:" + (childSlot - 1))
				}
				@Pc(99) var component5: Component? = Component()
				component5!!.type = ComponentType.fromId(componentTypeId)
				component5!!.idHash = component2!!.idHash
				component5!!.parentId = component5!!.idHash
				component5!!.slotId = childSlot
				component2!!.slotChildren!![childSlot] = component5
				if (hasIntOperand) {
					CS2State.activeComponent2 = component5
				} else {
					CS2State.activeComponent = component5
				}
				Component.redrawComponent(component2)
				return
			}
			if (opcode == 101) {
				component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
				if (component!!.slotId == -1) {
					if (hasIntOperand) {
						throw RuntimeException("Tried to .cc_delete static .active-component!")
					}
					throw RuntimeException("Tried to cc_delete static active-component!")
				}
				component4 = Interface.getComponent(component!!.idHash)
				component4!!.slotChildren!![component!!.slotId] = null
				Component.redrawComponent(component4)
				return
			}
			if (opcode == 102) {
				component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
				component!!.slotChildren = null
				Component.redrawComponent(component)
				return
			}
			if (opcode == ClientScriptOpCode.CC_FIND) {
				CS2State.intStackPtr -= 2
				var componentHash: Int = ScriptRunner.intStack[CS2State.intStackPtr]
				var childSlot: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
				component3 = Component.getComponentWithSlot(childSlot, componentHash)
				if (component3 != null && childSlot != -1) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1
					if (hasIntOperand) {
						CS2State.activeComponent2 = component3
						return
					}
					CS2State.activeComponent = component3
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0
				return
			}
			if (opcode == ClientScriptOpCode.IF_FIND) {
				var componentHash: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				component4 = Interface.getComponent(componentHash)
				if (component4 != null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1
					if (hasIntOperand) {
						CS2State.activeComponent2 = component4
						return
					}
					CS2State.activeComponent = component4
					return
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0
				return
			}
			if (opcode == ClientScriptOpCode.IF_SENDTOFRONT) {
				var componentHash: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				CS2State.sendToFront(componentHash)
				return
			}
			if (opcode == ClientScriptOpCode.IF_SENDTOBACK) {
				var componentHash: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
				CS2State.sendToBack(componentHash)
				return
			}
		} else {
			@Pc(357) var local357: Int
			@Pc(420) var local420: Boolean
			if (opcode < 500) {
				if (opcode == ClientScriptOpCode.BASEIDKIT) {
					CS2State.intStackPtr -= 2
					var idkitSlot: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var idkId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					if (PlayerUpdate.localPlayer!!.playerModel == null) {
						return
					}
					var maleSlotIndex: Int = 0
					while (maleSlotIndex < CS2Interpreter.MALE_IDKIT_SLOTS.size) {
						if (CS2Interpreter.MALE_IDKIT_SLOTS[maleSlotIndex] == idkitSlot) {
							PlayerUpdate.localPlayer!!.playerModel!!.addAppearanceIdentity(TypeLists.IDK, idkId, maleSlotIndex)
							return
						}
						maleSlotIndex++
					}
					var femaleSlotIndex: Int = 0
					while (femaleSlotIndex < CS2Interpreter.FEMALE_IDKIT_SLOTS.size) {
						if (CS2Interpreter.FEMALE_IDKIT_SLOTS[femaleSlotIndex] == idkitSlot) {
							PlayerUpdate.localPlayer!!.playerModel!!.addAppearanceIdentity(TypeLists.IDK, idkId, femaleSlotIndex)
							return
						}
						femaleSlotIndex++
					}
					return
				}
				if (opcode == ClientScriptOpCode.BASECOLOUR) {
					CS2State.intStackPtr -= 2
					var recolorSlot: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var colorIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					if (PlayerUpdate.localPlayer!!.playerModel == null) {
						return
					}
					PlayerUpdate.localPlayer!!.playerModel!!.recolor(recolorSlot, colorIndex)
					return
				}
				if (opcode == ClientScriptOpCode.SETGENDER) {
					var isFemale: Boolean = ScriptRunner.intStack[--CS2State.intStackPtr] != 0
					if (PlayerUpdate.localPlayer!!.playerModel == null) {
						return
					}
					PlayerUpdate.localPlayer!!.playerModel!!.changeGender(isFemale)
					return
				}
				if (opcode == ClientScriptOpCode.SETOBJ) {
					CS2State.intStackPtr -= 2
					var wornSlot: Int = ScriptRunner.intStack[CS2State.intStackPtr]
					var objId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					if (PlayerUpdate.localPlayer!!.playerModel == null) {
						return
					}
					PlayerUpdate.localPlayer!!.playerModel!!.addAppearanceItem(wornSlot, objId, TypeLists.OBJ)
					return
				}
			} else if ((opcode < 1000 || opcode >= 1100) && (opcode < 2000 || opcode >= 2100)) {
				@Pc(1523) var string: String?
				@Pc(1161) var string2: String?
				if (opcode >= 1100 && opcode < 1200 || opcode >= 2100 && opcode < 2200) {
					if (opcode >= 2000) {
						opcode -= 1000
						component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
					} else {
						component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETSCROLLPOS) {
						CS2State.intStackPtr -= 2
						component!!.scrollX = ScriptRunner.intStack[CS2State.intStackPtr]
						if (component!!.scrollX > component!!.scrollWidth - component!!.width) {
							component!!.scrollX = component!!.scrollWidth - component!!.width
						}
						if (component!!.scrollX < 0) {
							component!!.scrollX = 0
						}
						component!!.scrollY = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						if (component!!.scrollY > component!!.scrollHeight - component!!.height) {
							component!!.scrollY = component!!.scrollHeight - component!!.height
						}
						if (component!!.scrollY < 0) {
							component!!.scrollY = 0
						}
						Component.redrawComponent(component)
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfScrollPosUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETCOLOUR) {
						component!!.color = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfColourUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETFILL) {
						component!!.filled = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTTRANS) {
						component!!.transparency = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETLINEWID) {
						component!!.lineWidth = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETGRAPHIC) {
						var spriteId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (component!!.spriteId != spriteId) {
							component!!.spriteId = spriteId
							Component.redrawComponent(component)
						}
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfGraphicUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SET2DANGLE) {
						component!!.angle2d = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTILING) {
						component!!.tiling = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODEL) {
						component!!.mediaType = MediaType.RAW_MODEL
						component!!.primaryMediaId = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELANGLE) {
						CS2State.intStackPtr -= 6
						component!!.offsetX = ScriptRunner.intStack[CS2State.intStackPtr]
						component!!.offsetY = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						component!!.spritePitch = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						component!!.spriteRoll = ScriptRunner.intStack[CS2State.intStackPtr + 3]
						component!!.spriteYaw = ScriptRunner.intStack[CS2State.intStackPtr + 4]
						component!!.spriteScale = ScriptRunner.intStack[CS2State.intStackPtr + 5]
						Component.redrawComponent(component)
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfAngleUpdate(component!!.idHash)
							PulseEvent.delayedIfOffsetUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELANIM) {
						var animationId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (component!!.animation != animationId) {
							component!!.animation = animationId
							component!!.currentFrameId = 0
							component!!.nextFrameId = 1
							component!!.currentFrameDuration = 0
							@Pc(1110) var seqType: SeqType? = if (component!!.animation == -1) null else TypeLists.SEQ!!.list(component!!.animation)
							if (seqType != null) {
								SoundManager.playInterfaceSequenceSound(component!!.currentFrameId, seqType)
							}
							Component.redrawComponent(component)
						}
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfAnimUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELORTHOG) {
						component!!.usesOrthogonal = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTEXT) {
						string2 = CS2State.stringStack[--CS2State.stringStackPtr]
						if (!string2!!.equals(component!!.text)) {
							component!!.text = string2
							Component.redrawComponent(component)
						}
						if (component!!.slotId == -1) {
							DelayedIfTextUpdate.delayedIfTextUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTEXTFONT) {
						component!!.fontId = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfFontUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTEXTALIGN) {
						CS2State.intStackPtr -= 3
						component!!.textHorizontalAli = ScriptRunner.intStack[CS2State.intStackPtr]
						component!!.textVerticalAli = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						component!!.lineSpacing = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTEXTSHADOW) {
						component!!.shadow = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETOUTLINE) {
						component!!.borderThickness = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETGRAPHICSHADOW) {
						component!!.spriteShadow = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETHFLIP) {
						component!!.flipVertical = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETVFLIP) {
						component!!.flipHorizontal = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETSCROLLSIZE) {
						CS2State.intStackPtr -= 2
						component!!.scrollWidth = ScriptRunner.intStack[CS2State.intStackPtr]
						component!!.scrollHeight = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						Component.redrawComponent(component)
						if (component!!.type === ComponentType.CONTAINER) {
							Component.layoutChildren(false, component)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETALPHA) {
						component!!.alpha = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELZOOM) {
						component!!.spriteScale = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfAngleUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETLINEDIRECTION) {
						var lineDirection: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						component!!.lineDirection = lineDirection == 1
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELORIGIN) {
						CS2State.intStackPtr -= 2
						component!!.originX = ScriptRunner.intStack[CS2State.intStackPtr]
						component!!.originY = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						Component.redrawComponent(component)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMAXLINES) {
						component!!.maxTextLines = ScriptRunner.intStack[--CS2State.intStackPtr]
						Component.redrawComponent(component)
						return
					}
					@Pc(1489) var paramType: ParamType?
					if (opcode == ClientScriptOpCode.CC_IF_SETPARAM_INT) {
						CS2State.intStackPtr -= 2
						var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var paramValue: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						paramType = TypeLists.PARAMS!!.list(paramId)
						if (paramValue != paramType!!.defaultInt) {
							component!!.setIntParam(paramId, paramValue)
							return
						}
						component!!.removeParam(paramId)
						return
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETPARAM_STRING) {
						var paramId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						string = CS2State.stringStack[--CS2State.stringStackPtr]
						paramType = TypeLists.PARAMS!!.list(paramId)
						if (!paramType!!.typeName!!.equals(string)) {
							component!!.setStringParam(paramId, string)
							return
						}
						component!!.removeParam(paramId)
						return
					}
					if (opcode == 1129 || opcode == 1130) {
						var videoId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (component!!.type !== ComponentType.SPRITE && opcode == 1129 || component!!.type !== ComponentType.TEXT && opcode == 1130) {
							return
						}
						if (component!!.videoFileId != videoId) {
							component!!.videoFileId = videoId
							Component.redrawComponent(component)
						}
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfVideoUpdate(component!!.idHash)
						}
						return
					}
				} else if ((opcode < 1200 || opcode >= 1300) && (opcode < 2200 || opcode >= 2300)) {
					@Pc(2373) var local2373: Int
					if (opcode >= 1300 && opcode < 1400 || opcode >= 2300 && opcode < 2400) {
						if (opcode >= 2000) {
							opcode -= 1000
							component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
						} else {
							component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETOP) {
							var opIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr] - 1
							if (opIndex >= 0 && opIndex <= 9) {
								component!!.addOptionName(opIndex, CS2State.stringStack[--CS2State.stringStackPtr])
								return
							}
							CS2State.stringStackPtr--
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETDRAGGABLE) {
							CS2State.intStackPtr -= 2
							var dragTargetHash: Int = ScriptRunner.intStack[CS2State.intStackPtr]
							var dragTargetSlot: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
							if (dragTargetHash == -1 && dragTargetSlot == -1) {
								component!!.depthChildOverride = null
								return
							}
							component!!.depthChildOverride = Component.getComponentWithSlot(dragTargetSlot, dragTargetHash)
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETDRAGRENDERBEHAVIOUR) {
							var dragRenderType: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
							if (dragRenderType != Component.DRAG_RENDER_TRANSLUCENT && dragRenderType != InterfaceDrag.DRAG_RENDER_IN_LAYER && dragRenderType != InterfaceDrag.DRAG_RENDER_OPAQUE) {
								return
							}
							component!!.dragType = dragRenderType
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETDRAGDEADZONE) {
							component!!.dragDeadzone = ScriptRunner.intStack[--CS2State.intStackPtr]
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETDRAGDEADTIME) {
							component!!.dragDeadTime = ScriptRunner.intStack[--CS2State.intStackPtr]
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETOPBASE) {
							component!!.opBase = CS2State.stringStack[--CS2State.stringStackPtr]
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETTARGETVERB) {
							component!!.targetVerb = CS2State.stringStack[--CS2State.stringStackPtr]
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_CLEAROPS) {
							component!!.options = null
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETTARGETCURSORS) {
							component!!.targetLeaveCursor = ScriptRunner.intStack[--CS2State.intStackPtr]
							component!!.targetOverCursor = ScriptRunner.intStack[--CS2State.intStackPtr]
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETOPCURSOR) {
							var cursorId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
							var opNumber: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
							if (opNumber >= 1 && opNumber <= 10) {
								component!!.addCursor(opNumber - 1, cursorId)
							}
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETPAUSETEXT) {
							component!!.resumeText = CS2State.stringStack[--CS2State.stringStackPtr]
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETTARGETOPCURSOR) {
							component!!.targetOpCursor = ScriptRunner.intStack[--CS2State.intStackPtr]
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETOPCHAR || opcode == ClientScriptOpCode.CC_IF_SETOPKEY) {
							var keyTriggerAction: Int
							var keyTriggerParam: Int
							if (opcode == ClientScriptOpCode.CC_IF_SETOPCHAR) {
								CS2State.intStackPtr -= 3
								keyTriggerIndex = ScriptRunner.intStack[CS2State.intStackPtr] - 1
								keyTriggerAction = ScriptRunner.intStack[CS2State.intStackPtr + 1]
								keyTriggerParam = ScriptRunner.intStack[CS2State.intStackPtr + 2]
								if (keyTriggerIndex < 0 || keyTriggerIndex > 9) {
									throw RuntimeException("IOR13121313")
								}
							} else {
								CS2State.intStackPtr -= 2
								keyTriggerIndex = 10
								keyTriggerAction = ScriptRunner.intStack[CS2State.intStackPtr]
								keyTriggerParam = ScriptRunner.intStack[CS2State.intStackPtr + 1]
							}
							if (component!!.keyTriggeringAction == null) {
								if (keyTriggerAction == 0) {
									return
								}
								component!!.keyTriggeringAction = ByteArray(11)
								component!!.keyTriggerParams = ByteArray(11)
								component!!.cycleSteps = IntArray(11)
							}
							component!!.keyTriggeringAction!![keyTriggerIndex] = keyTriggerAction.toByte()
							if (keyTriggerAction == 0) {
								component!!.hasInteraction = false
								var keyIndex: Int = 0
								while (keyIndex < component!!.keyTriggeringAction!!.size) {
									if (component!!.keyTriggeringAction!![keyIndex].toInt() != 0) {
										component!!.hasInteraction = true
										break
									}
									keyIndex++
								}
							} else {
								component!!.hasInteraction = true
							}
							component!!.keyTriggerParams!![keyTriggerIndex] = keyTriggerParam.toByte()
							return
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETMOUSEOVERCURSOR) {
							component!!.moveOverCursor = ScriptRunner.intStack[--CS2State.intStackPtr]
							return
						}
					} else if (opcode >= 1400 && opcode < 1500 || opcode >= 2400 && opcode < 2500) {
						if (opcode >= 2000) {
							opcode -= 1000
							component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
						} else {
							component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
						}
						if (opcode == ClientScriptOpCode.CC_IF_CLEARSCRIPTHOOKS) {
							component!!.clearScriptHooks()
							return
						}
						string2 = CS2State.stringStack[--CS2State.stringStackPtr]
						@Pc(2459) var triggers: IntArray? = null
						if (string2!!.length > 0 && string2!![string2!!.length - 1] == 'Y') {
							var triggerCount: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
							if (triggerCount > 0) {
								triggers = IntArray(triggerCount)
								while (triggerCount-- > 0) {
									triggers[triggerCount] = ScriptRunner.intStack[--CS2State.intStackPtr]
								}
							}
							string2 = string2!!.substring(0, string2!!.length - 1)
						}
						@Pc(2511) var objects: Array<Any?>? = arrayOfNulls<Any>(string2!!.length + 1)
						var argIndex: Int = objects!!.size - 1
						while (argIndex >= 1) {
							if (string2!![argIndex - 1] == 's') {
								objects!![argIndex] = CS2State.stringStack[--CS2State.stringStackPtr]
							} else {
								objects!![argIndex] = (ScriptRunner.intStack[--CS2State.intStackPtr])
							}
							argIndex--
						}
						var scriptId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (scriptId == -1) {
							objects = null
						} else {
							objects!![0] = (scriptId)
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETONCLICK) {
							component!!.onClick = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONHOLD) {
							component!!.onHold = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONRELEASE) {
							component!!.onRelease = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONMOUSEOVER) {
							component!!.onMouseOver = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONMOUSELEAVE) {
							component!!.onMouseLeaveScript = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONDRAG) {
							component!!.onDrag = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONTARGETLEAVE) {
							component!!.hookParams = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONVARTRANSMIT) {
							component!!.onVarpTransmit = objects
							component!!.varps = triggers
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONTIMER) {
							component!!.onTimer = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONOP) {
							component!!.params = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONDRAGCOMPLETE) {
							component!!.onDragComplete = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONCLICKREPEAT) {
							component!!.onClickRepeat = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONMOUSEREPEAT) {
							component!!.popupScript = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONINVTRANSMIT) {
							component!!.mouseLeaveScript = objects
							component!!.mouseLeaveArrayParams = triggers
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONSTATTRANSMIT) {
							component!!.onStatTransmit = objects
							component!!.statTransmitFilter = triggers
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONTARGETENTER) {
							component!!.onTargetEnter = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONSCROLLWHEEL) {
							component!!.onMouseMove = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONCHATTRANSMIT) {
							component!!.onChatTransmit = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONKEY) {
							component!!.onKeyType = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONFRIENDTRANSMIT) {
							component!!.onFriendTransmit = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONCLANTRANSMIT) {
							component!!.onClanTransmit = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONMISCTRANSMIT) {
							component!!.onMiscTransmit = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONDIALOGABORT) {
							component!!.onSubClose = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONSUBCHANGE) {
							component!!.onSubChange = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONSTOCKTRANSMIT) {
							component!!.onStockTransmit = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONCAMFINISHED) {
							component!!.onCamFinished = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONRESIZE) {
							component!!.onResize = objects
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONVARCTRANSMIT) {
							component!!.onKey = objects
							component!!.keyPressArray = triggers
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONVARCSTRTRANSMIT) {
							component!!.onScrollWheel = objects
							component!!.mouseWheelArray = triggers
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONOPT) {
							component!!.onTargetLeave = objects
						}
						component!!.hasScript = true
						return
					} else if (opcode < 1600) {
						component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
						if (opcode == ClientScriptOpCode.CC_GETX) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.x
							return
						}
						if (opcode == ClientScriptOpCode.CC_GETY) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.y
							return
						}
						if (opcode == ClientScriptOpCode.CC_GETWIDTH) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.width
							return
						}
						if (opcode == ClientScriptOpCode.CC_GETHEIGHT) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.height
							return
						}
						if (opcode == ClientScriptOpCode.CC_GETHIDE) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = if (component!!.hidden) 1 else 0
							return
						}
						if (opcode == ClientScriptOpCode.CC_GETLAYER) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.parentId
							return
						}
						if (opcode == ClientScriptOpCode.CC_GETPARENTLAYER) {
							component4 = Component.getParentLayer(component)
							ScriptRunner.intStack[CS2State.intStackPtr++] = if (component4 == null) -1 else component4.idHash
							return
						}
					} else {
						@Pc(3107) var paramType2: ParamType?
						if (opcode < 1700) {
							component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
							if (opcode == ClientScriptOpCode.CC_GETSCROLLX) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.scrollX
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETSCROLLY) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.scrollY
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETTEXT) {
								CS2State.stringStack[CS2State.stringStackPtr++] = component!!.text
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETSCROLLWIDTH) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.scrollWidth
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETSCROLLHEIGHT) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.scrollHeight
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELZOOM) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spriteScale
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELANGLE_X) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spritePitch
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELANGLE_Z) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spriteYaw
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELANGLE_Y) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spriteRoll
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETTRANS) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.transparency
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELXOF) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.offsetX
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELYOF) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.offsetY
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETGRAPHIC) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spriteId
								return
							}
							if (opcode == ClientScriptOpCode.CC_PARAM) {
								var paramId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
								paramType2 = TypeLists.PARAMS!!.list(paramId)
								if (paramType2!!.isString()) {
									CS2State.stringStack[CS2State.stringStackPtr++] = component!!.getStringParam(paramId, paramType2!!.typeName)
									return
								}
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.getIntParam(paramType2!!.defaultInt, paramId)
								return
							}
							if (opcode == ClientScriptOpCode.CC_GET2DANGLE) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.angle2d
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETMODEL) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = if (component!!.mediaType === MediaType.RAW_MODEL) component!!.primaryMediaId else -1
								return
							}
						} else if (opcode < 1800) {
							component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
							if (opcode == ClientScriptOpCode.CC_GETINVOBJECT) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.containerItemId
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETINVCOUNT) {
								if (component!!.containerItemId != -1) {
									ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.itemStackSize
									return
								}
								ScriptRunner.intStack[CS2State.intStackPtr++] = 0
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETID) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.slotId
								return
							}
						} else if (opcode < 1900) {
							component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
							if (opcode == ClientScriptOpCode.CC_GETTARGETMASK) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = InterfaceManager.getComponentSettings(component)!!.getUseOptionFlags()
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETOP) {
								var opIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
								opIndex--
								if (component!!.options != null && opIndex < component!!.options!!.size && component!!.options!![opIndex] != null) {
									CS2State.stringStack[CS2State.stringStackPtr++] = component!!.options!![opIndex]
									return
								}
								CS2State.stringStack[CS2State.stringStackPtr++] = ""
								return
							}
							if (opcode == ClientScriptOpCode.CC_GETOPBASE) {
								if (component!!.opBase == null) {
									CS2State.stringStack[CS2State.stringStackPtr++] = ""
									return
								}
								CS2State.stringStack[CS2State.stringStackPtr++] = component!!.opBase
								return
							}
						} else if (opcode < 2000 || opcode >= 2900 && opcode < 3000) {
							if (opcode >= 2000) {
								component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
								opcode -= 1000
							} else {
								component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
							}
							if (CS2State.hookRequestCount >= 10) {
								throw RuntimeException("C29xx-1")
							}
							if (opcode == ClientScriptOpCode.CC_IF_CALLONRESIZE) {
								if (component!!.onResize == null) {
									return
								}
								@Pc(3376) var hookRequest: HookRequest? = HookRequest()
								hookRequest!!.source = component
								hookRequest!!.params = component!!.onResize
								hookRequest!!.id = CS2State.hookRequestCount + 1
								InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest)
								return
							}
						} else if (opcode < 2600) {
							component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
							if (opcode == ClientScriptOpCode.IF_GETX) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.x
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETY) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.y
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETWIDTH) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.width
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETHEIGHT) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.height
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETHIDE) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = if (component!!.hidden) 1 else 0
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETLAYER) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.parentId
								return
							}
							if (opcode == 1506) {
								component4 = Component.getParentLayer(component)
								ScriptRunner.intStack[CS2State.intStackPtr++] = if (component4 == null) -1 else component4.idHash
								return
							}
						} else if (opcode < 2700) {
							component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
							if (opcode == ClientScriptOpCode.IF_GETSCROLLX) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.scrollX
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETSCROLLY) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.scrollY
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETTEXT) {
								CS2State.stringStack[CS2State.stringStackPtr++] = component!!.text
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETSCROLLWIDTH) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.scrollWidth
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETSCROLLHEIGHT) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.scrollHeight
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELZOOM) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spriteScale
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELANGLE_X) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spritePitch
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELANGLE_Z) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spriteYaw
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELANGLE_Y) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spriteRoll
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETTRANS) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.transparency
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELXOF) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.offsetX
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELYOF) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.offsetY
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETGRAPHIC) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.spriteId
								return
							}
							if (opcode == ClientScriptOpCode.IF_GET2DANGLE) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.angle2d
								return
							}
							if (opcode == ClientScriptOpCode.IF_GETMODEL) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = if (component!!.mediaType === MediaType.RAW_MODEL) component!!.primaryMediaId else -1
								return
							}
						} else {
							@Pc(3905) var subInterface: SubInterface?
							@Pc(3803) var subInterface2: SubInterface?
							if (opcode < 2800) {
								if (opcode == ClientScriptOpCode.IF_GETINVOBJECT) {
									component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
									ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.containerItemId
									return
								}
								if (opcode == ClientScriptOpCode.IF_GETINVCOUNT) {
									component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
									if (component!!.containerItemId != -1) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = component!!.itemStackSize
										return
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = 0
									return
								}
								if (opcode == ClientScriptOpCode.IF_HASSUB) {
									var componentHash: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
									subInterface2 = (InterfaceManager.OPEN_INTERFACES.get(componentHash.toLong()) as SubInterface?)
									if (subInterface2 != null) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = 1
										return
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = 0
									return
								}
								if (opcode == ClientScriptOpCode.IF_GETNEXTSUBID) {
									component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
									if (component!!.slotChildren == null) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									var nextSubId: Int = component!!.slotChildren!!.size
									var childIndex: Int = 0
									while (childIndex < component!!.slotChildren!!.size) {
										if (component!!.slotChildren!![childIndex] == null) {
											nextSubId = childIndex
											break
										}
										childIndex++
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = nextSubId
									return
								}
								if (opcode == ClientScriptOpCode.IF_HASSUBMODAL || opcode == ClientScriptOpCode.IF_HASSUBOVERLAY) {
									CS2State.intStackPtr -= 2
									var componentHash: Int = ScriptRunner.intStack[CS2State.intStackPtr]
									var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
									subInterface = (InterfaceManager.OPEN_INTERFACES.get(componentHash.toLong()) as SubInterface?)
									if (subInterface != null && subInterface.interfaceId == interfaceId) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = 1
										return
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = 0
									return
								}
							} else if (opcode < 2900) {
								component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
								if (opcode == ClientScriptOpCode.IF_GETTARGETMASK) {
									ScriptRunner.intStack[CS2State.intStackPtr++] = InterfaceManager.getComponentSettings(component)!!.getUseOptionFlags()
									return
								}
								if (opcode == ClientScriptOpCode.IF_GETOP) {
									var opIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
									opIndex--
									if (component!!.options != null && opIndex < component!!.options!!.size && component!!.options!![opIndex] != null) {
										CS2State.stringStack[CS2State.stringStackPtr++] = component!!.options!![opIndex]
										return
									}
									CS2State.stringStack[CS2State.stringStackPtr++] = ""
									return
								}
								if (opcode == ClientScriptOpCode.IF_GETOPBASE) {
									if (component!!.opBase == null) {
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									CS2State.stringStack[CS2State.stringStackPtr++] = component!!.opBase
									return
								}
							} else {
								@Pc(4042) var string3: String?
								if (opcode < 3200) {
									if (opcode == ClientScriptOpCode.MES) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										ChatLineWriter.writeUnfilterableMessage(string3)
										return
									}
									if (opcode == 3101) {
										CS2State.intStackPtr -= 2
										PlayerEntity.animatePlayer(PlayerUpdate.localPlayer, ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1])
										return
									}
									if (opcode == ClientScriptOpCode.IF_CLOSE) {
										ModalInterfaceClose.closeInterface()
										return
									}
									if (opcode == ClientScriptOpCode.RESUME_COUNTDIALOG) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										var count: Int = 0
										if (Utils.isValidInteger(string3!!)) {
											count = Utils.parseInt(string3!!)
										}
										@Pc(4102) var countPacket: TcpPacket? = TcpPacket.createPacket(ClientProt.RESUME_COUNTDIALOG, TcpPacket.isaac)
										countPacket!!.buffer!!.writeInt(count)
										BufferedConnectionContext.queuePacket(countPacket)
										return
									}
									@Pc(4131) var packet: TcpPacket?
									if (opcode == ClientScriptOpCode.RESUME_NAMEDIALOG) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										packet = TcpPacket.createPacket(ClientProt.RESUME_P_NAMEDIALOG, TcpPacket.isaac)
										packet.buffer!!.writeByte(string3!!.length + 1)
										packet.buffer!!.writeString(string3)
										BufferedConnectionContext.queuePacket(packet)
										return
									}
									if (opcode == ClientScriptOpCode.RESUME_STRINGDIALOG) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										packet = TcpPacket.createPacket(ClientProt.RESUME_P_STRINGDIALOG, TcpPacket.isaac)
										packet.buffer!!.writeByte(string3!!.length + 1)
										packet.buffer!!.writeString(string3)
										BufferedConnectionContext.queuePacket(packet)
										return
									}
									if (opcode == ClientScriptOpCode.OPPLAYER) {
										var optionIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										string2 = CS2State.stringStack[--CS2State.stringStackPtr]
										ThreeDView.sendPlayerOperation(optionIndex, string2)
										return
									}
									if (opcode == ClientScriptOpCode.IF_DRAGPICKUP) {
										CS2State.intStackPtr -= 3
										var dragOffsetX: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var offsetY: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										var componentHash: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
										component2 = Interface.getComponent(componentHash)
										InterfaceManager.startDragging(component2, dragOffsetX, offsetY)
										return
									}
									if (opcode == ClientScriptOpCode.CC_DRAGPICKUP) {
										CS2State.intStackPtr -= 2
										var dragOffsetX: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var offsetY: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										component3 = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
										InterfaceManager.startDragging(component3, dragOffsetX, offsetY)
										return
									}
									if (opcode == ClientScriptOpCode.RESUME_OBJDIALOG) {
										var itemId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										packet = TcpPacket.createPacket(WorldMap.RESUME_P_OBJDIALOG, TcpPacket.isaac)
										packet.buffer!!.writeShort(itemId)
										BufferedConnectionContext.queuePacket(packet)
										return
									}
									if (opcode == ClientScriptOpCode.IF_OPENSUBCLIENT) {
										CS2State.intStackPtr -= 2
										var componentHash: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var interfaceId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										subInterface = (InterfaceManager.OPEN_INTERFACES.get(componentHash.toLong()) as SubInterface?)
										if (subInterface != null) {
											SubInterfaceCloser.closeSubInterface(subInterface.interfaceId != interfaceId, true, subInterface)
										}
										SubInterfaceOpener.openSubInterface(interfaceId, componentHash, SubInterfaceType.OVERLAY_CLIENT, true)
										return
									}
									if (opcode == ClientScriptOpCode.IF_CLOSESUBCLIENT) {
										CS2State.intStackPtr--
										var componentHash: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										subInterface2 = (InterfaceManager.OPEN_INTERFACES.get(componentHash.toLong()) as SubInterface?)
										if (subInterface2 != null && subInterface2.openType === SubInterfaceType.OVERLAY_CLIENT) {
											SubInterfaceCloser.closeSubInterface(true, true, subInterface2)
										}
										return
									}
									if (opcode == ClientScriptOpCode.OPPLAYERT) {
										InterfaceOnPlayerAction.sendInterfaceOnPlayer(CS2State.stringStack[--CS2State.stringStackPtr])
										return
									}
									if (opcode == ClientScriptOpCode.MES_TYPED) {
										CS2State.intStackPtr -= 2
										var messageType: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var effectFlags: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										string = CS2State.stringStack[--CS2State.stringStackPtr]
										ChatLineWriter.writeMessage("", messageType, effectFlags, string, "", "")
										return
									}
									if (opcode == ClientScriptOpCode.SETUP_MESSAGEBOX) {
										CS2State.intStackPtr -= 11
										@Pc(4432) var loadingScreenAlignmentXs: Array<LoadingScreenAlignmentX?>? = LoadingScreenAlignmentX.loadingScreenAlignmentXValues()
										@Pc(4435) var loadingScreenAlignmentYs: Array<LoadingScreenAlignmentY?>? = LoadingScreenAlignmentY.loadingScreenAlignmentYValues()
										MessageBox.setupMessageBox(ScriptRunner.intStack[CS2State.intStackPtr + 9], ScriptRunner.intStack[CS2State.intStackPtr + 5], loadingScreenAlignmentXs!![ScriptRunner.intStack[CS2State.intStackPtr]], loadingScreenAlignmentYs!![ScriptRunner.intStack[CS2State.intStackPtr + 1]], ScriptRunner.intStack[CS2State.intStackPtr + 7], ScriptRunner.intStack[CS2State.intStackPtr + 6], ScriptRunner.intStack[CS2State.intStackPtr + 4], ScriptRunner.intStack[CS2State.intStackPtr + 10], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr + 8])
										return
									}
								} else if (opcode < 3300) {
									if (opcode == ClientScriptOpCode.SOUND_SYNTH) {
										CS2State.intStackPtr -= 3
										AreaSoundPlayer.playSoundSynth(255, ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr + 2], 256)
										return
									}
									if (opcode == ClientScriptOpCode.SOUND_SONG) {
										SongPlayer.playSong(255, ScriptRunner.intStack[--CS2State.intStackPtr], 50)
										return
									}
									if (opcode == ClientScriptOpCode.SOUND_JINGLE) {
										CS2State.intStackPtr -= 2
										PlayMusic.playJingle(ScriptRunner.intStack[CS2State.intStackPtr], 255, ScriptRunner.intStack[CS2State.intStackPtr + 1])
										return
									}
									if (opcode == ClientScriptOpCode.SOUND_SYTHN_VOLUME) {
										CS2State.intStackPtr -= 4
										AreaSoundPlayer.playSoundSynth(ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr + 2], 256)
										return
									}
									if (opcode == ClientScriptOpCode.SOUND_SONG_VOLUME) {
										CS2State.intStackPtr -= 3
										SongPlayer.playSong(ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 2])
										return
									}
									if (opcode == ClientScriptOpCode.SOUND_JINGLE_VOLUME) {
										CS2State.intStackPtr -= 3
										PlayMusic.playJingle(ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr + 1])
										return
									}
									if (opcode == ClientScriptOpCode.SOUND_VORBIS_VOLUME) {
										CS2State.intStackPtr -= 4
										SoundManager.playSoundVorbis(ScriptRunner.intStack[CS2State.intStackPtr + 1], 256, false, ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr])
										return
									}
									if (opcode == ClientScriptOpCode.SOUND_SPEECH_VOLUME) {
										CS2State.intStackPtr -= 4
										SoundManager.playSoundVorbis(ScriptRunner.intStack[CS2State.intStackPtr + 1], 256, true, ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr])
										return
									}
									if (opcode == ClientScriptOpCode.SOUND_SYNTH_RATE) {
										CS2State.intStackPtr -= 5
										AreaSoundPlayer.playSoundSynth(ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr + 4])
										return
									}
									if (opcode == ClientScriptOpCode.SOUND_VORBIS_RATE) {
										CS2State.intStackPtr -= 5
										SoundManager.playSoundVorbis(ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr + 4], false, ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr])
										return
									}
								} else if (opcode < 3400) {
									if (opcode == ClientScriptOpCode.CLIENTCLOCK) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = ClientState.clientCycle
										return
									}
									if (opcode == ClientScriptOpCode.INV_GETOBJ) {
										CS2State.intStackPtr -= 2
										var inventoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var slot: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getItemIdAtSlot(false, inventoryId, slot)
										return
									}
									if (opcode == ClientScriptOpCode.INV_GETNUM) {
										CS2State.intStackPtr -= 2
										var inventoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var slot: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getItemAmountAtSlot(inventoryId, false, slot)
										return
									}
									if (opcode == ClientScriptOpCode.INV_TOTAL) {
										CS2State.intStackPtr -= 2
										var inventoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var itemId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryTotal(itemId, inventoryId, false)
										return
									}
									if (opcode == ClientScriptOpCode.INV_SIZE) {
										var inventoryId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OP_MAPELEMENT2!!.list(inventoryId)!!.size
										return
									}
									if (opcode == ClientScriptOpCode.STAT) {
										var skillId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = client.statLevels[skillId]
										return
									}
									if (opcode == ClientScriptOpCode.STAT_BASE) {
										var skillId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = StatBaseLevels.statBaseLevels[skillId]
										return
									}
									if (opcode == ClientScriptOpCode.STAT_VISIBLE_XP) {
										var skillId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = client.statXp[skillId]
										return
									}
									if (opcode == ClientScriptOpCode.COORD) {
										@Pc(4960) var level: Byte = PlayerUpdate.localPlayer!!.level
										var tileX: Int = (PlayerUpdate.localPlayer!!.x shr 9) + MapRegion.sceneBaseX
										var tileZ: Int = (PlayerUpdate.localPlayer!!.z shr 9) + MapRegion.sceneBaseY
										ScriptRunner.intStack[CS2State.intStackPtr++] = (level.toInt() shl 28) + (tileX shl 14) + tileZ
										return
									}
									if (opcode == ClientScriptOpCode.COORDX) {
										var coord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = (coord shr 14) and 0x3FFF
										return
									}
									if (opcode == ClientScriptOpCode.COORDY) {
										var coord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = coord shr 28
										return
									}
									if (opcode == ClientScriptOpCode.COORDZ) {
										var coord: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = coord and 0x3FFF
										return
									}
									if (opcode == ClientScriptOpCode.MAP_MEMBERS) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (LoginManager.memberWorld) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.INVOTHER_GETOBJ) {
										CS2State.intStackPtr -= 2
										var inventoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var slot: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getItemIdAtSlot(true, inventoryId, slot)
										return
									}
									if (opcode == ClientScriptOpCode.INVOTHER_GETNUM) {
										CS2State.intStackPtr -= 2
										var inventoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var slot: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getItemAmountAtSlot(inventoryId, true, slot)
										return
									}
									if (opcode == ClientScriptOpCode.INVOTHER_TOTAL) {
										CS2State.intStackPtr -= 2
										var inventoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var itemId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryTotal(itemId, inventoryId, true)
										return
									}
									if (opcode == ClientScriptOpCode.STAFFMODLEVEL) {
										if (LoginManager.staffModLevel >= 2) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.staffModLevel
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.REBOOTTIMER) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = client.rebootTimer
										return
									}
									if (opcode == ClientScriptOpCode.MAP_WORLD) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = TcpPacket.CURRENT_REQUEST!!.worldId
										return
									}
									if (opcode == ClientScriptOpCode.RUNENERGY_VISIBLE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = ClientState.runEnergy
										return
									}
									if (opcode == ClientScriptOpCode.RUNWEIGHT_VISIBLE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = client.runWeight
										return
									}
									if (opcode == ClientScriptOpCode.PLAYERMOD) {
										if (LoginManager.playerModLevel >= 5 && LoginManager.playerModLevel <= 9) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 1
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.PLAYERMODLEVEL) {
										if (LoginManager.playerModLevel >= 5 && LoginManager.playerModLevel <= 9) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.playerModLevel
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.PLAYERMEMBER) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (LoginManager.isMember) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.COMLEVEL_ACTIVE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = PlayerUpdate.localPlayer!!.combatLevel
										return
									}
									if (opcode == ClientScriptOpCode.GENDER) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (PlayerUpdate.localPlayer!!.playerModel != null && PlayerUpdate.localPlayer!!.playerModel!!.female) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.MAP_QUICKCHAT) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (LoginManager.quickchatOnly) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.INV_FREESPACE) {
										var inventoryId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryFreeSpace(inventoryId)
										return
									}
									if (opcode == ClientScriptOpCode.INV_TOTALPARAM) {
										CS2State.intStackPtr -= 2
										var inventoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryParamTotal(false, inventoryId, paramId)
										return
									}
									if (opcode == ClientScriptOpCode.INV_TOTALPARAM_STACK) {
										CS2State.intStackPtr -= 2
										var inventoryId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryParamTotal(true, inventoryId, paramId)
										return
									}
									if (opcode == 3333) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.anInt6172
										return
									}
									if (opcode == ClientScriptOpCode.MAP_LANG) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = ClientState.language
										return
									}
									if (opcode == ClientScriptOpCode.MOVE_COORD) {
										CS2State.intStackPtr -= 4
										var coord: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var deltaX: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										var deltaLevel: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
										var deltaZ: Int = ScriptRunner.intStack[CS2State.intStackPtr + 3]
										coord += deltaX shl 14
										coord += deltaLevel shl 28
										coord += deltaZ
										ScriptRunner.intStack[CS2State.intStackPtr++] = coord
										return
									}
									if (opcode == ClientScriptOpCode.AFFILIATE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.AFFILIATE
										return
									}
									if (opcode == ClientScriptOpCode.PROFILE_CPU) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = SoftwareRendererBenchmark.benchmarkSoftwareRenderer()
										return
									}
									if (opcode == ClientScriptOpCode.PLAYERDEMO) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.APPLET_HASFOCUS) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (GameShell.appletHasFocus) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.FROMBILLING) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (GameShell.fromBilling) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.GET_MOUSEX) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = MouseRecord.mouseRecorder!!.getMouseX()
										return
									}
									if (opcode == ClientScriptOpCode.GET_MOUSEY) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = MouseRecord.mouseRecorder!!.getMouseY()
										return
									}
									if (opcode == ClientScriptOpCode.GET_ACTIVE_MINIMENU_ENTRY) {
										CS2State.stringStack[CS2State.stringStackPtr++] = MiniMenu.getColoredNameText()
										return
									}
									if (opcode == ClientScriptOpCode.GET_SECOND_MINIMENU_ENTRY) {
										CS2State.stringStack[CS2State.stringStackPtr++] = MiniMenu.getActionText()
										return
									}
									if (opcode == ClientScriptOpCode.GET_MINIMENU_LENGTH) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = MiniMenu.getClickType()
										return
									}
									if (opcode == ClientScriptOpCode.GET_CURRENTCURSOR) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = CursorManager.currentCursor
										return
									}
									if (opcode == ClientScriptOpCode.GET_SELFYANGLE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = PlayerUpdate.localPlayer!!.yaw.getRotation() shr 3
										return
									}
									if (opcode == ClientScriptOpCode.GET_MOUSEBUTTONS && ClientFeatures.VOID) {
										// Not part of the original client. Void's scripts read the three mouse buttons through this instruction.
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (MouseRecord.mouseRecorder!!.isLeftButtonDown()) 1 else 0
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (MouseRecord.mouseRecorder!!.isMiddleButtonDown()) 1 else 0
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (MouseRecord.mouseRecorder!!.isRightButtonDown()) 1 else 0
										return
									}
								} else if (opcode < 3500) {
									@Pc(5711) var enumType: EnumType?
									if (opcode == ClientScriptOpCode.ENUM_STRING) {
										CS2State.intStackPtr -= 2
										var enumId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var key: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										enumType = TypeLists.ENUM!!.list(enumId)
										CS2State.stringStack[CS2State.stringStackPtr++] = enumType!!.getString(key)
										return
									}
									if (opcode == ClientScriptOpCode.ENUM) {
										CS2State.intStackPtr -= 4
										var inputType: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var valueType: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										var enumId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
										var enumKey: Int = ScriptRunner.intStack[CS2State.intStackPtr + 3]
										@Pc(5757) var enumType2: EnumType? = TypeLists.ENUM!!.list(enumId)
										if (enumType2!!.keyType.code == inputType && enumType2!!.valueType.code == valueType) {
											if (valueType == 115) {
												CS2State.stringStack[CS2State.stringStackPtr++] = enumType2!!.getString(enumKey)
												return
											}
											ScriptRunner.intStack[CS2State.intStackPtr++] = enumType2!!.getInt(enumKey)
											return
										}
										throw RuntimeException("C3408-1 " + enumId + "-" + enumKey)
									}
									if (opcode == ClientScriptOpCode.ENUM_HASOUTPUT) {
										CS2State.intStackPtr -= 3
										var outputType: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var enumId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										var outputValue: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
										if (enumId == -1) {
											throw RuntimeException("C3409-2")
										}
										@Pc(5843) var enumType3: EnumType? = TypeLists.ENUM!!.list(enumId)
										if (enumType3!!.valueType.code != outputType) {
											throw RuntimeException("C3409-1")
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (enumType3!!.hasOutput(outputValue)) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.ENUM_HASOUTPUT_STRING) {
										var enumId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										string2 = CS2State.stringStack[--CS2State.stringStackPtr]
										if (enumId == -1) {
											throw RuntimeException("C3410-2")
										}
										enumType = TypeLists.ENUM!!.list(enumId)
										if (enumType!!.valueType != 's') {
											throw RuntimeException("C3410-1")
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (enumType!!.hasOutputString(string2)) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.ENUM_GETOUTPUT_COUNT) {
										var enumId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										@Pc(5941) var enumType4: EnumType? = TypeLists.ENUM!!.list(enumId)
										ScriptRunner.intStack[CS2State.intStackPtr++] = enumType4!!.parameters!!.size()
										return
									}
								} else if (opcode < 3700) {
									if (opcode == ClientScriptOpCode.FRIEND_COUNT) {
										if (FriendList.friendListStatus == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = -2
											return
										}
										if (FriendList.friendListStatus == 1) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = -1
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = FriendList.FRIEND_COUNT
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETNAME) {
										var friendIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendList.friendListStatus == 2 && friendIndex < FriendList.FRIEND_COUNT) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendList.friendNames[friendIndex]
											if (FriendList.friendPreviousNames[friendIndex] != null) {
												CS2State.stringStack[CS2State.stringStackPtr++] = FriendList.friendPreviousNames[friendIndex]
												return
											}
											CS2State.stringStack[CS2State.stringStackPtr++] = ""
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETWORLD) {
										var friendIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendList.friendListStatus == 2 && friendIndex < FriendList.FRIEND_COUNT) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendList.friendWorldIds[friendIndex]
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETRANK) {
										var friendIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendList.friendListStatus == 2 && friendIndex < FriendList.FRIEND_COUNT) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendList.friendRanks[friendIndex]
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_SETRANK) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										var rank: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										FriendList.setFriendRank(rank, string3)
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_ADD) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										FriendList.addFriend(string3)
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_DEL) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										FriendList.removeFriend(string3)
										return
									}
									if (opcode == ClientScriptOpCode.IGNORE_ADD) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										IgnoreList.add(string3, false)
										return
									}
									if (opcode == ClientScriptOpCode.IGNORE_DEL) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										IgnoreList.removeIgnore(string3)
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_TEST) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										if (string3!!.startsWith("<img=0>") || string3!!.startsWith("<img=1>")) {
											string3 = string3!!.substring(7)
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (FriendOrSelfCheck.isFriendOrSelf(string3)) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETWORLDNAME) {
										var friendIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendList.friendListStatus == 2 && friendIndex < FriendList.FRIEND_COUNT) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendList.friendWorldNames[friendIndex]
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATDISPLAYNAME) {
										if (FriendsChat.friendsChatName != null) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.formatDisplayName(FriendsChat.friendsChatName)
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATCOUNT) {
										if (FriendsChat.friendsChatName != null) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.clanChatUserCount
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERNAME) {
										var clanMemberIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.clanChatUsers!![clanMemberIndex]!!.displayName
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERWORLD) {
										var clanMemberIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.clanChatUsers!![clanMemberIndex]!!.worldId
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERRANK) {
										var clanMemberIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.clanChatUsers!![clanMemberIndex]!!.rank.toInt()
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATMINKICK) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.friendChatMinKickRank.toInt()
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_KICKUSER) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										FriendsChat.kickFriendsChatUser(string3)
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATRANK) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.myFriendsChatRank.toInt()
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_JOINCHAT) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										FriendsChat.sendJoinFriendChat(string3)
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_LEAVECHAT) {
										FriendsChat.sendLeaveFriendChat()
										return
									}
									if (opcode == ClientScriptOpCode.IGNORE_COUNT) {
										if (FriendList.friendListStatus == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = -1
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = IgnoreList.ignoreCount
										return
									}
									if (opcode == ClientScriptOpCode.IGNORE_GETNAME) {
										var ignoreIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendList.friendListStatus != 0 && ignoreIndex < IgnoreList.ignoreCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = IgnoreList.ignoreNames[ignoreIndex]
											if (IgnoreList.ignorePreviousNames[ignoreIndex] != null) {
												CS2State.stringStack[CS2State.stringStackPtr++] = IgnoreList.ignorePreviousNames[ignoreIndex]
												return
											}
											CS2State.stringStack[CS2State.stringStackPtr++] = ""
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.IGNORE_TEXT) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										if (string3!!.startsWith("<img=0>") || string3!!.startsWith("<img=1>")) {
											string3 = string3!!.substring(7)
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (IgnoreList.isIgnored(string3)) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_ISSELF) {
										var clanMemberIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendsChat.clanChatUsers != null && clanMemberIndex < FriendsChat.clanChatUserCount && FriendsChat.clanChatUsers!![clanMemberIndex]!!.username!!.equals(PlayerUpdate.localPlayer!!.displayName, ignoreCase = true)) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 1
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATOWNERNAME) {
										if (FriendsChat.clanChatOwnerName != null) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.clanChatOwnerName
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERWORLDNAME) {
										var clanMemberIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.clanChatUsers!![clanMemberIndex]!!.worldName
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_SAME_GAME) {
										var friendIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendList.friendListStatus == 2 && friendIndex >= 0 && friendIndex < FriendList.FRIEND_COUNT) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = if (FriendList.friendSameGame[friendIndex]) 1 else 0
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETSLOTFROMNAME) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										if (string3!!.startsWith("<img=0>") || string3!!.startsWith("<img=1>")) {
											string3 = string3!!.substring(7)
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = FriendList.getFriendIndex(string3)
										return
									}
									if (opcode == ClientScriptOpCode.PLAYERCOUNTRY) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.country
										return
									}
									if (opcode == ClientScriptOpCode.IGNORE_ADD_TEMP) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										IgnoreList.add(string3, true)
										return
									}
									if (opcode == ClientScriptOpCode.IGNORE_IS_TEMP) {
										var ignoreIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (IgnoreList.ignoreIsTemporary[ignoreIndex]) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERNAME_UNFILTERED) {
										var clanMemberIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.clanChatUsers!![clanMemberIndex]!!.username
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.IGNORE_GETNAME_UNFILTERED) {
										var ignoreIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (FriendList.friendListStatus != 0 && ignoreIndex < IgnoreList.ignoreCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = IgnoreList.ignoreNamesUnfiltered[ignoreIndex]
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
								} else if (opcode < 4000) {
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERTYPE) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.isBuyOffer()
										return
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERITEM) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.itemId
										return
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERPRICE) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.price
										return
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERCOUNT) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.amount
										return
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERCOMPLETEDCOUNT) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.currentAmount
										return
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERCOMPLETEDGOLD) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.totalPrice
										return
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_ISOFFEREMPTY) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										var slotState: Int = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.getSlotState()
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (slotState == 0) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_ISOFFERSTABLE) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										var slotState: Int = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.getSlotState()
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (slotState == 2) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_ISOFFERFINISHED) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										var slotState: Int = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.getSlotState()
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (slotState == 5) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_ISOFFERADDING) {
										var offerSlot: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										var slotState: Int = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot]!!.getSlotState()
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (slotState == 1) 1 else 0
										return
									}
								} else if (opcode < 4100) {
									if (opcode == ClientScriptOpCode.ADD) {
										CS2State.intStackPtr -= 2
										var lhs: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var addend: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs + addend
										return
									}
									if (opcode == ClientScriptOpCode.SUB) {
										CS2State.intStackPtr -= 2
										var lhs: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var subtrahend: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs - subtrahend
										return
									}
									if (opcode == ClientScriptOpCode.MULTIPLY) {
										CS2State.intStackPtr -= 2
										var lhs: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var multiplier: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs * multiplier
										return
									}
									if (opcode == ClientScriptOpCode.DIVIDE) {
										CS2State.intStackPtr -= 2
										var dividend: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var divisor: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = dividend / divisor
										return
									}
									if (opcode == ClientScriptOpCode.RANDOM) {
										var randomBound: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = (util.random() * randomBound.toDouble()).toInt()
										return
									}
									if (opcode == ClientScriptOpCode.RANDOMINC) {
										var maxInclusive: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = (util.random() * (maxInclusive + 1).toDouble()).toInt()
										return
									}
									if (opcode == ClientScriptOpCode.INTERPOLATE) {
										CS2State.intStackPtr -= 5
										var startValue: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var endValue: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										var rangeStart: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
										var rangeEnd: Int = ScriptRunner.intStack[CS2State.intStackPtr + 3]
										var rangeValue: Int = ScriptRunner.intStack[CS2State.intStackPtr + 4]
										ScriptRunner.intStack[CS2State.intStackPtr++] = startValue + (endValue - startValue) * (rangeValue - rangeStart) / (rangeEnd - rangeStart)
										return
									}
									@Pc(7451) var local7451: Long
									@Pc(7444) var local7444: Long
									if (opcode == ClientScriptOpCode.ADDPERCENT) {
										CS2State.intStackPtr -= 2
										var baseAmount: Long = ScriptRunner.intStack[CS2State.intStackPtr].toLong()
										var percent: Long = ScriptRunner.intStack[CS2State.intStackPtr + 1].toLong()
										ScriptRunner.intStack[CS2State.intStackPtr++] = (baseAmount + baseAmount * percent / 100L).toInt()
										return
									}
									if (opcode == ClientScriptOpCode.SETBIT) {
										CS2State.intStackPtr -= 2
										var bitfield: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var bit: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = bitfield or (0x1 shl bit)
										return
									}
									if (opcode == ClientScriptOpCode.CLEARBIT) {
										CS2State.intStackPtr -= 2
										var bitfield: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var bit: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = bitfield and -(0x1 shl bit) - 1
										return
									}
									if (opcode == ClientScriptOpCode.TESTBIT) {
										CS2State.intStackPtr -= 2
										var bitfield: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var bit: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = if ((bitfield and (0x1 shl bit)) == 0) 0 else 1
										return
									}
									if (opcode == ClientScriptOpCode.MODULO) {
										CS2State.intStackPtr -= 2
										var dividend: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var divisor: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = dividend % divisor
										return
									}
									if (opcode == ClientScriptOpCode.POW) {
										CS2State.intStackPtr -= 2
										var base: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var power: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										if (base == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 0
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = (base.toDouble()).pow(power.toDouble()).toInt()
										return
									}
									if (opcode == ClientScriptOpCode.INVPOW) {
										CS2State.intStackPtr -= 2
										var base: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var root: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										if (base == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 0
											return
										}
										if (root == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = Int.MAX_VALUE
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = (base.toDouble()).pow(1.0 / root.toDouble()).toInt()
										return
									}
									if (opcode == ClientScriptOpCode.AND) {
										CS2State.intStackPtr -= 2
										var lhs: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var mask: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs and mask
										return
									}
									if (opcode == ClientScriptOpCode.OR) {
										CS2State.intStackPtr -= 2
										var lhs: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var mask: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs or mask
										return
									}
									if (opcode == ClientScriptOpCode.MIN) {
										CS2State.intStackPtr -= 2
										var lhs: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var other: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (lhs < other) lhs else other
										return
									}
									if (opcode == ClientScriptOpCode.MAX) {
										CS2State.intStackPtr -= 2
										var lhs: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var other: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (lhs > other) lhs else other
										return
									}
									if (opcode == ClientScriptOpCode.SCALE) {
										CS2State.intStackPtr -= 3
										var numerator: Long = ScriptRunner.intStack[CS2State.intStackPtr].toLong()
										var denominator: Long = ScriptRunner.intStack[CS2State.intStackPtr + 1].toLong()
										@Pc(7832) var value: Long = ScriptRunner.intStack[CS2State.intStackPtr + 2].toLong()
										ScriptRunner.intStack[CS2State.intStackPtr++] = (numerator * value / denominator).toInt()
										return
									}
									if (opcode == ClientScriptOpCode.RANDOM_SOUND_PITCH) {
										CS2State.intStackPtr -= 2
										var lowerRange: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var upperRange: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										if (lowerRange > 700 || upperRange > 700) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 256
										}
										@Pc(7891) var exponent: Double = (util.random() * (upperRange + lowerRange).toDouble() + 800.0 - lowerRange.toDouble()) / 100.0
										ScriptRunner.intStack[CS2State.intStackPtr++] = (2.0.pow(exponent) + 0.5).toInt()
										return
									}
								} else if (opcode < 4200) {
									if (opcode == ClientScriptOpCode.APPEND_NUM) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										var number: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										CS2State.stringStack[CS2State.stringStackPtr++] = string3 + number
										return
									}
									if (opcode == ClientScriptOpCode.APPEND) {
										CS2State.stringStackPtr -= 2
										string3 = CS2State.stringStack[CS2State.stringStackPtr]
										string2 = CS2State.stringStack[CS2State.stringStackPtr + 1]
										CS2State.stringStack[CS2State.stringStackPtr++] = string3 + string2
										return
									}
									if (opcode == ClientScriptOpCode.APPEND_SIGNNUM) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										var number: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										CS2State.stringStack[CS2State.stringStackPtr++] = string3 + Utils.toSignedString(number, true)
										return
									}
									if (opcode == ClientScriptOpCode.LOWERCASE) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										CS2State.stringStack[CS2State.stringStackPtr++] = string3!!.lowercase(util.defaultLocale())
										return
									}
									if (opcode == ClientScriptOpCode.FROMDATE) {
										CS2State.stringStack[CS2State.stringStackPtr++] = CS2State.runedayToDateString(ScriptRunner.intStack[--CS2State.intStackPtr])
										return
									}
									if (opcode == ClientScriptOpCode.TEXT_GENDER) {
										CS2State.stringStackPtr -= 2
										string3 = CS2State.stringStack[CS2State.stringStackPtr]
										string2 = CS2State.stringStack[CS2State.stringStackPtr + 1]
										if (PlayerUpdate.localPlayer!!.playerModel != null && PlayerUpdate.localPlayer!!.playerModel!!.female) {
											CS2State.stringStack[CS2State.stringStackPtr++] = string2
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = string3
										return
									}
									if (opcode == ClientScriptOpCode.TOSTRING) {
										var number: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										CS2State.stringStack[CS2State.stringStackPtr++] = number.toString()
										return
									}
									if (opcode == ClientScriptOpCode.COMPARE) {
										CS2State.stringStackPtr -= 2
										ScriptRunner.intStack[CS2State.intStackPtr++] = Utils.compareLocaleAware(CS2State.stringStack[CS2State.stringStackPtr + 1]!!, ClientState.language, CS2State.stringStack[CS2State.stringStackPtr]!!)
										return
									}
									@Pc(8174) var fontMetrics: FontMetrics?
									if (opcode == ClientScriptOpCode.PARAHEIGHT) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										CS2State.intStackPtr -= 2
										var maxWidth: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var fontId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										fontMetrics = FontMetricsLoader.getFontMetrics(fontId, Resource.FONT_METRICS)
										ScriptRunner.intStack[CS2State.intStackPtr++] = fontMetrics!!.getLineCount(StaticMedia.nameIconSprites, string3, maxWidth)
										return
									}
									if (opcode == 4109) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										CS2State.intStackPtr -= 2
										var maxWidth: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var fontId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										fontMetrics = FontMetricsLoader.getFontMetrics(fontId, Resource.FONT_METRICS)
										ScriptRunner.intStack[CS2State.intStackPtr++] = fontMetrics!!.getTextWidthMultiline(string3, maxWidth, StaticMedia.nameIconSprites)
										return
									}
									if (opcode == 4110) {
										CS2State.stringStackPtr -= 2
										string3 = CS2State.stringStack[CS2State.stringStackPtr]
										string2 = CS2State.stringStack[CS2State.stringStackPtr + 1]
										if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) {
											CS2State.stringStack[CS2State.stringStackPtr++] = string3
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = string2
										return
									}
									if (opcode == ClientScriptOpCode.ESCAPE) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										CS2State.stringStack[CS2State.stringStackPtr++] = Utils.escapeTags(string3!!)
										return
									}
									if (opcode == ClientScriptOpCode.APPEND_CHAR) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										var charCode: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										if (charCode == -1) {
											throw RuntimeException("null char")
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = string3 + charCode.toChar()
										return
									}
									if (opcode == ClientScriptOpCode.CHAR_ISPRINTABLE) {
										var charCode: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.isPrintableChar(charCode.toChar())
										return
									}
									if (opcode == ClientScriptOpCode.CHAR_ISALPHANUMERIC) {
										var charCode: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (Utils.isAlphanumeric(charCode.toChar())) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.CHAR_ISALPHA) {
										var charCode: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (Utils.isAlphabetic(charCode.toChar())) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.CHAR_ISNUMERIC) {
										var charCode: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (Utils.isDigit(charCode.toChar())) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.STRING_LENGTH) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										if (string3 != null) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = string3.length
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0
										return
									}
									if (opcode == ClientScriptOpCode.SUBSTRING) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										CS2State.intStackPtr -= 2
										var startIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var endIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										CS2State.stringStack[CS2State.stringStackPtr++] = string3!!.substring(startIndex, endIndex)
										return
									}
									if (opcode == ClientScriptOpCode.REMOVETAGS) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										@Pc(8532) var stringBuffer: StringBuffer? = StringBuffer(string3!!.length)
										@Pc(8534) var inTag: Boolean = false
										var charIndex: Int = 0
										while (charIndex < string3!!.length) {
											@Pc(8541) var ch: Char = string3!![charIndex]
											if (ch == '<') {
												inTag = true
											} else if (ch == '>') {
												inTag = false
											} else if (!inTag) {
												stringBuffer!!.append(ch)
											}
											charIndex++
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = stringBuffer.toString()
										return
									}
									if (opcode == ClientScriptOpCode.STRING_INDEXOF_CHAR) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										CS2State.intStackPtr -= 2
										var searchChar: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var fromIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										ScriptRunner.intStack[CS2State.intStackPtr++] = string3!!.javaIndexOf(searchChar, fromIndex)
										return
									}
									if (opcode == ClientScriptOpCode.STRING_INDEXOF_STRING) {
										CS2State.stringStackPtr -= 2
										string3 = CS2State.stringStack[CS2State.stringStackPtr]
										string2 = CS2State.stringStack[CS2State.stringStackPtr + 1]
										var fromIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = string3!!.indexOf(string2!!, fromIndex)
										return
									}
									if (opcode == ClientScriptOpCode.CHAR_TOLOWERCASE) {
										var charCode: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = charCode.toChar().lowercaseChar().code
										return
									}
									if (opcode == ClientScriptOpCode.CHAR_TOUPPERCASE) {
										var charCode: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = charCode.toChar().uppercaseChar().code
										return
									}
									if (opcode == ClientScriptOpCode.TOSTRING_LOCALISED) {
										var useThousandsSeparator: Boolean = ScriptRunner.intStack[--CS2State.intStackPtr] != 0
										var number: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										CS2State.stringStack[CS2State.stringStackPtr++] = Utils.formatLocalisedNumber(0, useThousandsSeparator, ClientState.language, number.toLong())
										return
									}
									if (opcode == ClientScriptOpCode.STRINGWIDTH) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										var fontId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										@Pc(8755) var fontMetrics2: FontMetrics? = FontMetricsLoader.getFontMetrics(fontId, Resource.FONT_METRICS)
										ScriptRunner.intStack[CS2State.intStackPtr++] = fontMetrics2!!.getTextWidth(string3, StaticMedia.nameIconSprites)
										return
									}
								} else if (opcode < 4300) {
									if (opcode == ClientScriptOpCode.OC_NAME) {
										var itemId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.OBJ!!.list(itemId).name
										return
									}
									@Pc(8817) var objType: ItemType?
									if (opcode == ClientScriptOpCode.OC_OP) {
										CS2State.intStackPtr -= 2
										var itemId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var opIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										objType = TypeLists.OBJ!!.list(itemId)
										if (opIndex >= 1 && opIndex <= 5 && objType.groundActions!![opIndex - 1] != null) {
											CS2State.stringStack[CS2State.stringStackPtr++] = objType.groundActions!![opIndex - 1]
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.OC_IOP) {
										CS2State.intStackPtr -= 2
										var itemId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var opIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										objType = TypeLists.OBJ!!.list(itemId)
										if (opIndex >= 1 && opIndex <= 5 && objType.inventoryActions!![opIndex - 1] != null) {
											CS2State.stringStack[CS2State.stringStackPtr++] = objType.inventoryActions!![opIndex - 1]
											return
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = ""
										return
									}
									if (opcode == ClientScriptOpCode.OC_COST) {
										var itemId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OBJ!!.list(itemId).value
										return
									}
									if (opcode == ClientScriptOpCode.OC_STACKABLE) {
										var itemId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (TypeLists.OBJ!!.list(itemId).stackable == 1) 1 else 0
										return
									}
									@Pc(8980) var objType2: ItemType?
									if (opcode == ClientScriptOpCode.OC_CERT) {
										var itemId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										objType2 = TypeLists.OBJ!!.list(itemId)
										if (objType2.certTemplateId == -1 && objType2.certId >= 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = objType2.certId
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = itemId
										return
									}
									if (opcode == ClientScriptOpCode.OC_UNCERT) {
										var itemId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										objType2 = TypeLists.OBJ!!.list(itemId)
										if (objType2.certTemplateId >= 0 && objType2.certId >= 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = objType2.certId
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = itemId
										return
									}
									if (opcode == ClientScriptOpCode.OC_MEMBERS) {
										var itemId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = if (TypeLists.OBJ!!.list(itemId).membersOnly) 1 else 0
										return
									}
									if (opcode == ClientScriptOpCode.OC_PARAM) {
										CS2State.intStackPtr -= 2
										var itemId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										paramType2 = TypeLists.PARAMS!!.list(paramId)
										if (paramType2!!.isString()) {
											CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.OBJ!!.list(itemId).getStringParam(paramType2!!.typeName, paramId)
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OBJ!!.list(itemId).getIntParam(paramType2!!.defaultInt, paramId)
										return
									}
									if (opcode == ClientScriptOpCode.OC_ICURSOR) {
										CS2State.intStackPtr -= 2
										var itemId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var opIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1] - 1
										objType = TypeLists.OBJ!!.list(itemId)
										if (objType.customCursorOp1 == opIndex) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = objType.customCursorId1
											return
										}
										if (objType.customCursorOp2 == opIndex) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = objType.customCursorId2
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = -1
										return
									}
									if (opcode == ClientScriptOpCode.OC_FIND) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										var tradeableOnly: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ItemFindQuery.queryItemNoParams(tradeableOnly == 1, string3)
										ScriptRunner.intStack[CS2State.intStackPtr++] = Find.findResultCount
										return
									}
									if (opcode == ClientScriptOpCode.OC_FINDNEXT) {
										if (Find.queryResults != null && MessageBox.queryResultsCursor < Find.findResultCount) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = Find.queryResults!![MessageBox.queryResultsCursor++].toInt() and 0xFFFF
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = -1
										return
									}
									if (opcode == ClientScriptOpCode.OC_FINDRESTART) {
										MessageBox.queryResultsCursor = 0
										return
									}
									if (opcode == ClientScriptOpCode.OC_MULTISTACKSIZE) {
										var itemId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OBJ!!.list(itemId).multiStackSize
										return
									}
									if (opcode == 4214) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr]
										CS2State.intStackPtr -= 3
										var tradeableOnly: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										var paramValue: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
										ItemFindQuery.queryItemParamInt(paramValue, tradeableOnly == 1, string3, paramId)
										ScriptRunner.intStack[CS2State.intStackPtr++] = Find.findResultCount
										return
									}
									if (opcode == 4215) {
										CS2State.stringStackPtr -= 2
										CS2State.intStackPtr -= 2
										string3 = CS2State.stringStack[CS2State.stringStackPtr]
										var tradeableOnly: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										@Pc(9379) var string4: String? = CS2State.stringStack[CS2State.stringStackPtr + 1]
										CS2Executor.queryItemParamString(tradeableOnly == 1, string3, string4, paramId)
										ScriptRunner.intStack[CS2State.intStackPtr++] = Find.findResultCount
										return
									}
								} else if (opcode < 4400) {
									if (opcode == ClientScriptOpCode.NC_PARAM) {
										CS2State.intStackPtr -= 2
										var npcId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										paramType2 = TypeLists.PARAMS!!.list(paramId)
										if (paramType2!!.isString()) {
											CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.NPC!!.list(npcId).getStringParam(paramId, paramType2!!.typeName)
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.NPC!!.list(npcId).getParam(paramType2!!.defaultInt, paramId)
										return
									}
								} else if (opcode < 4500) {
									if (opcode == 4400) {
										CS2State.intStackPtr -= 2
										var locId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										paramType2 = TypeLists.PARAMS!!.list(paramId)
										if (paramType2!!.isString()) {
											CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.LOC!!.list(locId).getStringParam(paramType2!!.typeName, paramId)
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.LOC!!.list(locId).getIntParam(paramId, paramType2!!.defaultInt)
										return
									}
								} else if (opcode < 4600) {
									if (opcode == 4500) {
										CS2State.intStackPtr -= 2
										var structId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
										var paramId: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
										paramType2 = TypeLists.PARAMS!!.list(paramId)
										if (paramType2!!.isString()) {
											CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.STRUCT!!.list(structId)!!.getStringParam(paramType2!!.typeName, paramId)
											return
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.STRUCT!!.list(structId)!!.getParam(paramId, paramType2!!.defaultInt)
										return
									}
								} else if (opcode < 4700 && opcode == ClientScriptOpCode.BAS_GETANIM_READY) {
									var basId: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
									@Pc(9607) var basType: BasType? = TypeLists.BAS!!.list(basId)
									if (basType!!.randomStandSequences != null && basType!!.randomStandSequences!!.size > 0) {
										var bestIndex: Int = 0
										var bestChance: Int = basType!!.randomStandSequenceChances!![0]
										var standIndex: Int = 1
										while (standIndex < basType!!.randomStandSequences!!.size) {
											if (basType!!.randomStandSequenceChances!![standIndex] > bestChance) {
												bestIndex = standIndex
												bestChance = basType!!.randomStandSequenceChances!![standIndex]
											}
											standIndex++
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = basType!!.randomStandSequences!![bestIndex]
										return
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = basType!!.standSequenceId
									return
								}
							}
						}
					}
				} else {
					if (opcode >= 2000) {
						opcode -= 1000
						component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
					} else {
						component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
					}
					Component.redrawComponent(component)
					if (opcode == 1200 || opcode == 1205 || opcode == 1208 || opcode == 1209 || opcode == 1212 || opcode == 1213) {
						CS2State.intStackPtr -= 2
						var itemId: Int = ScriptRunner.intStack[CS2State.intStackPtr]
						var stackSize: Int = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfItemUpdate(component!!.idHash)
							PulseEvent.delayedIfAngleUpdate(component!!.idHash)
							PulseEvent.delayedIfOffsetUpdate(component!!.idHash)
						}
						if (itemId == -1) {
							component!!.mediaType = MediaType.RAW_MODEL
							component!!.primaryMediaId = -1
							component!!.containerItemId = -1
							return
						}
						component!!.containerItemId = itemId
						component!!.itemStackSize = stackSize
						if (opcode == 1208 || opcode == 1209) {
							component!!.wearCol = true
						} else {
							component!!.wearCol = false
						}
						@Pc(1713) var objType3: ItemType? = TypeLists.OBJ!!.list(itemId)
						component!!.spritePitch = objType3!!.modelRotationX
						component!!.spriteRoll = objType3!!.modelRotationY
						component!!.spriteYaw = objType3!!.modelRotationZ
						component!!.offsetX = objType3!!.modelOffsetX
						component!!.offsetY = objType3!!.modelOffsetY
						component!!.spriteScale = objType3!!.modelZoom
						if (opcode == 1205 || opcode == 1209) {
							component!!.renderStack = 0
						} else if (opcode == 1212 || opcode == 1213) {
							component!!.renderStack = 1
						} else {
							component!!.renderStack = 2
						}
						if (component!!.aspectWidth > 0) {
							component!!.spriteScale = component!!.spriteScale * 32 / component!!.aspectWidth
							return
						}
						if (component!!.baseWidth > 0) {
							component!!.spriteScale = component!!.spriteScale * 32 / component!!.baseWidth
						}
						return
					}
					if (opcode == 1201) {
						component!!.mediaType = MediaType.NPC_HEAD
						component!!.primaryMediaId = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == 1202) {
						component!!.mediaType = MediaType.PLAYER_HEAD
						component!!.primaryMediaId = -1
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == 1203) {
						component!!.mediaType = MediaType.NPC_MODEL
						component!!.primaryMediaId = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == 1204) {
						component!!.mediaType = MediaType.PLAYER_MODEL
						component!!.primaryMediaId = ScriptRunner.intStack[--CS2State.intStackPtr]
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == 1206) {
						CS2State.intStackPtr -= 4
						component!!.skyboxId = ScriptRunner.intStack[CS2State.intStackPtr]
						component!!.lightPositionX = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						component!!.lightPositionY = ScriptRunner.intStack[CS2State.intStackPtr + 2]
						component!!.lightPositionZ = ScriptRunner.intStack[CS2State.intStackPtr + 3]
						Component.redrawComponent(component)
						return
					}
					if (opcode == 1207) {
						CS2State.intStackPtr -= 2
						component!!.sunRotationX = ScriptRunner.intStack[CS2State.intStackPtr]
						component!!.sunRotationY = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						Component.redrawComponent(component)
						return
					}
					if (opcode == 1210) {
						CS2State.intStackPtr -= 4
						component!!.primaryMediaId = ScriptRunner.intStack[CS2State.intStackPtr]
						component!!.secondaryMediaId = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						if (ScriptRunner.intStack[CS2State.intStackPtr + 2] == 1) {
							component!!.mediaType = MediaType.ITEM_CONTAINER_FEMALE
						} else {
							component!!.mediaType = MediaType.ITEM_CONTAINER_MALE
						}
						if (ScriptRunner.intStack[CS2State.intStackPtr + 3] == 1) {
							component!!.wearCol = true
						} else {
							component!!.wearCol = false
						}
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component!!.idHash)
						}
						return
					}
					if (opcode == 1211) {
						component!!.mediaType = MediaType.PLAYER_MODEL
						component!!.primaryMediaId = PlayerUpdate.myPlayerIndex
						component!!.secondaryMediaId = 0
						if (component!!.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component!!.idHash)
						}
						return
					}
				}
			} else {
				if (opcode >= 2000) {
					opcode -= 1000
					component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr])
				} else {
					component = if (hasIntOperand) CS2State.activeComponent2 else CS2State.activeComponent
				}
				if (opcode == 1000) {
					CS2State.intStackPtr -= 4
					component!!.basePositionX = ScriptRunner.intStack[CS2State.intStackPtr]
					component!!.basePositionY = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					var aspectXType: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
					if (aspectXType < 0) {
						aspectXType = 0
					} else if (aspectXType > 5) {
						aspectXType = 5
					}
					var aspectYType: Int = ScriptRunner.intStack[CS2State.intStackPtr + 3]
					if (aspectYType < 0) {
						aspectYType = 0
					} else if (aspectYType > 5) {
						aspectYType = 5
					}
					component!!.aspectXType = aspectXType.toByte()
					component!!.aspectYType = aspectYType.toByte()
					Component.redrawComponent(component)
					InterfaceManager.relayoutAgainstParent(component)
					if (component!!.slotId == -1) {
						PulseEvent.delayedIfPositionUpdate(component!!.idHash)
					}
					return
				}
				if (opcode == 1001) {
					CS2State.intStackPtr -= 4
					component!!.baseWidth = ScriptRunner.intStack[CS2State.intStackPtr]
					component!!.baseHeight = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					component!!.aspectWidth = 0
					component!!.aspectHeight = 0
					var aspectWidthType: Int = ScriptRunner.intStack[CS2State.intStackPtr + 2]
					if (aspectWidthType < 0) {
						aspectWidthType = 0
					} else if (aspectWidthType > 4) {
						aspectWidthType = 4
					}
					var aspectHeightType: Int = ScriptRunner.intStack[CS2State.intStackPtr + 3]
					if (aspectHeightType < 0) {
						aspectHeightType = 0
					} else if (aspectHeightType > 4) {
						aspectHeightType = 4
					}
					component!!.aspectWidthType = aspectWidthType.toByte()
					component!!.aspectHeightType = aspectHeightType.toByte()
					Component.redrawComponent(component)
					InterfaceManager.relayoutAgainstParent(component)
					if (component!!.type === ComponentType.CONTAINER) {
						Component.layoutChildren(false, component)
					}
					return
				}
				if (opcode == 1003) {
					@Pc(661) var hidden: Boolean = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
					if (component!!.hidden != hidden) {
						component!!.hidden = hidden
						Component.redrawComponent(component)
					}
					if (component!!.slotId == -1) {
						PulseEvent.delayedIfHideUpdate(component!!.idHash)
					}
					return
				}
				if (opcode == 1004) {
					CS2State.intStackPtr -= 2
					component!!.aspectX = ScriptRunner.intStack[CS2State.intStackPtr]
					component!!.aspectY = ScriptRunner.intStack[CS2State.intStackPtr + 1]
					Component.redrawComponent(component)
					InterfaceManager.relayoutAgainstParent(component)
					if (component!!.type === ComponentType.CONTAINER) {
						Component.layoutChildren(false, component)
					}
					return
				}
				if (opcode == 1005) {
					component!!.preventClickThrough = ScriptRunner.intStack[--CS2State.intStackPtr] == 1
					return
				}
			}
		}
		throw IllegalStateException(opcode.toString())
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Lclient!jk;I)V")
	@JvmStatic fun executeScript(@OriginalArg(0) clientScript: ClientScript?, @OriginalArg(1) stackLimit: Int) {
		@Suppress("NAME_SHADOWING") var clientScript: ClientScript? = clientScript
		CS2State.intStackPtr = 0
		CS2State.stringStackPtr = 0
		@Pc(5) var instrPtr: Int = -1
		@Pc(8) var operations: IntArray? = clientScript!!.operations
		@Pc(11) var intOpValues: IntArray? = clientScript!!.intOpValues
		@Pc(13) var lastOpcode: Byte = -1
		CS2State.framePtr = 0
		@Pc(603) var local603: Int
		try {
			@Pc(17) var instructionCount: Int = 0
			label260@ while (true) {
				instructionCount++
				if (instructionCount > stackLimit) {
					throw RuntimeException("slow")
				}
				instrPtr++
				@Pc(31) var currentOpcode: Int = operations!![instrPtr]
				if (currentOpcode >= 100) {
					@Pc(802) var hasIntOperand: Boolean
					if (intOpValues!![instrPtr] == 1) {
						hasIntOperand = true
					} else {
						hasIntOperand = false
					}
					if (currentOpcode >= 100 && currentOpcode < 5000) {
						executeOpBelow5000(currentOpcode, hasIntOperand)
					} else if (currentOpcode >= 5000 && currentOpcode < 10000) {
						executeOpFrom5000(currentOpcode, hasIntOperand)
					} else {
						throw IllegalStateException("Command: " + currentOpcode)
					}
				} else if (currentOpcode == ClientScriptOpCode.PUSH_CONSTANT_INT) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = intOpValues!![instrPtr]
				} else {
					@Pc(54) var operand: Int
					if (currentOpcode == ClientScriptOpCode.PUSH_VAR) {
						operand = intOpValues!![instrPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = PlayerVarDomain.playerVarProvider!!.activeVars[operand]
					} else if (currentOpcode == ClientScriptOpCode.POP_VAR) {
						operand = intOpValues!![instrPtr]
						PlayerVarDomain.playerVarProvider!!.setVarValueInt(ScriptRunner.intStack[--CS2State.intStackPtr], operand)
					} else if (currentOpcode == ClientScriptOpCode.PUSH_CONSTANT_STRING) {
						CS2State.stringStack[CS2State.stringStackPtr++] = clientScript!!.stringOpValues!![instrPtr]
					} else if (currentOpcode == ClientScriptOpCode.BRANCH) {
						instrPtr += intOpValues!![instrPtr]
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_NOT) {
						CS2State.intStackPtr -= 2
						if (ScriptRunner.intStack[CS2State.intStackPtr] != ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues!![instrPtr]
						}
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_EQUALS) {
						CS2State.intStackPtr -= 2
						if (ScriptRunner.intStack[CS2State.intStackPtr] == ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues!![instrPtr]
						}
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_LESS_THAN) {
						CS2State.intStackPtr -= 2
						if (ScriptRunner.intStack[CS2State.intStackPtr] < ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues!![instrPtr]
						}
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_GREATER_THAN) {
						CS2State.intStackPtr -= 2
						if (ScriptRunner.intStack[CS2State.intStackPtr] > ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues!![instrPtr]
						}
					} else if (currentOpcode == ClientScriptOpCode.RETURN) {
						if (CS2State.framePtr == 0) {
							return
						}
						@Pc(216) var stackFrame: StackFrame? = CS2State.frames[--CS2State.framePtr]
						clientScript = stackFrame!!.script
						operations = clientScript!!.operations
						intOpValues = clientScript!!.intOpValues
						instrPtr = stackFrame!!.pc
						ScriptRunner.intLocals = stackFrame!!.intVars
						ScriptRunner.stringVars = stackFrame!!.stringVars
					} else if (currentOpcode == ClientScriptOpCode.POP_VARBIT) {
						operand = intOpValues!![instrPtr]
						ScriptRunner.intStack[CS2State.intStackPtr++] = PlayerVarDomain.playerVarProvider!!.getVarBitValue(operand)
					} else if (currentOpcode == ClientScriptOpCode.PUSH_VARBIT) {
						operand = intOpValues!![instrPtr]
						PlayerVarDomain.playerVarProvider!!.setVarBitValue(operand, ScriptRunner.intStack[--CS2State.intStackPtr])
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_LESS_THAN_OR_EQUALS) {
						CS2State.intStackPtr -= 2
						if (ScriptRunner.intStack[CS2State.intStackPtr] <= ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues!![instrPtr]
						}
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_GREATER_THAN_OR_EQUALS) {
						CS2State.intStackPtr -= 2
						if (ScriptRunner.intStack[CS2State.intStackPtr] >= ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues!![instrPtr]
						}
					} else if (currentOpcode == ClientScriptOpCode.PUSH_INT_LOCAL) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = ScriptRunner.intLocals!![intOpValues!![instrPtr]]
					} else if (currentOpcode == ClientScriptOpCode.POP_INT_LOCAL) {
						ScriptRunner.intLocals!![intOpValues!![instrPtr]] = ScriptRunner.intStack[--CS2State.intStackPtr]
					} else if (currentOpcode == ClientScriptOpCode.PUSH_STRING_LOCAL) {
						CS2State.stringStack[CS2State.stringStackPtr++] = ScriptRunner.stringVars!![intOpValues!![instrPtr]]
					} else if (currentOpcode == ClientScriptOpCode.POP_STRING_LOCAL) {
						ScriptRunner.stringVars!![intOpValues!![instrPtr]] = CS2State.stringStack[--CS2State.stringStackPtr]
					} else if (currentOpcode == ClientScriptOpCode.JOIN_STRING) {
						operand = intOpValues!![instrPtr]
						CS2State.stringStackPtr -= operand
						@Pc(400) var string: String? = Utils.joinStrings(operand, CS2State.stringStack, CS2State.stringStackPtr)
						CS2State.stringStack[CS2State.stringStackPtr++] = string
					} else if (currentOpcode == ClientScriptOpCode.POP_INT_DISCARD) {
						CS2State.intStackPtr--
					} else if (currentOpcode == ClientScriptOpCode.POP_STRING_DISCARD) {
						CS2State.stringStackPtr--
					} else {
						@Pc(452) var index: Int
						if (currentOpcode == ClientScriptOpCode.GOSUB_WITH_PARAMS) {
							operand = intOpValues!![instrPtr]
							@Pc(436) var clientScript2: ClientScript? = ClientScript.getClientScript(operand)
							if (clientScript2 == null) {
								throw RuntimeException()
							}
							@Pc(446) var intLocals: IntArray? = IntArray(clientScript2.intLocalsCount)
							@Pc(450) var strings: Array<String?>? = arrayOfNulls<String>(clientScript2.stringLocalsCount)
							index = 0
							while (index < clientScript2.intArgsCount) {
								intLocals!![index] = ScriptRunner.intStack[CS2State.intStackPtr + index - clientScript2.intArgsCount]
								index++
							}
							@Pc(471) var i: Int = 0
							while (i < clientScript2.stringArgsCount) {
								strings!![i] = CS2State.stringStack[CS2State.stringStackPtr + i - clientScript2.stringArgsCount]
								i++
							}
							CS2State.intStackPtr -= clientScript2.intArgsCount
							CS2State.stringStackPtr -= clientScript2.stringArgsCount
							@Pc(502) var stackFrame2: StackFrame? = StackFrame()
							stackFrame2!!.script = clientScript
							stackFrame2!!.pc = instrPtr
							stackFrame2!!.intVars = ScriptRunner.intLocals
							stackFrame2!!.stringVars = ScriptRunner.stringVars
							if (CS2State.framePtr >= CS2State.frames.size) {
								throw RuntimeException()
							}
							CS2State.frames[CS2State.framePtr++] = stackFrame2
							clientScript = clientScript2
							operations = clientScript2.operations
							intOpValues = clientScript2.intOpValues
							instrPtr = -1
							ScriptRunner.intLocals = intLocals
							ScriptRunner.stringVars = strings
						} else if (currentOpcode == ClientScriptOpCode.PUSH_VARC) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = VarcTypeList.VARC_INT!![intOpValues!![instrPtr]]
						} else if (currentOpcode == ClientScriptOpCode.POP_VARC) {
							operand = intOpValues!![instrPtr]
							VarcTypeList.VARC_INT!![operand] = ScriptRunner.intStack[--CS2State.intStackPtr]
							PulseEvent.delayedVarcUpdate(operand)
							VarcTypeList.clientIsActive = VarcTypeList.clientIsActive or VarcTypeList.VARC_PERSISTENT!![operand]
						} else if (currentOpcode == ClientScriptOpCode.DEFINE_ARRAY) {
							operand = intOpValues!![instrPtr] shr 16
							var arrayType: Int = intOpValues!![instrPtr] and 0xFFFF
							@Pc(611) var length: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
							if (length >= 0 && length <= 5000) {
								CS2State.globalArrayLengths[operand] = length
								@Pc(626) var defaultValue: Byte = -1
								if (arrayType == 105) {
									defaultValue = 0
								}
								index = 0
								while (true) {
									if (index >= length) {
										continue@label260
									}
									ScriptRunner.arrayVars!![operand]!![index] = defaultValue.toInt()
									index++
								}
							}
							throw RuntimeException()
						} else if (currentOpcode == ClientScriptOpCode.PUSH_ARRAY_INT) {
							operand = intOpValues!![instrPtr]
							var arrayIndex: Int = ScriptRunner.intStack[--CS2State.intStackPtr]
							if (arrayIndex < 0 || arrayIndex >= CS2State.globalArrayLengths[operand]) {
								throw RuntimeException()
							}
							ScriptRunner.intStack[CS2State.intStackPtr++] = ScriptRunner.arrayVars!![operand]!![arrayIndex]
						} else if (currentOpcode == ClientScriptOpCode.POP_ARRAY_INT) {
							operand = intOpValues!![instrPtr]
							CS2State.intStackPtr -= 2
							var arrayIndex: Int = ScriptRunner.intStack[CS2State.intStackPtr]
							if (arrayIndex < 0 || arrayIndex >= CS2State.globalArrayLengths[operand]) {
								throw RuntimeException()
							}
							ScriptRunner.arrayVars!![operand]!![arrayIndex] = ScriptRunner.intStack[CS2State.intStackPtr + 1]
						} else if (currentOpcode == ClientScriptOpCode.PUSH_VARCSTR) {
							@Pc(730) var string2: String? = VarcTypeList.VARC_STRING!![intOpValues!![instrPtr]]
							if (string2 == null) {
								string2 = "null"
							}
							CS2State.stringStack[CS2State.stringStackPtr++] = string2
						} else if (currentOpcode == ClientScriptOpCode.POP_VARCSTR) {
							operand = intOpValues!![instrPtr]
							VarcTypeList.VARC_STRING!![operand] = CS2State.stringStack[--CS2State.stringStackPtr]
							PulseEvent.delayedVarcStringUpdate(operand)
						} else if (currentOpcode == ClientScriptOpCode.SWITCH) {
							@Pc(774) var iterableHashTable: IterableHashTable? = clientScript!!.switchMaps!![intOpValues!![instrPtr]]
							@Pc(787) var intNode: IntNode? = (iterableHashTable!!.get(ScriptRunner.intStack[--CS2State.intStackPtr].toLong()) as IntNode?)
							if (intNode != null) {
								instrPtr += intNode.value
							}
						} else if (currentOpcode == ClientScriptOpCode.BRANCH_IF_TRUE && ClientFeatures.VOID) {
							// Not part of the original client. Void's scripts branch on the top of the stack being 1.
							if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) {
								instrPtr += intOpValues!![instrPtr]
							}
						}
					}
				}
			}
		} catch (@Pc(837) ex: Exception) {
			@Pc(856) var stringBuffer: StringBuffer?
			if (clientScript!!.scriptName == null) {
				stringBuffer = StringBuffer(30)
				stringBuffer.append("CS2: ").append(clientScript!!.key).append(" ")
				var frameIndex: Int = CS2State.framePtr - 1
				while (frameIndex >= 0) {
					stringBuffer.append("v: ").append(CS2State.frames[frameIndex]!!.script!!.key).append(" ")
					frameIndex--
				}
				stringBuffer.append("op: ").append(lastOpcode.toInt())
				ExplicitRuntimeException.reportError(stringBuffer.toString(), ex)
			} else {
				ChatBox.writeGameMessage("Clientscript error in: " + clientScript!!.scriptName)
				stringBuffer = StringBuffer(30)
				stringBuffer.append("Clientscript error in: ").append(clientScript!!.scriptName).append("\n")
				var frameIndex: Int = CS2State.framePtr - 1
				while (frameIndex >= 0) {
					stringBuffer.append("via: ").append(CS2State.frames[frameIndex]!!.script!!.scriptName).append("\n")
					frameIndex--
				}
				stringBuffer.append("Op: ").append(lastOpcode.toInt()).append("\n")
				@Pc(896) var string3: String? = ex.message
				if (string3 != null && string3.length > 0) {
					stringBuffer.append("Message: ").append(string3).append("\n")
				}
				ExplicitRuntimeException.reportError(stringBuffer.toString(), ex)
				Console.printToConsole(stringBuffer.toString())
			}
		}
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Lclient!rk;I)V")
	@JvmStatic fun executeHookInternal(@OriginalArg(0) hook: HookRequest?, @OriginalArg(1) stackLimit: Int) {
		@Pc(2) var objects: Array<Any?>? = hook!!.params
		@Pc(8) var scriptId: Int = (objects!![0] as Int)
		@Pc(12) var clientScript: ClientScript? = ClientScript.getClientScript(scriptId)
		if (clientScript == null) {
			return
		}
		ScriptRunner.intLocals = IntArray(clientScript.intLocalsCount)
		@Pc(21) var intLocalCount: Int = 0
		ScriptRunner.stringVars = arrayOfNulls<String>(clientScript.stringLocalsCount)
		@Pc(27) var stringLocalCount: Int = 0
		@Pc(29) var i: Int = 1
		while (i < objects!!.size) {
			if (objects!![i] is Int) {
				@Pc(41) var intLocal: Int = (objects!![i] as Int)
				if (intLocal == -2147483647) {
					intLocal = hook!!.mouseX
				}
				if (intLocal == -2147483646) {
					intLocal = hook!!.mouseY
				}
				if (intLocal == -2147483645) {
					intLocal = if (hook!!.source == null) -1 else hook!!.source!!.idHash
				}
				if (intLocal == -2147483644) {
					intLocal = hook!!.clickedOption
				}
				if (intLocal == -2147483643) {
					intLocal = if (hook!!.source == null) -1 else hook!!.source!!.slotId
				}
				if (intLocal == -2147483642) {
					intLocal = if (hook!!.targetComponent == null) -1 else hook!!.targetComponent!!.idHash
				}
				if (intLocal == -2147483641) {
					intLocal = if (hook!!.targetComponent == null) -1 else hook!!.targetComponent!!.slotId
				}
				if (intLocal == -2147483640) {
					intLocal = hook!!.typedKeyCode
				}
				if (intLocal == -2147483639) {
					intLocal = hook!!.typedKeyChar
				}
				ScriptRunner.intLocals!![intLocalCount++] = intLocal
			} else if (objects!![i] is String) {
				@Pc(135) var string: String? = (objects!![i] as String?)
				if (string.equals("event_opbase")) {
					string = hook!!.optionName
				}
				ScriptRunner.stringVars!![stringLocalCount++] = string
			}
			i++
		}
		CS2State.hookRequestCount = hook!!.id
		executeScript(clientScript, stackLimit)
	}
	@OriginalMember(owner = "client!li", name = "c", descriptor = "[I")
	@JvmField val FEMALE_IDKIT_SLOTS: IntArray = intArrayOf(7, 8, 9, 10, 11, 12, 13, 15)
	@OriginalMember(owner = "client!oda", name = "k", descriptor = "[I")
	@JvmField val MALE_IDKIT_SLOTS: IntArray = intArrayOf(0, 1, 2, 3, 4, 5, 6, 14)
}
