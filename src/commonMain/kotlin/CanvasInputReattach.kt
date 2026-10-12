import awt.Color
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.client.input.keyboard.CanvasKeyRecorder
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.AbstractMouseManager
import com.jagex.game.runetek5.client.input.mouse.CursorManager
import kotlin.jvm.JvmStatic

object CanvasInputReattach {

	@OriginalMember(owner = "client!lo", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun buildCanvasComponents() {
		KeyRecorder.keyRecorder!!.remove()
		MouseRecord.mouseRecorder!!.remove()
		ActiveClient.activeClient!!.addcanvas()
		GameShell.gameCanvas!!.setBackground(awt.COLOR_BLACK)
		CursorManager.currentCursor = -1
		KeyRecorder.keyRecorder = CanvasKeyRecorder.create(GameShell.gameCanvas)
		MouseRecord.mouseRecorder = AbstractMouseManager.create(GameShell.gameCanvas)
	}
}
