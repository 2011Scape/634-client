import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.input.mouse.MouseManager
import com.jagex.game.runetek5.client.ClientState

object InterfaceInputTick {

	@OriginalMember(owner = "client!ru", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun processRootInterfaceInput() {
		if (ClientState.BASE_WINDOW_ID == -1) {
			return
		}
		@Pc(12) var mouseX: Int = MouseRecord.mouseRecorder!!.getMouseX()
		@Pc(16) var mouseY: Int = MouseRecord.mouseRecorder!!.getMouseY()
		@Pc(21) val mouseEvent: MouseRecord? = MouseManager.MOUSE_MOVEMENTS.first() as MouseRecord?
		if (mouseEvent != null) {
			mouseX = mouseEvent.getMouseX()
			mouseY = mouseEvent.getMouseY()
		}
		@Pc(33) var orthoOffsetX: Int = 0
		@Pc(40) var orthoOffsetY: Int = 0
		if (OrthoMode.orthoEnabled) {
			orthoOffsetX = OrthoMode.getOrthoOffsetX()
			orthoOffsetY = OrthoMode.getOrthoDrawOffsetY()
		}
		InterfaceRootInput.processInterfaceInput(ClientState.BASE_WINDOW_ID, orthoOffsetX, orthoOffsetX, GameShell.screenCanvasWidth + orthoOffsetX, mouseY, mouseX, mouseX + orthoOffsetX, GameShell.screenCanvasHeight + orthoOffsetY, orthoOffsetY, orthoOffsetY + mouseY, orthoOffsetY)
		if (WorldMap.worldmapComponent != null) {
			WorldMapZoomAnimation.ifWorldmapHover(orthoOffsetY + mouseY, orthoOffsetX + mouseX)
		}
	}
}
