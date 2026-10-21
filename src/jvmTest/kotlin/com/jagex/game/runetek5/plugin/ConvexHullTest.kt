package com.jagex.game.runetek5.plugin

import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: the hull ClientApi.actorHull takes of a model's projected box (ClientApiImpl.convexHull). */
class ConvexHullTest {

	private fun hull(px: IntArray, py: IntArray): List<Pair<Int, Int>> {
		val xs: IntArray = IntArray(px.size + 1)
		val ys: IntArray = IntArray(px.size + 1)
		val n: Int = ClientApiImpl.convexHull(px, py, px.size, IntArray(px.size), xs, ys)
		return (0 until n).map { Pair(xs[it], ys[it]) }
	}

	@Test
	fun theHullOfABoxWithPointsInsideIsItsCorners() {
		val got: List<Pair<Int, Int>> = hull(intArrayOf(0, 10, 10, 0, 5, 3, 7, 5), intArrayOf(0, 0, 10, 10, 5, 4, 6, 1))
		assertEquals(4, got.size)
		assertEquals(setOf(Pair(0, 0), Pair(10, 0), Pair(10, 10), Pair(0, 10)), got.toSet())
	}

	@Test
	fun collinearAndRepeatedPointsCollapse() {
		val got: List<Pair<Int, Int>> = hull(intArrayOf(0, 5, 10, 10, 10, 0, 0, 0), intArrayOf(0, 0, 0, 5, 10, 10, 5, 0))
		assertEquals(setOf(Pair(0, 0), Pair(10, 0), Pair(10, 10), Pair(0, 10)), got.toSet())
	}

	@Test
	fun aLineIsTwoPointsAndOnePointIsOne() {
		assertEquals(2, hull(intArrayOf(0, 5, 10), intArrayOf(0, 0, 0)).size)
		assertEquals(1, hull(intArrayOf(3), intArrayOf(3)).size)
	}
}
