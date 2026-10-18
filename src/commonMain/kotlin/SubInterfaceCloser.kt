import com.jagex.game.runetek5.cache.node.HashTableIterator
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.features.HdOptionsScreen
import kotlin.jvm.JvmStatic

object SubInterfaceCloser {

	@OriginalMember(owner = "client!ep", name = "a", descriptor = "(ZZLclient!uba;I)V")
	@JvmStatic
	fun closeSubInterface(@OriginalArg(0) unload: Boolean, @OriginalArg(1) suppressScripts: Boolean, @OriginalArg(2) subInterface: SubInterface?) {
		@Pc(8) val type: Int = subInterface!!.interfaceId
		@Pc(12) val pointer: Int = subInterface.key.toInt()
		if (type == 742) {
			HdOptionsScreen.reset() // Not part of the original client: the 634HD page lives in Graphics Options
		}
		subInterface.unlink()
		if (unload) {
			Interface.unloadInterface(type)
		}
		Interface.clearInterfaceSettings(type)
		@Pc(33) val component: Component? = Interface.getComponent(pointer)
		if (component != null) {
			Component.redrawComponent(component)
		}
		MiniMenu.removeComponentEntries()
		if (!suppressScripts && ClientState.BASE_WINDOW_ID != -1) {
			Static683.runSubInterfaceHooks(ClientState.BASE_WINDOW_ID, 1)
		}
		@Pc(55) val hashTableIterator: HashTableIterator = HashTableIterator(InterfaceManager.OPEN_INTERFACES)
		@Pc(60) var subInterface2: SubInterface? = hashTableIterator.first() as SubInterface?
		while (subInterface2 != null) {
			if (!subInterface2.isLinked()) {
				subInterface2 = hashTableIterator.first() as SubInterface?
				if (subInterface2 == null) {
					return
				}
			}
			if (subInterface2.openType === SubInterfaceType.OVERLAY_CLIENT) {
				@Pc(82) val parentHash: Int = subInterface2.key.toInt()
				if (type == parentHash ushr 16) {
					closeSubInterface(true, suppressScripts, subInterface2)
				}
			}
			subInterface2 = hashTableIterator.next() as SubInterface?
		}
	}
}
