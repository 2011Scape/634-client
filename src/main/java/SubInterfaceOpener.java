import com.jagex.game.runetek5.config.iftype.SubInterfaceType;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.client.ClientState;

public final class SubInterfaceOpener {

	@OriginalMember(owner = "client!ea", name = "a", descriptor = "(IIIZI)Lclient!uba;")
	public static SubInterface openSubInterface(@OriginalArg(0) int interfaceId, @OriginalArg(1) int parentHash, @OriginalArg(2) SubInterfaceType openType, @OriginalArg(3) boolean suppressScripts) {
		@Pc(17) SubInterface subInterface = new SubInterface();
		subInterface.interfaceId = interfaceId;
		subInterface.openType = openType;
		InterfaceManager.OPEN_INTERFACES.put((long) parentHash, subInterface);
		Interface.resetComponentSequences(interfaceId);
		@Pc(38) Component component = Interface.getComponent(parentHash);
		if (component != null) {
			Component.redrawComponent(component);
		}
		if (InterfaceManager.activeDialogueContinue != null) {
			Component.redrawComponent(InterfaceManager.activeDialogueContinue);
			InterfaceManager.activeDialogueContinue = null;
		}
		MiniMenu.removeComponentEntries();
		if (component != null) {
			Component.layoutChildren(!suppressScripts, component);
		}
		if (!suppressScripts) {
			CS2Interpreter.runInterfaceOnLoadScripts(interfaceId);
		}
		if (!suppressScripts && ClientState.BASE_WINDOW_ID != -1) {
			Static683.runSubInterfaceHooks(ClientState.BASE_WINDOW_ID, 1);
		}
		return subInterface;
	}
}
