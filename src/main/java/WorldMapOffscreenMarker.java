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
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapInteraction;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;

public final class WorldMapOffscreenMarker {

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(BLclient!lga;Lclient!ha;Lclient!ii;)V")
	public static void renderOffscreenIndicator(@OriginalArg(1) WorldMapCategory mapElementListEntry, @OriginalArg(2) Toolkit toolkit, @OriginalArg(3) MECType mapElementType) {
		@Pc(10) NativeSprite sprite = mapElementType.getSprite(toolkit);
		if (sprite == null) {
			return;
		}
		@Pc(16) int spriteDimension = sprite.getWidth();
		if (spriteDimension < sprite.getHeight()) {
			spriteDimension = sprite.getHeight();
		}
		@Pc(28) int spriteX = mapElementListEntry.screenX;
		@Pc(31) int spriteY = mapElementListEntry.screenY;
		@Pc(33) int lineCount = 0;
		@Pc(35) int maxTextWidth = 0;
		@Pc(37) int textHeight = 0;
		@Pc(51) int local51;
		@Pc(78) int local78;
		if (mapElementType.areaName != null) {
			lineCount = FontManager.worldMapAreaFont.splitLinesToBuffer(mapElementType.areaName, null, WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES, null);
			for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
				@Pc(57) String string = WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES[lineIndex];
				if (lineIndex < lineCount - 1) {
					string = string.substring(0, string.length() - 4);
				}
				int lineWidth = WorldMapHelvetica14.helvetica14Font.getTextWidth(string);
				if (lineWidth > maxTextWidth) {
					maxTextWidth = lineWidth;
				}
			}
			textHeight = lineCount * WorldMapHelvetica14.helvetica14Font.getLineHeight() + WorldMapHelvetica14.helvetica14Font.getHeight() / 2;
		}
		int textX = spriteDimension / 2 + mapElementListEntry.screenX;
		@Pc(112) int textY = mapElementListEntry.screenY;
		if (spriteX < WorldMap.pixelViewStartX + spriteDimension) {
			spriteX = WorldMap.pixelViewStartX;
			textX = maxTextWidth / 2 + spriteDimension / 2 + WorldMap.pixelViewStartX + 10 + 5;
		} else if (WorldMap.screenX2 - spriteDimension < spriteX) {
			spriteX = WorldMap.screenX2 - spriteDimension;
			textX = WorldMap.screenX2 - spriteDimension / 2 - maxTextWidth / 2 - 10 - 5;
		}
		if (spriteY < spriteDimension + WorldMap.screenY1) {
			spriteY = WorldMap.screenY1;
			textY = spriteDimension / 2 + WorldMap.screenY1 + 10;
		} else if (spriteY > WorldMap.pixelViewEndY - spriteDimension) {
			spriteY = WorldMap.pixelViewEndY - spriteDimension;
			textY = WorldMap.pixelViewEndY - spriteDimension / 2 - textHeight - 10;
		}
		int angle = (int) (Math.atan2((double) (spriteX - mapElementListEntry.screenX), (double) (spriteY - mapElementListEntry.screenY)) / 3.141592653589793D * 32767.0D) & 0xFFFF;
		sprite.drawRotatedCenter((float) spriteDimension / 2.0F + (float) spriteX, (float) spriteDimension / 2.0F + (float) spriteY, 4096, angle);
		@Pc(252) int textLeft = -2;
		@Pc(254) int textBottom = -2;
		@Pc(256) int textRight = -2;
		@Pc(264) int textTop = -2;
		if (mapElementType.areaName != null) {
			textBottom = textY;
			textLeft = textX - maxTextWidth / 2 - 5;
			textTop = lineCount * WorldMapHelvetica14.helvetica14Font.getLineHeight() + textY + 3;
			textRight = maxTextWidth + textLeft + 10;
			if (mapElementType.backgroundColor != 0) {
				toolkit.fillRect(textRight - textLeft, textLeft, textY, textTop - textY, mapElementType.backgroundColor);
			}
			if (mapElementType.outlineColor != 0) {
				toolkit.drawOpaqueRectangle(textRight - textLeft, textY, mapElementType.outlineColor, textLeft, textTop - textY);
			}
			for (@Pc(333) int i = 0; i < lineCount; i++) {
				@Pc(339) String string2 = WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES[i];
				if (lineCount - 1 > i) {
					string2 = string2.substring(0, string2.length() - 4);
				}
				WorldMapHelvetica14.helvetica14Font.renderCenter(toolkit, string2, textX, textY, mapElementType.defaultTextColor);
				textY += WorldMapHelvetica14.helvetica14Font.getLineHeight();
			}
		}
		if (mapElementType.defaultIconArchive == -1 && mapElementType.areaName == null) {
			return;
		}
		spriteDimension >>= 0x1;
		@Pc(390) ClickzoneMEC boundedMapElementListEntry = new ClickzoneMEC(mapElementListEntry);
		boundedMapElementListEntry.textRight = textRight;
		boundedMapElementListEntry.spriteBottom = spriteY - spriteDimension;
		boundedMapElementListEntry.spriteTop = spriteY + spriteDimension;
		boundedMapElementListEntry.spriteRight = spriteX + spriteDimension;
		boundedMapElementListEntry.rectY2 = textTop;
		boundedMapElementListEntry.textBottom = textBottom;
		boundedMapElementListEntry.spriteLeft = spriteX - spriteDimension;
		boundedMapElementListEntry.textLeft = textLeft;
		WorldMapInteraction.MEC_CLICKZONES.addLast(boundedMapElementListEntry);
	}
}
