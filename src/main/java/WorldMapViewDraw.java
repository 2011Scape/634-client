import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.client.JSObjectManager;
import com.jagex.game.runetek5.client.ServerEnvironment;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.client.ClientState;

public final class WorldMapViewDraw {

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(BIIILclient!ha;ILclient!d;)V")
	public static void drawWorldmap(@OriginalArg(1) int left, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) Toolkit toolkit, @OriginalArg(5) int top, @OriginalArg(6) MaterialProvider textureSource) {
		if (ComponentMinimap.mapAssetLoadingPercentage < 100) {
			WorldMapLoader.loadAssets(textureSource, toolkit);
		}
		toolkit.KA(left, top, width + left, top + height);
		@Pc(37) int local37;
		@Pc(48) int local48;
		if (ComponentMinimap.mapAssetLoadingPercentage < 100) {
			int centerX = width / 2 + left;
			int barY = top + height / 2 - 18 - 20;
			toolkit.aa(left, top, width, height, -16777216, 0);
			toolkit.outlineRect(centerX - 152, barY, 304, 34, AwtLoadingScreen.DEBUG_LOAD_OUTLINE_COLOR[AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX].getRGB(), 0);
			toolkit.aa(centerX - 150, barY + 2, ComponentMinimap.mapAssetLoadingPercentage * 3, 30, AwtLoadingScreen.DEBUG_LOAD_COLOR[AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX].getRGB(), 0);
			MiniMenu.minimenuHeader.renderCentered(centerX, AwtLoadingScreen.DEBUG_LOAD_TEXT_COLOR[AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX].getRGB(), LocalizedText.aClass351_20.get(ClientState.language), -1, barY + 20);
			return;
		}
		@Pc(114) int viewLeftTileX = WorldMapDisplay.mapCenterX - (int) ((float) width / WorldMap.mapZoom);
		int viewTopTileY = (int) ((float) height / WorldMap.mapZoom) + WorldMapDisplay.mapCenterY;
		int viewRightTileX = (int) ((float) width / WorldMap.mapZoom) + WorldMapDisplay.mapCenterX;
		WorldMap.worldMapViewMinY = WorldMapDisplay.mapCenterY - (int) ((float) height / WorldMap.mapZoom);
		Camera.worldMapViewMinX = WorldMapDisplay.mapCenterX - (int) ((float) width / WorldMap.mapZoom);
		@Pc(157) int viewBottomTileY = WorldMapDisplay.mapCenterY - (int) ((float) height / WorldMap.mapZoom);
		WorldMap.worldMapViewHeight = (int) ((float) (height * 2) / WorldMap.mapZoom);
		WorldMap.worldMapViewWidth = (int) ((float) (width * 2) / WorldMap.mapZoom);
		WorldMap.setView(viewLeftTileX + WorldMap.mapSquareAbsX, viewTopTileY + WorldMap.mapSquareAbsY, viewRightTileX + WorldMap.mapSquareAbsX, WorldMap.mapSquareAbsY + viewBottomTileY, left, top, width + left, height + top + 1);
		MapList.drawTiles(toolkit);
		@Pc(210) Deque deque = MapList.positionElements(toolkit);
		WorldMapElementPass.renderInformation(deque, toolkit);
		if (JSObjectManager.highlightFlashCycles > 0) {
			ServerEnvironment.flashCycleCounter--;
			if (ServerEnvironment.flashCycleCounter == 0) {
				JSObjectManager.highlightFlashCycles--;
				ServerEnvironment.flashCycleCounter = 20;
			}
		}
		if (!Culling.wallCullingEnabled) {
			return;
		}
		@Pc(244) int textX = left + width - 5;
		@Pc(250) int textY = height + top - 8;
		FontManager.aClass68_4.renderRightAligned("Fps:" + Camera.fps, textY, 16776960, textX, -1);
		@Pc(267) int memTextY = textY - 15;
		@Pc(269) Runtime runtime = Runtime.getRuntime();
		@Pc(279) int usedMemoryKb = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
		@Pc(281) int textColor = 16776960;
		if (usedMemoryKb > 65536) {
			textColor = 16711680;
		}
		FontManager.aClass68_4.renderRightAligned("Mem:" + usedMemoryKb + "k", memTextY, textColor, textX, -1);
		textY = memTextY - 15;
	}
}
