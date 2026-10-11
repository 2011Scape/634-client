import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.graphics.map.worldmap.ClickzoneMEC;
import com.jagex.game.runetek5.config.mec.MECType;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.JSObjectManager;
import com.jagex.game.runetek5.client.ServerEnvironment;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapInteraction;
import com.jagex.game.runetek5.graphics.font.FontManager;

public final class WorldMapElementDraw {

	@OriginalMember(owner = "client!bp", name = "a", descriptor = "(BLclient!ii;Lclient!lga;ILclient!ha;I)Z")
	public static boolean renderIconsAndNames(@OriginalArg(1) MECType mapElementType, @OriginalArg(2) WorldMapCategory mapElementListEntry, @OriginalArg(4) Toolkit toolkit) {
		@Pc(7) int boundsLeft = Integer.MAX_VALUE;
		@Pc(9) int boundsRight = Integer.MIN_VALUE;
		@Pc(11) int boundsBottom = Integer.MAX_VALUE;
		@Pc(13) int boundsTop = Integer.MIN_VALUE;
		if (mapElementType.offsets != null) {
			boundsRight = WorldMap.pixelViewStartX + (mapElementListEntry.x + mapElementType.maxOffsetX - WorldMap.tileViewStartX) * (WorldMap.screenX2 - WorldMap.pixelViewStartX) / (WorldMap.tileViewEndX - WorldMap.tileViewStartX);
			boundsBottom = WorldMap.pixelViewEndY - (WorldMap.pixelViewEndY - WorldMap.screenY1) * (mapElementType.maxOffsetY + (mapElementListEntry.z - WorldMap.tileViewStartY)) / (WorldMap.tileViewEndY - WorldMap.tileViewStartY);
			boundsTop = WorldMap.pixelViewEndY - (WorldMap.pixelViewEndY - WorldMap.screenY1) * (mapElementListEntry.z + mapElementType.minOffsetY + -WorldMap.tileViewStartY) / (WorldMap.tileViewEndY - WorldMap.tileViewStartY);
			boundsLeft = (WorldMap.screenX2 - WorldMap.pixelViewStartX) * (mapElementListEntry.x + mapElementType.minOffsetX - WorldMap.tileViewStartX) / (WorldMap.tileViewEndX - WorldMap.tileViewStartX) + WorldMap.pixelViewStartX;
		}
		@Pc(101) NativeSprite sprite = null;
		@Pc(103) int spriteLeft = 0;
		@Pc(110) int spriteRight = 0;
		@Pc(112) int spriteBottom = 0;
		@Pc(114) int spriteTop = 0;
		if (mapElementType.defaultIconArchive != -1) {
			if (mapElementListEntry.isMouseOver && mapElementType.mouseOverIconArchive != -1) {
				sprite = mapElementType.getIcon(true, toolkit);
			} else {
				sprite = mapElementType.getIcon(false, toolkit);
			}
			if (sprite != null) {
				spriteLeft = mapElementListEntry.screenX - (sprite.scaleWidth() + 1 >> 1);
				spriteRight = mapElementListEntry.screenX + (sprite.scaleWidth() + 1 >> 1);
				if (spriteLeft < boundsLeft) {
					boundsLeft = spriteLeft;
				}
				if (spriteRight > boundsRight) {
					boundsRight = spriteRight;
				}
				spriteBottom = mapElementListEntry.screenY - (sprite.scaleHeight() + 1 >> 1);
				if (spriteBottom < boundsBottom) {
					boundsBottom = spriteBottom;
				}
				spriteTop = mapElementListEntry.screenY + (sprite.scaleHeight() + 1 >> 1);
				if (spriteTop > boundsTop) {
					boundsTop = spriteTop;
				}
			}
		}
		@Pc(203) HelveticaFont worldMapFont = null;
		@Pc(205) int lineCount = 0;
		@Pc(207) int labelX = 0;
		@Pc(209) int labelY = 0;
		@Pc(211) int maxTextWidth = 0;
		@Pc(213) int textLeft = 0;
		@Pc(215) int textRight = 0;
		@Pc(217) int textBottom = 0;
		@Pc(219) int textTop = 0;
		@Pc(297) int local297;
		if (mapElementType.areaName != null) {
			worldMapFont = WorldMapZoomFont.getZoomedHelveticafont(mapElementType.baseTextzoom);
			if (worldMapFont != null) {
				lineCount = FontManager.worldMapAreaFont.splitLinesToBuffer(mapElementType.areaName, null, WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES, null);
				labelY = mapElementListEntry.screenY - mapElementType.labelOffsetY * (WorldMap.pixelViewEndY - WorldMap.screenY1) / (WorldMap.tileViewEndY - WorldMap.tileViewStartY);
				labelX = mapElementListEntry.screenX + (WorldMap.screenX2 - WorldMap.pixelViewStartX) * mapElementType.labelOffsetX / (WorldMap.tileViewEndX - WorldMap.tileViewStartX);
				if (sprite == null) {
					labelY -= lineCount * worldMapFont.getHeight() / 2;
				} else {
					labelY -= (sprite.scaleHeight() >> 1) + (lineCount * worldMapFont.getLineHeight());
				}
				for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
					@Pc(303) String string = WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES[lineIndex];
					if (lineIndex < lineCount - 1) {
						string = string.substring(0, string.length() - 4);
					}
					@Pc(320) int textWidth = worldMapFont.getTextWidth(string);
					if (maxTextWidth < textWidth) {
						maxTextWidth = textWidth;
					}
				}
				textLeft = labelX - maxTextWidth / 2;
				textRight = labelX + maxTextWidth / 2;
				if (textLeft < boundsLeft) {
					boundsLeft = textLeft;
				}
				textBottom = labelY;
				if (textRight > boundsRight) {
					boundsRight = textRight;
				}
				textTop = labelY + worldMapFont.getLineHeight() * lineCount;
				if (boundsBottom > textBottom) {
					boundsBottom = textBottom;
				}
				if (textTop > boundsTop) {
					boundsTop = textTop;
				}
			}
		}
		if (WorldMap.pixelViewStartX > boundsRight || WorldMap.screenX2 < boundsLeft || WorldMap.screenY1 > boundsTop || boundsBottom > WorldMap.pixelViewEndY) {
			return true;
		}
		WorldMap.renderCategoryLines(toolkit, mapElementListEntry, mapElementType);
		if (sprite != null) {
			if (JSObjectManager.highlightFlashCycles > 0 && (WorldMapDisplay.highlightedCategoryId != -1 && WorldMapDisplay.highlightedCategoryId == mapElementListEntry.id || WorldMapDisplay.someMecId != -1 && mapElementType.categoryId == WorldMapDisplay.someMecId)) {
				int highlightAlpha;
				if (ServerEnvironment.flashCycleCounter <= 50) {
					highlightAlpha = ServerEnvironment.flashCycleCounter * 2;
				} else {
					highlightAlpha = 200 - ServerEnvironment.flashCycleCounter * 2;
				}
				@Pc(457) int highlightColor = highlightAlpha << 24 | 0xFFFF00;
				toolkit.fillCircle(highlightColor, sprite.getWidth() / 2 + 7, mapElementListEntry.screenX, mapElementListEntry.screenY);
				toolkit.fillCircle(highlightColor, sprite.getWidth() / 2 + 5, mapElementListEntry.screenX, mapElementListEntry.screenY);
				toolkit.fillCircle(highlightColor, sprite.getWidth() / 2 + 3, mapElementListEntry.screenX, mapElementListEntry.screenY);
				toolkit.fillCircle(highlightColor, sprite.getWidth() / 2 + 1, mapElementListEntry.screenX, mapElementListEntry.screenY);
				toolkit.fillCircle(highlightColor, sprite.getWidth() / 2, mapElementListEntry.screenX, mapElementListEntry.screenY);
			}
			sprite.render(mapElementListEntry.screenX - (sprite.scaleWidth() >> 1), mapElementListEntry.screenY + -(sprite.scaleHeight() >> 1));
		}
		if (mapElementType.areaName != null && worldMapFont != null) {
			Static658.method2907(worldMapFont, mapElementListEntry, labelX, lineCount, toolkit, labelY, maxTextWidth, mapElementType);
		}
		if (mapElementType.defaultIconArchive != -1 || mapElementType.areaName != null) {
			@Pc(570) ClickzoneMEC boundedMapElementListEntry = new ClickzoneMEC(mapElementListEntry);
			boundedMapElementListEntry.spriteBottom = spriteBottom;
			boundedMapElementListEntry.textBottom = textBottom;
			boundedMapElementListEntry.spriteLeft = spriteLeft;
			boundedMapElementListEntry.rectY2 = textTop;
			boundedMapElementListEntry.textLeft = textLeft;
			boundedMapElementListEntry.spriteTop = spriteTop;
			boundedMapElementListEntry.textRight = textRight;
			boundedMapElementListEntry.spriteRight = spriteRight;
			WorldMapInteraction.MEC_CLICKZONES.addLast(boundedMapElementListEntry);
		}
		return false;
	}
}
