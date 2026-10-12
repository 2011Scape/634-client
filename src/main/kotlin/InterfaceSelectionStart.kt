import com.jagex.game.runetek5.network.world.WorldList
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder
import com.jagex.game.runetek5.Cursor
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.ClientState

object InterfaceSelectionStart {

	@OriginalMember(owner = "client!eaa", name = "a", descriptor = "(IILclient!at;B)V")
	@JvmStatic
	fun startInterfaceSelection(@OriginalArg(0) interfaceId: Int, @OriginalArg(1) useOptionFlags: Int, @OriginalArg(2) component: Component?) {
		if (component == null) {
			return
		}
		if (component.onTargetEnter != null) {
			@Pc(13) val hookRequest: HookRequest = HookRequest()
			hookRequest.params = component.onTargetEnter
			hookRequest.source = component
			CS2Interpreter.executeHookInner(hookRequest)
		}
		Cursor.interfaceCursorId = component.targetLeaveCursor
		MiniMenu.targetParamId = interfaceId
		MiniMenu.useTargetMask = useOptionFlags
		MiniMenu.selectedItemId = component.containerItemId
		MiniMenu.interfaceSelectionActive = true
		MiniMenu.slotId = component.slotId
		MiniMenu.useOnCursor = component.targetOverCursor
		MiniMenuBuilder.USE_OPTIONS_FLAGS = component.idHash
		Component.redrawComponent(component)
	}
}
