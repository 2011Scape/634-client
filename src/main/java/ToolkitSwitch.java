import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.client.ClientState;

public final class ToolkitSwitch {

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(ZBI)V")
	public static void loadAssets(@OriginalArg(0) boolean skipSettingsVerification, @OriginalArg(2) int toolkitId) {
		ToolkitRebuild.loadAssets(LocalizedText.aClass351_14.get(ClientState.language), skipSettingsVerification, toolkitId);
	}
}
