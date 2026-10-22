package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField

/**
 * An overlay the client places, RuneLite's Overlay with a [position]: give it to
 * [PluginContext.addOverlay] and the client draws it each frame in its corner of the game view, under
 * the panels before it, or where the player put it. The player holds Alt (the Overlays settings' drag
 * key) to see every panel's outline: dragging one moves it, dropping it on a corner snaps it there,
 * dragging a [resizable] one's edge resizes it, and Alt and a right click puts it back. Where it was put
 * is kept by its plugin's id and [name], so the name must stay the same between versions.
 *
 * [render] draws with its top-left corner at (0, 0), the client having moved the drawing there, and
 * says how big it drew; [Overlay.draw] is never called. Since API 12.
 */
abstract class PanelOverlay(
	/** What the client keeps the panel's place under, with the plugin's id: RuneLite's getName. */
	@JvmField val name: String,
) : Overlay {
	@JvmField
	var position: OverlayPosition = OverlayPosition.TOP_LEFT

	/** Which panels of a corner come first, nearest the corner: the higher first ([PRIORITY_HIGH] before [PRIORITY_LOW]). */
	@JvmField
	var priority: Float = PRIORITY_DEFAULT

	/** Whether the player can drag it. */
	@JvmField
	var movable: Boolean = true

	/** Whether dropping it on a corner snaps it there. */
	@JvmField
	var snappable: Boolean = true

	/** Whether Alt and a right click put it back. */
	@JvmField
	var resettable: Boolean = true

	/** Whether the player can drag its edges: [preferredWidth] and [preferredHeight] are then what they chose. */
	@JvmField
	var resizable: Boolean = false

	/** The smallest a resize makes it, both ways. */
	@JvmField
	var minimumSize: Int = 32

	/** Whether another drag-targetable panel can be dropped on it ([onDrag]). */
	@JvmField
	var dragTargetable: Boolean = false

	/** The corner the player snapped it to, or null for its own [position]. Set by the client. */
	@JvmField
	var preferredPosition: OverlayPosition? = null

	/** Whether the player put it somewhere of their own: [preferredX] and [preferredY] from its [origin]. Set by the client. */
	@JvmField
	var hasPreferredLocation: Boolean = false

	@JvmField
	var preferredX: Int = 0

	@JvmField
	var preferredY: Int = 0

	/** The size the player gave it (0 for none), for a [resizable] one. Set by the client. */
	@JvmField
	var preferredWidth: Int = 0

	@JvmField
	var preferredHeight: Int = 0

	@JvmField
	var origin: OverlayOrigin = OverlayOrigin.AUTO

	@JvmField
	var originX: OverlayOriginX = OverlayOriginX.LEFT

	@JvmField
	var originY: OverlayOriginY = OverlayOriginY.TOP

	/** Where it is on the screen this frame (its size the last [render]'s), set by the client before [render]. */
	@JvmField
	var boundsX: Int = 0

	@JvmField
	var boundsY: Int = 0

	@JvmField
	var boundsWidth: Int = 0

	@JvmField
	var boundsHeight: Int = 0

	/** The panel background the player chose (the Overlays settings), set by the client before [render]. */
	@JvmField
	var backgroundColour: Int = STANDARD_BACKGROUND

	private val menuEntries: ArrayList<OverlayMenuEntry> = ArrayList()

	/** Draws this frame at (0, 0) and returns the size drawn; null or an empty size for nothing drawn. */
	abstract fun render(g: Graphics, view: Viewport): Size?

	/** Laid out by the client through [render]: never called for a panel. */
	final override fun draw(g: Graphics, view: Viewport) {
		// nothing: the client places the panel and calls render
	}

	/** Adds an entry the player gets on shift-right-clicking the panel. */
	fun addMenuEntry(option: String, target: String, action: Task) {
		menuEntries.add(OverlayMenuEntry(option, target, action))
	}

	/** The entries shift-right-clicking the panel shows now; the ones from [addMenuEntry] unless overridden. */
	open fun menuEntries(): List<OverlayMenuEntry> = menuEntries

	/** Another drag-targetable panel [source] was dropped on this one; true when it was taken (and not moved). */
	open fun onDrag(source: PanelOverlay): Boolean = false

	/** The player dragged this panel to a new place this frame. */
	open fun onMoved() {
	}

	/** The mouse is over this panel (called after [render], a frame late, as RuneLite's onMouseOver). */
	open fun onMouseOver() {
	}

	/** The player put it back (Alt and a right click), its preferred place and size already cleared. */
	open fun onReset() {
	}

	companion object {
		const val PRIORITY_LOW: Float = 0f
		const val PRIORITY_DEFAULT: Float = 0.25f
		const val PRIORITY_MED: Float = 0.5f
		const val PRIORITY_HIGH: Float = 0.75f
		const val PRIORITY_HIGHEST: Float = 1f

		/** RuneLite's ComponentConstants.STANDARD_BACKGROUND_COLOR: (70, 61, 50) at 156. */
		const val STANDARD_BACKGROUND: Int = 0x9C463D32.toInt()
	}
}
