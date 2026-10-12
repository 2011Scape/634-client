import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.js5.MessageBoxBackgroundSprite
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.Cursor
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.core.datastruct.key.MouseOverTextY
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceRenderer
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.InterfaceManager

object GameScreenFrameRender {

	@OriginalMember(owner = "client!wo", name = "a", descriptor = "(BJ)V")
	@JvmStatic
	fun drawGameFrame(@OriginalArg(1) elapsedMillis: Long) {
		if (Scene.aClass100ArrayArrayArray3 != null) {
			if (Camera.cameraCalculationType == CameraMode.MODE_DEFAULT || Camera.cameraCalculationType == CameraMode.MODE_SMOOTH_RESET) {
				Camera.updateFollowPlayerCamera(elapsedMillis)
			} else if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
				Camera.updateOrbitTargetCamera(elapsedMillis)
			}
		}
		ParticleSystemManager.updateParticleSystems(ClientState.activeToolkit, ClientState.clientCycle.toLong())
		if (ClientState.BASE_WINDOW_ID != -1) {
			InterfaceManager.loadAndAnimateInterface(ClientState.BASE_WINDOW_ID)
		}
		@Pc(39) var i: Int = 0
		while (i < Camera.componentBackgroundCount) {
			if (InterfaceRectangles.IS_COMPONENT_ON_TOP[i]) {
				InterfaceRectangles.RECTANGLE_NEEDS_FLIP[i] = true
			}
			InterfaceRectangles.PREV_COMPONENT_ON_TOP[i] = InterfaceRectangles.IS_COMPONENT_ON_TOP[i]
			InterfaceRectangles.IS_COMPONENT_ON_TOP[i] = false
			i++
		}
		InterfaceRenderer.lastRenderedCycle = ClientState.clientCycle
		InterfaceComponentRenderer.setChatDisplay(null, -1, -1)
		MouseOverTextY.setMouseOverText(-1, null, -1)
		if (ClientState.BASE_WINDOW_ID != -1) {
			Camera.componentBackgroundCount = 0
			Static701.method5594()
		}
		ClientState.activeToolkit!!.la()
		MiniMenu.render(ClientState.activeToolkit)
		@Pc(94) var cursorId: Int = MiniMenu.getMinimenuCursor()
		if (cursorId == -1) {
			cursorId = Cursor.interfaceCursorId
		}
		if (cursorId == -1) {
			cursorId = CursorManager.defaultCursorId
		}
		CursorManager.setCursor(cursorId)
		@Pc(117) val size: Int = PlayerUpdate.localPlayer!!.getSize() shl 8
		AmbientSound.updateAllAmbientSounds(PlayerUpdate.localPlayer!!.level.toInt(), ClientState.animatingStep, size + PlayerUpdate.localPlayer!!.z, PlayerUpdate.localPlayer!!.x - -size)
		ClientState.animatingStep = 0
	}
}
