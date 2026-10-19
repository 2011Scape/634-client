package com.jagex.game.runetek5.plugin.api.events

/**
 * Where a plugin subscribes to the client's events. Listeners run on the game thread in the order
 * they subscribed. A listener that throws disables its plugin, so the game goes on.
 *
 * The event objects of the events that happen every frame or cycle ([ClientTick], [GameTick],
 * [ViewportDraw], [FrameDraw], [KeyPressed], [MouseClicked], [MenuEntryAdded], [MenuOptionClicked],
 * [ConsoleCommand]) are reused: read them in the listener and keep nothing.
 */
interface EventBus {
	fun <E> subscribe(type: EventType<E>, listener: EventListener<E>)

	fun <E> unsubscribe(type: EventType<E>, listener: EventListener<E>)
}
