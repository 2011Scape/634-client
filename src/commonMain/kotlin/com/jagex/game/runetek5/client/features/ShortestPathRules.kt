package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: what the player has when a Shortest Path search starts (levels, the
 * backpack and what is worn, the spellbook, the quest vars), read on the game thread so the search can
 * check the transports' requirements against it.
 */
class ShortestPathPlayer(
	@JvmField val levels: IntArray,
	/** Item id to amount, backpack and worn together (a worn glory teleports as one carried). */
	@JvmField val items: Map<Int, Int>,
	@JvmField val worn: Set<Int>,
	/** "modern", "ancient", "lunar", or null when no spellbook is open in its tab. */
	@JvmField val spellbook: String?,
	/** A varp's (false) or a varbit's (true) value: the quests Void keeps in them, which it sends to the client. */
	@JvmField val vars: (Boolean, Int) -> Int,
) {
	companion object {
		const val INVENTORY: Int = 93
		const val EQUIPMENT: Int = 94

		/** The spellbooks' interfaces in the magic tab, Void's modern_spellbook, ancient_spellbook and lunar_spellbook. */
		private const val MODERN: Int = 192
		private const val ANCIENT: Int = 193
		private const val LUNAR: Int = 430

		@JvmStatic
		fun read(client: ClientApi): ShortestPathPlayer {
			val levels = IntArray(Skill.COUNT) { client.skillLevel(it) }
			val items = HashMap<Int, Int>()
			val worn = HashSet<Int>()
			client.itemContainer(INVENTORY)?.let { add(it, items, null) }
			client.itemContainer(EQUIPMENT)?.let { add(it, items, worn) }
			var book: String? = null
			client.openInterfaces { id, _, _ ->
				when (id) {
					MODERN -> book = "modern"
					ANCIENT -> book = "ancient"
					LUNAR -> book = "lunar"
				}
			}
			return ShortestPathPlayer(levels, items, worn, book) { bit, id -> if (bit) client.varbit(id) else client.varp(id) }
		}

		private fun add(container: ItemContainer, items: HashMap<Int, Int>, worn: HashSet<Int>?) {
			for (slot in 0 until container.size()) {
				val id: Int = container.itemId(slot)
				if (id >= 0) {
					items[id] = (items[id] ?: 0) + container.amount(slot)
					worn?.add(id)
				}
			}
		}
	}
}

/**
 * Not part of the original client: whether a Shortest Path transport may be taken, by the plugin's
 * switches and the requirements the generator wrote for it (levels by skill name, a spellbook, items
 * with their amounts where `a|b` is either one, a worn item, and quests as the varps and varbits Void
 * keeps them in: `var=varp:302:60|61,varbit:2326:!0`, each var one of the values, or with `!` none of them).
 */
class ShortestPathRules(
	@JvmField val player: ShortestPathPlayer,
	/** Rune id to the weapons that stand in for it (the table's STAFF rows). */
	@JvmField val staves: Map<Int, Set<Int>>,
	/** Whether the items a teleport needs must be carried; false takes every teleport item as had. */
	@JvmField val checkItems: Boolean,
	/** The most coins a transport may cost. */
	@JvmField val coinLimit: Int,
) {
	fun meets(transport: ShortestPathTransport): Boolean {
		for ((key, value) in transport.requirements) {
			if (!meets(transport, key, value)) {
				return false
			}
		}
		return true
	}

	private fun meets(transport: ShortestPathTransport, key: String, value: String): Boolean {
		val skill: Int = SKILLS.indexOf(key)
		return when {
			skill >= 0 -> player.levels[skill] >= (value.toIntOrNull() ?: 0)
			key == "book" -> player.spellbook == null || player.spellbook == value
			key == "items" -> items(value, transport.type == "TELEPORT_ITEM" && !checkItems)
			key == "equipped" -> value.isEmpty() || value.split('|').any { (it.toIntOrNull() ?: -1) in player.worn }
			key == "var" -> value.split(',').all { varTest(it) }
			else -> true // "consumed" and other notes
		}
	}

	/** One `varp:<id>:<a|b>` or `varbit:<id>:!<a|b>` test. */
	private fun varTest(test: String): Boolean {
		val parts: List<String> = test.split(':')
		val id: Int = parts.getOrNull(1)?.toIntOrNull() ?: return true
		val list: String = parts.getOrElse(2) { "" }
		val negate: Boolean = list.startsWith("!")
		val values: List<Int> = list.removePrefix("!").split('|').mapNotNull { it.toIntOrNull() }
		val value: Int = player.vars(parts[0] == "varbit", id)
		return (value in values) != negate
	}

	/** Every group of `ids x amount` carried, a rune also covered by a worn staff; coins up to [coinLimit]. */
	private fun items(value: String, assumeHad: Boolean): Boolean {
		for (group in value.split(',')) {
			val x: Int = group.lastIndexOf('x')
			val amount: Int = if (x < 0) 1 else group.substring(x + 1).toIntOrNull() ?: 1
			val ids: List<Int> = (if (x < 0) group else group.substring(0, x)).split('|').mapNotNull { it.toIntOrNull() }
			if (!group(ids, amount, assumeHad)) {
				return false
			}
		}
		return true
	}

	private fun group(ids: List<Int>, amount: Int, assumeHad: Boolean): Boolean {
		if (COINS in ids) {
			return amount <= coinLimit && (assumeHad || (player.items[COINS] ?: 0) >= amount)
		}
		if (assumeHad || ids.any { (player.items[it] ?: 0) >= amount }) {
			return true
		}
		return ids.any { rune -> staves[rune]?.any { it in player.worn } == true }
	}

	companion object {
		const val COINS: Int = 995

		/** The generator's skill names, in Skill's order. */
		@JvmField
		val SKILLS: List<String> = listOf(
			"attack", "defence", "strength", "constitution", "ranged", "prayer", "magic", "cooking", "woodcutting",
			"fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblore", "agility", "thieving",
			"slayer", "farming", "runecrafting", "hunter", "construction", "summoning", "dungeoneering",
		)

		/** The STAFF rows: `rune=<id>;equipped=<a|b|...>`. */
		@JvmStatic
		fun staves(transports: List<ShortestPathTransport>): Map<Int, Set<Int>> {
			val map = HashMap<Int, Set<Int>>()
			for (row in transports) {
				val rune: Int = row.requirements["rune"]?.toIntOrNull() ?: -1
				if (row.type == "STAFF" && rune >= 0) {
					map[rune] = row.requirements["equipped"].orEmpty().split('|').mapNotNull { it.toIntOrNull() }.toSet()
				}
			}
			return map
		}
	}
}
