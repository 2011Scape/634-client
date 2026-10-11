import com.jagex.game.runetek5.statics.WorldMapRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;

public final class WorldMapZoomFont {

	@OriginalMember(owner = "client!pr", name = "d", descriptor = "(II)Lclient!cs;")
	public static HelveticaFont getZoomedHelveticafont(@OriginalArg(0) int predefinedTextZoom) {
		if (predefinedTextZoom == 0) {
			if ((double) WorldMap.mapZoom == 3.0D) {
				return WorldMapHelvetica11.helvetica11Font;
			}
			if ((double) WorldMap.mapZoom == 4.0D) {
				return WorldMapHelvetica12.helvetica12Font;
			}
			if ((double) WorldMap.mapZoom == 6.0D) {
				return WorldMapHelvetica14.helvetica14Font;
			}
			if ((double) WorldMap.mapZoom >= 8.0D) {
				return WorldMapHelvetica17.helvetica17Font;
			}
		} else if (predefinedTextZoom == 1) {
			if ((double) WorldMap.mapZoom == 3.0D) {
				return WorldMapHelvetica14.helvetica14Font;
			}
			if ((double) WorldMap.mapZoom == 4.0D) {
				return WorldMapHelvetica17.helvetica17Font;
			}
			if ((double) WorldMap.mapZoom == 6.0D) {
				return WorldMapHelvetica19.helvetica19Font;
			}
			if ((double) WorldMap.mapZoom >= 8.0D) {
				return WorldMapHelvetica22.helvetica22Font;
			}
		} else if (predefinedTextZoom == 2) {
			if ((double) WorldMap.mapZoom == 3.0D) {
				return WorldMapHelvetica19.helvetica19Font;
			}
			if ((double) WorldMap.mapZoom == 4.0D) {
				return WorldMapHelvetica22.helvetica22Font;
			}
			if ((double) WorldMap.mapZoom == 6.0D) {
				return WorldMapHelvetica26.helvetica26Font;
			}
			if ((double) WorldMap.mapZoom >= 8.0D) {
				return WorldMapHelvetica30.helvetica30Font;
			}
		}
		return null;
	}
}
