import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.ServerConnectionReader
import com.jagex.game.runetek5.ThirdLastPacket
import com.jagex.game.runetek5.core.constants.PlayerListConstants
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.client.PingWorker
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.client.ClientState

object ReconnectStateReset {

	@OriginalMember(owner = "client!cga", name = "b", descriptor = "(B)V")
	@JvmStatic
	fun onReconnect() {
		BufferedConnectionContext.resetQueue()
		BufferedConnectionContext.currentPacketSize = 0
		ThirdLastPacket.thirdLastPacket = null
		BufferedConnectionContext.receivedBuffer!!.currentPosition = 0
		BufferedConnectionContext.lastPacket = null
		BufferedConnectionContext.secondLastPacket = null
		BufferedConnectionContext.idleReadPulses = 0
		BufferedConnectionContext.currentPacket = null
		client.rebootTimer = 0
		MiniMenu.reset()
		Minimap.resetMinimapFlag()
		@Pc(34) var i: Int = 0
		while (i < PlayerListConstants.COUNT) {
			PlayerUpdate.players[i] = null
			i++
		}
		PlayerUpdate.localPlayer = null
		@Pc(48) var j: Int = 0
		while (j < NPCList.npcCount) {
			@Pc(55) val npcEntity: NPCEntity? = NPCList.NPC_ARRAY[j]!!.npc
			if (npcEntity != null) {
				npcEntity.faceEntity = -1
			}
			j++
		}
		ItemContainer.clearContainers()
		Camera.roofRemovalTargetY = -1
		Camera.roofRemovalTargetX = -1
		Camera.cameraCalculationType = CameraMode.MODE_DEFAULT
		MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_GAME_SCREEN)
		@Pc(83) var k: Int = 0
		while (k < 100) {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[k] = true
			k++
		}
		ServerConnectionReader.sendWindowmodePreferenceToServer()
		PingWorker.aClass2_Sub36_1 = null
		BufferedConnectionContext.NEXT_PING_UPDATE_TO_SERVER = 0L
	}
}
