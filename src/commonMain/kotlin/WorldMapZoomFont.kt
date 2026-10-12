import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import kotlin.jvm.JvmStatic

object WorldMapZoomFont {

	@OriginalMember(owner = "client!pr", name = "d", descriptor = "(II)Lclient!cs;")
	@JvmStatic
	fun getZoomedHelveticafont(@OriginalArg(0) predefinedTextZoom: Int): HelveticaFont? {
		if (predefinedTextZoom == 0) {
			if (WorldMap.mapZoom.toDouble() == 3.0) {
				return WorldMapHelvetica11.helvetica11Font
			}
			if (WorldMap.mapZoom.toDouble() == 4.0) {
				return WorldMapHelvetica12.helvetica12Font
			}
			if (WorldMap.mapZoom.toDouble() == 6.0) {
				return WorldMapHelvetica14.helvetica14Font
			}
			if (WorldMap.mapZoom.toDouble() >= 8.0) {
				return WorldMapHelvetica17.helvetica17Font
			}
		} else if (predefinedTextZoom == 1) {
			if (WorldMap.mapZoom.toDouble() == 3.0) {
				return WorldMapHelvetica14.helvetica14Font
			}
			if (WorldMap.mapZoom.toDouble() == 4.0) {
				return WorldMapHelvetica17.helvetica17Font
			}
			if (WorldMap.mapZoom.toDouble() == 6.0) {
				return WorldMapHelvetica19.helvetica19Font
			}
			if (WorldMap.mapZoom.toDouble() >= 8.0) {
				return WorldMapHelvetica22.helvetica22Font
			}
		} else if (predefinedTextZoom == 2) {
			if (WorldMap.mapZoom.toDouble() == 3.0) {
				return WorldMapHelvetica19.helvetica19Font
			}
			if (WorldMap.mapZoom.toDouble() == 4.0) {
				return WorldMapHelvetica22.helvetica22Font
			}
			if (WorldMap.mapZoom.toDouble() == 6.0) {
				return WorldMapHelvetica26.helvetica26Font
			}
			if (WorldMap.mapZoom.toDouble() >= 8.0) {
				return WorldMapHelvetica30.helvetica30Font
			}
		}
		return null
	}
}
