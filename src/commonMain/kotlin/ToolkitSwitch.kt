import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object ToolkitSwitch {

	@OriginalMember(owner = "client!ld", name = "a", descriptor = "(ZBI)V")
	@JvmStatic
	fun loadAssets(@OriginalArg(0) skipSettingsVerification: Boolean, @OriginalArg(2) toolkitId: Int) {
		ToolkitRebuild.loadAssets(LocalizedText.aClass351_14.get(ClientState.language), skipSettingsVerification, toolkitId)
	}
}
