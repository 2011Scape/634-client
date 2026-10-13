package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: what a 634HD frame cost on the GPU side, counted by the toolkit
 * (HdGl) and read by the smoke test's bench and the 634HD debug panel. Other toolkits leave it at 0.
 */
object RenderStats {

	/** Draw calls and triangles of the frame being drawn. */
	@JvmField
	var drawCalls: Int = 0

	@JvmField
	var triangles: Int = 0

	/** The same, for the last finished frame. */
	@JvmField
	var lastDrawCalls: Int = 0

	@JvmField
	var lastTriangles: Int = 0

	/** What is drawing now, for the per-category counts: CATEGORY_OTHER, _MODEL, _GROUND. */
	@JvmField
	var category: Int = 0

	const val CATEGORY_OTHER: Int = 0

	const val CATEGORY_MODEL: Int = 1

	const val CATEGORY_GROUND: Int = 2

	@JvmField
	val categoryDrawCalls: IntArray = IntArray(3)

	@JvmField
	val lastCategoryDrawCalls: IntArray = IntArray(3)

	/** Chunks of the retained scene rebuilt since the region was built (a loc added or removed). */
	@JvmField
	var chunkRebuilds: Int = 0

	/** The longest a frame spent building the retained scene of the last region (its hitch), in ms, or -1. */
	@JvmField
	var lastRegionUploadMs: Int = -1

	/** Milliseconds the last region's retained scene took to build in all, spread over frames, or -1. */
	@JvmField
	var lastRegionBuildMs: Int = -1

	@JvmStatic
	fun frameEnded() {
		for (i in 0 until 3) {
			lastCategoryDrawCalls[i] = categoryDrawCalls[i]
			categoryDrawCalls[i] = 0
		}
		lastDrawCalls = drawCalls
		lastTriangles = triangles
		drawCalls = 0
		triangles = 0
	}
}
