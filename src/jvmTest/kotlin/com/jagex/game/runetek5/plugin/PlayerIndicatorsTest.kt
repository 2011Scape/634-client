package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.PlayerIndicators
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.NameVisitor
import com.jagex.game.runetek5.plugin.api.Player
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: who Player Indicators colours, and how (PlayerIndicators). */
class PlayerIndicatorsTest {

	private val friends: Set<String> = setOf("Zezima")

	private val chat: Map<String, Int> = mapOf("Durial321" to 7, "Bob" to 1)

	private fun player(name: String, local: Boolean = false, team: Int = 0, attackable: Boolean = false): Player =
		Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
			when (method.name) {
				"name", "displayName" -> name
				"isLocalPlayer" -> local
				"teamId" -> team
				"isAttackable" -> attackable
				else -> null
			}
		} as Player

	private val me: Player = player("Me", local = true, team = 5)

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"localPlayer" -> me
			"isFriend" -> args[0] in friends
			"isInFriendsChat" -> args[0] in chat
			"serverTick" -> 30
			"clanMembers" -> {
				for ((name, rank) in chat) {
					(args[0] as NameVisitor).visit(name, 1, rank)
				}
				null
			}
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		PlayerIndicators.enabled = true
		PlayerIndicators.highlight.fill(PlayerIndicators.ENABLED)
		PlayerIndicators.highlight[PlayerIndicators.SELF] = PlayerIndicators.DISABLED
		PlayerIndicators.highlight[PlayerIndicators.OTHER] = PlayerIndicators.DISABLED
	}

	@AfterTest
	fun tidy() {
		fresh()
		PlayerIndicators.enabled = false
	}

	@Test
	fun friendsComeBeforeTheChatAndTheChatBeforeTheTeam() {
		assertEquals(PlayerIndicators.FRIEND, PlayerIndicators.relation(client, player("Zezima", team = 5)))
		assertEquals(PlayerIndicators.FRIENDS_CHAT, PlayerIndicators.relation(client, player("Durial321", team = 5)))
		assertEquals(PlayerIndicators.TEAM, PlayerIndicators.relation(client, player("Stranger", team = 5)))
		assertEquals(PlayerIndicators.NONE, PlayerIndicators.relation(client, player("Stranger", team = 4)))
	}

	@Test
	fun othersAndSelfOnlyWhenTurnedOn() {
		assertEquals(PlayerIndicators.NONE, PlayerIndicators.relation(client, me))
		PlayerIndicators.highlight[PlayerIndicators.SELF] = PlayerIndicators.ENABLED
		PlayerIndicators.highlight[PlayerIndicators.OTHER] = PlayerIndicators.ENABLED
		assertEquals(PlayerIndicators.SELF, PlayerIndicators.relation(client, me))
		assertEquals(PlayerIndicators.OTHER, PlayerIndicators.relation(client, player("Stranger")))
	}

	@Test
	fun pvpHighlightsOnlyThoseWhoCanBeAttacked() {
		PlayerIndicators.highlight[PlayerIndicators.FRIEND] = PlayerIndicators.PVP
		assertEquals(PlayerIndicators.NONE, PlayerIndicators.relation(client, player("Zezima")))
		assertEquals(PlayerIndicators.FRIEND, PlayerIndicators.relation(client, player("Zezima", attackable = true)))
	}

	@Test
	fun ranksComeFromTheFriendsChat() {
		assertEquals(7, PlayerIndicators.rank(client, "Durial321"))
		assertEquals(1, PlayerIndicators.rank(client, "bob"))
		assertEquals(-1, PlayerIndicators.rank(client, "Zezima"))
	}

	@Test
	fun theMenuNameIsColouredAndTheIconGoesFirst() {
		val target = "<col=ffffff>Zezima<col=00ff00>  (level-126)"
		assertEquals("<img=9><col=00c853>Zezima<col=00ff00>  (level-126)", PlayerIndicators.menuTarget(target, 0xFF00C853.toInt(), "<img=9>"))
		assertEquals(target, PlayerIndicators.menuTarget(target, 0, ""))
	}

	@Test
	fun playerEntriesAreKnownByTheirClickType() {
		assertTrue(PlayerIndicators.isPlayerEntry(11))
		assertTrue(PlayerIndicators.isPlayerEntry(2045))
		assertFalse(PlayerIndicators.isPlayerEntry(25))
	}
}
