import com.jagex.game.runetek5.ServerConnectionReader
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.network.packet.StockMarketOffer
import com.jagex.game.runetek5.core.constants.PlayerListConstants
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.statics.StatBaseLevels
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.config.npc.NPCType
import com.jagex.game.runetek5.config.varc.VarcTypeList
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.config.`object`.ObjectType
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.scene.LocAction
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.client.periodic.PulseEventHandler
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.botdetection.ReflectionCheckManager
import com.jagex.game.runetek5.client.PingWorker
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.tile.TileMessage
import com.jagex.game.runetek5.entity.ProjectileAnimation
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.network.BufferedConnectionContext
import kotlin.jvm.JvmStatic

object Static671 {

	@OriginalMember(owner = "client!aq", name = "d", descriptor = "(I)V")
	@JvmStatic
	fun closeWorldMap() {
		MainLogicStepTransition.setMainLogicStep(MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD)
		WorldMapExitToolkitRestore.releaseWorldMapRestoreToolkit()
		lang.gc()
	}

	@OriginalMember(owner = "client!aq", name = "e", descriptor = "(I)V")
	@JvmStatic
	fun method597() {
		Resource.CONFIG!!.loadType = 1
		SessionStateReset.prepareForIngame()
		GameShell.lastSentFocus = true
		GameShell.appletHasFocus = true
		ReflectionCheckManager.setupPendingChecks()
		@Pc(8130) var i: Int = 0
		while (i < PlayerUpdate.HINT_ARROWS.size) {
			PlayerUpdate.HINT_ARROWS[i] = null
			i++
		}
		MiniMenu.interfaceSelectionActive = false
		AreaSoundPlayer.resetAreaSounds()
		Minimap.minimapRotation = (util.random() * 120.0).toInt() - 60
		Camera.cameraRandomisationV = (util.random() * 110.0).toInt() - 55
		Camera.camAngleY = ((util.random() * 160.0).toInt() - 80 and 0x3FFF).toFloat()
		Minimap.minimapZoom = (util.random() * 30.0).toInt() - 20
		Camera.cameraRandomisationA = (util.random() * 80.0).toInt() - 40
		Camera.cameraRandomisationX = (util.random() * 100.0).toInt() - 50
		Minimap.resetMinimapFlagAndPlane()
		@Pc(8204) var j: Int = 0
		while (j < PlayerListConstants.COUNT) {
			PlayerUpdate.players[j] = null
			j++
		}
		NPCList.NPC_UPDATE_INDEX = 0
		NPCList.npcCount = 0
		NPCList.NPC_MAP.clear()
		ProjectileAnimation.aClass341_24.clear()
		Renderers.activeToolkit.clear()
		TileMessage.aClass325_8.clear()
		ObjStack.GROUND_ITEMS.clear()
		LocAction.pendingRemovals = Deque()
		PlayerVarDomain.playerVarProvider!!.reset()
		PulseEventHandler.clearAllPulseEvents()
		Camera.lookAtVariableSpeed = 0
		Camera.moveToLocalY = 0
		Camera.lookAtLocalY = 0
		Camera.moveToVariableSpeed = 0
		Camera.lookAtHeightOffset = 0
		Camera.moveToHeightOffset = 0
		Camera.lookAtLocalX = 0
		Camera.moveToConstantSpeed = 0
		Camera.moveToLocalX = 0
		Camera.lookAtConstantSpeed = 0
		@Pc(8268) var k: Int = 0
		while (k < VarcTypeList.VARC_INT!!.size) {
			if (!VarcTypeList.VARC_PERSISTENT!![k]) {
				VarcTypeList.VARC_INT!![k] = -1
			}
			k++
		}
		if (ClientState.BASE_WINDOW_ID != -1) {
			Interface.unloadInterface(ClientState.BASE_WINDOW_ID)
		}
		@Pc(8299) var subInterface: SubInterface? = InterfaceManager.OPEN_INTERFACES.first() as SubInterface?
		while (subInterface != null) {
			if (!subInterface.isLinked()) {
				subInterface = InterfaceManager.OPEN_INTERFACES.first() as SubInterface?
				if (subInterface == null) {
					break
				}
			}
			SubInterfaceCloser.closeSubInterface(true, false, subInterface)
			subInterface = InterfaceManager.OPEN_INTERFACES.next() as SubInterface?
		}
		ClientState.BASE_WINDOW_ID = -1
		InterfaceManager.OPEN_INTERFACES = IterableHashTable(8)
		InterfaceManager.resetInterfaces()
		InterfaceManager.activeDialogueContinue = null
		@Pc(8339) var m: Int = 0
		while (m < 8) {
			MiniMenu.PLAYER_OPTIONS[m] = null
			MiniMenu.PLAYER_OPTION_REDUCED_PRIORITY[m] = false
			MiniMenu.PLAYER_OPTION_CURSORS[m] = -1
			m++
		}
		ItemContainer.clearContainers()
		ClientState.awaitingPriorityRequests = true
		@Pc(8365) var rectangleIndex: Int = 0
		while (rectangleIndex < 100) {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[rectangleIndex] = true
			rectangleIndex++
		}
		@Pc(8377) var slot: Int = 0
		while (slot < 6) {
			StockMarketOffer.GRAND_EXCHANGE_SLOTS[slot] = StockMarketOffer()
			slot++
		}
		@Pc(8391) var skill: Int = 0
		while (skill < 25) {
			client.statLevels!![skill] = 0
			StatBaseLevels.statBaseLevels[skill] = 0
			client.statXp!![skill] = 0
			skill++
		}
		EnvironmentManager.requestDirectEnvironmentTransition()
		Camera.sendCameraAngle = true
		val recolorPalette: ShortArray = ShortArray(256)
		ItemType.recolorPalette = recolorPalette
		NPCType.npcRecolorPalette = recolorPalette
		ObjectType.locRecolorPalette = recolorPalette
		TypeLists.recolorPalette = recolorPalette
		MiniMenu.walkHereText = LocalizedText.WALK_HERE.get(ClientState.language)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.lb, GamePreferences.uid!!.removeRoofs!!.getValue())
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.animateBackgroundOverride, GamePreferences.uid!!.animateBackground!!.getValue())
		PlayerVarDomain.varVerifyId = 0
		MiniMenu.reset()
		ServerConnectionReader.sendWindowmodePreferenceToServer()
		BufferedConnectionContext.NEXT_PING_UPDATE_TO_SERVER = 0L
		PingWorker.aClass2_Sub36_1 = null
		Resource.CONFIG!!.loadType = 2
	}
}
