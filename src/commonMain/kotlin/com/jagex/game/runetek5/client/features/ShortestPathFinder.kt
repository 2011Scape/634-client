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
 * layers, before and after a visit to a bank, where the items kept there count as carried.
 *
 * Tiles are taken in the order they were reached within a tick (first in, first out), so a path goes
 * straight before it goes diagonal. A step costs a tick and marks its tile when it is queued; a transport
 * (or a bank visit) costs more, so its landing is queued unmarked and only taken if nothing reached that
 * tile sooner. [step] runs a slice at a time, so the game thread can spread it over a few frames (the
 * browser has no threads for it).
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

		/** The wilderness, as the server has it; null for none. */
		@JvmField var wilderness: ShortestPathArea? = null

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

	/** Transports' and bank visits' arrivals: the tile, where from, how; a bucket holds -(index + 1) for one. */
	private var arrivalNode: IntArray = IntArray(64)
	private var arrivalFrom: IntArray = IntArray(64)
	private val arrivalBy = ArrayList<ShortestPathTransport>()

	/** The tile a transport came from and the transport, for each tile reached by one. */
	private val cameFrom = HashMap<Int, Int>()
	private val cameBy = HashMap<Int, ShortestPathTransport>()

	/** Entries waiting by the tick they are reached on, a ring longer than any one cost; each a queue (head to size). */
	private val ring: Int = ringSize(options)
	private val buckets: Array<IntArray> = Array(ring) { IntArray(16) }
	private val bucketHeads: IntArray = IntArray(ring)
	private val bucketSizes: IntArray = IntArray(ring)
	private var queued: Int = 0
	private var cost: Int = 0

	private val sortedTargets: IntArray = targets.copyOf().also { it.sort() }
	private val sortedBanks: IntArray = options.banks.toIntArray().also { it.sort() }
	private val wildernessAllowed: Boolean = !options.avoidWilderness || options.wilderness == null || isWilderness(start) || targets.any { isWilderness(it) }
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

	/**
	 * Runs up to [budget] entries of the search, stopping early once [lang.nanoTime] passes [deadline]
	 * (checked every few hundred); true once it is [done].
	 */
	fun step(budget: Int, deadline: Long = Long.MAX_VALUE): Boolean {
		var left: Int = budget
		val before: Long = bestDistance
		var sinceClock = 0
		while (!done && left > 0) {
			if (queued == 0 || visitedCount >= options.maxNodes) {
				finish(best)
			} else {
				val slot: Int = cost % ring
				if (bucketHeads[slot] == bucketSizes[slot]) {
					bucketHeads[slot] = 0
					bucketSizes[slot] = 0
					cost++
				} else {
					val entry: Int = buckets[slot][bucketHeads[slot]++]
					queued--
					left--
					take(entry)
					if (++sinceClock >= CLOCK_EVERY) {
						sinceClock = 0
						if (lang.nanoTime() > deadline) {
							break
						}
					}
				}
			}
		}
		stale = if (bestDistance < before) 0 else stale + 1
		if (!done && options.cutoffSteps > 0 && stale >= options.cutoffSteps) {
			finish(best)
		}
		return done
	}

	/** A queued entry at the current tick: a tile reached by a step, or a transport's or bank's arrival. */
	private fun take(entry: Int) {
		var node: Int = entry
		if (entry < 0) {
			val i: Int = -entry - 1
			node = arrivalNode[i]
			if (isVisited(node)) {
				return // reached sooner some other way
			}
			val by: ShortestPathTransport = arrivalBy[i]
			if (by === BANK) {
				mark(node, VIA_BANK)
				// the bank's teleports, and the ones carried, from the bank
				for (transport in options.bankAnywhere) {
					arrive(transport, node, cost + transport.ticks)
				}
				for (transport in options.anywhere) {
					arrive(transport, node, cost + transport.ticks)
				}
			} else {
				mark(node, VIA_TRANSPORT)
				cameFrom[node] = arrivalFrom[i]
				cameBy[node] = by
			}
			consider(node)
		}
		if (contains(sortedTargets, position(node))) {
			reached = true
			finish(node)
		} else {
			expand(node)
		}
	}

	private fun expand(node: Int) {
		val x: Int = x(node)
		val y: Int = y(node)
		val level: Int = level(node)
		val layer: Int = layer(node)
		for (d in 0 until 8) {
			val nx: Int = x + DX[d]
			val ny: Int = y + DY[d]
			if (nx < 0 || ny < 0 || nx > MAX_COORD || ny > MAX_COORD) {
				continue
			}
			val next: Int = pack(nx, ny, level) or (layer shl LAYER_SHIFT)
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
		} else if (contains(sortedBanks, here)) {
			// the bank: on to the layer where its items are carried
			queueArrival(node or (1 shl LAYER_SHIFT), node, BANK, cost + max(1, options.bankCost))
		}
	}

	private fun open(node: Int): Boolean = !isVisited(node) && (wildernessAllowed || !isWilderness(node))

	private fun arrive(transport: ShortestPathTransport, from: Int, at: Int) {
		val next: Int = pack(transport.dstX, transport.dstY, transport.dstLevel) or (layer(from) shl LAYER_SHIFT)
		if (open(next)) {
			queueArrival(next, from, transport, at)
		}
	}

	private fun queueArrival(node: Int, from: Int, by: ShortestPathTransport, at: Int) {
		val i: Int = arrivalBy.size
		if (i == arrivalNode.size) {
			arrivalNode = arrivalNode.copyOf(i * 2)
			arrivalFrom = arrivalFrom.copyOf(i * 2)
		}
		arrivalNode[i] = node
		arrivalFrom[i] = from
		arrivalBy.add(by)
		push(-(i + 1), cost + (at - cost).coerceIn(1, ring - 1))
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

	private fun push(entry: Int, at: Int) {
		val slot: Int = at % ring
		var bucket: IntArray = buckets[slot]
		if (bucketSizes[slot] == bucket.size) {
			bucket = bucket.copyOf(bucket.size * 2)
			buckets[slot] = bucket
		}
		bucket[bucketSizes[slot]++] = entry
		queued++
	}

	private fun isWilderness(node: Int): Boolean = options.wilderness?.contains(x(node), y(node), level(node)) == true

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

		private const val MAX_PATH: Int = 100_000

		private const val MAX_COORD: Int = 0x3FFF

		private const val LAYER_SHIFT: Int = 30

		/** How many entries between looks at the clock. */
		private const val CLOCK_EVERY: Int = 512

		/** Straight steps first, so a path prefers them where a diagonal would do as well. */
		private val DX: IntArray = intArrayOf(-1, 1, 0, 0, -1, 1, -1, 1)
		private val DY: IntArray = intArrayOf(0, 0, -1, 1, -1, -1, 1, 1)

		/** A ring of buckets longer than the dearest cost (a transport's ticks, a bank visit), a power of two. */
		private fun ringSize(options: Options): Int {
			var most: Int = max(2, options.bankCost + 1)
			for (list in listOf(options.anywhere, options.bankAnywhere)) {
				for (t in list) most = max(most, t.ticks)
			}
			for (map in listOf(options.transports, options.bankTransports)) {
				for (list in map.values) for (t in list) most = max(most, t.ticks)
			}
			var size = 64
			while (size <= most + 1) {
				size *= 2
			}
			return size
		}

		/** Whether sorted [sorted] holds [value] (a binary search: IntArray.binarySearch is the JVM's only). */
		private fun contains(sorted: IntArray, value: Int): Boolean {
			var low = 0
			var high: Int = sorted.size - 1
			while (low <= high) {
				val middle: Int = (low + high) ushr 1
				val v: Int = sorted[middle]
				when {
					v < value -> low = middle + 1
					v > value -> high = middle - 1
					else -> return true
				}
			}
			return false
		}

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

		private const val LEVEL_PENALTY: Int = 20_000

		private fun chunk(node: Int): Int = ((((x(node) shr 6) shl 8) or (y(node) shr 6)) shl 3) or (layer(node) shl 2) or level(node)

		private fun tile(node: Int): Int = ((y(node) and 63) shl 6) or (x(node) and 63)
	}
}
