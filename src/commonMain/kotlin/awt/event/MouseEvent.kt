package awt.event

import awt.Component

expect open class MouseEvent {
    constructor(source: Component?, id: Int, `when`: Long, modifiers: Int, x: Int, y: Int, xAbs: Int, yAbs: Int, clickCount: Int, popupTrigger: Boolean, button: Int)
    fun getX(): Int
    fun getY(): Int
    fun getClickCount(): Int
    fun consume()
    fun isPopupTrigger(): Boolean
    fun getModifiers(): Int
    fun getButton(): Int
    fun isMetaDown(): Boolean
    fun getComponent(): Component
    fun getID(): Int
    fun getWhen(): Long
    fun getModifiersEx(): Int
    fun getXOnScreen(): Int
    fun getYOnScreen(): Int
}
