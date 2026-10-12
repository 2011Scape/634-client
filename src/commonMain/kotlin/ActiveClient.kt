import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object ActiveClient {

	@OriginalMember(owner = "client!bb", name = "Q", descriptor = "Lclient!client;")
	@JvmField
	var activeClient: client? = null
}
