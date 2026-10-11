import com.jagex.game.runetek5.core.constants.MiniMenuAction;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.ClientTrigger;

public final class MiniMenuTriggerDispatch {

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(IIII)V")
	public static void onWorldmapMinimenuClick(@OriginalArg(0) int clickType, @OriginalArg(1) int param0, @OriginalArg(2) int uid) {
		if (clickType == MiniMenuAction.OP_MAPELEMENT1) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_3, uid, param0);
		} else if (clickType == MiniMenuAction.OP_MAPELEMENT2) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_6, uid, param0);
		} else if (clickType == MiniMenuAction.OP_MAPELEMENT3) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_10, uid, param0);
		} else if (clickType == MiniMenuAction.OP_MAPELEMENT4) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_1, uid, param0);
		} else if (clickType == MiniMenuAction.OP_MAPELEMENT5) {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_9, uid, param0);
		}
	}
}
