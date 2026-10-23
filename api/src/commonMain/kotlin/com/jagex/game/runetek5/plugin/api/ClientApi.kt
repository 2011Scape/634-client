package com.jagex.game.runetek5.plugin.api

/**
 * The client as a plugin sees it: the state of the game to read, a way to draw and to speak, and
 * the menu to add to. Everything here is for the game thread, which is where every plugin callback
 * runs. Nothing here sends to the server or moves the player: a plugin reads and shows.
 */
interface ClientApi {
	/** Where the client is: loading, the login screen, the lobby or the game. */
	fun gameState(): GameState

	/** The client's cycle counter: 50 a second, from startup. */
	fun clientCycle(): Int

	/** How many client cycles (20 ms) have passed while logged in; a game tick of the server is 30 of them. */
	fun serverTick(): Int

	/** The player, or null before login. */
	fun localPlayer(): Player?

	/** How many players the last update put in the scene; [player] takes 0 until this. */
	fun playerCount(): Int

	/** The [i]th player in the scene (0 until [playerCount]), or null. */
	fun player(i: Int): Player?

	/** How many npcs the last update put in the scene; [npc] takes 0 until this. */
	fun npcCount(): Int

	/** The [i]th npc in the scene (0 until [npcCount]), or null. */
	fun npc(i: Int): Npc?

	/** The npc with [index] ([Npc.index], what the menu events carry), or null when it is not in the scene. */
	fun npcByIndex(index: Int): Npc?

	/** The player with [index] ([Player.index]), or null when not in the scene. */
	fun playerByIndex(index: Int): Player?

	/** Calls [visitor] for every item on the ground in the scene, pile by pile, most valuable first. */
	fun groundItems(visitor: GroundItemVisitor)

	/** The experience in [skill] (a [Skill] constant). */
	fun skillXp(skill: Int): Int

	/** The current (boosted or drained) level. */
	fun skillLevel(skill: Int): Int

	/** The level the experience gives. */
	fun skillBaseLevel(skill: Int): Int

	/** The player variable [id], 0 when unknown. */
	fun varp(id: Int): Int

	/** The bit variable [id] (a part of a varp, as the cache defines it), 0 when unknown. */
	fun varbit(id: Int): Int

	/** The inventory with [id] (93 is the backpack, which interface 149 component 0 shows), or null when the server has not sent it. */
	fun itemContainer(id: Int): ItemContainer?

	/** The component, or null when the interface is not loaded. */
	fun widget(interfaceId: Int, componentId: Int): Widget?

	/** The item's name; [item] has the rest of its type. */
	fun itemName(id: Int): String?

	/** The npc type's name; [npcType] has the rest. */
	fun npcName(id: Int): String?

	/** The location type's name; [locType] has the rest. */
	fun locName(id: Int): String?

	/** The world tile of the scene's south-west corner. */
	fun sceneBaseX(): Int

	/** The world tile of the scene's south-west corner, south to north. */
	fun sceneBaseY(): Int

	/** The scene's width and height in tiles. */
	fun sceneSize(): Int

	/** The graphics mode: 0 Java, 1 OpenGL, 2 native software, 3 DirectX, 5 native OpenGL, 6 634HD. */
	fun toolkitId(): Int

	/**
	 * Where a point in the scene is in the game view. [sceneX] and [sceneY] are in scene units (512 a
	 * tile, [heightOffset] how far above the ground. Writes the x and y relative to the game view into
	 * [out] (two ints) and returns true, or returns false when the point is off the screen. The
	 * view sits at the [Viewport]'s x and y on the screen: add them to draw there (they are 0 on the
	 * resizable game screen, not on the fixed one).
	 */
	fun projectPoint(sceneX: Int, sceneY: Int, level: Int, heightOffset: Int, out: IntArray): Boolean

	/** Where the middle of a tile is in the game view; see [projectPoint]. */
	fun projectTile(tileX: Int, tileY: Int, level: Int, heightOffset: Int, out: IntArray): Boolean

	/** Where the point [heightOffset] above the ground under [actor] is in the game view; see [projectPoint]. */
	fun projectActor(actor: Actor, heightOffset: Int, out: IntArray): Boolean

	/**
	 * The outline of the actor's body on the screen this frame: the convex hull of its model's
	 * bounding box, view-relative like [projectActor], written into [xs] and [ys] (8 or more long)
	 * and its point count returned, going round the shape; 0 when the actor was not drawn this
	 * frame or part of it is off the screen. Since API 7.
	 */
	fun actorHull(actor: Actor, xs: IntArray, ys: IntArray): Int

	/** Where the actor is on the minimap, to the fraction of a tile, in screen coordinates; false when the minimap is not shown or it is off its edge. Since API 7. */
	fun projectActorMinimap(actor: Actor, out: IntArray): Boolean

	/**
	 * The four corners of the [size] by [size] tiles whose south-west tile is ([tileX], [tileY]) in
	 * the game view, for [Graphics.polygon] and [Graphics.fillPolygon]: the x values into [xs] and the
	 * y values into [ys] (four ints each), south-west first and clockwise, relative to the game view like
	 * [projectPoint]. False when any corner is off the screen.
	 */
	fun tilePolygon(tileX: Int, tileY: Int, level: Int, size: Int, xs: IntArray, ys: IntArray): Boolean

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

	/** The item type with [id], or null before the cache's types load. */
	fun item(id: Int): ItemDefinition?

	/** The npc type with [id], or null before the cache's types load. */
	fun npcType(id: Int): NpcDefinition?

	/** The location type with [id], or null before the cache's types load. */
	fun locType(id: Int): LocDefinition?

	/**
	 * Calls [visitor] for every location on scene tile ([tileX], [tileY]) of floor [level]: its
	 * walls, their decorations, the floor decoration and the objects standing on it. An object
	 * larger than a tile is on every tile it covers. Nothing for a tile outside the scene.
	 */
	fun locsAt(tileX: Int, tileY: Int, level: Int, visitor: LocVisitor)

	/** The tile's movement flags, bits of [CollisionFlags]; 0 for a tile outside the scene. */
	fun collisionFlags(tileX: Int, tileY: Int, level: Int): Int

	/** The component with [id] ([Widget.id]), or null when its interface is not loaded. */
	fun widgetById(id: Int): Widget?

	/** The root interface: 548 on the fixed game screen, 746 on the resizable one, -1 for none. */
	fun topInterface(): Int

	/** Calls [visitor] for every interface open inside a component. */
	fun openInterfaces(visitor: InterfaceVisitor)

	/**
	 * How many entries the right-click menu being built has. The menu methods work inside a
	 * [MenuBuilt][com.jagex.game.runetek5.plugin.api.events.MenuBuilt] listener only; elsewhere this is 0
	 * and the others do nothing.
	 */
	fun menuEntryCount(): Int

	/** The entry at [i], 0 being the top row and the left click's choice, or null. */
	fun menuEntry(i: Int): MenuEntry?

	/** Takes entry [i] out of the menu. False for Cancel, or outside MenuBuilt. */
	fun removeMenuEntry(i: Int): Boolean

	/**
	 * Makes entry [i] the top row, and so the left click's choice. A component's options 6 to 10 stay
	 * where they are (on the top row they would turn the left click into a right click) but take the
	 * left click all the same; entry 0 is left as it is. False for Cancel, or outside MenuBuilt.
	 */
	fun promoteMenuEntry(i: Int): Boolean

	/** Changes entry [i]'s [option][MenuEntry.option] text. False outside MenuBuilt. */
	fun renameMenuEntry(i: Int, option: String): Boolean

	/** Gives entry [i] another target text (the coloured name after the verb, colour tags and all), while the menu is being built. Since API 7. */
	fun retargetMenuEntry(i: Int, target: String): Boolean

	/** Where the mouse is on the screen. */
	fun mouseX(): Int

	/** Where the mouse is on the screen, from the top. */
	fun mouseY(): Int

	/** Whether mouse button [button] (0 left, 1 middle, 2 right) is held down. */
	fun isMouseButtonDown(button: Int): Boolean

	/** Whether the key with the client's [keyCode] ([events.KeyPressed.keyCode]) is held down. */
	fun isKeyHeld(keyCode: Int): Boolean

	/** The scene tile under the mouse this cycle, or -1 when the mouse is not over the ground. */
	fun hoveredTileX(): Int

	/** See [hoveredTileX]. */
	fun hoveredTileY(): Int

	/** The camera's yaw as the game counts it, 16384 to a full turn. */
	fun cameraYaw(): Int

	/** The camera's pitch, 1024 (level) to 3072 (overhead). */
	fun cameraPitch(): Int

	/** The camera's position in scene units (512 to a tile). */
	fun cameraX(): Int

	/** The camera's position south to north, in scene units. */
	fun cameraZ(): Int

	/** The camera's height, in the scene's units; smaller is higher. */
	fun cameraHeight(): Int

	/** The minimap's place on the screen, x, y, width and height into [out] (four ints); false while no minimap is drawn. */
	fun minimapArea(out: IntArray): Boolean

	/** Where the middle of scene tile ([tileX], [tileY]) is on the minimap: the screen x and y into [out]; false when off the minimap or no minimap is drawn. */
	fun projectMinimap(tileX: Int, tileY: Int, out: IntArray): Boolean

	/** Run energy, 0 to 100. */
	fun runEnergy(): Int

	/** The weight carried, in kilograms. */
	fun weight(): Int

	/** The world the client is on, -1 before login. */
	fun worldId(): Int

	/** The 64 by 64 tile map region the player is in: its x shifted left 8, or its y; -1 when not logged in. */
	fun regionId(): Int

	/** Whether the account is a member. */
	fun isMember(): Boolean

	/** Plays sound effect [id] from the cache at [volume] 0 to 255, as the game's own effects play (and not at all when the player turned them off). */
	fun playSound(id: Int, volume: Int)

	/** Calls [visitor] for every friend on the list. */
	fun friends(visitor: NameVisitor)

	/** Calls [visitor] for every name on the ignore list (world 0, rank -1). */
	fun ignores(visitor: NameVisitor)

	/** Calls [visitor] for every member of the friends chat the player is in. */
	fun clanMembers(visitor: NameVisitor)

	/** The name of the friends chat the player is in, or null outside one. */
	fun friendsChatName(): String?

	/** Whether [name] (a [Player.name], compared ignoring case) is on the friends list. */
	fun isFriend(name: String?): Boolean

	/** Whether [name] (a [Player.name], compared ignoring case) is in the player's friends chat. */
	fun isInFriendsChat(name: String?): Boolean

	/** Whether the game's window (the canvas, in a browser) has the keyboard focus, as the client reports it to the server. Since API 8. */
	fun isFocused(): Boolean

	/**
	 * Shows [text] as a notification on the desktop, titled with the client's name: the desktop's own
	 * notifications on Linux (notify-send), a balloon from the system tray elsewhere on the JVM, a
	 * browser notification on the web (the first call asks the player's permission and shows nothing).
	 * The window is not raised; see [requestFocus]. True when the platform took it; false when it
	 * cannot (no notify-send and no system tray, a headless JVM, permission refused), so that a plugin
	 * can fall back to the chat. Since API 8.
	 */
	fun notify(text: String): Boolean

	/**
	 * Asks the desktop to bring the game's window to the front and give it the keyboard focus, as a
	 * notification the player should act on. The desktop may refuse and only mark the window as
	 * wanting attention (a highlighted task bar entry), which is its choice; a browser focuses its
	 * own window where it allows a page to. True when there was a window to ask for. Since API 8.
	 */
	fun requestFocus(): Boolean

	/**
	 * The Grand Exchange guide price of item [id], in coins, for one of it; a noted item is priced as
	 * the item it notes, coins are 1. The prices come from the game's website (`/items/prices.json`),
	 * fetched in the background when the client starts and every hour after, and kept on disk between
	 * runs; -1 while they have not arrived, for an item the exchange does not price, and in a client
	 * started without them (`--prices none`). Since API 9.
	 */
	fun itemPrice(id: Int): Int

	/**
	 * What High Level Alchemy gives for one of item [id]: its value times 0.6, rounded down, as
	 * RuneLite reckons it; a noted item is priced as the item it notes, coins are 1. 0 for an unknown item. Since API 9.
	 */
	fun itemHaPrice(id: Int): Int

	/**
	 * How far above the ground the pile on scene tile ([sceneX], [sceneY]) lies, in scene units
	 * (positive up): the top of the table or counter it is on, 0 on the floor or when the tile has no pile. Since API 9.
	 */
	fun groundItemHeight(sceneX: Int, sceneY: Int, level: Int): Int

	/**
	 * Whether the player is in an instance: a copy of an area built for them (a boss room, a
	 * minigame, Daemonheim), where items stay private and keep longer. Since API 9.
	 */
	fun isInInstance(): Boolean

	/**
	 * Sinks entry [i] of the menu being built (at [events.Events.MENU_BUILT]) below every entry that is
	 * not sunk, as the game sinks Attack on a weaker player when the player chose so: it is never the
	 * left click while another entry is there. True when there was such an entry. Since API 9.
	 */
	fun deprioritizeMenuEntry(i: Int): Boolean

	/**
	 * Adds an entry labelled [label] that opens a submenu when the mouse is on it, for the target the
	 * menu is being built for (inside a [events.MenuEntryAdded] listener, as [addMenuEntry]); fill it with
	 * [addSubmenuEntry]. Clicking the entry itself does nothing. The result names the submenu; -1 when
	 * no menu is being built. Since API 9.
	 */
	fun addSubmenu(label: String): Int

	/**
	 * Adds an entry labelled [label] to [submenu] (from [addSubmenu]), shown top to bottom in the order
	 * added; [clickType] is from [PluginContext.registerMenuAction], and the click reaches its
	 * [MenuAction] as an entry of the main menu would. Since API 9.
	 */
	fun addSubmenuEntry(submenu: Int, label: String, clickType: Int)

	/**
	 * What location [baseId] is now: a location that changes with the player's variables (a door
	 * that is open, a tree that is a stump, a farming patch) is another location type at times; this
	 * is that type's id, or [baseId] itself when it does not change, -1 for none. [MenuEntryAdded]
	 * names a location by this id, [locsAt] by its base one. Since API 10.
	 */
	fun resolveLoc(baseId: Int): Int

	/**
	 * The convex hull of location [baseId] (its base or its current id) on scene tile ([tileX],
	 * [tileY]) as drawn this frame, view-relative like [projectPoint], into [xs] and [ys]; the point
	 * count (up to their size), 0 when it is not on the screen. Since API 10.
	 */
	fun locHull(tileX: Int, tileY: Int, level: Int, baseId: Int, xs: IntArray, ys: IntArray): Int

	/** The game's own click box for the location, as [locHull] gives a hull: the area a click on it lands in. Since API 10. */
	fun locClickbox(tileX: Int, tileY: Int, level: Int, baseId: Int, xs: IntArray, ys: IntArray): Int

	/**
	 * Draws the location's outline, the line around its model's shape on the screen [width] pixels
	 * wide in [argb], its outer [feather] pixels fading (RuneLite's model outline); only inside an
	 * overlay's or a frame listener's draw. False when it is not on the screen. Since API 10.
	 */
	fun locOutline(tileX: Int, tileY: Int, level: Int, baseId: Int, width: Int, feather: Int, argb: Int): Boolean

	/**
	 * A picture from the bytes of a PNG file (a plugin's own resource, say), for [Graphics.image] and an
	 * [InfoBox]; null when the bytes are not a PNG the client can read. Since API 12.
	 */
	fun loadImage(png: ByteArray): Image?

	/** A picture [width] by [height] of [argb] pixels, row by row (RuneLite's BufferedImage); null for a size out of range. Since API 19. */
	fun createImage(width: Int, height: Int, argb: IntArray): Image?

	/**
	 * Adds [image] to the chat icons, RuneLite's ChatIconManager: the number to write as `<img=N>` in a
	 * chat line, an overhead line or interface text to show it; -1 while the game's own icons are not
	 * loaded yet (ask again once logged in). The icons stay for the session. Since API 19.
	 */
	fun addChatIcon(image: Image): Int

	/** Client variable [id] (a varc, the interfaces' own), as a number; 0 for one not set. Since API 20. */
	fun varcInt(id: Int): Int

	/** Client variable [id] as text (the chat box's typed line is one); null for one not set. Since API 20. */
	fun varcString(id: Int): String?

	/** Sets text client variable [id] on this client, as the interfaces' scripts do. Since API 20. */
	fun setVarcString(id: Int, value: String?)

	/**
	 * Shows [image] as the mouse cursor over the game, its point at ([hotX], [hotY]), instead of the game's
	 * own cursors (RuneLite's ClientUI.setCursor); [resetCursor] gives them back. Since API 21.
	 */
	fun setCursor(image: Image, hotX: Int, hotY: Int)

	/** The game's own cursors back. Since API 21. */
	fun resetCursor()

	/**
	 * Item [itemId]'s icon as a picture (RuneLite's ItemManager.getImage), with [quantity] shown as the
	 * game shows a stack; null while its model is not loaded yet (ask again later). Since API 21.
	 */
	fun itemImage(itemId: Int, quantity: Int): Image?

	/**
	 * Sprite [spriteId] of the cache (its first frame, the game's own icons and such) as a picture, for
	 * [addChatIcon], [Graphics.image] and an [InfoBox] (RuneLite's SpriteManager.getSprite); null when the
	 * cache has no such sprite or it is not loaded yet. Since API 23.
	 */
	fun spriteImage(spriteId: Int): Image?

	/**
	 * Draws [image] wherever an interface shows sprite [spriteId] of the cache, as RuneLite's sprite
	 * overrides (Client.getSpriteOverrides); null gives the cache's own back. Since API 24.
	 */
	fun overrideSprite(spriteId: Int, image: Image?)

	/**
	 * Shows [text] in a tooltip box by the mouse this frame, over everything, as RuneLite's TooltipManager:
	 * call it while drawing, each frame it is to show. `</br>` breaks a line and `<col=rrggbb>` colours
	 * what follows. Since API 12.
	 */
	fun addTooltip(text: String)

	/**
	 * Shows [panel] as a tooltip by the mouse this frame, over everything, with the text tooltips: RuneLite's
	 * Tooltip with a PanelComponent (lines with a label on the left and a value on the right, its preferred
	 * width). Call it while drawing, each frame it is to show; the panel is drawn as it stands then. Since API 16.
	 */
	fun addTooltip(panel: PanelComponent)

	/** How many quests the cache has: the quest ids are 0 until this. Since API 13. */
	fun questCount(): Int

	/** Quest [id]'s name, as the quest journal lists it; null for an id the cache does not have. Since API 13. */
	fun questName(id: Int): String?

	/**
	 * Where the player is with quest [id], a [QuestState], worked out as the game's quest journal does
	 * from the quest's progress variables in the cache: each is finished at or past its end value and
	 * started at or past its start. [QuestState.NOT_STARTED] for an id the cache does not have. Since API 13.
	 */
	fun questState(id: Int): Int

	/**
	 * The scene tile the player is walking to, the minimap's flag: its x and y, -1 when there is none
	 * (RuneLite's local destination). On the local player's level. Since API 15.
	 */
	fun destinationTileX(): Int

	fun destinationTileY(): Int
}
