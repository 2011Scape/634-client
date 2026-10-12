package com.jagex.game.runetek5

import java.awt.Rectangle
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.camera.Camera
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.client.ClientState

@OriginalClass("client!qaa")
object InterfaceRectangles {

	@OriginalMember(owner = "client!qaa", name = "b", descriptor = "[Ljava/awt/Rectangle;")
	@JvmField
	val INTERFACE_BG_COMPONENTS: Array<Rectangle?> = arrayOfNulls<Rectangle>(100)

	init {
		@Pc(7) var index: Int = 0
		while (index < 100) {
			INTERFACE_BG_COMPONENTS[index] = Rectangle()
			index++
		}
	}

	@OriginalMember(owner = "client!am", name = "a", descriptor = "(IIIII)V")
	@JvmStatic
	fun inboundsComponentToTop(@OriginalArg(0) x: Int, @OriginalArg(1) width: Int, @OriginalArg(2) y: Int, @OriginalArg(4) height: Int) {
		@Pc(3) var i: Int = 0
		while (i < Camera.componentBackgroundCount) {
			@Pc(9) val rectangle: Rectangle = InterfaceRectangles.INTERFACE_BG_COMPONENTS[i]!!
			if (rectangle.width + rectangle.x > x && rectangle.x < width + x && rectangle.y + rectangle.height > y && rectangle.y < y + height) {
				InterfaceRectangles.IS_COMPONENT_ON_TOP[i] = true
			}
			i++
		}
		OrthoMode.markOrthoTilesDirty(x, x + width, y, height + y)
	}

	@OriginalMember(owner = "client!dj", name = "a", descriptor = "(IIZII)V")
	@JvmStatic
	fun markRectanglesForFlip(@OriginalArg(0) width: Int, @OriginalArg(1) y: Int, @OriginalArg(3) x: Int, @OriginalArg(4) height: Int) {
		@Pc(7) var i: Int = 0
		while (i < Camera.componentBackgroundCount) {
			@Pc(13) val rectangle: Rectangle = InterfaceRectangles.INTERFACE_BG_COMPONENTS[i]!!
			if (x < rectangle.width + rectangle.x && width + x > rectangle.x && y < rectangle.height + rectangle.y && height + y > rectangle.y) {
				InterfaceRectangles.RECTANGLE_NEEDS_FLIP[i] = true
			}
			i++
		}
		OrthoMode.markOrthoTilesDirty(x, x + width, y, y + height)
	}

	@OriginalMember(owner = "client!pq", name = "q", descriptor = "[Z")
	@JvmField
	val PREV_COMPONENT_ON_TOP: BooleanArray = BooleanArray(100)

	@OriginalMember(owner = "client!cb", name = "j", descriptor = "[Z")
	@JvmField
	val RECTANGLE_NEEDS_FLIP: BooleanArray = BooleanArray(100)

	@OriginalMember(owner = "client!bp", name = "t", descriptor = "[Ljava/awt/Rectangle;")
	@JvmField
	val FLIP_RECTANGLES: Array<Rectangle?> = arrayOfNulls<Rectangle>(100)

	@OriginalMember(owner = "client!ol", name = "g", descriptor = "[Z")
	@JvmField
	val IS_COMPONENT_ON_TOP: BooleanArray = BooleanArray(100)

	@OriginalMember(owner = "client!eo", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun invalidateAllRectangles() {
		@Pc(15) var i: Int = 0
		while (i < 100) {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[i] = true
			i++
		}
	}
}
