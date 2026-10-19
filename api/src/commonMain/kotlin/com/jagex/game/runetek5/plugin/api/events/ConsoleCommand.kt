package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.CONSOLE_COMMAND]: [line] is the command as typed, without the `::`. A listener that
 * handles it calls [consume], and the client does nothing more with it. One reused instance.
 */
class ConsoleCommand {
	@JvmField
	var line: String = ""

	@JvmField
	var consumed: Boolean = false

	fun consume() {
		consumed = true
	}
}
