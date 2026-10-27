package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.menu.ColoredMiniMenuEntry
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.graphics.MiniMenuReset
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GroundItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: what API 9 added (PluginApi.VERSION), as far as it runs without the game. */
class PluginApiNineTest {

	private fun registration(id: String): PluginRegistration {
		return PluginRegistration(PluginDescriptor(id, id, "1", PluginApi.VERSION, "", ""), null, false, "test")
	}

	@Test
	fun thePriceDocumentIsReadAndOddEntriesAreSkipped() {
		val prices: HashMap<Int, Int> = ItemPrices.parse("{\"4151\": 1500000, \"995\":1, \"abc\": 5, \"11694\": 99999999999, \"2\": \"x\"}")!!
		assertEquals(1500000, prices[4151])
		assertEquals(1, prices[995])
		assertEquals(Int.MAX_VALUE, prices[11694], "held to an int")
		assertNull(prices[2])
		assertEquals(3, prices.size)
		assertNull(ItemPrices.parse("not json"))
	}

	@Test
	fun coloursRoundTripThroughTheConfig() {
		assertEquals(0xFFAA00FF.toInt(), PluginConfigImpl.parseColour("#AA00FF"), "six digits are opaque")
		assertEquals(0x80112233.toInt(), PluginConfigImpl.parseColour("80112233"))
		assertNull(PluginConfigImpl.parseColour("#12345"))
		assertNull(PluginConfigImpl.parseColour("#GG0000"))
		assertEquals("#80112233", PluginConfigImpl.formatColour(0x80112233.toInt()))
		val config: PluginConfigImpl = PluginConfigImpl("colour-test")
		config.setColour("c", 0x40FF0000)
		assertEquals(0x40FF0000, config.getColour("c", 0))
		config.set("bad", "red")
		assertEquals(7, config.getColour("bad", 7))
	}

	@Test
	fun keysHaveNames() {
		assertEquals("Alt", KeyCode.name(KeyCode.ALT))
		assertEquals("None", KeyCode.name(0))
		assertEquals("F5", KeyCode.name(5))
		assertEquals("A", KeyCode.name(48))
		assertEquals("Key 250", KeyCode.name(250))
	}

	@Test
	fun colourAndKeybindSettingsAreDeclared() {
		val schema: PluginSchemaImpl = PluginSchemaImpl()
		schema.colour("tint", "Tint", 0x20FF0000, false)
		schema.colour("fill", "Fill", 0x20FF0000, true)
		schema.keybind("hotkey", "Hotkey", KeyCode.ALT)
		assertEquals(PluginSchemaImpl.COLOUR, schema.entries[0].kind)
		assertEquals(0xFFFF0000.toInt(), schema.entries[0].defaultInt, "no alpha of the player's: opaque")
		assertEquals(0x20FF0000, schema.entries[1].defaultInt)
		assertEquals(1, schema.entries[1].max)
		assertEquals(PluginSchemaImpl.KEYBIND, schema.entries[2].kind)
		assertEquals(KeyCode.ALT, schema.entries[2].defaultInt)
	}

	@Test
	fun aRevealedItemIsAnotherPlayers() {
		val plugin: PluginRegistration = registration("reveal")
		val wasStarted: Boolean = PluginRuntime.started
		PluginRuntime.started = true
		val seen: ArrayList<GroundItem> = ArrayList()
		try {
			PluginRuntime.bus.subscribe(Events.ITEM_SPAWNED, EventListener { event -> seen.add(event) }, plugin)
			PluginRuntime.groundItemSpawned(995, 1, 1, 1, 0)
			PluginRuntime.groundItemSpawned(995, 1, 1, 1, 0, GroundItem.OWNERSHIP_OTHER)
			assertEquals(GroundItem.OWNERSHIP_NONE, seen[0].ownership)
			assertEquals(GroundItem.OWNERSHIP_OTHER, seen[1].ownership)
		} finally {
			PluginRuntime.started = wasStarted
			PluginRuntime.release(plugin)
		}
	}

	@Test
	fun aDeprioritizedEntrySinksToTheBottom() {
		val plugin: PluginRegistration = registration("sink")
		val wasStarted: Boolean = PluginRuntime.started
		PluginRuntime.started = true
		val wasOpen: Boolean = MiniMenu.isOpen
		MiniMenu.isOpen = false
		val walk: MiniMenuEntry = MiniMenuEntry("Walk here", "", -1, MiniMenuAction.WALK, -1, 0L, 0, 0, false, false, 0L, true)
		val take: MiniMenuEntry = MiniMenuEntry("Take", "", -1, MiniMenuAction.WALK, -1, 0L, 0, 0, false, false, 0L, true)
		try {
			MiniMenu.addEntry(walk)
			MiniMenu.addEntry(take)
			PluginRuntime.bus.subscribe(Events.MENU_BUILT, EventListener {
				assertEquals("Take", PluginRuntime.menuEntry(0)?.option())
				assertTrue(PluginRuntime.deprioritizeMenuEntry(0))
				assertEquals("Walk here", PluginRuntime.menuEntry(0)?.option())
				assertEquals("Take", PluginRuntime.menuEntry(1)?.option())
			}, plugin)
			assertTrue(PluginRuntime.menuBuilt())
			assertTrue(walk === MiniMenu.entryList.sentinel.previous, "Walk here is the top row and the left click")
			assertEquals(MiniMenuAction.WALK + 2000, take.clickType, "the game's below flag")
		} finally {
			PluginRuntime.started = wasStarted
			PluginRuntime.release(plugin)
			MiniMenu.reset()
			MiniMenu.isOpen = wasOpen
		}
	}

	@Test
	fun aSubmenuHoldsItsEntriesInOrderAndTheirClicksReachTheAction() {
		val plugin: PluginRegistration = registration("submenu")
		val wasStarted: Boolean = PluginRuntime.started
		PluginRuntime.started = true
		val wasOpen: Boolean = MiniMenu.isOpen
		MiniMenu.isOpen = false
		var clicked: Int = -1
		try {
			val slot: Int = PluginRuntime.registerMenuAction(plugin, MenuAction { _, x, _, _ -> clicked = x })
			assertEquals(-1, PluginRuntime.addSubmenu("Outside a build"))
			PluginRuntime.bus.subscribe(Events.MENU_ENTRY_ADDED, EventListener {
				val submenu: Int = PluginRuntime.addSubmenu("Color")
				assertEquals(0, submenu)
				PluginRuntime.addSubmenuEntry(submenu, "Red", slot)
				PluginRuntime.addSubmenuEntry(submenu, "Green", slot)
				PluginRuntime.addSubmenuEntry(submenu, "Blue", slot)
			}, plugin)
			PluginRuntime.menuTarget(MenuTarget.TILE, 9, 4, 0, 0, 0L)
			val parent: MiniMenuEntry = MiniMenu.entryList.sentinel.previous as MiniMenuEntry
			assertEquals(MiniMenuAction.PLUGIN_SUBMENU, parent.clickType)
			assertEquals("Color", parent.opBase)
			val submenu: ColoredMiniMenuEntry = assertNotNull(PluginRuntime.submenuOf(parent))
			assertEquals(3, submenu.queueSize)
			val names: ArrayList<String?> = ArrayList()
			var entry: MiniMenuEntry? = submenu.nodeQueue.first() as MiniMenuEntry?
			while (entry != null) {
				names.add(entry.opBase)
				entry = submenu.nodeQueue.next() as MiniMenuEntry?
			}
			assertEquals(listOf<String?>("Red", "Green", "Blue"), names, "top to bottom in the order added")
			val green: MiniMenuEntry = (submenu.nodeQueue.first() as MiniMenuEntry).let { submenu.nodeQueue.next() as MiniMenuEntry }
			assertTrue(PluginRuntime.menuClicked(green, green.clickType))
			assertEquals(9, clicked, "the tile's x, as a main menu entry gives it")
			MiniMenu.reset()
			assertNull(PluginRuntime.submenuOf(parent), "gone with the menu")
			// the menu is built again every client cycle while it is closed: the submenu must be there each time
			repeat(40) {
				MiniMenuReset.resetEntries()
				PluginRuntime.menuTarget(MenuTarget.TILE, 9, 4, 0, 0, 0L)
			}
			assertNotNull(PluginRuntime.submenuOf(MiniMenu.entryList.sentinel.previous as MiniMenuEntry), "still there after 40 builds")
		} finally {
			PluginRuntime.started = wasStarted
			PluginRuntime.release(plugin)
			MiniMenu.reset()
			MiniMenu.isOpen = wasOpen
		}
	}
}
