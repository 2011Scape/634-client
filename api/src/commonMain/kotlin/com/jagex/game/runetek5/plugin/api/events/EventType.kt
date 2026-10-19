package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * One kind of event, the key to subscribe with. The client's are the constants in [Events]; the
 * type parameter is the class of the event object listeners get.
 */
class EventType<E>(@JvmField val id: Int, @JvmField val name: String) {
	override fun toString(): String {
		return name
	}
}
