import com.jagex.game.runetek5.EntityHoverMenuOptions;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;

public final class PendingWorldMapOpen {

	@OriginalMember(owner = "client!ab", name = "a", descriptor = "(I)V")
	public static void openPendingWorldMap() {
		if (WorldMapDisplay.pendingMapElementId != -1) {
			WorldMapOpener.setWorldMap(false, -1, -1, WorldMapDisplay.pendingMapElementId);
			WorldMapDisplay.pendingMapElementId = -1;
		}
	}
}
