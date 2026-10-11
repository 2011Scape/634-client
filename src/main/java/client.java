import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.login.LoginStep;
import com.jagex.game.runetek5.client.window.WindowModeType;
import com.jagex.game.runetek5.client.features.SmokeTest;
import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.WorldConnectionInfo;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.LoadingRequirement;
import com.jagex.game.runetek5.LobbyManager;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.LastRedrawTime;
import com.jagex.game.runetek5.core.constants.LoginResponseCode;
import com.jagex.game.runetek5.core.constants.Ports;
import com.jagex.game.runetek5.core.datastruct.LoginStateUpdate;
import com.jagex.game.runetek5.cache.FileOnDisk;
import com.jagex.game.runetek5.core.io.BufferedSocket;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.io.connection.TickHookRequests;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.MoveSpeed;
import com.jagex.game.runetek5.PathFinder;
import com.jagex.game.runetek5.UnloadedLocCount;
import com.jagex.game.runetek5.camera.CameraMode;
import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.config.rsinterface.HookRequest;
import com.jagex.game.runetek5.graphics.FlipException;
import com.jagex.game.runetek5.graphics.MiniMenuReset;
import com.jagex.game.runetek5.graphics.ToolkitType;
import com.jagex.game.runetek5.js5.request.local.LocalRequester;
import com.jagex.game.runetek5.cache.Decompressor;
import com.jagex.game.runetek5.js5.Js5Archive;
import com.jagex.game.runetek5.js5.Js5ResponseCode;
import com.jagex.game.runetek5.js5.Js5State;
import com.jagex.game.runetek5.js5.Js5WorkerThread;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import com.jagex.game.runetek5.statics.OutboundByteCounter;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.GregorianCalendar;
import java.util.Vector;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecord;
import com.jagex.game.runetek5.client.event.keyboard.KeyLogConstants;
import com.jagex.game.runetek5.client.event.mouse.MouseLog;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.client.ServerEnvironment;
import com.jagex.game.runetek5.client.input.console.Console;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.entity.pathingentity.EntityUpdating;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder;
import com.jagex.game.runetek5.config.object.ObjectType;
import com.jagex.game.runetek5.network.Connection;
import com.jagex.game.runetek5.client.window.WindowMode;
import com.jagex.game.runetek5.network.PingRequester;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.config.varc.VarcTypeList;
import com.jagex.game.runetek5.ModeWhere;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.entity.PathingEntity;
import com.jagex.game.runetek5.interaction.FriendList;
import com.jagex.game.runetek5.ChatLineWriter;
import com.jagex.game.runetek5.client.input.keyboard.CanvasKeyRecorder;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.core.constants.ModeWhat;
import com.jagex.game.runetek5.js5.dependency.CacheableDependencies;
import com.jagex.game.runetek5.sound.SoundResourceManager;
import com.jagex.game.runetek5.client.login.LoginProt;
import com.jagex.game.runetek5.network.WorldConnection;
import com.jagex.game.runetek5.buffer.CircularBuffer;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.AbstractMouseManager;
import com.jagex.game.runetek5.InterfaceDrag;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.statics.BrowserJavaScript;
import com.jagex.game.runetek5.MainLogicManager;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.SoundManager;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.core.datastruct.key.MouseOverTextY;
import com.jagex.game.runetek5.js5.request.RequestManager;
import com.jagex.game.runetek5.client.PingWorker;
import com.jagex.game.runetek5.client.input.mouse.MouseManager;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.client.web.ClientURLTools;
import com.jagex.game.runetek5.preference.impl.ToolkitOption;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!client")
public final class client extends GameShell {

	@OriginalMember(owner = "client!client", name = "main", descriptor = "([Ljava/lang/String;)V")
	public static void main(@OriginalArg(0) String[] strings) {
		try {
			if (strings.length != 6) {
				GameShell.exitWithError("Argument count");
			}
			WorldConnection.aClass255_3 = new WorldConnectionInfo();
			WorldConnection.aClass255_3.worldId = Integer.parseInt(strings[0]);
			LoginManager.CURRENT_CONNECTION_CONTEXT = new WorldConnectionInfo();
			LoginManager.CURRENT_CONNECTION_CONTEXT.worldId = Integer.parseInt(strings[1]);
			WorldConnection.aClass255_1 = new WorldConnectionInfo();
			WorldConnection.aClass255_1.worldId = Integer.parseInt(strings[2]);
			ServerEnvironment.LIVE = ModeWhere.aClass107_5;
			if (strings[3].equals("live")) {
				client.buildEnvironment = ModeWhat.aClass317_1;
			} else if (strings[3].equals("rc")) {
				client.buildEnvironment = ModeWhat.aClass317_3;
			} else if (strings[3].equals("wip")) {
				client.buildEnvironment = ModeWhat.WIP;
			} else {
				GameShell.exitWithError("modewhat");
			}
			ClientState.language = FriendList.getLanguageId(strings[4]);
			if (ClientState.language == -1) {
				if (strings[4].equals("english")) {
					ClientState.language = 0;
				} else if (strings[4].equals("german")) {
					ClientState.language = 1;
				} else {
					GameShell.exitWithError("language");
				}
			}
			GameShell.objectTag = false;
			BrowserJavaScript.javaScriptEnabled = false;
			if (strings[5].equals("game0")) {
				Game.CURRENT_GAME = Game.RUNESCAPE;
			} else if (strings[5].equals("game1")) {
				Game.CURRENT_GAME = Game.STELLAR_DAWN;
			} else if (strings[5].equals("game2")) {
				Game.CURRENT_GAME = Game.GAME3;
			} else if (strings[5].equals("game3")) {
				Game.CURRENT_GAME = Game.GAME4;
			} else {
				GameShell.exitWithError("game");
			}
			GameShell.force64Mb = false;
			GameShell.siteSettings = "";
			GameShell.machineUid = 0L;
			LoginManager.ssKey = null;
			GameShell.AFFILIATE = 0;
			GameShell.siteSettingsMember = true;
			LoginManager.isMember = true;
			GameShell.fromBilling = false;
			GameShell.worldFlags = 0;
			AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX = Game.CURRENT_GAME.id;
			GameShell.country = 0;
			GameShell.additionalInfo = null;
			@Pc(182) client c = new client();
			ActiveClient.activeClient = c;
			c.startApplication(client.buildEnvironment.getId() + 32, Game.CURRENT_GAME.domainName);
			GameShell.engineFrame.setLocation(40, 40);
		} catch (@Pc(205) Exception ex) {
			ExplicitRuntimeException.reportError(null, ex);
		}
	}

	@OriginalMember(owner = "client!client", name = "e", descriptor = "(B)V")
	private void setupJs5Connection() {
		if (Resource.remoteFileRequesterJs5.errors > client.errors) {
			TcpPacket.CURRENT_REQUEST.cycleConnectionMode();
			Js5State.pulsesBeforeJs5Reconnect = (Resource.remoteFileRequesterJs5.errors * 50 - 50) * 5;
			if (Js5State.pulsesBeforeJs5Reconnect > 3000) {
				Js5State.pulsesBeforeJs5Reconnect = 3000;
			}
			if (Resource.remoteFileRequesterJs5.errors >= 2 && Resource.remoteFileRequesterJs5.response == Js5ResponseCode.CONNECT_OUTOFDATE) {
				this.raiseErrorScreen("js5connect_outofdate");
				ClientState.mainLogicStep = MainLogicStep.STEP_ERROR;
				return;
			}
			if (Resource.remoteFileRequesterJs5.errors >= 4 && Resource.remoteFileRequesterJs5.response == -1) {
				this.raiseErrorScreen("js5crc");
				ClientState.mainLogicStep = MainLogicStep.STEP_ERROR;
				return;
			}
			if (Resource.remoteFileRequesterJs5.errors >= 4 && MainLogicManager.isLoadingStep(ClientState.mainLogicStep)) {
				if (Resource.remoteFileRequesterJs5.response == Js5ResponseCode.CONNECT_FULL1 || Resource.remoteFileRequesterJs5.response == Js5ResponseCode.CONNECT_FULL2) {
					this.raiseErrorScreen("js5connect_full");
				} else if (Resource.remoteFileRequesterJs5.response <= 0) {
					this.raiseErrorScreen("js5io");
				} else if (client.js5ProxyAddress == null) {
					this.raiseErrorScreen("js5connect");
				} else {
					this.raiseErrorScreen("js5proxy_" + client.js5ProxyAddress.trim());
				}
				ClientState.mainLogicStep = MainLogicStep.STEP_ERROR;
				return;
			}
		}
		client.errors = Resource.remoteFileRequesterJs5.errors;
		if (Js5State.pulsesBeforeJs5Reconnect > 0) {
			Js5State.pulsesBeforeJs5Reconnect--;
			return;
		}
		try {
			if (Js5State.init == Js5State.INIT) {
				Js5State.aClass240_9 = TcpPacket.CURRENT_REQUEST.openSocket(GameShell.signLink);
				Js5State.init++;
			}
			if (Js5State.init == Js5State.WAITING_CONNECTION_OPENED) {
				if (Js5State.aClass240_9.status == SignedResourceStatus.ERROR) {
					if (Js5State.aClass240_9.result != null) {
						client.js5ProxyAddress = (String) Js5State.aClass240_9.result;
					}
					this.updateJs5ConnectionStatus(1000);
					return;
				}
				if (Js5State.aClass240_9.status == SignedResourceStatus.SUCCESS) {
					Js5State.init++;
				}
			}
			if (Js5State.init == Js5State.SEND_HEADER) {
				Connection.updateConnection = new BufferedSocket((Socket) Js5State.aClass240_9.result, GameShell.signLink, 25000);
				@Pc(190) Packet packet = new Packet(5);
				packet.writeByte(LoginProt.aClass141_2.id);
				packet.writeInt(634);
				Connection.updateConnection.write(packet.buffer, 5);
				Js5State.init++;
				Js5State.js5ConnectTime = Utils.currentTimeMillis();
			}
			if (Js5State.init == Js5State.WAITING_FIRST_RESPONSE) {
				if (MainLogicManager.isLoadingStep(ClientState.mainLogicStep) || Connection.updateConnection.available() > 0) {
					@Pc(233) int response = Connection.updateConnection.read();
					if (response != 0) {
						this.updateJs5ConnectionStatus(response);
						return;
					}
					Js5State.init++;
				} else if (Utils.currentTimeMillis() - Js5State.js5ConnectTime > 30000L) {
					this.updateJs5ConnectionStatus(1001);
					return;
				}
			}
			if (Js5State.init == Js5State.WAITING_LOADING_REQUIREMENTS) {
				@Pc(279) boolean loggedOut = MainLogicManager.isLoadingStep(ClientState.mainLogicStep) || MainLogicManager.isLoginScreenStep(ClientState.mainLogicStep) || MainLogicManager.inLobby(ClientState.mainLogicStep);
				@Pc(282) LoadingRequirement[] loadingRequirements = CacheableDependencies.getAll();
				@Pc(290) Packet packet2 = new Packet(loadingRequirements.length * 4);
				Connection.updateConnection.readFully(packet2.buffer, 0, packet2.buffer.length);
				for (@Pc(301) int i = 0; i < loadingRequirements.length; i++) {
					loadingRequirements[i].setSize(packet2.readInt());
				}
				Resource.remoteFileRequesterJs5.init(!loggedOut, Connection.updateConnection);
				Js5State.init = Js5State.INIT;
				Js5State.aClass240_9 = null;
				Connection.updateConnection = null;
			}
		} catch (@Pc(345) IOException ex) {
			this.updateJs5ConnectionStatus(1002);
		}
	}

	@OriginalMember(owner = "client!client", name = "e", descriptor = "(I)V")
	@Override
	public void maininit() {
		if (GameShell.force64Mb) {
			GameShell.maxMemory = 64;
		}
		@Pc(14) Frame frame = new Frame("Jagex");
		frame.pack();
		frame.dispose();
		GameCanvasLayout.adjustActiveWindow();
		LocalRequester.aClass5_3 = new LocalRequester(GameShell.signLink);
		Resource.remoteFileRequesterJs5 = new Js5WorkerThread();
		CircularBuffer.initialize(new int[] { 20, 260 }, new int[] { 1000, 100 });
		if (ServerEnvironment.LIVE != ModeWhere.WTRC) {
			PlayerAppearance.TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA = new byte[50][];
		}
		GamePreferences.uid = GamePreferences.loadPreferences();
		if (ServerEnvironment.LIVE == ModeWhere.WTRC) {
			WorldConnection.aClass255_3.host = this.getCodeBase().getHost();
		} else if (ModeWhere.isHostedTestEnvironment(ServerEnvironment.LIVE)) {
			WorldConnection.aClass255_3.host = this.getCodeBase().getHost();
			WorldConnection.aClass255_3.secondaryPort = WorldConnection.aClass255_3.worldId + Ports.SECONDARY_OFFSET;
			LoginManager.CURRENT_CONNECTION_CONTEXT.secondaryPort = LoginManager.CURRENT_CONNECTION_CONTEXT.worldId + Ports.SECONDARY_OFFSET;
			WorldConnection.aClass255_3.primaryPort = WorldConnection.aClass255_3.worldId + Ports.PRIMARY_OFFSET;
			WorldConnection.aClass255_1.secondaryPort = WorldConnection.aClass255_1.worldId + Ports.SECONDARY_OFFSET;
			LoginManager.CURRENT_CONNECTION_CONTEXT.primaryPort = LoginManager.CURRENT_CONNECTION_CONTEXT.worldId + Ports.PRIMARY_OFFSET;
			WorldConnection.aClass255_1.primaryPort = WorldConnection.aClass255_1.worldId + 50000;
		} else if (ModeWhere.aClass107_5 == ServerEnvironment.LIVE) {
			WorldConnection.aClass255_3.host = "127.0.0.1";
			LoginManager.CURRENT_CONNECTION_CONTEXT.host = "127.0.0.1";
			WorldConnection.aClass255_3.secondaryPort = WorldConnection.aClass255_3.worldId + 40000;
			WorldConnection.aClass255_1.host = "127.0.0.1";
			LoginManager.CURRENT_CONNECTION_CONTEXT.secondaryPort = LoginManager.CURRENT_CONNECTION_CONTEXT.worldId + 40000;
			WorldConnection.aClass255_1.secondaryPort = WorldConnection.aClass255_1.worldId + 40000;
			WorldConnection.aClass255_3.primaryPort = WorldConnection.aClass255_3.worldId + 50000;
			LoginManager.CURRENT_CONNECTION_CONTEXT.primaryPort = LoginManager.CURRENT_CONNECTION_CONTEXT.worldId + 50000;
			WorldConnection.aClass255_1.primaryPort = WorldConnection.aClass255_1.worldId + 50000;
		}
		// Not part of the original client: a host and port given to the launcher replace the ones
		// worked out above, for the world, the lobby and the demo world alike.
		if (ClientFeatures.ADDRESS != null) {
			WorldConnection.aClass255_3.host = ClientFeatures.ADDRESS;
			LoginManager.CURRENT_CONNECTION_CONTEXT.host = ClientFeatures.ADDRESS;
			WorldConnection.aClass255_1.host = ClientFeatures.ADDRESS;
		}
		if (ClientFeatures.PORT > 0) {
			WorldConnection.aClass255_3.primaryPort = WorldConnection.aClass255_3.secondaryPort = ClientFeatures.PORT;
			LoginManager.CURRENT_CONNECTION_CONTEXT.primaryPort = LoginManager.CURRENT_CONNECTION_CONTEXT.secondaryPort = ClientFeatures.PORT;
			WorldConnection.aClass255_1.primaryPort = WorldConnection.aClass255_1.secondaryPort = ClientFeatures.PORT;
		}
		TypeLists.recolorPalette = ObjectType.locRecolorPalette = NPCType.npcRecolorPalette = ItemType.recolorPalette = new short[256];
		if (Game.CURRENT_GAME == Game.RUNESCAPE) {
			ViewportMiniMenuPicking.showAdvert = false;
		}
		if (Game.STELLAR_DAWN == Game.CURRENT_GAME) {
			MiniMenu.shiftClickEnabled = true;
			PlayerAppearance.recolorPaletteSrc = PlayerAppearance.STELLAR_DAWN_RECOLOR_SRC;
			EnvironmentManager.defaultSunColour = 16777215;
			PlayerAppearance.recolorPaletteDst = PlayerAppearance.STELLAR_DAWN_RECOLOR_PALETTE_DST;
			EnvironmentManager.defaultFogColour = 0;
		} else if (Game.CURRENT_GAME == Game.GAME4) {
			PlayerAppearance.recolorPaletteSrc = PlayerAppearance.GAME4_RECOLOR_PALETTE_SRC;
			PlayerAppearance.recolorPaletteDst = PlayerAppearance.GAME4_RECOLOR_PALETTE_DST;
		} else {
			PlayerAppearance.recolorPaletteSrc = PlayerAppearance.DEFAULT_RECOLOR_PALETTE_SRC;
			PlayerAppearance.recolorPaletteDst = PlayerAppearance.DEFAULT_RECOLOR_PALETTE_DST;
		}
		TcpPacket.CURRENT_REQUEST = WorldConnection.aClass255_3;
		try {
			Console.clipboard = ActiveClient.activeClient.getToolkit().getSystemClipboard();
		} catch (@Pc(225) Exception ex) {
		}
		KeyRecorder.keyRecorder = CanvasKeyRecorder.create(GameShell.gameCanvas);
		MouseRecord.mouseRecorder = AbstractMouseManager.create(GameShell.gameCanvas);
		try {
			if (GameShell.signLink.cacheDat != null) {
				Resource.cacheFileData = new FileOnDisk(GameShell.signLink.cacheDat, 5200, 0);
				for (@Pc(247) int i = 0; i < 37; i++) {
					Resource.cacheFileIndices[i] = new FileOnDisk(GameShell.signLink.cacheIndex[i], 6000, 0);
				}
				Resource.aClass184_1 = new FileOnDisk(GameShell.signLink.cacheIndex255, 6000, 0);
				Resource.referenceTableDecompressor = new Decompressor(Js5Archive.ARCHIVESET, Resource.cacheFileData, Resource.aClass184_1, 500000);
				GameShell.aClass184_2 = new FileOnDisk(GameShell.signLink.randomDat, 24, 0);
				GameShell.signLink.cacheIndex255 = null;
				GameShell.signLink.cacheDat = null;
				GameShell.signLink.cacheIndex = null;
				GameShell.signLink.randomDat = null;
			}
		} catch (@Pc(303) IOException ex2) {
			Resource.cacheFileData = null;
			Resource.aClass184_1 = null;
			GameShell.aClass184_2 = null;
			Resource.referenceTableDecompressor = null;
		}
		if (ServerEnvironment.LIVE != ModeWhere.WTRC) {
			Culling.wallCullingEnabled = true;
		}
		AwtLoadingBarPainter.loadingText = LocalizedText.aClass351_14.get(ClientState.language);
	}

	@OriginalMember(owner = "client!client", name = "c", descriptor = "(B)V")
	@Override
	public void cleanup() {
	}

	@OriginalMember(owner = "client!client", name = "f", descriptor = "(B)V")
	private void pulseFileRequester() {
		@Pc(11) boolean connected = Resource.remoteFileRequesterJs5.loop();
		if (!connected) {
			this.setupJs5Connection();
		}
	}

	@OriginalMember(owner = "client!client", name = "a", descriptor = "(IB)V")
	private void updateJs5ConnectionStatus(@OriginalArg(0) int response) {
		Resource.remoteFileRequesterJs5.errors++;
		Js5State.aClass240_9 = null;
		Resource.remoteFileRequesterJs5.response = response;
		Js5State.init = 0;
		Connection.updateConnection = null;
	}

	@OriginalMember(owner = "client!client", name = "a", descriptor = "(I)V")
	@Override
	public void mainquit() {
		if (VarcTypeList.clientIsActive) {
			VarcTypeList.savePersistentVarcs();
		}
		LoadingScreenThreadStop.killLoginscreenThread();
		if (ClientState.activeToolkit != null) {
			ClientState.activeToolkit.free();
		}
		if (WindowMode.fullScreenFrame != null) {
			InterfaceManager.disposeFrame(WindowMode.fullScreenFrame, GameShell.signLink);
			WindowMode.fullScreenFrame = null;
		}
		if (Connection.connection != null) {
			Connection.connection.close();
			Connection.connection = null;
		}
		SoundEffectPlayer.shutdownPlayers();
		Resource.remoteFileRequesterJs5.quit();
		LocalRequester.aClass5_3.close();
		if (PingWorker.aClass262_1 != null) {
			PingWorker.aClass262_1.close();
			PingWorker.aClass262_1 = null;
		}
		try {
			Resource.cacheFileData.close();
			for (@Pc(55) int i = 0; i < Js5Archive.ID_LIMIT; i++) {
				Resource.cacheFileIndices[i].close();
			}
			Resource.aClass184_1.close();
			GameShell.aClass184_2.close();
			Console.shutdown();
		} catch (@Pc(76) Exception ex) {
		}
	}

	@OriginalMember(owner = "client!client", name = "d", descriptor = "(B)V")
	@Override
	public void onPulse() {
		// Not part of the original client: the smoke test (client.test=smoke) runs each logic tick.
		SmokeTest.tick();
		if (GamePreferences.uid.toolkit.getValue() != 2) {
			this.pulse();
			return;
		}
		try {
			this.pulse();
		} catch (@Pc(25) ThreadDeath threadDeath) {
			throw threadDeath;
		} catch (@Pc(28) Throwable ex) {
			ExplicitRuntimeException.reportError(ex.getMessage() + " (Recovered) " + this.getDebugString(), ex);
			ToolkitOption.renderWithCpu = true;
			ToolkitSwitch.loadAssets(false, 0);
		}
	}

	@OriginalMember(owner = "client!client", name = "init", descriptor = "()V")
	@Override
	public void init() {
		if (!this.isHostnameValid()) {
			return;
		}
		WorldConnection.aClass255_3 = new WorldConnectionInfo();
		WorldConnection.aClass255_3.worldId = Integer.parseInt(this.getParameter("worldid"));
		LoginManager.CURRENT_CONNECTION_CONTEXT = new WorldConnectionInfo();
		LoginManager.CURRENT_CONNECTION_CONTEXT.worldId = Integer.parseInt(this.getParameter("lobbyid"));
		LoginManager.CURRENT_CONNECTION_CONTEXT.host = this.getParameter("lobbyaddress");
		WorldConnection.aClass255_1 = new WorldConnectionInfo();
		WorldConnection.aClass255_1.worldId = Integer.parseInt(this.getParameter("demoid"));
		WorldConnection.aClass255_1.host = this.getParameter("demoaddress");
		ServerEnvironment.LIVE = ModeWhere.forId(Integer.parseInt(this.getParameter("modewhere")));
		if (ModeWhere.aClass107_5 == ServerEnvironment.LIVE) {
			ServerEnvironment.LIVE = ModeWhere.aClass107_6;
		} else if (!ModeWhere.isHostedTestEnvironment(ServerEnvironment.LIVE) && ModeWhere.WTRC != ServerEnvironment.LIVE) {
			ServerEnvironment.LIVE = ModeWhere.WTRC;
		}
		client.buildEnvironment = ModeWhat.forId(Integer.parseInt(this.getParameter("modewhat")));
		if (client.buildEnvironment != ModeWhat.WIP && client.buildEnvironment != ModeWhat.aClass317_3 && ModeWhat.aClass317_1 != client.buildEnvironment) {
			client.buildEnvironment = ModeWhat.aClass317_1;
		}
		try {
			ClientState.language = Integer.parseInt(this.getParameter("lang"));
		} catch (@Pc(98) Exception ex) {
			ClientState.language = 0;
		}
		@Pc(104) String string = this.getParameter("objecttag");
		if (string != null && string.equals("1")) {
			GameShell.objectTag = true;
		} else {
			GameShell.objectTag = false;
		}
		@Pc(120) String string2 = this.getParameter("js");
		if (string2 != null && string2.equals("1")) {
			BrowserJavaScript.javaScriptEnabled = true;
		} else {
			BrowserJavaScript.javaScriptEnabled = false;
		}
		@Pc(136) String string3 = this.getParameter("advert");
		if (string3 != null && string3.equals("1")) {
			GameShell.advertEnabled = true;
		} else {
			GameShell.advertEnabled = false;
		}
		@Pc(152) String string4 = this.getParameter("game");
		if (string4 != null) {
			if (string4.equals("0")) {
				Game.CURRENT_GAME = Game.RUNESCAPE;
			} else if (string4.equals("1")) {
				Game.CURRENT_GAME = Game.STELLAR_DAWN;
			} else if (string4.equals("2")) {
				Game.CURRENT_GAME = Game.GAME3;
			} else if (string4.equals("3")) {
				Game.CURRENT_GAME = Game.GAME4;
			}
		}
		try {
			GameShell.AFFILIATE = Integer.parseInt(this.getParameter("affid"));
		} catch (@Pc(191) Exception ex2) {
			GameShell.AFFILIATE = 0;
		}
		GameShell.quitUrl = this.getParameter("quiturl");
		GameShell.siteSettings = this.getParameter("settings");
		if (GameShell.siteSettings == null) {
			GameShell.siteSettings = "";
		}
		LobbyManager.isUnder13 = "1".equals(this.getParameter("under"));
		@Pc(215) String string5 = this.getParameter("country");
		if (string5 != null) {
			try {
				GameShell.country = Integer.parseInt(string5);
			} catch (@Pc(222) Exception ex3) {
				GameShell.country = 0;
			}
		}
		AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX = Integer.parseInt(this.getParameter("colourid"));
		if (AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX < 0 || AwtLoadingScreen.DEBUG_LOAD_COLOR.length <= AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX) {
			AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX = 0;
		}
		if (Integer.parseInt(this.getParameter("sitesettings_member")) == 1) {
			GameShell.siteSettingsMember = true;
			LoginManager.isMember = true;
		}
		@Pc(252) String string6 = this.getParameter("frombilling");
		if (string6 != null && string6.equals("true")) {
			GameShell.fromBilling = true;
		}
		LoginManager.ssKey = this.getParameter("sskey");
		if (LoginManager.ssKey != null && LoginManager.ssKey.length() < 2) {
			LoginManager.ssKey = null;
		}
		@Pc(276) String string7 = this.getParameter("force64mb");
		if (string7 != null && string7.equals("true")) {
			GameShell.force64Mb = true;
		}
		@Pc(288) String string8 = this.getParameter("worldflags");
		if (string8 != null) {
			try {
				GameShell.worldFlags = Integer.parseInt(string8);
			} catch (@Pc(295) Exception ex4) {
			}
		}
		@Pc(300) String string9 = this.getParameter("userFlow");
		if (string9 != null) {
			try {
				GameShell.machineUid = Long.parseLong(string9);
			} catch (@Pc(307) NumberFormatException ex5) {
			}
		}
		GameShell.additionalInfo = this.getParameter("additionalInfo");
		if (GameShell.additionalInfo != null && GameShell.additionalInfo.length() > 50) {
			GameShell.additionalInfo = null;
		}
		if (Game.RUNESCAPE == Game.CURRENT_GAME) {
			MessageBox.paddingTop = 503;
			GameShell.GAME_WIDTH = 765;
		} else if (Game.CURRENT_GAME == Game.STELLAR_DAWN) {
			GameShell.GAME_WIDTH = 640;
			MessageBox.paddingTop = 480;
		}
		ActiveClient.activeClient = this;
		this.startApplet(GameShell.GAME_WIDTH, MessageBox.paddingTop, client.buildEnvironment.getId() + 32, Game.CURRENT_GAME.domainName);
	}

	@OriginalMember(owner = "client!client", name = "f", descriptor = "(I)V")
	@Override
	public void mainRedraw() {
		if (GamePreferences.uid.toolkit.getValue() != 2) {
			this.redraw();
			// Not part of the original client: the smoke test (client.test=smoke) checks each drawn frame.
			SmokeTest.frame();
			return;
		}
		try {
			this.redraw();
		} catch (@Pc(26) ThreadDeath threadDeath) {
			throw threadDeath;
		} catch (@Pc(29) Throwable ex) {
			ExplicitRuntimeException.reportError(ex.getMessage() + " (Recovered) " + this.getDebugString(), ex);
			ToolkitOption.renderWithCpu = true;
			ToolkitSwitch.loadAssets(false, 0);
		}
		// Not part of the original client: the smoke test (client.test=smoke) checks each drawn frame.
		SmokeTest.frame();
	}

	@OriginalMember(owner = "client!client", name = "g", descriptor = "(B)V")
	private void processLobbyTick() {
		@Pc(23) int local23;
		if (ClientState.mainLogicStep == MainLogicStep.STEP_LOBBY_SCREEN && LoginManager.loginStep == LoginStep.IDLE) {
			if (client.rebootTimer > 1) {
				InterfaceManager.lastMiscUpdateTick = ClientState.serverTick;
				client.rebootTimer--;
			}
			if (!MiniMenu.isOpen) {
				MiniMenuReset.resetEntries();
			}
			for (int packetCount = 0; packetCount < 100 && GuardedPacketDecode.decodePacketFromStream(); packetCount++) {
			}
		}
		ClientState.animatingStep++;
		InterfaceComponentRenderer.setChatDisplay(null, -1, -1);
		MouseOverTextY.setMouseOverText(-1, null, -1);
		InterfaceInputTick.processRootInterfaceInput();
		ClientState.serverTick++;
		for (int npcIndex = 0; npcIndex < NPCList.npcCount; npcIndex++) {
			@Pc(67) NPCEntity npcEntity = NPCList.NPC_ARRAY[npcIndex].npc;
			if (npcEntity != null) {
				@Pc(73) byte walkingFlags = npcEntity.npcType.walkingFlags;
				if ((walkingFlags & 0x1) != 0) {
					@Pc(81) int size = npcEntity.getSize();
					@Pc(105) int local105;
					if ((walkingFlags & 0x2) != 0 && npcEntity.pathPointer == 0 && Math.random() * 1000.0D < 10.0D) {
						int deltaX = (int) Math.round(Math.random() * 10.0D - 5.0D);
						@Pc(113) int deltaY = (int) Math.round(Math.random() * 10.0D - 5.0D);
						if (deltaX != 0 || deltaY != 0) {
							@Pc(131) int destX = npcEntity.movementBaseX[0] + deltaX;
							if (destX < 0) {
								destX = 0;
							} else if (MapRegion.sceneSizeX - size - 1 < destX) {
								destX = MapRegion.sceneSizeX - size - 1;
							}
							@Pc(163) int destY = deltaY + npcEntity.movementBaseY[0];
							if (destY < 0) {
								destY = 0;
							} else if (destY > MapRegion.sceneSizeY - size - 1) {
								destY = MapRegion.sceneSizeY - size - 1;
							}
							@Pc(214) int stepCount = PathFinder.findRoute(destX, size, size, Camera.WALKING_STEPS_Y, true, -1, npcEntity.movementBaseX[0], npcEntity.movementBaseY[0], PathFinder.WALKING_STEPS_X, destY, MapRegion.aClass104Array3[npcEntity.level], size, 0, 0);
							if (stepCount > 0) {
								if (stepCount > 9) {
									stepCount = 9;
								}
								for (@Pc(228) int i = 0; i < stepCount; i++) {
									npcEntity.movementBaseX[i] = PathFinder.WALKING_STEPS_X[stepCount - i - 1];
									npcEntity.movementBaseY[i] = Camera.WALKING_STEPS_Y[stepCount - i - 1];
									npcEntity.movementQueueSpeed[i] = MoveSpeed.WALK;
								}
								npcEntity.pathPointer = stepCount;
							}
						}
					}
					EntityUpdating.updatePosition(true, npcEntity);
					int rotation = PathingEntity.turnTo(npcEntity);
					PathingEntity.updateEquipmentRotation(npcEntity);
					PathingEntity.animateMovement(EntityUpdating.currentMovementType, rotation, npcEntity, EntityUpdating.turnDirectionIndex);
					PlayerUpdate.animateMovement(npcEntity, EntityUpdating.currentMovementType);
					PathingEntity.updateAnimations(npcEntity);
				}
			}
		}
		if (LoginManager.loginStep == LoginStep.IDLE && LobbyManager.accountCreationStep == 0) {
			if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWPLAYER) {
				Camera.calculateCameraPosition();
			} else {
				Camera.calculateCutsceneCameraPosition();
			}
			if (Camera.cameraPositionAbsoluteX >> 9 < 14 || Camera.cameraPositionAbsoluteX >> 9 >= MapRegion.sceneSizeX - 14 || Camera.cameraPositionAbsoluteY >> 9 < 14 || Camera.cameraPositionAbsoluteY >> 9 >= MapRegion.sceneSizeY - 14) {
				BackgroundMapRegionSetup.setupBackgroundMapRegions();
			}
		}
		while (true) {
			@Pc(361) HookRequest hookRequest;
			@Pc(366) Component component;
			@Pc(379) Component component2;
			do {
				hookRequest = (HookRequest) TickHookRequests.TICK_HOOK_REQUESTS.removeFirst();
				if (hookRequest == null) {
					while (true) {
						do {
							hookRequest = (HookRequest) TickHookRequests.MOUSE_HOOK_REQUESTS.removeFirst();
							if (hookRequest == null) {
								while (true) {
									do {
										hookRequest = (HookRequest) InterfaceManager.PENDING_HOOK_REQUESTS.removeFirst();
										if (hookRequest == null) {
											if (InterfaceDrag.component != null) {
												ComponentDragTick.processDrag();
											}
											if (ClientState.clientCycle % 1500 == 0) {
												ToolbarRefresh.refreshToolbar();
											}
											if (ClientState.mainLogicStep == MainLogicStep.STEP_LOBBY_SCREEN && LoginManager.loginStep == LoginStep.IDLE) {
												MiniMenuInputTick.processMenuInput();
											}
											WorldConnection.pingWorlds();
											if (VarcTypeList.clientIsActive && Utils.currentTimeMillis() - TimeUtils.MILLISECONDS_PER_MINUTE > VarcTypeList.exitTime) {
												VarcTypeList.savePersistentVarcs();
											}
											for (@Pc(542) LoginStateUpdate friendNotification = (LoginStateUpdate) client.PLAYER_LOGINSTATE_UPDATES.getFirst(); friendNotification != null; friendNotification = (LoginStateUpdate) client.PLAYER_LOGINSTATE_UPDATES.getNext()) {
												if (Utils.currentTimeMillis() / 1000L - 5L > (long) friendNotification.time) {
													if (friendNotification.actionType > 0) {
														ChatLineWriter.writeMessage("", 5, 0, friendNotification.name + LocalizedText.HAS_LOGGED_IN.get(ClientState.language), "", "");
													}
													if (friendNotification.actionType == 0) {
														ChatLineWriter.writeMessage("", 5, 0, friendNotification.name + LocalizedText.HAS_LOGGED_OUT.get(ClientState.language), "", "");
													}
													friendNotification.unlinkNode();
												}
											}
											if (ClientState.mainLogicStep == MainLogicStep.STEP_LOBBY_SCREEN && LoginManager.loginStep == LoginStep.IDLE) {
												if (Connection.connection == null) {
													ClientLogout.logout(false);
													return;
												}
												BufferedConnectionContext.lastFlush++;
												if (BufferedConnectionContext.lastFlush > 50) {
													@Pc(650) TcpPacket pingPacket = TcpPacket.createPacket(ClientProt.PING, TcpPacket.isaac);
													BufferedConnectionContext.queuePacket(pingPacket);
												}
												try {
													BufferedConnectionContext.flush();
													return;
												} catch (@Pc(657) IOException ex) {
													ClientLogout.logout(false);
													return;
												}
											}
											return;
										}
										component = hookRequest.source;
										if (component.slotId < 0) {
											break;
										}
										component2 = Interface.getComponent(component.parentId);
									} while (component2 == null || component2.slotChildren == null || component2.slotChildren.length <= component.slotId || component != component2.slotChildren[component.slotId]);
									CS2Interpreter.executeHookInner(hookRequest);
								}
							}
							component = hookRequest.source;
							if (component.slotId < 0) {
								break;
							}
							component2 = Interface.getComponent(component.parentId);
						} while (component2 == null || component2.slotChildren == null || component2.slotChildren.length <= component.slotId || component != component2.slotChildren[component.slotId]);
						CS2Interpreter.executeHookInner(hookRequest);
					}
				}
				component = hookRequest.source;
				if (component.slotId < 0) {
					break;
				}
				component2 = Interface.getComponent(component.parentId);
			} while (component2 == null || component2.slotChildren == null || component.slotId >= component2.slotChildren.length || component != component2.slotChildren[component.slotId]);
			CS2Interpreter.executeHookInner(hookRequest);
		}
	}

	@OriginalMember(owner = "client!client", name = "l", descriptor = "(I)V")
	private void pulse() {
		if (ClientState.mainLogicStep == MainLogicStep.STEP_ERROR) {
			return;
		}
		ClientState.clientCycle++;
		if (ClientState.clientCycle % 1000 == 1) {
			@Pc(22) GregorianCalendar gregorianCalendar = new GregorianCalendar();
			MiniMenu.randomSeed = gregorianCalendar.get(11) * 600 + gregorianCalendar.get(12) * 10 + gregorianCalendar.get(13) / 6;
			MiniMenu.RANDOMIZER.setSeed((long) MiniMenu.randomSeed);
		}
		if (ClientState.clientCycle % 50 == 0) {
			OutboundByteCounter.outboundBytesPerSecond = OutboundByteCounter.bytesWritten;
			OutboundByteCounter.bytesWritten = 0;
			OutboundByteCounter.inboundBytesPerSecond = OutboundByteCounter.bytesRead;
			OutboundByteCounter.bytesRead = 0;
		}
		this.pulseFileRequester();
		if (RequestManager.requestManager != null) {
			RequestManager.requestManager.pulse();
		}
		SoundManager.updateSound();
		VideoStreamTick.processVorbisStreams();
		KeyRecorder.keyRecorder.prepareAwaitingKeys();
		MouseRecord.mouseRecorder.mainLogic();
		if (ClientState.activeToolkit != null) {
			ClientState.activeToolkit.tick((int) Utils.currentTimeMillis());
		}
		ClientURLTools.processPendingUrlOpen();
		KeyRecorder.keyCachePointerPoss = 0;
		client.maximumHeldKeys = 0;
		for (@Pc(91) KeyRecord keyLog = KeyRecorder.keyRecorder.removeFirstRecorded(); keyLog != null; keyLog = KeyRecorder.keyRecorder.removeFirstRecorded()) {
			@Pc(97) int type = keyLog.getType();
			if (type == KeyLogConstants.TYPE_HELD || type == KeyLogConstants.TYPE_TYPED) {
				@Pc(126) char keyChar = keyLog.getKeyChar();
				if (MainLogicManager.isPastStartup() && (keyChar == '`' || keyChar == '§' || keyChar == '²')) {
					if (Console.isOpen()) {
						Console.close();
					} else {
						Console.open();
					}
				} else if (client.maximumHeldKeys < 128) {
					client.KEY_RECORDS[client.maximumHeldKeys] = keyLog;
					client.maximumHeldKeys++;
				}
			} else if (type == 0 && KeyRecorder.keyCachePointerPoss < 75) {
				KeyRecorder.KEYS_PRESSED[KeyRecorder.keyCachePointerPoss] = keyLog;
				KeyRecorder.keyCachePointerPoss++;
			}
		}
		client.mouseWheelDelta = 0;
		if (ClientFeatures.ZOOM) {
			// Not part of the original client: the zoom keeps its own copy of the wheel, which the console clears.
			ClientFeatures.wheelDelta = 0;
		}
		for (@Pc(176) MouseRecord mouseEvent = MouseRecord.mouseRecorder.nextSubmission(); mouseEvent != null; mouseEvent = MouseRecord.mouseRecorder.nextSubmission()) {
			@Pc(182) int clickType = mouseEvent.getClickType();
			if (clickType == -1) {
				PingRequester.CURRENT_REQUEST.addLast(mouseEvent);
			} else if (clickType == MouseLog.TYPE_SCROLL) {
				client.mouseWheelDelta += mouseEvent.getMeta();
				if (ClientFeatures.ZOOM) {
					ClientFeatures.wheelDelta = mouseEvent.getMeta();
				}
			} else if (MouseLog.isMousePressType(clickType)) {
				MouseManager.MOUSE_MOVEMENTS.addLast(mouseEvent);
				if (MouseManager.MOUSE_MOVEMENTS.size() > 10) {
					MouseManager.MOUSE_MOVEMENTS.removeFirst();
				}
			}
		}
		if (Console.isOpen()) {
			ConsoleTick.processConsoleInput();
		}
		if (MainLogicManager.isLoadingStep(ClientState.mainLogicStep)) {
			LoadingStageTick.cycle();
			GameShell.resetDrawCache();
		} else if (MainLogicManager.isMapBuildStep(ClientState.mainLogicStep)) {
			MapBuildStep.loadRegion();
		}
		if (MainLogicManager.isLoginScreenStep(ClientState.mainLogicStep) && !MainLogicManager.isMapBuildStep(ClientState.mainLogicStep)) {
			this.processLobbyTick();
			LobbyManager.processAccountCreation();
			Static701.method5595();
		} else if (MainLogicManager.inLobby(ClientState.mainLogicStep) && !MainLogicManager.isMapBuildStep(ClientState.mainLogicStep)) {
			this.processLobbyTick();
			Static701.method5595();
		} else if (ClientState.mainLogicStep == MainLogicStep.STEP_LOGGING_IN_FROM_GAME_TO_LOBBY) {
			Static701.method5595();
		} else if (MainLogicManager.isInGameStep(ClientState.mainLogicStep) && !MainLogicManager.isMapBuildStep(ClientState.mainLogicStep)) {
			GameTick.processGameTick();
		} else if (ClientState.mainLogicStep == MainLogicStep.STEP_RECONNECTING) {
			Static701.method5595();
			if (LoginManager.worldLoginResponse != -3 && LoginManager.worldLoginResponse != LoginResponseCode.OK && LoginManager.worldLoginResponse != LoginResponseCode.RECONNECT_OK) {
				ClientLogout.logout(false);
			}
		}
		ItemSpriteBatchRender.renderItems(ClientState.activeToolkit);
		MouseManager.MOUSE_MOVEMENTS.removeFirst();
	}

	@OriginalMember(owner = "client!client", name = "b", descriptor = "(B)V")
	@Override
	public synchronized void addcanvas() {
		if (GameShell.applet != null && GameShell.gameCanvas == null && !GameShell.signLink.microsoftJava) {
			try {
				@Pc(18) Class class2 = GameShell.applet.getClass();
				@Pc(22) Field field = class2.getDeclaredField("canvas");
				GameShell.gameCanvas = (Canvas) field.get(GameShell.applet);
				field.set(GameShell.applet, null);
				if (GameShell.gameCanvas != null) {
					return;
				}
			} catch (@Pc(35) Exception ex) {
			}
		}
		super.addcanvas();
	}

	@OriginalMember(owner = "client!client", name = "b", descriptor = "(Z)V")
	private void redraw() {
		if (ClientState.mainLogicStep == MainLogicStep.STEP_ERROR) {
			return;
		}
		@Pc(17) long elapsedMillis = GameShell.getCurrentCycleNanos() / 1000000L - LastRedrawTime.lastRedrawTimeMillis;
		LastRedrawTime.lastRedrawTimeMillis = GameShell.getCurrentCycleNanos() / 1000000L;
		@Pc(29) boolean songLoaded = SoundResourceManager.loadPendingSong();
		if (songLoaded && SoundEffectPlayer.soundJinglePlaying && SoundEffectPlayer.jinglePlayer != null) {
			SoundEffectPlayer.jinglePlayer.start();
		}
		if (MainLogicManager.isLoginLobbyOrGameScreen(ClientState.mainLogicStep)) {
			if (WindowMode.nextDimensionUpdateCheck != 0L && Utils.currentTimeMillis() > WindowMode.nextDimensionUpdateCheck) {
				WindowModeRequest.requestWindowMode(WindowMode.getWindowMode(), WindowMode.UNCHANGED_WINDOW_WIDTH, false, WindowMode.UNCHANGED_WINDOW_HEIGHT);
			} else if (!ClientState.activeToolkit.supportsFullscreen() && GameShell.fullWindowRepainted) {
				CanvasReplace.exitFullscreen();
			}
		}
		@Pc(89) int local89;
		@Pc(93) int local93;
		if (WindowMode.fullScreenFrame == null) {
			@Pc(77) Container container;
			if (GameShell.engineFrame != null) {
				container = GameShell.engineFrame;
			} else if (GameShell.applet == null) {
				container = GameShell.anApplet_Sub1_1;
			} else {
				container = GameShell.applet;
			}
			int windowWidth = container.getSize().width;
			int windowHeight = container.getSize().height;
			if (container == GameShell.engineFrame) {
				@Pc(99) Insets insets = GameShell.engineFrame.getInsets();
				windowWidth -= insets.left + insets.right;
				windowHeight -= insets.top + insets.bottom;
				// Not part of the original client: with client.scale, the window is measured in game pixels.
				windowWidth = ClientFeatures.unscale(windowWidth);
				windowHeight = ClientFeatures.unscale(windowHeight);
			}
			if (WindowMode.resizableScreenWidth != windowWidth || windowHeight != WindowMode.resizableScreenHeight || WindowMode.needsResize) {
				if (ClientState.activeToolkit == null || ClientState.activeToolkit.forceFixedScreenmode()) {
					GameCanvasLayout.adjustActiveWindow();
				} else {
					WindowMode.resizableScreenHeight = windowHeight;
					WindowMode.resizableScreenWidth = windowWidth;
				}
				WindowMode.nextDimensionUpdateCheck = Utils.currentTimeMillis() + 500L;
				WindowMode.needsResize = false;
			}
		}
		if (WindowMode.fullScreenFrame != null && !GameShell.appletHasFocus && MainLogicManager.isLoginLobbyOrGameScreen(ClientState.mainLogicStep)) {
			WindowModeRequest.requestWindowMode(WindowModeType.fromId(GamePreferences.uid.fixedScreen.getValue()), -1, false, -1);
		}
		@Pc(167) boolean drawBackground = false;
		if (GameShell.fullRedrawRequested) {
			drawBackground = true;
			GameShell.fullRedrawRequested = false;
		}
		if (drawBackground) {
			GameShell.clearCanvasBackground();
		}
		if (ClientState.activeToolkit != null && ClientState.activeToolkit.supportsFullscreen() || WindowMode.getWindowMode() != WindowModeType.FIXED) {
			InterfaceRectangles.invalidateAllRectangles();
		}
		if (MainLogicManager.isLoadingStep(ClientState.mainLogicStep)) {
			LoadingScreenRepaint.initialize(drawBackground);
		} else if (MainLogicManager.isLoginScreenShown(ClientState.mainLogicStep)) {
			MenuScreenFrameRender.drawInterfaceFrame();
		} else if (MainLogicManager.isLobbyScreenShown(ClientState.mainLogicStep)) {
			MenuScreenFrameRender.drawInterfaceFrame();
		} else if (MainLogicManager.isMapBuildStep(ClientState.mainLogicStep)) {
			if (MapBuildStep.mapLoadingStage == 1) {
				if (MapBuildStep.pendingMapFileCount > MapBuildStep.totalMapCount) {
					MapBuildStep.totalMapCount = MapBuildStep.pendingMapFileCount;
				}
				int loadPercent = (MapBuildStep.totalMapCount - MapBuildStep.pendingMapFileCount) * 50 / MapBuildStep.totalMapCount;
				MessageBox.draw(true, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_14.get(ClientState.language) + "<br>(" + loadPercent + "%)", ClientState.activeToolkit);
			} else if (MapBuildStep.mapLoadingStage == 2) {
				if (MapBuildStep.maxUnloadedLocCount < UnloadedLocCount.unloadedLocCount) {
					MapBuildStep.maxUnloadedLocCount = UnloadedLocCount.unloadedLocCount;
				}
				int loadPercent = (MapBuildStep.maxUnloadedLocCount - UnloadedLocCount.unloadedLocCount) * 50 / MapBuildStep.maxUnloadedLocCount + 50;
				MessageBox.draw(true, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_14.get(ClientState.language) + "<br>(" + loadPercent + "%)", ClientState.activeToolkit);
			} else {
				MessageBox.draw(true, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_14.get(ClientState.language), ClientState.activeToolkit);
			}
		} else if (ClientState.mainLogicStep == MainLogicStep.STEP_GAME_SCREEN) {
			GameScreenFrameRender.drawGameFrame(elapsedMillis);
		} else if (ClientState.mainLogicStep == MainLogicStep.STEP_RECONNECTING) {
			MessageBox.draw(false, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_16.get(ClientState.language) + "<br>" + LocalizedText.aClass351_17.get(ClientState.language), ClientState.activeToolkit);
		}
		if (InterfaceManager.interfaceDebugMode == 3) {
			for (int componentIndex = 0; componentIndex < Camera.componentBackgroundCount; componentIndex++) {
				@Pc(360) Rectangle rectangle = InterfaceRectangles.INTERFACE_BG_COMPONENTS[componentIndex];
				if (InterfaceRectangles.PREV_COMPONENT_ON_TOP[componentIndex]) {
					ClientState.activeToolkit.drawOpaqueRectangle(rectangle.width, rectangle.y, -65281, rectangle.x, rectangle.height);
				} else if (InterfaceRectangles.RECTANGLE_NEEDS_FLIP[componentIndex]) {
					ClientState.activeToolkit.drawOpaqueRectangle(rectangle.width, rectangle.y, -65536, rectangle.x, rectangle.height);
				} else {
					ClientState.activeToolkit.drawOpaqueRectangle(rectangle.width, rectangle.y, -16711936, rectangle.x, rectangle.height);
				}
			}
		}
		if (Console.isOpen()) {
			Console.renderConsole(ClientState.activeToolkit);
		}
		if (GameShell.signLink.microsoftJava && MainLogicManager.isLoginLobbyOrGameScreen(ClientState.mainLogicStep) && InterfaceManager.interfaceDebugMode == 0 && WindowMode.getWindowMode() == WindowModeType.FIXED && !drawBackground) {
			int flipCount = 0;
			for (int componentIndex = 0; componentIndex < Camera.componentBackgroundCount; componentIndex++) {
				if (InterfaceRectangles.RECTANGLE_NEEDS_FLIP[componentIndex]) {
					InterfaceRectangles.RECTANGLE_NEEDS_FLIP[componentIndex] = false;
					InterfaceRectangles.FLIP_RECTANGLES[flipCount++] = InterfaceRectangles.INTERFACE_BG_COMPONENTS[componentIndex];
				}
			}
			try {
				if (OrthoMode.orthoEnabled) {
					OrthoMode.orthoFlipDirtyRect(flipCount, InterfaceRectangles.FLIP_RECTANGLES);
				} else {
					ClientState.activeToolkit.flipDirtyRects(InterfaceRectangles.FLIP_RECTANGLES, flipCount);
				}
			} catch (@Pc(483) FlipException ex) {
			}
		} else if (!MainLogicManager.isLoadingStep(ClientState.mainLogicStep)) {
			for (int componentIndex = 0; componentIndex < Camera.componentBackgroundCount; componentIndex++) {
				InterfaceRectangles.RECTANGLE_NEEDS_FLIP[componentIndex] = false;
			}
			try {
				if (OrthoMode.orthoEnabled) {
					OrthoMode.flipOrtho();
				} else {
					ClientState.activeToolkit.renderFrame();
				}
			} catch (@Pc(512) FlipException ex2) {
				ExplicitRuntimeException.reportError(ex2.getMessage() + " (Recovered) " + this.getDebugString(), ex2);
				ToolkitSwitch.loadAssets(false, 0);
			}
		}
		Static690.method5090();
		int cpuUsage = GamePreferences.uid.cpu.getValue();
		if (cpuUsage == 0) {
			TimeUtils.sleep(15L);
		} else if (cpuUsage == 1) {
			TimeUtils.sleep(10L);
		} else if (cpuUsage == 2) {
			TimeUtils.sleep(5L);
		} else if (cpuUsage == 3) {
			TimeUtils.sleep(2L);
		}
		if (TypeLists.cacheCleaningEnabled) {
			TypeLists.cleanCaches();
		}
		if (GamePreferences.uid.startupSafeMode.getValue() == 1 && ClientState.mainLogicStep == MainLogicStep.STEP_LOGIN_SCREEN && ClientState.BASE_WINDOW_ID != -1) {
			GamePreferences.uid.setValue(GamePreferences.uid.startupSafeMode, 0);
			GamePreferences.savePreferences();
		}
	}

	@OriginalMember(owner = "client!client", name = "a", descriptor = "(B)Ljava/lang/String;")
	@Override
	public String getDebugString() {
		@Pc(7) String string = null;
		try {
			string = "[1)" + MapRegion.sceneBaseX + "," + MapRegion.sceneBaseY + "," + MapRegion.sceneSizeX + "," + MapRegion.sceneSizeY + "|";
			if (PlayerUpdate.localPlayer != null) {
				string = string + "2)" + PlayerUpdate.currentLevel + "," + (MapRegion.sceneBaseX + PlayerUpdate.localPlayer.movementBaseX[0]) + "," + (MapRegion.sceneBaseY + PlayerUpdate.localPlayer.movementBaseY[0]) + "|";
			}
			string = string + "3)" + GamePreferences.uid.toolkit.getValue() + "|4)" + GamePreferences.uid.antiAliasing.getValue() + "|5)" + WindowMode.getWindowMode().id + "|6)" + GameShell.screenCanvasWidth + "," + GameShell.screenCanvasHeight + "|";
			string = string + "7)" + GamePreferences.uid.lightDetail.getValue() + "|";
			string = string + "8)" + GamePreferences.uid.hardShadows.getValue() + "|";
			string = string + "9)" + GamePreferences.uid.water.getValue() + "|";
			string = string + "10)" + GamePreferences.uid.textures.getValue() + "|";
			string = string + "11)" + GamePreferences.uid.bloom.getValue() + "|";
			string = string + "12)" + GamePreferences.uid.animateBackgroundOverride.getValue() + "|";
			string = string + "13)" + GameShell.maxMemory + "|";
			string = string + "14)" + ClientState.mainLogicStep.id();
			if (SystemInfo.SYSTEM_INFO != null) {
				string = string + "|15)" + SystemInfo.SYSTEM_INFO.ram;
			}
			try {
				if (GamePreferences.uid.toolkit.getValue() == ToolkitType.SSE) {
					@Pc(246) Class class2 = Class.forName("java.lang.ClassLoader");
					@Pc(250) Field field = class2.getDeclaredField("nativeLibraries");
					@Pc(253) Class class3 = Class.forName("java.lang.reflect.AccessibleObject");
					@Pc(263) Method method = class3.getDeclaredMethod("setAccessible", Boolean.TYPE);
					method.invoke(field, Boolean.TRUE);
					@Pc(286) Vector vector = (Vector) field.get(client.class.getClassLoader());
					for (@Pc(288) int i = 0; vector.size() > i; i++) {
						try {
							@Pc(294) Object object = vector.elementAt(i);
							@Pc(299) Field field2 = object.getClass().getDeclaredField("name");
							method.invoke(field2, Boolean.TRUE);
							try {
								@Pc(314) String string2 = (String) field2.get(object);
								if (string2 != null && string2.indexOf("sw3d.dll") != -1) {
									@Pc(326) Field field3 = object.getClass().getDeclaredField("handle");
									method.invoke(field3, Boolean.TRUE);
									string = string + "|16)" + Long.toHexString(field3.getLong(object));
									method.invoke(field3, Boolean.FALSE);
								}
							} catch (@Pc(362) Throwable ex) {
							}
							method.invoke(field2, Boolean.FALSE);
						} catch (@Pc(374) Throwable ex2) {
						}
					}
				}
			} catch (@Pc(386) Throwable ex3) {
			}
			string = string + "]";
		} catch (@Pc(397) Throwable ex4) {
		}
		return string;
	}
	@OriginalMember(owner = "client!qg", name = "h", descriptor = "[I")
	public static final int[] statXp = new int[25];

	@OriginalMember(owner = "client!pc", name = "h", descriptor = "[I")
	public static final int[] statLevels = new int[25];

	@OriginalMember(owner = "client!naa", name = "cb", descriptor = "Z")
	public static boolean isForcedSafemode = false;

	@OriginalMember(owner = "client!qj", name = "c", descriptor = "I")
	public static int lastGrandExchangeUpdateTick = 0;

	@OriginalMember(owner = "client!pi", name = "B", descriptor = "Ljava/lang/String;")
	public static String js5ProxyAddress = null;

	@OriginalMember(owner = "client!mda", name = "b", descriptor = "I")
	public static int mouseWheelDelta = 0;

	@OriginalMember(owner = "client!pb", name = "y", descriptor = "I")
	public static int errors = 0;

	@OriginalMember(owner = "client!lr", name = "K", descriptor = "[Lclient!ll;")
	public static final KeyRecord[] KEY_RECORDS = new KeyRecord[128];
	@OriginalMember(owner = "client!hba", name = "d", descriptor = "Lclient!sk;")
	public static ModeWhat buildEnvironment;

	@OriginalMember(owner = "client!nb", name = "a", descriptor = "I")
	public static int lobbyMembership = 0;

	@OriginalMember(owner = "client!nm", name = "c", descriptor = "I")
	public static int runWeight = 0;

	@OriginalMember(owner = "client!vs", name = "d", descriptor = "I")
	public static int rebootTimer = 0;

	@OriginalMember(owner = "client!dfa", name = "R", descriptor = "Lclient!tca;")
	public static final EntityNodeList PLAYER_LOGINSTATE_UPDATES = new EntityNodeList();

	@OriginalMember(owner = "client!mia", name = "a", descriptor = "I")
	public static int recentlyUsedItemContainersCursorPos = 0;
	@OriginalMember(owner = "client!mj", name = "t", descriptor = "[I")
	public static final int[] recentVarcUpdates = new int[32];

	@OriginalMember(owner = "client!jk", name = "I", descriptor = "I")
	public static int maximumHeldKeys = 0;
}
