import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.preference.GamePreferences
import kotlin.jvm.JvmStatic

object WorldMapOpener {

	@OriginalMember(owner = "client!ap", name = "a", descriptor = "(ZIIII)V")
	@JvmStatic
	fun setWorldMap(@OriginalArg(0) useTargetPosition: Boolean, @OriginalArg(1) scrollY: Int, @OriginalArg(2) scrollX: Int, @OriginalArg(3) mapAreaId: Int) {
		if (GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.JAVA) {
			WorldMapRelease.resetWorldmapCaches(false)
		} else {
			WorldMap.savedToolkitId = GamePreferences.uid!!.toolkit!!.getValue()
			ToolkitSwitch.loadAssets(true, 0)
		}
		LoginManager.worldMapUseTargetPosition = useTargetPosition
		WorldMapDisplay.worldMapRequestedScrollY = scrollY
		InterfaceComponentRenderer.worldMapRequestedScrollX = scrollX
		WorldMap.setArea(mapAreaId)
	}
}
