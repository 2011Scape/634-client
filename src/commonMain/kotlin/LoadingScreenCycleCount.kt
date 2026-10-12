import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmStatic

object LoadingScreenCycleCount {

	@OriginalMember(owner = "client!lp", name = "b", descriptor = "(I)I")
	@JvmStatic
	fun getCycle(): Int {
		return ActiveLoadingScreenRenderer.aClass8_1!!.getCycles()
	}
}
