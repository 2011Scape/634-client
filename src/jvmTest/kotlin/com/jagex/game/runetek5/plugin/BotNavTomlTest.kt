package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.BotNavAreaState
import com.jagex.game.runetek5.client.features.BotNavEdgeState
import com.jagex.game.runetek5.client.features.BotNavEditor
import com.jagex.game.runetek5.client.features.BotNavEdits
import com.jagex.game.runetek5.client.features.BotNavFile
import com.jagex.game.runetek5.client.features.BotNavGraph
import com.jagex.game.runetek5.client.features.BotNavOverlay
import com.jagex.game.runetek5.client.features.BotNavPanel
import com.jagex.game.runetek5.client.features.BotNavTile
import com.jagex.game.runetek5.client.features.BotNavToml
import com.jagex.game.runetek5.client.features.ShortestPathGzip
import com.jagex.game.runetek5.client.features.TomlArray
import com.jagex.game.runetek5.client.features.TomlException
import com.jagex.game.runetek5.client.features.TomlTable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Not part of the original client: the Bot Nav Editor's TOML, read from and written back to Void's files (BotNavToml, BotNavFile). */
class BotNavTomlTest {

	private val edges: String = """
		edges = [
		    { from = { x = 3236, y = 3218 }, to = { x = 3236, y = 3205 } }, # gate_to_village
		    # the stairs
		    { from = { x = 3205, y = 3209 }, to = { x = 3205, y = 3209, level = 1 }, cost = 1, actions = [{ object = { option = "Climb-up", id = "lumbridge_staircase", x = 3204, y = 3207, success = { tile = { level = 1 } } } }] },
		    { from = { x = 3017, y = 3258 }, to = { x = 3016, y = 3258 },
		        actions = [
		            { object = {
		                option = "Open",
		                id = "door_668_closed",
		                success = { object = { id = "door_668_opened", x = 3016, y = 3259 } }
		            } },
		            { tile = { x = 3016, y = 3258, radius = 1 } }
		        ]
		    },
		    { from = { x = 1, y = 2 }, to = { x = 3, y = 4 } }
		]
	""".trimIndent() + "\n"

	private val areas: String = """
		# Lumbridge
		[lumbridge_sheep_pen]
		x = [3193, 3212]
		y = [3260, 3276]
		level = 0
		bot_capacity = 2

		[lumbridge]
		x = [3252, 3136, 3136, 3153]
		y = [3136, 3136, 3336, 3336]
		tags = ["city"]
	""".trimIndent() + "\n"

	@Test
	fun everyBundledFileReadsAndWritesBackUnchanged() {
		val bundle: String = ShortestPathGzip.gunzip(assertNotNull(lang.readResource("/bot-nav/nav.txt.gz"))).decodeToString()
		val files = BotNavGraph.splitBundle(bundle)
		assertTrue(files.size > 100)
		var edgeCount = 0
		for ((path, text) in files) {
			val file = BotNavFile(path, text)
			assertEquals(text, file.render(), path)
			edgeCount += file.edges.size
		}
		assertTrue(edgeCount > 1000, "edges: $edgeCount")
	}

	@Test
	fun edgesAndTheirCommentsAreRead() {
		val file = BotNavFile("bot/test.nav-edges.toml", edges)
		assertEquals(4, file.edges.size)
		val walk: BotNavEdgeState = file.edges[0].state
		assertTrue(walk.isWalk)
		assertEquals("gate_to_village", walk.comment)
		assertEquals(13, walk.weight())
		val stairs: BotNavEdgeState = file.edges[1].state
		assertEquals(1, BotNavTile.level(stairs.to))
		assertEquals(1, stairs.weight())
		assertEquals(2, file.edges[2].state.actions!!.items.size)
	}

	@Test
	fun aChangedEdgeRewritesOnlyItsOwnLines() {
		val graph = BotNavGraph()
		val file = BotNavFile("bot/test.nav-edges.toml", edges)
		graph.files.add(file)
		graph.changeEdge(file.edges[2], file.edges[2].state.with(cost = 7))
		val lines: List<String> = file.render().lines()
		assertEquals(edges.lines().take(4), lines.take(4))
		assertTrue(lines[4].startsWith("    { from = { x = 3017, y = 3258 }, to = { x = 3016, y = 3258 }, cost = 7, actions = [{ object = "), lines[4])
		assertEquals("    { from = { x = 1, y = 2 }, to = { x = 3, y = 4 } }", lines[5])
		assertTrue(graph.undo())
		assertEquals(edges, file.render())
	}

	@Test
	fun anAddedEdgeGoesBeforeTheBracketAndTheLastEdgeGetsAComma() {
		val graph = BotNavGraph()
		val file = BotNavFile("bot/test.nav-edges.toml", edges)
		graph.files.add(file)
		graph.addEdge(file, BotNavEdgeState(BotNavTile.pack(5, 6, 0), BotNavTile.pack(7, 8, 0), 0, null, null, "new_one", LinkedHashMap()))
		val text: String = file.render()
		assertTrue(text.endsWith("    { from = { x = 1, y = 2 }, to = { x = 3, y = 4 } },\n    { from = { x = 5, y = 6 }, to = { x = 7, y = 8 } }, # new_one\n]\n"), text)
		BotNavToml.parseEdges(text)
	}

	@Test
	fun aRemovedEdgeTakesItsLineAndComment() {
		val graph = BotNavGraph()
		val file = BotNavFile("bot/test.nav-edges.toml", edges)
		graph.files.add(file)
		graph.removeEdge(file.edges[0])
		val text: String = file.render()
		assertFalse(text.contains("gate_to_village"))
		assertTrue(text.contains("# the stairs"))
		assertEquals(3, BotNavToml.parseEdges(text).edges.size)
	}

	@Test
	fun movingANodeMovesEveryEdgeEndOnIt() {
		val graph = BotNavGraph()
		val file = BotNavFile("bot/test.nav-edges.toml", edges)
		graph.files.add(file)
		val stairs: Int = BotNavTile.pack(3205, 3209, 0)
		val moved: Int = BotNavTile.pack(3206, 3209, 0)
		graph.moveNode(stairs, moved)
		assertEquals(moved, file.edges[1].state.from)
		assertTrue(graph.isNode(moved))
		assertFalse(graph.isNode(stairs))
		graph.undo()
		assertEquals(stairs, file.edges[1].state.from)
		graph.redo()
		graph.removeNode(moved)
		assertEquals(3, graph.edges().count())
	}

	@Test
	fun areasReadAndChangeOneAtATime() {
		val graph = BotNavGraph()
		val file = BotNavFile("area/test.areas.toml", areas)
		graph.files.add(file)
		assertEquals(areas, file.render())
		val pen: BotNavAreaState = file.areas[0].state
		assertTrue(pen.isRectangle())
		assertTrue(pen.contains(3200, 3270, 0))
		assertFalse(pen.contains(3200, 3270, 1))
		assertEquals(-1, file.areas[1].state.level())
		assertTrue(file.areas[1].state.contains(3200, 3200, 2))
		assertEquals("lumbridge_sheep_pen", graph.areaAt(3200, 3270, 0)!!.state.name)
		graph.changeArea(file.areas[0], pen.withPoints(intArrayOf(3190, 3212), intArrayOf(3260, 3276)))
		val text: String = file.render()
		assertTrue(text.startsWith("# Lumbridge\n[lumbridge_sheep_pen]\nx = [3190, 3212]\ny = [3260, 3276]\nlevel = 0\nbot_capacity = 2\n\n[lumbridge]\n"), text)
		graph.addArea(file, BotNavAreaState("new_pen", TomlTable(LinkedHashMap())).withPoints(intArrayOf(1, 2), intArrayOf(3, 4)))
		assertTrue(file.render().endsWith("tags = [\"city\"]\n\n[new_pen]\nx = [1, 2]\ny = [3, 4]\n"), file.render())
		assertEquals(3, BotNavToml.parseAreas(file.render()).size)
	}

	@Test
	fun templatesAreWrittenAsVoidsFilesWriteThem() {
		val from: Int = BotNavTile.pack(3205, 3209, 0)
		val up: Int = BotNavTile.pack(3205, 3209, 1)
		assertEquals("[{ object = { option = \"Climb-up\", id = \"\", x = 3205, y = 3209, success = { tile = { level = 1 } } } }]", BotNavEdits.template("stairs", from, up).format())
		assertEquals("[{ tile = { x = 3205, y = 3209, level = 1 } }]", BotNavEdits.template("walk", from, up).format())
		assertEquals(BotNavTile.pack(3197, 3276, 2), BotNavPanel.tile("(3197, 3276, 2)"))
		assertEquals(null, BotNavPanel.tile("3197"))
	}

	@Test
	fun theOpacityFadesColoursButNeverToAlphaNought() {
		val was: Int = BotNavEditor.opacity
		try {
			BotNavEditor.opacity = 100
			assertEquals(0xFF4CB8FF.toInt(), BotNavOverlay.fade(0xFF4CB8FF.toInt()))
			BotNavEditor.opacity = 40
			assertEquals(0x664CB8FF, BotNavOverlay.fade(0xFF4CB8FF.toInt()))
			BotNavEditor.opacity = 10
			assertEquals(0x01FFFFFF, BotNavOverlay.fade(0x05FFFFFF), "an alpha of 0 would draw solid")
		} finally {
			BotNavEditor.opacity = was
		}
	}

	@Test
	fun thePanelBackgroundTakesTheOverlaysColourAtItsOwnOpacity() {
		val was: Int = BotNavEditor.panelOpacity
		try {
			BotNavEditor.panelOpacity = 100
			assertEquals(0xFF463D32.toInt(), BotNavPanel.background(0x9C463D32.toInt()))
			BotNavEditor.panelOpacity = 0
			assertEquals(0x00463D32, BotNavPanel.background(0x9C463D32.toInt()))
		} finally {
			BotNavEditor.panelOpacity = was
		}
	}

	@Test
	fun onlyPathsUnderVoidsDataFolderAreTaken() {
		assertTrue(BotNavEditor.isDataPath("bot/lumbridge.nav-edges.toml"))
		assertTrue(BotNavEditor.isDataPath("area/misthalin/lumbridge/lumbridge.areas.toml"))
		for (bad in listOf("../x.nav-edges.toml", "bot/../../x.nav-edges.toml", "/etc/x.areas.toml", "bot//x.nav-edges.toml", "C:/x.areas.toml", "bot\\x.areas.toml", "bot/x.toml")) {
			assertFalse(BotNavEditor.isDataPath(bad), bad)
		}
	}

	@Test
	fun movingAnAreaPointLeavesTheOldStateForUndo() {
		val graph = BotNavGraph()
		val file = BotNavFile("area/test.areas.toml", areas)
		graph.files.add(file)
		val polygon = file.areas[1]
		BotNavEditor.graph = graph
		try {
			graph.changeArea(polygon, polygon.state.withPoints(polygon.state.xs().copyOf().also { it[0] = 3250 }, polygon.state.ys()))
			assertEquals(3250, polygon.state.xs()[0])
			graph.undo()
			assertEquals(3252, polygon.state.xs()[0], "the old state's points are untouched")
		} finally {
			BotNavEditor.graph = null
		}
	}

	@Test
	fun badTomlSaysWhere() {
		val error = assertFailsWith<TomlException> { BotNavToml.parseValue("[{ object = { id = \"x\" }") }
		assertTrue(error.message!!.contains("line 1"), error.message)
		val actions = BotNavToml.parseValue("[{ tile = { x = 1, y = 2 } }]") as TomlArray
		assertEquals("[{ tile = { x = 1, y = 2 } }]", actions.format())
	}
}
