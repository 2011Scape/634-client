import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap
import com.jagex.game.runetek5.graphics.map.worldmap.ClickzoneMEC
import com.jagex.game.runetek5.config.mec.MECType
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapCategory
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.WorldMapRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapInteraction
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import kotlin.jvm.JvmStatic

object WorldMapOffscreenMarker {

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(BLclient!lga;Lclient!ha;Lclient!ii;)V")
	@JvmStatic
	fun renderOffscreenIndicator(@OriginalArg(1) mapElementListEntry: WorldMapCategory?, @OriginalArg(2) toolkit: Toolkit?, @OriginalArg(3) mapElementType: MECType?) {
		@Pc(10) val sprite: NativeSprite? = mapElementType!!.getSprite(toolkit!!)
		if (sprite == null) {
			return
		}
		@Pc(16) var spriteDimension: Int = sprite.getWidth()
		if (spriteDimension < sprite.getHeight()) {
			spriteDimension = sprite.getHeight()
		}
		@Pc(28) var spriteX: Int = mapElementListEntry!!.screenX
		@Pc(31) var spriteY: Int = mapElementListEntry.screenY
		@Pc(33) var lineCount: Int = 0
		@Pc(35) var maxTextWidth: Int = 0
		@Pc(37) var textHeight: Int = 0
		@Pc(51) var local51: Int
		@Pc(78) var local78: Int
		if (mapElementType.areaName != null) {
			lineCount = FontManager.worldMapAreaFont!!.splitLinesToBuffer(mapElementType.areaName, null, WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES, null)
			var lineIndex: Int = 0
			while (lineIndex < lineCount) {
				@Pc(57) var string: String? = WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES[lineIndex]
				if (lineIndex < lineCount - 1) {
					string = string!!.substring(0, string.length - 4)
				}
				val lineWidth: Int = WorldMapHelvetica14.helvetica14Font!!.getTextWidth(string)
				if (lineWidth > maxTextWidth) {
					maxTextWidth = lineWidth
				}
				lineIndex++
			}
			textHeight = lineCount * WorldMapHelvetica14.helvetica14Font!!.getLineHeight() + WorldMapHelvetica14.helvetica14Font!!.getHeight() / 2
		}
		var textX: Int = spriteDimension / 2 + mapElementListEntry.screenX
		@Pc(112) var textY: Int = mapElementListEntry.screenY
		if (spriteX < WorldMap.pixelViewStartX + spriteDimension) {
			spriteX = WorldMap.pixelViewStartX
			textX = maxTextWidth / 2 + spriteDimension / 2 + WorldMap.pixelViewStartX + 10 + 5
		} else if (WorldMap.screenX2 - spriteDimension < spriteX) {
			spriteX = WorldMap.screenX2 - spriteDimension
			textX = WorldMap.screenX2 - spriteDimension / 2 - maxTextWidth / 2 - 10 - 5
		}
		if (spriteY < spriteDimension + WorldMap.screenY1) {
			spriteY = WorldMap.screenY1
			textY = spriteDimension / 2 + WorldMap.screenY1 + 10
		} else if (spriteY > WorldMap.pixelViewEndY - spriteDimension) {
			spriteY = WorldMap.pixelViewEndY - spriteDimension
			textY = WorldMap.pixelViewEndY - spriteDimension / 2 - textHeight - 10
		}
		val angle: Int = (kotlin.math.atan2((spriteX - mapElementListEntry.screenX).toDouble(), (spriteY - mapElementListEntry.screenY).toDouble()) / 3.141592653589793 * 32767.0).toInt() and 0xFFFF
		sprite.drawRotatedCenter(spriteDimension.toFloat() / 2.0f + spriteX.toFloat(), spriteDimension.toFloat() / 2.0f + spriteY.toFloat(), 4096, angle)
		@Pc(252) var textLeft: Int = -2
		@Pc(254) var textBottom: Int = -2
		@Pc(256) var textRight: Int = -2
		@Pc(264) var textTop: Int = -2
		if (mapElementType.areaName != null) {
			textBottom = textY
			textLeft = textX - maxTextWidth / 2 - 5
			textTop = lineCount * WorldMapHelvetica14.helvetica14Font!!.getLineHeight() + textY + 3
			textRight = maxTextWidth + textLeft + 10
			if (mapElementType.backgroundColor != 0) {
				toolkit!!.fillRect(textRight - textLeft, textLeft, textY, textTop - textY, mapElementType.backgroundColor)
			}
			if (mapElementType.outlineColor != 0) {
				toolkit!!.drawOpaqueRectangle(textRight - textLeft, textY, mapElementType.outlineColor, textLeft, textTop - textY)
			}
			@Pc(333) var i: Int = 0
			while (i < lineCount) {
				@Pc(339) var string2: String? = WorldMapDisplay.RENDERED_WORLDMAP_AREA_NAMELINES[i]
				if (lineCount - 1 > i) {
					string2 = string2!!.substring(0, string2.length - 4)
				}
				WorldMapHelvetica14.helvetica14Font!!.renderCenter(toolkit, string2, textX, textY, mapElementType.defaultTextColor)
				textY += WorldMapHelvetica14.helvetica14Font!!.getLineHeight()
				i++
			}
		}
		if (mapElementType.defaultIconArchive == -1 && mapElementType.areaName == null) {
			return
		}
		spriteDimension = spriteDimension shr 0x1
		@Pc(390) val boundedMapElementListEntry: ClickzoneMEC = ClickzoneMEC(mapElementListEntry)
		boundedMapElementListEntry.textRight = textRight
		boundedMapElementListEntry.spriteBottom = spriteY - spriteDimension
		boundedMapElementListEntry.spriteTop = spriteY + spriteDimension
		boundedMapElementListEntry.spriteRight = spriteX + spriteDimension
		boundedMapElementListEntry.rectY2 = textTop
		boundedMapElementListEntry.textBottom = textBottom
		boundedMapElementListEntry.spriteLeft = spriteX - spriteDimension
		boundedMapElementListEntry.textLeft = textLeft
		WorldMapInteraction.MEC_CLICKZONES!!.addLast(boundedMapElementListEntry)
	}
}
