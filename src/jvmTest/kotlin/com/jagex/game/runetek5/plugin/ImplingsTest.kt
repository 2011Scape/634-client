package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Implings
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Npc
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Implings' kinds by Void's ids and their announcements (Implings). */
class ImplingsTest {

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, _ ->
		if (method.name == "notify") true else null
	} as ClientApi

	private fun npc(typeId: Int): Npc = Proxy.newProxyInstance(Npc::class.java.classLoader, arrayOf(Npc::class.java)) { _, method, _ ->
		if (method.name == "typeId") typeId else null
	} as Npc

	@BeforeTest
	fun fresh() {
		for (k in Implings.KINDS.indices) {
			Implings.modes[k] = Implings.KINDS[k].defaultMode
		}
		Implings.lastNotification = null
	}

	@AfterTest
	fun tidy() = fresh()

	@Test
	fun theKindsAreKnownByVoidsIdsInBothPlaces() {
		assertEquals("Baby", Implings.KINDS[Implings.kindOf(1028)].name)
		assertEquals("Baby", Implings.KINDS[Implings.kindOf(6055)].name)
		assertEquals("Dragon", Implings.KINDS[Implings.kindOf(6064)].name)
		assertEquals("Kingly", Implings.KINDS[Implings.kindOf(7906)].name)
		assertEquals(-1, Implings.kindOf(1))
	}

	@Test
	fun anImplingSetToNotifyIsAnnounced() {
		Implings.spawned(client, npc(6054))
		assertNull(Implings.lastNotification)
		Implings.modes[Implings.kindOf(6054)] = Implings.NOTIFY
		Implings.spawned(client, npc(6054))
		assertEquals("Dragon impling is in the area", Implings.lastNotification)
	}

	@Test
	fun theDefaultsAreRuneLitesDragonsHighlighted() {
		assertEquals(Implings.HIGHLIGHT, Implings.KINDS[Implings.kindOf(6054)].defaultMode)
		assertEquals(Implings.NONE, Implings.KINDS[Implings.kindOf(1028)].defaultMode)
	}
}
