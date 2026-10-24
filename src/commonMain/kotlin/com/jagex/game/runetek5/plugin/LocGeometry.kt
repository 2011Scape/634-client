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

	/** The most picks a location's first, coarse pass makes: a larger location is sampled more sparsely. */
	private const val COARSE_SAMPLES: Int = 2500

	/** The most picks spent again along its edge, finer. */
	private const val EDGE_SAMPLES: Int = 4000

	/** The most cells a refined mask holds: past it the fine cells stay coarser. */
	private const val MAX_FINE_CELLS: Int = 80000

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
		val mask: Mask = sampleShape(box[0], box[1], width, height, COARSE_SAMPLES, EDGE_SAMPLES) { x, y -> entity.picked(toolkit, y, x) }
		cachedEntity = entity
		cachedKey = key
		cachedMask = mask
		return mask
	}

	/**
	 * A shape sampled with [pick] (is this screen point on it?) over the [width] by [height] box at ([left], [top]):
	 * first coarse, at most [coarseBudget] picks, then again finer along its edge (the coarse cells that differ from
	 * a neighbour), at most [edgeBudget] picks more, the cells inside and outside the edge copied from the coarse
	 * pass. A cell is sampled at its top-left pixel.
	 */
	@JvmStatic
	fun sampleShape(left: Int, top: Int, width: Int, height: Int, coarseBudget: Int, edgeBudget: Int, pick: (Int, Int) -> Boolean): Mask {
		var step: Int = 1
		while ((width / step + 1) * (height / step + 1) > coarseBudget) {
			step++
		}
		val columns: Int = width / step + 1
		val rows: Int = height / step + 1
		val hits: BooleanArray = BooleanArray(columns * rows)
		for (row in 0 until rows) {
			for (column in 0 until columns) {
				hits[row * columns + column] = pick(left + column * step, top + row * step)
			}
		}
		val coarse: Mask = Mask(left, top, step, columns, rows, hits)
		if (step == 1) {
			return coarse
		}
		val edge: BooleanArray = BooleanArray(columns * rows)
		var edges: Int = 0
		for (row in 0 until rows) {
			for (column in 0 until columns) {
				if (differsFromNeighbour(coarse, column, row)) {
					edge[row * columns + column] = true
					edges++
				}
			}
		}
		// the finest cells whose edge picks fit the budget, and the mask not too big
		var fine: Int = 1
		while (fine < step && (edges.toLong() * ((step + fine - 1) / fine) * ((step + fine - 1) / fine) > edgeBudget || (width / fine + 1).toLong() * (height / fine + 1) > MAX_FINE_CELLS)) {
			fine++
		}
		if (fine >= step) {
			return coarse
		}
		val fineColumns: Int = width / fine + 1
		val fineRows: Int = height / fine + 1
		val fineHits: BooleanArray = BooleanArray(fineColumns * fineRows)
		for (row in 0 until fineRows) {
			val coarseRow: Int = minOf(row * fine / step, rows - 1)
			for (column in 0 until fineColumns) {
				val coarseColumn: Int = minOf(column * fine / step, columns - 1)
				fineHits[row * fineColumns + column] = if (edge[coarseRow * columns + coarseColumn]) pick(left + column * fine, top + row * fine) else coarse.hit(coarseColumn, coarseRow)
			}
		}
		return Mask(left, top, fine, fineColumns, fineRows, fineHits)
	}

	private fun differsFromNeighbour(mask: Mask, column: Int, row: Int): Boolean {
		val hit: Boolean = mask.hit(column, row)
		for (dy in -1..1) {
			for (dx in -1..1) {
				if ((dx != 0 || dy != 0) && mask.hit(column + dx, row + dy) != hit) {
					return true
				}
			}
		}
		return false
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

	/** Squared distances to the shape, one per cell of the area an outline covers; reused frame to frame. */
	private var distances: FloatArray = FloatArray(0)

	/**
	 * Draws the mask's outline, as RuneLite's ModelOutlineRenderer: the pixels within [width] of the shape (by their
	 * distance from it, so corners round off), in [argb], the outer [feather] pixels of it fading out. [offsetX] and
	 * [offsetY] move the picking's screen space to the frame's. A row's cells of one alpha are drawn as one box.
	 */
	@JvmStatic
	fun drawOutline(toolkit: Toolkit, mask: Mask, width: Int, feather: Int, argb: Int, offsetX: Int, offsetY: Int) {
		val step: Int = mask.step
		val reach: Int = (width + step - 1) / step + 1
		val columns: Int = mask.columns + reach * 2
		val rows: Int = mask.rows + reach * 2
		if (distances.size < columns * rows) {
			distances = FloatArray(columns * rows)
		}
		val d2: FloatArray = distances
		d2.fill(Float.MAX_VALUE, 0, columns * rows)
		val stepSquared: Float = (step * step).toFloat()
		// from each hit on the shape's edge, the cells around it that are not on the shape
		for (row in 0 until mask.rows) {
			for (column in 0 until mask.columns) {
				if (!mask.hit(column, row) || !differsFromNeighbour(mask, column, row)) {
					continue
				}
				for (dy in -reach..reach) {
					for (dx in -reach..reach) {
						if (mask.hit(column + dx, row + dy)) {
							continue
						}
						val at: Int = (row + dy + reach) * columns + column + dx + reach
						val d: Float = (dx * dx + dy * dy) * stepSquared
						if (d < d2[at]) {
							d2[at] = d
						}
					}
				}
			}
		}
		val alpha: Int = (argb ushr 24) and 0xFF
		val rgb: Int = argb and 0xFFFFFF
		for (row in 0 until rows) {
			var runStart: Int = 0
			var runAlpha: Int = 0
			for (column in 0..columns) {
				val a: Int = if (column == columns) 0 else outlineAlpha(d2[row * columns + column], step, width, feather, alpha)
				if (a != runAlpha) {
					if (runAlpha > 0) {
						toolkit.aa(offsetX + mask.x + (runStart - reach) * step, offsetY + mask.y + (row - reach) * step, (column - runStart) * step, step, (runAlpha shl 24) or rgb, 1)
					}
					runStart = column
					runAlpha = a
				}
			}
		}
	}

	/**
	 * The outline's alpha at a cell [squared] (squared pixels) from the shape's nearest cell: full near it, fading
	 * across the outer [feather] pixels of [width], 0 beyond; in steps of 16, so a row's cells share runs.
	 */
	@JvmStatic
	fun outlineAlpha(squared: Float, step: Int, width: Int, feather: Int, alpha: Int): Int {
		if (squared == Float.MAX_VALUE) {
			return 0
		}
		// centre to centre, less half a cell: how far the cell is from the shape's edge
		val distance: Float = kotlin.math.sqrt(squared) - step * 0.5f
		if (distance > width) {
			return 0
		}
		if (feather <= 0 || distance <= width - feather) {
			return alpha
		}
		val a: Int = (alpha * (width + 0.5f - distance) / (feather + 0.5f)).toInt().coerceIn(0, alpha)
		val stepped: Int = a and 0xF0
		return if (stepped == 0 && a > 0) 16 else stepped
	}
}
