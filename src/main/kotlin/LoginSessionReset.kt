import org.openrs2.deob.annotation.OriginalMember

object LoginSessionReset {

	@OriginalMember(owner = "client!tt", name = "d", descriptor = "(I)V")
	@JvmStatic
	fun resetSession() {
		SessionStateReset.prepareForIngame()
	}
}
