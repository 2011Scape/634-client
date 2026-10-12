import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember

object LoadingScreenRepaint {

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(ZI)V")
	@JvmStatic
	fun initialize(@OriginalArg(0) fullRepaint: Boolean) {
		if (ActiveLoadingScreenRenderer.aClass8_1 == null) {
			LoadingScreenThreadStart.initialize()
		}
		if (fullRepaint) {
			ActiveLoadingScreenRenderer.aClass8_1!!.requestFullRepaint()
		}
	}
}
