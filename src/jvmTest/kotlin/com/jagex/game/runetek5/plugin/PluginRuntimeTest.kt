package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Not part of the original client: the runtime's registries, as far as they can be tried without
 * the game (PluginRuntime). The runtime is one object, so each test takes back what it registered.
 */
class PluginRuntimeTest {

	private fun registration(id: String, builtIn: Boolean = false): PluginRegistration {
		return PluginRegistration(PluginDescriptor(id, id, "1", 1, "", ""), null, builtIn, "test")
	}

	@Test
	fun aPluginCannotTakeTheClientsCommandsButABuiltInCan() {
		val plugin: PluginRegistration = registration("mine")
		val builtIn: PluginRegistration = registration("ours", true)
		val handler: CommandHandler = CommandHandler { _, _ -> }
		try {
			assertFalse(PluginRuntime.registerCommand(plugin, "plugins", handler))
			assertFalse(PluginRuntime.registerCommand(plugin, "Tk6", handler))
			assertFalse(PluginRuntime.registerCommand(plugin, "two words", handler))
			assertFalse(PluginRuntime.registerCommand(plugin, "", handler))
			assertTrue(PluginRuntime.registerCommand(plugin, "Mine", handler))
			assertFalse(PluginRuntime.registerCommand(builtIn, "mine", handler), "taken, whatever the case")
			assertTrue(PluginRuntime.registerCommand(builtIn, "renderer", handler), "a built-in may shadow a client command")
		} finally {
			PluginRuntime.bus.removeOwner(plugin)
			PluginRuntime.release(plugin)
			PluginRuntime.release(builtIn)
		}
	}

	@Test
	fun aRegisteredCommandRunsWithItsArgumentsAndTheEventTakesTheRest() {
		val plugin: PluginRegistration = registration("echo")
		val wasStarted: Boolean = PluginRuntime.started
		PluginRuntime.started = true
		var got: String? = null
		var eventLine: String? = null
		try {
			assertTrue(PluginRuntime.registerCommand(plugin, "echo", CommandHandler { command, args -> got = command + ":" + args.joinToString(",") }))
			assertTrue(PluginRuntime.consoleCommand("  Echo  one two "))
			assertEquals("Echo:one,two", got)
			PluginRuntime.bus.subscribe(Events.CONSOLE_COMMAND, EventListener { event ->
				eventLine = event.line
				if (event.line.startsWith("take")) {
					event.consume()
				}
			}, plugin)
			assertTrue(PluginRuntime.consoleCommand("take this"))
			assertEquals("take this", eventLine)
			assertFalse(PluginRuntime.consoleCommand("leave this"), "not consumed: the client's own commands get it")
			assertEquals("leave this", eventLine)
			assertFalse(PluginRuntime.consoleCommand("   "))
		} finally {
			PluginRuntime.started = wasStarted
			PluginRuntime.release(plugin)
		}
	}

	@Test
	fun aClosedContextRegistersNothing() {
		val plugin: PluginRegistration = registration("late")
		val other: PluginRegistration = registration("other")
		val context: PluginContextImpl = PluginContextImpl(plugin)
		val listener: EventListener<com.jagex.game.runetek5.plugin.api.events.GameTick> = EventListener { }
		val before: Int = PluginRuntime.bus.listenerCount(Events.GAME_TICK)
		try {
			assertTrue(context.registerCommand("late", CommandHandler { _, _ -> }))
			plugin.context = context
			PluginRuntime.release(plugin)
			assertTrue(context.closed, "stopping the plugin closes its context")
			assertFalse(context.registerCommand("late", CommandHandler { _, _ -> }))
			assertEquals(-1, context.registerMenuAction(MenuAction { _, _, _, _ -> }))
			context.events().subscribe(Events.GAME_TICK, listener)
			context.addOverlay { _, _ -> }
			assertEquals(before, PluginRuntime.bus.listenerCount(Events.GAME_TICK))
			assertTrue(PluginRuntime.registerCommand(other, "late", CommandHandler { _, _ -> }), "the name was given back")
		} finally {
			PluginRuntime.bus.unsubscribe(Events.GAME_TICK, listener)
			PluginRuntime.release(plugin)
			PluginRuntime.release(other)
		}
	}

	@Test
	fun aClickOnAnEntryFromBeforeThePluginStoppedGoesNowhere() {
		val first: PluginRegistration = registration("first")
		val second: PluginRegistration = registration("second")
		val wasStarted: Boolean = PluginRuntime.started
		PluginRuntime.started = true
		var firstClicks: Int = 0
		var secondClicks: Int = 0
		var secondTarget: MenuTarget? = null
		var secondX: Int = -1
		try {
			val slot: Int = PluginRuntime.registerMenuAction(first, MenuAction { _, _, _, _ -> firstClicks++ })
			val stale: MiniMenuEntry = MiniMenuEntry("Hello", "", -1, slot, -1, 12L, PluginRuntime.menuParam0(5, MenuTarget.NPC.kind, slot), 7, false, true, 0L, false)
			assertTrue(PluginRuntime.menuClicked(stale, slot))
			assertEquals(1, firstClicks)
			PluginRuntime.release(first)
			assertEquals(slot, PluginRuntime.registerMenuAction(second, MenuAction { target, x, _, _ ->
				secondClicks++
				secondTarget = target
				secondX = x
			}), "the slot is reused")
			assertTrue(PluginRuntime.menuClicked(stale, slot), "handled: nobody's")
			assertEquals(1, firstClicks)
			assertEquals(0, secondClicks)
			val fresh: MiniMenuEntry = MiniMenuEntry("Hello", "", -1, slot, -1, 12L, PluginRuntime.menuParam0(5, MenuTarget.NPC.kind, slot), 7, false, true, 0L, false)
			assertTrue(PluginRuntime.menuClicked(fresh, slot))
			assertEquals(1, secondClicks)
			assertEquals(MenuTarget.NPC, secondTarget)
			assertEquals(5, secondX)
		} finally {
			PluginRuntime.started = wasStarted
			PluginRuntime.release(first)
			PluginRuntime.release(second)
		}
	}

	@Test
	fun menuActionsGetTheirOwnClickTypes() {
		val plugin: PluginRegistration = registration("menu")
		val action: MenuAction = MenuAction { _, _, _, _ -> }
		try {
			val first: Int = PluginRuntime.registerMenuAction(plugin, action)
			val second: Int = PluginRuntime.registerMenuAction(plugin, action)
			assertTrue(first >= MiniMenuAction.PLUGIN_FIRST && first <= MiniMenuAction.PLUGIN_LAST)
			assertEquals(first + 1, second)
		} finally {
			PluginRuntime.release(plugin)
		}
		assertEquals(MiniMenuAction.PLUGIN_FIRST, PluginRuntime.registerMenuAction(plugin, action), "freed when the plugin stopped")
		PluginRuntime.release(plugin)
	}
}
