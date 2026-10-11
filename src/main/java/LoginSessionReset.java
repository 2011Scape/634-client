import org.openrs2.deob.annotation.OriginalMember;

public final class LoginSessionReset {

	@OriginalMember(owner = "client!tt", name = "d", descriptor = "(I)V")
	public static void resetSession() {
		SessionStateReset.prepareForIngame();
	}
}
