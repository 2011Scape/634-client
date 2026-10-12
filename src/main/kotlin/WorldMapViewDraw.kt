import com.jagex.game.runetek5.MapList
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.JSObjectManager
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.client.ClientState

object WorldMapViewDraw {

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(BIIILclient!ha;ILclient!d;)V")
	@JvmStatic
	fun drawWorldmap(@OriginalArg(1) left: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) toolkit: Toolkit?, @OriginalArg(5) top: Int, @OriginalArg(6) textureSource: MaterialProvider?) {
		if (ComponentMinimap.mapAssetLoadingPercentage < 100) {
			WorldMapLoader.loadAssets(textureSource, toolkit)
		}
		toolkit!!.KA(left, top, width + left, top + height)
		@Pc(37) var local37: Int
		@Pc(48) var local48: Int
		if (ComponentMinimap.mapAssetLoadingPercentage < 100) {
			val centerX: Int = width / 2 + left
			val barY: Int = top + height / 2 - 18 - 20
			toolkit.aa(left, top, width, height, -16777216, 0)
			toolkit.outlineRect(centerX - 152, barY, 304, 34, AwtLoadingScreen.DEBUG_LOAD_OUTLINE_COLOR!![AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX]!!.getRGB(), 0)
			toolkit.aa(centerX - 150, barY + 2, ComponentMinimap.mapAssetLoadingPercentage * 3, 30, AwtLoadingScreen.DEBUG_LOAD_COLOR!![AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX]!!.getRGB(), 0)
			MiniMenu.minimenuHeader!!.renderCentered(centerX, AwtLoadingScreen.DEBUG_LOAD_TEXT_COLOR!![AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX]!!.getRGB(), LocalizedText.aClass351_20.get(ClientState.language), -1, barY + 20)
			return
		}
		@Pc(114) val viewLeftTileX: Int = WorldMapDisplay.mapCenterX - (width.toFloat() / WorldMap.mapZoom).toInt()
		val viewTopTileY: Int = (height.toFloat() / WorldMap.mapZoom).toInt() + WorldMapDisplay.mapCenterY
		val viewRightTileX: Int = (width.toFloat() / WorldMap.mapZoom).toInt() + WorldMapDisplay.mapCenterX
		WorldMap.worldMapViewMinY = WorldMapDisplay.mapCenterY - (height.toFloat() / WorldMap.mapZoom).toInt()
		Camera.worldMapViewMinX = WorldMapDisplay.mapCenterX - (width.toFloat() / WorldMap.mapZoom).toInt()
		@Pc(157) val viewBottomTileY: Int = WorldMapDisplay.mapCenterY - (height.toFloat() / WorldMap.mapZoom).toInt()
		WorldMap.worldMapViewHeight = ((height * 2).toFloat() / WorldMap.mapZoom).toInt()
		WorldMap.worldMapViewWidth = ((width * 2).toFloat() / WorldMap.mapZoom).toInt()
		WorldMap.setView(viewLeftTileX + WorldMap.mapSquareAbsX, viewTopTileY + WorldMap.mapSquareAbsY, viewRightTileX + WorldMap.mapSquareAbsX, WorldMap.mapSquareAbsY + viewBottomTileY, left, top, width + left, height + top + 1)
		MapList.drawTiles(toolkit)
		@Pc(210) val deque: Deque? = MapList.positionElements(toolkit)
		WorldMapElementPass.renderInformation(deque, toolkit)
		if (JSObjectManager.highlightFlashCycles > 0) {
			ServerEnvironment.flashCycleCounter--
			if (ServerEnvironment.flashCycleCounter == 0) {
				JSObjectManager.highlightFlashCycles--
				ServerEnvironment.flashCycleCounter = 20
			}
		}
		if (!Culling.wallCullingEnabled) {
			return
		}
		@Pc(244) val textX: Int = left + width - 5
		@Pc(250) var textY: Int = height + top - 8
		FontManager.aClass68_4!!.renderRightAligned("Fps:" + Camera.fps, textY, 16776960, textX, -1)
		@Pc(267) val memTextY: Int = textY - 15
		@Pc(269) val runtime: Runtime = Runtime.getRuntime()
		@Pc(279) val usedMemoryKb: Int = ((runtime.totalMemory() - runtime.freeMemory()) / 1024L).toInt()
		@Pc(281) var textColor: Int = 16776960
		if (usedMemoryKb > 65536) {
			textColor = 16711680
		}
		FontManager.aClass68_4!!.renderRightAligned("Mem:" + usedMemoryKb + "k", memTextY, textColor, textX, -1)
		textY = memTextY - 15
	}
}
