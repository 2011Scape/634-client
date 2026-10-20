package com.jagex.game.runetek5.plugin.api

/** Runs a console command a plugin registered: `command` as typed, and the words after it. */
fun interface CommandHandler {
	/** [command] is the name as typed (any case), [args] the words after it; the handler answers through [ClientApi.consolePrint] or the chat. */
	fun run(command: String, args: Array<String>)
}
