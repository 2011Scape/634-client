package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.MenuTarget
import kotlin.jvm.JvmField

/**
 * See [Events.MENU_ENTRY_ADDED]: the client is adding the entries for [target] at scene tile
 * ([sceneX], [sceneY]) on [level]; for an npc or a player bigger than a tile, its south-west tile. [id] is an npc's index, a player's index, an item id or a location
 * id by the target's kind, and 0 for a tile. Call
 * [ClientApi.addMenuEntry][com.jagex.game.runetek5.plugin.api.ClientApi.addMenuEntry] in the
 * listener to add an entry for it. For [MenuTarget.WIDGET] the tile fields are 0 and [widgetId],
 * [slot] and [itemId] say which component, which of its slots and which item. One reused instance.
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

	/** The component's [Widget.id][com.jagex.game.runetek5.plugin.api.Widget.id] for [MenuTarget.WIDGET], else 0. */
	@JvmField
	var widgetId: Int = 0

	/** The component's slot for [MenuTarget.WIDGET], -1 for none. */
	@JvmField
	var slot: Int = -1

	/** The item for [MenuTarget.WIDGET] and [MenuTarget.GROUND_ITEM], -1 for none. */
	@JvmField
	var itemId: Int = -1
}
