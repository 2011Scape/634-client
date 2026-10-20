package com.jagex.game.runetek5.plugin.api

/** How an interface was opened. */
object InterfaceOpenType {
	/** A window that takes the input: the bank, a shop, a dialogue. */
	const val MODAL: Int = 0

	/** Over the game, without taking the input: the tabs, the chat box. */
	const val OVERLAY: Int = 1

	/** An overlay the client opened by itself. */
	const val OVERLAY_CLIENT: Int = 3
}
