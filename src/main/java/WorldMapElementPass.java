import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.config.mec.MECType;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapInteraction;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;

public final class WorldMapElementPass {

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(IILclient!uh;ILclient!ha;)V")
	public static void renderInformation(@OriginalArg(2) Deque deque, @OriginalArg(4) Toolkit toolkit) {
		WorldMapInteraction.MEC_CLICKZONES.clear();
		if (WorldMapDisplay.hideIcons) {
			return;
		}
		for (@Pc(16) WorldMapCategory mapElementListEntry = (WorldMapCategory) deque.first(); mapElementListEntry != null; mapElementListEntry = (WorldMapCategory) deque.next()) {
			@Pc(24) MECType mapElementType = WorldMap.MECTYPE_LIST.list(mapElementListEntry.id);
			if (WorldMapDisplay.shouldDraw(mapElementType)) {
				@Pc(36) boolean offscreen = WorldMapElementDraw.renderIconsAndNames(mapElementType, mapElementListEntry, toolkit);
				if (offscreen) {
					WorldMapOffscreenMarker.renderOffscreenIndicator(mapElementListEntry, toolkit, mapElementType);
				}
			}
		}
	}
}
