package com.jagex.game.runetek5.plugin.api

/**
 * The version of this API. A plugin's jar names the version it was built against
 * (`Plugin-Api-Version` in its manifest); the client refuses a plugin built against a newer one.
 *
 * The number goes up with every release that adds to the API, so that a plugin using the new parts
 * is refused by an older client with a clear message instead of failing inside. Nothing is ever
 * removed or changed in a way that breaks a plugin built against a lower version: a plugin built
 * against version N runs on every client whose API is version N or higher.
 *
 * - 1: the first release (client 2.13.0).
 * - 2: actor state (animation, graphic, interaction, facing, overhead text, hit splats, icons),
 *   item, npc and location definitions, the locations and movement flags of a tile, and the
 *   events from [events.Events.NPC_SPAWNED] on.
 * - 3: interface components in detail and on the screen, the open interfaces, menu entries on
 *   components, the menu edited (removed, promoted, renamed) at [events.Events.MENU_BUILT], a
 *   click consumed, and the events from [events.Events.INTERFACE_OPENED] on. MenuOptionClicked's option
 *   is the verb and its target the coloured name, as documented; a plugin that declares API 1 still
 *   gets what API 1 gave it (the name as the option, a player's name or null as the target).
 * - 4: sprites, item icons, circles, polygons, thick lines, clipping and fonts in [Graphics]; keys
 *   and clicks consumed, KeyTyped, KeyReleased and MouseWheel; the mouse, held keys and the hovered
 *   tile; the camera and the minimap; run energy, weight, world, region and membership; sound
 *   effects; the friends, ignore and friends chat lists; [ChatType]; and work scheduled onto the
 *   game thread from a tick or from another thread ([PluginContext.invokeLater]).
 * - 5: [Player.isAttackable] and [Player.teamId]; [ClientApi.friendsChatName], [ClientApi.isFriend]
 *   and [ClientApi.isInFriendsChat].
 * - 6: [PluginContext.settings], the [SettingsSchema] a plugin declares for the plugin manager, and
 *   [events.Events.SETTING_CHANGED].
 * - 7: [SettingsSchema.text]; [Actor.tileX], [Actor.tileY], [Actor.serverTileX], [Actor.serverTileY]
 *   and [Actor.isDying]; [ClientApi.actorHull], [ClientApi.projectActorMinimap] and
 *   [ClientApi.retargetMenuEntry].
 * - 8: [ClientApi.isFocused], [ClientApi.notify] (a notification on the desktop) and [ClientApi.requestFocus] (the window to the front).
 * - 9: [ClientApi.itemPrice] (Grand Exchange guide prices) and [ClientApi.itemHaPrice]; [ItemDefinition.isTradeable];
 *   [events.GroundItem.ownership], [ClientApi.groundItemHeight] and [ClientApi.isInInstance]; [ClientApi.deprioritizeMenuEntry]; submenus
 *   ([ClientApi.addSubmenu], [ClientApi.addSubmenuEntry]); [SettingsSchema.colour], [SettingsSchema.keybind],
 *   [PluginConfig.getColour], [PluginConfig.setColour] and [KeyCode.name]; [PluginContext.pickColour].
 * - 10: a location's shape on the screen: [ClientApi.locHull], [ClientApi.locClickbox] and
 *   [ClientApi.locOutline]; [ClientApi.resolveLoc] for a location that changes with the player's variables.
 * - 11: [Actor.healthScale], whether an actor's health bar shows and what a full one is.
 * - 12: overlays the client places and the player moves: [PanelOverlay] (its [OverlayPosition], [OverlayOrigin]
 *   and [OverlayMenuEntry]s), [OverlayPanel] and [PanelComponent]; infoboxes ([InfoBox], [Counter], [Timer],
 *   [PluginContext.addInfoBox]); [Graphics.translate], [Graphics.image] and [ClientApi.loadImage]; [ClientApi.addTooltip].
 * - 13: quests: [ClientApi.questCount], [ClientApi.questName] and [ClientApi.questState] ([QuestState]).
 * - 14: [InfoBox.itemId], an item's icon as an infobox's picture.
 * - 15: the walk destination, [ClientApi.destinationTileX] and [ClientApi.destinationTileY].
 * - 16: panel tooltips, [ClientApi.addTooltip] with a [PanelComponent].
 */
object PluginApi {
	/** This client's API version. */
	const val VERSION: Int = 16
}
