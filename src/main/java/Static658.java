import com.jagex.game.runetek5.config.mec.MECType;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory;
import com.jagex.game.runetek5.graphics.Toolkit;
import java.awt.Image;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay;

public final class Static658 {

	@OriginalMember(owner = "client!eca", name = "l", descriptor = "Ljava/awt/Image;")
	public static Image canvasImage;

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "(Lclient!cs;Lclient!lga;IIILclient!ha;IILclient!ii;)V")
	public static void method2907(@OriginalArg(0) HelveticaFont worldMapFont, @OriginalArg(1) WorldMapCategory mapElementListEntry, @OriginalArg(2) int labelX, @OriginalArg(4) int lineCount, @OriginalArg(5) Toolkit toolkit, @OriginalArg(6) int labelY, @OriginalArg(7) int maxTextWidth, @OriginalArg(8) MECType mapElementType) {
		@Pc(17) int rectX = labelX - maxTextWidth / 2 - 5;
		@Pc(21) int rectY = labelY + 2;
		if (mapElementType.backgroundColor != 0) {
			toolkit.fillRect(maxTextWidth + 10, rectX, rectY, labelY + worldMapFont.getLineHeight() * lineCount + 1 - rectY, mapElementType.backgroundColor);
		}
		if (mapElementType.outlineColor != 0) {
			toolkit.drawOpaqueRectangle(maxTextWidth + 10, rectY, mapElementType.outlineColor, rectX, lineCount * worldMapFont.getLineHeight() + labelY + 1 - rectY);
		}
		@Pc(74) int textColor = mapElementType.defaultTextColor;
		if (mapElementListEntry.isMouseOver && mapElementType.mouseOverTextColor != -1) {
			textColor = mapElementType.mouseOverTextColor;
		}
		for (@Pc(86) int i = 0; i < lineCount; i++) {
			@Pc(92) String string = WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES[i];
			if (lineCount - 1 > i) {
				string = string.substring(0, string.length() - 4);
			}
			worldMapFont.renderCenter(toolkit, string, labelX, labelY, textColor);
			labelY += worldMapFont.getLineHeight();
		}
	}
}
