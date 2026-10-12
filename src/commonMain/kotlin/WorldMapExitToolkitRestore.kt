import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import kotlin.jvm.JvmStatic

object WorldMapExitToolkitRestore {

	@OriginalMember(owner = "client!cda", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun releaseWorldMapRestoreToolkit() {
		WorldMapRelease.resetWorldmapCaches(false)
		if (WorldMap.savedToolkitId >= 0 && WorldMap.savedToolkitId != 0) {
			ToolkitSwitch.loadAssets(false, WorldMap.savedToolkitId)
			WorldMap.savedToolkitId = -1
		}
	}
}
