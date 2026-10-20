package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * For [Events.INTERFACE_OPENED] and [Events.INTERFACE_CLOSED]: [interfaceId] inside the component
 * [parentWidgetId], as an [openType][com.jagex.game.runetek5.plugin.api.InterfaceOpenType].
 */
class InterfaceChanged(@JvmField val interfaceId: Int, @JvmField val parentWidgetId: Int, @JvmField val openType: Int)
