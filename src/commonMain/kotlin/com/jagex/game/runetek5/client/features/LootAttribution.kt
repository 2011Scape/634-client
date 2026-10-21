package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.events.GroundItem
import kotlin.jvm.JvmField

/**
 * Not part of the original client: which items on the ground were a kill's loot, as RuneLite's
 * LootManager reckons it, for Ground Items and the Loot Tracker. An npc that leaves while dying
 * (its health bar showing empty; a few slayer monsters leave alive and count all the same) claims
 * the items that appear on the tiles under it, every tile of a big one, in the same server tick; a
 * player who leaves with an empty health bar claims those on its one tile. A tile is claimed once
 * a tick, so two deaths on it do not both take the items.
 *
 * The client cannot tell a server tick apart (its game tick is a client cycle of 20 ms), and the
 * packets of one server tick may land in neighbouring cycles, item before npc or after it: a death
 * and an item belong together when they are less than [WINDOW] cycles apart, half a server tick.
 * The plugin calls [npcDespawned], [playerDespawned], [itemSpawned] and [cycle] from its events, and
 * hears of each kill at [cycle] once its window has closed.
 */
class LootAttribution(private val listener: Listener) {

	/** What a kill left: [type] NPC or PLAYER, the name and level, the npc's type ([npcId], -1 for a player) and the items. */
	class Kill(@JvmField val type: Int, @JvmField val name: String, @JvmField val combatLevel: Int, @JvmField val npcId: Int, @JvmField val items: ArrayList<Stack>)

	/** An item of a kill's loot and where it lies, in world tiles. */
	class Stack(@JvmField val itemId: Int, @JvmField val quantity: Int, @JvmField val worldX: Int, @JvmField val worldY: Int, @JvmField val level: Int)

	fun interface Listener {
		fun loot(kill: Kill)
	}

	private class Death(@JvmField val type: Int, @JvmField val name: String, @JvmField val combatLevel: Int, @JvmField val npcId: Int, @JvmField val tiles: LongArray, @JvmField val cycle: Int)

	private class Spawn(@JvmField val stack: Stack, @JvmField val cycle: Int, @JvmField var claimed: Boolean)

	private val deaths: ArrayList<Death> = ArrayList()

	private val spawns: ArrayList<Spawn> = ArrayList()

	/** Tiles a death has claimed, and the cycle it died: no other death takes items on them within the window. */
	private val claimedTiles: HashMap<Long, Int> = HashMap()

	/** An npc left the scene; its name, type and tiles are still there to read. */
	fun npcDespawned(client: ClientApi, npc: Npc) {
		val name: String = npc.name() ?: return
		if (!npc.isDying() && !diesAlive(name)) {
			return
		}
		val size: Int = maxOf(1, npc.sizeInTiles())
		val tiles: LongArray = LongArray(size * size)
		val baseX: Int = client.sceneBaseX() + npc.tileX()
		val baseY: Int = client.sceneBaseY() + npc.tileY()
		var n: Int = 0
		for (dx in 0 until size) {
			for (dy in 0 until size) {
				tiles[n++] = tileKey(baseX + dx, baseY + dy, npc.level())
			}
		}
		deaths.add(Death(NPC, name, npc.combatLevel(), npc.typeId(), tiles, client.clientCycle()))
	}

	/** A player left the scene; with an empty health bar it died, and its tile's items are its loot. */
	fun playerDespawned(client: ClientApi, player: Player) {
		if (player.isLocalPlayer() || !player.isDying()) {
			return
		}
		val name: String = player.name() ?: return
		val tile: Long = tileKey(client.sceneBaseX() + player.tileX(), client.sceneBaseY() + player.tileY(), player.level())
		deaths.add(Death(PLAYER, name, player.combatLevel(), -1, longArrayOf(tile), client.clientCycle()))
	}

	fun itemSpawned(client: ClientApi, item: GroundItem) {
		if (item.ownership == GroundItem.OWNERSHIP_OTHER) {
			return // someone else's drop made public a minute after it fell: never a kill's loot now
		}
		val stack: Stack = Stack(item.itemId, item.amount, client.sceneBaseX() + item.sceneX, client.sceneBaseY() + item.sceneY, item.level)
		spawns.add(Spawn(stack, client.clientCycle(), false))
	}

	/** Each client cycle: the kills whose window has closed are handed on, and what is too old is forgotten. */
	fun cycle(now: Int) {
		var i: Int = 0
		while (i < deaths.size) {
			val death: Death = deaths[i]
			if (now - death.cycle < WINDOW) {
				i++
				continue
			}
			deaths.removeAt(i)
			settle(death)
		}
		spawns.removeAll { now - it.cycle > WINDOW * 2 }
		val stale: ArrayList<Long> = ArrayList()
		for (entry in claimedTiles.entries) {
			if (now - entry.value > WINDOW * 2) {
				stale.add(entry.key)
			}
		}
		for (key in stale) {
			claimedTiles.remove(key)
		}
	}

	/** Forgets everything, at logout. */
	fun clear() {
		deaths.clear()
		spawns.clear()
		claimedTiles.clear()
	}

	private fun settle(death: Death) {
		val items: ArrayList<Stack> = ArrayList()
		for (tile in death.tiles) {
			val claimedAt: Int? = claimedTiles[tile]
			if (claimedAt != null && kotlin.math.abs(claimedAt - death.cycle) < WINDOW) {
				continue // another death on this tile this tick took it
			}
			claimedTiles[tile] = death.cycle
			for (spawn in spawns) {
				if (!spawn.claimed && kotlin.math.abs(spawn.cycle - death.cycle) < WINDOW && tileKey(spawn.stack.worldX, spawn.stack.worldY, spawn.stack.level) == tile) {
					spawn.claimed = true
					items.add(spawn.stack)
				}
			}
		}
		if (items.isNotEmpty()) {
			listener.loot(Kill(death.type, death.name, death.combatLevel, death.npcId, items))
		}
	}

	companion object {
		const val NPC: Int = 0

		const val PLAYER: Int = 1

		/** Client cycles a death and its loot may be apart: half a server tick (30 cycles). */
		const val WINDOW: Int = 15

		/** Monsters that leave before their health bar is empty, as RuneLite's NpcUtil lists them (by their 2011 names). */
		private val DIES_ALIVE: Array<String> = arrayOf("gargoyle", "rockslug", "lizard", "desert lizard", "small lizard", "zygomite", "mutated zygomite")

		fun diesAlive(name: String): Boolean {
			val lower: String = name.lowercase()
			for (n in DIES_ALIVE) {
				if (lower == n) {
					return true
				}
			}
			return false
		}

		fun tileKey(worldX: Int, worldY: Int, level: Int): Long {
			return (level.toLong() shl 32) or (worldX.toLong() shl 16) or worldY.toLong()
		}
	}
}
