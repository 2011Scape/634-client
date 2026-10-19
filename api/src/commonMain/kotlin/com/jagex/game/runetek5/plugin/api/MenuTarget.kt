package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/** The kind of thing a right-click menu is being built for. One instance per kind. */
class MenuTarget private constructor(@JvmField val kind: Int, @JvmField val name: String) {
	override fun toString(): String {
		return name
	}

	companion object {
		@JvmField
		val TILE: MenuTarget = MenuTarget(0, "tile")

		@JvmField
		val NPC: MenuTarget = MenuTarget(1, "npc")

		@JvmField
		val PLAYER: MenuTarget = MenuTarget(2, "player")

		@JvmField
		val GROUND_ITEM: MenuTarget = MenuTarget(3, "ground item")

		@JvmField
		val LOC: MenuTarget = MenuTarget(4, "loc")

		@JvmField
		val ALL: Array<MenuTarget> = arrayOf(TILE, NPC, PLAYER, GROUND_ITEM, LOC)

		/** The target with [kind], or [TILE] for a kind that is not one. */
		@JvmStatic
		fun ofKind(kind: Int): MenuTarget {
			return if (kind >= 0 && kind < ALL.size) ALL[kind] else TILE
		}
	}
}
