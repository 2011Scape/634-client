import org.openrs2.deob.annotation.OriginalMember;

public final class LoadingScreenCycleCount {

	@OriginalMember(owner = "client!lp", name = "b", descriptor = "(I)I")
	public static int getCycle() {
		return ActiveLoadingScreenRenderer.aClass8_1.getCycles();
	}
}
