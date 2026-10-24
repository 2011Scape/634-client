package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ShortestPathFinder
import com.jagex.game.runetek5.client.features.ShortestPathFinder.Companion.pack
import com.jagex.game.runetek5.client.features.ShortestPathGrid
import com.jagex.game.runetek5.client.features.ShortestPathTransport
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Shortest Path's search on made-up maps, and on the map the jar ships. */
class ShortestPathTest {

	/** Region (0, 0) on levels 0 and 1, every step open until a test closes one. */
	private val regions: Array<Array<ByteArray?>?> = arrayOfNulls<Array<ByteArray?>?>(65536).also { it[0] = arrayOf(open(), open(), null, null) }

	private fun open(): ByteArray = ByteArray(1024) { -1 }

	private fun close(x: Int, y: Int, level: Int, east: Int) {
		val bits: ByteArray = regions[0]!![level]!!
		val i: Int = (y * 64 + x) * 2 + east
		bits[i shr 3] = (bits[i shr 3].toInt() and (1 shl (i and 7)).inv()).toByte()
	}

	/** A wall along the east side of column [x] on [level], all the way up but for [gapY]. */
	private fun wall(x: Int, level: Int, gapY: Int) {
		for (y in 0 until 64) {
			if (y != gapY) {
				close(x, y, level, 1)
			}
		}
	}

	private fun search(grid: ShortestPathGrid, start: Int, target: Int, transports: List<ShortestPathTransport> = emptyList(), anywhere: List<ShortestPathTransport> = emptyList()): ShortestPathFinder {
		val bySource = transports.groupBy { pack(it.srcX, it.srcY, it.srcLevel) }
		val finder = ShortestPathFinder(grid, bySource, anywhere, start, target, 2_500_000)
		var guard = 0
		while (!finder.step(40_000)) {
			assertTrue(guard++ < 10_000, "the search never finished")
		}
		return finder
	}

	private fun transport(sx: Int, sy: Int, sl: Int, dx: Int, dy: Int, dl: Int, ticks: Int = 3): ShortestPathTransport =
		ShortestPathTransport("OBJECT", sx, sy, sl, dx, dy, dl, ticks, "Climb-up Ladder", emptyMap())

	@Test
	fun openGroundGoesStraight() {
		val finder = search(ShortestPathGrid(regions), pack(2, 2, 0), pack(12, 7, 0))
		assertTrue(finder.reached)
		// ten steps (five of them diagonal): eleven tiles, the start included
		assertEquals(11, finder.path.size)
		assertEquals(pack(2, 2, 0), finder.path.first())
		assertEquals(pack(12, 7, 0), finder.path.last())
	}

	@Test
	fun aWallSendsThePathThroughItsGap() {
		wall(10, 0, 40)
		val finder = search(ShortestPathGrid(regions), pack(5, 5, 0), pack(15, 5, 0))
		assertTrue(finder.reached)
		assertTrue(finder.path.any { ShortestPathFinder.x(it) == 10 && ShortestPathFinder.y(it) == 40 }, "the path crosses at the gap")
		for (i in 1 until finder.path.size) {
			val a: Int = finder.path[i - 1]
			val b: Int = finder.path[i]
			if (ShortestPathFinder.x(a) == 10 && ShortestPathFinder.x(b) == 11) {
				assertEquals(40, ShortestPathFinder.y(a))
			}
		}
	}

	@Test
	fun aDiagonalNeedsBothSidesOpen() {
		val grid = ShortestPathGrid(regions)
		assertTrue(grid.canStep(5, 5, 0, 1, 1))
		close(5, 5, 0, 1) // no step east from (5, 5)
		assertFalse(grid.canStep(5, 5, 0, 1, 1))
		assertTrue(grid.canStep(5, 5, 0, 0, 1))
	}

	@Test
	fun laddersJoinTheLevels() {
		// level 1 cut off from level 0 but for the ladder
		val finder = search(ShortestPathGrid(regions), pack(2, 2, 0), pack(30, 30, 1), listOf(transport(20, 20, 0, 20, 21, 1)))
		assertTrue(finder.reached)
		val i: Int = finder.path.indexOf(pack(20, 21, 1))
		assertTrue(i > 0, "the path climbs the ladder")
		assertEquals(pack(20, 20, 0), finder.path[i - 1])
		assertNotNull(finder.pathTransports[i])
		assertNull(finder.pathTransports[i - 1])
	}

	@Test
	fun aTeleportStartsTheWayFromWhereThePlayerIs() {
		val teleport = ShortestPathTransport("TELEPORT_SPELL", -1, -1, -1, 60, 60, 0, 5, "Varrock Teleport", emptyMap())
		val finder = search(ShortestPathGrid(regions), pack(1, 1, 0), pack(61, 61, 0), anywhere = listOf(teleport))
		assertTrue(finder.reached)
		assertEquals(pack(60, 60, 0), finder.path[1])
		assertEquals(3, finder.path.size)
	}

	@Test
	fun anUnreachableTargetEndsAsNearAsTheMapAllows() {
		for (y in 0 until 64) {
			close(10, y, 0, 1)
		}
		val finder = search(ShortestPathGrid(regions), pack(2, 2, 0), pack(20, 2, 0))
		assertFalse(finder.reached)
		assertEquals(pack(10, 2, 0), finder.path.last())
	}

	@Test
	fun theTableParses() {
		val text = "# comment\nOBJECT\t3204 3207 0\t3205 3209 1\t3\tClimb-up Staircase\t\n" +
			"TELEPORT_SPELL\t\t3212 3428 0\t5\tVarrock Teleport\tmagic=25;book=modern;items=554x1,556x3,563x1\n"
		val table: List<ShortestPathTransport> = ShortestPathTransport.parse(text)
		assertEquals(2, table.size)
		assertEquals(3205, table[0].dstX)
		assertEquals(1, table[0].dstLevel)
		assertFalse(table[0].anywhere)
		assertTrue(table[1].anywhere)
		assertEquals("25", table[1].requirements["magic"])
		assertEquals("554x1,556x3,563x1", table[1].requirements["items"])
	}

	/** The map the jar ships: Lumbridge's castle to Varrock's square on foot, and up the castle's stairs. */
	@Test
	fun theShippedMapWalksLumbridgeToVarrock() {
		val grid = ShortestPathGrid.decode(lang.readResource("/shortest-path/collision.gz")!!)
		val table = ShortestPathTransport.decode(lang.readResource("/shortest-path/transports.tsv.gz")!!)
		assertTrue(table.size > 1000)
		val objects = table.filter { it.type == "OBJECT" }
		val walk = search(grid, pack(3222, 3218, 0), pack(3212, 3428, 0), objects)
		assertTrue(walk.reached, "Varrock is reachable on foot")
		assertTrue(walk.path.size in 200..320, "a road's length, not a detour: ${walk.path.size}")
		val upstairs = search(grid, pack(3222, 3218, 0), pack(3207, 3220, 2), objects)
		assertTrue(upstairs.reached, "the castle's top floor is reachable by its stairs")
		assertTrue(upstairs.pathTransports.count { it != null } >= 2, "two flights of stairs")
	}
}
