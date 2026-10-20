package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Player
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

	/** An npc entered the scene; the event is the npc. */
	@JvmField
	val NPC_SPAWNED: EventType<Npc> = EventType(15, "NpcSpawned")

	/** An npc is leaving the scene, or the area change that drops it is happening; the event is the npc, still whole. A logout or a reconnect drops everyone without this. */
	@JvmField
	val NPC_DESPAWNED: EventType<Npc> = EventType(16, "NpcDespawned")

	/** A player entered the scene, the local player included (at login); the event is the player. */
	@JvmField
	val PLAYER_SPAWNED: EventType<Player> = EventType(17, "PlayerSpawned")

	/** A player is leaving the scene; the event is the player, still whole. A logout or a reconnect drops everyone without this. */
	@JvmField
	val PLAYER_DESPAWNED: EventType<Player> = EventType(18, "PlayerDespawned")

	/** An item appeared on the ground. A change in a pile's amount is a despawn of the old and a spawn of the new. */
	@JvmField
	val ITEM_SPAWNED: EventType<GroundItem> = EventType(19, "ItemSpawned")

	/** An item left the ground: picked up, gone, or its zone was cleared by the server. Not posted when the scene is rebuilt. */
	@JvmField
	val ITEM_DESPAWNED: EventType<GroundItem> = EventType(20, "ItemDespawned")

	/** A hit on a player or an npc. */
	@JvmField
	val HITSPLAT_APPLIED: EventType<HitsplatApplied> = EventType(21, "HitsplatApplied")

	/** The server set an actor's animation. */
	@JvmField
	val ANIMATION_CHANGED: EventType<AnimationChanged> = EventType(22, "AnimationChanged")

	/** An actor's target changed; the event is the actor, and [Actor.interacting] is the new target. */
	@JvmField
	val INTERACTING_CHANGED: EventType<Actor> = EventType(23, "InteractingChanged")

	/** Text appeared over an actor's head. */
	@JvmField
	val OVERHEAD_TEXT_CHANGED: EventType<OverheadTextChanged> = EventType(24, "OverheadTextChanged")

	/** A projectile was fired. */
	@JvmField
	val PROJECTILE_SPAWNED: EventType<ProjectileSpawned> = EventType(25, "ProjectileSpawned")

	/** A graphic plays on a tile. */
	@JvmField
	val SPOT_ANIM_SPAWNED: EventType<SpotAnimSpawned> = EventType(26, "SpotAnimSpawned")

	/** An interface opened inside a component. */
	@JvmField
	val INTERFACE_OPENED: EventType<InterfaceChanged> = EventType(27, "InterfaceOpened")

	/** An interface is closing; its components are still there. */
	@JvmField
	val INTERFACE_CLOSED: EventType<InterfaceChanged> = EventType(28, "InterfaceClosed")

	/** The root interface changed: login, the lobby, the fixed and the resizable game screen. */
	@JvmField
	val TOP_INTERFACE_CHANGED: EventType<TopInterfaceChanged> = EventType(29, "TopInterfaceChanged")

	/**
	 * Every game tick while the menu is not open: the entries for what is under the mouse are
	 * complete, and the left click and the right-click menu are resolved after the listeners.
	 * The time to read and edit the menu through the ClientApi menu methods.
	 */
	@JvmField
	val MENU_BUILT: EventType<MenuBuilt> = EventType(30, "MenuBuilt")

	const val COUNT: Int = 31

	/** Every event type, at the index of its id. */
	@JvmField
	val ALL: Array<EventType<*>> = arrayOf(CLIENT_TICK, GAME_TICK, VIEWPORT_DRAW, FRAME_DRAW, STAT_CHANGED, VARP_CHANGED, CHAT_MESSAGE, ITEM_CONTAINER_CHANGED, MENU_ENTRY_ADDED, MENU_OPTION_CLICKED, GAME_STATE_CHANGED, TOOLKIT_CHANGED, CONSOLE_COMMAND, KEY_PRESSED, MOUSE_CLICKED, NPC_SPAWNED, NPC_DESPAWNED, PLAYER_SPAWNED, PLAYER_DESPAWNED, ITEM_SPAWNED, ITEM_DESPAWNED, HITSPLAT_APPLIED, ANIMATION_CHANGED, INTERACTING_CHANGED, OVERHEAD_TEXT_CHANGED, PROJECTILE_SPAWNED, SPOT_ANIM_SPAWNED, INTERFACE_OPENED, INTERFACE_CLOSED, TOP_INTERFACE_CHANGED, MENU_BUILT)
}
