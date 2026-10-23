package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ItemIdentification
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Item Identification's labels from RuneLite's table on 634's items (ItemIdentification). */
class ItemIdentificationTest {

	private val notedGuam: ItemDefinition = Proxy.newProxyInstance(ItemDefinition::class.java.classLoader, arrayOf(ItemDefinition::class.java)) { _, method, _ ->
		when (method.name) {
			"isNoted" -> true
			"linkedId" -> 249
			else -> null
		}
	} as ItemDefinition

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"item" -> if (args[0] == 250) notedGuam else null
			else -> null
		}
	} as ClientApi

	private val herbs: Int = ItemIdentification.TYPES.indexOf("HERB")

	@BeforeTest
	fun fresh() {
		ItemIdentification.medium = false
		ItemIdentification.CATEGORY_DEFAULTS.copyInto(ItemIdentification.shown)
		ItemIdentification.shown[herbs] = true
	}

	@AfterTest
	fun tidy() {
		ItemIdentification.CATEGORY_DEFAULTS.copyInto(ItemIdentification.shown)
	}

	@Test
	fun herbsCarryTheirShortOrMediumName() {
		// clean and grimy guam
		assertEquals("G", ItemIdentification.label(client, 249))
		assertEquals("G", ItemIdentification.label(client, 199))
		ItemIdentification.medium = true
		assertEquals("Guam", ItemIdentification.label(client, 249))
	}

	@Test
	fun aNotedItemIsLabelledAsItsItem() {
		assertEquals("G", ItemIdentification.label(client, 250))
	}

	@Test
	fun aKindSwitchedOffShowsNothing() {
		ItemIdentification.shown[herbs] = false
		assertNull(ItemIdentification.label(client, 249))
		assertNull(ItemIdentification.label(client, 1))
	}

	@Test
	fun theDefaultsAreRuneLites() {
		// RuneLite's 31, but the four with nothing in 634
		assertEquals(27, ItemIdentification.TYPES.size)
		assertEquals(-1, ItemIdentification.TYPES.indexOf("CORAL_FRAG"))
		assertEquals(ItemIdentification.TYPES.size, ItemIdentification.CATEGORY_KEYS.size)
		assertEquals(ItemIdentification.TYPES.size, ItemIdentification.CATEGORY_LABELS.size)
		assertEquals(true, ItemIdentification.CATEGORY_DEFAULTS[ItemIdentification.TYPES.indexOf("SAPLING")])
		assertEquals(false, ItemIdentification.CATEGORY_DEFAULTS[herbs])
	}
}
