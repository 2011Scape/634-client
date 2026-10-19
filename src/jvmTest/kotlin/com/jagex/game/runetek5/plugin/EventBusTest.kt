package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

/** Not part of the original client: the plugins' event bus (EventBusImpl). */
class EventBusTest {

	private fun registration(id: String): PluginRegistration {
		return PluginRegistration(PluginDescriptor(id, id, "1", 1, "", ""), null, false, "test")
	}

	@Test
	fun listenersRunInOrderAndCanUnsubscribe() {
		val bus: EventBusImpl = EventBusImpl()
		val seen: StringBuilder = StringBuilder()
		val first: EventListener<StatChanged> = EventListener { seen.append("a").append(it.skill) }
		val second: EventListener<StatChanged> = EventListener { seen.append("b").append(it.skill) }
		bus.subscribe(Events.STAT_CHANGED, first)
		bus.subscribe(Events.STAT_CHANGED, second)
		bus.post(Events.STAT_CHANGED, StatChanged(3, 0, 10, 1))
		assertEquals("a3b3", seen.toString())
		bus.unsubscribe(Events.STAT_CHANGED, first)
		bus.post(Events.STAT_CHANGED, StatChanged(4, 0, 10, 1))
		assertEquals("a3b3b4", seen.toString())
		assertEquals(1, bus.listenerCount(Events.STAT_CHANGED))
		bus.post(Events.VARP_CHANGED, com.jagex.game.runetek5.plugin.api.events.VarpChanged(1, 2))
		assertEquals("a3b3b4", seen.toString(), "no listener for another type")
	}

	@Test
	fun throwingListenerIsReportedAndTheOthersStillRun() {
		val bus: EventBusImpl = EventBusImpl()
		val bad: PluginRegistration = registration("bad")
		val good: PluginRegistration = registration("good")
		var failedOwner: PluginRegistration? = null
		var failure: Throwable? = null
		bus.onFailure = EventBusImpl.FailureHandler { owner, error ->
			failedOwner = owner
			failure = error
			bus.removeOwner(owner!!)
		}
		var goodRuns: Int = 0
		bus.subscribe(Events.GAME_TICK, EventListener { throw IllegalStateException("boom") }, bad)
		bus.subscribe(Events.GAME_TICK, EventListener { goodRuns++ }, good)
		val tick: com.jagex.game.runetek5.plugin.api.events.GameTick = com.jagex.game.runetek5.plugin.api.events.GameTick()
		bus.post(Events.GAME_TICK, tick)
		assertSame(bad, failedOwner)
		assertEquals("boom", failure!!.message)
		assertEquals(1, goodRuns)
		// the bad listener was removed during its own dispatch; the good one runs alone from now on
		assertEquals(1, bus.listenerCount(Events.GAME_TICK))
		failedOwner = null
		bus.post(Events.GAME_TICK, tick)
		assertNull(failedOwner)
		assertEquals(2, goodRuns)
	}

	@Test
	fun unsubscribingDuringAPostIsSafe() {
		val bus: EventBusImpl = EventBusImpl()
		var runs: Int = 0
		val listeners: Array<EventListener<StatChanged>?> = arrayOfNulls(3)
		for (i in 0 until 3) {
			listeners[i] = EventListener {
				runs++
				// the first listener drops the second while the post is running
				if (i == 0) {
					bus.unsubscribe(Events.STAT_CHANGED, listeners[1]!!)
				}
			}
			bus.subscribe(Events.STAT_CHANGED, listeners[i]!!)
		}
		bus.post(Events.STAT_CHANGED, StatChanged(0, 0, 1, 1))
		assertEquals(2, runs, "the first and the third ran; the second was gone")
		assertEquals(2, bus.listenerCount(Events.STAT_CHANGED))
		bus.post(Events.STAT_CHANGED, StatChanged(0, 0, 1, 1))
		assertEquals(4, runs)
	}

	@Test
	fun removingAnOwnerDropsAllOfItsListeners() {
		val bus: EventBusImpl = EventBusImpl()
		val owner: PluginRegistration = registration("mine")
		val other: PluginRegistration = registration("other")
		bus.subscribe(Events.STAT_CHANGED, EventListener { }, owner)
		bus.subscribe(Events.VARP_CHANGED, EventListener { }, owner)
		bus.subscribe(Events.STAT_CHANGED, EventListener { }, other)
		bus.removeOwner(owner)
		assertEquals(1, bus.listenerCount(Events.STAT_CHANGED))
		assertEquals(0, bus.listenerCount(Events.VARP_CHANGED))
	}

	@Test
	fun theListenerArraysGrow() {
		val bus: EventBusImpl = EventBusImpl()
		var runs: Int = 0
		for (i in 0 until 40) {
			bus.subscribe(Events.CLIENT_TICK, EventListener { runs++ })
		}
		bus.post(Events.CLIENT_TICK, com.jagex.game.runetek5.plugin.api.events.ClientTick())
		assertEquals(40, runs)
		assertEquals(40, bus.listenerCount(Events.CLIENT_TICK))
	}
}
