import java.awt.Dimension;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.client.ClientState;

public final class CanvasReplace {

	@OriginalMember(owner = "client!wm", name = "b", descriptor = "(I)V")
	public static void exitFullscreen() {
		if (ClientState.activeToolkit.replaceCanvasOnDisplayUpdate()) {
			ClientState.activeToolkit.releaseSurface(GameShell.gameCanvas);
			CanvasInputReattach.buildCanvasComponents();
			if (OrthoMode.orthoEnabled) {
				OrthoMode.addOrthoCanvas(GameShell.gameCanvas);
			} else {
				@Pc(27) Dimension dimension = GameShell.gameCanvas.getSize();
				ClientState.activeToolkit.addCanvas(GameShell.gameCanvas, dimension.width, dimension.height);
			}
			ClientState.activeToolkit.setCanvas(GameShell.gameCanvas);
		} else {
			ToolkitSwitch.loadAssets(false, GamePreferences.uid.toolkit.getValue());
		}
		InterfaceRectangles.invalidateAllRectangles();
		Scene.staticSceneDirty = true;
	}
}
