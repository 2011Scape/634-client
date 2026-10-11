import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;

public final class LoadingScreenRepaint {

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(ZI)V")
	public static void initialize(@OriginalArg(0) boolean fullRepaint) {
		if (ActiveLoadingScreenRenderer.aClass8_1 == null) {
			LoadingScreenThreadStart.initialize();
		}
		if (fullRepaint) {
			ActiveLoadingScreenRenderer.aClass8_1.requestFullRepaint();
		}
	}
}
