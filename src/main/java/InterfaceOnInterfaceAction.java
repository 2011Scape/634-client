import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.HookRequest;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.client.ClientState;

public final class InterfaceOnInterfaceAction {

	@OriginalMember(owner = "client!op", name = "a", descriptor = "(BLclient!at;)V")
	public static void sendInterfaceOnInterface(@OriginalArg(1) Component component) {
		if (!MiniMenu.interfaceSelectionActive) {
			return;
		}
		if (component.onTargetLeave != null) {
			@Pc(23) Component component2 = Component.getComponentWithSlot(MiniMenu.slotId, MiniMenuBuilder.USE_OPTIONS_FLAGS);
			if (component2 != null) {
				@Pc(29) HookRequest hookRequest = new HookRequest();
				hookRequest.source = component;
				hookRequest.targetComponent = component2;
				hookRequest.params = component.onTargetLeave;
				CS2Interpreter.executeHookInner(hookRequest);
			}
		}
		@Pc(46) TcpPacket packet = TcpPacket.createPacket(ClientProt.IF_ON_IF, TcpPacket.isaac);
		packet.buffer.writeInt(component.idHash);
		packet.buffer.writeIntInverseMiddle(MiniMenuBuilder.USE_OPTIONS_FLAGS);
		packet.buffer.writeShortAdd(MiniMenu.slotId);
		packet.buffer.writeShort(MiniMenu.selectedItemId);
		packet.buffer.writeShortAdd(component.slotId);
		packet.buffer.writeShort(component.containerItemId);
		BufferedConnectionContext.queuePacket(packet);
	}
}
