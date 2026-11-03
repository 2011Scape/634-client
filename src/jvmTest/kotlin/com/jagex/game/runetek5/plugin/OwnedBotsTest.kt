package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.OwnedBots
import com.jagex.game.runetek5.client.features.OwnedBotsLoadout
import com.jagex.game.runetek5.plugin.api.OverlayMenuEntry
import com.jagex.game.runetek5.plugin.api.Task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: the Owned Bots plugin's reading of the server's variables and chat lines. */
class OwnedBotsTest {

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
		map[OwnedBots.BASE] = (1 shl 12) or (0b0011 shl 4) or 0b0001
		val base = OwnedBots.BASE + 1
		val (hi, lo) = base37("Woody")
		map[base + OwnedBots.NAME_HI] = hi
		map[base + OwnedBots.NAME_LO] = lo
		map[base + OwnedBots.HP] = 45 shl 16 or 99
		map[base + OwnedBots.PRAYER] = 20 shl 16 or 43
		map[base + OwnedBots.STATUS] = 2
		map[base + OwnedBots.TASK] = (1 shl 12) or (1 shl 8) or 7
		map[base + OwnedBots.TILE] = pack(0, 3222, 3218)
		map[base + OwnedBots.ENERGY] = (1215 shl 8) or 80
		map[base + OwnedBots.EQUIP + 3] = (1205 shl 16) or 1
		map[base + OwnedBots.INV + 0] = (385 shl 16) or 12
		val second = OwnedBots.BASE + 1 + OwnedBots.FIELDS
		val (hi2, lo2) = base37("Minnie")
		map[second + OwnedBots.NAME_HI] = hi2
		map[second + OwnedBots.NAME_LO] = lo2
		map[second + OwnedBots.TILE] = -1
		return map
	}

	@Test
	fun theRosterIsReadFromTheVariables() {
		val state = OwnedBots.read { vars()[it] ?: 0 }
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
		assertEquals(7, woody.task)
		assertEquals(1, woody.profile)
		assertTrue(woody.aggressive)
		assertEquals(3222, woody.x)
		assertEquals(3218, woody.y)
		assertEquals(80, woody.run)
		assertEquals(1205, OwnedBots.itemId(woody.equipment[3]))
		assertEquals(12, OwnedBots.itemAmount(woody.inventory[0]))
		assertEquals(-1, OwnedBots.itemId(woody.inventory[1]))
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
		val state = OwnedBots.read { vars()[it] ?: -1 }
		val woody = state.bot(0)!!
		assertEquals("Woody", woody.name)
		assertEquals(-1, OwnedBots.itemId(woody.inventory[1]))
		assertEquals(0, OwnedBots.itemAmount(woody.inventory[1]))
		assertEquals(-1, OwnedBots.itemId(woody.equipment[0]))
		assertFalse(OwnedBots.read { -1 }.active())
	}

	@Test
	fun noAnswerFromTheServerIsNotActive() {
		val state = OwnedBots.read { 0 }
		assertFalse(state.active())
		assertTrue(state.bots.isEmpty())
	}

	@Test
	fun taskLinesAreParsedAndTheHeadingIsNot() {
		val task = OwnedBots.parseTask("[BotTasks] 3|pick_flax|Pick flax (Seers' Village)")!!
		assertEquals(3, task.index)
		assertEquals("pick_flax", task.id)
		assertEquals("Pick flax (Seers' Village)", task.label)
		assertNull(OwnedBots.parseTask("[BotTasks] 32 tasks: ::bot task <slot> <task>"))
		assertNull(OwnedBots.parseTask("Welcome to RuneScape."))
	}

	@Test
	fun botLinesNameTheirSlot() {
		assertEquals(0, OwnedBots.alertSlot("[Bot 1] Woody is ready."))
		assertEquals(3, OwnedBots.alertSlot("[Bot 4] Smith has died. Its items have been returned to your bank."))
		assertEquals(-1, OwnedBots.alertSlot("[Bot 5] nope"))
		assertEquals(-1, OwnedBots.alertSlot("[BotTasks] 1|a|b"))
		assertTrue(OwnedBots.isUrgent("[Bot 1] Woody is nearly dead and teleports home!"))
		assertTrue(OwnedBots.isUrgent("[Bot 2] Minnie stopped: it needs level 30 Mining."))
		assertFalse(OwnedBots.isUrgent("[Bot 1] Woody is ready."))
	}

	@Test
	fun theWorldTagIsTheNameAndSlot() {
		val state = OwnedBots.read { vars()[it] ?: 0 }
		assertEquals("Woody [1]", OwnedBots.tag(state.bot(0)!!))
		assertEquals("Minnie [2]", OwnedBots.tag(state.bot(1)!!))
	}

	@Test
	fun presetLinesAreParsed() {
		val preset = OwnedBots.parsePreset("[BotLoadouts] melee|iron_full_helm,bronze_sword")!!
		assertEquals("melee", preset.first)
		assertEquals("iron_full_helm,bronze_sword", preset.second)
		assertNull(OwnedBots.parsePreset("[BotLoadouts] 2 presets"))
		assertNull(OwnedBots.parsePreset("[BotTasks] 1|a|b"))
	}

	@Test
	fun presetEntriesSitUnderSubmenus() {
		val plain = OverlayMenuEntry("Close", "Loadout", Task {})
		assertEquals("", plain.submenu, "an entry is a plain one unless given a submenu")
		val load = OverlayMenuEntry("melee", "Woody", Task {}, OwnedBots.LOAD_PRESET)
		val delete = OverlayMenuEntry("melee", "preset", Task {}, OwnedBots.DELETE_PRESET)
		assertEquals("Load preset", load.submenu)
		assertEquals("Delete preset", delete.submenu)
		assertTrue(load.submenu != delete.submenu, "loading and deleting are separate submenus")
	}

	@Test
	fun itemNamesBecomeCommandArguments() {
		assertEquals("raw_shark", OwnedBots.commandName("Raw shark"))
		assertEquals("bronze_sword", OwnedBots.commandName("Bronze sword"))
		assertEquals("monks_robe", OwnedBots.commandName("Monk's robe"))
	}

	@Test
	fun worldOrderLabelsNameTheirBot() {
		OwnedBots.state = OwnedBots.read { vars()[it] ?: 0 }
		assertEquals("1", OwnedBots.botOf("Send Woody here"))
		assertEquals("2", OwnedBots.botOf("<col=ff9040>Minnie"))
		assertEquals("all", OwnedBots.botOf("All"))
		assertEquals("all", OwnedBots.botOf("Send all here"))
		assertEquals("", OwnedBots.botOf("Walk here"))
		OwnedBots.state = OwnedBots.State(emptyList(), 0)
	}

	@Test
	fun giveEntryLabelsCarryTheirAmount() {
		assertEquals("", OwnedBots.amountOf("Give to Woody"))
		assertEquals("5", OwnedBots.amountOf("5"))
		assertEquals("all", OwnedBots.amountOf("All (12)"))
		assertEquals("10", OwnedBots.amountOf("Woody: 10"))
		assertEquals("all", OwnedBots.amountOf("<col=ff9040>Woody: All (3)"))
	}

	@Test
	fun leftClickIsTheFirstOfTheFirstThreeOptions() {
		assertEquals("Eat", OwnedBotsLoadout.leftClickOption(arrayOf("Eat", null, null, null, "Drop")))
		assertEquals("Wield", OwnedBotsLoadout.leftClickOption(arrayOf(null, "Wield", null, null, "Drop")))
		// only options 4 and 5 (Drop): the game's left click is Use
		assertNull(OwnedBotsLoadout.leftClickOption(arrayOf(null, null, null, "Empty", "Drop")))
	}
}
