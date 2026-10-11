import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.InterfaceManager;

public final class InterfaceRootInput {

	@OriginalMember(owner = "client!qca", name = "a", descriptor = "(IIIIIIIIIIII)V")
	public static void processInterfaceInput(@OriginalArg(0) int interfaceId, @OriginalArg(1) int offsetX, @OriginalArg(2) int clipStartX, @OriginalArg(3) int clipEndX, @OriginalArg(4) int rawMouseY, @OriginalArg(5) int rawMouseX, @OriginalArg(7) int mouseX, @OriginalArg(8) int clipEndY, @OriginalArg(9) int offsetY, @OriginalArg(10) int mouseY, @OriginalArg(11) int clipStartY) {
		if (!InterfaceManager.loadInterface(interfaceId)) {
			return;
		}
		if (Interface.aClass24ArrayArray1[interfaceId] == null) {
			InterfaceComponentInput.processInterfaceCycle(Interface.interfaces[interfaceId], -1, clipStartX, clipStartY, clipEndX, clipEndY, offsetX, offsetY, rawMouseX, rawMouseY, mouseX, mouseY);
		} else {
			InterfaceComponentInput.processInterfaceCycle(Interface.aClass24ArrayArray1[interfaceId], -1, clipStartX, clipStartY, clipEndX, clipEndY, offsetX, offsetY, rawMouseX, rawMouseY, mouseX, mouseY);
		}
	}
}
