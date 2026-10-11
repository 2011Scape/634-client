import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class LoadingScreenThreadStop {

	@OriginalMember(owner = "client!hha", name = "b", descriptor = "(Z)V")
	public static void killLoginscreenThread() {
		if (ActiveLoadingScreenRenderer.aClass8_1 != null) {
			ActiveLoadingScreenRenderer.aClass8_1.stop();
		}
		if (ActiveLoadingScreenRenderer.loadingScreenThread == null) {
			return;
		}
		while (true) {
			try {
				ActiveLoadingScreenRenderer.loadingScreenThread.join();
				return;
			} catch (@Pc(18) InterruptedException ex) {
			}
		}
	}
}
