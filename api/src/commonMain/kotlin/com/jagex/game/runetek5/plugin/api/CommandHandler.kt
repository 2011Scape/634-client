package com.jagex.game.runetek5.plugin.api

/** Runs a console command a plugin registered: `command` as typed, and the words after it. */
fun interface CommandHandler {
	fun run(command: String, args: Array<String>)
}
