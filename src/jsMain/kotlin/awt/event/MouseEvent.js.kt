package awt.event

import awt.Component
import awt.UiScale

/*
 * A mouse event: either a DOM event read live (with the AWT event id and source given by the
 * component that received it), or one built from its fields, as AWT allows (ClientFeatures builds
 * unscaled copies).
 */
actual open class MouseEvent {
    private val event: org.w3c.dom.events.MouseEvent?
    private val source: Component?
    private val id: Int
    private val time: Long
    private val modifiersEx: Int
    private val x: Int
    private val y: Int
    private val xAbs: Int
    private val yAbs: Int
    private val clickCount: Int
    private val popupTrigger: Boolean
    private val button: Int

    internal constructor(event: org.w3c.dom.events.MouseEvent, source: Component?, id: Int) {
        this.event = event
        this.source = source
        this.id = id
        this.time = kotlin.js.Date.now().toLong()
        this.modifiersEx = 0
        this.x = 0
        this.y = 0
        this.xAbs = 0
        this.yAbs = 0
        this.clickCount = 0
        this.popupTrigger = false
        this.button = 0
    }

    actual constructor(source: Component?, id: Int, `when`: Long, modifiers: Int, x: Int, y: Int, xAbs: Int, yAbs: Int, clickCount: Int, popupTrigger: Boolean, button: Int) {
        this.event = null
        this.source = source
        this.id = id
        this.time = `when`
        this.modifiersEx = modifiers
        this.x = x
        this.y = y
        this.xAbs = xAbs
        this.yAbs = yAbs
        this.clickCount = clickCount
        this.popupTrigger = popupTrigger
        this.button = button
    }

    internal val domEvent: org.w3c.dom.events.MouseEvent? get() = event

    // offsetX/offsetY are relative to the padding edge of the target element (the canvas),
    // which is exactly what AWT callers expect - clientX/clientY would be page-relative.
    actual fun getX(): Int = if (event != null) event.offsetX.toInt() / UiScale.factor else x
    actual fun getY(): Int = if (event != null) event.offsetY.toInt() / UiScale.factor else y
    // DOM click events carry the click count in `detail`
    actual fun getClickCount(): Int = if (event != null) event.detail else clickCount
    actual fun consume() { event?.preventDefault() }
    // Right-click / context-menu button === 2 is the closest DOM analogue
    actual fun isPopupTrigger(): Boolean = if (event != null) event.button.toInt() == 2 else popupTrigger

    // DOM buttons are 0-indexed (0=left, 1=middle, 2=right); AWT's BUTTON1/2/3 are 1-indexed.
    actual fun getButton(): Int = if (event != null) event.button.toInt() + 1 else button
    actual fun isMetaDown(): Boolean = if (event != null) event.metaKey else (modifiersEx and 256) != 0

    actual fun getModifiers(): Int {
        if (event == null) {
            return modifiersEx
        }
        var mods = 0
        if (event.shiftKey) mods = mods or 1
        if (event.ctrlKey) mods = mods or 2
        if (event.altKey) mods = mods or 8
        if (event.metaKey) mods = mods or 4
        // AWT also folds the pressed-button state into getModifiers() using the legacy
        // BUTTON1_MASK/BUTTON2_MASK/BUTTON3_MASK bits (16/8/4) - MouseInputTracker reads
        // those bits instead of getButton(). event.buttons is the live pressed-buttons bitmask.
        val buttons = event.buttons.toInt()
        if (buttons and 1 != 0) mods = mods or 16
        if (buttons and 4 != 0) mods = mods or 8
        if (buttons and 2 != 0) mods = mods or 4
        return mods
    }

    actual fun getComponent(): Component = source ?: throw IllegalStateException("mouse event without a source")
    actual fun getID(): Int = id
    actual fun getWhen(): Long = time
    actual fun getModifiersEx(): Int = if (event == null) modifiersEx else getModifiers()
    actual fun getXOnScreen(): Int = if (event != null) event.screenX.toInt() else xAbs
    actual fun getYOnScreen(): Int = if (event != null) event.screenY.toInt() else yAbs
}
