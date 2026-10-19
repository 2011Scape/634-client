package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.MenuTarget
import kotlin.jvm.JvmField

/**
 * See [Events.MENU_ENTRY_ADDED]: the client is adding the entries for [target] at scene tile
 * ([sceneX], [sceneY]) on [level]; for an npc or a player bigger than a tile, its south-west tile. [id] is an npc's index, a player's index, an item id or a location
 * id by the target's kind, and 0 for a tile. Call
 * [ClientApi.addMenuEntry][com.jagex.game.runetek5.plugin.api.ClientApi.addMenuEntry] in the
 * listener to add an entry for it. One reused instance.
 */
class MenuEntryAdded {
	@JvmField
	var target: MenuTarget = MenuTarget.TILE

	@JvmField
	var sceneX: Int = 0

	@JvmField
	var sceneY: Int = 0

	@JvmField
	var level: Int = 0

	@JvmField
	var id: Int = 0
}
