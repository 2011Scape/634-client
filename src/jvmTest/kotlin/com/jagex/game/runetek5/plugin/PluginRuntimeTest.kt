package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.cache.node.ObjStackEntry
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.AnimationChanged
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GroundItem
import com.jagex.game.runetek5.plugin.api.events.HitsplatApplied
import com.jagex.game.runetek5.plugin.api.events.OverheadTextChanged
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

	/** An actor that is nowhere and does nothing: the hooks only carry it. */
	private class StillActor : Actor {
		override fun x(): Int = 0

		override fun z(): Int = 0

		override fun level(): Int = 0

		override fun sizeInTiles(): Int = 1

		override fun overheadHeight(): Int = 0

		override fun healthRatio(): Int = 0

		override fun isDrawnThisFrame(): Boolean = false

		override fun animationId(): Int = -1

		override fun spotAnimationId(): Int = -1

		override fun interacting(): Actor? = null

		override fun orientation(): Int = 0

		override fun isRunning(): Boolean = false

		override fun overheadText(): String? = null

		override fun hitsplatSlots(): Int = 0

		override fun hitsplatType(slot: Int): Int = -1

		override fun hitsplatDamage(slot: Int): Int = 0

		override fun hitsplatEndCycle(slot: Int): Int = 0
	}

	@Test
	fun theEventRegistryIsInOrder() {
		assertEquals(Events.COUNT, Events.ALL.size)
		for (i in 0 until Events.COUNT) {
			assertEquals(i, Events.ALL[i].id, Events.ALL[i].name)
		}
	}

	@Test
	fun groundItemHooksPostOneEventPerItem() {
		val plugin: PluginRegistration = registration("items")
		val wasStarted: Boolean = PluginRuntime.started
		PluginRuntime.started = true
		val spawned: ArrayList<GroundItem> = ArrayList()
		val despawned: ArrayList<GroundItem> = ArrayList()
		try {
			PluginRuntime.bus.subscribe(Events.ITEM_SPAWNED, EventListener { event -> spawned.add(event) }, plugin)
			PluginRuntime.bus.subscribe(Events.ITEM_DESPAWNED, EventListener { event -> despawned.add(event) }, plugin)
			PluginRuntime.groundItemSpawned(995, 100, 12, 34, 1)
			assertEquals(1, spawned.size)
			assertEquals(995, spawned[0].itemId)
			assertEquals(100, spawned[0].amount)
			assertEquals(12, spawned[0].sceneX)
			assertEquals(34, spawned[0].sceneY)
			assertEquals(1, spawned[0].level)
			val pile: ObjStack = ObjStack()
			pile.groundItems.addLast(ObjStackEntry(1, 1))
			pile.groundItems.addLast(ObjStackEntry(2, 5))
			PluginRuntime.groundPileDespawned(pile, 3, 4, 0)
			assertEquals(2, despawned.size)
			assertEquals(1, despawned[0].itemId)
			assertEquals(5, despawned[1].amount)
			assertEquals(3, despawned[1].sceneX)
			PluginRuntime.started = false
			PluginRuntime.groundItemSpawned(995, 1, 0, 0, 0)
			assertEquals(1, spawned.size, "nothing before the plugins start")
		} finally {
			PluginRuntime.started = wasStarted
			PluginRuntime.release(plugin)
		}
	}

	@Test
	fun actorHooksCarryTheActorAndSkipWhatIsNotThere() {
		val plugin: PluginRegistration = registration("actors")
		val wasStarted: Boolean = PluginRuntime.started
		PluginRuntime.started = true
		val actor: Actor = StillActor()
		val hits: ArrayList<HitsplatApplied> = ArrayList()
		val animations: ArrayList<AnimationChanged> = ArrayList()
		val texts: ArrayList<OverheadTextChanged> = ArrayList()
		val targets: ArrayList<Actor> = ArrayList()
		try {
			PluginRuntime.bus.subscribe(Events.HITSPLAT_APPLIED, EventListener { event -> hits.add(event) }, plugin)
			PluginRuntime.bus.subscribe(Events.ANIMATION_CHANGED, EventListener { event -> animations.add(event) }, plugin)
			PluginRuntime.bus.subscribe(Events.OVERHEAD_TEXT_CHANGED, EventListener { event -> texts.add(event) }, plugin)
			PluginRuntime.bus.subscribe(Events.INTERACTING_CHANGED, EventListener { event -> targets.add(event) }, plugin)
			PluginRuntime.hitsplatApplied(actor, 3, 12, -1, 0, 20)
			assertEquals(1, hits.size)
			assertTrue(hits[0].actor === actor)
			assertEquals(12, hits[0].damage)
			assertEquals(20, hits[0].delay)
			PluginRuntime.animationChanged(actor, intArrayOf(422, 423, 424, 425))
			PluginRuntime.animationChanged(actor, null)
			PluginRuntime.animationChanged(null, intArrayOf(1))
			assertEquals(2, animations.size)
			assertEquals(422, animations[0].animationId, "the standing animation stands for the set")
			assertEquals(-1, animations[1].animationId)
			PluginRuntime.overheadTextChanged(actor, "Hello")
			PluginRuntime.overheadTextChanged(actor, null)
			assertEquals(1, texts.size)
			assertEquals("Hello", texts[0].text)
			PluginRuntime.interactingChanged(actor)
			PluginRuntime.interactingChanged(null)
			assertEquals(1, targets.size)
			assertTrue(targets[0] === actor)
		} finally {
			PluginRuntime.started = wasStarted
			PluginRuntime.release(plugin)
		}
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
