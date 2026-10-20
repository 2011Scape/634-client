package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.MENU_OPTION_CLICKED]: the entry the player clicked, before the client acts on it.
 * [option] is the entry's verb ("Walk here", "Attack"...), [target] the coloured name of what it
 * acts on, [clickType] the client's action id, [itemId] the item it is about or -1. [consume] stops
 * the client from acting on it. A plugin that declares API 1 in its manifest gets the fields as API 1
 * filled them: the coloured name as [option], a player's name or null as [target]. One reused instance.
 */
class MenuOptionClicked {
	@JvmField
	var clickType: Int = 0

	@JvmField
	var option: String? = null

	@JvmField
	var target: String? = null

	@JvmField
	var param0: Int = 0

	@JvmField
	var param1: Int = 0

	@JvmField
	var entityUid: Long = 0L

	@JvmField
	var itemId: Int = -1

	@JvmField
	var consumed: Boolean = false

	/** Drops the click: the client does nothing with it. */
	fun consume() {
		consumed = true
	}
}
