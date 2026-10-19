package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** The client's events. The ids are 0 to [COUNT] - 1, and new ones are only ever added at the end. */
object Events {
	/** Every client cycle, 50 a second, in every state. */
	@JvmField
	val CLIENT_TICK: EventType<ClientTick> = EventType(0, "ClientTick")

	/** Every client cycle while logged in, after the server's packets for it are handled. */
	@JvmField
	val GAME_TICK: EventType<GameTick> = EventType(1, "GameTick")

	/** Every frame the game view is drawn, after the scene and before the interfaces; overlays run after the listeners. */
	@JvmField
	val VIEWPORT_DRAW: EventType<ViewportDraw> = EventType(2, "ViewportDraw")

	/** Every frame, after everything else, over the interfaces. [ViewportDraw] is usually the better hook. */
	@JvmField
	val FRAME_DRAW: EventType<FrameDraw> = EventType(3, "FrameDraw")

	/** A skill's experience or level from the server, before the client stores it. */
	@JvmField
	val STAT_CHANGED: EventType<StatChanged> = EventType(4, "StatChanged")

	/** A player variable changed. */
	@JvmField
	val VARP_CHANGED: EventType<VarpChanged> = EventType(5, "VarpChanged")

	/** A line was written to the chat box: a game message, a player's words, a clan message... */
	@JvmField
	val CHAT_MESSAGE: EventType<ChatMessage> = EventType(6, "ChatMessage")

	/** The server sent an inventory, whole or in part. */
	@JvmField
	val ITEM_CONTAINER_CHANGED: EventType<ItemContainerChanged> = EventType(7, "ItemContainerChanged")

	/** The right-click menu is being built for something in the scene: the moment to add an entry. */
	@JvmField
	val MENU_ENTRY_ADDED: EventType<MenuEntryAdded> = EventType(8, "MenuEntryAdded")

	/** The player clicked a menu entry (any entry, the client's included). */
	@JvmField
	val MENU_OPTION_CLICKED: EventType<MenuOptionClicked> = EventType(9, "MenuOptionClicked")

	/**
	 * The client moved between the login screen, the lobby and the game. An error screen (a lost
	 * connection to the cache server, say) is not a state: nothing is posted for it.
	 */
	@JvmField
	val GAME_STATE_CHANGED: EventType<GameStateChanged> = EventType(10, "GameStateChanged")

	/** The graphics mode changed, or loaded for the first time: every font and sprite is new. */
	@JvmField
	val TOOLKIT_CHANGED: EventType<ToolkitChanged> = EventType(11, "ToolkitChanged")

	/** A console command no plugin registered, before the client handles it. */
	@JvmField
	val CONSOLE_COMMAND: EventType<ConsoleCommand> = EventType(12, "ConsoleCommand")

	/** A key went down. */
	@JvmField
	val KEY_PRESSED: EventType<KeyPressed> = EventType(13, "KeyPressed")

	/** A mouse button went down. */
	@JvmField
	val MOUSE_CLICKED: EventType<MouseClicked> = EventType(14, "MouseClicked")

	const val COUNT: Int = 15

	/** Every event type, at the index of its id. */
	@JvmField
	val ALL: Array<EventType<*>> = arrayOf(CLIENT_TICK, GAME_TICK, VIEWPORT_DRAW, FRAME_DRAW, STAT_CHANGED, VARP_CHANGED, CHAT_MESSAGE, ITEM_CONTAINER_CHANGED, MENU_ENTRY_ADDED, MENU_OPTION_CLICKED, GAME_STATE_CHANGED, TOOLKIT_CHANGED, CONSOLE_COMMAND, KEY_PRESSED, MOUSE_CLICKED)
}
