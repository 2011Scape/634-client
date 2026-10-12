import com.jagex.game.runetek5.config.mec.MECType
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory
import com.jagex.game.runetek5.graphics.Toolkit
import java.awt.Image
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay

object Static658 {

	@OriginalMember(owner = "client!eca", name = "l", descriptor = "Ljava/awt/Image;")
	@JvmField
	var canvasImage: Image? = null

	@OriginalMember(owner = "client!eca", name = "a", descriptor = "(Lclient!cs;Lclient!lga;IIILclient!ha;IILclient!ii;)V")
	@JvmStatic
	fun method2907(@OriginalArg(0) worldMapFont: HelveticaFont?, @OriginalArg(1) mapElementListEntry: WorldMapCategory?, @OriginalArg(2) labelX: Int, @OriginalArg(4) lineCount: Int, @OriginalArg(5) toolkit: Toolkit?, @OriginalArg(6) labelY: Int, @OriginalArg(7) maxTextWidth: Int, @OriginalArg(8) mapElementType: MECType?) {
		var labelY: Int = labelY
		@Pc(17) val rectX: Int = labelX - maxTextWidth / 2 - 5
		@Pc(21) val rectY: Int = labelY + 2
		if (mapElementType!!.backgroundColor != 0) {
			toolkit!!.fillRect(maxTextWidth + 10, rectX, rectY, labelY + worldMapFont!!.getLineHeight() * lineCount + 1 - rectY, mapElementType.backgroundColor)
		}
		if (mapElementType.outlineColor != 0) {
			toolkit!!.drawOpaqueRectangle(maxTextWidth + 10, rectY, mapElementType.outlineColor, rectX, lineCount * worldMapFont!!.getLineHeight() + labelY + 1 - rectY)
		}
		@Pc(74) var textColor: Int = mapElementType.defaultTextColor
		if (mapElementListEntry!!.isMouseOver && mapElementType.mouseOverTextColor != -1) {
			textColor = mapElementType.mouseOverTextColor
		}
		@Pc(86) var i: Int = 0
		while (i < lineCount) {
			@Pc(92) var string: String? = WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES[i]
			if (lineCount - 1 > i) {
				string = string!!.substring(0, string.length - 4)
			}
			worldMapFont!!.renderCenter(toolkit, string, labelX, labelY, textColor)
			labelY += worldMapFont.getLineHeight()
			i++
		}
	}
}
