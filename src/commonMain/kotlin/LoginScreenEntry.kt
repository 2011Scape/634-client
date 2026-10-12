import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.config.defaults.EntityDefaults
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.Cursor
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object LoginScreenEntry {

	@OriginalMember(owner = "client!qia", name = "a", descriptor = "(ZB)V")
	@JvmStatic
	fun openLoginScreen(@OriginalArg(0) prepareWindow: Boolean) {
		if (prepareWindow) {
			if (ClientState.BASE_WINDOW_ID != -1) {
				Interface.unloadInterface(ClientState.BASE_WINDOW_ID)
			}
			@Pc(24) var subInterface: SubInterface? = InterfaceManager.OPEN_INTERFACES.first() as SubInterface?
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
			ClientState.BASE_WINDOW_ID = EntityDefaults.loginInterfaceId
			WindowMode.interfaceResize(false)
			InterfaceRectangles.invalidateAllRectangles()
			CS2Interpreter.runInterfaceOnLoadScripts(ClientState.BASE_WINDOW_ID)
		}
		LoginManager.currentlyInLobby = false
		LoginManager.loginPassword = ""
		LoginManager.loginUsername = ""
		MiniMenu.addCancelOption()
		Cursor.interfaceCursorId = -1
		CursorManager.setCursor(CursorManager.defaultCursorId)
		PlayerUpdate.localPlayer = PlayerEntity()
		PlayerUpdate.localPlayer!!.x = MapRegion.sceneSizeX * 512 / 2
		PlayerUpdate.localPlayer!!.movementBaseX[0] = MapRegion.sceneSizeX / 2
		PlayerUpdate.localPlayer!!.z = MapRegion.sceneSizeY * 512 / 2
		PlayerUpdate.localPlayer!!.movementBaseY[0] = MapRegion.sceneSizeY / 2
		Camera.cameraPositionAbsoluteY = 0
		Camera.cameraPositionAbsoluteX = 0
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWPLAYER) {
			Camera.cameraPositionAbsoluteX = Camera.moveToLocalX shl 9
			Camera.cameraPositionAbsoluteY = Camera.moveToLocalY shl 9
		} else {
			Camera.calculateCutsceneCameraPosition()
		}
		EnvironmentManager.requestDirectEnvironmentTransition()
	}
}
