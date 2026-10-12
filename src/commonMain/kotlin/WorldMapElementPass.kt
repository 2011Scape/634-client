import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.config.mec.MECType
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapInteraction
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import kotlin.jvm.JvmStatic

object WorldMapElementPass {

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(IILclient!uh;ILclient!ha;)V")
	@JvmStatic
	fun renderInformation(@OriginalArg(2) deque: Deque?, @OriginalArg(4) toolkit: Toolkit?) {
		WorldMapInteraction.MEC_CLICKZONES!!.clear()
		if (WorldMapDisplay.hideIcons) {
			return
		}
		@Pc(16) var mapElementListEntry: WorldMapCategory? = deque!!.first() as WorldMapCategory?
		while (mapElementListEntry != null) {
			@Pc(24) val mapElementType: MECType? = WorldMap.MECTYPE_LIST!!.list(mapElementListEntry.id)
			if (WorldMapDisplay.shouldDraw(mapElementType)) {
				@Pc(36) val offscreen: Boolean = WorldMapElementDraw.renderIconsAndNames(mapElementType, mapElementListEntry, toolkit)
				if (offscreen) {
					WorldMapOffscreenMarker.renderOffscreenIndicator(mapElementListEntry, toolkit, mapElementType)
				}
			}
			mapElementListEntry = deque.next() as WorldMapCategory?
		}
	}
}
