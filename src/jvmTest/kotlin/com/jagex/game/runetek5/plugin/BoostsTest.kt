package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Boosts
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Boosts Information's skills to display, timers and notification (Boosts). */
class BoostsTest {

	/** The client cycles counted in the game (ClientApi.serverTick): 30 to a game tick. */
	private var cycles: Int = 30_000

	private val levels: IntArray = IntArray(Skill.COUNT) { 1 }

	private val bases: IntArray = IntArray(Skill.COUNT) { 1 }

	private val varps: HashMap<Int, Int> = HashMap()

	private val notified: ArrayList<String> = ArrayList()

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"serverTick" -> cycles
			"skillLevel" -> levels[args[0] as Int]
			"skillBaseLevel" -> bases[args[0] as Int]
			"varp" -> varps[args[0] as Int] ?: 0
			"gameState" -> GameState.LOGGED_IN
			"notify" -> {
				notified.add(args[0] as String)
				true
			}
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		Boosts.reset()
		Boosts.enabled = true
		Boosts.displayBoosts = Boosts.BOOSTS_COMBAT
		Boosts.displayNextBuffChange = Boosts.CHANGE_BOOSTED
		Boosts.displayNextDebuffChange = Boosts.CHANGE_NEVER
		Boosts.boostThreshold = 0
		Boosts.notifyOnBoost = false
		Boosts.quiet = false
		Boosts.updateShownSkills()
	}

	@AfterTest
	fun tidy() {
		Boosts.reset()
	}

	/** A level as the packet sets it: the event (with the old level still in the client), then the store. */
	private fun level(skill: Int, level: Int) {
		Boosts.statChanged(client, StatChanged(skill, 0, 0, level))
		levels[skill] = level
	}

	private fun ticks(n: Int) {
		cycles += n * 30
	}

	@Test
	fun theCombatSkillsDifferingFromTheirLevelAreDisplayed() {
		bases[Skill.ATTACK] = 70
		bases[Skill.DEFENCE] = 70
		bases[Skill.MINING] = 70
		levels.fill(70)
		level(Skill.ATTACK, 75)
		level(Skill.DEFENCE, 70)
		level(Skill.MINING, 73)
		Boosts.tick(client)
		assertTrue(Boosts.toDisplay[Skill.ATTACK])
		assertFalse(Boosts.toDisplay[Skill.DEFENCE], "at its level")
		assertFalse(Boosts.toDisplay[Skill.MINING], "not a combat skill")
		Boosts.displayBoosts = Boosts.BOOSTS_BOTH
		Boosts.configChanged()
		Boosts.tick(client)
		assertTrue(Boosts.toDisplay[Skill.MINING])
		assertFalse(Boosts.toDisplay[Skill.PRAYER], "never shown")
	}

	@Test
	fun aBoostWearingOffTimesTheNextMinute() {
		bases[Skill.STRENGTH] = 60
		level(Skill.STRENGTH, 65)
		Boosts.tick(client)
		assertEquals(-1, Boosts.changeDownTicks(client), "a jump up is not a change")
		level(Skill.STRENGTH, 64)
		Boosts.tick(client)
		assertEquals(100, Boosts.changeDownTicks(client))
		assertEquals(60, Boosts.changeDownSeconds(client))
		ticks(40)
		assertEquals(60, Boosts.changeDownTicks(client))
		assertEquals(36, Boosts.changeDownSeconds(client))
		cycles += 15 // half a tick into the 41st
		assertEquals(35, Boosts.changeDownSeconds(client))
	}

	@Test
	fun berserkerHoldsTheBoostFifteenTicksLonger() {
		bases[Skill.ATTACK] = 80
		level(Skill.ATTACK, 90)
		level(Skill.ATTACK, 89)
		Boosts.tick(client)
		varps[Boosts.PRAYER_BOOK_VARP] = 1
		varps[Boosts.CURSES_VARP] = 1 shl Boosts.BERSERKER_BIT
		assertEquals(Boosts.BERSERKER_TICKS, Boosts.changeDownTicks(client))
		varps[Boosts.CURSES_VARP] = 0
		assertEquals(Boosts.RESTORE_TICKS, Boosts.changeDownTicks(client))
		// the firing at 100 went by without a drop: Berserker held it, the drop comes 15 later
		ticks(105)
		assertEquals(10, Boosts.changeDownTicks(client))
	}

	@Test
	fun aDrainComingBackTimesTheDebuffChangeWhenShown() {
		bases[Skill.DEFENCE] = 50
		Boosts.displayNextDebuffChange = Boosts.CHANGE_BOOSTED
		level(Skill.DEFENCE, 40)
		level(Skill.DEFENCE, 41)
		Boosts.tick(client)
		assertEquals(100, Boosts.changeUpTicks(client))
		// with Rapid Restore the server brings two levels back at a time
		varps[Boosts.PRAYERS_VARP] = 1 shl Boosts.RAPID_RESTORE_BIT
		ticks(100)
		Boosts.tick(client)
		assertEquals(-1, Boosts.changeUpTicks(client), "run out and not ALWAYS: dropped")
		level(Skill.DEFENCE, 43)
		Boosts.tick(client)
		assertEquals(100, Boosts.changeUpTicks(client))
	}

	@Test
	fun alwaysStartsTheTimerAgain() {
		bases[Skill.MAGIC] = 70
		Boosts.displayNextBuffChange = Boosts.CHANGE_ALWAYS
		level(Skill.MAGIC, 75)
		level(Skill.MAGIC, 74)
		Boosts.tick(client)
		ticks(100)
		Boosts.tick(client)
		assertEquals(100, Boosts.changeDownTicks(client))
	}

	@Test
	fun aBoostFallingToTheThresholdNotifiesOnce() {
		bases[Skill.RANGED] = 90
		Boosts.notifyOnBoost = true
		Boosts.boostThreshold = 2
		level(Skill.RANGED, 94)
		level(Skill.RANGED, 93)
		assertTrue(notified.isEmpty())
		level(Skill.RANGED, 92)
		assertEquals(listOf("Ranged level is getting low!"), notified)
		level(Skill.RANGED, 91)
		assertEquals(1, notified.size, "already below")
		assertEquals("Ranged level is getting low!", Boosts.lastNotification)
	}

	@Test
	fun theRelativeTextAndColours() {
		Boosts.boostThreshold = 3
		assertEquals("+4", Boosts.relative(4))
		assertEquals("-2", Boosts.relative(-2))
		assertEquals("0", Boosts.relative(0))
		assertEquals(Boosts.DRAINED, Boosts.colour(-1))
		assertEquals(Boosts.YELLOW, Boosts.colour(3))
		assertEquals(Boosts.GREEN, Boosts.colour(4))
		assertNull(Boosts.context())
	}
}
