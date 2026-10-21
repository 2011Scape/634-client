package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the skilling animations the idle notifier (IdleNotifier) watches
 * for stopping, as RuneLite's Idle Notifier keeps a list of its own. The ids are the 634 cache's,
 * read from the Void server's data (the `.anims.toml` files under `data/skill` and
 * `data/entity/player/human.anims.toml`); an animation not listed here, a fight's or an emote's
 * among them, never counts as skilling. Add to [SKILLING] as the server's data grows.
 */
object IdleAnimations {

	/** The ids, grouped by skill as the server's files group them; [sorted] is what [contains] searches. */
	@JvmField
	val SKILLING: IntArray = intArrayOf(
		// woodcutting: hatchet.anims.toml, bronze to dragon, the inferno adze, the sacred and volatile clay hatchets
		879, 877, 875, 873, 871, 869, 867, 2846, 10251, 1905, 1907,
		// woodcutting: ivy, bronze to dragon
		12282, 2847, 880, 878, 876, 874, 872, 870,
		// mining: pickaxe.anims.toml, bronze to rune and dragon
		625, 626, 627, 629, 628, 624, 12189,
		// fishing: fishing.anims.toml: nets, the rod's cast and repeat, harpoons, the pot and cage, the karambwan vessel, barehand
		621, 620, 622, 623, 618, 5108, 619, 10009, 1193, 6705, 6706, 6707, 6708, 6709, 6710, 6711,
		// cooking: cooking.anims.toml: a fire, a range, the churn
		897, 896, 2793,
		// firemaking: firemaking.anims.toml
		733,
		// fletching: fletching.anims.toml: the knife, stringing shortbows, longbows and crossbows
		1248, 6678, 6679, 6680, 6681, 6682, 6683, 6684, 6685, 6686, 6687, 6688, 6689, 6671, 6672, 6673, 6674, 6675, 6676, 6677,
		// crafting: crafting.anims.toml: gems, leather, glassblowing, weaving, molten glass
		888, 889, 887, 886, 885, 2717, 1249, 884, 2270, 899,
		// smithing: smithing.anims.toml: the anvil, the furnace
		898,
		// herblore: herblore.anims.toml: mixing, the pestle and mortar
		363, 364, 5249,
		// runecrafting: runecrafting.anims.toml
		791,
		// prayer: burying (human.anims.toml), the altar's offering and prayer, the ectofuntus
		827, 3705, 645, 1651,
		// magic: spellbook.anims.toml and magic.anims.toml: alchemy, superheat, enchanting, bones to bananas, orbs, lunar casts, plank make, string jewellery
		713, 9633, 725, 719, 720, 721, 931, 722, 726, 4411, 6298, 4412,
		// thieving: thieving.anims.toml and human.anims.toml: pickpocketing, a stall, a locked chest
		881, 5074, 5075, 5078, 832, 537,
		// hunter: hunter.anims.toml: traps, the butterfly net, the noose
		5208, 5207, 6605, 5259,
		// farming: farming.anims.toml and human.anims.toml: raking, planting, watering, digging, picking, curing
		2273, 2291, 2293, 830, 2280, 2288,
		// construction: construction.anims.toml
		3683, 3685,
	)

	/** [SKILLING] in order, for the binary search. */
	private val sorted: IntArray = sortedCopy(SKILLING)

	/** Whether [id] is a skilling animation. */
	@JvmStatic
	fun contains(id: Int): Boolean {
		var low: Int = 0
		var high: Int = sorted.size - 1
		while (low <= high) {
			val middle: Int = (low + high) ushr 1
			val value: Int = sorted[middle]
			if (value < id) {
				low = middle + 1
			} else if (value > id) {
				high = middle - 1
			} else {
				return true
			}
		}
		return false
	}

	/** A sorted copy of [ids], by insertion: a short list, sorted once. */
	private fun sortedCopy(ids: IntArray): IntArray {
		val copy: IntArray = IntArray(ids.size)
		for (i in ids.indices) {
			val value: Int = ids[i]
			var j: Int = i - 1
			while (j >= 0 && copy[j] > value) {
				copy[j + 1] = copy[j]
				j--
			}
			copy[j + 1] = value
		}
		return copy
	}
}
