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

		/** An interface component, an inventory's item slot included. */
		@JvmField
		val WIDGET: MenuTarget = MenuTarget(5, "widget")

		/**
		 * A world tile on the world map: the entry's sceneX and sceneY are the world tile's x and y (not scene
		 * tiles) and its id is the level. Since API 37.
		 */
		@JvmField
		val WORLD_MAP: MenuTarget = MenuTarget(6, "world map")

		@JvmField
		val ALL: Array<MenuTarget> = arrayOf(TILE, NPC, PLAYER, GROUND_ITEM, LOC, WIDGET, WORLD_MAP)

		/** The target with [kind], or [TILE] for a kind that is not one. */
		@JvmStatic
		fun ofKind(kind: Int): MenuTarget {
			return if (kind >= 0 && kind < ALL.size) ALL[kind] else TILE
		}
	}
}
