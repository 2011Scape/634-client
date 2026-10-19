package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.ITEM_CONTAINER_CHANGED]. [containerId] 93 is the backpack. */
class ItemContainerChanged(@JvmField val containerId: Int)
