package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Companions
import com.jagex.game.runetek5.client.features.CompanionsLoadout
import com.jagex.game.runetek5.plugin.api.OverlayLayer
import com.jagex.game.runetek5.plugin.api.OverlayMenuEntry
import com.jagex.game.runetek5.plugin.api.Task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: the Companions plugin's reading of the server's variables and chat lines. */
class CompanionsTest {

	/** Void's OwnedBotVarcs.base37. */
	private fun base37(name: String): Pair<Int, Int> {
		var value = 0L
		for (char in name.lowercase().take(12)) {
			value *= 37
			value += when (char) {
				in 'a'..'z' -> char - 'a' + 1
				in '0'..'9' -> char - '0' + 27
				else -> 0
			}
		}
		return (value ushr 32).toInt() to value.toInt()
	}

	private fun pack(level: Int, x: Int, y: Int): Int = level shl 28 or (x shl 14) or y

	private fun vars(): HashMap<Int, Int> {
		val map = HashMap<Int, Int>()
		// version 1, slots 0 and 1 alive, slot 0 online
		map[Companions.BASE] = (1 shl 12) or (0b0011 shl 4) or 0b0001
		val base = Companions.BASE + 1
		val (hi, lo) = base37("Woody")
		map[base + Companions.NAME_HI] = hi
		map[base + Companions.NAME_LO] = lo
		map[base + Companions.HP] = 45 shl 16 or 99
		map[base + Companions.PRAYER] = 20 shl 16 or 43
		map[base + Companions.STATUS] = (1 shl 24) or (32 shl 8) or 2
		map[base + Companions.TASK] = (1 shl 12) or (1 shl 8) or 7
		map[base + Companions.TILE] = pack(0, 3222, 3218)
		map[base + Companions.ENERGY] = (1215 shl 8) or 80
		map[base + Companions.EQUIP + 3] = (1205 shl 16) or 1
		map[base + Companions.INV + 0] = (385 shl 16) or 12
		val second = Companions.BASE + 1 + Companions.FIELDS
		val (hi2, lo2) = base37("Minnie")
		map[second + Companions.NAME_HI] = hi2
		map[second + Companions.NAME_LO] = lo2
		map[second + Companions.TILE] = -1
		return map
	}

	@Test
	fun theRosterIsReadFromTheVariables() {
		val state = Companions.read { vars()[it] ?: 0 }
		assertTrue(state.active())
		assertEquals(4, state.bots.size)
		val woody = state.bot(0)!!
		assertEquals("Woody", woody.name)
		assertTrue(woody.alive)
		assertEquals(80, woody.run)
		assertEquals(1215, woody.spec, "the special attack weapon rides above the run energy")
		assertTrue(woody.online)
		assertEquals(45, woody.hp)
		assertEquals(99, woody.hpMax)
		assertEquals(20, woody.prayer)
		assertEquals(43, woody.prayerMax)
		assertEquals("Working", woody.statusName())
		assertEquals(32, woody.spell, "the autocast spell's component rides above the status")
		assertEquals(193, woody.spellBook, "and the book it is in, the ancient one here, above that")
		assertEquals(7, woody.task)
		assertEquals(1, woody.profile)
		assertTrue(woody.aggressive)
		assertEquals(3222, woody.x)
		assertEquals(3218, woody.y)
		assertEquals(80, woody.run)
		assertEquals(1205, Companions.itemId(woody.equipment[3]))
		assertEquals(12, Companions.itemAmount(woody.inventory[0]))
		assertEquals(-1, Companions.itemId(woody.inventory[1]))
		val minnie = state.bot(1)!!
		assertEquals("Minnie", minnie.name)
		assertTrue(minnie.alive)
		assertFalse(minnie.online)
		assertEquals("Away", minnie.statusName())
		assertEquals(-1, minnie.x)
		val empty = state.bot(2)!!
		assertFalse(empty.alive)
		assertEquals("", empty.name)
	}

	@Test
	fun unsetVariablesReadAsEmpty() {
		// a short name's high half and empty item slots are never sent: the client holds -1 for them
		val state = Companions.read { vars()[it] ?: -1 }
		val woody = state.bot(0)!!
		assertEquals("Woody", woody.name)
		assertEquals(-1, Companions.itemId(woody.inventory[1]))
		assertEquals(0, Companions.itemAmount(woody.inventory[1]))
		assertEquals(-1, Companions.itemId(woody.equipment[0]))
		assertFalse(Companions.read { -1 }.active())
	}

	@Test
	fun noAnswerFromTheServerIsNotActive() {
		val state = Companions.read { 0 }
		assertFalse(state.active())
		assertTrue(state.bots.isEmpty())
	}

	@Test
	fun taskLinesAreParsedAndTheHeadingIsNot() {
		val task = Companions.parseTask("[BotTasks] 3|crafting|1|Seers' Village|1|Pick flax")!!
		assertEquals(3, task.index)
		assertEquals("3", task.id, "given back by its index")
		assertEquals("crafting", task.skill)
		assertEquals(1, task.level)
		assertEquals("Seers' Village", task.location)
		assertTrue(task.members)
		assertEquals("Pick flax", task.label)
		assertNull(Companions.parseTask("[BotTasks] 32 tasks: ::bot task <slot> <task>"))
		assertNull(Companions.parseTask("Welcome to RuneScape."))
		// the picker's tabs and rows
		Companions.tasks.clear()
		Companions.tasks.add(Companions.parseTask("[BotTasks] 0|woodcutting|1|Draynor|0|Chop logs")!!)
		Companions.tasks.add(Companions.parseTask("[BotTasks] 1|woodcutting|15|Draynor|0|Chop oak logs")!!)
		Companions.tasks.add(Companions.parseTask("[BotTasks] 2|woodcutting|60|Varrock|0|Chop yew logs")!!)
		Companions.tasks.add(task)
		assertEquals(listOf("woodcutting", "crafting"), Companions.taskSkills())
		assertEquals("Woodcutting", Companions.skillName("woodcutting"))
		val rows = Companions.taskRows("woodcutting")
		assertEquals(listOf("Draynor", "Chop logs", "Chop oak logs", "Varrock", "Chop yew logs"), rows.map { if (it is Companions.TaskEntry) it.label else it })
		// what a task needs, named for the tip
		assertEquals(0 to "1351/1349/1353:1", Companions.parseTaskItems("[BotTaskItems] 0|1351/1349/1353:1"))
		assertNull(Companions.parseTaskItems("[BotTasks] 0|woodcutting|1|Draynor|0|Chop logs"))
		val names = mapOf(1351 to "Bronze hatchet", 1349 to "Iron hatchet", 1353 to "Steel hatchet", 385 to "Shark")
		assertEquals(listOf("at least Bronze hatchet", "16 x Shark"), Companions.needLines("1351/1349/1353:1,385:16") { names[it] })
		assertEquals(emptyList(), Companions.needLines("") { names[it] })
		Companions.tasks.clear()
	}

	@Test
	fun botLinesNameTheirSlot() {
		assertEquals(0, Companions.alertSlot("[Companion 1] Woody is ready."))
		assertEquals(3, Companions.alertSlot("[Companion 4] Smith has died. Its items have been returned to your bank."))
		assertEquals(-1, Companions.alertSlot("[Companion 5] nope"))
		assertEquals(-1, Companions.alertSlot("[BotTasks] 1|a|b"))
		assertTrue(Companions.isUrgent("[Companion 1] Woody is nearly dead and teleports home!"))
		assertTrue(Companions.isUrgent("[Companion 2] Minnie stopped: it needs level 30 Mining."))
		assertFalse(Companions.isUrgent("[Companion 1] Woody is ready."))
	}

	@Test
	fun theWorldTagIsTheNameAndSlot() {
		val state = Companions.read { vars()[it] ?: 0 }
		assertEquals("Woody [1]", Companions.tag(state.bot(0)!!))
		assertEquals("Minnie [2]", Companions.tag(state.bot(1)!!))
	}

	@Test
	fun presetLinesAreParsed() {
		val preset = Companions.parsePreset("[BotLoadouts] melee|iron_full_helm,bronze_sword")!!
		assertEquals("melee", preset.first)
		assertEquals("iron_full_helm,bronze_sword", preset.second)
		assertNull(Companions.parsePreset("[BotLoadouts] 2 presets"))
		assertNull(Companions.parsePreset("[BotTasks] 1|a|b"))
	}

	@Test
	fun presetEntriesSitUnderSubmenus() {
		val plain = OverlayMenuEntry("Close", "Loadout", Task {})
		assertEquals("", plain.submenu, "an entry is a plain one unless given a submenu")
		val load = OverlayMenuEntry("melee", "Woody", Task {}, Companions.LOAD_PRESET)
		val delete = OverlayMenuEntry("melee", "preset", Task {}, Companions.DELETE_PRESET)
		assertEquals("Load preset", load.submenu)
		assertEquals("Delete preset", delete.submenu)
		assertTrue(load.submenu != delete.submenu, "loading and deleting are separate submenus")
	}

	@Test
	fun worldOrderLabelsNameTheirBot() {
		Companions.state = Companions.read { vars()[it] ?: 0 }
		assertEquals("1", Companions.botOf("Send Woody here"))
		assertEquals("2", Companions.botOf("<col=ff9040>Minnie"))
		assertEquals("all", Companions.botOf("All"))
		assertEquals("all", Companions.botOf("Send All here"))
		assertEquals("", Companions.botOf("Walk here"))
		assertEquals("", Companions.botOf("Send Woody2 here"), "a name is matched whole")
		Companions.state = Companions.State(emptyList(), 0)
	}

	@Test
	fun giveEntryLabelsCarryTheirAmount() {
		assertEquals("", Companions.amountOf("Give to Woody"))
		assertEquals("5", Companions.amountOf("5"))
		assertEquals("all", Companions.amountOf("All (12)"))
		assertEquals("10", Companions.amountOf("Woody: 10"))
		assertEquals("all", Companions.amountOf("<col=ff9040>Woody: All (3)"))
	}

	@Test
	fun aLoadoutHotkeyOpensThatCompanionsLoadoutAndClosesItAgain() {
		Companions.state = Companions.read { vars()[it] ?: 0 }
		Companions.enabled = true
		CompanionsLoadout.close()
		try {
			Companions.loadoutKeys[0] = 5
			Companions.loadoutKeys[2] = 7
			assertFalse(Companions.onKey(6), "an unbound key goes to the game")
			assertTrue(Companions.onKey(5), "the key is taken")
			assertTrue(CompanionsLoadout.open)
			assertEquals(0, Companions.selected)
			assertTrue(Companions.onKey(5))
			assertFalse(CompanionsLoadout.open, "the same key again closes it")
			assertTrue(Companions.onKey(7), "an empty slot's key is swallowed too")
			assertFalse(CompanionsLoadout.open, "but opens nothing")
		} finally {
			Companions.loadoutKeys.fill(0)
			Companions.enabled = false
			CompanionsLoadout.close()
		}
	}

	@Test
	fun theFoldedRosterOffersEveryBotUnderItsOwnSubmenu() {
		Companions.state = Companions.read { vars()[it] ?: 0 }
		Companions.collapsed = true
		try {
			val entries: List<OverlayMenuEntry> = Companions.panel.menuEntries()
			val woody: List<String> = entries.filter { it.submenu == "1." }.map { it.option }
			assertTrue("Follow" in woody && "Guard" in woody && "Loadout" in woody, "the online bot's orders: $woody")
			assertEquals(listOf("Summon"), entries.filter { it.submenu == "2." }.map { it.option }, "the away bot can only be summoned")
			assertTrue(entries.none { it.submenu == "3." || it.submenu == "4." }, "empty slots have no submenu")
			assertTrue(entries.any { it.option == "Guard all" && it.submenu.isEmpty() }, "the orders for every bot stay at the top level")
		} finally {
			Companions.collapsed = false
			Companions.state = Companions.State(emptyList(), 0)
		}
	}

	@Test
	fun theWindowsDrawOverTheGamesInterfacesAndTheRosterUnderThem() {
		assertEquals(OverlayLayer.ABOVE_WIDGETS, CompanionsLoadout.overlay.layer, "the loadout window stays in front of the bank")
		assertEquals(OverlayLayer.ABOVE_WIDGETS, Companions.picker.layer)
		assertEquals(OverlayLayer.UNDER_WIDGETS, Companions.panel.layer, "the roster is a HUD panel")
	}

	@Test
	fun leftClickIsTheFirstOfTheFirstThreeOptions() {
		assertEquals("Eat", CompanionsLoadout.leftClickOption(arrayOf("Eat", null, null, null, "Drop")))
		assertEquals("Wield", CompanionsLoadout.leftClickOption(arrayOf(null, "Wield", null, null, "Drop")))
		// only options 4 and 5 (Drop): the game's left click is Use
		assertNull(CompanionsLoadout.leftClickOption(arrayOf(null, null, null, "Empty", "Drop")))
	}
}
