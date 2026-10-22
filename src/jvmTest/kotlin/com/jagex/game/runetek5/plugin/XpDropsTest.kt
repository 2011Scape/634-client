package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.XpDrops
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.Skill
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

/** Not part of the original client: XP Drops' drops and RuneLite's XP Drop rules (XpDrops). */
class XpDropsTest {

	private var cycle: Int = 1000

	/** The varps the prayer colours read: the book, the prayers, the curses. */
	private val varps: HashMap<Int, Int> = HashMap()

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"clientCycle" -> cycle
			"varp" -> varps[args[0] as Int] ?: 0
			"gameState" -> GameState.LOGGED_IN
			else -> null
		}
	} as ClientApi

	@AfterTest
	fun tidy() {
		XpDrops.reset()
		XpDrops.group = false
		XpDrops.standardColour = null
		XpDrops.fakeXpDropDelay = 0
		XpDrops.colour = 0
		XpDrops.enabled = true
	}

	@Test
	fun theLoginsUpdateMakesNoDropAndAGainDoes() {
		XpDrops.reset()
		XpDrops.statUpdated(client, Skill.ATTACK, 0, 1000)
		assertNull(XpDrops.lastDropText, "the first update only primes the skill")
		XpDrops.statUpdated(client, Skill.ATTACK, 1000, 1083)
		assertEquals("+83", XpDrops.lastDropText)
		XpDrops.statUpdated(client, Skill.ATTACK, 1083, 0)
		assertEquals(1, XpDrops.dropCount(), "a loss is no drop")
	}

	@Test
	fun groupDropsShareATickAndIndividualOnesDoNot() {
		XpDrops.reset()
		XpDrops.group = true
		XpDrops.addDrop(client, Skill.ATTACK, 40, cycle)
		XpDrops.addDrop(client, Skill.CONSTITUTION, 13, cycle + 1)
		assertEquals(1, XpDrops.dropCount())
		assertEquals(listOf(Skill.ATTACK, Skill.CONSTITUTION), XpDrops.drops[0].skills)
		assertEquals(53, XpDrops.drops[0].amount)
		XpDrops.addDrop(client, Skill.ATTACK, 40, cycle + XpDrops.CYCLES_PER_TICK)
		assertEquals(2, XpDrops.dropCount(), "the next tick is a drop of its own")
		XpDrops.group = false
		XpDrops.addDrop(client, Skill.MAGIC, 1, cycle + XpDrops.CYCLES_PER_TICK)
		assertEquals(3, XpDrops.dropCount())
	}

	@Test
	fun aPrayerOfTheDropsKindColoursItAsRuneLiteDoes() {
		XpDrops.rangePrayerColour = 0xFF00FF00.toInt()
		XpDrops.meleePrayerColour = 0xFFFF0000.toInt()
		XpDrops.magePrayerColour = 0xFF0000FF.toInt()
		assertEquals(XpDrops.TYPE_RANGE, XpDrops.dropType(listOf(Skill.DEFENCE, Skill.RANGED)), "Ranged before melee (defensive shots)")
		assertEquals(XpDrops.TYPE_MAGIC, XpDrops.dropType(listOf(Skill.DEFENCE, Skill.MAGIC)), "Magic before melee (defensive casting)")
		assertEquals(0xFFFFFFFF.toInt(), XpDrops.colourOf(client, listOf(Skill.RANGED)), "no prayer: the setup's white")
		varps[XpDrops.PRAYERS_VARP] = 1 shl 22 // eagle eye
		assertEquals(0xFF00FF00.toInt(), XpDrops.colourOf(client, listOf(Skill.RANGED)))
		assertEquals(0xFFFFFFFF.toInt(), XpDrops.colourOf(client, listOf(Skill.STRENGTH)), "a ranged prayer does not colour melee")
		varps[XpDrops.PRAYER_BOOK_VARP] = 1
		varps[XpDrops.CURSES_VARP] = 1 shl 19 // turmoil
		assertEquals(0xFFFF0000.toInt(), XpDrops.colourOf(client, listOf(Skill.STRENGTH)), "curses: turmoil is melee")
		XpDrops.standardColour = 0xFF123456.toInt()
		assertEquals(0xFF123456.toInt(), XpDrops.colourOf(client, listOf(Skill.COOKING)), "the standard colour over the setup's")
	}

	@Test
	fun theFakeDropComesBackAfterItsDelay() {
		XpDrops.reset()
		XpDrops.fakeXpDropDelay = 2
		XpDrops.duration = 4
		XpDrops.statUpdated(client, Skill.WOODCUTTING, 0, 100)
		XpDrops.statUpdated(client, Skill.WOODCUTTING, 100, 125)
		XpDrops.cycle(client, cycle + 2 * XpDrops.CYCLES_PER_TICK - 1)
		assertEquals(1, XpDrops.dropCount(), "not before two ticks")
		XpDrops.cycle(client, cycle + 2 * XpDrops.CYCLES_PER_TICK)
		assertEquals(2, XpDrops.dropCount())
		assertEquals(25, XpDrops.drops[1].amount, "the last gain again")
		assertEquals(Skill.WOODCUTTING, XpDrops.drops[1].skills[0])
		XpDrops.duration = 2
	}

	@Test
	fun pickingAStandardColourTurnsItOn() {
		val saved: HashMap<String, Any?> = HashMap()
		val config: PluginConfig = Proxy.newProxyInstance(PluginConfig::class.java.classLoader, arrayOf(PluginConfig::class.java)) { _, method, args ->
			when (method.name) {
				"getBoolean" -> saved[args[0]] ?: args[1]
				"setBoolean" -> saved.put(args[0] as String, args[1]).let { null }
				"save" -> true
				else -> null
			}
		} as PluginConfig
		XpDrops.standardColourPicked(config, "colour")
		assertFalse(saved.containsKey("useStandardColor"), "another setting leaves the switch alone")
		XpDrops.standardColourPicked(config, "standardColor")
		assertEquals(true, saved["useStandardColor"])
	}
}
