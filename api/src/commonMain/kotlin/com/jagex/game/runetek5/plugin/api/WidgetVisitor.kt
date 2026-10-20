package com.jagex.game.runetek5.plugin.api

/** Receives components from [Widget.staticChildren]. */
fun interface WidgetVisitor {
	fun visit(widget: Widget)
}
