import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.InterfaceManager

object InterfaceRootInput {

	@OriginalMember(owner = "client!qca", name = "a", descriptor = "(IIIIIIIIIIII)V")
	@JvmStatic
	fun processInterfaceInput(@OriginalArg(0) interfaceId: Int, @OriginalArg(1) offsetX: Int, @OriginalArg(2) clipStartX: Int, @OriginalArg(3) clipEndX: Int, @OriginalArg(4) rawMouseY: Int, @OriginalArg(5) rawMouseX: Int, @OriginalArg(7) mouseX: Int, @OriginalArg(8) clipEndY: Int, @OriginalArg(9) offsetY: Int, @OriginalArg(10) mouseY: Int, @OriginalArg(11) clipStartY: Int) {
		if (!InterfaceManager.loadInterface(interfaceId)) {
			return
		}
		if (Interface.aClass24ArrayArray1!![interfaceId] == null) {
			InterfaceComponentInput.processInterfaceCycle(Interface.interfaces!![interfaceId], -1, clipStartX, clipStartY, clipEndX, clipEndY, offsetX, offsetY, rawMouseX, rawMouseY, mouseX, mouseY)
		} else {
			InterfaceComponentInput.processInterfaceCycle(Interface.aClass24ArrayArray1!![interfaceId], -1, clipStartX, clipStartY, clipEndX, clipEndY, offsetX, offsetY, rawMouseX, rawMouseY, mouseX, mouseY)
		}
	}
}
