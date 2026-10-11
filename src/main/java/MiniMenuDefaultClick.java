import com.jagex.game.runetek5.Preferences;
import com.jagex.game.runetek5.DraggedComponentLayer;
import com.jagex.game.runetek5.client.input.mouse.MouseRecord;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.InterfaceDrag;
import com.jagex.game.runetek5.client.input.mouse.MouseManager;

public final class MiniMenuDefaultClick {

	@OriginalMember(owner = "client!jaa", name = "f", descriptor = "(I)V")
	public static void clickDefaultEntry() {
		@Pc(10) MouseRecord mouseEvent = (MouseRecord) MouseManager.MOUSE_MOVEMENTS.first();
		@Pc(21) boolean isDeferring = InterfaceDrag.component != null || Preferences.favoriteWorlds > 0;
		@Pc(25) int mouseX = mouseEvent.getMouseX();
		@Pc(29) int mouseY = mouseEvent.getMouseY();
		if (isDeferring) {
			MiniMenu.deferredClickType = 1;
		}
		if (isDeferring) {
			MiniMenu.deferredEntry = MiniMenu.aClass2_Sub6_Sub3_1;
		} else {
			MiniMenuActionExecutor.processMenuClick(MiniMenu.aClass2_Sub6_Sub3_1, mouseY, mouseX);
		}
	}
}
