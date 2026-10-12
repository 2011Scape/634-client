package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.OverlayManager
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate

/**
 * Not part of the original client: tiles the player marks from the mini menu ("Mark tile"), each
 * drawn with an outline after the scene until it is unmarked. A marker is kept in world
 * coordinates, so it stays on its tile when the map region around the player is rebuilt. On only
 * with `client.tileMarkers=true`; the console command `tilemarkers` turns it on and off
 * while playing.
 */
object TileMarkers {

	@JvmField
	var enabled: Boolean = java.lang.Boolean.getBoolean("client.tileMarkers")

	/** The most tiles that can be marked at once. */
	const val MAX_MARKERS: Int = 64

	/** The outline colour, opaque yellow. */
	private const val COLOUR: Int = 0xFFFFFF00.toInt()

	/** The size of a tile in world units. */
	private const val TILE_SIZE: Int = 512

	/** The marked tiles, packed by [pack]. */
	private val markers: IntArray = IntArray(MAX_MARKERS)

	private var markerCount: Int = 0

	/** How many markers the last frame drew, for the smoke test. */
	@JvmField
	var drawnLastFrame: Int = 0

	/** The screen corners of the last marker drawn, x then y for each, for the smoke test. */
	@JvmField
	val lastCorners: IntArray = IntArray(8)

	/** A world tile packed into an int: level in the top 2 bits, then x and y in 15 bits each. */
	private fun pack(level: Int, worldX: Int, worldY: Int): Int {
		return level shl 30 or (worldX shl 15) or worldY
	}

	@JvmStatic
	fun markerCount(): Int {
		return markerCount
	}

	/** Whether the scene tile is marked on the local player's level. */
	@JvmStatic
	fun isMarked(sceneX: Int, sceneY: Int): Boolean {
		return indexOf(pack(PlayerUpdate.localPlayer!!.level.toInt(), MapRegion.sceneBaseX + sceneX, MapRegion.sceneBaseY + sceneY)) != -1
	}

	/** Marks the scene tile on the local player's level, or unmarks it when it is marked. */
	@JvmStatic
	fun toggle(sceneX: Int, sceneY: Int) {
		val packed: Int = pack(PlayerUpdate.localPlayer!!.level.toInt(), MapRegion.sceneBaseX + sceneX, MapRegion.sceneBaseY + sceneY)
		val index: Int = indexOf(packed)
		if (index != -1) {
			markers[index] = markers[--markerCount]
		} else if (markerCount < MAX_MARKERS) {
			markers[markerCount++] = packed
		}
	}

	private fun indexOf(packed: Int): Int {
		for (i in 0 until markerCount) {
			if (markers[i] == packed) {
				return i
			}
		}
		return -1
	}

	/**
	 * Outlines every marked tile on the local player's level that is in the scene, in the viewport
	 * the scene was just drawn into.
	 */
	@JvmStatic
	fun draw(areaX: Int, areaY: Int) {
		drawnLastFrame = 0
		if (!enabled || markerCount == 0) {
			return
		}
		val level: Int = PlayerUpdate.localPlayer!!.level.toInt()
		val screenX: IntArray = IntArray(4)
		val screenY: IntArray = IntArray(4)
		for (i in 0 until markerCount) {
			val packed: Int = markers[i]
			if (packed ushr 30 != level) {
				continue
			}
			val sceneX: Int = (packed shr 15 and 0x7FFF) - MapRegion.sceneBaseX
			val sceneY: Int = (packed and 0x7FFF) - MapRegion.sceneBaseY
			var visible: Boolean = true
			var corner: Int = 0
			while (corner < 4 && visible) {
				// corners in order around the tile: (0,0), (1,0), (1,1), (0,1)
				val x: Int = (sceneX + (if (corner == 1 || corner == 2) 1 else 0)) * TILE_SIZE
				val z: Int = (sceneY + (if (corner >= 2) 1 else 0)) * TILE_SIZE
				OverlayManager.projectToScreenWithOffset(level, 0, 0, x, 0, z, 0)
				if (OverlayManager.projectedScreenPosition[0] < 0) {
					visible = false
				}
				screenX[corner] = OverlayManager.projectedScreenPosition[0] + areaX
				screenY[corner] = OverlayManager.projectedScreenPosition[1] + areaY
				corner++
			}
			if (!visible) {
				continue
			}
			for (corner in 0 until 4) {
				lastCorners[corner * 2] = screenX[corner]
				lastCorners[corner * 2 + 1] = screenY[corner]
				val next: Int = (corner + 1) % 4
				ClientState.activeToolkit!!.line(screenX[corner], screenY[corner], screenX[next], screenY[next], COLOUR, 1)
			}
			drawnLastFrame++
		}
	}
}
