import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.config.rsinterface.HookRequest;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;

public final class SubInterfaceHookRunner {

	@OriginalMember(owner = "client!qu", name = "a", descriptor = "([Lclient!at;II)V")
	public static void runInterfaceScripts(@OriginalArg(0) Component[] components, @OriginalArg(1) int scriptMode) {
		for (@Pc(7) int i = 0; i < components.length; i++) {
			@Pc(13) Component component = components[i];
			if (component != null) {
				if (component.type == ComponentType.CONTAINER) {
					if (component.slotChildren != null) {
						runInterfaceScripts(component.slotChildren, scriptMode);
					}
					@Pc(37) SubInterface subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) component.idHash);
					if (subInterface != null) {
						Static683.runSubInterfaceHooks(subInterface.interfaceId, scriptMode);
					}
				}
				@Pc(56) HookRequest hookRequest;
				if (scriptMode == 0 && component.onSubClose != null) {
					hookRequest = new HookRequest();
					hookRequest.source = component;
					hookRequest.params = component.onSubClose;
					CS2Interpreter.executeHookInner(hookRequest);
				}
				if (scriptMode == 1 && component.onSubChange != null) {
					if (component.slotId >= 0) {
						@Pc(79) Component component2 = Interface.getComponent(component.idHash);
						if (component2 == null || component2.slotChildren == null || component2.slotChildren.length <= component.slotId || component != component2.slotChildren[component.slotId]) {
							continue;
						}
					}
					hookRequest = new HookRequest();
					hookRequest.source = component;
					hookRequest.params = component.onSubChange;
					CS2Interpreter.executeHookInner(hookRequest);
				}
			}
		}
	}
}
