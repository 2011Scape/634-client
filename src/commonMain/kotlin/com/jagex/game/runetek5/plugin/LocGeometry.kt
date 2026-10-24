package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.PickList
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.entity.PickableEntity
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.tile.Tile
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the shapes of a location on the screen for the plugins
 * (ClientApi.locHull, locClickbox, locOutline), as RuneLite's Object Markers draws them. The client
 * keeps no model triangles every toolkit can hand out, but every toolkit can say whether a screen
 * point is on a drawn model, which is how the game picks what is under the mouse (Entity.picked).
 * So the shape is sampled: over the area the location's pick capsules cover this frame (the
 * PickableEntity the scene drew it with, which is also the game's own click box), each sample asks
 * the model; the samples that hit make a mask, whose convex hull is the hull and whose edge is the
 * outline. A mask is kept while the location and its capsules do not move (the camera is still).
 */
object LocGeometry {

	/** The most samples a mask takes: a larger location is sampled more sparsely. */
	private const val MAX_SAMPLES: Int = 6000

	/** The points of each half-circle when a capsule is drawn as a polygon. */
	private const val CAPSULE_STEPS: Int = 8

	/** A sampled shape: the screen area it covers ([x], [y], [columns] by [rows] samples [step] pixels apart) and which samples hit. */
	class Mask(@JvmField val x: Int, @JvmField val y: Int, @JvmField val step: Int, @JvmField val columns: Int, @JvmField val rows: Int, @JvmField val hits: BooleanArray) {
		fun hit(column: Int, row: Int): Boolean {
			return column >= 0 && row >= 0 && column < columns && row < rows && hits[row * columns + column]
		}
	}

	private var cachedEntity: Entity? = null

	private var cachedKey: IntArray = IntArray(0)

	private var cachedMask: Mask? = null

	/** The location [baseId] (its base or its current multiloc id) on scene tile ([tileX], [tileY]), or null. */
	@JvmStatic
	fun find(tileX: Int, tileY: Int, level: Int, baseId: Int): Entity? {
		val tile: Tile = tile(tileX, tileY, level) ?: return null
		val candidates: Array<Any?> = arrayOf(tile.primaryWall, tile.secondaryWall, tile.primaryWallDecoration, tile.secondaryWallDecoration, tile.groundDecoration)
		for (candidate in candidates) {
			if (matches(candidate, baseId)) {
				return candidate as Entity
			}
		}
		var container: InteractiveEntityContainer? = tile.interactives
		while (container != null) {
			val loc: Any? = container.interactiveObject
			if (matches(loc, baseId)) {
				return loc as Entity
			}
			container = container.child
		}
		return null
	}

	private fun matches(candidate: Any?, baseId: Int): Boolean {
		val loc: GameObject = candidate as? GameObject ?: return false
		return candidate is Entity && (loc.getId() == baseId || ClientApiImpl.resolveLoc(loc.getId()) == baseId)
	}

	private fun tile(tileX: Int, tileY: Int, level: Int): Tile? {
		val levels: Array<Array<Array<Tile?>?>?> = Scene.aClass100ArrayArrayArray3 ?: return null
		if (level < 0 || level >= levels.size) {
			return null
		}
		val columns: Array<Array<Tile?>?> = levels[level] ?: return null
		if (tileX < 0 || tileX >= columns.size) {
			return null
		}
		val column: Array<Tile?> = columns[tileX] ?: return null
		return if (tileY < 0 || tileY >= column.size) null else column[tileY]
	}

	/** What the scene drew [entity] with this frame: its pick capsules; null when it was not drawn (off the screen) or cannot be clicked. */
	@JvmStatic
	fun pickable(entity: Entity): PickableEntity? {
		val list: PickList = PickList.aClass182_1 ?: return null
		var node = list.entities.getFirst()
		while (node != null) {
			if (node is PickableEntity && node.entity === entity) {
				return node
			}
			node = list.entities.getNext()
		}
		return null
	}

	/** The screen rectangle the capsules cover: left, top, right, bottom; null with none worked out this frame. */
	internal fun bounds(pickable: PickableEntity): IntArray? {
		val polygons: Array<BoundingBoxPolygon?> = pickable.boundingBoxPolygons ?: return null
		var minX: Int = Int.MAX_VALUE
		var minY: Int = Int.MAX_VALUE
		var maxX: Int = Int.MIN_VALUE
		var maxY: Int = Int.MIN_VALUE
		for (polygon in polygons) {
			if (polygon == null || !polygon.positionCalculated) {
				continue
			}
			minX = minOf(minX, minOf(polygon.left, polygon.right) - polygon.size)
			maxX = maxOf(maxX, maxOf(polygon.left, polygon.right) + polygon.size)
			minY = minOf(minY, minOf(polygon.bottom, polygon.top) - polygon.size)
			maxY = maxOf(maxY, maxOf(polygon.bottom, polygon.top) + polygon.size)
		}
		return if (minX > maxX) null else intArrayOf(minX, minY, maxX, maxY)
	}

	/** The location's sampled shape this frame, in the picking's screen space; null when it was not drawn. */
	@JvmStatic
	fun mask(entity: Entity): Mask? {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return null
		val pickable: PickableEntity = pickable(entity) ?: return null
		val box: IntArray = bounds(pickable) ?: return null
		val width: Int = box[2] - box[0] + 1
		val height: Int = box[3] - box[1] + 1
		if (width <= 0 || height <= 0) {
			return null
		}
		val key: IntArray = intArrayOf(box[0], box[1], box[2], box[3], entity.x, entity.z, entity.level.toInt(), toolkit.index)
		if (entity === cachedEntity && key.contentEquals(cachedKey)) {
			return cachedMask
		}
		var step: Int = 1
		while ((width / step + 1) * (height / step + 1) > MAX_SAMPLES) {
			step++
		}
		val columns: Int = width / step + 1
		val rows: Int = height / step + 1
		val hits: BooleanArray = BooleanArray(columns * rows)
		for (row in 0 until rows) {
			for (column in 0 until columns) {
				hits[row * columns + column] = entity.picked(toolkit, box[1] + row * step, box[0] + column * step)
			}
		}
		val mask: Mask = Mask(box[0], box[1], step, columns, rows, hits)
		cachedEntity = entity
		cachedKey = key
		cachedMask = mask
		return mask
	}

	/** The convex hull of the mask's hits (each hit a [Mask.step]-pixel square), into [xs] and [ys]; the point count, 0 for none. */
	@JvmStatic
	fun hull(mask: Mask, xs: IntArray, ys: IntArray): Int {
		val px: ArrayList<Int> = ArrayList()
		val py: ArrayList<Int> = ArrayList()
		for (row in 0 until mask.rows) {
			// only a row's first and last hit can be on the hull
			var first: Int = -1
			var last: Int = -1
			for (column in 0 until mask.columns) {
				if (mask.hit(column, row)) {
					if (first < 0) {
						first = column
					}
					last = column
				}
			}
			if (first >= 0) {
				for (column in intArrayOf(first, last)) {
					val x: Int = mask.x + column * mask.step
					val y: Int = mask.y + row * mask.step
					px.add(x)
					py.add(y)
					px.add(x + mask.step)
					py.add(y + mask.step)
					px.add(x + mask.step)
					py.add(y)
					px.add(x)
					py.add(y + mask.step)
				}
			}
		}
		if (px.size < 3) {
			return 0
		}
		val order: IntArray = IntArray(px.size)
		val outX: IntArray = IntArray(px.size + 1)
		val outY: IntArray = IntArray(px.size + 1)
		val count: Int = ClientApiImpl.convexHull(px.toIntArray(), py.toIntArray(), px.size, order, outX, outY)
		val n: Int = minOf(count, xs.size, ys.size)
		for (i in 0 until n) {
			xs[i] = outX[i]
			ys[i] = outY[i]
		}
		return n
	}

	/** The game's own click box for the location (its pick capsules, each a line swept by a circle), as one convex outline; the point count. */
	@JvmStatic
	fun clickbox(entity: Entity, xs: IntArray, ys: IntArray): Int {
		val pickable: PickableEntity = pickable(entity) ?: return 0
		val polygons: Array<BoundingBoxPolygon?> = pickable.boundingBoxPolygons ?: return 0
		val px: ArrayList<Int> = ArrayList()
		val py: ArrayList<Int> = ArrayList()
		for (polygon in polygons) {
			if (polygon == null || !polygon.positionCalculated) {
				continue
			}
			for (end in 0 until 2) {
				val cx: Int = if (end == 0) polygon.left else polygon.right
				val cy: Int = if (end == 0) polygon.bottom else polygon.top
				for (i in 0 until CAPSULE_STEPS * 2) {
					val angle: Double = kotlin.math.PI * i / CAPSULE_STEPS
					px.add(cx + kotlin.math.round(polygon.size * kotlin.math.cos(angle)).toInt())
					py.add(cy + kotlin.math.round(polygon.size * kotlin.math.sin(angle)).toInt())
				}
			}
		}
		if (px.size < 3) {
			return 0
		}
		val order: IntArray = IntArray(px.size)
		val outX: IntArray = IntArray(px.size + 1)
		val outY: IntArray = IntArray(px.size + 1)
		val count: Int = ClientApiImpl.convexHull(px.toIntArray(), py.toIntArray(), px.size, order, outX, outY)
		val n: Int = minOf(count, xs.size, ys.size)
		for (i in 0 until n) {
			xs[i] = outX[i]
			ys[i] = outY[i]
		}
		return n
	}

	/**
	 * Draws the mask's outline, as RuneLite's ModelOutlineRenderer: the samples within [width] pixels
	 * outside the shape, in [argb], the outer [feather] pixels of it fading out. [offsetX] and [offsetY]
	 * move the picking's screen space to the frame's.
	 */
	@JvmStatic
	fun drawOutline(toolkit: Toolkit, mask: Mask, width: Int, feather: Int, argb: Int, offsetX: Int, offsetY: Int) {
		val reach: Int = maxOf(1, (width + mask.step - 1) / mask.step)
		val alpha: Int = (argb ushr 24) and 0xFF
		val rgb: Int = argb and 0xFFFFFF
		for (row in -reach until mask.rows + reach) {
			for (column in -reach until mask.columns + reach) {
				val a: Int = outlineAlpha(mask, column, row, reach, width, feather, alpha)
				if (a > 0) {
					toolkit.aa(offsetX + mask.x + column * mask.step, offsetY + mask.y + row * mask.step, mask.step, mask.step, (a shl 24) or rgb, 1)
				}
			}
		}
	}

	/** The outline's alpha at a sample: 0 on the shape or beyond the outline, faded across the feather. */
	private fun outlineAlpha(mask: Mask, column: Int, row: Int, reach: Int, width: Int, feather: Int, alpha: Int): Int {
		if (mask.hit(column, row)) {
			return 0
		}
		val distance: Int = nearestHit(mask, column, row, reach)
		if (distance < 0) {
			return 0
		}
		// from the cell's near edge: a cell next to the shape is 1 pixel off it, however coarse the cells
		val pixels: Int = (distance - 1) * mask.step + 1
		if (feather > 0 && pixels > width - feather) {
			return alpha * (width - pixels + 1) / (feather + 1)
		}
		return alpha
	}

	/** How many samples away the nearest hit is (the larger of the two axis distances), up to [reach]; -1 for none within it. */
	private fun nearestHit(mask: Mask, column: Int, row: Int, reach: Int): Int {
		for (d in 1..reach) {
			for (i in -d..d) {
				if (mask.hit(column + i, row - d) || mask.hit(column + i, row + d) || mask.hit(column - d, row + i) || mask.hit(column + d, row + i)) {
					return d
				}
			}
		}
		return -1
	}
}
