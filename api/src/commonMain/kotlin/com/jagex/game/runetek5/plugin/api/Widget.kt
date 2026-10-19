package com.jagex.game.runetek5.plugin.api

/** An interface component, by its interface and component ids. Positions are on the screen as last drawn. */
interface Widget {
	fun interfaceId(): Int

	fun componentId(): Int

	fun text(): String?

	fun x(): Int

	fun y(): Int

	fun width(): Int

	fun height(): Int

	fun isHidden(): Boolean
}
