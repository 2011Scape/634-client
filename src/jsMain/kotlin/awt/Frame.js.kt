package awt

import awt.event.WindowListener
import kotlinx.browser.document

actual class Frame actual constructor(title: String) : Container() {
    private var frameTitle: String = title
    private var resizable: Boolean = true
    private var undecorated: Boolean = false
    private var extendedState: Int = FRAME_NORMAL

    actual constructor() : this("")

    /*
     * In a page the client's window is the box the page gives it (the `#client` element, or the
     * browser window without one): the frame fills it and follows it when it is resized
     * (getWidth/getHeight read the element's size). The client then lays out inside it as in a
     * maximised desktop window, centring the fixed-size game view or stretching the resizable one,
     * instead of opening a 1024x768 window 40 pixels in. It fills its container rather than the
     * viewport so a page that hosts it under its own header (Void's play.html) shows all of it.
     */
    internal override val fillsPage: Boolean get() = true

    init {
        element.style.position = "absolute"
        element.style.left = "0"
        element.style.top = "0"
        element.style.width = "100%"
        element.style.height = "100%"
        document.body?.appendChild(element)
        setTitle(title)
    }

    actual fun setTitle(title: String) {
        frameTitle = title
        document.title = title
    }

    actual fun getTitle(): String = frameTitle
    actual fun setResizable(resizable: Boolean) { this.resizable = resizable }
    actual fun isResizable(): Boolean = resizable
    actual fun setUndecorated(undecorated: Boolean) { this.undecorated = undecorated }
    actual fun isUndecorated(): Boolean = undecorated

    actual fun setExtendedState(state: Int) {
        extendedState = state
        if (state and FRAME_MAXIMIZED_BOTH == FRAME_MAXIMIZED_BOTH) {
            element.style.width = "100%"
            element.style.height = "100%"
        }
    }

    actual fun getExtendedState(): Int = extendedState

    actual fun pack() { }
    actual fun dispose() { element.remove() }
    actual fun toFront() { element.style.zIndex = "1000" }
    actual fun toBack() { element.style.zIndex = "0" }

    actual fun setLocationRelativeTo(c: Component) {
        element.style.left = "${c.getX() + (c.getWidth() - getWidth()) / 2}px"
        element.style.top = "${c.getY() + (c.getHeight() - getHeight()) / 2}px"
    }

    actual fun addWindowListener(listener: WindowListener) {
    }

    actual fun enableInputMethods(boolean: Boolean) {
    }
}

actual val FRAME_NORMAL: Int = 0
actual val FRAME_ICONIFIED: Int = 1
actual val FRAME_MAXIMIZED_BOTH: Int = 6