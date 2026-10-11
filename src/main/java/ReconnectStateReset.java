import com.jagex.game.runetek5.camera.CameraMode;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.ServerConnectionReader;
import com.jagex.game.runetek5.ThirdLastPacket;
import com.jagex.game.runetek5.core.constants.PlayerListConstants;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.client.PingWorker;
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.Minimap;
import com.jagex.game.runetek5.client.ClientState;

public final class ReconnectStateReset {

	@OriginalMember(owner = "client!cga", name = "b", descriptor = "(B)V")
	public static void onReconnect() {
		BufferedConnectionContext.resetQueue();
		BufferedConnectionContext.currentPacketSize = 0;
		ThirdLastPacket.thirdLastPacket = null;
		BufferedConnectionContext.receivedBuffer.currentPosition = 0;
		BufferedConnectionContext.lastPacket = null;
		BufferedConnectionContext.secondLastPacket = null;
		BufferedConnectionContext.idleReadPulses = 0;
		BufferedConnectionContext.currentPacket = null;
		client.rebootTimer = 0;
		MiniMenu.reset();
		Minimap.resetMinimapFlag();
		for (@Pc(34) int i = 0; i < PlayerListConstants.COUNT; i++) {
			PlayerUpdate.players[i] = null;
		}
		PlayerUpdate.localPlayer = null;
		for (@Pc(48) int j = 0; j < NPCList.npcCount; j++) {
			@Pc(55) NPCEntity npcEntity = NPCList.NPC_ARRAY[j].npc;
			if (npcEntity != null) {
				npcEntity.faceEntity = -1;
			}
		}
		ItemContainer.clearContainers();
		Camera.roofRemovalTargetY = -1;
		Camera.roofRemovalTargetX = -1;
		Camera.cameraCalculationType = CameraMode.MODE_DEFAULT;
		MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_GAME_SCREEN);
		for (@Pc(83) int k = 0; k < 100; k++) {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[k] = true;
		}
		ServerConnectionReader.sendWindowmodePreferenceToServer();
		PingWorker.aClass2_Sub36_1 = null;
		BufferedConnectionContext.NEXT_PING_UPDATE_TO_SERVER = 0L;
	}
}
