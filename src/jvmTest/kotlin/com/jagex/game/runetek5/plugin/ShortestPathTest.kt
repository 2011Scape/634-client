package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ShortestPathArea
import com.jagex.game.runetek5.client.features.ShortestPathFinder
import com.jagex.game.runetek5.client.features.ShortestPathFinder.Companion.pack
import com.jagex.game.runetek5.client.features.ShortestPathGrid
import com.jagex.game.runetek5.client.features.ShortestPathGzip
import com.jagex.game.runetek5.client.features.ShortestPathPlayer
import com.jagex.game.runetek5.client.features.ShortestPathRules
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

	private fun search(
		grid: ShortestPathGrid,
		start: Int,
		target: Int,
		transports: List<ShortestPathTransport> = emptyList(),
		anywhere: List<ShortestPathTransport> = emptyList(),
		targets: IntArray = intArrayOf(target),
		setup: (ShortestPathFinder.Options) -> Unit = {},
	): ShortestPathFinder {
		val options = ShortestPathFinder.Options()
		options.transports = transports.groupBy { pack(it.srcX, it.srcY, it.srcLevel) }
		options.anywhere = anywhere
		options.avoidWilderness = false
		setup(options)
		val finder = ShortestPathFinder(grid, start, targets, options)
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
		assertTrue(ShortestPathGrid(regions).canStep(5, 5, 0, 1, 1))
		close(5, 5, 0, 1) // no step east from (5, 5)
		val grid = ShortestPathGrid(regions) // the grid copies the planes it is made of
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

	private fun player(magic: Int, items: Map<Int, Int>, worn: Set<Int> = emptySet(), book: String? = "modern", vars: Map<Int, Int> = emptyMap()): ShortestPathPlayer =
		ShortestPathPlayer(IntArray(25) { if (it == 6) magic else 1 }, items, worn, book) { _, id -> vars[id] ?: 0 }

	private val varrockTeleport: ShortestPathTransport = ShortestPathTransport.parse(
		"TELEPORT_SPELL\t\t3212 3428 0\t5\tVarrock Teleport\tmagic=25;book=modern;items=554x1,556x3,563x1\n",
	)[0]

	@Test
	fun aSpellNeedsTheLevelTheBookAndTheRunes() {
		val runes: Map<Int, Int> = mapOf(554 to 1, 556 to 3, 563 to 1)
		assertTrue(ShortestPathRules(player(25, runes), emptyMap(), true, 100_000).meets(varrockTeleport))
		assertFalse(ShortestPathRules(player(24, runes), emptyMap(), true, 100_000).meets(varrockTeleport), "magic 24")
		assertFalse(ShortestPathRules(player(25, runes, book = "ancient"), emptyMap(), true, 100_000).meets(varrockTeleport), "ancient book")
		assertFalse(ShortestPathRules(player(25, mapOf(554 to 1, 563 to 1)), emptyMap(), true, 100_000).meets(varrockTeleport), "no air runes")
		// a staff of air worn stands in for the air runes
		val staves: Map<Int, Set<Int>> = mapOf(556 to setOf(1381))
		assertTrue(ShortestPathRules(player(25, mapOf(554 to 1, 563 to 1, 1381 to 1), setOf(1381)), staves, true, 100_000).meets(varrockTeleport))
	}

	@Test
	fun itemsShipsAndQuestVars() {
		val table = ShortestPathTransport.parse(
			"TELEPORT_ITEM\t\t3087 3496 0\t5\tAmulet of glory: Edgeville\titems=1706|1708|1710|1712;equipped\n" +
				"SHIP\t2760 3239 0\t2792 3417 1\t10\tCharter ship to Catherby\titems=995x480\n" +
				"SHIP\t2760 3239 0\t3702 3487 1\t10\tCharter ship to Port Phasmatys\titems=995x2900;var=varp:302:60|61\n" +
				"FAIRY_RING\t2996 3114 0\t2700 3247 0\t5\tFairy ring AIR\tvar=varbit:2326:!0\n",
		)
		val glory = table[0]
		assertTrue(ShortestPathRules(player(1, mapOf(1710 to 1), setOf(1710)), emptyMap(), true, 100_000).meets(glory))
		assertFalse(ShortestPathRules(player(1, emptyMap()), emptyMap(), true, 100_000).meets(glory))
		assertTrue(ShortestPathRules(player(1, emptyMap()), emptyMap(), false, 100_000).meets(glory), "all teleport items taken as carried")
		val ship = table[1]
		assertTrue(ShortestPathRules(player(1, mapOf(995 to 1000)), emptyMap(), true, 100_000).meets(ship))
		assertFalse(ShortestPathRules(player(1, mapOf(995 to 100)), emptyMap(), true, 100_000).meets(ship), "too few coins")
		assertFalse(ShortestPathRules(player(1, mapOf(995 to 1000)), emptyMap(), true, 400).meets(ship), "over the currency threshold")
		// Priest in Peril done (varp 302 at 60 or 61) for Port Phasmatys, Fairy Tale II started (varbit 2326 not 0) for the rings
		val coins: Map<Int, Int> = mapOf(995 to 5000)
		assertFalse(ShortestPathRules(player(1, coins), emptyMap(), true, 100_000).meets(table[2]), "Priest in Peril not done")
		assertTrue(ShortestPathRules(player(1, coins, vars = mapOf(302 to 61)), emptyMap(), true, 100_000).meets(table[2]))
		assertFalse(ShortestPathRules(player(1, emptyMap()), emptyMap(), true, 100_000).meets(table[3]), "Fairy Tale II unstarted")
		assertTrue(ShortestPathRules(player(1, emptyMap(), vars = mapOf(2326 to 10)), emptyMap(), true, 100_000).meets(table[3]))
	}

	/** With the runes and the level, Varrock from Lumbridge is a teleport and a short walk, not the road. */
	@Test
	fun theShippedTableTeleportsWhenItCan() {
		val grid = ShortestPathGrid.decode(lang.readResource("/shortest-path/collision.gz")!!)
		val table = ShortestPathTransport.decode(lang.readResource("/shortest-path/transports.tsv.gz")!!)
		val rules = ShortestPathRules(player(25, mapOf(554 to 1, 556 to 3, 563 to 1)), ShortestPathRules.staves(table), true, 100_000)
		val spells = table.filter { it.anywhere && it.type == "TELEPORT_SPELL" && rules.meets(it) }
		assertTrue(spells.any { it.label == "Varrock Teleport" })
		assertFalse(spells.any { it.label == "Falador Teleport" }, "Falador needs magic 37")
		val walk = search(grid, pack(3222, 3218, 0), pack(3212, 3440, 0), anywhere = spells)
		assertTrue(walk.reached)
		assertEquals("Varrock Teleport", walk.pathTransports[1]?.label)
		assertTrue(walk.path.size < 40, "teleported, not walked: ${walk.path.size}")
	}

	@Test
	fun theNearestOfSeveralTargetsIsTaken() {
		val finder = search(ShortestPathGrid(regions), pack(10, 10, 0), -1, targets = intArrayOf(pack(50, 50, 0), pack(14, 12, 0)))
		assertTrue(finder.reached)
		assertEquals(pack(14, 12, 0), finder.path.last())
	}

	@Test
	fun theWildernessIsAvoidedWhenAsked() {
		// region (47, 55): x 3008..3071, y 3520..3583; the wilderness starts at y 3523
		val wild: Array<Array<ByteArray?>?> = arrayOfNulls<Array<ByteArray?>?>(65536).also { it[(47 shl 8) or 55] = arrayOf(open(), null, null, null) }
		val bits: ByteArray = wild[(47 shl 8) or 55]!![0]!!
		for (y in 0 until 3) {
			// no step east from x 3030 on the rows south of the ditch
			val i: Int = (y * 64 + (3030 - 3008)) * 2 + 1
			bits[i shr 3] = (bits[i shr 3].toInt() and (1 shl (i and 7)).inv()).toByte()
		}
		val grid = ShortestPathGrid(wild)
		val start: Int = pack(3010, 3520, 0)
		val target: Int = pack(3060, 3520, 0)
		val area = ShortestPathArea(intArrayOf(3000, 3100, 3100, 3000), intArrayOf(3523, 3523, 3600, 3600), 0, 3)
		assertTrue(area.contains(3010, 3523, 0))
		assertFalse(area.contains(3010, 3520, 0))
		assertTrue(search(grid, start, target) { it.wilderness = area }.reached, "through the wilderness")
		assertFalse(
			search(grid, start, target) {
				it.wilderness = area
				it.avoidWilderness = true
			}.reached,
			"kept out of the wilderness",
		)
	}

	@Test
	fun aTeleportKeptInTheBankIsFetchedOnTheWay() {
		val tablet = ShortestPathTransport("TELEPORT_ITEM", -1, -1, -1, 60, 60, 1, 5, "Varrock teleport", mapOf("items" to "8007x1", "consumed" to ""))
		val finder = search(ShortestPathGrid(regions), pack(2, 2, 0), pack(61, 61, 1)) {
			it.banks = setOf(pack(6, 2, 0))
			it.bankAnywhere = listOf(tablet)
		}
		assertTrue(finder.reached)
		val bank: Int = finder.pathTransports.indexOfFirst { it === ShortestPathFinder.BANK }
		assertTrue(bank > 0, "the path visits the bank")
		assertEquals(pack(6, 2, 0), finder.path[bank])
		assertEquals("Varrock teleport", finder.pathTransports[bank + 1]?.label)
	}

	@Test
	fun wornGroupsPermanentItemsAndBlockedItems() {
		val grapple = ShortestPathTransport.parse("GRAPPLE_SHORTCUT\t1 1 0\t9 9 0\t10\tGrapple Wall\tagility=11;equipped=9419,9174|9177\n")[0]
		assertFalse(ShortestPathRules(player(1, emptyMap(), setOf(9419)), emptyMap(), true, 100_000).meets(grapple), "no crossbow")
		assertTrue(ShortestPathRules(player(1, emptyMap(), setOf(9419, 9177)).also { it.levels[16] = 11 }, emptyMap(), true, 100_000).meets(grapple))
		val tablet = ShortestPathTransport.parse("TELEPORT_ITEM\t\t3212 3428 0\t5\tVarrock teleport\titems=8007x1;consumed\n")[0]
		val carried: Map<Int, Int> = mapOf(8007 to 1)
		assertTrue(ShortestPathRules(player(1, carried), emptyMap(), true, 100_000).meets(tablet))
		assertFalse(ShortestPathRules(player(1, carried), emptyMap(), true, 100_000, permanentOnly = true).meets(tablet), "a tablet is used up")
		assertFalse(ShortestPathRules(player(1, carried), emptyMap(), true, 100_000, blocked = setOf(8007)).meets(tablet), "blocked")
		assertFalse(ShortestPathRules(player(1, emptyMap()), emptyMap(), true, 100_000).meets(tablet, true))
		assertTrue(ShortestPathRules(player(1, emptyMap()), emptyMap(), true, 100_000, bank = carried).meets(tablet, true), "in the bank")
	}

	/** The shipped map keeps a guarded door shut: the Crafting Guild (south of its door) lets in Crafting 40 with a brown apron worn, as Void does. */
	@Test
	fun theShippedMapKeepsGuardedDoorsToTheirRules() {
		val grid = ShortestPathGrid.decode(lang.readResource("/shortest-path/collision.gz")!!)
		val table = ShortestPathTransport.decode(lang.readResource("/shortest-path/transports.tsv.gz")!!).filter { it.type == "OBJECT" }
		val outside: Int = pack(2933, 3292, 0)
		val inside: Int = pack(2933, 3285, 0)
		fun way(crafting: Int, worn: Set<Int>): ShortestPathFinder {
			val player = ShortestPathPlayer(IntArray(25) { if (it == 12) crafting else 1 }, emptyMap(), worn, "modern") { _, _ -> 0 }
			val rules = ShortestPathRules(player, emptyMap(), true, 100_000)
			return search(grid, outside, inside, table.filter { rules.meets(it) }) { it.maxNodes = 200_000 }
		}
		assertFalse(way(1, emptySet()).reached, "Crafting 1")
		assertFalse(way(40, emptySet()).reached, "no apron")
		assertTrue(way(40, setOf(1757)).reached, "Crafting 40 and the brown apron")
	}

	/** The review's zig-zag: a path over open ground goes straight first, then diagonal, never back and forth. */
	@Test
	fun aPathOverOpenGroundDoesNotZigZag() {
		val finder = search(ShortestPathGrid(regions), pack(2, 2, 0), pack(30, 12, 0))
		val ys: List<Int> = finder.path.map { ShortestPathFinder.y(it) }
		for (i in 1 until ys.size) {
			assertTrue(ys[i] >= ys[i - 1], "y never goes back: $ys")
		}
		var turns = 0
		for (i in 2 until finder.path.size) {
			val a: Int = finder.path[i - 2]
			val b: Int = finder.path[i - 1]
			val c: Int = finder.path[i]
			if (ShortestPathFinder.x(b) - ShortestPathFinder.x(a) != ShortestPathFinder.x(c) - ShortestPathFinder.x(b) ||
				ShortestPathFinder.y(b) - ShortestPathFinder.y(a) != ShortestPathFinder.y(c) - ShortestPathFinder.y(b)
			) {
				turns++
			}
		}
		assertTrue(turns <= 1, "one turn, from straight to diagonal: $turns")
	}

	/** The review's mark-at-push: the cheaper of two transports to one tile is taken, whichever is listed first. */
	@Test
	fun theCheaperOfTwoTransportsToOneTileIsTaken() {
		val dear = ShortestPathTransport("TELEPORT_SPELL", -1, -1, -1, 50, 50, 0, 300, "Varrock Teleport", emptyMap())
		val cheap = ShortestPathTransport("TELEPORT_ITEM", -1, -1, -1, 50, 50, 0, 5, "Varrock teleport", emptyMap())
		val finder = search(ShortestPathGrid(regions), pack(2, 2, 0), pack(51, 51, 0), anywhere = listOf(dear, cheap))
		assertTrue(finder.reached)
		assertEquals("Varrock teleport", finder.pathTransports[1]?.label)
	}

	/** A walk that gets to a tile sooner than a dear transport's landing there keeps the tile. */
	@Test
	fun aWalkBeatsADearLanding() {
		val stairs = transport(2, 2, 0, 6, 2, 0, ticks = 50)
		val finder = search(ShortestPathGrid(regions), pack(2, 2, 0), pack(7, 2, 0), listOf(stairs))
		assertTrue(finder.reached)
		assertTrue(finder.pathTransports.all { it == null }, "walked, not by the transport")
		assertEquals(6, finder.path.size)
	}

	/** Version 3: each step out of a tile on its own, so a step can be open one way and shut the other. */
	@Test
	fun aOneWayStepIsOneWay() {
		val tiles = ByteArray(4096) { -1 } // every step open
		// from (5, 6) the step south (bit 2) is shut, though the step north from (5, 5) is open
		tiles[6 * 64 + 5] = (0xFF and (1 shl 2).inv()).toByte()
		val grid: ShortestPathGrid = ShortestPathGrid.decode(collisionFile(version = 3, planes = listOf(tiles)))
		assertTrue(grid.canStep(5, 5, 0, 0, 1))
		assertFalse(grid.canStep(5, 6, 0, 0, -1))
		assertTrue(search(grid, pack(5, 5, 0), pack(5, 6, 0)).path.size == 2, "north in one step")
		assertTrue(search(grid, pack(5, 6, 0), pack(5, 5, 0)).path.size > 2, "south goes round")
	}

	/** A gzip file with a name in its header (the gzip tool's) reads as well as Java's. */
	@Test
	fun aGzipWithAFileNameReads() {
		val body: ByteArray = "SPCM file".encodeToByteArray()
		val deflater = java.util.zip.Deflater(9, true)
		deflater.setInput(body)
		deflater.finish()
		val packed = ByteArray(256)
		val n: Int = deflater.deflate(packed)
		val crc = java.util.zip.CRC32().also { it.update(body) }.value
		val out = java.io.ByteArrayOutputStream()
		out.write(byteArrayOf(0x1F, 0x8B.toByte(), 8, 8, 0, 0, 0, 0, 0, 3))
		out.write("collision.bin".encodeToByteArray())
		out.write(0)
		out.write(packed, 0, n)
		for (v in longArrayOf(crc, body.size.toLong())) {
			for (b in 0 until 4) out.write(((v shr (8 * b)) and 0xFF).toInt())
		}
		assertEquals("SPCM file", ShortestPathGzip.gunzip(out.toByteArray()).decodeToString())
	}

	@Test
	fun aSearchStopsAtItsLimit() {
		for (y in 0 until 64) {
			close(30, y, 0, 1) // the target is walled off: the search would visit the whole region
		}
		val finder = search(ShortestPathGrid(regions), pack(2, 2, 0), pack(40, 2, 0)) { it.maxNodes = 100 }
		assertTrue(finder.done)
		assertFalse(finder.reached)
		assertTrue(finder.visitedCount in 100..110, "stopped near the limit: ${finder.visitedCount}")
	}

	/** A transport usable only with the bank's items, from a tile (not a teleport): the path goes to the bank first. */
	@Test
	fun aTransportNeedingTheBanksItemsGoesByTheBank() {
		// level 1 reached only by a ladder whose key is in the bank
		val ladder = transport(20, 20, 0, 20, 21, 1)
		val finder = search(ShortestPathGrid(regions), pack(2, 2, 0), pack(30, 30, 1)) {
			it.banks = setOf(pack(4, 2, 0))
			it.bankTransports = mapOf(pack(20, 20, 0) to listOf(ladder))
		}
		assertTrue(finder.reached)
		val bank: Int = finder.pathTransports.indexOfFirst { it === ShortestPathFinder.BANK }
		val climb: Int = finder.pathTransports.indexOfFirst { it === ladder }
		assertTrue(bank in 1 until climb, "the bank, then the ladder: $bank, $climb")
	}

	/** Void's wilderness from the shipped table: its notch south of the ditch and the dungeons are not in it. */
	@Test
	fun theShippedWildernessIsVoids() {
		val table = ShortestPathTransport.decode(lang.readResource("/shortest-path/transports.tsv.gz")!!)
		val area: ShortestPathArea = ShortestPathArea.of(table.first { it.type == "AREA" && it.label == "wilderness" })!!
		assertTrue(area.contains(3100, 3600, 0), "north of Edgeville")
		assertFalse(area.contains(3100, 3500, 0), "Edgeville")
		assertFalse(area.contains(3020, 3530, 0), "the notch by the Black Knights' fortress")
		assertFalse(area.contains(3100, 9900, 0), "the dungeon under Edgeville")
	}

	@Test
	fun blockingOneChargeOfAGloryKeepsTheOthers() {
		val glory = ShortestPathTransport.parse("TELEPORT_ITEM\t\t3087 3496 0\t5\tAmulet of glory: Edgeville\titems=1706|1708|1710|1712;charged\n")[0]
		val carried: Map<Int, Int> = mapOf(1712 to 1)
		assertTrue(ShortestPathRules(player(1, carried), emptyMap(), true, 100_000, blocked = setOf(1706)).meets(glory))
		assertFalse(ShortestPathRules(player(1, carried), emptyMap(), true, 100_000, blocked = setOf(1706, 1708, 1710, 1712)).meets(glory))
	}

	@Test
	fun aStaffInTheBankStandsInForItsRunesAfterABankVisit() {
		val staves: Map<Int, Set<Int>> = mapOf(556 to setOf(1381))
		val runes: Map<Int, Int> = mapOf(554 to 1, 563 to 1)
		val rules = ShortestPathRules(player(25, runes), staves, true, 100_000, bank = mapOf(1381 to 1))
		assertFalse(rules.meets(varrockTeleport), "no air runes, no staff worn")
		assertTrue(rules.meets(varrockTeleport, true), "the staff of air taken out of the bank")
	}

	/** A collision.gz of [planes] for region 0, level 0 up. */
	private fun collisionFile(version: Int, planes: List<ByteArray>): ByteArray {
		val body = java.io.ByteArrayOutputStream()
		val data = java.io.DataOutputStream(body)
		data.writeBytes("SPCM")
		data.writeInt(version)
		data.writeInt(1)
		data.writeShort(0)
		data.writeByte((1 shl planes.size) - 1)
		planes.forEach { data.write(it) }
		val out = java.io.ByteArrayOutputStream()
		java.util.zip.GZIPOutputStream(out).use { it.write(body.toByteArray()) }
		return out.toByteArray()
	}
}
