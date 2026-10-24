package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapDisplay
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMapElement
import kotlin.jvm.JvmStatic
import kotlin.math.floor

/**
 * Not part of the original client: where the world map was last drawn (WorldMapViewDraw.drawWorldmap), for
 * ClientApi.worldMapArea, projectWorldMap and worldMapTile: its box on the screen, the world tile at its top left and
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

	/**
	 * World tile ([x], [y]) on [level] (-1: the first level that has it)'s centre on the screen into [out];
	 * false when the map is not shown or the tile is off it. The map's area places each part of the world
	 * (a dungeon, an upper floor) where it draws it; with no area loaded a tile is where its coordinates say.
	 */
	@JvmStatic
	fun project(x: Int, y: Int, level: Int, out: IntArray): Boolean = project(x, y, level, out, true)

	/**
	 * As [project], and with [onScreen] false a tile off the map's box too (its pixel outside the box), for a
	 * line from a tile on the map to one off it: false then only when the map is not shown or shows no such tile.
	 */
	@JvmStatic
	fun project(x: Int, y: Int, level: Int, out: IntArray, onScreen: Boolean): Boolean {
		if (!shown() || out.size < 2) {
			return false
		}
		var mapX: Int = x
		var mapY: Int = y
		val area: WorldMapElement? = WorldMapDisplay.getWorldMapArea()
		if (area != null) {
			val position: IntArray = scratch
			val placed: Boolean = if (level >= 0) {
				area.convertSourceToDisplay(position, x, y, level) || area.convertDisplayToSource(position, y, x)
			} else {
				area.convertDisplayToSource(position, y, x)
			}
			if (!placed) {
				return false
			}
			mapX = position[1]
			mapY = position[2]
		}
		val px: Int = left + ((mapX - tileLeft + 0.5f) * pixelsPerTile).toInt()
		val py: Int = top + ((tileTop - mapY + 0.5f) * pixelsPerTile).toInt()
		if (onScreen && (px < left || py < top || px >= left + width || py >= top + height)) {
			return false
		}
		out[0] = px
		out[1] = py
		return true
	}

	/** The world tile (x, y, level) under screen pixel ([screenX], [screenY]) into [out]; false when the map is not shown there. */
	@JvmStatic
	fun tileAt(screenX: Int, screenY: Int, out: IntArray): Boolean {
		if (!shown() || out.size < 3 || screenX < left || screenY < top || screenX >= left + width || screenY >= top + height) {
			return false
		}
		val mapX: Int = tileLeft + floor((screenX - left) / pixelsPerTile).toInt()
		val mapY: Int = tileTop - floor((screenY - top) / pixelsPerTile).toInt()
		val area: WorldMapElement? = WorldMapDisplay.getWorldMapArea()
		if (area == null) {
			out[0] = mapX
			out[1] = mapY
			out[2] = 0
			return true
		}
		val position: IntArray = scratch
		if (!area.mapToGameCoords(mapY, mapX, position)) {
			return false
		}
		out[0] = position[1]
		out[1] = position[2]
		out[2] = position[0]
		return true
	}

	private val scratch: IntArray = IntArray(3)
}
