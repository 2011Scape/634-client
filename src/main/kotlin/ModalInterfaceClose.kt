import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.ClientState

object ModalInterfaceClose {

	@OriginalMember(owner = "client!va", name = "a", descriptor = "(BZ)V")
	@JvmStatic
	fun closeInterface() {
		@Pc(19) val packet: TcpPacket? = TcpPacket.createPacket(ClientProt.CLOSE_MODAL, TcpPacket.isaac)
		BufferedConnectionContext.queuePacket(packet)
		@Pc(27) var subInterface: SubInterface? = InterfaceManager.OPEN_INTERFACES.first() as SubInterface?
		while (subInterface != null) {
			if (!subInterface.isLinked()) {
				subInterface = InterfaceManager.OPEN_INTERFACES.first() as SubInterface?
				if (subInterface == null) {
					break
				}
			}
			if (subInterface.openType === SubInterfaceType.MODAL) {
				SubInterfaceCloser.closeSubInterface(true, true, subInterface)
			}
			subInterface = InterfaceManager.OPEN_INTERFACES.next() as SubInterface?
		}
		if (InterfaceManager.activeDialogueContinue != null) {
			Component.redrawComponent(InterfaceManager.activeDialogueContinue)
			InterfaceManager.activeDialogueContinue = null
		}
	}
}
