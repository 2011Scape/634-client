package com.jagex.game.runetek5.plugin.api

/** Receives the open interfaces from [ClientApi.openInterfaces]. */
fun interface InterfaceVisitor {
	/** [interfaceId] is open inside the component [parentWidgetId] ([Widget.id]), as an [InterfaceOpenType]. */
	fun visit(interfaceId: Int, parentWidgetId: Int, openType: Int)
}
