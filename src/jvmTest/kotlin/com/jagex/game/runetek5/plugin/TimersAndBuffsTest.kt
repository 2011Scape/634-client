package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.TimersAndBuffs
import com.jagex.game.runetek5.client.features.TimersAndBuffs.TimerType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Timer
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Not part of the original client: Timers and Buffs on the Void server's lines, animations and variables. */
class TimersAndBuffsTest {

	private var cycle: Int = 0

	private var graphic: Int = -1

	private var tileX: Int = 50

	private val varps: HashMap<Int, Int> = HashMap()

	private val me: Player = Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
		when (method.name) {
			"spotAnimationId" -> graphic
			"serverTileX" -> tileX
			"serverTileY" -> 50
			else -> null
		}
	} as Player

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"serverTick" -> cycle
			"localPlayer" -> me
			"varp" -> varps[args[0] as Int] ?: 0
			else -> null
		}
	} as ClientApi

	@AfterTest
	fun reset() {
		TimersAndBuffs.clear()
		TimersAndBuffs.shown.clear()
		TimersAndBuffs.enabled = true
	}

	private fun ticks(type: TimerType): Long = (TimersAndBuffs.active[type] as Timer).durationMillis / TimersAndBuffs.TICK_MILLIS

	private fun tick(n: Int) {
		cycle = n * 30
		TimersAndBuffs.gameTick(client, n)
	}

	@Test
	fun potionsStartFromTheDrinkLine() {
		TimersAndBuffs.chat(client, "You drink the antifire (4).")
		assertEquals(600L, ticks(TimersAndBuffs.ANTIFIRE))
		TimersAndBuffs.chat(client, "You drink the super antifire (3).")
		assertNotNull(TimersAndBuffs.active[TimersAndBuffs.SUPER_ANTIFIRE])
		TimersAndBuffs.chat(client, "Your resistance to dragonfire has run out.")
		assertFalse(TimersAndBuffs.ANTIFIRE in TimersAndBuffs.active || TimersAndBuffs.SUPER_ANTIFIRE in TimersAndBuffs.active)
		TimersAndBuffs.chat(client, "You drink the overload (2).")
		assertEquals(500L, ticks(TimersAndBuffs.OVERLOAD))
		TimersAndBuffs.chat(client, "You drink the juju fishing potion (1).")
		assertNotNull(TimersAndBuffs.active[TimersAndBuffs.JUJU_FISHING])
		// a plain drink starts nothing
		val before: Int = TimersAndBuffs.active.size
		TimersAndBuffs.chat(client, "You drink the cup of tea.")
		assertEquals(before, TimersAndBuffs.active.size)
	}

	@Test
	fun antipoisonFollowsTheVarp() {
		// Void: protection is -(ticks * 2), rising 2 every 30 ticks
		TimersAndBuffs.poison(-12)
		assertEquals(6L * 30L, ticks(TimersAndBuffs.ANTIPOISON))
		TimersAndBuffs.poison(4)
		assertFalse(TimersAndBuffs.ANTIPOISON in TimersAndBuffs.active)
	}

	@Test
	fun freezesAreRefinedByTheGraphicAndEndOnMoving() {
		tick(10)
		TimersAndBuffs.chat(client, "You have been frozen!")
		// no graphic yet: the longest, until the graphic says which
		assertEquals(32L, ticks(TimersAndBuffs.ICE_BARRAGE))
		graphic = 361
		tick(11)
		assertEquals(8L, ticks(TimersAndBuffs.ICE_RUSH))
		assertFalse(TimersAndBuffs.ICE_BARRAGE in TimersAndBuffs.active)
		tick(12)
		assertTrue(TimersAndBuffs.ICE_RUSH in TimersAndBuffs.active)
		tileX = 51
		tick(13)
		assertFalse(TimersAndBuffs.ICE_RUSH in TimersAndBuffs.active)
	}

	@Test
	fun protectFromMagicHalvesAFreeze() {
		varps[1395] = 1 shl 12
		graphic = 179
		tick(20)
		TimersAndBuffs.chat(client, "You have been frozen!")
		assertEquals(12L, ticks(TimersAndBuffs.ENTANGLE))
	}

	@Test
	fun vengeanceAndDeath() {
		TimersAndBuffs.animation(me, TimersAndBuffs.VENGEANCE_ANIMATION)
		assertEquals(50L, ticks(TimersAndBuffs.VENGEANCE))
		assertNotNull(TimersAndBuffs.active[TimersAndBuffs.VENGEANCE_ACTIVE])
		TimersAndBuffs.chat(client, "You drink the antifire (4).")
		TimersAndBuffs.chat(client, "You have been teleblocked.")
		TimersAndBuffs.chat(client, "Oh dear, you are dead!")
		// RuneLite keeps antifire and the vengeance cooldown; teleblock and vengeance active go
		assertTrue(TimersAndBuffs.ANTIFIRE in TimersAndBuffs.active && TimersAndBuffs.VENGEANCE in TimersAndBuffs.active)
		assertFalse(TimersAndBuffs.TELEBLOCK in TimersAndBuffs.active || TimersAndBuffs.VENGEANCE_ACTIVE in TimersAndBuffs.active)
	}

	@Test
	fun chargeOnlyWithNothingTargeted() {
		TimersAndBuffs.animation(me, TimersAndBuffs.CHARGE_ANIMATION)
		assertEquals(252L, ticks(TimersAndBuffs.CHARGE))
	}

	@Test
	fun aTimerSwitchedOffIsNotShown() {
		TimersAndBuffs.shown["showTeleblock"] = false
		TimersAndBuffs.chat(client, "You have been teleblocked.")
		assertFalse(TimersAndBuffs.TELEBLOCK in TimersAndBuffs.active)
		TimersAndBuffs.enabled = false
		TimersAndBuffs.chat(client, "You drink the antifire (4).")
		assertFalse(TimersAndBuffs.ANTIFIRE in TimersAndBuffs.active)
	}

	@Test
	fun theIconsAreItemsOrSprites() {
		TimersAndBuffs.chat(client, "You drink the antifire (4).")
		assertEquals(2452, TimersAndBuffs.active[TimersAndBuffs.ANTIFIRE]!!.itemId)
		TimersAndBuffs.chat(client, "You have been teleblocked.")
		assertEquals(1571, TimersAndBuffs.active[TimersAndBuffs.TELEBLOCK]!!.spriteId)
	}
}
