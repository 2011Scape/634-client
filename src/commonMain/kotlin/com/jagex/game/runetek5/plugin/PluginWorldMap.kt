package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: where the world map was last drawn (WorldMapViewDraw.drawWorldmap), for
 * ClientApi.worldMapArea and projectWorldMap: its box on the screen, the world tile at its top left and
 * its pixels a tile.
 */
object PluginWorldMap {

	private var left: Int = 0
	private var top: Int = 0
	private var width: Int = 0
	private var height: Int = 0
	private var tileLeft: Int = 0
	private var tileTop: Int = 0
	private var pixelsPerTile: Float = 0f
	private var drawnCycle: Int = -1

	/** The map was drawn into ([left], [top], [width], [height]), world tile ([tileLeft], [tileTop]) at its top left, [zoom] as the map's. */
	@JvmStatic
	fun drawn(left: Int, top: Int, width: Int, height: Int, tileLeft: Int, tileTop: Int, zoom: Float) {
		this.left = left
		this.top = top
		this.width = width
		this.height = height
		this.tileLeft = tileLeft
		this.tileTop = tileTop
		// the map shows width * 2 / zoom tiles across its width
		pixelsPerTile = zoom / 2f
		drawnCycle = ClientState.clientCycle
	}

	/** Whether the map was drawn this cycle (it is drawn each frame it shows). */
	@JvmStatic
	fun shown(): Boolean = drawnCycle >= ClientState.clientCycle - 1 && pixelsPerTile > 0f

	@JvmStatic
	fun area(out: IntArray): Boolean {
		if (!shown() || out.size < 4) {
			return false
		}
		out[0] = left
		out[1] = top
		out[2] = width
		out[3] = height
		return true
	}

	/** World tile ([x], [y])'s centre on the screen into [out]; false when the map is not shown or the tile is off it. */
	@JvmStatic
	fun project(x: Int, y: Int, out: IntArray): Boolean {
		if (!shown() || out.size < 2) {
			return false
		}
		val px: Int = left + ((x - tileLeft + 0.5f) * pixelsPerTile).toInt()
		val py: Int = top + ((tileTop - y + 0.5f) * pixelsPerTile).toInt()
		if (px < left || py < top || px >= left + width || py >= top + height) {
			return false
		}
		out[0] = px
		out[1] = py
		return true
	}
}
