import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmStatic

object LoginSessionReset {

	@OriginalMember(owner = "client!tt", name = "d", descriptor = "(I)V")
	@JvmStatic
	fun resetSession() {
		SessionStateReset.prepareForIngame()
	}
}
