import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks
import com.jagex.game.runetek5.graphics.map.worldmap.ClickzoneMEC
import com.jagex.game.runetek5.config.mec.MECType
import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.JSObjectManager
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapInteraction
import com.jagex.game.runetek5.trigger.ClientTriggerType
import kotlin.jvm.JvmStatic

object WorldMapZoomAnimation {

	@OriginalMember(owner = "client!si", name = "a", descriptor = "(III)V")
	@JvmStatic
	fun ifWorldmapHover(@OriginalArg(1) mouseX: Int, @OriginalArg(2) mouseY: Int) {
		if (WorldMapRenderer.requestedMapZoom > WorldMap.mapZoom) {
			WorldMap.mapZoom = (WorldMap.mapZoom.toDouble() + WorldMap.mapZoom.toDouble() / 30.0).toFloat()
			if (WorldMapRenderer.requestedMapZoom < WorldMap.mapZoom) {
				WorldMap.mapZoom = WorldMapRenderer.requestedMapZoom
			}
			WorldMapDisplay.clampMapCenter()
			WorldMap.tileSize = WorldMap.mapZoom.toInt() shr 1
			WorldMap.tileShapePatterns = WorldMap.createTileShapePatterns(WorldMap.tileSize)
		} else if (WorldMap.mapZoom > WorldMapRenderer.requestedMapZoom) {
			WorldMap.mapZoom = (WorldMap.mapZoom.toDouble() - WorldMap.mapZoom.toDouble() / 30.0).toFloat()
			if (WorldMapRenderer.requestedMapZoom > WorldMap.mapZoom) {
				WorldMap.mapZoom = WorldMapRenderer.requestedMapZoom
			}
			WorldMapDisplay.clampMapCenter()
			WorldMap.tileSize = WorldMap.mapZoom.toInt() shr 1
			WorldMap.tileShapePatterns = WorldMap.createTileShapePatterns(WorldMap.tileSize)
		}
		if (WorldMapDisplay.scrollTargetX != -1 && WorldMapDisplay.scrollTargetY != -1) {
			@Pc(82) var dx: Int = WorldMapDisplay.scrollTargetX - WorldMapDisplay.mapCenterX
			if (dx < 2 || dx > 2) {
				dx /= 8
			}
			@Pc(97) var dy: Int = WorldMapDisplay.scrollTargetY - WorldMapDisplay.mapCenterY
			WorldMapDisplay.mapCenterX += dx
			if (dy < 2 || dy > 2) {
				dy /= 8
			}
			WorldMapDisplay.mapCenterY -= -dy
			if (dx == 0 && dy == 0) {
				WorldMapDisplay.scrollTargetX = -1
				WorldMapDisplay.scrollTargetY = -1
			}
			WorldMapDisplay.clampMapCenter()
		}
		if (JSObjectManager.highlightFlashCycles > 0) {
			ServerEnvironment.flashCycleCounter--
			if (ServerEnvironment.flashCycleCounter == 0) {
				JSObjectManager.highlightFlashCycles--
				ServerEnvironment.flashCycleCounter = 100
			}
		} else {
			WorldMapDisplay.someMecId = -1
			WorldMapDisplay.highlightedCategoryId = -1
		}
		if (!WorldMapDisplay.isInteractive || WorldMapInteraction.MEC_CLICKZONES == null) {
			return
		}
		@Pc(161) var boundedMapElementListEntry: ClickzoneMEC? = WorldMapInteraction.MEC_CLICKZONES!!.first() as ClickzoneMEC?
		while (boundedMapElementListEntry != null) {
			@Pc(170) val mapElementType: MECType? = WorldMap.MECTYPE_LIST!!.list(boundedMapElementListEntry.category!!.id)
			if (boundedMapElementListEntry.contains(mouseX, mouseY)) {
				if (mapElementType!!.minimenuActions != null) {
					if (mapElementType.minimenuActions!![4] != null) {
						MiniMenu.addEntry(false, mapElementType.minimenuName, 0, false, mapElementType.categoryId, -1, true, MiniMenuAction.OP_MAPELEMENT5, boundedMapElementListEntry.category!!.id.toLong(), mapElementType.minimenuActions!![4], boundedMapElementListEntry.category!!.id.toLong(), -1)
					}
					if (mapElementType.minimenuActions!![3] != null) {
						MiniMenu.addEntry(false, mapElementType.minimenuName, 0, false, mapElementType.categoryId, -1, true, MiniMenuAction.OP_MAPELEMENT4, boundedMapElementListEntry.category!!.id.toLong(), mapElementType.minimenuActions!![3], boundedMapElementListEntry.category!!.id.toLong(), -1)
					}
					if (mapElementType.minimenuActions!![2] != null) {
						MiniMenu.addEntry(false, mapElementType.minimenuName, 0, false, mapElementType.categoryId, -1, true, MiniMenuAction.OP_MAPELEMENT3, boundedMapElementListEntry.category!!.id.toLong(), mapElementType.minimenuActions!![2], boundedMapElementListEntry.category!!.id.toLong(), -1)
					}
					if (mapElementType.minimenuActions!![1] != null) {
						MiniMenu.addEntry(false, mapElementType.minimenuName, 0, false, mapElementType.categoryId, -1, true, MiniMenuAction.OP_MAPELEMENT2, boundedMapElementListEntry.category!!.id.toLong(), mapElementType.minimenuActions!![1], boundedMapElementListEntry.category!!.id.toLong(), -1)
					}
					if (mapElementType.minimenuActions!![0] != null) {
						MiniMenu.addEntry(false, mapElementType.minimenuName, 0, false, mapElementType.categoryId, -1, true, MiniMenuAction.OP_MAPELEMENT1, boundedMapElementListEntry.category!!.id.toLong(), mapElementType.minimenuActions!![0], boundedMapElementListEntry.category!!.id.toLong(), -1)
					}
				}
				if (!boundedMapElementListEntry.category!!.isMouseOver) {
					boundedMapElementListEntry.category!!.isMouseOver = true
					CS2Interpreter.executeTrigger(ClientTriggerType.MEC_MOUSE_ENTER, boundedMapElementListEntry.category!!.id, mapElementType!!.categoryId)
				}
				if (boundedMapElementListEntry.category!!.isMouseOver) {
					CS2Interpreter.executeTrigger(ClientTriggerType.MEC_MOUSE_OVER, boundedMapElementListEntry.category!!.id, mapElementType!!.categoryId)
				}
			} else if (boundedMapElementListEntry.category!!.isMouseOver) {
				boundedMapElementListEntry.category!!.isMouseOver = false
				CS2Interpreter.executeTrigger(ClientTriggerType.MEC_MOUSE_LEAVE, boundedMapElementListEntry.category!!.id, mapElementType!!.categoryId)
			}
			boundedMapElementListEntry = WorldMapInteraction.MEC_CLICKZONES!!.next() as ClickzoneMEC?
		}
	}
}
