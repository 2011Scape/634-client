package com.jagex.game.runetek5.plugin.api

/** What a [Widget] is ([Widget.type]). */
object WidgetType {
	/** A layer holding other components. */
	const val CONTAINER: Int = 0

	/** A model of a player or an npc, or an animated figure. */
	const val FIGURE: Int = 3

	const val TEXT: Int = 4

	const val SPRITE: Int = 5

	const val MODEL: Int = 6

	const val LINE: Int = 9
}
