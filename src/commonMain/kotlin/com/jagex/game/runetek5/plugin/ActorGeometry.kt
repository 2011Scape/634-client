package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.entity.PickableEntity
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.model.FaceBuffer
import com.jagex.game.runetek5.graphics.model.Model
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.concurrent.Volatile

/**
 * Not part of the original client: the silhouette of a player or an npc on the screen for the plugins
 * (ClientApi.actorOutline, actorShape), the actor counterpart of LocGeometry. An actor's models are
 * animated for the frame and gone once drawn, so the shape is taken while they are drawn: a plugin asks
 * for an actor ([request]), and when the scene draws that actor ([capture], from the actor's render) its
 * models' triangles are projected as picking projects them (Model.projectFaces) and filled into a mask,
 * the same kind of mask LocGeometry samples for a location, so the same hull and outline code draws it.
 * A toolkit that cannot hand out triangles (the native SSE one) is sampled with Model.picked instead.
 * Asking costs nothing for the other actors; an actor not asked for in a few frames is let go.
 */
object ActorGeometry {

	/** The most mask cells: a bigger actor gets coarser cells. As LocGeometry's. */
	const val MAX_CELLS: Int = 6000

	/** The most picks the sampling fallback makes: each one projects the whole model (about 5 us each on the SSE toolkit). */
	private const val MAX_PICKS: Int = 600

	/** Frames an actor stays asked for after the last ask. */
	private const val KEEP_FRAMES: Int = 5

	/** Projected corners further out than this are off any screen: the mask is kept to it. */
	private const val LIMIT: Int = 8192

	/** How many actors can be asked for at once (a plugin's hovered and interacted ones, others'). */
	const val SLOTS: Int = 8

	/** An actor asked for: when it was last asked for, its last silhouette and the frame it was taken in. */
	class Slot {
		@Volatile
		@JvmField
		var entity: PathingEntity? = null

		@JvmField
		var requestedCycle: Int = -1

		@Volatile
		@JvmField
		var mask: LocGeometry.Mask? = null

		@JvmField
		var maskCycle: Int = -1

		/** Whether the mask was sampled this time (so the next frame may keep it). */
		@JvmField
		var sampled: Boolean = false

		/** Its own triangles: actors can be drawn on more than one render worker. */
		@JvmField
		val faces: FaceBuffer = FaceBuffer()
	}

	@JvmField
	val slots: Array<Slot> = Array(SLOTS) { Slot() }

	/** For the smoke test: how long the last silhouette took to take. */
	@JvmField
	var lastCaptureNanos: Long = 0L

	/** Asks for [entity]'s silhouette from the frames drawn from now on; the slot asked for longest ago goes when all are taken. */
	@JvmStatic
	fun request(entity: PathingEntity) {
		val now: Int = SceneDrawer.timesSceneRendered
		var free: Slot = slots[0]
		for (slot in slots) {
			if (slot.entity === entity) {
				slot.requestedCycle = now
				return
			}
			if (slot.entity == null || slot.requestedCycle < free.requestedCycle) {
				free = slot
			}
		}
		free.mask = null
		free.maskCycle = -1
		free.requestedCycle = now
		free.entity = entity
	}

	/** [entity]'s silhouette as this frame drew it, or null (not asked for until now, or not drawn). */
	@JvmStatic
	fun current(entity: PathingEntity): LocGeometry.Mask? {
		for (slot in slots) {
			if (slot.entity === entity) {
				val m: LocGeometry.Mask? = slot.mask
				return if (slot.maskCycle == SceneDrawer.timesSceneRendered) m else null
			}
		}
		return null
	}

	/** From the actor's render, with its [models] about to be let go: the silhouette, when it is one asked for. */
	@JvmStatic
	fun capture(entity: PathingEntity, models: Array<Model?>, matrix: Matrix, pickable: PickableEntity?, toolkit: Toolkit?) {
		val slot: Slot = slots.firstOrNull { it.entity === entity } ?: return
		if (SceneDrawer.timesSceneRendered - slot.requestedCycle > KEEP_FRAMES) {
			slot.entity = null
			slot.mask = null
			return
		}
		if (OrthoMode.orthographicProjection) {
			return // picking's orthographic space is not the one drawn into
		}
		val started: Long = lang.nanoTime()
		val faces: FaceBuffer = slot.faces
		var projected: Boolean = project(models, matrix, faces)
		// 634HD lets a drawn model's vertices go once uploaded: picking's own models keep them (the drawn ones are let go next anyway)
		if (!projected && entity.pickModels(toolkit)) {
			projected = project(models, matrix, faces)
		}
		val now: Int = SceneDrawer.timesSceneRendered
		if (!projected && slot.maskCycle == now - 1 && slot.sampled) {
			// sampling is slow: a sampled shape is taken every other frame, the last one kept between
			slot.maskCycle = now
			slot.sampled = false
			return
		}
		slot.mask = if (projected) rasterise(faces.points, faces.count, MAX_CELLS) else sample(models, matrix, pickable)
		slot.sampled = !projected
		slot.maskCycle = SceneDrawer.timesSceneRendered
		lastCaptureNanos = lang.nanoTime() - started
	}

	/** Every model's triangles into [faces]; false when one of them cannot be projected. */
	private fun project(models: Array<Model?>, matrix: Matrix, faces: FaceBuffer): Boolean {
		faces.clear()
		for (model in models) {
			if (model != null && !model.projectFaces(matrix, faces)) {
				return false
			}
		}
		return true
	}

	/**
	 * Fills the [count] triangles in [points] (six ints each: x, y of three corners) into a mask over the
	 * area they cover: a cell is a hit when its centre is inside a triangle (edges included). Null for
	 * nothing to fill.
	 */
	@JvmStatic
	fun rasterise(points: IntArray, count: Int, maxCells: Int): LocGeometry.Mask? {
		var minX: Int = Int.MAX_VALUE
		var minY: Int = Int.MAX_VALUE
		var maxX: Int = Int.MIN_VALUE
		var maxY: Int = Int.MIN_VALUE
		for (i in 0 until count * 3) {
			val x: Int = points[i * 2].coerceIn(-LIMIT, LIMIT)
			val y: Int = points[i * 2 + 1].coerceIn(-LIMIT, LIMIT)
			minX = minOf(minX, x)
			maxX = maxOf(maxX, x)
			minY = minOf(minY, y)
			maxY = maxOf(maxY, y)
		}
		if (count == 0 || minX > maxX) {
			return null
		}
		val width: Int = maxX - minX + 1
		val height: Int = maxY - minY + 1
		var step: Int = 1
		while ((width / step + 1) * (height / step + 1) > maxCells) {
			step++
		}
		val columns: Int = width / step + 1
		val rows: Int = height / step + 1
		val hits: BooleanArray = BooleanArray(columns * rows)
		for (t in 0 until count) {
			fill(points, t * 6, minX, minY, step, columns, rows, hits)
		}
		return LocGeometry.Mask(minX, minY, step, columns, rows, hits)
	}

	/** One triangle into [hits]: each cell whose centre the three edges agree on. */
	private fun fill(p: IntArray, at: Int, originX: Int, originY: Int, step: Int, columns: Int, rows: Int, hits: BooleanArray) {
		val ax: Long = p[at].coerceIn(-LIMIT, LIMIT).toLong()
		val ay: Long = p[at + 1].coerceIn(-LIMIT, LIMIT).toLong()
		val bx: Long = p[at + 2].coerceIn(-LIMIT, LIMIT).toLong()
		val by: Long = p[at + 3].coerceIn(-LIMIT, LIMIT).toLong()
		val cx: Long = p[at + 4].coerceIn(-LIMIT, LIMIT).toLong()
		val cy: Long = p[at + 5].coerceIn(-LIMIT, LIMIT).toLong()
		val area: Long = (bx - ax) * (cy - ay) - (by - ay) * (cx - ax)
		if (area == 0L) {
			return
		}
		// the cells whose centres can be inside: centre of cell c is origin + c * step + step / 2
		val half: Long = (step / 2).toLong()
		val firstColumn: Int = ((minOf(ax, bx, cx) - originX - half + step - 1) / step).toInt().coerceAtLeast(0)
		val lastColumn: Int = ((maxOf(ax, bx, cx) - originX - half) / step).toInt().coerceAtMost(columns - 1)
		val firstRow: Int = ((minOf(ay, by, cy) - originY - half + step - 1) / step).toInt().coerceAtLeast(0)
		val lastRow: Int = ((maxOf(ay, by, cy) - originY - half) / step).toInt().coerceAtMost(rows - 1)
		for (row in firstRow..lastRow) {
			val y: Long = originY + row.toLong() * step + half
			for (column in firstColumn..lastColumn) {
				val x: Long = originX + column.toLong() * step + half
				val w0: Long = (bx - ax) * (y - ay) - (by - ay) * (x - ax)
				val w1: Long = (cx - bx) * (y - by) - (cy - by) * (x - bx)
				val w2: Long = (ax - cx) * (y - cy) - (ay - cy) * (x - cx)
				val inside: Boolean = if (area > 0) w0 >= 0 && w1 >= 0 && w2 >= 0 else w0 <= 0 && w1 <= 0 && w2 <= 0
				if (inside) {
					hits[row * columns + column] = true
				}
			}
		}
	}

	/** A model's pick, false for one that cannot be picked closely (its vertices let go). */
	@Suppress("TooGenericExceptionCaught", "SwallowedException")
	private fun pickedSafely(model: Model, x: Int, y: Int, matrix: Matrix): Boolean {
		return try {
			model.picked(x, y, matrix, false, 0)
		} catch (e: NullPointerException) {
			false
		}
	}

	/** The fallback: the area the actor's pick capsules cover, sampled with each model's own pick. */
	private fun sample(models: Array<Model?>, matrix: Matrix, pickable: PickableEntity?): LocGeometry.Mask? {
		val box: IntArray = (if (pickable == null) null else LocGeometry.bounds(pickable)) ?: return null
		val width: Int = box[2] - box[0] + 1
		val height: Int = box[3] - box[1] + 1
		var step: Int = 1
		while ((width / step + 1) * (height / step + 1) > MAX_PICKS) {
			step++
		}
		val columns: Int = width / step + 1
		val rows: Int = height / step + 1
		val hits: BooleanArray = BooleanArray(columns * rows)
		for (row in 0 until rows) {
			for (column in 0 until columns) {
				val x: Int = box[0] + column * step + step / 2
				val y: Int = box[1] + row * step + step / 2
				hits[row * columns + column] = models.any { it != null && pickedSafely(it, x, y, matrix) }
			}
		}
		return LocGeometry.Mask(box[0], box[1], step, columns, rows, hits)
	}
}
