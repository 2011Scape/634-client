package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.BarrowsBrothers
import com.jagex.game.runetek5.client.features.BarrowsOverlay
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Widget
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Barrows Brothers' reward potential, puzzle answer and chest value (BarrowsBrothers). */
class BarrowsBrothersTest {

	private var cycle: Int = 10_000

	private val varbits: HashMap<Int, Int> = HashMap()

	/** The puzzle's components' models, by component id. */
	private val models: HashMap<Int, Int> = HashMap()

	private val prices: HashMap<Int, Int> = HashMap()

	private fun widget(component: Int): Widget = Proxy.newProxyInstance(Widget::class.java.classLoader, arrayOf(Widget::class.java)) { _, method, _ ->
		when (method.name) {
			"componentId" -> component
			"modelId" -> models[component] ?: -1
			else -> null
		}
	} as Widget

	private val brotherType: NpcDefinition = Proxy.newProxyInstance(NpcDefinition::class.java.classLoader, arrayOf(NpcDefinition::class.java)) { _, method, _ ->
		if (method.name == "combatLevel") BROTHER_LEVEL else null
	} as NpcDefinition

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"clientCycle" -> cycle
			"varbit" -> varbits[args[0] as Int] ?: 0
			"npcType" -> brotherType
			"itemPrice" -> prices[args[0] as Int] ?: 0
			"widget" -> if (args[0] == BarrowsBrothers.PUZZLE_INTERFACE) widget(args[1] as Int) else null
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		BarrowsBrothers.reset()
		BarrowsBrothers.enabled = true
	}

	@AfterTest
	fun tidy() {
		BarrowsBrothers.reset()
	}

	/** One kill as the server sends it: the killed-monsters count up by one, and the brother's varbit when he is one. */
	private fun kill(level: Int, brotherVarbit: Int = -1) {
		BarrowsBrothers.lastTargetLevel = level
		varbits[BarrowsBrothers.KILLED_MONSTERS_VARBIT] = (varbits[BarrowsBrothers.KILLED_MONSTERS_VARBIT] ?: 0) + 1
		if (brotherVarbit != -1) {
			varbits[brotherVarbit] = 1
		}
		cycle++
		BarrowsBrothers.countKills(client)
	}

	/** The values after login: the first look remembers them, the next ends the sync. */
	private fun sync() {
		BarrowsBrothers.countKills(client)
		cycle++
		BarrowsBrothers.countKills(client)
	}

	@Test
	fun eachKillAddsItsCombatLevel() {
		sync()
		kill(48)
		kill(25)
		assertEquals(73, BarrowsBrothers.killLevels)
	}

	@Test
	fun aBrotherSlainCountsHisOwnLevelNotTheLastTarget() {
		sync()
		kill(48, BarrowsBrothers.BROTHERS[1].killedVarbit)
		assertEquals(BROTHER_LEVEL, BarrowsBrothers.killLevels)
	}

	@Test
	fun theCountBackAtZeroClearsTheRun() {
		sync()
		kill(48)
		varbits.clear()
		cycle++
		BarrowsBrothers.countKills(client)
		assertEquals(0, BarrowsBrothers.killLevels)
	}

	@Test
	fun theServersCountArrivingAfterLoginIsNotAKill() {
		BarrowsBrothers.killLevels = 300
		BarrowsBrothers.countKills(client)
		varbits[BarrowsBrothers.KILLED_MONSTERS_VARBIT] = 5
		cycle++
		BarrowsBrothers.countKills(client)
		assertEquals(300, BarrowsBrothers.killLevels)
		kill(48)
		assertEquals(348, BarrowsBrothers.killLevels)
	}

	@Test
	fun aCountOfZeroOnceSyncedClearsAKeptSum() {
		BarrowsBrothers.killLevels = 300
		sync()
		assertEquals(0, BarrowsBrothers.killLevels)
	}

	@Test
	fun thePotentialStopsAtTheMost() {
		BarrowsBrothers.killLevels = 2000
		assertEquals(BarrowsBrothers.MAX_POTENTIAL, BarrowsBrothers.rewardPotential())
	}

	@Test
	fun thePotentialReadsAsRuneLitesPercentage() {
		assertEquals("0.00%", BarrowsOverlay.percent(0))
		assertEquals("100.00%", BarrowsOverlay.percent(1012))
		assertEquals("74.70%", BarrowsOverlay.percent(756))
		assertEquals("9.88%", BarrowsOverlay.percent(100))
	}

	@Test
	fun thePotentialIsGreenFromTheBloodRunesToTheBoltRacks() {
		assertEquals(-0x1, BarrowsOverlay.potentialColour(630))
		assertEquals(0xFFFFFF00.toInt(), BarrowsOverlay.potentialColour(631))
		assertEquals(0xFF00FF00.toInt(), BarrowsOverlay.potentialColour(756))
		assertEquals(0xFF00FF00.toInt(), BarrowsOverlay.potentialColour(880))
		assertEquals(0xFFFFFF00.toInt(), BarrowsOverlay.potentialColour(881))
	}

	@Test
	fun thePuzzlesAnswerIsTheFirstPictureLessThree() {
		BarrowsBrothers.interfaceChanged(BarrowsBrothers.PUZZLE_INTERFACE, true)
		// Void's "arrows": the sequence 6716-6718, the choices 6715, 6714, 6713, the answer 6713
		models[6] = 6716
		models[2] = 6715
		models[3] = 6714
		models[5] = 6713
		assertEquals(5, BarrowsBrothers.puzzleAnswer(client)?.componentId())
		BarrowsBrothers.interfaceChanged(BarrowsBrothers.PUZZLE_INTERFACE, false)
		assertNull(BarrowsBrothers.puzzleAnswer(client))
	}

	@Test
	fun theChestIsWorthWhatTheBackpackGained() {
		prices[995] = 1
		prices[565] = 400
		val before: IntArray = intArrayOf(995, 100, -1, 0, -1, 0)
		val after: IntArray = intArrayOf(995, 600, 565, 40, -1, 0)
		assertEquals(500L + 40L * 400L, BarrowsBrothers.gainedValue(client, before, after))
	}

	@Test
	fun barrowsIsLoadedWhenItsRegionIsInTheScene() {
		val scene = { baseX: Int, baseY: Int ->
			Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, _ ->
				when (method.name) {
					"sceneBaseX" -> baseX
					"sceneBaseY" -> baseY
					"sceneSize" -> 104
					else -> null
				}
			} as ClientApi
		}
		assertTrue(BarrowsBrothers.isBarrowsLoaded(scene(3520 - 40, 3264 - 40)))
		assertFalse(BarrowsBrothers.isBarrowsLoaded(scene(3520 - 40, 9664 - 40)))
	}

	companion object {
		private const val BROTHER_LEVEL: Int = 115
	}
}
