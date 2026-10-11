import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.HookRequest;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.client.ClientState;

public final class InterfaceOptionInteract {

	@OriginalMember(owner = "client!hf", name = "a", descriptor = "(ILjava/lang/String;BII)V")
	public static void ifOptionInteract(@OriginalArg(0) int slotId, @OriginalArg(1) String optionName, @OriginalArg(3) int option, @OriginalArg(4) int componentHash) {
		@Pc(8) Component component = Component.getComponentWithSlot(slotId, componentHash);
		if (component == null) {
			return;
		}
		if (component.params != null) {
			@Pc(18) HookRequest hookRequest = new HookRequest();
			hookRequest.source = component;
			hookRequest.optionName = optionName;
			hookRequest.params = component.params;
			hookRequest.clickedOption = option;
			CS2Interpreter.executeHookInner(hookRequest);
		}
		if (ClientState.mainLogicStep != MainLogicStep.STEP_GAME_SCREEN || !InterfaceManager.getComponentSettings(component).isOpEnabled(option - 1)) {
			return;
		}
		@Pc(60) TcpPacket packet;
		if (option == 1) {
			packet = TcpPacket.createPacket(ClientProt.IF_BUTTON1, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet);
			BufferedConnectionContext.queuePacket(packet);
		}
		if (option == 2) {
			packet = TcpPacket.createPacket(ClientProt.IF_BUTTON2, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet);
			BufferedConnectionContext.queuePacket(packet);
		}
		if (option == 3) {
			packet = TcpPacket.createPacket(ClientProt.IF_BUTTON3, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet);
			BufferedConnectionContext.queuePacket(packet);
		}
		if (option == 4) {
			packet = TcpPacket.createPacket(ClientProt.IF_BUTTON4, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet);
			BufferedConnectionContext.queuePacket(packet);
		}
		@Pc(160) TcpPacket packet2;
		if (option == 5) {
			packet2 = TcpPacket.createPacket(ClientProt.IF_BUTTON5, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet2);
			BufferedConnectionContext.queuePacket(packet2);
		}
		if (option == 6) {
			packet2 = TcpPacket.createPacket(ClientProt.IF_BUTTON6, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet2);
			BufferedConnectionContext.queuePacket(packet2);
		}
		if (option == 7) {
			packet2 = TcpPacket.createPacket(ClientProt.IF_BUTTON7, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet2);
			BufferedConnectionContext.queuePacket(packet2);
		}
		if (option == 8) {
			packet2 = TcpPacket.createPacket(ClientProt.IF_BUTTON8, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet2);
			BufferedConnectionContext.queuePacket(packet2);
		}
		if (option == 9) {
			packet2 = TcpPacket.createPacket(ClientProt.IF_BUTTON9, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet2);
			BufferedConnectionContext.queuePacket(packet2);
		}
		if (option == 10) {
			packet2 = TcpPacket.createPacket(ClientProt.IF_BUTTON10, TcpPacket.isaac);
			TcpPacket.writeInterfaceOptionPayload(componentHash, slotId, component.containerItemId, packet2);
			BufferedConnectionContext.queuePacket(packet2);
		}
	}
}
