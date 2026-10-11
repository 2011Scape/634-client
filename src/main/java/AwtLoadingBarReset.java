import org.openrs2.deob.annotation.OriginalMember;

public final class AwtLoadingBarReset {

	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(Z)V")
	public static void cleanupPlainLoadingBarResources() {
		Static658.canvasImage = null;
		AwtLoadingBarPainter.loadingFont = null;
	}
}
