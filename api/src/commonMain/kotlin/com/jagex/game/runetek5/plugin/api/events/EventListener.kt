package com.jagex.game.runetek5.plugin.api.events

/** Gets an event of one type. Runs on the game thread; keep it quick, it may run every frame. */
fun interface EventListener<E> {
	/** Receives one event; what it may keep is said on the event's type. */
	fun on(event: E)
}
