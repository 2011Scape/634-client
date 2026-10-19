package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/** The skills, numbered as the server sends them, with their names. */
object Skill {
	const val ATTACK: Int = 0
	const val DEFENCE: Int = 1
	const val STRENGTH: Int = 2
	const val CONSTITUTION: Int = 3
	const val RANGED: Int = 4
	const val PRAYER: Int = 5
	const val MAGIC: Int = 6
	const val COOKING: Int = 7
	const val WOODCUTTING: Int = 8
	const val FLETCHING: Int = 9
	const val FISHING: Int = 10
	const val FIREMAKING: Int = 11
	const val CRAFTING: Int = 12
	const val SMITHING: Int = 13
	const val MINING: Int = 14
	const val HERBLORE: Int = 15
	const val AGILITY: Int = 16
	const val THIEVING: Int = 17
	const val SLAYER: Int = 18
	const val FARMING: Int = 19
	const val RUNECRAFTING: Int = 20
	const val HUNTER: Int = 21
	const val CONSTRUCTION: Int = 22
	const val SUMMONING: Int = 23
	const val DUNGEONEERING: Int = 24

	const val COUNT: Int = 25

	@JvmField
	val NAMES: Array<String> = arrayOf("Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction", "Summoning", "Dungeoneering")

	/** The skill's name, or "Skill N" for a number that is not one. */
	@JvmStatic
	fun name(skill: Int): String {
		return if (skill >= 0 && skill < COUNT) NAMES[skill] else "Skill " + skill
	}
}
