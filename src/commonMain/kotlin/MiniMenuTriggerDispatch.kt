import com.jagex.game.runetek5.core.constants.MiniMenuAction
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.ClientTrigger
import kotlin.jvm.JvmStatic

object MiniMenuTriggerDispatch {

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(IIII)V")
	@JvmStatic
	fun onWorldmapMinimenuClick(@OriginalArg(0) clickType: Int, @OriginalArg(1) param0: Int, @OriginalArg(2) uid: Int) {
		if (clickType == MiniMenuAction.OP_MAPELEMENT1) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_3, uid, param0)
		} else if (clickType == MiniMenuAction.OP_MAPELEMENT2) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_6, uid, param0)
		} else if (clickType == MiniMenuAction.OP_MAPELEMENT3) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_10, uid, param0)
		} else if (clickType == MiniMenuAction.OP_MAPELEMENT4) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_1, uid, param0)
		} else if (clickType == MiniMenuAction.OP_MAPELEMENT5) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_9, uid, param0)
		}
	}
}
