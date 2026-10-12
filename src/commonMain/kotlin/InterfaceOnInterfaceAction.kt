import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object InterfaceOnInterfaceAction {

	@OriginalMember(owner = "client!op", name = "a", descriptor = "(BLclient!at;)V")
	@JvmStatic
	fun sendInterfaceOnInterface(@OriginalArg(1) component: Component?) {
		if (!MiniMenu.interfaceSelectionActive) {
			return
		}
		if (component!!.onTargetLeave != null) {
			@Pc(23) val component2: Component? = Component.getComponentWithSlot(MiniMenu.slotId, MiniMenuBuilder.USE_OPTIONS_FLAGS)
			if (component2 != null) {
				@Pc(29) val hookRequest: HookRequest = HookRequest()
				hookRequest.source = component
				hookRequest.targetComponent = component2
				hookRequest.params = component.onTargetLeave
				CS2Interpreter.executeHookInner(hookRequest)
			}
		}
		@Pc(46) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.IF_ON_IF, TcpPacket.isaac)
		packet.buffer!!.writeInt(component.idHash)
		packet.buffer!!.writeIntInverseMiddle(MiniMenuBuilder.USE_OPTIONS_FLAGS)
		packet.buffer!!.writeShortAdd(MiniMenu.slotId)
		packet.buffer!!.writeShort(MiniMenu.selectedItemId)
		packet.buffer!!.writeShortAdd(component.slotId)
		packet.buffer!!.writeShort(component.containerItemId)
		BufferedConnectionContext.queuePacket(packet)
	}
}
