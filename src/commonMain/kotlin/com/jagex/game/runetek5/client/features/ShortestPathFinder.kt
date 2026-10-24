package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.max

/**
 * Not part of the original client: Shortest Path's search, RuneLite's Pathfinder recreated. A search by
 * ticks from the start over the steps [ShortestPathGrid] opens and the [transports] usable from a tile
 * ([anywhere] ones, the teleports, only from the start), stopped at the target or after [maxNodes] tiles,
 * when the path goes to the tile it reached nearest the target. [step] runs a slice of it at a time, so
 * the game thread can spread it over a few frames (the browser has no threads for it).
 */
class ShortestPathFinder(
	private val grid: ShortestPathGrid,
	private val transports: Map<Int, List<ShortestPathTransport>>,
	private val anywhere: List<ShortestPathTransport>,
	@JvmField val start: Int,
	@JvmField val target: Int,
	private val maxNodes: Int,
) {
	/** Whether the search has finished: [path] is then the way it found. */
	@JvmField var done: Boolean = false

	/** Whether the path ends on the target itself rather than the nearest tile reached. */
	@JvmField var reached: Boolean = false

	/** The tiles of the way ([pack]ed), the start first; a transport is a jump between two of them. */
	@JvmField var path: IntArray = IntArray(0)

	/** The transport taken onto each tile of [path], null for a step. */
	@JvmField var pathTransports: Array<ShortestPathTransport?> = arrayOfNulls(0)

	/** How many tiles the search has visited. */
	@JvmField var visitedCount: Int = 0

	/** By (region id shl 2 or level): a byte a tile, 0 unvisited, 1..8 the step that came in, [VIA_TRANSPORT], [VIA_START]. */
	private val visited: Array<ByteArray?> = arrayOfNulls(65536 * 4)

	/** The tile a transport came from and the transport, for each tile reached by one. */
	private val cameFrom = HashMap<Int, Int>()
	private val cameBy = HashMap<Int, ShortestPathTransport>()

	/** Tiles waiting by the tick they are reached on, a ring as long as the longest transport. */
	private val buckets: Array<IntArray> = Array(RING) { IntArray(64) }
	private val bucketSizes: IntArray = IntArray(RING)
	private var queued: Int = 0
	private var cost: Int = 0

	private var best: Int = start
	private var bestDistance: Int = Int.MAX_VALUE

	init {
		mark(start, VIA_START)
		push(start, 0)
		for (transport in anywhere) {
			arrive(transport, start, transport.ticks.coerceIn(1, RING - 1))
		}
		consider(start)
	}

	/** Runs up to [budget] tiles of the search; true once it is [done]. */
	fun step(budget: Int): Boolean {
		var left: Int = budget
		while (!done && left > 0) {
			if (queued == 0 || visitedCount >= maxNodes) {
				finish(best)
			} else if (bucketSizes[cost % RING] == 0) {
				cost++
			} else {
				val slot: Int = cost % RING
				val node: Int = buckets[slot][--bucketSizes[slot]]
				queued--
				left--
				if (node == target) {
					reached = true
					finish(node)
				} else {
					expand(node)
				}
			}
		}
		return done
	}

	private fun expand(node: Int) {
		val x: Int = x(node)
		val y: Int = y(node)
		val level: Int = level(node)
		for (d in 0 until 8) {
			val next: Int = pack(x + DX[d], y + DY[d], level)
			if (grid.canStep(x, y, level, DX[d], DY[d]) && !isVisited(next)) {
				mark(next, d + 1)
				push(next, cost + 1)
				consider(next)
			}
		}
		val from: List<ShortestPathTransport> = transports[node] ?: return
		for (transport in from) {
			arrive(transport, node, cost + transport.ticks.coerceIn(1, RING - 1))
		}
	}

	private fun arrive(transport: ShortestPathTransport, from: Int, at: Int) {
		val next: Int = pack(transport.dstX, transport.dstY, transport.dstLevel)
		if (isVisited(next)) {
			return
		}
		mark(next, VIA_TRANSPORT)
		cameFrom[next] = from
		cameBy[next] = transport
		push(next, at)
		consider(next)
	}

	/** The tile nearest the target so far, for a search that runs out before reaching it. */
	private fun consider(node: Int) {
		// the nearest by tiles, then by the two axes together: straight across from the target beats a corner
		val distance: Int = distance(node, target) * 32768 + abs(x(node) - x(target)) + abs(y(node) - y(target))
		if (distance < bestDistance) {
			bestDistance = distance
			best = node
		}
	}

	private fun push(node: Int, at: Int) {
		val slot: Int = at % RING
		var bucket: IntArray = buckets[slot]
		if (bucketSizes[slot] == bucket.size) {
			bucket = bucket.copyOf(bucket.size * 2)
			buckets[slot] = bucket
		}
		bucket[bucketSizes[slot]++] = node
		queued++
	}

	private fun isVisited(node: Int): Boolean {
		val bits: ByteArray = visited[chunk(node)] ?: return false
		return bits[tile(node)].toInt() != 0
	}

	private fun mark(node: Int, how: Int) {
		val chunk: Int = chunk(node)
		var bits: ByteArray? = visited[chunk]
		if (bits == null) {
			bits = ByteArray(4096)
			visited[chunk] = bits
		}
		bits[tile(node)] = how.toByte()
		visitedCount++
	}

	private fun how(node: Int): Int = visited[chunk(node)]?.get(tile(node))?.toInt() ?: 0

	private fun finish(end: Int) {
		val nodes = ArrayList<Int>()
		val by = ArrayList<ShortestPathTransport?>()
		var node: Int = end
		while (node != -1 && nodes.size < MAX_PATH) {
			nodes.add(node)
			val how: Int = how(node)
			by.add(if (how == VIA_TRANSPORT) cameBy[node] else null)
			node = when (how) {
				VIA_START, 0 -> -1
				VIA_TRANSPORT -> cameFrom[node] ?: -1
				else -> pack(x(node) - DX[how - 1], y(node) - DY[how - 1], level(node))
			}
		}
		nodes.reverse()
		by.reverse()
		path = IntArray(nodes.size) { nodes[it] }
		pathTransports = Array(by.size) { by[it] }
		done = true
	}

	companion object {
		const val VIA_TRANSPORT: Int = 9
		const val VIA_START: Int = 10

		/** Longer than any transport's ticks: the ring of buckets wraps after this many. */
		private const val RING: Int = 128

		/** No path is longer than this many tiles (the world is 16384 across, the searches far shorter). */
		private const val MAX_PATH: Int = 100_000

		/** Straight steps first, so a path prefers them where a diagonal would do as well. */
		private val DX: IntArray = intArrayOf(-1, 1, 0, 0, -1, 1, -1, 1)
		private val DY: IntArray = intArrayOf(0, 0, -1, 1, -1, -1, 1, 1)

		@JvmStatic
		fun pack(x: Int, y: Int, level: Int): Int = (x and 0x3FFF) or ((y and 0x3FFF) shl 14) or ((level and 3) shl 28)

		@JvmStatic
		fun x(node: Int): Int = node and 0x3FFF

		@JvmStatic
		fun y(node: Int): Int = (node shr 14) and 0x3FFF

		@JvmStatic
		fun level(node: Int): Int = (node shr 28) and 3

		/** Tiles between two, the larger of the two axes; another level counts as far. */
		@JvmStatic
		fun distance(a: Int, b: Int): Int {
			val d: Int = max(abs(x(a) - x(b)), abs(y(a) - y(b)))
			return if (level(a) == level(b)) d else d + LEVEL_PENALTY
		}

		private const val LEVEL_PENALTY: Int = 20_000

		private fun chunk(node: Int): Int = ((((x(node) shr 6) shl 8) or (y(node) shr 6)) shl 2) or level(node)

		private fun tile(node: Int): Int = ((y(node) and 63) shl 6) or (x(node) and 63)
	}
}
