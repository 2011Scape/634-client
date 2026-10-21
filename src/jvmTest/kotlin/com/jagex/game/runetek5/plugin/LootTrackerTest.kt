package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.LootTracker
import com.jagex.game.runetek5.client.features.LootTrackerData
import com.jagex.game.runetek5.client.features.LootTrackerEvents
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Not part of the original client: the Loot Tracker's records, RuneLite's saved loot, and the loot of events (LootTracker, LootTrackerData, LootTrackerEvents). */
class LootTrackerTest {

	@AfterTest
	fun tidy() {
		LootTracker.sessionRecords.clear()
		LootTracker.aggregateRecords.clear()
		LootTrackerEvents.reset()
	}

	@Test
	fun runeLitesSavedLootReadsAndWritesBack() {
		val json: String = """{"type":"NPC","name":"Goblin","kills":3,"first":{"seconds":1700000000,"nanos":5},"last":{"seconds":1700000500,"nanos":0},"drops":[526,3,995,47]}"""
		val loot: LootTrackerData.ConfigLoot = assertNotNull(LootTrackerData.parse(json))
		assertEquals("NPC", loot.type)
		assertEquals("Goblin", loot.name)
		assertEquals(3, loot.kills)
		assertEquals(1700000500L, loot.lastSeconds)
		assertEquals(2, loot.numDrops())
		assertEquals(json, LootTrackerData.format(loot))
		loot.add(995, Int.MAX_VALUE)
		assertEquals(Int.MAX_VALUE, loot.drops[3], "held to the largest int")
		loot.add(1, 2)
		assertEquals(3, loot.numDrops())
		assertNotNull(LootTrackerData.parse("""{ "drops":[1,2], "name":"A \"b\"", "extra":{"x":[1]}, "type":"EVENT" }"""), "any order, unknown fields skipped")
		assertNull(LootTrackerData.parse("[]"))
		assertEquals("drops_PICKPOCKET_Man", LootTrackerData.key(LootTrackerData.PICKPOCKET, "Man"))
	}

	@Test
	fun lootStacksByItemAndASourceAddsUp() {
		val stacked: List<IntArray> = LootTracker.stack(listOf(intArrayOf(995, 10), intArrayOf(526, 1), intArrayOf(995, 5), intArrayOf(-1, 1)))
		assertEquals(listOf(995 to 15, 526 to 1), stacked.map { it[0] to it[1] })
		LootTracker.addLoot(client, "Goblin", 2, LootTrackerData.NPC, listOf(intArrayOf(995, 10)))
		LootTracker.addLoot(client, "Goblin", 2, LootTrackerData.NPC, listOf(intArrayOf(995, 5), intArrayOf(526, 1)))
		LootTracker.addLoot(client, "Man", 2, LootTrackerData.PICKPOCKET, listOf(intArrayOf(995, 3)))
		assertEquals(3, LootTracker.sessionRecords.size)
		assertEquals("(lvl-2)", LootTracker.sessionRecords[0].subTitle)
		assertEquals(2, LootTracker.aggregateRecords.size)
		val goblin: LootTrackerData.Record = LootTracker.aggregateRecords.values.first()
		assertEquals(2, goblin.kills)
		assertEquals(15, goblin.items.first { it.id == 995 }.quantity)
		assertEquals("Man", LootTracker.aggregateRecords.values.last().title, "the newest source last")
	}

	// --- the client stood in for ---

	private var cycle: Int = 1000

	private var backpack: IntArray = intArrayOf(1, 1) // id, amount pairs

	private val container: ItemContainer = Proxy.newProxyInstance(ItemContainer::class.java.classLoader, arrayOf(ItemContainer::class.java)) { _, method, args ->
		when (method.name) {
			"size" -> backpack.size / 2
			"itemId" -> backpack[(args[0] as Int) * 2]
			"amount" -> backpack[(args[0] as Int) * 2 + 1]
			else -> null
		}
	} as ItemContainer

	private val player: Player = Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
		when (method.name) {
			"tileX", "tileY", "level" -> 0
			"name" -> "smoketest"
			else -> null
		}
	} as Player

	private val item: ItemDefinition = Proxy.newProxyInstance(ItemDefinition::class.java.classLoader, arrayOf(ItemDefinition::class.java)) { _, method, _ ->
		when (method.name) {
			"name" -> "Thing"
			else -> null
		}
	} as ItemDefinition

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"clientCycle" -> cycle
			"itemContainer" -> container
			"localPlayer" -> player
			"item" -> item
			"itemName" -> if (args[0] == 405) "Casket" else "Thing"
			"itemPrice" -> 10
			"itemHaPrice" -> 6
			"regionId" -> 12850
			"sceneBaseX", "sceneBaseY" -> 0
			else -> null
		}
	} as ClientApi

	/** Runs the events' code as the plugin's listeners would, through a registration of their own. */
	private fun events(block: (PluginRegistration) -> Unit) {
		val registration: PluginRegistration = PluginRegistration(PluginDescriptor("loot-events", "loot-events", "1", PluginApi.VERSION, "", ""), null, true, "test")
		val context: PluginContextImpl = PluginContextImpl(registration)
		val wasStarted: Boolean = PluginRuntime.started
		PluginRuntime.started = true
		val fakeContext = java.lang.reflect.Proxy.newProxyInstance(com.jagex.game.runetek5.plugin.api.PluginContext::class.java.classLoader, arrayOf(com.jagex.game.runetek5.plugin.api.PluginContext::class.java)) { _, method, args ->
			when (method.name) {
				"client" -> client
				else -> if (args == null) method.invoke(context) else method.invoke(context, *args)
			}
		} as com.jagex.game.runetek5.plugin.api.PluginContext
		try {
			LootTrackerEvents.subscribe(fakeContext)
			block(registration)
		} finally {
			PluginRuntime.started = wasStarted
			PluginRuntime.release(registration)
		}
	}

	private fun changeBackpack(items: IntArray) {
		backpack = items
		PluginRuntime.bus.post(Events.ITEM_CONTAINER_CHANGED, ItemContainerChanged(LootTrackerEvents.BACKPACK))
	}

	private fun settle() {
		for (i in 0 until 40) {
			cycle++
			LootTrackerEvents.cycle(client)
		}
	}

	@Test
	fun aPickpocketWhoseLootCameBeforeTheMessageIsStillCounted() {
		events {
			cycle += 100
			changeBackpack(intArrayOf(1, 1, 995, 9)) // the server's items, then its chat line in the same tick
			cycle += 2
			PluginRuntime.bus.post(Events.CHAT_MESSAGE, ChatMessage(ChatType.GAME_FILTERED, null, "You pick the Man's pocket.", null))
			settle()
		}
		val record: LootTrackerData.Record = assertNotNull(LootTracker.sessionRecords.lastOrNull())
		assertEquals("Man", record.title)
		assertEquals(LootTrackerData.PICKPOCKET, record.type)
		assertEquals(listOf(995 to 9), record.items.map { it.id to it.quantity })
	}

	@Test
	fun aCasketsLootIsWhatTheBackpackGainsAfterTheClick() {
		events {
			cycle += 100
			val click = com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked()
			click.option = "Open"
			click.itemId = 405
			PluginRuntime.bus.post(Events.MENU_OPTION_CLICKED, click)
			settle() // nothing yet: the casket is still there
			cycle += 5
			changeBackpack(intArrayOf(1, 1, 1617, 1))
			settle()
		}
		val record: LootTrackerData.Record = assertNotNull(LootTracker.sessionRecords.lastOrNull())
		assertEquals("Casket", record.title)
		assertEquals(listOf(1617 to 1), record.items.map { it.id to it.quantity })
	}
}
