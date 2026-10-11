import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import java.awt.Dimension;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.config.defaults.EntityDefaults;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.utils.Utils;

public final class ToolkitProfiler {

	@OriginalMember(owner = "client!pp", name = "a", descriptor = "(III)I")
	public static int getProfilingPerformance(@OriginalArg(0) int toolkitId, @OriginalArg(2) int profilingTime) {
		if (EntityDefaults.profilingMesh == -1) {
			return 1;
		}
		if (GamePreferences.uid.toolkit.getValue() != toolkitId) {
			ToolkitRebuild.loadAssets(LocalizedText.aClass351_15.get(ClientState.language), true, toolkitId);
			if (toolkitId != GamePreferences.uid.toolkit.getValue()) {
				return -1;
			}
		}
		try {
			@Pc(39) Dimension dimension = GameShell.gameCanvas.getSize();
			MessageBox.draw(true, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_15.get(ClientState.language), ClientState.activeToolkit);
			@Pc(55) Mesh mesh = Mesh.fromResource(Resource.MODEL, EntityDefaults.profilingMesh);
			@Pc(58) long startTime = Utils.currentTimeMillis();
			ClientState.activeToolkit.la();
			ClientState.aClass30_3.applyTranslation(0, Scene.halfTileOffset, 0);
			ClientState.activeToolkit.setCamera(ClientState.aClass30_3);
			ClientState.activeToolkit.DA(dimension.width / 2, dimension.height / 2, 512, 512);
			ClientState.activeToolkit.xa(1.0F);
			ClientState.activeToolkit.ZA(16777215, 0.5F, 0.5F, 20.0F, -50.0F, 30.0F);
			@Pc(104) Model model = ClientState.activeToolkit.createModel(mesh, 2048, 64, 64, 768);
			@Pc(106) int modelsDrawn = 0;
			label41: for (@Pc(108) int i = 0; i < 500; i++) {
				ClientState.activeToolkit.GA(0);
				ClientState.activeToolkit.ya();
				for (@Pc(117) int j = 15; j >= 0; j--) {
					for (@Pc(121) int k = 0; k <= j; k++) {
						InterfaceComponentRenderer.aClass30_6.applyTranslation((int) ((-((float) j / 2.0F) + (float) k) * (float) Culling.fullTileDimension), 0, (j + 1) * Culling.fullTileDimension);
						modelsDrawn++;
						model.render(InterfaceComponentRenderer.aClass30_6, null, 0);
						if ((long) profilingTime <= Utils.currentTimeMillis() - startTime) {
							break label41;
						}
					}
				}
			}
			ClientState.activeToolkit.finish();
			@Pc(197) long performanceIndicator = (long) (modelsDrawn * 1000) / (Utils.currentTimeMillis() - startTime);
			ClientState.activeToolkit.GA(0);
			ClientState.activeToolkit.ya();
			return (int) performanceIndicator;
		} catch (@Pc(206) Throwable ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	@OriginalMember(owner = "client!sh", name = "c", descriptor = "I")
	public static int debugNpcCount = 0;

	@OriginalMember(owner = "client!ai", name = "x", descriptor = "I")
	public static int debugPlayerCount = 0;

	@OriginalMember(owner = "client!qi", name = "d", descriptor = "Z")
	public static boolean showProfiling = false;

	@OriginalMember(owner = "client!mf", name = "b", descriptor = "(B)V")
	public static void resetEntityDebugCounters() {
		ToolkitProfiler.debugPlayerCount = 0;
		ToolkitProfiler.debugNpcCount = 0;
		ToolkitProfiler.anInt6496 = 0;
		ToolkitProfiler.anInt8154 = 0;
	}

	@OriginalMember(owner = "client!pu", name = "d", descriptor = "I")
	public static int anInt8154 = 0;

	@OriginalMember(owner = "client!lea", name = "f", descriptor = "I")
	public static int anInt6496 = 0;
}
