import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import kotlin.jvm.JvmStatic

object SubInterfaceHookRunner {

	@OriginalMember(owner = "client!qu", name = "a", descriptor = "([Lclient!at;II)V")
	@JvmStatic
	fun runInterfaceScripts(@OriginalArg(0) components: Array<Component?>?, @OriginalArg(1) scriptMode: Int) {
		@Pc(7) var i: Int = 0
		while (i < components!!.size) {
			@Pc(13) val component: Component? = components[i]
			if (component != null) {
				if (component.type === ComponentType.CONTAINER) {
					if (component.slotChildren != null) {
						runInterfaceScripts(component.slotChildren, scriptMode)
					}
					@Pc(37) val subInterface: SubInterface? = InterfaceManager.OPEN_INTERFACES.get(component.idHash.toLong()) as SubInterface?
					if (subInterface != null) {
						Static683.runSubInterfaceHooks(subInterface.interfaceId, scriptMode)
					}
				}
				@Pc(56) var hookRequest: HookRequest
				if (scriptMode == 0 && component.onSubClose != null) {
					hookRequest = HookRequest()
					hookRequest.source = component
					hookRequest.params = component.onSubClose
					CS2Interpreter.executeHookInner(hookRequest)
				}
				if (scriptMode == 1 && component.onSubChange != null) {
					if (component.slotId >= 0) {
						@Pc(79) val component2: Component? = Interface.getComponent(component.idHash)
						if (component2 == null || component2.slotChildren == null || component2.slotChildren!!.size <= component.slotId || component !== component2.slotChildren!![component.slotId]) {
							i++
							continue
						}
					}
					hookRequest = HookRequest()
					hookRequest.source = component
					hookRequest.params = component.onSubChange
					CS2Interpreter.executeHookInner(hookRequest)
				}
			}
			i++
		}
	}
}
