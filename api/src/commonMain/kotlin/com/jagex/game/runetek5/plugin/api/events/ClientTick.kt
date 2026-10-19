package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.CLIENT_TICK]. One reused instance. */
class ClientTick {
	@JvmField
	var cycle: Int = 0
}
