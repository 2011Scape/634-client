import com.jagex.game.runetek5.cache.node.HashTableIterator;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.config.iftype.SubInterfaceType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.client.ClientState;

public final class SubInterfaceCloser {

	@OriginalMember(owner = "client!ep", name = "a", descriptor = "(ZZLclient!uba;I)V")
	public static void closeSubInterface(@OriginalArg(0) boolean unload, @OriginalArg(1) boolean suppressScripts, @OriginalArg(2) SubInterface subInterface) {
		@Pc(8) int type = subInterface.interfaceId;
		@Pc(12) int pointer = (int) subInterface.key;
		subInterface.unlink();
		if (unload) {
			Interface.unloadInterface(type);
		}
		Interface.clearInterfaceSettings(type);
		@Pc(33) Component component = Interface.getComponent(pointer);
		if (component != null) {
			Component.redrawComponent(component);
		}
		MiniMenu.removeComponentEntries();
		if (!suppressScripts && ClientState.BASE_WINDOW_ID != -1) {
			Static683.runSubInterfaceHooks(ClientState.BASE_WINDOW_ID, 1);
		}
		@Pc(55) HashTableIterator hashTableIterator = new HashTableIterator(InterfaceManager.OPEN_INTERFACES);
		for (@Pc(60) SubInterface subInterface2 = (SubInterface) hashTableIterator.first(); subInterface2 != null; subInterface2 = (SubInterface) hashTableIterator.next()) {
			if (!subInterface2.isLinked()) {
				subInterface2 = (SubInterface) hashTableIterator.first();
				if (subInterface2 == null) {
					return;
				}
			}
			if (subInterface2.openType == SubInterfaceType.OVERLAY_CLIENT) {
				@Pc(82) int parentHash = (int) subInterface2.key;
				if (type == parentHash >>> 16) {
					closeSubInterface(true, suppressScripts, subInterface2);
				}
			}
		}
	}
}
