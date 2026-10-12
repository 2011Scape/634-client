import org.openrs2.deob.annotation.OriginalMember

object AwtLoadingBarReset {

	@OriginalMember(owner = "client!qk", name = "a", descriptor = "(Z)V")
	@JvmStatic
	fun cleanupPlainLoadingBarResources() {
		Static658.canvasImage = null
		AwtLoadingBarPainter.loadingFont = null
	}
}
