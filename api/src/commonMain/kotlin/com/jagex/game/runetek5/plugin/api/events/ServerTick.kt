package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.SERVER_TICK]: [tick] counts the server's ticks since the client started, one each time the
 * player update comes. One reused instance. Since API 28.
 */
class ServerTick {
	@JvmField
	var tick: Int = 0
}
