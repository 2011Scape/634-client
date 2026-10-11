import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;

public final class WorldMapExitToolkitRestore {

	@OriginalMember(owner = "client!cda", name = "b", descriptor = "(I)V")
	public static void releaseWorldMapRestoreToolkit() {
		WorldMapRelease.resetWorldmapCaches(false);
		if (WorldMap.savedToolkitId >= 0 && WorldMap.savedToolkitId != 0) {
			ToolkitSwitch.loadAssets(false, WorldMap.savedToolkitId);
			WorldMap.savedToolkitId = -1;
		}
	}
}
