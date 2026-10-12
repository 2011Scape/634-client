import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.DraggedComponentLayer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.ClientState

object MiniMenuDeferredClick {

	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(ZII)V")
	@JvmStatic
	fun processDeferredClick(@OriginalArg(1) mouseX: Int, @OriginalArg(2) mouseY: Int) {
		if (MiniMenu.deferredClickType == 1) {
			MiniMenuActionExecutor.processMenuClick(MiniMenu.deferredEntry, mouseY, mouseX)
		} else if (MiniMenu.deferredClickType == 2) {
			if (OrthoMode.orthoEnabled) {
				MiniMenu.openContextMenu(mouseX + OrthoMode.getOrthoOffsetX(), OrthoMode.getOrthoDrawOffsetY() + mouseY)
			} else {
				MiniMenu.openContextMenu(mouseX, mouseY)
			}
		}
		MiniMenu.deferredClickType = 0
		MiniMenu.deferredEntry = null
	}
}
