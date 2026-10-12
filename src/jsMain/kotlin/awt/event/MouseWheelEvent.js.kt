package awt.event

import awt.Component

actual class MouseWheelEvent : MouseEvent {
    private val wheel: org.w3c.dom.events.WheelEvent?
    private val scrollType: Int
    private val scrollAmount: Int
    private val rotation: Int
    private val preciseRotation: Double

    internal constructor(event: org.w3c.dom.events.WheelEvent, source: Component?) : super(event, source, 507) {
        this.wheel = event
        this.scrollType = 0
        this.scrollAmount = 3
        this.rotation = 0
        this.preciseRotation = 0.0
    }

    actual constructor(source: Component?, id: Int, `when`: Long, modifiers: Int, x: Int, y: Int, xAbs: Int, yAbs: Int, clickCount: Int, popupTrigger: Boolean, scrollType: Int, scrollAmount: Int, wheelRotation: Int, preciseWheelRotation: Double) : super(source, id, `when`, modifiers, x, y, xAbs, yAbs, clickCount, popupTrigger, 0) {
        this.wheel = null
        this.scrollType = scrollType
        this.scrollAmount = scrollAmount
        this.rotation = wheelRotation
        this.preciseRotation = preciseWheelRotation
    }

    // Normalize deltaY into AWT-style "notches"; sign preserved, magnitude scaled
    actual fun getWheelRotation(): Int {
        if (wheel == null) {
            return rotation
        }
        val notches = wheel.deltaY / 100.0
        return when {
            notches > 0 -> kotlin.math.ceil(notches).toInt()
            notches < 0 -> kotlin.math.floor(notches).toInt()
            else -> 0
        }
    }

    actual fun getScrollType(): Int = scrollType
    actual fun getScrollAmount(): Int = scrollAmount
    actual fun getPreciseWheelRotation(): Double = if (wheel == null) preciseRotation else wheel.deltaY / 100.0
}
