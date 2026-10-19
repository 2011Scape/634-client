package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.MENU_OPTION_CLICKED]: the entry the player clicked, before the client acts on it.
 * [option] is the entry's text ("Walk here", "Attack"...), [target] what it names, [clickType] the
 * client's action id. One reused instance; the click cannot be cancelled.
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
}
