import com.jagex.game.runetek5.statics.BrowserJavaScript
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.ModeWhere
import kotlin.jvm.JvmStatic

object ToolbarRefresh {

	@OriginalMember(owner = "client!pda", name = "f", descriptor = "(I)V")
	@JvmStatic
	fun refreshToolbar() {
		if (ModeWhere.aClass107_5 !== ServerEnvironment.LIVE) {
			try {
				BrowserJavaScript.callJavaScript(ActiveClient.activeClient, "tbrefresh")
			} catch (@Pc(18) ex: Throwable) {
			}
		}
	}
}
