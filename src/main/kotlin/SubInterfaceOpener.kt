import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.ClientState

object SubInterfaceOpener {

	@OriginalMember(owner = "client!ea", name = "a", descriptor = "(IIIZI)Lclient!uba;")
	@JvmStatic
	fun openSubInterface(@OriginalArg(0) interfaceId: Int, @OriginalArg(1) parentHash: Int, @OriginalArg(2) openType: SubInterfaceType?, @OriginalArg(3) suppressScripts: Boolean): SubInterface? {
		@Pc(17) val subInterface: SubInterface = SubInterface()
		subInterface.interfaceId = interfaceId
		subInterface.openType = openType
		InterfaceManager.OPEN_INTERFACES.put(parentHash.toLong(), subInterface)
		Interface.resetComponentSequences(interfaceId)
		@Pc(38) val component: Component? = Interface.getComponent(parentHash)
		if (component != null) {
			Component.redrawComponent(component)
		}
		if (InterfaceManager.activeDialogueContinue != null) {
			Component.redrawComponent(InterfaceManager.activeDialogueContinue)
			InterfaceManager.activeDialogueContinue = null
		}
		MiniMenu.removeComponentEntries()
		if (component != null) {
			Component.layoutChildren(!suppressScripts, component)
		}
		if (!suppressScripts) {
			CS2Interpreter.runInterfaceOnLoadScripts(interfaceId)
		}
		if (!suppressScripts && ClientState.BASE_WINDOW_ID != -1) {
			Static683.runSubInterfaceHooks(ClientState.BASE_WINDOW_ID, 1)
		}
		return subInterface
	}
}
