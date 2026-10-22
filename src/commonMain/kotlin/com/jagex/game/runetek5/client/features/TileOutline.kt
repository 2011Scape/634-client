package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: a scene tile drawn as RuneLite's OverlayUtil.renderPolygon does, its
 * fill and then its border [width] pixels wide, for Ground Tiles and Tile Indicators. The polygon is
 * view-relative (ClientApi.tilePolygon), so the view's place is added.
 */
object TileOutline {

	/**
	 * Draws tile ([sceneX], [sceneY]) on [level], [size] tiles across, into [xs] and [ys] (4 each, left
	 * there on the screen); a colour whose alpha is 0 is not drawn. False when the tile is off the screen.
	 */
	@JvmStatic
	fun draw(g: Graphics, view: Viewport, client: ClientApi, sceneX: Int, sceneY: Int, level: Int, size: Int, border: Int, fill: Int, width: Int, xs: IntArray, ys: IntArray): Boolean {
		if (!client.tilePolygon(sceneX, sceneY, level, size, xs, ys)) {
			return false
		}
		for (i in 0 until 4) {
			xs[i] += view.x
			ys[i] += view.y
		}
		if (fill ushr 24 != 0) {
			g.fillPolygon(xs, ys, 4, fill)
		}
		if (border ushr 24 != 0 && width > 0) {
			for (i in 0 until 4) {
				val j: Int = if (i == 3) 0 else i + 1
				g.thickLine(xs[i], ys[i], xs[j], ys[j], width, border)
			}
		}
		return true
	}
}
