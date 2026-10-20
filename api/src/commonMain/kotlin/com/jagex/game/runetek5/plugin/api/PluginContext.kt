package com.jagex.game.runetek5.plugin.api

import com.jagex.game.runetek5.plugin.api.events.EventBus

/**
 * What a plugin is given in [Plugin.start]: the client, its events, the plugin's own settings, and
 * where to register what it adds to the game. Everything registered through it is the plugin's and
 * is removed when the plugin stops.
 */
interface PluginContext {
	fun descriptor(): PluginDescriptor

	fun client(): ClientApi

	fun events(): EventBus

	fun config(): PluginConfig

	/** Draws [overlay] on the game view every frame, after the scene and the client's own overlays. */
	fun addOverlay(overlay: Overlay)

	fun removeOverlay(overlay: Overlay)

	/**
	 * Claims the console command `::name` (compared ignoring case). False when another plugin or the
	 * client already has it. The handler gets the command as typed and its arguments.
	 */
	fun registerCommand(name: String, handler: CommandHandler): Boolean

	/**
	 * A right-click menu action of the plugin's own. The result is the click type to give
	 * [ClientApi.addMenuEntry] from a [MenuEntryAdded][com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded]
	 * listener; -1 when the client has no more to give (there are 100 for all plugins together).
	 */
	fun registerMenuAction(action: MenuAction): Int

	/** Writes [message] to the client's log (standard error), prefixed with the plugin id. */
	fun log(message: String)

	/**
	 * Runs [task] on the game thread at the next client cycle. The one method a plugin may call from
	 * another thread: the way work done elsewhere comes back to the game. Dropped if the plugin stops first.
	 */
	fun invokeLater(task: Task)

	/**
	 * Runs [task] on the game thread [ticks] game ticks (600 ms, 30 client cycles) from now, while
	 * logged in; 0 means the next cycle. False when the plugin has stopped.
	 */
	fun schedule(ticks: Int, task: Task): Boolean

	/**
	 * Runs [task] on a thread of its own, named after the plugin and [name], for work the game must
	 * not wait for: a web request, a file. The task is off the game thread: it must not read the
	 * client or draw, only compute and hand its result to [invokeLater]. One that throws disables the
	 * plugin. Not available in the browser.
	 */
	fun startThread(name: String, task: Task)
}
