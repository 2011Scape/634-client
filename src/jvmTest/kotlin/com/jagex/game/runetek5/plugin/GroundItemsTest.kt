package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.GroundItemRules
import com.jagex.game.runetek5.client.features.GroundItems
import com.jagex.game.runetek5.client.features.GroundItemsMenu
import com.jagex.game.runetek5.client.features.GroundItemsOverlay
import com.jagex.game.runetek5.client.features.LootAttribution
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.events.GroundItem
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Ground Items' rules against RuneLite's (GroundItemRules, GroundItems, GroundItemsMenu, LootAttribution). */
class GroundItemsTest {

	private fun item(name: String, quantity: Int, ge: Int, ha: Int): GroundItems.Item {
		val item: GroundItems.Item = GroundItems.Item(1, 3200, 3200, 0)
		item.name = name
		item.quantity = quantity
		item.gePrice = ge
		item.haPrice = ha
		item.tradeable = true
		return item
	}

	/** Every setting at RuneLite's default, the lists as given. */
	private fun lists(highlighted: String, hidden: String) {
		GroundItems.highlightedCsv = highlighted
		GroundItems.hiddenCsv = hidden
		GroundItems.highlightedList = GroundItemRules.parseList(highlighted)
		GroundItems.hiddenList = GroundItemRules.parseList(hidden)
	}

	@BeforeTest
	fun defaults() {
		GroundItems.valueCalculationMode = GroundItems.VALUE_HIGHEST
		GroundItems.hideUnderValue = 0
		GroundItems.dontHideUntradeables = true
		GroundItems.lowValuePrice = 20_000
		GroundItems.mediumValuePrice = 100_000
		GroundItems.highValuePrice = 1_000_000
		GroundItems.insaneValuePrice = 10_000_000
		GroundItems.tierPrices = intArrayOf(10_000_000, 1_000_000, 100_000, 20_000)
		GroundItems.tierColours = intArrayOf(GroundItems.insaneValueColour, GroundItems.highValueColour, GroundItems.mediumValueColour, GroundItems.lowValueColour)
		GroundItems.showLootbeamForHighlighted = false
		GroundItems.showLootbeamTier = 3
		GroundItems.ownershipFilterMode = GroundItems.OWNERSHIP_ALL
		GroundItems.priceDisplayMode = GroundItems.PRICE_BOTH
		lists("", "Vial, Ashes, Coins, Bones, Bucket, Jug, Seaweed")
	}

	@AfterTest
	fun tidy() {
		defaults()
	}

	@Test
	fun listEntriesReadAsRuneLiteReadsThem() {
		val plain: GroundItemRules.Threshold = assertNotNull(GroundItemRules.threshold("Coins"))
		assertEquals("Coins", plain.name)
		assertTrue(plain.quantityHolds(1), "more than 0")
		val more: GroundItemRules.Threshold = assertNotNull(GroundItemRules.threshold("Coins>1000"))
		assertEquals("Coins", more.name)
		assertFalse(more.quantityHolds(1000))
		assertTrue(more.quantityHolds(1001))
		val less: GroundItemRules.Threshold = assertNotNull(GroundItemRules.threshold("Bones < 5"))
		assertEquals("Bones", less.name)
		assertTrue(less.quantityHolds(4))
		assertFalse(less.quantityHolds(5))
		assertTrue(assertNotNull(GroundItemRules.threshold("Rune*")).wildcard)
		assertEquals("Item 2", assertNotNull(GroundItemRules.threshold("Item 2")).name, "digits without an operator stay in the name")
		assertNull(GroundItemRules.threshold(""))
	}

	@Test
	fun anExactMatchBeatsAWildcardAndAHighlightBeatsAHide() {
		lists("Rune*", "Rune scimitar, Bones")
		assertEquals(GroundItems.VERDICT_HIDDEN, GroundItems.hiddenOrHighlighted(item("Rune scimitar", 1, 0, 0)), "an exact hide beats a wildcard highlight")
		assertEquals(GroundItems.VERDICT_HIGHLIGHTED, GroundItems.hiddenOrHighlighted(item("Rune platebody", 1, 0, 0)))
		lists("Bones", "Bones")
		assertEquals(GroundItems.VERDICT_HIGHLIGHTED, GroundItems.hiddenOrHighlighted(item("Bones", 1, 0, 0)), "highlighted first")
		assertTrue(GroundItemRules.wildcardMatches("*impling*jar", "Baby impling jar"))
		assertFalse(GroundItemRules.wildcardMatches("rune*", "Adamant sword"))
	}

	@Test
	fun quantitiesAreWrittenShortAsRuneLiteWritesThem() {
		assertEquals("9,450", GroundItemRules.quantityToStackSize(9450))
		assertEquals("12.3K", GroundItemRules.quantityToStackSize(12_345))
		assertEquals("100K", GroundItemRules.quantityToStackSize(100_000))
		assertEquals("1.23M", GroundItemRules.quantityToStackSize(1_234_567))
		assertEquals("2.14B", GroundItemRules.quantityToStackSize(Int.MAX_VALUE.toLong()))
		assertEquals("-12.3K", GroundItemRules.quantityToStackSize(-12_345))
	}

	@Test
	fun aStackTakesTheHighestTierItsValueIsAbove() {
		val item: GroundItems.Item = item("Rune bar", 10, 12_000, 3_000) // 120,000 GE, 30,000 HA
		assertEquals(GroundItems.mediumValueColour, GroundItems.highlightColour(item))
		GroundItems.valueCalculationMode = GroundItems.VALUE_HA
		assertEquals(GroundItems.lowValueColour, GroundItems.highlightColour(item))
		assertNull(GroundItems.highlightColour(item("Logs", 1, 50, 2)))
		val exact: GroundItems.Item = item("Lobster", 1, 20_000, 0)
		GroundItems.valueCalculationMode = GroundItems.VALUE_HIGHEST
		assertNull(GroundItems.highlightColour(exact), "strictly above the tier's price")
	}

	@Test
	fun hideUnderValueSparesUntradeablesThatAreWorthNothing() {
		GroundItems.hideUnderValue = 1000
		val cheap: GroundItems.Item = item("Logs", 1, 50, 2)
		assertTrue(GroundItems.isHidden(cheap))
		val untradeable: GroundItems.Item = item("Quest item", 1, 0, 0)
		untradeable.tradeable = false
		assertFalse(GroundItems.isHidden(untradeable), "do not hide untradeables")
		GroundItems.dontHideUntradeables = false
		assertTrue(GroundItems.isHidden(untradeable))
		lists("Logs", "")
		assertFalse(GroundItems.isHidden(cheap), "an explicit highlight beats the value")
	}

	@Test
	fun updateListMovesANameBetweenTheListsAndTogglesIt() {
		lists("Bones", "Vial")
		GroundItems.updateList("bones", true)
		assertEquals("", GroundItems.highlightedCsv, "off the other list")
		assertEquals("Vial,bones", GroundItems.hiddenCsv)
		GroundItems.updateList("BONES", true)
		assertEquals("Vial", GroundItems.hiddenCsv, "toggled off again")
	}

	@Test
	fun theLabelCarriesTheQuantityAndBothPrices() {
		val item: GroundItems.Item = item("Rune bar", 12_345, 12_000, 3_000)
		assertEquals("Rune bar (12.3K) (GE: 148M gp) (HA: 37.0M gp)", GroundItemsOverlay.label(item))
		GroundItems.priceDisplayMode = GroundItems.PRICE_HA
		assertEquals("Rune bar (12.3K) (37.0M gp)", GroundItemsOverlay.label(item))
		val coins: GroundItems.Item = GroundItems.Item(GroundItems.COINS, 0, 0, 0)
		coins.name = "Coins"
		coins.quantity = 500
		coins.gePrice = 1
		coins.haPrice = 1
		assertEquals("Coins (500)", GroundItemsOverlay.label(coins), "no price for coins")
		assertEquals(" - 1.5", GroundItemsOverlay.timerText(1460, true))
		assertEquals(" - 2", GroundItemsOverlay.timerText(1460, false))
	}

	@Test
	fun despawnTimersAreKnownForDropsLootAndTables() {
		val drop: GroundItems.Item = item("Logs", 1, 0, 0)
		drop.spawnCycle = 1000
		assertEquals(-1, GroundItems.despawnCycle(drop, false), "nobody's: no timer")
		drop.lootType = GroundItems.LOOT_DROPPED
		assertEquals(1000 + 3 * 3000, GroundItems.despawnCycle(drop, false))
		drop.lootType = GroundItems.LOOT_PVM
		assertEquals(1000 + 2 * 3000, GroundItems.despawnCycle(drop, false))
		assertEquals(1000 + 30 * 3000, GroundItems.despawnCycle(drop, true), "an instance keeps it 30 minutes")
		assertEquals(0xFF00FF00.toInt(), GroundItems.timerColour(drop, 1500, false), "private the first minute")
		assertEquals(0xFFFFFF00.toInt(), GroundItems.timerColour(drop, 1000 + 3000 + 1, false), "public after it")
		assertNull(GroundItems.timerColour(drop, 1000 + 6001, false), "gone")
	}

	@Test
	fun theMenuNameIsRecolouredWhereItsColourTagIs() {
		assertEquals("<col=aa00ff>Logs", GroundItemsMenu.recolourName("<col=ff9040>Logs", "<col=aa00ff>"))
		assertEquals("Use -> <col=aa00ff>Logs", GroundItemsMenu.recolourName("Use -> <col=ff9040>Logs", "<col=aa00ff>"))
	}

	@Test
	fun aBeamTakesAHighlightFirstThenTheMostValuableItem() {
		val cheap: GroundItems.Item = item("Logs", 1, 50, 2)
		val rich: GroundItems.Item = item("Dragon bones", 1, 2_000_000, 0)
		assertEquals(GroundItems.highValueColour, GroundItemsOverlay.beamColour(listOf(cheap, rich)), "above the High tier")
		assertNull(GroundItemsOverlay.beamColour(listOf(cheap)))
		GroundItems.showLootbeamForHighlighted = true
		lists("Logs", "")
		assertEquals(GroundItems.highlightedColour, GroundItemsOverlay.beamColour(listOf(cheap, rich)))
	}

	// --- the loot attribution, with the client stood in for ---

	private var cycle: Int = 100

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, _ ->
		when (method.name) {
			"sceneBaseX", "sceneBaseY" -> 3200
			"clientCycle" -> cycle
			else -> null
		}
	} as ClientApi

	private fun npc(name: String, tileX: Int, tileY: Int, size: Int, dying: Boolean): Npc {
		return Proxy.newProxyInstance(Npc::class.java.classLoader, arrayOf(Npc::class.java)) { _, method, _ ->
			when (method.name) {
				"name" -> name
				"tileX" -> tileX
				"tileY" -> tileY
				"sizeInTiles" -> size
				"isDying" -> dying
				"level" -> 0
				"combatLevel" -> 10
				"typeId" -> 7
				else -> null
			}
		} as Npc
	}

	@Test
	fun aDyingNpcClaimsTheItemsUnderItInTheSameTick() {
		val kills: ArrayList<LootAttribution.Kill> = ArrayList()
		val loot: LootAttribution = LootAttribution { kills.add(it) }
		// the item comes a few cycles before the npc leaves, and one more on its second tile after
		loot.itemSpawned(client, GroundItem(526, 1, 10, 10, 0))
		cycle += 3
		loot.npcDespawned(client, npc("Giant", 10, 10, 2, true))
		cycle += 2
		loot.itemSpawned(client, GroundItem(995, 30, 11, 11, 0))
		loot.itemSpawned(client, GroundItem(995, 5, 12, 12, 0)) // outside its 2x2
		cycle += LootAttribution.WINDOW
		loot.cycle(cycle)
		assertEquals(1, kills.size)
		assertEquals("Giant", kills[0].name)
		assertEquals(listOf(526, 995), kills[0].items.map { it.itemId })
		assertEquals(3211, kills[0].items[1].worldX)
	}

	@Test
	fun aLivingNpcClaimsNothingAndATileIsClaimedOnce() {
		val kills: ArrayList<LootAttribution.Kill> = ArrayList()
		val loot: LootAttribution = LootAttribution { kills.add(it) }
		loot.npcDespawned(client, npc("Man", 5, 5, 1, false))
		loot.npcDespawned(client, npc("Goblin", 6, 6, 1, true))
		loot.npcDespawned(client, npc("Goblin", 6, 6, 1, true))
		loot.npcDespawned(client, npc("Gargoyle", 8, 8, 1, false))
		loot.itemSpawned(client, GroundItem(526, 1, 5, 5, 0))
		loot.itemSpawned(client, GroundItem(526, 1, 6, 6, 0))
		loot.itemSpawned(client, GroundItem(1, 1, 8, 8, 0))
		val revealed: GroundItem = GroundItem(2, 1, 6, 6, 0)
		revealed.ownership = GroundItem.OWNERSHIP_OTHER
		loot.itemSpawned(client, revealed)
		cycle += LootAttribution.WINDOW
		loot.cycle(cycle)
		assertEquals(listOf("Goblin", "Gargoyle"), kills.map { it.name }, "the man was alive; the second goblin found its tile taken; a gargoyle leaves alive")
		assertEquals(listOf(526), kills[0].items.map { it.itemId }, "a revealed drop is nobody's loot")
	}
}
