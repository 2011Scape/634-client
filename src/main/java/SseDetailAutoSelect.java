import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.graphics.ToolkitType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class SseDetailAutoSelect {

	@OriginalMember(owner = "client!it", name = "a", descriptor = "(IB)I")
	public static int selectSseDetailLevel(@OriginalArg(0) int profilingPerformance) {
		@Pc(22) byte detailLevel;
		if (profilingPerformance > 12000) {
			HighDetailPreset.loadHighDetailPreferences();
			detailLevel = 4;
		} else if (profilingPerformance > 5000) {
			MediumDetailPreset.loadMediumDetailPreferences();
			detailLevel = 3;
		} else if (profilingPerformance <= 2000) {
			detailLevel = 1;
			MinDetailPreset.loadMinDetailPreferences();
		} else {
			detailLevel = 2;
			LowDetailPreset.loadLowDetailPreferences();
		}
		if (GamePreferences.uid.toolkit.getValue() != ToolkitType.SSE) {
			GamePreferences.uid.setValue(GamePreferences.uid.toolkitDefault, 2);
			ToolkitSwitch.loadAssets(false, 2);
		}
		GamePreferences.savePreferences();
		return detailLevel;
	}
}
