import com.jagex.game.runetek5.config.rsinterface.MediaType;
import com.jagex.game.runetek5.config.iftype.SubInterfaceType;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.login.LoginStep;
import com.jagex.game.runetek5.client.window.WindowModeType;
import com.jagex.game.runetek5.ChatHistory;
import com.jagex.game.runetek5.ChatLine;
import com.jagex.game.runetek5.DisplayModeDetails;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.ClientTrigger;
import com.jagex.game.runetek5.cache.node.lru.ClientScript;
import com.jagex.game.runetek5.Country;
import com.jagex.game.runetek5.Find;
import com.jagex.game.runetek5.Fullscreen;
import com.jagex.game.runetek5.interaction.IgnoreList;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.LobbyManager;
import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.QuickChatPhrase;
import com.jagex.game.runetek5.ScriptRunner;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.SoundManager;
import com.jagex.game.runetek5.StackFrame;
import com.jagex.game.runetek5.ChatLineCount;
import com.jagex.game.runetek5.PlayMusic;
import com.jagex.game.runetek5.FontMetricsLoader;
import com.jagex.game.runetek5.EmailStatus;
import com.jagex.game.runetek5.WorldListSorter;
import com.jagex.game.runetek5.MidiChannelVolumeReset;
import com.jagex.game.runetek5.ThreeDView;
import com.jagex.game.runetek5.config.rsinterface.cs2.ClientScriptOpCode;
import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.Queue;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.camera.CameraMode;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.quickchat.QuickchatMessageType;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX;
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.config.enumtype.EnumType;
import com.jagex.game.runetek5.config.rsinterface.IFEvents;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.config.invtype.LoginDisallowReason;
import com.jagex.game.runetek5.config.mec.MECType;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.config.param.ParamType;
import com.jagex.game.runetek5.config.quickchat.QuickChatCategoryType;
import com.jagex.game.runetek5.config.vartype.player.ItemFindQuery;
import com.jagex.game.runetek5.config.rsinterface.HookRequest;
import com.jagex.game.runetek5.network.world.WorldDescriptor;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapElement;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.ToolkitType;
import com.jagex.game.runetek5.math.ColourUtils;
import com.jagex.game.runetek5.statics.CS2State;
import com.jagex.game.runetek5.statics.StatBaseLevels;
import com.jagex.game.runetek5.statics.BrowserJavaScript;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Date;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.event.keyboard.SimpleKeyboardMonitorConstants;
import com.jagex.game.runetek5.client.event.keyboard.FriendOrSelfCheck;
import com.jagex.game.runetek5.client.event.mouse.DelayedIfTextUpdate;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.client.web.ClientURLTools;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.client.input.console.Console;
import com.jagex.game.runetek5.client.input.mouse.CursorManager;
import com.jagex.game.runetek5.config.DefaultsFile;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import com.jagex.game.runetek5.network.WorldConnection;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import com.jagex.game.runetek5.config.varc.VarcTypeList;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;
import com.jagex.game.runetek5.interaction.FriendList;
import com.jagex.game.runetek5.trigger.ClientTriggerType;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.cache.node.lru.PulseEvent;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.ChatFilter;
import com.jagex.game.runetek5.ChatLineWriter;
import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import com.jagex.game.runetek5.client.window.WindowMode;
import com.jagex.game.runetek5.config.flo.FloType;
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer;
import com.jagex.game.runetek5.interaction.chat.ChatBox;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.sound.SoundResourceManager;
import com.jagex.game.runetek5.sound.player.song.SongPlayer;
import com.jagex.game.runetek5.toolkit.AbstractToolkit;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.core.constants.ModeWhat;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.InterfaceDrag;
import com.jagex.game.runetek5.NativeLibraryOwner;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.client.ServerEnvironment;
import com.jagex.game.runetek5.config.midi.VorbisNode;
import com.jagex.game.runetek5.config.quickchat.QuickchatMessageTypeList;
import com.jagex.game.runetek5.encoding.HuffmanList;
import com.jagex.game.runetek5.network.Connection;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.MainLogicManager;
import com.jagex.game.runetek5.config.rsinterface.cs2.CS2Executor;
import com.jagex.game.runetek5.network.packet.StockMarketOffer;
import com.jagex.game.runetek5.network.world.WorldMeta;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.client.LoadingStage;
import com.jagex.game.runetek5.preference.impl.ToolkitOption;

public final class CS2Interpreter {

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Lclient!uw;II)V")
	public static void executeTrigger(@OriginalArg(0) ClientTrigger triggerType, @OriginalArg(1) int v1, @OriginalArg(2) int v2) {
		@Pc(5) ClientScript clientScript = ClientScript.getTriggerScript(v1, v2, triggerType);
		if (clientScript == null) {
			return;
		}
		ScriptRunner.intLocals = new int[clientScript.intLocalsCount];
		ScriptRunner.stringVars = new String[clientScript.stringLocalsCount];
		if (clientScript.triggerType == ClientTriggerType.MEC_MOUSE_ENTER || clientScript.triggerType == ClientTriggerType.MEC_MOUSE_OVER || clientScript.triggerType == ClientTriggerType.MEC_MOUSE_LEAVE) {
			@Pc(30) int worldMapOffsetX = 0;
			@Pc(32) int worldMapOffsetY = 0;
			if (WorldMap.worldmapComponent != null) {
				worldMapOffsetX = WorldMap.worldmapComponent.x;
				worldMapOffsetY = WorldMap.worldmapComponent.y;
			}
			ScriptRunner.intLocals[0] = MouseRecord.mouseRecorder.getMouseX() - worldMapOffsetX;
			ScriptRunner.intLocals[1] = MouseRecord.mouseRecorder.getMouseY() - worldMapOffsetY;
		}
		executeScript(clientScript, 200000);
	}

	@OriginalMember(owner = "client!kca", name = "b", descriptor = "(I)V")
	public static void runInterfaceOnLoadScripts(@OriginalArg(0) int interfaceId) {
		if (interfaceId == -1 || !InterfaceManager.loadInterface(interfaceId)) {
			return;
		}
		@Pc(12) Component[] components = Interface.interfaces[interfaceId];
		for (@Pc(14) int i = 0; i < components.length; i++) {
			@Pc(19) Component component = components[i];
			if (component.onLoadScript != null) {
				@Pc(26) HookRequest hookRequest = new HookRequest();
				hookRequest.source = component;
				hookRequest.params = component.onLoadScript;
				executeHookInternal(hookRequest, 2000000);
			}
		}
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(IZ)V")
	public static void executeOpFrom5000(@OriginalArg(0) int opcode, @OriginalArg(1) boolean hasIntOperand) {
		@Pc(83) String string;
		@Pc(1496) boolean local1496;
		@Pc(97) int local97;
		@Pc(175) int local175;
		@Pc(776) int local776;
		@Pc(181) String string2;
		@Pc(103) int local103;
		@Pc(52) TcpPacket packet;
		@Pc(89) String string3;
		if (opcode < 5100) {
			if (opcode == ClientScriptOpCode.CHAT_GETFILTER_PUBLIC) {
				ScriptRunner.intStack[CS2State.intStackPtr++] = ChatBox.publicFilter;
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_SETFILTER) {
				CS2State.intStackPtr -= 3;
				ChatBox.publicFilter = ScriptRunner.intStack[CS2State.intStackPtr];
				ChatBox.privateFilter = ChatFilter.chatFilterForId(ScriptRunner.intStack[CS2State.intStackPtr + 1]);
				if (ChatBox.privateFilter == null) {
					ChatBox.privateFilter = ChatFilter.FRIENDS_ONLY;
				}
				ChatBox.tradeFilter = ScriptRunner.intStack[CS2State.intStackPtr + 2];
				packet = TcpPacket.createPacket(ClientProt.CHAT_SETFILTER, TcpPacket.isaac);
				packet.buffer.writeByte(ChatBox.publicFilter);
				packet.buffer.writeByte(ChatBox.privateFilter.id);
				packet.buffer.writeByte(ChatBox.tradeFilter);
				BufferedConnectionContext.queuePacket(packet);
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_SENDABUSEREPORT) {
				CS2State.stringStackPtr -= 2;
				string = CS2State.stringStack[CS2State.stringStackPtr];
				string3 = CS2State.stringStack[CS2State.stringStackPtr + 1];
				CS2State.intStackPtr -= 2;
				int reportType = ScriptRunner.intStack[CS2State.intStackPtr];
				int mute = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				if (string3 == null) {
					string3 = "";
				}
				if (string3.length() > 80) {
					string3 = string3.substring(0, 80);
				}
				@Pc(125) TcpPacket reportPacket = TcpPacket.createPacket(ClientProt.REPORT_ABUSE, TcpPacket.isaac);
				reportPacket.buffer.writeByte(Utils.getEncodedStringLength(string) + Utils.getEncodedStringLength(string3) + 2);
				reportPacket.buffer.writeString(string);
				reportPacket.buffer.writeByte(reportType - 1);
				reportPacket.buffer.writeByte(mute);
				reportPacket.buffer.writeString(string3);
				BufferedConnectionContext.queuePacket(reportPacket);
				return;
			}
			@Pc(179) ChatLine chatLine;
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORY_BYTYPEANDLINE) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				string2 = "";
				if (chatLine != null && chatLine.message != null) {
					string2 = chatLine.message;
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2;
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORY_BYUID) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				int messageType = -1;
				if (chatLine != null) {
					messageType = chatLine.type;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = messageType;
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_GETFILTER_PRIVATE) {
				if (ChatBox.privateFilter == null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = ChatBox.privateFilter.id;
				return;
			}
			@Pc(269) TcpPacket packet2;
			if (opcode == ClientScriptOpCode.CHAT_SETMODE) {
				int chatMode = ScriptRunner.intStack[--CS2State.intStackPtr];
				packet2 = TcpPacket.createPacket(ClientProt.CHAT_TYPE, TcpPacket.isaac);
				packet2.buffer.writeByte(chatMode);
				BufferedConnectionContext.queuePacket(packet2);
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_SENDPUBLIC) {
				string = CS2State.stringStack[--CS2State.stringStackPtr];
				ScriptRunner.sendPublicChat(string, opcode);
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_SENDPRIVATE) {
				CS2State.stringStackPtr -= 2;
				string = CS2State.stringStack[CS2State.stringStackPtr];
				string3 = CS2State.stringStack[CS2State.stringStackPtr + 1];
				if (LoginManager.staffModLevel != 0 || (!LoginManager.userDetailQuickchat || LoginManager.verifiedEmailAddress) && !LoginManager.quickchatOnly) {
					@Pc(328) TcpPacket privatePacket = TcpPacket.createPacket(ClientProt.SEND_PRIVATE_MESSAGE, TcpPacket.isaac);
					privatePacket.buffer.writeByte(0);
					int startPosition = privatePacket.buffer.currentPosition;
					privatePacket.buffer.writeString(string);
					HuffmanList.writeChatMessage(privatePacket.buffer, string3);
					privatePacket.buffer.writeIndex(privatePacket.buffer.currentPosition - startPosition);
					BufferedConnectionContext.queuePacket(privatePacket);
					return;
				}
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORYNAME) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				string2 = "";
				if (chatLine != null && chatLine.crownedName != null) {
					string2 = chatLine.crownedName;
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2;
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORYCLAN) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				string2 = "";
				if (chatLine != null && chatLine.clan != null) {
					string2 = chatLine.clan;
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2;
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_GETHISTORYPHRASE) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				int quickChatId = -1;
				if (chatLine != null) {
					quickChatId = chatLine.quickchatMessageId;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatId;
				return;
			}
			if (opcode == ClientScriptOpCode.CHAT_PLAYERNAME) {
				if (PlayerUpdate.localPlayer == null || PlayerUpdate.localPlayer.username == null) {
					string = "";
				} else {
					string = PlayerUpdate.localPlayer.getUsernameWithTitle();
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string;
				return;
			}
			if (opcode == 5016) {
				ScriptRunner.intStack[CS2State.intStackPtr++] = ChatBox.tradeFilter;
				return;
			}
			if (opcode == 5017) {
				ScriptRunner.intStack[CS2State.intStackPtr++] = ChatLineCount.getChatLineCount();
				return;
			}
			if (opcode == 5018) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				int effectFlags = 0;
				if (chatLine != null) {
					effectFlags = chatLine.effectFlags;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = effectFlags;
				return;
			}
			if (opcode == 5019) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				string2 = "";
				if (chatLine != null && chatLine.name != null) {
					string2 = chatLine.name;
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2;
				return;
			}
			if (opcode == 5020) {
				if (PlayerUpdate.localPlayer == null || PlayerUpdate.localPlayer.username == null) {
					string = "";
				} else {
					string = PlayerUpdate.localPlayer.getDisplayName();
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string;
				return;
			}
			if (opcode == 5023) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				int messageIdentifier = -1;
				if (chatLine != null) {
					messageIdentifier = chatLine.identifier;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = messageIdentifier;
				return;
			}
			if (opcode == 5024) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				int messageTime = -1;
				if (chatLine != null) {
					messageTime = chatLine.time;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = messageTime;
				return;
			}
			if (opcode == 5025) {
				int lineIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
				chatLine = ChatBox.getChatLine(lineIndex);
				string2 = "";
				if (chatLine != null && chatLine.simpleName != null) {
					string2 = chatLine.simpleName;
				}
				CS2State.stringStack[CS2State.stringStackPtr++] = string2;
				return;
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETDESC) {
				int categoryId = ScriptRunner.intStack[--CS2State.intStackPtr];
				CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY.list(categoryId).name;
				return;
			}
			@Pc(736) QuickChatCategoryType quickChatCatType;
			if (opcode == ClientScriptOpCode.CHATCAT_GETSUBCATCOUNT) {
				int categoryId = ScriptRunner.intStack[--CS2State.intStackPtr];
				quickChatCatType = TypeLists.QUICK_CHAT_CATEGORY.list(categoryId);
				if (quickChatCatType.subCategories == null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatCatType.subCategories.length;
				return;
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETSUBCAT) {
				CS2State.intStackPtr -= 2;
				int categoryId = ScriptRunner.intStack[CS2State.intStackPtr];
				int subCategoryIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				@Pc(781) QuickChatCategoryType quickChatCatType2 = TypeLists.QUICK_CHAT_CATEGORY.list(categoryId);
				int subCategoryId = quickChatCatType2.subCategories[subCategoryIndex];
				ScriptRunner.intStack[CS2State.intStackPtr++] = subCategoryId;
				return;
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETPHRASECOUNT) {
				int categoryId = ScriptRunner.intStack[--CS2State.intStackPtr];
				quickChatCatType = TypeLists.QUICK_CHAT_CATEGORY.list(categoryId);
				if (quickChatCatType.messages == null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatCatType.messages.length;
				return;
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETPHRASE) {
				CS2State.intStackPtr -= 2;
				int categoryId = ScriptRunner.intStack[CS2State.intStackPtr];
				int phraseIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY.list(categoryId).messages[phraseIndex];
				return;
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETTEXT) {
				int phraseId = ScriptRunner.intStack[--CS2State.intStackPtr];
				CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId).getText();
				return;
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETAUTORESPONSECOUNT) {
				int phraseId = ScriptRunner.intStack[--CS2State.intStackPtr];
				@Pc(907) QuickchatMessageType quickChatPhraseType = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId);
				if (quickChatPhraseType.responses == null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatPhraseType.responses.length;
				return;
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETAUTORESPONSE) {
				CS2State.intStackPtr -= 2;
				int phraseId = ScriptRunner.intStack[CS2State.intStackPtr];
				int responseIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId).responses[responseIndex];
				return;
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_PREPARE) {
				CS2State.currentQuickChatPhrase = new QuickChatPhrase();
				CS2State.currentQuickChatPhrase.id = ScriptRunner.intStack[--CS2State.intStackPtr];
				CS2State.currentQuickChatPhrase.type = TypeLists.QUICK_CHAT_MESSAGE.list(CS2State.currentQuickChatPhrase.id);
				CS2State.currentQuickChatPhrase.values = new int[CS2State.currentQuickChatPhrase.type.getDynamicCommandCount()];
				return;
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SENDPUBLIC) {
				packet = TcpPacket.createPacket(ClientProt.QUICKCHAT_PUBLIC, TcpPacket.isaac);
				packet.buffer.writeByte(0);
				int startPosition = packet.buffer.currentPosition;
				packet.buffer.writeByte(0);
				packet.buffer.writeShort(CS2State.currentQuickChatPhrase.id);
				CS2State.currentQuickChatPhrase.type.encodeValues(CS2State.currentQuickChatPhrase.values, packet.buffer);
				packet.buffer.writeIndex(packet.buffer.currentPosition - startPosition);
				BufferedConnectionContext.queuePacket(packet);
				return;
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SENDPRIVATE) {
				string = CS2State.stringStack[--CS2State.stringStackPtr];
				packet2 = TcpPacket.createPacket(ClientProt.QUICKCHAT_PRIVATE, TcpPacket.isaac);
				packet2.buffer.writeByte(0);
				int payloadStart = packet2.buffer.currentPosition;
				packet2.buffer.writeString(string);
				packet2.buffer.writeShort(CS2State.currentQuickChatPhrase.id);
				CS2State.currentQuickChatPhrase.type.encodeValues(CS2State.currentQuickChatPhrase.values, packet2.buffer);
				packet2.buffer.writeIndex(packet2.buffer.currentPosition - payloadStart);
				BufferedConnectionContext.queuePacket(packet2);
				return;
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SENDCLAN) {
				packet = TcpPacket.createPacket(ClientProt.QUICKCHAT_PUBLIC, TcpPacket.isaac);
				packet.buffer.writeByte(0);
				int startPosition = packet.buffer.currentPosition;
				packet.buffer.writeByte(1);
				packet.buffer.writeShort(CS2State.currentQuickChatPhrase.id);
				CS2State.currentQuickChatPhrase.type.encodeValues(CS2State.currentQuickChatPhrase.values, packet.buffer);
				packet.buffer.writeIndex(packet.buffer.currentPosition - startPosition);
				BufferedConnectionContext.queuePacket(packet);
				return;
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETSUBCATSHORTCUT) {
				CS2State.intStackPtr -= 2;
				int categoryId = ScriptRunner.intStack[CS2State.intStackPtr];
				int subCategoryIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY.list(categoryId).subCategoryHotkeys[subCategoryIndex];
				return;
			}
			if (opcode == ClientScriptOpCode.CHATCAT_GETPHRASESHORTCUT) {
				CS2State.intStackPtr -= 2;
				int categoryId = ScriptRunner.intStack[CS2State.intStackPtr];
				int phraseIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY.list(categoryId).messageHotkeys[phraseIndex];
				return;
			}
			if (opcode == ClientScriptOpCode.CHATCAT_FINDSUBCATBYSHORTCUT) {
				CS2State.intStackPtr -= 2;
				int categoryId = ScriptRunner.intStack[CS2State.intStackPtr];
				int shortcutKey = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				if (shortcutKey == -1) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY.list(categoryId).findSubcategoryByShortcut((char) shortcutKey);
				return;
			}
			if (opcode == ClientScriptOpCode.CHATCAT_FINDPHRASEBYSHORTCUT) {
				CS2State.intStackPtr -= 2;
				int categoryId = ScriptRunner.intStack[CS2State.intStackPtr];
				int shortcutKey = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				if (shortcutKey == -1) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_CATEGORY.list(categoryId).findPhraseByShortcut((char) shortcutKey);
				return;
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETDYNAMICCOMMANDCOUNT) {
				int phraseId = ScriptRunner.intStack[--CS2State.intStackPtr];
				ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId).getDynamicCommandCount();
				return;
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETDYNAMICCOMMAND) {
				CS2State.intStackPtr -= 2;
				int phraseId = ScriptRunner.intStack[CS2State.intStackPtr];
				int commandIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				int commandTypeId = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId).getValueType(commandIndex).id;
				ScriptRunner.intStack[CS2State.intStackPtr++] = commandTypeId;
				return;
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SETDYNAMICINT) {
				CS2State.intStackPtr -= 2;
				int valueIndex = ScriptRunner.intStack[CS2State.intStackPtr];
				int intValue = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				CS2State.currentQuickChatPhrase.values[valueIndex] = intValue;
				return;
			}
			if (opcode == ClientScriptOpCode.ACTIVECHATPHRASE_SETDYNAMICOBJ) {
				CS2State.intStackPtr -= 2;
				int valueIndex = ScriptRunner.intStack[CS2State.intStackPtr];
				int objId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				CS2State.currentQuickChatPhrase.values[valueIndex] = objId;
				return;
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_GETDYNAMICCOMMANDPARAM_ENUM) {
				CS2State.intStackPtr -= 3;
				int phraseId = ScriptRunner.intStack[CS2State.intStackPtr];
				int commandIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				int paramTypeId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
				@Pc(1448) QuickchatMessageType quickChatPhraseType2 = TypeLists.QUICK_CHAT_MESSAGE.list(phraseId);
				if (quickChatPhraseType2.getValueType(commandIndex).id != 0) {
					throw new RuntimeException("bad command");
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = quickChatPhraseType2.getDynamicCommandParam(paramTypeId, commandIndex);
				return;
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_FIND) {
				string = CS2State.stringStack[--CS2State.stringStackPtr];
				boolean isMenuSearch = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
				QuickchatMessageTypeList.searchQuickChatPhrases(isMenuSearch, string);
				ScriptRunner.intStack[CS2State.intStackPtr++] = Find.findResultCount;
				return;
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_FINDNEXT) {
				if (Find.queryResults != null && MessageBox.queryResultsCursor < Find.findResultCount) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = Find.queryResults[MessageBox.queryResultsCursor++] & 0xFFFF;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
				return;
			}
			if (opcode == ClientScriptOpCode.CHATPHRASE_FINDRESTART) {
				MessageBox.queryResultsCursor = 0;
				return;
			}
		} else if (opcode < 5200) {
			if (opcode == ClientScriptOpCode.KEYHELD_ALT) {
				if (KeyRecorder.keyRecorder.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_ALT)) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
				return;
			}
			if (opcode == ClientScriptOpCode.KEYHELD_CTRL) {
				if (KeyRecorder.keyRecorder.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_CONTROL)) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
				return;
			}
			if (opcode == ClientScriptOpCode.KEYHELD_SHIFT) {
				if (KeyRecorder.keyRecorder.isPressed(SimpleKeyboardMonitorConstants.KEY_CODE_SHIFT)) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
				return;
			}
		} else {
			@Pc(2833) int displayModeIndex;
			@Pc(2107) boolean local2107;
			if (opcode < 5300) {
				if (opcode == ClientScriptOpCode.WORLDMAP_SETZOOM) {
					WorldMapDisplay.setWorldMapZoom(ScriptRunner.intStack[--CS2State.intStackPtr]);
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETZOOM) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMapRenderer.getWorldMapZoom();
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_SETMAP) {
					WorldMapOpener.setWorldMap(false, -1, -1, ScriptRunner.intStack[--CS2State.intStackPtr]);
					return;
				}
				@Pc(1696) WorldMapElement worldMapArea;
				if (opcode == ClientScriptOpCode.WORLDMAP_GETMAP) {
					int coord = ScriptRunner.intStack[--CS2State.intStackPtr];
					worldMapArea = MapList.getMapDef(coord >> 14 & 0x3FFF, coord & 0x3FFF);
					if (worldMapArea == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.elementId;
					return;
				}
				@Pc(1729) WorldMapElement worldMapArea2;
				if (opcode == ClientScriptOpCode.ORLDMAP_GETMAPNAME) {
					worldMapArea2 = MapList.getArea(ScriptRunner.intStack[--CS2State.intStackPtr]);
					if (worldMapArea2 != null && worldMapArea2.displayName != null) {
						CS2State.stringStack[CS2State.stringStackPtr++] = worldMapArea2.displayName;
						return;
					}
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETSIZE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMap.worldMapViewWidth;
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMap.worldMapViewHeight;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISPLAYPOSITION) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMapDisplay.mapCenterX + WorldMap.mapSquareAbsX;
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMapDisplay.mapCenterY + WorldMap.mapSquareAbsY;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETCONFIGORIGIN) {
					int areaId = ScriptRunner.intStack[--CS2State.intStackPtr];
					worldMapArea = MapList.getArea(areaId);
					if (worldMapArea == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.bitpackedPlacement >> 14 & 0x3FFF;
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.bitpackedPlacement & 0x3FFF;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETCONFIGBOUNDS) {
					int areaId = ScriptRunner.intStack[--CS2State.intStackPtr];
					worldMapArea = MapList.getArea(areaId);
					if (worldMapArea == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.displayMaxX - worldMapArea.displayMinZ;
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.displayZ2 - worldMapArea.boundsMinX;
					return;
				}
				@Pc(1919) WorldMapCategory mapElementListEntry;
				if (opcode == ClientScriptOpCode.WORLDMAP_LISTELEMENT_START) {
					mapElementListEntry = WorldMap.getFirstWorldMapCategory();
					if (mapElementListEntry == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementListEntry.id;
					int coord = mapElementListEntry.level << 28 | mapElementListEntry.x + WorldMap.mapSquareAbsX << 14 | mapElementListEntry.z + WorldMap.mapSquareAbsY;
					ScriptRunner.intStack[CS2State.intStackPtr++] = coord;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_LISTELEMENT_NEXT) {
					mapElementListEntry = WorldMap.getNextActiveCategory();
					if (mapElementListEntry == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementListEntry.id;
					int coord = mapElementListEntry.level << 28 | mapElementListEntry.x + WorldMap.mapSquareAbsX << 14 | mapElementListEntry.z + WorldMap.mapSquareAbsY;
					ScriptRunner.intStack[CS2State.intStackPtr++] = coord;
					return;
				}
				@Pc(2067) boolean converted;
				if (opcode == ClientScriptOpCode.WORLDMAP_JUMPTOSOURCECOORD) {
					int sourceCoord = ScriptRunner.intStack[--CS2State.intStackPtr];
					worldMapArea = WorldMapDisplay.getWorldMapArea();
					if (worldMapArea != null) {
						converted = worldMapArea.convertSourceToDisplay(CS2State.coordConversionBuffer, sourceCoord >> 14 & 0x3FFF, sourceCoord & 0x3FFF, sourceCoord >> 28 & 0x3);
						if (converted) {
							WorldMapDisplay.setWorldMapScrollTarget(CS2State.coordConversionBuffer[1], CS2State.coordConversionBuffer[2]);
						}
					}
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_COORDINMAP) {
					CS2State.intStackPtr -= 2;
					int coord = ScriptRunner.intStack[CS2State.intStackPtr];
					int elementId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					@Pc(2105) Queue queue = MapList.getRenderedElements(coord >> 14 & 0x3FFF, coord & 0x3FFF);
					boolean found = false;
					for (@Pc(2112) WorldMapElement worldMapArea3 = (WorldMapElement) queue.first(); worldMapArea3 != null; worldMapArea3 = (WorldMapElement) queue.next()) {
						if (worldMapArea3.elementId == elementId) {
							found = true;
							break;
						}
					}
					if (found) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETCONFIGZOOM) {
					int areaId = ScriptRunner.intStack[--CS2State.intStackPtr];
					worldMapArea = MapList.getArea(areaId);
					if (worldMapArea == null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = worldMapArea.defaultZoom;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_ISLOADED) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = ComponentMinimap.mapAssetLoadingPercentage == 100 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_JUMPTODISPLAYCOORD) {
					int displayCoord = ScriptRunner.intStack[--CS2State.intStackPtr];
					WorldMapDisplay.setWorldMapScrollTarget(displayCoord >> 14 & 0x3FFF, displayCoord & 0x3FFF);
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETSOURCEPOSITION) {
					worldMapArea2 = WorldMapDisplay.getWorldMapArea();
					if (worldMapArea2 != null) {
						boolean coordsMapped = worldMapArea2.mapToGameCoords(WorldMapDisplay.mapCenterY + WorldMap.mapSquareAbsY, WorldMapDisplay.mapCenterX + WorldMap.mapSquareAbsX, CS2State.coordConversionBuffer);
						if (coordsMapped) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[1];
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[2];
							return;
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_SETMAP_COORD) {
					CS2State.intStackPtr -= 2;
					int areaId = ScriptRunner.intStack[CS2State.intStackPtr];
					int coord = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					WorldMapOpener.setWorldMap(false, coord & 0x3FFF, coord >> 14 & 0x3FFF, areaId);
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISPLAYCOORD) {
					int sourceCoord = ScriptRunner.intStack[--CS2State.intStackPtr];
					worldMapArea = WorldMapDisplay.getWorldMapArea();
					if (worldMapArea != null) {
						converted = worldMapArea.convertSourceToDisplay(CS2State.coordConversionBuffer, sourceCoord >> 14 & 0x3FFF, sourceCoord & 0x3FFF, sourceCoord >> 28 & 0x3);
						if (converted) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[1];
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[2];
							return;
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETSOURCECOORD) {
					int displayCoord = ScriptRunner.intStack[--CS2State.intStackPtr];
					worldMapArea = WorldMapDisplay.getWorldMapArea();
					if (worldMapArea != null) {
						converted = worldMapArea.mapToGameCoords(displayCoord & 0x3FFF, displayCoord >> 14 & 0x3FFF, CS2State.coordConversionBuffer);
						if (converted) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[1];
							ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.coordConversionBuffer[2];
							return;
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_FLASHELEMENT) {
					WorldMapDisplay.flashWorldMapElement(ScriptRunner.intStack[--CS2State.intStackPtr]);
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_SETMAP_COORD_OVERRIDE) {
					CS2State.intStackPtr -= 2;
					int areaId = ScriptRunner.intStack[CS2State.intStackPtr];
					int coord = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					WorldMapOpener.setWorldMap(true, coord & 0x3FFF, coord >> 14 & 0x3FFF, areaId);
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_DISABLEELEMENTS) {
					WorldMapDisplay.hideIcons = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISABLEELEMENTS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMapDisplay.hideIcons ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_FLASHELEMENTCATEGORY) {
					int categoryId = ScriptRunner.intStack[--CS2State.intStackPtr];
					WorldMapDisplay.flashWorldMapElementCategory(categoryId);
					return;
				}
				@Pc(2625) Node node;
				if (opcode == ClientScriptOpCode.WORLDMAP_DISABLEELEMENTCATEGORY) {
					CS2State.intStackPtr -= 2;
					int categoryId = ScriptRunner.intStack[CS2State.intStackPtr];
					boolean disabled = ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1;
					if (WorldMapDisplay.hiddenCategoryIds != null) {
						node = WorldMapDisplay.hiddenCategoryIds.get((long) categoryId);
						if (node != null && !disabled) {
							node.unlink();
							return;
						}
						if (node == null && disabled) {
							node = new Node();
							WorldMapDisplay.hiddenCategoryIds.put((long) categoryId, node);
						}
					}
					return;
				}
				@Pc(2667) Node node2;
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISABLEELEMENTCATEGORY) {
					int categoryId = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (WorldMapDisplay.hiddenCategoryIds != null) {
						node2 = WorldMapDisplay.hiddenCategoryIds.get((long) categoryId);
						ScriptRunner.intStack[CS2State.intStackPtr++] = node2 == null ? 0 : 1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_DISABLEELEMENT) {
					CS2State.intStackPtr -= 2;
					int elementId = ScriptRunner.intStack[CS2State.intStackPtr];
					boolean disabled = ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1;
					if (WorldMapDisplay.hiddenMecIds != null) {
						node = WorldMapDisplay.hiddenMecIds.get((long) elementId);
						if (node != null && !disabled) {
							node.unlink();
							return;
						}
						if (node == null && disabled) {
							node = new Node();
							WorldMapDisplay.hiddenMecIds.put((long) elementId, node);
						}
					}
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETDISABLEELEMENT) {
					int elementId = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (WorldMapDisplay.hiddenMecIds != null) {
						node2 = WorldMapDisplay.hiddenMecIds.get((long) elementId);
						ScriptRunner.intStack[CS2State.intStackPtr++] = node2 == null ? 0 : 1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_GETCURRENTMAP) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WorldMap.area == null ? -1 : WorldMap.area.elementId;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_FINDNEARESTELEMENT) {
					CS2State.intStackPtr -= 2;
					int categoryId = ScriptRunner.intStack[CS2State.intStackPtr];
					int coord = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					int coordX = coord >> 14 & 0x3FFF;
					int coordZ = coord & 0x3FFF;
					int nearestElementId = WorldMap.findNearestWorldMapElement(categoryId, coordX, coordZ);
					if (nearestElementId < 0) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = nearestElementId;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDMAP_CLOSEMAP) {
					Static671.closeWorldMap();
					return;
				}
			} else if (opcode < 5400) {
				if (opcode == ClientScriptOpCode.FULLSCREEN_ENTER) {
					CS2State.intStackPtr -= 2;
					int width = ScriptRunner.intStack[CS2State.intStackPtr];
					int height = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					WindowModeRequest.requestWindowMode(WindowModeType.FULLSCREEN, width, false, height);
					ScriptRunner.intStack[CS2State.intStackPtr++] = WindowMode.fullScreenFrame == null ? 0 : 1;
					return;
				}
				if (opcode == ClientScriptOpCode.FULLSCREEN_EXIT) {
					if (WindowMode.fullScreenFrame != null) {
						WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid.fixedScreen.getValue()), -1, false, -1);
					}
					return;
				}
				if (opcode == ClientScriptOpCode.FULLSCREEN_MODECOUNT) {
					@Pc(2919) DisplayModeDetails[] displayModes = Fullscreen.getActiveDisplayModes();
					ScriptRunner.intStack[CS2State.intStackPtr++] = displayModes.length;
					return;
				}
				if (opcode == ClientScriptOpCode.FULLSCREEN_GETMODE) {
					int modeIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
					@Pc(2943) DisplayModeDetails[] displayModes = Fullscreen.getActiveDisplayModes();
					ScriptRunner.intStack[CS2State.intStackPtr++] = displayModes[modeIndex].width;
					ScriptRunner.intStack[CS2State.intStackPtr++] = displayModes[modeIndex].height;
					return;
				}
				if (opcode == ClientScriptOpCode.FULLSCREEN_LASTMODE) {
					int width = WindowMode.fullScreenWidth;
					int height = WindowMode.fullScreenHeight;
					int modeIndex = -1;
					@Pc(2978) DisplayModeDetails[] displayModes = Fullscreen.getActiveDisplayModes();
					for (displayModeIndex = 0; displayModeIndex < displayModes.length; displayModeIndex++) {
						@Pc(2985) DisplayModeDetails displayMode = displayModes[displayModeIndex];
						if (displayMode.width == width && displayMode.height == height) {
							modeIndex = displayModeIndex;
							break;
						}
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = modeIndex;
					return;
				}
				if (opcode == ClientScriptOpCode.GETWINDOWMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = WindowMode.getWindowMode().id;
					return;
				}
				if (opcode == ClientScriptOpCode.SETWINDOWMODE) {
					int modeId = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (modeId >= 1 && modeId <= 2) {
						WindowModeRequest.requestWindowMode(WindowModeType.fromId(modeId), -1, false, -1);
						return;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.GETDEFAULTWINDOWMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.fixedScreen.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.SETDEFAULTWINDOWMODE) {
					int modeId = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (modeId >= 1 && modeId <= 2) {
						GamePreferences.uid.setValue(GamePreferences.uid.fixedScreen, modeId);
						GamePreferences.uid.setValue(GamePreferences.uid.fullScreen, modeId);
						GamePreferences.savePreferences();
						return;
					}
					return;
				}
			} else if (opcode < 5500) {
				if (opcode == ClientScriptOpCode.OPENURL) {
					CS2State.stringStackPtr -= 2;
					string = CS2State.stringStack[CS2State.stringStackPtr];
					string3 = CS2State.stringStack[CS2State.stringStackPtr + 1];
					int urlFlag = ScriptRunner.intStack[--CS2State.intStackPtr];
					@Pc(3133) TcpPacket urlPacket = TcpPacket.createPacket(ClientProt.OPEN_URL, TcpPacket.isaac);
					urlPacket.buffer.writeByte(Utils.getEncodedStringLength(string) + Utils.getEncodedStringLength(string3) + 1);
					urlPacket.buffer.writeString(string);
					urlPacket.buffer.writeString(string3);
					urlPacket.buffer.writeByte(urlFlag);
					BufferedConnectionContext.queuePacket(urlPacket);
					return;
				}
				if (opcode == ClientScriptOpCode.SETCLIENTPALETTE) {
					CS2State.intStackPtr -= 2;
					TypeLists.recolorPalette[ScriptRunner.intStack[CS2State.intStackPtr]] = (short) FloType.rgbToHsl(ScriptRunner.intStack[CS2State.intStackPtr + 1]);
					TypeLists.OBJ.modelCacheReset();
					TypeLists.OBJ.spriteCacheReset();
					TypeLists.NPC.clearModels();
					InterfaceRectangles.invalidateAllRectangles();
					return;
				}
				if (opcode == ClientScriptOpCode.SPLINE_NEW) {
					CS2State.intStackPtr -= 2;
					int pathIndex = ScriptRunner.intStack[CS2State.intStackPtr];
					int pointCount = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					if (pathIndex >= 0 && pathIndex < 2) {
						Camera.cameraSplinePaths[pathIndex] = new int[pointCount << 1][4];
					}
					return;
				}
				if (opcode == ClientScriptOpCode.SPLINE_ADDPOINT) {
					CS2State.intStackPtr -= 7;
					int pathIndex = ScriptRunner.intStack[CS2State.intStackPtr];
					int pointIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1] << 1;
					int pointCoord = ScriptRunner.intStack[CS2State.intStackPtr + 2];
					int cameraHeight = ScriptRunner.intStack[CS2State.intStackPtr + 3];
					int lookAtCoord = ScriptRunner.intStack[CS2State.intStackPtr + 4];
					@Pc(3271) int lookAtHeight = ScriptRunner.intStack[CS2State.intStackPtr + 5];
					@Pc(3277) int pitch = ScriptRunner.intStack[CS2State.intStackPtr + 6];
					if (pathIndex >= 0 && pathIndex < 2 && Camera.cameraSplinePaths[pathIndex] != null && pointIndex >= 0 && pointIndex < Camera.cameraSplinePaths[pathIndex].length) {
						Camera.cameraSplinePaths[pathIndex][pointIndex] = new int[] { (pointCoord >> 14 & 0x3FFF) << 9, cameraHeight << 2, (pointCoord & 0x3FFF) << 9, pitch };
						Camera.cameraSplinePaths[pathIndex][pointIndex + 1] = new int[] { (lookAtCoord >> 14 & 0x3FFF) << 9, lookAtHeight << 2, (lookAtCoord & 0x3FFF) << 9 };
					}
					return;
				}
				if (opcode == ClientScriptOpCode.SPLINE_LENGTH) {
					int pointCount = Camera.cameraSplinePaths[ScriptRunner.intStack[--CS2State.intStackPtr]].length >> 1;
					ScriptRunner.intStack[CS2State.intStackPtr++] = pointCount;
					return;
				}
				if (opcode == ClientScriptOpCode.QUIT) {
					if (WindowMode.fullScreenFrame != null) {
						WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid.fixedScreen.getValue()), -1, false, -1);
					}
					if (GameShell.engineFrame != null) {
						VarcTypeList.savePersistentVarcs();
						System.exit(0);
						return;
					}
					string = GameShell.quitUrl == null ? ServerEnvironment.getUrl() : GameShell.quitUrl;
					ClientURLTools.openUrl(string, GameShell.signLink, GamePreferences.uid.toolkit.getValue() == ToolkitType.GL, false);
					return;
				}
				if (opcode == ClientScriptOpCode.LASTLOGIN) {
					string = "";
					if (LoginManager.lastLoginHostLookup != null) {
						if (LoginManager.lastLoginHostLookup.result == null) {
							string = Utils.ipToString(LoginManager.lastLoginHostLookup.intData1);
						} else {
							string = (String) LoginManager.lastLoginHostLookup.result;
						}
					}
					CS2State.stringStack[CS2State.stringStackPtr++] = string;
					return;
				}
				if (opcode == 5420) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.signLink.signed ? 0 : 1;
					return;
				}
				if (opcode == ClientScriptOpCode.OPENURL_NOLOGIN) {
					if (WindowMode.fullScreenFrame != null) {
						WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid.fixedScreen.getValue()), -1, false, -1);
					}
					string = CS2State.stringStack[--CS2State.stringStackPtr];
					boolean newWindow = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
					string2 = ServerEnvironment.getUrl() + string;
					ClientURLTools.openUrl(string2, GameShell.signLink, GamePreferences.uid.toolkit.getValue() == ToolkitType.GL, newWindow);
					return;
				}
				if (opcode == 5422) {
					CS2State.stringStackPtr -= 2;
					string = CS2State.stringStack[CS2State.stringStackPtr];
					string3 = CS2State.stringStack[CS2State.stringStackPtr + 1];
					int affixIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (string.length() > 0) {
						if (PlayerEntity.playerNamePrefixes == null) {
							PlayerEntity.playerNamePrefixes = new String[PlayerEntity.playerNameAffixCounts[Game.CURRENT_GAME.id]];
						}
						PlayerEntity.playerNamePrefixes[affixIndex] = string;
					}
					if (string3.length() > 0) {
						if (PlayerEntity.playerNameSuffixes == null) {
							PlayerEntity.playerNameSuffixes = new String[PlayerEntity.playerNameAffixCounts[Game.CURRENT_GAME.id]];
						}
						PlayerEntity.playerNameSuffixes[affixIndex] = string3;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.WRITECONSOLE) {
					System.out.println(CS2State.stringStack[--CS2State.stringStackPtr]);
					return;
				}
				if (opcode == ClientScriptOpCode.FORMATMINIMENU) {
					CS2State.intStackPtr -= 11;
					MiniMenu.menuBackgroundColour = ScriptRunner.intStack[CS2State.intStackPtr];
					MiniMenu.menuBackgroundTransparency = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					MiniMenu.menuHighlightColour = ScriptRunner.intStack[CS2State.intStackPtr + 2];
					MiniMenu.menuHighlightTransparency = ScriptRunner.intStack[CS2State.intStackPtr + 3];
					MiniMenu.menuHeaderSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 4];
					MiniMenu.menuHeaderCornerSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 5];
					Find.menuBottomBorderSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 6];
					StaticMedia.menuSideBorderSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 7];
					ScriptRunner.menuCornerSpriteId = ScriptRunner.intStack[CS2State.intStackPtr + 8];
					MiniMenu.menuTextColour = ScriptRunner.intStack[CS2State.intStackPtr + 9];
					MiniMenu.menuHoverTextColour = ScriptRunner.intStack[CS2State.intStackPtr + 10];
					Resource.SPRITES.isFileReady(MiniMenu.menuHeaderSpriteId);
					Resource.SPRITES.isFileReady(MiniMenu.menuHeaderCornerSpriteId);
					Resource.SPRITES.isFileReady(Find.menuBottomBorderSpriteId);
					Resource.SPRITES.isFileReady(StaticMedia.menuSideBorderSpriteId);
					Resource.SPRITES.isFileReady(ScriptRunner.menuCornerSpriteId);
					StaticMedia.menuBottomBorderSprite = null;
					StaticMedia.menuBottomRightCornerSprite = null;
					StaticMedia.menuBottomLeftCornerSprite = null;
					MiniMenu.menuHeaderRightCornerSprite = null;
					StaticMedia.menuHeaderLeftCornerSprite = null;
					MiniMenu.menuHeaderSprite = null;
					StaticMedia.menuRightSideBorderSprite = null;
					StaticMedia.menuLeftSideBorderSprite = null;
					FontMetrics.useBoldFont = true;
					return;
				}
				if (opcode == ClientScriptOpCode.DEFAULTMINIMENU) {
					MiniMenu.resetMiniMenuSprites();
					FontMetrics.useBoldFont = false;
					return;
				}
				if (opcode == ClientScriptOpCode.SETDEFAULTCURSORS) {
					CS2State.intStackPtr -= 2;
					CursorManager.defaultCursorId = ScriptRunner.intStack[CS2State.intStackPtr];
					CursorManager.defaultAttackCursor = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					return;
				}
				if (opcode == ClientScriptOpCode.SETHARDCODEDOPCURSORS) {
					CS2State.intStackPtr -= 2;
					MiniMenu.examineCursor = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					return;
				}
				if (opcode == ClientScriptOpCode.MINIMENUOPEN) {
					CS2State.intStackPtr -= 2;
					int componentHash = ScriptRunner.intStack[CS2State.intStackPtr];
					int slot = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					ScriptRunner.intStack[CS2State.intStackPtr++] = MiniMenu.isOpenForComponent(slot, componentHash) ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DOCHEAT) {
					ClientCommandProcessor.executeCommand(CS2State.stringStack[--CS2State.stringStackPtr], false, false);
					return;
				}
				if (opcode == ClientScriptOpCode.NOTIFY_ACCOUNTCREATED) {
					try {
						BrowserJavaScript.callJavaScript(GameShell.applet, "accountcreated");
						return;
					} catch (@Pc(3819) Throwable ex) {
						return;
					}
				}
				if (opcode == ClientScriptOpCode.NOTIFY_ACCOUNTCREATESTARTED) {
					try {
						BrowserJavaScript.callJavaScript(GameShell.applet, "accountcreatestarted");
						return;
					} catch (@Pc(3830) Throwable ex2) {
						return;
					}
				}
				if (opcode == ClientScriptOpCode.GETCLIPBOARD) {
					string = "";
					if (Console.clipboard != null) {
						@Pc(3842) Transferable transferable = Console.clipboard.getContents(null);
						if (transferable != null) {
							try {
								string = (String) transferable.getTransferData(DataFlavor.stringFlavor);
								if (string == null) {
									string = "";
								}
							} catch (@Pc(3855) Exception ex3) {
							}
						}
					}
					CS2State.stringStack[CS2State.stringStackPtr++] = string;
					return;
				}
				if (opcode == ClientScriptOpCode.SETSUBMENUMINLENGTH) {
					MiniMenu.maxVisibleEntries = ScriptRunner.intStack[--CS2State.intStackPtr];
					return;
				}
			} else if (opcode < 5600) {
				if (opcode == ClientScriptOpCode.CAM_MOVETO) {
					CS2State.intStackPtr -= 4;
					int coord = ScriptRunner.intStack[CS2State.intStackPtr];
					int height = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					int constantSpeed = ScriptRunner.intStack[CS2State.intStackPtr + 2];
					int variableSpeed = ScriptRunner.intStack[CS2State.intStackPtr + 3];
					Camera.moveCamera(constantSpeed, (coord >> 14 & 0x3FFF) - MapRegion.sceneBaseX, false, height << 2, variableSpeed, (coord & 0x3FFF) - MapRegion.sceneBaseY);
					return;
				}
				if (opcode == 5501) {
					CS2State.intStackPtr -= 4;
					int coord = ScriptRunner.intStack[CS2State.intStackPtr];
					int height = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					int constantSpeed = ScriptRunner.intStack[CS2State.intStackPtr + 2];
					int variableSpeed = ScriptRunner.intStack[CS2State.intStackPtr + 3];
					Camera.lookAt((coord & 0x3FFF) - MapRegion.sceneBaseY, (coord >> 14 & 0x3FFF) - MapRegion.sceneBaseX, height << 2, variableSpeed, constantSpeed);
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_MOVEALONG) {
					CS2State.intStackPtr -= 6;
					int positionPathIndex = ScriptRunner.intStack[CS2State.intStackPtr];
					if (positionPathIndex >= 2) {
						throw new RuntimeException();
					}
					Camera.cameraPositionPathIndex = positionPathIndex;
					int positionKeyframe = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					if (positionKeyframe + 1 >= Camera.cameraSplinePaths[Camera.cameraPositionPathIndex].length >> 1) {
						throw new RuntimeException();
					}
					Camera.cameraPositionKeyframe = positionKeyframe;
					Camera.cameraSplineProgress = 0;
					Camera.cameraSplineSpeedStart = ScriptRunner.intStack[CS2State.intStackPtr + 2];
					Camera.cameraSplineSpeedEnd = ScriptRunner.intStack[CS2State.intStackPtr + 3];
					int lookAtPathIndex = ScriptRunner.intStack[CS2State.intStackPtr + 4];
					if (lookAtPathIndex >= 2) {
						throw new RuntimeException();
					}
					ColourUtils.cameraLookAtPathIndex = lookAtPathIndex;
					int lookAtKeyframe = ScriptRunner.intStack[CS2State.intStackPtr + 5];
					if (lookAtKeyframe + 1 >= Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex].length >> 1) {
						throw new RuntimeException();
					}
					Camera.cameraLookAtKeyframe = lookAtKeyframe;
					Camera.cameraCalculationType = CameraMode.MODE_SPLINE;
					Camera.roofRemovalTargetY = -1;
					Camera.roofRemovalTargetX = -1;
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_RESET) {
					Camera.reset();
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_FORCEANGLE) {
					CS2State.intStackPtr -= 2;
					Camera.forceCameraAngle(ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1]);
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_GETANGLE_XA) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = (int) Camera.camAngleX >> 3;
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_GETANGLE_YA) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = (int) Camera.camAngleY >> 3;
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_INC_Y) {
					Camera.applyCameraPitchUp();
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_DEC_Y) {
					Camera.applyCameraPitchDown();
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_INC_X) {
					Camera.applyCameraYawRight();
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_DEC_X) {
					Camera.applyCameraYawLeft();
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_FOLLOWCOORD) {
					int coord = ScriptRunner.intStack[--CS2State.intStackPtr];
					int localX = coord >> 14 & 0x3FFF;
					int sceneZ = coord & 0x3FFF;
					localX -= MapRegion.sceneBaseX;
					if (localX < 0) {
						localX = 0;
					} else if (localX >= MapRegion.sceneSizeX) {
						localX = MapRegion.sceneSizeX;
					}
					sceneZ -= MapRegion.sceneBaseY;
					if (sceneZ < 0) {
						sceneZ = 0;
					} else if (sceneZ >= MapRegion.sceneSizeY) {
						sceneZ = MapRegion.sceneSizeY;
					}
					Camera.cameraTargetAbsX = (localX << 9) + 256;
					Camera.cameraTargetAbsZ = (sceneZ << 9) + 256;
					Camera.cameraCalculationType = CameraMode.MODE_FOLLOWCOORD;
					Camera.roofRemovalTargetY = -1;
					Camera.roofRemovalTargetX = -1;
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_SMOOTH_RESET) {
					Camera.startSmoothTransition();
					return;
				}
				if (opcode == 5514) {
					OrthoMode.orthoZoom = ScriptRunner.intStack[--CS2State.intStackPtr];
					return;
				}
				if (opcode == 5516) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = OrthoMode.orthoZoom;
					return;
				}
				if (opcode == ClientScriptOpCode.CAM_REMOVEROOF) {
					int coord = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (coord == -1) {
						int localX = coord >> 14 & 0x3FFF;
						int sceneZ = coord & 0x3FFF;
						localX -= MapRegion.sceneBaseX;
						if (localX < 0) {
							localX = 0;
						} else if (localX >= MapRegion.sceneSizeX) {
							localX = MapRegion.sceneSizeX;
						}
						sceneZ -= MapRegion.sceneBaseY;
						if (sceneZ < 0) {
							sceneZ = 0;
						} else if (sceneZ >= MapRegion.sceneSizeY) {
							sceneZ = MapRegion.sceneSizeY;
						}
						Camera.roofRemovalTargetX = (localX << 9) + 256;
						Camera.roofRemovalTargetY = (sceneZ << 9) + 256;
						return;
					}
					Camera.roofRemovalTargetX = -1;
					Camera.roofRemovalTargetY = -1;
					return;
				}
				if (opcode == 5547 && ClientFeatures.VOID) {
					// Not part of the original client. Void's scripts ask whether the camera is in its default mode.
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.cameraCalculationType == CameraMode.MODE_DEFAULT ? 1 : 0;
					return;
				}
			} else if (opcode < 5700) {
				if (opcode == ClientScriptOpCode.LOGIN_REQUEST) {
					CS2State.stringStackPtr -= 2;
					string = CS2State.stringStack[CS2State.stringStackPtr];
					string3 = CS2State.stringStack[CS2State.stringStackPtr + 1];
					int loginFlag = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (string.length() > 320) {
						return;
					}
					if (ClientState.mainLogicStep != MainLogicStep.STEP_LOGIN_SCREEN) {
						return;
					}
					if (LoginManager.loginStep == LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						LoginManager.loginUsername = string;
						LoginManager.loginPassword = string3;
						LoginManager.anInt6172 = loginFlag;
						MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_GAME);
						return;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.LOGIN_CONTINUE) {
					LoginManager.confirmLoginContinue();
					return;
				}
				if (opcode == ClientScriptOpCode.LOGIN_RESETREPLY) {
					if (LoginManager.loginStep == LoginStep.IDLE) {
						LoginManager.lobbyLoginResponse = -2;
						LoginManager.worldLoginResponse = -2;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.CREATE_AVAILABLEREQUEST) {
					CS2State.stringStackPtr--;
					if (ClientState.mainLogicStep != MainLogicStep.STEP_LOGIN_SCREEN) {
						return;
					}
					if (LoginManager.loginStep == LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						LobbyManager.sendCreateAvailableRequest(CS2State.stringStack[CS2State.stringStackPtr]);
						return;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.CREATE_CONNECTREQUEST) {
					CS2State.stringStackPtr -= 2;
					CS2State.intStackPtr -= 2;
					if (ClientState.mainLogicStep != MainLogicStep.STEP_LOGIN_SCREEN) {
						return;
					}
					if (LoginManager.loginStep == LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						LobbyManager.sendCreateConnectRequest(CS2State.stringStack[CS2State.stringStackPtr + 1], CS2State.stringStack[CS2State.stringStackPtr], ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1);
						return;
					}
					return;
				}
				if (opcode == 5606) {
					if (LobbyManager.accountCreationStep == 0) {
						LobbyManager.accountCreationReply = -2;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.LOGIN_REPLY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.worldLoginResponse;
					return;
				}
				if (opcode == ClientScriptOpCode.LOGIN_HOPTIME) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.loginHopTime;
					return;
				}
				if (opcode == ClientScriptOpCode.CREATE_REPLY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LobbyManager.accountCreationReply;
					return;
				}
				if (opcode == ClientScriptOpCode.LOGIN_DISALLOWRESULT) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginDisallowReason.loginDisallowReason;
					return;
				}
				if (opcode == ClientScriptOpCode.LOBBY_ENTERGAME) {
					int loginFlag = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (ClientState.mainLogicStep != MainLogicStep.STEP_LOBBY_SCREEN) {
						return;
					}
					if (LoginManager.loginStep == LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						if (Connection.connection != null) {
							Connection.connection.close();
							Connection.connection = null;
						}
						LoginManager.anInt6172 = loginFlag;
						MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGGING_IN_FROM_LOBBYSCREEN_TO_GAME);
						return;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.LOBBY_ENTERGAME_REPLY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.worldLoginResponse;
					return;
				}
				if (opcode == ClientScriptOpCode.LOBBY_ENTERLOBBY) {
					CS2State.stringStackPtr -= 2;
					string = CS2State.stringStack[CS2State.stringStackPtr];
					string3 = CS2State.stringStack[CS2State.stringStackPtr + 1];
					if (string.length() > 320) {
						return;
					}
					if (ClientState.mainLogicStep != MainLogicStep.STEP_LOGIN_SCREEN) {
						return;
					}
					if (LoginManager.loginStep == LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						if (Connection.connection != null) {
							Connection.connection.close();
							Connection.connection = null;
						}
						LoginManager.loginUsername = string;
						LoginManager.loginPassword = string3;
						MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_LOBBY);
						return;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.LOBBY_LEAVELOBBY) {
					ClientLogout.logout(false);
					return;
				}
				if (opcode == ClientScriptOpCode.LOBBY_ENTERLOBBYREPLY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lobbyLoginResponse;
					return;
				}
				if (opcode == 5618) {
					CS2State.intStackPtr--;
					return;
				}
				if (opcode == 5619) {
					CS2State.intStackPtr--;
					return;
				}
				if (opcode == 5620) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					return;
				}
				if (opcode == 5621) {
					CS2State.stringStackPtr -= 2;
					CS2State.intStackPtr -= 2;
					return;
				}
				if (opcode == 5622) {
					return;
				}
				if (opcode == 5623) {
					if (LoginManager.ssKey != null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					return;
				}
				if (opcode == ClientScriptOpCode.USERFLOW_FLAGS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = (int) (GameShell.machineUid >> 32);
					ScriptRunner.intStack[CS2State.intStackPtr++] = (int) (GameShell.machineUid & 0xFFFFL);
					return;
				}
				if (opcode == ClientScriptOpCode.CREATE_UNDER13) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LobbyManager.isUnder13 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.CREATE_SETUNDER13) {
					LobbyManager.isUnder13 = true;
					LobbyManager.setUnder13Cookie();
					return;
				}
			} else if (opcode < 6100) {
				if (opcode == ClientScriptOpCode.DETAIL_BRIGHTNESS) {
					int brightness = ScriptRunner.intStack[--CS2State.intStackPtr];
					GamePreferences.uid.setValue(GamePreferences.uid.brightness, brightness);
					MapRebuild.rebuildMap();
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				@Pc(4786) boolean enabled;
				if (opcode == 6002) {
					enabled = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
					GamePreferences.uid.setValue(GamePreferences.uid.animateBackground, enabled ? 1 : 0);
					GamePreferences.uid.setValue(GamePreferences.uid.animateBackgroundOverride, enabled ? 1 : 0);
					MapRebuild.rebuildMap();
					Scene.setRoofOverride();
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_REMOVEROOFS_OPTION) {
					enabled = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
					GamePreferences.uid.setValue(GamePreferences.uid.removeRoofs, enabled ? 2 : 1);
					GamePreferences.uid.setValue(GamePreferences.uid.lb, enabled ? 2 : 1);
					Scene.setRoofOverride();
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_GROUNDDECOR_ON) {
					GamePreferences.uid.setValue(GamePreferences.uid.groundDecor, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 1 : 0);
					MapRebuild.rebuildMap();
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_IDLEANIMS_MANY) {
					GamePreferences.uid.setValue(GamePreferences.uid.idleAnimations, ScriptRunner.intStack[--CS2State.intStackPtr]);
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_FLICKERING_ON) {
					GamePreferences.uid.setValue(GamePreferences.uid.flickeringEffects, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 1 : 0);
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_SPOTSHADOWS_ON) {
					GamePreferences.uid.setValue(GamePreferences.uid.characterShadows, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 1 : 0);
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_HARDSHADOWS) {
					GamePreferences.uid.setValue(GamePreferences.uid.hardShadows, ScriptRunner.intStack[--CS2State.intStackPtr]);
					MapRebuild.rebuildMap();
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_LIGHTDETAIL_HIGH) {
					GamePreferences.uid.setValue(GamePreferences.uid.lightDetail, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 1 : 0);
					GraphicCacheReset.resetGraphicCaches();
					EnvironmentManager.requestDirectEnvironmentTransition();
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_WATERDETAIL_HIGH) {
					GamePreferences.uid.setValue(GamePreferences.uid.water, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 2 : 0);
					MapRebuild.rebuildMap();
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_FOG_ON) {
					GamePreferences.uid.setValue(GamePreferences.uid.fog, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 1 : 0);
					MapRebuild.rebuildMap();
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_ANTIALIASING_QUALITY) {
					GamePreferences.uid.setValue(GamePreferences.uid.antiAliasingPreview, ScriptRunner.intStack[--CS2State.intStackPtr]);
					ToolkitSwitch.loadAssets(false, GamePreferences.uid.toolkit.getValue());
					GamePreferences.savePreferences();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_STEREO) {
					GamePreferences.uid.setValue(GamePreferences.uid.monoStereo, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 1 : 0);
					SoundManager.reloadSoundSystem();
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_SOUNDVOL) {
					GamePreferences.uid.setValue(GamePreferences.uid.soundEffectVolume, ScriptRunner.intStack[--CS2State.intStackPtr]);
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_MUSICVOL) {
					int musicVolume = ScriptRunner.intStack[--CS2State.intStackPtr];
					int currentVolume = GamePreferences.uid.ingameMusicVolume.getValue();
					if (musicVolume != currentVolume) {
						if (MainLogicManager.isInGameStep(ClientState.mainLogicStep)) {
							if (currentVolume == 0 && AreaSoundPlayer.currentMusicArchive != -1) {
								PlayMusic.playMusic(AreaSoundPlayer.currentMusicArchive, Resource.MUSIC, musicVolume);
								MidiChannelVolumeReset.resetChannelVolumes();
								SoundEffectPlayer.soundJinglePlaying = false;
							} else if (musicVolume == 0) {
								SoundResourceManager.stopMusic();
								SoundEffectPlayer.soundJinglePlaying = false;
							} else {
								SoundEffectPlayer.setMusicVolume(musicVolume);
							}
						}
						GamePreferences.uid.setValue(GamePreferences.uid.ingameMusicVolume, musicVolume);
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_BGSOUNDVOL) {
					GamePreferences.uid.setValue(GamePreferences.uid.ambientSoundVolume, ScriptRunner.intStack[--CS2State.intStackPtr]);
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_REMOVEROOFS_OPTION_OVERRIDE) {
					int removeRoofsValue = GamePreferences.uid.removeRoofs.getValue();
					GamePreferences.uid.setValue(GamePreferences.uid.lb, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 0 : removeRoofsValue);
					Scene.setRoofOverride();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_PARTICLES) {
					int particlesLevel = ScriptRunner.intStack[--CS2State.intStackPtr];
					GamePreferences.uid.setValue(GamePreferences.uid.particles, particlesLevel);
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_ANTIALIASING_DEFAULT) {
					GamePreferences.uid.setValue(GamePreferences.uid.antiAliasing, ScriptRunner.intStack[--CS2State.intStackPtr]);
					GamePreferences.savePreferences();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_BUILDAREA) {
					GamePreferences.uid.setValue(GamePreferences.uid.buildArea, ScriptRunner.intStack[--CS2State.intStackPtr]);
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_BLOOM) {
					int bloomSetting = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (bloomSetting < 0 || bloomSetting > 1) {
						bloomSetting = 0;
					}
					GamePreferences.setBloom(bloomSetting == 1);
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_CUSTOMCURSORS) {
					GamePreferences.uid.setValue(GamePreferences.uid.customCursor, ScriptRunner.intStack[--CS2State.intStackPtr] == 0 ? 0 : 1);
					GamePreferences.savePreferences();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_IDLEANIMS) {
					GamePreferences.uid.setValue(GamePreferences.uid.idleAnimations, ScriptRunner.intStack[--CS2State.intStackPtr]);
					GamePreferences.savePreferences();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_GROUNDBLOUNDING) {
					GamePreferences.uid.setValue(GamePreferences.uid.groundBlending, ScriptRunner.intStack[--CS2State.intStackPtr] == 0 ? 0 : 1);
					GamePreferences.savePreferences();
					MapRebuild.rebuildMap();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_TOOLKIT) {
					int toolkitId = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (toolkitId < ToolkitType.JAVA || toolkitId > ToolkitType.GLX) {
						toolkitId = ToolkitType.SSE;
					}
					ToolkitSwitch.loadAssets(false, toolkitId);
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_TOOLKIT_DEFAULT) {
					CS2State.intStackPtr -= 2;
					int toolkitId = ScriptRunner.intStack[CS2State.intStackPtr];
					boolean keepGraphicsPreset = ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1;
					GamePreferences.uid.setValue(GamePreferences.uid.toolkitDefault, toolkitId);
					if (!keepGraphicsPreset) {
						GamePreferences.uid.setValue(GamePreferences.uid.graphics, 0);
					}
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_CPUUSAGE) {
					GamePreferences.uid.setValue(GamePreferences.uid.cpu, ScriptRunner.intStack[--CS2State.intStackPtr]);
					GamePreferences.savePreferences();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_TEXTURING) {
					GamePreferences.uid.setValue(GamePreferences.uid.textures, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 1 : 0);
					GamePreferences.savePreferences();
					GraphicCacheReset.resetGraphicCaches();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_ANIMDETAIL) {
					int animateBackgroundValue = GamePreferences.uid.animateBackground.getValue();
					GamePreferences.uid.setValue(GamePreferences.uid.animateBackgroundOverride, ScriptRunner.intStack[--CS2State.intStackPtr] == 1 ? 1 : animateBackgroundValue);
					MapRebuild.rebuildMap();
					Scene.setRoofOverride();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_MAXSCREENSIZE) {
					GamePreferences.uid.setValue(GamePreferences.uid.maxScreenSize, ScriptRunner.intStack[--CS2State.intStackPtr]);
					GamePreferences.savePreferences();
					WindowMode.needsResize = true;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_SPEECHVOL) {
					GamePreferences.uid.setValue(GamePreferences.uid.voiceOverVolume, ScriptRunner.intStack[--CS2State.intStackPtr]);
					GamePreferences.savePreferences();
					GamePreferences.preferencesSavedOnServer = false;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_LOGINVOL) {
					int loginVolume = ScriptRunner.intStack[--CS2State.intStackPtr];
					int currentVolume = GamePreferences.uid.musicVolume.getValue();
					if (loginVolume != currentVolume && AreaSoundPlayer.currentMusicArchive == DefaultsFile.fileId) {
						if (!MainLogicManager.isInGameStep(ClientState.mainLogicStep)) {
							if (currentVolume == 0) {
								PlayMusic.playMusic(AreaSoundPlayer.currentMusicArchive, Resource.MUSIC, loginVolume);
								MidiChannelVolumeReset.resetChannelVolumes();
								SoundEffectPlayer.soundJinglePlaying = false;
							} else if (loginVolume == 0) {
								SoundResourceManager.stopMusic();
								SoundEffectPlayer.soundJinglePlaying = false;
							} else {
								SoundEffectPlayer.setMusicVolume(loginVolume);
							}
						}
						GamePreferences.uid.setValue(GamePreferences.uid.musicVolume, loginVolume);
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_LOADINGSCREENTYPE) {
					int loadingScreenType = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (loadingScreenType > 255 || loadingScreenType < 0) {
						loadingScreenType = 0;
					}
					if (loadingScreenType != GamePreferences.uid.gametipStartStagePreference.getValue()) {
						GamePreferences.uid.setValue(GamePreferences.uid.gametipStartStagePreference, loadingScreenType);
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.DETAIL_ORTHOGRAPHIC) {
					int orthographicValue = ScriptRunner.intStack[--CS2State.intStackPtr];
					if (orthographicValue != GamePreferences.uid.orthographic.getValue()) {
						GamePreferences.uid.setValue(GamePreferences.uid.orthographic, orthographicValue);
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
						OrthoMode.applyOrthographicMode();
					}
					return;
				}
			} else if (opcode < 6200) {
				if (opcode == ClientScriptOpCode.DETAILGET_BRIGHTNESS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.brightness.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_ANIMDETAIL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.animateBackground.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_REMOVEROOFS_OPTIONS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.removeRoofs.getValue() == 2 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_GROUNDDECOR_ON) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.groundDecor.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_IDLEANIMS_MANY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.idleAnimations.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_FLICKERING_ON) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.flickeringEffects.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_SPOTSHADOWS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.characterShadows.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_HARDSHADOWS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.hardShadows.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_LIGHTDETAIL_HIGH) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.lightDetail.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_WATERDETAIL_HIGH) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.water.getValue() == 2 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_FOG_ON) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.fog.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_ANTIALIASING_QUALITY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.antiAliasingPreview.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_STEREO) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.monoStereo.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_SOUNDVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.soundEffectVolume.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_MUSICVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.ingameMusicVolume.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_BGSOUNDVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.ambientSoundVolume.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_PARTICLES) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = ChatHistory.getParticleDetailLevel();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_ANTIALIASING) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.antiAliasing.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_BUILDAREA) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.buildArea.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_BLOOM) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.bloom.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_CUSTOMCURSORS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.customCursor.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_IDLEANIMS) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.idleAnimations.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_GROUNDBLENDING) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.groundBlending.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_TOOLKIT) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.toolkit.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_TOOLKIT_DEFAULT) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.toolkitDefault.getValue();
					return;
				}
				if (opcode == 6133) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.signLink.signed && !GameShell.signLink.microsoftJava ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_CPUUSAGE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.cpu.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_TEXTURING) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.textures.getValue() == 1 ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_PERFORMANCE_METRIC) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = ToolkitProfiler.getProfilingPerformance(GamePreferences.uid.toolkit.getValue(), 200);
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_MAXSCREENSIZE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.maxScreenSize.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_SPEECHVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.voiceOverVolume.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_LOGINVOL) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.musicVolume.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_SAFEMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = client.isForcedSafemode ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_LOADINGSCREENTYPE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.gametipStartStagePreference.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_ORTHOGRAPHIC) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.orthographic.getValue();
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_CANCHOOSESAFEMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = SystemInfo.SYSTEM_INFO.ram < 512 || client.isForcedSafemode || ToolkitOption.renderWithCpu ? 1 : 0;
					return;
				}
				if (opcode == ClientScriptOpCode.DETAILGET_CHOSESAFEMODE) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = LoadingStage.safeModeChosenAtStartup ? 1 : 0;
					return;
				}
			} else if (opcode < 6300) {
				if (opcode == ClientScriptOpCode.VIEWPORT_SETFOV) {
					CS2State.intStackPtr -= 2;
					Camera.viewportFovMin = (short) ScriptRunner.intStack[CS2State.intStackPtr];
					if (Camera.viewportFovMin <= 0) {
						Camera.viewportFovMin = 256;
					}
					Camera.viewportFovMax = (short) ScriptRunner.intStack[CS2State.intStackPtr + 1];
					if (Camera.viewportFovMax <= 0) {
						Camera.viewportFovMax = 205;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_SETZOOM) {
					CS2State.intStackPtr -= 2;
					Camera.cameraZoomMin = (short) ScriptRunner.intStack[CS2State.intStackPtr];
					if (Camera.cameraZoomMin <= 0) {
						Camera.cameraZoomMin = 256;
					}
					Camera.cameraZoomMax = (short) ScriptRunner.intStack[CS2State.intStackPtr + 1];
					if (Camera.cameraZoomMax <= 0) {
						Camera.cameraZoomMax = 320;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_CLAMPFOV) {
					CS2State.intStackPtr -= 4;
					Camera.fovScaleMin = (short) ScriptRunner.intStack[CS2State.intStackPtr];
					if (Camera.fovScaleMin <= 0) {
						Camera.fovScaleMin = 1;
					}
					Camera.fovScaleMax = (short) ScriptRunner.intStack[CS2State.intStackPtr + 1];
					if (Camera.fovScaleMax <= 0) {
						Camera.fovScaleMax = 32767;
					} else if (Camera.fovScaleMax < Camera.fovScaleMin) {
						Camera.fovScaleMax = Camera.fovScaleMin;
					}
					Camera.aspectFovMin = (short) ScriptRunner.intStack[CS2State.intStackPtr + 2];
					if (Camera.aspectFovMin <= 0) {
						Camera.aspectFovMin = 1;
					}
					Camera.aspectFovMax = (short) ScriptRunner.intStack[CS2State.intStackPtr + 3];
					if (Camera.aspectFovMax <= 0) {
						Camera.aspectFovMax = 32767;
						return;
					}
					if (Camera.aspectFovMax < Camera.aspectFovMin) {
						Camera.aspectFovMax = Camera.aspectFovMin;
					}
					return;
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_GETEFFECTIVESIZE) {
					AbstractToolkit.calculateGameArea(0, 0, InterfaceManager.gameScreenComponent.width, false, InterfaceManager.gameScreenComponent.height);
					ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.gameAreaWidth;
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.gameAreaHeight;
					return;
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_GETZOOM) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.cameraZoomMin;
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.cameraZoomMax;
					return;
				}
				if (opcode == ClientScriptOpCode.VIEWPORT_GETFOV) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.viewportFovMin;
					ScriptRunner.intStack[CS2State.intStackPtr++] = Camera.viewportFovMax;
					return;
				}
			} else if (opcode < 6400) {
				if (opcode == ClientScriptOpCode.DATE_MINUTES) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = (int) (Utils.currentTimeMillis() / TimeUtils.MILLISECONDS_PER_MINUTE);
					return;
				}
				if (opcode == ClientScriptOpCode.DATE_RUNEDAY) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = (int) (Utils.currentTimeMillis() / TimeUtils.MILLISECONDS_PER_DAY) - TimeUtils.RUNEDAYS_SINCE_UNIX_EPOCH;
					return;
				}
				if (opcode == ClientScriptOpCode.DATE_RUNEDAY_FROMDATE) {
					CS2State.intStackPtr -= 3;
					int day = ScriptRunner.intStack[CS2State.intStackPtr];
					int month = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					int year = ScriptRunner.intStack[CS2State.intStackPtr + 2];
					CS2State.LOCAL_CALENDAR.clear();
					CS2State.LOCAL_CALENDAR.set(11, 12);
					CS2State.LOCAL_CALENDAR.set(year, month, day);
					int runeDay = (int) (CS2State.LOCAL_CALENDAR.getTime().getTime() / 86400000L) - 11745;
					if (year < 1970) {
						runeDay--;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = runeDay;
					return;
				}
				if (opcode == ClientScriptOpCode.DATE_YEAR) {
					CS2State.LOCAL_CALENDAR.clear();
					CS2State.LOCAL_CALENDAR.setTime(new Date(Utils.currentTimeMillis()));
					ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.LOCAL_CALENDAR.get(1);
					return;
				}
				if (opcode == ClientScriptOpCode.DATE_ISLEAPYEAR) {
					int year = ScriptRunner.intStack[--CS2State.intStackPtr];
					boolean isLeapYear = true;
					if (year < 0) {
						isLeapYear = (year + 1) % 4 == 0;
					} else if (year < 1582) {
						isLeapYear = year % 4 == 0;
					} else if (year % 4 != 0) {
						isLeapYear = false;
					} else if (year % 100 != 0) {
						isLeapYear = true;
					} else if (year % 400 != 0) {
						isLeapYear = false;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = isLeapYear ? 1 : 0;
					return;
				}
			} else if (opcode < 6500) {
				if (opcode == 6405) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = BrowserJavaScript.showVideoAd() ? 1 : 0;
					return;
				}
				if (opcode == 6406) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = BrowserJavaScript.isNotShowingVideoAd() ? 1 : 0;
					return;
				}
			} else if (opcode < 6600) {
				if (opcode == ClientScriptOpCode.WORLDLIST_FETCH) {
					if (ClientState.mainLogicStep == MainLogicStep.STEP_LOBBY_SCREEN && LoginManager.loginStep == LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						if (WorldConnection.requestingWorldList) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
							return;
						}
						if (WorldConnection.worldListReceivedTime > Utils.currentTimeMillis() - 1000L) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
							return;
						}
						WorldConnection.requestingWorldList = true;
						packet = TcpPacket.createPacket(ClientProt.REQUEST_WORLD_LIST, TcpPacket.isaac);
						packet.buffer.writeInt(WorldDescriptor.port);
						BufferedConnectionContext.queuePacket(packet);
						ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
					return;
				}
				@Pc(6862) Country country;
				@Pc(6829) WorldDescriptor gameWorld;
				if (opcode == ClientScriptOpCode.WORLDLIST_START) {
					gameWorld = WorldDescriptor.getFirstWorldDescriptor();
					if (gameWorld != null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.worldNumber;
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.flags;
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld.address;
						country = gameWorld.getCountry();
						ScriptRunner.intStack[CS2State.intStackPtr++] = country.flag;
						CS2State.stringStack[CS2State.stringStackPtr++] = country.name;
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.population;
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.ping;
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld.activity;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_NEXT) {
					gameWorld = WorldConnection.getNextWorldDescriptor();
					if (gameWorld != null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.worldNumber;
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.flags;
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld.address;
						country = gameWorld.getCountry();
						ScriptRunner.intStack[CS2State.intStackPtr++] = country.flag;
						CS2State.stringStack[CS2State.stringStackPtr++] = country.name;
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.population;
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld.ping;
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld.activity;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_SWITCH) {
					int worldId = ScriptRunner.intStack[--CS2State.intStackPtr];
					string3 = CS2State.stringStack[--CS2State.stringStackPtr];
					if (ClientState.mainLogicStep == MainLogicStep.STEP_LOBBY_SCREEN && LoginManager.loginStep == LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GameHostSelection.setGameHost(string3, worldId) ? 1 : 0;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_SPECIFIC) {
					int worldId = ScriptRunner.intStack[--CS2State.intStackPtr];
					@Pc(7189) WorldDescriptor gameWorld2 = WorldMeta.getWorldDescriptor(worldId);
					if (gameWorld2 != null) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld2.flags;
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld2.address;
						@Pc(7213) Country country2 = gameWorld2.getCountry();
						ScriptRunner.intStack[CS2State.intStackPtr++] = country2.flag;
						CS2State.stringStack[CS2State.stringStackPtr++] = country2.name;
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld2.population;
						ScriptRunner.intStack[CS2State.intStackPtr++] = gameWorld2.ping;
						CS2State.stringStack[CS2State.stringStackPtr++] = gameWorld2.activity;
						return;
					}
					ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
					CS2State.stringStack[CS2State.stringStackPtr++] = "";
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_SORT) {
					CS2State.intStackPtr -= 4;
					int sortMode = ScriptRunner.intStack[CS2State.intStackPtr];
					boolean ascending = ScriptRunner.intStack[CS2State.intStackPtr + 1] == 1;
					int secondarySortMode = ScriptRunner.intStack[CS2State.intStackPtr + 2];
					boolean secondaryAscending = ScriptRunner.intStack[CS2State.intStackPtr + 3] == 1;
					WorldListSorter.sortWorldList(secondaryAscending, secondarySortMode, sortMode, ascending);
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_AUTOWORLD) {
					HostPreparation.prepareHost();
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_PINGWORLDS) {
					if (ClientState.mainLogicStep != MainLogicStep.STEP_LOBBY_SCREEN) {
						return;
					}
					MiniMenu.debugOps = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
					return;
				}
				if (opcode == ClientScriptOpCode.WORLDLIST_SPECIFIC_THISWORLD) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.worldFlags;
					return;
				}
			} else if (opcode >= 6700) {
				if (opcode < 6800 && client.buildEnvironment == ModeWhat.WIP) {
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETOPENIFCOUNT) {
						int openCount = InterfaceManager.OPEN_INTERFACES.size();
						if (ClientState.BASE_WINDOW_ID != -1) {
							openCount++;
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = openCount;
						return;
					}
					if (opcode == ClientScriptOpCode.IF_GETTOP) {
						int ifIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (ClientState.BASE_WINDOW_ID != -1) {
							if (ifIndex == 0) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = ClientState.BASE_WINDOW_ID;
								return;
							}
							ifIndex--;
						}
						@Pc(7461) SubInterface subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.first();
						while (ifIndex-- > 0) {
							subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.next();
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = subInterface.interfaceId;
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETNAME) {
						int interfaceId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (Interface.interfaces[interfaceId] == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = "";
							return;
						}
						string3 = Interface.interfaces[interfaceId][0].name;
						if (string3 == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = "";
							return;
						}
						CS2State.stringStack[CS2State.stringStackPtr++] = string3.substring(0, string3.indexOf(58));
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETCOMCOUNT) {
						int interfaceId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (Interface.interfaces[interfaceId] == null) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
							return;
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = Interface.interfaces[interfaceId].length;
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETCOMNAME) {
						CS2State.intStackPtr -= 2;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						if (Interface.interfaces[interfaceId] == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = "";
							return;
						}
						string2 = Interface.interfaces[interfaceId][componentId].name;
						if (string2 == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = "";
							return;
						}
						CS2State.stringStack[CS2State.stringStackPtr++] = string2;
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_GETSERVERTRIGGERS) {
						CS2State.intStackPtr -= 2;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						if (Interface.interfaces[interfaceId] == null) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
							return;
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = Interface.interfaces[interfaceId][componentId].serverTriggers;
						return;
					}
					if (opcode == 6706) {
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON1) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 1, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON2) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 2, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON3) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 3, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON4) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 4, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON5) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 5, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON6) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 6, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON7) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 7, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON8) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 8, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON9) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 9, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_BUTTON10) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						InterfaceOptionInteract.ifOptionInteract(slotId, "", 10, interfaceId << 16 | componentId);
						return;
					}
					if (opcode == ClientScriptOpCode.IF_DEBUG_TARGET) {
						CS2State.intStackPtr -= 3;
						int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr];
						int componentId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						int slotId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						@Pc(8049) Component component = Component.getComponentWithSlot(slotId, interfaceId << 16 | componentId);
						Static657.cancelInterfaceSelection();
						@Pc(8054) IFEvents serverActiveProperties = InterfaceManager.getComponentSettings(component);
						InterfaceSelectionStart.startInterfaceSelection(serverActiveProperties.interfaceId, serverActiveProperties.getUseOptionFlags(), component);
						return;
					}
				} else if (opcode < 6900) {
					@Pc(8082) MECType mapElementType;
					if (opcode == ClientScriptOpCode.MEC_TEXT) {
						int mecId = ScriptRunner.intStack[--CS2State.intStackPtr];
						mapElementType = TypeLists.MEC.list(mecId);
						if (mapElementType.areaName == null) {
							CS2State.stringStack[CS2State.stringStackPtr++] = "";
							return;
						}
						CS2State.stringStack[CS2State.stringStackPtr++] = mapElementType.areaName;
						return;
					}
					if (opcode == ClientScriptOpCode.MEC_SPRITE) {
						int mecId = ScriptRunner.intStack[--CS2State.intStackPtr];
						mapElementType = TypeLists.MEC.list(mecId);
						ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementType.defaultIconArchive;
						return;
					}
					if (opcode == ClientScriptOpCode.MEC_TEXTSIZE) {
						int mecId = ScriptRunner.intStack[--CS2State.intStackPtr];
						mapElementType = TypeLists.MEC.list(mecId);
						ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementType.baseTextzoom;
						return;
					}
					if (opcode == ClientScriptOpCode.MEC_CATEGORY) {
						int mecId = ScriptRunner.intStack[--CS2State.intStackPtr];
						mapElementType = TypeLists.MEC.list(mecId);
						ScriptRunner.intStack[CS2State.intStackPtr++] = mapElementType.categoryId;
						return;
					}
					if (opcode == ClientScriptOpCode.MEC_PARAM) {
						CS2State.intStackPtr -= 2;
						int mecId = ScriptRunner.intStack[CS2State.intStackPtr];
						int paramId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						@Pc(8204) ParamType paramType = TypeLists.PARAMS.list(paramId);
						if (paramType.isString()) {
							CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.MEC.list(mecId).getStringParam(paramType.typeName, paramId);
							return;
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.MEC.list(mecId).getIntParam(paramType.defaultInt, paramId);
						return;
					}
				} else if (opcode < 7000) {
					if (opcode == ClientScriptOpCode.USERDETAIL_QUICKCHAT) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_MEMBERSHIP) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = client.lobbyMembership;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_RECOVERYDAY) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.recoveryQuestionsSetDay;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_UNREADMESSAGES) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.unreadMessages;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_LASTLOGINDAY) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lastLoginDay;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_LASTLOGINADDRESS) {
						string = "";
						if (LoginManager.lastLoginHostLookup != null) {
							if (LoginManager.lastLoginHostLookup.result == null) {
								string = Utils.ipToString(LoginManager.lastLoginHostLookup.intData1);
							} else {
								string = (String) LoginManager.lastLoginHostLookup.result;
							}
						}
						CS2State.stringStack[CS2State.stringStackPtr++] = string;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_EMAILSTATUS) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = EmailStatus.emailStatus;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_CCEXPIRY) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lobbyCreditCardExpiry;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_GRACEEXPIRY) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lobbyGraceExpiry;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_DOBREQUESTED) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.isDobRequested ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.UDETAIL_DOB) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.playerDob;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_MEMBERSSTATS) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LobbyManager.lobbyMembershipStatus;
						return;
					}
					if (opcode == ClientScriptOpCode.USERDETAIL_LOBBY_PLAYAGE) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.lobbyPlayAge;
						return;
					}
				} else if (opcode < 7100) {
					if (opcode == ClientScriptOpCode.AUTOSETUP_DOSETUP) {
						int graphicsLevel = GraphicsAutoDetect.calculateGraphics();
						ScriptRunner.intStack[CS2State.intStackPtr++] = ToolkitOption.autoSetupToolkit = GamePreferences.uid.toolkit.getValue();
						ScriptRunner.intStack[CS2State.intStackPtr++] = graphicsLevel;
						MapRebuild.rebuildMap();
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
						return;
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETHIGH) {
						HighDetailPreset.loadHighDetailPreferences();
						MapRebuild.rebuildMap();
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
						return;
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETMEDIUM) {
						MediumDetailPreset.loadMediumDetailPreferences();
						MapRebuild.rebuildMap();
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
						return;
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETLOW) {
						LowDetailPreset.loadLowDetailPreferences();
						MapRebuild.rebuildMap();
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
						return;
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETMIN) {
						MinDetailPreset.loadMinDetailPreferences();
						MapRebuild.rebuildMap();
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
						return;
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_SETCUSTOM) {
						GamePreferences.uid.setValue(GamePreferences.uid.graphics, 0);
						GamePreferences.savePreferences();
						GamePreferences.preferencesSavedOnServer = false;
						return;
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_BLACKFLAGLAST) {
						if (ToolkitOption.autoSetupToolkit == 2) {
							GameShell.softwareMode = true;
							return;
						}
						if (ToolkitOption.autoSetupToolkit == 1) {
							GameShell.openglMode = true;
							return;
						}
						if (ToolkitOption.autoSetupToolkit == 3) {
							GameShell.directxMode = true;
						}
						return;
					}
					if (opcode == ClientScriptOpCode.AUTOSETUP_GETLEVEL) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.graphics.getValue();
						return;
					}
				} else if (opcode < 7200) {
					if (opcode == ClientScriptOpCode.VIDEO_ADVERT_PLAY) {
						CS2State.intStackPtr -= 2;
						int fileId = ScriptRunner.intStack[CS2State.intStackPtr];
						int volume = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						if (fileId != -1) {
							if (volume > 255) {
								volume = 255;
							} else if (volume < 0) {
								volume = 0;
							}
							VorbisStreamPreload.preloadVorbisSound(volume, false, fileId);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.VIDEO_ADVERT_FORCE_REMOVE) {
						int streamId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (streamId != -1) {
							VorbisStreamStop.stopVorbisStream(streamId);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.VIDEO_ADVERT_ALLOW_SKIP) {
						int streamId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (streamId != -1) {
							VorbisNode.toggleVorbisPause(streamId);
						}
						return;
					}
					if (opcode == 7103) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = NativeLibraryOwner.isLibraryLoaded("jagtheora") ? 1 : 0;
						return;
					}
				} else if (opcode < 7300) {
					if (opcode == ClientScriptOpCode.DETAILCANMOD_GROUNDDECOR) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.groundDecor.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_SPOTSHADOWS) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.characterShadows.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_HARDSHADOWS) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.hardShadows.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_WATERDETAIL) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.water.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_ANTIALIASING) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.antiAliasing.canMod() && ClientState.activeToolkit.supportsAntiAliasing() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_PARTICLES) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.particles.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_BUILDAREA) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.buildArea.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_BLOOM) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.bloom.canMod() && ClientState.activeToolkit.supportsBloom() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_GROUNDBLENDING) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.groundBlending.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_TEXTURING) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.textures.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_MAXSCREENSIZE) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.maxScreenSize.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_FOG) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.fog.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_ORTHOGRAPHIC) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.orthographic.canMod() ? 1 : 0;
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANMOD_TOOLKIT_DEFAULT) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.toolkitDefault.canMod() ? 1 : 0;
						return;
					}
				} else if (opcode < 7400) {
					if (opcode == ClientScriptOpCode.DETAILCANSET_GROUNDDECOR) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.groundDecor.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_SPOTSHADOWS) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.characterShadows.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_HARDSHADOWS) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.hardShadows.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_WATERDETAIL) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.water.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_ANTIALIASING) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (!ClientState.activeToolkit.supportsAntiAliasing()) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 3;
							return;
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.antiAliasing.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_PARTICLES) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.particles.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_BUILDAREA) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.buildArea.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_BLOOM) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (!ClientState.activeToolkit.supportsBloom()) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = 3;
							return;
						}
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.bloom.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_GROUNDBLENDING) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.groundBlending.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_TEXTURING) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.textures.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_MAXSCREENSIZE) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.maxScreenSize.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_FOG) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.fog.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_ORTHOGRAPHIC) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.orthographic.canSet(preferenceValue);
						return;
					}
					if (opcode == ClientScriptOpCode.DETAILCANSET_TOOLKIT_DEFAULT) {
						int preferenceValue = ScriptRunner.intStack[--CS2State.intStackPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = GamePreferences.uid.toolkitDefault.canSet(preferenceValue);
						return;
					}
				}
			}
		}
		throw new IllegalStateException(String.valueOf(opcode));
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Lclient!rk;)V")
	public static void executeHookInner(@OriginalArg(0) HookRequest hookRequest) {
		executeHookInternal(hookRequest, 200000);
	}

	@OriginalMember(owner = "client!kca", name = "b", descriptor = "(IZ)V")
	public static void executeOpBelow5000(@OriginalArg(0) int opcode, @OriginalArg(1) boolean hasIntOperand) {
		@Pc(137) Component component;
		@Pc(25) int local25;
		@Pc(56) int slotChildIndex;
		@Pc(19) int keyTriggerIndex;
		@Pc(35) Component component2;
		@Pc(13) int local13;
		@Pc(210) Component component3;
		@Pc(158) Component component4;
		if (opcode < 300) {
			if (opcode == 100) {
				CS2State.intStackPtr -= 3;
				int componentHash = ScriptRunner.intStack[CS2State.intStackPtr];
				int componentTypeId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				int childSlot = ScriptRunner.intStack[CS2State.intStackPtr + 2];
				if (componentTypeId == 0) {
					throw new RuntimeException();
				}
				component2 = Interface.getComponent(componentHash);
				if (component2.slotChildren == null) {
					component2.slotChildren = new Component[childSlot + 1];
				}
				if (component2.slotChildren.length <= childSlot) {
					@Pc(54) Component[] components = new Component[childSlot + 1];
					for (slotChildIndex = 0; slotChildIndex < component2.slotChildren.length; slotChildIndex++) {
						components[slotChildIndex] = component2.slotChildren[slotChildIndex];
					}
					component2.slotChildren = components;
				}
				if (childSlot > 0 && component2.slotChildren[childSlot - 1] == null) {
					throw new RuntimeException("Gap at:" + (childSlot - 1));
				}
				@Pc(99) Component component5 = new Component();
				component5.type = ComponentType.fromId(componentTypeId);
				component5.parentId = component5.idHash = component2.idHash;
				component5.slotId = childSlot;
				component2.slotChildren[childSlot] = component5;
				if (hasIntOperand) {
					CS2State.activeComponent2 = component5;
				} else {
					CS2State.activeComponent = component5;
				}
				Component.redrawComponent(component2);
				return;
			}
			if (opcode == 101) {
				component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
				if (component.slotId == -1) {
					if (hasIntOperand) {
						throw new RuntimeException("Tried to .cc_delete static .active-component!");
					}
					throw new RuntimeException("Tried to cc_delete static active-component!");
				}
				component4 = Interface.getComponent(component.idHash);
				component4.slotChildren[component.slotId] = null;
				Component.redrawComponent(component4);
				return;
			}
			if (opcode == 102) {
				component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
				component.slotChildren = null;
				Component.redrawComponent(component);
				return;
			}
			if (opcode == ClientScriptOpCode.CC_FIND) {
				CS2State.intStackPtr -= 2;
				int componentHash = ScriptRunner.intStack[CS2State.intStackPtr];
				int childSlot = ScriptRunner.intStack[CS2State.intStackPtr + 1];
				component3 = Component.getComponentWithSlot(childSlot, componentHash);
				if (component3 != null && childSlot != -1) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
					if (hasIntOperand) {
						CS2State.activeComponent2 = component3;
						return;
					}
					CS2State.activeComponent = component3;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
				return;
			}
			if (opcode == ClientScriptOpCode.IF_FIND) {
				int componentHash = ScriptRunner.intStack[--CS2State.intStackPtr];
				component4 = Interface.getComponent(componentHash);
				if (component4 != null) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
					if (hasIntOperand) {
						CS2State.activeComponent2 = component4;
						return;
					}
					CS2State.activeComponent = component4;
					return;
				}
				ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
				return;
			}
			if (opcode == ClientScriptOpCode.IF_SENDTOFRONT) {
				int componentHash = ScriptRunner.intStack[--CS2State.intStackPtr];
				CS2State.sendToFront(componentHash);
				return;
			}
			if (opcode == ClientScriptOpCode.IF_SENDTOBACK) {
				int componentHash = ScriptRunner.intStack[--CS2State.intStackPtr];
				CS2State.sendToBack(componentHash);
				return;
			}
		} else {
			@Pc(357) int local357;
			@Pc(420) boolean local420;
			if (opcode < 500) {
				if (opcode == ClientScriptOpCode.BASEIDKIT) {
					CS2State.intStackPtr -= 2;
					int idkitSlot = ScriptRunner.intStack[CS2State.intStackPtr];
					int idkId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					if (PlayerUpdate.localPlayer.playerModel == null) {
						return;
					}
					for (int maleSlotIndex = 0; maleSlotIndex < CS2Interpreter.MALE_IDKIT_SLOTS.length; maleSlotIndex++) {
						if (CS2Interpreter.MALE_IDKIT_SLOTS[maleSlotIndex] == idkitSlot) {
							PlayerUpdate.localPlayer.playerModel.addAppearanceIdentity(TypeLists.IDK, idkId, maleSlotIndex);
							return;
						}
					}
					for (int femaleSlotIndex = 0; femaleSlotIndex < CS2Interpreter.FEMALE_IDKIT_SLOTS.length; femaleSlotIndex++) {
						if (CS2Interpreter.FEMALE_IDKIT_SLOTS[femaleSlotIndex] == idkitSlot) {
							PlayerUpdate.localPlayer.playerModel.addAppearanceIdentity(TypeLists.IDK, idkId, femaleSlotIndex);
							return;
						}
					}
					return;
				}
				if (opcode == ClientScriptOpCode.BASECOLOUR) {
					CS2State.intStackPtr -= 2;
					int recolorSlot = ScriptRunner.intStack[CS2State.intStackPtr];
					int colorIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					if (PlayerUpdate.localPlayer.playerModel == null) {
						return;
					}
					PlayerUpdate.localPlayer.playerModel.recolor(recolorSlot, colorIndex);
					return;
				}
				if (opcode == ClientScriptOpCode.SETGENDER) {
					boolean isFemale = ScriptRunner.intStack[--CS2State.intStackPtr] != 0;
					if (PlayerUpdate.localPlayer.playerModel == null) {
						return;
					}
					PlayerUpdate.localPlayer.playerModel.changeGender(isFemale);
					return;
				}
				if (opcode == ClientScriptOpCode.SETOBJ) {
					CS2State.intStackPtr -= 2;
					int wornSlot = ScriptRunner.intStack[CS2State.intStackPtr];
					int objId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					if (PlayerUpdate.localPlayer.playerModel == null) {
						return;
					}
					PlayerUpdate.localPlayer.playerModel.addAppearanceItem(wornSlot, objId, TypeLists.OBJ);
					return;
				}
			} else if ((opcode < 1000 || opcode >= 1100) && (opcode < 2000 || opcode >= 2100)) {
				@Pc(1523) String string;
				@Pc(1161) String string2;
				if (opcode >= 1100 && opcode < 1200 || opcode >= 2100 && opcode < 2200) {
					if (opcode >= 2000) {
						opcode -= 1000;
						component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
					} else {
						component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETSCROLLPOS) {
						CS2State.intStackPtr -= 2;
						component.scrollX = ScriptRunner.intStack[CS2State.intStackPtr];
						if (component.scrollX > component.scrollWidth - component.width) {
							component.scrollX = component.scrollWidth - component.width;
						}
						if (component.scrollX < 0) {
							component.scrollX = 0;
						}
						component.scrollY = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						if (component.scrollY > component.scrollHeight - component.height) {
							component.scrollY = component.scrollHeight - component.height;
						}
						if (component.scrollY < 0) {
							component.scrollY = 0;
						}
						Component.redrawComponent(component);
						if (component.slotId == -1) {
							PulseEvent.delayedIfScrollPosUpdate(component.idHash);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETCOLOUR) {
						component.color = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						if (component.slotId == -1) {
							PulseEvent.delayedIfColourUpdate(component.idHash);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETFILL) {
						component.filled = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTTRANS) {
						component.transparency = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETLINEWID) {
						component.lineWidth = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETGRAPHIC) {
						int spriteId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (component.spriteId != spriteId) {
							component.spriteId = spriteId;
							Component.redrawComponent(component);
						}
						if (component.slotId == -1) {
							PulseEvent.delayedIfGraphicUpdate(component.idHash);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SET2DANGLE) {
						component.angle2d = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTILING) {
						component.tiling = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODEL) {
						component.mediaType = MediaType.RAW_MODEL;
						component.primaryMediaId = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						if (component.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component.idHash);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELANGLE) {
						CS2State.intStackPtr -= 6;
						component.offsetX = ScriptRunner.intStack[CS2State.intStackPtr];
						component.offsetY = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						component.spritePitch = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						component.spriteRoll = ScriptRunner.intStack[CS2State.intStackPtr + 3];
						component.spriteYaw = ScriptRunner.intStack[CS2State.intStackPtr + 4];
						component.spriteScale = ScriptRunner.intStack[CS2State.intStackPtr + 5];
						Component.redrawComponent(component);
						if (component.slotId == -1) {
							PulseEvent.delayedIfAngleUpdate(component.idHash);
							PulseEvent.delayedIfOffsetUpdate(component.idHash);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELANIM) {
						int animationId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (component.animation != animationId) {
							component.animation = animationId;
							component.currentFrameId = 0;
							component.nextFrameId = 1;
							component.currentFrameDuration = 0;
							@Pc(1110) SeqType seqType = component.animation == -1 ? null : TypeLists.SEQ.list(component.animation);
							if (seqType != null) {
								SoundManager.playInterfaceSequenceSound(component.currentFrameId, seqType);
							}
							Component.redrawComponent(component);
						}
						if (component.slotId == -1) {
							PulseEvent.delayedIfAnimUpdate(component.idHash);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELORTHOG) {
						component.usesOrthogonal = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTEXT) {
						string2 = CS2State.stringStack[--CS2State.stringStackPtr];
						if (!string2.equals(component.text)) {
							component.text = string2;
							Component.redrawComponent(component);
						}
						if (component.slotId == -1) {
							DelayedIfTextUpdate.delayedIfTextUpdate(component.idHash);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTEXTFONT) {
						component.fontId = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						if (component.slotId == -1) {
							PulseEvent.delayedIfFontUpdate(component.idHash);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTEXTALIGN) {
						CS2State.intStackPtr -= 3;
						component.textHorizontalAli = ScriptRunner.intStack[CS2State.intStackPtr];
						component.textVerticalAli = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						component.lineSpacing = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETTEXTSHADOW) {
						component.shadow = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETOUTLINE) {
						component.borderThickness = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETGRAPHICSHADOW) {
						component.spriteShadow = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETHFLIP) {
						component.flipVertical = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETVFLIP) {
						component.flipHorizontal = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETSCROLLSIZE) {
						CS2State.intStackPtr -= 2;
						component.scrollWidth = ScriptRunner.intStack[CS2State.intStackPtr];
						component.scrollHeight = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						Component.redrawComponent(component);
						if (component.type == ComponentType.CONTAINER) {
							Component.layoutChildren(false, component);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETALPHA) {
						component.alpha = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELZOOM) {
						component.spriteScale = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						if (component.slotId == -1) {
							PulseEvent.delayedIfAngleUpdate(component.idHash);
						}
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETLINEDIRECTION) {
						int lineDirection = ScriptRunner.intStack[--CS2State.intStackPtr];
						component.lineDirection = lineDirection == 1;
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMODELORIGIN) {
						CS2State.intStackPtr -= 2;
						component.originX = ScriptRunner.intStack[CS2State.intStackPtr];
						component.originY = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						Component.redrawComponent(component);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETMAXLINES) {
						component.maxTextLines = ScriptRunner.intStack[--CS2State.intStackPtr];
						Component.redrawComponent(component);
						return;
					}
					@Pc(1489) ParamType paramType;
					if (opcode == ClientScriptOpCode.CC_IF_SETPARAM_INT) {
						CS2State.intStackPtr -= 2;
						int paramId = ScriptRunner.intStack[CS2State.intStackPtr];
						int paramValue = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						paramType = TypeLists.PARAMS.list(paramId);
						if (paramValue != paramType.defaultInt) {
							component.setIntParam(paramId, paramValue);
							return;
						}
						component.removeParam(paramId);
						return;
					}
					if (opcode == ClientScriptOpCode.CC_IF_SETPARAM_STRING) {
						int paramId = ScriptRunner.intStack[--CS2State.intStackPtr];
						string = CS2State.stringStack[--CS2State.stringStackPtr];
						paramType = TypeLists.PARAMS.list(paramId);
						if (!paramType.typeName.equals(string)) {
							component.setStringParam(paramId, string);
							return;
						}
						component.removeParam(paramId);
						return;
					}
					if (opcode == 1129 || opcode == 1130) {
						int videoId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (component.type != ComponentType.SPRITE && opcode == 1129 || component.type != ComponentType.TEXT && opcode == 1130) {
							return;
						}
						if (component.videoFileId != videoId) {
							component.videoFileId = videoId;
							Component.redrawComponent(component);
						}
						if (component.slotId == -1) {
							PulseEvent.delayedIfVideoUpdate(component.idHash);
						}
						return;
					}
				} else if ((opcode < 1200 || opcode >= 1300) && (opcode < 2200 || opcode >= 2300)) {
					@Pc(2373) int local2373;
					if (opcode >= 1300 && opcode < 1400 || opcode >= 2300 && opcode < 2400) {
						if (opcode >= 2000) {
							opcode -= 1000;
							component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
						} else {
							component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETOP) {
							int opIndex = ScriptRunner.intStack[--CS2State.intStackPtr] - 1;
							if (opIndex >= 0 && opIndex <= 9) {
								component.addOptionName(opIndex, CS2State.stringStack[--CS2State.stringStackPtr]);
								return;
							}
							CS2State.stringStackPtr--;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETDRAGGABLE) {
							CS2State.intStackPtr -= 2;
							int dragTargetHash = ScriptRunner.intStack[CS2State.intStackPtr];
							int dragTargetSlot = ScriptRunner.intStack[CS2State.intStackPtr + 1];
							if (dragTargetHash == -1 && dragTargetSlot == -1) {
								component.depthChildOverride = null;
								return;
							}
							component.depthChildOverride = Component.getComponentWithSlot(dragTargetSlot, dragTargetHash);
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETDRAGRENDERBEHAVIOUR) {
							int dragRenderType = ScriptRunner.intStack[--CS2State.intStackPtr];
							if (dragRenderType != Component.DRAG_RENDER_TRANSLUCENT && dragRenderType != InterfaceDrag.DRAG_RENDER_IN_LAYER && dragRenderType != InterfaceDrag.DRAG_RENDER_OPAQUE) {
								return;
							}
							component.dragType = dragRenderType;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETDRAGDEADZONE) {
							component.dragDeadzone = ScriptRunner.intStack[--CS2State.intStackPtr];
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETDRAGDEADTIME) {
							component.dragDeadTime = ScriptRunner.intStack[--CS2State.intStackPtr];
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETOPBASE) {
							component.opBase = CS2State.stringStack[--CS2State.stringStackPtr];
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETTARGETVERB) {
							component.targetVerb = CS2State.stringStack[--CS2State.stringStackPtr];
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_CLEAROPS) {
							component.options = null;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETTARGETCURSORS) {
							component.targetLeaveCursor = ScriptRunner.intStack[--CS2State.intStackPtr];
							component.targetOverCursor = ScriptRunner.intStack[--CS2State.intStackPtr];
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETOPCURSOR) {
							int cursorId = ScriptRunner.intStack[--CS2State.intStackPtr];
							int opNumber = ScriptRunner.intStack[--CS2State.intStackPtr];
							if (opNumber >= 1 && opNumber <= 10) {
								component.addCursor(opNumber - 1, cursorId);
							}
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETPAUSETEXT) {
							component.resumeText = CS2State.stringStack[--CS2State.stringStackPtr];
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETTARGETOPCURSOR) {
							component.targetOpCursor = ScriptRunner.intStack[--CS2State.intStackPtr];
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETOPCHAR || opcode == ClientScriptOpCode.CC_IF_SETOPKEY) {
							int keyTriggerAction;
							int keyTriggerParam;
							if (opcode == ClientScriptOpCode.CC_IF_SETOPCHAR) {
								CS2State.intStackPtr -= 3;
								keyTriggerIndex = ScriptRunner.intStack[CS2State.intStackPtr] - 1;
								keyTriggerAction = ScriptRunner.intStack[CS2State.intStackPtr + 1];
								keyTriggerParam = ScriptRunner.intStack[CS2State.intStackPtr + 2];
								if (keyTriggerIndex < 0 || keyTriggerIndex > 9) {
									throw new RuntimeException("IOR13121313");
								}
							} else {
								CS2State.intStackPtr -= 2;
								keyTriggerIndex = 10;
								keyTriggerAction = ScriptRunner.intStack[CS2State.intStackPtr];
								keyTriggerParam = ScriptRunner.intStack[CS2State.intStackPtr + 1];
							}
							if (component.keyTriggeringAction == null) {
								if (keyTriggerAction == 0) {
									return;
								}
								component.keyTriggeringAction = new byte[11];
								component.keyTriggerParams = new byte[11];
								component.cycleSteps = new int[11];
							}
							component.keyTriggeringAction[keyTriggerIndex] = (byte) keyTriggerAction;
							if (keyTriggerAction == 0) {
								component.hasInteraction = false;
								for (int keyIndex = 0; keyIndex < component.keyTriggeringAction.length; keyIndex++) {
									if (component.keyTriggeringAction[keyIndex] != 0) {
										component.hasInteraction = true;
										break;
									}
								}
							} else {
								component.hasInteraction = true;
							}
							component.keyTriggerParams[keyTriggerIndex] = (byte) keyTriggerParam;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETMOUSEOVERCURSOR) {
							component.moveOverCursor = ScriptRunner.intStack[--CS2State.intStackPtr];
							return;
						}
					} else if (opcode >= 1400 && opcode < 1500 || opcode >= 2400 && opcode < 2500) {
						if (opcode >= 2000) {
							opcode -= 1000;
							component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
						} else {
							component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
						}
						if (opcode == ClientScriptOpCode.CC_IF_CLEARSCRIPTHOOKS) {
							component.clearScriptHooks();
							return;
						}
						string2 = CS2State.stringStack[--CS2State.stringStackPtr];
						@Pc(2459) int[] triggers = null;
						if (string2.length() > 0 && string2.charAt(string2.length() - 1) == 'Y') {
							int triggerCount = ScriptRunner.intStack[--CS2State.intStackPtr];
							if (triggerCount > 0) {
								triggers = new int[triggerCount];
								while (triggerCount-- > 0) {
									triggers[triggerCount] = ScriptRunner.intStack[--CS2State.intStackPtr];
								}
							}
							string2 = string2.substring(0, string2.length() - 1);
						}
						@Pc(2511) Object[] objects = new Object[string2.length() + 1];
						for (int argIndex = objects.length - 1; argIndex >= 1; argIndex--) {
							if (string2.charAt(argIndex - 1) == 's') {
								objects[argIndex] = CS2State.stringStack[--CS2State.stringStackPtr];
							} else {
								objects[argIndex] = Integer.valueOf(ScriptRunner.intStack[--CS2State.intStackPtr]);
							}
						}
						int scriptId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (scriptId == -1) {
							objects = null;
						} else {
							objects[0] = Integer.valueOf(scriptId);
						}
						if (opcode == ClientScriptOpCode.CC_IF_SETONCLICK) {
							component.onClick = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONHOLD) {
							component.onHold = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONRELEASE) {
							component.onRelease = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONMOUSEOVER) {
							component.onMouseOver = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONMOUSELEAVE) {
							component.onMouseLeaveScript = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONDRAG) {
							component.onDrag = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONTARGETLEAVE) {
							component.hookParams = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONVARTRANSMIT) {
							component.onVarpTransmit = objects;
							component.varps = triggers;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONTIMER) {
							component.onTimer = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONOP) {
							component.params = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONDRAGCOMPLETE) {
							component.onDragComplete = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONCLICKREPEAT) {
							component.onClickRepeat = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONMOUSEREPEAT) {
							component.popupScript = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONINVTRANSMIT) {
							component.mouseLeaveScript = objects;
							component.mouseLeaveArrayParams = triggers;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONSTATTRANSMIT) {
							component.onStatTransmit = objects;
							component.statTransmitFilter = triggers;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONTARGETENTER) {
							component.onTargetEnter = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONSCROLLWHEEL) {
							component.onMouseMove = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONCHATTRANSMIT) {
							component.onChatTransmit = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONKEY) {
							component.onKeyType = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONFRIENDTRANSMIT) {
							component.onFriendTransmit = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONCLANTRANSMIT) {
							component.onClanTransmit = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONMISCTRANSMIT) {
							component.onMiscTransmit = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONDIALOGABORT) {
							component.onSubClose = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONSUBCHANGE) {
							component.onSubChange = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONSTOCKTRANSMIT) {
							component.onStockTransmit = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONCAMFINISHED) {
							component.onCamFinished = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONRESIZE) {
							component.onResize = objects;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONVARCTRANSMIT) {
							component.onKey = objects;
							component.keyPressArray = triggers;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONVARCSTRTRANSMIT) {
							component.onScrollWheel = objects;
							component.mouseWheelArray = triggers;
						} else if (opcode == ClientScriptOpCode.CC_IF_SETONOPT) {
							component.onTargetLeave = objects;
						}
						component.hasScript = true;
						return;
					} else if (opcode < 1600) {
						component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
						if (opcode == ClientScriptOpCode.CC_GETX) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component.x;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_GETY) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component.y;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_GETWIDTH) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component.width;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_GETHEIGHT) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component.height;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_GETHIDE) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component.hidden ? 1 : 0;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_GETLAYER) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = component.parentId;
							return;
						}
						if (opcode == ClientScriptOpCode.CC_GETPARENTLAYER) {
							component4 = Component.getParentLayer(component);
							ScriptRunner.intStack[CS2State.intStackPtr++] = component4 == null ? -1 : component4.idHash;
							return;
						}
					} else {
						@Pc(3107) ParamType paramType2;
						if (opcode < 1700) {
							component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
							if (opcode == ClientScriptOpCode.CC_GETSCROLLX) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.scrollX;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETSCROLLY) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.scrollY;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETTEXT) {
								CS2State.stringStack[CS2State.stringStackPtr++] = component.text;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETSCROLLWIDTH) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.scrollWidth;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETSCROLLHEIGHT) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.scrollHeight;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELZOOM) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spriteScale;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELANGLE_X) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spritePitch;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELANGLE_Z) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spriteYaw;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELANGLE_Y) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spriteRoll;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETTRANS) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.transparency;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELXOF) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.offsetX;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETMODELYOF) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.offsetY;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETGRAPHIC) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spriteId;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_PARAM) {
								int paramId = ScriptRunner.intStack[--CS2State.intStackPtr];
								paramType2 = TypeLists.PARAMS.list(paramId);
								if (paramType2.isString()) {
									CS2State.stringStack[CS2State.stringStackPtr++] = component.getStringParam(paramId, paramType2.typeName);
									return;
								}
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.getIntParam(paramType2.defaultInt, paramId);
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GET2DANGLE) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.angle2d;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETMODEL) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.mediaType == MediaType.RAW_MODEL ? component.primaryMediaId : -1;
								return;
							}
						} else if (opcode < 1800) {
							component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
							if (opcode == ClientScriptOpCode.CC_GETINVOBJECT) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.containerItemId;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETINVCOUNT) {
								if (component.containerItemId != -1) {
									ScriptRunner.intStack[CS2State.intStackPtr++] = component.itemStackSize;
									return;
								}
								ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETID) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.slotId;
								return;
							}
						} else if (opcode < 1900) {
							component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
							if (opcode == ClientScriptOpCode.CC_GETTARGETMASK) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = InterfaceManager.getComponentSettings(component).getUseOptionFlags();
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETOP) {
								int opIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
								opIndex--;
								if (component.options != null && opIndex < component.options.length && component.options[opIndex] != null) {
									CS2State.stringStack[CS2State.stringStackPtr++] = component.options[opIndex];
									return;
								}
								CS2State.stringStack[CS2State.stringStackPtr++] = "";
								return;
							}
							if (opcode == ClientScriptOpCode.CC_GETOPBASE) {
								if (component.opBase == null) {
									CS2State.stringStack[CS2State.stringStackPtr++] = "";
									return;
								}
								CS2State.stringStack[CS2State.stringStackPtr++] = component.opBase;
								return;
							}
						} else if (opcode < 2000 || opcode >= 2900 && opcode < 3000) {
							if (opcode >= 2000) {
								component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
								opcode -= 1000;
							} else {
								component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
							}
							if (CS2State.hookRequestCount >= 10) {
								throw new RuntimeException("C29xx-1");
							}
							if (opcode == ClientScriptOpCode.CC_IF_CALLONRESIZE) {
								if (component.onResize == null) {
									return;
								}
								@Pc(3376) HookRequest hookRequest = new HookRequest();
								hookRequest.source = component;
								hookRequest.params = component.onResize;
								hookRequest.id = CS2State.hookRequestCount + 1;
								InterfaceManager.PENDING_HOOK_REQUESTS.addLast(hookRequest);
								return;
							}
						} else if (opcode < 2600) {
							component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
							if (opcode == ClientScriptOpCode.IF_GETX) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.x;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETY) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.y;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETWIDTH) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.width;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETHEIGHT) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.height;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETHIDE) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.hidden ? 1 : 0;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETLAYER) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.parentId;
								return;
							}
							if (opcode == 1506) {
								component4 = Component.getParentLayer(component);
								ScriptRunner.intStack[CS2State.intStackPtr++] = component4 == null ? -1 : component4.idHash;
								return;
							}
						} else if (opcode < 2700) {
							component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
							if (opcode == ClientScriptOpCode.IF_GETSCROLLX) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.scrollX;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETSCROLLY) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.scrollY;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETTEXT) {
								CS2State.stringStack[CS2State.stringStackPtr++] = component.text;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETSCROLLWIDTH) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.scrollWidth;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETSCROLLHEIGHT) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.scrollHeight;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELZOOM) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spriteScale;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELANGLE_X) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spritePitch;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELANGLE_Z) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spriteYaw;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELANGLE_Y) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spriteRoll;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETTRANS) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.transparency;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELXOF) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.offsetX;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETMODELYOF) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.offsetY;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETGRAPHIC) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.spriteId;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GET2DANGLE) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.angle2d;
								return;
							}
							if (opcode == ClientScriptOpCode.IF_GETMODEL) {
								ScriptRunner.intStack[CS2State.intStackPtr++] = component.mediaType == MediaType.RAW_MODEL ? component.primaryMediaId : -1;
								return;
							}
						} else {
							@Pc(3905) SubInterface subInterface;
							@Pc(3803) SubInterface subInterface2;
							if (opcode < 2800) {
								if (opcode == ClientScriptOpCode.IF_GETINVOBJECT) {
									component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
									ScriptRunner.intStack[CS2State.intStackPtr++] = component.containerItemId;
									return;
								}
								if (opcode == ClientScriptOpCode.IF_GETINVCOUNT) {
									component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
									if (component.containerItemId != -1) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = component.itemStackSize;
										return;
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
									return;
								}
								if (opcode == ClientScriptOpCode.IF_HASSUB) {
									int componentHash = ScriptRunner.intStack[--CS2State.intStackPtr];
									subInterface2 = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) componentHash);
									if (subInterface2 != null) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
										return;
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
									return;
								}
								if (opcode == ClientScriptOpCode.IF_GETNEXTSUBID) {
									component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
									if (component.slotChildren == null) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									int nextSubId = component.slotChildren.length;
									for (int childIndex = 0; childIndex < component.slotChildren.length; childIndex++) {
										if (component.slotChildren[childIndex] == null) {
											nextSubId = childIndex;
											break;
										}
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = nextSubId;
									return;
								}
								if (opcode == ClientScriptOpCode.IF_HASSUBMODAL || opcode == ClientScriptOpCode.IF_HASSUBOVERLAY) {
									CS2State.intStackPtr -= 2;
									int componentHash = ScriptRunner.intStack[CS2State.intStackPtr];
									int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
									subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) componentHash);
									if (subInterface != null && subInterface.interfaceId == interfaceId) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
										return;
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
									return;
								}
							} else if (opcode < 2900) {
								component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
								if (opcode == ClientScriptOpCode.IF_GETTARGETMASK) {
									ScriptRunner.intStack[CS2State.intStackPtr++] = InterfaceManager.getComponentSettings(component).getUseOptionFlags();
									return;
								}
								if (opcode == ClientScriptOpCode.IF_GETOP) {
									int opIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
									opIndex--;
									if (component.options != null && opIndex < component.options.length && component.options[opIndex] != null) {
										CS2State.stringStack[CS2State.stringStackPtr++] = component.options[opIndex];
										return;
									}
									CS2State.stringStack[CS2State.stringStackPtr++] = "";
									return;
								}
								if (opcode == ClientScriptOpCode.IF_GETOPBASE) {
									if (component.opBase == null) {
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									CS2State.stringStack[CS2State.stringStackPtr++] = component.opBase;
									return;
								}
							} else {
								@Pc(4042) String string3;
								if (opcode < 3200) {
									if (opcode == ClientScriptOpCode.MES) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										ChatLineWriter.writeUnfilterableMessage(string3);
										return;
									}
									if (opcode == 3101) {
										CS2State.intStackPtr -= 2;
										PlayerEntity.animatePlayer(PlayerUpdate.localPlayer, ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1]);
										return;
									}
									if (opcode == ClientScriptOpCode.IF_CLOSE) {
										ModalInterfaceClose.closeInterface();
										return;
									}
									if (opcode == ClientScriptOpCode.RESUME_COUNTDIALOG) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										int count = 0;
										if (Utils.isValidInteger(string3)) {
											count = Utils.parseInt(string3);
										}
										@Pc(4102) TcpPacket countPacket = TcpPacket.createPacket(ClientProt.RESUME_COUNTDIALOG, TcpPacket.isaac);
										countPacket.buffer.writeInt(count);
										BufferedConnectionContext.queuePacket(countPacket);
										return;
									}
									@Pc(4131) TcpPacket packet;
									if (opcode == ClientScriptOpCode.RESUME_NAMEDIALOG) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										packet = TcpPacket.createPacket(ClientProt.RESUME_P_NAMEDIALOG, TcpPacket.isaac);
										packet.buffer.writeByte(string3.length() + 1);
										packet.buffer.writeString(string3);
										BufferedConnectionContext.queuePacket(packet);
										return;
									}
									if (opcode == ClientScriptOpCode.RESUME_STRINGDIALOG) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										packet = TcpPacket.createPacket(ClientProt.RESUME_P_STRINGDIALOG, TcpPacket.isaac);
										packet.buffer.writeByte(string3.length() + 1);
										packet.buffer.writeString(string3);
										BufferedConnectionContext.queuePacket(packet);
										return;
									}
									if (opcode == ClientScriptOpCode.OPPLAYER) {
										int optionIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										string2 = CS2State.stringStack[--CS2State.stringStackPtr];
										ThreeDView.sendPlayerOperation(optionIndex, string2);
										return;
									}
									if (opcode == ClientScriptOpCode.IF_DRAGPICKUP) {
										CS2State.intStackPtr -= 3;
										int dragOffsetX = ScriptRunner.intStack[CS2State.intStackPtr];
										int offsetY = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										int componentHash = ScriptRunner.intStack[CS2State.intStackPtr + 2];
										component2 = Interface.getComponent(componentHash);
										InterfaceManager.startDragging(component2, dragOffsetX, offsetY);
										return;
									}
									if (opcode == ClientScriptOpCode.CC_DRAGPICKUP) {
										CS2State.intStackPtr -= 2;
										int dragOffsetX = ScriptRunner.intStack[CS2State.intStackPtr];
										int offsetY = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										component3 = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
										InterfaceManager.startDragging(component3, dragOffsetX, offsetY);
										return;
									}
									if (opcode == ClientScriptOpCode.RESUME_OBJDIALOG) {
										int itemId = ScriptRunner.intStack[--CS2State.intStackPtr];
										packet = TcpPacket.createPacket(WorldMap.RESUME_P_OBJDIALOG, TcpPacket.isaac);
										packet.buffer.writeShort(itemId);
										BufferedConnectionContext.queuePacket(packet);
										return;
									}
									if (opcode == ClientScriptOpCode.IF_OPENSUBCLIENT) {
										CS2State.intStackPtr -= 2;
										int componentHash = ScriptRunner.intStack[CS2State.intStackPtr];
										int interfaceId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) componentHash);
										if (subInterface != null) {
											SubInterfaceCloser.closeSubInterface(subInterface.interfaceId != interfaceId, true, subInterface);
										}
										SubInterfaceOpener.openSubInterface(interfaceId, componentHash, SubInterfaceType.OVERLAY_CLIENT, true);
										return;
									}
									if (opcode == ClientScriptOpCode.IF_CLOSESUBCLIENT) {
										CS2State.intStackPtr--;
										int componentHash = ScriptRunner.intStack[CS2State.intStackPtr];
										subInterface2 = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) componentHash);
										if (subInterface2 != null && subInterface2.openType == SubInterfaceType.OVERLAY_CLIENT) {
											SubInterfaceCloser.closeSubInterface(true, true, subInterface2);
										}
										return;
									}
									if (opcode == ClientScriptOpCode.OPPLAYERT) {
										InterfaceOnPlayerAction.sendInterfaceOnPlayer(CS2State.stringStack[--CS2State.stringStackPtr]);
										return;
									}
									if (opcode == ClientScriptOpCode.MES_TYPED) {
										CS2State.intStackPtr -= 2;
										int messageType = ScriptRunner.intStack[CS2State.intStackPtr];
										int effectFlags = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										string = CS2State.stringStack[--CS2State.stringStackPtr];
										ChatLineWriter.writeMessage("", messageType, effectFlags, string, "", "");
										return;
									}
									if (opcode == ClientScriptOpCode.SETUP_MESSAGEBOX) {
										CS2State.intStackPtr -= 11;
										@Pc(4432) LoadingScreenAlignmentX[] loadingScreenAlignmentXs = LoadingScreenAlignmentX.loadingScreenAlignmentXValues();
										@Pc(4435) LoadingScreenAlignmentY[] loadingScreenAlignmentYs = LoadingScreenAlignmentY.loadingScreenAlignmentYValues();
										MessageBox.setupMessageBox(ScriptRunner.intStack[CS2State.intStackPtr + 9], ScriptRunner.intStack[CS2State.intStackPtr + 5], loadingScreenAlignmentXs[ScriptRunner.intStack[CS2State.intStackPtr]], loadingScreenAlignmentYs[ScriptRunner.intStack[CS2State.intStackPtr + 1]], ScriptRunner.intStack[CS2State.intStackPtr + 7], ScriptRunner.intStack[CS2State.intStackPtr + 6], ScriptRunner.intStack[CS2State.intStackPtr + 4], ScriptRunner.intStack[CS2State.intStackPtr + 10], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr + 8]);
										return;
									}
								} else if (opcode < 3300) {
									if (opcode == ClientScriptOpCode.SOUND_SYNTH) {
										CS2State.intStackPtr -= 3;
										AreaSoundPlayer.playSoundSynth(255, ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr + 2], 256);
										return;
									}
									if (opcode == ClientScriptOpCode.SOUND_SONG) {
										SongPlayer.playSong(255, ScriptRunner.intStack[--CS2State.intStackPtr], 50);
										return;
									}
									if (opcode == ClientScriptOpCode.SOUND_JINGLE) {
										CS2State.intStackPtr -= 2;
										PlayMusic.playJingle(ScriptRunner.intStack[CS2State.intStackPtr], 255, ScriptRunner.intStack[CS2State.intStackPtr + 1]);
										return;
									}
									if (opcode == ClientScriptOpCode.SOUND_SYTHN_VOLUME) {
										CS2State.intStackPtr -= 4;
										AreaSoundPlayer.playSoundSynth(ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr + 2], 256);
										return;
									}
									if (opcode == ClientScriptOpCode.SOUND_SONG_VOLUME) {
										CS2State.intStackPtr -= 3;
										SongPlayer.playSong(ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 2]);
										return;
									}
									if (opcode == ClientScriptOpCode.SOUND_JINGLE_VOLUME) {
										CS2State.intStackPtr -= 3;
										PlayMusic.playJingle(ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr + 1]);
										return;
									}
									if (opcode == ClientScriptOpCode.SOUND_VORBIS_VOLUME) {
										CS2State.intStackPtr -= 4;
										SoundManager.playSoundVorbis(ScriptRunner.intStack[CS2State.intStackPtr + 1], 256, false, ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr]);
										return;
									}
									if (opcode == ClientScriptOpCode.SOUND_SPEECH_VOLUME) {
										CS2State.intStackPtr -= 4;
										SoundManager.playSoundVorbis(ScriptRunner.intStack[CS2State.intStackPtr + 1], 256, true, ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr]);
										return;
									}
									if (opcode == ClientScriptOpCode.SOUND_SYNTH_RATE) {
										CS2State.intStackPtr -= 5;
										AreaSoundPlayer.playSoundSynth(ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr], ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr + 4]);
										return;
									}
									if (opcode == ClientScriptOpCode.SOUND_VORBIS_RATE) {
										CS2State.intStackPtr -= 5;
										SoundManager.playSoundVorbis(ScriptRunner.intStack[CS2State.intStackPtr + 1], ScriptRunner.intStack[CS2State.intStackPtr + 4], false, ScriptRunner.intStack[CS2State.intStackPtr + 3], ScriptRunner.intStack[CS2State.intStackPtr + 2], ScriptRunner.intStack[CS2State.intStackPtr]);
										return;
									}
								} else if (opcode < 3400) {
									if (opcode == ClientScriptOpCode.CLIENTCLOCK) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = ClientState.clientCycle;
										return;
									}
									if (opcode == ClientScriptOpCode.INV_GETOBJ) {
										CS2State.intStackPtr -= 2;
										int inventoryId = ScriptRunner.intStack[CS2State.intStackPtr];
										int slot = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getItemIdAtSlot(false, inventoryId, slot);
										return;
									}
									if (opcode == ClientScriptOpCode.INV_GETNUM) {
										CS2State.intStackPtr -= 2;
										int inventoryId = ScriptRunner.intStack[CS2State.intStackPtr];
										int slot = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getItemAmountAtSlot(inventoryId, false, slot);
										return;
									}
									if (opcode == ClientScriptOpCode.INV_TOTAL) {
										CS2State.intStackPtr -= 2;
										int inventoryId = ScriptRunner.intStack[CS2State.intStackPtr];
										int itemId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryTotal(itemId, inventoryId, false);
										return;
									}
									if (opcode == ClientScriptOpCode.INV_SIZE) {
										int inventoryId = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OP_MAPELEMENT2.list(inventoryId).size;
										return;
									}
									if (opcode == ClientScriptOpCode.STAT) {
										int skillId = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = client.statLevels[skillId];
										return;
									}
									if (opcode == ClientScriptOpCode.STAT_BASE) {
										int skillId = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = StatBaseLevels.statBaseLevels[skillId];
										return;
									}
									if (opcode == ClientScriptOpCode.STAT_VISIBLE_XP) {
										int skillId = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = client.statXp[skillId];
										return;
									}
									if (opcode == ClientScriptOpCode.COORD) {
										@Pc(4960) byte level = PlayerUpdate.localPlayer.level;
										int tileX = (PlayerUpdate.localPlayer.x >> 9) + MapRegion.sceneBaseX;
										int tileZ = (PlayerUpdate.localPlayer.z >> 9) + MapRegion.sceneBaseY;
										ScriptRunner.intStack[CS2State.intStackPtr++] = (level << 28) + (tileX << 14) + tileZ;
										return;
									}
									if (opcode == ClientScriptOpCode.COORDX) {
										int coord = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = coord >> 14 & 0x3FFF;
										return;
									}
									if (opcode == ClientScriptOpCode.COORDY) {
										int coord = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = coord >> 28;
										return;
									}
									if (opcode == ClientScriptOpCode.COORDZ) {
										int coord = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = coord & 0x3FFF;
										return;
									}
									if (opcode == ClientScriptOpCode.MAP_MEMBERS) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.memberWorld ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.INVOTHER_GETOBJ) {
										CS2State.intStackPtr -= 2;
										int inventoryId = ScriptRunner.intStack[CS2State.intStackPtr];
										int slot = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getItemIdAtSlot(true, inventoryId, slot);
										return;
									}
									if (opcode == ClientScriptOpCode.INVOTHER_GETNUM) {
										CS2State.intStackPtr -= 2;
										int inventoryId = ScriptRunner.intStack[CS2State.intStackPtr];
										int slot = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getItemAmountAtSlot(inventoryId, true, slot);
										return;
									}
									if (opcode == ClientScriptOpCode.INVOTHER_TOTAL) {
										CS2State.intStackPtr -= 2;
										int inventoryId = ScriptRunner.intStack[CS2State.intStackPtr];
										int itemId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryTotal(itemId, inventoryId, true);
										return;
									}
									if (opcode == ClientScriptOpCode.STAFFMODLEVEL) {
										if (LoginManager.staffModLevel >= 2) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.staffModLevel;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.REBOOTTIMER) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = client.rebootTimer;
										return;
									}
									if (opcode == ClientScriptOpCode.MAP_WORLD) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = TcpPacket.CURRENT_REQUEST.worldId;
										return;
									}
									if (opcode == ClientScriptOpCode.RUNENERGY_VISIBLE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = ClientState.runEnergy;
										return;
									}
									if (opcode == ClientScriptOpCode.RUNWEIGHT_VISIBLE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = client.runWeight;
										return;
									}
									if (opcode == ClientScriptOpCode.PLAYERMOD) {
										if (LoginManager.playerModLevel >= 5 && LoginManager.playerModLevel <= 9) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.PLAYERMODLEVEL) {
										if (LoginManager.playerModLevel >= 5 && LoginManager.playerModLevel <= 9) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.playerModLevel;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.PLAYERMEMBER) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.isMember ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.COMLEVEL_ACTIVE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = PlayerUpdate.localPlayer.combatLevel;
										return;
									}
									if (opcode == ClientScriptOpCode.GENDER) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = PlayerUpdate.localPlayer.playerModel != null && PlayerUpdate.localPlayer.playerModel.female ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.MAP_QUICKCHAT) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.quickchatOnly ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.INV_FREESPACE) {
										int inventoryId = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryFreeSpace(inventoryId);
										return;
									}
									if (opcode == ClientScriptOpCode.INV_TOTALPARAM) {
										CS2State.intStackPtr -= 2;
										int inventoryId = ScriptRunner.intStack[CS2State.intStackPtr];
										int paramId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryParamTotal(false, inventoryId, paramId);
										return;
									}
									if (opcode == ClientScriptOpCode.INV_TOTALPARAM_STACK) {
										CS2State.intStackPtr -= 2;
										int inventoryId = ScriptRunner.intStack[CS2State.intStackPtr];
										int paramId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = ItemContainer.getInventoryParamTotal(true, inventoryId, paramId);
										return;
									}
									if (opcode == 3333) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = LoginManager.anInt6172;
										return;
									}
									if (opcode == ClientScriptOpCode.MAP_LANG) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = ClientState.language;
										return;
									}
									if (opcode == ClientScriptOpCode.MOVE_COORD) {
										CS2State.intStackPtr -= 4;
										int coord = ScriptRunner.intStack[CS2State.intStackPtr];
										int deltaX = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										int deltaLevel = ScriptRunner.intStack[CS2State.intStackPtr + 2];
										int deltaZ = ScriptRunner.intStack[CS2State.intStackPtr + 3];
										coord += deltaX << 14;
										coord += deltaLevel << 28;
										coord += deltaZ;
										ScriptRunner.intStack[CS2State.intStackPtr++] = coord;
										return;
									}
									if (opcode == ClientScriptOpCode.AFFILIATE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.AFFILIATE;
										return;
									}
									if (opcode == ClientScriptOpCode.PROFILE_CPU) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = SoftwareRendererBenchmark.benchmarkSoftwareRenderer();
										return;
									}
									if (opcode == ClientScriptOpCode.PLAYERDEMO) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.APPLET_HASFOCUS) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.appletHasFocus ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.FROMBILLING) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.fromBilling ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.GET_MOUSEX) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = MouseRecord.mouseRecorder.getMouseX();
										return;
									}
									if (opcode == ClientScriptOpCode.GET_MOUSEY) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = MouseRecord.mouseRecorder.getMouseY();
										return;
									}
									if (opcode == ClientScriptOpCode.GET_ACTIVE_MINIMENU_ENTRY) {
										CS2State.stringStack[CS2State.stringStackPtr++] = MiniMenu.getColoredNameText();
										return;
									}
									if (opcode == ClientScriptOpCode.GET_SECOND_MINIMENU_ENTRY) {
										CS2State.stringStack[CS2State.stringStackPtr++] = MiniMenu.getActionText();
										return;
									}
									if (opcode == ClientScriptOpCode.GET_MINIMENU_LENGTH) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = MiniMenu.getClickType();
										return;
									}
									if (opcode == ClientScriptOpCode.GET_CURRENTCURSOR) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = CursorManager.currentCursor;
										return;
									}
									if (opcode == ClientScriptOpCode.GET_SELFYANGLE) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = PlayerUpdate.localPlayer.yaw.getRotation() >> 3;
										return;
									}
									if (opcode == ClientScriptOpCode.GET_MOUSEBUTTONS && ClientFeatures.VOID) {
										// Not part of the original client. Void's scripts read the three mouse buttons through this instruction.
										ScriptRunner.intStack[CS2State.intStackPtr++] = MouseRecord.mouseRecorder.isLeftButtonDown() ? 1 : 0;
										ScriptRunner.intStack[CS2State.intStackPtr++] = MouseRecord.mouseRecorder.isMiddleButtonDown() ? 1 : 0;
										ScriptRunner.intStack[CS2State.intStackPtr++] = MouseRecord.mouseRecorder.isRightButtonDown() ? 1 : 0;
										return;
									}
								} else if (opcode < 3500) {
									@Pc(5711) EnumType enumType;
									if (opcode == ClientScriptOpCode.ENUM_STRING) {
										CS2State.intStackPtr -= 2;
										int enumId = ScriptRunner.intStack[CS2State.intStackPtr];
										int key = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										enumType = TypeLists.ENUM.list(enumId);
										CS2State.stringStack[CS2State.stringStackPtr++] = enumType.getString(key);
										return;
									}
									if (opcode == ClientScriptOpCode.ENUM) {
										CS2State.intStackPtr -= 4;
										int inputType = ScriptRunner.intStack[CS2State.intStackPtr];
										int valueType = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										int enumId = ScriptRunner.intStack[CS2State.intStackPtr + 2];
										int enumKey = ScriptRunner.intStack[CS2State.intStackPtr + 3];
										@Pc(5757) EnumType enumType2 = TypeLists.ENUM.list(enumId);
										if (enumType2.keyType == inputType && enumType2.valueType == valueType) {
											if (valueType == 115) {
												CS2State.stringStack[CS2State.stringStackPtr++] = enumType2.getString(enumKey);
												return;
											}
											ScriptRunner.intStack[CS2State.intStackPtr++] = enumType2.getInt(enumKey);
											return;
										}
										throw new RuntimeException("C3408-1 " + enumId + "-" + enumKey);
									}
									if (opcode == ClientScriptOpCode.ENUM_HASOUTPUT) {
										CS2State.intStackPtr -= 3;
										int outputType = ScriptRunner.intStack[CS2State.intStackPtr];
										int enumId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										int outputValue = ScriptRunner.intStack[CS2State.intStackPtr + 2];
										if (enumId == -1) {
											throw new RuntimeException("C3409-2");
										}
										@Pc(5843) EnumType enumType3 = TypeLists.ENUM.list(enumId);
										if (enumType3.valueType != outputType) {
											throw new RuntimeException("C3409-1");
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = enumType3.hasOutput(outputValue) ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.ENUM_HASOUTPUT_STRING) {
										int enumId = ScriptRunner.intStack[--CS2State.intStackPtr];
										string2 = CS2State.stringStack[--CS2State.stringStackPtr];
										if (enumId == -1) {
											throw new RuntimeException("C3410-2");
										}
										enumType = TypeLists.ENUM.list(enumId);
										if (enumType.valueType != 's') {
											throw new RuntimeException("C3410-1");
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = enumType.hasOutputString(string2) ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.ENUM_GETOUTPUT_COUNT) {
										int enumId = ScriptRunner.intStack[--CS2State.intStackPtr];
										@Pc(5941) EnumType enumType4 = TypeLists.ENUM.list(enumId);
										ScriptRunner.intStack[CS2State.intStackPtr++] = enumType4.parameters.size();
										return;
									}
								} else if (opcode < 3700) {
									if (opcode == ClientScriptOpCode.FRIEND_COUNT) {
										if (FriendList.friendListStatus == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = -2;
											return;
										}
										if (FriendList.friendListStatus == 1) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = FriendList.FRIEND_COUNT;
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETNAME) {
										int friendIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendList.friendListStatus == 2 && friendIndex < FriendList.FRIEND_COUNT) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendList.friendNames[friendIndex];
											if (FriendList.friendPreviousNames[friendIndex] != null) {
												CS2State.stringStack[CS2State.stringStackPtr++] = FriendList.friendPreviousNames[friendIndex];
												return;
											}
											CS2State.stringStack[CS2State.stringStackPtr++] = "";
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETWORLD) {
										int friendIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendList.friendListStatus == 2 && friendIndex < FriendList.FRIEND_COUNT) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendList.friendWorldIds[friendIndex];
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETRANK) {
										int friendIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendList.friendListStatus == 2 && friendIndex < FriendList.FRIEND_COUNT) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendList.friendRanks[friendIndex];
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_SETRANK) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										int rank = ScriptRunner.intStack[--CS2State.intStackPtr];
										FriendList.setFriendRank(rank, string3);
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_ADD) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										FriendList.addFriend(string3);
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_DEL) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										FriendList.removeFriend(string3);
										return;
									}
									if (opcode == ClientScriptOpCode.IGNORE_ADD) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										IgnoreList.add(string3, false);
										return;
									}
									if (opcode == ClientScriptOpCode.IGNORE_DEL) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										IgnoreList.removeIgnore(string3);
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_TEST) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										if (string3.startsWith("<img=0>") || string3.startsWith("<img=1>")) {
											string3 = string3.substring(7);
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = FriendOrSelfCheck.isFriendOrSelf(string3) ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETWORLDNAME) {
										int friendIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendList.friendListStatus == 2 && friendIndex < FriendList.FRIEND_COUNT) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendList.friendWorldNames[friendIndex];
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATDISPLAYNAME) {
										if (FriendsChat.friendsChatName != null) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.formatDisplayName(FriendsChat.friendsChatName);
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATCOUNT) {
										if (FriendsChat.friendsChatName != null) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.clanChatUserCount;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERNAME) {
										int clanMemberIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.clanChatUsers[clanMemberIndex].displayName;
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERWORLD) {
										int clanMemberIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.clanChatUsers[clanMemberIndex].worldId;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERRANK) {
										int clanMemberIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.clanChatUsers[clanMemberIndex].rank;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATMINKICK) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.friendChatMinKickRank;
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_KICKUSER) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										FriendsChat.kickFriendsChatUser(string3);
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATRANK) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = FriendsChat.myFriendsChatRank;
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_JOINCHAT) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										FriendsChat.sendJoinFriendChat(string3);
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_LEAVECHAT) {
										FriendsChat.sendLeaveFriendChat();
										return;
									}
									if (opcode == ClientScriptOpCode.IGNORE_COUNT) {
										if (FriendList.friendListStatus == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = IgnoreList.ignoreCount;
										return;
									}
									if (opcode == ClientScriptOpCode.IGNORE_GETNAME) {
										int ignoreIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendList.friendListStatus != 0 && ignoreIndex < IgnoreList.ignoreCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = IgnoreList.ignoreNames[ignoreIndex];
											if (IgnoreList.ignorePreviousNames[ignoreIndex] != null) {
												CS2State.stringStack[CS2State.stringStackPtr++] = IgnoreList.ignorePreviousNames[ignoreIndex];
												return;
											}
											CS2State.stringStack[CS2State.stringStackPtr++] = "";
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.IGNORE_TEXT) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										if (string3.startsWith("<img=0>") || string3.startsWith("<img=1>")) {
											string3 = string3.substring(7);
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = IgnoreList.isIgnored(string3) ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_ISSELF) {
										int clanMemberIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendsChat.clanChatUsers != null && clanMemberIndex < FriendsChat.clanChatUserCount && FriendsChat.clanChatUsers[clanMemberIndex].username.equalsIgnoreCase(PlayerUpdate.localPlayer.displayName)) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 1;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATOWNERNAME) {
										if (FriendsChat.clanChatOwnerName != null) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.clanChatOwnerName;
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERWORLDNAME) {
										int clanMemberIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.clanChatUsers[clanMemberIndex].worldName;
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_SAME_GAME) {
										int friendIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendList.friendListStatus == 2 && friendIndex >= 0 && friendIndex < FriendList.FRIEND_COUNT) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = FriendList.friendSameGame[friendIndex] ? 1 : 0;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.FRIEND_GETSLOTFROMNAME) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										if (string3.startsWith("<img=0>") || string3.startsWith("<img=1>")) {
											string3 = string3.substring(7);
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = FriendList.getFriendIndex(string3);
										return;
									}
									if (opcode == ClientScriptOpCode.PLAYERCOUNTRY) {
										ScriptRunner.intStack[CS2State.intStackPtr++] = GameShell.country;
										return;
									}
									if (opcode == ClientScriptOpCode.IGNORE_ADD_TEMP) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										IgnoreList.add(string3, true);
										return;
									}
									if (opcode == ClientScriptOpCode.IGNORE_IS_TEMP) {
										int ignoreIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = IgnoreList.ignoreIsTemporary[ignoreIndex] ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.CLAN_GETCHATUSERNAME_UNFILTERED) {
										int clanMemberIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendsChat.friendsChatName != null && clanMemberIndex < FriendsChat.clanChatUserCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = FriendsChat.clanChatUsers[clanMemberIndex].username;
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.IGNORE_GETNAME_UNFILTERED) {
										int ignoreIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (FriendList.friendListStatus != 0 && ignoreIndex < IgnoreList.ignoreCount) {
											CS2State.stringStack[CS2State.stringStackPtr++] = IgnoreList.ignoreNamesUnfiltered[ignoreIndex];
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
								} else if (opcode < 4000) {
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERTYPE) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].isBuyOffer();
										return;
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERITEM) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].itemId;
										return;
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERPRICE) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].price;
										return;
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERCOUNT) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].amount;
										return;
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERCOMPLETEDCOUNT) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].currentAmount;
										return;
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_GETOFFERCOMPLETEDGOLD) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].totalPrice;
										return;
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_ISOFFEREMPTY) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										int slotState = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].getSlotState();
										ScriptRunner.intStack[CS2State.intStackPtr++] = slotState == 0 ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_ISOFFERSTABLE) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										int slotState = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].getSlotState();
										ScriptRunner.intStack[CS2State.intStackPtr++] = slotState == 2 ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_ISOFFERFINISHED) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										int slotState = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].getSlotState();
										ScriptRunner.intStack[CS2State.intStackPtr++] = slotState == 5 ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.STOCKMARKET_ISOFFERADDING) {
										int offerSlot = ScriptRunner.intStack[--CS2State.intStackPtr];
										int slotState = StockMarketOffer.GRAND_EXCHANGE_SLOTS[offerSlot].getSlotState();
										ScriptRunner.intStack[CS2State.intStackPtr++] = slotState == 1 ? 1 : 0;
										return;
									}
								} else if (opcode < 4100) {
									if (opcode == ClientScriptOpCode.ADD) {
										CS2State.intStackPtr -= 2;
										int lhs = ScriptRunner.intStack[CS2State.intStackPtr];
										int addend = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs + addend;
										return;
									}
									if (opcode == ClientScriptOpCode.SUB) {
										CS2State.intStackPtr -= 2;
										int lhs = ScriptRunner.intStack[CS2State.intStackPtr];
										int subtrahend = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs - subtrahend;
										return;
									}
									if (opcode == ClientScriptOpCode.MULTIPLY) {
										CS2State.intStackPtr -= 2;
										int lhs = ScriptRunner.intStack[CS2State.intStackPtr];
										int multiplier = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs * multiplier;
										return;
									}
									if (opcode == ClientScriptOpCode.DIVIDE) {
										CS2State.intStackPtr -= 2;
										int dividend = ScriptRunner.intStack[CS2State.intStackPtr];
										int divisor = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = dividend / divisor;
										return;
									}
									if (opcode == ClientScriptOpCode.RANDOM) {
										int randomBound = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = (int) (Math.random() * (double) randomBound);
										return;
									}
									if (opcode == ClientScriptOpCode.RANDOMINC) {
										int maxInclusive = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = (int) (Math.random() * (double) (maxInclusive + 1));
										return;
									}
									if (opcode == ClientScriptOpCode.INTERPOLATE) {
										CS2State.intStackPtr -= 5;
										int startValue = ScriptRunner.intStack[CS2State.intStackPtr];
										int endValue = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										int rangeStart = ScriptRunner.intStack[CS2State.intStackPtr + 2];
										int rangeEnd = ScriptRunner.intStack[CS2State.intStackPtr + 3];
										int rangeValue = ScriptRunner.intStack[CS2State.intStackPtr + 4];
										ScriptRunner.intStack[CS2State.intStackPtr++] = startValue + (endValue - startValue) * (rangeValue - rangeStart) / (rangeEnd - rangeStart);
										return;
									}
									@Pc(7451) long local7451;
									@Pc(7444) long local7444;
									if (opcode == ClientScriptOpCode.ADDPERCENT) {
										CS2State.intStackPtr -= 2;
										long baseAmount = ScriptRunner.intStack[CS2State.intStackPtr];
										long percent = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = (int) (baseAmount + baseAmount * percent / 100L);
										return;
									}
									if (opcode == ClientScriptOpCode.SETBIT) {
										CS2State.intStackPtr -= 2;
										int bitfield = ScriptRunner.intStack[CS2State.intStackPtr];
										int bit = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = bitfield | 0x1 << bit;
										return;
									}
									if (opcode == ClientScriptOpCode.CLEARBIT) {
										CS2State.intStackPtr -= 2;
										int bitfield = ScriptRunner.intStack[CS2State.intStackPtr];
										int bit = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = bitfield & -(0x1 << bit) - 1;
										return;
									}
									if (opcode == ClientScriptOpCode.TESTBIT) {
										CS2State.intStackPtr -= 2;
										int bitfield = ScriptRunner.intStack[CS2State.intStackPtr];
										int bit = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = (bitfield & 0x1 << bit) == 0 ? 0 : 1;
										return;
									}
									if (opcode == ClientScriptOpCode.MODULO) {
										CS2State.intStackPtr -= 2;
										int dividend = ScriptRunner.intStack[CS2State.intStackPtr];
										int divisor = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = dividend % divisor;
										return;
									}
									if (opcode == ClientScriptOpCode.POW) {
										CS2State.intStackPtr -= 2;
										int base = ScriptRunner.intStack[CS2State.intStackPtr];
										int power = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										if (base == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = (int) Math.pow((double) base, (double) power);
										return;
									}
									if (opcode == ClientScriptOpCode.INVPOW) {
										CS2State.intStackPtr -= 2;
										int base = ScriptRunner.intStack[CS2State.intStackPtr];
										int root = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										if (base == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
											return;
										}
										if (root == 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = Integer.MAX_VALUE;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = (int) Math.pow((double) base, 1.0D / (double) root);
										return;
									}
									if (opcode == ClientScriptOpCode.AND) {
										CS2State.intStackPtr -= 2;
										int lhs = ScriptRunner.intStack[CS2State.intStackPtr];
										int mask = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs & mask;
										return;
									}
									if (opcode == ClientScriptOpCode.OR) {
										CS2State.intStackPtr -= 2;
										int lhs = ScriptRunner.intStack[CS2State.intStackPtr];
										int mask = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs | mask;
										return;
									}
									if (opcode == ClientScriptOpCode.MIN) {
										CS2State.intStackPtr -= 2;
										int lhs = ScriptRunner.intStack[CS2State.intStackPtr];
										int other = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs < other ? lhs : other;
										return;
									}
									if (opcode == ClientScriptOpCode.MAX) {
										CS2State.intStackPtr -= 2;
										int lhs = ScriptRunner.intStack[CS2State.intStackPtr];
										int other = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = lhs > other ? lhs : other;
										return;
									}
									if (opcode == ClientScriptOpCode.SCALE) {
										CS2State.intStackPtr -= 3;
										long numerator = ScriptRunner.intStack[CS2State.intStackPtr];
										long denominator = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										@Pc(7832) long value = (long) ScriptRunner.intStack[CS2State.intStackPtr + 2];
										ScriptRunner.intStack[CS2State.intStackPtr++] = (int) (numerator * value / denominator);
										return;
									}
									if (opcode == ClientScriptOpCode.RANDOM_SOUND_PITCH) {
										CS2State.intStackPtr -= 2;
										int lowerRange = ScriptRunner.intStack[CS2State.intStackPtr];
										int upperRange = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										if (lowerRange > 700 || upperRange > 700) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = 256;
										}
										@Pc(7891) double exponent = (Math.random() * (double) (upperRange + lowerRange) + 800.0D - (double) lowerRange) / 100.0D;
										ScriptRunner.intStack[CS2State.intStackPtr++] = (int) (Math.pow(2.0D, exponent) + 0.5D);
										return;
									}
								} else if (opcode < 4200) {
									if (opcode == ClientScriptOpCode.APPEND_NUM) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										int number = ScriptRunner.intStack[--CS2State.intStackPtr];
										CS2State.stringStack[CS2State.stringStackPtr++] = string3 + number;
										return;
									}
									if (opcode == ClientScriptOpCode.APPEND) {
										CS2State.stringStackPtr -= 2;
										string3 = CS2State.stringStack[CS2State.stringStackPtr];
										string2 = CS2State.stringStack[CS2State.stringStackPtr + 1];
										CS2State.stringStack[CS2State.stringStackPtr++] = string3 + string2;
										return;
									}
									if (opcode == ClientScriptOpCode.APPEND_SIGNNUM) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										int number = ScriptRunner.intStack[--CS2State.intStackPtr];
										CS2State.stringStack[CS2State.stringStackPtr++] = string3 + Utils.toSignedString(number, true);
										return;
									}
									if (opcode == ClientScriptOpCode.LOWERCASE) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										CS2State.stringStack[CS2State.stringStackPtr++] = string3.toLowerCase();
										return;
									}
									if (opcode == ClientScriptOpCode.FROMDATE) {
										CS2State.stringStack[CS2State.stringStackPtr++] = CS2State.runedayToDateString(ScriptRunner.intStack[--CS2State.intStackPtr]);
										return;
									}
									if (opcode == ClientScriptOpCode.TEXT_GENDER) {
										CS2State.stringStackPtr -= 2;
										string3 = CS2State.stringStack[CS2State.stringStackPtr];
										string2 = CS2State.stringStack[CS2State.stringStackPtr + 1];
										if (PlayerUpdate.localPlayer.playerModel != null && PlayerUpdate.localPlayer.playerModel.female) {
											CS2State.stringStack[CS2State.stringStackPtr++] = string2;
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = string3;
										return;
									}
									if (opcode == ClientScriptOpCode.TOSTRING) {
										int number = ScriptRunner.intStack[--CS2State.intStackPtr];
										CS2State.stringStack[CS2State.stringStackPtr++] = Integer.toString(number);
										return;
									}
									if (opcode == ClientScriptOpCode.COMPARE) {
										CS2State.stringStackPtr -= 2;
										ScriptRunner.intStack[CS2State.intStackPtr++] = Utils.compareLocaleAware(CS2State.stringStack[CS2State.stringStackPtr + 1], ClientState.language, CS2State.stringStack[CS2State.stringStackPtr]);
										return;
									}
									@Pc(8174) FontMetrics fontMetrics;
									if (opcode == ClientScriptOpCode.PARAHEIGHT) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										CS2State.intStackPtr -= 2;
										int maxWidth = ScriptRunner.intStack[CS2State.intStackPtr];
										int fontId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										fontMetrics = FontMetricsLoader.getFontMetrics(fontId, Resource.FONT_METRICS);
										ScriptRunner.intStack[CS2State.intStackPtr++] = fontMetrics.getLineCount(StaticMedia.nameIconSprites, string3, maxWidth);
										return;
									}
									if (opcode == 4109) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										CS2State.intStackPtr -= 2;
										int maxWidth = ScriptRunner.intStack[CS2State.intStackPtr];
										int fontId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										fontMetrics = FontMetricsLoader.getFontMetrics(fontId, Resource.FONT_METRICS);
										ScriptRunner.intStack[CS2State.intStackPtr++] = fontMetrics.getTextWidthMultiline(string3, maxWidth, StaticMedia.nameIconSprites);
										return;
									}
									if (opcode == 4110) {
										CS2State.stringStackPtr -= 2;
										string3 = CS2State.stringStack[CS2State.stringStackPtr];
										string2 = CS2State.stringStack[CS2State.stringStackPtr + 1];
										if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) {
											CS2State.stringStack[CS2State.stringStackPtr++] = string3;
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = string2;
										return;
									}
									if (opcode == ClientScriptOpCode.ESCAPE) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										CS2State.stringStack[CS2State.stringStackPtr++] = Utils.escapeTags(string3);
										return;
									}
									if (opcode == ClientScriptOpCode.APPEND_CHAR) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										int charCode = ScriptRunner.intStack[--CS2State.intStackPtr];
										if (charCode == -1) {
											throw new RuntimeException("null char");
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = string3 + (char) charCode;
										return;
									}
									if (opcode == ClientScriptOpCode.CHAR_ISPRINTABLE) {
										int charCode = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = CS2State.isPrintableChar((char) charCode);
										return;
									}
									if (opcode == ClientScriptOpCode.CHAR_ISALPHANUMERIC) {
										int charCode = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = Utils.isAlphanumeric((char) charCode) ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.CHAR_ISALPHA) {
										int charCode = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = Utils.isAlphabetic((char) charCode) ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.CHAR_ISNUMERIC) {
										int charCode = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = Utils.isDigit((char) charCode) ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.STRING_LENGTH) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										if (string3 != null) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = string3.length();
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.SUBSTRING) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										CS2State.intStackPtr -= 2;
										int startIndex = ScriptRunner.intStack[CS2State.intStackPtr];
										int endIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										CS2State.stringStack[CS2State.stringStackPtr++] = string3.substring(startIndex, endIndex);
										return;
									}
									if (opcode == ClientScriptOpCode.REMOVETAGS) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										@Pc(8532) StringBuffer stringBuffer = new StringBuffer(string3.length());
										@Pc(8534) boolean inTag = false;
										for (int charIndex = 0; charIndex < string3.length(); charIndex++) {
											@Pc(8541) char ch = string3.charAt(charIndex);
											if (ch == '<') {
												inTag = true;
											} else if (ch == '>') {
												inTag = false;
											} else if (!inTag) {
												stringBuffer.append(ch);
											}
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = stringBuffer.toString();
										return;
									}
									if (opcode == ClientScriptOpCode.STRING_INDEXOF_CHAR) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										CS2State.intStackPtr -= 2;
										int searchChar = ScriptRunner.intStack[CS2State.intStackPtr];
										int fromIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										ScriptRunner.intStack[CS2State.intStackPtr++] = string3.indexOf(searchChar, fromIndex);
										return;
									}
									if (opcode == ClientScriptOpCode.STRING_INDEXOF_STRING) {
										CS2State.stringStackPtr -= 2;
										string3 = CS2State.stringStack[CS2State.stringStackPtr];
										string2 = CS2State.stringStack[CS2State.stringStackPtr + 1];
										int fromIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = string3.indexOf(string2, fromIndex);
										return;
									}
									if (opcode == ClientScriptOpCode.CHAR_TOLOWERCASE) {
										int charCode = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = Character.toLowerCase((char) charCode);
										return;
									}
									if (opcode == ClientScriptOpCode.CHAR_TOUPPERCASE) {
										int charCode = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = Character.toUpperCase((char) charCode);
										return;
									}
									if (opcode == ClientScriptOpCode.TOSTRING_LOCALISED) {
										boolean useThousandsSeparator = ScriptRunner.intStack[--CS2State.intStackPtr] != 0;
										int number = ScriptRunner.intStack[--CS2State.intStackPtr];
										CS2State.stringStack[CS2State.stringStackPtr++] = Utils.formatLocalisedNumber(0, useThousandsSeparator, ClientState.language, (long) number);
										return;
									}
									if (opcode == ClientScriptOpCode.STRINGWIDTH) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										int fontId = ScriptRunner.intStack[--CS2State.intStackPtr];
										@Pc(8755) FontMetrics fontMetrics2 = FontMetricsLoader.getFontMetrics(fontId, Resource.FONT_METRICS);
										ScriptRunner.intStack[CS2State.intStackPtr++] = fontMetrics2.getTextWidth(string3, StaticMedia.nameIconSprites);
										return;
									}
								} else if (opcode < 4300) {
									if (opcode == ClientScriptOpCode.OC_NAME) {
										int itemId = ScriptRunner.intStack[--CS2State.intStackPtr];
										CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.OBJ.list(itemId).name;
										return;
									}
									@Pc(8817) ItemType objType;
									if (opcode == ClientScriptOpCode.OC_OP) {
										CS2State.intStackPtr -= 2;
										int itemId = ScriptRunner.intStack[CS2State.intStackPtr];
										int opIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										objType = TypeLists.OBJ.list(itemId);
										if (opIndex >= 1 && opIndex <= 5 && objType.groundActions[opIndex - 1] != null) {
											CS2State.stringStack[CS2State.stringStackPtr++] = objType.groundActions[opIndex - 1];
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.OC_IOP) {
										CS2State.intStackPtr -= 2;
										int itemId = ScriptRunner.intStack[CS2State.intStackPtr];
										int opIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										objType = TypeLists.OBJ.list(itemId);
										if (opIndex >= 1 && opIndex <= 5 && objType.inventoryActions[opIndex - 1] != null) {
											CS2State.stringStack[CS2State.stringStackPtr++] = objType.inventoryActions[opIndex - 1];
											return;
										}
										CS2State.stringStack[CS2State.stringStackPtr++] = "";
										return;
									}
									if (opcode == ClientScriptOpCode.OC_COST) {
										int itemId = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OBJ.list(itemId).value;
										return;
									}
									if (opcode == ClientScriptOpCode.OC_STACKABLE) {
										int itemId = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OBJ.list(itemId).stackable == 1 ? 1 : 0;
										return;
									}
									@Pc(8980) ItemType objType2;
									if (opcode == ClientScriptOpCode.OC_CERT) {
										int itemId = ScriptRunner.intStack[--CS2State.intStackPtr];
										objType2 = TypeLists.OBJ.list(itemId);
										if (objType2.certTemplateId == -1 && objType2.certId >= 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = objType2.certId;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = itemId;
										return;
									}
									if (opcode == ClientScriptOpCode.OC_UNCERT) {
										int itemId = ScriptRunner.intStack[--CS2State.intStackPtr];
										objType2 = TypeLists.OBJ.list(itemId);
										if (objType2.certTemplateId >= 0 && objType2.certId >= 0) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = objType2.certId;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = itemId;
										return;
									}
									if (opcode == ClientScriptOpCode.OC_MEMBERS) {
										int itemId = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OBJ.list(itemId).membersOnly ? 1 : 0;
										return;
									}
									if (opcode == ClientScriptOpCode.OC_PARAM) {
										CS2State.intStackPtr -= 2;
										int itemId = ScriptRunner.intStack[CS2State.intStackPtr];
										int paramId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										paramType2 = TypeLists.PARAMS.list(paramId);
										if (paramType2.isString()) {
											CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.OBJ.list(itemId).getStringParam(paramType2.typeName, paramId);
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OBJ.list(itemId).getIntParam(paramType2.defaultInt, paramId);
										return;
									}
									if (opcode == ClientScriptOpCode.OC_ICURSOR) {
										CS2State.intStackPtr -= 2;
										int itemId = ScriptRunner.intStack[CS2State.intStackPtr];
										int opIndex = ScriptRunner.intStack[CS2State.intStackPtr + 1] - 1;
										objType = TypeLists.OBJ.list(itemId);
										if (objType.customCursorOp1 == opIndex) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = objType.customCursorId1;
											return;
										}
										if (objType.customCursorOp2 == opIndex) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = objType.customCursorId2;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
										return;
									}
									if (opcode == ClientScriptOpCode.OC_FIND) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										int tradeableOnly = ScriptRunner.intStack[--CS2State.intStackPtr];
										ItemFindQuery.queryItemNoParams(tradeableOnly == 1, string3);
										ScriptRunner.intStack[CS2State.intStackPtr++] = Find.findResultCount;
										return;
									}
									if (opcode == ClientScriptOpCode.OC_FINDNEXT) {
										if (Find.queryResults != null && MessageBox.queryResultsCursor < Find.findResultCount) {
											ScriptRunner.intStack[CS2State.intStackPtr++] = Find.queryResults[MessageBox.queryResultsCursor++] & 0xFFFF;
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = -1;
										return;
									}
									if (opcode == ClientScriptOpCode.OC_FINDRESTART) {
										MessageBox.queryResultsCursor = 0;
										return;
									}
									if (opcode == ClientScriptOpCode.OC_MULTISTACKSIZE) {
										int itemId = ScriptRunner.intStack[--CS2State.intStackPtr];
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.OBJ.list(itemId).multiStackSize;
										return;
									}
									if (opcode == 4214) {
										string3 = CS2State.stringStack[--CS2State.stringStackPtr];
										CS2State.intStackPtr -= 3;
										int tradeableOnly = ScriptRunner.intStack[CS2State.intStackPtr];
										int paramId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										int paramValue = ScriptRunner.intStack[CS2State.intStackPtr + 2];
										ItemFindQuery.queryItemParamInt(paramValue, tradeableOnly == 1, string3, paramId);
										ScriptRunner.intStack[CS2State.intStackPtr++] = Find.findResultCount;
										return;
									}
									if (opcode == 4215) {
										CS2State.stringStackPtr -= 2;
										CS2State.intStackPtr -= 2;
										string3 = CS2State.stringStack[CS2State.stringStackPtr];
										int tradeableOnly = ScriptRunner.intStack[CS2State.intStackPtr];
										int paramId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										@Pc(9379) String string4 = CS2State.stringStack[CS2State.stringStackPtr + 1];
										CS2Executor.queryItemParamString(tradeableOnly == 1, string3, string4, paramId);
										ScriptRunner.intStack[CS2State.intStackPtr++] = Find.findResultCount;
										return;
									}
								} else if (opcode < 4400) {
									if (opcode == ClientScriptOpCode.NC_PARAM) {
										CS2State.intStackPtr -= 2;
										int npcId = ScriptRunner.intStack[CS2State.intStackPtr];
										int paramId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										paramType2 = TypeLists.PARAMS.list(paramId);
										if (paramType2.isString()) {
											CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.NPC.list(npcId).getStringParam(paramId, paramType2.typeName);
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.NPC.list(npcId).getParam(paramType2.defaultInt, paramId);
										return;
									}
								} else if (opcode < 4500) {
									if (opcode == 4400) {
										CS2State.intStackPtr -= 2;
										int locId = ScriptRunner.intStack[CS2State.intStackPtr];
										int paramId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										paramType2 = TypeLists.PARAMS.list(paramId);
										if (paramType2.isString()) {
											CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.LOC.list(locId).getStringParam(paramType2.typeName, paramId);
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.LOC.list(locId).getIntParam(paramId, paramType2.defaultInt);
										return;
									}
								} else if (opcode < 4600) {
									if (opcode == 4500) {
										CS2State.intStackPtr -= 2;
										int structId = ScriptRunner.intStack[CS2State.intStackPtr];
										int paramId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
										paramType2 = TypeLists.PARAMS.list(paramId);
										if (paramType2.isString()) {
											CS2State.stringStack[CS2State.stringStackPtr++] = TypeLists.STRUCT.list(structId).getStringParam(paramType2.typeName, paramId);
											return;
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = TypeLists.STRUCT.list(structId).getParam(paramId, paramType2.defaultInt);
										return;
									}
								} else if (opcode < 4700 && opcode == ClientScriptOpCode.BAS_GETANIM_READY) {
									int basId = ScriptRunner.intStack[--CS2State.intStackPtr];
									@Pc(9607) BasType basType = TypeLists.BAS.list(basId);
									if (basType.randomStandSequences != null && basType.randomStandSequences.length > 0) {
										int bestIndex = 0;
										int bestChance = basType.randomStandSequenceChances[0];
										for (int standIndex = 1; standIndex < basType.randomStandSequences.length; standIndex++) {
											if (basType.randomStandSequenceChances[standIndex] > bestChance) {
												bestIndex = standIndex;
												bestChance = basType.randomStandSequenceChances[standIndex];
											}
										}
										ScriptRunner.intStack[CS2State.intStackPtr++] = basType.randomStandSequences[bestIndex];
										return;
									}
									ScriptRunner.intStack[CS2State.intStackPtr++] = basType.standSequenceId;
									return;
								}
							}
						}
					}
				} else {
					if (opcode >= 2000) {
						opcode -= 1000;
						component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
					} else {
						component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
					}
					Component.redrawComponent(component);
					if (opcode == 1200 || opcode == 1205 || opcode == 1208 || opcode == 1209 || opcode == 1212 || opcode == 1213) {
						CS2State.intStackPtr -= 2;
						int itemId = ScriptRunner.intStack[CS2State.intStackPtr];
						int stackSize = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						if (component.slotId == -1) {
							PulseEvent.delayedIfItemUpdate(component.idHash);
							PulseEvent.delayedIfAngleUpdate(component.idHash);
							PulseEvent.delayedIfOffsetUpdate(component.idHash);
						}
						if (itemId == -1) {
							component.mediaType = MediaType.RAW_MODEL;
							component.primaryMediaId = -1;
							component.containerItemId = -1;
							return;
						}
						component.containerItemId = itemId;
						component.itemStackSize = stackSize;
						if (opcode == 1208 || opcode == 1209) {
							component.wearCol = true;
						} else {
							component.wearCol = false;
						}
						@Pc(1713) ItemType objType3 = TypeLists.OBJ.list(itemId);
						component.spritePitch = objType3.modelRotationX;
						component.spriteRoll = objType3.modelRotationY;
						component.spriteYaw = objType3.modelRotationZ;
						component.offsetX = objType3.modelOffsetX;
						component.offsetY = objType3.modelOffsetY;
						component.spriteScale = objType3.modelZoom;
						if (opcode == 1205 || opcode == 1209) {
							component.renderStack = 0;
						} else if (opcode == 1212 || opcode == 1213) {
							component.renderStack = 1;
						} else {
							component.renderStack = 2;
						}
						if (component.aspectWidth > 0) {
							component.spriteScale = component.spriteScale * 32 / component.aspectWidth;
							return;
						}
						if (component.baseWidth > 0) {
							component.spriteScale = component.spriteScale * 32 / component.baseWidth;
						}
						return;
					}
					if (opcode == 1201) {
						component.mediaType = MediaType.NPC_HEAD;
						component.primaryMediaId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (component.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component.idHash);
						}
						return;
					}
					if (opcode == 1202) {
						component.mediaType = MediaType.PLAYER_HEAD;
						component.primaryMediaId = -1;
						if (component.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component.idHash);
						}
						return;
					}
					if (opcode == 1203) {
						component.mediaType = MediaType.NPC_MODEL;
						component.primaryMediaId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (component.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component.idHash);
						}
						return;
					}
					if (opcode == 1204) {
						component.mediaType = MediaType.PLAYER_MODEL;
						component.primaryMediaId = ScriptRunner.intStack[--CS2State.intStackPtr];
						if (component.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component.idHash);
						}
						return;
					}
					if (opcode == 1206) {
						CS2State.intStackPtr -= 4;
						component.skyboxId = ScriptRunner.intStack[CS2State.intStackPtr];
						component.lightPositionX = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						component.lightPositionY = ScriptRunner.intStack[CS2State.intStackPtr + 2];
						component.lightPositionZ = ScriptRunner.intStack[CS2State.intStackPtr + 3];
						Component.redrawComponent(component);
						return;
					}
					if (opcode == 1207) {
						CS2State.intStackPtr -= 2;
						component.sunRotationX = ScriptRunner.intStack[CS2State.intStackPtr];
						component.sunRotationY = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						Component.redrawComponent(component);
						return;
					}
					if (opcode == 1210) {
						CS2State.intStackPtr -= 4;
						component.primaryMediaId = ScriptRunner.intStack[CS2State.intStackPtr];
						component.secondaryMediaId = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						if (ScriptRunner.intStack[CS2State.intStackPtr + 2] == 1) {
							component.mediaType = MediaType.ITEM_CONTAINER_FEMALE;
						} else {
							component.mediaType = MediaType.ITEM_CONTAINER_MALE;
						}
						if (ScriptRunner.intStack[CS2State.intStackPtr + 3] == 1) {
							component.wearCol = true;
						} else {
							component.wearCol = false;
						}
						if (component.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component.idHash);
						}
						return;
					}
					if (opcode == 1211) {
						component.mediaType = MediaType.PLAYER_MODEL;
						component.primaryMediaId = PlayerUpdate.myPlayerIndex;
						component.secondaryMediaId = 0;
						if (component.slotId == -1) {
							PulseEvent.delayedIfContentUpdate(component.idHash);
						}
						return;
					}
				}
			} else {
				if (opcode >= 2000) {
					opcode -= 1000;
					component = Interface.getComponent(ScriptRunner.intStack[--CS2State.intStackPtr]);
				} else {
					component = hasIntOperand ? CS2State.activeComponent2 : CS2State.activeComponent;
				}
				if (opcode == 1000) {
					CS2State.intStackPtr -= 4;
					component.basePositionX = ScriptRunner.intStack[CS2State.intStackPtr];
					component.basePositionY = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					int aspectXType = ScriptRunner.intStack[CS2State.intStackPtr + 2];
					if (aspectXType < 0) {
						aspectXType = 0;
					} else if (aspectXType > 5) {
						aspectXType = 5;
					}
					int aspectYType = ScriptRunner.intStack[CS2State.intStackPtr + 3];
					if (aspectYType < 0) {
						aspectYType = 0;
					} else if (aspectYType > 5) {
						aspectYType = 5;
					}
					component.aspectXType = (byte) aspectXType;
					component.aspectYType = (byte) aspectYType;
					Component.redrawComponent(component);
					InterfaceManager.relayoutAgainstParent(component);
					if (component.slotId == -1) {
						PulseEvent.delayedIfPositionUpdate(component.idHash);
					}
					return;
				}
				if (opcode == 1001) {
					CS2State.intStackPtr -= 4;
					component.baseWidth = ScriptRunner.intStack[CS2State.intStackPtr];
					component.baseHeight = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					component.aspectWidth = 0;
					component.aspectHeight = 0;
					int aspectWidthType = ScriptRunner.intStack[CS2State.intStackPtr + 2];
					if (aspectWidthType < 0) {
						aspectWidthType = 0;
					} else if (aspectWidthType > 4) {
						aspectWidthType = 4;
					}
					int aspectHeightType = ScriptRunner.intStack[CS2State.intStackPtr + 3];
					if (aspectHeightType < 0) {
						aspectHeightType = 0;
					} else if (aspectHeightType > 4) {
						aspectHeightType = 4;
					}
					component.aspectWidthType = (byte) aspectWidthType;
					component.aspectHeightType = (byte) aspectHeightType;
					Component.redrawComponent(component);
					InterfaceManager.relayoutAgainstParent(component);
					if (component.type == ComponentType.CONTAINER) {
						Component.layoutChildren(false, component);
					}
					return;
				}
				if (opcode == 1003) {
					@Pc(661) boolean hidden = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
					if (component.hidden != hidden) {
						component.hidden = hidden;
						Component.redrawComponent(component);
					}
					if (component.slotId == -1) {
						PulseEvent.delayedIfHideUpdate(component.idHash);
					}
					return;
				}
				if (opcode == 1004) {
					CS2State.intStackPtr -= 2;
					component.aspectX = ScriptRunner.intStack[CS2State.intStackPtr];
					component.aspectY = ScriptRunner.intStack[CS2State.intStackPtr + 1];
					Component.redrawComponent(component);
					InterfaceManager.relayoutAgainstParent(component);
					if (component.type == ComponentType.CONTAINER) {
						Component.layoutChildren(false, component);
					}
					return;
				}
				if (opcode == 1005) {
					component.preventClickThrough = ScriptRunner.intStack[--CS2State.intStackPtr] == 1;
					return;
				}
			}
		}
		throw new IllegalStateException(String.valueOf(opcode));
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Lclient!jk;I)V")
	public static void executeScript(@OriginalArg(0) ClientScript clientScript, @OriginalArg(1) int stackLimit) {
		CS2State.intStackPtr = 0;
		CS2State.stringStackPtr = 0;
		@Pc(5) int instrPtr = -1;
		@Pc(8) int[] operations = clientScript.operations;
		@Pc(11) int[] intOpValues = clientScript.intOpValues;
		@Pc(13) byte lastOpcode = -1;
		CS2State.framePtr = 0;
		@Pc(603) int local603;
		try {
			@Pc(17) int instructionCount = 0;
			label260: while (true) {
				instructionCount++;
				if (instructionCount > stackLimit) {
					throw new RuntimeException("slow");
				}
				instrPtr++;
				@Pc(31) int currentOpcode = operations[instrPtr];
				if (currentOpcode >= 100) {
					@Pc(802) boolean hasIntOperand;
					if (intOpValues[instrPtr] == 1) {
						hasIntOperand = true;
					} else {
						hasIntOperand = false;
					}
					if (currentOpcode >= 100 && currentOpcode < 5000) {
						executeOpBelow5000(currentOpcode, hasIntOperand);
					} else if (currentOpcode >= 5000 && currentOpcode < 10000) {
						executeOpFrom5000(currentOpcode, hasIntOperand);
					} else {
						throw new IllegalStateException("Command: " + currentOpcode);
					}
				} else if (currentOpcode == ClientScriptOpCode.PUSH_CONSTANT_INT) {
					ScriptRunner.intStack[CS2State.intStackPtr++] = intOpValues[instrPtr];
				} else {
					@Pc(54) int operand;
					if (currentOpcode == ClientScriptOpCode.PUSH_VAR) {
						operand = intOpValues[instrPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = PlayerVarDomain.playerVarProvider.activeVars[operand];
					} else if (currentOpcode == ClientScriptOpCode.POP_VAR) {
						operand = intOpValues[instrPtr];
						PlayerVarDomain.playerVarProvider.setVarValueInt(ScriptRunner.intStack[--CS2State.intStackPtr], operand);
					} else if (currentOpcode == ClientScriptOpCode.PUSH_CONSTANT_STRING) {
						CS2State.stringStack[CS2State.stringStackPtr++] = clientScript.stringOpValues[instrPtr];
					} else if (currentOpcode == ClientScriptOpCode.BRANCH) {
						instrPtr += intOpValues[instrPtr];
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_NOT) {
						CS2State.intStackPtr -= 2;
						if (ScriptRunner.intStack[CS2State.intStackPtr] != ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues[instrPtr];
						}
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_EQUALS) {
						CS2State.intStackPtr -= 2;
						if (ScriptRunner.intStack[CS2State.intStackPtr] == ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues[instrPtr];
						}
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_LESS_THAN) {
						CS2State.intStackPtr -= 2;
						if (ScriptRunner.intStack[CS2State.intStackPtr] < ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues[instrPtr];
						}
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_GREATER_THAN) {
						CS2State.intStackPtr -= 2;
						if (ScriptRunner.intStack[CS2State.intStackPtr] > ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues[instrPtr];
						}
					} else if (currentOpcode == ClientScriptOpCode.RETURN) {
						if (CS2State.framePtr == 0) {
							return;
						}
						@Pc(216) StackFrame stackFrame = CS2State.frames[--CS2State.framePtr];
						clientScript = stackFrame.script;
						operations = clientScript.operations;
						intOpValues = clientScript.intOpValues;
						instrPtr = stackFrame.pc;
						ScriptRunner.intLocals = stackFrame.intVars;
						ScriptRunner.stringVars = stackFrame.stringVars;
					} else if (currentOpcode == ClientScriptOpCode.POP_VARBIT) {
						operand = intOpValues[instrPtr];
						ScriptRunner.intStack[CS2State.intStackPtr++] = PlayerVarDomain.playerVarProvider.getVarBitValue(operand);
					} else if (currentOpcode == ClientScriptOpCode.PUSH_VARBIT) {
						operand = intOpValues[instrPtr];
						PlayerVarDomain.playerVarProvider.setVarBitValue(operand, ScriptRunner.intStack[--CS2State.intStackPtr]);
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_LESS_THAN_OR_EQUALS) {
						CS2State.intStackPtr -= 2;
						if (ScriptRunner.intStack[CS2State.intStackPtr] <= ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues[instrPtr];
						}
					} else if (currentOpcode == ClientScriptOpCode.BRANCH_GREATER_THAN_OR_EQUALS) {
						CS2State.intStackPtr -= 2;
						if (ScriptRunner.intStack[CS2State.intStackPtr] >= ScriptRunner.intStack[CS2State.intStackPtr + 1]) {
							instrPtr += intOpValues[instrPtr];
						}
					} else if (currentOpcode == ClientScriptOpCode.PUSH_INT_LOCAL) {
						ScriptRunner.intStack[CS2State.intStackPtr++] = ScriptRunner.intLocals[intOpValues[instrPtr]];
					} else if (currentOpcode == ClientScriptOpCode.POP_INT_LOCAL) {
						ScriptRunner.intLocals[intOpValues[instrPtr]] = ScriptRunner.intStack[--CS2State.intStackPtr];
					} else if (currentOpcode == ClientScriptOpCode.PUSH_STRING_LOCAL) {
						CS2State.stringStack[CS2State.stringStackPtr++] = ScriptRunner.stringVars[intOpValues[instrPtr]];
					} else if (currentOpcode == ClientScriptOpCode.POP_STRING_LOCAL) {
						ScriptRunner.stringVars[intOpValues[instrPtr]] = CS2State.stringStack[--CS2State.stringStackPtr];
					} else if (currentOpcode == ClientScriptOpCode.JOIN_STRING) {
						operand = intOpValues[instrPtr];
						CS2State.stringStackPtr -= operand;
						@Pc(400) String string = Utils.joinStrings(operand, CS2State.stringStack, CS2State.stringStackPtr);
						CS2State.stringStack[CS2State.stringStackPtr++] = string;
					} else if (currentOpcode == ClientScriptOpCode.POP_INT_DISCARD) {
						CS2State.intStackPtr--;
					} else if (currentOpcode == ClientScriptOpCode.POP_STRING_DISCARD) {
						CS2State.stringStackPtr--;
					} else {
						@Pc(452) int index;
						if (currentOpcode == ClientScriptOpCode.GOSUB_WITH_PARAMS) {
							operand = intOpValues[instrPtr];
							@Pc(436) ClientScript clientScript2 = ClientScript.getClientScript(operand);
							if (clientScript2 == null) {
								throw new RuntimeException();
							}
							@Pc(446) int[] intLocals = new int[clientScript2.intLocalsCount];
							@Pc(450) String[] strings = new String[clientScript2.stringLocalsCount];
							for (index = 0; index < clientScript2.intArgsCount; index++) {
								intLocals[index] = ScriptRunner.intStack[CS2State.intStackPtr + index - clientScript2.intArgsCount];
							}
							for (@Pc(471) int i = 0; i < clientScript2.stringArgsCount; i++) {
								strings[i] = CS2State.stringStack[CS2State.stringStackPtr + i - clientScript2.stringArgsCount];
							}
							CS2State.intStackPtr -= clientScript2.intArgsCount;
							CS2State.stringStackPtr -= clientScript2.stringArgsCount;
							@Pc(502) StackFrame stackFrame2 = new StackFrame();
							stackFrame2.script = clientScript;
							stackFrame2.pc = instrPtr;
							stackFrame2.intVars = ScriptRunner.intLocals;
							stackFrame2.stringVars = ScriptRunner.stringVars;
							if (CS2State.framePtr >= CS2State.frames.length) {
								throw new RuntimeException();
							}
							CS2State.frames[CS2State.framePtr++] = stackFrame2;
							clientScript = clientScript2;
							operations = clientScript2.operations;
							intOpValues = clientScript2.intOpValues;
							instrPtr = -1;
							ScriptRunner.intLocals = intLocals;
							ScriptRunner.stringVars = strings;
						} else if (currentOpcode == ClientScriptOpCode.PUSH_VARC) {
							ScriptRunner.intStack[CS2State.intStackPtr++] = VarcTypeList.VARC_INT[intOpValues[instrPtr]];
						} else if (currentOpcode == ClientScriptOpCode.POP_VARC) {
							operand = intOpValues[instrPtr];
							VarcTypeList.VARC_INT[operand] = ScriptRunner.intStack[--CS2State.intStackPtr];
							PulseEvent.delayedVarcUpdate(operand);
							VarcTypeList.clientIsActive |= VarcTypeList.VARC_PERSISTENT[operand];
						} else if (currentOpcode == ClientScriptOpCode.DEFINE_ARRAY) {
							operand = intOpValues[instrPtr] >> 16;
							int arrayType = intOpValues[instrPtr] & 0xFFFF;
							@Pc(611) int length = ScriptRunner.intStack[--CS2State.intStackPtr];
							if (length >= 0 && length <= 5000) {
								CS2State.globalArrayLengths[operand] = length;
								@Pc(626) byte defaultValue = -1;
								if (arrayType == 105) {
									defaultValue = 0;
								}
								index = 0;
								while (true) {
									if (index >= length) {
										continue label260;
									}
									ScriptRunner.arrayVars[operand][index] = defaultValue;
									index++;
								}
							}
							throw new RuntimeException();
						} else if (currentOpcode == ClientScriptOpCode.PUSH_ARRAY_INT) {
							operand = intOpValues[instrPtr];
							int arrayIndex = ScriptRunner.intStack[--CS2State.intStackPtr];
							if (arrayIndex < 0 || arrayIndex >= CS2State.globalArrayLengths[operand]) {
								throw new RuntimeException();
							}
							ScriptRunner.intStack[CS2State.intStackPtr++] = ScriptRunner.arrayVars[operand][arrayIndex];
						} else if (currentOpcode == ClientScriptOpCode.POP_ARRAY_INT) {
							operand = intOpValues[instrPtr];
							CS2State.intStackPtr -= 2;
							int arrayIndex = ScriptRunner.intStack[CS2State.intStackPtr];
							if (arrayIndex < 0 || arrayIndex >= CS2State.globalArrayLengths[operand]) {
								throw new RuntimeException();
							}
							ScriptRunner.arrayVars[operand][arrayIndex] = ScriptRunner.intStack[CS2State.intStackPtr + 1];
						} else if (currentOpcode == ClientScriptOpCode.PUSH_VARCSTR) {
							@Pc(730) String string2 = VarcTypeList.VARC_STRING[intOpValues[instrPtr]];
							if (string2 == null) {
								string2 = "null";
							}
							CS2State.stringStack[CS2State.stringStackPtr++] = string2;
						} else if (currentOpcode == ClientScriptOpCode.POP_VARCSTR) {
							operand = intOpValues[instrPtr];
							VarcTypeList.VARC_STRING[operand] = CS2State.stringStack[--CS2State.stringStackPtr];
							PulseEvent.delayedVarcStringUpdate(operand);
						} else if (currentOpcode == ClientScriptOpCode.SWITCH) {
							@Pc(774) IterableHashTable iterableHashTable = clientScript.switchMaps[intOpValues[instrPtr]];
							@Pc(787) IntNode intNode = (IntNode) iterableHashTable.get((long) ScriptRunner.intStack[--CS2State.intStackPtr]);
							if (intNode != null) {
								instrPtr += intNode.value;
							}
						} else if (currentOpcode == ClientScriptOpCode.BRANCH_IF_TRUE && ClientFeatures.VOID) {
							// Not part of the original client. Void's scripts branch on the top of the stack being 1.
							if (ScriptRunner.intStack[--CS2State.intStackPtr] == 1) {
								instrPtr += intOpValues[instrPtr];
							}
						}
					}
				}
			}
		} catch (@Pc(837) Exception ex) {
			@Pc(856) StringBuffer stringBuffer;
			if (clientScript.scriptName == null) {
				stringBuffer = new StringBuffer(30);
				stringBuffer.append("CS2: ").append(clientScript.key).append(" ");
				for (int frameIndex = CS2State.framePtr - 1; frameIndex >= 0; frameIndex--) {
					stringBuffer.append("v: ").append(CS2State.frames[frameIndex].script.key).append(" ");
				}
				stringBuffer.append("op: ").append(lastOpcode);
				ExplicitRuntimeException.reportError(stringBuffer.toString(), ex);
			} else {
				ChatBox.writeGameMessage("Clientscript error in: " + clientScript.scriptName);
				stringBuffer = new StringBuffer(30);
				stringBuffer.append("Clientscript error in: ").append(clientScript.scriptName).append("\n");
				for (int frameIndex = CS2State.framePtr - 1; frameIndex >= 0; frameIndex--) {
					stringBuffer.append("via: ").append(CS2State.frames[frameIndex].script.scriptName).append("\n");
				}
				stringBuffer.append("Op: ").append(lastOpcode).append("\n");
				@Pc(896) String string3 = ex.getMessage();
				if (string3 != null && string3.length() > 0) {
					stringBuffer.append("Message: ").append(string3).append("\n");
				}
				ExplicitRuntimeException.reportError(stringBuffer.toString(), ex);
				Console.printToConsole(stringBuffer.toString());
			}
		}
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Lclient!rk;I)V")
	public static void executeHookInternal(@OriginalArg(0) HookRequest hook, @OriginalArg(1) int stackLimit) {
		@Pc(2) Object[] objects = hook.params;
		@Pc(8) int scriptId = (Integer) objects[0];
		@Pc(12) ClientScript clientScript = ClientScript.getClientScript(scriptId);
		if (clientScript == null) {
			return;
		}
		ScriptRunner.intLocals = new int[clientScript.intLocalsCount];
		@Pc(21) int intLocalCount = 0;
		ScriptRunner.stringVars = new String[clientScript.stringLocalsCount];
		@Pc(27) int stringLocalCount = 0;
		for (@Pc(29) int i = 1; i < objects.length; i++) {
			if (objects[i] instanceof Integer) {
				@Pc(41) int intLocal = (Integer) objects[i];
				if (intLocal == -2147483647) {
					intLocal = hook.mouseX;
				}
				if (intLocal == -2147483646) {
					intLocal = hook.mouseY;
				}
				if (intLocal == -2147483645) {
					intLocal = hook.source == null ? -1 : hook.source.idHash;
				}
				if (intLocal == -2147483644) {
					intLocal = hook.clickedOption;
				}
				if (intLocal == -2147483643) {
					intLocal = hook.source == null ? -1 : hook.source.slotId;
				}
				if (intLocal == -2147483642) {
					intLocal = hook.targetComponent == null ? -1 : hook.targetComponent.idHash;
				}
				if (intLocal == -2147483641) {
					intLocal = hook.targetComponent == null ? -1 : hook.targetComponent.slotId;
				}
				if (intLocal == -2147483640) {
					intLocal = hook.typedKeyCode;
				}
				if (intLocal == -2147483639) {
					intLocal = hook.typedKeyChar;
				}
				ScriptRunner.intLocals[intLocalCount++] = intLocal;
			} else if (objects[i] instanceof String) {
				@Pc(135) String string = (String) objects[i];
				if (string.equals("event_opbase")) {
					string = hook.optionName;
				}
				ScriptRunner.stringVars[stringLocalCount++] = string;
			}
		}
		CS2State.hookRequestCount = hook.id;
		executeScript(clientScript, stackLimit);
	}
	@OriginalMember(owner = "client!li", name = "c", descriptor = "[I")
	public static final int[] FEMALE_IDKIT_SLOTS = new int[] { 7, 8, 9, 10, 11, 12, 13, 15 };
	@OriginalMember(owner = "client!oda", name = "k", descriptor = "[I")
	public static final int[] MALE_IDKIT_SLOTS = new int[] { 0, 1, 2, 3, 4, 5, 6, 14 };
}
