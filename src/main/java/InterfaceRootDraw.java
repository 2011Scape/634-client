import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.client.ClientState;

public final class InterfaceRootDraw {

	@OriginalMember(owner = "client!ow", name = "a", descriptor = "(IIIIIIIBI)V")
	public static void drawInterface(@OriginalArg(0) int offsetY, @OriginalArg(1) int interfaceId, @OriginalArg(2) int clipStartY, @OriginalArg(3) int layerIndex, @OriginalArg(4) int offsetX, @OriginalArg(5) int clipEndX, @OriginalArg(6) int clipEndY, @OriginalArg(8) int clipStartX) {
		@Pc(14) int local14;
		if (InterfaceManager.loadInterface(interfaceId)) {
			int savedDirtyMinX = 0;
			@Pc(36) int savedDirtyMinY = 0;
			@Pc(38) int savedDirtyMaxX = 0;
			@Pc(40) int savedDirtyMaxY = 0;
			@Pc(42) int mediumPos = 0;
			if (OrthoMode.orthoEnabled) {
				savedDirtyMinX = OrthoMode.dirtyAreaMinX;
				mediumPos = Packet.mediumPos;
				savedDirtyMaxX = OrthoMode.dirtyAreaMaxX;
				savedDirtyMinY = LoginManager.dirtyAreaMinY;
				savedDirtyMaxY = OrthoMode.dirtyAreaMaxY;
				Packet.mediumPos = 1;
			}
			if (Interface.aClass24ArrayArray1[interfaceId] == null) {
				InterfaceComponentDraw.drawComponents(clipStartX, offsetY, layerIndex < 0, offsetX, clipStartY, layerIndex, clipEndX, Interface.interfaces[interfaceId], -1, clipEndY);
			} else {
				InterfaceComponentDraw.drawComponents(clipStartX, offsetY, layerIndex < 0, offsetX, clipStartY, layerIndex, clipEndX, Interface.aClass24ArrayArray1[interfaceId], -1, clipEndY);
			}
			if (OrthoMode.orthoEnabled) {
				if (layerIndex >= 0 && Packet.mediumPos == 2) {
					OrthoMode.markOrthoTilesDirty(OrthoMode.dirtyAreaMinX, OrthoMode.dirtyAreaMaxX, LoginManager.dirtyAreaMinY, OrthoMode.dirtyAreaMaxY);
				}
				Packet.mediumPos = mediumPos;
				OrthoMode.dirtyAreaMaxX = savedDirtyMaxX;
				LoginManager.dirtyAreaMinY = savedDirtyMinY;
				OrthoMode.dirtyAreaMaxY = savedDirtyMaxY;
				OrthoMode.dirtyAreaMinX = savedDirtyMinX;
			}
		} else if (layerIndex == -1) {
			for (int rectangleIndex = 0; rectangleIndex < 100; rectangleIndex++) {
				InterfaceRectangles.IS_COMPONENT_ON_TOP[rectangleIndex] = true;
			}
		} else {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[layerIndex] = true;
		}
	}
}
