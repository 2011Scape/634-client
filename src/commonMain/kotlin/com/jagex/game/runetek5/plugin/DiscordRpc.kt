package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.DiscordActivity

/** What the connection to Discord reports, on its own thread: pass it to the game thread before touching the client. */
interface DiscordRpcListener {
	/** A line worth the plugin's log: connected, disconnected, refused, a presence accepted. */
	fun event(message: String)
}

/**
 * Not part of the original client: the connection to the Discord desktop app for the Discord
 * plugin, over Discord's local RPC socket (`discord-ipc-N` in the runtime folder on Linux and
 * macOS, a named pipe on Windows). A thread of its own connects, shakes hands as the application,
 * sends the latest activity set with [setActivity], answers pings and reconnects with a growing
 * wait when Discord is not running. Nothing on the web: there the presence is never shown.
 */
expect object DiscordRpc {
	/** Starts the connection thread as application [applicationId]; nothing when it already runs. */
	fun start(applicationId: String, listener: DiscordRpcListener)

	/** Shows [activity], or clears the presence when null; sent when connected, kept for the next connection meanwhile. */
	fun setActivity(activity: DiscordActivity?)

	/** Clears the presence and ends the connection thread. */
	fun stop()

	fun isConnected(): Boolean

	/** One line on the connection, for `::discord`. */
	fun status(): String
}
