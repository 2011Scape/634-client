package com.jagex.game.runetek5.plugin.api

/**
 * An entry of the right-click menu, as [ClientApi.menuEntry] hands it out while the menu is being
 * built. [option] is the verb ("Attack", "Drop", "Walk here"), [target] the coloured name of what it
 * acts on, [clickType] the client's action id (a plugin's entries have one of their own).
 */
interface MenuEntry {
	/** The verb: "Attack", "Drop", "Walk here". */
	fun option(): String?

	/** The name of what the verb acts on, with its colour tags. */
	fun target(): String?

	/** The client's action id; a plugin's entries carry the click type their [MenuAction] got. */
	fun clickType(): Int

	/** For an entry on an interface component, the component's [Widget.id]. */
	fun param1(): Int

	/** For an entry on an interface component, its slot; for a scene entry, the tile's x. */
	fun param0(): Int

	/** The item the entry is about, or -1. */
	fun itemId(): Int

	/** What the client identifies the target by: an npc's index, a player's index, a component's option number. */
	fun entityUid(): Long
}
