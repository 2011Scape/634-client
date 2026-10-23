package com.jagex.game.runetek5.plugin.api

/**
 * An interface component. [x] and [y] are where it sits inside its parent; [screenX] and [screenY]
 * where it was last drawn on the screen. A component from the cache has a [parentId] and no [slot];
 * a dynamic one (an inventory's item slots, a list's rows) is a child of a container, at [slot] in
 * its [child] list, with a [parentId] of its own.
 */
interface Widget {
	fun interfaceId(): Int

	fun componentId(): Int

	/** The component's id as the client and the menu carry it: interface id shifted left 16, or component id. */
	fun id(): Int

	/** What the component is, a [WidgetType]. */
	fun type(): Int

	/** The [id] of the component this one is inside, or -1 for one at the top of its interface. */
	fun parentId(): Int

	/** The index of a dynamic child in its parent, or -1 for a component from the cache. */
	fun slot(): Int

	fun text(): String?

	/** The offset from the parent's content, before the parent's scroll. */
	fun x(): Int

	fun y(): Int

	/** Where the component's left edge is on the screen: its offset and its parents', less their scroll. */
	fun screenX(): Int

	fun screenY(): Int

	fun width(): Int

	fun height(): Int

	fun isHidden(): Boolean

	/**
	 * Hides the component (and what is inside it) or shows it again, on this client only, as RuneLite's
	 * Widget.setHidden: the server and the interface's scripts may show it again, so a plugin that keeps
	 * one hidden sets it each tick, and shows it again when it stops. Since API 17.
	 */
	fun setHidden(hidden: Boolean)

	/**
	 * How many client cycles (20 ms each) the mouse must be held on the component before it can be
	 * dragged, RuneLite's drag dead time: the game's from the cache or its scripts until a plugin sets
	 * it. Since API 18.
	 */
	fun dragDeadTime(): Int

	/** Sets [dragDeadTime] on this client: RuneLite's setDragDeadTime, Anti-Drag's. Since API 18. */
	fun setDragDeadTime(cycles: Int)

	/** The item the component shows (an inventory slot, a shop's stock), or -1. */
	fun itemId(): Int

	/** How many of [itemId] the component shows. */
	fun itemQuantity(): Int

	/** The sprite the component shows, or -1. */
	fun spriteId(): Int

	/** The model the component shows, or -1. */
	fun modelId(): Int

	/** The component's colour, as 0xRRGGBB. */
	fun colour(): Int

	/** How see-through the component is: 0 opaque to 255 invisible. */
	fun transparency(): Int

	/** How many right-click options the component may have; [action] takes 0 until this. */
	fun actionCount(): Int

	/** The right-click option [i] ("Wield", "Drop"...), or null when there is none. */
	fun action(i: Int): String?

	/** What the menu names the component by: an item's name in an inventory, a button's label. */
	fun targetText(): String?

	/** The component's name in the cache, or null. */
	fun name(): String?

	fun scrollX(): Int

	fun scrollY(): Int

	fun scrollWidth(): Int

	fun scrollHeight(): Int

	/** The client's content type for a special component (the minimap, the compass...), 0 for none. */
	fun contentType(): Int

	/** How many dynamic children the component has; [child] takes 0 until this. */
	fun childCount(): Int

	fun child(i: Int): Widget?

	/** Calls [visitor] with every component from the cache whose parent is this one. */
	fun staticChildren(visitor: WidgetVisitor)
}
