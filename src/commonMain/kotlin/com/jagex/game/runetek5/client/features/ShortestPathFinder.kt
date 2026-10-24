package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.max

/**
 * Not part of the original client: Shortest Path's search, RuneLite's Pathfinder recreated. A search by
 * ticks from the start over the steps [ShortestPathGrid] opens and the transports usable from a tile
 * ([Options.anywhere] ones, the teleports, from the start and from a bank), stopped at the nearest of the
 * [targets], after [Options.maxNodes] tiles, or when [Options.cutoffSteps] slices bring it no nearer: the
 * path then goes to the tile it reached nearest a target. With [Options.banks] the search runs on two
 * layers, before and after a visit to a bank, where the items kept there count as carried. [step] runs a
 * slice at a time, so the game thread can spread it over a few frames (the browser has no threads for it).
 */
class ShortestPathFinder(
	private val grid: ShortestPathGrid,
	@JvmField val start: Int,
	@JvmField val targets: IntArray,
	private val options: Options,
) {
	/** What a search may use besides the steps. */
	class Options {
		/** By the tile they leave from: transports usable with what is carried. */
		@JvmField var transports: Map<Int, List<ShortestPathTransport>> = emptyMap()

		/** Teleports with what is carried, from the start (and from a bank). */
		@JvmField var anywhere: List<ShortestPathTransport> = emptyList()

		/** By the tile they leave from: transports usable once the bank's items are taken out. */
		@JvmField var bankTransports: Map<Int, List<ShortestPathTransport>> = emptyMap()

		/** Teleports with the bank's items, from a bank. */
		@JvmField var bankAnywhere: List<ShortestPathTransport> = emptyList()

		/** Tiles a bank is used from: there the search moves to the layer with the bank's items. */
		@JvmField var banks: Set<Int> = emptySet()

		/** Ticks a visit to the bank costs on top of the way there. */
		@JvmField var bankCost: Int = 0

		/** Whether the wilderness is kept out of the path (neither the start nor a target in it). */
		@JvmField var avoidWilderness: Boolean = false

		@JvmField var maxNodes: Int = 2_500_000

		/** Slices ([step] calls) in a row that bring the search no nearer before it stops; 0 for never. */
		@JvmField var cutoffSteps: Int = 0
	}

	/** Whether the search has finished: [path] is then the way it found. */
	@JvmField var done: Boolean = false

	/** Whether the path ends on a target itself rather than the nearest tile reached. */
	@JvmField var reached: Boolean = false

	/** The tiles of the way ([pack]ed, no layer), the start first; a transport is a jump between two of them. */
	@JvmField var path: IntArray = IntArray(0)

	/** The transport taken onto each tile of [path], null for a step; [BANK] where the bank is visited. */
	@JvmField var pathTransports: Array<ShortestPathTransport?> = arrayOfNulls(0)

	/** How many tiles the search has visited. */
	@JvmField var visitedCount: Int = 0

	/** How far (in tiles) the path's end is from the nearest target. */
	@JvmField var endDistance: Int = 0

	/** By (region id shl 3 or layer shl 2 or level): a byte a tile, 0 unvisited, 1..8 the step in, [VIA_TRANSPORT], [VIA_START], [VIA_BANK]. */
	private val visited: Array<ByteArray?> = arrayOfNulls(65536 * 8)

	private val cameFrom = HashMap<Int, Int>()
	private val cameBy = HashMap<Int, ShortestPathTransport>()

	private val buckets: Array<IntArray> = Array(RING) { IntArray(16) }
	private val bucketSizes: IntArray = IntArray(RING)
	private var queued: Int = 0
	private var cost: Int = 0

	private val targetSet: HashSet<Int> = HashSet<Int>().also { set -> targets.forEach { set.add(it) } }
	private val wildernessAllowed: Boolean = !options.avoidWilderness || isWilderness(start) || targets.any { isWilderness(it) }
	private var best: Int = start
	private var bestDistance: Long = Long.MAX_VALUE
	private var stale: Int = 0

	init {
		mark(start, VIA_START)
		push(start, 0)
		for (transport in options.anywhere) {
			arrive(transport, start, transport.ticks)
		}
		consider(start)
	}

	/** Runs up to [budget] tiles of the search; true once it is [done]. */
	fun step(budget: Int): Boolean {
		var left: Int = budget
		val before: Long = bestDistance
		while (!done && left > 0) {
			if (queued == 0 || visitedCount >= options.maxNodes) {
				finish(best)
			} else if (bucketSizes[cost % RING] == 0) {
				cost++
			} else {
				val slot: Int = cost % RING
				val node: Int = buckets[slot][--bucketSizes[slot]]
				queued--
				left--
				if (targetSet.contains(position(node))) {
					reached = true
					finish(node)
				} else {
					expand(node)
				}
			}
		}
		stale = if (bestDistance < before) 0 else stale + 1
		if (!done && options.cutoffSteps > 0 && stale >= options.cutoffSteps) {
			finish(best)
		}
		return done
	}

	private fun expand(node: Int) {
		val x: Int = x(node)
		val y: Int = y(node)
		val level: Int = level(node)
		val layer: Int = layer(node)
		for (d in 0 until 8) {
			val next: Int = pack(x + DX[d], y + DY[d], level) or (layer shl LAYER_SHIFT)
			if (grid.canStep(x, y, level, DX[d], DY[d]) && open(next)) {
				mark(next, d + 1)
				push(next, cost + 1)
				consider(next)
			}
		}
		val here: Int = position(node)
		options.transports[here]?.forEach { arrive(it, node, cost + it.ticks) }
		if (layer == 1) {
			options.bankTransports[here]?.forEach { arrive(it, node, cost + it.ticks) }
		} else if (here in options.banks) {
			// the bank: on to the layer where its items are carried, and the teleports they give from here
			val banked: Int = node or (1 shl LAYER_SHIFT)
			if (!isVisited(banked)) {
				val at: Int = cost + max(1, options.bankCost)
				mark(banked, VIA_BANK)
				push(banked, at)
				for (transport in options.bankAnywhere) {
					arrive(transport, banked, at + transport.ticks)
				}
				for (transport in options.anywhere) {
					arrive(transport, banked, at + transport.ticks)
				}
			}
		}
	}

	private fun open(node: Int): Boolean = !isVisited(node) && (wildernessAllowed || !isWilderness(node))

	private fun arrive(transport: ShortestPathTransport, from: Int, at: Int) {
		val next: Int = pack(transport.dstX, transport.dstY, transport.dstLevel) or (layer(from) shl LAYER_SHIFT)
		if (!open(next)) {
			return
		}
		mark(next, VIA_TRANSPORT)
		cameFrom[next] = from
		cameBy[next] = transport
		push(next, cost + (at - cost).coerceIn(1, RING - 1))
		consider(next)
	}

	/** The tile nearest a target so far, for a search that runs out before reaching one. */
	private fun consider(node: Int) {
		var distance: Long = Long.MAX_VALUE
		for (target in targets) {
			// the nearest by tiles, then by the two axes together: straight across from a target beats a corner
			val d: Long = distance(node, target).toLong() * 32768L + abs(x(node) - x(target)) + abs(y(node) - y(target))
			if (d < distance) {
				distance = d
			}
		}
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

	/** The way back from [end]: a bank visit is a second entry for its tile, marked [BANK]. */
	private fun finish(end: Int) {
		val nodes = ArrayList<Int>()
		val by = ArrayList<ShortestPathTransport?>()
		var node: Int = end
		while (node != -1 && nodes.size < MAX_PATH) {
			val how: Int = how(node)
			nodes.add(position(node))
			by.add(
				when (how) {
					VIA_TRANSPORT -> cameBy[node]
					VIA_BANK -> BANK
					else -> null
				},
			)
			node = when (how) {
				VIA_START, 0 -> -1
				VIA_TRANSPORT -> cameFrom[node] ?: -1
				VIA_BANK -> node and (1 shl LAYER_SHIFT).inv()
				else -> pack(x(node) - DX[how - 1], y(node) - DY[how - 1], level(node)) or (layer(node) shl LAYER_SHIFT)
			}
		}
		nodes.reverse()
		by.reverse()
		path = IntArray(nodes.size) { nodes[it] }
		pathTransports = Array(by.size) { by[it] }
		endDistance = targets.minOfOrNull { distance(position(end), it) } ?: 0
		done = true
	}

	companion object {
		const val VIA_TRANSPORT: Int = 9
		const val VIA_START: Int = 10
		const val VIA_BANK: Int = 11

		/** The bank visit in [pathTransports]: the items are taken out on that tile. */
		@JvmField
		val BANK: ShortestPathTransport = ShortestPathTransport("BANK", -1, -1, -1, -1, -1, -1, 0, "Bank", emptyMap())

		/** Longer than any transport's cost (its ticks and its threshold): the ring of buckets wraps after this many. */
		private const val RING: Int = 4096

		private const val MAX_PATH: Int = 100_000

		private const val LAYER_SHIFT: Int = 30

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

		private fun layer(node: Int): Int = (node shr LAYER_SHIFT) and 1

		/** The tile without the search's layer. */
		@JvmStatic
		fun position(node: Int): Int = node and ((1 shl LAYER_SHIFT) - 1)

		/** Tiles between two, the larger of the two axes; another level counts as far. */
		@JvmStatic
		fun distance(a: Int, b: Int): Int {
			val d: Int = max(abs(x(a) - x(b)), abs(y(a) - y(b)))
			return if (level(a) == level(b)) d else d + LEVEL_PENALTY
		}

		/**
		 * Whether the tile is in the wilderness: the surface north of the ditch and the dungeons under it, as
		 * the game's wilderness level counts it (RuneLite's WildernessChecker).
		 */
		@JvmStatic
		fun isWilderness(node: Int): Boolean {
			val x: Int = x(node)
			val y: Int = y(node)
			return x in 2944..3391 && (y in 3523..3967 || y in 9918..10366)
		}

		private const val LEVEL_PENALTY: Int = 20_000

		private fun chunk(node: Int): Int = ((((x(node) shr 6) shl 8) or (y(node) shr 6)) shl 3) or (layer(node) shl 2) or level(node)

		private fun tile(node: Int): Int = ((y(node) and 63) shl 6) or (x(node) and 63)
	}
}
