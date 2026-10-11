import com.jagex.game.runetek5.ServerConnectionReader;
import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.network.packet.StockMarketOffer;
import com.jagex.game.runetek5.core.constants.PlayerListConstants;
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.statics.StatBaseLevels;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.config.varc.VarcTypeList;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.config.object.ObjectType;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.scene.LocAction;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.client.periodic.PulseEventHandler;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.botdetection.ReflectionCheckManager;
import com.jagex.game.runetek5.client.PingWorker;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.Minimap;
import com.jagex.game.runetek5.cache.node.ObjStack;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.scene.tile.TileMessage;
import com.jagex.game.runetek5.entity.ProjectileAnimation;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.network.BufferedConnectionContext;

public final class Static671 {

	@OriginalMember(owner = "client!aq", name = "d", descriptor = "(I)V")
	public static void closeWorldMap() {
		MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD);
		WorldMapExitToolkitRestore.releaseWorldMapRestoreToolkit();
		System.gc();
	}

	@OriginalMember(owner = "client!aq", name = "e", descriptor = "(I)V")
	public static void method597() {
		Resource.CONFIG.loadType = 1;
		SessionStateReset.prepareForIngame();
		GameShell.lastSentFocus = true;
		GameShell.appletHasFocus = true;
		ReflectionCheckManager.setupPendingChecks();
		for (@Pc(8130) int i = 0; i < PlayerUpdate.HINT_ARROWS.length; i++) {
			PlayerUpdate.HINT_ARROWS[i] = null;
		}
		MiniMenu.interfaceSelectionActive = false;
		AreaSoundPlayer.resetAreaSounds();
		Minimap.minimapRotation = (int) (Math.random() * 120.0D) - 60;
		Camera.cameraRandomisationV = (int) (Math.random() * 110.0D) - 55;
		Camera.camAngleY = (int) (Math.random() * 160.0D) - 80 & 0x3FFF;
		Minimap.minimapZoom = (int) (Math.random() * 30.0D) - 20;
		Camera.cameraRandomisationA = (int) (Math.random() * 80.0D) - 40;
		Camera.cameraRandomisationX = (int) (Math.random() * 100.0D) - 50;
		Minimap.resetMinimapFlagAndPlane();
		for (@Pc(8204) int j = 0; j < PlayerListConstants.COUNT; j++) {
			PlayerUpdate.players[j] = null;
		}
		NPCList.NPC_UPDATE_INDEX = 0;
		NPCList.npcCount = 0;
		NPCList.NPC_MAP.clear();
		ProjectileAnimation.aClass341_24.clear();
		Renderers.activeToolkit.clear();
		TileMessage.aClass325_8.clear();
		ObjStack.GROUND_ITEMS.clear();
		LocAction.pendingRemovals = new Deque();
		PlayerVarDomain.playerVarProvider.reset();
		PulseEventHandler.clearAllPulseEvents();
		Camera.lookAtVariableSpeed = 0;
		Camera.moveToLocalY = 0;
		Camera.lookAtLocalY = 0;
		Camera.moveToVariableSpeed = 0;
		Camera.lookAtHeightOffset = 0;
		Camera.moveToHeightOffset = 0;
		Camera.lookAtLocalX = 0;
		Camera.moveToConstantSpeed = 0;
		Camera.moveToLocalX = 0;
		Camera.lookAtConstantSpeed = 0;
		for (@Pc(8268) int k = 0; k < VarcTypeList.VARC_INT.length; k++) {
			if (!VarcTypeList.VARC_PERSISTENT[k]) {
				VarcTypeList.VARC_INT[k] = -1;
			}
		}
		if (ClientState.BASE_WINDOW_ID != -1) {
			Interface.unloadInterface(ClientState.BASE_WINDOW_ID);
		}
		for (@Pc(8299) SubInterface subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.first(); subInterface != null; subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.next()) {
			if (!subInterface.isLinked()) {
				subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.first();
				if (subInterface == null) {
					break;
				}
			}
			SubInterfaceCloser.closeSubInterface(true, false, subInterface);
		}
		ClientState.BASE_WINDOW_ID = -1;
		InterfaceManager.OPEN_INTERFACES = new IterableHashTable(8);
		InterfaceManager.resetInterfaces();
		InterfaceManager.activeDialogueContinue = null;
		for (@Pc(8339) int m = 0; m < 8; m++) {
			MiniMenu.PLAYER_OPTIONS[m] = null;
			MiniMenu.PLAYER_OPTION_REDUCED_PRIORITY[m] = false;
			MiniMenu.PLAYER_OPTION_CURSORS[m] = -1;
		}
		ItemContainer.clearContainers();
		ClientState.awaitingPriorityRequests = true;
		for (@Pc(8365) int rectangleIndex = 0; rectangleIndex < 100; rectangleIndex++) {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[rectangleIndex] = true;
		}
		for (@Pc(8377) int slot = 0; slot < 6; slot++) {
			StockMarketOffer.GRAND_EXCHANGE_SLOTS[slot] = new StockMarketOffer();
		}
		for (@Pc(8391) int skill = 0; skill < 25; skill++) {
			client.statLevels[skill] = 0;
			StatBaseLevels.statBaseLevels[skill] = 0;
			client.statXp[skill] = 0;
		}
		EnvironmentManager.requestDirectEnvironmentTransition();
		Camera.sendCameraAngle = true;
		TypeLists.recolorPalette = ObjectType.locRecolorPalette = NPCType.npcRecolorPalette = ItemType.recolorPalette = new short[256];
		MiniMenu.walkHereText = LocalizedText.WALK_HERE.get(ClientState.language);
		GamePreferences.uid.setValue(GamePreferences.uid.lb, GamePreferences.uid.removeRoofs.getValue());
		GamePreferences.uid.setValue(GamePreferences.uid.animateBackgroundOverride, GamePreferences.uid.animateBackground.getValue());
		PlayerVarDomain.varVerifyId = 0;
		MiniMenu.reset();
		ServerConnectionReader.sendWindowmodePreferenceToServer();
		BufferedConnectionContext.NEXT_PING_UPDATE_TO_SERVER = 0L;
		PingWorker.aClass2_Sub36_1 = null;
		Resource.CONFIG.loadType = 2;
	}
}
