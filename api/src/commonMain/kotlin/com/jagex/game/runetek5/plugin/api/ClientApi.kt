package com.jagex.game.runetek5.plugin.api

/**
 * The client as a plugin sees it: the state of the game to read, a way to draw and to speak, and
 * the menu to add to. Everything here is for the game thread, which is where every plugin callback
 * runs. Nothing here sends to the server or moves the player: a plugin reads and shows.
 */
interface ClientApi {
	fun gameState(): GameState

	/** The client's cycle counter: 50 a second, from startup. */
	fun clientCycle(): Int

	/** The server's tick counter, 1 every 600 ms while logged in. */
	fun serverTick(): Int

	fun localPlayer(): Player?

	/** How many players the last update put in the scene; [player] takes 0 until this. */
	fun playerCount(): Int

	fun player(i: Int): Player?

	/** How many npcs the last update put in the scene; [npc] takes 0 until this. */
	fun npcCount(): Int

	fun npc(i: Int): Npc?

	/** Calls [visitor] for every item on the ground in the scene, pile by pile, most valuable first. */
	fun groundItems(visitor: GroundItemVisitor)

	fun skillXp(skill: Int): Int

	/** The current (boosted or drained) level. */
	fun skillLevel(skill: Int): Int

	/** The level the experience gives. */
	fun skillBaseLevel(skill: Int): Int

	fun varp(id: Int): Int

	fun varbit(id: Int): Int

	/** The inventory with [id] (93 is the backpack), or null when the server has not sent it. */
	fun itemContainer(id: Int): ItemContainer?

	/** The component, or null when the interface is not loaded. */
	fun widget(interfaceId: Int, componentId: Int): Widget?

	fun itemName(id: Int): String?

	fun npcName(id: Int): String?

	fun locName(id: Int): String?

	/** The world tile of the scene's south-west corner. */
	fun sceneBaseX(): Int

	fun sceneBaseY(): Int

	/** The scene's width and height in tiles. */
	fun sceneSize(): Int

	/** The graphics mode: 0 Java, 1 OpenGL, 2 native software, 3 DirectX, 5 native OpenGL, 6 634HD. */
	fun toolkitId(): Int

	/**
	 * Where a point in the scene is in the game view. [sceneX] and [sceneY] are in scene units (512 a
	 * tile), [heightOffset] how far above the ground. Writes the view-relative x and y into [out]
	 * (two ints) and returns true, or returns false when the point is off the screen.
	 */
	fun projectPoint(sceneX: Int, sceneY: Int, level: Int, heightOffset: Int, out: IntArray): Boolean

	/** Where the middle of a tile is in the game view; see [projectPoint]. */
	fun projectTile(tileX: Int, tileY: Int, level: Int, heightOffset: Int, out: IntArray): Boolean

	/** Where the point [heightOffset] above the ground under [actor] is in the game view; see [projectPoint]. */
	fun projectActor(actor: Actor, heightOffset: Int, out: IntArray): Boolean

	/** Writes a line to the chat box, as a game message. Dropped when not logged in. */
	fun chatMessage(text: String)

	/** Writes a line to the developer console. */
	fun consolePrint(text: String)

	/**
	 * Adds an entry to the right-click menu being built. Only works inside a
	 * [MenuEntryAdded][com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded] listener; the entry
	 * is for that event's target, and clicking it runs the [MenuAction] [clickType] was registered for.
	 */
	fun addMenuEntry(label: String, clickType: Int)
}
