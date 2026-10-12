package awt.event

import awt.Component

expect class MouseWheelEvent : MouseEvent {
    constructor(source: Component?, id: Int, `when`: Long, modifiers: Int, x: Int, y: Int, xAbs: Int, yAbs: Int, clickCount: Int, popupTrigger: Boolean, scrollType: Int, scrollAmount: Int, wheelRotation: Int, preciseWheelRotation: Double)
    fun getWheelRotation(): Int
    fun getScrollType(): Int
    fun getScrollAmount(): Int
    fun getPreciseWheelRotation(): Double
}
