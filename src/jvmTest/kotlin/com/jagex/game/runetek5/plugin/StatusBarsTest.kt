package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ItemHeals
import com.jagex.game.runetek5.client.features.StatusBars
import com.jagex.game.runetek5.client.features.StatusBarsOverlay
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Skill
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Status Bars against RuneLite's rules (StatusBarsPlugin, StatusBarsOverlay, BarRenderer). */
class StatusBarsTest {

	private val varps: HashMap<Int, Int> = HashMap()

	private val varbits: HashMap<Int, Int> = HashMap()

	private val levels: IntArray = IntArray(Skill.COUNT) { 99 }

	private val baseLevels: IntArray = IntArray(Skill.COUNT) { 99 }

	private var interacting: Any? = null

	private val me: Player = proxy(Player::class.java) { name, _ -> if (name == "interacting") interacting else null } as Player

	private val client: ClientApi = proxy(ClientApi::class.java) { name, args ->
		when (name) {
			"varp" -> varps[args!![0] as Int] ?: 0
			"varbit" -> varbits[args!![0] as Int] ?: 0
			"skillLevel" -> levels[args!![0] as Int]
			"skillBaseLevel" -> baseLevels[args!![0] as Int]
			"runEnergy" -> 64
			"localPlayer" -> me
			else -> null
		}
	} as ClientApi

	private fun proxy(type: Class<*>, answer: (String, Array<Any?>?) -> Any?): Any {
		return Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { _, method, args -> answer(method.name, args) }
	}

	private fun npc(vararg actions: String?): Npc {
		val type: NpcDefinition = proxy(NpcDefinition::class.java) { name, args -> if (name == "action") actions.getOrNull(args!![0] as Int) else null } as NpcDefinition
		return proxy(Npc::class.java) { name, _ -> if (name == "type") type else null } as Npc
	}

	@AfterTest
	fun reset() {
		StatusBars.hoveredItem = -1
		StatusBars.hideAfterCombatDelay = 0
		StatusBars.barsDisplayed = true
	}

	@Test
	fun modesReadRuneLitesNames() {
		assertEquals(StatusBars.HITPOINTS, StatusBars.mode("Hitpoints", StatusBars.PRAYER))
		assertEquals(StatusBars.RUN_ENERGY, StatusBars.mode("RUN_ENERGY", StatusBars.PRAYER))
		assertEquals(StatusBars.SPECIAL_ATTACK, StatusBars.mode("Special Attack", StatusBars.PRAYER))
		assertEquals(StatusBars.SUMMONING, StatusBars.mode("WARMTH", StatusBars.PRAYER))
		assertEquals(StatusBars.PRAYER, StatusBars.mode("nonsense", StatusBars.PRAYER))
	}

	@Test
	fun valuesComeFromTheGame() {
		varbits[StatusBars.LIFE_POINTS_VARBIT] = 853
		assertEquals(853, StatusBars.current(client, StatusBars.HITPOINTS))
		assertEquals(990, StatusBars.maximum(client, StatusBars.HITPOINTS))
		// before the varbit comes, the level times ten
		varbits.remove(StatusBars.LIFE_POINTS_VARBIT)
		levels[Skill.CONSTITUTION] = 50
		assertEquals(500, StatusBars.current(client, StatusBars.HITPOINTS))
		varps[StatusBars.SPECIAL_VARP] = 750
		assertEquals(75, StatusBars.current(client, StatusBars.SPECIAL_ATTACK))
		assertEquals(64, StatusBars.current(client, StatusBars.RUN_ENERGY))
		assertEquals(100, StatusBars.maximum(client, StatusBars.RUN_ENERGY))
	}

	@Test
	fun coloursFollowTheStatus() {
		assertEquals(StatusBarsOverlay.HEALTH, StatusBarsOverlay.colour(client, StatusBars.HITPOINTS))
		varps[StatusBars.POISON_VARP] = 6
		assertEquals(StatusBarsOverlay.POISONED, StatusBarsOverlay.colour(client, StatusBars.HITPOINTS))
		// an antipoison's protection is a negative value: not poisoned
		varps[StatusBars.POISON_VARP] = -20
		varps[StatusBars.DISEASE_VARP] = 3
		assertEquals(StatusBarsOverlay.DISEASE, StatusBarsOverlay.colour(client, StatusBars.HITPOINTS))
		assertEquals(StatusBarsOverlay.PRAYER, StatusBarsOverlay.colour(client, StatusBars.PRAYER))
		varps[1395] = 1 shl 6
		assertEquals(StatusBarsOverlay.ACTIVE_PRAYER, StatusBarsOverlay.colour(client, StatusBars.PRAYER))
		// on curses the normal prayers' varp does not count
		varps[1584] = 1
		assertEquals(StatusBarsOverlay.PRAYER, StatusBarsOverlay.colour(client, StatusBars.PRAYER))
		varps[StatusBars.MOVEMENT_VARP] = StatusBars.RESTING
		assertEquals(StatusBarsOverlay.RUN_RESTING, StatusBarsOverlay.colour(client, StatusBars.RUN_ENERGY))
	}

	@Test
	fun barHeightIsRuneLites() {
		assertEquals(252, StatusBarsOverlay.barHeight(99, 99, 252))
		assertEquals(252, StatusBarsOverlay.barHeight(99, 120, 252))
		assertEquals(127, StatusBarsOverlay.barHeight(99, 50, 252))
		assertEquals(0, StatusBarsOverlay.barHeight(990, 0, 252))
	}

	@Test
	fun restoresComeFromTheHoveredItem() {
		// a shark heals 200 lifepoints; a prayer potion 7 + a quarter of the most prayer; a super energy 20 run energy
		StatusBars.hoveredItem = 385
		assertEquals(200, StatusBars.restore(client, StatusBars.HITPOINTS))
		assertEquals(0, StatusBars.restore(client, StatusBars.PRAYER))
		StatusBars.hoveredItem = 2434
		assertEquals(7 + 99 / 4, StatusBars.restore(client, StatusBars.PRAYER))
		StatusBars.hoveredItem = 3016
		assertEquals(20, StatusBars.restore(client, StatusBars.RUN_ENERGY))
		// a Saradomin brew: 20 + 15% of the most lifepoints
		assertEquals(20 + 990 * 15 / 100, ItemHeals.restore(6685, ItemHeals.HITPOINTS, 990))
		StatusBars.hoveredItem = -1
		assertEquals(0, StatusBars.restore(client, StatusBars.HITPOINTS))
	}

	@Test
	fun barsHideAfterTheFight() {
		StatusBars.hideAfterCombatDelay = 5
		interacting = npc(null, "Attack", null)
		StatusBars.checkStatusBars(client, 100)
		assertTrue(StatusBars.barsDisplayed)
		interacting = null
		StatusBars.checkStatusBars(client, 104)
		assertTrue(StatusBars.barsDisplayed)
		StatusBars.checkStatusBars(client, 105)
		assertFalse(StatusBars.barsDisplayed)
		// an npc without Attack is not a fight
		interacting = npc("Talk-to")
		StatusBars.checkStatusBars(client, 120)
		assertFalse(StatusBars.barsDisplayed)
		// a delay of 0 always shows them
		StatusBars.hideAfterCombatDelay = 0
		StatusBars.checkStatusBars(client, 121)
		assertTrue(StatusBars.barsDisplayed)
	}
}
