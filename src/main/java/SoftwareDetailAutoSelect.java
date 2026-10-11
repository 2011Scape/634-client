import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.graphics.ToolkitType;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.GameShell;

public final class SoftwareDetailAutoSelect {

	@OriginalMember(owner = "client!bja", name = "g", descriptor = "(I)I")
	public static int selectSoftwareDetailLevel() {
		@Pc(21) byte detailLevel;
		if (GameShell.maxMemory < 96) {
			MinDetailPreset.loadMinDetailPreferences();
			detailLevel = 1;
		} else {
			@Pc(26) int benchmarkExecutionTime = SoftwareRendererBenchmark.benchmarkSoftwareRenderer();
			if (benchmarkExecutionTime <= 100) {
				detailLevel = 4;
				HighDetailPreset.loadHighDetailPreferences();
			} else if (benchmarkExecutionTime <= 500) {
				detailLevel = 3;
				MediumDetailPreset.loadMediumDetailPreferences();
			} else if (benchmarkExecutionTime <= 1000) {
				detailLevel = 2;
				LowDetailPreset.loadLowDetailPreferences();
			} else {
				MinDetailPreset.loadMinDetailPreferences();
				detailLevel = 1;
			}
		}
		if (GamePreferences.uid.toolkit.getValue() != ToolkitType.JAVA) {
			GamePreferences.uid.setValue(GamePreferences.uid.toolkitDefault, 0);
			ToolkitSwitch.loadAssets(false, 0);
		}
		GamePreferences.savePreferences();
		return detailLevel;
	}
}
