package com.jagex.game.runetek5.client.input.mouse

import awt.Component
import awt.Point
import awt.Robot
import awt.image.BufferedImage
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.plugin.PluginCursor
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.cursor.CursorType
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.preference.GamePreferences
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!pfa")
class CursorManager @OriginalMember(owner = "client!pfa", name = "<init>", descriptor = "()V") @Throws(Exception::class) constructor() {

	@OriginalMember(owner = "client!pfa", name = "b", descriptor = "Ljava/awt/Component;")
	private var component: Component? = null

	@OriginalMember(owner = "client!pfa", name = "a", descriptor = "Ljava/awt/Robot;")
	private val positionManager: Robot = Robot()

	@OriginalMember(owner = "client!pfa", name = "movemouse", descriptor = "(II)V")
	private fun movemouse(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int) {
		this.positionManager.mouseMove(x, y)
	}

	@OriginalMember(owner = "client!pfa", name = "showcursor", descriptor = "(Ljava/awt/Component;Z)V")
	private fun showcursor(@OriginalArg(0) component2: Component?, @OriginalArg(1) show: Boolean) {
		var component2: Component? = component2
		if (show) {
			component2 = null
		} else if (component2 == null) {
			throw NullPointerException()
		}
		if (component2 === this.component) {
			return
		}
		if (this.component != null) {
			this.component!!.setCursor(null)
			this.component = null
		}
		if (component2 != null) {
			component2.setCursor(component2.getToolkit().createCustomCursor(BufferedImage(1, 1, 2), Point(0, 0), null))
			this.component = component2
		}
	}

	@OriginalMember(owner = "client!pfa", name = "setcustomcursor", descriptor = "(Ljava/awt/Component;[IIILjava/awt/Point;)V")
	private fun setcustomcursor(@OriginalArg(0) component: Component?, @OriginalArg(1) pixels: IntArray?, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) point: Point?) {
		if (pixels == null) {
			component!!.setCursor(null)
		} else {
			@Pc(8) val bufferedImage: BufferedImage = BufferedImage(width, height, 2)
			bufferedImage.setRGB(0, 0, width, height, pixels, 0, width)
			component!!.setCursor(component.getToolkit().createCustomCursor(bufferedImage, point, null))
		}
	}

	companion object {
		@OriginalMember(owner = "client!mba", name = "A", descriptor = "I")
		@JvmField
		var defaultCursorId: Int = -1
		@OriginalMember(owner = "client!iea", name = "j", descriptor = "I")
		@JvmField
		var defaultAttackCursor: Int = -1

		@OriginalMember(owner = "client!aga", name = "a", descriptor = "I")
		@JvmField
		var activeCursorId: Int = -1
		@OriginalMember(owner = "client!dw", name = "g", descriptor = "I")
		@JvmField
		var currentCursor: Int = -1
		@OriginalMember(owner = "client!iba", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun setCursor(@OriginalArg(1) cursorId: Int) {
			if (PluginCursor.override()) {
				return // Not part of the original client: a plugin's cursor stands (ClientApi.setCursor)
			}
			var cursorId: Int = cursorId
			if (GamePreferences.uid!!.customCursor!!.getValue() == 0) {
				cursorId = -1
			}
			if (CursorManager.currentCursor == cursorId) {
				return
			}
			if (cursorId != -1) {
				@Pc(27) val cursorType: CursorType? = TypeLists.CURSOR!!.list(cursorId)
				@Pc(31) val indexedImage: SpriteType? = cursorType!!.getSprite()
				if (indexedImage == null) {
					cursorId = -1
				} else {
					GameShell.signLink!!.setCustomCursor(indexedImage.heightSum(), indexedImage.getPixels(), Point(cursorType.hotspotPointX, cursorType.hotspotPointY), GameShell.gameCanvas, indexedImage.widthSum())
					CursorManager.currentCursor = cursorId
				}
			}
			if (cursorId == -1 && CursorManager.currentCursor != -1) {
				GameShell.signLink!!.setCustomCursor(-1, null, Point(), GameShell.gameCanvas, -1)
				CursorManager.currentCursor = -1
			}
		}
	}
}
