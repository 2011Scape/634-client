import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object InterfaceRootDraw {

	@OriginalMember(owner = "client!ow", name = "a", descriptor = "(IIIIIIIBI)V")
	@JvmStatic
	fun drawInterface(@OriginalArg(0) offsetY: Int, @OriginalArg(1) interfaceId: Int, @OriginalArg(2) clipStartY: Int, @OriginalArg(3) layerIndex: Int, @OriginalArg(4) offsetX: Int, @OriginalArg(5) clipEndX: Int, @OriginalArg(6) clipEndY: Int, @OriginalArg(8) clipStartX: Int) {
		@Pc(14) var local14: Int
		if (InterfaceManager.loadInterface(interfaceId)) {
			var savedDirtyMinX: Int = 0
			@Pc(36) var savedDirtyMinY: Int = 0
			@Pc(38) var savedDirtyMaxX: Int = 0
			@Pc(40) var savedDirtyMaxY: Int = 0
			@Pc(42) var mediumPos: Int = 0
			if (OrthoMode.orthoEnabled) {
				savedDirtyMinX = OrthoMode.dirtyAreaMinX
				mediumPos = Packet.mediumPos
				savedDirtyMaxX = OrthoMode.dirtyAreaMaxX
				savedDirtyMinY = LoginManager.dirtyAreaMinY
				savedDirtyMaxY = OrthoMode.dirtyAreaMaxY
				Packet.mediumPos = 1
			}
			if (Interface.aClass24ArrayArray1!![interfaceId] == null) {
				InterfaceComponentDraw.drawComponents(clipStartX, offsetY, layerIndex < 0, offsetX, clipStartY, layerIndex, clipEndX, Interface.interfaces!![interfaceId], -1, clipEndY)
			} else {
				InterfaceComponentDraw.drawComponents(clipStartX, offsetY, layerIndex < 0, offsetX, clipStartY, layerIndex, clipEndX, Interface.aClass24ArrayArray1!![interfaceId], -1, clipEndY)
			}
			if (OrthoMode.orthoEnabled) {
				if (layerIndex >= 0 && Packet.mediumPos == 2) {
					OrthoMode.markOrthoTilesDirty(OrthoMode.dirtyAreaMinX, OrthoMode.dirtyAreaMaxX, LoginManager.dirtyAreaMinY, OrthoMode.dirtyAreaMaxY)
				}
				Packet.mediumPos = mediumPos
				OrthoMode.dirtyAreaMaxX = savedDirtyMaxX
				LoginManager.dirtyAreaMinY = savedDirtyMinY
				OrthoMode.dirtyAreaMaxY = savedDirtyMaxY
				OrthoMode.dirtyAreaMinX = savedDirtyMinX
			}
		} else if (layerIndex == -1) {
			for (rectangleIndex in 0 until 100) {
				InterfaceRectangles.IS_COMPONENT_ON_TOP[rectangleIndex] = true
			}
		} else {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[layerIndex] = true
		}
	}
}
