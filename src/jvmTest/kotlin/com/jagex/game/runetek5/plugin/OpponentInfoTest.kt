package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.HiscoreLookup
import com.jagex.game.runetek5.client.features.NpcHitpoints
import com.jagex.game.runetek5.client.features.OpponentInfo
import com.jagex.game.runetek5.client.features.OpponentInfoOverlay
import com.jagex.game.runetek5.client.features.PlayerComparisonOverlay
import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Skill
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

/** Not part of the original client: Opponent Information against RuneLite's rules (OpponentInfoPlugin, OpponentInfoOverlay, PlayerComparisonOverlay). */
class OpponentInfoTest {

	private var now: Long = 100_000L

	/** What each actor deals with, by actor. */
	private val interacting: HashMap<Any, Actor?> = HashMap()

	private val me: Player = actor(Player::class.java, "Me") as Player

	private val goblin: Npc = actor(Npc::class.java, "Goblin") as Npc

	private val guard: Npc = actor(Npc::class.java, "Guard") as Npc

	private val other: Player = actor(Player::class.java, "Other") as Player

	/** The menu: option, target, click type and npc index of each entry; the targets as retargeted. */
	private val menu: ArrayList<Array<Any>> = ArrayList()

	private val npcs: HashMap<Int, Npc> = hashMapOf(1 to goblin, 2 to guard)

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"localPlayer" -> me
			"npcByIndex" -> npcs[args[0] as Int]
			"menuEntryCount" -> menu.size
			"menuEntry" -> entry(args[0] as Int)
			"retargetMenuEntry" -> {
				menu[args[0] as Int][1] = args[1] as String
				true
			}
			else -> null
		}
	} as ClientApi

	private fun actor(type: Class<*>, name: String): Any {
		return Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { proxy, method, args ->
			when (method.name) {
				"name" -> name
				"interacting" -> interacting[proxy]
				"hashCode" -> System.identityHashCode(proxy)
				"equals" -> proxy === args[0]
				"toString" -> name
				else -> null
			}
		}
	}

	private fun entry(i: Int): MenuEntry {
		val row: Array<Any> = menu[i]
		return Proxy.newProxyInstance(MenuEntry::class.java.classLoader, arrayOf(MenuEntry::class.java)) { _, method, _ ->
			when (method.name) {
				"option" -> row[0]
				"target" -> row[1]
				"clickType" -> row[2]
				"entityUid" -> (row[3] as Int).toLong()
				else -> null
			}
		} as MenuEntry
	}

	private fun interact(source: Actor, target: Actor?) {
		interacting[source] = target
		OpponentInfo.interactingChanged(client, source)
	}

	init {
		OpponentInfo.clock = { now }
	}

	@AfterTest
	fun tidy() {
		OpponentInfo.reset()
		OpponentInfo.enabled = true
		OpponentInfo.showOpponentsInMenu = false
		OpponentInfo.clock = { lang.currentTimeMillis() }
		HiscoreLookup.clear()
	}

	@Test
	fun lifepointsFromTheBarAsRuneLiteWorksThemOut() {
		// RuneLite's own example: 99 at most, a bar of 30 at 15
		assertEquals(50, OpponentInfoOverlay.healthFromRatio(15, 30, 99))
		assertEquals(0, OpponentInfoOverlay.healthFromRatio(0, 30, 99))
		assertEquals(99, OpponentInfoOverlay.healthFromRatio(30, 30, 99))
		// 634's bar: 255 parts. A man (70) at half: 1 + 254 * 35 / 70 = 128
		assertEquals(35, OpponentInfoOverlay.healthFromRatio(128, 255, 70))
		assertEquals(70, OpponentInfoOverlay.healthFromRatio(255, 255, 70))
		// the thinnest bar of 990 is 1 to 3 lifepoints: the middle, rounded up
		assertEquals(2, OpponentInfoOverlay.healthFromRatio(1, 255, 990))
		// round trip over a big npc's every lifepoint
		for (hp in 1..2550) {
			val ratio: Int = 1 + 254 * hp / 2550
			val back: Int = OpponentInfoOverlay.healthFromRatio(ratio, 255, 2550)
			assert(kotlin.math.abs(back - hp) <= 6) { "$hp came back as $back" }
		}
	}

	@Test
	fun labelsAsRuneLitesProgressBar() {
		assertEquals("50/99", OpponentInfoOverlay.fullLabel(50.0, 99))
		assertEquals("50.0%", OpponentInfoOverlay.percentLabel(0.5))
		assertEquals("50/99 (50.5%)", OpponentInfoOverlay.bothLabel(50.0, 99))
		// DecimalFormat's half to even: 12.25 to 12.2, 12.35 to 12.4
		assertEquals("12.2%", OpponentInfoOverlay.percentLabel(0.1225))
		assertEquals("100.0%", OpponentInfoOverlay.percentLabel(1.0))
	}

	@Test
	fun onlyYourOwnDealingsMakeAnOpponent() {
		// being attacked does not
		interact(goblin, me)
		assertNull(OpponentInfo.lastOpponent)
		interact(other, me)
		assertNull(OpponentInfo.lastOpponent)
		// attacking does, and so does following or trading a player
		interact(me, goblin)
		assertSame(goblin, OpponentInfo.lastOpponent)
		interact(me, other)
		assertSame(other, OpponentInfo.lastOpponent)
	}

	@Test
	fun theOpponentGoesFiveSecondsAfterYouStop() {
		interact(me, goblin)
		interact(me, null)
		assertSame(goblin, OpponentInfo.lastOpponent, "stopping keeps it")
		now += 5_000L
		OpponentInfo.tick(client)
		assertSame(goblin, OpponentInfo.lastOpponent, "five seconds is not more than five")
		now += 1L
		OpponentInfo.tick(client)
		assertNull(OpponentInfo.lastOpponent)
	}

	@Test
	fun notWhileYouDealWithSomethingElse() {
		interact(me, goblin)
		interact(me, null)
		interacting[me] = guard // facing the guard without the change seen yet
		now += 10_000L
		OpponentInfo.tick(client)
		assertSame(goblin, OpponentInfo.lastOpponent)
	}

	@Test
	fun attackEntriesMarkedForOpponentsAndAttackers() {
		OpponentInfo.showOpponentsInMenu = true
		interact(me, goblin)
		interacting[guard] = me
		menu.add(arrayOf("Attack", "<col=ffff00>Goblin", 20, 1))
		menu.add(arrayOf("Attack", "<col=ffff00>Guard", 20, 2))
		menu.add(arrayOf("Examine", "<col=ffff00>Goblin", 1008, 1))
		OpponentInfo.markOpponents(client)
		assertEquals("*<col=ffff00>Goblin", menu[0][1])
		assertEquals("*<col=ffff00>Guard", menu[1][1])
		assertEquals("<col=ffff00>Goblin", menu[2][1], "only Attack")
	}

	@Test
	fun noMarksWhenTheSettingIsOff() {
		interact(me, goblin)
		menu.add(arrayOf("Attack", "Goblin", 20, 1))
		OpponentInfo.markOpponents(client)
		assertEquals("Goblin", menu[0][1])
	}

	@Test
	fun comparisonColours() {
		assertEquals(PlayerComparisonOverlay.HIGHER, PlayerComparisonOverlay.colour(99, 80))
		assertEquals(PlayerComparisonOverlay.LOWER, PlayerComparisonOverlay.colour(60, 80))
		assertEquals(PlayerComparisonOverlay.EQUAL, PlayerComparisonOverlay.colour(80, 80))
	}

	@Test
	fun hiscoresFromTheWebsite() {
		val result: HiscoreLookup.Result? = HiscoreLookup.parse("{\"player\":\"Zezima\",\"skills\":{\"attack\":{\"rank\":3,\"level\":99,\"xp\":13034431},\"constitution\":{\"rank\":12,\"level\":87,\"xp\":4000000},\"prayer\":{\"rank\":-1,\"level\":1,\"xp\":0}}}")
		assertNotNull(result)
		assertEquals("Zezima", result.player)
		assertEquals(99, result.levels[Skill.ATTACK])
		assertEquals(87, result.levels[Skill.CONSTITUTION])
		assertEquals(-1, result.levels[Skill.PRAYER], "no rank")
		assertEquals(-1, result.levels[Skill.MAGIC], "missing")
		assertNull(HiscoreLookup.parse("{}"))
		// a player's most lifepoints: Constitution times ten
		HiscoreLookup.put("Other", result)
		assertEquals(870, OpponentInfo.maxHealthOf(other))
		assertEquals("A%20Name%C3%A9", HiscoreLookup.encode("A Nameé"))
	}

	@Test
	fun npcLifepoints() {
		assertEquals(70, NpcHitpoints.hitpoints(1)) // man
		assertEquals(290, NpcHitpoints.hitpoints(4458)) // ork
		assertNull(NpcHitpoints.hitpoints(945)) // the tutorial guide: not fought
		assert(NpcHitpoints.count() > 4000)
	}
}
