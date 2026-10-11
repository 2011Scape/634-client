import com.jagex.game.runetek5.config.iftype.SubInterfaceType;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.client.ClientState;

public final class ModalInterfaceClose {

	@OriginalMember(owner = "client!va", name = "a", descriptor = "(BZ)V")
	public static void closeInterface() {
		@Pc(19) TcpPacket packet = TcpPacket.createPacket(ClientProt.CLOSE_MODAL, TcpPacket.isaac);
		BufferedConnectionContext.queuePacket(packet);
		for (@Pc(27) SubInterface subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.first(); subInterface != null; subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.next()) {
			if (!subInterface.isLinked()) {
				subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.first();
				if (subInterface == null) {
					break;
				}
			}
			if (subInterface.openType == SubInterfaceType.MODAL) {
				SubInterfaceCloser.closeSubInterface(true, true, subInterface);
			}
		}
		if (InterfaceManager.activeDialogueContinue != null) {
			Component.redrawComponent(InterfaceManager.activeDialogueContinue);
			InterfaceManager.activeDialogueContinue = null;
		}
	}
}
