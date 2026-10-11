import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.EntityHoverMenuOptions;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapInteraction;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;

public final class WorldMapRelease {

	@OriginalMember(owner = "client!lk", name = "a", descriptor = "(IZ)V")
	public static void resetWorldmapCaches(@OriginalArg(1) boolean preserveArea) {
		if (preserveArea && WorldMap.area != null) {
			WorldMapDisplay.pendingMapElementId = WorldMap.area.elementId;
		} else {
			WorldMapDisplay.pendingMapElementId = -1;
		}
		WorldMap.area = null;
		WorldMap.worldmapComponent = null;
		ComponentMinimap.mapAssetLoadingPercentage = 0;
		WorldMapInteraction.MEC_CLICKZONES = null;
		WorldMap.freeBuffers();
		MapList.worldMapCategories.clear();
		WorldMapHelvetica22.helvetica22Font = null;
		WorldMapHelvetica17.helvetica17Font = null;
		ComponentMinimap.worldMapOverviewSprite = null;
		WorldMapHelvetica11.helvetica11Font = null;
		WorldMapHelvetica19.helvetica19Font = null;
		WorldMapHelvetica26.helvetica26Font = null;
		WorldMapHelvetica14.helvetica14Font = null;
		MapList.staticElements = null;
		WorldMapHelvetica12.helvetica12Font = null;
		WorldMapDisplay.scrollTargetY = -1;
		WorldMapDisplay.scrollTargetX = -1;
		WorldMapHelvetica30.helvetica30Font = null;
		if (WorldMap.MECTYPE_LIST != null) {
			WorldMap.MECTYPE_LIST.cacheReset();
			WorldMap.MECTYPE_LIST.setCacheSize(64, 128);
		}
		if (MapList.MAP_SPRITE_TYPE_LIST != null) {
			MapList.MAP_SPRITE_TYPE_LIST.setCacheSize(64, 64);
		}
		if (MapList.LOCTYPE_LIST != null) {
			MapList.LOCTYPE_LIST.setCacheSize(64);
		}
		TypeLists.VARBIT.setCacheSize(64);
	}
}
