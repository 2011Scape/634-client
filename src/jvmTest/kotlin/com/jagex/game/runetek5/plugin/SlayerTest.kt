package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Slayer
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Npc
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Slayer's task from Void's varps, its monsters and their weaknesses (Slayer). */
class SlayerTest {

	private var count: Int = 0

	private var target: Int = 0

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"varp" -> if (args[0] == Slayer.COUNT_VARP) count else if (args[0] == Slayer.TASK_VARP) target else 0
			else -> null
		}
	} as ClientApi

	private fun npc(typeId: Int): Npc = Proxy.newProxyInstance(Npc::class.java.classLoader, arrayOf(Npc::class.java)) { _, method, _ ->
		when (method.name) {
			"typeId" -> typeId
			else -> null
		}
	} as Npc

	@BeforeTest
	fun fresh() {
		count = 0
		target = 0
		Slayer.update(client)
	}

	@AfterTest
	fun tidy() = fresh()

	@Test
	fun theTaskComesFromTheVarps() {
		count = 37
		target = 2
		Slayer.update(client)
		assertEquals("Goblins", Slayer.task!!.name)
		assertEquals(37, Slayer.amount)
		assertEquals(37, Slayer.counter!!.count)
		count = 36
		Slayer.update(client)
		assertEquals(36, Slayer.counter!!.count)
	}

	@Test
	fun aFinishedTaskIsNoTask() {
		count = 0
		target = 2
		Slayer.update(client)
		assertNull(Slayer.task)
		assertNull(Slayer.counter)
	}

	@Test
	fun theTasksMonstersAreItsCategorysNpcs() {
		count = 10
		target = 2
		Slayer.update(client)
		// 445 is a goblin, 1610 a gargoyle
		assertTrue(Slayer.isTarget(npc(445)))
		assertFalse(Slayer.isTarget(npc(1610)))
	}

	@Test
	fun aGargoyleIsFinishedUnder90LifePoints() {
		val gargoyle = Slayer.weakness(1610)!!
		assertEquals(4162, gargoyle.itemId)
		// 1,050 life points: 2 of 30 is 70, 3 of 30 is 105
		assertTrue(Slayer.finishable(gargoyle, 2, 30))
		assertFalse(Slayer.finishable(gargoyle, 3, 30))
		assertFalse(Slayer.finishable(gargoyle, -1, 30))
	}

	@Test
	fun aLizardsThresholdIsAQuarter() {
		val lizard = Slayer.weakness(2803)!!
		assertEquals(100, lizard.threshold)
	}
}
