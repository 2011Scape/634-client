import org.openrs2.deob.annotation.OriginalMember

object ActiveClient {

	@OriginalMember(owner = "client!bb", name = "Q", descriptor = "Lclient!client;")
	@JvmField
	var activeClient: client? = null
}
