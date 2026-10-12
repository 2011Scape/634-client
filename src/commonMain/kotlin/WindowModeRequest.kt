import com.jagex.game.runetek5.client.window.WindowModeType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object WindowModeRequest {

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(IIBZI)V")
	@JvmStatic
	fun requestWindowMode(@OriginalArg(0) windowMode: WindowModeType?, @OriginalArg(1) width: Int, @OriginalArg(3) replaceCanvas: Boolean, @OriginalArg(4) height: Int) {
		var replaceCanvas: Boolean = replaceCanvas
		OrthoMode.applyOrthographicMode()
		WindowMode.nextDimensionUpdateCheck = 0L
		@Pc(10) val activeMode: WindowModeType? = WindowMode.getWindowMode()
		if (windowMode === WindowModeType.FULLSCREEN || activeMode === WindowModeType.FULLSCREEN) {
			replaceCanvas = true
		}
		if (!ClientState.activeToolkit!!.forceFixedScreenmode()) {
			replaceCanvas = true
		}
		WindowModeApply.applyWindowMode(replaceCanvas, width, activeMode, windowMode, height)
	}
}
