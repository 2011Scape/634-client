package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ItemStatTables
import com.jagex.game.runetek5.client.features.ItemStats
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Skill
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Not part of the original client: Item Stats' changes as Void makes them, and RuneLite's text for them (ItemStats). */
class ItemStatsTest {

	private val levels: IntArray = IntArray(Skill.COUNT) { 70 }
	private val bases: IntArray = IntArray(Skill.COUNT) { 70 }
	private var lifePoints: Int = 700

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"skillLevel" -> levels[args[0] as Int]
			"skillBaseLevel" -> bases[args[0] as Int]
			"varbit" -> lifePoints
			"runEnergy" -> 50
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		ItemStats.relative = true
		ItemStats.absolute = true
		ItemStats.theoretical = false
		ItemStats.alwaysShowBaseStats = false
	}

	@AfterTest
	fun tidy() {
		fresh()
	}

	private fun effects(vararg e: IntArray): Array<IntArray> = arrayOf(*e)

	@Test
	fun aSuperStrengthBoostsFivePlusFifteenPercent() {
		val changes = ItemStats.changes(client, effects(intArrayOf(ItemStats.KIND_BOOST, Skill.STRENGTH, 5, 1500, 0)))
		assertEquals(15, changes[0].relativeLow)
		assertEquals(85, changes[0].absoluteLow)
		assertEquals("<col=33ee33>+15 (85) Strength</br>", ItemStats.statChangeString(changes[0]))
	}

	@Test
	fun aBoostDoesNotStackOnItself() {
		levels[Skill.STRENGTH] = 85
		val changes = ItemStats.changes(client, effects(intArrayOf(ItemStats.KIND_BOOST, Skill.STRENGTH, 5, 1500, 0)))
		assertEquals(0, changes[0].relativeLow)
		assertEquals(ItemStats.NO_CHANGE, changes[0].positivity)
	}

	@Test
	fun aRestoreStopsAtTheLevel() {
		levels[Skill.PRAYER] = 60
		val changes = ItemStats.changes(client, effects(intArrayOf(ItemStats.KIND_RESTORE, Skill.PRAYER, 7, 2500, 0)))
		assertEquals(10, changes[0].relativeLow)
		assertEquals(ItemStats.BETTER_CAPPED, changes[0].positivity)
	}

	@Test
	fun foodHealsARangeOfLifepoints() {
		lifePoints = 500
		val changes = ItemStats.changes(client, effects(intArrayOf(ItemStats.KIND_HEAL, Skill.CONSTITUTION, 70, 100, 0)))
		assertEquals("<col=33ee33>+70~+100 (570~600) Constitution</br>", ItemStats.statChangeString(changes[0]))
	}

	@Test
	fun aSaradominBrewDrainsTheOthers() {
		val changes = ItemStats.changes(client, effects(intArrayOf(ItemStats.KIND_DRAIN, Skill.ATTACK, 2, 1000, 0)))
		assertEquals(-9, changes[0].relativeLow)
		assertEquals(ItemStats.WORSE, changes[0].positivity)
	}

	@Test
	fun theWeightAndBonusesReadAsRuneLites() {
		assertEquals("0.453", ItemStats.number(0.453))
		assertEquals("41", ItemStats.number(41.0))
		assertEquals("<col=ee3333>+0.65</col>", ItemStats.changeString(0.65, true, false))
		assertEquals("<col=33ee33>+3%</col>", ItemStats.changeString(3.0, false, true))
		assertEquals("Speed: <col=33ee33>-1</col></br>", ItemStats.statRow("Speed", 4.0, -1.0, true, false, true))
	}

	@Test
	fun voidsTablesKnowTheWhipAndTheShark() {
		assertEquals(3, ItemStatTables.slot(4151))
		assertTrue(ItemStatTables.isTwoHanded(7158))
		assertNotNull(ItemStatTables.consumable(385))
		assertNotNull(ItemStatTables.duration(2448))
	}
}
