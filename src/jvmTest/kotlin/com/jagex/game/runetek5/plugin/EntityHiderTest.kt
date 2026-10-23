package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.EntityHider
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Player
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Entity Hider's choices (EntityHider.shouldDraw). */
class EntityHiderTest {

	private val local: Player = player("me", null, true)

	private fun player(name: String, interacting: Any?, isLocal: Boolean = false): Player =
		Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
			when (method.name) {
				"name" -> name
				"interacting" -> interacting
				"isLocalPlayer" -> isLocal
				else -> null
			}
		} as Player

	private fun npc(typeId: Int, actions: List<String?> = emptyList(), dying: Boolean = false, interacting: Any? = null): Npc {
		val type: NpcDefinition = Proxy.newProxyInstance(NpcDefinition::class.java.classLoader, arrayOf(NpcDefinition::class.java)) { _, method, args ->
			if (method.name == "action") actions.getOrNull(args[0] as Int) else null
		} as NpcDefinition
		return Proxy.newProxyInstance(Npc::class.java.classLoader, arrayOf(Npc::class.java)) { _, method, _ ->
			when (method.name) {
				"typeId" -> typeId
				"type" -> type
				"isDying" -> dying
				"interacting" -> interacting
				else -> null
			}
		} as Npc
	}

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"localPlayer" -> local
			"isFriend" -> args[0] == "pal"
			"isInFriendsChat" -> args[0] == "clanmate"
			"isIgnored" -> args[0] == "pest"
			"varp" -> 6829
			else -> null
		}
	} as ClientApi

	@AfterTest
	fun tidy() {
		EntityHider.hideOthers = false
		EntityHider.hideOthers2D = false
		EntityHider.hideFriends = false
		EntityHider.hideIgnores = false
		EntityHider.hideLocalPlayer2D = false
		EntityHider.hidePets = false
		EntityHider.hideAttackers = false
		EntityHider.hideRandomEvents = false
		EntityHider.hideDeadNpcs = false
		EntityHider.hideNPCs = false
	}

	@Test
	fun othersAndTheir2DApart() {
		EntityHider.hideOthers = true
		assertFalse(EntityHider.shouldDraw(client, player("stranger", null), false))
		assertTrue(EntityHider.shouldDraw(client, player("stranger", null), true))
		EntityHider.hideOthers2D = true
		assertFalse(EntityHider.shouldDraw(client, player("stranger", null), true))
	}

	@Test
	fun friendsClanAndIgnoresByTheirOwnSettings() {
		EntityHider.hideOthers = true
		assertTrue(EntityHider.shouldDraw(client, player("pal", null), false))
		assertTrue(EntityHider.shouldDraw(client, player("clanmate", null), false))
		assertTrue(EntityHider.shouldDraw(client, player("pest", null), false))
		EntityHider.hideIgnores = true
		assertFalse(EntityHider.shouldDraw(client, player("pest", null), false))
	}

	@Test
	fun theLocalPlayerAndAttackers() {
		EntityHider.hideLocalPlayer2D = true
		assertTrue(EntityHider.shouldDraw(client, local, false))
		assertFalse(EntityHider.shouldDraw(client, local, true))
		EntityHider.hideAttackers = true
		assertFalse(EntityHider.shouldDraw(client, player("pal", local), false))
		assertFalse(EntityHider.shouldDraw(client, npc(1, interacting = local), false))
	}

	@Test
	fun petsDeadAndRandomEvents() {
		EntityHider.hidePets = true
		assertFalse(EntityHider.shouldDraw(client, npc(6830, listOf(null, null, "Interact")), false))
		// the player's own familiar (the follower details' npc)
		assertTrue(EntityHider.shouldDraw(client, npc(6829, listOf(null, null, "Interact")), false))
		EntityHider.hideDeadNpcs = true
		assertFalse(EntityHider.shouldDraw(client, npc(81, dying = true), false))
		EntityHider.hideRandomEvents = true
		assertFalse(EntityHider.shouldDraw(client, npc(8629), false))
		assertTrue(EntityHider.shouldDraw(client, npc(81), false))
		EntityHider.hideNPCs = true
		assertFalse(EntityHider.shouldDraw(client, npc(81), false))
		assertTrue(EntityHider.shouldDraw(client, npc(81), true))
	}
}
