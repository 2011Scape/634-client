import org.openrs2.deob.annotation.OriginalMember

object ActiveLoadingScreenRenderer {

	@OriginalMember(owner = "client!iv", name = "F", descriptor = "Lclient!ae;")
	@JvmField
	var aClass8_1: AssetLoadingScreenRenderer? = null
	@OriginalMember(owner = "client!qw", name = "I", descriptor = "Ljava/lang/Thread;")
	@JvmField
	var loadingScreenThread: Thread? = null
}
