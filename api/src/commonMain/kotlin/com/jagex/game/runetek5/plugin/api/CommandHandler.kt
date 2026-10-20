package com.jagex.game.runetek5.plugin.api

/** Runs a console command a plugin registered: `command` as typed, and the words after it. */
fun interface CommandHandler {
	/** [command] is the name as typed (any case), [args] the words after it split on spaces, an empty array for none; the handler answers through [ClientApi.consolePrint], which works in every state, or the chat, which only shows in the game. A command runs whenever the console is open, logged in or not. */
	fun run(command: String, args: Array<String>)
}
