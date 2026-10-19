package com.jagex.game.runetek5.plugin.api.events

/** Gets an event of one type. Runs on the game thread; keep it quick, it may run every frame. */
fun interface EventListener<E> {
	fun on(event: E)
}
