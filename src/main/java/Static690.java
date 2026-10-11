import com.jagex.game.runetek5.config.midi.VorbisNode;
import com.jagex.game.runetek5.preference.GamePreferences;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class Static690 {

	@OriginalMember(owner = "client!jw", name = "b", descriptor = "(I)V")
	public static void method5090() {
		for (@Pc(15) VorbisNode videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.first(); videoType != null; videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.next()) {
			if (videoType.isReferencedThisCycle) {
				videoType.isReferencedThisCycle = false;
			} else {
				VorbisStreamStop.stopVorbisStream(videoType.fileId);
			}
		}
	}

	@OriginalMember(owner = "client!jw", name = "a", descriptor = "(BII)I")
	public static int method5092(@OriginalArg(1) int toolkitType, @OriginalArg(2) int performanceScore) {
		@Pc(21) byte detailLevel;
		if (performanceScore > 20000) {
			HighDetailPreset.loadHighDetailPreferences();
			detailLevel = 4;
		} else if (performanceScore > 10000) {
			detailLevel = 3;
			MediumDetailPreset.loadMediumDetailPreferences();
		} else if (performanceScore <= 5000) {
			MinDetailPreset.loadMinDetailPreferences();
			detailLevel = 1;
		} else {
			detailLevel = 2;
			LowDetailPreset.loadLowDetailPreferences();
		}
		if (GamePreferences.uid.toolkit.getValue() != toolkitType) {
			GamePreferences.uid.setValue(GamePreferences.uid.toolkitDefault, toolkitType);
			ToolkitSwitch.loadAssets(false, toolkitType);
		}
		GamePreferences.savePreferences();
		return detailLevel;
	}
}
