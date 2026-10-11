import com.jagex.game.runetek5.client.window.WindowModeType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.window.WindowMode;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.client.ClientState;

public final class WindowModeRequest {

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(IIBZI)V")
	public static void requestWindowMode(@OriginalArg(0) WindowModeType windowMode, @OriginalArg(1) int width, @OriginalArg(3) boolean replaceCanvas, @OriginalArg(4) int height) {
		OrthoMode.applyOrthographicMode();
		WindowMode.nextDimensionUpdateCheck = 0L;
		@Pc(10) WindowModeType activeMode = WindowMode.getWindowMode();
		if (windowMode == WindowModeType.FULLSCREEN || activeMode == WindowModeType.FULLSCREEN) {
			replaceCanvas = true;
		}
		if (!ClientState.activeToolkit.forceFixedScreenmode()) {
			replaceCanvas = true;
		}
		WindowModeApply.applyWindowMode(replaceCanvas, width, activeMode, windowMode, height);
	}
}
