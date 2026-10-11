import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.js5.MessageBoxBackgroundSprite;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.mouse.CursorManager;
import com.jagex.game.runetek5.Cursor;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceRenderer;
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.InterfaceManager;

public final class MenuScreenFrameRender {

	@OriginalMember(owner = "client!od", name = "d", descriptor = "(I)V")
	public static void drawInterfaceFrame() {
		ParticleSystemManager.updateParticleSystems(ClientState.activeToolkit, (long) ClientState.clientCycle);
		if (ClientState.BASE_WINDOW_ID != -1) {
			InterfaceManager.loadAndAnimateInterface(ClientState.BASE_WINDOW_ID);
		}
		for (@Pc(23) int i = 0; i < Camera.componentBackgroundCount; i++) {
			if (InterfaceRectangles.IS_COMPONENT_ON_TOP[i]) {
				InterfaceRectangles.RECTANGLE_NEEDS_FLIP[i] = true;
			}
			InterfaceRectangles.PREV_COMPONENT_ON_TOP[i] = InterfaceRectangles.IS_COMPONENT_ON_TOP[i];
			InterfaceRectangles.IS_COMPONENT_ON_TOP[i] = false;
		}
		InterfaceRenderer.lastRenderedCycle = ClientState.clientCycle;
		if (ClientState.BASE_WINDOW_ID != -1) {
			Camera.componentBackgroundCount = 0;
			Static701.method5594();
		}
		ClientState.activeToolkit.la();
		MiniMenu.render(ClientState.activeToolkit);
		@Pc(68) int cursorId = MiniMenu.getMinimenuCursor();
		if (cursorId == -1) {
			cursorId = Cursor.interfaceCursorId;
		}
		if (cursorId == -1) {
			cursorId = CursorManager.defaultCursorId;
		}
		CursorManager.setCursor(cursorId);
		ClientState.animatingStep = 0;
	}
}
