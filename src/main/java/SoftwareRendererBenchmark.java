import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.ToolkitType;
import java.awt.Canvas;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

public final class SoftwareRendererBenchmark {

	@OriginalMember(owner = "client!vha", name = "a", descriptor = "(I)I")
	public static int benchmarkSoftwareRenderer() {
		@Pc(7) Toolkit toolkit = ClientState.activeToolkit;
		@Pc(9) boolean createdToolkit = false;
		if (GamePreferences.uid.toolkit.getValue() != ToolkitType.JAVA) {
			@Pc(23) Canvas canvas = new Canvas();
			canvas.setSize(100, 100);
			toolkit = CanvasToolkitFactory.create(ToolkitType.JAVA, null, 0, canvas, null);
			createdToolkit = true;
		}
		@Pc(42) long startTime = Utils.currentTimeMillis();
		for (@Pc(44) int i = 0; i < 10000; i++) {
			toolkit.drawTriangle();
		}
		@Pc(72) int elapsedTime = (int) (Utils.currentTimeMillis() - startTime);
		toolkit.fillRect(100, 0, 0, 100, -16777216);
		if (createdToolkit) {
			toolkit.free();
		}
		return elapsedTime;
	}
}
