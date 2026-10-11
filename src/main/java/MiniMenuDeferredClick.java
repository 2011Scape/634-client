import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.DraggedComponentLayer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.client.ClientState;

public final class MiniMenuDeferredClick {

	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(ZII)V")
	public static void processDeferredClick(@OriginalArg(1) int mouseX, @OriginalArg(2) int mouseY) {
		if (MiniMenu.deferredClickType == 1) {
			MiniMenuActionExecutor.processMenuClick(MiniMenu.deferredEntry, mouseY, mouseX);
		} else if (MiniMenu.deferredClickType == 2) {
			if (OrthoMode.orthoEnabled) {
				MiniMenu.openContextMenu(mouseX + OrthoMode.getOrthoOffsetX(), OrthoMode.getOrthoDrawOffsetY() + mouseY);
			} else {
				MiniMenu.openContextMenu(mouseX, mouseY);
			}
		}
		MiniMenu.deferredClickType = 0;
		MiniMenu.deferredEntry = null;
	}
}
